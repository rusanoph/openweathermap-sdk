package io.openweathermap.sdk.adapters.http.jdk;


import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HttpJdkClient implements HttpClientPort {

    private final HttpClient client;

    public HttpJdkClient() {
        this(HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build());
    }

    public HttpJdkClient(HttpClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        Objects.requireNonNull(request, "request");

        var jdkRequest = toJdk(request);

        return client.sendAsync(jdkRequest, java.net.http.HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> HttpResponse.builder()
                        .status(response.statusCode())
                        .body(response.body())
                        .contentType(response.headers().firstValue("Content-Type").orElse(null))
                        .build());
    }

    private java.net.http.HttpRequest toJdk(HttpRequest request) {
        var builder = java.net.http.HttpRequest.newBuilder(request.getUri())
                .timeout(Duration.ofMillis(request.getReadTimeoutMillis()))
                .GET();

        request.getHeaders().forEach(builder::header);
        return builder.build();
    }
}
