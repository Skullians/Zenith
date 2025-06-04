package net.skullian.zenith.gradle.extension.deps

import net.skullian.zenith.gradle.ZenithConstants
import net.skullian.zenith.gradle.extension.ZenithExtension
import org.gradle.api.Project

public object ZenithDependencies {
    public fun apply(project: Project, extension: ZenithExtension) {
        fun add(notation: Any) {
            project.dependencies.add("zenithLibrary", notation)
            project.dependencies.add("implementation", notation)
        }

        val version = ZenithConstants.ZENITH
        extension.modules.forEach { module -> add("net.skullian.zenith:zenith-$module:$version") }
    }
}