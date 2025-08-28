package ai.mcpdirect.studio.app.theme.purple
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


private val lightScheme = lightColorScheme(
    primary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryLight,
    onPrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryLight,
    primaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryContainerLight,
    onPrimaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryContainerLight,
    secondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryLight,
    onSecondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryLight,
    secondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryContainerLight,
    onSecondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryContainerLight,
    tertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryLight,
    onTertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryLight,
    tertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryContainerLight,
    onTertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryContainerLight,
    error = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorLight,
    onError = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorLight,
    errorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorContainerLight,
    onErrorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorContainerLight,
    background = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.backgroundLight,
    onBackground = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onBackgroundLight,
    surface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceLight,
    onSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceLight,
    surfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceVariantLight,
    onSurfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceVariantLight,
    outline = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineLight,
    outlineVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineVariantLight,
    scrim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.scrimLight,
    inverseSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseSurfaceLight,
    inverseOnSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseOnSurfaceLight,
    inversePrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inversePrimaryLight,
    surfaceDim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceDimLight,
    surfaceBright = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceBrightLight,
    surfaceContainerLowest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowestLight,
    surfaceContainerLow = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowLight,
    surfaceContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLight,
    surfaceContainerHigh = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighLight,
    surfaceContainerHighest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryDark,
    onPrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryDark,
    primaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryContainerDark,
    onPrimaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryContainerDark,
    secondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryDark,
    onSecondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryDark,
    secondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryContainerDark,
    onSecondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryContainerDark,
    tertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryDark,
    onTertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryDark,
    tertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryContainerDark,
    onTertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryContainerDark,
    error = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorDark,
    onError = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorDark,
    errorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorContainerDark,
    onErrorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorContainerDark,
    background = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.backgroundDark,
    onBackground = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onBackgroundDark,
    surface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceDark,
    onSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceDark,
    surfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceVariantDark,
    onSurfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceVariantDark,
    outline = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineDark,
    outlineVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineVariantDark,
    scrim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.scrimDark,
    inverseSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseSurfaceDark,
    inverseOnSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseOnSurfaceDark,
    inversePrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inversePrimaryDark,
    surfaceDim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceDimDark,
    surfaceBright = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceBrightDark,
    surfaceContainerLowest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowestDark,
    surfaceContainerLow = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowDark,
    surfaceContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerDark,
    surfaceContainerHigh = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighDark,
    surfaceContainerHighest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryLightMediumContrast,
    onPrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryLightMediumContrast,
    primaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryContainerLightMediumContrast,
    onPrimaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryContainerLightMediumContrast,
    secondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryLightMediumContrast,
    onSecondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryLightMediumContrast,
    secondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryContainerLightMediumContrast,
    onSecondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryContainerLightMediumContrast,
    tertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryLightMediumContrast,
    onTertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryLightMediumContrast,
    tertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryContainerLightMediumContrast,
    onTertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryContainerLightMediumContrast,
    error = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorLightMediumContrast,
    onError = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorLightMediumContrast,
    errorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorContainerLightMediumContrast,
    onErrorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorContainerLightMediumContrast,
    background = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.backgroundLightMediumContrast,
    onBackground = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onBackgroundLightMediumContrast,
    surface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceLightMediumContrast,
    onSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceLightMediumContrast,
    surfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceVariantLightMediumContrast,
    onSurfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceVariantLightMediumContrast,
    outline = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineLightMediumContrast,
    outlineVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineVariantLightMediumContrast,
    scrim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.scrimLightMediumContrast,
    inverseSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseSurfaceLightMediumContrast,
    inverseOnSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseOnSurfaceLightMediumContrast,
    inversePrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inversePrimaryLightMediumContrast,
    surfaceDim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceDimLightMediumContrast,
    surfaceBright = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceBrightLightMediumContrast,
    surfaceContainerLowest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowLightMediumContrast,
    surfaceContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLightMediumContrast,
    surfaceContainerHigh = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryLightHighContrast,
    onPrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryLightHighContrast,
    primaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryContainerLightHighContrast,
    onPrimaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryContainerLightHighContrast,
    secondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryLightHighContrast,
    onSecondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryLightHighContrast,
    secondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryContainerLightHighContrast,
    onSecondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryContainerLightHighContrast,
    tertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryLightHighContrast,
    onTertiary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryLightHighContrast,
    tertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.tertiaryContainerLightHighContrast,
    onTertiaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onTertiaryContainerLightHighContrast,
    error = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorLightHighContrast,
    onError = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorLightHighContrast,
    errorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.errorContainerLightHighContrast,
    onErrorContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onErrorContainerLightHighContrast,
    background = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.backgroundLightHighContrast,
    onBackground = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onBackgroundLightHighContrast,
    surface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceLightHighContrast,
    onSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceLightHighContrast,
    surfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceVariantLightHighContrast,
    onSurfaceVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSurfaceVariantLightHighContrast,
    outline = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineLightHighContrast,
    outlineVariant = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.outlineVariantLightHighContrast,
    scrim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.scrimLightHighContrast,
    inverseSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseSurfaceLightHighContrast,
    inverseOnSurface = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inverseOnSurfaceLightHighContrast,
    inversePrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.inversePrimaryLightHighContrast,
    surfaceDim = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceDimLightHighContrast,
    surfaceBright = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceBrightLightHighContrast,
    surfaceContainerLowest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLowLightHighContrast,
    surfaceContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerLightHighContrast,
    surfaceContainerHigh = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryDarkMediumContrast,
    onPrimary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryDarkMediumContrast,
    primaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.primaryContainerDarkMediumContrast,
    onPrimaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onPrimaryContainerDarkMediumContrast,
    secondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryDarkMediumContrast,
    onSecondary = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryDarkMediumContrast,
    secondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.secondaryContainerDarkMediumContrast,
    onSecondaryContainer = _root_ide_package_.ai.mcpdirect.studio.app.theme.purple.onSecondaryContainerDarkMediumContrast,
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

//val LocalCardColors = compositionLocalOf<CardColors> {
//    error("No CardColors provided!")
//}

@Composable
fun PurpleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
//      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//          val context = LocalContext.current
//          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//      }

      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
     content = content
  )
//  {
//            CompositionLocalProvider(
//            LocalCardColors provides CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.surface,
//                contentColor = MaterialTheme.colorScheme.onSurface,
//                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
//                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
//            ),
//            content = content
//        )
//  }
}

