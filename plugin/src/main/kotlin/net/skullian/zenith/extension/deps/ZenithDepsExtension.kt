package net.skullian.zenith.extension.deps

import net.skullian.zenith.platform.paper.PaperPluginYml
import net.skullian.zenith.platform.paper.ZenithPaperPlatform
import net.skullian.zenith.platform.paper.model.PaperDependency
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

public abstract class ZenithDepsExtension @Inject constructor(
    private val project: Project,
) {
    /**
     * Adds the provided [notation] for a dependency as a zenith and compileOnly library
     */
    public fun runtimeDependency(notation: Any) {
        val dependency = project.dependencies.create(notation)
        project.dependencies.add("zenithLibrary", dependency)
        project.dependencies.add("compileOnly", dependency)
    }

    /**
     * Adds the specified [provider] for a dependency as a zenith and compileOnly library
     */
    public fun runtimeProvider(provider: Provider<out ExternalModuleDependency>) {
        project.dependencies.add("zenithLibrary", provider)
        project.dependencies.add("compileOnly", provider)
    }

    /**
     * Adds the specified [bundleProvider] as a zenith and compileOnly library.
     */
    @JvmName("runtimeBundle")
    public fun runtimeProvider(bundleProvider: Provider<out ExternalModuleDependencyBundle>) {
        project.dependencies.add("zenithLibrary", bundleProvider)
        project.dependencies.add("compileOnly", bundleProvider)
    }

    public fun plugin(
        dependency: String,
        pluginName: String,
        bootstrap: Boolean = false,
        dependencyAction: Action<PaperDependency>,
    ): ExternalModuleDependency {
        val createdDependency = project.dependencies.create(dependency) { isTransitive = true }
        project.dependencies.add("compileOnly", createdDependency)

        project.extensions.getByType<PaperPluginYml>().apply {
            if (bootstrap)
                bootstrapDependencies.create(pluginName, dependencyAction)
            else
                serverDependencies.create(pluginName, dependencyAction)
        }

        return createdDependency
    }

    public fun applyPaper(
        version: String,
        internals: Boolean = false,
    ): Unit = ZenithPaperPlatform.apply(version, internals, project)
}
