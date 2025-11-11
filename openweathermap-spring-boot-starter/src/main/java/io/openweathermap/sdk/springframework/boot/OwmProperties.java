package io.openweathermap.sdk.springframework.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("owm")
public record OwmProperties(
        String apiKey,
        String host,
        Duration connectTimeout,
        Duration readTimeout,
        Integer retryAttempts,
        Duration retryBaseDelay,
        Duration retryMaxDelay,
        Double retryJitter,
        Integer cacheSizeMb,
        String userAgent
) {
    public String hostOrDefault()           {
        return host != null ? host : "http://api.openweathermap.org";
    }

    public Duration connectOrDefault()      {
        return connectTimeout != null ? connectTimeout : Duration.ofSeconds(5);
    }

    public Duration readOrDefault()         {
        return readTimeout != null ? readTimeout : Duration.ofSeconds(10);
    }

    public int attemptsOrDefault()          {
        return retryAttempts != null ? retryAttempts : 3;
    }

    public Duration baseDelayOrDefault()    {
        return retryBaseDelay != null ? retryBaseDelay : Duration.ofMillis(200);
    }

    public Duration maxDelayOrDefault()     {
        return retryMaxDelay != null ? retryMaxDelay : Duration.ofSeconds(2);
    }

    public double jitterOrDefault()         {
        return retryJitter != null ? retryJitter : 0.2;
    }

    public int cacheSizeBytesOrDefault()    {
        return (cacheSizeMb != null ? cacheSizeMb : 128) * 1024 * 1024;
    }

    public String userAgentOrDefault()      {
        return userAgent != null ? userAgent : "openweathermap-sdk/" + OwmAutoConfiguration.version();
    }

}
