# ROSTRY Code Style Guide

This document defines coding conventions for consistency, readability, and maintainability across the ROSTRY codebase.

## Table of Contents

- [File Organization](#file-organization)
- [Kotlin Conventions](#kotlin-conventions)
- [Jetpack Compose](#jetpack-compose)
- [Architecture Patterns](#architecture-patterns)
- [Naming Conventions](#naming-conventions)
- [Error Handling](#error-handling)
- [Documentation](#documentation)
- [Imports](#imports)
- [Comments](#comments)
- [Testing](#testing)
- [Performance](#performance)
- [Git Practices](#git-practices)
- [Dependencies](#dependencies)

## 🚩 Priority Levels
| Level | Description |
|-------|-------------|
| **Must Follow** | Critical rules impacting correctness, security, or performance |
| **Should Follow** | Recommended best practices for readability & maintainability |
| **Nice to Have** | Stylistic preferences with minimal impact on functionality |

## 🔎 Quick Reference Table
| Category | Rule | Example |
|----------|------|---------|
| Null Safety | Avoid `!!` | `val name = user?.name ?: "Unknown"` |
| Collections | Prefer functional APIs | `products.filter { it.active }` |
| Compose | Stateless composables | `@Composable fun ProductCard(...)` |
| Naming | CamelCase & meaningful | `ProductListViewModel` |
| Immutability | Prefer `val` | `val uiState` not `var` |
| Error Handling | Use sealed `Result` | `Result.Error(message)` |
| Tests | Descriptive names | `shouldReturnActiveProducts()` |

> For full details see the sections below.

Quick links: see `docs/DOCUMENTATION_STANDARDS.md` and `docs/api-documentation.md` for documentation style, and `docs/README-docs.md` for navigation.

---

## File Organization

### Package Structure

Organize packages by **feature**, not by layer:

✅ **Good**:
```
com.rio.rostry.
├── ui/
│   ├── transfer/
│   │   ├── TransferCreateScreen.kt
│   │   ├── TransferCreateViewModel.kt
│   │   └── TransferManagementScreen.kt
│   └── marketplace/
│       ├── MarketplaceScreen.kt
│       └── MarketplaceViewModel.kt
├── data/
│   └── repository/
│       ├── TransferRepository.kt
│       └── MarketplaceRepository.kt
```

❌ **Bad**:
```
com.rio.rostry.
├── viewmodels/
│   ├── TransferCreateViewModel.kt
│   └── MarketplaceViewModel.kt
├── screens/
│   ├── TransferCreateScreen.kt
│   └── MarketplaceScreen.kt
```

### File Naming

- **Screens**: `<Feature><UserType>Screen.kt` (e.g., `TransferCreateScreen.kt`)
- **ViewModels**: `<Feature>ViewModel.kt` (e.g., `TransferCreateViewModel.kt`)
- **Repositories**: `<Feature>Repository.kt` and `<Feature>RepositoryImpl.kt`
- **Entities**: `<Name>Entity.kt` (e.g., `TransferEntity.kt`)
- **DAOs**: `<Name>Dao.kt` (e.g., `TransferDao.kt`)

## Kotlin Conventions

### Null Safety

Always prefer safe calls over force unwraps:

✅ **Good**:
```kotlin
val name = user?.name ?: "Unknown"
val length = text?.length ?: 0
```

❌ **Bad**:
```kotlin
val name = user!!.name  // Can crash
val length = text!!.length
```

### Data Classes

Use data classes for models and state:

✅ **Good**:
```kotlin
data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Sealed Classes

Use sealed classes for state machines and result types:

✅ **Good**:
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### Extension Functions

Use extension functions to enhance readability:

✅ **Good**:
```kotlin
fun String.toTitleCase(): String = 
    this.lowercase().replaceFirstChar { it.uppercase() }

fun Long.toFormattedDate(): String = 
    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(this))
```

### Scope Functions

Use scope functions appropriately:

- **`let`**: Transform non-null values
- **`apply`**: Configure objects
- **`run`**: Execute block on object
- **`with`**: Execute block on receiver
- **`also`**: Side effects

✅ **Good**:
```kotlin
val user = getUserOrNull()?.let { user ->
    UserDto(
        id = user.id,
        name = user.name
    )
}

val database = Room.databaseBuilder(...).apply {
    addMigrations(MIGRATION_1_2)
    setQueryCallback(queryCallback, executor)
}.build()
```

### Collections

Prefer Kotlin collection functions over loops:

✅ **Good**:
```kotlin
val activeProducts = products.filter { it.isActive }
val productNames = products.map { it.name }
val totalPrice = products.sumOf { it.price }
```

❌ **Bad**:
```kotlin
val activeProducts = mutableListOf<Product>()
for (product in products) {
    if (product.isActive) {
        activeProducts.add(product)
    }
}
```

### Coroutines

Use structured concurrency; avoid `GlobalScope`:

✅ **Good**:
```kotlin
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getProducts()
            _uiState.update { 
                it.copy(
                    products = result.getOrElse { emptyList() },
                    isLoading = false
                )
            }
        }
    }
}
```

❌ **Bad**:
```kotlin
GlobalScope.launch {  // Leaks if ViewModel is cleared
    val products = repository.getProducts()
}
```

## Jetpack Compose

### Stateless Composables

Keep composables stateless; hoist state to ViewModels:

✅ **Good**:
```kotlin
@Composable
fun ProductCard(
    product: Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onProductClick(product.id) }
    ) {
        Text(product.name)
        Text(product.price.toString())
    }
}
```

❌ **Bad**:
```kotlin
@Composable
fun ProductCard(product: Product) {
    var isExpanded by remember { mutableStateOf(false) }  // State in composable
    // ...
}
```

### Remember and Side Effects

Use `remember` for computations, side effects appropriately:

✅ **Good**:
```kotlin
@Composable
fun ProductList(
    products: List<Product>,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }
    
    val sortedProducts = remember(products) {
        products.sortedBy { it.name }
    }
}
```

### Modifiers

Chain modifiers in the correct order (size → layout → behavior → drawing):

✅ **Good**:
```kotlin
Box(
    modifier = Modifier
        .size(100.dp)  // Size first
        .padding(8.dp)  // Layout
        .clickable { }  // Behavior
        .background(Color.Blue)  // Drawing
)
```

### Composable Naming

- **Nouns**: Components that display UI (`ProductCard`, `UserAvatar`)
- **Adjectives/Verbs**: Modifiers and utilities (`Scrollable`, `Clickable`)

### Preview Functions

Provide preview functions for key components:

```kotlin
@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    ROSTRYTheme {
        ProductCard(
            product = Product(
                id = "1",
                name = "Rhode Island Red Rooster",
                price = 500.0
            ),
            onProductClick = {}
        )
    }
}
```

## Architecture Patterns

### MVVM Implementation

✅ **Good ViewModel**:
```kotlin
@HiltViewModel
class ProductCreateViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductCreateUiState())
    val uiState: StateFlow<ProductCreateUiState> = _uiState.asStateFlow()
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun createProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.createProduct(_uiState.value.toProduct())
            _uiState.update { 
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, successProductId = result.data)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                    Result.Loading -> it.copy(isLoading = true)
                }
            }
        }
    }
}
```

### Repository Pattern

✅ **Good Repository**:
```kotlin
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) : ProductRepository {
    
    override fun getProducts(): Flow<List<Product>> = 
        productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    
    override suspend fun createProduct(product: Product): Result<String> = try {
        val entity = product.toEntity()
        productDao.insert(entity)
        firestore.collection("products").add(product.toMap()).await()
        Result.Success(product.id)
    } catch (e: Exception) {
        Result.Error("Failed to create product", e)
    }
}
```

### Dependency Injection (Hilt)

Use constructor injection:

✅ **Good**:
```kotlin
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val analytics: AnalyticsRepository
) : ViewModel()
```

## Naming Conventions

### Variables and Functions

- **Variables**: `camelCase`
- **Functions**: `camelCase` with verb prefix
- **Private properties**: `_camelCase` for backing properties

```kotlin
class ProductViewModel {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun loadProducts() { }
    fun createProduct() { }
    private fun validateInput(): Boolean { }
}
```

### Classes and Interfaces

- **Classes**: `PascalCase`
- **Interfaces**: `PascalCase` (no "I" prefix)
- **Data classes**: `PascalCase`

```kotlin
interface ProductRepository { }
class ProductRepositoryImpl : ProductRepository { }
data class Product(val id: String, val name: String)
```

### Constants

- **Compile-time constants**: `UPPER_SNAKE_CASE`
- **Runtime constants**: `camelCase`

```kotlin
const val MAX_RETRY_COUNT = 3
const val DEFAULT_TIMEOUT_MS = 5000L

class Config {
    companion object {
        val defaultPageSize = 20
    }
}
```

### Resource Files

- **Layouts**: `<type>_<feature>_<description>.xml`
- **Drawables**: `ic_<name>_<size>.xml` or `bg_<description>.xml`
- **Strings**: `<feature>_<description>`

## Error Handling

### Use Sealed Result Types

✅ **Good**:
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

suspend fun getProducts(): Result<List<Product>> = try {
    val products = api.getProducts()
    Result.Success(products)
} catch (e: Exception) {
    Result.Error("Failed to load products", e)
}
```

### Proper Try-Catch Usage

✅ **Good**:
```kotlin
try {
    val result = repository.createProduct(product)
    _uiState.update { it.copy(success = true) }
} catch (e: IOException) {
    Timber.e(e, "Network error creating product")
    _uiState.update { it.copy(error = "Network error. Please try again.") }
} catch (e: Exception) {
    Timber.e(e, "Unexpected error creating product")
    _uiState.update { it.copy(error = "Something went wrong") }
}
```

### Logging

Use Timber for logging:

```kotlin
Timber.d("Loading products for user: $userId")
Timber.w("Product cache is stale, refreshing...")
Timber.e(exception, "Failed to create product")
```

## Documentation

### KDoc for Public APIs

```kotlin
/**
 * Repository for managing product data.
 *
 * Provides methods to create, read, update, and delete products
 * with offline-first caching via Room database.
 */
interface ProductRepository {
    
    /**
     * Retrieves all products for the current user.
     *
     * @return Flow of product list, updated in real-time from local database
     */
    fun getProducts(): Flow<List<Product>>
    
    /**
     * Creates a new product.
     *
     * @param product The product to create
     * @return Result with product ID on success, error message on failure
     */
    suspend fun createProduct(product: Product): Result<String>
}
```

## Imports

### Organize Imports

1. Android/Java imports
2. Third-party libraries
3. Project imports

Use IDE auto-formatting for consistent import ordering.

### Avoid Wildcard Imports

❌ **Bad**:
```kotlin
import com.rio.rostry.data.model.*
```

✅ **Good**:
```kotlin
import com.rio.rostry.data.model.Product
import com.rio.rostry.data.model.User
```

## Comments

### When to Comment

- **Complex algorithms**: Explain the "why", not the "what"
- **Business logic**: Clarify domain-specific rules
- **Workarounds**: Document why a workaround exists
- **TODOs**: Mark with ticket number

✅ **Good**:
```kotlin
// Round to 2 decimal places to match payment gateway precision requirements
val roundedPrice = (price * 100).roundToInt() / 100.0

// TODO(#456): Replace with server-side validation once API is ready
val isValid = validateLocally(input)
```

❌ **Bad**:
```kotlin
// Increment counter
counter++

// Get user name
val name = user.name
```

## Testing

### Test File Organization

Mirror the package structure:

```
src/test/java/com/rio/rostry/
└── ui/transfer/
    └── TransferCreateViewModelTest.kt
```

### Test Naming

Use descriptive names:

```kotlin
@Test
fun `createProduct with valid data should return Success`() { }

@Test
fun `createProduct with network error should return Error`() { }

@Test
fun `applyFilters should update UI state with filtered products`() { }
```

### Use Fakes Over Mocks

When possible, use fakes for better maintainability:

```kotlin
class FakeProductRepository : ProductRepository {
    private val products = mutableListOf<Product>()
    
    override suspend fun createProduct(product: Product): Result<String> {
        products.add(product)
        return Result.Success(product.id)
    }
}
```

## Performance

### Avoid Work on Main Thread

✅ **Good**:
```kotlin
viewModelScope.launch {
    val result = withContext(Dispatchers.IO) {
        // Heavy computation or I/O
        processLargeDataset()
    }
    _uiState.update { it.copy(result = result) }
}
```

### Profile Critical Paths

Use Android Profiler for:
- Memory leaks
- CPU usage
- Network calls
- Database queries

## Git Practices

### Commit Message Format

```
<type>: <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat: add multi-step wizard for product creation

- Implement 4-step wizard (BASICS → DETAILS → MEDIA → REVIEW)
- Add progress indicator and navigation controls
- Include inline validation for each step

Closes #123
```

### Branch Naming

- `feat/add-payment-gateway`
- `fix/sync-crash`
- `docs/update-architecture`
- `refactor/simplify-viewmodel`

## Dependencies

### Adding Dependencies

1. Add to `build.gradle.kts` with version catalog when possible
2. Document why the dependency is needed
3. Consider library size and maintenance status
4. Prefer Jetpack/AndroidX libraries

### Dependency Updates

- Review release notes before updating
- Test thoroughly after updates
- Update one dependency at a time when possible

## DataStore Patterns

### Consistent DataStore Initialization

Use the `preferencesDataStore` delegate pattern for all DataStore instances to ensure singletons and prevent multiple instances accessing the same file:

✅ **Good**:
```kotlin
// Define the extension at the top of the file
private val Context.myFeatureDataStore by preferencesDataStore(name = "my_feature_prefs")

// Use in ViewModel/Repository constructor
class MyFeatureViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val dataStore = context.myFeatureDataStore

    // Use dataStore for operations
    fun saveValue(key: String, value: String) {
        viewModelScope.launch {
            context.myFeatureDataStore.edit { prefs ->
                prefs[stringPreferencesKey(key)] = value
            }
        }
    }
}
```

❌ **Bad**:
```kotlin
// Creating multiple instances per ViewModel creation
private val dataStore = PreferenceDataStoreFactory.create(
    produceFile = { context.preferencesDataStoreFile("my_feature_prefs") }
)
```

### Multiple DataStore Files

When multiple DataStore files are needed for different purposes:
- Use descriptive names that clearly indicate the purpose
- Add KDoc comments explaining the scope of each DataStore
- Avoid overlapping data between different DataStore files

✅ **Good**:
```kotlin
// Authentication-related preferences
private val Context.authPrefsDataStore by preferencesDataStore(name = "auth_prefs")

// Phone verification state (separate for process death recovery)
private val Context.authVerificationDataStore by preferencesDataStore(name = "auth_state")
```

### Error Handling

Always wrap DataStore operations in `runCatching` blocks:

```kotlin
suspend fun savePreference(key: String, value: String): Result<Unit> {
    return runCatching {
        context.myFeatureDataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(it) }
    )
}
```

### Migration Patterns

When migrating from SharedPreferences to DataStore:

```kotlin
private suspend fun migrateLegacyIfNeeded() {
    val migratedKey = booleanPreferencesKey("data_migrated")
    val prefs = context.myFeatureDataStore.data.first()
    val alreadyMigrated = prefs[migratedKey] ?: false
    if (alreadyMigrated) return

    // Read legacy SharedPreferences
    val legacy = context.getSharedPreferences("legacy_prefs", Context.MODE_PRIVATE)
    val legacyValue = legacy.getString("key", null)

    // Write to DataStore
    if (legacyValue != null) {
        context.myFeatureDataStore.edit { prefs ->
            prefs[stringPreferencesKey("key")] = legacyValue
        }
    }

    // Mark as migrated
    context.myFeatureDataStore.edit { prefs ->
        prefs[migratedKey] = true
    }

    // Clear legacy values
    legacy.edit().remove("key").apply()
}
```

---

**References**:
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- [Jetpack Compose Guidelines](https://developer.android.com/jetpack/compose/mental-model)

---

## Linting & Static Analysis

### Run Locally
```bash
./gradlew ktlintCheck detekt
```

### Auto-fix
```bash
# If ktlint plugin with format task is configured
./gradlew ktlintFormat
```

### IDE Integration
- Android Studio: enable Kotlin style formatting and import optimization
- Install ktlint and detekt plugins if preferred; set to run on save

### CI Requirements
- All PRs must pass ktlint and detekt; see `CONTRIBUTING.md` and `docs/ci-cd.md`
