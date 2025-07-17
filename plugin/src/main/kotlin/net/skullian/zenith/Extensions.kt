package net.skullian.zenith

import net.skullian.zenith.extension.deps.ZenithDepsExtension
import net.skullian.zenith.platform.paper.model.PaperDependency
import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType

public fun DependencyHandler.paper(
    version: String,
    internals: Boolean = false
): Unit = extensions.getByType<ZenithDepsExtension>()
    .applyPaper(version, internals)