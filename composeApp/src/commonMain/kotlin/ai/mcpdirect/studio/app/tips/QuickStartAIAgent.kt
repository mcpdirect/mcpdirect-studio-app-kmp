package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServer
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64

val aiAgentIntegrationGuide="""
[
  {
    "name":"Claude Code",
    "configs": [
      {
        "title": "Add HTTP server with Claude CLI",
        "config": "claude mcp add --transport http ${'$'}{MCPDIRECT_KEY_NAME} \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp"
      },
      {
        "title": "Add HTTP server in .mcp.json",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"http\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "Claude Code MCP Documentation",
        "url": "https://code.claude.com/docs/en/mcp"
      }
    ]
  },
  {
    "name": "Cline",
    "configs": [
      {
        "title": "Add HTTP server to MCP Servers configuration",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\",\n      \"type\": \"streamableHttp\"\n      }\n    }\n}",
        "paths": [
          {
            "os": "Cline",
            "path": "Hamburger menu icon > MCP Servers > Remote Servers > Edit Configuration"
          }
        ]
      }
    ]
  },
  {
    "name":"Copilot Coding Agent",
    "configs": [
      {
        "title": "Add HTTP server",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"http\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}",
        "paths": [
          {
            "os": "Copilot Coding Agent",
            "path": "Repository > Settings > Copilot > Coding agent > MCP"
          }
        ]
      }
    ],
    "references": [
      {
        "name": "Copilot Coding Agent Documentation",
        "url": "https://docs.github.com/en/enterprise-cloud@latest/copilot/how-tos/agents/copilot-coding-agent/extending-copilot-coding-agent-with-mcp"
      }
    ]
  },
  {
    "name":"Copilot CLI",
    "configs": [
      {
        "title": "Add HTTP server to mcp-config.json",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"http\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}",
        "paths": [
          {
            "os": "Linux",
            "path": "~/.copilot/mcp-config.json"
          }
        ]
      }
    ]
  },
  {
    "name": "Cursor",
    "configs": [
      {
        "title": "Add HTTP server in mcp.json",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"http\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}",
        "paths": [
          {
            "os":"macOS/Linux",
            "path":"~/.cursor/mcp.json"
          },
          {
            "os":"Windows",
            "path":"%USERPROFILE%\\.cursor\\mcp.json"
          }
        ],
        "deeplink": {
          "name": "Add to Cursor",
          "icon": "mcp_install_dark",
          "deeplink": "cursor://anysphere.cursor-deeplink/mcp/install?name=${'$'}{MCPDIRECT_KEY_NAME}&config=${'$'}{MCPDIRECT_CONFIG}",
          "config": "{\"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"}"
        }
      }
    ],
    "references": [
      {
        "name": "Cursor MCP Documentation",
        "url": "https://cursor.com/docs/context/mcp"
      }
    ]
  },
  {
    "name": "Gemini CLI",
    "configs": [
      {
        "title": "Add HTTP server in settings.json",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"httpUrl\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\",\n      \"headers\": {\n        \"Accept\": \"application/json, text/event-stream\"\n      }\n    }\n  }\n}",
        "paths": [
          {
            "os": "Linux",
            "path": "~/.gemini/settings.json"
          }
        ]
      }
    ],
    "references": [
      {
        "name": "Gemini CLI MCP Configuration",
        "url": "https://google-gemini.github.io/gemini-cli/docs/tools/mcp-server.html"
      }
    ]
  },
  {
    "name": "Google Antigravity",
    "configs": [
      {
        "title": "Add HTTP server",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"serverUrl\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\",\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "Google Antigravity MCP Configuration",
        "url": "https://antigravity.google/docs/mcp"
      }
    ]
  },
  {
    "name": "JetBrains AI Assistant",
    "configs": [
      {
        "title": "Add HTTP server in settings.json",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n      }\n    }\n}",
        "paths": [
          {
            "os": "JetBrains IDE",
            "path": "Settings > Tools > AI Assistant > Model Context Protocol (MCP)"
          }
        ]
      }
    ],
    "references": [
      {
        "name": "JetBrains AI Assistant Documentation",
        "url": "https://www.jetbrains.com/help/ai-assistant/configure-an-mcp-server.html"
      }
    ]
  },
  {
    "name": "LM Studio",
    "configs": [
      {
        "title": "Add HTTP server",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "LM Studio MCP Documentation",
        "url": "https://lmstudio.ai/blog/lmstudio-v0.3.17"
      }
    ]
  },
  {
    "name": "OpenAI Codex",
    "configs": [
      {
        "title": "Add HTTP server in mcp.json",
        "config": "[${'$'}{MCPDIRECT_KEY_NAME}]\nurl = \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\""
      }
    ],
    "references": [
      {
        "name": "OpenAI Codex Project",
        "url": "https://github.com/openai/codex"
      }
    ]
  },
  {
    "name": "OpenCode",
    "configs": [
      {
        "title": "Add HTTP server in opencode.json",
        "config": "{\n  \"mcp\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"remote\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n      },\n      \"enabled\": true\n    }\n  }\n}",
        "paths": [
          {
            "os": "Linux",
            "path": "~/.config/opencode/opencode.json"
          }
        ]
      }
    ],
    "references": [
      {
        "name": "OpenCode MCP Documentation",
        "url": "https://opencode.ai/docs/mcp-servers"
      }
    ]
  },
  {
    "name": "Qwen Coder",
    "configs": [
      {
        "title": "Add HTTP server in settings.json",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"httpUrl\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\",\n      \"headers\": {\n        \"Accept\": \"application/json, text/event-stream\"\n      }\n    }\n  }\n}",
        "paths": [
          {
            "os": "Linux",
            "path": "~/.qwen/settings.json"
          }
        ]
      }
    ],
    "references": [
      {
        "name": "Qwen Coder MCP Documentation",
        "url": "https://qwenlm.github.io/qwen-code-docs/en/tools/mcp-server/#how-to-set-up-your-mcp-server"
      }
    ]
  },
  {
    "name": "Trae",
    "configs": [
      {
        "title": "Add HTTP server",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "Trae MCP Documentation",
        "url": "https://docs.trae.ai/ide/model-context-protocol?_lang=en"
      }
    ]
  },
  {
    "name": "Visual Studio",
    "configs": [
      {
        "title": "Add HTTP server",
        "config": "{\n  \"servers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"http\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "Visual Studio MCP Documentation",
        "url": "https://learn.microsoft.com/visualstudio/ide/mcp-servers?view=vs-2022"
      }
    ]
  },
  {
    "name": "VS Code",
    "configs": [
      {
        "title": "Add HTTP server to .vscode/mcp.json in your project root",
        "config": "{\n  \"servers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"type\": \"http\",\n      \"url\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\"\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "VS Code MCP Documentation",
        "url": "https://code.visualstudio.com/docs/copilot/chat/mcp-servers"
      }
    ]
  },
  {
    "name": "Windsurf",
    "configs": [
      {
        "title": "Add HTTP server",
        "config": "{\n  \"mcpServers\": {\n    \"${'$'}{MCPDIRECT_KEY_NAME}\": {\n      \"serverUrl\": \"${'$'}{MCPDIRECT_URL}/${'$'}{MCPDIRECT_KEY}/mcp\",\n    }\n  }\n}"
      }
    ],
    "references": [
      {
        "name": "Windsurfy MCP Configuration",
        "url": "https://docs.windsurf.com/windsurf/cascade/mcp"
      }
    ]
  }
]""".trimIndent()

@Serializable
data class AIAgentReference(val name: String, val url: String)

@Serializable
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

@Serializable
data class AIAgentConfigPath(val os: String, val path: String)

@Serializable
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

@Serializable
data class AIAgent(
    val name:String,
    val references: List<AIAgentReference>? = null,
    val configs: List<AIAgentConfig> = listOf())
val generalStreamableHTTPConfig = $$"""
{
  "mcpServers": {
    "${MCPDIRECT_KEY_NAME}": {
      "type": "http",
      "url": "${MCPDIRECT_URL}/${MCPDIRECT_KEY}/mcp"
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
                  "type": "sse",
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
claude mcp add --transport http ${MCPDIRECT_KEY_NAME} "${MCPDIRECT_URL}/${MCPDIRECT_KEY}/mcp"""".trimIndent()
    ),
    AIAgentConfig(
        "Add HTTP server in .mcp.json",
        generalStreamableHTTPConfig
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
            $$"{\"url\": \"${MCPDIRECT_URL}/${MCPDIRECT_KEY}/mcp\"}"
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
    ), inputArgs = listOf("/absolute/path/to/allowed/folder1", "/absolute/path/to/allowed/folder2")),
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


