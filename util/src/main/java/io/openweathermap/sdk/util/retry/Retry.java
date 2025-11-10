package io.openweathermap.sdk.util.retry;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Retry {

    public record Policy(
            int attempts,
            Duration baseDelay,
            Duration maxDelay,
            double jitter,
            Predicate<Throwable> retryIf
    ) {}

    public static <T> CompletableFuture<T> withRetryAsync(
            Supplier<CompletableFuture<T>> op,
            Policy policy,
            ScheduledExecutorService scheduler
    ) {
        CompletableFuture<T> promise = new CompletableFuture<>();
        attempt(op, policy, scheduler, 1, promise);
        return promise;
    }

    private static <T> void attempt(
            Supplier<CompletableFuture<T>> op,
            Policy p,
            ScheduledExecutorService sch,
            int n,
            CompletableFuture<T> out
    ) {
        op.get().whenComplete((v, e) -> {
            if (e == null) { out.complete(v); return; }
            Throwable cause = unwrap(e);
            if (n >= p.attempts || !p.retryIf.test(cause)) {
                out.completeExceptionally(cause); return;
            }
            long delayMs = backoffMillis(p.baseDelay, p.maxDelay, p.jitter, n);
            sch.schedule(() -> attempt(op, p, sch, n + 1, out), delayMs, TimeUnit.MILLISECONDS);
        });
    }

    private static Throwable unwrap(Throwable t) {
        if (t instanceof CompletionException || t instanceof ExecutionException) {
            return t.getCause() != null ? t.getCause() : t;
        }
        return t;
    }

    private static long backoffMillis(Duration base, Duration max, double jitter, int attemptIdx) {
        long raw = (long) (base.toMillis() * Math.pow(2, attemptIdx - 1));
        long capped = Math.min(raw, max.toMillis());
        long jitterMs = (long) (capped * jitter * Math.random());
        return capped + jitterMs;
    }
}
