package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.theme.purple.AppTypography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Typography
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.window.ComposeViewport
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.noto_sans_sc_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    ComposeViewport{
        val notoSansSCFont by preloadFont(Res.font.noto_sans_sc_regular)
        var typography by remember { mutableStateOf<Typography?>(null) }
        LaunchedEffect(notoSansSCFont) {
            notoSansSCFont?.let {
                typography = Typography(
                    displayLarge = AppTypography.displayLarge.copy(fontFamily = it.toFontFamily()),
                    displayMedium = AppTypography.displayMedium.copy(fontFamily = it.toFontFamily()),
                    displaySmall = AppTypography.displaySmall.copy(fontFamily = it.toFontFamily()),
                    headlineLarge = AppTypography.headlineLarge.copy(fontFamily = it.toFontFamily()),
                    headlineMedium = AppTypography.headlineMedium.copy(fontFamily = it.toFontFamily()),
                    headlineSmall = AppTypography.headlineSmall.copy(fontFamily = it.toFontFamily()),
                    titleLarge = AppTypography.titleLarge.copy(fontFamily = it.toFontFamily()),
                    titleMedium = AppTypography.titleMedium.copy(fontFamily = it.toFontFamily()),
                    titleSmall = AppTypography.titleSmall.copy(fontFamily = it.toFontFamily()),
                    bodyLarge = AppTypography.bodyLarge.copy(fontFamily = it.toFontFamily()),
                    bodyMedium = AppTypography.bodyMedium.copy(fontFamily = it.toFontFamily()),
                    bodySmall = AppTypography.bodySmall.copy(fontFamily = it.toFontFamily()),
                    labelLarge = AppTypography.labelLarge.copy(fontFamily = it.toFontFamily()),
                    labelMedium = AppTypography.labelMedium.copy(fontFamily = it.toFontFamily()),
                    labelSmall = AppTypography.labelSmall.copy(fontFamily = it.toFontFamily()),
                )
            }
        }
        if(typography==null){
            Column(Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        }else App(typography=typography)
    }
}