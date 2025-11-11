package io.openweathermap.sdk.core.logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JdkLoggerAdapter implements LoggerPort {

    private final Logger log;
    private final Map<String, Object> context;

    private static final StackWalker CALLER_WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private JdkLoggerAdapter(Logger jul, Map<String, ?> context) {
        this.log = Objects.requireNonNull(jul, "logger");
        this.context = context == null || context.isEmpty()
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(context));
    }

    public static JdkLoggerAdapter of(Logger log) {
        return new JdkLoggerAdapter(log, Map.of());
    }

    public static JdkLoggerAdapter get(String name) {
        return of(Logger.getLogger(name));
    }

    @Override
    public String getName() {
        return log.getName();
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return log.isLoggable(toJul(level));
    }

    @Override
    public void log(LogLevel level, String message) {
        if (!isEnabled(level)) return;
        logRecord(level, buildMessage(message, null));
    }

    @Override
    public void log(LogLevel level, String message, Throwable t) {
        if (!isEnabled(level)) return;
        logRecord(level, buildMessage(message, null), t);
    }

    @Override
    public void log(LogLevel level, String format, Object... args) {
        if (!isEnabled(level)) return;
        String msg = safeFormat(format, args);
        logRecord(level, buildMessage(msg, null));
    }

    @Override
    public void log(LogLevel level, Throwable t, String format, Object... args) {
        if (!isEnabled(level)) return;
        String msg = safeFormat(format, args);
        logRecord(level, buildMessage(msg, null), t);
    }

    @Override
    public void log(LogLevel level, Supplier<String> messageSupplier) {
        if (!isEnabled(level)) return;
        String msg = String.valueOf(messageSupplier.get());
        logRecord(level, buildMessage(msg, null));
    }

    @Override
    public void log(LogLevel level, String message, Map<String, ?> fields) {
        if (!isEnabled(level)) return;
        logRecord(level, buildMessage(message, fields));
    }

    @Override
    public LoggerPort withContext(Map<String, ?> fields) {
        if (fields == null || fields.isEmpty()) {
            return this;
        }

        Map<String, Object> merged = new LinkedHashMap<>(this.context);

        fields.forEach((k, v) -> {
            if (v == null) {
                merged.remove(k);
            } else {
                merged.put(k, v);
            }
        });
        return new JdkLoggerAdapter(log, merged);
    }

    @Override
    public void flush() {
        for (Handler h : log.getHandlers()) {
            try { h.flush(); } catch (RuntimeException ignore) {}
        }
    }

    @Override
    public void close() {
        flush();
    }

    // ===== Internal helpers =====

    private void logRecord(LogLevel level, String message) {
        logRecord(level, message, null);
    }

    private void logRecord(LogLevel level, String message, Throwable t) {
        Level jlevel = toJul(level);
        CallerInfo caller = findCaller();

        LogRecord rec = new LogRecord(jlevel, message);
        if (caller != null) {
            rec.setSourceClassName(caller.className);
            rec.setSourceMethodName(caller.methodName);
            rec.setLoggerName(log.getName());
        }

        rec.setThrown(t);
        log.log(rec);
    }

    private String buildMessage(String base, Map<String, ?> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append(base == null ? "null" : base);

        if (!context.isEmpty()) {
            sb.append(" | ctx=").append(kvJoin(context));
        }

        if (fields != null && !fields.isEmpty()) {
            sb.append(" | fields=").append(kvJoin(fields));
        }
        return sb.toString();
    }

    private static String kvJoin(Map<String, ?> map) {
        StringJoiner j = new StringJoiner(",", "{", "}");
        map.forEach((k, v) -> j.add(escapeKey(k) + "=" + String.valueOf(v)));
        return j.toString();
    }

    private static String escapeKey(String k) {
        if (k == null) return "null";
        return k.replace("=", "\\=").replace(",", "\\,").replace(" ", "_");
    }

    private static String safeFormat(String fmt, Object... args) {
        try {
            return String.format(Locale.ROOT, fmt, args);
        } catch (Exception e) {
            return fmt + " " + Arrays.toString(args);
        }
    }

    private static Level toJul(LogLevel lvl) {
        return switch (lvl) {
            case TRACE -> Level.FINER;
            case DEBUG -> Level.FINE;
            case INFO  -> Level.INFO;
            case WARN  -> Level.WARNING;
            case ERROR, FATAL -> Level.SEVERE;
        };
    }

    private CallerInfo findCaller() {
        return CALLER_WALKER.walk(stream ->
                stream.dropWhile(f -> f.getClassName().equals(JdkLoggerAdapter.class.getName())
                                || f.getClassName().equals(LoggerPort.class.getName()))
                        .findFirst()
                        .map(f -> new CallerInfo(f.getClassName(), f.getMethodName()))
                        .orElse(null)
        );
    }

    private record CallerInfo(String className, String methodName) {}
}
