package com.rio.rostry.domain.model

enum class UserType(
    val displayName: String,
    val primaryFeatures: List<String>
) {
    GENERAL(
        displayName = "General",
        primaryFeatures = listOf(
            "Market browsing",
            "Ordering",
            "Basic social feed",
            "Cart management",
            "Profile management"
        )
    ),
    FARMER(
        displayName = "Farmer",
        primaryFeatures = listOf(
            "Market participation",
            "Product listing",
            "Farm management",
            "Sales analytics",
            "Community engagement"
        )
    ),
    ENTHUSIAST(
        displayName = "Enthusiast",
        primaryFeatures = listOf(
            "Advanced tracking",
            "Breeding records",
            "Transfer management",
            "Analytics dashboard",
            "Coin wallet"
        )
    ),
    SUPPORT(
        displayName = "Support Agent",
        primaryFeatures = listOf(
            "User lookup",
            "Order viewing",
            "Communication tools"
        )
    ),
    MODERATOR(
        displayName = "Content Moderator",
        primaryFeatures = listOf(
            "Product moderation",
            "User moderation",
            "Review management"
        )
    ),
    ADMIN(
        displayName = "Super Admin",
        primaryFeatures = listOf(
            "Verification management",
            "Audit log viewing",
            "System configuration",
            "User management",
            "Full access"
        )
    );

    fun nextLevel(): UserType? = when (this) {
        GENERAL -> FARMER
        FARMER -> ENTHUSIAST
        ENTHUSIAST -> null
        SUPPORT -> null
        MODERATOR -> null
        ADMIN -> null
    }
}
