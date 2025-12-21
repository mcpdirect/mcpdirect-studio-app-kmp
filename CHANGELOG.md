# Changelog

## [December 21, 2025]
### Summary
Optimized tool filtering and improved UI state management in MCPServerMainView

### Details
- Moved currentTool state declaration outside the conditional block in MCPServerMainView
- Implemented derivedStateOf for efficient tool filtering based on toolMaker.id
- Added LaunchedEffect to reset currentTool when toolMaker changes
- Restructured tool details loading with proper state management
- Separated tool selection UI from details display for better performance
- Updated tools iteration to use the filtered tools list instead of full viewModel.tools

## [December 18, 2025]
### Summary
Added JSON tree viewer component and enhanced tool details view

### Details
- Added new JsonTree.kt file with JsonTreeView composable for displaying JSON in a tree structure with expand/collapse functionality
- Added JsonTreeNode sealed class with ObjectNode, ArrayNode and PrimitiveNode implementations
- Implemented JsonTreeParser to convert JSON strings to tree nodes with filtering capabilities
- Integrated JSON tree viewer into QuickStartScreen.kt for displaying tool input schemas
- Added tool details view with description and input schema tabs in QuickStartScreen.kt
- Added secondary tab row for switching between description and input schema
- Created SimpleSchema data class and convertSchemaToReadableText function for schema display

## [December 18, 2025]
### Summary
Enhanced UI components, snackbar functionality, and MCP server configuration handling

### Details
- Enhanced snackbar with color customization based on message type in NavigationTopBar.kt
- Updated padding/spacing in ConfigMCPServerDialog.kt for better UI consistency
- Fixed args logic in ConfigMCPServerDialog.kt to properly handle input arguments
- Improved tool display layout in QuickStartScreen.kt using FlowRow instead of LazyColumn
- Extracted installMCPServer function in QuickStartScreen.kt for better code organization
- Removed unused imports and cleaned up commented code in GeneralViewModel.kt
- Minor formatting improvements in QuickStartViewModel.kt

## [December 18, 2025]
### Summary
Renamed dialog, improved API response handling, and enhanced tool agent management

### Details
- Renamed ConfigMCPServerFromTemplateDialog.kt to ConfigMCPServerByTemplateDialog.kt
- Updated function name from ConfigMCPServerFromTemplatesDialog to ConfigMCPServerByTemplateDialog
- Renamed getMakerTemplateConfigFromStudio to getToolMakerTemplateConfigFromStudio in StudioRepository
- Updated API response handling to use AIPortServiceResponse consistently
- Added currentToolAgent state management in QuickStartViewModel
- Improved UI layout and added tool agent selection in QuickStartScreen
- Removed redundant parameter in modifyToolMakerStatus function
- Updated all relevant function calls to use the new currentToolAgent instead of localToolAgent
- Added more icon import and adjusted StudioBoard sizing

## [December 18, 2025]
### Summary
Added modifyMCPServerForStudio function and improved MCP configuration change detection

### Details
- Updated ConfigMCPServerView function signature to include change detection
- Added logic to compare current config values with previous ones to detect changes
- Changed name property in MCPConfig from String to String?
- Added modifyMCPServerForStudio function in StudioRepository with separate parameters for name, status, and config
- Updated modifyMCPServerConfigForStudio to use the new parameters
- Added "-y" flag to Chrome DevTools in QuickStartAIAgent
- Updated function calls in QuickStartScreen to handle change detection
- Modified modifyMCPServerConfig in QuickStartViewModel to accept name, status, and config separately

## [December 18, 2025]
### Summary
Major UI improvements to MCP server configuration screen and refactored dialogs

### Details
- Added imports, segmented buttons for transport selection, input validation and UI enhancements in ConfigMCPServerDialog.kt
- Refactored dialog system to action-based navigation in QuickStartScreen.kt
- Created new MCPServerMainView composable for better UI separation
- Implemented back navigation in configuration views
- Renamed "General" to "MCP Server" in server catalog
- Changed button text from "Install" to "Confirm" in configuration views

## [December 18, 2025]
### Summary
Fixed MCP server configuration saving and adjusted default status

### Details
- Added config arguments and environment variable assignments in ConfigMCPServerDialog.kt
- Changed default status from 0 to 1 in MCPConfig.kt
- Added JSON import and commented debug print statement in QuickStartScreen.kt

## [December 18, 2025]
### Summary
Updated MCP server connection handling and improved UI state management

### Details
- Modified parameter destructuring in MyStudioViewModel.kt
- Added config name assignment in ConfigMCPServerDialog.kt
- Updated response handling mechanism in StudioRepository.kt for MCP server connections
- Improved UI state management in QuickStartScreen.kt
- Refactored QuickStartViewModel.kt with better state flow management using MutableStateFlow
- Added installMCPServer function to QuickStartViewModel
- Various UI improvements including loading indicators and state updates

All notable changes to this file.