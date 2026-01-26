package ai.mcpdirect.studio.app.tips.component

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MCPdirectKeyQuickstartComponentViewModel(
    val tools:List<AIPortTool>
): ViewModel(){
    fun countTools(toolMaker: AIPortToolMaker): Int{
        return tools.count { it.makerId == toolMaker.id }
    }
    private val _selectedTools = mutableStateMapOf<Long,AIPortTool>()
    val selectedTools by derivedStateOf { _selectedTools.values.toList() }

    fun selectTool(selected:Boolean,tool: AIPortTool){
        if(selected) _selectedTools[tool.id] = tool
        else _selectedTools.remove(tool.id)
    }
    fun selectedTool(tool: AIPortTool):Boolean{
        return _selectedTools.containsKey(tool.id)
    }
    fun selectAllTools(selected: Boolean){
        if(selected) {
            for (tool in tools) {
                _selectedTools[tool.id] = tool
            }
        } else for (entry in _selectedTools){
            _selectedTools.remove(entry.key)
        }
    }
    fun selectAllTools(selected: Boolean,toolMaker: AIPortToolMaker){
        if(selected) {
            for (tool in tools) if (tool.makerId == toolMaker.id) {
                _selectedTools[tool.id] = tool
            }
        } else for (entry in _selectedTools) if(entry.value.makerId==toolMaker.id) {
            _selectedTools.remove(entry.key)
        }
    }
    fun countSelectedTools(toolMaker: AIPortToolMaker): Int{
        return _selectedTools.values.count { it.makerId == toolMaker.id }
    }
    val accessKeys: StateFlow<List<AIPortToolAccessKey>> = AccessKeyRepository.accessKeys
        .map { accessKeys-> accessKeys.values.sortedBy { it.name } }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    var currentAccessKey by mutableStateOf<AIPortToolAccessKey?>(null)
        private set
    fun selectAccessKey(accessKey: AIPortToolAccessKey?){
        currentAccessKey = accessKey
    }
    fun generateMCPdirectKey(
        keyName:String,
        onResponse: (resp: AIPortServiceResponse<AIPortToolAccessKey>) -> Unit
    ) {
        viewModelScope.launch {
            AccessKeyRepository.generateAccessKey(keyName){
                if(it.successful()) it.data?.let { data->
                    currentAccessKey = data
                }
                onResponse(it)
            }
        }
    }
    fun selectedAccessKey(accessKey: AIPortToolAccessKey): Boolean{
        return currentAccessKey?.id == accessKey.id
    }
}
@Composable
fun MCPdirectKeyQuickstartComponent(
    selectedToolMakers: List<AIPortToolMaker>,
    selectedTools:List<AIPortTool>,
    modifier: Modifier = Modifier,
    onToolPermissionChange:(accessKey: AIPortToolAccessKey?,List<AIPortTool>)->Unit
){
    val viewModel by remember { mutableStateOf(MCPdirectKeyQuickstartComponentViewModel(selectedTools)) }
    val accessKeys by viewModel.accessKeys.collectAsState()
    var generateKey by remember { mutableStateOf(false) }
    LaunchedEffect(selectedTools){
        viewModel.selectAllTools(true)
    }
    Row(modifier.fillMaxSize()){
        LazyColumn(Modifier.weight(2f)) {
            items(selectedToolMakers) { toolMaker ->
                val toolCount = viewModel.countTools(toolMaker)
                val selectedToolCount = viewModel.countSelectedTools(toolMaker)
                val tools = selectedTools.filter { it.makerId == toolMaker.id }.toList()

                OutlinedCard{
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedToolCount>0,
                            onCheckedChange = {
                                viewModel.selectAllTools(it,toolMaker)
                                onToolPermissionChange(viewModel.currentAccessKey,viewModel.selectedTools)
                            }
                        )
                        Text("${toolMaker.name} ($selectedToolCount/$toolCount)")
                    }
                    HorizontalDivider()
                    FlowRow(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ){
                        tools.forEach { tool ->
                            Tag(
                                tool.name,
                                toggle = viewModel.selectedTool(tool),
                            ){
                                viewModel.selectTool(it,tool)
                                onToolPermissionChange(viewModel.currentAccessKey,viewModel.selectedTools)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            if(!generateKey&&accessKeys.isNotEmpty()){

                StudioActionBar("MCPdirect Keys"){
                    TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onClick = { generateKey = true }
                    ) {
                        Text(
                            "Generate New Key",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                HorizontalDivider()
                LazyColumn {
                    items(accessKeys) { accessKey ->
                        val selected = viewModel.selectedAccessKey(accessKey)
                        StudioListItem(
                            modifier = Modifier.clickable {
                                viewModel.selectAccessKey(accessKey)
                                onToolPermissionChange(viewModel.currentAccessKey,viewModel.selectedTools)
                            },
                            selected = selected,
                            leadingContent = {
                                Checkbox(checked = selected, onCheckedChange = { selected->
                                    val key = if(selected) accessKey else null
                                    viewModel.selectAccessKey(key)
                                    onToolPermissionChange(key,viewModel.selectedTools)
                                })
                            },
                            headlineContent = { Text(accessKey.name) },
                        )
                    }
                }
            } else {
                StudioActionBar("Generate New Key"){
                    if(accessKeys.isNotEmpty())TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onClick = { generateKey = false }
                    ) {
                        Text(
                            "MCPdirect Keys",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                HorizontalDivider()
                var name by remember { mutableStateOf("") }
                var nameError by remember { mutableStateOf(true) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    value = name,
                    onValueChange = { text ->
                        nameError = text.isBlank() || text.length>20
                        name = text.ifBlank { "" }
                    },
                    label = { Text("MCPdirect Key Name") },
                    isError = nameError,
                    supportingText = {
                        Text("Name must not be empty and should have at most 20 characters")
                    },
                )
                Button(
                    enabled = !nameError,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = {
                        viewModel.generateMCPdirectKey(name){
                            generateKey = !it.successful()
                        }
                    },
                ){
                    Text("Generate")
                }
            }
        }
    }
}