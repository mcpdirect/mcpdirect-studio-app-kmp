# Changelog

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