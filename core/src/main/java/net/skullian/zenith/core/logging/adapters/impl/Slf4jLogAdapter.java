package net.skullian.zenith.core.logging.adapters.impl;

import net.skullian.zenith.core.logging.LogLevel;
import net.skullian.zenith.core.logging.adapters.LogAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * A simple built-in log adapter for the Slf4j {@link Logger}.
 */
public class Slf4jLogAdapter implements LogAdapter {

    private final Logger logger;

    public Slf4jLogAdapter(@NotNull Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(@NotNull LogLevel level, String message, Object... args) {
        switch (level) {
            case DEBUG:
                logger.debug(message, args);
                break;
            case INFO:
                logger.info(message, args);
                break;
            case WARN:
                logger.warn(message, args);
                break;
            case ERROR:
                logger.error(message, args);
                break;
        }
    }

    @Override
    public void log(@NotNull LogLevel level, String message, Throwable throwable, Object... args) {
        switch (level) {
            case DEBUG:
                logger.debug(message, throwable, args);
                break;
            case INFO:
                logger.info(message, throwable, args);
                break;
            case WARN:
                logger.warn(message, throwable, args);
                break;
            case ERROR:
                logger.error(message, throwable, args);
                break;
        }
    }
}
