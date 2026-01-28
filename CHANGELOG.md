# Changelog
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