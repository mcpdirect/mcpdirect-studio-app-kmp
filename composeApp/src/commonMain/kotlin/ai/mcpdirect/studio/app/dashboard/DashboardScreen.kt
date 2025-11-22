package ai.mcpdirect.studio.app.dashboard

import ai.mcpdirect.studio.app.compose.Wizard
import ai.mcpdirect.studio.app.compose.WizardStep
import ai.mcpdirect.studio.app.dashboard.card.MyStudiosCard
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(){
    val viewModel = remember { DashboardViewModel() }
//    LaunchedEffect(viewModel){
//        viewModel.refreshToolAgents()
//        generalViewModel.refreshToolMakers()
//    }
    Column(
        Modifier.width(1200.dp).fillMaxHeight().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier.fillMaxWidth().height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            MyStudiosCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
            OutlinedCard(Modifier.weight(1.0f).fillMaxHeight()) {
                Text("MCP Tools", modifier = Modifier.padding(8.dp))
                HorizontalDivider()
            }
            OutlinedCard(Modifier.weight(1.0f).fillMaxHeight()) {
                Text("MCPdirect Keys", modifier = Modifier.padding(8.dp))
                HorizontalDivider()
            }
            OutlinedCard(Modifier.weight(1.0f).fillMaxHeight()) {
                Text("My Teams", modifier = Modifier.padding(8.dp))
                HorizontalDivider()
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Row(
            Modifier.fillMaxHeight().weight(1.0f)
        ){
            Column(Modifier.weight(1.0f)){
                Text("Shortcuts", modifier = Modifier.padding(8.dp))
                HorizontalDivider()
                ListItem(
                    headlineContent = {Text("Connect OpenAPI as MCP")}
                )
            }
            val wizardSteps = remember {
                listOf(
                    WizardStep("Welcome") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Welcome to the Setup Wizard",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Let's get started with the setup process",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    WizardStep("Account") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Enter your account details:", style = MaterialTheme.typography.titleMedium)
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("Email") }
                            )
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    },
                    WizardStep("Preferences") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Choose your preferences:", style = MaterialTheme.typography.titleMedium)

                            var notificationsEnabled by remember { mutableStateOf(true) }
                            var themeDark by remember { mutableStateOf(false) }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it }
                                )
                                Text("Enable notifications")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = themeDark,
                                    onCheckedChange = { themeDark = it }
                                )
                                Text("Use dark theme")
                            }
                        }
                    },
                    WizardStep("Complete") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Setup Complete!",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Your configuration has been saved successfully."
                            )
                        }
                    }
                )
            }
            OutlinedCard(Modifier.weight(3.0f)) {
                Wizard(wizardSteps)
            }

        }
    }
}