package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPServersWidget(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val localToolAgent by viewModel.localToolAgent.collectAsState()
    val toolMakers by viewModel.toolMakers.collectAsState()
    val scrollState = rememberScrollState()
    Column(modifier.padding(start =16.dp,end = 5.dp,bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.plug_connect),
                ""
            )
            Text("MCP Servers (${toolMakers.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MyStudio(),
                        "Install MCP server",
                        Screen.Home
                    )
                }
            ) {
                Icon(
                    painterResource(Res.drawable.add),
                    contentDescription = ""
                )
            }
        }
        StudioSearchbar(modifier=Modifier.padding(end=11.dp)) {
            viewModel.toolMakerFilter.value = it
        }
        if (toolMakers.isNotEmpty()) {
            Box(Modifier.weight(1f).padding(top = 8.dp)) {
                FlowRow(
                    Modifier.verticalScroll(scrollState).padding( end = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    toolMakers.forEach { toolMaker ->
                        if(UserRepository.me(toolMaker.userId))
                            ToolMakerCard(toolMaker,localToolAgent,Modifier.weight(1f).height(120.dp))
                        else
                            TeamToolMakerCard(toolMaker,Modifier.weight(1f).height(120.dp))
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(scrollState = scrollState)
                )
            }

        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button({
                generalViewModel.currentScreen(
                    Screen.MyStudio(),
                    "Install MCP server",
                    Screen.Home
                )
            }){
                Text("Install MCP server")
            }
        }
    }
}
class ToolMakerCardViewModel(toolMaker: AIPortToolMaker): ViewModel(){
    val toolAgent : StateFlow<AIPortToolAgent?> =  StudioRepository.toolAgents
        .map { it[toolMaker.agentId] }     // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = AIPortToolAgent()
        )
}
@Composable
fun ToolMakerCard(
    toolMaker: AIPortToolMaker,
    localToolAgent : AIPortToolAgent,
    modifier: Modifier
){
    val viewModel by remember { mutableStateOf(ToolMakerCardViewModel(toolMaker)) }
    val toolAgent by viewModel.toolAgent.collectAsState()
//    var toolAgent by remember { mutableStateOf(AIPortToolAgent()) }
//    LaunchedEffect(toolMaker) {
//        StudioRepository.toolAgent(toolMaker.agentId) {
//            if (it.successful()) it.data?.let { data ->
//                toolAgent = data
//            }
//        }
//    }
    OutlinedCard(modifier) {
        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                toolMaker.name,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MyStudio(toolMaker = toolMaker),
                        "MCP Servers",
                        Screen.Home
                    )
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painterResource(Res.drawable.setting_config), null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Spacer(Modifier.weight(1.0f))
        toolAgent?.let { toolAgent->
            HorizontalDivider()
            Row(
                Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(painterResource(Res.drawable.design_services), contentDescription = null, Modifier.size(20.dp))
                Text(toolAgent.name,
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.weight(1.0f))
                if(toolMaker.agentId==localToolAgent.id) Text(
                    "Local",
                    Modifier.background(
                        color = MaterialTheme.colorScheme.background,
                        ButtonDefaults.outlinedShape
                    ).clip(ButtonDefaults.outlinedShape).border(
                        ButtonDefaults.outlinedButtonBorder(),
                        ButtonDefaults.outlinedShape
                    ).padding(horizontal = 8.dp,vertical = 2.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamToolMakerCard(
    toolMaker: AIPortToolMaker,
    modifier: Modifier
){
    var team by remember { mutableStateOf<AIPortTeam?>(null) }
    var teams by remember { mutableStateOf<List<AIPortTeam>>(emptyList()) }
    LaunchedEffect(null) {
        TeamRepository.getTeams(toolMaker){
            if(it.successful()) it.data?.let { data ->
                if(data.isNotEmpty()) team = data[0]
                teams = data
            }
        }
    }
    OutlinedCard(modifier) {
        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                toolMaker.name,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MCPTeam(team,listOf(toolMaker)),
                        "My Teams",
                        Screen.Home
                    )
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painterResource(Res.drawable.setting_config),
                    "Team",
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Spacer(Modifier.weight(1.0f))
        HorizontalDivider()
        team?.let { t1 ->
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                Modifier.padding(start=8.dp),
            ) {
                Row(
                    Modifier.padding(vertical = 4.dp).clickable { expanded = !expanded },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painterResource(Res.drawable.group), contentDescription = null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        t1.name,style = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    teams.forEach { t2 ->
                        DropdownMenuItem(
                            text = { Text(t2.name, style = MaterialTheme.typography.bodySmall) },
                            onClick = {
                                expanded = false
                                team = t2
                            },
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}