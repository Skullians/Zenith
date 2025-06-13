package net.skullian.zenith.platform

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import net.skullian.zenith.extension.ZenithExtension
import net.skullian.zenith.model.ZenithModules
import net.skullian.zenith.model.ZenithRepositories
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.the

public object ZenithPaperPlatform {
    public fun apply(
        version: String,
        internals: Boolean = false,
        project: Project,
    ) {
        project.repositories.maven(ZenithRepositories.PAPER.url)
        project.extensions
            .getByType<ZenithExtension>()
            .modules
            .add(ZenithModules.PAPER)

        val paperVersion = "$version-R0.1-SNAPSHOT"

        if (internals) {
            project.plugins.apply(PaperweightUser::class.java)

            val paperweight = project.dependencies.the<PaperweightUserDependenciesExtension>()
            paperweight.paperDevBundle(paperVersion)
        } else {
            project.dependencies.add("compileOnly", "io.papermc.paper:paper-api:$paperVersion")
        }
    }
}
