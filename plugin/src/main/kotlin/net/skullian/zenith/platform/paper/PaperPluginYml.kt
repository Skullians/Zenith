package net.skullian.zenith.platform.paper

import net.skullian.zenith.platform.paper.model.DefaultPermissions
import net.skullian.zenith.platform.paper.model.PaperDependency
import net.skullian.zenith.platform.paper.model.PaperPermission
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

// https://github.com/PaperMC/Paper/blob/main/paper-server/src/main/java/io/papermc/paper/plugin/provider/configuration/PaperPluginMeta.java#L43-L68
@Suppress("ktlint:standard:spacing-between-declarations-with-annotations")
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy::class)
public class PaperPluginYml(
    project: Project
) {
    @Internal
    @JsonIgnore
    public val fileName: String = "paper-plugin.yml"

    @Input public var name: String = project.rootProject.name
    @Input public var version: String = project.rootProject.version.toString()
    @Input public lateinit var main: String
    @Input @Optional public var loader: String? = null // todo
    @Input public lateinit var apiVersion: String

    @Input @Optional public var bootstrapper: String? = null
    @Input @Optional public var provides: List<String> = emptyList()
    @Input @Optional public var hasOpenClassloader: Boolean = false
    @Input @Optional public var description: String? = null
    @Input @Optional public var authors: List<String>? = null
    @Input @Optional public var contributors: List<String> = listOf("Skullians")
    @Input @Optional public var website: String? = null
    @Input @Optional public var prefix: String? = null
    @Input @Optional @JsonProperty("defaultPerm") public var defaultPermission: DefaultPermissions? = null
    @Input @Optional public var foliaSupported: Boolean? = null

    // Dependencies
    @Nested @Optional @JsonIgnore
    public val serverDependencies: NamedDomainObjectContainer<PaperDependency> = project.container(PaperDependency::class.java)
    @Nested @Optional @JsonIgnore
    public val bootstrapDependencies: NamedDomainObjectContainer<PaperDependency> = project.container(PaperDependency::class.java)

    @JsonGetter
    public fun dependencies(): Map<String, NamedDomainObjectContainer<PaperDependency>> = mapOf(
        "server" to serverDependencies,
        "bootstrap" to bootstrapDependencies,
    )

    // Permissions
    @Nested
    public val permissions: NamedDomainObjectContainer<PaperPermission> = project.container(PaperPermission::class.java)
}
