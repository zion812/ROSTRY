package com.rio.rostry.domain.model

import com.rio.rostry.data.database.entity.FarmAssetEntity

/**
 * Extension functions for FarmAssetEntity to support the farm-to-market bridge feature.
 * These extensions help convert farm assets to market listing data.
 */

/**
 * Form data for pre-populating the Create Listing screen.
 * Contains suggested values derived from the source FarmAssetEntity.
 */
data class ListingFormData(
    val suggestedTitle: String,
    val suggestedDescription: String,
    val imageUrls: List<String>,
    val category: String,
    val maxQuantity: Double,
    val unit: String,
    val latitude: Double?,
    val longitude: Double?,
    val locationName: String?,
    val breed: String?,
    val healthStatus: String
)

/**
 * Converts a FarmAssetEntity to pre-filled listing form data.
 * Used by CreateListingScreen for form pre-population.
 * 
 * The suggested title combines breed and name for marketing appeal.
 * The description is auto-generated from asset properties.
 */
fun FarmAssetEntity.toListingData(): ListingFormData = ListingFormData(
    suggestedTitle = buildSuggestedTitle(),
    suggestedDescription = buildSuggestedDescription(),
    imageUrls = imageUrls,
    category = category,
    maxQuantity = quantity,
    unit = unit,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    breed = breed,
    healthStatus = healthStatus
)

/**
 * Builds a marketing-friendly title from asset properties.
 * Examples:
 * - "Aseel Rooster" (breed + generic name)
 * - "Layer Batch 203" (category + name)
 */
private fun FarmAssetEntity.buildSuggestedTitle(): String {
    val parts = listOfNotNull(
        breed?.takeIf { it.isNotBlank() },
        name.takeIf { it.isNotBlank() }
    )
    return parts.joinToString(" ").takeIf { it.isNotBlank() } 
        ?: "$category for sale"
}

/**
 * Builds an informative description from asset properties.
 * Includes: category, breed, age, weight, health status.
 */
private fun FarmAssetEntity.buildSuggestedDescription(): String {
    if (description.isNotBlank()) return description
    
    return buildString {
        append(category)
        
        breed?.takeIf { it.isNotBlank() }?.let {
            append(" - $it")
        }
        
        ageWeeks?.let {
            append(", $it weeks old")
        }
        
        weightGrams?.let { weight ->
            val kg = weight / 1000.0
            if (kg >= 1.0) {
                append(", %.1f kg".format(kg))
            } else {
                append(", ${weight.toInt()}g")
            }
        }
        
        if (healthStatus != "HEALTHY") {
            append(" (Status: $healthStatus)")
        }
        
        gender?.let {
            append(". ${it.lowercase().replaceFirstChar { c -> c.uppercase() }}")
        }
        
        color?.takeIf { it.isNotBlank() }?.let {
            append(", $it color")
        }
    }
}

/**
 * Checks if the asset can be listed for sale.
 * 
 * Rules:
 * - Not already listed (listingId == null)
 * - Not deleted
 * - Status is ACTIVE
 * - Quantity > 0
 */
fun FarmAssetEntity.canBeListed(): Boolean =
    listingId == null &&
    !isDeleted &&
    status == "ACTIVE" &&
    quantity > 0

/**
 * Checks if the asset is ready for sale based on weight threshold.
 * Default threshold is 1500g for broilers, adjustable per category.
 */
fun FarmAssetEntity.isMarketReady(minWeightGrams: Double = 1500.0): Boolean =
    canBeListed() && (weightGrams ?: 0.0) >= minWeightGrams

/**
 * Returns a human-readable status for display in the asset details screen.
 */
fun FarmAssetEntity.getListingStatus(): String = when {
    listingId != null -> "Listed for Sale"
    soldAt != null -> "Sold"
    isDeleted -> "Archived"
    status != "ACTIVE" -> status.lowercase().replaceFirstChar { it.uppercase() }
    else -> "Available"
}
