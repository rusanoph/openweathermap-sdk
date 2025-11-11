package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.client.OwmEndpointHelper;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.model.CoordinatesRequest;
import io.openweathermap.sdk.core.model.air.AirPollution;
import io.openweathermap.sdk.util.http.QueryBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class AirPollutionApiImpl implements AirPollutionApi {

    private static final String PATH = "/data/2.5/air_pollution";
    private static final long TTL_MS = 45 * 1000L;

    private final OwmEndpointHelper h;

    @Override
    public CompletableFuture<AirPollution> nowAsync(CoordinatesRequest request) {
        QueryBuilder qb = new QueryBuilder()
                .add("lat", request.getLat())
                .add("lon", request.getLon());

        URI uri = h.uri(PATH, qb);
        HttpRequest req = h.buildGet(uri);
        String key = OwmEndpointHelper.key(PATH, qb);

        return h.getWithCacheAsync(req, key, TTL_MS)
                .thenCompose(bytes -> h.decodeAsync(bytes, AirPollution.class));
    }
}
