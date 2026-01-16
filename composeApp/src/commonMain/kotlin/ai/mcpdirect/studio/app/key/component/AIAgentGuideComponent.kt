package ai.mcpdirect.studio.app.key.component

import ai.mcpdirect.mcpdirectstudioapp.AppInfo
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.tips.AIAgent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.allDrawableResources
import mcpdirectstudioapp.composeapp.generated.resources.content_copy
import mcpdirectstudioapp.composeapp.generated.resources.docs
import org.jetbrains.compose.resources.painterResource

@Composable
fun AIAgentGuideComponent(
    accessKey: AIPortToolAccessKey,
    aiAgent: AIAgent,
    modifier: Modifier = Modifier,
    actions: (@Composable RowScope.() -> Unit)?=null
) {
    Column(modifier) {
        StudioActionBar(
            aiAgent.name
        ) {
            actions?.invoke(this)
        }

        var accessKeyCredential by remember { mutableStateOf("") }
        LaunchedEffect(accessKey){
            AccessKeyRepository.getAccessKeyCredential(accessKey){ data->
                data?.let {
                    accessKeyCredential = it.secretKey.substring(4)
                }
            }
        }
        val listState = rememberLazyListState()
        val uriHandler = LocalUriHandler.current
        Column( modifier.padding(8.dp)) {
            aiAgent.references?.let {
                if (it.isNotEmpty()) Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ){
                    TextButton(
                        onClick = {
                            uriHandler.openUri(it[0].url)
                        }
                    ) {
                        Text(it[0].name)
                        Icon(painterResource(Res.drawable.docs), contentDescription = "")
                    }
                }
            }
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(aiAgent.configs) { option ->

                    ElevatedCard{
                        var endpoint = AppInfo.MCPDIRECT_GATEWAY_ENDPOINT
                        if(endpoint.endsWith("/")){
                            endpoint = endpoint.substring(0, endpoint.length - 1)
                        }
                        val keyName = accessKey.name.replace(" ","_")
                        val config = option.config
                            .replace($$"${MCPDIRECT_KEY_NAME}",keyName)
                            .replace($$"${MCPDIRECT_URL}", endpoint)
                            .replace($$"${MCPDIRECT_KEY}",accessKeyCredential)
                        Row(
                            Modifier.padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(option.title)
                            Spacer(Modifier.weight(1f))
                            option.deeplink?.let { deeplink ->
                                TextButton(onClick = {
                                    uriHandler.openUri(deeplink.deeplink(
                                        keyName,
                                        accessKeyCredential,
                                        endpoint
                                    ))
                                }){
                                    deeplink.icon?.let { icon ->
                                        Res.allDrawableResources[icon]?.let {
                                            Image(painterResource(it),contentDescription = "")
                                        }
                                    }?: Text(deeplink.name)
                                }
                            }
                            IconButton(onClick = {
                                getPlatform().copyToClipboard(config)
                            }){
                                Icon(painterResource(Res.drawable.content_copy), contentDescription = "Copy")
                            }
                        }
                        HorizontalDivider()
                        SelectionContainer(Modifier.padding(16.dp)) {
                            Text(config, style = MaterialTheme.typography.bodyMedium)
                        }
                        option.paths?.let { paths ->
                            HorizontalDivider()
                            for (path in paths) {
                                Text(path.os,modifier = Modifier.padding(start = 16.dp,top=16.dp))
                                Text(path.path,modifier = Modifier.padding(start = 32.dp),style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}