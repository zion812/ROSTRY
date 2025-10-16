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

    @Test
    fun registeredRoutes_areAccessibleByAtLeastOneRole() {
        val registered = roleGraphRegisteredRoutesBasic()
        val allAccessible = UserType.values().flatMap {
            Routes.configFor(it).accessibleRoutes
        }.toSet()
        val orphaned = registered.filter { route ->
            !Routes.isRouteAccessible(allAccessible, route)
        }
        assertTrue("Registered routes not accessible by any role: ${'$'}orphaned", orphaned.isEmpty())
    }

    @Test
    fun dynamicRoutes_matchConcreteValues() {
        val allowed = setOf("product/{productId}", "transfer/{transferId}")
        assertTrue(Routes.isRouteAccessible(allowed, "product/abc123"))
        assertTrue(Routes.isRouteAccessible(allowed, "transfer/xyz789"))
        assertTrue(!Routes.isRouteAccessible(allowed, "product/abc/extra"))
    }

    @Test
    fun queryParameters_validateAgainstWhitelist() {
        val allowed = setOf(Routes.FarmerNav.CREATE)
        // Allowed query params
        assertTrue(Routes.isRouteAccessible(allowed, Routes.FarmerNav.CREATE + "?prefillProductId=123"))
        assertTrue(Routes.isRouteAccessible(allowed, Routes.FarmerNav.CREATE + "?pairId=abc"))
        // Unexpected query param
        assertTrue(!Routes.isRouteAccessible(allowed, Routes.FarmerNav.CREATE + "?malicious=true"))
    }
}
