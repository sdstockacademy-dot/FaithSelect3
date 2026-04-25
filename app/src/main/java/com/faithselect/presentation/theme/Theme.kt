package com.faithselect.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Brand Colors ─────────────────────────────────────────────────────────────
// Spiritual palette: Deep navy, warm gold, soft cream

object FaithColors {
    // Primary — Deep Navy Blue
    val NavyDeep       = Color(0xFF0D1B2A)
    val NavyMid        = Color(0xFF1B2F4A)
    val NavyLight      = Color(0xFF2A4464)

    // Accent — Sacred Gold
    val GoldDeep       = Color(0xFFB8860B)
    val GoldPrimary    = Color(0xFFD4AF37)
    val GoldLight      = Color(0xFFF0D060)
    val GoldSoft       = Color(0xFFFFF8E1)

    // Neutral
    val Cream          = Color(0xFFFAF6EE)
    val CreamDark      = Color(0xFFF0E8D5)
    val Parchment      = Color(0xFFE8DCC8)

    // Text
    val TextDark       = Color(0xFF1A1A2E)
    val TextMid        = Color(0xFF4A4A6A)
    val TextLight      = Color(0xFFE8E0CC)

    // Status
    val Success        = Color(0xFF2E7D32)
    val Error          = Color(0xFFC62828)
    val Surface        = Color(0xFFFFFFFF)

    // Dark theme surfaces
    val DarkSurface    = Color(0xFF0F1923)
    val DarkCard       = Color(0xFF162030)
    val DarkElevated   = Color(0xFF1E2D42)
}

// ─── Light Color Scheme ───────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = FaithColors.NavyDeep,
    onPrimary          = Color.White,
    primaryContainer   = FaithColors.NavyLight,
    onPrimaryContainer = FaithColors.GoldLight,

    secondary          = FaithColors.GoldPrimary,
    onSecondary        = FaithColors.NavyDeep,
    secondaryContainer = FaithColors.GoldSoft,
    onSecondaryContainer = FaithColors.NavyDeep,

    tertiary           = FaithColors.GoldDeep,
    onTertiary         = Color.White,

    background         = FaithColors.Cream,
    onBackground       = FaithColors.TextDark,

    surface            = FaithColors.Surface,
    onSurface          = FaithColors.TextDark,
    surfaceVariant     = FaithColors.CreamDark,
    onSurfaceVariant   = FaithColors.TextMid,

    outline            = FaithColors.Parchment,
    outlineVariant     = FaithColors.GoldSoft,

    error              = FaithColors.Error,
    onError            = Color.White
)

// ─── Dark Color Scheme ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = FaithColors.GoldPrimary,
    onPrimary          = FaithColors.NavyDeep,
    primaryContainer   = FaithColors.NavyMid,
    onPrimaryContainer = FaithColors.GoldLight,

    secondary          = FaithColors.GoldLight,
    onSecondary        = FaithColors.NavyDeep,
    secondaryContainer = FaithColors.NavyLight,
    onSecondaryContainer = FaithColors.GoldSoft,

    tertiary           = FaithColors.GoldDeep,
    onTertiary         = FaithColors.NavyDeep,

    background         = FaithColors.DarkSurface,
    onBackground       = FaithColors.TextLight,

    surface            = FaithColors.DarkCard,
    onSurface          = FaithColors.TextLight,
    surfaceVariant     = FaithColors.DarkElevated,
    onSurfaceVariant   = Color(0xFFAAAFC0),

    outline            = FaithColors.NavyLight,
    outlineVariant     = FaithColors.NavyMid,

    error              = Color(0xFFEF9A9A),
    onError            = FaithColors.Error
)

// ─── Typography ──────────────────────────────────────────────────────────────
// Using system fonts as fallback — in production add custom .ttf to res/font/
val FaithTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ─── Main Theme Composable ────────────────────────────────────────────────────
@Composable
fun FaithSelectTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FaithTypography,
        content = content
    )
}
