package ai.mcpdirect.mcpdirectstudioapp

//import androidx.compose.ui.Alignment
//import androidx.compose.ui.unit.DpSize
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Window
//import androidx.compose.ui.window.WindowPlacement
//import androidx.compose.ui.window.WindowPosition
//import androidx.compose.ui.window.application
//import androidx.compose.ui.window.rememberWindowState
//import mcpdirectstudioapp.composeapp.generated.resources.Res
//import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_logo_48
//import org.jetbrains.compose.resources.painterResource
//
//fun main(args: Array<String>) = application {
//    var mcpdirect = false;
//    var mcpdirectKey:String? = null
//    for (arg in args) {
//        if(mcpdirect) {
//            mcpdirectKey = arg
//            break;
//        }
//        if(arg=="--mcpdirect") mcpdirect=true
//    }
//    if(mcpdirectKey!=null){
//        //TODO launch mcpdirect proxy
//    }else {
//        val windowState = rememberWindowState(
//            size = DpSize(1200.dp, 900.dp),
//            position = WindowPosition.Aligned(Alignment.Center),
//        )
//        val version = AppInfo.APP_VERSION
//        Window(
//            onCloseRequest = ::exitApplication,
//            title = "MCPdirect Studio $version",
//            state = windowState,
//            icon = painterResource(Res.drawable.mcpdirect_logo_48),
////            undecorated = true,
////            transparent = true,
//        ) {
//            App()
//        }
//    }
//}

//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Surface
//import androidx.compose.material.Text

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.ToolAgentScreen
import ai.mcpdirect.studio.app.auth.*
import ai.mcpdirect.studio.app.compose.Carousel
import ai.mcpdirect.studio.app.compose.CarouselSlide
import ai.mcpdirect.studio.app.dashboard.DashboardScreen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeScreen
import ai.mcpdirect.studio.app.key.MCPdirectKeyScreen
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.team.MCPTeamScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerTemplateScreen
import ai.mcpdirect.studio.app.team.TeamScreen
import ai.mcpdirect.studio.app.theme.AppTheme
import ai.mcpdirect.studio.app.tips.QuickStartScreen
import ai.mcpdirect.studio.app.tips.TipsScreen
import ai.mcpdirect.studio.app.tool.MCPToolsScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMCPScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerToolConfigScreen
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import java.awt.Cursor
import java.awt.Frame
import javax.swing.SwingUtilities

@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {
    val windowState = rememberWindowState(
        width = 1280.dp,
        height = 960.dp,
        position = WindowPosition(Alignment.Center)
    )

    val version = AppInfo.APP_VERSION
    // undecorated = true removes the OS chrome
    // transparent = true allows us to control the corner radius
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        undecorated = true,
        transparent = true,
        title = "MCPdirect Studio $version",
        icon = painterResource(Res.drawable.mcpdirect_logo_48),
    ) {
        AppTheme{
            // This Surface is our actual "Window" background
            var padding by remember { mutableStateOf(16.dp) }
            var shadowElevation by remember { mutableStateOf(8.dp) }
            var maximize by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier.fillMaxSize().padding(padding),
                color = MaterialTheme.colorScheme.background,
//                shape = RoundedCornerShape(8.dp,8.dp,0.dp,0.dp),
//            border = BorderStroke(1.dp,MaterialTheme.colorScheme.onBackground),
//            tonalElevation = 8.dp,
                shadowElevation = shadowElevation
            ) {
//                println(MaterialTheme.colorScheme.background.value.toHexString())
//                WindowDraggableArea(modifier = Modifier.fillMaxSize()) {
                    Scaffold (
                        snackbarHost = { SnackbarHost(generalViewModel.snackbarHostState){ data ->
                            var containerColor = SnackbarDefaults.color
                            var contentColor = SnackbarDefaults.contentColor
                            var actionColor = SnackbarDefaults.actionColor
                            var actionContentColor = SnackbarDefaults.actionContentColor
                            var dismissActionContentColor = SnackbarDefaults.dismissActionContentColor
                            data.visuals.actionLabel?.let { label->
                                when(label.lowercase()) {
                                    "error" -> {
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                        contentColor = MaterialTheme.colorScheme.error
                                        actionColor = MaterialTheme.colorScheme.error
                                        actionContentColor = MaterialTheme.colorScheme.error
                                        dismissActionContentColor = MaterialTheme.colorScheme.error
                                    }
                                    else -> {}
                                }
                            }
                            Snackbar(
                                data,
                                containerColor = containerColor,
                                contentColor = contentColor,
                                actionColor = actionColor,
                                actionContentColor = actionContentColor,
                                dismissActionContentColor = dismissActionContentColor,
                            )
                        } },
                        topBar = {
                            if(generalViewModel.previousScreen!=null) TopAppBar(
                                navigationIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically){
                                        IconButton(
                                            onClick = {
                                                generalViewModel.currentScreen(generalViewModel.previousScreen!!)
                                            }
                                        ) {
                                            Icon(
                                                painterResource(Res.drawable.arrow_back),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                },
                                title = { generalViewModel.currentScreenTitle?.let {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ){
                                        Icon(
                                            painterResource(generalViewModel.currentScreen.icon),
                                            contentDescription = it
                                        )
                                        Text(it)
                                    }

                                } },
                                actions = {
                                    generalViewModel.topBarActions()
                                }
                            )
                        }
                    ){ paddingValues ->
                        if(authViewModel.uiState == UIState.Success) {
                            when (val screen = generalViewModel.currentScreen) {
                                Screen.Home -> {
                                    HomeScreen()
                                }
                                Screen.Tips -> {
                                    TipsScreen()
                                }
                                Screen.Dashboard -> {
                                    DashboardScreen()
                                }
                                Screen.ToolDevelopment -> {}
                                Screen.ConnectMCP -> {}
                                is Screen.MCPAccessKey -> {
//                                    MCPAccessKeyScreen(
//                                        screen.accessKey,
//                                        screen.dialog,
//                                        paddingValues
//                                    )
                                    MCPdirectKeyScreen(
                                        screen.accessKey,
                                        paddingValues
                                    )
                                }
                                Screen.ToolsLogbook -> {}
                                Screen.UserSetting -> {
                                    SettingsScreen()
                                }
                                is Screen.ToolPermission -> {
                                    ToolPermissionScreen(screen.accessKey)
                                }
                                is Screen.MyStudio -> {
//                                    MyStudioScreen(
//                                        screen.toolAgent,
//                                        screen.toolMaker,
//                                        screen.dialog,
//                                        paddingValues
//                                    )
                                    ToolAgentScreen(
                                        screen.toolAgent,
                                        screen.toolMaker,
                                        paddingValues
                                    )
                                }
                                is Screen.MCPTeam -> {
//                                    MCPTeamScreen(screen.dialog,paddingValues)
                                    TeamScreen(screen.team,paddingValues)
                                }
                                is Screen.MCPTeamToolMaker -> {
                                    MCPTeamToolMakerScreen(screen.team)
                                }
                                is Screen.MCPTeamToolMakerTemplate -> {
                                    MCPTeamToolMakerTemplateScreen(screen.team)
                                }
                                is Screen.VirtualMCP -> {
//                                    VirtualMakerScreen()
                                    VirtualMCPScreen(screen.toolMaker,paddingValues)
                                }
                                Screen.VirtualMCPToolConfig -> {
                                    VirtualMakerToolConfigScreen()
                                }
//                        Screen.ToolDetails -> {
//                            ToolDetailScreen()
//                        }
                                Screen.MCPTools -> {
                                    MCPToolsScreen()
                                }
//                        Screen.OpenAPIMCP -> {
//                            OpenAPIToolMakerScreen()
//                        }
                                Screen.QuickStart ->{
                                    QuickStartScreen(paddingValues)
                                }
                            }
                        } else Row(
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            val slides = listOf(
                                CarouselSlide(
                                    imageResource = Res.drawable.mcpdirect_tips_one_url, // Platform-specific
                                    description = "One URL access your any in-house MCP Servers",
                                    title = ""
                                ),
                                CarouselSlide(
                                    imageResource = Res.drawable.mcpdirect_tips_openapi,
                                    description = "Zero code to connect OpenAPI as MCP Tools",
                                    title = ""
                                ),
                                CarouselSlide(
                                    imageResource = Res.drawable.mcpdirect_tips_share_tools,
                                    description = "Share MCP tools with your team",
                                    title = ""
                                )
                            )
                            Carousel(
                                "Let MCP power your business",
                                slides,
                                Modifier.width(800.dp),
                            )
                            VerticalDivider()
                            when(authViewModel.currentScreen){
                                AuthScreen.Login -> {
                                    LoginScreen()
                                }
                                AuthScreen.Register ->{
                                    RegisterScreen()
                                }
                                AuthScreen.RegisterOtpVerification -> {
                                    RegisterOtpVerificationScreen()
                                }
                                AuthScreen.ForgotPassword -> {
                                    ForgotPasswordScreen()
                                }
                                AuthScreen.ForgotPasswordOtpVerification -> {
                                    ForgotPasswordOtpVerificationScreen()
                                }
                                else -> {

                                }
                            }
                        }
                }
            }
            WindowDraggableArea(modifier = Modifier.fillMaxWidth().height(
                if(maximize) 32.dp else 64.dp
            ).padding(padding)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            SwingUtilities.invokeLater {
                                (window as? Frame)?.extendedState = Frame.ICONIFIED
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(painterResource(Res.drawable.check_indeterminate_small),contentDescription = "")
                    }

                    IconButton(
                        onClick = {
                            SwingUtilities.invokeLater {
                                if(!maximize){
                                    padding = 0.dp
                                    shadowElevation = 0.dp
                                    (window as? Frame)?.extendedState = Frame.MAXIMIZED_BOTH
                                }else{
                                    padding = 16.dp
                                    shadowElevation = 8.dp
                                    (window as? Frame)?.extendedState = Frame.NORMAL
                                }
                                maximize = !maximize
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painterResource(Res.drawable.check_box_outline_blank),
                            contentDescription = "",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { exitApplication() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(painterResource(Res.drawable.close_small),contentDescription = "")
                    }
                }
            }
        }
        // 3. Resize Logic (Overlay)
        // We pass the AWT window instance to handle the bounds directly
        WindowResizeEdges(window)
    }
}

// --- Composable: Custom Title Bar ---
//@Composable
//fun WindowScope.AppTitleBar(
//    window: ComposeWindow,
//    onClose: () -> Unit,
//    onMinimize: () -> Unit
//) {
//    val osName = System.getProperty("os.name").lowercase()
//    val isMac = osName.contains("mac")
//
//    val buttonContent = @Composable {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            IconButton(onClick = onMinimize, modifier = Modifier.size(40.dp)) {
//                Icon(painterResource(Res.drawable.check), contentDescription = "Minimize")
//            }
//            IconButton(onClick = onClose, modifier = Modifier.size(40.dp)) {
//                Icon(painterResource(Res.drawable.close), contentDescription = "Close")
//            }
//        }
//    }
//
//    // Now WindowDraggableArea is accessible because we are inside WindowScope
//    WindowDraggableArea(modifier = Modifier.fillMaxWidth().height(48.dp).background(Color.LightGray)) {
//        Row(
//            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (isMac) {
//                buttonContent()
//                Spacer(Modifier.width(10.dp))
//                Text("My App", modifier = Modifier.weight(1f))
//            } else {
//                Text("My App", modifier = Modifier.weight(1f))
//                Spacer(Modifier.width(10.dp))
//                buttonContent()
//            }
//        }
//    }
//}

// --- Composable: Resize Logic ---
@Composable
fun WindowResizeEdges(window: ComposeWindow) {
    val edgeSize = 8.dp // The thickness of the invisible resize hit-boxes

    Box(modifier = Modifier.fillMaxSize()) {
        // Top
        ResizeEdge(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(edgeSize),
            cursor = Cursor.N_RESIZE_CURSOR,
            onDrag = { _, dy -> window.setBounds(window.x, window.y + dy.toInt(), window.width, window.height - dy.toInt()) }
        )
        // Bottom
        ResizeEdge(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(edgeSize),
            cursor = Cursor.S_RESIZE_CURSOR,
            onDrag = { _, dy -> window.setSize(window.width, window.height + dy.toInt()) }
        )
        // Left
        ResizeEdge(
            modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight().width(edgeSize),
            cursor = Cursor.W_RESIZE_CURSOR,
            onDrag = { dx, _ -> window.setBounds(window.x + dx.toInt(), window.y, window.width - dx.toInt(), window.height) }
        )
        // Right
        ResizeEdge(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(edgeSize),
            cursor = Cursor.E_RESIZE_CURSOR,
            onDrag = { dx, _ -> window.setSize(window.width + dx.toInt(), window.height) }
        )

        // Corners
        // Top-Left
        ResizeEdge(
            modifier = Modifier.align(Alignment.TopStart).size(edgeSize),
            cursor = Cursor.NW_RESIZE_CURSOR,
            onDrag = { dx, dy -> window.setBounds(window.x + dx.toInt(), window.y + dy.toInt(), window.width - dx.toInt(), window.height - dy.toInt()) }
        )
        // Top-Right
        ResizeEdge(
            modifier = Modifier.align(Alignment.TopEnd).size(edgeSize),
            cursor = Cursor.NE_RESIZE_CURSOR,
            onDrag = { dx, dy -> window.setBounds(window.x, window.y + dy.toInt(), window.width + dx.toInt(), window.height - dy.toInt()) }
        )
        // Bottom-Left
        ResizeEdge(
            modifier = Modifier.align(Alignment.BottomStart).size(edgeSize),
            cursor = Cursor.SW_RESIZE_CURSOR,
            onDrag = { dx, dy -> window.setBounds(window.x + dx.toInt(), window.y, window.width - dx.toInt(), window.height + dy.toInt()) }
        )
        // Bottom-Right
        ResizeEdge(
            modifier = Modifier.align(Alignment.BottomEnd).size(edgeSize),
            cursor = Cursor.SE_RESIZE_CURSOR,
            onDrag = { dx, dy -> window.setSize(window.width + dx.toInt(), window.height + dy.toInt()) }
        )
    }
}

@Composable
fun ResizeEdge(
    modifier: Modifier,
    cursor: Int,
    onDrag: (dx: Float, dy: Float) -> Unit
) {
    Box(
        modifier = modifier
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(cursor)))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
    )
}