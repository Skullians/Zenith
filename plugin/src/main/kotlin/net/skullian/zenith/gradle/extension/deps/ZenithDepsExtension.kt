package net.skullian.zenith.gradle.extension.deps

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.provider.Provider
import javax.inject.Inject

public abstract class ZenithDepsExtension @Inject constructor(
    private val project: Project
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
     * Adds the specified [provider] for a dependency as a zenith and compileOnly library]
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
}