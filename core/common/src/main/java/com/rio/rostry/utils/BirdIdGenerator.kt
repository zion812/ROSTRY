package com.rio.rostry.utils

/**
 * Utility object for generating human-readable bird IDs.
 *
 * This provides a stable, memorable ID layer independent of the technical productId UUID.
 * Bird codes are deterministic based on product attributes and are intended for local,
 * in-person exchanges and QR code displays to make identification easier for users.
 * For existing products with null birdCode/colorTag, use the backfill migration in `ProductRepository` or lazy initialization in UI layers.
 */
object BirdIdGenerator {
    /**
     * Generates a deterministic, human-readable bird code from product attributes.
     *
     * Format: [COLOR_CODE]-[BREED_CODE]-[SEQUENCE]
     * - COLOR_CODE: First 3 uppercase letters of the color, or "UNK" if null or blank
     * - BREED_CODE: First 2 uppercase letters of the breed, or "XX" if null or blank
     * - SEQUENCE: Last 3 uppercase characters of the productId for uniqueness, or "000" if productId is blank
     *
     * Example: "BLK-RIR-001" for a black Rhode Island Red with productId ending in "001"
     * Blank strings are treated as missing values.
     *
     * @param color The bird's color (e.g., "Black", "White")
     * @param breed The bird's breed (e.g., "Rhode Island Red")
     * @param sellerId The seller's ID (currently unused but included for future extensibility)
     * @param productId The technical product ID (used for sequence uniqueness; falls back to "000" if blank)
     * @return A formatted bird code string
     */
    fun generate(color: String?, breed: String?, sellerId: String, productId: String): String {
        val colorCode = if (color.isNullOrBlank()) "UNK" else color.take(3).uppercase()
        val breedCode = if (breed.isNullOrBlank()) "XX" else breed.take(2).uppercase()
        // Use last 3 chars of productId for uniqueness, or "000" if blank
        val sequence = productId.takeIf { it.isNotBlank() }?.takeLast(3)?.uppercase() ?: "000"
        return "$colorCode-$breedCode-$sequence"
    }
    
    /**
     * Generates a color tag class for visual identification.
     * 
     * Maps common color names to standardized tags for filtering and display.
     * This helps in creating visual cues like colored chips or badges.
     * Blank strings are treated as missing values.
     * 
     * @param color The bird's color string
     * @return A standardized color tag or null if color is null or blank
     */
    fun colorTag(color: String?): String? {
        return if (color.isNullOrBlank()) null else color.uppercase()
    }
}
