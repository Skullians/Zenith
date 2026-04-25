plugins {
    zenith.common
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":zenith-core"))

    compileOnly(libs.paper.api)
}
