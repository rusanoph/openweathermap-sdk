package io.openweathermap.sdk.core.client;

import io.openweathermap.sdk.core.cache.AsyncCache;
import io.openweathermap.sdk.core.cache.CacheEntry;
import io.openweathermap.sdk.core.exception.HttpStatusException;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.model.OwmLanguage;
import io.openweathermap.sdk.core.model.OwmUnits;
import io.openweathermap.sdk.util.http.QueryBuilder;
import io.openweathermap.sdk.util.retry.Retry;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public final class OwmEndpointHelper {

    private final OwmRuntime runtime;
//    private final OwmHttpPipeline pipeline;

    private final AsyncCache<String, byte[]> cache;

    public OwmEndpointHelper(OwmRuntime runtime) {
        this.runtime = runtime;
        this.cache = new AsyncCache<>(runtime.getCache(), runtime.getConfig().getHttpExecutor());
    }

    public URI uri(String path, QueryBuilder qb) {
        String base = runtime.getConfig().getHost() + path;
        String query = qb.add("appid", runtime.getConfig().getApiKey()).build();
        return URI.create(base + "?" + query);
    }

    public HttpRequest buildGet(URI uri) {
        return HttpRequest.builder()
                .method("GET")
                .uri(uri)
                .connectTimeoutMillis(runtime.getConfig().getConnectTimeout().toMillis())
                .readTimeoutMillis(runtime.getConfig().getReadTimeout().toMillis())
                .header("Accept", "application/json")
                .build();
    }

    public static String key(String path, QueryBuilder qb) {
        return path + "?" + qb.build();
    }

    public CompletableFuture<byte[]> getWithCacheAsync(HttpRequest req, String cacheKey, long ttlMillis) {
        long now = System.currentTimeMillis();
        return cache.getEntryAsync(cacheKey).thenCompose(opt -> {
            if (opt.isPresent() && !opt.get().isExpired(now)) {
                return CompletableFuture.completedFuture(opt.get().value());
            }

            Retry.Policy policy = new Retry.Policy(
                    runtime.getConfig().getRetryAttempts(),
                    runtime.getConfig().getRetryBaseDelay(),
                    runtime.getConfig().getRetryMaxDelay(),
                    runtime.getConfig().getRetryJitter(),
                    OwmEndpointHelper::isRetryable
            );

            return Retry.withRetryAsync(
                    () -> runtime.getHttp().executeAsync(req).thenApply(resp -> {
                        int s = resp.getStatus();
                        if (s >= 200 && s < 300) return resp.getBody();
                        throw new HttpStatusException(s, "HTTP " + s);
                    }),
                    policy,
                    runtime.getConfig().getScheduler()
            ).thenCompose(body ->
                    cache.putAsync(cacheKey, new CacheEntry<>(body, now + ttlMillis))
                            .thenApply(v -> body)
            );
        });
    }

    public <T> CompletableFuture<T> decodeAsync(byte[] body, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> runtime.getSer().fromJson(body, type),
                runtime.getConfig().getDecoderExecutor());
    }

    public static QueryBuilder defaults(double lat, double lon, OwmUnits u, OwmLanguage l) {
        return new QueryBuilder().add("lat", lat).add("lon", lon)
                .add("units", u != null ? u.toString() : null)
                .add("lang",  l != null ? l.toString() : null);
    }

    public QueryBuilder defaultsWithFallback(double lat, double lon, OwmUnits u, OwmLanguage l) {
        OwmUnits units = (u != null) ? u : runtime.getConfig().getDefaultUnits();
        OwmLanguage language = (l != null) ? l : runtime.getConfig().getDefaultLanguage();
        return defaults(lat, lon, units, language);
    }

    private static boolean isRetryable(Throwable t) {
        if (t instanceof HttpStatusException h) {
            int s = h.getStatus();
            return s == 429 || (s >= 500 && s < 600);
        }

        return true;
    }
}