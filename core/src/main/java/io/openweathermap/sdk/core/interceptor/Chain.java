package io.openweathermap.sdk.core.interceptor;

import io.openweathermap.sdk.core.client.OwmRequestMeta;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;

import java.util.concurrent.CompletableFuture;

public interface Chain {

    HttpRequest request();

    OwmRequestMeta meta();

    CompletableFuture<HttpResponse> proceed(HttpRequest request, OwmRequestMeta requestMeta);
}
