// Gradle plugin modules don't like libs or buildSrc conventions
plugins {
    id("com.gradleup.shadow") version "9.0.0-beta15"

    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.blossom)

    `kotlin-dsl`
    `embedded-kotlin`
    `java-gradle-plugin`
}

group = "net.skullian.zenith"
version = property("version") as String

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.bundles.kotlin.plugin)
}

kotlin {
    explicitApi()
}

tasks {
    shadowJar {
        archiveClassifier = ""
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        manifest {
            attributes(
                "Implementation-Version" to version,
            )
        }
    }
}


gradlePlugin {
    website = "https://github.com/Skullians/Zenith"
    vcsUrl = "https://github.com/Skullians/Zenith"

    plugins {
        create("zenith") {
            id = "net.skullian.zenith"
            implementationClass = "net.skullian.zenith.gradle.ZenithPlugin"
            displayName = "Zenith"
            description = "Gradle Plugin for the Zenith utility library"
            tags = listOf("minecraft", "game")
        }
    }
}

publishing {
    repositories.configureRepository()
}

fun RepositoryHandler.configureRepository() {
    val user: String? = properties["repository_username"]?.toString() ?: System.getenv("repository_username")
    val pw: String? = properties["repository_password"]?.toString() ?: System.getenv("repository_password")

    if (user != null && pw != null) {
        maven("https://repo.skullian.com/releases/") {
            name = "skullian-releases"
            credentials {
                username = user
                password = pw
            }
        }

        return
    } else {
        throw IllegalArgumentException("Missing credentials for repository.")
    }
}

sourceSets.main {
    blossom.kotlinSources {
        property("zenithVersion", version.toString())
    }
}

