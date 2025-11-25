package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.check
import org.jetbrains.compose.resources.painterResource

class WizardViewModel: ViewModel(){
    var nextStep by mutableStateOf(0)
}
data class WizardStep(
    val title: String,
    val onFinish: (() -> Unit)? = null,
    val content: @Composable ColumnScope.(viewModel: WizardViewModel) -> Unit
)
@Composable
fun Wizard(
    steps: List<WizardStep>,
    onFinish: (() -> Unit),
    modifier: Modifier = Modifier
) {
    val viewModel by remember { mutableStateOf(WizardViewModel()) }
    var currentStep by remember { mutableIntStateOf(0) }
    Row (modifier = modifier.fillMaxSize()){
        // Step indicator
        WizardStepIndicator(
            steps, currentStep,
            Modifier.weight(1.0f)
        )
        VerticalDivider()
        Column(Modifier.weight(2.0f).fillMaxSize().padding(8.dp)) {
            // Step content
            val step = steps[currentStep]
            step.content.invoke(this@Column,viewModel)
            Spacer(Modifier.weight(1.0f))
            // Navigation buttons
//            WizardNavigation(
//                currentStep = currentStep,
//                totalSteps = steps.size,
//                onPrevious = { if (currentStep > 0) currentStep-- },
//                onNext = { if (currentStep < steps.size - 1) currentStep++ },
//                onFinish = onFinish
//            )
            Row(
                modifier = Modifier.fillMaxWidth().height(32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                if (currentStep > 0) {
                    TextButton(
                        onClick = { if (currentStep > 0) currentStep-- },
                    ) {
                        Text("Previous")
                    }

                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    enabled = viewModel.nextStep>currentStep,
                    onClick = { if (currentStep < steps.size - 1) currentStep++ else onFinish() },
                ) {
                    Text(
                        if (currentStep < steps.size - 1) "Next" else "Finish"
                    )
                }
            }
        }
    }
}
@Composable
fun WizardStepIndicator(
    steps: List<WizardStep>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        steps.forEachIndexed { index, step ->
            val colors = if (index < currentStep) {
                ListItemDefaults.colors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
                    headlineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            } else if (index == currentStep) {
                ListItemDefaults.colors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    headlineColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                ListItemDefaults.colors()
            }
            ListItem(
                colors = colors,
                leadingContent = {Text("${index+1}.")},
                headlineContent = {Text(step.title)},
                trailingContent = {
                    if (index < currentStep) {
                        Icon(
                            painterResource(Res.drawable.check),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else if (index == currentStep) {
                        MaterialTheme.colorScheme.primary to (index + 1).toString()
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) to (index + 1).toString()
                    }
                }
            )
        }
    }
}

@Composable
fun WizardNavigation(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1.0f))
        if (currentStep > 0) {
            TextButton(
                onClick = onPrevious,
//                modifier = Modifier.weight(1f)
            ) {
                Text("Previous")
            }

        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = if (currentStep < totalSteps - 1) onNext else onFinish,
//            modifier = Modifier.weight(1f)
        ) {
            Text(
                if (currentStep < totalSteps - 1) "Next" else "Finish"
            )
        }
    }
}