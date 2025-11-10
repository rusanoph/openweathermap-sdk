package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.air.AirPollution;

import java.util.concurrent.CompletableFuture;

public interface AirPollutionApi {

    CompletableFuture<AirPollution> nowAsync(double lat, double lon);

    default AirPollution now(double lat, double lon) {
        return nowAsync(lat, lon).join();
    }
}
