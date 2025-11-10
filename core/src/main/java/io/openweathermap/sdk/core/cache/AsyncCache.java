package io.openweathermap.sdk.core.cache;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

public final class AsyncCache<K,V> {

    private final CachePort<K,V> delegate;
    private final Executor executor;

    public AsyncCache(CachePort<K,V> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor != null ? executor : ForkJoinPool.commonPool();
    }

    public CompletableFuture<Optional<CacheEntry<V>>> getEntryAsync(K key) {
        return CompletableFuture.supplyAsync(() -> delegate.get(key), executor);
    }

    public CompletableFuture<Void> putAsync(K key, CacheEntry<V> entry) {
        return CompletableFuture.runAsync(() -> delegate.put(key, entry), executor);
    }

    public CompletableFuture<Void> invalidateAsync(K key) {
        return CompletableFuture.runAsync(() -> delegate.invalidate(key), executor);
    }
}
