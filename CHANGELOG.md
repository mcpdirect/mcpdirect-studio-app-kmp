### 2025-09-11

**Summary:**

Update MCPdirect Studio Core dependency and application version

**Details:**

- **Dependency Update:**
    - Updated `mcpdirect-studio-core` dependency from version `1.1.1-SNAPSHOT` to `1.1.2-SNAPSHOT` in `composeApp/build.gradle.kts`.
- **Version Update:**
    - Updated application version from `1.0.1` to `1.0.2` in `composeApp/build.gradle.kts`.
- **Debugging:**
    - Added system property prints in `main.kt` for version, webport, and service gateway for debugging purposes.
- **Code Cleanup:**
    - Removed unused `app_name` and `app_version` string resources from `strings.xml`.

### 2025-09-10

**Summary:**

Update MCPdirect Studio Core dependency and rename ToolsLogbookScreen

**Details:**

- **Dependency Update:**
    - Updated `mcpdirect-studio-core` dependency from version `1.1.0-SNAPSHOT` to `1.1.1-SNAPSHOT` in `composeApp/build.gradle.kts`.
- **Refactoring:**
    - Renamed `ToolsLogbookScreen.kt` to `HostConfigScreen.kt` to better reflect its purpose.
    - Updated content of `HostConfigScreen.kt` with initial implementation for configuring MCPdirect host.

### 2025-08-30

**Summary:**

Add reload functionality to MCP Server Integration and update dependencies

**Details:**

- **New Feature:**
    - Added a reload button to the MCP Server Integration screen that allows users to refresh the tools list for the selected maker.
    - Implemented `reloadMCPServer()` function in `MCPServerIntegrationViewModel.kt` to handle the reload functionality.
- **UI Enhancement:**
    - Added a restart icon (`restart_alt.svg`) to the MakerToolView section in `MCPServerIntegrationScreen.kt`.
    - Fixed a typo in the remove server button's content description from "Remote Local MCP Server" to "Remove Local MCP Server".
- **Dependency Update:**
    - Updated `mcpdirect-studio-core` dependency from version `1.0.0-SNAPSHOT` to `1.1.0-SNAPSHOT` in `composeApp/build.gradle.kts`.
- **Branding:**
    - Updated version number from `1.1.0` to `1.0.1` in `composeApp/build.gradle.kts`.
    - Added AGPL-3.0 license file.
    - Updated README with license information and improved formatting.
    - Renamed GEMINI.md to QWEN.md to reflect the project's context.
**Details:**

- **Dependency Update:**
    - Updated `mcpdirect-studio-core` dependency from version `1.1.0-SNAPSHOT` to `1.1.1-SNAPSHOT` in `composeApp/build.gradle.kts`.
- **Refactoring:**
    - Renamed `ToolsLogbookScreen.kt` to `HostConfigScreen.kt` to better reflect its purpose.
    - Updated content of `HostConfigScreen.kt` with initial implementation for configuring MCPdirect host.

### 2025-08-30

**Summary:**

Add reload functionality to MCP Server Integration and update dependencies

**Details:**

- **New Feature:**
    - Added a reload button to the MCP Server Integration screen that allows users to refresh the tools list for the selected maker.
    - Implemented `reloadMCPServer()` function in `MCPServerIntegrationViewModel.kt` to handle the reload functionality.
- **UI Enhancement:**
    - Added a restart icon (`restart_alt.svg`) to the MakerToolView section in `MCPServerIntegrationScreen.kt`.
    - Fixed a typo in the remove server button's content description from "Remote Local MCP Server" to "Remove Local MCP Server".
- **Dependency Update:**
    - Updated `mcpdirect-studio-core` dependency from version `1.0.0-SNAPSHOT` to `1.1.0-SNAPSHOT` in `composeApp/build.gradle.kts`.
- **Branding:**
    - Updated version number from `1.1.0` to `1.0.1` in `composeApp/build.gradle.kts`.
    - Added AGPL-3.0 license file.
    - Updated README with license information and improved formatting.
    - Renamed GEMINI.md to QWEN.md to reflect the project's context.