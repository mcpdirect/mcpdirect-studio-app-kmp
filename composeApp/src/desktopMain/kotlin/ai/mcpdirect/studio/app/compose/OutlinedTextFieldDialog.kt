package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction

@Composable
fun OutlinedTextFieldDialog(
    title: @Composable (() -> Unit)?,
    label: @Composable (() -> Unit)?=null,
    supportingText: @Composable ((value:String,isValid:Boolean) -> Unit)?=null,
    onValueChange: (value: String,onValueChanged:(value:String,isValid:Boolean)->Unit) -> Unit,
    confirmButton: @Composable ((value:String,isValid:Boolean) -> Unit),
    onDismissRequest: (value:String,isValid:Boolean) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        title = title,
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = text,
                    onValueChange = { onValueChange(it){
                        value, isValid ->
                        text = value
                        isError = !isValid
                    } },
                    label = label,
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            onValueChange(text){
                                value, isValid ->
                                text = value
                                isError = !isValid
                            }
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = { supportingText?.invoke(text,!isError) }
                )
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = { confirmButton(text,!isError) },
        dismissButton = {
            Button(
                onClick = {onDismissRequest(text,!isError)}
            ){
                Text("Cancel")
            }
        },
        onDismissRequest = { onDismissRequest(text,!isError) },
    )
}