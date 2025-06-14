package net.skullian.zenith.core.event.bus;

import net.skullian.zenith.core.event.ZenithEvent;
import net.skullian.zenith.core.event.ZenithListener;

/**
 * The event bus for Zenith.
 * This will handle all {@link ZenithEvent}s,
 * and will be where you register your {@link ZenithListener}s.
 * <p>
 * You can either implement this interface manually or
 * use the built-in {@link net.skullian.zenith.core.event.bus.impl.EventBusImpl}.
 */
public interface EventBus {

    void subscribe(ZenithListener listener);

    void unsubscribe(ZenithListener listener);

    void emit(ZenithEvent event);
}
