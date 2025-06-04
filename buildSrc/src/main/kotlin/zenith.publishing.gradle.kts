plugins {
    `maven-publish`
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