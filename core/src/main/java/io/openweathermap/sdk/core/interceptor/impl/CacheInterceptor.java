package io.openweathermap.sdk.core.interceptor.impl;

import io.openweathermap.sdk.core.cache.CacheEntry;
import io.openweathermap.sdk.core.cache.CachePort;
import io.openweathermap.sdk.core.client.OwmRequestMeta;
import io.openweathermap.sdk.core.http.HttpResponse;
import io.openweathermap.sdk.core.interceptor.Chain;
import io.openweathermap.sdk.core.interceptor.OwmInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class CacheInterceptor implements OwmInterceptor {

    private final CachePort<String, byte[]> cache;
    private final Supplier<Long> clock;

    @Override
    public CompletableFuture<HttpResponse> intercept(Chain chain) {
        OwmRequestMeta meta = chain.meta();
        String key = meta.getCacheKey();
        long now = clock.get();

        if (key != null) {
            var hit = cache.get(key);
            if (hit.isPresent() && !hit.get().isExpired(now)) {
                meta.setCacheHit();

                return CompletableFuture.completedFuture(HttpResponse.builder()
                        .status(200)
                        .body(hit.get().value())
                        .contentType("application/json")
                        .build());
            }
        }

        return chain.proceed(chain.request(), meta).thenApply(resp -> {
            if (key != null && meta.getTtlMillis() > 0 && resp.getStatus() >= 200 && resp.getStatus() < 300) {
                cache.put(key, new CacheEntry<>(resp.getBody(), now + meta.getTtlMillis()));
            }
            return resp;
        });
    }
}
