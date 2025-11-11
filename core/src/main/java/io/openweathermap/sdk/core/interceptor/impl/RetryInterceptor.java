package io.openweathermap.sdk.core.interceptor.impl;

import io.openweathermap.sdk.core.client.OwmClientConfig;
import io.openweathermap.sdk.core.http.HttpResponse;
import io.openweathermap.sdk.core.interceptor.Chain;
import io.openweathermap.sdk.core.interceptor.OwmInterceptor;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RetryInterceptor implements OwmInterceptor {

    private final int attempts;
    private final Duration base;
    private final Duration max;
    private final double jitter;
    private final ScheduledExecutorService sch;

    public RetryInterceptor(int attempts, Duration base, Duration max, double jitter, ScheduledExecutorService sch) {
        this.attempts = Math.max(1, attempts);
        this.base = base;
        this.max = max;
        this.jitter = Math.max(0, Math.min(1, jitter));
        this.sch = sch;
    }

    @Override
    public CompletableFuture<HttpResponse> intercept(Chain chain) {
        return retry(1, chain);
    }

    private CompletableFuture<HttpResponse> retry(int n, Chain chain) {
        return chain.proceed(chain.request(), chain.meta()).handle((resp, err) -> {
            if (err != null) {
                return new AbstractMap.SimpleEntry<HttpResponse,Throwable>(null, err);
            }

            return new AbstractMap.SimpleEntry<>(resp, null);
        }).thenCompose(pair -> {
            HttpResponse resp = pair.getKey();
            Throwable err = (Throwable) pair.getValue();


            boolean isError = err != null;
            boolean networkError = resp.getStatus() == 429 || (resp.getStatus() >= 500 && resp.getStatus() < 600);
            boolean shouldRetry = isError || networkError;

            if (!shouldRetry || n >= attempts) {
                if (err != null) {
                    return CompletableFuture.failedFuture(err);
                }

                return CompletableFuture.completedFuture(resp);
            }

            long delay = backoffMillis(n);
            CompletableFuture<HttpResponse> next = new CompletableFuture<>();

            sch.schedule(() -> retry(n + 1, chain).whenComplete((v, e) -> {
                if (e != null) {
                    next.completeExceptionally(e);
                } else {
                    next.complete(v);
                }
            }), delay, TimeUnit.MILLISECONDS);
            return next;
        });
    }

    private long backoffMillis(int n) {
        long raw = (long) (base.toMillis() * Math.pow(2, n - 1));
        long capped = Math.min(raw, max.toMillis());
        long j = (long) (capped * jitter * Math.random());
        return capped + j;
    }
}
