package io.openweathermap.sdk.core.model.weather;

import java.util.List;

public record Weather(
        Long dt,
        Integer timezone,
        String name,
        Main main,
        Wind wind,
        Clouds clouds,
        List<Item> weather
) {
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

    public record Clouds(Integer all) {
    }

    public record Item(
            Integer id,
            String main,
            String description,
            String icon
    ) {
    }

}
