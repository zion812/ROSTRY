package com.rio.rostry.ui.navigation

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationStructureTest {

    private val navHostFile = listOf(
        File("src/main/java/com/rio/rostry/ui/navigation/AppNavHost.kt"),
        File("app/src/main/java/com/rio/rostry/ui/navigation/AppNavHost.kt")
    ).firstOrNull(File::exists)
        ?: error("Unable to locate AppNavHost.kt for navigation structure tests.")

    @Test
    fun appNavHost_stays_below_size_budget() {
        val lineCount = navHostFile.readLines().size
        assertTrue(
            lineCount <= 3800,
            "AppNavHost.kt exceeded the 3800-line budget. Split routes into dedicated navigation files."
        )
    }

    @Test
    fun appNavHost_does_not_inline_extracted_auth_and_chrome_components() {
        val source = navHostFile.readText()
        val extractedFunctions = listOf(
            "private fun AuthFlow(",
            "private fun RoleBottomBar(",
            "private fun NotificationsAction(",
            "private fun AccountMenuAction(",
            "private fun GuestModeBanner(",
            "private fun GuestSignInDialog(",
            "private fun ErrorScreen(",
            "private fun isWriteAction(",
            "composable(Routes.Admin.PORTAL)",
            "composable(Routes.Admin.DASHBOARD)",
            "composable(Routes.ANALYTICS_GENERAL)",
            "composable(Routes.MONITORING_VACCINATION)",
            "composable(Routes.LEADERBOARD)",
            "composable(Routes.NOTIFICATIONS)",
            "composable(Routes.EVENTS)",
            "composable(Routes.EXPERT_BOOKING)"
        )

        extractedFunctions.forEach { signature ->
            assertFalse(
                source.contains(signature),
                "Expected $signature to stay extracted from AppNavHost.kt."
            )
        }
    }
}
