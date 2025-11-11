package io.openweathermap.sdk.example.java;

import io.openweathermap.sdk.adapters.cache.guava.GuavaCacheAdapter;
import io.openweathermap.sdk.adapters.http.jdk.HttpJdkClient;
import io.openweathermap.sdk.adapters.serializer.jackson.JacksonSerializer;
import io.openweathermap.sdk.core.cache.CachePort;
import io.openweathermap.sdk.core.client.OwmClient;
import io.openweathermap.sdk.core.client.OwmClientConfig;
import io.openweathermap.sdk.core.client.OwmEndpointHelper;
import io.openweathermap.sdk.core.client.OwmRuntime;
import io.openweathermap.sdk.core.http.HttpClientPort;
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

        HttpClientPort http = new HttpJdkClient();
        SerializerPort serializer = new JacksonSerializer();
        CachePort<String, byte[]> cache = new GuavaCacheAdapter(CACHE_SIZE);

        var owmConfig = OwmClientConfig.builder()
                .apiKey(apiKey)
                .build();

        var owmRuntime = new OwmRuntime(owmConfig, http, serializer, cache);
        var owmEndpointHelper = new OwmEndpointHelper(owmRuntime);
        var owmClient = new OwmClient(owmRuntime, owmEndpointHelper);

        // Retrieve coordinates by query string
        List<GeoCity> geoCities = owmClient.getGeoApi().direct("Saint-Petersburg", 10);

        // Extract place coordinates
        double lat = geoCities.getFirst().lat();
        double lon = geoCities.getFirst().lon();

        var request = CoordinatesRequest.builder()
                .lat(lat)
                .lon(lon)
                .build();

        // Example of usages: weather, forecast, air pollution
        Weather weather = owmClient.getWeatherApi().byCoords(request);
        Forecast5d forecast5d = owmClient.getForecastApi().byCoords(request);
        AirPollution airPollution = owmClient.getAirPollutionApi().now(request);
    }
}
