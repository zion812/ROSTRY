---
name: review-code
description: Enforces ROSTRY's architectural and styling rules during code reviews. Use this whenever asked to review code, PRs, or check for best practices in existing files.
---

# Code Review Skill

This skill instructions how to perform a code review on the ROSTRY codebase. You should evaluate the provided code against this checklist and provide specific, actionable feedback.

## Review Checklist

### 1. Architectural Boundaries (Clean Architecture)
*   **Domain Independence**: Does the Domain layer (UseCases, Domain Entities) contain any Android, Room, Retrofit, or Compose imports? If yes, flag as a critical violation.
*   **Layer Bypassing**: Does the Presentation layer (ViewModels/Compose) interact directly with DAOs or Retrofit services? It must communicate through the Domain layer (UseCases or Repository Interfaces).
*   **Data Mapping**: Are Data entities (DTOs, Room Entities) leaking into the Presentation layer? Ensure mappers are used to convert them to Domain models.

### 2. Jetpack Compose Best Practices
*   **State Hoisting**: Are Composable functions mostly stateless? Do they accept their state as inputs and emit events via callbacks?
*   **Stability**: Are the data classes passed to Composables stable? (Collections should ideally be immutable, e.g., `kotlinx.collections.immutable.ImmutableList`).
*   **Recomposition**: Are lambdas passed to Composables unnecessarily causing recomposition? (Suggest method references or `remember`ing lambdas).
*   **Avoid Unnecessary States**: Is `remember` used appropriately? Is state that survives process death stored in the `ViewModel` instead of the UI?

### 3. Concurrency and Coroutines
*   **Dispatcher Usage**: Are specific Dispatchers (`Dispatchers.IO`, `Dispatchers.Default`) hardcoded inside Repositories or ViewModels? (They should ideally be injected for testability, though `viewModelScope.launch` is acceptable for UI triggers).
*   **Flows**: Are `Flow`s collected lifecycle-aware in the UI? (e.g., using `collectAsStateWithLifecycle()` from `androidx.lifecycle.compose`).
*   **Blocking Calls**: Ensure no blocking calls are made on the main thread.

### 4. Error Handling
*   **Result Wrapping**: Are errors caught at the Repository layer and returned as a wrapped `Result` or custom domain error sealed class? Exceptions shouldn't crash the app from the data layer up unless they are fatal.

## Feedback Format

When providing feedback:
1.  Reference the specific file and line number (if applicable).
2.  State the violation clearly.
3.  Provide a short, correct code snippet demonstrating the fix.
4.  Explain *why* the fix is necessary based on ROSTRY's principles.
