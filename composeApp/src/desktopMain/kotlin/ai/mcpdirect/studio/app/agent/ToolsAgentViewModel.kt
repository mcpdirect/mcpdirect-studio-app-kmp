//package ai.mcpdirect.studio.app.agent
//
//import ai.mcpdirect.studio.app.logbook.ToolLog
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolsAgent
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolsMaker
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class ToolsAgentViewModel() : ViewModel() {
//    private val _viewType = MutableStateFlow(ViewType.MAKERS)
//    val viewType: StateFlow<ViewType> = _viewType
//
//    private val _makerSummaries = MutableStateFlow<List<AIPortToolsMaker>>(emptyList())
//    val makerSummaries: StateFlow<List<AIPortToolsMaker>> = _makerSummaries
//
//    private val _agentSummaries = MutableStateFlow<List<AIPortToolsAgent>>(emptyList())
//    val agentSummaries: StateFlow<List<AIPortToolsAgent>> = _agentSummaries
//
//    private val _tools = MutableStateFlow<List<AIPortTool>>(emptyList())
//    val tools: StateFlow<List<AIPortTool>> = _tools
//
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery
//
//    private val _selectedMaker = MutableStateFlow<String?>(null)
//    val selectedMaker: StateFlow<String?> = _selectedMaker
//
//    private val _selectedAgent = MutableStateFlow<String?>(null)
//    val selectedAgent: StateFlow<String?> = _selectedAgent
//
//    init {
////        FakeToolsLogDataGenerator.populateRepository(repository,50)
////        viewModelScope.launch {
////            repository.getAllLogs().collect { logs ->
////                _currentLogs.value = logs.sortedByDescending { it.timestamp }
////            }
////        }
//        loadSummaries()
//    }
//
//    fun switchViewType(viewType: ViewType) {
//        _viewType.value = viewType
//        loadSummaries()
//    }
//
//    fun selectMaker(makerName: String) {
//        _selectedMaker.value = makerName
//        viewModelScope.launch {
//            repository.getLogsByMaker(makerName).collect { logs ->
//                _tools.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//    }
//
//    fun selectAgent(agentName: String) {
//        _selectedAgent.value = agentName
//        viewModelScope.launch {
//            repository.getLogsByClient(agentName).collect { logs ->
//                _tools.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//    }
//
//    fun backToList() {
//        _selectedMaker.value = null
//        _selectedAgent.value = null
//        viewModelScope.launch {
//            repository.getAllLogs().collect { logs ->
//                _tools.value = logs.sortedByDescending { it.timestamp }
//            }
//        }
//    }
//
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//        loadSummaries()
//    }
//
//    private fun loadSummaries() {
//        viewModelScope.launch {
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
//                ViewType.AGENTS -> {
//                    if (_searchQuery.value.isNotEmpty()) {
//                        _agentSummaries.value = repository.searchClients(_searchQuery.value)
//                    } else {
//                        repository.getClientSummaries().collect { list->
//                            _agentSummaries.value = list
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun filterLogs(toolName: String, filterBy: String) {
//        viewModelScope.launch {
//            _tools.value = when (filterBy) {
//                "tool" -> repository.filterLogsByToolName(toolName)
//                "agent" -> repository.filterLogsByClientName(toolName)
//                "maker" -> repository.filterLogsByMakerName(toolName)
//                else -> repository.getAllLogs()
//            } as List<ToolLog>
//        }
//    }
//
//    enum class ViewType {
//        MAKERS, AGENTS
//    }
//}