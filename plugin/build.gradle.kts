import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    implementation(libs.plugin.gradle.paperweight)
    implementation(libs.jackson.dataformat.yaml) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

kotlin {
    explicitApi()
}

tasks {
    withType<ShadowJar> {
        archiveClassifier = ""
    }

    build {
        dependsOn(shadowJar)
    }

    withType<Jar> {
        manifest {
            attributes(
                "Implementation-Version" to version,
            )
        }
    }

    withType<Javadoc> {
        (options as StandardJavadocDocletOptions).tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:",
        )
    }
}


gradlePlugin {
    website = "https://github.com/Skullians/Zenith"
    vcsUrl = "https://github.com/Skullians/Zenith"

    plugins {
        create("zenith") {
            id = "net.skullian.zenith"
            implementationClass = "net.skullian.zenith.ZenithPlugin"
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

