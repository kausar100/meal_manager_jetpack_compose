package com.kausar.messmanagementapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = gk_theme_dark_primary,
    onPrimary = gk_theme_dark_onPrimary,
    primaryContainer = gk_theme_dark_primaryContainer,
    onPrimaryContainer = gk_theme_dark_onPrimaryContainer,
    secondary = gk_theme_dark_secondary,
    onSecondary = gk_theme_dark_onSecondary,
    secondaryContainer = gk_theme_dark_secondaryContainer,
    onSecondaryContainer = gk_theme_dark_onSecondaryContainer,
    tertiary = gk_theme_dark_tertiary,
    onTertiary = gk_theme_dark_onTertiary,
    tertiaryContainer = gk_theme_dark_tertiaryContainer,
    onTertiaryContainer = gk_theme_dark_onTertiaryContainer,
    error = gk_theme_dark_error,
    errorContainer = gk_theme_dark_errorContainer,
    onError = gk_theme_dark_onError,
    onErrorContainer = gk_theme_dark_onErrorContainer,
    background = gk_theme_dark_background,
    onBackground = gk_theme_dark_onBackground,
    surface = gk_theme_dark_surface,
    onSurface = gk_theme_dark_onSurface,
    surfaceVariant = gk_theme_dark_surfaceVariant,
    onSurfaceVariant = gk_theme_dark_onSurfaceVariant,
    outline = gk_theme_dark_outline,
    inverseOnSurface = gk_theme_dark_inverseOnSurface,
    inverseSurface = gk_theme_dark_inverseSurface,
    inversePrimary = gk_theme_dark_inversePrimary,
    surfaceTint = gk_theme_dark_surfaceTint,
    outlineVariant = gk_theme_dark_outlineVariant,
    scrim = gk_theme_dark_scrim,
)

private val LightColorScheme = lightColorScheme(
    primary = gk_theme_light_primary,
    onPrimary = gk_theme_light_onPrimary,
    primaryContainer = gk_theme_light_primaryContainer,
    onPrimaryContainer = gk_theme_light_onPrimaryContainer,
    secondary = gk_theme_light_secondary,
    onSecondary = gk_theme_light_onSecondary,
    secondaryContainer = gk_theme_light_secondaryContainer,
    onSecondaryContainer = gk_theme_light_onSecondaryContainer,
    tertiary = gk_theme_light_tertiary,
    onTertiary = gk_theme_light_onTertiary,
    tertiaryContainer = gk_theme_light_tertiaryContainer,
    onTertiaryContainer = gk_theme_light_onTertiaryContainer,
    error = gk_theme_light_error,
    errorContainer = gk_theme_light_errorContainer,
    onError = gk_theme_light_onError,
    onErrorContainer = gk_theme_light_onErrorContainer,
    background = gk_theme_light_background,
    onBackground = gk_theme_light_onBackground,
    surface = gk_theme_light_surface,
    onSurface = gk_theme_light_onSurface,
    surfaceVariant = gk_theme_light_surfaceVariant,
    onSurfaceVariant = gk_theme_light_onSurfaceVariant,
    outline = gk_theme_light_outline,
    inverseOnSurface = gk_theme_light_inverseOnSurface,
    inverseSurface = gk_theme_light_inverseSurface,
    inversePrimary = gk_theme_light_inversePrimary,
    surfaceTint = gk_theme_light_surfaceTint,
    outlineVariant = gk_theme_light_outlineVariant,
    scrim = gk_theme_light_scrim,
)

@Composable
fun MessManagementAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}