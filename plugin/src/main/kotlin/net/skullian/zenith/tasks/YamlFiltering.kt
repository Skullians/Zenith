package net.skullian.zenith.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.yaml.snakeyaml.DumperOptions
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import java.io.File
import javax.inject.Inject
import kotlin.collections.contains

public abstract class YamlFiltering @Inject constructor(
    private val compilation: KotlinCompilation<*>,
) : DefaultTask() {
    @TaskAction
    public fun run() {
        val yaml = Yaml()
        val resources = compilation.defaultSourceSet.resources

        val pluginYml = resources.srcDirs.first().resolve("plugin.yml")
        val paperPluginYml = resources.srcDirs.first().resolve("paper-plugin.yml")

        if (!pluginYml.exists() && !paperPluginYml.exists()) return

        if (pluginYml.exists() && paperPluginYml.exists() || (!pluginYml.exists() && paperPluginYml.exists())) {
            processPaper(yaml.load(paperPluginYml.readText()), paperPluginYml)
        } else {
            val yamlData: Map<String, Any> = yaml.load(pluginYml.readText())
            if ("libraries" in yamlData) {
                println("w: spigot library loader entries will be ignored.")
                println("   Consider using the `zenithLibrary` extension instead.")
            }

            processPaper(yamlData, pluginYml)
        }
    }

    private fun processPaper(yaml: Map<String, Any>, file: File) {
        val copy = yaml.toMutableMap()
        if ("loader" !in yaml) {
            copy["loader"] = "net.skullian.zenith.paper.loader.ZenithLibraryLoader"
        }

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        val dumper = DumperOptions().apply {
            indent = 2
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        }

        val inst = Yaml(dumper)
        val serialized = inst.dump(copy)
        val output = compilation.output.resourcesDir.resolve("paper-plugin.yml")

        if (!output.exists()) {
            output.parentFile.mkdirs()
        }

        output.writeText(serialized)
    }
}
