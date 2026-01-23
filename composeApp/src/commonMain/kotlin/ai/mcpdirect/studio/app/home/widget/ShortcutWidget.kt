package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.studio.app.home.HomeViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShortcutWidget(
    modifier: Modifier,
    viewModel: HomeViewModel,
) {
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Shortcut",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        TextButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ){Row(Modifier.fillMaxWidth()) {
            Text("Connect OpenAPI as MCP")
        }}
        TextButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ){Row(Modifier.fillMaxWidth()) {
            Text("Integrate with AI Agents")
        }}
    }
}