package com.akhil.aimaze.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGameScheme = darkColorScheme(
    primary = GameOrange,
    onPrimary = GameBackground,
    secondary = GameBlue,
    tertiary = GameGreen,
    background = GameBackground,
    surface = GameSurface,
    surfaceVariant = GameSurfaceRaised,
    onBackground = GameText,
    onSurface = GameText,
    onSurfaceVariant = GameTextMuted,
)

private val LightGameScheme = lightColorScheme(
    primary = Color(0xFFD86100),
    secondary = Color(0xFF315BC8),
    tertiary = Color(0xFF168A4C),
    background = GameLightBackground,
    surface = GameLightSurface,
    surfaceVariant = Color(0xFFE8ECF3),
    onBackground = GameLightText,
    onSurface = GameLightText,
    onSurfaceVariant = Color(0xFF5F6775),
)

@Composable
fun AIMazeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkGameScheme else LightGameScheme,
        typography = Typography,
        content = content,
    )
}
