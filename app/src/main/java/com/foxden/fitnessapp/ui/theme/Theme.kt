package com.foxden.fitnessapp.ui.theme

import android.app.Activity
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

// TODO: Fill out more values so that material components look right
private val LightColorScheme = lightColorScheme(
    primary = LightBlue, onPrimary = DarkBlue,
    primaryContainer = Color.White, onPrimaryContainer = DarkBlue,
    secondaryContainer = MidBlue, onSecondaryContainer = Color.White,
    tertiary = MidBlue, onTertiary = Yellow, error = Orange
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlue, onPrimary = Color.White,
    primaryContainer = MidBlue, onPrimaryContainer = Color.White,
    secondaryContainer = MidBlue, onSecondaryContainer = Color.White,
    tertiary = LightBlue, onTertiary = Color.Green, error = Color.Magenta
)

@Composable
fun FitnessAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
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