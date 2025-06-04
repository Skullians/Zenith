package net.skullian.zenith.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.gson.Gson
import org.gradle.kotlin.dsl.get
import java.io.File
import java.net.URI
import javax.inject.Inject

public abstract class DepsGeneration @Inject constructor(
    private val output: File,
    private val project: Project
) : DefaultTask() {
    private data class Dependencies(
        val repositories: Set<String>,
        val dependencies: Set<String>,
    )

    @TaskAction
    public fun run() {
        val repositories = project.repositories
            .filterIsInstance<MavenArtifactRepository>()
            .map(MavenArtifactRepository::getUrl)
            .map(URI::toString)
        val dependencies = project.configurations["zenithLibrary"]
            .dependencies
            .map { dependency ->
                "${dependency.group}:${dependency.name}:${dependency.version}"
            }

        output.parentFile.mkdirs()
        output.createNewFile()

        val deps = Dependencies(repositories.toSet(), dependencies.toSet())
        val json = Gson().toJson(deps)

        output.writeText(json)
    }

}
