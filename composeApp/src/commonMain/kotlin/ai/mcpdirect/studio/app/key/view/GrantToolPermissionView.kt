package ai.mcpdirect.studio.app.key.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualToolPermission
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
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
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

class GrantToolPermissionViewModel : ViewModel() {
    var expanded by mutableStateOf(false)
    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
    val toolMaker = MutableStateFlow<AIPortToolMaker?>(null)
    val tools : StateFlow<List<AIPortTool>> = combine(
        ToolRepository.tools,
        ToolRepository.virtualTools,
        toolMaker
    ){ tools,vtools,maker ->
        if(maker!=null) {
            if(maker.type==0) vtools.values.filter { it.makerId == maker.id }.toList()
            else tools.values.filter { it.makerId == maker.id }.sortedBy { it.name }
        }
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    var selectedTools = MutableStateFlow<Map<Long,Boolean>>(emptyMap())
    var selectedToolCount by mutableStateOf(0)
    fun selectTools(
        toolPermissions: Map<Long,AIPortToolPermission>
    ){
        toolMaker.value?.let { toolMaker->
            selectedTools.update { map->
                map.toMutableMap().apply {
                    clear()
                }
            }
            selectedToolCount = 0
            toolPermissions.values.forEach {
                if(it.makerId == toolMaker.id&&it.status>0) {
                    selectedTools.update { map->
                        map.toMutableMap().apply {
                            if(it is AIPortVirtualToolPermission) put(it.originalToolId,true)
                            else put(it.toolId,true)
                        }
                    }
                    selectedToolCount++
                }
            }
        }
    }
    fun toolMaker(maker: AIPortToolMaker){
        toolMaker.value = maker
        viewModelScope.launch {
            if(maker.type>0) ToolRepository.loadTools(maker.userId,maker)
            else ToolRepository.loadVirtualTools(maker)
            StudioRepository.toolAgent(maker.agentId){
                if(it.successful()) it.data?.let { toolAgent = it }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrantToolPermissionView(
//    toolMaker: AIPortToolMaker,
    viewModel: GrantToolPermissionViewModel,
    onReset:()-> Unit,
    onPermissionsChange: (checked:Boolean,tools:List<AIPortTool>)->Unit,
){
    val toolMaker by viewModel.toolMaker.collectAsState()
    val localToolAgent by StudioRepository.localToolAgent.collectAsState()
    val tools by viewModel.tools.collectAsState()
    val selectedTools by viewModel.selectedTools.collectAsState()
    OutlinedCard(Modifier.fillMaxWidth()) {
        StudioActionBar("${toolMaker?.name} (${viewModel.selectedToolCount}/${tools.size})", navigationIcon = {
            Checkbox(checked = viewModel.selectedToolCount>0,onCheckedChange = {
                if(it) viewModel.selectedToolCount = tools.size
                else viewModel.selectedToolCount = 0
                tools.forEach { tool ->
                    viewModel.selectedTools.update { map ->
                        map.toMutableMap().apply {
                            put(tool.id, it)
                        }
                    }
                }
                onPermissionsChange(it,tools)
            }, Modifier.size(32.dp))
        }) {
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {onReset()}){
                Icon(painterResource(Res.drawable.reset_settings),contentDescription = null, Modifier.size(20.dp))
            }
            IconButton(onClick = { viewModel.expanded = !viewModel.expanded },modifier = Modifier.size(32.dp)) {
                val icon = if(viewModel.expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                Icon(painterResource(icon),contentDescription = null, Modifier.size(20.dp))
            }
        }
        if(viewModel.expanded)FlowRow(
            Modifier.padding(start = 16.dp,end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            tools.forEach { tool ->
                val toolId = if(tool is AIPortVirtualTool) tool.toolId else tool.id
                var checked = selectedTools[toolId]?:false
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
                Button(
                    modifier = Modifier.height(28.dp).hoverable(interactionSource),
                    colors = if(checked) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                    border = ButtonDefaults.outlinedButtonBorder(!checked),
                    onClick = {
                        checked=!checked
                        if(checked) {
                            viewModel.selectedToolCount++
                        } else {
                            viewModel.selectedToolCount--
                        }
                        viewModel.selectedTools.update { map ->
                            map.toMutableMap().apply {
                                put(toolId, checked)
                            }
                        }
                        onPermissionsChange(checked,listOf(tool)) },
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Box(contentAlignment = Alignment.CenterEnd) {
                        Text(tool.name, Modifier.padding(horizontal = 8.dp))
                        if (isHovered) {
                            val colors = TooltipDefaults.richTooltipColors()
                            IconButton(
                                onClick = {}, Modifier.size(28.dp),
                                colors = IconButtonDefaults.iconButtonColors().copy(
                                    containerColor = colors.containerColor,
                                    contentColor = colors.contentColor,
                                ),
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
        Row(Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            viewModel.toolAgent?.let {
                Text(it.name)
                if (it.id == localToolAgent.id) Badge(Modifier.padding(start = 8.dp)) { Text("Local") }
            }
        }
    }
}