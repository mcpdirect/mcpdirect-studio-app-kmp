//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.mutableStateSetOf
//import androidx.compose.runtime.toMutableStateList
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolPermission
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolsAgent
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolsMaker
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.util.*
//
//class ToolPermissionViewModel : ViewModel() {
//    // 当前选中的API Key ID
//    var apiKey = mutableStateOf<AIPortAccessKeyCredential?>(null)
//
//    // 数据列表
//    val agents = mutableStateListOf<AIPortToolAgent>()
//    val makers = mutableStateListOf<AIPortToolMaker>()
//    val tools = mutableStateListOf<AIPortTool>()
//    val permissions = mutableStateListOf<AIPortToolPermission>()
//
//        // 下拉菜单展开状态
//    val agentsDropdownExpanded = mutableStateOf(false)
//    val makersDropdownExpanded = mutableStateOf(false)
//
//    // 选择状态
//    val selectedAgents = mutableStateSetOf<Long>()
//    val selectedMakers = mutableStateSetOf<Long>()
//    val selectedTools = mutableStateSetOf<Long>()
//
//    // 获取当前可见的制造商列表
//    fun getVisibleMakers(): List<AIPortToolsMaker> {
//        return if (selectedAgents.isEmpty()) {
//            makers
//        } else {
//            makers.filter { it.agentId in selectedAgents }
//        }
//    }
//
//    // 全选/取消全选代理商
//    fun toggleAllAgents(selectAll: Boolean) {
//        if (selectAll) {
//            agents.forEach { selectedAgents.add(it.id) }
//        } else {
//            selectedAgents.clear()
//        }
//        // 清除下级选择
////        selectedMakers.clear()
////        selectedTools.clear()
//    }
//
//    // 全选/取消全选制造商
//    fun toggleAllMakers(selectAll: Boolean) {
//        if (selectAll) {
//            getVisibleMakers().forEach { selectedMakers.add(it.id) }
//        } else {
//            selectedMakers.clear()
//        }
//        // 清除工具选择
////        selectedTools.clear()
//    }
//
//        // 获取选中的代理商名称列表
//    fun getSelectedAgentNames(): List<String> {
//        return if (selectedAgents.isEmpty()) {
//            emptyList()
//        } else if (selectedAgents.size == agents.size) {
//            listOf("All Agents")
//        } else {
//            agents.filter { it.id in selectedAgents }.map { it.name }
//        }
//    }
//
//    // 获取选中的制造商名称列表
//    fun getSelectedMakerNames(): List<String> {
//        val visibleMakers = getVisibleMakers()
//        return if (selectedMakers.isEmpty()) {
//            emptyList()
//        } else if (selectedMakers.size == visibleMakers.size) {
//            listOf("All Makers")
//        } else {
//            makers.filter { it.id in selectedMakers }.map { it.name }
//        }
//    }
//
//    private var tempSelectedTools = mutableSetOf<Long>()
//
//    // UI状态
//    val isLoading = mutableStateOf(false)
//    val showSaveSuccess = mutableStateOf(false)
//    val errorMessage = mutableStateOf<String?>(null)
//
//    init {
//        // 初始化时生成假数据
////        generateFakeData()
//    }
//
//    // 加载数据（模拟网络请求）
//    fun loadKeyPermissions() {
//        generateFakeData()
//        viewModelScope.launch {
//            isLoading.value = true
////            apiKey.value = keyId
//            try {
//                // 模拟网络延迟
//                delay(500)
//
//                // 加载当前权限
//                permissions.clear()
//                permissions.addAll(
//                    fakePermissions.filter { it.accessKeyId == apiKey.value!!.id }
//                )
//
//                // 初始化选中状态
//                selectedTools.clear()
//                tempSelectedTools.clear()
//                permissions
//                    .filter { it.status == 1 }
//                    .forEach { selectedTools.add(it.toolId) }
//
//                isLoading.value = false
//            } catch (e: Exception) {
//                errorMessage.value = "Failed to load data: ${e.message}"
//                isLoading.value = false
//            }
//        }
//    }
//
//    fun savePermissions() {
//        viewModelScope.launch {
//            isLoading.value = true
//            try {
//                // 确定最终要授权的工具
//                val toolsToGrant = when {
//                    // 如果直接选择了工具，优先使用这些工具
//                    selectedTools.isNotEmpty() -> selectedTools.toList()
//
//                    // 如果选择了制造商，则授权这些制造商的所有工具
//                    selectedMakers.isNotEmpty() -> {
//                        tools
//                            .filter { it.makerId in selectedMakers }
//                            .map { it.id }
//                    }
//
//                    // 如果选择了代理商，则授权这些代理商的所有工具
//                    selectedAgents.isNotEmpty() -> {
//                        val makerIds = makers
//                            .filter { it.agentId in selectedAgents }
//                            .map { it.id }
//                        tools
//                            .filter { it.makerId in makerIds }
//                            .map { it.id }
//                    }
//
//                    // 默认情况不授权任何工具
//                    else -> emptyList()
//                }
//
//                // 调用API保存权限
////                repository.updatePermissions(apiKeyId.value, toolsToGrant)
//
//                showSaveSuccess.value = true
//                isLoading.value = false
//            } catch (e: Exception) {
//                errorMessage.value = "Failed to save permissions: ${e.message}"
//                isLoading.value = false
//            }
//        }
//    }
//
//    // 检查是否有未保存的更改
//    fun hasUnsavedChanges(): Boolean {
//        val currentActive = permissions
//            .filter { it.accessKeyId == apiKey.value!!.id && it.status == 1 }
//            .map { it.toolId }
//            .toSet()
//
//        return selectedTools != currentActive
//    }
//
//    // 生成假数据
//    private fun generateFakeData() {
//        // 清空现有数据
//        agents.clear()
//        makers.clear()
//        tools.clear()
//        permissions.clear()
//
//        // 添加假数据
//        agents.addAll(fakeAgents)
//        makers.addAll(fakeMakers)
//        tools.addAll(fakeTools)
//        permissions.addAll(fakePermissions)
//    }
//
//    // 根据制造商ID获取工具列表
//    fun getToolsByMaker(makerId: Long): List<AIPortTool> {
//        return tools.filter { it.makerId == makerId }
//    }
//
//    // 根据代理商ID获取制造商列表
//    fun getMakersByAgent(agentId: Long): List<AIPortToolsMaker> {
//        return makers.filter { it.agentId == agentId }
//    }
//
//    // 根据ID获取制造商
//    fun getMakerById(makerId: Long): AIPortToolsMaker? {
//        return makers.firstOrNull { it.id == makerId }
//    }
//
//    // 根据ID获取代理商
//    fun getAgentById(agentId: Long): AIPortToolsAgent? {
//        return agents.firstOrNull { it.id == agentId }
//    }
//
//    fun getToolById(toolId:Long): AIPortTool?{
//        return tools.firstOrNull { it.id == toolId }
//    }
//
//    // 假数据定义
//    private val fakeAgents = listOf(
//        AIPortToolsAgent(
//            1,1, 1,0,
//            System.currentTimeMillis(),
//            "MacMini-Pro",
//            "Alpha Agent",
//            "production,main"
//        ),
//        AIPortToolsAgent(
//            2,1, 1,0,
//            System.currentTimeMillis(),
//            "WinServer-01",
//            "Beta Agent",
//            "testing,backup"
//        ),
//        AIPortToolsAgent(
//            3,1, 2,0,
//            System.currentTimeMillis(),
//            "Linux-Node",
//            "Gamma Agent",
//            "development"
//        )
//    )
//
//    private val fakeMakers = listOf(
//        AIPortToolsMaker(
//            101,
//            System.currentTimeMillis(),
//            1,
//            System.currentTimeMillis(),0,null,
//            AIPortToolsMaker.TYPE_MCP,
//            "Boeing Tools",
//            "aviation,production",
//            1
//        ),
//        AIPortToolsMaker(
//            102,
//            System.currentTimeMillis(),
//            1,
//            System.currentTimeMillis(),0,null,
//            2000,
//            "Airbus Utilities",
//            "aviation,analysis",
//            1
//        ),
//        AIPortToolsMaker(
//            103,
//            System.currentTimeMillis(),
//            1,
//            System.currentTimeMillis(),0,null,
//            3000,
//            "GE Diagnostics",
//            "engine,monitoring",
//            2
//        )
//    )
//
//    private val fakeTools = listOf(
//        AIPortTool(
//            1001,
//            101,
//            1,
//            System.currentTimeMillis(),
//            "Wing Stress Analyzer",
//            0x123456,
//            "Analyzes wing stress under various conditions",
//            "analysis,wing"
//        ),
//        AIPortTool(
//            1002,
//            101,
//            1,
//            System.currentTimeMillis(),
//            "Flap Control Monitor",
//            0x234567,
//            "Monitors flap control systems",
//            "control,flap"
//        ),
//        AIPortTool(
//            1003,
//            102,
//            1,
//            System.currentTimeMillis(),
//            "Cabin Pressure Simulator",
//            0x345678,
//            "Simulates cabin pressure scenarios",
//            "cabin,simulation"
//        ),
//        AIPortTool(
//            1004,
//            102,
//            1,
//            System.currentTimeMillis(),
//            "Fuel Efficiency Calculator",
//            0x456789,
//            "Calculates optimal fuel consumption",
//            "fuel,calculation"
//        ),
//        AIPortTool(
//            1005,
//            103,
//            1,
//            System.currentTimeMillis(),
//            "Engine Vibration Analyzer",
//            0x567890,
//            "Analyzes engine vibration patterns",
//            "engine,diagnostics"
//        )
//    )
//
//    private val fakePermissions = listOf(
//        AIPortToolPermission(
//            1,
//            1,
//            1001,
//            System.currentTimeMillis(),
//            1
//        ),
//        AIPortToolPermission(
//            1,
//            1,
//            1003,
//            System.currentTimeMillis(),
//            1
//        ),
//        AIPortToolPermission(
//            1,
//            2,
//            1002,
//            System.currentTimeMillis(),
//            1
//        ),
//        AIPortToolPermission(
//            1,
//            2,
//            1005,
//            System.currentTimeMillis(),
//            1
//        )
//    )
//}