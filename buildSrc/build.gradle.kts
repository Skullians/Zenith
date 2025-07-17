plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.shadow)
    implementation(libs.plugin.gradle.kotlin)
    implementation(libs.plugin.gradle.dokka)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
