package ai.mcpdirect.studio.app.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightedText() {
    val annotatedString = buildAnnotatedString {
        append("Welcome to ")

        // Push a style for the "special" word
        withStyle(style = SpanStyle(
            color = Color(0xFF007AFF),
            fontWeight = FontWeight.Bold
        )
        ) {
            append("Kotlin Multiplatform")
        }

        append(" development!")
    }

    Text(text = annotatedString)
}