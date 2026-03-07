package com.rio.rostry.core.testing

import java.time.Instant
import java.util.UUID

/**
 * Factory object for creating test data.
 * Provides consistent test data across test suites.
 */
object FakeData {
    
    /**
     * Generate a random UUID string for testing.
     */
    fun randomId(): String = UUID.randomUUID().toString()
    
    /**
     * Generate a test timestamp.
     */
    fun testTimestamp(): Instant = Instant.parse("2024-01-01T00:00:00Z")
    
    /**
     * Generate a test user ID.
     */
    fun testUserId(suffix: String = "1"): String = "user-$suffix"
    
    /**
     * Generate a test farmer ID.
     */
    fun testFarmerId(suffix: String = "1"): String = "farmer-$suffix"
    
    /**
     * Generate a test asset ID.
     */
    fun testAssetId(suffix: String = "1"): String = "asset-$suffix"
    
    /**
     * Generate a test inventory ID.
     */
    fun testInventoryId(suffix: String = "1"): String = "inventory-$suffix"
    
    /**
     * Generate a test listing ID.
     */
    fun testListingId(suffix: String = "1"): String = "listing-$suffix"
    
    /**
     * Generate a test phone number.
     */
    fun testPhoneNumber(): String = "+1234567890"
    
    /**
     * Generate a test email.
     */
    fun testEmail(username: String = "test"): String = "$username@example.com"
}
