package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.TYPE_VIRTUAL
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.description
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.person
import org.jetbrains.compose.resources.painterResource

class SharedMCPServerViewModel : ViewModel() {
    var user by mutableStateOf<AIPortUser?>(null)
    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
    var expanded by mutableStateOf(false)
    val toolMaker = MutableStateFlow<AIPortToolMaker?>(null)
    val tools : StateFlow<List<AIPortTool>> = combine(
        ToolRepository.tools,
        ToolRepository.virtualTools,
        toolMaker
    ){ tools,vtools,maker ->
        if(maker!=null) {
            if(maker.type==0) vtools.values.filter { it.makerId == maker.id }.toList()
            else tools.values.filter { it.makerId == maker.id }.toList()
        }
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toolMaker(maker: AIPortToolMaker){
        toolMaker.value = maker
        viewModelScope.launch {
            if(maker.type==TYPE_VIRTUAL) ToolRepository.loadVirtualTools(toolMaker=maker)
            else ToolRepository.loadTools(maker.userId,toolMaker=maker)
            StudioRepository.toolAgent(maker.agentId){
                if(it.successful()) it.data?.let { toolAgent = it }
            }
            UserRepository.user(maker.userId){
                if(it.successful()) it.data?.let { user = it }
            }
        }
    }
}
@Composable
fun SharedMCPServerView(
    toolMaker: AIPortToolMaker,
    expanded: Boolean = false,
    selected: Boolean? = null,
    modifier: Modifier,
    onSelectedChange: ((Boolean)->Unit)? = null
){
    val viewModel by remember { mutableStateOf(SharedMCPServerViewModel()) }
    LaunchedEffect(toolMaker){
        viewModel.toolMaker(toolMaker)
    }
    LaunchedEffect(expanded) {
        viewModel.expanded = expanded
    }
//    val toolMaker by viewModel.toolMaker.collectAsState()
    val localToolAgent by StudioRepository.localToolAgent.collectAsState()
    val tools by viewModel.tools.collectAsState()

//    toolMaker?.let { toolMaker ->
        OutlinedCard(modifier) {
            StudioActionBar(
                "${toolMaker.name} (${tools.size})",
                navigationIcon = {
                    selected?.let {
                        var checked by remember { mutableStateOf(selected) }
                        Checkbox(checked = checked,onCheckedChange = {
                            checked = it
                            onSelectedChange?.invoke(it)
                        }, Modifier.size(32.dp))
                    }
                }
            ) {
                IconButton(onClick = { viewModel.expanded = !viewModel.expanded }, modifier = Modifier.size(32.dp)) {
                    val icon = if (viewModel.expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                    Icon(painterResource(icon), contentDescription = null, Modifier.size(20.dp))
                }
            }
            if (viewModel.expanded) FlowRow(
                Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tools.forEach { tool ->
                    val toolId = if (tool is AIPortVirtualTool) tool.toolId else tool.id
                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()
                    OutlinedButton(
                        modifier = Modifier.height(28.dp).hoverable(interactionSource),
                        onClick = {
                        },
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Box(contentAlignment = Alignment.CenterEnd) {
                            Text(tool.name, Modifier.padding(horizontal = 8.dp))
                            if (isHovered) {
                                Box(
                                    Modifier.size(28.dp).background(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = MaterialTheme.shapes.large
                                    ), contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.description),
                                        contentDescription = null,
                                        Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                }
            }
            HorizontalDivider()
            Row(
                Modifier.padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.user?.let { user ->
                    if(UserRepository.me(user)) viewModel.toolAgent?.let {
                        Icon(painterResource(Res.drawable.design_services), contentDescription = null, Modifier.size(20.dp))
                        Text(it.name,
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodySmall)
                        if (it.id == localToolAgent.id) Badge { Text("This device") }
                    }else {
                        Icon(painterResource(Res.drawable.person), contentDescription = null, Modifier.size(20.dp))
                        Text(user.name,
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
//        }
    }
}