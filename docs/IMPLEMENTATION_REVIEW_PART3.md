# ROSTRY End-to-End Implementation Review - PART 3
## Extension Guide & Common Patterns

**Version:** 1.0  
**Last Updated:** 2025-01-16

---

## 1. Extension Guide: Adding New Features

### 1.1 Step-by-Step: Adding a New Repository

#### Step 1: Define Domain Model

```kotlin
// domain/model/NewFeature.kt
data class NewFeature(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

#### Step 2: Create Database Entity

```kotlin
// data/database/entity/NewFeatureEntity.kt
@Entity(tableName = "new_features")
data class NewFeatureEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
```

#### Step 3: Create DAO

```kotlin
// data/database/dao/NewFeatureDao.kt
@Dao
interface NewFeatureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NewFeatureEntity)
    
    @Update
    suspend fun update(entity: NewFeatureEntity)
    
    @Delete
    suspend fun delete(entity: NewFeatureEntity)
    
    @Query("SELECT * FROM new_features WHERE id = :id")
    suspend fun getById(id: String): NewFeatureEntity?
    
    @Query("SELECT * FROM new_features ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<NewFeatureEntity>>
}
```

#### Step 4: Add DAO to Database

```kotlin
// data/database/AppDatabase.kt
@Database(
    entities = [
        // ... existing entities
        NewFeatureEntity::class
    ],
    version = 17,
    autoMigrations = [
        AutoMigration(from = 16, to = 17)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    // ... existing DAOs
    abstract fun newFeatureDao(): NewFeatureDao
}
```

#### Step 5: Create Repository Interface

```kotlin
// data/repository/NewFeatureRepository.kt
interface NewFeatureRepository {
    suspend fun getFeature(id: String): Result<NewFeature>
    fun getAllFeatures(): Flow<Result<List<NewFeature>>>
    suspend fun createFeature(feature: NewFeature): Result<String>
    suspend fun updateFeature(feature: NewFeature): Result<Unit>
    suspend fun deleteFeature(id: String): Result<Unit>
}
```

#### Step 6: Implement Repository

```kotlin
// data/repository/NewFeatureRepositoryImpl.kt
@Singleton
class NewFeatureRepositoryImpl @Inject constructor(
    private val newFeatureDao: NewFeatureDao,
    private val firestore: FirebaseFirestore
) : BaseRepository(), NewFeatureRepository {
    
    override suspend fun getFeature(id: String): Result<NewFeature> =
        safeCall {
            newFeatureDao.getById(id)?.toDomain()
                ?: throw Exception("Feature not found")
        }
    
    override fun getAllFeatures(): Flow<Result<List<NewFeature>>> =
        newFeatureDao.getAllFlow()
            .map { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { error ->
                emit(Result.Error(error.message ?: "Unknown error"))
            }
    
    override suspend fun createFeature(feature: NewFeature): Result<String> =
        safeCall {
            val entity = feature.toEntity()
            newFeatureDao.insert(entity)
            
            firestore.collection("new_features")
                .document(feature.id)
                .set(feature)
                .await()
            
            feature.id
        }
    
    override suspend fun updateFeature(feature: NewFeature): Result<Unit> =
        safeCall {
            newFeatureDao.update(feature.toEntity())
            firestore.collection("new_features")
                .document(feature.id)
                .set(feature)
                .await()
        }
    
    override suspend fun deleteFeature(id: String): Result<Unit> =
        safeCall {
            newFeatureDao.delete(id)
            firestore.collection("new_features")
                .document(id)
                .delete()
                .await()
        }
    
    private fun NewFeatureEntity.toDomain(): NewFeature =
        NewFeature(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    
    private fun NewFeature.toEntity(): NewFeatureEntity =
        NewFeatureEntity(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
}
```

#### Step 7: Register in Hilt Module

```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // ... existing bindings
    
    @Binds
    @Singleton
    abstract fun bindNewFeatureRepository(
        impl: NewFeatureRepositoryImpl
    ): NewFeatureRepository
}
```

#### Step 8: Create ViewModel

```kotlin
// ui/newfeature/NewFeatureViewModel.kt
@HiltViewModel
class NewFeatureViewModel @Inject constructor(
    private val repository: NewFeatureRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NewFeatureUiState())
    val uiState: StateFlow<NewFeatureUiState> = _uiState.asStateFlow()
    
    init {
        loadFeatures()
    }
    
    fun loadFeatures() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getAllFeatures()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    features = result.data
                                )
                            }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                        Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }
}

data class NewFeatureUiState(
    val isLoading: Boolean = false,
    val features: List<NewFeature> = emptyList(),
    val error: String? = null
)
```

#### Step 9: Create Compose Screen

```kotlin
// ui/newfeature/NewFeatureScreen.kt
@Composable
fun NewFeatureScreen(
    viewModel: NewFeatureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(uiState.error!!)
        uiState.features.isEmpty() -> EmptyState()
        else -> FeatureList(uiState.features)
    }
}

@Composable
fun FeatureList(features: List<NewFeature>) {
    LazyColumn {
        items(features) { feature ->
            FeatureCard(feature)
        }
    }
}
```

#### Step 10: Add Navigation Route

```kotlin
// ui/navigation/Routes.kt
object Routes {
    // ... existing routes
    const val NEW_FEATURE = "new_feature"
    const val NEW_FEATURE_DETAIL = "new_feature/{id}"
}

// ui/navigation/AppNavHost.kt
NavHost(navController, startDestination = Routes.HOME) {
    // ... existing routes
    composable(Routes.NEW_FEATURE) {
        NewFeatureScreen()
    }
}
```

---

## 2. Common Patterns & Best Practices

### 2.1 Error Handling Pattern

```kotlin
// ✅ GOOD: Comprehensive error handling
suspend fun getProduct(id: String): Result<Product> = try {
    val product = firestore.collection("products")
        .document(id)
        .get()
        .await()
        .toObject(Product::class.java)
        ?: throw Exception("Product not found")
    
    Result.Success(product)
} catch (e: FirebaseNetworkException) {
    // Network error - try local cache
    val cached = productDao.getById(id)?.toDomain()
    if (cached != null) {
        Result.Success(cached)
    } else {
        Result.Error("Network error and no cache available")
    }
} catch (e: Exception) {
    Timber.e(e, "Failed to get product")
    Result.Error(e.message ?: "Unknown error")
}

// ❌ AVOID: Swallowing exceptions
suspend fun getProduct(id: String): Product {
    return try {
        firestore.collection("products")
            .document(id)
            .get()
            .await()
            .toObject(Product::class.java)!!
    } catch (e: Exception) {
        Product() // Silent failure
    }
}
```

### 2.2 State Management Pattern

```kotlin
// ✅ GOOD: Proper state hoisting
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.updateProduct(product)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onError { error ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = error)
                    }
                }
        }
    }
}

// ❌ AVOID: Mutable state in Compose
@Composable
fun ProductScreen() {
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // This causes recomposition issues
}
```

### 2.3 Flow Handling Pattern

```kotlin
// ✅ GOOD: Proper Flow collection
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    ProductListContent(
        products = uiState.products,
        isLoading = uiState.isLoading
    )
}

// ❌ AVOID: Blocking Flow collection
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState = runBlocking {
        viewModel.uiState.first()
    }
    // This blocks the UI thread
}
```

### 2.4 Dependency Injection Pattern

```kotlin
// ✅ GOOD: Constructor injection with Hilt
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel()

// ❌ AVOID: Service locator pattern
class ProductViewModel {
    private val repository = ServiceLocator.getRepository()
    private val analytics = ServiceLocator.getAnalytics()
}
```

### 2.5 Database Transaction Pattern

```kotlin
// ✅ GOOD: Atomic operations
@Dao
interface TransferDao {
    @Transaction
    suspend fun completeTransfer(
        transfer: TransferEntity,
        auditLog: AuditLogEntity
    ) {
        update(transfer)
        auditLogDao.insert(auditLog)
    }
}

// ❌ AVOID: Non-atomic operations
suspend fun completeTransfer(transfer: Transfer) {
    transferDao.update(transfer.toEntity())
    // If this fails, transfer is already updated
    auditLogDao.insert(auditLog.toEntity())
}
```

---

## 3. Anti-Patterns to Avoid

### 3.1 Blocking Operations

```kotlin
// ❌ AVOID
viewModelScope.launch {
    val data = repository.getData().first() // Blocks
}

// ✅ GOOD
viewModelScope.launch {
    repository.getData()
        .collect { data ->
            // Handle data
        }
}
```

### 3.2 Unhandled Exceptions

```kotlin
// ❌ AVOID
viewModelScope.launch {
    repository.getData() // Exception crashes app
}

// ✅ GOOD
viewModelScope.launch {
    try {
        repository.getData()
    } catch (e: Exception) {
        _uiState.update { it.copy(error = e.message) }
    }
}
```

### 3.3 Memory Leaks

```kotlin
// ❌ AVOID: Holding context reference
class MyRepository {
    private val context: Context = applicationContext
}

// ✅ GOOD: Use @ApplicationContext
class MyRepository @Inject constructor(
    @ApplicationContext private val context: Context
)
```

### 3.4 Tight Coupling

```kotlin
// ❌ AVOID: Direct dependency
class ProductViewModel {
    private val repository = ProductRepositoryImpl()
}

// ✅ GOOD: Interface dependency
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
)
```

---

## 4. Performance Optimization Checklist

### 4.1 Database Optimization

- [ ] Add indexes to frequently queried columns
- [ ] Use pagination for large datasets
- [ ] Implement incremental sync
- [ ] Use transactions for related operations
- [ ] Monitor query performance with Profiler

### 4.2 Network Optimization

- [ ] Implement request batching
- [ ] Use incremental fetching
- [ ] Cache responses locally
- [ ] Implement retry logic with exponential backoff
- [ ] Use compression for large payloads

### 4.3 UI Optimization

- [ ] Use LazyColumn/LazyRow for lists
- [ ] Implement proper key() in list items
- [ ] Use remember for expensive computations
- [ ] Avoid recomposition of large trees
- [ ] Profile with Compose Layout Inspector

### 4.4 Memory Optimization

- [ ] Release resources in onDispose
- [ ] Use WeakReference for caches
- [ ] Implement proper lifecycle management
- [ ] Monitor with LeakCanary
- [ ] Profile with Memory Profiler

---

## 5. Testing Checklist

### 5.1 Unit Tests

- [ ] Test repository methods with mocked DAOs
- [ ] Test ViewModel state updates
- [ ] Test error handling paths
- [ ] Test data transformations
- [ ] Achieve 80%+ coverage

### 5.2 Integration Tests

- [ ] Test repository + DAO integration
- [ ] Test database migrations
- [ ] Test Firebase integration
- [ ] Test navigation flows
- [ ] Test background workers

### 5.3 UI Tests

- [ ] Test screen rendering
- [ ] Test user interactions
- [ ] Test navigation
- [ ] Test accessibility
- [ ] Test error states

---

## 6. Monitoring & Debugging

### 6.1 Logging Setup

```kotlin
// RostryApp.kt
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
} else {
    Timber.plant(CrashlyticsTree())
}

// Usage
Timber.d("Debug message")
Timber.e(exception, "Error occurred")
```

### 6.2 Analytics Tracking

```kotlin
// Track important events
analyticsTracker.trackEvent("product_viewed", mapOf(
    "product_id" to product.id,
    "category" to product.category
))
```

### 6.3 Performance Monitoring

```kotlin
// Firebase Performance Monitoring
val trace = FirebasePerformance.getInstance()
    .newTrace("product_load")
trace.start()
// ... load product
trace.stop()
```

---

## 7. Deployment Checklist

- [ ] All tests passing
- [ ] Code reviewed
- [ ] Documentation updated
- [ ] Database migrations tested
- [ ] Firebase rules updated
- [ ] Analytics events tracked
- [ ] Error handling verified
- [ ] Performance profiled
- [ ] Security review completed
- [ ] Release notes prepared

---

**For more details, refer to PART 1 & 2 of this review**
