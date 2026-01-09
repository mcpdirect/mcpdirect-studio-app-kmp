package ai.mcpdirect.studio.app.key.component

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MCPdirectKeysComponentViewModel : ViewModel() {
    val accessKeys: StateFlow<List<AIPortToolAccessKey>> = AccessKeyRepository.accessKeys
        .map { it.values.toList() }      // 转为 List
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
fun MCPdirectKeysComponent(
    key: AIPortToolAccessKey?=null,
    showKeyGeneration: Boolean = false,
    viewModel: MCPdirectKeysComponentViewModel,
    modifier: Modifier = Modifier,
){
    val accessKeys by viewModel.accessKeys.collectAsState()
    var showGenerateKeyView by remember { mutableStateOf(showKeyGeneration) }
    LaunchedEffect(key){
        viewModel.selectAccessKey(key)
    }
    Column(modifier) {
        if(!showGenerateKeyView){
            StudioActionBar("MCPdirect Keys"){
                TextButton(
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = { showGenerateKeyView = true }
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
                        },
                        selected = selected,
//                        leadingContent = {
//                            Checkbox(checked = selected, onCheckedChange = {
//                                viewModel.selectAccessKey(accessKey)
//                            })
//                        },
                        headlineContent = { Text(accessKey.name) },
                    )
                }
            }
        } else {
            StudioActionBar("Generate New Key"){
                if(accessKeys.isNotEmpty())TextButton(
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = { showGenerateKeyView = false }
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
                    if(text.isBlank()) name = ""
                    else name = text
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
                        showGenerateKeyView = !it.successful()
                    }
                },
            ){
                Text("Generate")
            }
        }
    }
}