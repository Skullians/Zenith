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
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

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
        }

    public fun eval(project: Project): Unit = with(project) {
        configurations["zenithLibrary"]
            .extendsFrom(configurations["compileClasspath"])
            .extendsFrom(configurations["runtimeClasspath"])

        ZenithDependencies.apply(project, zenith)
    }

    public fun apply(compilation: KotlinCompilation<*>) {
        val project = compilation.project

        val dependencyTaskName = "generateDependencies" + compilation.name.capitalized()
        val dependencyTaskOutput = compilation.output.resourcesDir.resolve("zenith-dependencies.json")
        val dependencyTask = project.tasks.register(dependencyTaskName, DepsGeneration::class.java, dependencyTaskOutput)

        val yamlTaskName = "generatePluginYml" + compilation.name.capitalized()
        val yamlTaskOutput = compilation.output.resourcesDir.resolve("paper-plugin.yml")
        val yamlTask = project.tasks.register(yamlTaskName, PaperPluginGeneration::class.java, yamlTaskOutput, project)

        val sourceSetName = compilation.defaultSourceSet.name
        val processResourcesTaskName = if (sourceSetName == "main") "processResources" else "process" + sourceSetName.capitalized() + "Resources"
        project.tasks.named(processResourcesTaskName, ProcessResources::class.java) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE

            exclude("plugin.yml")
            from(compilation.defaultSourceSet.resources) {
                exclude("paper-plugin.yml")
            }

            from(compilation.output.resourcesDir) {
                include("paper-plugin.yml")
            }

            doLast {
                yamlTask.get().run()
            }
        }

        project.tasks.withType(Jar::class.java) {
            dependsOn(processResourcesTaskName)
            dependsOn(dependencyTask)
        }
    }
}
