package io.openweathermap.sdk.core.http;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HttpResponse {
    int status;
    byte[] body;
    String contentType;
}
