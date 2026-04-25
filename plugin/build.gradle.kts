import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`

    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)

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
    repositories {
        val user: String? = findProperty("SkulliansRepoUsername") as? String
        val pw: String? = findProperty("SkulliansRepoPassword") as? String

        if (user != null && pw != null) {
            maven("https://repo.skullian.com/releases/") {
                name = "skullian-releases"
                credentials {
                    username = user
                    password = pw
                }
            }
        } else {
            println("Using repository without credentials.")
            maven("https://repo.skullian.com/releases/") {
                name = "skullian-releases"
            }
        }
    }
}

sourceSets.main {
    blossom.kotlinSources {
        property("zenithVersion", version.toString())
    }
}
