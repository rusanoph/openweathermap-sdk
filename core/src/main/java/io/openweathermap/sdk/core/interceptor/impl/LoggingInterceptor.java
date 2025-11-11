package io.openweathermap.sdk.core.interceptor.impl;

import io.openweathermap.sdk.core.http.HttpResponse;
import io.openweathermap.sdk.core.interceptor.Chain;
import io.openweathermap.sdk.core.interceptor.OwmInterceptor;
import io.openweathermap.sdk.core.logger.LoggerPort;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class LoggingInterceptor implements OwmInterceptor {

    private final LoggerPort log;

    @Override
    public CompletableFuture<HttpResponse> intercept(Chain chain) {
        long start = System.nanoTime();
        var request = chain.request();
        var meta = chain.meta();

        String url = secureApiKey(request.getUri().toString());
        log.info("Request", Map.ofEntries(
                Map.entry("method", request.getMethod()),
                Map.entry("url", url),
                Map.entry("endpointId", meta.getEndpointId()),
                Map.entry("ttlMillis",meta.getTtlMillis())
        ));

        return chain.proceed(request, meta).whenComplete((response, err) -> {
            long ms = (System.nanoTime() - start) / 1_000_000;

            if (err != null) {
                log.warn("Request error", Map.ofEntries(
                        Map.entry("method", request.getMethod()),
                        Map.entry("url", url),
                        Map.entry("executedMillis", ms),
                        Map.entry("fromCache", meta.getCacheHit())
                ));
            } else {
                log.info("Response", Map.ofEntries(
                        Map.entry("status", response.getStatus()),
                        Map.entry("method", request.getMethod()),
                        Map.entry("url", url),
                        Map.entry("executedMillis", ms),
                        Map.entry("fromCache", meta.getCacheHit() != null ? meta.getCacheHit() : false)
                ));
            }
        });
    }

    private static String secureApiKey(String url) {
        return url.replaceAll("(?i)(appid=)([^&]+)", "$1***");
    }
}
