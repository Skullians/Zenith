package net.skullian.zenith.platform.paper.model

import org.gradle.api.tasks.Input
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy::class)
public data class PaperDependency(@Input @JsonIgnore val name: String) {
    @Input public var load: DependencyLoadOrder = DependencyLoadOrder.BEFORE
    @Input public var required: Boolean = true
    @Input public var joinClasspath: Boolean = true
}

public enum class DependencyLoadOrder {
    BEFORE,
    AFTER,
    OMIT,
}