---
name: codebase-comprehension
description: Establishes an end-to-end grip on the ROSTRY codebase. Use this skill when entering a new task that requires a deep, holistic understanding of the project's architecture, existing implementations, and business logic before making complex modifications.
---

# Codebase Comprehension (End-to-End Grip) Skill

This skill dictates the required protocol for the Antigravity agent when tasked with a complex problem that demands an "end-to-end grip" on the ROSTRY project. It ensures you do not make blind edits, but instead fully contextualize your actions within the broader architectural and product goals (e.g., the Enthusiast Persona, AI Farm Services).

## Execution Strategy: The "Grip" Protocol

When you activate this skill, you MUST follow these steps before proposing any structural code changes.

### Step 1: Read the Blueprints (Contextual Grounding)
Before looking at code, you must understand *why* the code exists.
1.  **Read Core Documentation**: Use the `view_file` tool to read the root-level architectural and planning documents. Specifically, evaluate:
    *   `SYSTEM_BLUEPRINT.md` (or equivalent architecture docs)
    *   `ROADMAP.md`
    *   `README.md`
2.  **Review Knowledge Items (KIs)**: Look for established KIs in the conversation summaries (e.g., `Enthusiast Persona Blueprint`, `AI-Powered Farm Intelligence Services`) and review their underlying artifacts using `view_file` to understand the product vision and domain language.

### Step 2: Top-Down Discovery (Structural Grounding)
Once the business context is understood, establish the technical structure.
1.  **Navigation Map**: Understand how the user moves through the app. Locate the main `NavHost` or routing graphs (usually in the `Presentation` layer or a dedicated `navigation` package).
2.  **Dependency Graph**: Locate the core dependency injection modules (Hilt `@Module` classes). This tells you what data sources and repositories are available across the app.
3.  **Database Hub**: Locate the `AppDatabase` (Room) interface to understand the complete data model available locally.

### Step 3: Bottom-Up Tracing (Implementation Grounding)
When you have a specific target (e.g., "Add a feature to the Dashboard"), trace its existing lineage End-to-End:
1.  **Data Origin**: Where does the data come from? (Find the `Retrofit` interface or `Room` DAO).
2.  **Repository Mapping**: How is the data standardized? (Find the `RepositoryImpl` and the `Domain` Repository Interface).
3.  **Business Logic**: What rules apply to the data? (Find the corresponding `UseCase`s).
4.  **State Management**: How is the data prepared for the screen? (Find the `ViewModel` and `UiState`).
5.  **Rendering**: How is the data displayed? (Find the `Compose` Screen).

## Expected Output format
When this skill is invoked explicitly by the user (e.g., "Get a grip on the checkout flow"), you must output an artifact (e.g., `comprehension_summary.md`) detailing:
1.  **The Trace**: A bulleted list or Mermaid diagram showing the exact files involved in the flow from UI down to the Database/Network.
2.  **Architectural Assessment**: Does the existing flow violate any Clean Architecture principles?
3.  **Impact Radius**: If we change component X, what else will break? (List dependent classes/files).

*Rule of Thumb: Never begin editing a file in a complex flow until you can confidently trace its data back to the source.*
