package io.openweathermap.sdk.core.exception;

import lombok.Getter;

@Getter
public class HttpStatusException extends OwmClientException {

    private final int status;

    public HttpStatusException(int status, String msg) {
        super(msg);
        this.status = status;
    }
}
