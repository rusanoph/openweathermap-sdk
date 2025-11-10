package io.openweathermap.sdk.core.model.geo;

public record GeoCity(
        String name,
        String country,
        String state,
        Double lat,
        Double lon
) {
}
