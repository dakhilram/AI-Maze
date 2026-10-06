package com.akhil.aimaze.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MazeTealLight,
    secondary = MazeSlateLight,
    tertiary = MazeIndigoLight,
    background = MazeBackgroundDark,
    surface = MazeSurfaceDark,
    onBackground = Color(0xFFE0E6E6),
    onSurface = Color(0xFFE0E6E6),
)

private val LightColorScheme = lightColorScheme(
    primary = MazeTealDark,
    secondary = MazeSlateDark,
    tertiary = MazeIndigoDark,
    background = MazeBackgroundLight,
    surface = MazeSurfaceLight,
    onBackground = Color(0xFF161D1D),
    onSurface = Color(0xFF161D1D),
)

@Composable
fun AIMazeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
