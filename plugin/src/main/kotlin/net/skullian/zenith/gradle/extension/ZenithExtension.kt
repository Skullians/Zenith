package net.skullian.zenith.gradle.extension

import net.skullian.zenith.gradle.model.ZenithModules
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.the

public abstract class ZenithExtension(
    objects: ObjectFactory,
) {
    // Misc
    public val modules: MutableList<ZenithModules> = mutableListOf()
    public val remap: Attribute<Boolean> = REMAP_ATTRIBUTE
    public val kotlin: Property<Boolean> = objects.property<Boolean>().convention(false)

    public fun modules(vararg modules: ZenithModules) {
        this.modules.addAll(modules)
    }

    internal companion object {
        val Project.zenith get() = the<ZenithExtension>()
        val REMAP_ATTRIBUTE: Attribute<Boolean> = Attribute.of("remap", Boolean::class.javaObjectType)
    }
}
