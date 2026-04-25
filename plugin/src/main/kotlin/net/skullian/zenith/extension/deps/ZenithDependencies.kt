package net.skullian.zenith.extension.deps

import net.skullian.zenith.extension.ZenithExtension
import net.skullian.zenith.gradle.ZenithConstants
import org.gradle.api.Project

public object ZenithDependencies {
    private const val prefix = "net.skullian.zenith"

    public fun apply(
        project: Project,
        extension: ZenithExtension,
    ) {
        fun add(notation: Any) {
            project.dependencies.add("zenithLibrary", notation)
            project.dependencies.add("implementation", notation)
        }

        val version = ZenithConstants.ZENITH
        extension.modules.forEach { module ->
            val name = module.name.lowercase()
            add("$prefix:zenith-$name:$version")
            if (extension.kotlin.get()) add("$prefix:zenith-$name-kotlin:$version")
        }
    }
}
