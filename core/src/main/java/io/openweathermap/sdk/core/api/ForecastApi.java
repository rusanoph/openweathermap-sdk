package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.forecast.Forecast5d;

import java.util.concurrent.CompletableFuture;

public interface ForecastApi {

    CompletableFuture<Forecast5d> byCoordsAsync(CoordinatesRequest request);

    default Forecast5d byCoords(CoordinatesRequest request) {
        return byCoordsAsync(request).join();
    }
}
