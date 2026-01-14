package ai.mcpdirect.studio.app.virtualmcp.view

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.TYPE_VIRTUAL
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.set

class VirtualToolMakersViewModel : ViewModel() {
    val toolMakerFilter = MutableStateFlow("")
    val toolMakers : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        toolMakerFilter
    ){ makers,filter -> makers.values.filter {
        it.type==TYPE_VIRTUAL&&(filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))
    }.toList() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var currentToolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
    fun currentToolMaker(toolMaker: AIPortToolMaker?){
        currentToolMaker = toolMaker
    }
//    fun generateMCPdirectKey(
//        keyName:String,
//        onResponse: (resp: AIPortServiceResponse<AIPortToolAccessKey>) -> Unit
//    ) {
//        viewModelScope.launch {
//            AccessKeyRepository.generateAccessKey(keyName){
//                if(it.successful()) it.data?.let { data->
//                    currentAccessKey = data
//                }
//                onResponse(it)
//            }
//        }
//    }
    fun selectedToolMaker(toolMaker: AIPortToolMaker): Boolean{
        return currentToolMaker?.id == toolMaker.id
    }
    fun createVirtualToolMaker(
        name:String,
        onResponse: (resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit
    ){
        viewModelScope.launch {
            ToolRepository.createVirtualToolMaker(name,listOf()){
                onResponse(it)
            }
        }
    }
}
@Composable
fun VirtualToolMakersView(
    toolMaker: AIPortToolMaker?=null,
    showKeyGeneration: Boolean = false,
    modifier: Modifier = Modifier,
    onToolMakerChange:(toolMaker:AIPortToolMaker)->Unit
){
    val viewModel by remember {mutableStateOf(VirtualToolMakersViewModel())}
    val toolMakers by viewModel.toolMakers.collectAsState()
    var showGenerateKeyView by remember { mutableStateOf(showKeyGeneration) }
    LaunchedEffect(toolMaker){
        if(toolMaker!=null){
            viewModel.currentToolMaker(toolMaker)
            onToolMakerChange(toolMaker)
        }
    }
    Column(modifier) {
        if(!showGenerateKeyView){
            StudioActionBar("Virtual MCP"){
                TextButton(
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = { showGenerateKeyView = true }
                ) {
                    Text(
                        "Create",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            HorizontalDivider()
            LazyColumn {
                items(toolMakers) { toolMaker ->
                    val selected = viewModel.selectedToolMaker(toolMaker)
                    StudioListItem(
                        modifier = Modifier.clickable {
                            viewModel.currentToolMaker(toolMaker)
                            onToolMakerChange(toolMaker)
                        },
                        selected = selected,
//                        leadingContent = {
//                            Checkbox(checked = selected, onCheckedChange = {
//                                viewModel.selectAccessKey(toolMaker)
//                            })
//                        },
                        headlineContent = { Text(toolMaker.name) },
                    )
                }
            }
        } else {
            StudioActionBar("Create"){
                if(toolMakers.isNotEmpty())TextButton(
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = { showGenerateKeyView = false }
                ) {
                    Text(
                        "Virtual MCP",
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
                label = { Text("Virtual MCP Name") },
                isError = nameError,
                supportingText = {
                    Text("Name must not be empty and should have at most 20 characters")
                },
            )
            Button(
                enabled = !nameError,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = {
                    viewModel.createVirtualToolMaker(name){
                        if(it.successful()) it.data?.let {
                            viewModel.currentToolMaker(it)
                            onToolMakerChange(it)
                        }
                    }
                },
            ){
                Text("Create")
            }
        }
    }
}