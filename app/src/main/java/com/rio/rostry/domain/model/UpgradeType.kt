package com.rio.rostry.domain.model

/**
 * Categorizes different types of verification/upgrade requests in the system.
 */
enum class UpgradeType(
    val displayName: String,
    val description: String,
    val requiredDocuments: List<String>,
    val requiredImages: List<String>
) {
    /**
     * Documents for upgrading from General to Farmer role.
     */
    GENERAL_TO_FARMER(
        displayName = "Become a Farmer",
        description = "Upgrade your account to access farming features",
        requiredDocuments = listOf("FARM_OWNERSHIP_PROOF", "IDENTITY_PROOF"),
        requiredImages = listOf("FARM_PHOTO", "SELFIE_WITH_ID")
    ),

    /**
     * Documents for verifying an unverified/pending Farmer.
     */
    FARMER_VERIFICATION(
        displayName = "Verify Farm",
        description = "Verify your farm to unlock marketplace listing",
        requiredDocuments = listOf("LAND_RECORD", "GOVT_ID"),
        requiredImages = listOf("FARM_ENTRANCE", "LIVESTOCK_PHOTO")
    ),

    /**
     * Documents for upgrading from Farmer to Enthusiast role.
     */
    FARMER_TO_ENTHUSIAST(
        displayName = "Become an Enthusiast",
        description = "Upgrade to Enthusiast for advanced breeding and community features",
        requiredDocuments = listOf("BREEDING_RECORD", "VET_CERTIFICATE"),
        requiredImages = listOf("PRIZE_WINNING_STOCK", "FACILITY_PHOTO")
    );
}
