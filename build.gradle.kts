// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.hilt) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
    // Dokka for API documentation generation
    id("org.jetbrains.dokka") version "1.9.20"
}

// Configure existing Dokka multi-module task to output into docs/api/
tasks.named<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>("dokkaHtmlMultiModule").configure {
    outputDirectory.set(file("docs/api"))
}