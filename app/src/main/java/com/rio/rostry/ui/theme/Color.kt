package com.rio.rostry.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * ROSTRY Tiered Color System
 * 
 * 1. Global Brand Spine: Deep Organic Green (#1B5E20) + Warm Neutrals
 * 2. General Role: Trust & Discovery (Teal/Green)
 * 3. Farmer Role: Earthy & Robust (High Contrast Green + Terracotta)
 * 4. Enthusiast Role: Electric & Heroic (Royal Violet + Cyan)
 */

// ==========================================
// 1. GLOBAL BRAND SPINE (Used by all roles)
// ==========================================
val BrandGreen = Color(0xFF1B5E20) // Deep organic green - Main brand identity
val BrandGreenLight = Color(0xFF4C8C4A)
val BrandGreenDark = Color(0xFF003300)

// Neutrals - Warm & Organic
val NeutralBg = Color(0xFFF5F2EB)      // Warm off-white (Farm paper / Sunlight)
val NeutralSurface = Color(0xFFE4DED2) // Soft warm grey for cards
val NeutralTextPrimary = Color(0xFF212121)   // Dark charcoal (not black)
val NeutralTextSecondary = Color(0xFF4E4E4E) // softer dark grey
val NeutralBorder = Color(0xFFD0C7BA)

val White = Color(0xFFFFFFFF)

// Functional Alert System
val FunctionalError = Color(0xFFD32F2F)   // Clear red (not neon)
val FunctionalWarning = Color(0xFFFFC107) // Amber/Yellow
val FunctionalSuccess = BrandGreen        // Success feels like Rostry

// ==========================================
// 2. GENERAL ROLE (Trust + Discovery)
// ==========================================
// Feelings: Safe, curious, clean, trustworthy
val GeneralPrimary = Color(0xFF2E7D32)    // Friendly green for main CTAs
val GeneralAccent = Color(0xFF009688)     // Soft teal for links/info
val GeneralInfo = Color(0xFF1976D2)       // Information blue
val GeneralSurface = Color(0xFFFFFFFF)    // Pure white cards

// ==========================================
// 3. FARMER ROLE (Earthy + Bold + Legible)
// ==========================================
// Feelings: In control, productive, respected. Optimized for sunlight.
val FarmerPrimary = Color(0xFF4A8C2A)     // Earthy action green (brighter than brand)
val FarmerAccent = Color(0xFFD1A24E)      // Golden wheat/Terracotta for tasks
val FarmerEmphasis = Color(0xFFFF9800)    // Orange for "Today's Tasks" / Progress
val FarmerBackground = Color(0xFFF3E7D8)  // Light warm beige (Soil/Shed vibe)
val FarmerSurface = Color(0xFFFFF9F0)     // Very soft warm card
val FarmerBorder = Color(0xFFC9B79D)

// ==========================================
// 4. ENTHUSIAST ROLE (Arena / Champion Mode)
// ==========================================
// Feelings: Elite, competitive, proud, "Command Center"
val EnthusiastPrimary = Color(0xFF673AB7) // Royal Violet
val EnthusiastSecondary = Color(0xFF512DA8) // Deeper Violet
val EnthusiastElectric = Color(0xFF00E5FF) // Electric Cyan (Streaks, Rarity)
val EnthusiastGold = Color(0xFFFFC107)    // Rank: Gold
val EnthusiastSilver = Color(0xFFB0BEC5)  // Rank: Silver
val EnthusiastBronze = Color(0xFF8D6E63)  // Rank: Bronze
val EnthusiastBackground = Color(0xFFF0EDF9) // Light violet-tinted neutral
val EnthusiastOverlay = Color(0xFF1C102F) // Dark arena overlay

// ==========================================
// THEME SCHEMES
// ==========================================

// GENERAL THEME (Base)
val GeneralLightColors: ColorScheme = lightColorScheme(
    primary = GeneralPrimary,
    onPrimary = White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF002105),
    
    secondary = GeneralAccent,
    onSecondary = White,
    secondaryContainer = Color(0xFFDBFbf8),
    onSecondaryContainer = Color(0xFF004D40),
    
    tertiary = GeneralInfo,
    onTertiary = White,
    
    background = NeutralBg,
    onBackground = NeutralTextPrimary,
    
    surface = GeneralSurface,
    onSurface = NeutralTextPrimary,
    surfaceVariant = NeutralSurface,
    onSurfaceVariant = NeutralTextSecondary,
    
    error = FunctionalError,
    onError = White,
    
    outline = NeutralBorder
)

// GENERAL DARK (Mapped to same vibe but dark)
val GeneralDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF81C784), // Lighter green
    onPrimary = Color(0xFF003300),
    primaryContainer = GeneralPrimary,
    onPrimaryContainer = White,
    
    secondary = Color(0xFF80CBC4), // Lighter teal
    onSecondary = Color(0xFF004D40),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    
    error = Color(0xFFCF6679)
)

// FARMER THEME (High Contrast / Earthy)
val FarmerLightColors: ColorScheme = lightColorScheme(
    primary = FarmerPrimary,
    onPrimary = White,
    primaryContainer = Color(0xFFDCEDC8),
    onPrimaryContainer = Color(0xFF33691E),
    
    secondary = FarmerAccent,
    onSecondary = Color(0xFF3E2723), // Dark brown on gold
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Color(0xFF3E2723),
    
    tertiary = FarmerEmphasis,
    onTertiary = Color.Black,
    
    background = FarmerBackground,
    onBackground = Color(0xFF3E2723), // Dark earthy text
    
    surface = FarmerSurface,
    onSurface = Color(0xFF3E2723),
    surfaceVariant = FarmerBorder,
    onSurfaceVariant = Color(0xFF5D4037),
    
    error = FunctionalError,
    onError = White,
    
    outline = FarmerBorder
)

val FarmerDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFAED581), // Light earthy green
    onPrimary = Color(0xFF33691E),
    
    secondary = FarmerAccent,
    onSecondary = Color(0xFF3E2723),
    
    background = Color(0xFF1B1B1B), // Dark soil
    onBackground = Color(0xFFEFEBE9),
    
    surface = Color(0xFF2D2D2D),
    onSurface = Color(0xFFEFEBE9),
    
    error = Color(0xFFE57373)
)

// ENTHUSIAST THEME (Royal / Electric)
val EnthusiastLightColors: ColorScheme = lightColorScheme(
    primary = EnthusiastPrimary,
    onPrimary = White,
    primaryContainer = Color(0xFFD1C4E9),
    onPrimaryContainer = Color(0xFF311B92),
    
    secondary = EnthusiastSecondary,
    onSecondary = White,
    
    tertiary = EnthusiastElectric,
    onTertiary = Color.Black,
    
    background = EnthusiastBackground,
    onBackground = Color(0xFF1A237E), // Deep indigo text
    
    surface = White,
    onSurface = Color(0xFF1A237E),
    surfaceVariant = Color(0xFFE8EAF6),
    onSurfaceVariant = Color(0xFF3949AB),
    
    error = FunctionalError,
    onError = White,
    
    outline = Color(0xFF9FA8DA)
)

val EnthusiastDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFB39DDB), // Light violet
    onPrimary = Color(0xFF311B92),
    primaryContainer = EnthusiastPrimary,
    onPrimaryContainer = White,
    
    secondary = EnthusiastElectric, // Electric cyan pops in dark
    onSecondary = Color.Black,
    
    tertiary = EnthusiastGold,
    onTertiary = Color.Black,
    
    background = EnthusiastOverlay,
    onBackground = White,
    
    surface = Color(0xFF282845),
    onSurface = White,
    
    error = Color(0xFFFF8A80)
)

// Legacy / Helper Colors for backwards compatibility
val SuccessColor = FunctionalSuccess
val RostryGreen = BrandGreen
val RostryGreenLight = BrandGreenLight
val RostryAmber = FunctionalWarning
val WhiteColor = White

// Legacy Enthusiast colors (mapped to new system)
val EnthusiastGoldVariant = Color(0xFFDAA520) // Darker gold
val EnthusiastObsidian = Color(0xFF0F0F0F)    // Near black
val EnthusiastGlass = Color(0x33FFFFFF)       // Translucent white
val EnthusiastVelvet = Color(0xFF4B0082)      // Deep indigo/purple

// Legacy Psychology colors
val ElectricBlue = Color(0xFF00D4FF)   // Interactive CTAs
val Coral = Color(0xFFFF6B6B)           // Notifications / Urgency
val Emerald = Color(0xFF10B981)         // Success / Health
val RostryBlue = Color(0xFF1565C0)      // Trust / Info
val DeepNavy = Color(0xFF0A1628)        // Premium dark

