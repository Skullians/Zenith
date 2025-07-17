package net.skullian.zenith.core;

import net.skullian.zenith.core.event.bus.EventBus;
import net.skullian.zenith.core.event.bus.impl.EventBusImpl;
import net.skullian.zenith.core.flavor.Flavor;

/**
 * ZenithPlatform is an interface designed to represent the core platform functionality.
 * </p>
 * This is implemented by ZenithPlugin, ZenithMod, etc., which you would extend.
 */
public interface ZenithPlatform {

    /**
     * Get the instance of your platform.
     *
     * @return the {@link ZenithPlatform} instance.
     */
    static ZenithPlatform getInstance() {
        return PlatformHolder.platformInstance;
    }

    /**
     * Fetches the {@link EventBus} instance of this platform.
     * </p>
     * By default, all platform extensions will use {@link net.skullian.zenith.core.event.bus.impl.EventBusImpl},
     * however, feel free to implement your own.
     *
     * @return The {@link EventBus} instance.
     */
    default EventBus getEventBus() {
        return EventBusImpl.getInstance();
    }

    /**
     * Fetches the {@link Flavor} instance, allowing you to manage
     * service lifecycles and dependency injection.
     * </p>
     * Typically, you shouldn't need to access this yourself.
     * However, we expose it just in case.
     *
     * @return The {@link Flavor} instance.
     */
    Flavor getFlavor();

    /**
     * Reload the platform, subsequently runs the reload annotation
     * depending on the platform (e.g. PluginReload for paper)
     */
    void reload();

    class PlatformHolder {
        static ZenithPlatform platformInstance;
    }
}
