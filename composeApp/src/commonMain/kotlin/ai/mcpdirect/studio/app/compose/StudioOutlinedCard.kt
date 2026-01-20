package ai.mcpdirect.studio.app.compose

import androidx.compose.runtime.Composable
// commonMain/kotlin/components/OutlinedCard.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * An outlined card component with a floating label similar to OutlinedTextField.
 * Can be used as a clickable container for various content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioOutlinedCard(
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: OutlinedCardColors = OutlinedCardDefaults.outlinedCardColors(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: CardElevation? = null,
    borderWidth: Dp = 1.dp,
    isError: Boolean = false,
    supportingText: String? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {

    Box(
        modifier = modifier
    ) {
        // Floating label
        label?.let {
            FloatingLabel(
                text = it,
                isError = isError,
                enabled = enabled,
                colors = colors,
                shouldFloat = true
            )
        }

        // Main card content
        OutlinedCard() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Content row with prefix and suffix
                content()

                // Supporting text
                supportingText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.supportingTextColor(enabled, isError).value,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Placeholder (shown when content is empty)
        placeholder?.let {
            if (isContentEmpty()) {
                PlaceholderText(
                    text = it,
                    enabled = enabled,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun FloatingLabel(
    text: String,
    isError: Boolean,
    enabled: Boolean,
    colors: OutlinedCardColors,
    shouldFloat: Boolean
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = colors.labelColor(enabled, isError).value,
        modifier = Modifier
            .padding(start = 12.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp)
            .offset(y = (-8).dp)
    )
}

@Composable
private fun PlaceholderText(
    text: String,
    enabled: Boolean,
    colors: OutlinedCardColors
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = colors.placeholderColor(enabled).value,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
    )
}

// Helper function to check if content is empty (you'd need to implement this based on your content)
@Composable
private fun isContentEmpty(): Boolean {
    // This is a placeholder - you'd need to implement actual content emptiness check
    return false
}

/**
 * Colors for the outlined card component.
 */
interface OutlinedCardColors {
    @Composable
    fun containerColor(enabled: Boolean, isError: Boolean): State<Color>

    @Composable
    fun contentColor(enabled: Boolean, isError: Boolean): State<Color>

    @Composable
    fun disabledContainerColor(enabled: Boolean, isError: Boolean): State<Color>

    @Composable
    fun disabledContentColor(enabled: Boolean, isError: Boolean): State<Color>

    @Composable
    fun borderColor(enabled: Boolean, isError: Boolean): State<Color>

    @Composable
    fun labelColor(enabled: Boolean, isError: Boolean): State<Color>

    @Composable
    fun placeholderColor(enabled: Boolean): State<Color>

    @Composable
    fun supportingTextColor(enabled: Boolean, isError: Boolean): State<Color>

    fun copy(
        containerColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null,
        contentColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null,
        disabledContainerColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null,
        disabledContentColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null,
        borderColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null,
        labelColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null,
        placeholderColor: (@Composable (enabled: Boolean) -> State<Color>)? = null,
        supportingTextColor: (@Composable (enabled: Boolean, isError: Boolean) -> State<Color>)? = null
    ): OutlinedCardColors
}

/**
 * Default implementations for OutlinedCardColors.
 */
object OutlinedCardDefaults {
    @Composable
    fun outlinedCardColors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor: Color = containerColor.copy(alpha = 0.38f),
        disabledContentColor: Color = contentColor.copy(alpha = 0.38f),
        borderColor: Color = MaterialTheme.colorScheme.outline,
        disabledBorderColor: Color = borderColor.copy(alpha = 0.38f),
        errorBorderColor: Color = MaterialTheme.colorScheme.error,
        labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor: Color = labelColor.copy(alpha = 0.38f),
        errorLabelColor: Color = MaterialTheme.colorScheme.error,
        placeholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        disabledPlaceholderColor: Color = placeholderColor.copy(alpha = 0.38f),
        supportingTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledSupportingTextColor: Color = supportingTextColor.copy(alpha = 0.38f),
        errorSupportingTextColor: Color = MaterialTheme.colorScheme.error
    ): OutlinedCardColors = DefaultOutlinedCardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        borderColor = borderColor,
        disabledBorderColor = disabledBorderColor,
        errorBorderColor = errorBorderColor,
        labelColor = labelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        placeholderColor = placeholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        supportingTextColor = supportingTextColor,
        disabledSupportingTextColor = disabledSupportingTextColor,
        errorSupportingTextColor = errorSupportingTextColor
    )
}

private class DefaultOutlinedCardColors(
    private val containerColor: Color,
    private val contentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color,
    private val borderColor: Color,
    private val disabledBorderColor: Color,
    private val errorBorderColor: Color,
    private val labelColor: Color,
    private val disabledLabelColor: Color,
    private val errorLabelColor: Color,
    private val placeholderColor: Color,
    private val disabledPlaceholderColor: Color,
    private val supportingTextColor: Color,
    private val disabledSupportingTextColor: Color,
    private val errorSupportingTextColor: Color
) : OutlinedCardColors {

    @Composable
    override fun containerColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(
            if (!enabled) disabledContainerColor else containerColor
        )
    }

    @Composable
    override fun contentColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(
            if (!enabled) disabledContentColor else contentColor
        )
    }

    @Composable
    override fun disabledContainerColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(disabledContainerColor)
    }

    @Composable
    override fun disabledContentColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(disabledContentColor)
    }

    @Composable
    override fun borderColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(
            when {
                isError -> errorBorderColor
                !enabled -> disabledBorderColor
                else -> borderColor
            }
        )
    }

    @Composable
    override fun labelColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(
            when {
                isError -> errorLabelColor
                !enabled -> disabledLabelColor
                else -> labelColor
            }
        )
    }

    @Composable
    override fun placeholderColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (!enabled) disabledPlaceholderColor else placeholderColor
        )
    }

    @Composable
    override fun supportingTextColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(
            when {
                isError -> errorSupportingTextColor
                !enabled -> disabledSupportingTextColor
                else -> supportingTextColor
            }
        )
    }

    override fun copy(
        containerColor: (@Composable (Boolean, Boolean) -> State<Color>)?,
        contentColor: (@Composable (Boolean, Boolean) -> State<Color>)?,
        disabledContainerColor: (@Composable (Boolean, Boolean) -> State<Color>)?,
        disabledContentColor: (@Composable (Boolean, Boolean) -> State<Color>)?,
        borderColor: (@Composable (Boolean, Boolean) -> State<Color>)?,
        labelColor: (@Composable (Boolean, Boolean) -> State<Color>)?,
        placeholderColor: (@Composable (Boolean) -> State<Color>)?,
        supportingTextColor: (@Composable (Boolean, Boolean) -> State<Color>)?
    ): OutlinedCardColors {
        return DefaultOutlinedCardColors(
            containerColor = this.containerColor,
            contentColor = this.contentColor,
            disabledContainerColor = this.disabledContainerColor,
            disabledContentColor = this.disabledContentColor,
            borderColor = this.borderColor,
            disabledBorderColor = this.disabledBorderColor,
            errorBorderColor = this.errorBorderColor,
            labelColor = this.labelColor,
            disabledLabelColor = this.disabledLabelColor,
            errorLabelColor = this.errorLabelColor,
            placeholderColor = this.placeholderColor,
            disabledPlaceholderColor = this.disabledPlaceholderColor,
            supportingTextColor = this.supportingTextColor,
            disabledSupportingTextColor = this.disabledSupportingTextColor,
            errorSupportingTextColor = this.errorSupportingTextColor
        )
    }
}