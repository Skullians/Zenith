plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "zenith"
listOf(
    "bom",
    "core",
).forEach { module ->
    include(":zenith-$module")
    project(":zenith-$module").projectDir = file(module)
}

includeBuild("plugin")