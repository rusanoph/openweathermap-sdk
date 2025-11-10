package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.OwmLanguage;
import io.openweathermap.sdk.core.model.OwmUnits;
import io.openweathermap.sdk.core.model.forecast.Forecast5d;

import java.util.concurrent.CompletableFuture;

public interface ForecastApi {

    CompletableFuture<Forecast5d> byCoordsAsync(double lat, double lon, OwmUnits units, OwmLanguage lang);

    default Forecast5d byCoords(double lat, double lon, OwmUnits units, OwmLanguage lang) throws Exception {
        return byCoordsAsync(lat, lon, units, lang).join();
    };
}
