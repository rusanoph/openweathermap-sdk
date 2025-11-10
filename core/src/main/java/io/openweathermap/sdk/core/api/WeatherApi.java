package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.OwmLanguage;
import io.openweathermap.sdk.core.model.OwmUnits;
import io.openweathermap.sdk.core.model.weather.Weather;

import java.util.concurrent.CompletableFuture;

public interface WeatherApi {

    CompletableFuture<Weather> byCoordsAsync(double lat, double lon, OwmUnits units, OwmLanguage lang);

    default Weather byCoords(double lat, double lon, OwmUnits units, OwmLanguage lang) throws Exception {
        return byCoordsAsync(lat, lon, units, lang).join();
    };
}
