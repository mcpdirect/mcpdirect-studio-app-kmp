package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource

@Composable
fun InlineTextField(
    name:String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    validator: (String) -> Boolean = { true },
    onValueChange: (String?) -> Unit,
){
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var error by remember { mutableStateOf(true) }
    var textFieldState by remember {
        mutableStateOf(TextFieldValue(name))
    }
    Box(
        modifier = modifier.border(
            ButtonDefaults.outlinedButtonBorder(),
            ButtonDefaults.shape
        ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.padding(paddingValues),
//                .height(48.dp)
//                .background(
//                    MaterialTheme.colorScheme.surface,
//                    ButtonDefaults.shape
//                )
//                .clip(ButtonDefaults.shape)
//                .border(
//                    ButtonDefaults.outlinedButtonBorder(),
//                    ButtonDefaults.shape
//                ),
//                .padding(start = 8.dp)
//                .focusRequester(focusRequester),
            verticalAlignment = Alignment.CenterVertically,
        ){
            IconButton(
                enabled = !error,
                modifier = Modifier.size(32.dp),
                onClick = {
                    onValueChange(textFieldState.text)
                }
            ) {
                Icon(
                    painterResource(Res.drawable.save),
                    contentDescription = "Save",
                    modifier = Modifier.size(20.dp),
                )
            }

            BasicTextField(
                modifier=Modifier
                    .weight(1f)
                    .padding(start=8.dp,end = 4.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            val text = textFieldState.text
                            textFieldState = textFieldState.copy(
                                selection = TextRange(text.length)
                            )
                        }
                    }
                    .focusRequester(focusRequester),
                value = textFieldState,
                onValueChange = {
                    if(validator(it.text)) {
                        textFieldState = it
                    }
                    error = textFieldState.text.length>30
                            ||textFieldState.text.isEmpty()
                            ||textFieldState.text==name
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onValueChange(if(error) null else textFieldState.text)
                        // Optional: If you want to hide the keyboard too
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true
            )

            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = {
                    onValueChange(null)
                }
            ) {
                Icon(
                    painterResource(Res.drawable.close),
                    contentDescription = "Clear",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
@Composable
fun EditableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    onEdit: ((Boolean)->Unit)? = null,
    validator: (String) -> Boolean = { true },
    onTextChange: ((String) -> Unit)?=null
){
    var editable by remember { mutableStateOf(false) }
    if(onTextChange==null||!editable)Row(
        modifier = modifier,
    ){
        Text(
            text,
            Modifier,
            color,
            autoSize,
            fontSize,
            fontStyle,
            fontWeight,
            fontFamily,
            letterSpacing,
            textDecoration,
            textAlign,
            lineHeight,
            overflow,
            softWrap,
            maxLines,
            minLines,
            onTextLayout,
            style
        )
        Spacer(Modifier.width(2.dp))
        IconButton(onClick={
            editable = !editable
            onEdit?.invoke(editable)
        }, Modifier.size(20.dp)){
            Icon(
                painterResource(Res.drawable.edit),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    } else InlineTextField(text,modifier,validator=validator){ text ->
        editable = false
        onEdit?.invoke(editable)
        text?.let { onTextChange(it) }
    }
}