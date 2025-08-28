package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.studio.dao.entity.MCPServer


interface MCPServerRepository {
    suspend fun getServers(): List<MCPServer>
    suspend fun getServerDetails(serverName: String): MCPServer?
    suspend fun addServer(server: MCPServer)
}
