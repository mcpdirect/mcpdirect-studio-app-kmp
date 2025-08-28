package ai.mcpdirect.studio.app.logbook

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.handler.ToolLogHandler
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.toList

class ToolsLogViewModel() : ViewModel() {
//    private val _viewType = MutableStateFlow(ViewType.MAKERS)
//    val viewType: StateFlow<ViewType> = _viewType
    
//    private val _makerSummaries = MutableStateFlow<List<MakerSummary>>(emptyList())
//    val makerSummaries: StateFlow<List<MakerSummary>> = _makerSummaries
//
//    private val _clientSummaries = MutableStateFlow<List<ClientSummary>>(emptyList())
//    val clientSummaries: StateFlow<List<ClientSummary>> = _clientSummaries
    private val _allLogs = MutableStateFlow<MutableList<ToolLogHandler.ToolLog>>(mutableListOf())
    private val _currentLogs = MutableStateFlow<List<ToolLogHandler.ToolLog>>(emptyList())
    val currentLogs: StateFlow<List<ToolLogHandler.ToolLog>> = _currentLogs

    val dateGroups = mutableMapOf<String,List<ToolLogHandler.ToolLog>>()
    val dateGroupNames = MutableStateFlow<List<String>>(emptyList());
    val selectedGroup = MutableStateFlow("")

    val selectedLog = mutableStateOf<ToolLogHandler.ToolLog?>(null)
    fun selectLog(log: ToolLogHandler.ToolLog) {
        selectedLog.value = log
        viewModelScope.launch {
            logDetails.value = MCPDirectStudio.getToolLogDetails(log.id)
        }
    }
    val logDetails = mutableStateOf<ToolLogHandler.ToolLogDetails?>(null)
    fun clearSelectedLog() {
        selectedLog.value = null
        logDetails.value = null
    }
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
//    private val _selectedMaker = MutableStateFlow<String?>(null)
//    val selectedMaker: StateFlow<String?> = _selectedMaker
//
//    private val _selectedClient = MutableStateFlow<String?>(null)
//    val selectedClient: StateFlow<String?> = _selectedClient

    
//    init {
//        FakeToolsLogDataGenerator.populateRepository(repository,50)
//        viewModelScope.launch {
//            repository.getAllLogs().collect { logs ->
//                _currentLogs.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//        loadSummaries()
//    }
    fun addLog(log: ToolLogHandler.ToolLog) {
        _allLogs.value.add(0,log)
        dateGroups.putAll(_allLogs.value.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(it.timestamp))
        })
        dateGroupNames.value = dateGroups.keys.sortedDescending().toList()
        loadSummaries()
    }
    fun selectDateGroup(group:String){
        selectedGroup.value = group;
        loadSummaries()
    }
    fun selectDateGroup(){
        if(selectedGroup.value.isEmpty()&&dateGroupNames.value.isNotEmpty()){
            selectDateGroup(dateGroupNames.value[0])
        }
    }
//    fun switchViewType(viewType: ViewType) {
//        _viewType.value = viewType
//        loadSummaries()
//    }
//
//    fun selectMaker(makerName: String) {
//        _selectedMaker.value = makerName
//        viewModelScope.launch {
//            repository.getLogsByMaker(makerName).collect { logs ->
//                _currentLogs.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//    }
//
//    fun selectClient(agentName: String) {
//        _selectedClient.value = agentName
//        viewModelScope.launch {
//            repository.getLogsByClient(agentName).collect { logs ->
//                _currentLogs.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//    }
//
//    fun backToList() {
//        _selectedMaker.value = null
//        _selectedClient.value = null
//        viewModelScope.launch {
//            repository.getAllLogs().collect { logs ->
//                _currentLogs.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        loadSummaries()
    }
    fun filterLogs(query: String): List<ToolLogHandler.ToolLog> {
        val logs = dateGroups[selectedGroup.value]
        if(logs!=null&&logs.isNotEmpty())
            if(query.isBlank()){
                return logs;
            }else if(query.startsWith("marker?")){
                return logs.filter { it.makerName.contains(query.substring(7), ignoreCase = true) }
            }else if(query.startsWith("client?")){
                return logs.filter { it.clientName.contains(query.substring(8), ignoreCase = true) }
            }else if(query.startsWith("tool?")){
                return logs.filter { it.toolName.contains(query.substring(5), ignoreCase = true) }
            }else{
                return logs.filter { it.toolName.contains(query, ignoreCase = true)
                            ||it.clientName.contains(query, ignoreCase = true)
                            ||it.makerName.contains(query, ignoreCase = true)
                }
            }
        return emptyList();
    }
    private fun loadSummaries() {
        viewModelScope.launch {
            _currentLogs.value = filterLogs(_searchQuery.value)
        }
//            val query = _searchQuery.value;
//            if(query.isBlank()){
//                _currentLogs.value = repository.getAllLogs();
//            }else if(query.startsWith("marker?")){
//                _currentLogs.value = repository.filterLogsByMakerName(query.substring(7))
//            }else if(query.startsWith("client?")){
//                _currentLogs.value = repository.filterLogsByMakerName(query.substring(7))
//            }
//            when (_viewType.value) {
//                ViewType.MAKERS -> {
//                    if (_searchQuery.value.isNotEmpty()) {
//                        _makerSummaries.value = repository.searchMakers(_searchQuery.value)
//                    } else {
//                        repository.getMakerSummaries().collect { list->
//                            _makerSummaries.value = list
//                        }
//                    }
//                }
//                ViewType.CLIENTS -> {
//                    if (_searchQuery.value.isNotEmpty()) {
//                        _clientSummaries.value = repository.searchClients(_searchQuery.value)
//                    } else {
//                        repository.getClientSummaries().collect { list->
//                            _clientSummaries.value = list
//                        }
//                    }
//                }
//            }
//        }
    }
    
//    fun filterLogs(toolName: String, filterBy: String) {
//        viewModelScope.launch {
//            _currentLogs.value = when (filterBy) {
//                "tool" -> repository.filterLogsByToolName(toolName)
//                "client" -> repository.filterLogsByClientName(toolName)
//                "maker" -> repository.filterLogsByMakerName(toolName)
//                else -> repository.getAllLogs()
//            } as List<ToolLogHandler.ToolLog>
//        }
//    }
//    enum class ViewType {
//        MAKERS, CLIENTS
//    }
}