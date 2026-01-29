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
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.UserRepository
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

class ConnectMCPShortcut : Shortcut {
    override val title = "Connect MCP"
    @Composable
    override fun wizard() {
        val myStudioViewModel by remember { mutableStateOf(MyStudioViewModel()) }
        LaunchedEffect(myStudioViewModel) {
            myStudioViewModel.refreshToolAgents()
        }
        val toolAgents by myStudioViewModel.toolAgents.collectAsState()
        var toolAgent by remember { mutableStateOf<AIPortToolAgent?>(null) }
//        val viewModel by remember { mutableStateOf(ConnectOpenAPIServerViewModel()) }
//        var yaml by remember { mutableStateOf("")}
        val wizardSteps = remember {
            listOf(
                WizardStep("Select a MCPdirect Studio") {
                    wizardViewModel ->
                    if(toolAgents.size>1) {
                        val localToolAgent by myStudioViewModel.localToolAgent.collectAsState()
                        LazyColumn {
                            items(toolAgents) {
                                println("${it.id},${it.name}")
                                if (it.id != 0L && UserRepository.me(it.userId)) ListItem(
                                    modifier = Modifier.clickable(
                                        enabled = it.status == AIPortToolMaker.STATUS_ON
                                    ) {
                                        toolAgent = it
                                        wizardViewModel.nextStep++
                                    },
                                    leadingContent = {
                                        if(toolAgent==null||toolAgent!!.id!=it.id){
                                            Icon(
                                                painterResource(Res.drawable.uncheck_box),
                                                ""
                                            )
                                        }else{
                                            Icon(
                                                painterResource(Res.drawable.check_box),
                                                ""
                                            )
                                        }
                                    },
                                    headlineContent = { Text(it.name, softWrap = false, overflow = TextOverflow.MiddleEllipsis) },
                                    supportingContent = {
                                        if (it.id == localToolAgent.id)
                                            Tag("Local")
                                    },
                                    trailingContent = {
                                        if (it.status == 0) Icon(
                                            painterResource(Res.drawable.cloud_off),
                                            contentDescription = "Offline",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                )
                            }
                        }
                    }
                },
                WizardStep("Connect MCP Server") { wizardViewModel ->
                    wizardViewModel.nextStep++
                    Column(
                        modifier = Modifier.weight(1.0f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Click finish button to connect a MCP server"
                        )
                    }
                }
            )
        }
//    Modifier.weight(3.0f)
        Wizard(
            wizardSteps,
            onFinish = {
                generalViewModel.currentScreen(Screen.MyStudio(
                    toolAgent,null, MyStudioScreenDialog.ConnectMCP))
            }
        )
    }
}