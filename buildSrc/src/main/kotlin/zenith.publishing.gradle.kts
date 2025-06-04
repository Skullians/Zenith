plugins {
    `maven-publish`
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
    }

    println("Using repository without credentials.")
    maven("https://repo.skullian.com/releases/") {
        name = "skullian-releases"
    }
}