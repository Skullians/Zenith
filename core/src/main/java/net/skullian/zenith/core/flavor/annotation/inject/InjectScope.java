package net.skullian.zenith.core.flavor.annotation.inject;

import java.lang.reflect.Field;
import java.util.function.Function;

public enum InjectScope {

    SINGLETON(clazz -> {
        Field singleton;

        try {
            singleton = clazz.getDeclaredField("instance");
        } catch (NoSuchFieldException e) {
            try {
                singleton = clazz.getDeclaredField("INSTANCE");
            }  catch (NoSuchFieldException ex) {
                throw new RuntimeException("Failed to fetch singleton instance for class %s! Do you have an accessible field (\"INSTANCE\" or \"instance\") containing a singleton of the class?"
                        .formatted(clazz.getSimpleName()), ex);
            }
        }

        try {
            return singleton.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get value of singleton field %s for service class %s."
                    .formatted(singleton != null ? singleton.getName() : "NULL", clazz.getSimpleName()), e);
        }
    }),

    NO_SCOPE(clazz -> null);

    public final Function<Class<?>, Object> instanceCreator;

    InjectScope(Function<Class<?>, Object> instanceCreator) {
        this.instanceCreator = instanceCreator;
    }
}
