package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.client.EndpointHelper;
import io.openweathermap.sdk.core.http.HttpRequest;
import io.openweathermap.sdk.core.model.geo.GeoCity;
import io.openweathermap.sdk.util.http.QueryBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class GeoApiImpl implements GeoApi {

    private static final String PATH = "/geo/1.0/direct";
    private static final long REQUEST_TTL_MS = 45 * 1000L;

    private final EndpointHelper h;

    @Override
    public CompletableFuture<List<GeoCity>> directAsync(String q, int limit) {
        QueryBuilder qb = new QueryBuilder()
                .add("q", q)
                .add("limit", Math.max(1, limit));

        URI uri = h.uri(PATH, qb);
        HttpRequest req = h.buildGet(uri);
        String key = EndpointHelper.key(PATH, qb);

        return h.getWithCacheAsync(req, key, REQUEST_TTL_MS)
                .thenCompose(bytes -> h.decodeAsync(bytes, GeoCity[].class))
                .thenApply(List::of);
    }
}
