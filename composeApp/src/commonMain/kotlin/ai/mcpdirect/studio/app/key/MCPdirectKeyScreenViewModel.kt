package ai.mcpdirect.studio.app.key

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualToolPermission
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
    var expanded by mutableStateOf(false)
    val toolMakerCandidateFilter = MutableStateFlow("")
//    val toolMakerFilter = MutableStateFlow("")
    var accessKey by mutableStateOf<AIPortToolAccessKey?>(null)
        private set
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    var toolPermissionCount by mutableStateOf(0)
        private set

    private val _virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()
    val virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()
    var virtualToolPermissionCount by mutableStateOf(0)
        private set

    private val _toolMarkerCandidateIds = MutableStateFlow(mutableSetOf<Long>())
    val toolMarkerCandidateIds: StateFlow<Set<Long>> = _toolMarkerCandidateIds
//    fun toolMarkerCandidate(toolMaker: AIPortToolMaker): Boolean{
//        return toolMaker.id in _toolMarkerCandidateIds.value
//    }
    val toolMarkerCandidates : StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _toolMarkerCandidateIds,
        toolMakerCandidateFilter
    ){ makers,ids,filter ->
        makers.values.filter { (filter.isEmpty()||it.name.contains(filter,ignoreCase = true))&&it.id in ids }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
//    val toolMarkers : StateFlow<List<AIPortToolMaker>> = combine(
//        ToolRepository.toolMakers,
//        toolMakerFilter
//    ){ makers,filter ->
//        makers.values.filter { (filter.isEmpty()||it.name.contains(filter,ignoreCase = true)) }.toList()
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
    private fun <T : AIPortToolPermission> onLoadToolPermissions(resp: AIPortServiceResponse<List<T>>) {
        if (resp.successful()) resp.data?.let { data->
            data.forEach {
                if(it.makerId>0) {
                    if (it is AIPortVirtualToolPermission) {
                        _virtualToolPermissions[it.originalToolId]=it
                        virtualToolPermissions[it.originalToolId] = it.copy()
                        if(it.status>0) {
                            virtualToolPermissionCount++
                            _toolMarkerCandidateIds.update { set ->
                                set.toMutableSet().apply { add(it.makerId) }
                            }
                        }
                    } else {
                        _toolPermissions[it.toolId] = it
                        toolPermissions[it.toolId] = it.copy()
                        if (it.status > 0) {
                            toolPermissionCount++
                            _toolMarkerCandidateIds.update { set ->
                                set.toMutableSet().apply { add(it.makerId) }
                            }
                        }
                    }
                }
            }
        }
    }
    fun accessKey(key: AIPortToolAccessKey?){
        accessKey = key
        if(key!=null&&key.id>Int.MAX_VALUE) {
            _toolPermissions.clear()
            toolPermissions.clear()
            toolPermissionCount = 0
            _virtualToolPermissions.clear()
            virtualToolPermissions.clear()
            virtualToolPermissionCount = 0
            _toolMarkerCandidateIds.update { set->
                set.toMutableSet().apply { clear() }
            }
            viewModelScope.launch {
                ToolRepository.loadToolPermissions(key) { resp->
                    onLoadToolPermissions(resp)
                }
                ToolRepository.loadVirtualToolPermissions(key) { resp->
                    onLoadToolPermissions(resp)
                }
            }
        }
    }
    fun nominate(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { add(toolMaker.id) }
        }
        resetPermissions(toolMaker)
    }
    fun cancelNomination(toolMaker: AIPortToolMaker) {
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply { remove(toolMaker.id) }
        }
    }

    fun permit(permitted: Boolean, tools: List<AIPortTool>){
//        var permission = toolPermissions[tool.id]
        accessKey?.let { accessKey ->
            tools.forEach { tool ->
                if(tool is AIPortVirtualTool){
                    var permission = virtualToolPermissions.remove(tool.toolId)
                    if(permission!=null){
                        if(permission.status==Short.MAX_VALUE.toInt()){
                            if(permitted) virtualToolPermissions[permission.originalToolId]=permission
                            else virtualToolPermissionCount--
                        }else {
                            if (permitted) {
                                permission.status = 1
                                virtualToolPermissionCount++
                            } else {
                                permission.status = 0
                                virtualToolPermissionCount--
                            }
                            virtualToolPermissions[permission.originalToolId]=permission
                        }
                    }else if(permitted) {
                        permission = AIPortVirtualToolPermission()
                        permission.toolId = tool.id
                        permission.originalToolId = tool.toolId
                        permission.status = Short.MAX_VALUE.toInt()
                        permission.makerId = tool.makerId
                        permission.agentId = 0
                        permission.accessKeyId = accessKey.id
                        virtualToolPermissions[permission.originalToolId]=permission
                        virtualToolPermissionCount++
                    }
                }else {
                    var permission = toolPermissions.remove(tool.id)
                    if (permission != null) {
                        if (permission.status == Short.MAX_VALUE.toInt()) {
                            if(permitted) toolPermissions[permission.toolId] = permission
                            else toolPermissionCount--
                        } else {
                            if (permitted&&permission.status==0) {
                                permission.status = 1
                                toolPermissionCount++
                            } else if(!permitted&&permission.status==1) {
                                permission.status = 0
                                toolPermissionCount--
                            }
                            toolPermissions[permission.toolId] = permission
                        }
                    } else if (permitted) {
                        permission = AIPortToolPermission()
                        permission.toolId = tool.id
                        permission.status = Short.MAX_VALUE.toInt()
                        permission.makerId = tool.makerId
                        permission.agentId = tool.agentId
                        permission.accessKeyId = accessKey.id
                        toolPermissions[permission.toolId]=permission
//                        toolPermissions.toMutableMap().apply {
//                            put(permission.toolId,permission)
//                        }
                        toolPermissionCount++
                    }
                }
            }
        }
    }

    fun resetPermissions(toolMaker: AIPortToolMaker){
        if(toolMaker.type==0){
            for(p in virtualToolPermissions.values){
                if(p.makerId==toolMaker.id){
                    virtualToolPermissions.remove(p.originalToolId)
                    if(p.status>0){
                        virtualToolPermissionCount--
                    }
                }
            }
            for(p in _virtualToolPermissions.values){
                if(p.makerId==toolMaker.id) {
                    virtualToolPermissions[p.originalToolId] = p.copy()
                    if(p.status>0){
                        virtualToolPermissionCount++
                    }
                }
            }
        }else{
            for(p in toolPermissions.values){
                if(p.makerId==toolMaker.id){
                    toolPermissions.remove(p.toolId)
                    if(p.status>0){
                        toolPermissionCount--
                    }
                }
            }
            for(p in _toolPermissions.values){
                if(p.makerId==toolMaker.id) {
                    toolPermissions[p.toolId] = p.copy()
                    if(p.status>0){
                        toolPermissionCount++
                    }
                }
            }
        }
    }
    fun resetAllPermissions(){
        toolPermissions.clear()
        toolPermissionCount = 0
        virtualToolPermissions.clear()
        virtualToolPermissionCount = 0
        _toolMarkerCandidateIds.update { set->
            set.toMutableSet().apply {
                clear()
            }
        }
        for(p in _toolPermissions.values){
            toolPermissions[p.toolId] = p.copy()
            if (p.status > 0) {
                toolPermissionCount++
                _toolMarkerCandidateIds.update { set ->
                    set.toMutableSet().apply { add(p.makerId) }
                }
            }
        }
        for(p in _virtualToolPermissions.values){
            virtualToolPermissions[p.toolId] = p.copy()
            if (p.status > 0) {
                virtualToolPermissionCount++
                _toolMarkerCandidateIds.update { set ->
                    set.toMutableSet().apply { add(p.makerId) }
                }
            }
        }
    }
    fun permissionsChanged():Boolean{
        if(virtualToolPermissions.size!=_virtualToolPermissions.size||
            toolPermissions.size!=_toolPermissions.size){
            return true;
        }
        for(v in toolPermissions.values){
            val p = _toolPermissions[v.toolId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        for(v in virtualToolPermissions.values){
            val p = _virtualToolPermissions[v.originalToolId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        return false
    }
    fun savePermissions(){
        if(permissionsChanged()) {
            viewModelScope.launch {
                getPlatform().grantToolPermission(
                    toolPermissions.values.toList(),
                    virtualToolPermissions.values.toList()
                ){(code, message, data) ->
                    if(code==0) {
                        accessKey(accessKey)
                        generalViewModel.showSnackbar("${accessKey?.name} update successfully")
                    }
                }
            }
        } else generalViewModel.showSnackbar("No change in ${accessKey?.name}")
    }
}