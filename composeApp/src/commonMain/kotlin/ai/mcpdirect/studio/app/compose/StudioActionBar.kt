package ai.mcpdirect.studio.app.compose

import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudioActionBar(
    title: String?=null,
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {},
){
    Row(Modifier.fillMaxWidth()
//        .padding().padding(horizontal = 8.dp)
        .background(color = MaterialTheme.colorScheme.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically) {
        navigationIcon()
        title?.let {
            Text(it, modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.weight(1.0f))
        actions()
    }
}