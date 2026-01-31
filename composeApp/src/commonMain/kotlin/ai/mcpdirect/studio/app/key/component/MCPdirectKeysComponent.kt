package ai.mcpdirect.studio.app.key.component

import ai.mcpdirect.studio.app.compose.ListButton
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.edit
import org.jetbrains.compose.resources.painterResource

class MCPdirectKeysComponentViewModel : ViewModel() {
    val accessKeys: StateFlow<List<AIPortToolAccessKey>> = AccessKeyRepository.accessKeys
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )

//    var currentAccessKey by mutableStateOf<AIPortToolAccessKey?>(null)
//        private set
//    fun selectAccessKey(accessKey: AIPortToolAccessKey?){
//        currentAccessKey = accessKey
//    }
    fun generateMCPdirectKey(
        keyName:String,
        onResponse: (resp: AIPortServiceResponse<AIPortToolAccessKey>) -> Unit
    ) {
        viewModelScope.launch {
            AccessKeyRepository.generateAccessKey(keyName){
//                if(it.successful()) it.data?.let { data->
//                    currentAccessKey = data
//                }
                onResponse(it)
            }
        }
    }

    fun modifyMCPdirectKey(
        key: AIPortToolAccessKey, name: String,
        onResponse: (resp: AIPortServiceResponse<AIPortToolAccessKey>) -> Unit
    ) {
        viewModelScope.launch {
            AccessKeyRepository.modifyAccessKey(key,name=name, onResponse = onResponse)
        }
    }
//    fun selectedAccessKey(accessKey: AIPortToolAccessKey): Boolean{
//        return currentAccessKey?.id == accessKey.id
//    }
}
@Composable
fun MCPdirectKeysComponent(
    accessKey: AIPortToolAccessKey?=null,
    showKeyGeneration: Boolean = false,
//    viewModel: MCPdirectKeysComponentViewModel,
    modifier: Modifier = Modifier,
    onAccessKeyChange:(key:AIPortToolAccessKey?)->Unit
){
    val viewModel by remember {mutableStateOf(MCPdirectKeysComponentViewModel())}
    val accessKeys by viewModel.accessKeys.collectAsState()
    var currentAccessKey by remember { mutableStateOf<AIPortToolAccessKey?>(null) }
    var editableAccessKey by remember { mutableStateOf<AIPortToolAccessKey?>(null) }
    var showGenerateKeyView by remember { mutableStateOf(showKeyGeneration) }
    LaunchedEffect(accessKey){
//        viewModel.selectAccessKey(accessKey)
        currentAccessKey = accessKey
        onAccessKeyChange(accessKey)
    }
    Box(
        modifier,
        contentAlignment = Alignment.BottomEnd
    ){
        Column(modifier) {
            if(!showGenerateKeyView){
                StudioActionBar("MCPdirect Keys"){
//                    TooltipIconButton(
//                        "Edit MCPdirect Key Name",
//                        onClick = {},
//                        modifier = Modifier.size(32.dp)
//                    ){
//                        Icon(painterResource(Res.drawable.edit),contentDescription = null,Modifier.size(20.dp))
//                    }
                }
//                HorizontalDivider()
                LazyColumn(
                    Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                    items(accessKeys) { accessKey ->
                        val selected = currentAccessKey?.id == accessKey.id
                        ListButton(
                            onClick = {
                                currentAccessKey = accessKey
                                onAccessKeyChange(accessKey)
                            },
                            selected = selected,
//                        leadingContent = {
//                            Checkbox(checked = selected, onCheckedChange = {
//                                viewModel.selectAccessKey(accessKey)
//                            })
//                        },
                            headlineContent = {
                                Row {
                                    Text(accessKey.name)
                                    TooltipIconButton(
                                        "Edit MCPdirect Key Name",
                                        onClick = {
                                            editableAccessKey = accessKey
                                            showGenerateKeyView = true
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
                var name by remember { mutableStateOf(editableAccessKey?.name ?: "") }
                var nameError by remember { mutableStateOf(true) }
                StudioActionBar(editableAccessKey?.name ?: "Generate New Key")
//                {
//                    if(accessKeys.isNotEmpty())TextButton(
//                        modifier = Modifier.height(32.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp),
//                        onClick = { showGenerateKeyView = false }
//                    ) {
//                        Text(
//                            "MCPdirect Keys",
//                            style = MaterialTheme.typography.bodySmall,
//                        )
//                    }
//                }
//                HorizontalDivider()
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    value = name,
                    onValueChange = { text ->
                        nameError = text.isBlank() || text.length>20
                        if(text.isBlank()) name = ""
                        else name = text
                    },
                    label = { Text("MCPdirect Key Name") },
                    singleLine = true,
                    isError = nameError,
                    supportingText = {
                        Text("Name must not be empty and should have at most 20 characters")
                    },
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            editableAccessKey = null
                            showGenerateKeyView = false
                        },
                    ){
                        Text("Cancel")
                    }
                    Button(
                        enabled = !nameError,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            if(editableAccessKey!=null)
                                viewModel.modifyMCPdirectKey(editableAccessKey!!,name){
                                    if(it.successful()) it.data?.let{ data ->
                                        editableAccessKey = null
                                        showGenerateKeyView = false
                                        if(currentAccessKey?.id == data.id) {
                                            currentAccessKey = data
                                            onAccessKeyChange(data)
                                        }
                                    }
                                }
                            else
                                viewModel.generateMCPdirectKey(name){
                                    showGenerateKeyView = !it.successful()
                                    if(!showGenerateKeyView) it.data?.let { data->
                                        currentAccessKey = data
                                        onAccessKeyChange(data)
                                    }
                                }
                        },
                    ){
                        Text(if(editableAccessKey!=null) "Save" else "Generate")
                    }
                }
            }
        }
        if(!showGenerateKeyView){
            FloatingActionButton(
                onClick = { showGenerateKeyView = true },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
            ){
                Icon(painterResource(Res.drawable.add),contentDescription = null)
            }
        }
    }
}