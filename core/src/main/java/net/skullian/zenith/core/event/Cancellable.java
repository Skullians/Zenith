package net.skullian.zenith.core.event;

/**
 * Implemented alongside {@link ZenithEvent} to allow for the event to be cancelled.
 */
public interface Cancellable {

    boolean cancelled = false;

    /**
     * Is the event marked as cancelled?
     * @return true if the event is cancelled, false otherwise.
     */
    boolean isCancelled();

    /**
     * Modify the cancellation state of the event.
     * @param cancelled The new cancellation state.
     */
    void setCancelled(boolean cancelled);

}
