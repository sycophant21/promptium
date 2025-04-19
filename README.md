# Promptium

# LLM-Driven UI Automation with Appium and MCP

This project is a Java-based UI automation framework that integrates with **Appium** and utilizes **LLMs (like Claude)**
to drive intelligent test actions on mobile devices and simulators.

## Key Features

- **UI Automation with Appium**  
  Supports device interaction such as tap, swipe/scroll, and screenshot capture on both local simulators and
  real/physical devices.

- **LLM Integration for Intelligent Test Control**  
  Leverages large language models (LLMs) to interpret natural-language instructions and drive UI behavior.

- **Model Context Protocol (MCP) Server**  
  Implements an MCP server to allow communication with **Claude Desktop**, enabling context-aware, language-driven test
  flows.

- **Java-Based Architecture**  
  Built with Java for reliability, performance, and seamless integration with existing test ecosystems.

## Technologies Used

- Java 23+ (Minimum Required Java 17)
- Appium
- MCP Protocol
- LLM (Claude Desktop integration)

## Project Structure

```plaintext
src/
├── main/
│   └── java/
│       └── com/jadt/
│           ├── builder/       # Appium driver capabilities builder
│           ├── connector/     # Appium driver download, intiate and connection logic
│           └── domain/        # Domain Objects for images and page source
│           └── handler/       # Actions logic
│           └── util/          # Image (Screenshot) and Page Source utilities
```

## Configration Steps (for macOS)

1. Install Homebrew
    1. Install:
       ```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"```
    2. Add to System Path:
       ``` 
       echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
       eval "$(/opt/homebrew/bin/brew shellenv)" 
       ```
2. Install Node:
   ```brew install node```
3. Install Appium
   ```npm install -g appium```
4. Install XCode: ```xcode-select --install```
5. Carthage ```brew install carthage``` (Only for Physical iOS Devices)
6. Install XCUITest Plugin for appium ```appium driver install xcuitest```

## iOS Device Access Requirements

1. Apple Hardware Required
2. You must have Xcode installed from the App Store on Mac.
3. Connect a real iOS device via USB or use a simulator.
4. Trust the developer certs on the device (if testing on real devices).
5. Enable Developer Mode on ios in System Settings > Privacy & Security > Developer Mode > On
6. Enable UI Automation on ios in System Settings > Developer > Enable UI Automation (Tap/Toggle to enable)

## Claude Setup

1. Download Claude Desktop from https://claude.ai/download (Mcp not supported on claude web yet.)
2. Setup MCP -> Settings > Developer > Edit Config
3. Add the following code to ```claude_desktop_config.json``` under ```mcpServers```, if you want to use the offical release.
    ```json
    {
      "promptium": {
        "command": "sh",
        "args": ["-c", "curl -sL https://github.com/sycophant21/promptium/releases/latest/download/Promptium_v0.0.1.jar -o /tmp/promptium-mcp-server.jar && java -jar /tmp/promptium-mcp-server.jar"]
      }
    }
    ```
4. Add the following code to ```claude_desktop_config.json``` under ```mcpServers```, if you want to use your own compiled version.
   ```json
    {
      "promptium": {
        "command": "java",
        "args": ["-jar", "Path/to/your/compiled/jar"]
      }
    }
    ```
   *Note*: Jar can be compiled using java and maven, the instructions to create the jar are already written in the pom.xml file, just run ```mvn package``` in the project folder and you will find the jar inside "/target" folder. Use the shaded jar.
