package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AetherDarkColorScheme = darkColorScheme(
    primary = PrimaryViolet,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryVioletVariant,
    onPrimaryContainer = TextPrimary,
    secondary = SecondaryCyan,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceHighlight,
    onSecondaryContainer = SecondaryCyan,
    tertiary = AccentCoral,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AetherDarkColorScheme,
        typography = Typography,
        content = content
    )
}
