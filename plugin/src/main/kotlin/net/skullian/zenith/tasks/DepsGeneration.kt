package net.skullian.zenith.tasks

import net.skullian.zenith.extension.ZenithExtension
import net.skullian.zenith.model.library.ZenithLibrary
import net.skullian.zenith.model.library.ZenithRepository
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.gson.Gson
import org.gradle.internal.impldep.com.google.gson.GsonBuilder
import org.gradle.kotlin.dsl.get
import java.io.File
import java.io.FileWriter
import java.net.URI
import javax.inject.Inject

/**
 * Created on 10/06/2025
 * Modified to suit Zenith.
 *
 * https://github.com/Finally-A-Decent/Trashcan/blob/dev/tooling/src/main/kotlin/info/preva1l/trashcan/tasks/GenerateLibrariesFileTask.kt
 * Thanks, mate <3
 *
 * @author Preva1l
 */
public abstract class DepsGeneration @Inject constructor(
    private val output: File,
    project: Project
) : DefaultTask() {

    private data class ZenithLibraries(
        val repositories: MutableMap<String, ZenithRepository>,
    )

    private val libraries = ZenithLibraries(mutableMapOf())
    
    private val configuration = project.configurations["zenithLibrary"]
    public val repositories: List<MavenArtifactRepository> = (project.repositories + project.rootProject.repositories)
        .filterIsInstance<MavenArtifactRepository>()
    
    private val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    @TaskAction
    public fun run() {
        if (repositories.isEmpty()) {
            logger.warn("No Maven repositories were found to search.")
            return
        }

        val dependencies = configuration
            .resolvedConfiguration.firstLevelModuleDependencies
            .associateBy { "${it.moduleGroup}:${it.moduleName}" }
        val remapDependencies = configuration.dependencies
            .filterIsInstance<ExternalModuleDependency>()
            .associateBy { "${it.module.group}:${it.module.name}" }
            .mapValues { it.value.attributes.getAttribute(ZenithExtension.Companion.REMAP_ATTRIBUTE) == true }

        val artifacts = configuration.incoming.artifacts.artifacts
        logger.info("Resolving repository URLs for ${artifacts.size} dependencies.")

        artifacts.forEach { artifact ->
            resolveRepository(artifact, dependencies, remapDependencies)
        }

        FileWriter(output).use {
            gson.toJson(
                libraries,
                ZenithLibraries::class.java,
                it,
            )
        }
    }

    private fun resolveRepository(
        artifact: ResolvedArtifactResult,
        dependencies: Map<String, ResolvedDependency>,
        remapDependencies: Map<String, Boolean>
    ) {
        var dependency = parseDependencyGav(artifact.id.componentIdentifier.displayName) ?: return
        val dependencyKey = "${dependency.group}:${dependency.artifact}"

        if (!dependencies.containsKey(dependencyKey)) return
        dependency = dependency.remapped(remapDependencies[dependencyKey] == true)

        val jarName = "${dependency.artifact}-${dependency.versionName()}.jar"
        val artifactPath = "${dependency.group.replace(".", "/")}/${dependency.artifact}/${dependency.version}/$jarName"

        repositories.forEach { repository ->
            val baseUrl = "${repository.url.toString().trimEnd('/')}/"
            val candidateUrl = "$baseUrl$artifactPath"

            if (urlExists(candidateUrl)) {
                logger.info("Resolved [${dependency.gavCoordinate()}] via [$candidateUrl]")
                libraries.repositories.getOrPut(repository.name) { ZenithRepository.Companion.named(repository.name, baseUrl) }
                    .libraries.add(dependency)

                return
            }
        }

        logger.warn("Failed to resolve [${dependency.gavCoordinate()}] via any of the ${repositories.size} repositories.")
    }

    private fun parseDependencyGav(gav: String): ZenithLibrary? {
        val parts = gav.split(":")

        return when {
            parts.size >= 3 -> ZenithLibrary(
                parts[0],
                parts[1],
                parts[2],
                if (parts.size >= 4) parts[3] else null,
                false,
            )
            else -> {
                logger.warn("Invalid dependency format for GAV parsing: $gav")
                null
            }
        }
    }

    private fun urlExists(url: String): Boolean = try {
        URI.create(url).toURL().openStream().let { it != null }
    } catch (_: Exception) {
        false
    }
}
