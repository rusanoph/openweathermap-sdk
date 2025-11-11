package io.openweathermap.sdk.core.interceptor;

import io.openweathermap.sdk.core.client.OwmRequestMeta;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class OwmHttpPipeline {

    private final List<OwmInterceptor> interceptors;
    private final HttpClientPort http;

    public OwmHttpPipeline(HttpClientPort http, List<OwmInterceptor> interceptors) {
        this.http = Objects.requireNonNull(http);
        this.interceptors = Objects.requireNonNull(List.copyOf(interceptors));
    }

    public CompletableFuture<HttpResponse> execute(HttpRequest req, OwmRequestMeta meta) {
        return new ChainImpl(http, interceptors, 0, req, meta).proceed(req, meta);
    }
}
