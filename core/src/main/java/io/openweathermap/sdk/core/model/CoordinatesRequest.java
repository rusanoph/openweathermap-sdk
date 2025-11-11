package io.openweathermap.sdk.core.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CoordinatesRequest {

    Double lat;
    Double lon;

    @Builder.Default
    OwmUnits units = OwmUnits.METRIC;
    @Builder.Default
    OwmLanguage language = OwmLanguage.EN;
}
