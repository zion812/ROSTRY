# Code Style Quick Reference

⚡ **Quick Reference** - This is a condensed version of the full code style guide. For complete details, see `../CODE_STYLE.md`. Last synced: 2025-10-29.

Version: 1.0
Last Updated: 2025-10-29
Audience: Contributors

---

Top 20 rules with examples. See full guide in `../CODE_STYLE.md`.

1. Naming: PascalCase types, camelCase members
```kotlin
class ProductListViewModel
val maxCount = 10
```
2. Immutability: prefer val
```kotlin
val items = listOf(1,2,3)
```
3. Null-safety: avoid !!; use ?: and let
```kotlin
val name = user?.name ?: "Unknown"
```
4. Result pattern for errors
```kotlin
sealed class Result<out T>
```
5. Stateless Composables + state hoisting
```kotlin
@Composable fun ProductCard(state: CardState, onClick: ()->Unit)
```
6. ViewModel exposes StateFlow
```kotlin
val uiState: StateFlow<UiState>
```
7. Repositories abstract data sources
```kotlin
interface ProductRepository { suspend fun get(): Result<List<Product>> }
```
8. Coroutines: structured concurrency
```kotlin
viewModelScope.launch { /* ... */ }
```
9. Flow operators: map/filter distinctUntilChanged
10. DI with Hilt: @Inject, @HiltViewModel, @Singleton
11. Room: DAO interfaces, Entities with defaults
12. Mappers: toEntity()/toDomain()
13. Logging: Timber in debug; avoid PII
14. Tests: descriptive names, Given/When/Then
15. Fakes over mocks where feasible
16. Compose: remember, keys in Lazy lists
17. Strings: use string resources; no hardcoded UI text
18. Formatting: ktlint + detekt must pass
19. Imports: optimize; avoid wildcard
20. Docs: KDoc public APIs; follow Documentation Standards

Links:
- Full code style: `../CODE_STYLE.md`
- Documentation standards: `DOCUMENTATION_STANDARDS.md`
- Testing strategy: `testing-strategy.md`
- Sync Process: Reviewed quarterly for alignment with full guide; consider automated extraction in future.

<!-- TODO: Consider automating generation from CODE_STYLE.md to ensure sync -->
