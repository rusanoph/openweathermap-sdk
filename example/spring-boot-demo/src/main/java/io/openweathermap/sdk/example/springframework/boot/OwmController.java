package io.openweathermap.sdk.example.springframework.boot;

import io.openweathermap.sdk.core.client.OwmClient;
import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.OwmLanguage;
import io.openweathermap.sdk.core.model.OwmUnits;
import io.openweathermap.sdk.core.model.geo.GeoCity;
import io.openweathermap.sdk.core.model.weather.Weather;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/openweathermap/v1")
@RequiredArgsConstructor
public class OwmController {

    private final OwmClient client;

    // Async usage (non-blocking)
    @GetMapping("/geo")
    public CompletableFuture<ResponseEntity<List<GeoCity>>> geo(
            @RequestParam("query") String query,
            @RequestParam(value = "limit", defaultValue = "1") Integer limit
    ) {
        return client.getGeoApi().directAsync(query, limit)
                .thenApply(ResponseEntity::ok);
    }

    // Sync usage (blocks request thread)
    @GetMapping("/weather")
    public ResponseEntity<Weather> weather(
            @RequestParam(name = "lat") Double lat,
            @RequestParam(name = "lon") Double lon,
            @RequestParam(value = "units", defaultValue = "METRIC") OwmUnits units,
            @RequestParam(value = "lang",  defaultValue = "EN")     OwmLanguage lang
    ) {
        var req = CoordinatesRequest.builder()
                .lat(lat).lon(lon)
                .units(units).language(lang)
                .build();
        return ResponseEntity.ok(client.getWeatherApi().byCoords(req));
    }
}
