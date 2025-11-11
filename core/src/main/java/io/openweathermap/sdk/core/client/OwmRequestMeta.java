package io.openweathermap.sdk.core.client;

import lombok.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Value
@Builder
public class OwmRequestMeta {

    public static final String CACHE_HIT_ATTRIBUTE = "cache-hit";

    String endpointId;
    String cacheKey;
    Long ttlMillis;

    @Getter(AccessLevel.PRIVATE)
    Map<String, Object> attributes = new ConcurrentHashMap<>();

    public void setCacheHit() {
        attributes.put(CACHE_HIT_ATTRIBUTE, Boolean.TRUE);
    }

    public Boolean getCacheHit() {
        return attributes.containsKey(CACHE_HIT_ATTRIBUTE);
    }
}
