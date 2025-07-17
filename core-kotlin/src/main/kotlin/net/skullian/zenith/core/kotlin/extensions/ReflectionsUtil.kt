package net.skullian.zenith.core.kotlin.extensions

import net.skullian.zenith.core.reflection.ReflectionsUtil
import java.lang.reflect.Method

public inline fun <reified T : Annotation> ReflectionsUtil.getSubTypes(): List<Class<*>> = getSubTypes(T::class.java)

public inline fun <reified T : Annotation> ReflectionsUtil.getMethodsAnnotatedWith(): List<Method> = getMethodsAnnotatedWith(T::class.java)

public inline fun <reified T : Annotation> ReflectionsUtil.getTypesAnnotatedWith(): List<Class<*>> = getTypesAnnotatedWith(T::class.java)

