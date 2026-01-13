---
Version: 2.0
Last Updated: 2025-12-29
Audience: Android developers
Status: Active
---

# State Management Guide

Version: 2.1
Last Updated: 2026-01-13
Audience: Android developers

---

## StateFlow Pattern
- Prefer `StateFlow` over `LiveData` for Kotlin-first, predictable state.
- Expose immutable `StateFlow` from ViewModels, keep `MutableStateFlow` private.
- Use `stateIn` operator for converting flows to StateFlow with proper lifecycle awareness.
- Use `combine` and `flatMapLatest` for complex state composition.

Example:
```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {  // Extends BaseViewModel for common error handling
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { error ->
                    handleError(error.message ?: "Unknown error")
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(isLoading = false, products = products, error = null)
                    }
                }
        }
    }
}
```

Best practices:
- Use `.update {}` for atomic updates.
- Handle loading/error paths explicitly.
- Use sealed classes for UI state (Loading, Success, Error).
- Leverage `collectAsStateWithLifecycle()` in composables for lifecycle-aware collection.

## State Hoisting in Compose
- Keep composables stateless; hoist state to ViewModel.
- Pass callbacks to UI for actions.
- Use `collectAsStateWithLifecycle()` instead of `collectAsState()` for proper lifecycle awareness.

```kotlin
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProductListContent(
        products = uiState.products,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRefresh = viewModel::refresh
    )
}
```

## SavedStateHandle
- Use `SavedStateHandle` to survive process death and store navigation args/state.
- Use `getStateFlow()` for reactive state that survives configuration changes.
- Store navigation arguments in SavedStateHandle for restoration.

```kotlin
@HiltViewModel
class FilterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ProductRepository
) : BaseViewModel() {
    // Survives process death
    var selectedCategory: String?
        get() = savedStateHandle.get<String>("category")
        set(value) { savedStateHandle["category"] = value }

    val filters = savedStateHandle.getStateFlow("filters", FilterState())

    // Handle navigation arguments
    private val productId: String? = savedStateHandle["productId"]
}
```

## DataStore for Preferences
- Use DataStore for lightweight user preferences; Room for structured data.
- Provide flows for UI to observe preference changes.
- Use Proto DataStore for type-safe preferences or Preferences DataStore for simple key-value storage.

```kotlin
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<UserPreferences> = context.createDataStore(
        fileName = "user_preferences.pb",
        serializer = UserPreferencesSerializer
    )

    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateTheme(theme: ThemeOption) {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setTheme(theme)
                .build()
        }
    }
}
```

## BaseViewModel Pattern
- All ViewModels extend `BaseViewModel` for common error handling and logging.
- Provides centralized error handling and UI state management.

```kotlin
abstract class BaseViewModel : ViewModel() {
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    protected fun handleError(message: String?) {
        viewModelScope.launch {
            _error.emit(message ?: "An unknown error occurred")
        }
    }
}

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {
    // Inherits error handling capabilities
}
```

## Advanced Patterns

### 1. Visualization State (Digital Farm)
Managing complex visualization state involves combining base data with environmental state (weather, time).

```kotlin
@HiltViewModel
class DigitalFarmViewModel @Inject constructor(
    private val farmAssetRepository: FarmAssetRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(DigitalFarmState(isLoading = true))
    val uiState: StateFlow<DigitalFarmState> = _uiState.asStateFlow()

    private val _timeOfDay = MutableStateFlow(TimeOfDay.fromCurrentTime())
    val timeOfDay: StateFlow<TimeOfDay> = _timeOfDay.asStateFlow()

    private val _weather = MutableStateFlow(WeatherType.SUNNY)
    val weather: StateFlow<WeatherType> = _weather.asStateFlow()

    init {
        loadFarmAssets()
    }

    private fun loadFarmAssets() {
        viewModelScope.launch {
            farmAssetRepository.getFarmAssets()
                .catch { handleError("Failed to load farm assets") }
                .collect { assets ->
                    _uiState.update { it.copy(assets = assets, isLoading = false) }
                }
        }
    }
}
```

### 2. Wizard State Management
For multi-step processes, use a state machine approach within the ViewModel.

```kotlin
data class WizardState(
    val currentStep: WizardStep = WizardStep.BASICS,
    val data: FormData = FormData(),
    val isCurrentStepValid: Boolean = false,
    val isComplete: Boolean = false
)

@HiltViewModel
class OnboardingWizardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _state = MutableStateFlow(WizardState())
    val state: StateFlow<WizardState> = _state.asStateFlow()

    fun nextStep() {
        if (_state.value.isCurrentStepValid) {
            _state.update {
                val nextStep = it.currentStep.next()
                it.copy(
                    currentStep = nextStep,
                    isComplete = nextStep == WizardStep.COMPLETE
                )
            }
        }
    }

    fun updateFormData(field: Field, value: Any) {
        _state.update {
            it.copy(data = it.data.copyField(field, value))
        }
    }
}
```

### 3. Filter Preset State
Efficiently managing complex filters using pre-defined templates.

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    fun applyQuickPreset(preset: QuickPreset) {
        val newFilters = when (preset) {
            QuickPreset.NEARBY_VERIFIED -> _state.value.filters.copy(radius = 25, verifiedOnly = true)
            QuickPreset.BUDGET -> _state.value.filters.copy(maxPrice = 500.0)
            QuickPreset.NEWEST_FIRST -> _state.value.filters.copy(sortOrder = SortOrder.NEWEST)
        }
        _state.update { it.copy(filters = newFilters) }

        // Save to SavedStateHandle for process death recovery
        savedStateHandle["filters"] = newFilters.toJson()
    }
}
```

### 4. Paging with StateFlow
Using Paging 3 with StateFlow for efficient list loading.

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {
    val pagedProducts: Flow<PagingData<ProductUiModel>> = repository
        .getPagedProducts()
        .map { pagingData ->
            pagingData.map { product ->
                ProductUiModel.fromEntity(product)
            }
        }
        .cachedIn(viewModelScope)
}
```

### 5. Complex State Composition
Combining multiple data sources into a single state.

```kotlin
@HiltViewModel
class FarmerDashboardViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val analyticsRepository: AnalyticsRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        // Combine multiple data sources
        viewModelScope.launch {
            combine(
                productRepository.getActiveProducts(),
                orderRepository.getRecentOrders(),
                analyticsRepository.getWeeklyMetrics()
            ) { products, orders, metrics ->
                DashboardState(
                    products = products,
                    recentOrders = orders.take(5),
                    weeklyMetrics = metrics,
                    isLoading = false
                )
            }
            .catch { handleError("Failed to load dashboard data") }
            .collect { newState ->
                _state.value = newState
            }
        }
    }
}
```

## Anti-Patterns
- Avoid `GlobalScope`.
- Avoid mutable shared state without synchronization.
- Avoid keeping heavy state in Composables; hoist instead.
- Avoid direct repository calls in composables.
- Avoid complex state transformations in composables.
- Avoid using `collectAsState()` without lifecycle awareness.

## Current State Management Practices in ROSTRY
- **114+ ViewModels** follow the BaseViewModel pattern with standardized error handling
- **StateFlow** is used consistently across all ViewModels for predictable state management
- **SavedStateHandle** is utilized for process death survival and navigation arguments
- **DataStore** is used for user preferences and settings
- **Paging 3** is integrated with StateFlow for efficient list loading
- **combine** and **flatMapLatest** operators are used for complex state composition

See also: `architecture.md`, `CODE_STYLE.md`, `dependency-injection.md`.
