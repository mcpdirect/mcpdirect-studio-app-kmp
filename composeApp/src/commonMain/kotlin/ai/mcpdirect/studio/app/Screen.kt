package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.app.agent.MyStudioScreenDialog
import ai.mcpdirect.studio.app.mcpkey.MCPKeyDialog
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.team.MCPTeamDialog
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class Screen(open val title: StringResource, open val icon: DrawableResource) {
    object ToolDevelopment : Screen(Res.string.tool_development,
        Res.drawable.handyman)
    object ConnectMCP : Screen(Res.string.connect_mcp,
        Res.drawable.plug_connect)
    object ToolsLogbook : Screen(Res.string.tools_logbook,
        Res.drawable.data_info_alert)
    //    object AgentInteraction : Screen(Res.string.mcp_keys,
//        Res.drawable.key)
    object UserSetting : Screen(Res.string.user_setting,
        Res.drawable.settings)
//    object ToolPermission : Screen(Res.string.tool_permission,
//        Res.drawable.shield_toggle)

    data class ToolPermission(val accessKey: AIPortToolAccessKey) : Screen(Res.string.tool_permission,
        Res.drawable.shield_toggle)

//    object MyStudio : Screen(Res.string.connect_mcp,
//        Res.drawable.plug_connect)
//    object MCPTeam : Screen(Res.string.mcp_team,
//        Res.drawable.groups)

    data class MCPTeam(
        val dialog: MCPTeamDialog = MCPTeamDialog.None
    ) : Screen(Res.string.mcp_team,
        Res.drawable.groups)

//    object MCPTeamToolMaker : Screen(Res.string.share_mcp_server,
//        Res.drawable.share)

    data class MCPTeamToolMaker(val team: AIPortTeam) : Screen(Res.string.share_mcp_server,
        Res.drawable.share)
//    object MCPTeamToolMakerTemplate : Screen(Res.string.share_mcp_template,
//        Res.drawable.share)

    data class MCPTeamToolMakerTemplate(val team: AIPortTeam) : Screen(Res.string.share_mcp_template,
        Res.drawable.share)

//    object MCPAccessKey : Screen(Res.string.mcp_keys,
//        Res.drawable.key)
    data class MCPAccessKey(
        val accessKey: AIPortToolAccessKey?=null,
        val dialog: MCPKeyDialog = MCPKeyDialog.None
    ):Screen(Res.string.mcp_keys,Res.drawable.key)

    object VirtualMCP : Screen(
        Res.string.virtual_mcp,
        Res.drawable.design_services)
    object VirtualMCPToolConfig : Screen(
        Res.string.virtual_mcp,
        Res.drawable.design_services)
//    object ToolDetails: Screen(
//        Res.string.tool_details,
//        Res.drawable.info)
    object Dashboard: Screen(
        Res.string.dashboard,
        Res.drawable.dashboard)
    object MCPTools: Screen(
        Res.string.mcp_tools,
        Res.drawable.service_toolbox)
//    object OpenAPIMCP: Screen(
//        Res.string.open_api_mcp,
//        Res.drawable.openapi)
    data class MyStudio(
        val toolAgent: AIPortToolAgent?=null,
        val toolMaker: AIPortToolMaker?=null,
        val dialog: MyStudioScreenDialog = MyStudioScreenDialog.None
    ) : Screen(Res.string.connect_mcp, Res.drawable.plug_connect)
    object Tips : Screen(Res.string.tips, Res.drawable.lightbulb_2)
    object QuickStart : Screen(Res.string.tips, Res.drawable.lightbulb_2)
}