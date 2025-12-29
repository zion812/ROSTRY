package com.rio.rostry.ui.theme

import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.rio.rostry.domain.model.UserType

/**
 * ROSTRY role-aware theme.
 *
 * @param userRole The active user role. When null/unknown, falls back to General.
 * @param darkTheme Whether dark mode is active (defaults to system).
 * @param dynamicColor Whether to use Android 12+ dynamic color. When enabled, dynamic colors
 *                     take precedence over role palettes to follow system appearance. Disable
 *                     to strictly enforce role palettes across devices.
 */
@Composable
fun ROSTRYTheme(
    userRole: UserType? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val roleColors = rememberRoleColorScheme(userRole, darkTheme)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> roleColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
@VisibleForTesting
fun rememberRoleColorScheme(userRole: UserType?, darkTheme: Boolean) = when (userRole) {
    UserType.FARMER -> if (darkTheme) FarmerDarkColors else FarmerLightColors
    UserType.ENTHUSIAST -> if (darkTheme) EnthusiastDarkColors else EnthusiastLightColors
    UserType.GENERAL, UserType.ADMIN, null -> if (darkTheme) GeneralDarkColors else GeneralLightColors
}