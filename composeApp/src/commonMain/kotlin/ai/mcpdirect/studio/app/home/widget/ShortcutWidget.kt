package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.group
import mcpdirectstudioapp.composeapp.generated.resources.more
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
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
            onClick = {
                generalViewModel.currentScreen(
                    Screen.MyStudio(toolMaker = AIPortToolMaker(1)),
                    "My Studios",
                    Screen.Home
                )
            },
            modifier = Modifier.fillMaxWidth().height(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ){Row(Modifier.fillMaxWidth()) {
            Text("Connect OpenAPI as MCP")
        }}
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextButton(
                onClick = {
                    expanded = !expanded
                },
                modifier = Modifier.fillMaxWidth().height(32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ){Row(Modifier.fillMaxWidth()) {
                Text("Integrate with AI Agents")
                Spacer(Modifier.weight(1f))
                Icon(painterResource(Res.drawable.more), contentDescription = "", Modifier.size(20.dp))
            }}

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                viewModel.accessKeys.value.forEach { accessKey ->
                    DropdownMenuItem(
                        text = { Text(accessKey.name, style = MaterialTheme.typography.bodySmall) },
                        onClick = {
                            expanded = false
                            generalViewModel.currentScreen(
                                Screen.MCPAccessKey(
                                    accessKey = accessKey,
                                    integrationGuide = true
                                ),
                                "MCPdirect Keys",
                                Screen.Home
                            )
                        },
                        modifier = Modifier.height(24.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}