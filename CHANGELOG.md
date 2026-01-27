# Changelog

## [January 20, 2026]
### Summary
Enhanced key and team management with improved editing functionality

### Details
- Enhanced MCPdirectKeysComponent with ListButton for better UI consistency
- Improved access key editing with proper state management
- Enhanced AccessKeyRepository with modifyAccessKey functionality
- Improved team management with better editing workflow
- Enhanced UI with better spacing and arrangement of components
- Improved code clarity with better variable naming and state management
- Enhanced team list with proper padding and spacing
- Improved edit functionality with better user experience

## [January 20, 2026]
### Summary
Enhanced ListButton component and improved team management UI

### Details
- Enhanced ToolAgentComponent with improved padding and spacing for list items
- Improved ListButton component with cleaner implementation and better interaction handling
- Enhanced TeamListView with better team editing workflow
- Improved team name editing with proper state management
- Enhanced UI with better spacing and arrangement of components
- Improved code clarity with better variable naming and state management
- Enhanced team list with proper padding and spacing
- Improved edit functionality with better user experience

## [January 20, 2026]
### Summary
Enhanced UI components with new ListButton and improved styling

### Details
- Created new ListButton composable component for improved list item interactions
- Enhanced ToolAgentComponent with ListButton for better UI consistency
- Improved StudioListItem with reduced dependencies and cleaner code
- Enhanced TooltipText with improved API and styling options
- Updated MyStudioScreen and MCTemplateListView with improved tooltip usage
- Enhanced TeamListView with ListButton implementation
- Improved TeamMemberView with better status indicators using Badges
- Enhanced UI with better visual hierarchy and interaction patterns
- Improved code clarity with better component organization

## [January 20, 2026]
### Summary
Enhanced SharedMCPServerListView with improved UI and navigation

### Details
- Updated SharedMCPServerListView with arrow icons for better navigation
- Enhanced UI with improved share/unshare functionality
- Added proper padding and content values for better layout
- Improved text button styling with MaterialTheme
- Enhanced expand/collapse functionality with proper icons
- Improved UI with better visual indicators for sharing state
- Enhanced code clarity with better variable naming

## [January 20, 2026]
### Summary
Enhanced team management with improved UI and functionality

### Details
- Updated TeamRepository with improved modifyTeam function using AIPortServiceResponse
- Enhanced MCPTeamScreen with proper lambda syntax for response handling
- Improved MCPTeamViewModel with AIPortServiceResponse for team modification
- Enhanced TeamListView with better team editing functionality
- Added proper team name editing with owner validation
- Improved UI with person icons for team owners
- Enhanced team creation and modification workflows
- Added proper state management for team editing operations
- Improved code clarity with better variable naming

## [January 20, 2026]
### Summary
Enhanced team sharing UI with improved icons and functionality

### Details
- Added encrypted_add_circle and forms_add_on SVG icons
- Enhanced SharedMCPServerListView with improved action bar text based on grantable state
- Updated SharedMCPServerListView with forms_add_on icon for sharing functionality
- Improved SharedMCPServerListView with better button layout and cancel/save functionality
- Enhanced TeamListView with improved create team form handling
- Added proper form validation and state management in team creation
- Improved UI with better icons and enhanced user experience
- Enhanced code clarity with better variable naming

## [January 20, 2026]
### Summary
Enhanced team and key management UI with improved functionality

### Details
- Added TooltipIconButton for editing MCPdirect key names in MCPdirectKeyScreen
- Enhanced MCPdirectKeysComponent with edit functionality
- Improved TeamScreen with proper modifier handling
- Renamed editable to grantable in SharedMCPServerListView for better clarity
- Enhanced SharedMCPServerListView with shield toggle icon for grant functionality
- Improved TeamListView with better UI layout and create team functionality
- Added floating action button for team creation in TeamListView
- Enhanced UI with better icons and improved user experience
- Improved code clarity with better variable naming

## [January 20, 2026]
### Summary
Cleaned up unused imports and improved code clarity in SharedMCPServerView

### Details
- Removed unused AIPortVirtualTool import from SharedMCPServerView
- Removed unused info icon import from SharedMCPServerView
- Improved code clarity with better variable naming in data assignment
- Commented out unused toolId calculation in SharedMCPServerView
- Enhanced code readability and maintainability

## [January 20, 2026]
### Summary
Enhanced team sharing functionality and improved tool maker selection

### Details
- Updated MCPTeam screen to use list of tool makers instead of single tool maker
- Enhanced ToolAgentScreen to pass selected tool makers to team sharing
- Improved MCPServersWidget and VirtualMCPWidget with proper tool maker list handling
- Enhanced TeamScreen with editable parameter and list of tool makers support
- Updated SharedMCPServerListView with improved tool maker selection logic
- Enhanced SharedMCPServerView with proper selection handling
- Improved team sharing functionality with better tool maker management
- Updated main application window to handle tool maker list in MCPTeam screen

## [January 20, 2026]
### Summary
Enhanced dialog management and improved UI components

### Details
- Added ToolAgentScreenDialog enum for better dialog management
- Enhanced ToolAgentScreen with proper dialog state handling
- Improved ShortcutWidget with dropdown menu for AI agent integration
- Added access key selection in AI agent integration dropdown
- Enhanced TeamScreen with nullable parameters
- Improved TeamMemberView with TooltipIconButton for invite functionality
- Enhanced UI with proper dialog state management and improved user experience
- Added dropdown menu for selecting access keys in AI agent integration

## [January 20, 2026]
### Summary
Major refactoring with new component architecture and UI improvements

### Details
- Refactored ToolAgentScreen to use new ToolAgentComponent and ToolAgentComponentViewModel
- Created new ToolAgentComponent and ToolMakerComponent for better modularity
- Added MCPdirectKeyQuickstartComponent for streamlined key generation workflow
- Enhanced tool permission granting with improved UI and workflow
- Updated app version from 2.3.0 to 2.3.1
- Updated app version code from 26012201 to 260122001
- Improved UI with new color scheme and styling
- Enhanced access key generation and selection workflow
- Improved tool selection with better filtering and display
- Added TODO comment for future deep link functionality

## [January 20, 2026]
### Summary
Enhanced MCP access key screen with integration guide functionality

### Details
- Added integrationGuide parameter to MCPAccessKey screen for direct access to AI agent integration
- Enhanced ShortcutWidget with navigation to MCP access key integration guide
- Improved AIAgentGuideComponent to handle optional access key parameter
- Updated MCPdirectKeyScreen to support direct integration guide display
- Added SDK icon resource for UI enhancements
- Enhanced access key credential handling with proper null safety
- Improved UI with better navigation flow for AI agent integration
- Added HighlightedText composable component for future UI enhancements

## [January 20, 2026]
### Summary
Enhanced tool agent screen and improved catalog navigation logic

### Details
- Updated ToolAgentScreen to handle different catalog display modes based on tool agent/maker presence
- Enhanced catalog display logic with proper ID handling for different tool maker types
- Improved ShortcutWidget with proper navigation to My Studio screen
- Added secondary constructor to AIPortToolMaker for easier instantiation
- Refactored ConnectMCPView to use Long type for showCatalog parameter instead of Boolean
- Enhanced catalog navigation with improved state management using -1, 0, and positive IDs
- Improved tool maker template handling with proper ID-based logic
- Enhanced UI with better navigation flow between catalog and installed servers views

## [January 20, 2026]
### Summary
Enhanced team screen functionality and improved UI component structure

### Details
- Updated Screen.MCPTeam to include toolMaker parameter for better team navigation
- Enhanced MCPServersWidget with improved TeamToolMakerCard navigation
- Refactored VirtualMCPWidget with separate VirtualToolMakerCard and TeamVirtualToolMakerCard components
- Implemented team dropdown menu in TeamVirtualToolMakerCard for better team navigation
- Enhanced TeamScreen with toolMaker parameter support
- Updated SharedMCPServerListView to handle toolMaker selection properly
- Improved UI with better separation of user-owned and team-shared virtual tool makers
- Enhanced team navigation with proper toolMaker context passing
- Improved component structure with dedicated card components for different tool maker types

## [January 20, 2026]
### Summary
Enhanced team collaboration features and improved UI components

### Details
- Updated Platform interface to include lastUpdated parameter in queryTeamMembers
- Enhanced HomeScreen with automatic refresh of team tool makers
- Added refreshTeamToolMakers functionality to HomeViewModel
- Refactored MCPServersWidget with separate ToolMakerCard and TeamToolMakerCard components
- Implemented team dropdown menu in TeamToolMakerCard for better team navigation
- Enhanced VirtualMCPWidget with proper refresh logic
- Added getTeams function to TeamRepository for retrieving teams associated with tool makers
- Implemented loadTeamMembers functionality with proper caching and refresh logic
- Improved UI with better separation of user-owned and team-shared tool makers
- Enhanced team member query with lastUpdated timestamp support

## [January 20, 2026]
### Summary
Enhanced home screen with shortcut widget and improved access controls

### Details
- Created ShortcutWidget component for quick access to common functions
- Added shortcut buttons for connecting OpenAPI as MCP and integrating with AI agents
- Enhanced MCPServersWidget with team access controls and group icons
- Improved VirtualMCPWidget with proper user permission checks
- Updated HomeViewModel to remove user ownership filter from tool maker lists
- Added ShortcutWidget to HomeScreen layout
- Updated app version code from 26012200 to 26012201
- Removed version display from window title
- Updated mcpdirect-studio-core dependency from SNAPSHOT to release version
- Enhanced UI with proper access controls based on user permissions

## [January 20, 2026]
### Summary
Updated app version code and enhanced version management

### Details
- Updated app version code from 20261016 to 26012200
- Enhanced version management system with proper version comparison
- Improved app version checking functionality

## [January 20, 2026]
### Summary
Implemented app version checking and update notification system

### Details
- Added AppVersionRepository for managing application version information
- Implemented checkAppVersion functionality in Platform interfaces
- Created LinkButton composable for clickable text elements
- Enhanced HomeScreen with version display and update notifications
- Added badge indicator for new version availability
- Implemented hover effect for update check functionality
- Added link to GitHub releases for downloading new versions
- Integrated app version checking with login process
- Enhanced UI with proper styling for version information display
- Added marketplace USL constant for app version queries

## [January 20, 2026]
### Summary
Enhanced AI agent integration with general transport options and UI improvements

### Details
- Added general transport options (SSE and Streamable HTTP) to AI agent integration guide
- Updated template variable syntax in AI agent configurations from ${'$'}{VAR} to ${VAR}
- Enhanced AIAgentListComponent with default modifier parameter
- Refactored ConfigAIAgentView to use AIAgentListComponent and AIAgentGuideComponent
- Added Done button to QuickStartScreen for completing the setup process
- Improved UI organization in AI agent configuration view
- Enhanced code modularity by using dedicated components for AI agent guidance

## [January 20, 2026]
### Summary
Refactored home screen with QuickstartWidget and enhanced tool agent functionality

### Details
- Created QuickstartWidget component for better modularity in home screen
- Refactored HomeScreen to use QuickstartWidget instead of inline code
- Moved quick start functionality to separate widget component
- Added modifyToolAgent function in StudioRepository for updating tool agent properties
- Enhanced UI with improved navigation and layout in main application
- Added proper imports for lifecycle and coroutine functionality
- Improved code organization by separating concerns into dedicated components
- Updated navigation icons and layout in main application window

## [January 20, 2026]
### Summary
Enhanced AI agent list with search functionality and improved LM Studio integration

### Details
- Added search functionality to AIAgentListComponent with StudioSearchbar
- Implemented filtering for AI agents based on search input
- Added vertical scrollbar to AI agent list for better navigation
- Enhanced UI with proper padding and alignment in AI agent list
- Added LM Studio configuration path to the AI agent integration guide
- Improved layout with Box wrapper and LazyColumn state management
- Enhanced user experience with searchable AI agent list

## [January 20, 2026]
### Summary
Enhanced AI agent integration guide and improved UI components

### Details
- Added comprehensive AI agent integration guide with configurations for multiple platforms
- Implemented JSON serialization for AI agent data classes
- Enhanced AIAgentListComponent to dynamically load AI agent configurations
- Added TooltipIconButton composable with improved tooltip functionality
- Updated QuickStartScreen with tooltip-enhanced action buttons
- Added removeToolMaker functionality in QuickStartViewModel
- Enhanced UI with proper tooltips for remove, restart, and configure actions
- Updated Kotlin version from 2.2.21 to 2.3.0 in dependencies

## [January 20, 2026]
### Summary
Enhanced API server management and improved response handling

### Details
- Modified modifyOpenAPIServerForStudio function to handle null parameters properly
- Updated MyStudioViewModel to use AIPortServiceResponse for OpenAPI server connections
- Enhanced StudioRepository with modifyOpenAPIServerForStudio functionality
- Improved response handling for API server modifications and connections
- Added proper null checks and response processing in API server operations
- Updated QuickStartScreen to handle OpenAPI server configuration properly
- Enhanced QuickStartViewModel with install and modify OpenAPI server functions
- Added proper error handling and response processing for API server operations
- Improved type safety with ToolProviderType import in QuickStartScreen

## [January 20, 2026]
### Summary
Enhanced loading indicator and improved top bar implementation

### Details
- Added loading progress indicator to the top bar in main application window
- Modified GeneralViewModel to handle loading states with proper float values
- Updated loading function to accept float parameter with default value
- Improved top bar layout with proper padding and positioning
- Enhanced UI with LinearProgressIndicator for better user feedback during loading states
- Refined loading process state management in GeneralViewModel

## [January 20, 2026]
### Summary
Removed unnecessary shape properties in UI components

### Details
- Removed shape property from OutlinedTextField in ConfigMCPServerDialog
- Removed shape property from input fields in ConfigMCPServerDialog
- Cleaned up unused shape configurations in UI components

## [January 20, 2026]
### Summary
Improved UI layout and padding in MCP server configuration screens

### Details
- Updated padding in ConfigMCPServerDialog for better spacing and layout
- Replaced some HorizontalDivider elements with commented-out alternatives
- Changed OutlinedCard to Card in QuickStartScreen for different styling
- Updated padding in MCPServerMainView for improved content spacing
- Modified Card to OutlinedCard in tool view for consistent styling
- Adjusted padding values in various UI components for better responsive design
- Removed unnecessary container and content color specifications in tab rows

## [January 20, 2026]
### Summary
Implemented StudioOutlinedCard component and enhanced MCP server configuration UI

### Details
- Created new StudioOutlinedCard composable with floating label functionality
- Added swap_vert.svg icon resource for UI enhancements
- Enhanced ConfigMCPServerDialog with improved transport type selection using dropdown menu
- Replaced segmented buttons with ExposedDropdownMenu for transport selection
- Added proper imports for dropdown menu components and text input functionality
- Implemented StudioOutlinedCard in MCP server configuration UI
- Added check and swap_vert drawable resources for improved UI elements
- Enhanced accessibility with proper content descriptions and labels
- Improved UI layout with better arrangement of transport type selection options

## [January 20, 2026]
### Summary
Implemented ToolAgent selection menu with dropdown functionality

### Details
- Added ToolAgentSelectionMenu composable with dropdown functionality for selecting tool agents
- Implemented ExposedDropdownMenuBox for tool agent selection UI
- Added BadgedBox to indicate "This device" for local tool agent
- Enhanced QuickStartScreen with improved tool agent selection interface
- Modified QuickStartViewModel to trigger tool maker queries when current tool agent changes
- Added proper imports for text input and UI components
- Commented out legacy tool agent selection code in ConnectMCPView
- Implemented proper state handling for tool agent selection dropdown
- Added icons and styling for improved UX in tool agent selection
- Enhanced accessibility with proper content descriptions and labels

## [January 19, 2026]
### Summary
Added macOS-style window controls and improved UI layout

### Details
- Added macOS-style close, minimize, and maximize button vector drawables
- Implemented platform-specific window controls with proper styling
- Enhanced main window with macOS-specific window properties
- Improved Home screen layout with better spacing and alignment
- Added macOS traffic light-style window controls with hover effects
- Enhanced widget layouts with fillMaxWidth for better responsiveness
- Improved window management with platform-appropriate UI elements
- Added proper imports for UI components and platform detection
- Enhanced window styling with conditional shape based on OS
- Added circle-shaped window controls for macOS compatibility
- Improved window control layout with platform-specific arrangements
- Added proper color definitions for macOS traffic light buttons
- Enhanced exitApplication functionality with proper click handling
- Updated AppInfo object with APP_VERSION_CODE constant
- Added architecture constants to AIPortAppVersion model
- Improved platform detection with proper OS name matching
- Enhanced UI with proper modifiers and styling for window controls
- Added background and clipping modifiers for window control styling

## [January 15, 2026]
### Summary
Enhanced app version model and improved macOS window controls

### Details
- Added AIPortAppVersion model with platform and architecture constants
- Updated build configuration with app package ID and version code
- Enhanced main.kt with OS detection function for platform-specific behavior
- Improved window controls for macOS with traffic light-style buttons
- Added macOS-specific window styling with rounded corners
- Enhanced window management with platform-appropriate UI elements
- Added platform constants for Windows, Linux, and macOS
- Improved window maximize/minimize/close functionality
- Added proper imports for UI components and platform detection
- Enhanced window styling with conditional shape based on OS
- Added circle-shaped window controls for macOS compatibility
- Improved window control layout with platform-specific arrangements
- Added proper color definitions for macOS traffic light buttons
- Enhanced exitApplication functionality with proper click handling
- Updated AppInfo object with APP_VERSION_CODE constant
- Added architecture constants to AIPortAppVersion model
- Improved platform detection with proper OS name matching
- Enhanced UI with proper modifiers and styling for window controls
- Added background and clipping modifiers for window control styling

## [January 2, 2026]
### Summary
Enhanced Home screen with improved tool maker filtering and user access control

### Details
- Enhanced HomeViewModel with improved filtering for tool makers based on user ownership
- Added proper access control to ensure users only see their own tool makers
- Improved UI with better tool maker filtering functionality in Home screen
- Enhanced tool maker filtering with accurate user identification checks
- Added UserRepository import for user access validation in Home screen
- Improved UI with better visual feedback for user-specific tool maker filtering
- Enhanced Home screen management with accurate user-based filtering for tools
- Added proper state handling for user-specific tool maker access
- Improved filtering logic with case-insensitive name matching for tool makers
- Enhanced component organization with better user access control in Home screen views