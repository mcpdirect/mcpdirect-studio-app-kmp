package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val connectMCPViewModel = ConnectMCPViewModel()
class ConnectMCPViewModel: ViewModel() {
    var uiState by mutableStateOf<UIState>(UIState.Idle)

    private var _toolMakers = mutableStateMapOf<Long,AIPortToolMaker>()
    val toolMakers by derivedStateOf {
        _toolMakers.values.toList()
    }
    //    val toolMakers = mutableStateListOf<AIPortToolMaker>()
    var toolMaker by mutableStateOf(AIPortToolMaker())
        private set
    fun toolMaker(maker:AIPortToolMaker){
        if(maker.id!=toolMaker.id){
            queryMCPTools(maker)
        }
        toolMaker = maker
    }
    val tools = mutableStateListOf<AIPortTool>()
    fun reset(){
        toolMaker = AIPortToolMaker()
        _toolMakers.clear()
        tools.clear()
    }
    private fun updateUIState(code:Int){
        uiState = if(code==0) UIState.Success else UIState.Error(code)
    }
    fun updateToolMaker(maker: AIPortToolMaker){
        viewModelScope.launch {
            if(maker.status==-1) _toolMakers.remove(maker.id)
            else if(maker.id!=0L)_toolMakers[maker.id]=maker;
        }
    }
    fun connectMCPServer( configs:Map<String, MCPServerConfig>){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().connectMCPServerToStudio(MCPDirectStudio.studioId(),configs){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
                    it.forEach {
//                        it.id = makerId(it.name)
                        _toolMakers[it.id] = it
                    }
                }
            }
        }
    }

    fun configMCPServer( config:MCPServerConfig){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().configMCPServerForStudio(
                MCPDirectStudio.studioId(), toolMaker.id, config
            ){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
//                    it.id = makerId(it.name)
                    _toolMakers[it.id] = it
                    if(toolMaker.id==it.id){
                        toolMaker = it
                    }
                }
            }
        }
    }

//    fun queryMCPServers(){
//        uiState = UIState.Loading
//        _toolMakers.clear()
//        viewModelScope.launch {
//            getPlatform().queryMCPServersFromStudio(MCPDirectStudio.studioId()) {
//                updateUIState(it.code)
//                if (it.code == 0) {
//                    it.data?.let {
//                        it.forEach {
////                                it.id = makerId(it.name)
//                            _toolMakers[it.id] = it
//                            if(toolMaker.id==it.id){
//                                toolMaker = it
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    fun queryMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
            uiState = UIState.Loading
            tools.clear()
            getPlatform().queryMCPToolsFromStudio(MCPDirectStudio.studioId(),toolMaker.id){
                updateUIState(it.code)
                if(it.code==0){
                    it.data?.let {
                        tools.addAll(it)
                    }
                }
            }
        }
    }
    fun publishMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
            uiState = UIState.Loading
            tools.clear()
            getPlatform().publishMCPToolsForStudio(MCPDirectStudio.studioId(),toolMaker.id){
                updateUIState(it.code)
                if(it.code==0){
                    it.data?.let {
                        _toolMakers.remove(toolMaker.id)
                        _toolMakers[it.id]=it
                        if(this@ConnectMCPViewModel.toolMaker.name==toolMaker.name){
                            this@ConnectMCPViewModel.toolMaker = it
                            queryMCPTools(it)
                        }
                    }
                }
            }
        }
    }
}