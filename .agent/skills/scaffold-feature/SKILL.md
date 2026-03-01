---
name: scaffold-feature
description: Scaffolds a new feature module using Clean Architecture layers (Domain, Data, Presentation) and wires it up with Hilt and Jetpack Compose. Use this when the user asks to create a new feature or module.
---

# Scaffold Feature Skill

This skill provides instructions on how to generate the boilerplate code for a new feature in the ROSTRY project. ROSTRY strictly adheres to Clean Architecture principles.

## Process Overview

When asked to scaffold a new feature (e.g., `Dashboard`), you MUST generate the following structure in the appropriate package (usually `com.rio.rostry.[feature_name]` or similar feature-based package structure):

1.  **Domain Layer**
    *   **Entity/Model**: Plain Kotlin data classes representing the business objects. No Android or framework dependencies.
    *   **Repository Interface**: The contract for data access. Returns `Flow<Result<T>>` or `suspend` functions returning `Result<T>`.
    *   **Use Cases**: Classes that encapsulate specific business logic, injecting the Repository Interface. Use the `operator fun invoke()` pattern.

2.  **Data Layer**
    *   **Repository Implementation**: Implements the Domain Repository Interface. Injects Data Sources (e.g., DAOs, API Services).
    *   **Data Models (Entities/DTOs)**: Room entities or Retrofit network DTOs.
    *   **Mappers**: Extension functions to map between Data Models and Domain Models.
    *   **DI Module**: A Hilt `@Module` (usually annotated with `@InstallIn(SingletonComponent::class)`) providing the Repository implementation bound to its interface using `@Binds`.

3.  **Presentation Layer**
    *   **ViewModel**: Annotated with `@HiltViewModel`. Injects Use Cases. Exposes state via `StateFlow` and handles user intents/events. Do not pass Android Contexts into the ViewModel.
    *   **UI State/Events**: Sealed interfaces/classes representing the UI state and possible user actions.
    *   **Compose Screens**: The actual UI built with Jetpack Compose. Ensure state hoisting—screens should accept state and event callbacks, and should be stateless where possible.

## Execution Rules

*   **Dependency Injection**: Use standard Hilt annotations (`@Inject`, `@HiltViewModel`, `@Module`, `@Binds`, `@Provides`).
*   **Coroutines**: Always use Kotlin Coroutines/Flows for asynchronous operations.
*   **File Naming**: Follow standard Kotlin naming conventions (e.g., `DashboardViewModel.kt`, `DashboardScreen.kt`, `GetDashboardDataUseCase.kt`).
*   **Clean Architecture Strictness**: The Domain layer MUST NOT know about the Data or Presentation layers. The Presentation layer MUST NOT interact directly with the Data layer (always go through Use Cases or the Domain Repository Interface).
