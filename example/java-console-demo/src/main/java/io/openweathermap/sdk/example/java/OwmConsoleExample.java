package io.openweathermap.sdk.example.java;

import io.openweathermap.sdk.adapters.cache.guava.GuavaCacheAdapter;
import io.openweathermap.sdk.adapters.http.jdk.HttpJdkClientAdapter;
import io.openweathermap.sdk.adapters.serializer.jackson.JacksonSerializerAdapter;
import io.openweathermap.sdk.core.cache.CachePort;
import io.openweathermap.sdk.core.client.OwmClient;
import io.openweathermap.sdk.core.client.OwmClientConfig;
import io.openweathermap.sdk.core.client.OwmEndpointHelper;
import io.openweathermap.sdk.core.client.OwmRuntime;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.interceptor.OwmHttpPipeline;
import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.air.AirPollution;
import io.openweathermap.sdk.core.model.forecast.Forecast5d;
import io.openweathermap.sdk.core.model.geo.GeoCity;
import io.openweathermap.sdk.core.model.weather.Weather;
import io.openweathermap.sdk.core.serializer.SerializerPort;

import java.util.List;

public class OwmConsoleExample {

    private static final String OPENWEATHERMAP_API_KEY_ENV = "OPENWEATHERMAP_API_KEY";
    private static final Integer CACHE_SIZE = 128 * 1024 * 1024;

    public static void main(String[] args) {
        String apiKey = System.getenv(OPENWEATHERMAP_API_KEY_ENV);

        HttpClientPort http = new HttpJdkClientAdapter();
        SerializerPort serializer = new JacksonSerializerAdapter();
        CachePort<String, byte[]> cache = new GuavaCacheAdapter(CACHE_SIZE);

        var owmConfig = OwmClientConfig.builder()
                .apiKey(apiKey)
                .build();

        var runtime = new OwmRuntime(owmConfig, http, serializer, cache);
        var pipeline = new OwmHttpPipeline(runtime);  // By default: logging + headers + cache + retry
        var helper = new OwmEndpointHelper(runtime, pipeline);
        var owm = new OwmClient(runtime, helper);

        // 1) Geocoding
        List<GeoCity> geoCities = owm.getGeoApi().direct("Saint-Petersburg", 10);

        // 2) Coordinates
        double lat = geoCities.getFirst().lat();
        double lon = geoCities.getFirst().lon();

        var request = CoordinatesRequest.builder()
                .lat(lat)
                .lon(lon)
                .build();

        // 3) Weather, forecast, air quality
        Weather weather = owm.getWeatherApi().byCoords(request);
        Forecast5d forecast5d = owm.getForecastApi().byCoords(request);
        AirPollution airPollution = owm.getAirPollutionApi().now(request);

        System.out.println(weather);
        System.out.println(forecast5d);
        System.out.println(airPollution);
    }
}
