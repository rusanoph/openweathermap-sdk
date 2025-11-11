package io.openweathermap.sdk.core.interceptor;

import io.openweathermap.sdk.core.http.HttpResponse;

import java.util.concurrent.CompletableFuture;

public interface OwmInterceptor {

    CompletableFuture<HttpResponse> intercept(Chain chain);

}
