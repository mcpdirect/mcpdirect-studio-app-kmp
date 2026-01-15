package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_down
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import org.jetbrains.compose.resources.painterResource

@Composable
fun JsonTreeView(
    jsonString: String,
    modifier: Modifier = Modifier,
    filterable: Boolean = false,
    onNodeSelected: (String, Any?) -> Unit = { _, _ -> }
) {
    val parser = remember { JsonTreeParser() }
    val nodes = remember(jsonString) { parser.parseJson(jsonString) }
    var expandedPaths by remember { mutableStateOf(emptySet<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        // Search Bar
        if(filterable) OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search JSON") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Tree View
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val filteredNodes = if (searchQuery.isNotBlank()) {
                parser.filterNodes(nodes, searchQuery)
            } else {
                nodes
            }

            items(filteredNodes) { node ->
                JsonTreeNodeItem(
                    node = node,
                    depth = 0,
                    expandedPaths = expandedPaths,
                    onExpandedChange = { path ->
                        expandedPaths = if (expandedPaths.contains(path)) {
                            expandedPaths - path
                        } else {
                            expandedPaths + path
                        }
                    },
                    onNodeSelected = onNodeSelected
                )
            }
        }
    }
}

@Composable
fun JsonTreeNodeItem(
    node: JsonTreeNode,
    depth: Int,
    expandedPaths: Set<String>,
    onExpandedChange: (String) -> Unit,
    onNodeSelected: (String, Any?) -> Unit,
    path: String = ""
) {
    val currentPath = if (path.isEmpty()) node.key else "$path.${node.key}"
    val isExpanded = node.key=="root"||expandedPaths.contains(currentPath)
    val hasChildren = when (node) {
        is JsonTreeNode.ObjectNode -> node.children.isNotEmpty()
        is JsonTreeNode.ArrayNode -> node.children.isNotEmpty()
        else -> false
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNodeSelected(currentPath, node.value)
                    if (hasChildren) {
                        onExpandedChange(currentPath)
                    }
                }
                .padding(start = (depth * 16).dp, top = 4.dp, bottom = 4.dp),
//            verticalAlignment = Alignment.CenterVertically
        ) {
            // Expand/collapse icon
            if (hasChildren) {
                Icon(
                    painterResource(if (isExpanded) Res.drawable.keyboard_arrow_down
                    else Res.drawable.keyboard_arrow_right),
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }

            // Key
            Text(
                text = "${node.key} : ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Type and value
            Text(
                text = when (node) {
                    is JsonTreeNode.ObjectNode -> "{${node.children.size}}"
                    is JsonTreeNode.ArrayNode -> "[${node.children.size}]"
                    is JsonTreeNode.PrimitiveNode -> {
                        when (node.value) {
                            null -> "null"
                            is String -> node.value
                            else -> node.value.toString()
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when (node.type) {
                    "string" -> MaterialTheme.colorScheme.secondary
                    "number" -> MaterialTheme.colorScheme.tertiary
                    "boolean" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }

        // Children
        if (isExpanded && hasChildren) {
            val children = when (node) {
                is JsonTreeNode.ObjectNode -> node.children
                is JsonTreeNode.ArrayNode -> node.children
                else -> emptyList()
            }

            children.forEach { child ->
                JsonTreeNodeItem(
                    node = child,
                    depth = depth + 1,
                    expandedPaths = expandedPaths,
                    onExpandedChange = onExpandedChange,
                    onNodeSelected = onNodeSelected,
                    path = currentPath
                )
            }
        }
    }
}
@Serializable
sealed class JsonTreeNode {
    abstract val key: String
    abstract val value: Any?
    abstract val type: String
    abstract val isExpanded: Boolean

    data class ObjectNode(
        override val key: String,
        val children: List<JsonTreeNode> = emptyList(),
        override val isExpanded: Boolean = false
    ) : JsonTreeNode() {
        override val value: Any? = null
        override val type: String = "object"
    }

    data class ArrayNode(
        override val key: String,
        val children: List<JsonTreeNode> = emptyList(),
        override val isExpanded: Boolean = false
    ) : JsonTreeNode() {
        override val value: Any? = null
        override val type: String = "array"
    }

    data class PrimitiveNode(
        override val key: String,
        override val value: Any?,
        override val isExpanded: Boolean = false
    ) : JsonTreeNode() {
        override val type: String = when (value) {
            is String -> "string"
            is Number -> "number"
            is Boolean -> "boolean"
            null -> "null"
            else -> "unknown"
        }
    }
}

data class TreeViewState(
    val nodes: List<JsonTreeNode> = emptyList(),
    val selectedPath: String? = null,
    val searchQuery: String = ""
)

class JsonTreeParser {

    fun parseJson(jsonString: String): List<JsonTreeNode> {
        return try {
            val jsonElement = Json.parseToJsonElement(jsonString)
            listOf(parseElement("root", jsonElement))
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseElement(key: String, element: JsonElement): JsonTreeNode {
        return when (element) {
            is JsonObject -> {
                val children = element.entries.map { (childKey, childElement) ->
                    parseElement(childKey, childElement)
                }
                JsonTreeNode.ObjectNode(key, children)
            }

            is JsonArray -> {
                val children = element.mapIndexed { index, childElement ->
                    parseElement("[$index]", childElement)
                }
                JsonTreeNode.ArrayNode(key, children)
            }

            is JsonPrimitive -> {
                JsonTreeNode.PrimitiveNode(
                    key = key,
                    value = when {
                        element.isString -> element.content
                        element.booleanOrNull != null -> element.boolean
                        element.doubleOrNull != null -> element.double
                        element.longOrNull != null -> element.long
                        else -> element.content
                    }
                )
            }

            JsonNull -> JsonTreeNode.PrimitiveNode(key, null)
        }
    }

    fun filterNodes(
        nodes: List<JsonTreeNode>,
        query: String
    ): List<JsonTreeNode> {
        if (query.isBlank()) return nodes

        return nodes.flatMap { node ->
            filterNode(node, query.lowercase())
        }
    }

    private fun filterNode(node: JsonTreeNode, query: String): List<JsonTreeNode> {
        val matches = mutableListOf<JsonTreeNode>()

        if (node.key.contains(query,ignoreCase = true) ||
            node.value.toString().contains(query,ignoreCase = true)) {
            matches.add(node)
        }

        when (node) {
            is JsonTreeNode.ObjectNode -> {
                node.children.flatMapTo(matches) { child ->
                    filterNode(child, query)
                }
            }
            is JsonTreeNode.ArrayNode -> {
                node.children.flatMapTo(matches) { child ->
                    filterNode(child, query)
                }
            }
            else -> {}
        }

        return matches
    }
}