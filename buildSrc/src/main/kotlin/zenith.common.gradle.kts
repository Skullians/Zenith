import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.skullian.zenith.Zenith

plugins {
    id("com.gradleup.shadow")
    id("zenith.publishing")

    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo")
}

tasks {
    withType<Jar> {
        manifest {
            attributes(
                "Implementation-Version" to version,
            )
        }
    }

    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.fork()
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    withType<ShadowJar> {
        archiveClassifier.set("")
        exclude(
            "**/*.kotlin_metadata",
            "**/*.kotlin_builtins",
            "META-INF/",
            "kotlin/**",
        )

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    register<Jar>("generateJdoc") {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        archiveClassifier.set("javadoc")
        from(tasks.javadoc)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["shadow"])
            artifact(tasks["generateJdoc"])
        }
    }
}

extensions.create("zenith", Zenith::class)

afterEvaluate {
    val extension = the<Zenith>()

    dependencies {
        extension.dependencies.forEach { dependency ->
            add("compileOnly", project(":zenith-$dependency"))
        }
    }
}
