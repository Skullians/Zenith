plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.shadow)
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.dokka)
    implementation(libs.plugin.kotlin.serialization)
    implementation(libs.plugin.ktlint)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
