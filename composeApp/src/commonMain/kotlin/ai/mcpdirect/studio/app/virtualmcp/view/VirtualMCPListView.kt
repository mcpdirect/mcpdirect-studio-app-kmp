package ai.mcpdirect.studio.app.virtualmcp.view

import ai.mcpdirect.studio.app.compose.ListButton
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.TYPE_VIRTUAL
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.edit
import org.jetbrains.compose.resources.painterResource

class VirtualMCPListViewModel : ViewModel() {
    val toolMakerFilter = MutableStateFlow("")
    val toolMakers : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        toolMakerFilter
    ){ makers,filter -> makers.values.filter {
        it.type==TYPE_VIRTUAL&&(filter.isEmpty()||it.name.contains(filter,ignoreCase = true))
    }.sortedBy { it.name } }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var currentToolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
    fun currentToolMaker(toolMaker: AIPortToolMaker?){
        currentToolMaker = toolMaker
    }
    fun selectedToolMaker(toolMaker: AIPortToolMaker): Boolean{
        return currentToolMaker?.id == toolMaker.id
    }
    fun createVirtualToolMaker(
        name:String,
        onResponse: (resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit
    ){
        viewModelScope.launch {
            ToolRepository.createVirtualToolMaker(name,listOf(),onResponse)
        }
    }
    fun modifyVirtualToolMaker(
        toolMaker: AIPortToolMaker,
        name: String? = null,
        status: Int? = null,
        tags: String? = null,
        onResponse: (resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit
    ){
        viewModelScope.launch {
            ToolRepository.modifyToolMaker(toolMaker,name,status,tags,onResponse)
        }
    }
}
@Composable
fun VirtualMCPListView(
    toolMaker: AIPortToolMaker?=null,
    showKeyGeneration: Boolean = false,
    modifier: Modifier = Modifier,
    onToolMakerChange:(toolMaker:AIPortToolMaker)->Unit
){
    val viewModel by remember {mutableStateOf(VirtualMCPListViewModel())}
    val toolMakers by viewModel.toolMakers.collectAsState()
    var showCreateView by remember { mutableStateOf(showKeyGeneration) }
    var editableMaker by remember { mutableStateOf<AIPortToolMaker?>(null) }
    LaunchedEffect(toolMaker){
        if(toolMaker!=null){
            viewModel.currentToolMaker(toolMaker)
            onToolMakerChange(toolMaker)
        }
    }
    Box(
        modifier,
        contentAlignment = Alignment.BottomEnd
    ){
        Column(modifier) {
            if(!showCreateView){
                StudioActionBar("Virtual MCP")
//                {
//                    TextButton(
//                        modifier = Modifier.height(32.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp),
//                        onClick = { showCreateView = true }
//                    ) {
//                        Text(
//                            "Create",
//                            style = MaterialTheme.typography.bodySmall,
//                        )
//                    }
//                }
//            HorizontalDivider()
                LazyColumn(
                    Modifier.padding(horizontal = 16.dp)
                ) {
                    items(toolMakers) { toolMaker ->
                        val selected = viewModel.selectedToolMaker(toolMaker)
                        ListButton(
                            onClick = {
                                viewModel.currentToolMaker(toolMaker)
                                onToolMakerChange(toolMaker)
                            },
                            selected = selected,
//                        leadingContent = {
//                            Checkbox(checked = selected, onCheckedChange = {
//                                viewModel.selectAccessKey(toolMaker)
//                            })
//                        },
                            headlineContent = {
                                Row {
                                    Text(toolMaker.name)
                                    TooltipIconButton(
                                        "Edit Virtual MCP name",
                                        onClick = {
                                            editableMaker = toolMaker
                                            showCreateView = true
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ){
                                        Icon(painterResource(Res.drawable.edit),contentDescription = null,Modifier.size(16.dp))
                                    }
                                }
                            },
                        )
                    }
                }
            } else {
                StudioActionBar(editableMaker?.name?:"Create")
//                {
//                    if(toolMakers.isNotEmpty())TextButton(
//                        modifier = Modifier.height(32.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp),
//                        onClick = { showCreateView = false }
//                    ) {
//                        Text(
//                            "Virtual MCP",
//                            style = MaterialTheme.typography.bodySmall,
//                        )
//                    }
//                }
//                HorizontalDivider()
                var name by remember { mutableStateOf(editableMaker?.name?:"") }
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
                Row {
                    if(toolMakers.isNotEmpty())TextButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            editableMaker = null
                            showCreateView = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        enabled = !nameError,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            if(editableMaker!=null){
                                viewModel.modifyVirtualToolMaker(editableMaker!!,name){
                                  if(it.successful()) it.data?.let { data->
                                      editableMaker = null
                                      showCreateView = false
                                      if(viewModel.currentToolMaker?.id==data.id){
                                          viewModel.currentToolMaker(data)
                                          onToolMakerChange(data)
                                      }
                                  }
                                }
                            }else viewModel.createVirtualToolMaker(name) {
                                showCreateView = !it.successful()
                                if (!showCreateView) it.data?.let {
                                    viewModel.currentToolMaker(it)
                                    onToolMakerChange(it)

                                }
                            }
                        },
                    ) {
                        Text(if(editableMaker!=null) "Save" else "Create")
                    }
                }
            }
        }
        if(!showCreateView){
            FloatingActionButton(
                onClick = {
                    editableMaker = null
                    showCreateView = true
                },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
            ){
                Icon(painterResource(Res.drawable.add),contentDescription = null)
            }
        }
    }
}