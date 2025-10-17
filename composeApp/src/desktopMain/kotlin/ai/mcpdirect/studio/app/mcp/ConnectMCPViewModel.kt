package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import ai.mcpdirect.studio.dao.entity.MCPServer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
sealed class ConnectMCPDialog() {
    object None : ConnectMCPDialog()
    object AddServer : ConnectMCPDialog()
    object EditServerName : ConnectMCPDialog()
    object EditServerTags : ConnectMCPDialog()
}
class ConnectMCPViewModel(private val repository: MCPServerRepository = MCPServerRepositoryImpl()): ViewModel()  {
    val _makers = mutableStateMapOf<String, MCPServer>()
    val makers by derivedStateOf {
        if (searchQuery.isNotEmpty()) {

            _makers.values.stream().filter { server -> server.name.contains(searchQuery) }.toList();
        } else {
            _makers.values.stream().toList();
        }
    }
//    var makers  = mutableListOf<MCPServer>()
//        private set
//    private val _makerSummaries = MutableStateFlow<List<MCPServer>>(emptyList())
//    val makerSummaries: StateFlow<List<MCPServer>> = _makerSummaries

    private val _localMakers = mutableStateMapOf<String,MCPServer>()
    val localMakers by derivedStateOf {
        if (searchQuery.isNotEmpty()) {

            _localMakers.values.stream().filter { server -> server.name.contains(searchQuery) }.toList();
        } else {
            _localMakers.values.stream().toList();
        }
    }
//    private val _localMakerSummaries = MutableStateFlow<List<MCPServer>>(emptyList())
//    val localMakerSummaries: StateFlow<List<MCPServer>> = _localMakerSummaries

    var searchQuery by mutableStateOf("")
        private  set

//    val searchQuery = _searchQuery

    private var _selectedMaker by mutableStateOf<MCPServer?>(null)
    val selectedMaker by derivedStateOf {
        _selectedMaker
    }

    private val _currentTools = MutableStateFlow<List<AIPortTool>>(emptyList())
    val currentTools: StateFlow<List<AIPortTool>> = _currentTools

    private val _selectedTools = MutableStateFlow<List<AIPortTool>>(emptyList())
    val selectedTools: StateFlow<List<AIPortTool>> = _selectedTools
    val selectedTool = mutableStateOf<AIPortTool?>(null)

    fun updateSearchQuery(query: String) {
        searchQuery = query
        loadSummaries()
        loadLocalSummaries()
    }

    private fun loadSummaries() {
//        if (searchQuery.isNotEmpty()) {
//            makers =_makers.values.stream().filter { server -> server.name.contains(searchQuery) }.toList();
//        } else {
//            makers = _makers.values.stream().toList();
//        }
    }
    private fun loadLocalSummaries() {
//        if (searchQuery.isNotEmpty()) {
//            localMakers = _localMakers.values.stream().filter { server -> server.name.contains(searchQuery) }.toList();
//        } else {
//            localMakers = _localMakers.values.stream().toList();
//        }
    }
    fun selectMaker(maker: MCPServer) {
        _selectedMaker = maker
        viewModelScope.launch {
            _selectedTools.value = MCPDirectStudio.getAIPortTools(maker);
            _currentTools.value = _selectedTools.value;
        }
    }
    fun backToList() {
        _selectedMaker = null
    }

    fun filterTools(toolName: String, filterBy: String) {
        viewModelScope.launch {
            if(_selectedMaker!=null) {
                _currentTools.value = _selectedTools.value.stream().filter { tool -> tool.name.contains(toolName) }.toList()
            }
        }
    }
    fun updateServer(servers: List<MCPServer>){
//        viewModelScope.launch {
            _makers.clear()
            for (server in servers) {
                _makers[server.name] = server
            }
//            loadSummaries()
//        }
    }
    fun updateLocalServer(servers: List<MCPServer>){
        _localMakers.clear();
        for (server in servers) {
            _localMakers[server.name] = server
        }
        loadLocalSummaries()
    }

    var servers by mutableStateOf<List<MCPServer>>(emptyList())
    var selectedServer by mutableStateOf<MCPServer?>(null)
    var showAddServerDialog by mutableStateOf(false)
    var newServerName by mutableStateOf("")
    var newServerType by mutableStateOf(0) // "stdio" or "sse"
    var newServerCommand by mutableStateOf("")
    var newServerArgs by mutableStateOf(mutableStateListOf<String>())
    var newServerUrl by mutableStateOf("")
    var newServerEnv by mutableStateOf(mutableStateListOf<Pair<String, String>>())

    var showJsonView by mutableStateOf(false)
    var serverJsonString by mutableStateOf("")

    var isServerNameValid by mutableStateOf(true)
    var isCommandValid by mutableStateOf(true)
    var isUrlValid by mutableStateOf(true)
    var showValidationError by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onNewServerNameChange(name: String) {
        isServerNameValid = name.isNotBlank()&&name.length<33
        if(isServerNameValid) {
            newServerName = name
        }
    }
    fun onNewServerTypeChange(type: Int) { newServerType = type }
    fun onNewServerCommandChange(command: String) { newServerCommand = command }
//    fun onNewServerArgsChange(args: List<String>) { newServerArgs = args }
    fun onNewServerUrlChange(url: String) { newServerUrl = url }

    fun dismissAddServerDialog() { showAddServerDialog = false }
    fun showAddServerDialog() { showAddServerDialog = true }

    init {
        fetchServers()
    }

    fun fetchServers() {
        println("Fetching servers...")
        CoroutineScope(Dispatchers.Main).launch {
            servers = repository.getServers()
            println("Servers fetched: ${servers.size}")
        }
    }

    fun selectServer(serverName: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            selectedServer = if (serverName != null) {
                repository.getServerDetails(serverName)
            } else {
                null
            }
        }
    }

    @Serializable
    data class ConfigData(
        val mcpServers: Map<String, ServerData>
    )

    @Serializable
    data class ServerData(
        val url: String? = null,
        val command: String? = null,
        val args: List<String>? = null,
        val env: Map<String, String>? = null
    )

//    fun convertFormToJson() {
//        val envMap = newServerEnv.associate { it.first to it.second }
//        val serverData = ServerData(
////            name = newServerName,
////            type = newServerType,
//            command = if (newServerType == "stdio") newServerCommand else null,
//            args = if (newServerType == "stdio") newServerArgs else null,
//            url = if (newServerType == "sse") newServerUrl else null,
//            env = envMap.ifEmpty { null }
//        )
//        val configData = ConfigData(mcpServers = mapOf(newServerName to serverData))
//        serverJsonString = Json.encodeToString(configData)
//    }

    fun convertJsonToForm() {
        try {
            val configData = Json.decodeFromString<ConfigData>(serverJsonString)
            val serverEntry = configData.mcpServers.entries.firstOrNull()

            serverEntry?.let { (name, serverData) ->
                newServerName = name
                if(serverData.command!=null) newServerType=0
                newServerCommand = serverData.command ?: ""
                newServerArgs.clear()
                serverData.args?.let { newServerArgs.addAll(it) }
                newServerUrl = serverData.url ?: ""
                newServerEnv.clear()
                serverData.env?.forEach { (key, value) -> newServerEnv.add(key to value) }
            }
        } catch (e: Exception) {
            errorMessage = "Error parsing JSON: ${e.message}"
        }
    }

    fun pasteJsonFromClipboard() {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                serverJsonString = clipboard.getData(DataFlavor.stringFlavor) as String
                convertJsonToForm() // Automatically convert after pasting
            }
        } catch (e: Exception) {
            errorMessage = "Error pasting from clipboard: ${e.message}"
        }
    }

    fun addServer() {
        showValidationError = false
        isServerNameValid = newServerName.isNotBlank() && newServerName.length<17

        if (!showJsonView) {
            if (newServerType == 0) {
                isCommandValid = newServerCommand.isNotBlank()
                if (!isCommandValid) {
                    showValidationError = true
                    return
                }
            } else { // sse
                isUrlValid = newServerUrl.isNotBlank()
                if (!isUrlValid) {
                    showValidationError = true
                    return
                }
            }
        }

        if (showJsonView) {
//            convertJsonToForm() // Ensure form data is updated from JSON before adding
//            CoroutineScope(Dispatchers.Main).launch {
//                try {
//                    val newServers = MCPDirectStudio.addMCPServer(serverJsonString)
////                    loadSummaries();
//                    fetchServers() // Refresh the list after adding
//                    dismissAddServerDialog()
//                    // Clear input fields
////                    convertFormToJson()
//                } catch (e: Exception) {
//                    errorMessage = "Error adding server from JSON: ${e.message}"
//                }
//            }
        } else {
            val environmentVariables = newServerEnv.associate { it.first to it.second }
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val newServer = MCPDirectStudio.addMCPServer(
                        newServerName,
                        newServerType,
                        newServerUrl,
                        newServerCommand,
                        newServerArgs,
                        environmentVariables
                    )
                    println("Server added to repository.")
//                    loadSummaries();
                    fetchServers() // Refresh the list after adding
                    dismissAddServerDialog()
                    // Clear input fields
                    newServerName = ""
                    newServerType = 0
                    newServerCommand = ""
                    newServerArgs.clear()
                    newServerUrl = ""
                    newServerEnv.clear()
                } catch (e: Exception) {
                    errorMessage = "Error adding server: ${e.message}"
                }
            }
        }
    }

//    fun publishServer(server: MCPServer,onPublished: () -> Unit) {
//        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                MCPDirectStudio.publishTools(server)
//                if(server.id>0) {
//                    onPublished()
//                }
//                fetchServers() // Refresh the list after publishing
//            }catch (e: Exception){
//                errorMessage = "Error publishing MCP tools: ${e.message}"
//            }
//
//        }
//    }

    fun publishMCPServer() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _selectedMaker?.let {
                    try {
                        _selectedMaker = MCPDirectStudio.publishTools(it)
                    }catch (e: Exception){
                        errorMessage = "Error publishing MCP tools: ${e.message}"
                    }
                }
            }
        }
//        CoroutineScope(Dispatchers.Main).launch {
//            if(_selectedMaker.value!=null) try {
//                _selectedMaker.value = MCPDirectStudio.publishTools(_selectedMaker.value)
//            }catch (e: Exception){
//                errorMessage = "Error publishing MCP tools: ${e.message}"
//            }
//
//        }
    }

    fun unpublishMCPServer() {
        CoroutineScope(Dispatchers.Main).launch {
            if(_selectedMaker!=null) try {
                _selectedMaker = MCPDirectStudio.unpublishTools(_selectedMaker)
            }catch (e: Exception){
                errorMessage = "Error unpublishing MCP tools: ${e.message}"
            }
        }
    }
    fun removeLocalMCPServer() {
        CoroutineScope(Dispatchers.Main).launch {
            if(_selectedMaker!=null) try {
                MCPDirectStudio.removeLocalMCPServer(_selectedMaker)
                _selectedMaker = null;
            }catch (e: Exception){
                errorMessage = "Error unpublishing MCP tools: ${e.message}"
            }
        }
    }
    fun reloadMCPServer() {
        CoroutineScope(Dispatchers.Main).launch {
            _selectedTools.value = listOf()
            _currentTools.value = _selectedTools.value;
        }
        CoroutineScope(Dispatchers.Main).launch {
            _selectedMaker?.let {
                try {
                    viewModelScope.launch {
                        it.refreshTools()
                        _selectedTools.value = MCPDirectStudio.getAIPortTools(it)
                        _currentTools.value = _selectedTools.value
                    }
                }catch (e: Exception){
                    errorMessage = "Error unpublishing MCP tools: ${e.message}"
                }
            }
        }
    }
    fun updateServerName(serverName:String,onResponse:((code:Int,message:String?)->Unit)?=null) {
        _selectedMaker?.let {
            viewModelScope.launch {
                val server = _makers.remove(it.name)
                server?.let {
                    withContext(Dispatchers.IO){
                        MCPDirectStudio.modifyToolMaker(it.id, serverName,null,null) {
                                code, message, data ->
                            if (code == 0 && data != null) {
                                server.name = data.name
                                _selectedMaker?.let {
                                    if(it.id==data.id){
                                        _selectedMaker = server
                                    }
                                }
                                _makers[data.name] = server
                                loadSummaries()
                            }
                            onResponse?.invoke(code, message)
                        }
                    }
                }
            }

        }
    }
    fun updateServerTags(serverTags:String,onResponse:((code:Int,message:String?)->Unit)?=null) {
        _selectedMaker?.let {
            val server = _makers.remove(it.name)
            server?.let {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        MCPDirectStudio.modifyToolMaker(it.id, null,serverTags,null) {
                                code, message, data ->
                            if (code == 0 && data != null) {
                                server.tags = data.tags
                                _selectedMaker?.let {
                                    if(it.id==data.id){
                                        _selectedMaker = server
                                    }
                                }
                                _makers[data.name] = server
                                loadSummaries()
                            }
                            onResponse?.invoke(code, message)
                        }

                    }
                }
            }
        }
    }
}
