package io.openweathermap.sdk.core.client;

import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;
import io.openweathermap.sdk.core.interceptor.OwmHttpPipeline;
import io.openweathermap.sdk.util.http.QueryBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public final class OwmEndpointHelper {

    private final OwmRuntime runtime;
    private final OwmHttpPipeline pipeline;

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
                .build();
    }

    public static String key(String path, QueryBuilder qb) {
        return path + "?" + qb.build();
    }

    public CompletableFuture<HttpResponse> executeAsync(HttpRequest req, OwmRequestMeta meta) {
        return pipeline.execute(req, meta);
    }

    public <T> CompletableFuture<T> decodeAsync(byte[] body, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> runtime.getSer().fromJson(body, type),
                runtime.getConfig().getDecoderExecutor());
    }
}