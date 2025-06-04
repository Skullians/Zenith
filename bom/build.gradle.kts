plugins {
    zenith.publishing
    `java-platform`
}

group = rootProject.group
version = rootProject.version

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        rootProject.childProjects
            .filter { (name) -> name != "bom" }
            .forEach { (_, project) -> api(project) }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["javaPlatform"])
        }
    }
}