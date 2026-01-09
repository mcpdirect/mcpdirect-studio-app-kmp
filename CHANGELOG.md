# Changelog

## [January 2, 2026]
### Summary
Enhanced Home screen UI with improved Quick start layout

### Details
- Updated Quick start section layout in Home screen with improved alignment
- Enhanced IconButton positioning for Tips dialog in Home screen
- Improved UI consistency with CenterVertically alignment in Quick start section
- Updated Home screen layout with better component organization
- Enhanced visual hierarchy with improved spacing and alignment in Quick start section
- Improved accessibility with proper alignment of UI elements

## [January 2, 2026]
### Summary
Enhanced AI agent configuration with improved security and copy functionality

### Details
- Added 'type' property to HTTP and SSE configurations in QuickStartAIAgent
- Enhanced File System MCP server configuration with input arguments
- Improved security by truncating access key credential (removing first 4 characters)
- Added copy to clipboard functionality for AI agent configurations
- Enhanced configuration generation with improved variable substitution
- Updated access key name sanitization in configuration generation
- Improved endpoint URL handling by removing trailing slashes
- Enhanced UI with better configuration display and copy functionality
- Updated AIAgentConfigOptionView with improved configuration workflow

## [January 2, 2026]
### Summary
Improved Home screen layout with enhanced spacing and structure

### Details
- Updated Home screen layout with improved spacing between components
- Modified Home screen UI structure using Column with weight-based sizing
- Enhanced spacing consistency with uniform 16dp spacing between sections
- Improved UI layout with better component organization in Home screen
- Updated MyStudios, MCPDirectKeys, and MyTeams positioning in Home screen
- Enhanced visual hierarchy with improved spacing and layout structure

## [January 2, 2026]
### Summary
Improved MCP server catalog UI and key generation workflow

### Details
- Updated MCP server catalog toggle functionality with improved UI flow
- Modified ConnectMCPView to default to installed servers view instead of catalog
- Enhanced catalog toggle behavior with improved action buttons
- Improved GenerateMCPdirectKeyView UI with better key list and generation flow
- Added conditional rendering for key generation and key list views
- Updated QuickStart screen UI components with improved layout and navigation
- Enhanced StudioActionBar functionality in catalog views
- Improved access key selection workflow in key generation UI
- Fixed currentToolAgent assignment logic in ConnectMCPView
- Enhanced UI consistency with proper conditional rendering

## [January 2, 2026]
### Summary
Enhanced Home screen with Tips dialog automation and UI improvements

### Details
- Implemented automatic Tips dialog display when no MCP servers are installed
- Added showTips state management in HomeViewModel for Tips dialog control
- Enhanced HomeScreen to conditionally show Tips dialog based on server availability
- Updated Home screen layout with improved padding in cards
- Modified Home screen view components to accept optional modifier parameter
- Enhanced ToolRepository with loadToolMakers state flow and response callbacks
- Improved endpoint URL handling by removing trailing slashes in QuickStart screen
- Added access key name sanitization by replacing spaces with underscores
- Enhanced UI consistency with uniform padding in home view components
- Improved navigation and dialog dismissal behavior in Home screen

## [January 2, 2026]
### Summary
Enhanced MCPDirect key generation UI and repository functionality

### Details
- Enhanced MCPDirect key generation UI with improved form validation
- Added real-time validation for key name length (max 20 characters)
- Implemented generate key functionality with success/error handling
- Added toggle between key list and generate form in QuickStart screen
- Updated AccessKeyRepository to support response callbacks for key generation
- Fixed background color from FAFAFA to pure white (FFFFFF)
- Enabled grantToolPermissions functionality that was previously commented out
- Improved endpoint URL handling by removing trailing slashes
- Added MCPKeyNameError import to QuickStartViewModel
- Enhanced UI with proper error messaging and validation feedback

## [January 2, 2026]
### Summary
Added Tips dialog to Home screen and enhanced window controls

### Details
- Added Tips dialog functionality to Home screen with lightbulb icon
- Implemented FullControlDialog composable for displaying Tips in a modal
- Updated navigation from Tips screen to return to Home screen instead of Tips
- Enhanced window controls in main.kt with minimize, maximize, and close functionality
- Added Swing utilities for window state management (minimize, maximize, close)
- Implemented dynamic padding and shadow elevation for window states
- Added maximize toggle functionality that adjusts window styling accordingly
- Improved window close button to properly exit the application
- Added DialogProperties for proper dialog behavior and dismissal
- Enhanced UI with rounded corner surface for the Tips dialog

## [January 2, 2026]
### Summary
Enhanced Home screen UI with MCPDirect branding and improved navigation

### Details
- Enhanced Home screen with MCPDirect logo and branding elements
- Added refresh functionality to Home screen for all data views
- Updated LoginScreen to use higher resolution text logo (256px)
- Modified MyStudioScreen, MCPAccessKeyScreen, and MCPTeamScreen to accept padding values
- Implemented navigation from Home screen cards to respective detail screens
- Added new mcpdirect_text_logo_150.png and mcpdirect_text_logo_256.png resources
- Updated window dimensions from 1200x900 to 1280x960 in main.kt
- Improved UI elements in QuickStartScreen for generating MCPDirect keys
- Enhanced padding and layout in various screens for better UI consistency
- Added hover pointer icons to UI elements for better user experience
- Improved button styling and sizing in key management views

## [January 2, 2026]
### Summary
Implemented new Home screen with enhanced UI and window management features

### Details
- Implemented new Home screen with MCPDirect Keys, Studios, Teams, and Servers views
- Added HomeViewModel with data flows for access keys, tool agents, tool makers, and teams
- Created dedicated view components for MCPDirectKeys, MCPServers, MyStudios, and MyTeams
- Added new SVG icons (check_box_outline_blank, group, home, toggle_off, toggle_on)
- Implemented hover effects and improved UI interactions for cards
- Added mcpdirect_tips_600.png image resource for dashboard
- Updated app version from 2.2.2 to 2.3.0 in build.gradle.kts
- Added 'home' string resource for UI localization
- Changed default screen from Dashboard to Home in GeneralViewModel
- Implemented custom window management with resize edges in main.kt
- Added rounded corner window styling with shadow elevation
- Updated background color from FDF8F8 to FAFAFA
- Modified QuickStartScreen to accept padding values and adjusted step text
- Enhanced dashboard UI with improved layout and navigation elements
- Added custom resize functionality for application window

## [January 2, 2026]
### Summary
Enhanced dashboard UI and MCP server configuration with new icons and layout improvements

### Details
- Enhanced dashboard UI with improved layout using weight-based sizing
- Added list views for MCPDirect keys, studios, and teams with count indicators
- Implemented user filtering in MyStudiosCard to show only user's studios
- Added new SVG icons for UI elements (asterisk, captive_portal, http, label, language, link, parameter, symbol-parameter, terminal)
- Improved MCP server configuration UI with icon labels for name, command, arguments, URL, and environment variables
- Enhanced ConfigMCPServerDialog with better form layout and icon indicators
- Updated QuickStartScreen UI layout for access key generation
- Added cloud_off icon for offline status indicators
- Improved text overflow handling with MiddleEllipsis in studio card
- Added size modifier to code file icon in OpenAPI configuration

## [January 2, 2026]
### Summary
Added Context7 MCP server and enhanced Quick Start UI with access key management

### Details
- Added Context7 MCP server configuration to mcpServerCatalog with API key support
- Enhanced GenerateMCPdirectKeyView with current access key display and generation UI
- Modified QuickStartViewModel to support nullable access key selection
- Updated TipsScreen UI with improved layout, styling, and updated text
- Added steps display for MCP server connection process in TipsScreen
- Implemented access key selection functionality in QuickStartScreen
- Added background styling for step indicators in TipsScreen
- Updated "Let MCP power your business" text to "Let MCP power your work"
- Added Button and background imports to TipsScreen for enhanced UI components

## [December 25, 2025]
### Summary
Added code file icon and enhanced OpenAPI configuration and access key management

### Details
- Added new code_file.svg resource for code editing icon
- Changed parameter name from "openAPIServerConfig" to "openapiServerConfig" in Platform.kt
- Added modifyOpenAPIServer function to ConfigOpenAPIServerViewModel
- Updated edit icon in ConfigOpenAPIServerView to use code_file resource
- Added logic to create and apply new OpenAPIServerConfig in onConfirmRequest
- Added helper functions to update MCPServers and OpenAPIServers in StudioRepository
- Refactored existing code in StudioRepository to use the new update functions
- Updated AIAgentConfigOptionView to use access keys and credentials
- Added credential replacement logic in AIAgentConfigOptionView
- Added AppInfo import for gateway endpoint access
- Added AccessKeyRepository import for credential management
- Minor formatting changes in AccessKeyRepository

## [December 25, 2025]
### Summary
Enhanced OpenAPI configuration validation and template management

### Details
- Added security input validation with securityErrorCount tracking in ConfigOpenAPIServerViewModel
- Updated isConfigError logic to include security validation errors
- Modified parseYaml function to support tool agent parameter for studio-based parsing
- Implemented improved UI with different button states based on configuration state
- Added wildcard imports for better code readability in ConfigOpenAPIServer.kt
- Renamed currentMCPServer to currentMCPTemplate in QuickStartScreen.kt for clarity
- Added toolAgent parameter to ConfigOpenAPIServerView function
- Updated variable references to use currentMCPTemplate in catalog view
- Improved validation for security inputs to ensure they are not blank

## [December 25, 2025]
### Summary
Implemented catalog toggle feature and restructured MCP server management UI

### Details
- Added catalog toggle functionality to switch between "MCP Catalog" and "Installed MCP servers"
- Implemented UI restructuring to support catalog/installation views in ConnectMCPView
- Removed unused OpenAPIServerDoc import from QuickStartScreen.kt
- Removed MCPServerCatalogView composable function and integrated its functionality into ConnectMCPView
- Enhanced StudioActionBar with toggle button between catalog and installed servers views
- Updated UI layout weights and styling for better user experience
- Added currentMCPServer state management for catalog selection
- Implemented installMCPServer function within the catalog view context

## [December 25, 2025]
### Summary
Updated security configuration UI with improved styling and component changes

### Details
- Changed from OutlinedTextField to TextField for security configuration inputs
- Commented out shape property in security configuration UI for better default styling
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