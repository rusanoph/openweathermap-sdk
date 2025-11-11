package io.openweathermap.sdk.core.model.air;

import java.util.List;

public record AirPollution(List<Item> list) {

    public record Item(
            Long dt,
            Main main,
            Components components
    ) {
    }

    public record Main(Integer aqi) {
    }

    public record Components(
            Double co,
            Double no,
            Double no2,
            Double o3,
            Double so2,
            Double pm2_5,
            Double pm10,
            Double nh3
    ) {
    }
}
