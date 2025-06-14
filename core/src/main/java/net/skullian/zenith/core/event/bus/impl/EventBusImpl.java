package net.skullian.zenith.core.event.bus.impl;

import net.skullian.zenith.core.event.EventPriority;
import net.skullian.zenith.core.event.ZenithEvent;
import net.skullian.zenith.core.event.ZenithListener;
import net.skullian.zenith.core.event.bus.EventBus;
import net.skullian.zenith.core.event.bus.Subscribe;
import net.skullian.zenith.core.flavor.annotation.IgnoreAutoScan;
import net.skullian.zenith.core.flavor.annotation.Service;
import net.skullian.zenith.core.flavor.annotation.inject.Inject;
import net.skullian.zenith.core.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zenith's built-in implementation of {@link EventBus}.
 */
@Service(
        name = "Zenith EventBus"
)
@IgnoreAutoScan
public class EventBusImpl implements EventBus {
    private static final EventBusImpl instance = new EventBusImpl();

    private final Map<ZenithListener, List<Method>> listeners = new ConcurrentHashMap<>();

    @Inject
    public Logger logger;

    @Override
    public void subscribe(ZenithListener listener) {
        List<Method> methods = new ArrayList<>();

        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)
                    && method.getParameters().length != 1
                    && method.getParameters()[0].getType().isAssignableFrom(ZenithEvent.class)) methods.add(method);
        }

        if (methods.isEmpty()) throw new IllegalArgumentException("Could not find any public methods annotated with @Subscribe in listener " + listener.getClass().getSimpleName());
        logger.info("Registered listener {} with {} subscribe methods.", listener.getClass().getSimpleName(), methods.size());

        listeners.put(listener, methods);
    }

    @Override
    public void unsubscribe(ZenithListener listener) {
        logger.info("Unregistered listener {}", listener.getClass().getSimpleName());
        listeners.remove(listener);
    }

    @Override
    public void emit(ZenithEvent event) {
        for (EventPriority priority : EventPriority.values()) {
            for (Map.Entry<ZenithListener, List<Method>> entry : listeners.entrySet()) {
                for (Method method : entry.getValue()) {
                    Subscribe annotation = method.getAnnotation(Subscribe.class);
                    if (annotation == null) continue; // this should never happen

                    if (annotation.priority() != priority) continue;
                    invoke(method, entry.getKey(), event);
                }
            }
        }
    }

    private void invoke(Method method, ZenithListener listener, Object... args) {
        method.setAccessible(true);

        try {
            method.invoke(
                    listener,
                    args
            );
        } catch (InvocationTargetException e) {
            logger.error("Failed to access method {} on listener {}, despite attempting to mark it accessible.", e, method.getName(), listener.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            logger.error("Failed to invoke method {} on listener {}.", e, method.getName(), listener.getClass().getSimpleName());
        }
    }

    public static EventBusImpl getInstance() {
        return instance;
    }
}
