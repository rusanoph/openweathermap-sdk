package io.openweathermap.sdk.core.interceptor;

import io.openweathermap.sdk.core.client.OwmRequestMeta;
import io.openweathermap.sdk.core.client.OwmRuntime;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.http.HttpResponse;
import io.openweathermap.sdk.core.interceptor.impl.CacheInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.DefaultHeadersInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.LoggingInterceptor;
import io.openweathermap.sdk.core.interceptor.impl.RetryInterceptor;
import io.openweathermap.sdk.core.logger.JdkLoggerAdapter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class OwmHttpPipeline {

    private final List<OwmInterceptor> interceptors;
    private final OwmRuntime runtime;

    public OwmHttpPipeline(OwmRuntime runtime) {
        this(runtime, List.of(
                new LoggingInterceptor(JdkLoggerAdapter.get("OpenWeatherMap-SDK")),
                new DefaultHeadersInterceptor(Map.of(
                        "Accept", "application/json",
                        "User-Agent", "openweathermap-sdk/" + version()
                )),
                new CacheInterceptor(runtime.getCache(), System::currentTimeMillis),
                new RetryInterceptor(
                        runtime.getConfig().getRetryAttempts(),
                        runtime.getConfig().getRetryBaseDelay(),
                        runtime.getConfig().getRetryMaxDelay(),
                        runtime.getConfig().getRetryJitter(),
                        runtime.getConfig().getScheduler()
                )
        ));
    }

    public OwmHttpPipeline(OwmRuntime runtime, List<OwmInterceptor> interceptors) {
        this.runtime = Objects.requireNonNull(runtime);
        this.interceptors = Objects.requireNonNull(List.copyOf(interceptors));
    }

    public CompletableFuture<HttpResponse> execute(HttpRequest req, OwmRequestMeta meta) {
        return new ChainImpl(runtime.getHttp(), interceptors, 0, req, meta).proceed(req, meta);
    }

    private static String version() {
        String v = OwmHttpPipeline.class.getPackage().getImplementationVersion();
        return (v != null && !v.isBlank()) ? v : "dev";
    }
}
