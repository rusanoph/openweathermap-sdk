package io.openweathermap.sdk.core.client;

import io.openweathermap.sdk.core.api.*;
import io.openweathermap.sdk.core.cache.CachePort;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.serializer.SerializerPort;
import io.openweathermap.sdk.util.lazy.LazyRef;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public final class OwmClient {

    private final OwmRuntime runtime;
    private final EndpointHelper endpointHelper;

    private final LazyRef<AirPollutionApi> air;
    private final LazyRef<ForecastApi> forecast;
    private final LazyRef<GeoApi> geo;
    private final LazyRef<WeatherApi> weather;

    public OwmClient(OwmRuntime runtime, EndpointHelper endpointHelper) {
        this.runtime = runtime;
        this.endpointHelper = endpointHelper;

        this.air = new LazyRef<>(() -> new AirPollutionApiImpl(endpointHelper));
        this.forecast = new LazyRef<>(() -> new ForecastApiImpl(endpointHelper));
        this.geo = new LazyRef<>(() -> new GeoApiImpl(endpointHelper));
        this.weather = new LazyRef<>(() -> new WeatherApiImpl(endpointHelper));
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
