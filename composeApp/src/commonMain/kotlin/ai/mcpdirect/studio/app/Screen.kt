package ai.mcpdirect.studio.app

import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.connect_mcp
import mcpdirectstudioapp.composeapp.generated.resources.dashboard
import mcpdirectstudioapp.composeapp.generated.resources.data_info_alert
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import mcpdirectstudioapp.composeapp.generated.resources.groups
import mcpdirectstudioapp.composeapp.generated.resources.graph_5
import mcpdirectstudioapp.composeapp.generated.resources.handyman
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.key
import mcpdirectstudioapp.composeapp.generated.resources.mcp_keys
import mcpdirectstudioapp.composeapp.generated.resources.my_studio
import mcpdirectstudioapp.composeapp.generated.resources.mcp_team
import mcpdirectstudioapp.composeapp.generated.resources.mcp_tools
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import mcpdirectstudioapp.composeapp.generated.resources.service_toolbox
import mcpdirectstudioapp.composeapp.generated.resources.settings
import mcpdirectstudioapp.composeapp.generated.resources.share
import mcpdirectstudioapp.composeapp.generated.resources.share_mcp_server
import mcpdirectstudioapp.composeapp.generated.resources.share_mcp_template
import mcpdirectstudioapp.composeapp.generated.resources.shield_toggle
import mcpdirectstudioapp.composeapp.generated.resources.table_view
import mcpdirectstudioapp.composeapp.generated.resources.tool_details
import mcpdirectstudioapp.composeapp.generated.resources.tool_development
import mcpdirectstudioapp.composeapp.generated.resources.tool_permission
import mcpdirectstudioapp.composeapp.generated.resources.tools_logbook
import mcpdirectstudioapp.composeapp.generated.resources.user_setting
import mcpdirectstudioapp.composeapp.generated.resources.virtual_mcp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class Screen(var title: StringResource, val icon: DrawableResource) {
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
    object ToolPermission : Screen(Res.string.tool_permission,
        Res.drawable.shield_toggle)
    object MyStudio : Screen(Res.string.connect_mcp,
        Res.drawable.plug_connect)
    object MCPTeam : Screen(Res.string.mcp_team,
        Res.drawable.groups)

    object MCPTeamToolMaker : Screen(Res.string.share_mcp_server,
        Res.drawable.share)

    object MCPTeamToolMakerTemplate : Screen(Res.string.share_mcp_template,
        Res.drawable.share)

    object MCPAccessKey : Screen(Res.string.mcp_keys,
        Res.drawable.key)

    object VirtualMCP : Screen(
        Res.string.virtual_mcp,
        Res.drawable.design_services)
    object VirtualMCPToolConfig : Screen(
        Res.string.virtual_mcp,
        Res.drawable.design_services)
    object ToolDetails: Screen(
        Res.string.tool_details,
        Res.drawable.info)
    object Dashboard: Screen(
        Res.string.dashboard,
        Res.drawable.dashboard)
    object MCPTools: Screen(
        Res.string.mcp_tools,
        Res.drawable.service_toolbox)
}