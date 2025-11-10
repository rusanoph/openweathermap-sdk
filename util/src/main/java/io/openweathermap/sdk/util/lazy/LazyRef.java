package io.openweathermap.sdk.util.lazy;

import java.util.Objects;
import java.util.function.Supplier;

public final class LazyRef<T> {

    private volatile T v;
    private final Supplier<? extends T> factory;

    public LazyRef(Supplier<? extends T> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public T get() {
        T x = v;
        if (x == null) {
            synchronized (this) {
                x = v;
                if (x == null) v = x = factory.get();
            }
        }
        return x;
    }
}
