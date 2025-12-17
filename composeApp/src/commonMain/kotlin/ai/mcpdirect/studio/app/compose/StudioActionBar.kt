package ai.mcpdirect.studio.app.compose

import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StudioActionBar(
    title: String?=null,
    navigationIcon: @Composable ((() -> Unit))? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
){
    val start = if(navigationIcon!=null) 4.dp else 8.dp
    Row(Modifier.fillMaxWidth().height(48.dp).padding(start = start, end = 4.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        navigationIcon?.invoke()
        title?.let {
            Text(it, modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1.0f))
        actions?.let { it() }
    }
}