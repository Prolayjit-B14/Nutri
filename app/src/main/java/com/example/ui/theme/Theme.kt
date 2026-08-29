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
    primary = ClinicalTealLight,
    onPrimary = ClinicalTealDark,
    primaryContainer = ClinicalTealDark,
    onPrimaryContainer = ClinicalTealLight,
    secondary = MedicalBlueLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = MedicalBlueLight,
    tertiary = NutrientCarbs,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF153335),
    onTertiaryContainer = ClinicalTealLight,
    background = DarkBackground,
    onBackground = Color(0xFFE2EBEB),
    surface = DarkSurface,
    onSurface = Color(0xFFE2EBEB),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = SubtleText,
    outline = Color(0xFF334A4D),
    outlineVariant = DarkNeutralBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ClinicalTealPrimary,
    onPrimary = Color.White,
    primaryContainer = ClinicalTealLight,
    onPrimaryContainer = ClinicalTealPrimary,
    secondary = MedicalBlue,
    onSecondary = Color.White,
    secondaryContainer = MedicalBlueLight,
    onSecondaryContainer = MedicalBlueDark,
    tertiary = NutrientCarbs,
    onTertiary = Color.White,
    tertiaryContainer = NutrientCarbsContainer,
    onTertiaryContainer = ClinicalTealDark,
    background = ClinicalBackground,
    onBackground = PrimaryText,
    surface = ClinicalSurface,
    onSurface = PrimaryText,
    surfaceVariant = ClinicalSurfaceVariant,
    onSurfaceVariant = SecondaryText,
    outline = Color(0xFFBAC7C8),
    outlineVariant = ClinicalBorder,
    error = ClinicalError,
    onError = Color.White,
    errorContainer = ClinicalErrorLight,
    onErrorContainer = ClinicalError
)

@Composable
fun NutriFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to ensure our custom health/nutrition palette is prominent
    content: @Composable () -> Unit
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
