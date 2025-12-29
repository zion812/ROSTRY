---
Version: 2.0
Last Updated: 2025-12-29
Audience: Android developers
Status: Active
---

# State Management Guide

Version: 2.0
Last Updated: 2025-12-29
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

## Advanced Patterns

### 1. Visualization State (Digital Farm)
Managing complex visualization state involves combining base data with environmental state (weather, time).

```kotlin
@HiltViewModel
class DigitalFarmViewModel @Inject constructor(...) : ViewModel() {
    private val _uiState = MutableStateFlow(DigitalFarmState(isLoading = true))
    val uiState: StateFlow<DigitalFarmState> = _uiState.asStateFlow()

    private val _timeOfDay = MutableStateFlow(TimeOfDay.fromCurrentTime())
    val timeOfDay: StateFlow<TimeOfDay> = _timeOfDay.asStateFlow()
    
    private val _weather = MutableStateFlow(WeatherType.SUNNY)
    val weather: StateFlow<WeatherType> = _weather.asStateFlow()
}
```

### 2. Wizard State Management
For multi-step processes, use a state machine approach within the ViewModel.

```kotlin
data class WizardState(
    val currentStep: WizardStep = WizardStep.BASICS,
    val data: FormData = FormData(),
    val isCurrentStepValid: Boolean = false
)

// In ViewModel
fun nextStep() {
    if (validateCurrentStep()) {
        _state.update { it.copy(currentStep = it.currentStep.next()) }
    }
}
```

### 3. Filter Preset State
Efficiently managing complex filters using pre-defined templates.

```kotlin
fun applyQuickPreset(preset: QuickPreset) {
    val newFilters = when (preset) {
        QuickPreset.NEARBY_VERIFIED -> filters.copy(radius = 25, verifiedOnly = true)
        QuickPreset.BUDGET -> filters.copy(maxPrice = 500.0)
    }
    _state.update { it.copy(filters = newFilters) }
}
```

## Anti-Patterns
- Avoid `GlobalScope`.
- Avoid mutable shared state without synchronization.
- Avoid keeping heavy state in Composables; hoist instead.

See also: `architecture.md`, `CODE_STYLE.md`.
