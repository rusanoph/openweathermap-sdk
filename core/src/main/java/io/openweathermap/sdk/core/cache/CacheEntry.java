package io.openweathermap.sdk.core.cache;

public record CacheEntry<V>(
        V value,
        long expiresAtEpochMillis
//        boolean allowStaleOnError
) {

    public boolean isExpired(long now) {
        return now >= expiresAtEpochMillis;
    }
}
