package ai.mcpdirect.studio.app.compose

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ChipColors
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.cancel
import org.jetbrains.compose.resources.painterResource


@Composable
fun OutlinedTagFieldDialog(
    title: @Composable (() -> Unit)?,
    label: @Composable (() -> Unit)?=null,
    tags:String?=null,
    placeholder: @Composable (() -> Unit)?=null,
    confirmButton: @Composable ((value:String,isValid:Boolean) -> Unit)?=null,
    onDismissRequest: (submit: Boolean, value:String, isValid:Boolean) -> Unit,
){
    val tagSet = mutableStateSetOf<String>()

    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val nameFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()
    fun onValuedChang(tag:String){
        isError = tag.length>20
        if(!isError) {
            if(tag.contains(",")){
                text = ""
                tag.split(",").forEach {
                    val text = it.trim()
                    if(text.isNotBlank())
                        tagSet.add(it.trim())
                    println(tagSet.joinToString())
                }
            }else text = tag
        }
    }
    AlertDialog(
        title = title,
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = text,
                    onValueChange = { onValuedChang(it) },
                    label = label,
                    placeholder = placeholder,
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if(text.isEmpty())
                            onDismissRequest(true,tagSet.joinToString(),!isError)
                            else onValuedChang("$text,")
                        }
                    ),
                    supportingText = {
                        if(isError) {
                            Text("Tag length less than 21")
                        }
                    }
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.Start
                ) {
                    tagSet.forEach {
                        AssistChip(
                            modifier = Modifier.padding(horizontal = 2.dp)
                                .align(Alignment.CenterVertically),
//                            colors = ChipColors(containerColor = ButtonDefaults.buttonColors().containerColor),
                            onClick = { tagSet.remove(it)},
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
                nameFocusRequester.requestFocus()
                tags?.let {
                    it.split(",").forEach {
                        val text = it.trim()
                        if (text.isNotBlank())
                            tagSet.add(it.trim())
                        println(tagSet.joinToString())
                    }
                }
            }
        },
        confirmButton = {
            if(confirmButton!=null) confirmButton(tagSet.joinToString(),!isError)
            else Button(
                enabled = tagSet.isNotEmpty()&&!isError,
                onClick = {onDismissRequest(true,tagSet.joinToString(),isError)}
            ){
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = {onDismissRequest(false,tagSet.joinToString(),!isError)}
            ){
                Text("Cancel")
            }
        },
        onDismissRequest = { onDismissRequest(false,tagSet.joinToString(),!isError) },
    )
}