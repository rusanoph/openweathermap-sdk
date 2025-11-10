package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.OwmLanguage;
import io.openweathermap.sdk.core.model.OwmUnits;
import io.openweathermap.sdk.core.client.EndpointHelper;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.model.forecast.Forecast5d;
import io.openweathermap.sdk.util.http.QueryBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ForecastApiImpl implements ForecastApi {

    private static final String PATH = "/data/2.5/forecast";
    private static final long REQUEST_TTL_MS = 10 * 60 * 1000L;

    private final EndpointHelper h;

    @Override
    public CompletableFuture<Forecast5d> byCoordsAsync(double lat, double lon, OwmUnits units, OwmLanguage lang) {
        QueryBuilder qb = h.defaultsWithFallback(lat, lon, units, lang);

        URI uri = h.uri(PATH, qb);
        HttpRequest req = h.buildGet(uri);
        String key = EndpointHelper.key(PATH, qb);

        return h.getWithCacheAsync(req, key, REQUEST_TTL_MS)
                .thenCompose(bytes -> h.decodeAsync(bytes, Forecast5d.class));
    }
}
