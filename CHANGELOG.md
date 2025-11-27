# Changelog

All notable changes to this file.

## 2025-11-27
### Added
- Enhanced MyStudioViewModel.kt with toolMakers state flow and refreshToolMakers function
- Added user function to UserRepository.kt with response callback

### Changed
- Updated MyStudioScreen.kt to use view model's refreshToolMakers function
- Modified MyStudioScreen.kt to use UserRepository for user lookups
- Updated MCPTemplateListView.kt to use UserRepository for user lookups
- Removed authViewModel import from MyStudioScreen.kt
- Removed unused import from ConnectMCPTemplateDialog.kt
- Improved user lookup logic in MyStudioScreen.kt with LaunchedEffect

## 2025-11-27
### Added
- Enhanced TeamRepository.kt with team tool maker and template functions
- Added toolMakers function to ToolRepository.kt for filtering by agent
- Added refresh functions to ToolPermissionViewModel.kt for teams, tool makers and templates
- Enhanced ToolPermissionViewModel.kt with team state flow and repository integration

### Changed
- Updated ToolPermissionScreen.kt to use repository-based tool maker filtering
- Modified ToolPermissionScreen.kt to replace generalViewModel calls with view model calls
- Updated ToolPermissionViewModel.kt to use proper state flows for tool agent and team
- Enhanced ToolPermissionViewModel.kt with improved data binding and repository integration

## 2025-11-27
### Added
- Modified Screen.kt to make ToolPermission a parameterized screen with AIPortAccessKey
- Added accessKey parameter to ToolPermissionScreen.kt
- Enhanced ToolPermissionViewModel.kt with proper state flows and repository integration
- Added queryVirtualTools parameters in Platform.kt interface

### Changed
- Updated App.kt to pass accessKey parameter to ToolPermissionScreen
- Modified AuthViewModel.kt to comment out unused imports and reset calls
- Updated MCPAccessKeyScreen.kt to use local view model instance and updated UI
- Enhanced AccessKeyRepository.kt with generate, modify, and get credential functions
- Updated ToolRepository.kt to include tool permission and virtual tool functions
- Modified MCPTeamToolMakerViewModel.kt to update queryVirtualTools call
- Updated ToolPermissionScreen.kt to use view model state flows and parameterized functions
- Enhanced ToolPermissionViewModel.kt with improved state management and repository integration
- Updated VirtualMakerViewModel.kt to fix queryVirtualTools call

## 2025-11-26
### Added
- Added close.svg icon resource
- Created UserRepository.kt with me() function for user identity verification
- Added reset() functions to repository classes for state management

### Changed
- Updated app version from 2.2.0 to 2.2.1 in build.gradle.kts
- Updated mcpdirect-studio-core dependency to 2.2.1-SNAPSHOT
- Replaced direct authViewModel.user checks with UserRepository.me() calls throughout the application
- Updated NavigationTopBar.kt to use UserRepository.me for user display
- Modified MyStudioScreen.kt to use UserRepository.me for tool agent filtering
- Updated Team screens to use UserRepository.me for user identity checks
- Updated VirtualMakerScreen.kt and VirtualToolMakerListView.kt to use UserRepository.me
- Modified ConnectMCPScreen.kt to use UserRepository.me for user checks
- Updated ToolPermissionViewModel.kt to use UserRepository.me
- Removed unused code in GeneralViewModel.kt and AuthViewModel.kt
- Updated Wizard components to remove unnecessary OutlinedCard wrapper
- Modified Shortcut interface to remove modifier parameter from wizard function
- Updated MCPAccessKeyScreen.kt to remove loading state display
- Enhanced AIPortUser.kt with proper constructor and ANONYMOUS constant
- Updated Platform.jvm.kt to pass account information in user object
- Updated ToolRepository.kt to use UserRepository.me for createToolMaker function

## 2025-11-26
### Changed
- Updated README.md and README.zh-CN.md to include MCP Server Template and OpenAPI Server as MCP features
- Added MCP Server Template to the features list in documentation
- Renamed OpenAPI server as MCP to OpenAPI Server as MCP in documentation

## 2025-11-26
### Changed
- Updated MyStudioScreen.kt to simplify tool agent selection logic by removing loading state check
- Enhanced MyStudioViewModel.kt to update tool agent value before querying tool makers

## 2025-11-25
### Changed
- Added "java.sql" module to native distributions in build.gradle.kts
- Updated native distributions to include both java.naming and java.sql modules

## 2025-11-20
### Added
- Created new Wizard.kt composable component with WizardStep, WizardStepIndicator, and WizardNavigation components
- Added Wizard implementation to DashboardScreen.kt with multiple setup steps

### Changed
- Updated main.kt to center the application window on launch
- Enhanced NavigationTopBar.kt with horizontal center alignment for content
- Redesigned DashboardScreen.kt with a new layout featuring cards for Studios, MCP Tools, Keys, and Teams
- Modified DashboardScreen.kt to include shortcut list and wizard component

## 2025-11-20
### Changed
- Updated theming system in Color.kt and Theme.kt with new color schemes
- Modified NavigationTopBar.kt to change selected navigation item appearance using background color
- Updated MCPAccessKeyScreen.kt to use OutlinedCard instead of StudioCard
- Modified StudioCard.kt to use OutlinedCard as default implementation
- Updated StudioActionBar.kt to remove background color styling
- Enhanced StudioListItem.kt to improve selected item styling with colors
- Updated MCPAccessKeyScreen.kt UI implementation using LazyColumn with custom list items
- Modified MCPAccessKeyViewModel.kt to update access key status values (0 to -1 for disabled, 1 to 0 for enabled)

## 2025-11-20
### Changed
- Updated ConnectMCPScreen.kt to use STATUS_WAITING constant for enabling/disabling UI elements
- Enhanced MyStudioScreen.kt to use STATUS_WAITING constant when selecting active tool maker items
- Added import for AIPortToolMaker.STATUS_WAITING constant in MyStudioScreen.kt

## 2025-11-20
### Changed
- Updated Platform.kt to change studioId parameter from Long to String for all studio-related API functions
- Enhanced MyStudioScreen.kt to use localToolAgent for identifying current device
- Updated MyStudioScreen.kt to comment out automatic navigation to ConnectMCP screen
- Modified MyStudioScreen.kt to only show template creation button for MCP tool makers (not virtual)
- Improved MyStudioScreen.kt to only show refresh button for other tool agents than local
- Added AIPortToolMaker status constants (STATUS_OFF, STATUS_ON, STATUS_ERROR, STATUS_WAITING, STATUS_ABANDONED)
- Updated various screens (MCPTeamScreen.kt, MCPTeamToolMakerScreen.kt, VirtualMakerScreen.kt, etc.) to use status constants
- Enhanced StudioRepository.kt with localToolAgent state flow and improved server management
- Updated Platform.jvm.kt to handle onToolAgentNotification and onToolMakerNotification events
- Modified ConnectMCPScreen.kt to properly display status indicators using constants
- Updated ConnectMCPViewModel.kt to use STATUS_ABANDONED constant when removing tool makers
- Changed AIPortToolAgent.engineId from Long to String

### Fixed
- Corrected status handling for tool makers in various UI components
- Fixed notification handling for MCP and OpenAPI servers in Platform.jvm.kt
- Improved status checking logic in ConnectMCPScreen.kt

## 2025-11-19
### Changed
- Renamed publishMCPToolsForStudio to publishMCPToolsFromStudio in Platform.kt
- Renamed publishOpenAPIToolsForStudio to publishOpenAPIToolsFromStudio in Platform.kt
- Updated MyStudioViewModel.kt to use publishToolsFromStudio function
- Enhanced StudioRepository.kt with unified publishToolsFromStudio function for both MCP and OpenAPI servers
- Improved notification handling in Platform.jvm.kt with status checks
- Updated ConnectMCPViewModel.kt to use Int.MIN_VALUE for status comparison
- Reduced name length limit from 32 to 20 characters in ConnectOpenAPIServerDialog
- Added optional toolPrefix field to OpenAPIServerConfig model

## 2025-11-17
### Added
- New menu.svg icon resource
- AIPortServiceResponse.OPENAPI_DOC_NOT_EXIST constant
- OpenAPIServer.statusMessage property
- AIPortToolMaker.openapi() and AIPortToolMaker.notVirtual() functions
- AIPortToolMaker.TYPE_OPENAPI constant
- onOpenAPIServerNotification function in Platform.jvm.kt
- ToolMakerItem composable function

### Changed
- Updated default gateway endpoints in build.gradle.kts from port 8080 to 8088
- Updated Platform.kt API endpoints for OpenAPI server operations
- Improved virtual() function usage in GeneralViewModel.kt
- Updated MyStudioScreen.kt with improved tool provider type selection
- Enhanced MyStudioViewModel.kt with resetToolAgent and resetToolMaker functions
- Updated StudioRepository.kt with improved name modification and status update functions
- Enhanced ConnectOpenAPIServerDialog.kt with improved dropdown menu handling
- Updated TeamToolMakerScreen.kt to use virtual() function
- Updated MCPTemplateScreen.kt to use notVirtual() function
- Updated ToolMakerListView.kt to use virtual() function
- Updated VirtualMakerViewModel.kt to use virtual() function
- Enhanced Platform.jvm.kt with notification handling for OpenAPI servers

## 2025-11-16
### Added
- New openapi.svg icon resource
- ConnectOpenAPIServerDialog composable for connecting OpenAPI servers
- ConnectOpenAPIServerViewModel for OpenAPI server connection state management
- OpenAPIToolMakerScreen composable for OpenAPI tool maker UI
- OpenAPIToolMakerViewModel for OpenAPI tool maker state management
- OpenAPIServer model class
- OpenAPIServerConfig model class
- OpenAPI MCP string resource in strings.xml
- Platform interface functions for parsing and connecting OpenAPI from studio
- ConnectOpenAPI dialog option in MyStudioScreen dropdown menu
- OpenAPIServerDoc model for OpenAPI server documentation
- StudioToolMakers model for studio tool makers
- StudioRepository for studio-related data management

### Changed
- Modified Platform.kt to update OpenAPI-related functions
- Updated MyStudioScreen.kt to properly handle ConnectOpenAPIServerDialog onConfirmRequest
- Enhanced MyStudioViewModel.kt with connectOpenAPIServer functionality
- Updated ConnectOpenAPIServerDialog.kt with improved UI and functionality
- Updated ConnectOpenAPIServerViewModel.kt to use OpenAPIServerDoc instead of OpenAPIServerConfig
- Modified OpenAPIServer.kt to update securities property type
- Updated OpenAPIServerConfig.kt with simplified structure
- Enhanced OpenAPIServerDoc.kt with proper serialization support
- Updated StudioToolMakers.kt with proper structure and serialization
- Redesigned StudioRepository.kt for studio-specific data management
- Updated MCPTemplateListViewModel.kt to include OpenAPIServerConfig import
- Enhanced GeneralViewModel.kt with improved loading and error handling functions
- Updated MyStudioViewModel.kt to use StudioRepository functions
- Enhanced StudioRepository.kt with comprehensive studio management functions
- Updated TeamRepository.kt with improved loading handling
- Enhanced ToolRepository.kt with improved tool management functions
- Updated MCPTemplateListView.kt to use ViewModel for toolAgent queries
- Updated MCPTemplateListViewModel.kt with toolAgent query function
- Updated MCPToolsScreen.kt to modify tool agent refresh behavior
- Updated ToolListViewModel.kt to use improved tool loading methods

## 2025-11-14
### Added
- New hubspot.svg icon resource

### Changed
- Bumped app version from 2.1.2 to 2.2.0 in build.gradle.kts
- Updated mcpdirect-studio-core dependency from 2.1.2-SNAPSHOT to 2.2.0-SNAPSHOT

## 2025-11-12
### Added
- New restart-circle.svg icon for restart functionality
- New repository classes for data management (TeamRepository, ToolRepository, UserRepository)
- New view models for tool management (ToolListViewModel, ToolMakerListViewModel)
- ToolMakerListView composable for displaying tool makers

### Changed
- Enhanced Platform.kt with serverStatus parameter for modifyMCPServerForStudio function
- Updated MyStudioScreen.kt and ConnectMCPScreen.kt with improved server configuration handling
- Enhanced MyStudioViewModel.kt and ConnectMCPViewModel.kt with optional onResponse parameter
- Updated ConfigMCPServerDialog.kt to pass both mcpServer and config to onConfirmRequest
- Enhanced ConnectMCPScreen.kt with server status indicators and controls (start/stop/restart)
- Added modifyToolMakerStatus functionality to ConnectMCPViewModel.kt for server status management
- Enhanced MyStudioScreen.kt with server status indicators and controls (start/stop/restart)
- Added modifyToolMakerStatus functionality to MyStudioViewModel.kt for server status management
- Reverted material3 dependency comment in build.gradle.kts
- Fixed conditional logic in ConnectMCPScreen.kt for proper UI element display
- Enhanced Platform.kt with queryTools function to include lastUpdated parameter
- Enhanced GeneralViewModel.kt with loading functions
- Updated MyStudioViewModel.kt with repository integration
- Added virtual() function to AIPortToolMaker.kt
- Updated TeamRepository.kt with team data management functions
- Updated ToolRepository.kt with tool and tool maker data management functions
- Updated UserRepository.kt with user data management functions
- Enhanced MCPToolsScreen.kt with tool list and tool maker list view models
- Updated MCPToolsViewModel.kt with simplified implementation
- Enhanced ToolListView.kt with detailed tool information display
- Updated ToolListViewModel.kt with proper tool loading and management
- Updated ToolMakerListViewModel.kt with proper tool maker loading and management
- Minor update to Platform.jvm.kt
- Added print statements for gateway endpoint configuration in build.gradle.kts
- Enhanced Platform.kt queryTeams function to include lastUpdated parameter
- Updated TeamRepository.kt with time-based caching using Kotlin's time library
- Updated ToolRepository.kt with time-based caching using Kotlin's time library
- Added refresh button to ToolListView with StudioActionBar and TooltipIconButton
- Enhanced ToolListView.kt with conditional rendering when no tool maker is selected
- Added refreshTools functionality to ToolListViewModel.kt
- Updated TeamRepository loadTeams call in ToolMakerListViewModel.kt

## 2025-11-10
### Changed
- Updated build configuration in build.gradle.kts
- Enhanced NavigationTopBar with improved functionality
- Updated theme colors and styling in purple theme
- Enhanced platform-specific code for JVM and web platforms

## 2025-11-10
### Added
- Created developer-landing-page.html for developer documentation
- Added JavaScript functionality for dark mode toggle
- Added smooth scrolling navigation to the landing page
- Added back-to-top button functionality

### Changed
- Translated and updated README.md with content from README.zh-CN.md
- Updated README.md with new sections: 'What Problems MCPdirect Solve?', 'MCPdirect Usage Scenarios', and 'How to Use MCPdirect'
- Updated README.md to MCPdirect 2.x new features
- Added unified-access-gateway-studio.png image to README
- Added unified-access-gateway.png asset back to assets/image
- Updated README.zh-CN.md with improved formatting and content
- Added width attribute to unified-access-gateway.png image in README
- Reordered and updated problem descriptions in README
- Updated use case descriptions in README

## 2025-11-10
### Changed
- Enhanced MyStudioScreen and MyStudioViewModel with further UI improvements
- Updated ToolListView with better tool display functionality

## 2025-11-10
### Changed
- Updated build configuration in build.gradle.kts
- Enhanced GeneralViewModel with additional features
- Improved NavigationTopBar with better navigation
- Updated MyStudioScreen with UI enhancements
- Improved MCPAccessKeyScreen with new functionality
- Enhanced MCPTeamScreen with improved UI
- Updated MCPTeamToolMakerScreen with better UX
- Enhanced MCPTeamToolMakerTemplateScreen with new features
- Improved MCPToolsScreen with enhanced functionality
- Updated ToolPermissionScreen with improved UX
- Enhanced VirtualMakerScreen with new capabilities
- Updated ConnectMCPScreen and ConnectMCPViewModel with improvements
- Modified web index.html for improved web experience

## 2025-11-09
### Changed
- Enhanced MyStudioScreen and MyStudioViewModel with UI improvements
- Updated ConnectMCPScreen and ConnectMCPViewModel with improved functionality

## 2025-11-09
### Changed
- Updated platform-specific code in Platform.kt and Platform.jvm.kt
- Enhanced ConnectMCPScreen and ConnectMCPViewModel with improved functionality

## 2025-11-09
### Changed
- Updated GeneralViewModel with enhanced functionality
- Improved MyStudioScreen and MyStudioViewModel with further UI enhancements
- Enhanced configuration dialog for MCP server with improved UX

## 2025-11-09
### Changed
- Updated MyStudioScreen with further UI improvements
- Enhanced configuration dialog for MCP server from template with improved UX

## 2025-11-09
### Changed
- Updated MyStudioScreen with additional UI improvements

## 2025-11-09
### Changed
- Updated MyStudioScreen with UI improvements
- Enhanced MyStudioViewModel with new functionality
- Improved MCP server tags editing dialog with better UX

## 2025-11-09
### Changed
- Updated GeneralViewModel with additional features
- Enhanced MyStudioScreen and MyStudioViewModel with new UI elements
- Improved StudioIcon with new icons
- Updated MCP server name and tags editing dialogs with improved UX

## 2025-11-07
### Added
- New ConfigMCPServerFromTemplateDialog for configuring servers from templates
- New EditMCPServerNameDialog for editing server names
- New EditMCPServerTagsDialog for editing server tags
- New data_object.svg drawable resource

### Changed
- Enhanced platform-specific code functionality
- Updated GeneralViewModel with new capabilities
- Improved MyStudioScreen and MyStudioViewModel with new features
- Enhanced virtual maker view model and tool list view
- Updated connect MCP screen and view model with improved functionality

## 2025-11-07
### Changed
- Enhanced authentication view model with improved functionality

## 2025-11-06
### Added
- New VirtualToolMakerListView component for displaying virtual tools

### Changed
- Updated dependencies in build configuration and libraries versions
- Enhanced MCP tools screen with improved functionality
- Improved virtual maker tool configuration screen
- Updated platform-specific application components for JVM and web
- Updated build configurations with new dependencies

## 2025-11-06
### Changed
- Updated entitlements.plist for composeApp
- Updated Gradle wrapper to newer version
- Added entitlements.plist to composeApp resources
- Removed unified-access-gateway.png asset
- Updated platform-specific code functionality
- Updated MCP access key screen with new functionality
- Updated web index.html with new changes
- Updated main function in JVM platform

## 2025-11-05
### Changed
- Enhanced GeneralViewModel with additional functionality
- Updated navigation screen definitions and routing
- Improved MyStudioScreen with UI updates
- Enhanced MCP access key screen functionality
- Updated team screen and view model with new features
- Improved template list view and model capabilities
- Enhanced tools screen with better UX
- Updated web index.html for improved web experience
- Updated Gradle wrapper properties

## 2025-11-05
### Changed
- Enhanced GeneralViewModel with additional functionality
- Updated navigation screen definitions and routing
- Improved MyStudioScreen with UI updates
- Enhanced MCP access key screen functionality
- Updated team screen and view model with new features
- Improved template list view and model capabilities
- Enhanced tools screen with better UX
- Updated web index.html for improved web experience
- Updated Gradle wrapper properties

## 2025-11-04
### Changed
- Updated platform-specific code functionality
- Enhanced GeneralViewModel with new capabilities
- Updated MyStudioScreen and MyStudioViewModel with new features
- Improved team tool maker models with enhanced functionality
- Enhanced team tool maker and team view models
- Improved tool permission screen and view model
- Updated team tool maker template functionality

## 2025-11-04
### Changed
- Enhanced GeneralViewModel with new capabilities
- Updated NavigationTopBar with improved navigation
- Enhanced MyStudioScreen with new UI elements and functionality
- Improved CreateMCPTemplateDialog with better UX
- Enhanced MCPToolsScreen with improved tool management
- Updated platform-specific JVM and web app components
- Improved ConnectMCP screen and view model functionality
- Enhanced main application startup and configuration

## 2025-11-04
### Added
- New ToolListView component for displaying tools
- Renamed StudioToolBar to StudioActionBar for better semantic meaning

### Changed
- Updated My Studio screen and view model with new functionality
- Enhanced MCP server configuration model
- Improved template creation dialog
- Enhanced tools screen and connect MCP screen
- Updated platform-specific JavaScript code
- Improved tool list display functionality

## 2025-11-03
### Changed
- Updated My Studio screen with UI improvements
- Improved platform-specific code functionality
- Enhanced general view model with additional features
- Updated My Studio screen and view model with new UI elements
- Improved MCP server configuration model
- Enhanced tool maker model with new capabilities
- Updated connect template dialog with improved UX
- Enhanced tool management and permission screens
- Improved tools screen and view model functionality

## 2025-11-03
### Changed
- Improved platform-specific code functionality
- Enhanced general view model with additional features
- Updated My Studio screen and view model with new UI elements
- Improved MCP server configuration model
- Enhanced tool maker model with new capabilities
- Updated connect template dialog with improved UX
- Enhanced tool management and permission screens
- Improved tools screen and view model functionality

## 2025-11-02
### Added
- New connect template dialog for MCP templates
- Renamed CreateMCPServerTemplateDialog to CreateMCPTemplateDialog for better clarity
- Updated library versions in gradle dependencies

### Changed
- Improved My Studio screen and view model functionality
- Enhanced template management and tool screen functionality
- Updated README.zh-CN.md documentation
- Updated platform-specific code and app components
- Modified MCP server configuration model

## 2025-11-02
### Added
- New template list view and view model for MCP templates
- Enhanced template management functionality
- Improved user and team member data models

### Changed
- Updated platform-specific code and general view model
- Enhanced My Studio screen and tool management
- Improved web platform app components
- Updated navigation and screen components

## 2025-11-02
### Added
- New team tool maker template screen and view model
- Enhanced team tool maker functionality with template support

### Changed
- Improved team management and tool maker screens
- Updated string resources and platform-specific code
- Enhanced general view model functionality
- Improved template creation and tool management
- Updated app components for web platform

## 2025-10-29
### Added
- New template functionality for creating MCP server templates
- Tooltips implementation for improved UI experience
- New SVG icons for UI elements
- Template-related view models and screens

### Changed
- Updated UI components and navigation elements
- Enhanced tool management and permission screens
- Improved authentication and authorization view models
- Updated gradle wrapper and build configurations
- UI state and screen navigation improvements

## 2025-10-29
### Changed
- Translated README content from Chinese to English
- Replaced README.md content with English translation of README.zh-CN.md
- Updated UI components from material to material3
- Fixed potential null pointer issue in ConnectMCP server dialog
- Improved null safety handling in application startup

## 2025-10-22
### Added
- Third iteration of changelog generation and git commit workflow
- Update workflow to ensure proper documentation of all changes

## 2025-10-21
### Added
- Initialize changelog update process and commit workflow
- Add Qwen Code context setup for project development

### Updated
- Rerun changelog generation and git commit process

## 2025-10-17
### Changed
- Convert Java entity classes to Kotlin Serializable classes
- Migrate AIPortAccessKey, AIPortAccessKeyCredential, AIPortAccount, AIPortAnonymous, AIPortAnonymousCredential, 
  AIPortOtp, AIPortTeam, AIPortTeamMember, AIPortUser, AIPortMCPServerConfig, AIPortTeamToolMaker, AIPortTool, 
  AIPortToolAgent, AIPortToolMaker, AIPortToolPermission, AIPortToolPermissionMakerSummary, AIPortToolsApp, 
  AIPortVirtualTool, AIPortVirtualToolPermission from Java to Kotlin with Serializable support