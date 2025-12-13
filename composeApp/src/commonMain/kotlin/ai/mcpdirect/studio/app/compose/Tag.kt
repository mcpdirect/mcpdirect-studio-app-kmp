package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.check
import org.jetbrains.compose.resources.painterResource

@Composable
fun Tag(
    text:String,
    toggle:Boolean=false,
    toggleColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onToggleChanged:((Boolean)->Unit)?=null,
){
    val backgroundColor = if(onToggleChanged != null&&toggle) toggleColor else MaterialTheme.colorScheme.surfaceContainer
    val contentColor = contentColorFor(backgroundColor)
    Row(
        Modifier.background(
                backgroundColor,
                ButtonDefaults.shape
            )
//            .border(
//                width = 1.dp,
//                color = contentColor,
//                shape = ButtonDefaults.shape
//            )
            .clip(ButtonDefaults.shape)
            .clickable(onToggleChanged!=null) {
                onToggleChanged?.invoke(!toggle)
            }
            .padding(horizontal = 8.dp,vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor,
        )
        if(onToggleChanged != null&&toggle) Icon(
            painterResource(Res.drawable.check),
            contentDescription = null,
            Modifier.size(16.dp),
            contentColor
        )
    }
}