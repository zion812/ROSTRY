
**Version:** 1.0  
**Last Updated:** 2025-01-16  
**Print-Friendly:** Yes

---

## 🏗️ Architecture Layers

```
┌─────────────────────────────────────────┐
│  UI (Compose Screens)                   │  ui/
├─────────────────────────────────────────┤
│  ViewModel (State Management)           │  ui/<feature>/*ViewModel.kt
├─────────────────────────────────────────┤
│  Repository (Business Logic)            │  data/repository/
├─────────────────────────────────────────┤
│  Data Sources (Room, Firebase, API)     │  data/database/, services/
└─────────────────────────────────────────┘
```

---

## 📋 Quick Task Guide

### Task 1: Add New Entity

```kotlin
// 1. Create entity
@Entity(tableName = "my_entities")
data class MyEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)

// 2. Create DAO
@Dao
interface MyDao {
    @Insert suspend fun insert(entity: MyEntity)
    @Query("SELECT * FROM my_entities") 
    fun getAll(): Flow<List<MyEntity>>
}

// 3. Add to AppDatabase
@Database(entities = [..., MyEntity::class], version = 17)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myDao(): MyDao
}

// 4. Create migration if needed
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS my_entities (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)
    }
}
```

### Task 2: Create Repository

```kotlin
interface MyRepository {
    suspend fun get(id: String): Result<MyItem>
    fun getAll(): Flow<Result<List<MyItem>>>
    suspend fun create(item: MyItem): Result<String>
}

@Singleton
class MyRepositoryImpl @Inject constructor(
    private val dao: MyDao,
    private val firestore: FirebaseFirestore
) : BaseRepository(), MyRepository {
    
    override suspend fun get(id: String): Result<MyItem> =
        safeCall {
            dao.getById(id)?.toDomain()
                ?: throw Exception("Not found")
        }
    
    override fun getAll(): Flow<Result<List<MyItem>>> =
        dao.getAll()
            .map { Result.Success(it.map { it.toDomain() }) }
            .catch { emit(Result.Error(it.message ?: "Error")) }
    
    override suspend fun create(item: MyItem): Result<String> =
        safeCall {
            dao.insert(item.toEntity())
            firestore.collection("my_items")
                .document(item.id)
                .set(item)
                .await()
            item.id
        }
}
```

### Task 3: Create ViewModel

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
    
    init { loadItems() }
    
    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAll()
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Success -> 
                                it.copy(isLoading = false, items = result.data)
                            is Result.Error -> 
                                it.copy(isLoading = false, error = result.message)
                            Result.Loading -> it.copy(isLoading = true)
                        }
                    }
                }
        }
    }
}

data class MyUiState(
    val isLoading: Boolean = false,
    val items: List<MyItem> = emptyList(),
    val error: String? = null
)
```

### Task 4: Create Compose Screen

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(uiState.error!!)
        uiState.items.isEmpty() -> EmptyState()
        else -> ItemList(uiState.items)
    }
}

@Composable
fun ItemList(items: List<MyItem>) {
    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemCard(item)
        }
    }
}
```

### Task 5: Add Navigation Route

```kotlin
// In Routes.kt
object Routes {
    const val MY_SCREEN = "my_screen"
    const val MY_DETAIL = "my_detail/{id}"
}

// In AppNavHost.kt
composable(Routes.MY_SCREEN) {
    MyScreen()
}

composable(Routes.MY_DETAIL) { backStackEntry ->
    val id = backStackEntry.arguments?.getString("id") ?: return@composable
    MyDetailScreen(id)
}
```

### Task 6: Register in Hilt

```kotlin
// In RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMyRepository(
        impl: MyRepositoryImpl
    ): MyRepository
}
```

---

## 🔄 Data Flow Patterns

### Read Flow
```
User taps → ViewModel.loadItems()
  ↓
Repository.getAll()
  ├─ Try local (DAO)
  ├─ Try remote (Firebase)
  └─ Return Result
  ↓
StateFlow updates
  ↓
Compose recomposes
  ↓
UI displays data
```

### Write Flow
```
User submits → ViewModel.createItem(item)
  ↓
Repository.create(item)
  ├─ Validate
  ├─ Save local (DAO)
  ├─ Upload remote (Firebase)
  └─ Return Result
  ↓
StateFlow updates
  ↓
UI shows success/error
```

---

## 🧪 Testing Template

```kotlin
@ExperimentalCoroutinesTest
class MyRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private val dao = mockk<MyDao>()
    private val firestore = mockk<FirebaseFirestore>()
    private val repository = MyRepositoryImpl(dao, firestore)
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @Test
    fun `getAll returns success`() = runTest {
        // Given
        val entities = listOf(MyEntity("1", "Test"))
        coEvery { dao.getAll() } returns flowOf(entities)
        
        // When
        val result = repository.getAll().first()
        
        // Then
        assertTrue(result is Result.Success)
    }
}
```

---

## 🔌 Integration Patterns

### Firebase Firestore
```kotlin
// Read
val doc = firestore.collection("items")
    .document(id)
    .get()
    .await()

// Write
firestore.collection("items")
    .document(id)
    .set(item)
    .await()

// Listen
firestore.collection("items")
    .addSnapshotListener { snapshot, error ->
        // Handle updates
    }
```

### Retrofit API
```kotlin
interface ApiService {
    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): Item
    
    @POST("items")
    suspend fun createItem(@Body item: Item): CreateResponse
}
```

### Room Database
```kotlin
@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<ItemEntity>>
    
    @Insert
    suspend fun insert(item: ItemEntity)
    
    @Transaction
    suspend fun updateWithAudit(item: ItemEntity, audit: AuditEntity) {
        update(item)
        auditDao.insert(audit)
    }
}
```

---

## 📊 Common Queries

### Get Single Item
```kotlin
@Query("SELECT * FROM items WHERE id = :id")
suspend fun getById(id: String): ItemEntity?
```

### Get with Filter
```kotlin
@Query("SELECT * FROM items WHERE category = :category")
fun getByCategory(category: String): Flow<List<ItemEntity>>
```

### Get with Pagination
```kotlin
@Query("SELECT * FROM items ORDER BY created_at DESC")
fun getPaged(): PagingSource<Int, ItemEntity>
```

### Get with Relations
```kotlin
@Transaction
@Query("SELECT * FROM items WHERE id = :id")
fun getWithDetails(id: String): Flow<ItemWithDetails>
```

### Count
```kotlin
@Query("SELECT COUNT(*) FROM items")
suspend fun count(): Int
```

### Delete
```kotlin
@Query("DELETE FROM items WHERE created_at < :timestamp")
suspend fun deleteOlderThan(timestamp: Long)
```

---

## ⚠️ Common Mistakes

| ❌ Avoid | ✅ Do Instead |
|---------|--------------|
| `runBlocking { flow.first() }` | `flow.collect { }` |
| Direct context reference | `@ApplicationContext` |
| Unhandled exceptions | Try-catch with Result |
| Mutable state in Compose | StateFlow in ViewModel |
| Tight coupling | Interface-based DI |
| Blocking UI | Coroutines + Flow |
| No error handling | Result type pattern |
| Hardcoded strings | Constants/Resources |

---

## 🎯 Checklist: Adding a Feature

- [ ] Create entity in `data/database/entity/`
- [ ] Create DAO in `data/database/dao/`
- [ ] Add to AppDatabase
- [ ] Create migration if needed
- [ ] Create repository interface
- [ ] Implement repository
- [ ] Register in Hilt module
- [ ] Create ViewModel
- [ ] Create Compose screen
- [ ] Add navigation route
- [ ] Write unit tests
- [ ] Write UI tests
- [ ] Update documentation

---

## 📁 File Locations

```
com.rio.rostry/
├── data/
│   ├── database/
│   │   ├── entity/MyEntity.kt
│   │   ├── dao/MyDao.kt
│   │   └── AppDatabase.kt
│   └── repository/
│       ├── MyRepository.kt
│       └── MyRepositoryImpl.kt
├── domain/
│   └── model/MyItem.kt
├── ui/
│   └── myfeature/
│       ├── MyScreen.kt
│       └── MyViewModel.kt
└── di/
    └── RepositoryModule.kt
```

---

## 🚀 Common Commands

```bash
# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Check code style
./gradlew ktlint

# Generate Dokka docs
./gradlew :app:dokkaHtml

# Clean build
./gradlew clean build

# Run specific test
./gradlew test --tests "MyTest"
```

---

## 🔐 Security Checklist

- [ ] No hardcoded secrets
- [ ] Use BuildConfig for API keys
- [ ] Validate input on client & server
- [ ] Encrypt sensitive data
- [ ] Use HTTPS only
- [ ] Implement RBAC checks
- [ ] Log security events
- [ ] Review Firebase rules

---

## 📈 Performance Tips

- Use `LazyColumn` for lists
- Implement pagination
- Cache responses
- Use `remember` for expensive ops
- Profile with Layout Inspector
- Monitor with Profiler
- Use incremental sync
- Batch network requests

---

## 🔗 Related Documents

| Document | Purpose |
|----------|---------|
| `IMPLEMENTATION_REVIEW_INDEX.md` | Navigation guide |
| `archive/IMPLEMENTATION_REVIEW_SUMMARY.md` | Executive summary (archived) |
| `IMPLEMENTATION_REVIEW_PART1.md` | Architecture & data layer |
| `IMPLEMENTATION_REVIEW_PART2.md` | Fetchers & integration |
| `IMPLEMENTATION_REVIEW_PART3.md` | Extension guide |
| `architecture.md` | High-level overview |
| `testing-strategy.md` | Testing approach |

---

## 💡 Pro Tips

1. **Always use Result type** for error handling
2. **Use @Inject constructor** for DI
3. **Use Flow for streams** not callbacks
4. **Use StateFlow for state** in ViewModels
5. **Use @Transaction** for atomic operations
6. **Use LazyColumn** for lists
7. **Use remember** for expensive computations
8. **Use collectAsStateWithLifecycle** in Compose
9. **Always write tests** before deploying
10. **Profile before optimizing**

---

**Print this page for quick reference!**

**For detailed information, see the full implementation review documents.**

---

**Last Updated:** 2025-01-16  
**Version:** 1.0
# ROSTRY Implementation - Quick Reference Card
## One-Page Cheat Sheet for Common Tasks

**Version:** 1.0  
**Last Updated:** 2025-01-16  
**Print-Friendly:** Yes

---

## 🏗️ Architecture Layers

```
┌─────────────────────────────────────────┐
│  UI (Compose Screens)                   │  ui/
├─────────────────────────────────────────┤
│  ViewModel (State Management)           │  ui/<feature>/*ViewModel.kt
├─────────────────────────────────────────┤
│  Repository (Business Logic)            │  data/repository/
├─────────────────────────────────────────┤
│  Data Sources (Room, Firebase, API)     │  data/database/, services/
└─────────────────────────────────────────┘
```

---

## 📋 Quick Task Guide

### Task 1: Add New Entity

```kotlin
// 1. Create entity
@Entity(tableName = "my_entities")
data class MyEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)

// 2. Create DAO
@Dao
interface MyDao {
    @Insert suspend fun insert(entity: MyEntity)
    @Query("SELECT * FROM my_entities") 
    fun getAll(): Flow<List<MyEntity>>
}

// 3. Add to AppDatabase
@Database(entities = [..., MyEntity::class], version = 17)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myDao(): MyDao
}

// 4. Create migration if needed
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS my_entities (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)
    }
}
```

### Task 2: Create Repository

```kotlin
interface MyRepository {
    suspend fun get(id: String): Result<MyItem>
    fun getAll(): Flow<Result<List<MyItem>>>
    suspend fun create(item: MyItem): Result<String>
}

@Singleton
class MyRepositoryImpl @Inject constructor(
    private val dao: MyDao,
    private val firestore: FirebaseFirestore
) : BaseRepository(), MyRepository {
    
    override suspend fun get(id: String): Result<MyItem> =
        safeCall {
            dao.getById(id)?.toDomain()
                ?: throw Exception("Not found")
        }
    
    override fun getAll(): Flow<Result<List<MyItem>>> =
        dao.getAll()
            .map { Result.Success(it.map { it.toDomain() }) }
            .catch { emit(Result.Error(it.message ?: "Error")) }
    
    override suspend fun create(item: MyItem): Result<String> =
        safeCall {
            dao.insert(item.toEntity())
            firestore.collection("my_items")
                .document(item.id)
                .set(item)
                .await()
            item.id
        }
}
```

### Task 3: Create ViewModel

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
    
    init { loadItems() }
    
    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getAll()
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Success -> 
                                it.copy(isLoading = false, items = result.data)
                            is Result.Error -> 
                                it.copy(isLoading = false, error = result.message)
                            Result.Loading -> it.copy(isLoading = true)
                        }
                    }
                }
        }
    }
}

data class MyUiState(
    val isLoading: Boolean = false,
    val items: List<MyItem> = emptyList(),
    val error: String? = null
)
```

### Task 4: Create Compose Screen

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(uiState.error!!)
        uiState.items.isEmpty() -> EmptyState()
        else -> ItemList(uiState.items)
    }
}

@Composable
fun ItemList(items: List<MyItem>) {
    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemCard(item)
        }
    }
}
```

### Task 5: Add Navigation Route

```kotlin
// In Routes.kt
object Routes {
    const val MY_SCREEN = "my_screen"
    const val MY_DETAIL = "my_detail/{id}"
}

// In AppNavHost.kt
composable(Routes.MY_SCREEN) {
    MyScreen()
}

composable(Routes.MY_DETAIL) { backStackEntry ->
    val id = backStackEntry.arguments?.getString("id") ?: return@composable
    MyDetailScreen(id)
}
```

### Task 6: Register in Hilt

```kotlin
// In RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMyRepository(
        impl: MyRepositoryImpl
    ): MyRepository
}
```

---

## 🔄 Data Flow Patterns

### Read Flow
```
User taps → ViewModel.loadItems()
  ↓
Repository.getAll()
  ├─ Try local (DAO)
  ├─ Try remote (Firebase)
  └─ Return Result
  ↓
StateFlow updates
  ↓
Compose recomposes
  ↓
UI displays data
```

### Write Flow
```
User submits → ViewModel.createItem(item)
  ↓
Repository.create(item)
  ├─ Validate
  ├─ Save local (DAO)
  ├─ Upload remote (Firebase)
  └─ Return Result
  ↓
StateFlow updates
  ↓
UI shows success/error
```

---

## 🧪 Testing Template

```kotlin
@ExperimentalCoroutinesTest
class MyRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private val dao = mockk<MyDao>()
    private val firestore = mockk<FirebaseFirestore>()
    private val repository = MyRepositoryImpl(dao, firestore)
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @Test
    fun `getAll returns success`() = runTest {
        // Given
        val entities = listOf(MyEntity("1", "Test"))
        coEvery { dao.getAll() } returns flowOf(entities)
        
        // When
        val result = repository.getAll().first()
        
        // Then
        assertTrue(result is Result.Success)
    }
}
```

---

## 🔌 Integration Patterns

### Firebase Firestore
```kotlin
// Read
val doc = firestore.collection("items")
    .document(id)
    .get()
    .await()

// Write
firestore.collection("items")
    .document(id)
    .set(item)
    .await()

// Listen
firestore.collection("items")
    .addSnapshotListener { snapshot, error ->
        // Handle updates
    }
```

### Retrofit API
```kotlin
interface ApiService {
    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): Item
    
    @POST("items")
    suspend fun createItem(@Body item: Item): CreateResponse
}
```

### Room Database
```kotlin
@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<ItemEntity>>
    
    @Insert
    suspend fun insert(item: ItemEntity)
    
    @Transaction
    suspend fun updateWithAudit(item: ItemEntity, audit: AuditEntity) {
        update(item)
        auditDao.insert(audit)
    }
}
```

---

## 📊 Common Queries

### Get Single Item
```kotlin
@Query("SELECT * FROM items WHERE id = :id")
suspend fun getById(id: String): ItemEntity?
```

### Get with Filter
```kotlin
@Query("SELECT * FROM items WHERE category = :category")
fun getByCategory(category: String): Flow<List<ItemEntity>>
```

### Get with Pagination
```kotlin
@Query("SELECT * FROM items ORDER BY created_at DESC")
fun getPaged(): PagingSource<Int, ItemEntity>
```

### Get with Relations
```kotlin
@Transaction
@Query("SELECT * FROM items WHERE id = :id")
fun getWithDetails(id: String): Flow<ItemWithDetails>
```

### Count
```kotlin
@Query("SELECT COUNT(*) FROM items")
suspend fun count(): Int
```

### Delete
```kotlin
@Query("DELETE FROM items WHERE created_at < :timestamp")
suspend fun deleteOlderThan(timestamp: Long)
```

---

## ⚠️ Common Mistakes

| ❌ Avoid | ✅ Do Instead |
|---------|--------------|
| `runBlocking { flow.first() }` | `flow.collect { }` |
| Direct context reference | `@ApplicationContext` |
| Unhandled exceptions | Try-catch with Result |
| Mutable state in Compose | StateFlow in ViewModel |
| Tight coupling | Interface-based DI |
| Blocking UI | Coroutines + Flow |
| No error handling | Result type pattern |
| Hardcoded strings | Constants/Resources |

---

## 🎯 Checklist: Adding a Feature

- [ ] Create entity in `data/database/entity/`
- [ ] Create DAO in `data/database/dao/`
- [ ] Add to AppDatabase
- [ ] Create migration if needed
- [ ] Create repository interface
- [ ] Implement repository
- [ ] Register in Hilt module
- [ ] Create ViewModel
- [ ] Create Compose screen
- [ ] Add navigation route
- [ ] Write unit tests
- [ ] Write UI tests
- [ ] Update documentation

---

## 📁 File Locations

```
com.rio.rostry/
├── data/
│   ├── database/
│   │   ├── entity/MyEntity.kt
│   │   ├── dao/MyDao.kt
│   │   └── AppDatabase.kt
│   └── repository/
│       ├── MyRepository.kt
│       └── MyRepositoryImpl.kt
├── domain/
│   └── model/MyItem.kt
├── ui/
│   └── myfeature/
│       ├── MyScreen.kt
│       └── MyViewModel.kt
└── di/
    └── RepositoryModule.kt
```

---

## 🚀 Common Commands

```bash
# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Check code style
./gradlew ktlint

# Generate Dokka docs
./gradlew :app:dokkaHtml

# Clean build
./gradlew clean build

# Run specific test
./gradlew test --tests "MyTest"
```

---

## 🔐 Security Checklist

- [ ] No hardcoded secrets
- [ ] Use BuildConfig for API keys
- [ ] Validate input on client & server
- [ ] Encrypt sensitive data
- [ ] Use HTTPS only
- [ ] Implement RBAC checks
- [ ] Log security events
- [ ] Review Firebase rules

---

## 📈 Performance Tips

- Use `LazyColumn` for lists
- Implement pagination
- Cache responses
- Use `remember` for expensive ops
- Profile with Layout Inspector
- Monitor with Profiler
- Use incremental sync
- Batch network requests

---

## 🔗 Related Documents

| Document | Purpose |
|----------|---------|
| `IMPLEMENTATION_REVIEW_INDEX.md` | Navigation guide |
| `archive/IMPLEMENTATION_REVIEW_SUMMARY.md` | Executive summary (archived) |
| `IMPLEMENTATION_REVIEW_PART1.md` | Architecture & data layer |
| `IMPLEMENTATION_REVIEW_PART2.md` | Fetchers & integration |
| `IMPLEMENTATION_REVIEW_PART3.md` | Extension guide |
| `architecture.md` | High-level overview |
| `testing-strategy.md` | Testing approach |

---

## 💡 Pro Tips

1. **Always use Result type** for error handling
2. **Use @Inject constructor** for DI
3. **Use Flow for streams** not callbacks
4. **Use StateFlow for state** in ViewModels
5. **Use @Transaction** for atomic operations
6. **Use LazyColumn** for lists
7. **Use remember** for expensive computations
8. **Use collectAsStateWithLifecycle** in Compose
9. **Always write tests** before deploying
10. **Profile before optimizing**

---

**Print this page for quick reference!**

**For detailed information, see the full implementation review documents.**

---

**Last Updated:** 2025-01-16  
**Version:** 1.0
