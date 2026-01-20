# Changelog

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