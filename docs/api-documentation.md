# API Documentation Standards

**Version:** 1.0  
**Last Updated:** 2025-01-15  
**Audience:** Developers

---

## Table of Contents

- [Overview](#overview)
- [KDoc Standards](#kdoc-standards)
- [Documentation Requirements](#documentation-requirements)
- [KDoc Syntax Guide](#kdoc-syntax-guide)
- [Examples by Component Type](#examples-by-component-type)
- [Dokka Configuration](#dokka-configuration)
- [Generating Documentation](#generating-documentation)
- [Documentation Review Checklist](#documentation-review-checklist)
- [Best Practices](#best-practices)

---

## Overview

ROSTRY uses **KDoc** (Kotlin Documentation) for inline API documentation and **Dokka** to generate HTML documentation. Well-documented code is essential for maintainability, onboarding, and collaboration.

###

 Purpose

- **For Developers**: Understand APIs without reading implementation
- **For Contributors**: Know how to use and extend components
- **For Reviewers**: Verify correctness and completeness
- **For Future Maintenance**: Reduce time to understand code

---

## KDoc Standards

### When to Document

**REQUIRED** for:
- ✅ All public classes, interfaces, and objects
- ✅ All public functions and properties
- ✅ All public parameters with non-obvious purpose
- ✅ Complex algorithms or non-obvious logic
- ✅ Return types that need explanation
- ✅ Exceptions that can be thrown

**OPTIONAL** for:
- Private functions (unless complex)
- Self-explanatory properties
- Override functions (if parent is documented)

### Documentation Principles

1. **Be Clear**: Write for readers who don't know the implementation
2. **Be Concise**: Don't repeat what's obvious from signatures
3. **Be Specific**: Provide concrete examples
4. **Be Accurate**: Keep docs in sync with code
5. **Be Helpful**: Explain "why" not just "what"

---

## Documentation Requirements

### Minimum Requirements

Every public API must include:

1. **Summary**: One-line description of purpose
2. **Detailed Description**: What it does, when to use it
3. **Parameters**: Purpose and constraints for each parameter
4. **Return Value**: What is returned and when
5. **Exceptions**: What exceptions can be thrown and why
6. **Examples**: Usage examples for complex APIs
7. **See Also**: Related classes/functions

### Quality Standards

- Maximum line length: 100 characters
- Use proper grammar and punctuation
- Write in present tense ("Returns the user" not "Will return the user")
- Use active voice ("Creates a product" not "A product is created")
- Start with capital letter, end with period

---

## KDoc Syntax Guide

### Basic Structure

```kotlin
/**
 * Single-line summary (required).
 *
 * Detailed description with multiple paragraphs if needed.
 * Can include markdown formatting.
 *
 * @param paramName Parameter description
 * @return Return value description
 * @throws ExceptionType When this exception is thrown
 * @see RelatedClass
 * @sample com.example.SampleClass.sampleMethod
 */
```

### Tags

| Tag | Purpose | Example |
|-----|---------|---------|
| `@param` | Document parameters | `@param userId The unique user identifier` |
| `@return` | Document return value | `@return List of active products` |
| `@throws` | Document exceptions | `@throws IllegalArgumentException if userId is empty` |
| `@see` | Cross-reference | `@see ProductRepository` |
| `@sample` | Link to example | `@sample com.example.Samples.createProduct` |
| `@property` | Document property | `@property userId The user's unique ID` |
| `@constructor` | Document constructor | `@constructor Creates a new Product instance` |
| `@suppress` | Suppress warnings | `@suppress("unused")` |

### Markdown Support

KDoc supports markdown:

```kotlin
/**
 * Creates a new product with the given details.
 *
 * **Usage**:
 * ```kotlin
 * val product = Product(
 *     id = "123",
 *     name = "Rhode Island Red"
 * )
 * ```
 *
 * **Important**: The product must have a unique ID.
 *
 * - Validates input data
 * - Stores in local database
 * - Syncs to Firestore
 *
 * @param id Unique product identifier
 * @param name Product display name (max 100 chars)
 * @return Created product with generated timestamps
 * @throws IllegalArgumentException if name is empty or too long
 */
```

---

## Examples by Component Type

### Classes

```kotlin
/**
 * Repository for managing product data with offline-first architecture.
 *
 * Provides methods to create, read, update, and delete products
 * with automatic synchronization between Room database and Firestore.
 * All operations are suspend functions that should be called from
 * a coroutine or ViewModel scope.
 *
 * **Offline Support**:
 * - All read operations work offline using cached data
 * - Write operations are queued when offline and synced when online
 *
 * **Thread Safety**: All methods are thread-safe and can be called
 * from any coroutine dispatcher.
 *
 * @constructor Creates a ProductRepository with the given dependencies.
 * @property productDao DAO for local database operations
 * @property firestore Firestore instance for remote sync
 *
 * @see Product
 * @see ProductDao
 *
 * @sample com.rio.rostry.samples.ProductRepositorySamples.createProduct
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) : ProductRepository {
    // Implementation
}
```

### Functions

```kotlin
/**
 * Creates a new product and stores it locally and remotely.
 *
 * Validates the product data, assigns timestamps, stores in Room database,
 * and queues for Firestore sync. Returns immediately after local storage,
 * background sync happens automatically.
 *
 * **Validation Rules**:
 * - Name must not be empty and max 100 characters
 * - Price must be positive
 * - Quantity must be non-negative
 * - Seller ID must match authenticated user
 *
 * **Example**:
 * ```kotlin
 * viewModelScope.launch {
 *     val result = repository.createProduct(product)
 *     when (result) {
 *         is Result.Success -> showSuccess(result.data)
 *         is Result.Error -> showError(result.message)
 *     }
 * }
 * ```
 *
 * @param product The product to create (with ID and sellerId set)
 * @return Result.Success with product ID, or Result.Error with message
 * @throws IllegalArgumentException if validation fails
 * @throws SecurityException if user is not authenticated
 *
 * @see Product
 * @see getProducts
 * @see updateProduct
 */
suspend fun createProduct(product: Product): Result<String>
```

### ViewModels

```kotlin
/**
 * ViewModel for product creation and management.
 *
 * Manages UI state for creating and listing products, handles
 * user input validation, and coordinates with repository for
 * data operations. Survives configuration changes.
 *
 * **State Management**:
 * - UI state exposed via [uiState] StateFlow
 * - Loading states handled automatically
 * - Errors surfaced to UI for display
 *
 * **Lifecycle**:
 * - ViewModel-scoped coroutines used for background work
 * - All operations cancelled when ViewModel is cleared
 *
 * @property repository Repository for product operations
 * @property savedStateHandle For process death survival
 *
 * @see ProductCreateUiState
 * @see ProductRepository
 */
@HiltViewModel
class ProductCreateViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    /**
     * Current UI state for product creation.
     *
     * Emits updates when:
     * - User input changes
     * - Loading state changes
     * - Errors occur
     * - Product is created successfully
     */
    val uiState: StateFlow<ProductCreateUiState> = _uiState.asStateFlow()
    
    /**
     * Updates the product title in the UI state.
     *
     * Triggers validation and updates error state if title is invalid.
     *
     * @param title New product title (trimmed automatically)
     */
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title.trim()) }
        validateTitle(title)
    }
}
```

### Data Classes

```kotlin
/**
 * Domain model representing a marketplace product.
 *
 * Immutable data class used throughout the app for product data.
 * Can be mapped to/from database entities and DTOs.
 *
 * **Lifecycle**:
 * - Created when user lists a product
 * - Updated when seller modifies details
 * - Soft-deleted (isActive=false) when removed
 *
 * @property id Unique product identifier (UUID)
 * @property sellerId User ID of the seller
 * @property name Product display name (max 100 chars)
 * @property description Detailed product description (max 500 chars)
 * @property price Price in local currency (must be positive)
 * @property quantity Available quantity (non-negative, 0 = out of stock)
 * @property category Product category (FOWL, EGGS, FEED, EQUIPMENT)
 * @property breed Breed name (e.g., "Rhode Island Red")
 * @property imageUrls List of image URLs (max 5 images)
 * @property isActive Whether product is currently listed
 * @property createdAt Creation timestamp
 * @property updatedAt Last update timestamp
 *
 * @constructor Creates a Product instance with all required fields.
 *
 * @see ProductEntity
 * @see ProductDto
 */
data class Product(
    val id: String,
    val sellerId: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val category: ProductCategory,
    val breed: String? = null,
    val imageUrls: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Validates that all product fields meet business rules.
     *
     * @return true if valid, false otherwise
     */
    fun isValid(): Boolean {
        return name.isNotEmpty() && 
               name.length <= 100 &&
               price > 0 &&
               quantity >= 0
    }
}
```

### Sealed Classes

```kotlin
/**
 * Result wrapper for asynchronous operations.
 *
 * Represents three states of an operation: loading, success, or error.
 * Prevents null checks and provides type-safe error handling.
 *
 * **Usage**:
 * ```kotlin
 * when (result) {
 *     is Result.Loading -> showLoader()
 *     is Result.Success -> showData(result.data)
 *     is Result.Error -> showError(result.message)
 * }
 * ```
 *
 * @param T Type of successful result data
 *
 * @see Success
 * @see Error
 * @see Loading
 */
sealed class Result<out T> {
    /**
     * Successful operation with result data.
     *
     * @property data The successful result
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Failed operation with error details.
     *
     * @property message User-friendly error message
     * @property throwable Optional exception for logging
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Result<Nothing>()
    
    /**
     * Operation in progress, no data yet.
     */
    object Loading : Result<Nothing>()
}
```

### Composables

```kotlin
/**
 * Displays a product card in the marketplace.
 *
 * Shows product image, name, price, and action buttons.
 * Handles click events and favorites marking. Stateless component
 * that receives state and callbacks from parent.
 *
 * **Responsibilities**:
 * - Display product information
 * - Handle user interactions
 * - Show loading and error states
 *
 * **Does NOT**:
 * - Manage product state
 * - Handle business logic
 * - Make direct repository calls
 *
 * @param product The product to display
 * @param onProductClick Callback when card is clicked, receives product ID
 * @param onFavoriteClick Callback when favorite icon clicked
 * @param modifier Modifier for customizing layout and appearance
 *
 * @sample com.rio.rostry.samples.ProductCardPreview
 */
@Composable
fun ProductCard(
    product: Product,
    onProductClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation
}

/**
 * Preview for ProductCard in light theme.
 */
@Preview(name = "Product Card - Light", showBackground = true)
@Composable
private fun ProductCardPreview() {
    ROSTRYTheme {
        ProductCard(
            product = Product(
                id = "1",
                sellerId = "seller1",
                name = "Rhode Island Red Rooster",
                description = "Healthy rooster, 8 months old",
                price = 500.0,
                quantity = 1,
                category = ProductCategory.FOWL
            ),
            onProductClick = {},
            onFavoriteClick = {}
        )
    }
}
```

### Extension Functions

```kotlin
/**
 * Converts timestamp to formatted date string.
 *
 * Formats the Long timestamp (milliseconds since epoch) into
 * a human-readable date string using the specified pattern.
 *
 * **Format Examples**:
 * - "MMM dd, yyyy" → "Jan 15, 2025"
 * - "dd/MM/yyyy HH:mm" → "15/01/2025 14:30"
 *
 * @param pattern Date format pattern (default: "MMM dd, yyyy")
 * @param locale Locale for formatting (default: system default)
 * @return Formatted date string
 *
 * @see SimpleDateFormat
 */
fun Long.toFormattedDate(
    pattern: String = "MMM dd, yyyy",
    locale: Locale = Locale.getDefault()
): String {
    val dateFormat = SimpleDateFormat(pattern, locale)
    return dateFormat.format(Date(this))
}
```

### Interfaces

```kotlin
/**
 * Repository interface for product data operations.
 *
 * Defines the contract for product CRUD operations with
 * offline-first architecture. Implementations must handle
 * local caching and remote synchronization.
 *
 * **Implementation Requirements**:
 * - All operations must be thread-safe
 * - Read operations should work offline
 * - Write operations should queue when offline
 * - Use Result wrapper for error handling
 *
 * @see ProductRepositoryImpl
 * @see Product
 */
interface ProductRepository {
    /**
     * Retrieves all products with optional filtering.
     *
     * Returns a Flow that emits product list updates in real-time
     * as data changes in the local database. Updates automatically
     * when products are added, modified, or deleted.
     *
     * @param filters Optional filters to apply (category, price range, etc.)
     * @return Flow of product lists, updated in real-time
     */
    fun getProducts(filters: ProductFilters? = null): Flow<List<Product>>
    
    /**
     * Creates a new product.
     *
     * @param product Product to create
     * @return Result with product ID on success, error on failure
     */
    suspend fun createProduct(product: Product): Result<String>
}
```

---

## Dokka Configuration

### build.gradle.kts

Dokka is already configured in `app/build.gradle.kts`:

```kotlin
plugins {
    id("org.jetbrains.dokka")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        // Kotlin stdlib docs
        externalDocumentationLink {
            url.set(URL("https://kotlinlang.org/api/kotlin/"))
        }
        // Android framework docs
        externalDocumentationLink {
            url.set(URL("https://developer.android.com/reference/"))
        }
    }
}
```

---

## Generating Documentation

### Generate HTML Documentation

```bash
# Generate docs for app module
./gradlew :app:dokkaHtml

# View generated docs
open app/build/dokka/html/index.html
```

### Generate for All Modules

```bash
./gradlew dokkaHtml
```

### Output Location

Generated documentation: `app/build/dokka/html/`

### CI/CD Integration

Add to your CI pipeline:

```yaml
- name: Generate API Documentation
  run: ./gradlew dokkaHtml

- name: Upload Documentation
  uses: actions/upload-artifact@v3
  with:
    name: api-docs
    path: app/build/dokka/html/
```

---

## Documentation Review Checklist

### Before Creating PR

- [ ] All public APIs have KDoc comments
- [ ] Summary is clear and concise
- [ ] All parameters are documented
- [ ] Return values are explained
- [ ] Exceptions are documented
- [ ] Complex logic has inline comments
- [ ] Examples provided for complex APIs
- [ ] Cross-references added where relevant
- [ ] Spelling and grammar checked
- [ ] Dokka generates without warnings

### Code Review Focus

- [ ] Documentation matches implementation
- [ ] No outdated information
- [ ] Examples are runnable and correct
- [ ] Links to related classes work
- [ ] Consistent terminology used
- [ ] No redundant documentation

---

## Best Practices

### Do's

✅ **Be Specific**:
```kotlin
// Good
/**
 * Fetches products created in the last 30 days, sorted by creation date descending.
 * Maximum 50 products returned. Use pagination for more.
 *
 * @return Flow emitting list of recent products
 */

// Bad
/**
 * Gets products
 */
```

✅ **Provide Examples**:
```kotlin
/**
 * Validates phone number format.
 *
 * **Example**:
 * ```kotlin
 * val isValid = validatePhoneNumber("+919876543210") // true
 * val isInvalid = validatePhoneNumber("123") // false
 * ```
 */
```

✅ **Explain Behavior**:
```kotlin
/**
 * Deletes a product by setting isActive to false.
 *
 * Note: Products are soft-deleted, not removed from database.
 * This preserves order history and prevents broken references.
 */
```

✅ **Document Nullability**:
```kotlin
/**
 * @param userId User identifier (must not be empty)
 * @param email Optional email address (null if not provided)
 * @return User if found, null if not exists
 */
```

### Don'ts

❌ **Don't Repeat Signature**:
```kotlin
// Bad
/**
 * Gets the product ID
 * @return the product ID
 */
val productId: String
```

❌ **Don't Use Vague Terms**:
```kotlin
// Bad
/**
 * Does something with the product
 */
```

❌ **Don't Leave TODOs**:
```kotlin
// Bad
/**
 * TODO: Document this later
 */
```

❌ **Don't Document Obvious Code**:
```kotlin
// Bad
/**
 * Sets the name
 * @param name the name to set
 */
fun setName(name: String)
```

### Formatting

```kotlin
/**
 * Short summary (single line, under 120 chars).
 *
 * Longer description with details, examples, and additional context.
 * Can span multiple paragraphs.
 *
 * Second paragraph if needed.
 *
 * @param firstParam Description of first parameter
 * @param secondParam Description of second parameter with
 *        continuation on next line if needed
 * @return What this function returns
 * @throws IllegalArgumentException When invalid argument provided
 * @see RelatedClass
 */
```

---

## Related Documentation

- [CODE_STYLE.md](../CODE_STYLE.md) - Coding standards
- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines
- [Architecture](architecture.md) - System architecture
- [Dokka Documentation](https://kotlinlang.org/docs/dokka-introduction.html)

---

**Good documentation is code that explains itself. Invest time in writing clear, helpful API documentation!** 📚
