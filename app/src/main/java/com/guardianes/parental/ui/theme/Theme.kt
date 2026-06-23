package com.guardianes.parental.ui.theme

import android.app.Activity
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

// Paleta de marca: verde "guardián" + acentos cálidos.
private val Green = Color(0xFF1B873F)
private val GreenDark = Color(0xFF0E5C28)
private val Amber = Color(0xFFFFB300)
private val Coral = Color(0xFFE5484D)

private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB6F0C4),
    secondary = Amber,
    tertiary = Color(0xFF3D5AFE),
    error = Coral,
    background = Color(0xFFF6FBF7),
    surface = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7BE0A0),
    onPrimary = Color(0xFF003919),
    primaryContainer = GreenDark,
    secondary = Amber,
    tertiary = Color(0xFF9DA9FF),
    error = Color(0xFFFFB4AB),
    background = Color(0xFF101713),
    surface = Color(0xFF18211B),
)

@Composable
fun GuardianesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = GuardianesTypography,
        content = content,
    )
}
