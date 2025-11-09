
**Version:** 1.0  
**Last Updated:** 2025-01-16

---

## 1. Fetcher Architecture

### 1.1 Simple Fetcher Pattern

```kotlin
class ProductFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchAndCache(id: String): Result<Product> = try {
        // Fetch from remote
        val doc = firestore.collection("products")
            .document(id)
            .get()
            .await()
        
        val product = doc.toObject(Product::class.java)
            ?: throw Exception("Product not found")
        
        // Cache locally
        productDao.insert(product.toEntity())
        
        Result.Success(product)
    } catch (e: Exception) {
        // Fallback to local cache
        val cached = productDao.getById(id)?.toDomain()
        if (cached != null) {
            Result.Success(cached)
        } else {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
```

### 1.2 Batch Fetcher Pattern

```kotlin
class ProductBatchFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchBatch(
        ids: List<String>,
        batchSize: Int = 10
    ): Result<List<Product>> = try {
        val products = mutableListOf<Product>()
        
        ids.chunked(batchSize).forEach { chunk ->
            val snapshot = firestore.collection("products")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
            
            val fetched = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)
            }
            
            products.addAll(fetched)
            productDao.insertAll(fetched.map { it.toEntity() })
        }
        
        Result.Success(products)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Batch fetch failed")
    }
}
```

### 1.3 Paginated Fetcher Pattern

```kotlin
class ProductPaginatedFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {
    fun fetchPaginated(
        pageSize: Int = 20
    ): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize),
        pagingSourceFactory = {
            ProductPagingSource(firestore, productDao)
        }
    ).flow
}

class ProductPagingSource(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao
) : PagingSource<QuerySnapshot, Product>() {
    
    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, Product> = try {
        val query = firestore.collection("products")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .limit(params.loadSize.toLong())
        
        val snapshot = if (params.key != null) {
            query.startAfter(params.key!!)
                .get()
                .await()
        } else {
            query.get().await()
        }
        
        val products = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)
        }
        
        productDao.insertAll(products.map { it.toEntity() })
        
        LoadResult.Page(
            data = products,
            prevKey = null,
            nextKey = if (snapshot.isEmpty) null else snapshot.documents.last()
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
    
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Product>): 
        QuerySnapshot? = null
}
```

### 1.4 Incremental Fetcher with Sync State

```kotlin
class IncrementalProductFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val syncStateDao: SyncStateDao
) {
    suspend fun fetchIncremental(): Result<Unit> = try {
        // Get last sync time
        val lastSync = syncStateDao.getLastSyncTime("products") ?: 0L
        
        // Fetch only changed items
        val snapshot = firestore.collection("products")
            .whereGreaterThan("updated_at", lastSync)
            .get()
            .await()
        
        val products = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)
        }
        
        // Update local database
        productDao.insertAll(products.map { it.toEntity() })
        
        // Update sync state
        syncStateDao.markSynced("products", System.currentTimeMillis())
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Incremental fetch failed")
    }
}
```

### 1.5 Fetcher Integration with Repositories

```kotlin
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productFetcher: ProductFetcher,
    private val batchFetcher: ProductBatchFetcher,
    private val incrementalFetcher: IncrementalProductFetcher
) : ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> {
        return productFetcher.fetchAndCache(id)
    }
    
    override suspend fun getProducts(ids: List<String>): Result<List<Product>> {
        return batchFetcher.fetchBatch(ids)
    }
    
    override fun getProductsPaged(): Flow<PagingData<Product>> {
        return incrementalFetcher.fetchPaginated()
    }
    
    override suspend fun syncProducts(): Result<Unit> {
        return incrementalFetcher.fetchIncremental()
    }
}
```

---

## 2. Integration Patterns

### 2.1 Firebase Integration

#### Firestore Integration

```kotlin
@Singleton
class FirestoreProductDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getProduct(id: String): Product = 
        firestore.collection("products")
            .document(id)
            .get()
            .await()
            .toObject(Product::class.java)
            ?: throw Exception("Not found")
    
    suspend fun createProduct(product: Product) {
        firestore.collection("products")
            .document(product.id)
            .set(product)
            .await()
    }
    
    fun watchProduct(id: String): Flow<Product> = callbackFlow {
        val listener = firestore.collection("products")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                snapshot?.toObject(Product::class.java)?.let {
                    trySend(it)
                }
            }
        
        awaitClose { listener.remove() }
    }
}
```

#### Firebase Functions Integration

```kotlin
@Singleton
class FirebaseFunctionsClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    suspend fun initiateTransfer(
        fowlId: String,
        toUserId: String
    ): Result<String> = try {
        val result = functions
            .getHttpsCallable("initiateTransfer")
            .call(mapOf(
                "fowlId" to fowlId,
                "toUserId" to toUserId
            ))
            .await()
        
        val transferId = (result.data as? Map<*, *>)
            ?.get("transferId") as? String
            ?: throw Exception("Invalid response")
        
        Result.Success(transferId)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Function call failed")
    }
}
```

### 2.2 Retrofit Integration

```kotlin
interface ProductApiService {
    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): Product
    
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): ProductListResponse
    
    @POST("products")
    suspend fun createProduct(
        @Body product: Product
    ): CreateProductResponse
}

@Singleton
class RetrofitProductDataSource @Inject constructor(
    private val apiService: ProductApiService
) {
    suspend fun getProduct(id: String): Result<Product> = try {
        Result.Success(apiService.getProduct(id))
    } catch (e: Exception) {
        Result.Error(e.message ?: "API call failed")
    }
}
```

### 2.3 Multi-Source Data Fetching

```kotlin
@Singleton
class MultiSourceProductRepository @Inject constructor(
    private val localDataSource: ProductLocalDataSource,
    private val remoteDataSource: ProductRemoteDataSource,
    private val apiDataSource: ProductApiDataSource
) : ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> {
        // Try local first
        localDataSource.getProduct(id)?.let {
            return Result.Success(it)
        }
        
        // Try Firestore
        remoteDataSource.getProduct(id)
            .onSuccess { product ->
                localDataSource.saveProduct(product)
                return Result.Success(product)
            }
        
        // Try REST API as fallback
        return apiDataSource.getProduct(id)
            .onSuccess { product ->
                localDataSource.saveProduct(product)
            }
    }
}
```

---

## 3. State Management Flow

### 3.1 ViewModel State Pattern

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {
    
    // State Definition
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = 
        _uiState.asStateFlow()
    
    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getAllProducts()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    products = result.data,
                                    error = null
                                )
                            }
                            analyticsTracker.trackEvent("products_loaded")
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
    
    fun onProductClick(product: Product) {
        viewModelScope.launch {
            analyticsTracker.trackEvent(
                "product_clicked",
                mapOf("product_id" to product.id)
            )
            _navigationEvent.send(
                NavigationEvent.NavigateToProductDetail(product.id)
            )
        }
    }
}

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

sealed class NavigationEvent {
    data class NavigateToProductDetail(val productId: String) : NavigationEvent()
}
```

### 3.2 Compose UI Integration

```kotlin
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToProductDetail -> {
                    // Navigate to detail screen
                }
            }
        }
    }
    
    ProductListContent(
        products = uiState.products,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onProductClick = viewModel::onProductClick,
        onRefresh = viewModel::loadProducts
    )
}

@Composable
fun ProductListContent(
    products: List<Product>,
    isLoading: Boolean,
    error: String?,
    onProductClick: (Product) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading -> LoadingState()
        error != null -> ErrorState(error, onRefresh)
        products.isEmpty() -> EmptyState()
        else -> ProductList(products, onProductClick)
    }
}
```

---

## 4. Background Processing

### 4.1 WorkManager Integration

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ProductRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = try {
        repository.syncProducts()
        Result.retry()
    } catch (e: Exception) {
        Timber.e(e, "Sync failed")
        Result.retry()
    }
}

// Schedule in RostryApp
class RostryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "product_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<SyncWorker>(
                Duration.ofHours(6)
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }
}
```

---

## 5. Testing Strategy

### 5.1 Repository Testing

```kotlin
@ExperimentalCoroutinesTest
class ProductRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private val productDao = mockk<ProductDao>()
    private val firestore = mockk<FirebaseFirestore>()
    private val repository = ProductRepositoryImpl(
        productDao, firestore, mockk(), mockk()
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @Test
    fun `getProduct returns success when data exists`() = runTest {
        // Given
        val productId = "123"
        val entity = ProductEntity(
            id = productId,
            name = "Test Product",
            price = 100.0,
            sellerId = "seller1",
            createdAt = System.currentTimeMillis()
        )
        coEvery { productDao.getById(productId) } returns entity
        
        // When
        val result = repository.getProduct(productId)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals("Test Product", (result as Result.Success).data.name)
    }
}
```

### 5.2 ViewModel Testing

```kotlin
@ExperimentalCoroutinesTest
class ProductListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<ProductRepository>()
    private lateinit var viewModel: ProductListViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductListViewModel(repository, mockk())
    }
    
    @Test
    fun `loadProducts updates state with products`() = runTest {
        // Given
        val products = listOf(
            Product("1", "Product 1", 100.0, "seller1"),
            Product("2", "Product 2", 200.0, "seller2")
        )
        coEvery { repository.getAllProducts() } returns 
            flowOf(Result.Success(products))
        
        // When
        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(products, viewModel.uiState.value.products)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
```

---

## 6. Performance Considerations

### 6.1 Caching Strategy

```kotlin
class CachedProductRepository @Inject constructor(
    private val repository: ProductRepository,
    private val cache: MutableMap<String, Product> = mutableMapOf()
) : ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> {
        cache[id]?.let { return Result.Success(it) }
        
        return repository.getProduct(id).onSuccess { product ->
            cache[id] = product
        }
    }
}
```

### 6.2 Pagination Optimization

```kotlin
val products: Flow<PagingData<Product>> = Pager(
    config = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = true
    ),
    pagingSourceFactory = { productDao.getProductsPaged() }
).flow
    .map { pagingData -> pagingData.map { it.toDomain() } }
    .cachedIn(viewModelScope)
```

---

**Continue to PART 3 for Extension Guide & Common Patterns**
# ROSTRY End-to-End Implementation Review - PART 2
## Fetcher Architecture & Integration Patterns

**Version:** 1.0  
**Last Updated:** 2025-01-16

---

## 1. Fetcher Architecture

### 1.1 Simple Fetcher Pattern

```kotlin
class ProductFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchAndCache(id: String): Result<Product> = try {
        // Fetch from remote
        val doc = firestore.collection("products")
            .document(id)
            .get()
            .await()
        
        val product = doc.toObject(Product::class.java)
            ?: throw Exception("Product not found")
        
        // Cache locally
        productDao.insert(product.toEntity())
        
        Result.Success(product)
    } catch (e: Exception) {
        // Fallback to local cache
        val cached = productDao.getById(id)?.toDomain()
        if (cached != null) {
            Result.Success(cached)
        } else {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
```

### 1.2 Batch Fetcher Pattern

```kotlin
class ProductBatchFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchBatch(
        ids: List<String>,
        batchSize: Int = 10
    ): Result<List<Product>> = try {
        val products = mutableListOf<Product>()
        
        ids.chunked(batchSize).forEach { chunk ->
            val snapshot = firestore.collection("products")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
            
            val fetched = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)
            }
            
            products.addAll(fetched)
            productDao.insertAll(fetched.map { it.toEntity() })
        }
        
        Result.Success(products)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Batch fetch failed")
    }
}
```

### 1.3 Paginated Fetcher Pattern

```kotlin
class ProductPaginatedFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) {
    fun fetchPaginated(
        pageSize: Int = 20
    ): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = pageSize),
        pagingSourceFactory = {
            ProductPagingSource(firestore, productDao)
        }
    ).flow
}

class ProductPagingSource(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao
) : PagingSource<QuerySnapshot, Product>() {
    
    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, Product> = try {
        val query = firestore.collection("products")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .limit(params.loadSize.toLong())
        
        val snapshot = if (params.key != null) {
            query.startAfter(params.key!!)
                .get()
                .await()
        } else {
            query.get().await()
        }
        
        val products = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)
        }
        
        productDao.insertAll(products.map { it.toEntity() })
        
        LoadResult.Page(
            data = products,
            prevKey = null,
            nextKey = if (snapshot.isEmpty) null else snapshot.documents.last()
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
    
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Product>): 
        QuerySnapshot? = null
}
```

### 1.4 Incremental Fetcher with Sync State

```kotlin
class IncrementalProductFetcher @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val syncStateDao: SyncStateDao
) {
    suspend fun fetchIncremental(): Result<Unit> = try {
        // Get last sync time
        val lastSync = syncStateDao.getLastSyncTime("products") ?: 0L
        
        // Fetch only changed items
        val snapshot = firestore.collection("products")
            .whereGreaterThan("updated_at", lastSync)
            .get()
            .await()
        
        val products = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)
        }
        
        // Update local database
        productDao.insertAll(products.map { it.toEntity() })
        
        // Update sync state
        syncStateDao.markSynced("products", System.currentTimeMillis())
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Incremental fetch failed")
    }
}
```

### 1.5 Fetcher Integration with Repositories

```kotlin
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productFetcher: ProductFetcher,
    private val batchFetcher: ProductBatchFetcher,
    private val incrementalFetcher: IncrementalProductFetcher
) : ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> {
        return productFetcher.fetchAndCache(id)
    }
    
    override suspend fun getProducts(ids: List<String>): Result<List<Product>> {
        return batchFetcher.fetchBatch(ids)
    }
    
    override fun getProductsPaged(): Flow<PagingData<Product>> {
        return incrementalFetcher.fetchPaginated()
    }
    
    override suspend fun syncProducts(): Result<Unit> {
        return incrementalFetcher.fetchIncremental()
    }
}
```

---

## 2. Integration Patterns

### 2.1 Firebase Integration

#### Firestore Integration

```kotlin
@Singleton
class FirestoreProductDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getProduct(id: String): Product = 
        firestore.collection("products")
            .document(id)
            .get()
            .await()
            .toObject(Product::class.java)
            ?: throw Exception("Not found")
    
    suspend fun createProduct(product: Product) {
        firestore.collection("products")
            .document(product.id)
            .set(product)
            .await()
    }
    
    fun watchProduct(id: String): Flow<Product> = callbackFlow {
        val listener = firestore.collection("products")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                snapshot?.toObject(Product::class.java)?.let {
                    trySend(it)
                }
            }
        
        awaitClose { listener.remove() }
    }
}
```

#### Firebase Functions Integration

```kotlin
@Singleton
class FirebaseFunctionsClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    suspend fun initiateTransfer(
        fowlId: String,
        toUserId: String
    ): Result<String> = try {
        val result = functions
            .getHttpsCallable("initiateTransfer")
            .call(mapOf(
                "fowlId" to fowlId,
                "toUserId" to toUserId
            ))
            .await()
        
        val transferId = (result.data as? Map<*, *>)
            ?.get("transferId") as? String
            ?: throw Exception("Invalid response")
        
        Result.Success(transferId)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Function call failed")
    }
}
```

### 2.2 Retrofit Integration

```kotlin
interface ProductApiService {
    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): Product
    
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): ProductListResponse
    
    @POST("products")
    suspend fun createProduct(
        @Body product: Product
    ): CreateProductResponse
}

@Singleton
class RetrofitProductDataSource @Inject constructor(
    private val apiService: ProductApiService
) {
    suspend fun getProduct(id: String): Result<Product> = try {
        Result.Success(apiService.getProduct(id))
    } catch (e: Exception) {
        Result.Error(e.message ?: "API call failed")
    }
}
```

### 2.3 Multi-Source Data Fetching

```kotlin
@Singleton
class MultiSourceProductRepository @Inject constructor(
    private val localDataSource: ProductLocalDataSource,
    private val remoteDataSource: ProductRemoteDataSource,
    private val apiDataSource: ProductApiDataSource
) : ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> {
        // Try local first
        localDataSource.getProduct(id)?.let {
            return Result.Success(it)
        }
        
        // Try Firestore
        remoteDataSource.getProduct(id)
            .onSuccess { product ->
                localDataSource.saveProduct(product)
                return Result.Success(product)
            }
        
        // Try REST API as fallback
        return apiDataSource.getProduct(id)
            .onSuccess { product ->
                localDataSource.saveProduct(product)
            }
    }
}
```

---

## 3. State Management Flow

### 3.1 ViewModel State Pattern

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {
    
    // State Definition
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = 
        _uiState.asStateFlow()
    
    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getAllProducts()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    products = result.data,
                                    error = null
                                )
                            }
                            analyticsTracker.trackEvent("products_loaded")
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
    
    fun onProductClick(product: Product) {
        viewModelScope.launch {
            analyticsTracker.trackEvent(
                "product_clicked",
                mapOf("product_id" to product.id)
            )
            _navigationEvent.send(
                NavigationEvent.NavigateToProductDetail(product.id)
            )
        }
    }
}

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

sealed class NavigationEvent {
    data class NavigateToProductDetail(val productId: String) : NavigationEvent()
}
```

### 3.2 Compose UI Integration

```kotlin
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToProductDetail -> {
                    // Navigate to detail screen
                }
            }
        }
    }
    
    ProductListContent(
        products = uiState.products,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onProductClick = viewModel::onProductClick,
        onRefresh = viewModel::loadProducts
    )
}

@Composable
fun ProductListContent(
    products: List<Product>,
    isLoading: Boolean,
    error: String?,
    onProductClick: (Product) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        isLoading -> LoadingState()
        error != null -> ErrorState(error, onRefresh)
        products.isEmpty() -> EmptyState()
        else -> ProductList(products, onProductClick)
    }
}
```

---

## 4. Background Processing

### 4.1 WorkManager Integration

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ProductRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = try {
        repository.syncProducts()
        Result.retry()
    } catch (e: Exception) {
        Timber.e(e, "Sync failed")
        Result.retry()
    }
}

// Schedule in RostryApp
class RostryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "product_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<SyncWorker>(
                Duration.ofHours(6)
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }
}
```

---

## 5. Testing Strategy

### 5.1 Repository Testing

```kotlin
@ExperimentalCoroutinesTest
class ProductRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private val productDao = mockk<ProductDao>()
    private val firestore = mockk<FirebaseFirestore>()
    private val repository = ProductRepositoryImpl(
        productDao, firestore, mockk(), mockk()
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @Test
    fun `getProduct returns success when data exists`() = runTest {
        // Given
        val productId = "123"
        val entity = ProductEntity(
            id = productId,
            name = "Test Product",
            price = 100.0,
            sellerId = "seller1",
            createdAt = System.currentTimeMillis()
        )
        coEvery { productDao.getById(productId) } returns entity
        
        // When
        val result = repository.getProduct(productId)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals("Test Product", (result as Result.Success).data.name)
    }
}
```

### 5.2 ViewModel Testing

```kotlin
@ExperimentalCoroutinesTest
class ProductListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<ProductRepository>()
    private lateinit var viewModel: ProductListViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductListViewModel(repository, mockk())
    }
    
    @Test
    fun `loadProducts updates state with products`() = runTest {
        // Given
        val products = listOf(
            Product("1", "Product 1", 100.0, "seller1"),
            Product("2", "Product 2", 200.0, "seller2")
        )
        coEvery { repository.getAllProducts() } returns 
            flowOf(Result.Success(products))
        
        // When
        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(products, viewModel.uiState.value.products)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
```

---

## 6. Performance Considerations

### 6.1 Caching Strategy

```kotlin
class CachedProductRepository @Inject constructor(
    private val repository: ProductRepository,
    private val cache: MutableMap<String, Product> = mutableMapOf()
) : ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> {
        cache[id]?.let { return Result.Success(it) }
        
        return repository.getProduct(id).onSuccess { product ->
            cache[id] = product
        }
    }
}
```

### 6.2 Pagination Optimization

```kotlin
val products: Flow<PagingData<Product>> = Pager(
    config = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = true
    ),
    pagingSourceFactory = { productDao.getProductsPaged() }
).flow
    .map { pagingData -> pagingData.map { it.toDomain() } }
    .cachedIn(viewModelScope)
```

---

**Continue to PART 3 for Extension Guide & Common Patterns**
