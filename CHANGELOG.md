# Changelog

## [December 25, 2025]
### Summary
Enhanced OpenAPI server configuration UI with improved path display and scrollable layout

### Details
- Updated OpenAPI path display UI with scrollable FlowRow layout and vertical scrollbar
- Added padding to horizontal divider in OpenAPI server configuration view
- Changed from OutlinedCard to Card for OpenAPI paths display component
- Arranged OpenAPI server name and URL fields in a side-by-side layout
- Added imports for UI components including VerticalScrollbar, FlowRow, and various layout elements
- Improved accessibility and usability of OpenAPI paths display with numbered entries
- Enhanced layout with proper alignment and spacing for better user experience

## [December 25, 2025]
### Summary
Enhanced OpenAPI server configuration with path display and improved parsing functionality

### Details
- Replaced convertYamlToJson with parseOpenAPIDoc function in Platform interface
- Updated parseYaml function in ConfigOpenAPIServerViewModel with simplified parameters
- Updated onConfirmRequest parameter in ConfigOpenAPIServerView to pass full config object
- Added UI component to display OpenAPI paths and tools in ConfigOpenAPIServerView
- Added Path class and paths field to OpenAPIServerDoc model for storing API endpoints
- Added status filter to tool display in QuickStartScreen to show only active tools
- Implemented parseOpenAPIDoc function in JVMPlatform with proper error handling
- Updated androidx-lifecycle version from 2.9.5 to 2.9.6 in libs.versions.toml

## [December 21, 2025]
### Summary
Implemented OpenAPI server configuration functionality with repository and platform integration

### Details
- Added OpenAPIServerConfig and OpenAPIServerDoc models to QuickStartScreen.kt
- Implemented getOpenAPIServerConfigFromStudio method in StudioRepository.kt
- Added getOpenAPIServerConfigFromStudio API endpoint in Platform.kt
- Integrated OpenAPI server configuration into ConnectMCPView in QuickStartScreen.kt
- Added InstallRTMViewModel for managing RTM installation in ConfigMCPServerDialog.kt
- Enhanced ConfigMCPServerView to accept AIPortToolAgent parameter
- Implemented OpenAPI server configuration loading with LaunchedEffect in QuickStartScreen.kt
- Added proper state management for OpenAPI server configuration in UI
- Created ConfigOpenAPIServerView with back navigation support
- Implemented proper error handling and loading states for OpenAPI server configuration

## [December 21, 2025]
### Summary
Improved OpenAPI and MCP server configuration UI with enhanced layout and validation

### Details
- Updated confirmation button text in ConfigMCPServerDialog.kt to remove redundant reference to MCP server name
- Enhanced OpenAPI server configuration UI with improved layout organization
- Moved OpenAPI server name field to appear before URL field for better user flow
- Improved security configuration UI with LazyColumn for better performance with multiple security schemes
- Added border and background styling to security configuration section for better visual separation
- Added header text for security configuration section
- Implemented error validation for security input fields
- Updated confirmation button text in ConfigOpenAPIServer.kt to remove redundant reference to OpenAPI server name
- Added padding and spacing improvements for better UI consistency
- Added fontWeight styling to security section header

## [December 21, 2025]
### Summary
Enhanced OpenAPI server configuration UI with URL and security fields

### Details
- Added URL input field with dropdown menu for available OpenAPI servers
- Implemented security configuration fields based on OpenAPI specification
- Added MCP server name input field with validation
- Enhanced ConfigOpenAPIServerView with additional UI components for server configuration
- Added dropdown menu functionality to select from available OpenAPI servers
- Implemented proper error handling and validation for URL and name fields
- Added supporting text for user guidance in configuration fields
- Improved layout with proper spacing and arrangement of UI components
- Updated error message text for better clarity

## [December 21, 2025]
### Summary
Enhanced YAML text field with optimized highlighting and performance improvements

### Details
- Implemented UltraOptimizedYamlHighlighter for faster YAML syntax highlighting
- Added OptimizedYamlHighlighter with improved multiline content handling
- Enhanced YAML parsing with better detection of multiline blocks, arrays, and key-value pairs
- Improved performance of YAML syntax highlighting with caching mechanisms
- Added better handling for boolean, number, string, and multiline content types
- Replaced basic YamlHighlighter with UltraOptimizedYamlHighlighter in YamlTextField
- Removed SimpleYamlTextField function that was no longer needed
- Added comprehensive type detection for YAML values (boolean, number, URL, email)
- Implemented efficient multiline block detection and highlighting
- Optimized the number detection algorithm for better performance

## [December 21, 2025]
### Summary
Implemented OpenAPI server configuration with YAML parsing and UI enhancements

### Details
- Created ConfigOpenAPIServerViewModel with state management for server configuration
- Added YAML parsing functionality with error handling in OpenAPI server configuration
- Implemented paste from clipboard feature in OpenAPI server configuration UI
- Added JSON tree view for displaying parsed OpenAPI specification
- Added security configuration fields for OpenAPI servers
- Updated QuickStartScreen to pass callback for YAML confirmation
- Added OpenAPI documentation field to OpenAPIServerDoc model
- Updated compose hot reload version in libs.versions.toml
- Added platform-specific implementations for YAML/JSON conversion
- Added search_off.svg icon resource for UI
- Implemented back navigation functionality in OpenAPI configuration

## [December 21, 2025]
### Summary
Removed external code editor dependencies and simplified OpenAPI configuration

### Details
- Removed Wakaztahir code editor library files (com.wakaztahir.codeeditor.*)
- Replaced SimpleYamlTextField with YamlText in ConfigOpenAPIServer.kt
- Removed unused imports related to the code editor library
- Simplified ConfigOpenAPIServerView implementation by removing complex YAML editor
- Added YamlText composable for basic YAML display without editing capabilities

## [December 21, 2025]
### Summary
Added SimpleYamlTextField and further simplified YamlTextField

### Details
- Added import for SimpleYamlTextField in ConfigOpenAPIServer.kt
- Further simplified YamlTextField.kt with additional imports and UI enhancements
- Added focus management capabilities to YamlTextField
- Added clip modifier for better UI rendering in YamlTextField

## [December 21, 2025]
### Summary
Simplified YamlTextField by removing line numbers functionality

### Details
- Removed line numbers column from YamlTextField.kt
- Removed unused imports related to line numbering and scrolling
- Simplified the YamlTextField composable function parameters
- Removed LineNumbersColumn and LineNumbersLazyColumn composables
- Streamlined the layout structure of the YamlTextField

## [December 21, 2025]
### Summary
Added OpenAPI server configuration with YAML editor functionality

### Details
- Created new ConfigOpenAPIServer.kt file with OpenAPI server configuration UI
- Added YamlEditor.kt with full-featured YAML editor component with syntax highlighting
- Implemented YamlTextField.kt with custom YAML syntax highlighting and line numbers
- Added YAML color scheme and highlighting functions for proper syntax visualization
- Integrated OpenAPI server configuration into the QuickStartScreen
- Added import for ConfigOpenAPIServerView in QuickStartScreen.kt
- Implemented YAML highlighting transformation for enhanced editing experience
- Added line numbers column and status bar to YAML editor

## [December 21, 2025]
### Summary
Code cleanup and import optimizations in QuickStartScreen

### Details
- Consolidated imports using wildcard (*) for ai.mcpdirect.studio.app.compose.*
- Simplified imports by using wildcard (*) for mcpdirectstudioapp.composeapp.generated.resources.*
- Removed unused imports including VerticalScrollbar, rememberScrollState, rememberScrollbarAdapter, verticalScroll, Serializable, and Json
- Removed unused variables and simplified code formatting in several functions
- Removed the SimpleSchema data class and convertSchemaToReadableText function that were no longer needed
- Added checkbox functionality for selecting access keys in GenerateMCPdirectKeyView

## [December 21, 2025]
### Summary
Improved UI responsiveness and fixed tool maker update in QuickStartScreen

### Details
- Wrapped viewModel.updateCurrentToolMaker in LaunchedEffect to improve UI responsiveness
- Adjusted the UI layout in MCPServerMainView to show loading state differently
- Updated the loading text to include the tool maker name for better UX
- Made minor formatting improvements to code in QuickStartScreen.kt

## [December 21, 2025]
### Summary
Refined state management in MCPServerMainView and added rememberSaveable import

### Details
- Moved currentTool state declaration back inside the conditional block in MCPServerMainView
- Restored LaunchedEffect for resetting currentTool when toolMaker changes
- Added import for androidx.compose.runtime.saveable.rememberSaveable
- Adjusted the placement of state variables for better composability

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