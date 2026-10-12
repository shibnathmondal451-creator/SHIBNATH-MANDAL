package com.example.ui.theme

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
    primary = PujoGoldLight,
    onPrimary = PujoCrimsonDark,
    primaryContainer = PujoCrimson,
    onPrimaryContainer = PujoGoldLight,
    secondary = PujoGold,
    onSecondary = Color.Black,
    secondaryContainer = PujoGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = PujoPurpleLight,
    onTertiary = Color.White,
    background = Color(0xFF141113),
    onBackground = Color(0xFFF5EFEB),
    surface = Color(0xFF221C20),
    onSurface = Color(0xFFF5EFEB),
    surfaceVariant = Color(0xFF2D252A),
    onSurfaceVariant = Color(0xFFD7CCC8),
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PujoCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DF),
    onPrimaryContainer = PujoCrimsonDark,
    secondary = PujoGold,
    onSecondary = Color(0xFF332000),
    secondaryContainer = Color(0xFFFFF0C2),
    onSecondaryContainer = Color(0xFF4A3400),
    tertiary = PujoPurple,
    onTertiary = Color.White,
    background = PujoIvory,
    onBackground = PujoCharcoal,
    surface = Color.White,
    onSurface = PujoCharcoal,
    surfaceVariant = PujoCream,
    onSurfaceVariant = Color(0xFF524443),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Preserve custom branding colors over generic dynamic color
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
        content = content
    )
}
