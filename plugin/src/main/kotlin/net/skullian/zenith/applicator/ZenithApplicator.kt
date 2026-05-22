package net.skullian.zenith.applicator

import net.skullian.zenith.extension.ZenithExtension
import net.skullian.zenith.extension.ZenithExtension.Companion.zenith
import net.skullian.zenith.extension.deps.ZenithDependencies
import net.skullian.zenith.extension.deps.ZenithDepsExtension
import net.skullian.zenith.model.ZenithRepositories
import net.skullian.zenith.platform.paper.PaperPluginYml
import net.skullian.zenith.platform.paper.ZenithPaperPlatform
import net.skullian.zenith.platform.paper.tasks.PaperPluginGeneration
import net.skullian.zenith.tasks.DepsGeneration
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import org.gradle.language.jvm.tasks.ProcessResources

public object ZenithApplicator {
    public fun apply(project: Project): Unit =
        with(project) {
            extensions.create("zenith", ZenithExtension::class.java)
            extensions.add("plugin", PaperPluginYml(project))

            dependencies.extensions.create("zenith", ZenithDepsExtension::class.java, project)
            dependencies.extensions.add(
                "paper",
            ) { version: String, internals: Boolean ->
                ZenithPaperPlatform.apply(version, internals, project)
            }
            configurations.create("zenithLibrary")

            repositories.mavenCentral()
            repositories.gradlePluginPortal()
            repositories.maven(ZenithRepositories.SKULLIANS.url)

            afterEvaluate(::eval)
            applyTasks(this)
        }

    public fun eval(project: Project): Unit = with(project) {
        ZenithDependencies.apply(project, zenith)
    }

    private fun applyTasks(project: Project): Unit = with(project) {
        val generatedDir = layout.buildDirectory.dir("generated/zenith")

        val dependencyTask = tasks.register("generateDependencies", DepsGeneration::class.java,
            generatedDir.get().file("zenith-dependencies.json").asFile)

        val yamlTask = tasks.register("generatePluginYml", PaperPluginGeneration::class.java,
            generatedDir.get().file("paper-plugin.yml").asFile, project)

        plugins.withType(JavaPlugin::class.java) {
            extensions.getByType(SourceSetContainer::class.java).named(SourceSet.MAIN_SOURCE_SET_NAME) {
                resources.srcDir(generatedDir)
            }
            tasks.named("processResources").configure { dependsOn(yamlTask, dependencyTask) }
        }

        tasks.withType(ProcessResources::class.java) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            exclude("plugin.yml")
        }
    }
}
