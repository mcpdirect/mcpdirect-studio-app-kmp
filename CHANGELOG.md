# Changelog

All notable changes to this project will be documented in this file.

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