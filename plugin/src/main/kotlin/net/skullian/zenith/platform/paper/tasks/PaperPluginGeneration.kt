package net.skullian.zenith.platform.paper.tasks

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.Converter
import com.fasterxml.jackson.databind.util.StdConverter
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.skullian.zenith.platform.paper.PaperPluginYml
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputFile
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject

public abstract class PaperPluginGeneration @Inject constructor(
    output: File,
    project: Project
) : DefaultTask() {
    @OutputFile
    public val output: File = output
    @TaskAction
    public fun run() {
        val ymlStructure = project.extensions.getByType(PaperPluginYml::class) ?: return

        val factory = YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)

        val module = SimpleModule()
        @Suppress("UNCHECKED_CAST")
        module.addSerializer(
            StdDelegatingSerializer(
                NamedDomainObjectCollection::class.java,
                NamedDomainObjectCollectionConverter as Converter<NamedDomainObjectCollection<*>, *>
            )
        )

        val mapper = ObjectMapper(factory)
            .registerKotlinModule()
            .registerModule(module)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        mapper.writeValue(output, ymlStructure)
    }

    private object NamedDomainObjectCollectionConverter : StdConverter<NamedDomainObjectCollection<Any>, Map<String, Any>>() {
        override fun convert(value: NamedDomainObjectCollection<Any>): Map<String, Any> {
            val namer = value.namer
            return value.associateBy { namer.determineName(it) }
        }
    }
}
