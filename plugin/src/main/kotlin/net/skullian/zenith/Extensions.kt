package net.skullian.zenith

import net.skullian.zenith.extension.deps.ZenithDepsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.getByType

public fun DependencyHandler.paper(
    version: String,
    internals: Boolean = false
): Unit = extensions.getByType<ZenithDepsExtension>()
    .applyPaper(version, internals)
