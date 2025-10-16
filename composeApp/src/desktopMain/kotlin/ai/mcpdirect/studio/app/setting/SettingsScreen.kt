package ai.mcpdirect.studio.app.setting
import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ai.mcpdirect.backend.dao.entity.account.AIPortUser
import ai.mcpdirect.studio.app.settingsViewModel
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.block
import mcpdirectstudioapp.composeapp.generated.resources.check
import mcpdirectstudioapp.composeapp.generated.resources.delete
import mcpdirectstudioapp.composeapp.generated.resources.deployed_code_account
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.visibility
import mcpdirectstudioapp.composeapp.generated.resources.visibility_off
import org.jetbrains.compose.resources.painterResource

// SettingsScreen.kt
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    userInfo: State<AIPortUser?>,
    viewModel: SettingsViewModel,
    onLogout: () -> Unit
) {
    val state = viewModel.state.value
    var showDeviceNameDialog by remember { mutableStateOf(false) }
    val passwordChangeState = viewModel.passwordChangeState.value


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
            )
        }
    ) { padding ->
        LaunchedEffect(passwordChangeState) {
            if (passwordChangeState is SettingsViewModel.PasswordChangeState.Error) {
                viewModel.showSnackbar(
                    message = passwordChangeState.message
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Device Name Section
            SettingItem(
                value = state.deviceName,
                onClick = { showDeviceNameDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tags Section
            Text("Tags", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Add Tag Input
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = state.newTagInput,
                    onValueChange = viewModel::updateNewTagInput,
                    modifier = Modifier.weight(1f),
                    label = { Text("Add new tag") },
                    singleLine = true
                )
                IconButton(
                    onClick = viewModel::addTag,
                    enabled = state.newTagInput.isNotBlank()
                ) {
                    Icon(painterResource(Res.drawable.add), contentDescription = "Add Tags")
                }
            }

            // Tags List
            FlowRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.tags.forEach { tag ->
                    Chip(
                        onClick = {},
                        colors = ChipDefaults.chipColors(),
                        leadingIcon = {
                            IconButton(
                                onClick = { viewModel.removeTag(tag) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    painterResource(Res.drawable.delete),
                                    "Remove tag",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    ) {
                        Text(tag)
                    }
                }
            }

            if(userInfo.value!!.type==Integer.MAX_VALUE) {
                var passwordVisibility by remember { mutableStateOf(false) }
                Spacer(modifier = Modifier.height(24.dp))

                // Tags Section
                Text("Anonymous Key", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = viewModel.getAnonymousKey(),
                        onValueChange = { },
                        readOnly = true,
//                        label = { Text("Anonymous Key") },
                        singleLine = true,
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Icon(
                                    painter = painterResource(if (!passwordVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                                    contentDescription = if (!passwordVisibility) "Hide anonymous key" else "Show anonymous key"
                                )
                            }
                        }
                    )
                }
            }else{
                var anonymousKey by remember { mutableStateOf("") }
                Text("Transfer anonymous key to you", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = anonymousKey,
                        onValueChange = {anonymousKey=it},
                        modifier = Modifier.weight(1f),
                        label = { Text("anonymous key") },
                        singleLine = true
                    )
                    IconButton(
                        onClick = { viewModel.transferAnonymous(anonymousKey) },
                    ) {
                        Icon(painterResource(Res.drawable.deployed_code_account), contentDescription = "Transfer anonymous key")
                    }
                }
            }
        }
    }

    // Device Name Dialog
    if (showDeviceNameDialog) {
        AlertDialog(
            onDismissRequest = { showDeviceNameDialog = false },
            title = { Text("Edit Device Name") },
            text = {
                OutlinedTextField(
                    value = state.deviceName,
                    onValueChange = viewModel::updateDeviceName,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeviceNameDialog = false
                        settingsViewModel.saveDeviceName(state.deviceName)
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeviceNameDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
private fun SettingItem(
    value: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Device Name :", style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painterResource(Res.drawable.edit),
                    "Edit",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}