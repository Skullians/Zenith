package net.skullian.zenith.core.flavor;

import net.skullian.zenith.core.logging.Logger;
import net.skullian.zenith.core.logging.adapters.LogAdapter;
import net.skullian.zenith.core.logging.adapters.impl.JavaLogAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FlavorOptions {
    private final Logger logger;
    private final String mainPackage;

    public FlavorOptions(@NotNull LogAdapter adapter, String mainPackage) {
        this.logger = new Logger(adapter);
        this.mainPackage = mainPackage;
    }

    public FlavorOptions(LogAdapter adapter) {
        this(adapter, null);
    }

    public FlavorOptions() {
        this(new JavaLogAdapter("Zenith"));
    }

    public Logger getLogger() {
        return this.logger;
    }

    public String getMainPackage() {
        return Objects.requireNonNull(this.mainPackage);
    }

}
