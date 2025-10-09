# State Management Guide

Version: 1.0
Last Updated: 2025-01-15
Audience: Android developers

---

## StateFlow Pattern
- Prefer `StateFlow` over `LiveData` for Kotlin-first, predictable state.
- Expose immutable `StateFlow` from ViewModels, keep `MutableStateFlow` private.

Example:
```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
}
```

Best practices:
- Use `.update {}` for atomic updates.
- Handle loading/error paths explicitly.

## State Hoisting in Compose
- Keep composables stateless; hoist state to ViewModel.
- Pass callbacks to UI for actions.

```kotlin
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    ProductListContent(
        products = ui.products,
        isLoading = ui.isLoading,
        onRefresh = viewModel::refresh
    )
}
```

## SavedStateHandle
- Use `SavedStateHandle` to survive process death and store navigation args/state.

```kotlin
@HiltViewModel
class FilterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val filters = savedStateHandle.getStateFlow("filters", FilterState())
}
```

## DataStore for Preferences
- Use DataStore for lightweight user preferences; Room for structured data.
- Provide flows for UI to observe preference changes.

## Anti-Patterns
- Avoid `GlobalScope`.
- Avoid mutable shared state without synchronization.
- Avoid keeping heavy state in Composables; hoist instead.

See also: `architecture.md`, `CODE_STYLE.md`.
