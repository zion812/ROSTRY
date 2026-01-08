package com.rio.rostry.domain.model

/**
 * Categorizes different types of verification/upgrade requests in the system.
 * 
 * Photo types:
 * - SELFIE_WITH_CHICKEN: User's photo holding or standing with their chicken
 * - FARM_PHOTO: Overview photo of the farm/poultry area
 * - CHICKENS_PHOTO: Photo showing the chickens/birds
 * 
 * Document types:
 * - GOVT_ID: Any government ID (Aadhar, Voter ID, Job Card, PAN, etc.)
 */
enum class UpgradeType(
    val displayName: String,
    val description: String,
    val requiredDocuments: List<String>,
    val requiredImages: List<String>
) {
    /**
     * Documents for upgrading from General to Farmer role.
     * Requires: Selfie with chicken + Any Government ID
     */
    GENERAL_TO_FARMER(
        displayName = "Become a Farmer",
        description = "Upgrade your account to access farming features",
        requiredDocuments = emptyList(),
        requiredImages = emptyList()
    ),

    /**
     * Documents for verifying an unverified/pending Farmer.
     * Requires: Photos of user with chickens, farm, and chickens + Any Government ID
     */
    FARMER_VERIFICATION(
        displayName = "Verify Farm",
        description = "Verify your farm to unlock marketplace listing",
        requiredDocuments = emptyList(),
        requiredImages = emptyList()
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

