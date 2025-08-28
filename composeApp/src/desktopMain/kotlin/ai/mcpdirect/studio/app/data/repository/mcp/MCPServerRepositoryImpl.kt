package ai.mcpdirect.studio.app.data.repository.mcp

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.dao.entity.MCPServer


class MCPServerRepositoryImpl : MCPServerRepository {

    companion object {
        private var _mockServers  = mutableListOf<MCPServer>()
//        (
//            StdioMCPServer(
//                id = "server1",
//                name = "NLP Production Server",
//                command = "mcp-nlp-service",
//                args = emptyList(),
//                status = ServerStatus.ONLINE,
//                environment = listOf(EnvironmentVariable("ENV_VAR_1", "value1"), EnvironmentVariable("ENV_VAR_2", "value2")),
//                tools = listOf(
//                    MCPTool(
//                        id = "tool1",
//                        name = "Sentiment Analysis",
//                        description = "Analyzes the sentiment of a given text.",
//                        parametersJsonSchema = """{ \"type\": \"object\", \"properties\": { \"text\": { \"type\": \"string\", \"description\": \"The text to analyze.\" } }, \"required\": [\"text\"] }"""
//                    ),
//                    MCPTool(
//                        id = "tool2",
//                        name = "Text Summarization",
//                        description = "Summarizes a long piece of text.",
//                        parametersJsonSchema = """{ \"type\": \"object\", \"properties\": { \"text\": { \"type\": \"string\", \"description\": \"The text to summarize.\" }, \"length\": { \"type\": \"integer\", \"description\": \"Desired summary length.\" } }, \"required\": [\"text\"] }"""
//                    )
//                )
//            ),
//            SseMCPServer(
//                id = "server2",
//                name = "Image Processing Dev",
//                url = "mcp://image.dev:8080",
//                status = ServerStatus.OFFLINE,
//                environment = listOf(EnvironmentVariable("DEBUG", "true")),
//                tools = listOf(
//                    MCPTool(
//                        id = "tool3",
//                        name = "Image Resizer",
//                        description = "Resizes an image to specified dimensions.",
//                        parametersJsonSchema = "{\"type\": \"object\", \"properties\": {\"imageUrl\": {\"type\": \"string\", \"description\": \"URL of the image.\"}, \"width\": {\"type\": \"integer\"}, \"height\": {\"type\": \"integer\"}}, \"required\": [\"imageUrl\", \"width\", \"height\"]}"
//                    )
//                )
//            )
//        )
    }


//    private val mockServers: List<MCPServer>
//        get() = _mockServers

    override suspend fun getServers(): List<MCPServer> {
//        println("Getting servers from repository: ${mockServers.size}")
        return MCPDirectStudio.getMCPServers().toList();
//        return mockServers
    }

    override suspend fun getServerDetails(serverName: String): MCPServer? {
        return MCPDirectStudio.getMCPServer(serverName)
//        return mockServers.find { it.name == serverName }
    }

    override suspend fun addServer(server: MCPServer) {
//        _mockServers.add(server)
    }
}
