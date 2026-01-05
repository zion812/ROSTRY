package com.rio.rostry.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * ROSTRY Design System Colors.
 * 
 * Primary: Forest Green (#2E7D32) - Represents growth, nature, and farming.
 * Secondary: Amber (#FF8F00) - Represents harvest, sun, and warmth.
 * Tertiary/Accent: Blue (#1565C0) - Represents trust, water, and technology.
 */

// Core Brand Colors
val RostryGreen = Color(0xFF2E7D32)
val RostryGreenLight = Color(0xFF60AD5E)
val RostryGreenDark = Color(0xFF005005)

val RostryAmber = Color(0xFFFF8F00)
val RostryAmberLight = Color(0xFFFFC046)
val RostryAmberDark = Color(0xFFC56000)

val RostryBlue = Color(0xFF1565C0)
val RostryBlueLight = Color(0xFF5E92F3)
val RostryBlueDark = Color(0xFF003C8F)

// Functional Colors
val ErrorRed = Color(0xFFB00020)
val SuccessGreen = Color(0xFF2E7D32) // Same as primary for now, or slightly brighter
val WarningYellow = Color(0xFFFBC02D)

// Neutrals
val Neutral950 = Color(0xFF0A0A0A) // Ultra dark for premium feel
val Neutral900 = Color(0xFF121212) // Darkest background
val Neutral800 = Color(0xFF212121)
val Neutral100 = Color(0xFFF5F5F5) // Lightest background
val Neutral50 = Color(0xFFFAFAFA)
val White = Color(0xFFFFFFFF)

// Enthusiast Premium Palette
val EnthusiastGold = Color(0xFFFFD700)
val EnthusiastGoldVariant = Color(0xFFDAA520)
val EnthusiastObsidian = Color(0xFF0F0F0F)
val EnthusiastVelvet = Color(0xFF4B0082)
val EnthusiastGlass = Color(0x33FFFFFF)
val EnthusiastSurface = Color(0xFF1A1A1A)

// Light Theme Scheme
val RostryLightColors: ColorScheme = lightColorScheme(
    primary = RostryGreen,
    onPrimary = White,
    primaryContainer = Color(0xFFC8E6C9), // Light green container
    onPrimaryContainer = Color(0xFF002105),
    
    secondary = RostryAmber,
    onSecondary = Color(0xFF000000), // High contrast on amber
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF261100),
    
    tertiary = RostryBlue,
    onTertiary = White,
    tertiaryContainer = Color(0xFFBBDEFB),
    onTertiaryContainer = Color(0xFF001E3C),
    
    error = ErrorRed,
    onError = White,
    errorContainer = Color(0xFFFCD5D7),
    onErrorContainer = Color(0xFF410002),
    
    background = Neutral50,
    onBackground = Neutral900,
    
    surface = White,
    onSurface = Neutral900,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF424242),
    
    outline = Color(0xFF757575)
)

// Dark Theme Scheme
val RostryDarkColors: ColorScheme = darkColorScheme(
    primary = RostryGreenLight, // Lighter for dark mode
    onPrimary = Color(0xFF003300),
    primaryContainer = RostryGreenDark,
    onPrimaryContainer = Color(0xFFC8E6C9),
    
    secondary = RostryAmber,
    onSecondary = Color(0xFF000000),
    secondaryContainer = RostryAmberDark,
    onSecondaryContainer = Color(0xFFFFE0B2),
    
    tertiary = RostryBlueLight,
    onTertiary = Color(0xFF001E3C),
    tertiaryContainer = RostryBlueDark,
    onTertiaryContainer = Color(0xFFBBDEFB),
    
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Neutral900,
    onBackground = Neutral100,
    
    surface = Neutral800,
    onSurface = Neutral100,
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = Color(0xFFBDBDBD),
    
    outline = Color(0xFF8A8A8A)
)

// Enthusiast Theme Schemes (Premium Experience)
val EnthusiastDarkColors: ColorScheme = darkColorScheme(
    primary = EnthusiastGold,
    onPrimary = EnthusiastObsidian,
    primaryContainer = EnthusiastGoldVariant,
    onPrimaryContainer = Color.White,
    
    secondary = EnthusiastVelvet,
    onSecondary = White,
    secondaryContainer = Color(0xFF310055),
    onSecondaryContainer = Color(0xFFE1BEE7),
    
    tertiary = Color(0xFFAFAFAF), // Silver/Steel accent
    onTertiary = EnthusiastObsidian,
    
    background = Neutral950,
    onBackground = White,
    
    surface = EnthusiastSurface,
    onSurface = White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
    
    outline = EnthusiastGoldVariant
)

val EnthusiastLightColors: ColorScheme = lightColorScheme(
    primary = EnthusiastGoldVariant,
    onPrimary = White,
    primaryContainer = Color(0xFFFFF1C1),
    onPrimaryContainer = Color(0xFF261100),
    
    secondary = EnthusiastVelvet,
    onSecondary = White,
    
    background = White,
    onBackground = EnthusiastObsidian,
    
    surface = Neutral50,
    onSurface = EnthusiastObsidian
)

// Legacy Mappings
val GeneralLightColors = RostryLightColors
val GeneralDarkColors = RostryDarkColors
val FarmerLightColors = RostryLightColors
val FarmerDarkColors = RostryDarkColors

