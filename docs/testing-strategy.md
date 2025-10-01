# Testing Strategy

This document outlines the testing approach, coverage goals, and best practices for the ROSTRY Android application.

## Goals
- Ensure reliability of critical paths (auth, sync, marketplace, transfer, monitoring).
- Maintain fast feedback with unit tests and focused integration tests.
- Provide stable end-to-end UI coverage for primary user journeys.

## Test Pyramid
- Unit (fast, many): ViewModels, repositories, mappers, validators, use-cases.
- Integration (medium): Auth flow, Sync scenarios, Database performance.
- UI/E2E (slower): Espresso tests for core flows and accessibility.

## Scopes
- Unit: mock external dependencies (Firestore, Functions, Room, WorkManager) using fakes/mocks.
- Integration: run against in-memory Room and mocked network layers.
- UI: use orchestrator, idling resources, and accessibility checks.

## New Tests
- `app/src/test/java/com/rio/rostry/integration/AuthFlowIntegrationTest.kt` (scaffold)
- `app/src/test/java/com/rio/rostry/integration/SyncIntegrationTest.kt` (scaffold)
- `app/src/test/java/com/rio/rostry/performance/DatabasePerformanceTest.kt` (scaffold)
- `app/src/androidTest/java/com/rio/rostry/ui/accessibility/AccessibilityTest.kt` (scaffold)

## Best Practices
- Keep tests deterministic and independent.
- Prefer constructor injection; avoid singletons in testable components.
- Use Test Dispatchers; control time with `TestScope`.
- Name tests with behavior-focused style: `shouldX_whenY`.
- Verify public behavior, not implementation details.

## Coverage Targets
- Unit: 80%+ for core modules (auth, sync, marketplace, monitoring).
- Integration: cover all critical flows and edge cases (conflicts, offline mode).
- UI: cover main navigation paths and error surfaces.

## CI Guidance
- Run unit tests on every commit.
- Run integration/UI suites on PR and nightly.
- Flake retries limited to 1; track flaky tests for remediation.
