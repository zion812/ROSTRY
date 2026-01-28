package com.rio.rostry.domain.showcase

/**
 * Configuration options for generating a showcase card.
 */
data class ShowcaseConfig(
    val theme: ShowcaseTheme = ShowcaseTheme.DARK_PREMIUM,
    val showWeight: Boolean = true,
    val showAge: Boolean = true,
    val showPedigreeBadge: Boolean = true,
    val showVaccinationBadge: Boolean = true,
    val showWins: Boolean = true
)

/**
 * Visual themes for the showcase card.
 */
enum class ShowcaseTheme(val label: String, val primaryColor: Int, val accentColor: Int) {
    DARK_PREMIUM("Midnight", 0xFF1a1a2e.toInt(), 0xFFFFD700.toInt()), // Dark Blue & Gold
    LIGHT_ELEGANCE("Marble", 0xFFF5F5F5.toInt(), 0xFF1B5E20.toInt()), // White & Green
    GOLD_LUXURY("Royal", 0xFF2C2C2C.toInt(), 0xFFDAA520.toInt()),    // Black & Gold
    NATURE_FRESH("Nature", 0xFFE8F5E9.toInt(), 0xFF4CAF50.toInt())    // Light Green & Dark Green
}
