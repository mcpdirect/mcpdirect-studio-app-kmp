//package ai.mcpdirect.studio.app.compose
//
//import androidx.compose.foundation.*
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.text.*
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.delay
//
//@Composable
//fun YamlEditor(
//    value: String,
//    onValueChange: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    label: String? = null,
//    placeholder: String = "Enter YAML...",
//    readOnly: Boolean = false,
//    showLineNumbers: Boolean = true,
//    autoIndent: Boolean = true
//) {
//    var textFieldValue by remember(value) {
//        mutableStateOf(TextFieldValue(text = value))
//    }
//    val lines = remember(textFieldValue.text) {
//        textFieldValue.text.lines()
//    }
//    val lineCount = lines.size
//    val scrollState = rememberLazyListState()
//    val focusRequester = remember { FocusRequester() }
//
//    var showLineHighlight by remember { mutableStateOf(false) }
//    var highlightedLine by remember { mutableStateOf(-1) }
//
//    val yamlColors = YamlColorScheme()
//
//    LaunchedEffect(textFieldValue.selection.start) {
//        val cursorLine = textFieldValue.text.substring(0, textFieldValue.selection.start).count { it == '\n' }
//        if (cursorLine >= 0 && cursorLine < lineCount) {
//            scrollState.animateScrollToItem(cursorLine)
//            highlightedLine = cursorLine
//            showLineHighlight = true
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .border(
//                width = 1.dp,
//                color = MaterialTheme.colorScheme.outline,
//                shape = MaterialTheme.shapes.small
//            )
//            .background(MaterialTheme.colorScheme.surface)
//    ) {
//        label?.let {
//            Text(
//                text = it,
//                style = MaterialTheme.typography.labelMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
//            )
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .heightIn(min = 150.dp, max = 400.dp)
//                .background(yamlColors.background)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxSize()
//            ) {
//                // Line numbers column
//                if (showLineNumbers) {
//                    LineNumbersColumn(
//                        lineCount = lineCount,
//                        scrollState = scrollState,
//                        highlightedLine = highlightedLine,
//                        modifier = Modifier
//                            .width(48.dp)
//                            .fillMaxHeight()
//                            .background(yamlColors.lineNumberBackground)
//                            .border(
//                                width = 1.dp,
//                                color = yamlColors.lineNumberBorder,
//                                shape = MaterialTheme.shapes.small
//                            )
//                    )
//                }
//
//                // Editor content
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight()
//                        .verticalScroll(rememberScrollState())
//                        .pointerInput(Unit) {
//                            detectTapGestures(
//                                onTap = {
//                                    focusRequester.requestFocus()
//                                }
//                            )
//                        }
//                ) {
//                    BasicTextField(
//                        value = textFieldValue,
//                        onValueChange = { newValue ->
//                            textFieldValue = if (autoIndent) {
//                                handleAutoIndent(newValue, textFieldValue)
//                            } else {
//                                newValue
//                            }
//                            onValueChange(textFieldValue.text)
//                        },
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(12.dp)
//                            .focusRequester(focusRequester),
//                        textStyle = TextStyle(
//                            fontFamily = FontFamily.Monospace,
//                            fontSize = 14.sp,
//                            lineHeight = 20.sp,
//                            color = yamlColors.text
//                        ),
//                        cursorBrush = SolidColor(yamlColors.cursor),
//                        keyboardOptions = KeyboardOptions(
//                            imeAction = ImeAction.Default
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { /* Handle done */ }
//                        ),
//                        decorationBox = { innerTextField ->
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//                                propagateMinConstraints = true
//                            ) {
//                                // Syntax highlighted text
//                                Text(
//                                    text = buildAnnotatedString {
//                                        highlightYamlAdvanced(textFieldValue.text, yamlColors)
//                                    },
//                                    style = TextStyle(
//                                        fontFamily = FontFamily.Monospace,
//                                        fontSize = 14.sp,
//                                        lineHeight = 20.sp
//                                    ),
//                                    modifier = Modifier.fillMaxSize()
//                                )
//
//                                // Show placeholder when empty
//                                if (textFieldValue.text.isEmpty()) {
//                                    Text(
//                                        text = placeholder,
//                                        style = TextStyle(
//                                            fontFamily = FontFamily.Monospace,
//                                            fontSize = 14.sp,
//                                            lineHeight = 20.sp,
//                                            color = yamlColors.placeholder
//                                        ),
//                                        modifier = Modifier.fillMaxSize()
//                                    )
//                                }
//
//                                innerTextField()
//
//                                // Current line highlight
//                                if (showLineHighlight && highlightedLine >= 0) {
//                                    LaunchedEffect(highlightedLine) {
//                                        delay(1000)
//                                        showLineHighlight = false
//                                    }
//                                }
//                            }
//                        }
//                    )
//                }
//            }
//        }
//
//        // Status bar
//        YamlStatusBar(
//            lineCount = lineCount,
//            cursorPosition = textFieldValue.selection.start,
//            totalLength = textFieldValue.text.length,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(24.dp)
//                .background(yamlColors.statusBarBackground)
//        )
//    }
//}
//
//@Composable
//private fun LineNumbersColumn(
//    lineCount: Int,
//    scrollState: LazyListState,
//    highlightedLine: Int,
//    modifier: Modifier = Modifier
//) {
//    LazyColumn(
//        state = scrollState,
//        modifier = modifier
//    ) {
//        itemsIndexed(List(lineCount) { it + 1 }) { index, lineNumber ->
//            val isHighlighted = index == highlightedLine
//            Text(
//                text = lineNumber.toString(),
//                style = TextStyle(
//                    fontFamily = FontFamily.Monospace,
//                    fontSize = 12.sp,
//                    color = if (isHighlighted) Color.White else Color.Gray,
//                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp, vertical = 2.dp)
//                    .background(
//                        if (isHighlighted) Color.Blue.copy(alpha = 0.3f)
//                        else Color.Transparent
//                    )
//                    .padding(horizontal = 4.dp)
//            )
//        }
//    }
//}
//
//@Composable
//private fun YamlStatusBar(
//    lineCount: Int,
//    cursorPosition: Int,
//    totalLength: Int,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(horizontal = 12.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Text(
//            text = "Lines: $lineCount",
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.Gray
//        )
//
//        Text(
//            text = "Length: $totalLength chars",
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.Gray
//        )
//
//        Text(
//            text = "Pos: $cursorPosition",
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.Gray
//        )
//    }
//}
//
//private fun handleAutoIndent(
//    newValue: TextFieldValue,
//    oldValue: TextFieldValue
//): TextFieldValue {
//    val oldText = oldValue.text
//    val newText = newValue.text
//
//    // Handle Enter key press
//    if (newText.length > oldText.length && newText.endsWith("\n")) {
//        val cursorPos = newValue.selection.start
//        val lineStart = newText.lastIndexOf('\n', cursorPos - 2) + 1
//        val currentLine = newText.substring(lineStart, cursorPos - 1)
//
//        // Calculate indentation of current line
//        val indentMatch = Regex("^(\\s*)").find(currentLine)
//        val indent = indentMatch?.value ?: ""
//
//        // Check if current line ends with colon (YAML key)
//        val shouldIndent = currentLine.trim().endsWith(":") &&
//                !currentLine.trim().endsWith(":#")
//
//        if (shouldIndent) {
//            val newIndent = "$indent  "
//            val updatedText = StringBuilder(newText)
//                .insert(cursorPos, newIndent)
//                .toString()
//
//            return TextFieldValue(
//                text = updatedText,
//                selection = TextRange(cursorPos + newIndent.length)
//            )
//        } else if (indent.isNotEmpty()) {
//            // Keep same indentation
//            val updatedText = StringBuilder(newText)
//                .insert(cursorPos, indent)
//                .toString()
//
//            return TextFieldValue(
//                text = updatedText,
//                selection = TextRange(cursorPos + indent.length)
//            )
//        }
//    }
//
//    // Handle backspace at beginning of indented line
//    if (newText.length < oldText.length) {
//        val cursorPos = newValue.selection.start
//        if (cursorPos > 0 && cursorPos < newText.length) {
//            val charBefore = newText[cursorPos - 1]
//            val charAfter = newText.getOrNull(cursorPos)
//
//            // If backspacing into an indented line, remove 2 spaces at once
//            if (charBefore == ' ' && charAfter == ' ') {
//                val lineStart = newText.lastIndexOf('\n', cursorPos - 1) + 1
//                val lineContent = newText.substring(lineStart, cursorPos + 1)
//
//                if (lineContent.replace(" ", "").isEmpty()) {
//                    // We're in an indentation area, remove 2 spaces
//                    val updatedText = StringBuilder(newText)
//                        .deleteRange(cursorPos - 1, cursorPos + 1)
//                        .toString()
//
//                    return TextFieldValue(
//                        text = updatedText,
//                        selection = TextRange(cursorPos - 1)
//                    )
//                }
//            }
//        }
//    }
//
//    return newValue
//}
//
//data class YamlColorScheme(
//    val background: Color = Color(0xFF1E1E1E),
//    val text: Color = Color(0xFFD4D4D4),
//    val key: Color = Color(0xFF569CD6),
//    val string: Color = Color(0xFFCE9178),
//    val number: Color = Color(0xFFB5CEA8),
//    val boolean: Color = Color(0xFF569CD6),
//    val nullColor: Color = Color(0xFF569CD6),
//    val comment: Color = Color(0xFF6A9955),
//    val arrayMarker: Color = Color(0xFFD69D85),
//    val directive: Color = Color(0xFF808080),
//    val placeholder: Color = Color.Gray.copy(alpha = 0.6f),
//    val cursor: Color = Color.White,
//    val lineNumberBackground: Color = Color(0xFF2D2D2D),
//    val lineNumberBorder: Color = Color(0xFF3D3D3D),
//    val statusBarBackground: Color = Color(0xFF007ACC)
//)
//
//private fun AnnotatedString.Builder.highlightYamlAdvanced(
//    yamlContent: String,
//    colors: YamlColorScheme
//) {
//    val lines = yamlContent.lines()
//    val regexPatterns = listOf(
//        // Comments
//        Regex("#.*") to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.comment), range.first, range.last + 1)
//        },
//
//        // Strings (double quoted)
//        Regex("\"[^\"]*\"") to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.string), range.first, range.last + 1)
//        },
//
//        // Strings (single quoted)
//        Regex("'[^']*'") to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.string), range.first, range.last + 1)
//        },
//
//        // Numbers
//        Regex("\\b-?\\d+(\\.\\d+)?\\b") to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.number), range.first, range.last + 1)
//        },
//
//        // Booleans
//        Regex("\\b(true|false|yes|no|on|off)\\b", RegexOption.IGNORE_CASE) to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.boolean), range.first, range.last + 1)
//        },
//
//        // Null
//        Regex("\\b(null)\\b", RegexOption.IGNORE_CASE) to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.nullColor), range.first, range.last + 1)
//        },
//
//        // Directives
//        Regex("^(---|\\.\\.\\.)$", RegexOption.MULTILINE) to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.directive), range.first, range.last + 1)
//        },
//
//        // Array markers
//        Regex("^\\s*-\\s") to { range: IntRange ->
//            addStyle(SpanStyle(color = colors.arrayMarker), range.first, range.last + 1)
//        },
//
//        // Keys (before colon)
//        Regex("^\\s*[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*\\s*:") to { range: IntRange ->
//            // Find the colon position
//            val line = yamlContent.substring(range.first, range.last + 1)
//            val colonIndex = line.indexOf(':')
//            if (colonIndex > 0) {
//                addStyle(SpanStyle(color = colors.key), range.first, range.first + colonIndex)
//            }
//        }
//    )
//
//    // First, append all text
//    append(yamlContent)
//
//    // Then apply highlighting
//    regexPatterns.forEach { (regex, styleApplier) ->
//        regex.findAll(yamlContent).forEach { matchResult ->
//            styleApplier(matchResult.range)
//        }
//    }
//}