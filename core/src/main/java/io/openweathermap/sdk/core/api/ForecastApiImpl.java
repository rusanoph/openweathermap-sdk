package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.client.OwmEndpointHelper;
import io.openweathermap.sdk.core.client.OwmRequestMeta;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.forecast.Forecast5d;
import io.openweathermap.sdk.util.http.QueryBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ForecastApiImpl implements ForecastApi {

    private static final String PATH = "/data/2.5/forecast";
    private static final long REQUEST_TTL_MS = 10 * 60 * 1000L;

    private final OwmEndpointHelper h;

    @Override
    public CompletableFuture<Forecast5d> byCoordsAsync(CoordinatesRequest request) {
        QueryBuilder qb = new QueryBuilder()
                .add("lat", request.getLat())
                .add("lon", request.getLon())
                .add("units", request.getUnits())
                .add("lang", request.getLanguage());


        URI uri = h.uri(PATH, qb);
        HttpRequest req = h.buildGet(uri);
        String key = OwmEndpointHelper.key(PATH, qb);

        OwmRequestMeta meta = OwmRequestMeta.builder()
                .endpointId(PATH)
                .cacheKey(key)
                .ttlMillis(REQUEST_TTL_MS)
                .build();

        return h.executeAsync(req, meta)
                .thenCompose(bytes -> h.decodeAsync(bytes.getBody(), Forecast5d.class));
    }
}
