package ai.mcpdirect.studio.app.compose
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Custom VisualTransformation for YAML highlighting
class YamlHighlightTransformation(
    private val colors: YamlColorScheme,
    private val originalText: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Create the highlighted text
        val highlightedText = buildAnnotatedString {
            highlightYamlFull(originalText, colors)
        }

        return TransformedText(
            text = highlightedText,
            offsetMapping = OffsetMapping.Identity
        )
    }
}

@Composable
fun YamlTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Enter YAML content...",
    readOnly: Boolean = false
) {

    val yamlColors = remember {
        YamlColorScheme()
    }

    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value))
    }

    // Create highlighted text
    val annotatedString = remember(textFieldValue.text) {
        buildAnnotatedString {
            highlightYamlFull(textFieldValue.text,yamlColors)
        }
    }


    val textStyle = LocalTextStyle.current.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = yamlColors.text // Base text color
    )

    Column(
        modifier = modifier
//            .border(
//                width = 1.dp,
//                color = MaterialTheme.colorScheme.outline,
//                shape = MaterialTheme.shapes.small
//            )
            .background(yamlColors.background)
    ) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .padding(12.dp)
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onValueChange(newValue.text)
                },
                modifier = Modifier.fillMaxSize(),
                textStyle = textStyle,
                cursorBrush = SolidColor(yamlColors.cursor),
                readOnly = readOnly,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Default
                ),
                visualTransformation = YamlHighlightTransformation(
                    colors = yamlColors,
                    originalText = textFieldValue.text
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        propagateMinConstraints = true
                    ) {
                        // Show placeholder when empty
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle.copy(color = yamlColors.placeholder),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }

                        // Show the syntax-highlighted version
//                        Text(
//                            text = annotatedString,
//                            style = textStyle,
//                            modifier = Modifier.fillMaxSize()
//                        )

                        // This is the actual text field for input
                        innerTextField()
                    }
                }
            )
        }
    }
}

// Improved YAML highlighting function
private fun AnnotatedString.Builder.highlightYamlFull(yamlContent: String, colors: YamlColorScheme) {
    if (yamlContent.isEmpty()) return

    val lines = yamlContent.lines()

    lines.forEachIndexed { lineIndex, line ->
        if (lineIndex > 0) append("\n")

        // Handle indentation
        val indentMatch = Regex("^(\\s*)").find(line)
        val indent = indentMatch?.value ?: ""
        if (indent.isNotEmpty()) {
            append(indent)
        }

        val content = line.substring(indent.length)

        when {
            content.isEmpty() -> {
                // Empty line, nothing to append
            }

            content.startsWith("#") -> { // Comment
                withStyle(SpanStyle(color = colors.comment)) {
                    append(content)
                }
            }

            content.contains(":") -> { // Key-value pair
                val colonIndex = content.indexOf(':')
                val beforeColon = content.substring(0, colonIndex + 1)
                val afterColon = content.substring(colonIndex + 1)

                // Highlight key (including the colon)
                withStyle(SpanStyle(color = colors.key)) {
                    append(beforeColon)
                }

                // Highlight value
                highlightYamlValueFull(afterColon, colors)
            }

            content.trim().startsWith("-") -> { // Array item
                val dashIndex = content.indexOf('-')
                val beforeDash = content.substring(0, dashIndex + 1)
                val afterDash = content.substring(dashIndex + 1)

                append(beforeDash)
                highlightYamlValueFull(afterDash, colors)
            }

            content in listOf("---", "...") -> { // YAML directive
                withStyle(SpanStyle(color = colors.directive)) {
                    append(content)
                }
            }

            else -> {
                // Plain text
                append(content)
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightYamlValueFull(value: String, colors: YamlColorScheme) {
    val trimmed = value.trim()

    when {
        // Check for quotes first
        value.contains("\"") || value.contains("'") -> {
            // Handle quoted strings
            val regex = Regex("""(['"])(.*?)\1""")
            val matches = regex.findAll(value)

            var lastIndex = 0
            matches.forEach { match ->
                // Append text before the match
                append(value.substring(lastIndex, match.range.first))

                // Append the quoted string with highlighting
                withStyle(SpanStyle(color = colors.string)) {
                    append(match.value)
                }

                lastIndex = match.range.last + 1
            }

            // Append remaining text
            if (lastIndex < value.length) {
                append(value.substring(lastIndex))
            }
        }

        // Numbers
        trimmed.matches(Regex("-?\\d+(\\.\\d+)?")) -> {
            withStyle(SpanStyle(color = colors.number)) {
                append(value)
            }
        }

        // Booleans and null
        trimmed.matches(Regex("(?i)(true|false|null|yes|no|on|off)")) -> {
            withStyle(SpanStyle(color = colors.boolean)) {
                append(value)
            }
        }

        // Array or object markers
        trimmed.startsWith("[") || trimmed.startsWith("{") -> {
            withStyle(SpanStyle(color = colors.arrayMarker)) {
                append(value)
            }
        }

        else -> {
            append(value)
        }
    }
}

// Simple but effective YAML TextField
@Composable
fun SimpleYamlHighlightField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(value) { mutableStateOf(value) }
    val yamlColors = remember { YamlColorScheme() }

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    Column(modifier = modifier) {
        // Display highlighted text
        Text(
            text = buildAnnotatedString {
                appendYamlWithHighlight(text, yamlColors)
            },
            style = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .background(yamlColors.background)
                .padding(12.dp)
        )

        // Hidden text field for input
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onValueChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp) // Hide it
                .alpha(0f),
            textStyle = textStyle
        )
    }
}

private fun AnnotatedString.Builder.appendYamlWithHighlight(text: String, colors: YamlColorScheme) {
    val lines = text.lines()

    lines.forEachIndexed { index, line ->
        if (index > 0) append("\n")

        // Skip empty lines
        if (line.isBlank()) {
            append(line)
            return@forEachIndexed
        }

        // Check for comment
        if (line.trimStart().startsWith("#")) {
            withStyle(SpanStyle(color = colors.comment)) {
                append(line)
            }
            return@forEachIndexed
        }

        // Try to parse as key-value
        if (line.contains(":")) {
            val colonIndex = line.indexOf(':')
            val keyPart = line.substring(0, colonIndex + 1)
            val valuePart = if (colonIndex < line.length - 1)
                line.substring(colonIndex + 1)
            else ""

            // Highlight key
            withStyle(SpanStyle(color = colors.key)) {
                append(keyPart)
            }

            // Highlight value if exists
            if (valuePart.isNotEmpty()) {
                highlightValue(valuePart, colors)
            }
        } else {
            // Not a key-value line, check for array
            if (line.trimStart().startsWith("-")) {
                val dashIndex = line.indexOf('-')
                val beforeDash = line.substring(0, dashIndex + 1)
                val afterDash = line.substring(dashIndex + 1)

                append(beforeDash)
                highlightValue(afterDash, colors)
            } else {
                // Plain text
                append(line)
            }
        }
    }
}

private fun AnnotatedString.Builder.highlightValue(value: String, colors: YamlColorScheme) {
    val trimmed = value.trim()

    when {
        trimmed.startsWith("\"") && trimmed.endsWith("\"") -> {
            withStyle(SpanStyle(color = colors.string)) {
                append(value)
            }
        }
        trimmed.startsWith("'") && trimmed.endsWith("'") -> {
            withStyle(SpanStyle(color = colors.string)) {
                append(value)
            }
        }
        trimmed.matches(Regex("-?\\d+(\\.\\d+)?")) -> {
            withStyle(SpanStyle(color = colors.number)) {
                append(value)
            }
        }
        trimmed in listOf("true", "false", "null", "yes", "no", "on", "off") -> {
            withStyle(SpanStyle(color = colors.boolean)) {
                append(value)
            }
        }
        else -> {
            append(value)
        }
    }
}

// Color scheme for YAML
data class YamlColorScheme(
    val background: Color = Color(0xFFD4D4D4),
    val text: Color = Color(0xFF1E1E1E),
    val key: Color = Color(0xFF569CD6),
    val string: Color = Color(0xFFCE9178),
    val number: Color = Color(0xFFB5CEA8),
    val boolean: Color = Color(0xFF569CD6),
    val nullColor: Color = Color(0xFF569CD6),
    val comment: Color = Color(0xFF6A9955),
    val arrayMarker: Color = Color(0xFFD69D85),
    val directive: Color = Color(0xFF808080),
    val placeholder: Color = Color.Gray.copy(alpha = 0.6f),
    val cursor: Color = Color.White
)