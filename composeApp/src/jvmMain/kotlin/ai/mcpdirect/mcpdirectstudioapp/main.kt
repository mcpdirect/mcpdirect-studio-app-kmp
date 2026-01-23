package ai.mcpdirect.mcpdirectstudioapp

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
import ai.mcpdirect.studio.app.model.aitool.AIPortAppVersion
import ai.mcpdirect.studio.app.model.aitool.AIPortAppVersion.Companion.PLATFORM_MACOS
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerTemplateScreen
import ai.mcpdirect.studio.app.team.TeamScreen
import ai.mcpdirect.studio.app.theme.AppTheme
import ai.mcpdirect.studio.app.tips.QuickStartScreen
import ai.mcpdirect.studio.app.tips.TipsScreen
import ai.mcpdirect.studio.app.tool.MCPToolsScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMCPScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerToolConfigScreen
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import java.awt.Cursor
import java.awt.Frame
import javax.swing.SwingUtilities

fun os(): Int {
    return System.getProperty("os.name").lowercase().let { osName ->
        when {
            osName.contains("win") -> AIPortAppVersion.PLATFORM_WINDOWS
            osName.contains("nix") || osName.contains("nux") -> AIPortAppVersion.PLATFORM_LINUX
            osName.contains("mac") -> AIPortAppVersion.PLATFORM_MACOS
            else -> 0
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun main() = application {
    val windowState = rememberWindowState(
        width = 1280.dp,
        height = 960.dp,
        position = WindowPosition(Alignment.Center)
    )
    val os = os()
//    val version = AppInfo.APP_VERSION
    // undecorated = true removes the OS chrome
    // transparent = true allows us to control the corner radius
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        undecorated = os!=PLATFORM_MACOS,
        transparent = os != PLATFORM_MACOS,
        title = "MCPdirect Studio",
        icon = painterResource(Res.drawable.mcpdirect_logo_48),
    ) {
        var padding by remember { mutableStateOf(if(os==PLATFORM_MACOS) 0.dp else 16.dp) }
        var shadowElevation by remember { mutableStateOf(if(os==PLATFORM_MACOS) 0.dp else 8.dp) }
        LaunchedEffect(Unit) {
            if (os==PLATFORM_MACOS) {
                // macOS 特定设置
                System.setProperty("apple.awt.fullWindowContent", "true")
                System.setProperty("apple.awt.transparentTitleBar", "true")

                // 启用原生窗口控制
                window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
                window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                window.rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
            }
        }
        AppTheme{
            // This Surface is our actual "Window" background

            var maximize by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier.fillMaxSize().padding(padding),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = shadowElevation,
//                shape = if(!maximize&&os==PLATFORM_MACOS) MaterialTheme.shapes.medium else RectangleShape
            ) {
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
                        if(generalViewModel.previousScreen!=null) {
                            TopAppBar(
                                navigationIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                generalViewModel.currentScreen(generalViewModel.previousScreen!!)
                                            }
                                        ) {
                                            Icon(
                                                painterResource(
//                                                    if(generalViewModel.previousScreen==Screen.Home)
//                                                        Res.drawable.home
//                                                    else
                                                        Res.drawable.arrow_back
                                                ),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                },
                                title = {
                                    generalViewModel.currentScreenTitle?.let {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
//                                            Icon(
//                                                painterResource(generalViewModel.currentScreen.icon),
//                                                contentDescription = it
//                                            )
                                            Text(it)
                                        }

                                    }
                                },
                                actions = {
                                    generalViewModel.topBarActions()
                                }
                            )
                            Row(Modifier.padding(top = 62.dp)) {
                                val process = generalViewModel.loadingProcess
                                if(process==0f) LinearProgressIndicator(Modifier.height(2.dp).fillMaxWidth())
                                else if(process<1.0f) LinearProgressIndicator({ process }, Modifier.height(2.dp).fillMaxWidth())
//                                generalViewModel.loadingProcess?.let {
//                                    LinearProgressIndicator({ it }, Modifier.height(2.dp).fillMaxWidth())
//                                }?: LinearProgressIndicator(Modifier.height(2.dp).fillMaxWidth())
                            }
                        }
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
                                    screen.integrationGuide,
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
                                TeamScreen(screen.team,screen.toolMaker,paddingValues)
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
            if(os!=PLATFORM_MACOS)WindowDraggableArea(modifier = Modifier.fillMaxWidth().height(
                if(maximize) 32.dp else 64.dp
            ).padding(padding)) {

                Row(modifier = Modifier.fillMaxWidth()) {
//                    if(os==PLATFORM_MACOS){
////                        val interactionSource = remember { MutableInteractionSource() }
////                        val isHovered by interactionSource.collectIsHoveredAsState()
////                        Box(
////                            modifier = Modifier
////                                .padding(start = 16.dp,top=16.dp)
////                                .size(12.dp)
////                                .clip(CircleShape)
////                                .background(
////                                    color = Color(0xFFF96057)
////                                )
////                                .clickable{
////                                    exitApplication()
////                                }
////                                .hoverable(interactionSource)
////                        ){
////                            Icon(painterResource(Res.drawable.macos_close),contentDescription = null)
////                        }
//                        Spacer(Modifier.width(16.dp))
//                        MacOSWindowButton(
//                            onClick = {exitApplication()},
//                            color = Color(0xFFF96057)
//                        ){
//                            Icon(painterResource(Res.drawable.macos_close),contentDescription = null)
//                        }
//                        Spacer(Modifier.width(8.dp))
////                        Box(
////                            modifier = Modifier
////                                .padding(top = 16.dp)
////                                .size(12.dp)
////                                .clip(CircleShape)
////                                .background(
////                                    color = if(maximize) Color.Gray else Color(0xFFF8BC31)
////                                )
////                                .clickable(!maximize){
////                                    SwingUtilities.invokeLater {
////                                        (window as? Frame)?.extendedState = Frame.ICONIFIED
////                                    }
////                                }
////                        )
//                        MacOSWindowButton(
//                            onClick = {
//                                SwingUtilities.invokeLater {
//                                    (window as? Frame)?.extendedState = Frame.ICONIFIED
//                                }
//                            },
//                            color =  if(maximize) Color.Gray else Color(0xFFF8BC31)
//                        ){
//                            Icon(painterResource(Res.drawable.macos_min),contentDescription = null)
//                        }
//                        Spacer(Modifier.width(8.dp))
////                        Box(
////                            modifier = Modifier
////                                .padding(top = 16.dp)
////                                .size(12.dp)
////                                .clip(CircleShape)
////                                .background(
////                                    color = Color(0xFF44C748)
////                                )
////                                .clickable{
////                                    SwingUtilities.invokeLater {
////                                        if (!maximize) {
////                                            padding = 0.dp
////                                            shadowElevation = 0.dp
////                                            (window as? Frame)?.extendedState = Frame.MAXIMIZED_BOTH
////                                        } else {
////                                            padding = 16.dp
////                                            shadowElevation = 8.dp
////                                            (window as? Frame)?.extendedState = Frame.NORMAL
////                                        }
////                                        maximize = !maximize
////                                    }
////                                }
////                        )
//                        MacOSWindowButton(
//                            onClick = {
//                                SwingUtilities.invokeLater {
//                                    if (!maximize) {
//                                        padding = 0.dp
//                                        shadowElevation = 0.dp
//                                        windowState.placement = WindowPlacement.Fullscreen
////                                        (window as? Frame)?.extendedState = Frame.MAXIMIZED_BOTH
//                                    } else {
//                                        padding = 16.dp
//                                        shadowElevation = 8.dp
//                                        windowState.placement = WindowPlacement.Floating
////                                        (window as? Frame)?.extendedState = Frame.NORMAL
//                                    }
//                                    maximize = !maximize
//                                }
//                            },
//                            color = Color(0xFF44C748)
//                        ){
////                            Icon(painterResource(Res.drawable.macos_max),contentDescription = null)
//                            MacMaximizeIconCanvas(maximize)
//                        }
//                        Row {
//                            // 1. 直角在左下角
//                            Canvas(modifier = Modifier.size(32.dp)) {
//                                drawPath(
//                                    path = Path().apply {
//                                        moveTo(0f, size.height)
//                                        lineTo(0f, 0f)
//                                        lineTo(size.width, size.height)
//                                        close()
//                                    },
//                                    color = Color.Red
//                                )
//                            }
//
//                            // 2. 直角在左上角
//                            Canvas(modifier = Modifier.size(32.dp)) {
//                                drawPath(
//                                    path = Path().apply {
//                                        moveTo(8f, 8f)
//                                        lineTo(size.width-8, 8f)
//                                        lineTo(8f, size.height-8f)
//                                        close()
//                                    },
//                                    color = Color.Blue
//                                )
//                            }
//
//                            // 3. 直角在右下角
//                            Canvas(modifier = Modifier.size(32.dp)) {
//                                drawPath(
//                                    path = Path().apply {
//                                        moveTo(size.width, size.height)
//                                        lineTo(0f, size.height)
//                                        lineTo(size.width, 0f)
//                                        close()
//                                    },
//                                    color = Color.Green
//                                )
//                            }
//
//                            // 4. 直角在右上角
//                            Canvas(modifier = Modifier.size(32.dp)) {
//                                drawPath(
//                                    path = Path().apply {
//                                        moveTo(size.width, 0f)
//                                        lineTo(size.width, size.height)
//                                        lineTo(0f, 0f)
//                                        close()
//                                    },
//                                    color = Color.Yellow
//                                )
//                            }
//                        }
//                    }
                    Spacer(Modifier.weight(1f))
                    if(os!=PLATFORM_MACOS) {
                        IconButton(
                            onClick = {
                                SwingUtilities.invokeLater {
                                    (window as? Frame)?.extendedState = Frame.ICONIFIED
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(painterResource(Res.drawable.check_indeterminate_small), contentDescription = "")
                        }

                        IconButton(
                            onClick = {
                                SwingUtilities.invokeLater {
                                    if (!maximize) {
                                        padding = 0.dp
                                        shadowElevation = 0.dp
                                        (window as? Frame)?.extendedState = Frame.MAXIMIZED_BOTH
                                    } else {
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
                            Icon(painterResource(Res.drawable.close_small), contentDescription = "")
                        }
                    }
                }
            }
        }
        // 3. Resize Logic (Overlay)
        // We pass the AWT window instance to handle the bounds directly
        if(os!=PLATFORM_MACOS)WindowResizeEdges(window)
    }
}

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

@Composable
fun MacOSWindowButton(
    onClick: () -> Unit,
    color: Color,
    icon: @Composable () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .padding(top=16.dp)
            .size(12.dp)
            .clip(CircleShape)
            .background(
                color = color
            )
            .clickable{
                onClick()
            }
            .hoverable(interactionSource)
    ){
        if(isHovered) {icon()}
    }
}

@Composable
fun MacMaximizeIconCanvas(isMaximized: Boolean = false) {
    Canvas(modifier = Modifier.size(12.dp)) {
        // 绿色圆形背景
//        drawCircle(
//            color = Color(0xFF27C93F),
//            radius = size.minDimension / 2.5f,
//            center = center
//        )

        if (isMaximized) {
            drawPath(
                path = Path().apply {
                    moveTo(size.width/2+3f, size.height/2+3f)
                    lineTo(size.width-5, size.height/2+3f)
                    lineTo(size.width/2+3f, size.height-5)
                    close()
                },
                color = Color.Black
            )
            drawPath(
                path = Path().apply {
                    moveTo(size.width/2-1, size.height/2-1)
                    lineTo(2f, size.height/2-1)
                    lineTo(size.width/2-1, 2f)
                    close()
                },
                color = Color.Black
            )
        } else {
            drawPath(
                path = Path().apply {
                    moveTo(3f, 3f)
                    lineTo(size.width-5, 3f)
                    lineTo(3f, size.height-5)
                    close()
                },
                color = Color.Black
            )
            drawPath(
                path = Path().apply {
                    moveTo(size.width-3, size.height-3)
                    lineTo(5f, size.height-3)
                    lineTo(size.width-3, 5f)
                    close()
                },
                color = Color.Black
            )
        }
    }
}