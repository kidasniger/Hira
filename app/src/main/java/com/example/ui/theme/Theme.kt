package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HiraDarkColorScheme = darkColorScheme(
    primary = HiraRoyalBlue,
    onPrimary = HiraWhite,
    primaryContainer = HiraRoyalBlueDark,
    onPrimaryContainer = HiraWhite,
    secondary = HiraRoyalBlueLight,
    onSecondary = HiraRoyalBlue,
    background = HiraBlack,
    surface = HiraBlack,
    onBackground = HiraWhite,
    onSurface = HiraWhite
)

private val HiraLightColorScheme = lightColorScheme(
    primary = HiraRoyalBlue,
    onPrimary = HiraWhite,
    primaryContainer = HiraRoyalBlueLight,
    onPrimaryContainer = HiraRoyalBlueDark,
    secondary = HiraRoyalBlueLight,
    onSecondary = HiraRoyalBlue,
    background = HiraWhite,
    surface = HiraWhite,
    onBackground = HiraBlack,
    onSurface = HiraBlack
)

@Composable
fun HiraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HiraDarkColorScheme else HiraLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Ensure edge to edge styling
                WindowCompat.getInsetsController(window, view).apply {
                    // For splash and general dark blue, status bar text is white (dark appearance)
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for compatibility if needed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    HiraTheme(darkTheme = darkTheme, content = content)
}
