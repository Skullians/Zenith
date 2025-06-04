package net.skullian.zenith.gradle.extension

import net.skullian.zenith.gradle.model.ZenithModules
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

public abstract class ZenithExtension {
    public val modules: MutableList<ZenithModules> = mutableListOf()

    public fun modules(vararg modules: ZenithModules) {
        this.modules.addAll(modules)
    }

    internal companion object {
        val Project.zenith get() = the<ZenithExtension>()
    }
}
