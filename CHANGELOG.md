### 2025-07-29

**Summary:**

Rename 'MCP Server' to 'Maker' and Refine UI

**Details:**

- **Renaming:**
    - Renamed "MCP Server" to "Maker" throughout the application for consistency. This affects the "Maker Integration" screen (previously "MCP Server Integration"), related view models, and string resources.
    - The navigation item in `App.kt` is now "Maker".
- **UI Enhancements:**
    - Added a new icon `design_services.svg` for the "Maker" navigation item.
    - Updated the application title in `main.kt` to "MCPdirect Studio".
    - Refined the UI of the `ToolsLogbookScreen` and the `MakerIntegrationScreen`.
- **Build:**
    - Updated `composeApp/build.gradle.kts`.

### 2025-07-29

**Summary:**

Refactor Tools Logbook and MCP Server Integration

**Details:**

- **Refactoring:**
    - Renamed `MCPServer` to `Maker` across the codebase for better clarity and consistency. This includes changes in `MCPServerIntegrationScreen.kt`, `MCPServerIntegrationViewModel.kt`, and related files.
    - Updated the `ToolsLogbook` feature to align with the new `Maker` naming convention.
    - Refined the UI of both the `ToolsLogbookScreen` and the `MCPServerIntegrationScreen` to improve user experience.
- **Code Cleanup:**
    - Removed unused code and improved code formatting in several files.

### 2025-07-29

**Summary:**

Refine MCP Server Integration Screen UI

**Details:**

- **UI Refinements:**
    - The `MCPServerIntegrationScreen` has been updated to improve the user experience by refining the layout and selection state of the maker list.

### 2025-07-29

**Summary:**

Refactor App.kt to use a mutable dark theme

**Details:**

- **Refactoring:**
    - Updated `App.kt` to use a `mutableStateOf` for the dark theme, allowing the theme to be changed dynamically.
    - This enables the app to toggle between light and dark mode without a restart.

### 2025-07-29

**Summary:**

Add grid layout to MCP Server Integration Screen

**Details:**

- **UI Refinements:**
    - Implemented a `LazyVerticalGrid` in the `MCPServerIntegrationScreen` to display the list of makers in a grid layout.
    - This provides a more compact and visually appealing way to browse the available MCP servers.

### 2025-07-29

**Summary:**

Refine MCP Server Integration Screen UI

**Details:**

- **UI Refinements:**
    - Updated the `MCPServerIntegrationScreen` to use a two-pane layout, showing the list of makers on the left and the tools for the selected maker on the right.
    - This provides a more integrated and efficient user experience for browsing servers and their associated tools.

### 2025-07-29

**Summary:**

Refactor MCP Server Integration to support multiple instances

**Details:**

- **Refactoring:**
    - Modified `MCPServerIntegrationViewModel.kt` to manage multiple MCP server instances, allowing for the display and management of multiple servers at once.
    - Updated `MCPServerIntegrationScreen.kt` to reflect the changes in the view model, including the ability to select and interact with multiple servers.
    - Refactored `MCPServerNotificationHandlerImplement.kt` to handle notifications from multiple MCP server instances.

### 2025-07-29

**Summary:**

Implement dark mode toggle and refine UI

**Details:**

- **New Feature:**
    - Added a dark mode toggle to the UI, allowing users to switch between light and dark themes.
    - Added new icons for the dark mode toggle (`dark_mode.svg`, `light_mode.svg`).
- **UI Refinements:**
    - Improved the layout and styling of the `AccessKeyPermissionScreen`, `AccessKeyScreen`, and `MCPServerIntegrationScreen`.
    - Updated the `PurpleTheme` to support the new dark mode toggle.
- **Code Cleanup:**
    - Removed unnecessary code and improved code formatting in several files.

### 2025-07-29

**Summary:**

Refine UI and Codebase

**Details:**

- **UI Refinements:**
    - Improved the layout and styling of various authentication screens (`AnonymousLoginScreen`, `AuthOptionScreen`, `LoginScreen`, `RegisterOtpVerificationScreen`, `RegisterScreen`).
    - Enhanced the `AccessKeyScreen` with better styling for API key display and permissions management.
    - Updated the `PurpleTheme` to be the default dark theme.
- **Code Cleanup:**
    - Removed unnecessary code and improved code formatting in several files.
    - Renamed `getKeyLocally` to `showKeyLocally` in `AccessKeyViewModel.js` for better clarity.

### 2025-07-20

**Summary:**

Update Build Configuration for Native Distributions and Maven Repository

**Details:**

- **Build System:**
    - Reordered `modules("java.naming")` in `composeApp/build.gradle.kts` to appear after `targetFormats`.
    - Added a new insecure Maven repository (`http://192.168.1.3:3000/api/packages/robin.shang/maven`) to `settings.gradle.kts`.
- **Documentation:**
    - Added a new instruction to `GEMINI.md` to always commit changelog entries.

---

### 2025-07-20

**Summary:**

Refine Settings Screen and Access Key Permission UI

**Details:**

- **UI Enhancements:**
    - Integrated `AgentDropdown` and `MakerDropdown` directly into the `SingleChoiceSegmentedButtonRow` for a more compact and intuitive filtering experience in `AccessKeyPermissionScreen.kt`.
    - Added a "Save" button to the `ToolList` section in `AccessKeyPermissionScreen.kt`.
    - Adjusted the layout of the "Select All" checkbox and text in `ToolList` for better alignment.
    - `MakerHeader` is now collapsed by default in `AccessKeyPermissionScreen.kt`.
    - Added `snackbarHost` and error display for password changes in `SettingsScreen.kt`.
    - Implemented password requirements validation and visual feedback in `SettingsScreen.kt`.
    - Added a delete icon to tags in `SettingsScreen.kt`.
    - Removed `showMenu`, `showLogoutDialog`, `showResetPasswordDialog`, `showChangePasswordDialog`, `currentPassword`, `newPassword`, and `confirmPassword` states from `SettingsScreen.kt` as they are now managed by `AuthViewModel`.
    - Updated `SettingsScreen.kt` to display anonymous key or provide an option to transfer anonymous key.
- **Code Refinement:**
    - Modified `changePassword` in `SettingsViewModel.kt` to use `MCPDirectStudio.changePassword` and handle different outcomes.
    - Added `transferAnonymous` and `getAnonymousKey` functions to `SettingsViewModel.kt`.

---

### 2025-07-20

**Summary:**

Implement Anonymous Login and User Info Display

**Details:**

- **New Feature:**
    - Implemented `AnonymousLoginScreen.kt` for anonymous user login and registration.
    - Introduced `UserInfoNotificationHandlerImplement.kt` to handle user information updates.
- **UI Enhancements:**
    - Updated `App.kt` to display `AuthOptionScreen` as the initial authentication screen.
    - Modified `LoginScreen.kt` to include a "Back" button to `AuthOptionScreen`.
    - Display user information (name) in the `NavigationRail` in `App.kt`.
    - Added `account_circle.svg` drawable for user icon.
    - Updated `strings.xml` with new string resources for `agent_interaction` and `user_setting`.
    - Refined `AccessKeyPermissionScreen.kt` to display agent names with a "(Local)" suffix for local agents.
- **Code Refinement:**
    - Changed `currentScreen` in `AuthViewModel.kt` to `AuthScreen.AuthOption`.
    - Modified `AuthViewModel.kt` to handle `UiState.SuccessWithAccount` and `UiState.SuccessWithAnonymous`.
    - Updated `AccessKeyNotificationHandlerImplement.kt` to rename `onToolsAgentsNotification` to `onToolAgentsNotification` and include `localAgent` in the update.
    - Added `userInfo` and `logout` functions to `AuthViewModel.kt`.
    - Added `getAgentName` and `isLocalAgent` functions to `AccessKeyViewModel.kt` for better agent identification.

---

### 2025-07-20

**Summary:**

Update Access Key Management Functions to Use Long IDs and Refine Status Handling

**Details:**

- **Code Refinement:**
    - Changed the type of `id` parameter from `Int` to `Long` in `enableApiKey`, `disableApiKey`, `deprecateApiKey`, `getKeyLocally`, and `updateApiKeyName` functions within `AccessKeyViewModel.kt` for consistency and broader compatibility.
    - Modified `p.status = 1` to `p.status = Short.MAX_VALUE.toInt()` in the `savePermissions` function in `AccessKeyViewModel.kt` to better represent an active status.

---

### 2025-07-20

**Summary:**

Refine Settings Screen and Access Key Permission UI

**Details:**

- **UI Enhancements:**
    - Integrated `AgentDropdown` and `MakerDropdown` directly into the `SingleChoiceSegmentedButtonRow` for a more compact and intuitive filtering experience in `AccessKeyPermissionScreen.kt`.
    - Added a "Save" button to the `ToolList` section in `AccessKeyPermissionScreen.kt`.
    - Adjusted the layout of the "Select All" checkbox and text in `ToolList` for better alignment.
    - `MakerHeader` is now collapsed by default in `AccessKeyPermissionScreen.kt`.
    - Added `snackbarHost` and error display for password changes in `SettingsScreen.kt`.
    - Implemented password requirements validation and visual feedback in `SettingsScreen.kt`.
    - Added a delete icon to tags in `SettingsScreen.kt`.
- **Code Refinement:**
    - Modified `changePassword` in `SettingsViewModel.kt` to use `MCPDirectStudio.changePassword` and handle different outcomes.

---

### 2025-07-20

**Summary:**

Add `mcpdirect_studio_icon_transparent_48.png` drawable

**Details:**

- **New Assets:**
    - Added `mcpdirect_studio_icon_transparent_48.png` drawable for use in the application.

---

### 2025-07-20

**Summary:**

Update Branding, Implement Custom Theme, and Refactor UI Components

**Details:**

- **Branding Update:**
    - Replaced old `mcpwings` icons and banners with new `mcpdirect` branding assets (`mcpdirect_icon_48.png`, `mcpdirect_studio_banner_transparent_256.png`, `mcpdirect_studio_icon_48.png`).
    - Updated `README.md` title and application window title in `main.kt` to reflect "MCPdirect Studio".
- **Theming:**
    - Implemented a new custom theme (`PurpleTheme`) with defined color schemes (`Color.kt`, `Theme.kt`) and typography (`Type.kt`).
    - Applied `PurpleTheme` across the application by updating `App.kt`.
- **UI Components:**
    - Created a reusable `StudioCard.kt` composable for consistent card styling across the application.
    - Integrated `StudioCard` into `AccessKeyPermissionScreen.kt`, `AccessKeyScreen.kt`, `ToolsLogbookScreen.kt`, and `MCPServerIntegrationScreen.kt`.
    - Further refined the UI of `AccessKeyPermissionScreen.kt` for agent and maker selection dropdowns and tool list display.
- **Content Update:**
    - Updated the title in `README.md` for better clarity.
- **New Assets:**
    - Added `draft.svg` icon.

---

### 2025-07-20

**Summary:**

Refine Access Key Permission Screen UI and Functionality

**Details:**

- **UI Enhancements:**
    - Integrated `AgentDropdown` and `MakerDropdown` directly into the `SingleChoiceSegmentedButtonRow` for a more compact and intuitive filtering experience in `AccessKeyPermissionScreen.kt`.
    - Added a "Save" button to the `ToolList` section in `AccessKeyPermissionScreen.kt`.
    - Adjusted the layout of the "Select All" checkbox and text in `ToolList` for better alignment.
    - `MakerHeader` is now collapsed by default.
    - Applied visual enhancements to `ToolList` and `ToolItem` including padding, background, and shadow.
- **Code Refinement:**
    - Removed `generateFakeData()` call from `loadKeyPermissions()` in `AccessKeyViewModel.kt`.
    - Removed `fakeAgents`, `fakeMakers`, `fakeTools`, and `fakePermissions` from `AccessKeyViewModel.kt`.
    - Changed `selectedMakers` and `selectedAgents` to `remember` in `ToolList` to ensure correct recomposition.
    - Updated `AccessKeyScreen.kt` to enable API Key Generation section and display tool permissions using `Chip`s.
    - Modified `AccessKeyViewModel.kt` to include `MCPDirectStudio.grantToolPermission()` call in `savePermissions()`.
    - `hasUnsavedChanges()` in `AccessKeyViewModel.kt` now always returns `false`.

---

### 2025-07-20

**Summary:**

Integrate Dropdowns Directly into Segmented Buttons and Refine UI

**Details:**

- **UI Enhancements:**
    - Integrated `AgentDropdown` and `MakerDropdown` directly into `SingleChoiceSegmentedButtonRow` in `AccessKeyPermissionScreen.kt`, simplifying the layout and interaction flow.
    - Removed separate `FlowRow`s for displaying selected chips in `AccessKeyPermissionScreen.kt` as the dropdowns now handle this directly.
    - Set `MakerHeader` to be collapsed by default in `AccessKeyPermissionScreen.kt`.
    - Applied visual enhancements to `ToolList` and `ToolItem` in `AccessKeyPermissionScreen.kt`, including `CardDefaults.cardElevation`, `background` for `ToolItem`, and `shadow` for `HorizontalDivider`.
- **Code Refinement:**
    - Adjusted the logic in `AccessKeyPermissionScreen.kt` to use the `filterBy` state more directly for controlling the active dropdown.

---

### 2025-07-20

**Summary:**

Refine Tool Filtering and Display in Access Key Permissions

**Details:**

- **UI Enhancements:**
    - Modified the tool list filtering in `AccessKeyPermissionScreen.kt` to dynamically filter by selected agents or makers based on the `filterBy` state.
    - Updated `MakerHeader` to display the associated agent's name for better context.
    - Changed `MakerHeader` to be collapsed by default.
    - Added visual enhancements to `MakerHeader` and `ToolItem` including padding, background, and shadow.
    - `ToolItem` now includes a status display with a `Spacer` and `Box`.
- **Code Refinement:**
    - Changed `selectedChoiceIndex` to `filterBy` and made it a global mutable state for consistent filtering across the screen.

---

### 2025-07-20

**Summary:**

Implement Access Key Permission Screen and UI Enhancements

**Details:**

- **New Feature:**
    - Introduced `AccessKeyPermissionScreen.kt` for managing access key permissions with granular control over agents, makers, and tools.
    - Added `keyboard_arrow_right.svg` icon for expandable sections.
- **UI Enhancements:**
    - Updated `app_name` in `strings.xml` and window title in `main.kt` to "MCPdirect Studio".
    - Streamlined `AccessKeyScreen.kt` by commenting out the API Key Generation section and updating text labels for clarity (e.g., "Your API Keys" to "Your Agent Keys", "Show MCPwings Pass" to "Show Agent Key").
    - Refined the UI in `AccessKeyPermissionScreen.kt` for agent and maker selection, using `SingleChoiceSegmentedButtonRow` for filtering and displaying selected items as `Chip`s.
    - Implemented expandable maker headers in `ToolList` within `AccessKeyPermissionScreen.kt` for better organization.
    - Changed `TextField` to `OutlinedTextField` in `MCPServerIntegrationScreen.kt`'s `SearchView`.
- **Codebase Integration:**
    - Updated `App.kt` to integrate `AccessKeyPermissionScreen.kt` and manage navigation between `AccessKeyScreen` and `AccessKeyPermissionScreen`.

---

### 2025-07-20

**Summary:**

Refactor Access Key and Tool Permission UI for Enhanced User Experience

**Details:**

- **UI Refinements:**
    - Commented out the API Key Generation section in `AccessKeyScreen.kt` to streamline the interface.
    - Reworked agent and maker selection in `ToolPermissionScreen.kt` to utilize `Button` components for dropdowns and display selections as `Chip`s in `FlowRow`s, enhancing usability and visual feedback.
    - Encapsulated the `ToolList` within a `Card` and added `HorizontalDivider`s in `ToolPermissionScreen.kt` for improved layout and readability.
- **Code Simplification:**
    - Removed redundant column composables (`AgentColumn`, `MakerColumn`, `ToolColumn`) from `ToolPermissionScreen.kt`.
    - Optimized checkbox logic in `ToolPermissionScreen.kt` to allow independent selection management without cascading deselections.

---

### 2025-07-20

**Summary:**

Enhance Tool Permission UI and Refine Selection Logic

**Details:**

- **UI Enhancements:**
    - Improved agent and maker selection dropdowns in `ToolPermissionScreen.kt` by using `Button` components for activation and displaying selected items as `Chip`s within a `FlowRow` for better visual representation.
    - Added `keyboard_arrow_down.svg` icon to enhance dropdown visual cues.
- **Logic Refinement:**
    - Modified `ToolPermissionViewModel.kt` to prevent automatic clearing of lower-level selections (makers and tools) when higher-level selections (agents or makers) change, providing more granular control over permission configurations.
- **Documentation:**
    - Updated `GEMINI.md` to explicitly state the requirement for committing changelog entries.

---

### 2025-07-20

**Summary:**

Refine Tool Permission UI and Update Agent Data Model

**Details:**

- **UI:**
    - Modified the agent selection dropdown and checkboxes in `ToolPermissionScreen.kt` to improve interaction and state management.
- **Data Model:**
    - Updated the `AIPortToolsAgent` data class in `ToolPermissionViewModel.kt` to include a new `id` field, reflecting a change in the underlying data structure.

---

### 2025-07-20

**Summary:**

Refactor Agent and Server Handlers, Implement Tool Permission Management, and Update Logging

**Details:**

- **New Feature:**
    - Introduced a comprehensive Tool Permission Management system with `ToolPermissionScreen.kt` and `ToolPermissionViewModel.kt` for granular control over tool access.
    - Added new SVG icons: `info.svg` for details and `shield_toggle.svg` for permission toggles.
- **Refactoring & Renaming:**
    - Renamed `AgentInteractionScreen.kt` to `AccessKeyScreen.kt` and moved it to `ai.mcpdirect.studio.app.key` package.
    - Renamed `AgentInteractionViewModel.kt` to `AccessKeyViewModel.kt` and moved it to `ai.mcpdirect.studio.app.key` package.
    - Replaced `AccessKeyHandlerImplement.kt` with `AccessKeyNotificationHandlerImplement.kt` to align with notification-based updates.
    - Replaced `MCPServerHandlerImplement.kt` with `MCPServerNotificationHandlerImplement.kt` for consistent server notification handling.
    - Updated `App.kt` to reflect all new file names, package structures, and handler initializations, centralizing the setup.
    - Renamed `agentName` to `clientName` and `AgentSummary` to `ClientSummary` across `ToolsLogHandlerImplement.kt`, `ToolsLogModel.kt`, `ToolsLogRepositoryImplement.kt`, `ToolsLogViewModel.kt`, and `ToolsLogbookScreen.kt` for better clarity and consistency.
- **New Components:**
    - `ToolsAgentNotificationHandlerImplement.kt` and `ToolsAgentViewModel.kt` for future agent-related notifications.
- **Dependencies:**
    - Added `material3Desktop` dependency in `gradle/libs.versions.toml`.
- **Localization:**
    - Added `tool_permission` string resource in `strings.xml`.

---

### 2025-07-20

**Summary:**

Implement API Key Permission Management and Refactor Handlers

**Details:**

- **New Feature:**
    - Implemented a UI for managing API key permissions, allowing selection of makers and tools.
    - Added `event_list.svg` and `refresh.svg` icons for the new UI.
- **Refactoring:**
    - Created `AccessKeyHandlerImplement.kt` to handle access key updates from `MCPDirectStudio` and update the `AgentInteractionViewModel`.
    - Refactored `AgentInteractionScreen.kt` to integrate the new permission selection view and display API keys more effectively.
    - Moved the initialization of `MCPDirectStudio` handlers (ToolsLog, MCPServer, AccessKey) to the `App.kt` composable for centralized setup.
    - Modified `AgentInteractionViewModel.kt` to use the new `AccessKeyHandlerImplement` for updating API keys.

---

### 2025-07-20

**Summary:**

Refactor UI with SVG Icons and Code Cleanup

**Details:**

- **UI:**
    - Replaced all Material Icons with custom SVG icons for a more consistent and branded look.
    - Updated various button texts and labels throughout the application for improved clarity and user experience.
- **Refactoring:**
    - Moved `AgentInteractionScreen.kt` to the `ai.mcpdirect.studio.app.agent` package to improve code organization.
- **Dependencies:**
    - Commented out the `materialIconsExtended` dependency in `composeApp/build.gradle.kts` as it is no longer required.

---

### 2025-07-19

**Summary:**

Refactor MCP Server Management and UI

**Details:**

- **New Feature:**
    - Implemented `MCPServerHandlerImplement.kt` to handle MCP server data updates.
    - The MCP Server Integration screen now dynamically displays publish, unpublish, and update actions based on server state.
    - The UI now shows the tool count and status for each server.
- **Refactoring:**
    - In `MCPServerIntegrationViewModel.kt`, refactored server management to use a map for efficient lookups and updates.
    - Modified the server loading logic.
    - Added a `publishMCPServer` function to handle publishing servers.
    - In `App.kt`, moved view model initializations to a global scope and set the `MCPServerHandler`.
- **Documentation:**
    - Updated `README.md` with a "MCPdirect" quick start guide.
- **Code Cleanup:**
    - Commented out fake data generation in `FakeData.kt` and `ToolsLogViewModel.kt`.

---

### 2025-07-19

**Summary:**

Refactor MCP Server Integration Screen and ViewModel

**Details:**

- **New Feature:**
    - Implemented a new UI for the MCP Server Integration screen with detailed log views, search functionality, and filtering options.
- **Refactoring:**
    - Moved and completely refactored `MCPServerIntegrationScreen.kt` to support the new UI.
    - Refactored `MCPServerIntegrationViewModel.kt` to use `ViewModel` and `StateFlow` for better state management.
    - Updated `App.kt` to integrate the new MCP Server Integration screen and ViewModel.
    - Added `MCPServerRepository.kt` and `MCPServerRepositoryImpl.kt` for data handling.
- **Code Cleanup:**
    - Removed the `-Dskiko.renderApi=Software` JVM argument from `composeApp/build.gradle.kts`.
    - Corrected the package declaration in `composeApp/src/desktopTest/kotlin/ai/mcpdirect/studio/app/ComposeAppDesktopTest.kt`.
    - Renamed "Agent Talk" to "Agent Pass" in `composeApp/src/desktopMain/composeResources/values/strings.xml`.
- **File Deletions:**
    - Removed the unused `ToolsLogbookViewModel.kt`.

---

## 2025-07-12

**Summary:** Clean up: Remove unused ToolManagementScreen

**Detail:**
- Removed the `ToolManagementScreen` composable and its related entries from `App.kt` as it is no longer used.

---

## 2025-07-12

**Summary:** Integrate tool logging across MCPwings projects

**Detail:**
- Updated `../mcpwings-gateway/src/main/java/appnet.hstp.labs.ai/mcpwings/util/AITool.java` to handle `McpSchema.Implementation` client info and add `X-MCP-Client-Name` header to service requests.
- Modified `../mcpwings-workshop/src/main/java/appnet.hstp.labs.aiport.mcpwings/MCPwingsWorkshop.java` to include `setToolsLogHandler` and `logToolUsage` methods.
- Updated `../mcpwings-workshop/src/main/java/appnet.hstp.labs.aiport.mcpwings/service/AIToolsServiceHandler.java` to pass agent name to `MCPwingsWorkshop.logTools`.
- Modified `../mcpwings-workshop/src/main/java/appnet.hstp.labs.aiport.mcpwings/tool/util/ToolsLogHandler.java` to accept `agentName`, `makerName`, and `toolName` in the `log` method.

---

## 2025-07-12

**Summary:** Fix: Invalid banner image format

**Detail:**
- Resolved `java.lang.IllegalArgumentException: Failed to Image::makeFromEncoded` by identifying that the `mcpwings-workshop-banner.png` was a placeholder text file.
- **Action Required:** Replace `/home/robin/CodeHub/projects/appnet-hstp-labs/ai/mcpwings-workshop-app-kmp/composeApp/src/desktopMain/composeResources/drawable/mcpwings-workshop-banner.png` with a valid PNG image.

---

## 2025-07-12

**Summary:** Update aiport-admin pom.xml version

**Detail:**
- Updated `aiport-admin/pom.xml` version from `1.2.4-SNAPSHOT` to `1.2.5-SNAPSHOT`.

---

## 2025-07-12

**Summary:** Add banner to NavigationRail

**Detail:**
- Added `mcpwings-workshop-banner.png` to the top of the NavigationRail in `App.kt`.
- Created a placeholder image file for `mcpwings-workshop-banner.png`.

---

## 2025-07-12

**Summary:** Refactor: Streamline authentication and update UI text

**Detail:**
- Streamlined the user registration flow by integrating the password setting step into the OTP verification screen, removing the need for a separate "Set Password" screen.
- Simplified the password reset process by allowing users to set a new password directly after OTP verification.
- Updated UI text for navigation and buttons to improve clarity and consistency (e.g., "MCP Extensions" to "Connect MCP").
- Refined backend authentication logic to support the new, more efficient user flows and improve account verification.

---

## 2025-07-12

**Summary:** Refactor: Standardize MCPwingsWorkshop naming and enhance UI

**Detail:**
- Standardized the naming of `MCPwingsWorkshop` across the application to ensure consistency.
- Enhanced the Agent Interaction screen with improved API key display and management.
- Added conditional visibility for save options.
- Included new icons for enabling, disabling, and editing API keys.
- Updated the application theme and color scheme.

---

## 2025-07-10

**Summary:** Fixed syntax error in `AgentInteractionScreen.kt`.

**Detail:**
- Added missing closing brace `}` in `AgentInteractionScreen.kt` to resolve syntax error.

---

## 2025-07-10

**Summary:** Implemented confirmation messages using Snackbar in AgentInteraction screen.

**Detail:**
- Added `snackbarHostState` and `showSnackbar` function to `AgentInteractionViewModel.kt`.
- Modified `AgentInteractionScreen.kt` to include `SnackbarHost`.
- Updated `copyToClipboard` and `saveKeyLocally` functions to display confirmation messages via Snackbar.

---

## 2025-07-10

**Summary:** Implemented Agent Interaction screen with API key management.

**Detail:**
- Created `AgentInteractionScreen.kt` for UI and `AgentInteractionViewModel.kt` for logic.
- Added features for generating API keys, listing existing keys (name, created, masked key, status), and enabling/disabling/deprecating keys.
- Integrated `AgentInteractionScreen` into `App.kt` navigation.
- Added local file saving and clipboard copy functionality for generated keys.

---

## 2025-07-10

**Summary:** Display exceptions in `AddServerDialog` using an `AlertDialog`.

**Detail:**
- Modified `MCPServerIntegrationViewModel.kt` to set an `errorMessage` state variable when exceptions occur during server addition, JSON conversion, or clipboard paste.
- Modified `MCPServerIntegrationScreen.kt` to display an `AlertDialog` showing the `errorMessage` when it is not null.

---

## 2025-07-10

**Summary:** Ensured exception handling in `AddServerDialog`.

**Detail:**
- Confirmed that `try-catch` blocks are in place within the `addServer()` function in `MCPServerIntegrationViewModel.kt` to handle exceptions during both JSON and form-based server additions.

---

## 2025-07-10

**Summary:** Fixed `Unresolved reference 'colors'` errors in authentication screens.

**Detail:**
- Replaced `MaterialTheme.colors.error` with `MaterialTheme.colorScheme.error` in `ForgotPasswordScreen.kt` and `RegisterScreen.kt`.

---

## 2025-07-10

**Summary:** Added validation for empty email/password fields in authentication screens.

**Detail:**
- Modified `AuthViewModel.kt` to include validation checks for empty email and password fields in login, registration, and forgot password flows.
- Updated `LoginScreen.kt`, `RegisterScreen.kt`, and `ForgotPasswordScreen.kt` to display error messages when email or password fields are empty.

---

## 2025-07-10

**Summary:** Added validation to `AddServerDialog` in `MCPServerIntegrationScreen.kt`.

**Detail:**
- Modified `MCPServerIntegrationViewModel.kt` to include validation logic for server name, command (for stdio type), and URL (for sse type).
- Modified `MCPServerIntegrationScreen.kt` to display error messages for invalid inputs in the `AddServerDialog`.

---

## 2025-07-10

**Summary:** Fixed deprecated `Icons.Default.List` usage.

**Detail:**
- Replaced `Icons.Default.List` with `Icons.AutoMirrored.Filled.List` in `MCPServerIntegrationScreen.kt` to address deprecation warning.

---

## 2025-07-10

**Summary:** Rebuilt `MCPServerIntegrationScreen.kt` UI to use an inbox layout.

**Detail:**
- Modified `MCPServerIntegrationScreen.kt` to conditionally display either the server list or server details.
- Added a "Back" button to `ServerDetailsCard` to navigate back to the server list.

---

## 2025-07-10

**Summary:** Enabled space key input in authentication text fields.

**Detail:**
- Modified `LoginScreen.kt`, `RegisterSetPasswordScreen.kt`, and `SetNewPasswordScreen.kt`.
- Updated the `onKeyEvent` modifiers to explicitly allow `Key.Spacebar` events to pass through, resolving the issue where space key input was not registered.

---

## 2025-07-10

**Summary:** Set all text fields to single line except Server JSON.

**Detail:**
- Modified `MCPServerIntegrationScreen.kt`, `LoginScreen.kt`, `RegisterSetPasswordScreen.kt`, and `SetNewPasswordScreen.kt`.
- Added `singleLine = true` to all `OutlinedTextField` components, except for the `OutlinedTextField` used for "Server JSON" in `MCPServerIntegrationScreen.kt`.

---

## 2025-07-10

**Summary:** Enabled tab key navigation and show/hide password functionality for input fields.

**Detail:**
- Modified `LoginScreen.kt`, `RegisterSetPasswordScreen.kt`, and `SetNewPasswordScreen.kt`.
- Implemented `FocusRequester` and `onKeyEvent` modifiers to enable tab key navigation between input fields.
- Added show/hide password toggle to password input fields using `PasswordVisualTransformation`, `VisualTransformation.None`, `IconButton`, and `Icons.Default.Visibility`/`Icons.Default.VisibilityOff`.

---

## 2025-07-10

**Summary:** Fixed password fields to hide password text.

**Detail:**
- Modified `LoginScreen.kt`, `RegisterSetPasswordScreen.kt`, and `SetNewPasswordScreen.kt`.
- Added `PasswordVisualTransformation` to the `OutlinedTextField` components for password input to ensure that the entered text is obscured.

---

## 2025-07-09

**Summary:** Fixed conditional display of Publish/Discontinue buttons.

**Detail:**
- Modified `MCPServerIntegrationViewModel.kt`:
    - Ensured `selectServer` is called after `publishServer` and `discontinueServer` to refresh `selectedServer` and trigger UI recomposition.

---

## 2025-07-09

**Summary:** Added Publish/Discontinue buttons to Server Details Card.

**Detail:**
- Modified `MCPServerIntegrationScreen.kt`:
    - Passed `viewModel` to `ServerDetailsCard`.
    - Implemented conditional rendering of "Publish" and "Discontinue" buttons based on `server.id`.
    - Linked button `onClick` events to `viewModel.publishServer` and `viewModel.discontinueServer`.
- Modified `MCPServerIntegrationViewModel.kt`:
    - Added placeholder `publishServer` and `discontinueServer` functions.

---

## 2025-07-09

**Summary:** Integrated MCPWingsWorkshop backend for user authentication.

**Detail:**
- Modified `UserRepositoryImpl.kt` to use `MCPWingsWorkshop.login` and `MCPWingsWorkshop.getUserInfo` for user authentication.
- Added `MCPWingsWorkshop.start()` call in `main.kt` to initialize the backend.

---

## 2025-07-09

**Summary:** Added JSON edit view to "Add Server" dialog with clipboard functionality.

**Detail:**
- Modified `MCPServerIntegrationViewModel.kt`:
    - Added `showJsonView` and `serverJsonString` state variables.
    - Implemented `convertFormToJson()`, `convertJsonToForm()`, and `pasteJsonFromClipboard()` functions for JSON serialization/deserialization and clipboard integration.
    - Updated `addServer()` to use JSON data if `showJsonView` is enabled.
- Modified `MCPServerIntegrationScreen.kt`:
    - Added `TabRow` to `AddServerDialog` for switching between "Form" and "JSON" views.
    - Implemented JSON edit UI with `OutlinedTextField` for JSON input, "Paste from Clipboard", "Load JSON", and "Generate JSON" buttons.
    - Added necessary imports for `TabRow`, `Tab`, and new icons.

---

## 2025-07-09

**Summary:** Enhanced server creation with dynamic arguments and externalized server addition logic.

**Detail:**
- Modified `MCPServerIntegrationViewModel.kt`:
    - Changed `newServerArgs` from `String` to `MutableList<String>`.
    - Removed `onNewServerArgsChange` function.
    - Integrated `MCPWingsWorkshop.addMCPServer` for externalized server creation, passing environment variables as a `Map`.
- Modified `MCPServerIntegrationScreen.kt`:
    - Updated `AddServerDialog` to replace the single `OutlinedTextField` for server arguments with a dynamic list of input fields, including add/remove functionality.
    - Added scrolling to server list and details panes for improved usability.

---

## 2025-07-09

**Summary:** Changed "Environment Variables" input to a map in "Add Server" dialog.

**Detail:**
- Modified `MCPServerIntegrationViewModel.kt`:
    - Changed `newServerEnv` from `String` to `MutableList<Pair<String, String>>`.
    - Removed `onNewServerEnvChange` function.
    - Updated `addServer` function to handle the new `newServerEnv` type and convert it to `List<EnvironmentVariable>`.
- Modified `MCPServerIntegrationScreen.kt`:
    - Updated `AddServerDialog` to replace the single `OutlinedTextField` for environment variables with a dynamic list of key-value input fields, including add/remove functionality.

---

## 2025-07-07

**Summary:**

Updated `MCPServer` entity and related components to include `usl` and `hash` fields.

**Details:**

- **Data Model:**
    - Modified `MCPServer.kt` to add `usl` (String) and `hash` (Long) properties.
- **Repository:**
    - Updated `MCPServerRepository.kt` and `MCPServerRepositoryImpl.kt` to handle the new `usl` and `hash` fields when adding and retrieving MCP servers.
- **UI/ViewModel:**
    - Adjusted `MCPServerIntegrationScreen.kt` and `MCPServerIntegrationViewModel.kt` to display and manage the new `usl` and `hash` fields.

## 2025-07-09

**Summary:** Refactored MCP Server Integration to use external `aiport-mcpwings-workshop` module.

**Detail:**
- Externalized `MCPServer` data model to `appnet.hstp.labs.aiport.mcpwings.dao.entity.MCPServer`.
- Updated `MCPServerIntegrationScreen.kt` to reflect changes in `MCPServer` properties and tool handling.
- Refactored `MCPServerRepository` and `MCPServerRepositoryImpl` to use the external `MCPServer` model and rely on external `addServer` logic.
- Simplified `settings.gradle.kts` by removing numerous `includeBuild` statements, indicating a shift in module dependency management.

---

## 2025-07-09

**Summary:** Fix "Add Server" dialog not appearing on click.

**Detail:**
- In `MCPServerIntegrationScreen.kt`, the `MCPServerIntegrationViewModel` was being re-initialized on every recomposition, causing the state of the `showAddServerDialog` to be lost.
- The fix was to use `remember { MCPServerIntegrationViewModel() }` to ensure the same ViewModel instance is used across recompositions. This preserves the state of the dialog.

---

## 2025-07-09

**Summary:** Fixed "Add Server" functionality in MCP Server Integration.

**Details:**

- Moved the `_mockServers` list in `MCPServerRepositoryImpl.kt` to a companion object to ensure a single, shared instance of the server list across the application. This resolves the issue where new servers were added to a temporary, unobserved list, making them disappear after being added.

---

## 2025-07-08

**Summary:** Updated window icon loading to use `compose-resources` plugin.

**Details:**

- Replaced deprecated `painterResource("mcpwings-icon_256.png")` with `painterResource(Res.drawable.mcpwings_icon_256)` in `main.kt`.

---

## 2025-07-08

**Summary:** Moved MCPwings icon files and set window icon.

**Details:**

- Moved `mcpwings-icon` PNG files from `../mcpwings-workshop/src/main/resources/webapp/` to `composeApp/src/desktopMain/composeResources/drawable/`
- Modified `main.kt` to set the window icon using `painterResource("mcpwings-icon_256.png")`.

---

## 2025-07-08

**Summary:** Added MCPwings icon to the desktop application window.

**Details:**

- Modified `main.kt` to set the window icon using `painterResource("mcpwings-icon_256.png")`.

---

## 2025-07-08

**Summary:** Added MCPwings workshop icon files.

**Details:**

- Added various icon files (`WechatIMG184.png`, `mcpwings-icon_128.png`, `mcpwings-icon_16.png`, `mcpwings-icon_256.png`, `mcpwings-icon_32.png`, `mcpwings-icon_512.png`, `mcpwings-icon_64.png`) to `../mcpwings-workshop/src/main/resources/webapp/`.

---

## 2025-07-08

**Summary:** Set default window size for the desktop application.

**Details:**

- Modified `main.kt` to set the initial window size to 1024x768 using `rememberWindowState` and `DpSize`.

---

## 2025-07-08

**Summary:** Implemented "Add MCP Server" functionality and refactored MCP server types.

**Details:**

- Modified `MCPServer.kt` to use a sealed interface for `MCPServer` (Stdio and SSE types) and added `EnvironmentVariable` data class.
- Updated `MCPServerRepository.kt` and `MCPServerRepositoryImpl.kt` to support the new `MCPServer` types and added `addServer` function.
- Enhanced `MCPServerIntegrationViewModel.kt` with state variables and functions for the "Add Server" dialog and server creation logic.
- Modified `MCPServerIntegrationScreen.kt` to include an "Add Server" button, an `AddServerDialog` composable for input, and updated `ServerListItem` and `ServerDetailsCard` to display information based on the new `MCPServer` types.

---

## 2025-07-08

**Summary:** Implemented user authentication UI and navigation flow.

**Details:**

- Created `LoginScreen.kt`, `RegisterScreen.kt`, and `ForgotPasswordScreen.kt` with placeholder UI elements.
- Added an `AuthViewModel` to manage authentication state and navigation.

---

## 2025-07-08

**Summary:** Implemented the data layer and integrated it with the `AuthViewModel`.

**Details:**

- Created a `User` data class to represent the user model.
- Created a `UserRepository` interface and a `UserRepositoryImpl` class to handle user-related data operations.

---

## 2025-07-08

**Summary:** Implemented the registration and password reset flows.

**Details:**

- Updated the `AuthViewModel` to include methods for registration and password reset.

---

## 2025-07-08

**Summary:** Implemented social media login flows.

**Details:**

- Updated the `AuthViewModel` to include methods for Google, GitHub, and Apple logins.

---

## 2025-07-08

**Summary:** Enhanced registration and forgot password flows with OTP and password input.

**Details:**

- Modified `UserRepository` and `UserRepositoryImpl` to support OTP sending, OTP verification, and setting new passwords.

---

## 2025-07-08

**Summary:** Fixed compilation errors in `LoginScreen.kt` and `ForgotPasswordScreen.kt`.

**Details:**

- Corrected the `replace` operation in `ForgotPasswordScreen.kt` to avoid syntax errors.

---

## 2025-07-08

**Summary:** Fixed `Unresolved reference` errors by adding explicit imports.

**Details:**

- Added explicit import for `AuthScreen` in `App.kt`.

---

## 2025-07-08

**Summary:** Fixed registration OTP flow to navigate to set password screen.

**Details:**

- Modified `AuthViewModel.kt` to directly navigate to `RegisterSetPasswordScreen` upon successful OTP verification for registration.

---

## 2025-07-08

**Summary:** Added explicit OTP verification step for forgot password flow.

**Details:**

- Added `verifyOtpForForgotPassword` method to `UserRepository` and `UserRepositoryImpl`.

---

## 2025-07-08

**Summary:** Implemented MCP Server Integration feature.

**Details:**

- Created `MCPServer.kt` and `MCPTool.kt` data models.
- Defined `MCPServerRepository` interface and `MCPServerRepositoryImpl` with mock data.
- Created `MCPServerIntegrationViewModel.kt` to manage server and tool data.
- Implemented `MCPServerIntegrationScreen.kt` with master-detail layout for server list and details.
- Integrated `MCPServerIntegrationScreen` into `App.kt` navigation.

---

## 2025-07-08

**Summary:** Fixed JSON string formatting in `MCPServerRepositoryImpl.kt`.

**Details:**

- Converted multiline JSON strings to single-line escaped strings to resolve compilation errors.