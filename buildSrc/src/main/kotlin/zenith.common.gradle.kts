import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.skullian.zenith.ZenithExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")

    id("com.gradleup.shadow")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("zenith.publishing")

    `java-library`
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

val libs = the<LibrariesForLibs>()

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    withType<KotlinCompile> {
        explicitApiMode = ExplicitApiMode.Strict
        compilerOptions.freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
            "-opt-in=kotlin.contracts.ExperimentalContracts",
            "-Xcontext-receivers",
        )
    }

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

    register<Jar>("dokkaHtmlJar") {
        archiveClassifier.set("javadoc")

        dependsOn(tasks.dokkaGeneratePublicationHtml)
        from(tasks.dokkaGeneratePublicationHtml.flatMap(DokkaGeneratePublicationTask::outputDirectory))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["shadow"])
            artifact(tasks["dokkaHtmlJar"])
        }
    }
}

ktlint {
    version = libs.versions.ktlint.asProvider()

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }

    additionalEditorconfig.set(
        mapOf(
            "ktlint_function_signature_body_expression_wrapping" to "default",
            "ktlint_code_style" to "intellij_idea",
            "ktlint_experimental" to "enabled",
            "ktlint_standard_multiline-expression-wrapping" to "disabled",
            "ktlint_standard_property-wrapping" to "disabled",
            "ktlint_standard_condition-wrapping" to "disabled",
            "ktlint_standard_function-expression-body" to "disabled",
            "ktlint_standard_if-else-bracing" to "enabled",
            "insert_final_newline" to "true",
            "end_of_line" to "lf",
            "indent_style" to "space",
            "max_line_length" to "off",
        ),
    )
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
