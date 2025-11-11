package io.openweathermap.sdk.springframework.boot;

import io.openweathermap.sdk.core.interceptor.OwmInterceptor;

import java.util.List;

@FunctionalInterface
public interface OwmInterceptorsCustomizer {
    void customize(List<OwmInterceptor> interceptors);
}