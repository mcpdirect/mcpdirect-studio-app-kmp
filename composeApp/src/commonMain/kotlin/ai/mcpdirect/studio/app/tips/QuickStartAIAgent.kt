package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServer
import kotlin.io.encoding.Base64

data class AIAgentReference(val name: String, val url: String)

data class AIAgentDeeplink(val name:String,val icon: String?,val deeplink: String,val config: String){
    fun deeplink(keyName: String, key:String, url: String,):String{
        val base64Config = Base64.encode(config
                .replace($$"${MCPDIRECT_URL}", url)
                .replace($$"${MCPDIRECT_KEY}", key)
                .encodeToByteArray())
        return deeplink
            .replace($$"${MCPDIRECT_KEY_NAME}", keyName)
            .replace(
                $$"${MCPDIRECT_CONFIG}",
                base64Config)
    }
}

data class AIAgentConfigPath(val os: String, val path: String)

data class AIAgentConfig(
    val title: String, val config: String,
    val paths: List<AIAgentConfigPath>? = null,
    val deeplink: AIAgentDeeplink?=null
) {
    fun config(keyName: String, key:String, url: String): String{
        return config
            .replace($$"${MCPDIRECT_KEY_NAME}", keyName)
            .replace($$"${MCPDIRECT_URL}", url)
            .replace($$"${MCPDIRECT_KEY}", key)
    }
}

data class AIAgent(
    val name:String,
    val references: List<AIAgentReference>? = null,
    val configs: List<AIAgentConfig> = listOf())
val generalStreamableHTTPConfig = $$"""
{
  "mcpServers": {
    "${MCPDIRECT_KEY_NAME}": {
      "url": "${MCPDIRECT_URL}/mcp",
      "headers": {
        "Authorization": "Bearer ${MCPDIRECT_KEY}"
      }      
    }
  }
}""".trimIndent()
val generalConfigs = listOf(
    AIAgentConfig(
        "SSE",
        $$"""
            {
              "mcpServers": {
                "${MCPDIRECT_KEY_NAME}": {
                  "url": "${MCPDIRECT_URL}/${MCPDIRECT_KEY}/sse"
                }
              }
            }""".trimIndent()
    ),
    AIAgentConfig(
        "Streamable HTTP",
        generalStreamableHTTPConfig
    ),
)
val claudeCodeReferences = listOf(
    AIAgentReference(
        "Claude Code MCP Documentation",
        "https://code.claude.com/docs/en/mcp"
    )
)
val claudeCodeConfigs = listOf(
    AIAgentConfig(
        "Add HTTP server with Claude CLI",
        $$"""
claude mcp add --transport http ${MCPDIRECT_KEY_NAME} "${MCPDIRECT_URL}/mcp" \
  --header "Authorization: Bearer ${MCPDIRECT_KEY}"""".trimIndent()
    ),
    AIAgentConfig(
        "Add HTTP server in .mcp.json",
        $$"""
{
  "mcpServers": {
    "${MCPDIRECT_KEY_NAME}": {
      "type": "http",
      "url": "${MCPDIRECT_URL}/mcp",
      "headers": {
        "Authorization": "Bearer ${MCPDIRECT_KEY}"
      }      
    }
  }
}""".trimIndent()
    ),
)

val cursorReferences = listOf(
    AIAgentReference(
        "Cursor MCP Documentation",
        "https://cursor.com/cn/docs/context/mcp"
    )
)
val cursorConfigPaths = listOf(
    AIAgentConfigPath(
        "macOS/Linux",
        "~/.cursor/mcp.json"
    ),
    AIAgentConfigPath(
        "Windows",
        "%USERPROFILE%\\.cursor\\mcp.json"
    )
)
val cursorConfigs = listOf(
    AIAgentConfig(
        "Add HTTP server in mcp.json",
        generalStreamableHTTPConfig,
        cursorConfigPaths,
        AIAgentDeeplink(
            "Add to Cursor",
            "mcp_install_dark",
            $$"cursor://anysphere.cursor-deeplink/mcp/install?name=${MCPDIRECT_KEY_NAME}&config=${MCPDIRECT_CONFIG}",
            $$"{\"url\": \"${MCPDIRECT_URL}/mcp\",\"headers\": {\"Authorization\": \"Bearer ${MCPDIRECT_KEY}\"}}"
        )
    ),
)

val aiAgents = listOf(
    AIAgent("General",configs=generalConfigs),
    AIAgent("Claude Code",claudeCodeReferences,claudeCodeConfigs),
//    AIAgent("Claude Desktop"),
//    AIAgent("Cherry Studio"),
    AIAgent("Cursor",cursorReferences,cursorConfigs),
//    AIAgent("Dify"),
//    AIAgent("Qwen Code"),
//    AIAgent("VS Code")
)

val mcpServerCatalog = listOf(
    AIPortMCPServer(),
    AIPortMCPServer(1),
    AIPortMCPServer(-1),
    AIPortMCPServer(300,"Chrome DevTools","npx",listOf(
        "-y",
        "chrome-devtools-mcp@latest"
    )),
    AIPortMCPServer(301,"Context7","npx",listOf(
        "-y",
        "@upstash/context7-mcp",
        "--api-key",
        $$"${YOUR_API_KEY}"
    ),null,mapOf("YOUR_API_KEY" to "your api key")),
    AIPortMCPServer(400,"DBHub","npx",
        listOf("@bytebase/dbhub", "--transport", "stdio", "--dsn", $$"${DSN}"),
        null,mapOf("DSN" to "postgres://user:password@localhost:5432/dbname")
    ),
    AIPortMCPServer(600,"Figma Context","npx",
        listOf(
        "-y",
        "figma-developer-mcp",
        $$"--figma-api-key=${YOUR-KEY}",
        "--stdio"),
        null,mapOf("YOUR-KEY" to "your key")
    ),
    AIPortMCPServer(601,"File System","npx",listOf(
        "-y","@modelcontextprotocol/server-filesystem"
    )),
    AIPortMCPServer(700,"GitHub",2,
        "https://api.githubcopilot.com/mcp/",
        mapOf("Authorization" to $$"Bearer ${YOUR_TOKEN}"),
        mapOf(
            "YOUR_TOKEN" to "your token",
        ),
        mapOf(
            "X-MCP-Tools" to "get_file_contents,issue_read",
        )
    ),
    AIPortMCPServer(1600,"Plane","npx",
        listOf("-y","@makeplane/plane-mcp-server"),
        mapOf(
            "PLANE_API_KEY" to $$"${PLANE_API_KEY}",
            "PLANE_WORKSPACE_SLUG" to $$"${PLANE_WORKSPACE_SLUG}"
        ),
        mapOf(
            "PLANE_API_KEY" to "plane api key",
            "PLANE_WORKSPACE_SLUG" to "plane workspace slug",
        )
    ),
    AIPortMCPServer(1900,"Stripe","npx",
        listOf("-y", "@stripe/mcp", "--tools=all", $$"--api-key=${YOUR_STRIPE_SECRET_KEY}"),
        null,mapOf("YOUR_STRIPE_SECRET_KEY" to "your stripe secret key",)
    ),
    AIPortMCPServer(2300,"Weather API",2,
        "https://api.weather.com/mcp",mapOf(
            "Authorization" to $$"Bearer ${YOUR_TOKEN}"
        ),mapOf("YOUR_TOKEN" to "your token")
    )
)


