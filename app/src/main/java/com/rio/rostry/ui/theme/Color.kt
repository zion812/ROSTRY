package com.rio.rostry.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Role-specific color systems for ROSTRY.
 *
 * Themes are designed with accessibility in mind. While runtime contrast checks are not
 * implemented here, the chosen pairs aim to meet WCAG AA for typical UI text sizes.
 */

// =============== General (Blue-centric) ===============
private val GeneralPrimary = Color(0xFF1976D2) // Professional blue
private val GeneralOnPrimary = Color(0xFFFFFFFF)
private val GeneralPrimaryContainer = Color(0xFFBBDEFB)
private val GeneralOnPrimaryContainer = Color(0xFF0B2A47)

private val GeneralSecondary = Color(0xFF42A5F5) // Light blue accent
private val GeneralOnSecondary = Color(0xFF08263A)
private val GeneralSecondaryContainer = Color(0xFFCFE8FF)
private val GeneralOnSecondaryContainer = Color(0xFF0A2A45)

private val GeneralTertiary = Color(0xFF607D8B) // Blue-gray
private val GeneralOnTertiary = Color(0xFFFFFFFF)
private val GeneralTertiaryContainer = Color(0xFFD4E3EA)
private val GeneralOnTertiaryContainer = Color(0xFF102027)

private val GeneralError = Color(0xFFB00020) // Material error
private val GeneralOnError = Color(0xFFFFFFFF)
private val GeneralErrorContainer = Color(0xFFFCD5D7)
private val GeneralOnErrorContainer = Color(0xFF410002)

private val GeneralBackground = Color(0xFFF7FAFC) // Clean whites/light grays
private val GeneralOnBackground = Color(0xFF101317)
private val GeneralSurface = Color(0xFFF5F9FF) // Subtle blue-tinted surface
private val GeneralOnSurface = Color(0xFF111417)
private val GeneralSurfaceVariant = Color(0xFFE1EAF2)
private val GeneralOnSurfaceVariant = Color(0xFF424B53)
private val GeneralOutline = Color(0xFF7B8791)
private val GeneralOutlineVariant = Color(0xFFB9C6D1)

val GeneralLightColors: ColorScheme = lightColorScheme(
    primary = GeneralPrimary,
    onPrimary = GeneralOnPrimary,
    primaryContainer = GeneralPrimaryContainer,
    onPrimaryContainer = GeneralOnPrimaryContainer,
    secondary = GeneralSecondary,
    onSecondary = GeneralOnSecondary,
    secondaryContainer = GeneralSecondaryContainer,
    onSecondaryContainer = GeneralOnSecondaryContainer,
    tertiary = GeneralTertiary,
    onTertiary = GeneralOnTertiary,
    tertiaryContainer = GeneralTertiaryContainer,
    onTertiaryContainer = GeneralOnTertiaryContainer,
    error = GeneralError,
    onError = GeneralOnError,
    errorContainer = GeneralErrorContainer,
    onErrorContainer = GeneralOnErrorContainer,
    background = GeneralBackground,
    onBackground = GeneralOnBackground,
    surface = GeneralSurface,
    onSurface = GeneralOnSurface,
    surfaceVariant = GeneralSurfaceVariant,
    onSurfaceVariant = GeneralOnSurfaceVariant,
    outline = GeneralOutline,
    outlineVariant = GeneralOutlineVariant
)

val GeneralDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D2C47),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF81D4FA),
    onSecondary = Color(0xFF00283E),
    secondaryContainer = Color(0xFF01579B),
    onSecondaryContainer = Color(0xFFE1F5FE),
    tertiary = Color(0xFF90A4AE),
    onTertiary = Color(0xFF0E1A20),
    tertiaryContainer = Color(0xFF37474F),
    onTertiaryContainer = Color(0xFFDDE7EC),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0D1116),
    onBackground = Color(0xFFE2E6EA),
    surface = Color(0xFF0E141A),
    onSurface = Color(0xFFE1E6EA),
    surfaceVariant = Color(0xFF37424C),
    onSurfaceVariant = Color(0xFFBFC9D1),
    outline = Color(0xFF8A96A1),
    outlineVariant = Color(0xFF4B5660)
)

// =============== Farmer (Green/Earth) ===============
private val FarmerPrimary = Color(0xFF388E3C)
private val FarmerOnPrimary = Color(0xFFFFFFFF)
private val FarmerPrimaryContainer = Color(0xFFC8E6C9)
private val FarmerOnPrimaryContainer = Color(0xFF0F2911)

private val FarmerSecondary = Color(0xFF66BB6A)
private val FarmerOnSecondary = Color(0xFF0A2A0C)
private val FarmerSecondaryContainer = Color(0xFFDFF5E1)
private val FarmerOnSecondaryContainer = Color(0xFF103116)

private val FarmerTertiary = Color(0xFF8D6E63) // Earth brown
private val FarmerOnTertiary = Color(0xFFFFFFFF)
private val FarmerTertiaryContainer = Color(0xFFEFDCD5)
private val FarmerOnTertiaryContainer = Color(0xFF2B1B16)

private val FarmerError = Color(0xFFD32F2F)
private val FarmerOnError = Color(0xFFFFFFFF)
private val FarmerErrorContainer = Color(0xFFFAD4D4)
private val FarmerOnErrorContainer = Color(0xFF410002)

private val FarmerBackground = Color(0xFFFAFDF8) // Warm off-whites
private val FarmerOnBackground = Color(0xFF101411)
private val FarmerSurface = Color(0xFFF4FBF5) // Nature-inspired light greens
private val FarmerOnSurface = Color(0xFF111412)
private val FarmerSurfaceVariant = Color(0xFFE2ECE3)
private val FarmerOnSurfaceVariant = Color(0xFF414B43)
private val FarmerOutline = Color(0xFF7F8B82)
private val FarmerOutlineVariant = Color(0xFFBECABD)

val FarmerLightColors: ColorScheme = lightColorScheme(
    primary = FarmerPrimary,
    onPrimary = FarmerOnPrimary,
    primaryContainer = FarmerPrimaryContainer,
    onPrimaryContainer = FarmerOnPrimaryContainer,
    secondary = FarmerSecondary,
    onSecondary = FarmerOnSecondary,
    secondaryContainer = FarmerSecondaryContainer,
    onSecondaryContainer = FarmerOnSecondaryContainer,
    tertiary = FarmerTertiary,
    onTertiary = FarmerOnTertiary,
    tertiaryContainer = FarmerTertiaryContainer,
    onTertiaryContainer = FarmerOnTertiaryContainer,
    error = FarmerError,
    onError = FarmerOnError,
    errorContainer = FarmerErrorContainer,
    onErrorContainer = FarmerOnErrorContainer,
    background = FarmerBackground,
    onBackground = FarmerOnBackground,
    surface = FarmerSurface,
    onSurface = FarmerOnSurface,
    surfaceVariant = FarmerSurfaceVariant,
    onSurfaceVariant = FarmerOnSurfaceVariant,
    outline = FarmerOutline,
    outlineVariant = FarmerOutlineVariant
)

val FarmerDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF0B2710),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFDFF6E1),
    secondary = Color(0xFFC8E6C9),
    onSecondary = Color(0xFF0A1E0C),
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFFBCAAA4),
    onTertiary = Color(0xFF231916),
    tertiaryContainer = Color(0xFF5D4037),
    onTertiaryContainer = Color(0xFFF2E1DB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0E140F),
    onBackground = Color(0xFFE2E7E3),
    surface = Color(0xFF101710),
    onSurface = Color(0xFFE1E6E2),
    surfaceVariant = Color(0xFF404B42),
    onSurfaceVariant = Color(0xFFBECABD),
    outline = Color(0xFF8A958C),
    outlineVariant = Color(0xFF4B554C)
)

// =============== Enthusiast (Deep Orange/Red) ===============
private val EnthusiastPrimary = Color(0xFFF57C00)
private val EnthusiastOnPrimary = Color(0xFF261400)
private val EnthusiastPrimaryContainer = Color(0xFFFFE0B2)
private val EnthusiastOnPrimaryContainer = Color(0xFF3A2000)

private val EnthusiastSecondary = Color(0xFFFF9800)
private val EnthusiastOnSecondary = Color(0xFF251300)
private val EnthusiastSecondaryContainer = Color(0xFFFFE0B2)
private val EnthusiastOnSecondaryContainer = Color(0xFF3A1F00)

private val EnthusiastTertiary = Color(0xFFD84315)
private val EnthusiastOnTertiary = Color(0xFFFFFFFF)
private val EnthusiastTertiaryContainer = Color(0xFFFFCCBC)
private val EnthusiastOnTertiaryContainer = Color(0xFF3B0B00)

private val EnthusiastError = Color(0xFFC62828)
private val EnthusiastOnError = Color(0xFFFFFFFF)
private val EnthusiastErrorContainer = Color(0xFFF6D1D1)
private val EnthusiastOnErrorContainer = Color(0xFF410002)

private val EnthusiastBackground = Color(0xFFFFFBF8) // Warm neutrals
private val EnthusiastOnBackground = Color(0xFF13110F)
private val EnthusiastSurface = Color(0xFFFFF7F1) // Subtle orange-tinted
private val EnthusiastOnSurface = Color(0xFF141210)
private val EnthusiastSurfaceVariant = Color(0xFFEEDFD6)
private val EnthusiastOnSurfaceVariant = Color(0xFF4A403A)
private val EnthusiastOutline = Color(0xFF8A7C73)
private val EnthusiastOutlineVariant = Color(0xFFCABBB1)

val EnthusiastLightColors: ColorScheme = lightColorScheme(
    primary = EnthusiastPrimary,
    onPrimary = EnthusiastOnPrimary,
    primaryContainer = EnthusiastPrimaryContainer,
    onPrimaryContainer = EnthusiastOnPrimaryContainer,
    secondary = EnthusiastSecondary,
    onSecondary = EnthusiastOnSecondary,
    secondaryContainer = EnthusiastSecondaryContainer,
    onSecondaryContainer = EnthusiastOnSecondaryContainer,
    tertiary = EnthusiastTertiary,
    onTertiary = EnthusiastOnTertiary,
    tertiaryContainer = EnthusiastTertiaryContainer,
    onTertiaryContainer = EnthusiastOnTertiaryContainer,
    error = EnthusiastError,
    onError = EnthusiastOnError,
    errorContainer = EnthusiastErrorContainer,
    onErrorContainer = EnthusiastOnErrorContainer,
    background = EnthusiastBackground,
    onBackground = EnthusiastOnBackground,
    surface = EnthusiastSurface,
    onSurface = EnthusiastOnSurface,
    surfaceVariant = EnthusiastSurfaceVariant,
    onSurfaceVariant = EnthusiastOnSurfaceVariant,
    outline = EnthusiastOutline,
    outlineVariant = EnthusiastOutlineVariant
)

val EnthusiastDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFF2D1700),
    primaryContainer = Color(0xFFE65100),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFFFFCC80),
    onSecondary = Color(0xFF2C1600),
    secondaryContainer = Color(0xFFEF6C00),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFFFF8A65),
    onTertiary = Color(0xFF2E0E04),
    tertiaryContainer = Color(0xFFBF360C),
    onTertiaryContainer = Color(0xFFFFE0D7),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF14100D),
    onBackground = Color(0xFFEAE5E1),
    surface = Color(0xFF17120F),
    onSurface = Color(0xFFE9E4E0),
    surfaceVariant = Color(0xFF4B4039),
    onSurfaceVariant = Color(0xFFCBBDB3),
    outline = Color(0xFF95877D),
    outlineVariant = Color(0xFF524840)
)
