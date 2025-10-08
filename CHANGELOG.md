### 2025-10-08

**Summary:**

Add Virtual MCP feature, update dependencies, and refactor UI navigation

**Details:**

- **New Feature:**
    - Added Virtual MCP functionality with dedicated screens and view models (`VirtualMakerScreen`, `VirtualMakerToolConfigScreen`, `VirtualMakerViewModel`).
    - Introduced "Virtual MCP" and "My Team" options in the main navigation with corresponding string resources.

- **UI Improvements:**
    - Refactored navigation rail to use a reusable `navigationRailItem` composable function for cleaner code.
    - Added horizontal dividers to group navigation sections for better visual organization.
    - Improved layout spacing in the navigation rail.
    - Replaced inline SearchView implementation with a reusable component.

- **Dependency Updates:**
    - Updated `mcpdirect-studio-core` dependency from version `1.1.2-SNAPSHOT` to `1.2.0-SNAPSHOT`.
    - Bumped application version from `1.0.2` to `1.0.3`.
    - Updated androidx-lifecycle from `2.9.1` to `2.9.4`.
    - Updated composeHotReload from `1.0.0-alpha11` to `1.0.0-beta09`.
    - Updated composeMultiplatform from `1.8.2` to `1.9.0`.
    - Updated Kotlin from `2.2.0` to `2.2.20`.
    - Updated kotlinx-coroutines from `1.10.2` to `1.10.3` (via libs.versions.toml change).
    - Removed unused `androidx-material3-desktop` dependency.

- **Code Refactoring:**
    - Modified the Screen sealed class to use StringResource instead of String for titles.
    - Renamed `my_studios` string resource to `my_studio` for consistency.
    - Added virtual maker view model to the main app initialization.
    - Commented out unused code related to code highlighting functionality.

### 2025-09-11

**Summary:**

Add menu option for Windows native distribution

**Details:**

- **Build Configuration:**
    - Added `menu = true` for Windows native distribution in `composeApp/build.gradle.kts` to ensure the application appears in the Start menu.

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