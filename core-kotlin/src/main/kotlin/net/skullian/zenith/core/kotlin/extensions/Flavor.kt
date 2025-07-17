package net.skullian.zenith.core.kotlin.extensions

import net.skullian.zenith.core.flavor.Flavor
import net.skullian.zenith.core.flavor.binder.FlavorBinder

/**
 * Searches & returns the service instance matching the class type [T].
 *
 * @returns The service of type [T]
 * @throws RuntimeException if there is no service with type [T].
 */
public inline fun <reified T> Flavor.service(): T = service(T::class.java)

/**
 * Binds the specified type `T` to a new instance of `FlavorBinder<T>`.
 * This binder is associated with the current `Flavor` instance, allowing
 * configurations and management for the given type within the binding context.
 *
 * @return A `FlavorBinder` instance corresponding to the specified type `T`.
 */
public inline fun <reified T> Flavor.bind(): FlavorBinder<T> = bind(T::class.java)

/**
 * Creates and injects a new instance of [T] in Flavor.
 *
 * @return The injected instance of type [T].
 */
public inline fun <reified T> Flavor.injected(vararg params: Any): T = injected(T::class.java, params)
