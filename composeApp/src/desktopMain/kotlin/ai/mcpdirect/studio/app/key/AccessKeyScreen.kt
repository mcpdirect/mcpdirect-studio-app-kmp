package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AccessKeyScreen(viewModel: AccessKeyViewModel, onPermissionClick: (key: AIPortAccessKeyCredential) -> Unit) {
    val keyListScrollState = rememberScrollState()
    LaunchedEffect(viewModel) {
        viewModel.apiKey.value = null
        viewModel.listApiKeys()
    }
    Scaffold(snackbarHost = { SnackbarHost(viewModel.snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp).padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // API Key Generation Section
            StudioCard(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if(viewModel.generatedKey==null) {
                        Text("Generate New Agent Key", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            OutlinedTextField(
                                value = viewModel.newKeyName,
                                onValueChange = { viewModel.newKeyName = it },
                                label = { Text("Key Name") },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.generateApiKey() },
                                enabled = viewModel.newKeyName.isNotBlank()
                            ) {
                                Text("Generate Key")
                            }
                        }
                    }
                    viewModel.generatedKey?.let { key ->
                        Text("The Key of ${key.name}", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        StudioCard(
                            modifier = Modifier.fillMaxWidth(),
                        ){
                            Text(key.secretKey, modifier = Modifier.padding(24.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                        if(viewModel.generatedKey==null||viewModel.generatedKey?.status!!>0){
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("IMPORTANT: Copy or save this key now. You will not be able to see it again!", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { viewModel.copyToClipboard(key) }) {
                                Text("Copy as MCP Server Config")
                            }
                            if(viewModel.generatedKey==null||viewModel.generatedKey?.status!!>0)
                                Button(onClick = { viewModel.saveKeyLocally(key) }) {
                                    Text("Save to Local")
                                }
                            Button(
                                onClick = { viewModel.clearGeneratedKey() },
                            ) {
                                Text("Generate Key")
                            }
                        }
                    }
                    viewModel.errorMessage?.let { message ->
                        Text("Error: $message", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // API Key List Section
            StudioCard(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column() {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your Agent Keys", style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = { viewModel.listApiKeys() }) {
                            Icon(painterResource(Res.drawable.refresh),
                                contentDescription = "Refresh List")
                        }
                    }

                    if (viewModel.apiKeys.isEmpty()) {
                        Column(modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceAround,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painterResource(Res.drawable.draft),
                                contentDescription = "Empty",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    } else Column(Modifier.verticalScroll(keyListScrollState)) {
                        viewModel.apiKeys.forEach { key ->
                            HorizontalDivider()
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        var keyName:String? by rememberSaveable { mutableStateOf(null) }
                                        if(keyName==null)Row(verticalAlignment = Alignment.CenterVertically){
                                            Text(key.name, style = MaterialTheme.typography.bodyLarge)
                                            IconButton(
                                                modifier = Modifier.size(32.dp),
                                                onClick = {
                                                    keyName = key.name
                                                }) {
                                                Icon(
                                                    modifier = Modifier.size(16.dp),
                                                    painter = painterResource(Res.drawable.edit),
                                                    contentDescription = "Edit"
                                                )
                                            }
                                        }
                                        if(keyName!=null)Row{
                                            OutlinedTextField(
                                                value = keyName!!,
                                                onValueChange = { keyName= it.trim() },
                                                singleLine = true,
                                                modifier = Modifier.height(48.dp).width(200.dp).padding(0.dp),
                                                textStyle = MaterialTheme.typography.bodyMedium,
                                                leadingIcon = {
                                                    IconButton(
                                                        modifier = Modifier.size(32.dp),
                                                        onClick = {
                                                            keyName = null
                                                        }) {
                                                        Icon(
                                                            modifier = Modifier.size(16.dp),
                                                            painter = painterResource(Res.drawable.cancel),
                                                            contentDescription = "Cancel"
                                                        )
                                                    }
                                                },
                                                trailingIcon = {
                                                    Row{
                                                        IconButton(
                                                            modifier = Modifier.size(32.dp),
                                                            onClick = {
                                                                if(keyName!!.isNotEmpty()) {
                                                                    viewModel.updateApiKeyName(key.id, keyName!!)
                                                                    keyName = null
                                                                }
                                                            }) {
                                                            Icon(
                                                                modifier = Modifier.size(16.dp),
                                                                painter = painterResource(Res.drawable.save),
                                                                contentDescription = "Save"
                                                            )
                                                        }
                                                    }
                                                }
                                            )

                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(key.secretKey, style = MaterialTheme.typography.bodyMedium)
                                            val secretKeyVisibility = mutableStateOf(false);
                                            IconButton(
                                                modifier = Modifier.size(32.dp),
                                                onClick = {
                                                    secretKeyVisibility.value = !secretKeyVisibility.value
                                                    if(secretKeyVisibility.value) viewModel.showKeyLocally(key.id,key.name)
                                                    else viewModel.generatedKey = null
                                                }) {
                                                Icon(
                                                    modifier = Modifier.size(16.dp),
                                                    painter = painterResource(
                                                        if (secretKeyVisibility.value) Res.drawable.visibility_off
                                                        else Res.drawable.visibility),
                                                    contentDescription = "Show Agent Key"
                                                )
                                            }
                                        }

                                    }
                                    Row {
                                        if (key.status == 0) IconButton(onClick = { viewModel.enableApiKey(key.id) }) {
                                            Icon(painter = painterResource(Res.drawable.block),
                                                contentDescription = "Click to enable",
                                                tint = Color.Red)
                                        } else IconButton(onClick = { viewModel.disableApiKey(key.id) }) {
                                            Icon(painter = painterResource(Res.drawable.check),
                                                contentDescription = "Click to disable",
                                                tint = Color(0xFF63A002))
                                        }
                                        IconButton(onClick = { viewModel.deprecateApiKey(key.id) }) {
                                            Icon(painter = painterResource(Res.drawable.delete), contentDescription = "Deprecate")
                                        }
                                    }

                                }
                                Text("${Date(key.created)}", style = MaterialTheme.typography.bodySmall)
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(align = Alignment.Top),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    val permissionList = viewModel.permissions.filter { it.status>0 && it.accessKeyId == key.id }.toList()
                                    if(permissionList.isNotEmpty()) {
                                        Chip(
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                                .align(Alignment.CenterVertically),
//                                            border = BorderStroke(width = 1.dp, color = MaterialTheme .colorScheme.primary),
                                            colors = ChipDefaults.chipColors(backgroundColor = ButtonDefaults.buttonColors().containerColor),
                                            onClick = { onPermissionClick(key)},
                                            leadingIcon = {
                                                Icon(
                                                    painterResource(
                                                        Res.drawable.shield_toggle),
                                                    contentDescription = "Edit Tool Permissions",
                                                    tint = ButtonDefaults.buttonColors().contentColor
                                                )
                                            }
                                        ) {
                                            Text(
                                                "Edit Permissions",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ButtonDefaults.buttonColors().contentColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.size(8.dp))
                                        val toolIdList = permissionList.map { it.toolId }.toList()
                                        val permissionTools = viewModel.tools.filter { it.id in toolIdList }
                                        val toolsByMaker = permissionTools.groupBy { it.makerId }
                                        toolsByMaker.forEach {
                                            val makerName = viewModel.makers.first { maker -> it.key == maker.id }.name
                                            AssistChip(
                                                modifier = Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .align(Alignment.CenterVertically),
                                                onClick = { /* do something */ },
                                                label = {
                                                    Text(
                                                        "$makerName(${it.value.size})",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            )
                                        }
                                    }else{
                                        Chip(
                                            onClick = { onPermissionClick(key) },
                                            colors = ChipDefaults.chipColors(backgroundColor = SegmentedButtonDefaults.colors().activeContainerColor),
                                            leadingIcon = {
                                                Icon(
                                                    painterResource(Res.drawable.shield_toggle),
                                                    contentDescription = "Add Tool Permissions First",
                                                    tint = SegmentedButtonDefaults.colors().activeContentColor
                                                )
                                            }
                                        ) {
                                            Text(
                                                "Add Tool Permissions First",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SegmentedButtonDefaults.colors().activeContentColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
