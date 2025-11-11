package io.openweathermap.sdk.core.api;

import io.openweathermap.sdk.core.model.geo.GeoCity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GeoApi {
    CompletableFuture<List<GeoCity>> directAsync(String q, int limit);

    default List<GeoCity> direct(String q, int limit) {
        return directAsync(q, limit).join();
    }
}
