package com.rio.rostry.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = BrandOnPrimaryLight,
    secondary = BrandSecondaryLight,
    onSecondary = BrandOnSecondaryLight,
    tertiary = BrandTertiaryLight,
    onTertiary = BrandOnTertiaryLight,

    background = Surface2Light,
    onBackground = Neutral90,
    surface = Surface1Light,
    onSurface = Neutral90,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Neutral80,

    error = Error,
    onError = OnError,
    outline = OutlineLight,
    outlineVariant = Neutral30,

    scrim = Color(0x80000000)
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDark,
    onPrimary = BrandOnPrimaryDark,
    secondary = BrandSecondaryDark,
    onSecondary = BrandOnSecondaryDark,
    tertiary = BrandTertiaryDark,
    onTertiary = BrandOnTertiaryDark,

    background = Surface2Dark,
    onBackground = Neutral20,
    surface = Surface1Dark,
    onSurface = Neutral20,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Neutral50,

    error = Error,
    onError = OnError,
    outline = OutlineDark,
    outlineVariant = Color(0xFF2A3230),

    scrim = Color(0x80FFFFFF)
)

@Composable
fun ROSTRYTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = {
            CompositionLocalProvider(LocalSpacing provides Spacing()) {
                content()
            }
        }
    )
}