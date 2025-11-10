package io.openweathermap.sdk.core.client;

import io.openweathermap.sdk.core.model.OwmLanguage;
import io.openweathermap.sdk.core.model.OwmUnits;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Builder
public class OwmClientConfig {

    @NonNull
    private String apiKey;
    @NonNull
    @Builder.Default
    private String host = "http://api.openweathermap.org";

    @Builder.Default
    OwmUnits defaultUnits = OwmUnits.METRIC;
    @Builder.Default
    OwmLanguage defaultLanguage = OwmLanguage.EN;

    @Builder.Default
    private int retryAttempts = 3;
    @Builder.Default
    private Duration retryBaseDelay = Duration.ofMillis(200);
    @Builder.Default
    private Duration retryMaxDelay = Duration.ofSeconds(2);
    @Builder.Default
    private double retryJitter = 0.2; // 0..1

    @Builder.Default
    private Duration connectTimeout = Duration.ofSeconds(5);
    @Builder.Default
    private Duration readTimeout = Duration.ofSeconds(10);

    private Executor httpExecutor;     // for http requests
    private Executor decoderExecutor;  // for heavy jsons
    private ScheduledExecutorService scheduler; // for retry, timeouts

    public OwmClientConfig(
            String host, String apiKey,
            int retryAttempts, Duration retryBaseDelay, Duration retryMaxDelay, double retryJitter,
            Duration connectTimeout, Duration readTimeout,
            Executor httpExecutor, Executor decoderExecutor, ScheduledExecutorService scheduler
    ) {
        this.apiKey = Objects.requireNonNull(apiKey);
        this.host = Objects.requireNonNull(host);

        this.retryAttempts = Math.max(1, retryAttempts);
        this.retryBaseDelay = Objects.requireNonNull(retryBaseDelay);
        this.retryMaxDelay = Objects.requireNonNull(retryMaxDelay);
        this.retryJitter = Math.max(0.0, Math.min(1.0, retryJitter));

        this.connectTimeout = Objects.requireNonNull(connectTimeout);
        this.readTimeout = Objects.requireNonNull(readTimeout);

        this.httpExecutor = httpExecutor != null ? httpExecutor : ForkJoinPool.commonPool();
        this.decoderExecutor = decoderExecutor != null ? decoderExecutor : ForkJoinPool.commonPool();
        this.scheduler = scheduler != null ? scheduler : Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "owm-scheduler"); t.setDaemon(true); return t;
        });
    }
}
