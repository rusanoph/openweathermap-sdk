package io.openweathermap.sdk.core.http;

import java.util.concurrent.CompletableFuture;

public interface HttpClientPort {

    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);

    default HttpResponse execute(HttpRequest request) {
        return executeAsync(request).join();
    }
}
