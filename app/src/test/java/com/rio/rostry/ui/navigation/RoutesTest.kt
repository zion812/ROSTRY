package com.rio.rostry.ui.navigation

import com.rio.rostry.domain.model.UserType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutesTest {

    @Test
    fun isRouteAccessible_matchesDynamicSegments() {
        val allowed = setOf(
            Routes.PRODUCT_DETAILS,           // product/{productId}
            Routes.TRANSFER_DETAILS,          // transfer/{transferId}
            Routes.MONITORING_DAILY_LOG_PRODUCT // monitoring/daily_log/{productId}
        )

        assertTrue(Routes.isRouteAccessible(allowed, "product/abc123"))
        assertTrue(Routes.isRouteAccessible(allowed, "transfer/tx_987"))
        assertTrue(Routes.isRouteAccessible(allowed, "monitoring/daily_log/p_42"))
    }

    @Test
    fun isRouteAccessible_rejectsUnknownRoutes() {
        val allowed = Routes.configFor(UserType.ENTHUSIAST).accessibleRoutes
        assertFalse(Routes.isRouteAccessible(allowed, "admin/secret"))
        assertFalse(Routes.isRouteAccessible(allowed, "random/route"))
    }

    @Test
    fun isRouteAccessible_exactMatchWhenNoArgs() {
        val allowed = setOf(Routes.SOCIAL_FEED)
        assertTrue(Routes.isRouteAccessible(allowed, Routes.SOCIAL_FEED))
        assertFalse(Routes.isRouteAccessible(allowed, "social/feed/extra"))
    }
}
