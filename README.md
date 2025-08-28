# MCPdirect: Connect your home/office MCP servers directly to AI agents worldwide

## Quick Start

### Prepare
1.  Download MCPdirect Studio App from ...
2.  Anonymous sign in

### 3 Steps to Enable Any Agent to Securely Connect to Your MCP Servers

1.  **Add a MCP Server:** In the MCPdirect Studio App, add your local or cloud-based MCP server and publish it to MCPdirect.
2.  **Create a Key:** Generate a secure key for the AI Agent and copy the generated configuration.
3.  **Configure the Agent:** Paste the copied configuration into your Agent's MCP server settings.

---

## Overview

MCPdirect Studio is a desktop application built with Kotlin and Jetpack Compose for Desktop. It provides developers with a comprehensive suite of tools to connect, manage, and monitor "MCP" (Model-View-Controller Platform) servers. The application simplifies the workflow for tasks such as logging, permission management, and API key administration.

## Features

* **Connect MCP:** The core feature of the application, allowing users to add, monitor, and manage MCP servers. It supports both "stdio" and "sse" server types.
* **Agent Keys:** Manage API keys for connecting to MCP servers. Users can generate, view, enable/disable, and revoke keys.
* **Tool Permission:** Fine-grained permission control for each API key, allowing users to specify which tools a key can access.
* **Offline Key Persistence:** Keys generated while offline remain valid and can be used seamlessly after you register or sign in to an account.
* **Multi-Device Support:** A single key can be used to connect and authenticate from multiple different devices or AI agents simultaneously.
* **Tools Logbook:** Logs the usage of various tools, providing insights from both "manufacturer" and "agent" perspectives.
* **Settings:**
    * Customize device information such as name and tags.
    * Manage your account, including password changes and logout.
* **DIY Tools:** (Coming Soon) This feature is currently under development.

## Getting Started with Development

### Prerequisites

* Java Development Kit (JDK) 17 or later.

### Installation & Running the App from Source

1.  Clone the repository to your local machine.
2.  Open a terminal or command prompt in the project's root directory.
3.  Run the application using the Gradle wrapper:
    * On Windows: `gradlew.bat run`
    * On macOS/Linux: `./gradlew run`

## Technology Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose for Desktop
* **Architecture:** Model-View-ViewModel (MVVM)

## Roadmap

* Implementation of the "DIY Tools" feature.
* ...
* ...

## Contributing

We welcome contributions! Please see our `CONTRIBUTING.md` for details on how to get started.

## License

**(This section can be filled in with the project's license information.)**