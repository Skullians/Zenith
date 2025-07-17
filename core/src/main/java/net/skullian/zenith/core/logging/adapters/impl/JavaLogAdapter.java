package net.skullian.zenith.core.logging.adapters.impl;

import net.skullian.zenith.core.logging.LogLevel;
import net.skullian.zenith.core.logging.adapters.LogAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.helpers.MessageFormatter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A built-in logging adapter for the built-in Java {@link Logger} class.
 */
public class JavaLogAdapter implements LogAdapter {

    private final Logger logger;

    public JavaLogAdapter(@NotNull Logger logger) {
        this.logger = logger;
    }

    public JavaLogAdapter(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    @Override
    public void log(@NotNull LogLevel level, String message, Object... args) {
        message = MessageFormatter.arrayFormat(message, args).getMessage();

        switch (level) {
            case DEBUG:
                logger.log(Level.FINE, message);
                break;
            case INFO:
                logger.log(Level.INFO, message);
                break;
            case WARN:
                logger.log(Level.WARNING, message);
                break;
            case ERROR:
                logger.log(Level.SEVERE, message);
                break;
        }
    }

    @Override
    public void log(@NotNull LogLevel level, String message, Throwable throwable, Object... args) {
        message = MessageFormatter.arrayFormat(message, args).getMessage();

        switch (level) {
            case DEBUG:
                logger.log(Level.FINE, message, throwable);
                break;
            case INFO:
                logger.log(Level.INFO, message, throwable);
                break;
            case WARN:
                logger.log(Level.WARNING, message, throwable);
                break;
            case ERROR:
                logger.log(Level.SEVERE, message, throwable);
                break;
        }
    }
}
