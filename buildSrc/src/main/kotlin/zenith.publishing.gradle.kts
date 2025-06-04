plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "skullians-public"
            url = uri("https://repo.skullian.com/releases")

            credentials {
                username = properties["repo_username"]?.toString() ?: System.getenv("repository_username")
                password = properties["repo-password"]?.toString() ?: System.getenv("repository_password")
            }
        }
    }
}