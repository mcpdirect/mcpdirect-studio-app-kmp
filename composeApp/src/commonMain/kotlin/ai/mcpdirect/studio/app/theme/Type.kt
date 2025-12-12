package ai.mcpdirect.studio.app.theme

import androidx.compose.material3.Typography

import androidx.compose.ui.text.font.FontWeight


// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontWeight = FontWeight(100)),
    displayMedium = baseline.displayMedium.copy(fontWeight = FontWeight(100)),
    displaySmall = baseline.displaySmall.copy(fontWeight = FontWeight(100)),
    headlineLarge = baseline.headlineLarge.copy(fontWeight = FontWeight(200)),
    headlineMedium = baseline.headlineMedium.copy(fontWeight = FontWeight(200)),
    headlineSmall = baseline.headlineSmall.copy(fontWeight = FontWeight(200)),
    titleLarge = baseline.titleLarge.copy(fontWeight = FontWeight(300)),
    titleMedium = baseline.titleMedium.copy(fontWeight = FontWeight(300)),
    titleSmall = baseline.titleSmall.copy(fontWeight = FontWeight(300)),
    bodyLarge = baseline.bodyLarge.copy(fontWeight = FontWeight(300)),
    bodyMedium = baseline.bodyMedium.copy(fontWeight = FontWeight(300)),
    bodySmall = baseline.bodySmall.copy(fontWeight = FontWeight(400)),
    labelLarge = baseline.labelLarge.copy(fontWeight = FontWeight(300)),
    labelMedium = baseline.labelMedium.copy(fontWeight = FontWeight(400)),
    labelSmall = baseline.labelSmall.copy(fontWeight = FontWeight(400)),
)

