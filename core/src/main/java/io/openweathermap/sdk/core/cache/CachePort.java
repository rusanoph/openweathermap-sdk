package io.openweathermap.sdk.core.cache;

import java.util.Optional;

public interface CachePort<K, V> {

    Optional<CacheEntry<V>> get(K key);

    void put(K key, CacheEntry<V> entry);

    void invalidate(K key);
}
