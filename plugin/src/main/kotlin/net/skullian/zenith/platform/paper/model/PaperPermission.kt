package net.skullian.zenith.platform.paper.model

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonProperty

public data class PaperPermission(@Input @JsonIgnore val name: String) {
    @Input @Optional
    var description: String? = null

    @Input @Optional var default: DefaultPermissions? = null
    var children: List<String>?
        @Internal @JsonIgnore get() = childrenMap?.filterValues { it }?.keys?.toList()
        set(value) {
            childrenMap = value?.associateWith { true }
        }

    @Input @Optional @JsonProperty("children") var childrenMap: Map<String, Boolean>? = null
}

public enum class DefaultPermissions {
    @JsonProperty("true") TRUE,
    @JsonProperty("false") FALSE,
    @JsonProperty("op") OP,
    @JsonProperty("!op") NOT_OP,
}