# Error Handling Guide

Version: 1.1
Last Updated: 2026-01-13
Audience: Android developers

---

## Resource Type Pattern
Use the `Resource` sealed class for success/loading/error states with extension helpers.

```kotlin
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String?, val exception: Exception? = null) : Resource<Nothing>()
}

// Extension functions for handling Resource types
fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

fun <T> Resource<T>.onError(action: (String?) -> Unit): Resource<T> {
    if (this is Resource.Error) action(message)
    return this
}

fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) action()
    return this
}
```

## BaseViewModel Error Handling
All ViewModels extend `BaseViewModel` for centralized error handling and logging.

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
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {
    // Inherits error handling capabilities
}
```

## Repository Error Handling
- Validate inputs early; return `Resource.Error` with actionable messages
- Catch network exceptions; prefer offline-first behavior when safe
- Handle specific Firebase exceptions appropriately

```kotlin
suspend fun createProduct(p: Product): Resource<String> = try {
    // Validate input
    if (p.name.isBlank()) {
        return Resource.Error("Product name is required")
    }

    // Save locally first
    dao.insert(p.toEntity())

    // Attempt remote sync
    try {
        api.create(p)
        Resource.Success(p.id)
    } catch (e: FirebaseNetworkException) {
        // Local success, remote will sync later via WorkManager
        Resource.Success(p.id)
    }
} catch (e: Exception) {
    Timber.e(e, "createProduct failed for product: ${p.id}")
    Resource.Error("Failed to create product: ${e.localizedMessage}", e)
}
```

## ViewModel Error Mapping
- Convert technical messages to user-friendly text
- Provide retry actions where possible
- Use the BaseViewModel's error handling for centralized management

```kotlin
private fun handleError(error: String?) {
    _uiState.update { currentState ->
        currentState.copy(
            isLoading = false,
            error = when {
                error?.contains("network", ignoreCase = true) == true ->
                    "No internet connection. Changes saved locally and will sync when connected."
                error?.contains("permission", ignoreCase = true) == true ->
                    "You don't have permission to perform this action."
                error?.contains("quota exceeded", ignoreCase = true) == true ->
                    "Storage quota exceeded. Please free up space."
                error?.contains("verification", ignoreCase = true) == true ->
                    "Account verification required. Please complete verification."
                else ->
                    "Something went wrong. Please try again."
            }
        )
    }
}
```

## UI State Management with Resource
Handle Resource states in ViewModels and expose them to Composables:

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { exception ->
                    handleError("Failed to load products: ${exception.message}")
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load products") }
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(isLoading = false, products = resource.data, error = null)
                            }
                        }
                        is Resource.Error -> {
                            handleError(resource.message)
                            _uiState.update {
                                it.copy(isLoading = false, error = resource.message)
                            }
                        }
                    }
                }
        }
    }
}

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)
```

## Logging Strategy
- Use Timber in debug; CrashlyticsTree in release
- Avoid logging PII; redact sensitive fields
- Log with context for easier debugging

```kotlin
// In RostryApp.kt
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
} else {
    Timber.plant(CrashlyticsTree())
}

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.ERROR) {
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("priority", priority)
                tag?.let { setCustomKey("tag", it) }
                t?.let { recordException(it) }
                log(message)
            }
        }
    }
}
```

## Error Monitoring
- Enable Firebase Crashlytics with custom keys
- Track error categories for analytics
- Monitor specific error patterns for proactive fixes

## Network Error Handling
Handle different types of network errors with appropriate user feedback:

```kotlin
private fun handleNetworkError(exception: Exception): String {
    return when (exception) {
        is java.net.SocketTimeoutException -> "Request timed out. Please check your connection."
        is java.net.UnknownHostException -> "No internet connection. Working offline."
        is java.net.ConnectException -> "Unable to connect. Please check your connection."
        is retrofit2.HttpException -> {
            when (exception.code()) {
                401 -> "Session expired. Please sign in again."
                403 -> "Access denied. Insufficient permissions."
                404 -> "Requested resource not found."
                429 -> "Too many requests. Please try again later."
                500 -> "Server error. Please try again later."
                else -> "Network error: ${exception.message}"
            }
        }
        else -> "Connection failed: ${exception.message}"
    }
}
```

## Database Error Handling
Handle database-specific errors gracefully:

```kotlin
suspend fun insertProduct(product: ProductEntity): Resource<Unit> = try {
    dao.insert(product)
    Resource.Success(Unit)
} catch (e: android.database.SQLException) {
    Timber.e(e, "Database insertion failed for product: ${product.productId}")
    Resource.Error("Database error. Please try again.")
} catch (e: Exception) {
    Timber.e(e, "Unexpected error inserting product: ${product.productId}")
    Resource.Error("Unexpected error occurred.")
}
```

## Testing Error Scenarios
- Mock network failures, DAO exceptions
- Assert UI state transitions (loading → error, with retry)
- Test error recovery and retry mechanisms

```kotlin
@Test
fun `loadProducts error updates state with user-friendly message`() = runTest {
    // Given
    val errorMessage = "Network unavailable"
    coEvery { repository.getProducts() } returns flowOf(Resource.Error(errorMessage))

    // When
    val viewModel = ProductListViewModel(repository)

    // Then
    assertEquals("No internet connection. Changes saved locally and will sync when connected.",
                 viewModel.uiState.value.error)
    assertFalse(viewModel.uiState.value.isLoading)
}
```

## Current Error Handling Practices in ROSTRY
- **Resource sealed class** used consistently across all repositories and ViewModels
- **BaseViewModel pattern** provides centralized error handling for all 114+ ViewModels
- **User-friendly error messages** with context-specific feedback
- **Offline-first approach** with graceful degradation
- **Comprehensive logging** with Crashlytics integration
- **Network error categorization** with appropriate user feedback
- **Database error handling** with specific SQLite exception handling

See also: `architecture.md`, `security-encryption.md`, `testing-strategy.md`, `state-management.md`, `dependency-injection.md`.
