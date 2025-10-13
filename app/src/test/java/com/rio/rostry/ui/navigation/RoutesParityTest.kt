package com.rio.rostry.ui.navigation

import com.rio.rostry.domain.model.UserType
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutesParityTest {

    @Test
    fun accessibleRoutes_haveRegisteredComposable_forEachRole() {
        val registered = roleGraphRegisteredRoutesBasic()

        fun assertAllAllowedAreRegistered(role: UserType) {
            val config = Routes.configFor(role)
            val missing = config.accessibleRoutes.filter { allowed ->
                !Routes.isRouteAccessible(registered, allowed)
            }
            assertTrue("Missing registered routes for role=$role: ${'$'}missing", missing.isEmpty())
        }

        assertAllAllowedAreRegistered(UserType.GENERAL)
        assertAllAllowedAreRegistered(UserType.FARMER)
        assertAllAllowedAreRegistered(UserType.ENTHUSIAST)
    }
}
