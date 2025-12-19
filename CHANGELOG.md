# Changelog

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