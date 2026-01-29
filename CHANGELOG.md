# Changelog
## [January 28, 2026]
### Summary
Enhance theme system and add validation features to editable components

### Details
- Fixed light/dark mode color scheme assignment in Theme.kt
- Added refreshAll function to HomeViewModel for comprehensive refresh
- Enhanced EditableText component with validation features and rules
- Added new TooltipBox composable for improved tooltips
- Added warning SVG icon for UI enhancements
- Improved validation system with ValidationResult and ValidationRule interfaces
- Added error, info, and warning icons to EditableText component

## [January 28, 2026]
### Summary
Standardize UI terminology and enhance app update functionality

### Details
- Renamed "This device" to "Local" throughout the UI for consistency
- Modified HomeViewModel to improve app update checking functionality
- Updated multiple UI components to reflect the new "Local" terminology
- Enhanced app update checking with proper response handling
- Improved UI consistency across various screens and widgets

## [January 28, 2026]
### Summary
Enhance HomeScreen with improved update and refresh functionality

### Details
- Added upgrade icon to indicate when a new version is available
- Implemented refresh icon for update checking functionality
- Added delayed visibility for refresh button to improve UX
- Included loading indicator during refresh operations
- Added delay functionality using kotlinx.coroutines.delay
- Enhanced visual feedback for loading states in HomeScreen

## [January 28, 2026]
### Summary
Improve team handling and error messaging consistency

### Details
- Added AIPortTeam import to ToolAgentScreen for proper team handling
- Fixed team initialization condition to check for valid team ID
- Updated error messaging to be consistent across repositories
- Enhanced Modify MCP server error message in ToolRepository
- Improved Modify MCP Team error message in TeamRepository
- Standardized duplicate key error messages across repositories
- Updated Modify MCPdirect Access Key error message in AccessKeyRepository

## [January 28, 2026]
### Summary
Enhance editable text functionality with improved UI and error handling

### Details
- Added padding option to InlineTextField composable
- Fixed border display issue in InlineTextField
- Corrected cancel behavior to properly return to non-editing state
- Enhanced error messaging for duplicate access key names
- Implemented editable user profile name in HomeScreen
- Improved validation and error handling in access key repository
- Updated QWEN.md instructions for changelog generation

## [January 28, 2026]
### Summary
Add editable text functionality to allow in-place name editing

### Details
- Created new EditableText and InlineTextField composables for inline editing
- Implemented editable access key names in MCPDirectKeysWidget
- Added editable tool agent names in MyStudiosWidget
- Enabled direct name editing in UI components without separate edit screens
- Added validation and error handling for name changes
- Included save and cancel functionality for editing operations
- Used ellipsis text overflow for better display of long names

## [January 28, 2026]
### Summary
Improve authentication UI with better keyboard navigation and focus management

### Details
- Enhanced LoginScreen with proper keyboard navigation using IME actions
- Implemented focus management with FocusRequester and FocusManager
- Added keyboard actions for "Next" and "Done" transitions
- Improved error messages with more descriptive text
- Optimized imports and cleaned up unused variables in AuthViewModel
- Removed redundant state variables in authentication view model
- Enhanced user experience with proper keyboard flow in authentication screens

## [January 27, 2026]
### Summary
Updated theme color scheme and cleaned up changelog entries

### Details
- Updated theme with new green color variants for light and dark modes
- Modified AppColorScheme to use green color properties instead of success/warning
- Cleaned up previous changelog entries to maintain concise history
- Adjusted color scheme assignments in AppTheme composable

## [January 27, 2026]
### Summary
Updated project documentation and prepared for new feature development

### Details
- Updated QWEN.md with clearer instructions about change log format
- Reviewed project structure and documentation for consistency
- Prepared development environment for upcoming feature implementations
- Enhanced project documentation with additional guidelines