package io.openweathermap.sdk.core.exception;

public class OwmClientException extends RuntimeException {

    public OwmClientException(String msg) {
        super(msg);
    }

    public OwmClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
