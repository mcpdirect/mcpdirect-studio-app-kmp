package ai.mcpdirect.studio.app.mcp
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.cancel
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditMCPServerTagsDialog(
    toolMaker: AIPortToolMaker,
    onConfirmRequest: (AIPortToolMaker,String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val tagFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()
    var serverTag by remember { mutableStateOf("") }
    var serverTagErrors by remember { mutableStateOf(false) }
    val serverTags = remember { mutableStateSetOf<String>() }
    fun onServerTagChange(tag: String){
//        val text = tag.trim();
        serverTagErrors = tag.isBlank()&&tag.length<33
        if(!serverTagErrors) {
            if(tag.contains(",")){
                serverTag = ""
                tag.split(",").forEach {
                    val text = it.trim()
                    if(text.isNotBlank())
                        serverTags.add(it.trim())
                    println(serverTags.joinToString())
                }
            }else serverTag = tag

        }
    }

    fun removeServerTag(tag:String){
        serverTags.remove(tag)
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Column {
            Text("Edit MCP Server Tags")
            Text("of ${toolMaker.name}", style = MaterialTheme.typography.titleLarge)
        } },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(tagFocusRequester),
                    value = serverTag,
                    onValueChange = { onServerTagChange(it) },
                    label = { Text("Server Tags") },
                    placeholder = {Text("Input server tag, end with \",\"")},
                    singleLine = true,
                    isError = serverTagErrors,
                    supportingText = {Text("Tag must not be empty and length<33")},
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if(serverTag.isEmpty())
                                addFocusRequester.requestFocus(FocusDirection.Next)
                            else
                                onServerTagChange(serverTag+",")
                        }
                    )
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.Start
                ) {
                    serverTags.forEach {
                        AssistChip(
                            modifier = Modifier.padding(horizontal = 2.dp)
                                .align(Alignment.CenterVertically),
                            colors = AssistChipDefaults.assistChipColors(containerColor = ButtonDefaults.buttonColors().containerColor),
                            onClick = { removeServerTag(it)},
                            leadingIcon = {
                                Icon(
                                    painterResource(
                                        Res.drawable.cancel),
                                    contentDescription = "Delete tag",
                                    tint = ButtonDefaults.buttonColors().contentColor
                                )
                            },
                            label = {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ButtonDefaults.buttonColors().contentColor
                                )
                            }
                        )
                    }
                }
            }
            LaunchedEffect(Unit) {
                tagFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = serverTags.isNotEmpty(),
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    onConfirmRequest(toolMaker,serverTags.joinToString())
                    onDismissRequest()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}