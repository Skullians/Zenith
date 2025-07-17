package net.skullian.zenith.platform.paper.tasks

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import net.skullian.zenith.platform.paper.PaperPluginYml
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject

public abstract class PaperPluginGeneration @Inject constructor(
    private val output: File,
    project: Project
) : DefaultTask() {
    @TaskAction
    public fun run() {
        val ymlStructure = project.extensions.getByType(PaperPluginYml::class) ?: return
        val mapper: YAMLMapper = YAMLMapper.builder()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .build()

        mapper.writeValue(output, ymlStructure)
    }
}