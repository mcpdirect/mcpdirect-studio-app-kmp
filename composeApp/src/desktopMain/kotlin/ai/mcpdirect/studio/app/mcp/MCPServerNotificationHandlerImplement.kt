package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.studio.dao.entity.MCPServer
import ai.mcpdirect.studio.handler.MCPServerNotificationHandler

class MCPServerNotificationHandlerImplement(private val viewModel: MCPServerIntegrationViewModel) : MCPServerNotificationHandler {

    override fun onMCPServersNotification(servers: List<MCPServer>) {
        viewModel.updateServer(servers)
    }

    override fun onLocalMCPServersNotification(servers: List<MCPServer>) {
        viewModel.updateLocalServer(servers)
    }
}