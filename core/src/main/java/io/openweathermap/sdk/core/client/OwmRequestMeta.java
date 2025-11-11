package io.openweathermap.sdk.core.client;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Value
@Builder
public class OwmRequestMeta {
    String endpointId;
    String cacheKey;
    Long ttlMillis;

    @Singular("attribute")
    Map<String, Objects> attributes = new ConcurrentHashMap<>();
}
