package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
@Composable
fun EditMCPServerNameDialog(
    toolMaker: AIPortToolMaker,
    onConfirmRequest: (AIPortToolMaker,String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()
    var serverName by remember { mutableStateOf("${toolMaker.name}-${currentMilliseconds()}") }
    var serverNameErrors by remember { mutableStateOf(false) }
    fun onServerNameChange(name: String) {
        val text = name.replace(" ","_")
        serverNameErrors = text.isBlank()&&text.length>32
        if(text.length<33) {
            serverName = text
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Column {
            Text("Edit MCP Server Name")
            Text("of ${toolMaker.name}", style = MaterialTheme.typography.titleLarge)
        } },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = serverName,
                    onValueChange = { onServerNameChange(it) },
                    label = { Text("Server Name") },
                    singleLine = true,
                    isError = serverNameErrors,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        if (serverNameErrors) {
                            Text("Server Name cannot be empty and the max length is 32", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = !serverNameErrors,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    onConfirmRequest(toolMaker,serverName)
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