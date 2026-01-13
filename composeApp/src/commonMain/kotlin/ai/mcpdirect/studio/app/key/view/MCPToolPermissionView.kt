package ai.mcpdirect.studio.app.key.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
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

class ToolMakerPermissionViewModel : ViewModel() {
    var expanded by mutableStateOf(false)
    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
    val toolMaker = MutableStateFlow<AIPortToolMaker?>(null)
    val tools : StateFlow<List<AIPortTool>> = combine(
        ToolRepository.tools,
        toolMaker
    ){ tools,maker ->
        if(maker!=null)tools.values.filter { it.makerId == maker.id }.toList()
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    var checkedTools = mutableStateSetOf<Long>()
    var checkedToolCount by mutableStateOf(0)
    fun toolMaker(maker: AIPortToolMaker){
        toolMaker.value = maker
        viewModelScope.launch {
            if(maker.type>0) ToolRepository.loadTools(maker.userId,maker)
            else ToolRepository.loadVirtualTools(maker.userId,maker)
            StudioRepository.toolAgent(maker.agentId){
                if(it.successful()) it.data?.let { toolAgent = it }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolMakerPermissionView(
    toolMaker: AIPortToolMaker,
    toolPermissions: Map<Long,AIPortToolPermission>,
    viewModel: ToolMakerPermissionViewModel,
    onPermissionsChange: (checked:Boolean,tools:List<AIPortTool>)->Unit,
){
//    val viewModel by rememberSaveable(toolMaker.id) { mutableStateOf(ToolMakerPermissionViewModel()) }
    val localToolAgent by StudioRepository.localToolAgent.collectAsState()
    val tools by viewModel.tools.collectAsState()
//    var checkedTools by remember { mutableStateOf(0) }
//    LaunchedEffect(toolMaker){
//        viewModel.toolMaker(toolMaker)
//    }
//    LaunchedEffect(toolPermissions) {
//        viewModel.checkedTools = toolPermissions.values.count{it.status>0&&it.makerId == toolMaker.id}
//    }
    OutlinedCard(Modifier.fillMaxWidth()) {
        StudioActionBar("${toolMaker.name} (${viewModel.checkedToolCount}/${tools.size})", navigationIcon = {
            Checkbox(checked = viewModel.checkedToolCount>0,onCheckedChange = {}, Modifier.size(32.dp))
        }) {
//            Icon(painterResource(Res.drawable.shield_toggle),contentDescription = null)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}){
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
                val toolPermission = toolPermissions[tool.id]
                var checked by remember { mutableStateOf(tool.id in viewModel.checkedTools || toolPermission?.status?.let { it>0 }?:false) }
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
//                OutlinedButton()
//                TextButton()
                Button(
                    modifier = Modifier.height(28.dp).hoverable(interactionSource),
//                    shape = if(checked) ButtonDefaults.outlinedShape else ButtonDefaults.textShape,
                    colors = if(checked) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                    border = ButtonDefaults.outlinedButtonBorder(!checked),
//                    elevation = if(checked) ButtonDefaults.text() else null,
                    onClick = {
                        checked=!checked
                        if(checked) {
                            viewModel.checkedToolCount++
                            viewModel.checkedTools.add(tool.id)
                        } else {
                            viewModel.checkedToolCount--
                            viewModel.checkedTools.remove(tool.id)
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
                if (it.id == localToolAgent.id) Badge(Modifier.padding(start = 8.dp)) { Text("This device") }
            }
        }
    }
}
//class MCPdirectKeyToolPermissionViewModel : ViewModel() {
//    val toolMakerFilter = MutableStateFlow("")
//    var accessKey by mutableStateOf<AIPortToolAccessKey?>(null)
//        private set
//    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
//    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
//    private val _toolMarkerIds = MutableStateFlow(mutableSetOf<Long>())
//    val toolMarkers : StateFlow<List<AIPortToolMaker>> = combine(
//        ToolRepository.toolMakers,
//        _toolMarkerIds,
//                toolMakerFilter
//        ){ makers,ids,filter ->
//        makers.values.filter { (filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))&&it.id in ids }.toList()
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
////    val toolMarkers : StateFlow<List<AIPortToolMaker>> = combine(
////        ToolRepository.toolMakers,
////        toolMakerFilter
////    ){ makers,filter ->
////        makers.values.filter { (filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))}.toList()
////    }.stateIn(
////        scope = viewModelScope,
////        started = SharingStarted.WhileSubscribed(5000),
////        initialValue = emptyList()
////    )
//    fun <T : AIPortToolPermission> onLoadToolPermissions(resp: AIPortServiceResponse<List<T>>) {
//        if (resp.successful()) resp.data?.let {
//            it.forEach {
//                _toolPermissions[it.toolId]=it
//                toolPermissions[it.toolId]= it.copy()
//                _toolMarkerIds.update { set->
//                    set.toMutableSet().apply { add(it.makerId) }
//                }
//            }
//        }
//    }
//    fun accessKey(key: AIPortToolAccessKey?){
//        accessKey = key
//        if(key!=null&&key.id>Int.MAX_VALUE) {
//            _toolPermissions.clear()
//            toolPermissions.clear()
//            _toolMarkerIds.update { set->
//                set.toMutableSet().apply { clear() }
//            }
//            viewModelScope.launch {
//                ToolRepository.loadToolPermissions(key) {
//                    onLoadToolPermissions(it)
//                }
//                ToolRepository.loadVirtualToolPermissions(key) {
//                    onLoadToolPermissions(it)
//                }
//            }
//        }
//    }
//
//    fun resetAllPermissions(){
//        toolPermissions.clear()
//        _toolMarkerIds.value.clear()
//        for(p in _toolPermissions.values){
//            toolPermissions[p.toolId] = p.copy()
//            _toolMarkerIds.value.add(p.makerId)
//        }
//    }
//}
//@Composable
//fun MCPdirectKeyToolPermissionView(
//    accessKey: AIPortToolAccessKey,
//    modifier: Modifier = Modifier
//){
//    val viewModel by remember {mutableStateOf(MCPdirectKeyToolPermissionViewModel())}
//    val toolMakers by viewModel.toolMarkers.collectAsState()
//    LaunchedEffect(accessKey){
//        viewModel.accessKey(accessKey)
//    }
//    Box(modifier = Modifier.padding(16.dp)){
//        Row(
//            Modifier.background(
//                MaterialTheme.colorScheme.surfaceContainerHigh,
//                ButtonDefaults.shape
//            ).clip(ButtonDefaults.shape),
//            verticalAlignment = Alignment.CenterVertically,
//        ){
//            var value by remember { mutableStateOf("") }
//            Icon(painterResource(Res.drawable.search), contentDescription = null,
//                Modifier.padding(12.dp))
//            BasicTextField(
//                modifier=Modifier.weight(1f).padding(end = 4.dp),
//                value = value,
//                onValueChange = {
//                    value = it
//                    viewModel.toolMakerFilter.value = it
//                },
//            )
//            if(value.isNotEmpty()) IconButton(onClick = {
//                value=""
//                viewModel.toolMakerFilter.value = ""
//            }){
//                Icon(painterResource(Res.drawable.close), contentDescription = null)
//            }
//        }
//    }
//    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
//        items(toolMakers){ toolMaker->
//            ToolMakerPermissionView(toolMaker)
//        }
//    }
//}