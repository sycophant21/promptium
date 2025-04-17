# Promptium

# LLM-Driven UI Automation with Appium and MCP

This project is a Java-based UI automation framework that integrates with **Appium** and utilizes **LLMs (like Claude)** to drive intelligent test actions on mobile devices and simulators.

## Key Features

- **UI Automation with Appium**  
  Supports device interaction such as tap, swipe, and screenshot capture on both local simulators and physical devices.

- **LLM Integration for Intelligent Test Control**  
  Leverages large language models (LLMs) to interpret natural-language instructions and drive UI behavior.

- **Model Context Protocol (MCP) Server**  
  Implements an MCP server to allow communication with **Claude Desktop**, enabling context-aware, language-driven test flows.

- **Java-Based Architecture**  
  Built with Java for reliability, performance, and seamless integration with existing test ecosystems.

## Technologies Used

- Java 23+
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
