package io.openweathermap.sdk.core.http;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.net.URI;
import java.util.Map;

@Value
@Builder
public class HttpRequest {
    String method;
    URI uri;

    @Singular("header")
    Map<String, String> headers;

    byte[] body;

    long connectTimeoutMillis;
    long readTimeoutMillis;
}
