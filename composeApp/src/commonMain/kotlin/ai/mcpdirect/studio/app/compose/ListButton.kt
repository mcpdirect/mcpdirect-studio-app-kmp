package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ListButton(
    selected:Boolean=false,
    headlineContent: @Composable (() -> Unit),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
){
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Row(
        modifier.padding(8.dp)
            .hoverable(interactionSource),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
//        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.invoke()
        var shape = CardDefaults.outlinedShape
        var colors = CardDefaults.outlinedCardColors()
        var elevation = CardDefaults.outlinedCardElevation()
        var border: BorderStroke? = null
        if(selected){
            shape = CardDefaults.shape
            colors = CardDefaults.cardColors()
            elevation = CardDefaults.cardElevation()
        }else if(isHovered) {
            border = CardDefaults.outlinedCardBorder()
        }
        OutlinedCard {  }
        Card(
            modifier = Modifier.weight(1f).clip(shape).clickable(true,onClick=onClick),
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        ) {
            Column(
                Modifier.padding(horizontal = 12.dp,vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                overlineContent?.invoke()
                headlineContent.invoke()
                supportingContent?.invoke()
            }
        }
        trailingContent?.invoke()
//            if(selected){
//                Column(
//                    Modifier.weight(1f)
//                        .border(
//                            CardDefaults.outlinedCardBorder(),
//                            CardDefaults.outlinedShape
//                        )
//                        .clip(ButtonDefaults.shape)
//                        .padding(horizontal = 12.dp,vertical = 4.dp)
//                ) {
//                    headlineContent.invoke()
//                    supportingContent?.invoke()
//                }
//                trailingContent?.invoke()
//            }else if(isHovered||selected) {
//                OutlinedButton(onClick, Modifier.weight(1f)) {
//                    headlineContent.invoke()
//                }
//                trailingContent?.invoke()
//            }else Column {
//                headlineContent.invoke()
//                supportingContent?.invoke()
//            }
    }
}