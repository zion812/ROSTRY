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

### Complex Business Logic
  *
  * **Marketplace Filtering Example**:
  * The `GeneralMarketViewModel` applies filters locally using reactive flows.
  *
  * **Logic Flow**:
  * 1. `observeProducts()` combines `productRepository.getProducts()` with `filterState`
  * 2. `ProductEntity.isPublic` is checked to ensure only public items are shown in Marketplace
  * 3. `applyFilters()` handles client-side filtering (category, price range)
  * 4. Result is emitted to `uiState`
  *
  * **Key Constraint**:
  * Products in private context (`HomeFarmerScreen`) ignore `isPublic` check.
  */
 
 ---
 
 ## Repository Contracts

### Repository Interface Documentation Standards

Repository interfaces define the contract between the domain layer and data sources. Proper documentation is essential for understanding data flow and business logic.

### Base Repository Contract

```kotlin
/**
 * Base repository interface providing common operations for all repositories.
 *
 * Defines standard CRUD operations with error handling and resource wrapping.
 * All repository implementations should extend this interface or implement
 * similar patterns for consistency.
 *
 * **Thread Safety**: All methods are thread-safe and can be called from any
 * coroutine context.
 *
 * **Error Handling**: All operations return Resource wrapper to handle
 * success, loading, and error states consistently.
 *
 * @param T Type of entity managed by the repository
 */
interface BaseRepository<T> {
    /**
     * Retrieves all entities of type T.
     *
     * Returns a Flow that emits updates when data changes in the underlying
     * data source. The flow emits Resource wrapper for consistent state
     * handling.
     *
     * **Implementation Notes**:
     * - Should work offline using local cache/database
     * - Updates automatically when data changes
     * - May apply default sorting/filtering
     *
     * @return Flow emitting Resource<List<T>> with current data state
     */
    fun getAll(): Flow<Resource<List<T>>>

    /**
     * Retrieves a specific entity by its identifier.
     *
     * @param id Unique identifier of the entity to retrieve
     * @return Resource<T> with the entity if found, Error if not found
     */
    suspend fun getById(id: String): Resource<T>

    /**
     * Creates a new entity.
     *
     * Validates the entity, stores it in the local data source, and
     * queues for remote synchronization if applicable.
     *
     * @param entity Entity to create
     * @return Resource<String> with the created entity's ID on success
     */
    suspend fun create(entity: T): Resource<String>

    /**
     * Updates an existing entity.
     *
     * Validates the entity, updates it in the local data source, and
     * queues for remote synchronization if applicable.
     *
     * @param entity Entity to update (must have valid ID)
     * @return Resource<Unit> indicating success or failure
     */
    suspend fun update(entity: T): Resource<Unit>

    /**
     * Deletes an entity by its identifier.
     *
     * Marks the entity as deleted in the local data source and
     * queues for remote synchronization if applicable.
     *
     * @param id Unique identifier of the entity to delete
     * @return Resource<Unit> indicating success or failure
     */
    suspend fun delete(id: String): Resource<Unit>
}
```

### Product Repository Contract

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
 * **Data Validation**:
 * - Product name must not be empty and max 100 characters
 * - Price must be positive
 * - Quantity must be non-negative
 * - Seller ID must match authenticated user
 *
 * **Visibility Control**:
 * - Public products (isPublic=true) appear in marketplace
 * - Private products (isPublic=false) only visible to owner
 *
 * @see Product
 * @see ProductEntity
 * @see ProductDao
 */
interface ProductRepository : BaseRepository<Product> {
    /**
     * Retrieves products with optional filtering and pagination.
     *
     * Applies filters to the product list and returns paginated results.
     * Supports filtering by category, price range, location, and other
     * attributes. Results are sorted by relevance or specified criteria.
     *
     * **Filtering Logic**:
     * - Category: Matches exact category enum value
     * - Price Range: Inclusive bounds [minPrice, maxPrice]
     * - Location: Within specified radius of user's location
     * - Status: ACTIVE products only (inactive filtered out)
     *
     * @param filters Optional filters to apply to the product query
     * @param pagination Optional pagination parameters (page, size)
     * @return Flow<Resource<List<Product>>> with filtered products
     */
    fun getProducts(
        filters: ProductFilters? = null,
        pagination: Pagination? = null
    ): Flow<Resource<List<Product>>>

    /**
     * Retrieves products by seller ID.
     *
     * Returns all products owned by the specified seller, regardless
     * of their public/private status. Used for seller's inventory view.
     *
     * @param sellerId ID of the seller whose products to retrieve
     * @return Flow<Resource<List<Product>>> with seller's products
     */
    fun getProductsBySeller(sellerId: String): Flow<Resource<List<Product>>>

    /**
     * Searches products by text query.
     *
     * Performs full-text search on product names, descriptions, and
     * other searchable fields. Uses database indexing for performance.
     *
     * **Search Fields**:
     * - Product name (exact and partial matches)
     * - Description (partial matches)
     * - Breed information (if applicable)
     * - Category keywords
     *
     * @param query Text to search for in product data
     * @return Flow<Resource<List<Product>>> with matching products
     */
    fun searchProducts(query: String): Flow<Resource<List<Product>>>

    /**
     * Updates product visibility status.
     *
     * Changes whether a product appears in the public marketplace.
     * Private products are only visible to the owner.
     *
     * @param productId ID of the product to update
     * @param isPublic New visibility status
     * @return Resource<Unit> indicating success or failure
     */
    suspend fun updateVisibility(productId: String, isPublic: Boolean): Resource<Unit>

    /**
     * Syncs local products with remote data source.
     *
     * Processes pending changes in the sync queue and synchronizes
     * with the remote data source. Handles conflicts using timestamp
     * comparison (remote wins).
     *
     * **Sync Process**:
     * 1. Upload local changes to remote
     * 2. Download remote changes to local
     * 3. Resolve conflicts (remote timestamp wins)
     * 4. Update sync status
     *
     * @return Resource<SyncResult> with sync statistics
     */
    suspend fun syncProducts(): Resource<SyncResult>
}
```

### User Repository Contract

```kotlin
/**
 * Repository for managing user data and authentication state.
 *
 * Handles user profile management, authentication state, and
 * user-specific preferences. Integrates with Firebase Authentication
 * and local data storage.
 *
 * **Authentication Integration**:
 * - Syncs Firebase Auth state with local user profile
 * - Manages custom claims and role assignments
 * - Handles token refresh and validation
 *
 * @see User
 * @see UserEntity
 * @see UserDao
 */
interface UserRepository : BaseRepository<User> {
    /**
     * Retrieves current authenticated user.
     *
     * Returns the user profile for the currently authenticated user.
     * If no user is authenticated, returns an error.
     *
     * @return Resource<User> with current user profile or error
     */
    suspend fun getCurrentUser(): Resource<User>

    /**
     * Updates user profile information.
     *
     * Updates the user's profile data in both local storage and
     * Firebase Authentication. Some fields may require special
     * validation or permissions.
     *
     * **Validation Rules**:
     * - Display name: 1-50 characters
     * - Email: Valid email format (if changing)
     * - Phone: Valid phone number format
     * - Profile image: Valid URL or base64 data
     *
     * @param user Updated user profile data
     * @return Resource<Unit> indicating success or failure
     */
    suspend fun updateUserProfile(user: User): Resource<Unit>

    /**
     * Updates user role and permissions.
     *
     * Changes the user's role (GENERAL, FARMER, ENTHUSIAST) and
     * updates corresponding permissions and access rights.
     *
     * **Role Implications**:
     * - FARMER: Access to farm management features
     * - ENTHUSIAST: Access to breeding and showing features
     * - GENERAL: Basic marketplace access
     *
     * @param userId ID of user to update
     * @param newRole New role assignment
     * @return Resource<Unit> indicating success or failure
     */
    suspend fun updateUserRole(userId: String, newRole: UserRole): Resource<Unit>

    /**
     * Checks if user has required permissions.
     *
     * Evaluates whether the user has the specified permissions
     * based on their role and custom claims.
     *
     * @param userId ID of user to check
     * @param permission Required permission
     * @return Resource<Boolean> with permission status
     */
    suspend fun hasPermission(userId: String, permission: Permission): Resource<Boolean>
}
```

### Transfer Workflow Repository Contract

```kotlin
/**
 * Repository for managing ownership transfer workflows.
 *
 * Handles the complete transfer lifecycle from initiation to completion,
 * including verification, documentation, and audit trails. Ensures
 * secure and traceable transfers between users.
 *
 * **Transfer States**:
 * - INITIATED: Transfer created, awaiting acceptance
 * - ACCEPTED: Recipient accepted, pending verification
 * - VERIFIED: Transfer verified, awaiting completion
 * - COMPLETED: Transfer completed successfully
 * - CANCELLED: Transfer cancelled by either party
 * - DISPUTED: Transfer in dispute resolution
 *
 * @see Transfer
 * @see TransferEntity
 * @see TransferDao
 */
interface TransferWorkflowRepository {
    /**
     * Initiates a new transfer request.
     *
     * Creates a transfer record with initial state and sends
     * notification to the recipient. Validates transfer eligibility
     * and ownership before creation.
     *
     * **Validation Checks**:
     * - Sender owns the product being transferred
     * - Product is eligible for transfer (not sold/locked)
     * - Recipient exists and is active
     * - Transfer limits not exceeded
     *
     * @param transferRequest Details of the transfer to initiate
     * @return Resource<Transfer> with created transfer record
     */
    suspend fun initiateTransfer(transferRequest: TransferRequest): Resource<Transfer>

    /**
     * Updates transfer status and processes state changes.
     *
     * Handles state transitions and triggers appropriate actions
     * based on the new status (notifications, verifications, etc.).
     *
     * **State Transitions**:
     * - INITIATED → ACCEPTED: Recipient accepts transfer
     * - ACCEPTED → VERIFIED: Verification completed
     * - VERIFIED → COMPLETED: Transfer finalized
     * - Any → CANCELLED: Either party cancels
     *
     * @param transferId ID of transfer to update
     * @param newStatus New status for the transfer
     * @param verificationData Optional verification data
     * @return Resource<Transfer> with updated transfer record
     */
    suspend fun updateTransferStatus(
        transferId: String,
        newStatus: TransferStatus,
        verificationData: VerificationData? = null
    ): Resource<Transfer>

    /**
     * Retrieves transfers for a specific user.
     *
     * Returns transfers where the user is either sender or recipient,
     * filtered by status and date range.
     *
     * @param userId ID of user whose transfers to retrieve
     * @param statusFilter Optional status filter
     * @param dateRange Optional date range filter
     * @return Flow<Resource<List<Transfer>>> with user's transfers
     */
    fun getUserTransfers(
        userId: String,
        statusFilter: TransferStatus? = null,
        dateRange: DateRange? = null
    ): Flow<Resource<List<Transfer>>>

    /**
     * Creates transfer verification record.
     *
     * Records verification details including photos, GPS coordinates,
     * and other evidence supporting the transfer completion.
     *
     * @param verificationRequest Verification details
     * @return Resource<TransferVerification> with verification record
     */
    suspend fun createVerification(verificationRequest: VerificationRequest): Resource<TransferVerification>
}
```

### Evidence Order Repository Contract

```kotlin
/**
 * Repository for managing evidence-based order workflows.
 *
 * Implements the 10-state evidence order system with immutable
 * evidence collection and state-locked agreements. Ensures trust
 * through transparent and verifiable order processes.
 *
 * **Order States**:
 * 1. ENQUIRY: Initial inquiry from buyer
 * 2. QUOTE: Seller responds with pricing/terms
 * 3. AGREEMENT: Both parties agree to terms
 * 4. ADVANCE_PAYMENT: Buyer pays initial amount
 * 5. VERIFICATION: Payment proof uploaded/verified
 * 6. DISPATCH: Seller ships product with proof
 * 7. DELIVERY: Product delivered to buyer
 * 8. COMPLETION: Buyer confirms receipt/satisfaction
 * 9. DISPUTE: Raised if issues arise during any state
 * 10. CANCELLED: Order cancelled by either party
 *
 * @see EvidenceOrder
 * @see EvidenceOrderEntity
 * @see EvidenceOrderDao
 */
interface EvidenceOrderRepository {
    /**
     * Creates a new evidence order enquiry.
     *
     * Initiates the evidence order process with initial enquiry
     * from buyer to seller. Creates order record in ENQUIRY state.
     *
     * @param enquiryRequest Initial order enquiry details
     * @return Resource<EvidenceOrder> with created order
     */
    suspend fun createEnquiry(enquiryRequest: EnquiryRequest): Resource<EvidenceOrder>

    /**
     * Submits a quote for an enquiry.
     *
     * Seller responds to buyer's enquiry with pricing, terms,
     * and conditions. Moves order to QUOTE state.
     *
     * @param quoteRequest Quote details and terms
     * @return Resource<EvidenceOrder> with updated order
     */
    suspend fun submitQuote(quoteRequest: QuoteRequest): Resource<EvidenceOrder>

    /**
     * Accepts an order quote and moves to agreement state.
     *
     * Buyer accepts seller's quote, creating binding agreement
     * and moving order to AGREEMENT state.
     *
     * @param orderId ID of order to accept
     * @param acceptanceData Acceptance confirmation data
     * @return Resource<EvidenceOrder> with updated order
     */
    suspend fun acceptQuote(
        orderId: String,
        acceptanceData: OrderAcceptanceData
    ): Resource<EvidenceOrder>

    /**
     * Processes advance payment for an order.
     *
     * Records advance payment from buyer and moves order to
     * ADVANCE_PAYMENT state. Validates payment and updates status.
     *
     * @param paymentRequest Payment details and proof
     * @return Resource<EvidenceOrder> with updated order
     */
    suspend fun processAdvancePayment(paymentRequest: PaymentRequest): Resource<EvidenceOrder>

    /**
     * Verifies payment and moves order to verification state.
     *
     * Validates payment proof and updates order status to VERIFICATION.
     * May trigger additional verification steps.
     *
     * @param verificationRequest Payment verification details
     * @return Resource<EvidenceOrder> with updated order
     */
    suspend fun verifyPayment(verificationRequest: PaymentVerificationRequest): Resource<EvidenceOrder>

    /**
     * Records dispatch with shipping proof.
     *
     * Updates order to DISPATCH state with shipping details
     * and proof of shipment.
     *
     * @param dispatchRequest Dispatch details and proof
     * @return Resource<EvidenceOrder> with updated order
     */
    suspend fun recordDispatch(dispatchRequest: DispatchRequest): Resource<EvidenceOrder>

    /**
     * Confirms delivery and moves to completion state.
     *
     * Records delivery confirmation and moves order toward
     * completion. May require OTP or other verification.
     *
     * @param deliveryConfirmation Delivery confirmation details
     * @return Resource<EvidenceOrder> with updated order
     */
    suspend fun confirmDelivery(deliveryConfirmation: DeliveryConfirmation): Resource<EvidenceOrder>

    /**
     * Retrieves evidence orders for a user.
     *
     * Returns orders where user is either buyer or seller,
     * filtered by state and date range.
     *
     * @param userId ID of user whose orders to retrieve
     * @param stateFilter Optional state filter
     * @param dateRange Optional date range filter
     * @return Flow<Resource<List<EvidenceOrder>>> with user's orders
     */
    fun getUserOrders(
        userId: String,
        stateFilter: OrderState? = null,
        dateRange: DateRange? = null
    ): Flow<Resource<List<EvidenceOrder>>>
}
```

## Related Documentation

- [CODE_STYLE.md](../CODE_STYLE.md) - Coding standards
- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines
- [Architecture](architecture.md) - System architecture
- [Dokka Documentation](https://kotlinlang.org/docs/dokka-introduction.html)
- [Repository Pattern Guide](repository-pattern.md) - Detailed repository implementation guide

---

**Good documentation is code that explains itself. Invest time in writing clear, helpful API documentation!** 📚
