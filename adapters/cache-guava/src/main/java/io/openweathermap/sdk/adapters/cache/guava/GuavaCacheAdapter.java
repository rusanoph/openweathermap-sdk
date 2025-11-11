package io.openweathermap.sdk.adapters.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.openweathermap.sdk.core.cache.CacheEntry;
import io.openweathermap.sdk.core.cache.CachePort;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GuavaCacheAdapter implements CachePort<String, byte[]> {
    private final Cache<String, CacheEntry<byte[]>> cache;

    public GuavaCacheAdapter(long maxSize) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build();
    }

    @Override
    public Optional<CacheEntry<byte[]>> get(String key) {
        CacheEntry<byte[]> e = cache.getIfPresent(key);
        var now = Instant.now().toEpochMilli();

        return switch (e) {
            case null -> Optional.empty();
            case CacheEntry<byte[]> ce when ce.isExpired(now) -> {
                cache.invalidate(key);
                yield  Optional.empty();
            }
            default -> Optional.of(e);
        };
    }

    @Override
    public void put(String key, CacheEntry<byte[]> entry) {
        cache.put(key, entry);
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }
}
