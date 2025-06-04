import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.skullian.zenith.ZenithExtension

plugins {
    id("com.gradleup.shadow")
    id("zenith.publishing")

    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

//val libs = the<LibrariesForLibs>()

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

extensions.create("zenith", ZenithExtension::class)

afterEvaluate {
    val extension = the<ZenithExtension>()

    dependencies {
        extension.dependencies.forEach { dependency ->
            add("compileOnly", project(":zenith-$dependency"))
        }
    }
}
