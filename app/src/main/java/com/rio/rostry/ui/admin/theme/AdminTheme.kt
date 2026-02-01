package com.rio.rostry.ui.admin.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Admin-specific theme for the Admin Portal.
 * 
 * Provides a distinct visual identity for the admin experience
 * with a dark, professional color scheme that clearly differentiates
 * admin mode from regular user mode.
 */

// ========== Admin Color Palette ==========

object AdminColors {
    // Primary colors - Deep Indigo/Navy theme
    val Primary = Color(0xFF1A237E)
    val PrimaryVariant = Color(0xFF0D47A1)
    val PrimaryLight = Color(0xFF534BAE)
    
    // Secondary colors - Cyan accent for highlights
    val Secondary = Color(0xFF00BCD4)
    val SecondaryVariant = Color(0xFF0097A7)
    val SecondaryLight = Color(0xFF4DD0E1)
    
    // Background & Surface
    val Background = Color(0xFF0F0F14)
    val Surface = Color(0xFF1A1A24)
    val SurfaceVariant = Color(0xFF252532)
    val SurfaceElevated = Color(0xFF2A2A3A)
    
    // Sidebar & Navigation
    val SidebarBackground = Color(0xFF12121A)
    val TopBarBackground = Color(0xFF0D0D12)
    val NavigationRail = Color(0xFF151520)
    
    // Text colors
    val OnPrimary = Color.White
    val OnSecondary = Color.Black
    val OnBackground = Color(0xFFE0E0E0)
    val OnSurface = Color(0xFFE0E0E0)
    val OnSurfaceVariant = Color(0xFFB0B0B0)
    
    // State colors
    val SelectedItem = Color(0xFF3949AB)
    val HoverItem = Color(0xFF2A2A4A)
    val DisabledItem = Color(0xFF4A4A5A)
    
    // Status colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFC107)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    
    // Admin-specific accent colors
    val SuperAdmin = Color(0xFFE040FB)  // Purple for super admin
    val Moderator = Color(0xFF00E676)   // Green for moderator
    val Support = Color(0xFF29B6F6)     // Light blue for support
    
    // Chart colors
    val ChartPrimary = Color(0xFF6C63FF)
    val ChartSecondary = Color(0xFF00E5FF)
    val ChartTertiary = Color(0xFFFF6B6B)
    val ChartQuaternary = Color(0xFF4CAF50)
    
    // Composable accessors for admin-aware theming
    @Composable
    fun adminBackground(): Color = Background
    
    @Composable
    fun adminSurface(): Color = Surface
    
    @Composable
    fun adminOnBackground(): Color = OnBackground
    
    @Composable
    fun adminOnSurface(): Color = OnSurface
}

// ========== Admin Color Scheme ==========

private val AdminDarkColorScheme = darkColorScheme(
    primary = AdminColors.Primary,
    onPrimary = AdminColors.OnPrimary,
    primaryContainer = AdminColors.PrimaryVariant,
    onPrimaryContainer = AdminColors.OnPrimary,
    secondary = AdminColors.Secondary,
    onSecondary = AdminColors.OnSecondary,
    secondaryContainer = AdminColors.SecondaryVariant,
    onSecondaryContainer = AdminColors.OnSecondary,
    tertiary = AdminColors.SuperAdmin,
    onTertiary = Color.White,
    background = AdminColors.Background,
    onBackground = AdminColors.OnBackground,
    surface = AdminColors.Surface,
    onSurface = AdminColors.OnSurface,
    surfaceVariant = AdminColors.SurfaceVariant,
    onSurfaceVariant = AdminColors.OnSurfaceVariant,
    error = AdminColors.Error,
    onError = Color.White,
    outline = Color(0xFF444454)
)

// Light theme variant for admin (if needed)
private val AdminLightColorScheme = lightColorScheme(
    primary = AdminColors.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1C4E9),
    onPrimaryContainer = Color(0xFF1A237E),
    secondary = AdminColors.Secondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF00838F),
    background = Color(0xFFF5F5F7),
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEEEEF2),
    onSurfaceVariant = Color(0xFF5A5A6A),
    error = AdminColors.Error,
    onError = Color.White,
    outline = Color(0xFFCCCCDD)
)

// ========== Admin Theme Composable ==========

/**
 * Admin Portal theme wrapper.
 * 
 * Always uses dark theme for admin portal to maintain
 * visual distinction from regular user mode.
 * 
 * @param useDarkTheme Force dark theme (default: true for admin)
 * @param content The composable content to theme
 */
@Composable
fun AdminTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) {
        AdminDarkColorScheme
    } else {
        AdminLightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AdminTypography,
        shapes = AdminShapes,
        content = content
    )
}

// ========== Admin Typography ==========

private val AdminTypography = Typography(
    // Use default Material3 typography with potential customizations
    // Can be extended with custom fonts if needed
)

// ========== Admin Shapes ==========

private val AdminShapes = Shapes(
    // Use default Material3 shapes
    // Can be customized for admin-specific rounded corners
)

// ========== Extension Functions ==========

/**
 * Returns the appropriate status color for a given status string.
 */
fun getStatusColor(status: String): Color = when (status.lowercase()) {
    "active", "approved", "verified", "completed", "healthy" -> AdminColors.Success
    "pending", "processing", "syncing", "in_progress" -> AdminColors.Warning
    "rejected", "suspended", "error", "failed", "flagged" -> AdminColors.Error
    "inactive", "cancelled", "closed" -> AdminColors.DisabledItem
    else -> AdminColors.Info
}

/**
 * Returns the admin role color based on role type.
 */
fun getAdminRoleColor(role: String): Color = when (role.lowercase()) {
    "super_admin", "superadmin" -> AdminColors.SuperAdmin
    "moderator", "mod" -> AdminColors.Moderator
    "support" -> AdminColors.Support
    else -> AdminColors.Primary
}
