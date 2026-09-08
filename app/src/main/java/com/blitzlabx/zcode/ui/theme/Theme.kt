package com.blitzlabx.zcode.ui.theme

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

// Premium dark blue / glow aesthetic matching the concept
val ZBlue = Color(0xFF00B4FF)
val ZBlueDark = Color(0xFF0077CC)
val ZBlueGlow = Color(0xFF00D4FF)
val ZBackground = Color(0xFF0A0E1A)
val ZSurface = Color(0xFF12182B)
val ZSurfaceVariant = Color(0xFF1A2238)
val ZOnBackground = Color(0xFFE8F0FF)
val ZOnSurface = Color(0xFFD0DCF0)
val ZAccent = Color(0xFF00E5FF)
val ZSuccess = Color(0xFF00E676)
val ZError = Color(0xFFFF5252)
val ZWarning = Color(0xFFFFAB40)

private val DarkColorScheme = darkColorScheme(
    primary = ZBlue,
    onPrimary = Color.Black,
    primaryContainer = ZBlueDark,
    onPrimaryContainer = ZOnBackground,
    secondary = ZAccent,
    onSecondary = Color.Black,
    tertiary = ZBlueGlow,
    background = ZBackground,
    onBackground = ZOnBackground,
    surface = ZSurface,
    onSurface = ZOnSurface,
    surfaceVariant = ZSurfaceVariant,
    onSurfaceVariant = ZOnSurface,
    error = ZError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ZBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB3E5FC),
    onPrimaryContainer = Color(0xFF001F2A),
    secondary = Color(0xFF0277BD),
    background = Color(0xFFF5F9FF),
    onBackground = Color(0xFF0A0E1A),
    surface = Color.White,
    onSurface = Color(0xFF0A0E1A),
    surfaceVariant = Color(0xFFE3F2FD),
    error = ZError
)

@Composable
fun ZCodeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
