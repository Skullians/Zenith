package net.skullian.zenith.gradle.applicator

import net.skullian.zenith.gradle.extension.ZenithExtension.Companion.zenith
import net.skullian.zenith.gradle.extension.deps.ZenithDependencies
import net.skullian.zenith.gradle.extension.deps.ZenithDepsExtension
import net.skullian.zenith.gradle.model.ZenithRepositories
import net.skullian.zenith.gradle.tasks.DepsGeneration
import net.skullian.zenith.gradle.tasks.YamlFiltering
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
            dependencies.extensions.create("zenith", ZenithDepsExtension::class.java, project)
            configurations.create("zenithLibrary")

            repositories.mavenCentral()
            repositories.gradlePluginPortal()
            repositories.maven(ZenithRepositories.PAPER)
            repositories.maven(ZenithRepositories.SKULLIANS)

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
        val dependencyTask = project.tasks.register(dependencyTaskName, DepsGeneration::class.java,  dependencyTaskOutput)

        val yamlTaskName = "filterYaml" + compilation.name.capitalized()
        val yamlTask = project.tasks.register(yamlTaskName, YamlFiltering::class.java, compilation)

        project.tasks.withType(Jar::class.java) {
            dependsOn("processResources")
            dependsOn(dependencyTask)
        }

        project.tasks.withType(ProcessResources::class.java) {
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

    }
}
