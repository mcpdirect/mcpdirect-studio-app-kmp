package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.set

class MCPdirectKeyScreenViewModel: ViewModel() {
    val toolMakerCandidateFilter = MutableStateFlow("")
    val toolMakerFilter = MutableStateFlow("")
    var accessKey by mutableStateOf<AIPortToolAccessKey?>(null)
        private set
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    private val _toolMarkerCandidateIds = MutableStateFlow(mutableSetOf<Long>())
    val toolMarkerCandidates : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _toolMarkerCandidateIds,
        toolMakerCandidateFilter
    ){ makers,ids,filter ->
        makers.values.filter { (filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))&&it.id in ids }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val toolMarkers : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _toolMarkerCandidateIds,
        toolMakerFilter
    ){ makers,ids,filter ->
        makers.values.filter { (filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))&& it.id !in ids }.toList()
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
                _toolMarkerCandidateIds.update { set->
                    set.toMutableSet().apply { add(it.makerId) }
                }
            }
        }
    }
    fun accessKey(key: AIPortToolAccessKey?){
        accessKey = key
        if(key!=null&&key.id>Int.MAX_VALUE) {
            _toolPermissions.clear()
            toolPermissions.clear()
            _toolMarkerCandidateIds.update { set->
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
        _toolMarkerCandidateIds.value.clear()
        for(p in _toolPermissions.values){
            toolPermissions[p.toolId] = p.copy()
            _toolMarkerCandidateIds.value.add(p.makerId)
        }
    }
    fun nominate(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { add(toolMaker.id) }
        }
    }
    fun cancelNomination(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { remove(toolMaker.id) }
        }
    }
}