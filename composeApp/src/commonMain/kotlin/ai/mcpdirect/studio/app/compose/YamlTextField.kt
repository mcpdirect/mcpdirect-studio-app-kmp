package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

data class YamlColorScheme(
    val background: Color = Color.White,
    val text: Color = Color.Black,
    val key: Color = Color(0xFF005CC5),           // Blue
    val string: Color = Color(0xFF032F62),        // Dark Blue
    val number: Color = Color(0xFFE36209),        // Orange
    val boolean: Color = Color(0xFF005CC5),       // Blue
    val nullColor: Color = Color(0xFF005CC5),     // Blue
    val comment: Color = Color(0xFF6A737D),       // Gray
    val arrayMarker: Color = Color(0xFF5A32A3),   // Purple
    val directive: Color = Color(0xFF6A737D),     // Gray
    val placeholder: Color = Color.Gray.copy(alpha = 0.6f),
    val cursor: Color = Color.Black,
    val lineNumberText: Color = Color.Gray,
    val lineNumberBackground: Color = Color(0xFFF8F9FA),
    val currentLineHighlight: Color = Color.Blue.copy(alpha = 0.1f),
    val multilineMarker: Color = Color(0xFF5A32A3),   // Purple
    val multilineContent: Color = Color(0xFF24292E)   // Dark Gray
)

class YamlHighlighter(private val colors: YamlColorScheme) {

    fun highlight(content: String): AnnotatedString {
        return buildAnnotatedString {
            val lines = content.lines()
            var inMultilineBlock = false
            var multilineIndent = 0

            lines.forEachIndexed { index, line ->
                if (index > 0) append("\n")

                if (!inMultilineBlock) {
                    val multilineResult = detectMultilineStart(line)
                    if (multilineResult.isMultiline) {
                        inMultilineBlock = true
                        multilineIndent = multilineResult.indent
                        highlightMultilineStart(line, multilineResult)
                    } else {
                        highlightRegularLine(line)
                    }
                } else {


                    if (shouldEndMultilineBlock(line, multilineIndent)) {
                        inMultilineBlock = false
                        multilineIndent = 0
                        highlightRegularLine(line)
                    } else highlightMultilineContent(line, multilineIndent)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightRegularLine(line: String) {
        val trimmed = line.trimStart()
        val indent = line.length - trimmed.length

        if (indent > 0) {
            append(" ".repeat(indent))
        }

        when {
            trimmed.isEmpty() -> {
            }

            trimmed.startsWith("#") -> {
                withStyle(SpanStyle(color = colors.comment)) {
                    append(trimmed)
                }
            }

            trimmed in listOf("---", "...") -> {
                withStyle(SpanStyle(color = colors.directive)) {
                    append(trimmed)
                }
            }

            ":" in trimmed -> {
                highlightKeyValueLine(trimmed)
            }

            trimmed.startsWith("- ") -> {
                highlightArrayItemLine(trimmed)
            }

            else -> {
                withStyle(SpanStyle(color = colors.text)) {
                    append(trimmed)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightKeyValueLine(line: String) {
        val colonIndex = line.indexOf(':')
        val keyPart = line.substring(0, colonIndex + 1)
        val valuePart = if (colonIndex < line.length - 1) line.substring(colonIndex + 1) else ""

        withStyle(SpanStyle(color = colors.key, fontWeight = FontWeight.Bold)) {
            append(keyPart)
        }

        if (valuePart.isNotEmpty()) {
            highlightValue(valuePart.trimStart())
        }
    }

    private fun AnnotatedString.Builder.highlightArrayItemLine(line: String) {
        val dashIndex = line.indexOf('-')
        val beforeDash = line.substring(0, dashIndex)
        val afterDash = line.substring(dashIndex)

        append(beforeDash)

        withStyle(SpanStyle(color = colors.arrayMarker)) {
            append("-")
        }

        if (afterDash.length > 1) {
            highlightValue(afterDash.substring(1).trimStart())
        }
    }

    private fun AnnotatedString.Builder.highlightValue(value: String) {
        val trimmed = value.trim()

        when {
            trimmed.matches(Regex("""^[|>][+-]?\s*$""")) -> {
                withStyle(SpanStyle(color = colors.multilineMarker, fontWeight = FontWeight.Bold)) {
                    append(value)
                }
            }

            (trimmed.startsWith("\"") && trimmed.endsWith("\"")) ||
                    (trimmed.startsWith("'") && trimmed.endsWith("'")) -> {
                withStyle(SpanStyle(color = colors.string)) {
                    append(value)
                }
            }

            trimmed.matches(Regex("-?\\d+(\\.\\d+)?")) -> {
                withStyle(SpanStyle(color = colors.number)) {
                    append(value)
                }
            }

            trimmed.matches(Regex("(?i)(true|false|null|yes|no|on|off)")) -> {
                withStyle(SpanStyle(color = colors.boolean)) {
                    append(value)
                }
            }

            else -> {
                append(value)
            }
        }
    }

    private fun detectMultilineStart(line: String): MultilineResult {
        val trimmed = line.trimStart()
        val indent = line.length - trimmed.length

        if (":" in trimmed) {
            val afterColon = trimmed.substring(trimmed.indexOf(":") + 1).trim()
            val multilineRegex = Regex("""^([|>][+-]?)\s*""")
            val match = multilineRegex.find(afterColon)

            if (match != null) {
                return MultilineResult(
                    isMultiline = true,
                    indent = indent,
                    marker = match.groupValues[1]
                )
            }
        }

        return MultilineResult(false, 0, "")
    }

    private fun AnnotatedString.Builder.highlightMultilineStart(
        line: String,
        result: MultilineResult
    ) {
        val indent = line.length - line.trimStart().length

        if (indent > 0) {
            append(" ".repeat(indent))
        }

        val content = line.substring(indent)
        val colonIndex = content.indexOf(':')

        if (colonIndex >= 0) {
            val keyPart = content.substring(0, colonIndex + 1)
            val valuePart = content.substring(colonIndex + 1).trim()

            withStyle(SpanStyle(color = colors.key, fontWeight = FontWeight.Bold)) {
                append(keyPart)
            }

            if (content.length > colonIndex + 1) {
                append(content.substring(colonIndex + 1, colonIndex + 2))
            }

            withStyle(SpanStyle(color = colors.multilineMarker, fontWeight = FontWeight.Bold)) {
                append(result.marker)
            }

            val afterMarker = valuePart.substring(result.marker.length).trim()
            if (afterMarker.isNotEmpty()) {
                withStyle(SpanStyle(color = colors.multilineContent)) {
                    append(" $afterMarker")
                }
            }
        }
    }

    private fun AnnotatedString.Builder.highlightMultilineContent(
        line: String,
        baseIndent: Int
    ) {
        val lineIndent = line.takeWhile { it == ' ' }.length
        val effectiveIndent = max(lineIndent, baseIndent)

        if (effectiveIndent > 0) {
            append(" ".repeat(effectiveIndent))
        }

        val content = line.substring(effectiveIndent)
        withStyle(SpanStyle(color = colors.multilineContent)) {
            append(content)
        }
    }

    private fun shouldEndMultilineBlock(line: String, baseIndent: Int): Boolean {
        val lineIndent = line.takeWhile { it == ' ' }.length
        val trimmed = line.trim()

        return lineIndent <= baseIndent && trimmed.isNotEmpty()
    }

    data class MultilineResult(
        val isMultiline: Boolean,
        val indent: Int,
        val marker: String
    )
}

@Composable
fun YamlTextField(
    value: String="",
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Enter YAML content...",
    readOnly: Boolean = false,
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value))
    }

    val lines = remember(textFieldValue.text) {
        textFieldValue.text.lines()
    }

    val (currentLine, currentColumn) = remember(textFieldValue.selection.start) {
        calculateCursorPosition(textFieldValue.text, textFieldValue.selection.start)
    }

    val yamlColors = remember { YamlColorScheme() }
    val highlighter = remember { YamlHighlighter(yamlColors) }

    val highlightedText = remember(textFieldValue.text) {
        highlighter.highlight(textFieldValue.text)
    }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val lineNumbersScrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        lineNumbersScrollState.scrollTo(scrollState.value)
    }

    LaunchedEffect(lineNumbersScrollState.value) {
        scrollState.scrollTo(lineNumbersScrollState.value)
    }

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = yamlColors.text
    )

    Column(modifier = modifier) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            Column {
                Text(
                    text = highlightedText,
                    style = textStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = 12.dp
                        ),
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onValueChange(newValue.text)
                },
//                        onFocusChanged = { focusState ->
//                            isFocused = focusState.isFocused
//                        },
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .padding(12.dp),
                textStyle = textStyle.copy(color = Color.Transparent),
                cursorBrush = SolidColor(yamlColors.cursor),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default
                ),
                keyboardActions = KeyboardActions(),
                readOnly = readOnly,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        propagateMinConstraints = true
                    ) {
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle.copy(color = yamlColors.placeholder),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        StatusBar(
            line = currentLine,
            column = currentColumn,
            totalLines = lines.size,
            isFocused = isFocused,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun YamlText(
    value: String,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value))
    }
    val yamlColors = remember { YamlColorScheme() }
    val highlighter = remember { YamlHighlighter(yamlColors) }

    val highlightedText = remember(textFieldValue.text) {
        highlighter.highlight(textFieldValue.text)
    }
    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = yamlColors.text
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = highlightedText,
            style = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
        )
    }

}

private fun calculateCursorPosition(text: String, cursorPos: Int): Pair<Int, Int> {
    if (cursorPos == 0) return Pair(1, 1)

    val textBeforeCursor = text.substring(0, cursorPos)
    val line = textBeforeCursor.count { it == '\n' } + 1
    val lastNewLineIndex = textBeforeCursor.lastIndexOf('\n')
    val column = cursorPos - lastNewLineIndex

    return Pair(line, column)
}

@Composable
private fun StatusBar(
    line: Int,
    column: Int,
    totalLines: Int,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Ln $line, Col $column",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Lines: $totalLines",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (isFocused) {
            Text(
                text = "YAML",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SimpleYamlTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Enter YAML..."
) {
    var text by remember(value) { mutableStateOf(value) }

    val yamlColors = remember { YamlColorScheme() }
    val highlighter = remember { YamlHighlighter(yamlColors) }

    val highlightedText = remember(text) {
        highlighter.highlight(text)
    }

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    Column(modifier = modifier) {
        label?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                .background(yamlColors.background)
                .padding(12.dp)
        ) {
//            Text(
//                text = highlightedText,
//                style = textStyle,
//                modifier = Modifier.fillMaxWidth()
//            )

            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    onValueChange(it)
                },
                modifier = Modifier
                    .fillMaxSize(),
                textStyle = textStyle.copy(color = Color.Transparent)
            )
        }
    }
}

private fun countMultilineBlocks(yamlContent: String): Int {
    val multilineRegex = Regex("""[|>][+-]?\s*$""", RegexOption.MULTILINE)
    return multilineRegex.findAll(yamlContent).count()
}