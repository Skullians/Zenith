package net.skullian.zenith.gradle

import net.skullian.zenith.gradle.applicator.ZenithApplicator
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

public class ZenithPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        ZenithApplicator.apply(kotlinCompilation)
        return kotlinCompilation.project.provider(::emptyList)
    }

    override fun getCompilerPluginId(): String = "net.skullian.zenith"

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact("net.skullian.zenith", "plugin", javaClass.`package`.implementationVersion)
    }

    override fun apply(target: Project) {
        ZenithApplicator.apply(target)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.target.platformType == KotlinPlatformType.jvm
    }
}
