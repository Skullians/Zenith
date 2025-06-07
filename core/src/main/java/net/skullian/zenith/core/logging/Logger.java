package net.skullian.zenith.core.logging;

import net.skullian.zenith.core.logging.adapters.LogAdapter;
import org.jetbrains.annotations.NotNull;

public class Logger {

    private final LogAdapter adapter;
    private LogLevel level = LogLevel.INFO;

    public Logger(@NotNull LogAdapter adapter) {
        this.adapter = adapter;
    }

    public LogLevel getLevel() {
        return this.level;
    }

    public void setLevel(@NotNull LogLevel level) {
        this.level = level;
    }

    private boolean levelAllowed(LogLevel level) {
        return level.compareTo(this.level) >= 0;
    }

    public void log(LogLevel level, String message, Object... args) {
        if (levelAllowed(level)) {
            this.adapter.log(level, message, args);
        }
    }

    public void log(LogLevel level, String message, Throwable throwable, Object... args) {
        if (levelAllowed(level)) {
            this.adapter.log(level, message, throwable, args);
        }
    }

    public void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    public void info(String message, Throwable throwable, Object... args) {
        log(LogLevel.INFO, message, throwable, args);
    }

    public void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    public void warn(String message, Throwable throwable, Object... args) {
        log(LogLevel.WARN, message, throwable, args);
    }

    public void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }

    public void error(String message, Throwable throwable, Object... args) {
        log(LogLevel.ERROR, message, throwable, args);
    }

    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    public void debug(String message, Throwable throwable, Object... args) {
        log(LogLevel.DEBUG, message, throwable, args);
    }
}
