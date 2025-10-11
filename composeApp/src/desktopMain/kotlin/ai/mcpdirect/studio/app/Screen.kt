package ai.mcpdirect.studio.app

import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.connect_mcp
import mcpdirectstudioapp.composeapp.generated.resources.data_info_alert
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import mcpdirectstudioapp.composeapp.generated.resources.diversity_3
import mcpdirectstudioapp.composeapp.generated.resources.graph_2
import mcpdirectstudioapp.composeapp.generated.resources.handyman
import mcpdirectstudioapp.composeapp.generated.resources.key
import mcpdirectstudioapp.composeapp.generated.resources.mcp_keys
import mcpdirectstudioapp.composeapp.generated.resources.my_studio
import mcpdirectstudioapp.composeapp.generated.resources.my_team
import mcpdirectstudioapp.composeapp.generated.resources.settings
import mcpdirectstudioapp.composeapp.generated.resources.shield_toggle
import mcpdirectstudioapp.composeapp.generated.resources.tool_development
import mcpdirectstudioapp.composeapp.generated.resources.tool_permission
import mcpdirectstudioapp.composeapp.generated.resources.tools_logbook
import mcpdirectstudioapp.composeapp.generated.resources.usb
import mcpdirectstudioapp.composeapp.generated.resources.user_setting
import mcpdirectstudioapp.composeapp.generated.resources.virtual_mcp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class Screen(val title: StringResource, val icon: DrawableResource) {
    object ToolDevelopment : Screen(Res.string.tool_development,
        Res.drawable.handyman)
    object MCPServerIntegration : Screen(Res.string.connect_mcp,
        Res.drawable.usb)
    object ToolsLogbook : Screen(Res.string.tools_logbook,
        Res.drawable.data_info_alert)
    //    object AgentInteraction : Screen(Res.string.mcp_keys,
//        Res.drawable.key)
    object UserSetting : Screen(Res.string.user_setting,
        Res.drawable.settings)
    object ToolPermission : Screen(Res.string.tool_permission,
        Res.drawable.shield_toggle)
    object MyStudio : Screen(Res.string.my_studio,
        Res.drawable.design_services)
    object MyTeam : Screen(Res.string.my_team,
        Res.drawable.diversity_3)

    object MCPAccessKey : Screen(Res.string.mcp_keys,
        Res.drawable.key)

    object VirtualMCP : Screen(
        Res.string.virtual_mcp,
        Res.drawable.graph_2)
    object VirtualMCPToolConfig : Screen(
        Res.string.virtual_mcp,
        Res.drawable.graph_2)
}