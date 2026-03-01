---
name: complex-refactor
description: Guidelines for managing massive architectural shifts, DB migrations, or multi-file refactors using Task Groups. Use this when the user requests a sweeping change.
---

# Complex Refactor Skill

This skill outlines how you (the Agent) should approach a massive refactoring task in the ROSTRY project. Heavy refactors can quickly overwhelm your context window or become untrackable for the user. 

## The Core Rule: Use Task Groups

When handling a large refactor, you MUST enter **Planning Mode**, define a detailed `implementation_plan.md`, and utilize **Task Groups** (`task_boundary` tool) to manage the execution.

### Execution Strategy

1.  **Analyze & Plan First**: Never jump straight into code edits. Read all relevant files, trace dependencies, and write out a step-by-step plain in an artifact.
2.  **Modularize via Task Boundaries**: Break the implementation into distinct `TaskName` chunks. For example, if restructuring authentication:
    *   `TaskName: Refactoring Auth Domain Layer`
    *   `TaskName: Refactoring Auth Data Layer`
    *   `TaskName: Updating Auth UI Components`
3.  **Validate Incrementally**: Within a Task Group, make the changes, and optionally run `./gradlew assembleDebug` or a linter to ensure you haven't broken the build locally before proceeding to the next chunk.
4.  **Communicate via Pending Steps**: If you hit a roadblock (e.g., ambiguous business logic left behind by a deleted entity), stop. Update the Task Status, and use `notify_user` to ask a direct, clear question. The Task Group UI will cleanly present this pending step to the user.

## Common ROSTRY Refactor Targets

*   **Database Migrations**: When updating a Room Entity, ensure you update the version in `AppDatabase`, generate the `Migration` block, update queries in the DAO, and adjust mappers in the Repository. Do this incrementally.
*   **Dependency Updates**: If updating a major dependency (e.g., Compose or Navigation) that introduces breaking changes, update `libs.versions.toml` first, sync, and then fix compilation errors module by module.
