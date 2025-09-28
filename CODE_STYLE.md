# ROSTRY Code Style

This document defines coding conventions for consistency and readability.

## Kotlin & Android

- Use Kotlin idioms; prefer data classes and sealed classes for state.
- Null-safety: avoid `!!`; use safe calls and require checks.
- Coroutines: structured concurrency; avoid GlobalScope; use `viewModelScope`.

## Compose

- Stateless composables with state hoisted to ViewModels where possible.
- Use `remember` and `derivedStateOf` appropriately; avoid unnecessary recompositions.
- Preview functions for key components.

## Architecture

- MVVM with repositories; DI via Hilt.
- Single source of truth: Room + Flow.
- Clear layering: UI, domain, data.

## Naming & Organization

- Packages by feature (e.g., `ui/transfer`, `data/repository`).
- Classes: `PascalCase`; methods/vars: `camelCase`; constants: `UPPER_SNAKE_CASE`.

## Error Handling

- Use sealed `Result`/`Resource` types; provide user-friendly messages.

## Testing

- Co-locate tests mirroring package structure; use fakes where appropriate.

## Performance

- Avoid work on main thread; profile critical paths.

## ProGuard & Release

- Keep rules minimal and justified; document in PRs.
