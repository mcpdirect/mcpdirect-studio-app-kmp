package ai.mcpdirect.studio.app.dashboard.shortcut

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.agent.MyStudioScreenDialog
import ai.mcpdirect.studio.app.agent.MyStudioViewModel
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.Wizard
import ai.mcpdirect.studio.app.compose.WizardStep
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.openapi.ConnectOpenAPIServerViewModel
import ai.mcpdirect.studio.app.mcpkey.MCPKeyDialog
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.team.MCPTeamDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.check_box
import mcpdirectstudioapp.composeapp.generated.resources.cloud_off
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_down
import mcpdirectstudioapp.composeapp.generated.resources.uncheck_box
import org.jetbrains.compose.resources.painterResource

class CreateMCPTeamShortcut : Shortcut {
    override val title = "Create MCP Team"
    @Composable
    override fun wizard() {
        val wizardSteps = remember {
            listOf(
                WizardStep("Create MCP Team") { wizardViewModel ->
                    wizardViewModel.nextStep++
                    Column(
                        modifier = Modifier.weight(1.0f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Click finish button to create a MCP team"
                        )
                    }
                }
            )
        }
        Wizard(
            wizardSteps,
            onFinish = {
                generalViewModel.currentScreen(Screen.MCPTeam(
                    MCPTeamDialog.CreateTeam))
            }
        )
    }
}