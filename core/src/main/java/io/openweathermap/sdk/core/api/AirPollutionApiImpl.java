package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.client.EndpointHelper;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.model.air.AirPollution;
import io.openweathermap.sdk.util.http.QueryBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class AirPollutionApiImpl implements AirPollutionApi {

    private static final String PATH = "/data/2.5/air_pollution";
    private static final long TTL_MS = 45 * 1000L;

    private final EndpointHelper h;

    @Override
    public CompletableFuture<AirPollution> nowAsync(double lat, double lon) {
        QueryBuilder qb = new QueryBuilder().add("lat", lat).add("lon", lon);
        URI uri = h.uri(PATH, qb);
        HttpRequest req = h.buildGet(uri);
        String key = EndpointHelper.key(PATH, qb);

        return h.getWithCacheAsync(req, key, TTL_MS)
                .thenCompose(bytes -> h.decodeAsync(bytes, AirPollution.class));
    }
}
