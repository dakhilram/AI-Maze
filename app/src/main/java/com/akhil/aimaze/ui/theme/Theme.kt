package com.akhil.aimaze.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GameColorScheme = darkColorScheme(
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

@Composable
fun AIMazeTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content,
    )
}
