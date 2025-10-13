### 2025-10-08

**Summary:**

Add Team Tool Maker functionality, restructure packages, and enhance UI components

**Details:**

- **New Feature:**
    - Added MCP Tool Maker Team functionality with dedicated screens and view models (`MCPToolMakerTeamScreen`, `MCPToolMakerTeamViewModel`).
    - Introduced comprehensive tool maker team management allowing users to manage tools for team members.
    - Added MCP Team Management functionality with dedicated screens and view models (`MCPTeamScreen`, `MCPTeamViewModel`).
    - Introduced comprehensive team management allowing users to manage team members and permissions.
    - Added MCP Access Key Management functionality with dedicated screens and view models (`MCPAccessKeyScreen`, `MCPAccessKeyViewModel`).
    - Introduced comprehensive access key management allowing users to create, enable/disable, and configure MCP access keys.
    - Added Access Key Tool Permission functionality with dedicated screens and view models (`AccessKeyToolPermissionScreen`, `AccessKeyToolPermissionViewModel`).
    - Introduced comprehensive tool permission management allowing users to grant access to specific tools for API access keys.
    - Added Virtual MCP functionality with dedicated screens and view models (`VirtualMakerScreen`, `VirtualMakerToolConfigScreen`, `VirtualMakerViewModel`).
    - Introduced "Virtual MCP" and "My Team" options in the main navigation with corresponding string resources.

- **UI Improvements:**
    - Refactored navigation rail to use a reusable `navigationRailItem` composable function for cleaner code.
    - Added horizontal dividers to group navigation sections for better visual organization.
    - Improved layout spacing in the navigation rail.
    - Replaced inline SearchView implementation with a reusable component.
    - Enhanced tool permission management screen with improved filtering and selection UI.
    - Added segmented button row for filter options in tool permissions.
    - Improved virtual maker tool configuration screen with better organization and UI elements.
    - Added new SVG icons for checkbox controls (`check_box.svg`, `uncheck_box.svg`) and reset settings (`reset_settings.svg`).
    - Added play and stop circle SVG icons (`play_circle.svg`, `stop_circle.svg`) for enhanced UI functionality.
    - Added person addition and sharing SVG icons (`person_add.svg`, `share.svg`) for new team functionality.
    - Enhanced access key permission screen with better visual indicators and tool selection UI.
    - Added confirmation dialog for unsaved changes when navigating away from permission screen.
    - Implemented reset to default functionality for tool permissions.
    - Added counts to list items to show number of selected permissions.
    - Improved text overflow handling with ellipsis for long names.
    - Enhanced login screen UI components and styling.
    - Improved agent management screen with better data handling.
    - Added a reusable `TooltipIconButton` component for better UI consistency.
    - Added reusable `Tag` component for consistent tag display throughout the application.
    - Added reusable `OutlinedTextFieldDialog` component for consistent dialog input fields.
    - Enhanced `StudioCard` component with improved styling and functionality.

- **Structural Changes:**
    - Moved virtual-related files from `virtual` package to `virtualmcp` package for better organization.
    - Renamed `mcpkeys` package to `mcpkey` for better consistency.
    - Created dedicated `mcpkey` package for access key related functionality.
    - Created dedicated `viewmodel` package for shared view models.
    - Moved `GeneralViewModel` from `viewmodel` package to main app package for better accessibility.
    - Moved `MCPAccessKeyViewModel` from `viewmodel` package to `mcpkey` package for better organization.
    - Created `team` package for team management functionality.
    - Added a new `UIState.kt` file to define a standard UI state pattern across the application.
    - Created separate `Screen.kt` file to better organize screen definitions.
    - Updated main application flow to use new access key management screen.
    - Removed `VirtualMakerRepository` as its functionality was integrated into the view model.
    - Added `Validator.kt` file to centralize input validation logic.
    - Renamed `MCPTeamToolMakerScreen` to `MCPToolMakerTeamScreen` for better clarity.
    - Renamed `MCPTeamToolMakerViewModel` to `MCPToolMakerTeamViewModel` for better clarity.

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
    - Added access key tool permission view model to the main app initialization.
    - Updated main application flow to use the new access key permission screen.
    - Commented out unused code related to code highlighting functionality.
    - Improved tool detail view models with better data handling and UI state management.
    - Added horizontal dividers in virtual maker UI for better separation of sections.
    - Refined tool item display in virtual maker screen for better visual organization.
    - Enhanced permission management logic with better state tracking and change detection.
    - Implemented virtual tool agent for unified access control.
    - Improved permission saving and reset functionality with better change detection.
    - Updated API calls to include virtual tool permissions.
    - Implemented proper state management for tool selection and permissions.
    - Enhanced access key management with comprehensive CRUD operations.
    - Improved data flow between different view models for better state consistency.
    - Added GeneralViewModel for shared application state management.
    - Updated authentication flow with improved state management in AuthViewModel.
    - Enhanced tool detail screen with additional UI elements and improved navigation.
    - Refined strings.xml with additional localization entries for new features.
    - Updated screen navigation flow for better user experience.
    - Integrated repository functionality directly into view models for better architecture.
    - Improved UI consistency with reusable components like `TooltipIconButton`.
    - Implemented standardized `UIState` pattern for better loading/error state handling.
    - Enhanced team management functionality with proper state management.
    - Centralized input validation logic with a dedicated `Validator` class.
    - Enhanced team screen with improved UI and validation.
    - Further refined team screen and view model with additional improvements and bug fixes.
    - Enhanced team tool maker functionality with comprehensive management tools.

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