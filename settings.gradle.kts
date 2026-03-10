pluginManagement {
    includeBuild("build-logic")
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
    }
}

rootProject.name = "ROSTRY"

// ──────────────────────────────────────────────
// App Shell
// ──────────────────────────────────────────────
include(":app")

// ──────────────────────────────────────────────
// Core Modules
// ──────────────────────────────────────────────
include(":core:common")
include(":core:database")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:navigation")
include(":core:network")
include(":core:testing")

// ──────────────────────────────────────────────
// Domain Modules (business contracts)
// ──────────────────────────────────────────────
include(":domain:account")
include(":domain:admin")
include(":domain:commerce")
include(":domain:farm")
include(":domain:monitoring")
include(":domain:social")

// ──────────────────────────────────────────────
// Data Modules (implementations)
// ──────────────────────────────────────────────
include(":data:account")
include(":data:admin")
include(":data:commerce")
include(":data:farm")
include(":data:monitoring")
include(":data:social")

// ──────────────────────────────────────────────
// Feature Modules
// ──────────────────────────────────────────────
// Wave A: Authentication & Onboarding
include(":feature:login")
include(":feature:onboarding")

// Wave B: Core Farm Management
include(":feature:farm-dashboard")
include(":feature:asset-management")
include(":feature:farm-profile")
include(":feature:breeding")

// Wave D: Marketplace & Commerce
include(":feature:listing-management")
include(":feature:orders")

// Wave E: Social & Community
include(":feature:community")

// Wave F: Admin & Support
include(":feature:admin-dashboard")
include(":feature:moderation")

// Existing Feature Modules
include(":feature:achievements")
include(":feature:events")
include(":feature:expert")
include(":feature:feedback")
include(":feature:insights")
include(":feature:leaderboard")
include(":feature:notifications")
include(":feature:support")

// New Feature Modules (Phase 1 Migration)
include(":feature:marketplace")
include(":feature:social-feed")
include(":feature:monitoring")
include(":feature:farmer-tools")
include(":feature:enthusiast-tools")
include(":feature:analytics")
include(":feature:transfers")
include(":feature:profile")
include(":feature:general")
include(":feature:traceability")
