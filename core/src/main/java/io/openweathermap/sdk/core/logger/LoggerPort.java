package io.openweathermap.sdk.core.logger;

import java.util.Map;
import java.util.function.Supplier;

public interface LoggerPort extends AutoCloseable {

    String getName();

    boolean isEnabled(LogLevel level);

    void log(LogLevel level, String message);

    void log(LogLevel level, String message, Throwable t);

    void log(LogLevel level, String format, Object... args);

    void log(LogLevel level, Throwable t, String format, Object... args);

    void log(LogLevel level, Supplier<String> messageSupplier);

    void log(LogLevel level, String message, Map<String, ?> fields);

    LoggerPort withContext(Map<String, ?> fields);

    default void flush() { /* Optional */ }

    @Override
    default void close() { /* Optional */ }

    default void trace(String msg) {
        log(LogLevel.TRACE, msg);
    }

    default void trace(String msg, Throwable t) {
        log(LogLevel.TRACE, msg, t);
    }

    default void tracef(String fmt, Object... args) {
        log(LogLevel.TRACE, fmt, args);
    }

    default void tracef(Throwable t, String fmt, Object... args) {
        log(LogLevel.TRACE, t, fmt, args);
    }

    default void trace(Supplier<String> msg) {
        log(LogLevel.TRACE, msg);
    }

    default void trace(String msg, Map<String, ?> fields) {
        log(LogLevel.TRACE, msg, fields);
    }

    default void debug(String msg) {
        log(LogLevel.DEBUG, msg);
    }

    default void debug(String msg, Throwable t) {
        log(LogLevel.DEBUG, msg, t);
    }

    default void debugf(String fmt, Object... args) {
        log(LogLevel.DEBUG, fmt, args);
    }

    default void debugf(Throwable t, String fmt, Object... args) {
        log(LogLevel.DEBUG, t, fmt, args);
    }

    default void debug(Supplier<String> msg) {
        log(LogLevel.DEBUG, msg);
    }

    default void debug(String msg, Map<String, ?> fields) {
        log(LogLevel.DEBUG, msg, fields);
    }

    default void info(String msg) {
        log(LogLevel.INFO, msg);
    }

    default void info(String msg, Throwable t) {
        log(LogLevel.INFO, msg, t);
    }

    default void infof(String fmt, Object... args) {
        log(LogLevel.INFO, fmt, args);
    }

    default void infof(Throwable t, String fmt, Object... args) {
        log(LogLevel.INFO, t, fmt, args);
    }

    default void info(Supplier<String> msg) {
        log(LogLevel.INFO, msg);
    }

    default void info(String msg, Map<String, ?> fields) {
        log(LogLevel.INFO, msg, fields);
    }

    default void warn(String msg) {
        log(LogLevel.WARN, msg);
    }

    default void warn(String msg, Throwable t) {
        log(LogLevel.WARN, msg, t);
    }

    default void warnf(String fmt, Object... args) {
        log(LogLevel.WARN, fmt, args);
    }

    default void warnf(Throwable t, String fmt, Object... args) {
        log(LogLevel.WARN, t, fmt, args);
    }

    default void warn(Supplier<String> msg) {
        log(LogLevel.WARN, msg);
    }

    default void warn(String msg, Map<String, ?> fields) {
        log(LogLevel.WARN, msg, fields);
    }

    default void error(String msg) {
        log(LogLevel.ERROR, msg);
    }

    default void error(String msg, Throwable t) {
        log(LogLevel.ERROR, msg, t);
    }

    default void errorf(String fmt, Object... args) {
        log(LogLevel.ERROR, fmt, args);
    }

    default void errorf(Throwable t, String fmt, Object... args) {
        log(LogLevel.ERROR, t, fmt, args);
    }

    default void error(Supplier<String> msg) {
        log(LogLevel.ERROR, msg);
    }

    default void error(String msg, Map<String, ?> fields) {
        log(LogLevel.ERROR, msg, fields);
    }

    default void fatal(String msg) {
        log(LogLevel.FATAL, msg);
    }

    default void fatal(String msg, Throwable t) {
        log(LogLevel.FATAL, msg, t);
    }

    default void fatalf(String fmt, Object... args) {
        log(LogLevel.FATAL, fmt, args);
    }

    default void fatalf(Throwable t, String fmt, Object... args) {
        log(LogLevel.FATAL, t, fmt, args);
    }

    default void fatal(Supplier<String> msg) {
        log(LogLevel.FATAL, msg);
    }

    default void fatal(String msg, Map<String, ?> fields) {
        log(LogLevel.FATAL, msg, fields);
    }

    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    }
}

