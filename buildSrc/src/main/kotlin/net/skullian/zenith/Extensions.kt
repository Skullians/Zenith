package net.skullian.zenith

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

abstract class Zenith {
    internal val dependencies: MutableList<String> = mutableListOf()

    fun modules(vararg modules: String) {
        this.dependencies.addAll(modules)
    }
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
