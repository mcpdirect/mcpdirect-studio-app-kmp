package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun VirtualMCPWidget(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val toolMakers by viewModel.virtualToolMakers.collectAsState()
//    val scrollState = rememberScrollState()
    val gridState = rememberLazyGridState()
//    LaunchedEffect(viewModel) {
//        viewModel.refreshToolMakers(true)
//    }
    Column(modifier.padding(start =16.dp,end = 5.dp,bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.virtual_machine),
                ""
            )
            Text("Virtual MCP (${toolMakers.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.VirtualMCP(),
                        "Create Virtual MCP",
                        Screen.Home
                    )
                }
            ) {
                Icon(
                    painterResource(Res.drawable.add),
                    contentDescription = ""
                )
            }
        }
        StudioSearchbar(modifier=Modifier.padding(end=11.dp)) {
            viewModel.virtualToolMakerFilter.value = it
        }
        if (toolMakers.isNotEmpty()) {
            Box(Modifier.weight(1f).padding(top = 8.dp)) {
                BoxWithConstraints(
                    modifier = Modifier
//                        .verticalScroll(scrollState)
//                        .padding(end = 11.dp)
                        .fillMaxSize()
                ) {
                    // 根据宽度计算每行最大项目数
                    val maxItemsInEachRow = remember(maxWidth) {
                        when {
                            maxWidth < 400.dp -> 1  // 窄屏幕：1列
                            maxWidth < 600.dp -> 2  // 中等屏幕：2列
                            maxWidth < 900.dp -> 3  // 宽屏幕：3列
                            maxWidth < 1200.dp -> 4
                            else -> 5               // 超宽屏幕：5列
                        }
                    }
//                    FlowRow(
////                        Modifier.verticalScroll(scrollState).padding( end = 11.dp),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp),
//                        maxItemsInEachRow = maxItemsInEachRow
//                    ) {
//                        toolMakers.forEach { toolMaker ->
//                            if(UserRepository.me(toolMaker.userId)) {
//                                VirtualToolMakerCard(toolMaker,Modifier.weight(1f).height(120.dp))
//                            }else{
//                                TeamVirtualToolMakerCard(toolMaker,Modifier.weight(1f).height(120.dp))
//                            }
//
//                        }
//                    }
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(maxItemsInEachRow), // 固定2列
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 11.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        contentPadding = PaddingValues(bottom = 8.dp) // 底部内边距
                    ){
                        items(toolMakers) { toolMaker ->
                            if(UserRepository.me(toolMaker.userId)) {
                                VirtualToolMakerCard(toolMaker,Modifier.height(120.dp))
                            }else{
                                TeamVirtualToolMakerCard(toolMaker,Modifier.height(120.dp))
                            }

                        }
                    }
                }
//                VerticalScrollbar(
//                    modifier = Modifier.align(Alignment.CenterEnd),
//                    adapter = rememberScrollbarAdapter(scrollState = scrollState)
//                )
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(scrollState = gridState)
                )
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button({
                generalViewModel.currentScreen(
                    Screen.MyStudio(),
                    "Create Virtual MCP",
                    Screen.Home
                )
            }){
                Text("Create Virtual MCP")
            }
        }
    }
}

@Composable
fun VirtualToolMakerCard(
    toolMaker: AIPortToolMaker,
    modifier: Modifier
){
    OutlinedCard(modifier) {
        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                toolMaker.name,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.VirtualMCP(toolMaker),
                        "Virtual MCP",
                        Screen.Home
                    )
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painterResource(Res.drawable.setting_config),
                    "Virtual MCP",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.weight(1.0f))
        HorizontalDivider()
        Row(
            Modifier.padding(start=8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(painterResource(Res.drawable.person), contentDescription = null, Modifier.size(20.dp))
            Text(
                "Me",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamVirtualToolMakerCard(
    toolMaker: AIPortToolMaker,
    modifier: Modifier
){
    var team by remember { mutableStateOf<AIPortTeam?>(null) }
    var teams by remember { mutableStateOf<List<AIPortTeam>>(emptyList()) }
    LaunchedEffect(null) {
        TeamRepository.getTeams(toolMaker){
            if(it.successful()) it.data?.let { data ->
                if(data.isNotEmpty()) team = data[0]
                teams = data
            }
        }
    }
    OutlinedCard(modifier) {
        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                toolMaker.name,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MCPTeam(team,listOf(toolMaker)),
                        "My Teams",
                        Screen.Home
                    )
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painterResource(Res.drawable.setting_config),
                    "Team",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.weight(1.0f))
        HorizontalDivider()
        team?.let { t1 ->
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                Modifier.padding(start=8.dp),
            ) {
                Row(
                    Modifier.padding(vertical = 4.dp).clickable { expanded = !expanded },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(painterResource(Res.drawable.group), contentDescription = null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        t1.name,style = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    teams.forEach { t2 ->
                        DropdownMenuItem(
                            text = { Text(t2.name, style = MaterialTheme.typography.bodySmall) },
                            onClick = {
                                expanded = false
                                team = t2
                            },
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}