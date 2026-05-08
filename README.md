# IntelliJ Docstring Generator

## Overview

IntelliJ Docstring Generator is an IntelliJ IDEA plugin developed as part of an internship project. Its primary function is to automate the generation of professional Javadoc docstrings for Java methods by using Google Gemini. The project serves as a practical application of integrating artificial intelligence into developer tooling.

## Functionality

The plugin extends the IDE's capabilities by adding an action to generate documentation based on the selected code context. The general workflow is as follows:

1. **Configuration**: The user provides a Google Gemini API key via the plugin's settings panel. 
2. **Context Extraction**: When the action is triggered, the plugin analyzes the current editor context using the Program Structure Interface (PSI). It identifies the selected text, verifies that the selection corresponds to a valid Java method, and extracts the relevant code block.
3. **AI Processing**: The extracted code is formatted alongside a strict, predefined prompt and sent to the Gemini API via the `langchain4j` library. The request is processed asynchronously using a background task to prevent freezing the IDE.  
4. **Code Modification**: Upon receiving the generated Javadoc block from the LLM, the plugin schedules a write command on the application thread. The documentation is then seamlessly inserted directly above the method declaration in the active document.

## Architecture

*   **Actions**: Handles user interactions and integrates with the IntelliJ Action System. It manages the extraction of PSI elements and document offsets.
*   **Services**: Encapsulates the business logic. It handles the instantiation of the AI client and the orchestration of the generation request.
*   **Prompts**: Centralizes the static instructions sent to the LLM, ensuring that the model strictly returns Javadoc format without extraneous text.
*   **Settings**: Manages the UI components and persistent storage for user preferences, specifically the secure management of the API key.

## Technical Requirements

*   IntelliJ IDEA (Community or Ultimate Edition)
*   Java Development Kit (JDK) 17 or higher
*   An active Google Gemini API Key
