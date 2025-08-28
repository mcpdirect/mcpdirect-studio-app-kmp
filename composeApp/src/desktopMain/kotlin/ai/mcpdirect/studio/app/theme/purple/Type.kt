package ai.mcpdirect.studio.app.theme.purple

import androidx.compose.material3.Typography

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font



// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontWeight = FontWeight(300)),
    displayMedium = baseline.displayMedium.copy(fontWeight = FontWeight(300)),
    displaySmall = baseline.displaySmall.copy(fontWeight = FontWeight(300)),
    headlineLarge = baseline.headlineLarge.copy(fontWeight = FontWeight(300)),
    headlineMedium = baseline.headlineMedium.copy(fontWeight = FontWeight(300)),
    headlineSmall = baseline.headlineSmall.copy(fontWeight = FontWeight(300)),
    titleLarge = baseline.titleLarge.copy(fontWeight = FontWeight(300)),
    titleMedium = baseline.titleMedium.copy(fontWeight = FontWeight(300)),
    titleSmall = baseline.titleSmall.copy(fontWeight = FontWeight(300)),
    bodyLarge = baseline.bodyLarge.copy(fontWeight = FontWeight(200)),
    bodyMedium = baseline.bodyMedium.copy(fontWeight = FontWeight(200)),
    bodySmall = baseline.bodySmall.copy(fontWeight = FontWeight(200)),
    labelLarge = baseline.labelLarge.copy(fontWeight = FontWeight(200)),
    labelMedium = baseline.labelMedium.copy(fontWeight = FontWeight(200)),
    labelSmall = baseline.labelSmall.copy(fontWeight = FontWeight(200)),
)

