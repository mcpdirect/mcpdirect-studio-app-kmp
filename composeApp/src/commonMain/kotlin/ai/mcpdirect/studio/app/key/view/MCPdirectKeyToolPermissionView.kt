package ai.mcpdirect.studio.app.key.view

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MCPdirectKeyToolPermissionViewModel : ViewModel() {
    var accessKey by mutableStateOf<AIPortToolAccessKey?>(null)
        private set
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    private val _toolMarkerIds = MutableStateFlow(mutableSetOf<Long>())
    val toolMarkers : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _toolMarkerIds
        ){ makers,ids ->
        makers.values.filter { it.id in ids }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    fun <T : AIPortToolPermission> onLoadToolPermissions(resp: AIPortServiceResponse<List<T>>) {
        if (resp.successful()) resp.data?.let {
            it.forEach {
                _toolPermissions[it.toolId]=it
                toolPermissions[it.toolId]= it.copy()
                _toolMarkerIds.update { set->
                    set.toMutableSet().apply { add(it.makerId) }
                }
//                _toolMarkerIds.value.add(it.makerId)
            }
        }
    }
    fun accessKey(key: AIPortToolAccessKey?){
        accessKey = key
        if(key!=null&&key.id>Int.MAX_VALUE) {
            _toolPermissions.clear()
            toolPermissions.clear()
//            _toolMarkerIds.value.clear()
            _toolMarkerIds.update { set->
                set.toMutableSet().apply { clear() }
            }
            viewModelScope.launch {
                ToolRepository.loadToolPermissions(key) {
                    onLoadToolPermissions(it)
                }
                ToolRepository.loadVirtualToolPermissions(key) {
                    onLoadToolPermissions(it)
                }
            }
        }
    }

    fun resetAllPermissions(){
        toolPermissions.clear()
        _toolMarkerIds.value.clear()
        for(p in _toolPermissions.values){
            toolPermissions[p.toolId] = p.copy()
            _toolMarkerIds.value.add(p.makerId)
        }
    }
}
@Composable
fun MCPdirectKeyToolPermissionView(
    accessKey: AIPortToolAccessKey,
){
    val viewModel by remember {mutableStateOf(MCPdirectKeyToolPermissionViewModel())}
    val toolMakers by viewModel.toolMarkers.collectAsState()
    LaunchedEffect(accessKey){
        viewModel.accessKey(accessKey)
    }
    LazyColumn {
        items(toolMakers){
            OutlinedCard {
                Text(it.name)
            }
        }
    }
}