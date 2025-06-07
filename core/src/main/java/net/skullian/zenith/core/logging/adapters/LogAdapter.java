package net.skullian.zenith.core.logging.adapters;

import net.skullian.zenith.core.logging.LogLevel;
import net.skullian.zenith.core.logging.adapters.impl.JavaLogAdapter;
import net.skullian.zenith.core.logging.adapters.impl.Slf4jLogAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Acts as an interface for adapting platform-specific loggers to Zenith's logging system.
 * Zenith provides you with two built-in adapters: {@link JavaLogAdapter} and {@link Slf4jLogAdapter}.}
 */
public interface LogAdapter {
    void log(@NotNull LogLevel level, String message, Object... args);

    void log(@NotNull LogLevel level, String message, Throwable throwable, Object... args);
}
