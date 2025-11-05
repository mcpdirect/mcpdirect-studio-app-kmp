package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tag(
    text:String,
    color: Color = MaterialTheme.colorScheme.primary

){
    Box(
        Modifier.border(
            width = 1.dp,
            color = color,
            shape = RoundedCornerShape(8.dp)
        )
    ){
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp)
        )
    }
}