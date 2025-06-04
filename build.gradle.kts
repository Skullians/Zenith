group = "net.skullian.zenith"
version = libs.versions.library.version.get()

tasks.register("publish") {
    childProjects.forEach { _, project ->
        runCatching {
            dependsOn(project.tasks.getByName("publish"))
        }
    }
}