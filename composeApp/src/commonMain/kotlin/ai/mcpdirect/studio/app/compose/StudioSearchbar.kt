package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource

@Composable
fun StudioSearchbar(
    value:String="",
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
){
    var value by remember { mutableStateOf(value) }
    Box(modifier = modifier) {
        Row(
            Modifier.background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                ButtonDefaults.shape
            ).clip(ButtonDefaults.shape),
            verticalAlignment = Alignment.CenterVertically,
        ){

            Icon(painterResource(Res.drawable.search), contentDescription = "Search",
                Modifier.padding(12.dp))
            BasicTextField(
                modifier=Modifier.weight(1f).padding(end = 4.dp),
                value = value,
                onValueChange = {
                    value = it
                    onValueChange(value)
                },
            )
            if(value.isNotEmpty()) IconButton(onClick = {
                value = ""
                onValueChange(value)
            }){
                Icon(painterResource(Res.drawable.close), contentDescription = "Clear")
            }
        }
    }
}