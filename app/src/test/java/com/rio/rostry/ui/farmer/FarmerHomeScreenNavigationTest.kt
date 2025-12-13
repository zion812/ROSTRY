package com.rio.rostry.ui.farmer

import com.rio.rostry.ui.navigation.Routes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// This test file would validate that when the navigation methods in FarmerHomeViewModel
// are called with the new builder patterns, they produce the expected routes
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FarmerHomeScreenNavigationTest {

    @Test
    fun `transfersWithFilter builder generates correct route with ELIGIBLE status`() = runBlocking {
        val expectedRoute = "transfer/list?status=ELIGIBLE"
        val actualRoute = Routes.Builders.transfersWithFilter("ELIGIBLE")
        
        assertEquals(expectedRoute, actualRoute)
    }

    @Test
    fun `productsWithFilter builder generates correct route with ready_to_list filter`() = runBlocking {
        val expectedRoute = "product/market?filter=ready_to_list"
        val actualRoute = Routes.Builders.productsWithFilter("ready_to_list")
        
        assertEquals(expectedRoute, actualRoute)
    }

    @Test
    fun `transfersWithFilter builder generates correct route with PENDING status`() = runBlocking {
        val expectedRoute = "transfer/list?status=PENDING"
        val actualRoute = Routes.Builders.transfersWithFilter("PENDING")
        
        assertEquals(expectedRoute, actualRoute)
    }

    @Test
    fun `productsWithFilter builder generates correct route with different filters`() = runBlocking {
        val testCases = mapOf(
            "recent" to "product/market?filter=recent",
            "trending" to "product/market?filter=trending",
            "verified" to "product/market?filter=verified"
        )
        
        testCases.forEach { (filter, expectedRoute) ->
            val actualRoute = Routes.Builders.productsWithFilter(filter)
            assertEquals("Route mismatch for filter: $filter", expectedRoute, actualRoute)
        }
    }
}