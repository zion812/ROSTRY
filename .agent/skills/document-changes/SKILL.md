---
name: document-changes
description: Analyzes the codebase or specific directories to identify recent developments, implemented fetchers, and structural changes, then generates high-quality, expert-level documentation following project standards. Use this when asked to document the codebase or recent updates.
---

# Document Changes Skill

This skill outlines how the Antigravity agent should analyze the ROSTRY codebase and generate professional, expert-level documentation for newly implemented features, data fetchers, or architectural updates.

## Execution Strategy

When asked to document recent development or specific components:

### 1. Information Gathering (Analysis Phase)
*   **Identify Changes**: If the user asks for *recent* changes, use the `run_command` tool to execute `git status`, `git diff --stat`, or `git log -n 5` to pinpoint recently modified files.
*   **Deep Dive**: Use the `view_file` or `codebase_search` tools to read the actual implementations of the changed files.
*   **Focus Areas**: Pay special attention to:
    *   **Data Fetchers**: New UseCases, Repository implementations, Retrofit API interfaces, or Room DAOs.
    *   **State Management**: Changes to ViewModels and UI State classes.
    *   **Architecture**: New modules or significant structural shifts.

### 2. Documentation Standards
Always generate the documentation as an artifact (e.g., `feature_documentation.md` or `api_fetchers.md`) unless the user explicitly asks for it in the chat. Use expert-level technical writing standards.

Your documentation MUST include:

*   **Executive Summary**: A concise, 1-2 paragraph overview of what was implemented or changed and its business value.
*   **Architectural Overview**: Explain how the new components fit into ROSTRY's Clean Architecture pattern (Domain, Data, Presentation).
*   **Component Breakdown**:
    *   **Fetchers / Data Sources**: Document the endpoints, queries, or data streams. What are the inputs? What are the outputs (`Flow`, `Result`, exception handling)?
    *   **Domain Logic**: Describe the strictly business-centric rules encapsulated in the UseCases.
    *   **Presentation / UI**: Detail the state hoisting and ViewModel interactions.
*   **Code Examples**: Provide short, relevant code blocks highlighting the interfaces or key implementations (do not copy-paste entire files; focus on the contract).
*   **Mermaid Diagrams**: For complex logic, data flows, or dependency graphs, include a Mermaid diagram. 
    *   *Example: A sequence diagram showing the flow from the UI triggering an event -> ViewModel -> UseCase -> Repository -> Network/DB.*

### 3. Review and Refine
Ensure the generated documentation is concise, accurately reflects the current state of the code (no hallucinations), and uses correct markdown formatting (e.g., bolding file paths, backticks for class names). If any part of the codebase is ambiguous during analysis, actively point out the ambiguity in a "Known Limitations/Open Questions" section of the document.
