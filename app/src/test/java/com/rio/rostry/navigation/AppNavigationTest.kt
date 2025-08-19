package com.rio.rostry.navigation

import org.junit.Test
import org.junit.Assert.*

class AppNavigationTest {
    
    @Test
    fun testRouteBuilding() {
        // Test that route builders work correctly
        val fowlId = "test-fowl-id"
        val expectedRoute = "fowl_detail/$fowlId"
        val actualRoute = AppRoutes.fowlDetail(fowlId)
        assertEquals(expectedRoute, actualRoute)
    }
    
    @Test
    fun testDestinationRoutes() {
        // Test that destination routes are correctly defined
        assertEquals("home", AppDestination.Home.route)
        assertEquals("fowl_detail/{fowlId}", AppDestination.FowlDetail("").route)
    }
    
    @Test
    fun testDestinationArguments() {
        // Test that destination arguments are correctly defined
        val fowlDetailDestination = AppDestination.FowlDetail("")
        assertEquals(1, fowlDetailDestination.arguments.size)
        assertEquals("fowlId", fowlDetailDestination.arguments[0].name)
    }
}