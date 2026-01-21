# Changelog

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