package io.openweathermap.sdk.core.interceptor.impl;

import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;
import io.openweathermap.sdk.core.interceptor.Chain;
import io.openweathermap.sdk.core.interceptor.OwmInterceptor;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DefaultHeadersInterceptor implements OwmInterceptor {

    private final Map<String, String> defaults;

    @Override
    public CompletableFuture<HttpResponse> intercept(Chain chain) {
        HttpRequest request = chain.request();
        Map<String,String> merged = new LinkedHashMap<>();
        if (request.getHeaders() != null) merged.putAll(request.getHeaders());
        defaults.forEach(merged::putIfAbsent);

        HttpRequest out = HttpRequest.builder()
                .method(request.getMethod())
                .uri(request.getUri())
                .headers(merged)
                .body(request.getBody())
                .connectTimeoutMillis(request.getConnectTimeoutMillis())
                .readTimeoutMillis(request.getReadTimeoutMillis())
                .build();

        return chain.proceed(out, chain.meta());
    }
}
