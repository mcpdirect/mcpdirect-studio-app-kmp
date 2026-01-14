package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.studio.app.model.aitool.AIPortTool
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
import kotlin.collections.set

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
        resetTools(toolMaker)
    }
    fun cancelNomination(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { remove(toolMaker.id) }
        }
    }
    fun selectTools(selected: Boolean, tools: List<AIPortTool>){
        currentToolMaker?.let { toolMaker ->
            tools.forEach { tool ->
                var virtualTool = virtualTools.remove(tool.id)
                if(virtualTool!=null){
                    if(virtualTool.status==Short.MAX_VALUE.toInt()){
                        if(selected) virtualTools[virtualTool.toolId]=virtualTool
                        else virtualToolCount--
                    }else {
                        if (selected) {
                            virtualTool.status = 1
                            virtualToolCount++
                        } else {
                            virtualTool.status = 0
                            virtualToolCount--
                        }
                        virtualTools[virtualTool.toolId]=virtualTool
                    }
                }else if(selected) {
                    virtualTool = AIPortVirtualTool()
                    virtualTool.toolId = tool.id
                    virtualTool.status = Short.MAX_VALUE.toInt()
                    virtualTool.makerId = toolMaker.id
                    virtualTool.originalMakerId = tool.makerId
                    virtualTool.agentId = 0
                    virtualTools[virtualTool.toolId]=virtualTool
                    virtualToolCount++
                }
            }
        }
    }
    fun resetTools(toolMaker: AIPortToolMaker){
        for(p in virtualTools.values){
            if(p.originalMakerId==toolMaker.id){
                virtualTools.remove(p.toolId)
                if(p.status>0){
                    virtualToolCount--
                }
            }
        }
        for(p in _virtualTools.values){
            if(p.originalMakerId==toolMaker.id) {
                virtualTools[p.toolId] = p.copy()
                if(p.status>0){
                    virtualToolCount++
                }
            }
        }
    }
    fun resetAllTools(){
        virtualTools.clear()
        virtualToolCount = 0
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply {
                clear()
            }
        }
        for(p in _virtualTools.values){
            virtualTools[p.toolId] = p.copy()
            if (p.status > 0) {
                virtualToolCount++
                _toolMarkerCandidateIds.update { set ->
                    set.toMutableSet().apply { add(p.originalMakerId) }
                }
            }
        }
    }
    fun saveVirtualTools(){
        currentToolMaker?.let { toolMaker ->
            val tools = mutableMapOf<Long, AIPortVirtualTool>()
            tools.putAll(virtualTools)
            _virtualTools.values.forEach { tool ->
                val newTool = tools[tool.toolId]
                if(newTool!=null&&newTool.status==tool.status){
                    tools.remove(tool.toolId)
                }
            }
            if(tools.isNotEmpty()){
                viewModelScope.launch {
                    ToolRepository.modifyVirtualTools(toolMaker,tools.values.toList()){
                        if(it.successful()) it.data?.let { currentToolMaker(toolMaker) }
                    }
                }
            }
        }
    }
}