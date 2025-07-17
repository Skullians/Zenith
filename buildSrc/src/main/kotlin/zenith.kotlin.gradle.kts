import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")

    `java-library`
    `maven-publish`
}

tasks.withType<KotlinCompile> {
    explicitApiMode = ExplicitApiMode.Strict
    compilerOptions.freeCompilerArgs.addAll(
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
        "-opt-in=kotlin.contracts.ExperimentalContracts",
        "-Xcontext-receivers",
    )
}

val dokkaJar = tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap(DokkaGeneratePublicationTask::outputDirectory))
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        remove(findByName("mavenJava"))

        create<MavenPublication>("mavenKotlin") {
            from(components["shadow"])

            artifact(tasks.getByName("kotlinSourcesJar")) {
                classifier = "sources"
            }

            artifact(dokkaJar) {
                classifier = "javadoc"
            }
        }
    }
}