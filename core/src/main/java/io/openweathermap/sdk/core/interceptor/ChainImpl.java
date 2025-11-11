package io.openweathermap.sdk.core.interceptor;

import io.openweathermap.sdk.core.client.OwmRequestMeta;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
final class ChainImpl implements Chain {
    private final HttpClientPort http;
    private final List<OwmInterceptor> list;
    private final int index;
    private final HttpRequest req;
    private final OwmRequestMeta meta;

    @Override
    public HttpRequest request() {
        return req;
    }

    @Override
    public OwmRequestMeta meta() {
        return meta;
    }

    @Override
    public CompletableFuture<HttpResponse> proceed(HttpRequest request, OwmRequestMeta requestMeta) {
        if (index >= list.size()) {
            return http.executeAsync(request);
        }

        OwmInterceptor next = list.get(index);
        return next.intercept(new ChainImpl(http, list, index + 1, request, meta));
    }
}
