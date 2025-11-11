package io.openweathermap.sdk.core.client;

import io.openweathermap.sdk.core.api.*;
import io.openweathermap.sdk.util.lazy.LazyRef;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class OwmClient {

    private final OwmRuntime runtime;
    private final OwmEndpointHelper owmEndpointHelper;

    private final LazyRef<AirPollutionApi> air;
    private final LazyRef<ForecastApi> forecast;
    private final LazyRef<GeoApi> geo;
    private final LazyRef<WeatherApi> weather;

    public OwmClient(OwmRuntime runtime, OwmEndpointHelper owmEndpointHelper) {
        this.runtime = runtime;
        this.owmEndpointHelper = owmEndpointHelper;

        this.air = new LazyRef<>(() -> new AirPollutionApiImpl(owmEndpointHelper));
        this.forecast = new LazyRef<>(() -> new ForecastApiImpl(owmEndpointHelper));
        this.geo = new LazyRef<>(() -> new GeoApiImpl(owmEndpointHelper));
        this.weather = new LazyRef<>(() -> new WeatherApiImpl(owmEndpointHelper));
    }

    public AirPollutionApi getAirPollutionApi() {
        return air.get();
    }

    public ForecastApi getForecastApi() {
        return forecast.get();
    }

    public GeoApi getGeoApi() {
        return geo.get();
    }

    public WeatherApi getWeatherApi() {
        return weather.get();
    }
}
