package net.skullian.zenith.core.flavor;

import net.skullian.zenith.core.flavor.annotation.Configure;
import net.skullian.zenith.core.flavor.annotation.IgnoreAutoScan;
import net.skullian.zenith.core.flavor.annotation.Service;
import net.skullian.zenith.core.flavor.binder.FlavorBinder;
import net.skullian.zenith.core.flavor.binder.FlavorBinderContainer;
import net.skullian.zenith.core.flavor.exception.FlavorException;
import net.skullian.zenith.core.reflection.ReflectionsUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Flavor is a lightweight Kotlin IoC container and lifecycle management framework,
 * ported to Java.
 */
public class Flavor {

    private final FlavorOptions options;
    private final ReflectionsUtil reflections;

    private final List<FlavorBinder<?>> binders = new ArrayList<>();
    private final Map<Class<?>, Object> services = new HashMap<>();

    private final Map<Class<? extends Annotation>, BiConsumer<Method, Object>> scanners = new HashMap<>();

    public Flavor(Class<?> initializer, FlavorOptions options) {
        this.options = options;
        this.reflections = new ReflectionsUtil(initializer, options.getMainPackage());
    }

    public static <T> Flavor create(T initializer, FlavorOptions options) {
        return new Flavor(initializer.getClass(), options);
    }

    public static Flavor create(Class<?> initializer, FlavorOptions options) {
        return new Flavor(initializer, options);
    }

    // ---- Methods ---- //

    /**
     * Registers a listener for methods annotated with a specific annotation. This listener will
     * be invoked for each method discovered with the given annotation.
     *
     * @param <T>       the type of the annotation
     * @param annotation the annotation class to listen for
     * @param lambda     the function that will be executed when a method with the annotated
     *                   signature is found. The `lambda` takes the annotated `Method` and an
     *                   `Object` instance (or null for static methods) as parameters.
     */
    public <T extends Annotation> void listen(Class<T> annotation, BiConsumer<Method, Object> lambda) {
        scanners.put(annotation, lambda);
    }

    /**
     * Inherits binders from the provided {@link FlavorBinderContainer} into the current Flavor instance.
     * This method calls the {@link FlavorBinderContainer#populate()} method to ensure the container
     * is populated, then merges its binders into the current Flavor instance.
     *
     * @param container the FlavorBinderContainer containing the binders to be inherited
     * @return the current Flavor instance for method chaining
     */
    public Flavor inherit(FlavorBinderContainer container) {
        container.populate();
        binders.addAll(container.getBinders());
        return this;
    }

    /**
     * Retrieves a service instance of the specified type from the Flavor container.
     * If no service of the specified type is found, an {@link IllegalArgumentException} is thrown.
     *
     * @param <T>   the type of the service to retrieve
     * @param clazz the class of the service to retrieve
     * @return an instance of the specified service type
     * @throws IllegalArgumentException if the requested service type is not found in the container
     */
    @SuppressWarnings("unchecked")
    public <T> T service(Class<T> clazz) {
        Object service = services.get(clazz);
        if (service == null) throw new IllegalArgumentException("Service %s not found".formatted(clazz.getSimpleName()));
        return (T) service;
    }

    /**
     * Binds a given class type to a new {@link FlavorBinder} instance.
     * The created binder is registered within the current instance of Flavor,
     * allowing further configurations and bindings for the specified class.
     *
     * @param <T>   the type of the class to be bound
     * @param clazz the {@link Class} object representing the type to be bound
     * @return a new {@link FlavorBinder} instance associated with the specified class
     */
    public <T> FlavorBinder<T> bind(Class<T> clazz) {
        FlavorBinder<T> binder = new FlavorBinder<>(clazz);
        binders.add(binder);

        return binder;
    }

    /**
     * Finds and retrieves a list of singleton object instances for all classes annotated
     * with the specified annotation. A singleton instance is determined by an accessible
     * "instance" field in annotated classes.
     *
     * @param <A> the type of the annotation
     * @param annotation the annotation class used to identify annotated classes
     * @return a list of singleton object instances of classes annotated with the specified annotation
     */
    public <A extends Annotation> List<Object> findSingletons(Class<A> annotation) {
        return reflections.getTypesAnnotatedWith(annotation).stream()
                .map(Flavor::objectInstance)
                .filter(it -> it != null && it.getClass().isAnnotationPresent(annotation))
                .toList();
    }

    /**
     * Creates and returns an instance of the specified class type, optionally injecting
     * dependencies or parameters provided. The method first attempts to create a new instance
     * using the class type and its constructor, with or without parameters depending on the
     * provided arguments. After instantiation, additional injections are applied if necessary.
     *
     * @param <T>        the type of the class to instantiate
     * @param clazz      the {@link Class} object representing the type to instantiate
     * @param parameters the optional list of parameters to pass to the constructor
     *                   of the class being instantiated
     * @return an instance of the specified type, with any injections applied
     * @throws FlavorException if the instantiation or injection process fails
     */
    public <T> T injected(Class<T> clazz, Object... parameters) {
        T instance;
        try {
            if (parameters.length == 0) {
                instance = clazz.getDeclaredConstructor().newInstance();
            } else {
                instance = clazz.getDeclaredConstructor(
                        Arrays.stream(parameters)
                                .map(Object::getClass)
                                .toArray(Class[]::new)
                ).newInstance(Arrays.stream(parameters).toArray());
            }
        } catch (Exception e) {
            throw new FlavorException("Failed to inject class %s with parameters %s".formatted(clazz.getSimpleName(), Arrays.toString(parameters)), e);
        }

        inject(instance);
        return instance;
    }

    /**
     * Injects fields into a pre-existing class.
     * @param instance The instance to inject.
     */
    public void inject(Object instance) {
        scanAndInject(instance.getClass(), instance);
    }

    /**
     * Called on platform startup.
     * This will automatically initialise the service lifecycle,
     * as well as dependency injection, according to priority.
     */
    public void startup() {
        List<Class<?>> classes = reflections
                .getTypesAnnotatedWith(Service.class)
                .stream()
                .sorted(Comparator.comparingInt((Class<?> clazz) -> {
                    Service service = clazz.getAnnotation(Service.class);
                    return service != null ? service.priority() : 1;
                }).reversed())
                .toList();

        for (Class<?> clazz : classes) {
            try {
                if (!clazz.isAnnotationPresent(IgnoreAutoScan.class)) {
                    scanAndInject(clazz, objectInstance(clazz));
                }
            } catch (Exception e) {
                throw new FlavorException("Failed to start service %s".formatted(clazz.getSimpleName()), e);
            }
        }
    }

    /**
     * Scans the given class for fields and methods that require dependency injection or other annotations
     * and processes them. It initializes and injects dependencies, invokes lifecycle methods,
     * and registers services where appropriate.
     *
     * @param clazz    the {@link Class} object to scan for fields and methods
     * @param instance an optional pre-existing instance of the class; if null, a new instance will
     *                 be created using the singleton field or constructor
     * @throws FlavorException if dependency injection or lifecycle method invocation fails
     */
    private void scanAndInject(Class<?> clazz, Object instance) {
        Object singleton = instance != null ? instance : objectInstance(clazz);
        if (singleton == null) return;

        for (Field field : clazz.getDeclaredFields()) {
            List<FlavorBinder<?>> bindersOfType = binders.stream()
                    .filter(it -> it.instance.getClass().isAssignableFrom(field.getType()))
                    .collect(Collectors.toList());

            for (FlavorBinder<?> binder : bindersOfType) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    Predicate<Annotation> predicate = binder.annotationCheck(annotation.getClass());
                    boolean passes = predicate == null || predicate.test(annotation);

                    if (!passes) {
                        bindersOfType.remove(binder);
                    }
                }
            }

            FlavorBinder<?> binder = bindersOfType.getFirst();
            boolean canAccess = field.canAccess(singleton);

            if (binder == null) continue;

            try {
                field.setAccessible(true);
                field.set(singleton, binder.instance);
                field.setAccessible(canAccess);
            } catch (IllegalAccessException e) {
                throw new FlavorException("Failed to inject field %s".formatted(field.getName()), e);
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            List<Annotation> annotations = Arrays.stream(method.getDeclaredAnnotations())
                    .filter(it -> scanners.containsKey(it.getClass()))
                    .toList();

            for (Annotation annotation : annotations) {
                scanners.get(annotation.getClass())
                        .accept(method, singleton);
            }
        }

        boolean isServiceClass = clazz.isAnnotationPresent(Service.class);
        if (!isServiceClass) return;

        Optional<Method> configure = Arrays.stream(clazz.getDeclaredMethods())
                .filter(it -> it.isAnnotationPresent(Configure.class))
                .findFirst();

        services.put(clazz, singleton);

        long duration = tracked(() -> configure.ifPresent(it -> {
            try {
                it.invoke(singleton);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FlavorException("Failed to invoke configure method %s for class %s".formatted(it.getName(), clazz.getSimpleName()), e);
            }
        }));

        if (duration != -1L) {
            options.getLogger().info("Configured service {} in {}ms", clazz.getSimpleName(), duration);
        } else {
            options.getLogger().info("Configured service {}", clazz.getSimpleName());
        }
    }

    /**
     * Small utility method that allows us
     * to track lambda execution time.
     * @param runnable Runnable to track
     * @return Execution time as a {@link Long}
     */
    private Long tracked(Runnable runnable) {
        long start = System.currentTimeMillis();

        try {
            runnable.run();
        } catch (Exception e) {
            options.getLogger().error("Failed to invoke runnable", e);
            return -1L;
        }

        return System.currentTimeMillis() - start;
    }

    /**
     * Retrieves a singleton instance of the specified class by accessing its static "instance" field.
     * The field must be named either "instance" or "INSTANCE" and must be publicly accessible or
     * accessible through reflection.
     *
     * @param instance the {@link Class} object representing the type whose singleton instance is to be fetched
     * @return the singleton instance of the specified class
     * @throws RuntimeException if no accessible "instance" field is found, or if the field's value cannot be accessed
     */
    public static Object objectInstance(Class<?> instance) {
        Field singleton;

        try {
            singleton = instance.getDeclaredField("instance");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to fetch singleton instance for class %s! Do you have an accessible field (\"INSTANCE\" or \"instance\") containing a singleton of the class?"
                    .formatted(instance.getSimpleName()), e);
        }

        try {
            return singleton.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get value of singleton field %s for service class %s."
                    .formatted(singleton.getName(), instance.getSimpleName()), e);
        }
    }



}
