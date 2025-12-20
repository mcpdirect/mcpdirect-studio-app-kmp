# Changelog

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