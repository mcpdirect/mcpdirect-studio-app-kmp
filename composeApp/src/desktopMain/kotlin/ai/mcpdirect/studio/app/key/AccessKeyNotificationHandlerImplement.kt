package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.handler.AccessKeyNotificationHandler
import ai.mcpdirect.studio.handler.ToolAgentsDetailsNotificationHandler
import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolPermission
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolAgent
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker

class AccessKeyNotificationHandlerImplement(private val viewModel: AccessKeyViewModel)
    : AccessKeyNotificationHandler, ToolAgentsDetailsNotificationHandler {

    override fun onAccessKeysNotification(keys: List<AIPortAccessKeyCredential>) {
        viewModel.updateKeys(keys)
    }

    override fun onAccessKeyPermissionsNotification(permissions: List<AIPortToolPermission?>?) {

    }

    override fun onToolAgentsNotification(
        agents: List<AIPortToolAgent>?,
        makers: List<AIPortToolMaker>?,
        tools: List<AIPortTool>?,
        permissions: List<AIPortToolPermission>?,
        localAgent: AIPortToolAgent
    ) {
        viewModel.agents.clear()
        if(agents!=null) viewModel.agents.addAll(agents)
        viewModel.makers.clear()
        if(makers!=null) viewModel.makers.addAll(makers)
        viewModel.tools.clear()
        if(tools!=null) viewModel.tools.addAll(tools)
        viewModel.permissions.clear()
        if(permissions!=null) viewModel.permissions.addAll(permissions)
        viewModel.localAgent.value = localAgent
    }
}