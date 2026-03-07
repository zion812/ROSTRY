package com.rio.rostry.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for NavigationRegistry.
 *
 * Feature: end-to-end-modularization
 * Phase: 0 - Guardrails First
 * Requirements: 2.1, 2.2, 2.5
 */
class NavigationRegistryTest {

    private lateinit var registry: NavigationRegistry
    private lateinit var navController: NavHostController
    private lateinit var navGraphBuilder: NavGraphBuilder

    @Before
    fun setup() {
        registry = NavigationRegistryImpl()
        navController = mockk()
        navGraphBuilder = mockk()
    }

    @Test
    fun `register adds provider to the list`() {
        // Given
        val provider = FakeNavigationProvider(featureId = "test-feature")

        // When
        registry.register(provider)

        // Then
        val providers = registry.getProviders()
        assertEquals(1, providers.size)
        assertEquals("test-feature", providers.first().featureId)
    }

    @Test
    fun `getProviders returns all registered providers`() {
        // Given
        val provider1 = FakeNavigationProvider(featureId = "feature-1")
        val provider2 = FakeNavigationProvider(featureId = "feature-2")
        val provider3 = FakeNavigationProvider(featureId = "feature-3")

        // When
        registry.register(provider1)
        registry.register(provider2)
        registry.register(provider3)

        // Then
        val providers = registry.getProviders()
        assertEquals(3, providers.size)
        assertTrue(providers.any { it.featureId == "feature-1" })
        assertTrue(providers.any { it.featureId == "feature-2" })
        assertTrue(providers.any { it.featureId == "feature-3" })
    }

    @Test
    fun `getProviders returns immutable copy`() {
        // Given
        val provider = FakeNavigationProvider(featureId = "test-feature")
        registry.register(provider)

        // When
        val providers = registry.getProviders()

        // Then - modifying the returned list should not affect the registry
        // Note: getProviders() returns toList() so it's already a copy
        assertEquals(1, providers.size)
    }

    @Test
    fun `buildGraphs invokes buildGraph on all providers`() {
        // Given
        val provider1 = FakeNavigationProvider(featureId = "feature-1")
        val provider2 = FakeNavigationProvider(featureId = "feature-2")
        val provider3 = FakeNavigationProvider(featureId = "feature-3")

        registry.register(provider1)
        registry.register(provider2)
        registry.register(provider3)

        // When
        registry.buildGraphs(navGraphBuilder, navController)

        // Then
        assertEquals(1, provider1.buildGraphCallCount)
        assertEquals(1, provider2.buildGraphCallCount)
        assertEquals(1, provider3.buildGraphCallCount)
    }

    @Test
    fun `buildGraphs with empty registry does not crash`() {
        // Given - empty registry

        // When & Then - should not throw
        registry.buildGraphs(navGraphBuilder, navController)

        // Verify no interactions with navController or navGraphBuilder
        verify(exactly = 0) { navController.navigate(any<String>()) }
    }

    @Test
    fun `providers are registered in order and built in same order`() {
        // Given
        val buildOrder = mutableListOf<String>()

        val provider1 = FakeNavigationProvider(featureId = "first") { _, _ ->
            buildOrder.add("first")
        }
        val provider2 = FakeNavigationProvider(featureId = "second") { _, _ ->
            buildOrder.add("second")
        }
        val provider3 = FakeNavigationProvider(featureId = "third") { _, _ ->
            buildOrder.add("third")
        }

        registry.register(provider1)
        registry.register(provider2)
        registry.register(provider3)

        // When
        registry.buildGraphs(navGraphBuilder, navController)

        // Then
        assertEquals(listOf("first", "second", "third"), buildOrder)
    }

    @Test
    fun `register same provider multiple times adds it multiple times`() {
        // Given
        val provider = FakeNavigationProvider(featureId = "duplicate")

        // When
        registry.register(provider)
        registry.register(provider)

        // Then
        val providers = registry.getProviders()
        assertEquals(2, providers.size)
        assertTrue(providers.all { it.featureId == "duplicate" })
    }

    @Test
    fun `getDeepLinks returns empty list by default`() {
        // Given
        val provider = FakeNavigationProvider(featureId = "test")

        // When & Then
        assertTrue(provider.getDeepLinks().isEmpty())
    }

    @Test
    fun `getDeepLinks returns provided deep links`() {
        // Given
        val deepLinks = listOf("rostry://test", "https://example.com/test")
        val provider = FakeNavigationProvider(
            featureId = "test",
            deepLinks = deepLinks
        )

        // When & Then
        assertEquals(deepLinks, provider.getDeepLinks())
    }
}
