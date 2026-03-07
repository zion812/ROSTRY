includeBuild("build-logic")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.google.com") }  // Added for Hilt plugin
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.google.com") }  // Added for Hilt dependencies
    }
}

rootProject.name = "ROSTRY"
include(":app")
include(":core:designsystem")
include(":core:common")
include(":core:model")
include(":core:database")
include(":core:network")
include(":core:domain")
include(":core:navigation")
include(":core:testing")
include(":feature:achievements")
include(":feature:leaderboard")
include(":feature:notifications")
include(":feature:events")
include(":feature:expert")
include(":feature:insights")
include(":feature:feedback")
include(":feature:support")

// Phase 2: Domain modules (business-area contracts)
include(":domain:account")
include(":domain:commerce")
include(":domain:farm")
include(":domain:monitoring")
include(":domain:social")
include(":domain:admin")
