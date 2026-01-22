package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary, // Classic macOS/iOS Blue
    style: TextStyle = LocalTextStyle.current
) {
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = text,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Removes the gray ripple box
                onClick = onClick
            ),
//            .padding(vertical = 4.dp),
        style = style.copy(
            color = color,
            textDecoration = TextDecoration.Underline
        ),
    )
}