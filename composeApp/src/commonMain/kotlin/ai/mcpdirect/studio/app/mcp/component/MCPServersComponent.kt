package ai.mcpdirect.studio.app.mcp.component

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.search_off
import org.jetbrains.compose.resources.painterResource

class MCPServersComponentViewModel : ViewModel() {
    val toolMakerFilter = MutableStateFlow("")
    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        toolMakerFilter
    ) { makers, toolMakerFilter -> makers.values.filter { maker->
        toolMakerFilter.isEmpty()||maker.name.contains(toolMakerFilter,ignoreCase = true)
    }.sortedBy { it.name } }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}

@Composable
fun MCPServersComponent(
    showMyMCPServerOnly:Boolean=false,
    showVirtualMCP: Boolean = true,
    selectedMCPServers: Set<Long> = setOf(),
    modifier: Modifier = Modifier,
    onMCPServerSelectedChange: (selected:Boolean,toolMaker: AIPortToolMaker) -> Unit,
){
    val viewModel by remember { mutableStateOf(MCPServersComponentViewModel()) }
    val toolMakers by viewModel.toolMakers.collectAsState()
//    val selectedToolMakers by viewModel.selectedToolMakers.collectAsState()
    Column(modifier) {
        StudioActionBar(
            title = "MCP Servers",
        )
//        HorizontalDivider()
        StudioSearchbar(modifier = Modifier.padding(start = 16.dp, end = 16.dp,bottom = 16.dp)) {
            viewModel.toolMakerFilter.value = it
        }
        if(toolMakers.isEmpty()) StudioBoard(Modifier.weight(1f)) {
            Icon(
                painterResource(Res.drawable.search_off),
                contentDescription = "No MCP server found",
                modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
            )
            Text("No MCP server found.")
        } else {
            LazyColumn(Modifier.padding(start=16.dp,end=16.dp)) {
                items(toolMakers){ toolMaker ->
                    if((!showMyMCPServerOnly|| UserRepository.me(toolMaker.userId))&&(showVirtualMCP||toolMaker.type>0)) Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = toolMaker.id in selectedMCPServers,
                            onCheckedChange = {
//                                viewModel.selectToolMaker(it,toolMaker)
                                onMCPServerSelectedChange(it,toolMaker)
                            },
                        )
                        Text(toolMaker.name)
                    }
                }
            }
        }
    }
}