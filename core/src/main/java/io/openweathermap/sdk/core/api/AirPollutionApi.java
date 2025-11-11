package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.air.AirPollution;

import java.util.concurrent.CompletableFuture;

public interface AirPollutionApi {

    CompletableFuture<AirPollution> nowAsync(CoordinatesRequest request);

    default AirPollution now(CoordinatesRequest request) {
        return nowAsync(request).join();
    }
}
