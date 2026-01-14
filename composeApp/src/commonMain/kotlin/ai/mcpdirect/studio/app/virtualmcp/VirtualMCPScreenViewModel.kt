package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.TYPE_VIRTUAL
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VirtualMCPScreenViewModel: ViewModel() {
    var expanded by mutableStateOf(false)
    var currentToolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
//    val toolMakerFilter = MutableStateFlow("")
    val toolMakerCandidateFilter = MutableStateFlow("")
//    val toolMarkers : StateFlow<List<AIPortToolMaker>> = combine(
//        ToolRepository.toolMakers,
//        toolMakerFilter
//    ){ makers,filter -> makers.values.filter {
//        it.type==TYPE_VIRTUAL&&(filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))
//    }.toList() }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
    private val _toolMarkerCandidateIds = MutableStateFlow(mutableSetOf<Long>())
    val toolMarkerCandidateIds: StateFlow<Set<Long>> = _toolMarkerCandidateIds
    val toolMarkerCandidates : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _toolMarkerCandidateIds,
        toolMakerCandidateFilter
    ){ makers,ids,filter -> makers.values.filter {
        (filter.isEmpty()||it.name.lowercase().contains(filter.lowercase()))&&it.id in ids
    }.toList() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    var virtualToolCount by mutableStateOf(0)
        private set
    private val _virtualTools = mutableStateMapOf<Long, AIPortVirtualTool>()
    val virtualTools = mutableStateMapOf<Long, AIPortVirtualTool>()

    fun currentToolMaker(toolMaker: AIPortToolMaker?){
        if (toolMaker!=null&&toolMaker.type == TYPE_VIRTUAL) {
            currentToolMaker = toolMaker
            _virtualTools.clear()
            virtualTools.clear()
            virtualToolCount = 0
            _toolMarkerCandidateIds.update { set ->
                set.toMutableSet().apply { clear() }
            }
            viewModelScope.launch {
                ToolRepository.loadVirtualTools(toolMaker) { resp ->
                    if (resp.successful()) resp.data?.let { data ->
                        data.forEach {
                            _virtualTools[it.toolId] = it
                            virtualTools[it.toolId] = it.copy()
                            if (it.status > 0) {
                                virtualToolCount++
                                _toolMarkerCandidateIds.update { set ->
                                    set.toMutableSet().apply { add(it.originalMakerId) }
                                }
                            }
                        }
                    }
                }
            }
        } else currentToolMaker = null
    }
    fun nominate(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { add(toolMaker.id) }
        }
//        resetPermissions(toolMaker)
    }
    fun cancelNomination(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { remove(toolMaker.id) }
        }
    }
}