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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class MCPServerIntegrationViewModel(private val repository: MCPServerRepository = MCPServerRepositoryImpl()): ViewModel()  {
    private val _makers = mutableMapOf<String, MCPServer>();
    private val _makerSummaries = MutableStateFlow<List<MCPServer>>(emptyList())
    val makerSummaries: StateFlow<List<MCPServer>> = _makerSummaries

    private val _localMakers = mutableMapOf<String, MCPServer>();
    private val _localMakerSummaries = MutableStateFlow<List<MCPServer>>(emptyList())
    val localMakerSummaries: StateFlow<List<MCPServer>> = _localMakerSummaries

    private val _searchQuery = mutableStateOf("")
    val searchQuery = _searchQuery

    private val _selectedMaker = mutableStateOf<MCPServer?>(null)
    val selectedMaker = _selectedMaker

    private val _currentTools = MutableStateFlow<List<AIPortTool>>(emptyList())
    val currentTools: StateFlow<List<AIPortTool>> = _currentTools

    private val _selectedTools = MutableStateFlow<List<AIPortTool>>(emptyList())
    val selectedTools: StateFlow<List<AIPortTool>> = _selectedTools
    val selectedTool = mutableStateOf<AIPortTool?>(null)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadSummaries()
        loadLocalSummaries()
    }

    private fun loadSummaries() {
        viewModelScope.launch {
            if (_searchQuery.value.isNotEmpty()) {
                _makerSummaries.value =
                    _makers.values.stream().filter { server -> server.name.contains(_searchQuery.value) }.toList();
            } else {
                _makerSummaries.value = _makers.values.stream().toList();
            }
        }
    }

    fun selectMaker(maker: MCPServer) {
        _selectedMaker.value = maker
        viewModelScope.launch {
            _selectedTools.value = MCPDirectStudio.getAIPortTools(maker);
            _currentTools.value = _selectedTools.value;
        }
    }
    fun backToList() {
        _selectedMaker.value = null
    }

    fun filterTools(toolName: String, filterBy: String) {
        viewModelScope.launch {
            if(_selectedMaker.value!=null) {
                _currentTools.value = _selectedTools.value.stream().filter { tool -> tool.name.contains(toolName) }.toList()
            }
        }
    }
    fun updateServer(servers: List<MCPServer>){
        for (server in servers) {
            _makers.put(server.name,server)
        }
        loadSummaries()
    }
    fun updateLocalServer(servers: List<MCPServer>){
        _localMakers.clear();
        for (server in servers) {
            _localMakers.put(server.name,server)
        }
        loadLocalSummaries()
    }

    private fun loadLocalSummaries() {
        viewModelScope.launch {
            if (_searchQuery.value.isNotEmpty()) {
                _localMakerSummaries.value =
                    _localMakers.values.stream().filter { server -> server.name.contains(_searchQuery.value) }.toList();
            } else {
                _localMakerSummaries.value = _localMakers.values.stream().toList();
            }
        }
    }
    var servers by mutableStateOf<List<MCPServer>>(emptyList())
    var selectedServer by mutableStateOf<MCPServer?>(null)
    var showAddServerDialog by mutableStateOf(false)
    var newServerName by mutableStateOf("")
    var newServerType by mutableStateOf("stdio") // "stdio" or "sse"
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
    fun onNewServerTypeChange(type: String) { newServerType = type }
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

    fun convertFormToJson() {
        val envMap = newServerEnv.associate { it.first to it.second }
        val serverData = ServerData(
//            name = newServerName,
//            type = newServerType,
            command = if (newServerType == "stdio") newServerCommand else null,
            args = if (newServerType == "stdio") newServerArgs else null,
            url = if (newServerType == "sse") newServerUrl else null,
            env = envMap.ifEmpty { null }
        )
        val configData = ConfigData(mcpServers = mapOf(newServerName to serverData))
        serverJsonString = Json.encodeToString(configData)
    }

    fun convertJsonToForm() {
        try {
            val configData = Json.decodeFromString<ConfigData>(serverJsonString)
            val serverEntry = configData.mcpServers.entries.firstOrNull()
            serverEntry?.let { (name, serverData) ->
                newServerName = name
                newServerType = if(serverData.command==null) "sse" else "stdio"
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
            if (newServerType == "stdio") {
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
            convertJsonToForm() // Ensure form data is updated from JSON before adding
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val newServers = MCPDirectStudio.addMCPServer(serverJsonString)
//                    loadSummaries();
                    fetchServers() // Refresh the list after adding
                    dismissAddServerDialog()
                    // Clear input fields
//                    convertFormToJson()
                } catch (e: Exception) {
                    errorMessage = "Error adding server from JSON: ${e.message}"
                }
            }
        } else {
            val environmentVariables = newServerEnv.associate { it.first to it.second }
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val newServer = MCPDirectStudio.addMCPServer(
                        newServerName,
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
                    newServerType = "stdio"
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

    fun publishServer(server: MCPServer,onPublished: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                MCPDirectStudio.publishTools(server)
                if(server.id>0) {
                    onPublished()
                }
                fetchServers() // Refresh the list after publishing
            }catch (e: Exception){
                errorMessage = "Error publishing MCP tools: ${e.message}"
            }

        }
    }

    fun publishMCPServer() {
        CoroutineScope(Dispatchers.Main).launch {
            if(_selectedMaker.value!=null) try {
                _selectedMaker.value = MCPDirectStudio.publishTools(_selectedMaker.value)
            }catch (e: Exception){
                errorMessage = "Error publishing MCP tools: ${e.message}"
            }

        }
    }

    fun unpublishMCPServer() {
        CoroutineScope(Dispatchers.Main).launch {
            if(_selectedMaker.value!=null) try {
                _selectedMaker.value = MCPDirectStudio.unpublishTools(_selectedMaker.value)
            }catch (e: Exception){
                errorMessage = "Error unpublishing MCP tools: ${e.message}"
            }
        }
    }
    fun removeLocalMCPServer() {
        CoroutineScope(Dispatchers.Main).launch {
            if(_selectedMaker.value!=null) try {
                MCPDirectStudio.removeLocalMCPServer(_selectedMaker.value)
                _selectedMaker.value = null;
            }catch (e: Exception){
                errorMessage = "Error unpublishing MCP tools: ${e.message}"
            }
        }
    }
}
