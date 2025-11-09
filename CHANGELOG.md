# Changelog

All notable changes to this project will be documented in this file.

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