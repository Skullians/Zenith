plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.shadow)
    implementation(libs.plugin.gradle.kotlin)
    implementation(libs.plugin.gradle.dokka)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

fun RepositoryHandler.configureRepository(project: Project) {
    val user: String? = project.findProperty("SkulliansRepoUsername") as? String
    val pw: String? = project.findProperty("SkulliansRepoPassword") as? String

    if (user != null && pw != null) {
        maven("https://repo.skullian.com/releases/") {
            name = "skullian-releases"
            credentials {
                username = user
                password = pw
            }
        }

        return
    }

    println("Using repository without credentials.")
    maven("https://repo.skullian.com/releases/") {
        name = "skullian-releases"
    }
}
