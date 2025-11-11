package io.openweathermap.sdk.core.model.forecast;

import java.util.List;

public record Forecast5d(
        City city,
        List<Item> list
) {
    public record City(
            String name,
            String country,
            Integer timezone,
            Coordinates coord
    ) {
        public record Coordinates(
                Double lat,
                Double lon
        ) {}
    }

    public record Item(
            Long dt,
            Main main,
            List<Weather> weather,
            Wind wind,
            Double pop
    ) {
    }

    public record Main(
            Double temp,
            Double feelsLike,
            Integer pressure,
            Integer humidity
    ) {
    }

    public record Wind(
            Double speed,
            Integer deg
    ) {
    }

    public record Weather(
            Integer id,
            String main,
            String description,
            String icon
    ) {
    }
}
