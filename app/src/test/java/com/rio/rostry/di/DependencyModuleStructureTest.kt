package com.rio.rostry.di

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DependencyModuleStructureTest {

    private val diDir = listOf(
        File("src/main/java/com/rio/rostry/di"),
        File("app/src/main/java/com/rio/rostry/di")
    ).firstOrNull(File::exists)
        ?: error("Unable to locate app DI directory.")

    @Test
    fun repository_bindings_are_split_across_domain_modules() {
        assertFalse(
            File(diDir, "RepositoryModule.kt").exists(),
            "RepositoryModule.kt should stay deleted. Bind repositories in domain-scoped modules instead."
        )

        val expectedModules = listOf(
            "CoreRepositoryModule.kt",
            "CommerceRepositoryModule.kt",
            "MonitoringRepositoryModule.kt",
            "SocialRepositoryModule.kt",
            "OperationsRepositoryModule.kt",
            "AssetRepositoryModule.kt",
            "PaymentGatewayModule.kt"
        )

        expectedModules.forEach { fileName ->
            assertTrue(
                File(diDir, fileName).exists(),
                "Expected $fileName to exist in the DI package."
            )
        }
    }

    @Test
    fun extracted_feature_sources_stay_out_of_app_module() {
        val appJavaRoot = diDir.parentFile

        val extractedFiles = listOf(
            "ui/gamification/AchievementsScreen.kt",
            "ui/social/LeaderboardScreen.kt",
            "ui/notifications/NotificationsScreen.kt",
            "ui/events/EventsScreen.kt",
            "ui/expert/ExpertBookingScreen.kt",
            "ui/insights/InsightsScreen.kt",
            "ui/feedback/FeedbackScreen.kt",
            "ui/support/HelpScreen.kt"
        )

        extractedFiles.forEach { relativePath ->
            assertFalse(
                File(appJavaRoot, relativePath).exists(),
                "Expected $relativePath to stay extracted from the app module."
            )
        }
    }
}
