plugins {
    `maven-publish`
}

publishing {
    repositories.configureRepository()
}

fun RepositoryHandler.configureRepository() {
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
