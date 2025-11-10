package io.openweathermap.sdk.core.client;

import io.openweathermap.sdk.core.cache.CachePort;
import io.openweathermap.sdk.core.http.HttpClientPort;
import io.openweathermap.sdk.core.serializer.SerializerPort;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OwmRuntime {

    @NonNull
    private final OwmClientConfig config;
    @NonNull
    private final HttpClientPort http;
    @NonNull
    private final SerializerPort ser;
    @NonNull
    private final CachePort<String, byte[]> cache;
}
