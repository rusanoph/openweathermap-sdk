package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.weather.Weather;

import java.util.concurrent.CompletableFuture;

public interface WeatherApi {

    CompletableFuture<Weather> byCoordsAsync(CoordinatesRequest request);

    default Weather byCoords(CoordinatesRequest request) {
        return byCoordsAsync(request).join();
    }
}
