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
    primary = MangasiTealLight,
    onPrimary = MangasiDarkNavy,
    primaryContainer = MangasiTealDark,
    onPrimaryContainer = MangasiTealContainer,
    secondary = MangasiBlueContainer,
    onSecondary = MangasiDarkNavy,
    tertiary = MangasiEmeraldContainer,
    background = MangasiDarkNavy,
    surface = MangasiDarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = MangasiDarkCard,
    onSurfaceVariant = Color(0xFFC7D2D2)
)

private val LightColorScheme = lightColorScheme(
    primary = MangasiTealPrimary,
    onPrimary = Color.White,
    primaryContainer = MangasiTealContainer,
    onPrimaryContainer = MangasiOnTealContainer,
    secondary = MangasiBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = MangasiBlueContainer,
    onSecondaryContainer = MangasiOnBlueContainer,
    tertiary = MangasiEmeraldTertiary,
    background = CleanBackgroundLight,
    surface = CleanSurfaceLight,
    onBackground = Color(0xFF131D1E),
    onSurface = Color(0xFF131D1E),
    surfaceVariant = CleanSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3E4D4E),
    outline = CleanOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional Mangasi Brand palette
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
