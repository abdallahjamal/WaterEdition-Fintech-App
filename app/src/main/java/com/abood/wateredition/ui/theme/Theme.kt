package com.abood.wateredition.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val WaterDarkColorScheme = darkColorScheme(
    primary          = CyanPrimary,
    onPrimary        = NavyBackground,
    primaryContainer = CyanDark,
    onPrimaryContainer = TextPrimary,

    secondary        = FillBlue,
    onSecondary      = TextPrimary,

    tertiary         = PaymentGold,
    onTertiary       = NavyBackground,

    error            = DebtRed,
    onError          = TextPrimary,
    errorContainer   = DebtRedMuted,

    background       = NavyBackground,
    onBackground     = TextPrimary,

    surface          = NavySurface,
    onSurface        = TextPrimary,
    surfaceVariant   = NavyCard,
    onSurfaceVariant = TextSecondary,

    outline          = NavyCardBorder,
    outlineVariant   = DividerColor
)

@Composable
fun WaterEditionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always dark (water distribution apps run 24/7, dark is preferred)
    MaterialTheme(
        colorScheme = WaterDarkColorScheme,
        typography  = WaterTypography,
        content     = content
    )
}