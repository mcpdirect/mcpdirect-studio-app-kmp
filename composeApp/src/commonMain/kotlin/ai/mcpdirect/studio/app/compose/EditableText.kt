package ai.mcpdirect.studio.app.compose

import ai.mcpdirect.studio.app.theme.AppColorScheme
import ai.mcpdirect.studio.app.theme.AppColors
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
import mcpdirectstudioapp.composeapp.generated.resources.error
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.save
import mcpdirectstudioapp.composeapp.generated.resources.warning
import org.jetbrains.compose.resources.painterResource
// 一行定义，极致简洁
sealed interface ValidationResult {
    object OK : ValidationResult
    data class Fail(val reason: String,val severity: ValidationSeverity= ValidationSeverity.Error) : ValidationResult
}
// 验证规则接口
interface ValidationRule {
    fun validate(input: String): ValidationResult
}
enum class ValidationSeverity {
    Info,      // 信息提示
    Warning,   // 警告
    Error      // 错误
}
// 验证规则组合器
class Validator(private val rules: List<ValidationRule>) {
    fun validate(input: String): ValidationResult {
        return rules
            .map { it.validate(input) }
            .firstOrNull { it is ValidationResult.Fail }
            ?: ValidationResult.OK
    }

    companion object {
        fun builder() = ValidatorBuilder()
    }
}

class ValidatorBuilder {
    private val rules = mutableListOf<ValidationRule>()

    fun addRule(rule: ValidationRule) = apply {
        rules.add(rule)
    }

    fun addRule(
        condition: (String) -> Boolean,
        error: String,
        severity: ValidationSeverity = ValidationSeverity.Error
    ) = apply {
        rules.add(object : ValidationRule {
            override fun validate(input: String): ValidationResult {
                return if (condition(input)) {
                    ValidationResult.OK
                } else {
                    ValidationResult.Fail(error, severity)
                }
            }
        })
    }
    fun required() = apply{
        rules.add(ValidationRules.Required)
    }
    fun minLength(min:Int) =apply{
        rules.add(ValidationRules.MinLength(min = min))
    }
    fun maxLength(max:Int) =apply{
        rules.add(ValidationRules.MaxLength(max = max))
    }
    fun build() = Validator(rules)
}

// 预定义规则
object ValidationRules {
    object Required : ValidationRule {
        override fun validate(input: String): ValidationResult {
            return if (input.isNotBlank()) {
                ValidationResult.OK
            } else {
                ValidationResult.Fail(
                    "Required",
                    severity = ValidationSeverity.Error
                )
            }
        }
    }

    class MinLength(private val min: Int) : ValidationRule {
        override fun validate(input: String): ValidationResult {
            return if (input.length >= min) {
                ValidationResult.OK
            } else {
                ValidationResult.Fail(
                    "At least $min characters required",
                    severity = ValidationSeverity.Error
                )
            }
        }
    }

    class MaxLength(private val max: Int) : ValidationRule {
        override fun validate(input: String): ValidationResult {
            return if (input.length <= max) {
                ValidationResult.OK
            } else {
                ValidationResult.Fail(
                    "Maximum $max characters allowed",
                    severity = ValidationSeverity.Error
                )
            }
        }
    }

//    object EmailFormat : ValidationRule {
//        private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
//
//        override fun validate(input: String): ValidationResult {
//            return if (emailRegex.matches(input)) {
//                ValidationResult.Success
//            } else {
//                ValidationResult.error(
//                    code = "INVALID_EMAIL",
//                    message = "请输入有效的邮箱地址",
//                    severity = ValidationSeverity.Error
//                )
//            }
//        }
//    }
}

@Composable
fun InlineTextField(
    name:String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    validator: Validator?=null,
    onValueChange: (String?) -> Unit,
){
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var validationResult by remember { mutableStateOf<ValidationResult>(
        ValidationResult.Fail("Please enter a text", ValidationSeverity.Info)
    ) }
    var textFieldState by remember {
        mutableStateOf(TextFieldValue(name))
    }
    LaunchedEffect(Unit){
        validationResult = validator?.validate(name)?: ValidationResult.OK
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
            if(validationResult is ValidationResult.Fail) {
                val fail = (validationResult as ValidationResult.Fail)
                TooltipBox(
                    tooltip = fail.reason,
                ){

                    Icon(
                        painterResource(
                            when((validationResult as ValidationResult.Fail).severity){
                                ValidationSeverity.Info -> Res.drawable.info
                                ValidationSeverity.Warning -> Res.drawable.warning
                                ValidationSeverity.Error -> Res.drawable.error
                            }
                        ),
                        contentDescription = fail.reason,
                        modifier = Modifier.size(32.dp).padding(6.dp),
                        tint = when((validationResult as ValidationResult.Fail).severity){
                            ValidationSeverity.Info -> AppColors.current.green
                            ValidationSeverity.Warning -> MaterialTheme.colorScheme.errorContainer
                            ValidationSeverity.Error -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }else {
                FilledIconButton(
                    enabled = validationResult is ValidationResult.OK,
                    modifier = Modifier.size(32.dp).padding(4.dp),
                    onClick = {
                        onValueChange(textFieldState.text)
                    }
                ) {
                    Icon(
                        painterResource(Res.drawable.save),
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp),
                    )
                }
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
                    validator?.let { validator->
                        validationResult = validator.validate(it.text)
                        textFieldState = it
                    }

//                    error = !validator(it.text)
//                    if(validator(it.text)) {
//                        textFieldState = it
//                    }
//                    error = textFieldState.text.length>30
//                            ||textFieldState.text.isEmpty()
//                            ||textFieldState.text==name
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onValueChange(if(validationResult is ValidationResult.OK) textFieldState.text else null)
                        // Optional: If you want to hide the keyboard too
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true
            )

            IconButton(
                modifier = Modifier.size(32.dp).padding(4.dp),
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
    validator: Validator?=null,
    onTextChange: ((String) -> Unit)?=null
){
    var editable by remember { mutableStateOf(false) }
    if(onTextChange==null||!editable)Row(
        modifier = modifier,
    ){
        Text(
            text,
            Modifier.weight(1f,false),
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
                modifier = Modifier.size(14.dp)
            )
        }
    } else InlineTextField(text,modifier,validator=validator){ text ->
        editable = false
        onEdit?.invoke(editable)
        text?.let { onTextChange(it) }
    }
}