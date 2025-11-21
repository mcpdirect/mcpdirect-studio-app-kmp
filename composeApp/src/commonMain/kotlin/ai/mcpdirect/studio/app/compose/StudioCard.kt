package ai.mcpdirect.studio.app.compose
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val studioCardColors: CardColors
    @Composable
    get() =  CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    )
val studioCardElevation : CardElevation
    @Composable
    get() = CardDefaults.cardElevation(
        defaultElevation = 1.dp,
        pressedElevation = 2.dp,
        disabledElevation = 0.dp
    )

val studioCardBorderStroke: BorderStroke
    @Composable
    get() = BorderStroke(
        width = 1.dp,
        color =  MaterialTheme.colorScheme.outline.copy(0.2f)
    )

//val stduioCardShap:

@Composable
fun StudioCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    content: @Composable () -> Unit,
) {
    OutlinedCard (
        modifier=modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        content()
    }
}