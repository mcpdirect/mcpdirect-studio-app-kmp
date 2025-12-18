package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServer
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConfigMCPServerDialog(
    mcpServer: MCPServer,
    onConfirmRequest: (MCPServer,MCPServerConfig) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var newServerTransport by remember { mutableStateOf(mcpServer.transport)  }
    var newServerUrl by remember { mutableStateOf(mcpServer.url?:"")}
    var newServerCommand by remember { mutableStateOf(mcpServer.command?:"")}
    val newServerArgs = remember { mutableStateListOf<String>()}
    mcpServer.args?.let {
        newServerArgs.addAll(it)
    }
    val newServerEnv = remember { mutableStateListOf<Pair<String, String>>()}
    mcpServer.env?.let {
        newServerEnv.addAll(it.entries.map { it.toPair() })
    }
    var isCommandValid by remember {mutableStateOf(true)}
    var isUrlValid by remember {mutableStateOf(true)}
    val formScrollState = rememberScrollState()
    fun onNewServerCommandChange(command: String) { newServerCommand = command }
    //    fun onNewServerArgsChange(args: List<String>) { newServerArgs = args }
    fun onNewServerUrlChange(url: String) { newServerUrl = url }
    fun onNewServerTypeChange(type: Int) { newServerTransport = type }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Config MCP Server of ${mcpServer.name}")
        } },
        text = {
            Column (modifier = Modifier.height(500.dp),){
                Column(Modifier.verticalScroll(formScrollState)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Server Type:")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(selected = newServerTransport == 0, onClick = { onNewServerTypeChange(0) })
                        Text("Stdio")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = newServerTransport == 1, onClick = { onNewServerTypeChange(1) })
                        Text("SSE")
                        RadioButton(selected = newServerTransport == 2, onClick = { onNewServerTypeChange(2) })
                        Text("Streamable Http")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (newServerTransport == 0) {
                        OutlinedTextField(
                            value = newServerCommand,
                            onValueChange = { onNewServerCommandChange(it) },
                            label = { Text("Command") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !isCommandValid,
                            supportingText = {
                                if (!isCommandValid) {
                                    Text("Command cannot be empty for Stdio type", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Arguments:")
                        Column {
                            newServerArgs.forEachIndexed { index, arg ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = arg,
                                        onValueChange = { newServerArgs[index] = it },
                                        modifier = Modifier.weight(1f),
                                    )
//                                    OutlinedTextField(
//                                        value = arg,
//                                        onValueChange = { newServerArgs[index] = it },
//                                        modifier = Modifier.weight(0.45f),
//                                        singleLine = true
//                                    )
                                    IconButton(onClick = { newServerArgs.removeAt(index) }) {
                                        Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                    }
                                }
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Button(onClick = { newServerArgs.add("") }) {
                                Text("Add Argument")
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = newServerUrl,
                            onValueChange = { onNewServerUrlChange(it) },
                            label = { Text("URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !isUrlValid,
                            supportingText = {
                                if (!isUrlValid) {
                                    Text("URL cannot be empty for SSE/Streamable type", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Environment Variables:")
                    Column {
                        newServerEnv.forEachIndexed { index, pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = pair.first,
                                    onValueChange = { newServerEnv[index] = it to pair.second },
                                    label = { Text("Key") },
                                    modifier = Modifier.weight(0.45f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = pair.second,
                                    onValueChange = { newServerEnv[index] = pair.first to it },
                                    label = { Text("Value") },
                                    modifier = Modifier.weight(0.45f),
                                    singleLine = true
                                )
                                IconButton(onClick = { newServerEnv.removeAt(index) }) {
                                    Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Button(onClick = { newServerEnv.add("" to "") }) {
                            Text("Add Environment Variable")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = (newServerTransport==0&&newServerCommand.isNotBlank()&&isCommandValid) ||(newServerUrl.isNotBlank()&&isUrlValid),
                onClick = {
                    val config = MCPServerConfig()
                    config.transport = newServerTransport
                    config.url = newServerUrl
                    config.command = newServerCommand
                    if(newServerArgs.isNotEmpty()) config.args = newServerArgs
                    if(newServerEnv.isNotEmpty()) config.env = newServerEnv.associate { it.first to it.second }
                    onConfirmRequest(mcpServer,config)
                    onDismissRequest()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun InstallRTMView(command: String){
    if(command == "node"||command=="npx"||command=="npm"){
        var version by remember { mutableStateOf<String?>("") }
        LaunchedEffect(command){
            StudioRepository.checkMCPServerRTMFromStudio(
                StudioRepository.localToolAgent.value,command
            ){
                version = it.data
            }
        }
        version?.let{
            if(it.isNotEmpty())Box(Modifier.padding(end=16.dp)) {
                Tag("$command $it installed")
            }
        }?:Button(
            onClick = {}
        ){
            Text("Install $command")
        }
    }
}
@Composable
fun ConfigMCPServerView(
    title:String,
    mcpServer: MCPServer?=null,
    modifier: Modifier = Modifier,
){
    var name by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(true) }
    var transport by remember { mutableStateOf(mcpServer?.transport?:0)  }
    var url by remember { mutableStateOf(mcpServer?.url?:"")}
    var command by remember { mutableStateOf(mcpServer?.command?:"")}
    val args = remember { mutableStateListOf<String>()}
    mcpServer?.args?.let {
        args.addAll(it)
    }
    val env = remember { mutableStateMapOf<String, String>()}
    mcpServer?.env?.let {
        env.putAll(it)
    }
    var isCommandValid by remember {mutableStateOf(true)}
    var isUrlError by remember {mutableStateOf(true)}
    val formScrollState = rememberScrollState()
    fun onCommandChange(value: String) { command = value }
    fun onUrlChange(value: String) {
        url = value.trim().replace(" ","")
        isUrlError = !(url.startsWith("http://")|| url.startsWith("https://"))
    }
    fun onTypeChange(type: Int) { transport = type }
    fun onNameChange(value:String){
        isNameError = value.isBlank()||value.length>20
        name = value
    }
    Column(modifier) {
        StudioActionBar(title) {
            TextButton(onClick = {
                val text = getPlatform().pasteFromClipboard()
                if(text==null){
                    generalViewModel.showSnackbar("Clipboard is empty")
                }else try{
                    var config:Map<String, JsonElement>? = null
                    val json = Json.decodeFromString<Map<String, JsonElement>>(text)
                    val mcpServersConfig = json["mcpServers"]?.jsonObject
                    if(mcpServersConfig==null) for (entry in json) {
                        onNameChange(entry.key)
                        config = entry.value.jsonObject
                        break;
                    }else for (entry in mcpServersConfig) {
                        onNameChange(entry.key)
                        config = entry.value.jsonObject
                        break;
                    }
                    if(config!=null) {
                        val command = config["command"]?.jsonPrimitive?.content
                        val url = config["url"]?.jsonPrimitive?.content
                        val type = config["type"]?.jsonPrimitive?.content
                        val a = config["args"]?.jsonArray
                        val e = config["env"]?.jsonObject
                        val h = config["headers"]?.jsonObject
                        if (command == null && url == null) {
                            throw Exception()
                        }
                        if (type == null) {
                            if (command != null) {
                                onTypeChange(0)
                            } else if (url != null) {
                                if (url.endsWith("/sse")) onTypeChange(1)
                                else onTypeChange(2)
                            }
                        } else when (type.lowercase()) {
                            "stdio" -> onTypeChange(0)
                            "sse" -> onTypeChange(1)
                            "http" -> onTypeChange(2)
                            "streamable" -> onTypeChange(2)
                            "streamablehttp" -> onTypeChange(2)
                            else -> throw Exception()
                        }
                        if (transport == 0) {
                            onCommandChange(command!!)
                            a?.let {
                                args.clear()
                                for (element in it) {
                                    args.add(element.jsonPrimitive.content)
                                }
                            }
                            e?.let {
                                env.clear()
                                for (entry in it) {
                                    env[entry.key] = entry.value.jsonPrimitive.content
                                }
                            }
                        } else {
                            onUrlChange(url!!)
                            h?.let {
                                env.clear()
                                for (entry in it) {
                                    env[entry.key] = entry.value.jsonPrimitive.content
                                }
                            }
                        }
                    }else throw Exception()
                }catch (e:Exception){
                    generalViewModel.showSnackbar("Invalid JSON format:\n$text")
                }
            }) {
                Text("Paste from JSON")
            }
        }
        HorizontalDivider()
        Column(
            Modifier.verticalScroll(formScrollState).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceAround
            ) {
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = transport == 0,
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
//                    contentPadding = PaddingValues(horizontal = 0.dp),
                        onClick = { onTypeChange(0) }){
                        Text("STDIO",style = MaterialTheme.typography.bodyMedium)
                    }
                    SegmentedButton(
                        selected = transport == 1,
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
//                    contentPadding = PaddingValues(horizontal = 0.dp),
                        onClick = { onTypeChange(1) }) {
                        Text("SSE",style = MaterialTheme.typography.bodyMedium)
                    }
                    SegmentedButton(
                        selected = transport == 2,
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
//                    contentPadding = PaddingValues(horizontal = 0.dp),
                        onClick = { onTypeChange(2) }) {
                        Text("HTTP",style = MaterialTheme.typography.bodyMedium)
                    }
                }
//            Spacer(modifier = Modifier.weight(1f))
//            Row(verticalAlignment = Alignment.CenterVertically,){
//                RadioButton(modifier=Modifier.scale(0.75f),selected = transport == 0,
//                    onClick = { onTypeChange(0) })
//                Text("STDIO")
//            }
//            Row(verticalAlignment = Alignment.CenterVertically,) {
//                RadioButton(modifier=Modifier.scale(0.75f),selected = transport == 1,
//                    onClick = { onTypeChange(1) })
//                Text("SSE")
//            }
//            Row(verticalAlignment = Alignment.CenterVertically,) {
//                RadioButton(modifier=Modifier.scale(0.75f),selected = transport == 2,
//                    onClick = { onTypeChange(2) })
//                Text("Streamable HTTP")
//            }

            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { onNameChange(it)},
                label = { Text("MCP Server Name") },
                isError = isNameError,
                supportingText = {
                    Text("Name must not be empty and length < 21")
                },
            )

            if (transport == 0) {
                OutlinedTextField(
                    value = command,
                    onValueChange = { onCommandChange(it) },
                    label = { Text("Command") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !isCommandValid,
                    supportingText = {
                        Text("Command must not be empty")
                    },
                    trailingIcon = {
                        InstallRTMView(command)
                    }
                )
                OutlinedTextField(
                    label = { Text("Arguments") },
                    value = args.joinToString(separator = "\n"),
                    placeholder = { Text("arg1\narg2\narg3") },
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    supportingText = {Text("Each argument on a new line")}
                )
            } else {
                OutlinedTextField(
                    value = url,
                    onValueChange = { onUrlChange(it) },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isUrlError,
                    supportingText = {
                        Text("URL must not be empty and must start with http:// or https://")
                    }
                )

            }
            OutlinedTextField(
                label = {Text(if(transport==0) "Environment Variables" else "Headers")},
                placeholder = { Text("KEY1=value1\nKEY2=value2\nKEY3=value3") },
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().height(150.dp),
                supportingText = {Text("Each KEY=value on a new line")}
            )

        }
    }
}

@Composable
fun ConfigMCPServerView(
    mcpServer: AIPortMCPServer,
    modifier: Modifier = Modifier,
    onInstallRequest: (config:MCPServerConfig) -> Unit,
){
//    val prettyJson = Json { prettyPrint = true }
//    var preview by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(true) }
    val inputs =  remember { mutableStateMapOf<String,String>() }
    var isInputsError by remember { mutableStateOf(true) }
    var args by remember { mutableStateOf("") }
    val inputArgs = remember { mutableStateListOf<String>() }
//    var isInputArgsError by remember { mutableStateOf(false) }
//    mcpServer.inputArgs?.let { inputArgs.addAll(it)}
    var env by remember { mutableStateOf("") }
    val inputEnv =  remember { mutableStateMapOf<String,String>() }
    var isInputEnvError by remember { mutableStateOf(false) }
//    mcpServer.inputEnv?.let {
//        inputEnv.clear()
//        inputEnv.putAll(it)
//    }
    val formScrollState = rememberScrollState()
    var currentTab by remember { mutableStateOf(0) }
    var currentTabIndex by remember { mutableStateOf(0) }
    val tabs = remember { mutableStateListOf<String>() }
    LaunchedEffect(mcpServer){
        name = mcpServer.name.lowercase().replace(" ","_")
        isNameError = name.isBlank()
        args = mcpServer.inputArgs?.joinToString(separator = "\n")?:""
        inputs.clear()
        isInputsError = mcpServer.inputs?.isEmpty() == false
        inputArgs.clear()

        env = mcpServer.inputEnv?.entries?.joinToString(separator = "\n") {
            "${it.key}=${it.value}"
        }?:""
        isInputEnvError = false
        inputEnv.clear()

        currentTabIndex = 0
        tabs.clear()
        if(mcpServer.inputs!=null){
            tabs.add("Inputs")
        }
        if(mcpServer.transport == 0) {
            tabs.add("Arguments")
            tabs.add("Env")
            currentTab = 3-tabs.size
        }else {
            tabs.add("Headers")
            if(tabs.size==1) currentTab = 2
        }
    }
//    fun build(){
//        val type = when(mcpServer.transport){
//            0 -> "stdio"
//            1 -> "sse"
//            2 -> "http"
//            else -> null
//        }
//        val map = mutableMapOf<String, JsonElement>()
//        map["type"]=JsonPrimitive(type);
//        if(type=="stdio"){
//            map["command"]=JsonPrimitive(mcpServer.command)
//            val args = inputArgs.toMutableList()
//            mcpServer.args?.let { args.addAll(it) }
//            map["args"] = Json.encodeToJsonElement(args)
//            val env = inputEnv.toMutableMap()
//            mcpServer.env?.let { env.putAll(it) }
//            map["env"] = Json.encodeToJsonElement(env)
//        }else{
//            map["url"]=Json.encodeToJsonElement(mcpServer.url)
//            val env = inputEnv.toMutableMap()
//            mcpServer.env?.let { env.putAll(it) }
//            map["headers"] = Json.encodeToJsonElement(env)
//        }
//        preview = prettyJson.encodeToString(mapOf<String, JsonElement>(
//            "mcpServers" to JsonObject(mapOf(
//                name to JsonObject(map),
//            ))
//        ))
//    }
//    build()
    fun onNameChange(value:String){
        isNameError = value.isBlank()||value.length>20
        name = value
//        if(!isNameError){
//            build()
//        }
    }
    fun onArgsChange(value:String){
        args = value.replace(" ","").replace("\n\n","\n")
        inputArgs.clear()
        val strings = value.split("\n")
        strings.forEach {
            val arg = it.trim()
            if(it.isNotEmpty())inputArgs.add(arg)
        }
    }
    fun onEnvChange(value:String){
        env = value.replace(" ","").replace("\n\n","\n")
        inputEnv.clear()
        val strings = value.split("\n")
        var errorCount = 0
        strings.forEach { pair ->
            if(pair.isNotBlank())pair.split("=").let {
                if(it.size == 2 && it[0].isNotBlank() && it[1].isNotBlank()) inputEnv[it[0].trim()]=it[1].trim()
                else errorCount++
            }
        }
        isInputEnvError = errorCount>0
    }
    Column(modifier) {
        StudioActionBar(mcpServer.name) {
            mcpServer.command?.let { InstallRTMView(it) }
        }
        HorizontalDivider()
        Column(
            Modifier.weight(1f).padding(8.dp).verticalScroll(formScrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val type = when(mcpServer.transport){
                0 -> "stdio"
                1 -> "sse"
                2 -> "http"
                else -> "Invalid Type"
            }
            val bodyMedium = MaterialTheme.typography.bodyMedium
            val bodySmall = MaterialTheme.typography.bodySmall
            val bold = FontWeight.Bold

            Card{Column(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    Text("Type: ",Modifier.width(80.dp),textAlign = TextAlign.End, style = bodyMedium)
                    Text(type,Modifier.padding(2.dp), style = bodySmall,fontWeight = bold)
                }
//                HorizontalDivider()
                if (mcpServer.transport == 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                        Text("Command: ",Modifier.width(80.dp),textAlign = TextAlign.End, style = bodyMedium)
                        Text(mcpServer.command!!,Modifier.padding(2.dp), style = bodySmall,fontWeight = bold)
                    }
                    mcpServer.args?.let { args ->
                        if(args.isNotEmpty()){
//                            HorizontalDivider()
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                                Text("Arguments: ",Modifier.width(80.dp),textAlign = TextAlign.End, style = bodyMedium)
                                FlowRow(
                                    Modifier.padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    args.forEach { arg ->
                                        Text(arg, style = bodySmall,fontWeight = bold)
                                    }
                                }
                            }
                        }
                    }
                } else Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    Text("URL: ",Modifier.width(80.dp),textAlign = TextAlign.End, style = bodyMedium)
                    Text(mcpServer.url!!,Modifier.padding(2.dp), style = bodySmall,fontWeight = bold)
                }

                mcpServer.env?.let { env ->
                    if(env.isNotEmpty()) {
//                        HorizontalDivider()
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                            Text(if (mcpServer.transport == 0) "Env: " else "Headers: ",Modifier.width(80.dp),textAlign = TextAlign.End, style = bodyMedium)
                            Column(
                                Modifier.padding(2.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                env.forEach {
                                    Text("${it.key} = ${it.value}", style = bodySmall,fontWeight = bold)
                                }
                            }
                        }
                    }
                }
            } }
            OutlinedCard(Modifier.weight(1f)) {
                SecondaryTabRow(selectedTabIndex = currentTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = currentTabIndex == index,
                            onClick = {
                                currentTabIndex = index
                                currentTab = when (tabs[index]) {
                                    "Inputs" -> 0
                                    "Arguments" -> 1
                                    "Env" -> 2
                                    "Headers" -> 2
                                    else -> 0
                                }
                                println(currentTab)
                            },
                            text = { Text(title) }
                        )
                    }
                }
                Column(Modifier.weight(1f)) {
                    // Content based on selected tab
                    when (currentTab) {
                        0 -> mcpServer.inputs?.let {
                            it.forEach { input ->
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp,vertical = 8.dp),
                                    value = inputs[input.key] ?: "",
                                    onValueChange = { value ->
                                        val error = value.isBlank()
                                        if (error) inputs.remove(input.key)
                                        else inputs[input.key] = value
                                        isInputsError = inputs.size != it.size
                                    },
                                    label = { Text(input.key) },
                                    placeholder = { Text(input.value) },
                                    isError = inputs[input.key] == null,
                                    supportingText = {
                                        Text("${input.key} must not be empty")
                                    },
                                    shape = ButtonDefaults.shape
                                )
                            }
                        }

                        1 -> TextField(
//                    label = { Text("Arguments") },
                            value = args,
                            placeholder = { Text("arg1\narg2") },
                            onValueChange = { onArgsChange(it) },
                            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
                            supportingText = { Text("Each argument on a new line") },
                            colors = OutlinedTextFieldDefaults.colors()
                        )

                        2 -> TextField(
//                    label = {Text(if(mcpServer.transport==0) "Environment Variables" else "Headers")},
                            placeholder = { Text("KEY1=value1\nKEY2=value2") },
                            value = env,
                            onValueChange = { onEnvChange(it) },
                            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
                            isError = isInputEnvError,
                            supportingText = { Text("Each KEY=value on a new line") },
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                    }
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = name,
            onValueChange = { onNameChange(it)},
            label = { Text("MCP Server Name") },
            isError = isNameError,
            supportingText = {
                Text("Name must not be empty and length < 21")
            },
            shape = CardDefaults.shape,
            singleLine = true
        )
        val enabled = !isNameError&&!isInputsError&&!isInputEnvError
        Button(
            enabled = enabled,
            modifier =Modifier.padding(8.dp).fillMaxWidth(),
            onClick = {
                val config = MCPServerConfig()
                config.name = name
                config.transport = mcpServer.transport
                config.url = mcpServer.url
                if(config.url!=null)inputs.forEach { entry ->
                    config.url = config.url!!.replace($$"${$${entry.key}}",entry.value)
                }
                config.command = mcpServer.command
                if(config.command!=null)inputs.forEach { entry ->
                    config.command = config.command!!.replace($$"${$${entry.key}}",entry.value)
                }
                val args =  hashSetOf<String>()
                inputArgs.forEach { arg ->
                    args.add(arg)
                }
                mcpServer.args?.forEach { arg ->
                    var value = arg
                    inputs.forEach { entry ->
                        value = value.replace($$"${$${entry.key}}",entry.value)
                    }
                    args.add(value)
                }
                val env = mutableStateMapOf<String,String>()
                env.putAll(inputEnv)
                mcpServer.env?.forEach { entry ->
                    var value = entry.value
                    inputs.forEach { entry ->
                        value = value.replace($$"${$${entry.key}}",entry.value)
                    }
                    env[entry.key]=value
                }
                onInstallRequest(config)
            }
        ){
            if(enabled)Text("Install")
            else Text("Please complete required inputs and MCP server name before install")
        }
    }

}