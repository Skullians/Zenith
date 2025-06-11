package net.skullian.zenith.gradle.model.library

import org.intellij.lang.annotations.Pattern
import org.intellij.lang.annotations.RegExp


public data class ZenithRepository(
    val name: String,
    val url: String,
    val libraries: MutableList<ZenithLibrary>
) {
    public companion object {
        private var GENERIC_INDEX: Int = 0

        public fun named(
            name: String,
            @RegExp
            @Pattern("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")
            url: String,
        ): ZenithRepository = ZenithRepository(name, url, mutableListOf())

        public fun url(
            @RegExp
            @Pattern("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")
            url: String,
        ): ZenithRepository {
            val name = if (GENERIC_INDEX == 0) "maven" else "maven${GENERIC_INDEX++}"
            return ZenithRepository(name, url, mutableListOf())
        }
    }
}
