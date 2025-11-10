package io.openweathermap.sdk.core.model;

public enum OwmUnits {

    STANDARD(),
    METRIC(),
    IMPERIAL()
    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
