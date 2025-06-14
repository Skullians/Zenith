package net.skullian.zenith.core.event.bus;

import net.skullian.zenith.core.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to mark methods that should be invoked when a method is fired.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    /**
     * Whether the method should be invoked if the event was previously cancelled.
     * If this is set to true, the method will not be invoked.
     */
    boolean ignoreCancelled() default false;

    /**
     * The priority of the event, determined by {@link EventPriority}.
     * This determines the order that event listeners receive the event.
     *
     * @apiNote {@link EventPriority#MONITOR} should only be used for monitoring data, not modifying.
     */
    EventPriority priority() default EventPriority.NORMAL;
}
