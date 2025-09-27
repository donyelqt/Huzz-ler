package com.example.huzzler.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = ColorWhite,
    primaryContainer = RedDark,
    onPrimaryContainer = ColorWhite,
    secondary = NeutralGray,
    onSecondary = ColorWhite,
    secondaryContainer = NeutralLight,
    onSecondaryContainer = ColorBlack,
    tertiary = YellowAccent,
    onTertiary = ColorBlack,
    tertiaryContainer = YellowAccent.copy(alpha = 0.3f),
    onTertiaryContainer = ColorBlack,
    background = ColorWhite,
    onBackground = ColorBlack,
    surface = ColorWhite,
    onSurface = ColorBlack,
    surfaceVariant = NeutralLight,
    onSurfaceVariant = NeutralGray,
    outline = NeutralGray,
    scrim = ColorBlack
)

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = ColorWhite,
    primaryContainer = RedDark,
    onPrimaryContainer = ColorWhite,
    secondary = NeutralGray,
    onSecondary = ColorWhite,
    secondaryContainer = NeutralGray.copy(alpha = 0.3f),
    onSecondaryContainer = ColorWhite,
    tertiary = YellowAccent,
    onTertiary = ColorBlack,
    tertiaryContainer = YellowAccent.copy(alpha = 0.3f),
    onTertiaryContainer = ColorWhite,
    background = ColorBlack,
    onBackground = ColorWhite,
    surface = NeutralDarkSurface,
    onSurface = ColorWhite,
    surfaceVariant = NeutralGray.copy(alpha = 0.3f),
    onSurfaceVariant = ColorWhite.copy(alpha = 0.7f),
    outline = NeutralGray,
    scrim = ColorBlack
)

@Composable
fun HuzzlerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
