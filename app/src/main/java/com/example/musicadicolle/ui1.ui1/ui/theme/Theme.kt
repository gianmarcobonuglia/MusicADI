package com.example.musicadicolle.ui1.ui1.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.musicadicolle.ui1.ui1.ui.theme.backgroundDark
import com.example.musicadicolle.ui1.ui1.ui.theme.backgroundDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.backgroundDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.backgroundLight
import com.example.musicadicolle.ui1.ui1.ui.theme.backgroundLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.backgroundLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.errorContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.errorContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorDark
import com.example.musicadicolle.ui1.ui1.ui.theme.errorDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorLight
import com.example.musicadicolle.ui1.ui1.ui.theme.errorLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.errorLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseOnSurfaceDark
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseOnSurfaceDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseOnSurfaceDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseOnSurfaceLight
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseOnSurfaceLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseOnSurfaceLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inversePrimaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.inversePrimaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inversePrimaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inversePrimaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.inversePrimaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inversePrimaryLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseSurfaceDark
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseSurfaceDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseSurfaceDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseSurfaceLight
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseSurfaceLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.inverseSurfaceLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onBackgroundDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onBackgroundDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onBackgroundDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onBackgroundLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onBackgroundLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onBackgroundLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onErrorLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onPrimaryLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSecondaryLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceVariantDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceVariantDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceVariantDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceVariantLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceVariantLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onSurfaceVariantLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.onTertiaryLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineDark
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineLight
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineVariantDark
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineVariantDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineVariantDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineVariantLight
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineVariantLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.outlineVariantLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.primaryLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.scrimDark
import com.example.musicadicolle.ui1.ui1.ui.theme.scrimDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.scrimDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.scrimLight
import com.example.musicadicolle.ui1.ui1.ui.theme.scrimLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.scrimLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.secondaryLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceBrightDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceBrightDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceBrightDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceBrightLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceBrightLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceBrightLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighestDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighestDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighestDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighestLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighestLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerHighestLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowestDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowestDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowestDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowestLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowestLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceContainerLowestLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDimDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDimDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDimDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDimLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDimLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceDimLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceVariantDark
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceVariantDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceVariantDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceVariantLight
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceVariantLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.surfaceVariantLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryContainerDark
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryContainerDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryContainerDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryContainerLight
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryContainerLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryContainerLightMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryDark
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryDarkHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryDarkMediumContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryLight
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryLightHighContrast
import com.example.musicadicolle.ui1.ui1.ui.theme.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

