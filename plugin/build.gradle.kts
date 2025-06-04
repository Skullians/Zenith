import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.withType

// Gradle plugin modules don't like libs or buildSrc conventions
plugins {
    id("com.gradleup.shadow") version "9.0.0-beta15"

    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.blossom)

    `kotlin-dsl`
    `embedded-kotlin`
    `java-gradle-plugin`
}

group = rootProject.group
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
    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude(
            "**/*.kotlin_metadata",
            "**/*.kotlin_builtins",
            "META-INF/",
            "kotlin/**",
        )

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
    repositories {
        maven {
            name = "skullians-public"
            url = uri("https://repo.skullian.com/releases")

            credentials {
                username = System.getenv("repository_username")
                password = System.getenv("repository_password")
            }
        }
    }
}

sourceSets.main {
    blossom.kotlinSources {
        property("zenithVersion", version.toString())
    }
}
