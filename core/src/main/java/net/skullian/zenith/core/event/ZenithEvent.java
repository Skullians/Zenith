package net.skullian.zenith.core.event;

import net.skullian.zenith.core.ZenithPlatform;

/**
 * Marks a class as a {@link ZenithEvent}
 * Can be used in conjunction with classes like {@link Cancellable}
 * for extra functionality.
 */
public abstract class ZenithEvent {
    public boolean callEvent() {
        return ZenithPlatform.getInstance().getEventBus().emit(this);
    }
}
