//package ai.mcpdirect.studio.app.data.model.mcp
//
//sealed interface MCPServer {
//    val id: String
//    val name: String
//    val environment: List<EnvironmentVariable>
//    val status: ServerStatus
//    val tools: List<MCPTool>
//}
//
//data class StdioMCPServer(
//    override val id: String,
//    override val name: String,
//    val command: String,
//    val args: List<String>,
//    override val environment: List<EnvironmentVariable> = emptyList(),
//    override val status: ServerStatus,
//    override val tools: List<MCPTool> = emptyList()
//) : MCPServer
//
//data class SseMCPServer(
//    override val id: String,
//    override val name: String,
//    val url: String,
//    override val environment: List<EnvironmentVariable> = emptyList(),
//    override val status: ServerStatus,
//    override val tools: List<MCPTool> = emptyList()
//) : MCPServer
//
data class EnvironmentVariable(
    val key: String,
    val value: String
)
//
//data class MCPTool(
//    val id: String,
//    val name: String,
//    val description: String,
//    val parametersJsonSchema: String
//)
//
//enum class ServerStatus {
//    ONLINE,
//    OFFLINE,
//    CONNECTING
//}
