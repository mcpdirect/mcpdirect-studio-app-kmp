package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.virtualmcp.VirtualToolMakerListView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToolListView(
    viewModel: ToolListViewModel,
    actionBar: (@Composable (() -> Unit))?=null,
    leadingContent: (@Composable ((AIPortTool) -> Unit))?=null
){
    val toolMaker by viewModel.toolMaker.collectAsState()
    if(toolMaker!=null) {
        val tools by viewModel.tools.collectAsState()
        Column {
            actionBar?.let {
                it()
                HorizontalDivider()
            }
            Row {
                LazyColumn(Modifier.weight(2.0f)) {
                    items(tools) {
                        ListItem(
                            modifier = Modifier.clickable {
                                viewModel.tool(it)
                            },
                            leadingContent = if (leadingContent != null) {
                                { leadingContent(it) }
                            } else null,
                            headlineContent = { Text(it.name) },
                            trailingContent = {
                                viewModel.tool?.let { tool ->
                                    if (tool.id == it.id) Icon(
                                        painterResource(Res.drawable.keyboard_arrow_right),
                                        contentDescription = "Details"
                                    )
                                }
                            }
                        )
                    }
                }
                viewModel.tool?.let {
                    val json = JSON.parseToJsonElement(it.metaData)
                    val description = json.jsonObject["description"]?.jsonPrimitive?.content
                    val prettyJson = Json { prettyPrint = true }
                    val requestSchema = prettyJson.encodeToString(
                        JSON.parseToJsonElement(json.jsonObject["requestSchema"]?.jsonPrimitive?.content ?: "{}")
                    )
                    var currentTabIndex by remember { mutableStateOf(0) }
                    VerticalDivider()
                    Column(Modifier.weight(3.0f)) {

                        val tabs = listOf("Description", "Input Schema")
                        SecondaryTabRow(selectedTabIndex = currentTabIndex) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = currentTabIndex == index,
                                    onClick = {
                                        currentTabIndex = index
                                    },
                                    text = { Text(title) }
                                )
                            }
                        }
                        val scrollState = rememberScrollState()
                        // Content based on selected tab
                        when (currentTabIndex) {
                            0 -> Text(
                                text = description ?: "",
                                modifier = Modifier.padding(8.dp).fillMaxSize().verticalScroll(scrollState)
                            )

                            1 -> Text(
                                text = requestSchema,
                                modifier = Modifier.padding(8.dp).fillMaxSize().verticalScroll(scrollState)
                            )
                        }
                    }
                }
            }
        }
    } else StudioBoard {
        Text("Select a MCP Server to view")
    }
}