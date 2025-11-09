
**Version:** 1.0  
**Last Updated:** 2025-01-16  
**Audience:** R&D Teams, Architecture Leads, Feature Developers

---

## 1. Executive Summary

ROSTRY is a sophisticated Android application built on **Clean Architecture** with **MVVM** pattern:

- **Offline-First Approach**: Room database as source of truth
- **Reactive Programming**: StateFlow/Flow for data streams
- **Modular Design**: Feature-based package organization
- **Type Safety**: Kotlin with sealed classes for error handling
- **Testability**: Dependency injection via Hilt
- **Scalability**: 60+ database entities with comprehensive migrations

### Key Statistics

| Metric | Value |
|--------|-------|
| **Database Entities** | 60+ |
| **Repositories** | 29+ |
| **DAOs** | 37+ |
| **Features** | 12+ major domains |
| **Migrations** | 16+ |
| **Workers** | 10+ background jobs |

---

## 2. Architecture Overview

### 2.1 Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                   │
│         Screens, Components, Navigation Flows           │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   ViewModel Layer                       │
│      State Management, Business Logic Orchestration     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  Repository Layer                       │
│    Data Orchestration, Validation, Synchronization     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Room (Local) │  │ Firebase     │  │ Retrofit API │  │
│  │ SQLCipher    │  │ (Cloud)      │  │ (REST)       │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Package Structure

```
com.rio.rostry/
├── data/
│   ├── auth/                    # Authentication data sources
│   ├── base/                    # Base repository classes
│   ├── database/                # Room database setup
│   │   ├── dao/                 # Data Access Objects (37+)
│   │   ├── entity/              # Database entities (60+)
│   │   └── migrations/          # Schema migrations (16+)
│   ├── repository/              # Repository implementations (29+)
│   ├── sync/                    # Synchronization logic
│   └── demo/                    # Demo data generators
├── domain/
│   ├── auth/                    # Authentication models
│   ├── model/                   # Domain models
│   ├── rbac/                    # Role-based access control
│   └── social/                  # Social domain models
├── ui/
│   ├── auth/                    # Authentication screens
│   ├── analytics/               # Analytics dashboards
│   ├── marketplace/             # Marketplace features
│   ├── monitoring/               # Farm monitoring
│   ├── social/                  # Social features
│   ├── transfer/                # Transfer workflows
│   ├── components/              # Reusable components
│   └── navigation/              # Navigation setup
├── di/                          # Dependency injection modules
├── services/                    # Background services
├── notifications/               # Notification system
├── security/                    # Security utilities
└── utils/                       # Utility functions
```

---

## 3. Data Layer Deep Dive

### 3.1 Database Architecture

#### AppDatabase Structure

```kotlin
@Database(
    entities = [
        // User & Auth (5 entities)
        UserEntity::class,
        SessionEntity::class,
        // Marketplace (12 entities)
        ProductEntity::class,
        AuctionEntity::class,
        CartEntity::class,
        // Social (8 entities)
        PostEntity::class,
        CommentEntity::class,
        // Transfer (6 entities)
        TransferEntity::class,
        // Monitoring (10 entities)
        GrowthEntity::class,
        VaccinationEntity::class,
        // ... 20+ more entities
    ],
    version = 16,
    autoMigrations = [
        AutoMigration(from = 15, to = 16)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun transferDao(): TransferDao
    // ... 34+ more DAOs
}
```

#### Key Features

- **Encryption**: SQLCipher with device keystore
- **Type Converters**: Enums, dates, complex types
- **Relationships**: Foreign keys with cascade rules
- **Migrations**: 16 versions with backward compatibility
- **Transactions**: Atomic operations for consistency

### 3.2 Entity Design Patterns

#### Pattern 1: Simple Entity

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
```

#### Pattern 2: Entity with Relations

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["seller_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "seller_id") val sellerId: String,
    val price: Double,
    @ColumnInfo(name = "created_at") val createdAt: Long
)

data class ProductWithSeller(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "seller_id",
        entityColumn = "id"
    )
    val seller: UserEntity
)
```

#### Pattern 3: Entity with Type Converters

```kotlin
@Entity(tableName = "transfers")
data class TransferEntity(
    @PrimaryKey val id: String,
    val fowlId: String,
    val fromUserId: String,
    val toUserId: String,
    @TypeConverters(TransferStatusConverter::class)
    val status: TransferStatus,
    @TypeConverters(LocalDateTimeConverter::class)
    val createdAt: LocalDateTime,
    val metadata: String
)

class TransferStatusConverter {
    @TypeConverter
    fun fromStatus(status: TransferStatus): String = status.name
    
    @TypeConverter
    fun toStatus(value: String): TransferStatus = 
        TransferStatus.valueOf(value)
}
```

### 3.3 DAO Patterns

#### Pattern 1: Basic CRUD

```kotlin
@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)
    
    @Update
    suspend fun update(product: ProductEntity)
    
    @Delete
    suspend fun delete(product: ProductEntity)
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?
}
```

#### Pattern 2: Flow-Based Queries

```kotlin
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE seller_id = :sellerId")
    fun getBySellerFlow(sellerId: String): Flow<List<ProductEntity>>
    
    @Query("""
        SELECT * FROM products 
        WHERE price BETWEEN :minPrice AND :maxPrice
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    fun searchByPriceFlow(
        minPrice: Double,
        maxPrice: Double,
        limit: Int = 50
    ): Flow<List<ProductEntity>>
}
```

#### Pattern 3: Complex Queries with Relations

```kotlin
@Dao
interface TransferDao {
    @Transaction
    @Query("""
        SELECT * FROM transfers 
        WHERE from_user_id = :userId 
        ORDER BY created_at DESC
    """)
    fun getTransfersWithDetails(userId: String): 
        Flow<List<TransferWithDetails>>
    
    @Transaction
    suspend fun updateTransferWithAudit(
        transfer: TransferEntity,
        auditLog: AuditLogEntity
    ) {
        update(transfer)
        auditLogDao.insert(auditLog)
    }
}

data class TransferWithDetails(
    @Embedded val transfer: TransferEntity,
    @Relation(parentColumn = "fowl_id", entityColumn = "id")
    val fowl: FowlEntity,
    @Relation(parentColumn = "from_user_id", entityColumn = "id")
    val fromUser: UserEntity,
    @Relation(parentColumn = "to_user_id", entityColumn = "id")
    val toUser: UserEntity
)
```

#### Pattern 4: Pagination

```kotlin
@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM products 
        WHERE category = :category
        ORDER BY created_at DESC
    """)
    fun getProductsByCategory(
        category: String
    ): PagingSource<Int, ProductEntity>
}

// ViewModel usage
val products: Flow<PagingData<Product>> = Pager(
    config = PagingConfig(pageSize = 20),
    pagingSourceFactory = { 
        productDao.getProductsByCategory("poultry")
    }
).flow
    .map { pagingData -> pagingData.map { it.toDomain() } }
    .cachedIn(viewModelScope)
```

---

## 4. Repository Pattern Implementation

### 4.1 Base Repository Class

```kotlin
abstract class BaseRepository {
    protected suspend inline fun <T> safeCall(
        crossinline block: suspend () -> T
    ): Result<T> = try {
        Result.Success(block())
    } catch (e: FirebaseNetworkException) {
        Result.Success(null as T)
    } catch (e: Exception) {
        Timber.e(e, "Repository error")
        Result.Error(
            message = e.localizedMessage ?: "Unknown error",
            exception = e
        )
    }
}
```

### 4.2 Result Type Pattern

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val message: String,
        exception: Exception? = null
    ) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun <T> Result<T>.getOrNull(): T? = 
    (this as? Result.Success)?.data

fun <T> Result<T>.isSuccess(): Boolean = 
    this is Result.Success
```

### 4.3 Complete Repository Implementation

```kotlin
interface ProductRepository {
    suspend fun getProduct(id: String): Result<Product>
    fun getAllProducts(): Flow<Result<List<Product>>>
    fun getProductsPaged(): Flow<PagingData<Product>>
    fun searchProducts(query: String): Flow<Result<List<Product>>>
    suspend fun createProduct(product: Product): Result<String>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(id: String): Result<Unit>
    suspend fun syncProducts(): Result<Unit>
}

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val retrofit: ProductApiService,
    private val syncStateDao: SyncStateDao
) : BaseRepository(), ProductRepository {
    
    override suspend fun getProduct(id: String): Result<Product> =
        safeCall {
            productDao.getById(id)?.toDomain()
                ?: throw Exception("Product not found")
        }
    
    override fun getAllProducts(): Flow<Result<List<Product>>> =
        productDao.getAllFlow()
            .map { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { error ->
                emit(Result.Error(error.message ?: "Unknown error"))
            }
    
    override suspend fun createProduct(product: Product): Result<String> =
        safeCall {
            if (product.name.isBlank()) {
                throw IllegalArgumentException("Product name required")
            }
            
            val entity = product.toEntity()
            productDao.insert(entity)
            
            firestore.collection("products")
                .document(product.id)
                .set(product)
                .await()
            
            syncStateDao.markSynced("product", product.id)
            product.id
        }
    
    override suspend fun syncProducts(): Result<Unit> =
        safeCall {
            val snapshot = firestore.collection("products")
                .get()
                .await()
            
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)
            }
            
            productDao.insertAll(products.map { it.toEntity() })
            syncStateDao.markSynced("products", "all")
        }
    
    private fun ProductEntity.toDomain(): Product =
        Product(
            id = id,
            name = name,
            description = description,
            price = price,
            sellerId = sellerId,
            createdAt = createdAt
        )
    
    private fun Product.toEntity(): ProductEntity =
        ProductEntity(
            id = id,
            name = name,
            description = description,
            price = price,
            sellerId = sellerId,
            createdAt = createdAt
        )
}
```

---

## 5. Repository Patterns by Domain

### Pattern A: Simple CRUD Repository

Used for: Products, Users, Basic entities

```kotlin
interface SimpleRepository<T> {
    suspend fun get(id: String): Result<T>
    fun getAll(): Flow<Result<List<T>>>
    suspend fun create(item: T): Result<String>
    suspend fun update(item: T): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
```

### Pattern B: Complex Workflow Repository

Used for: Transfers, Orders, Multi-step processes

```kotlin
interface TransferRepository {
    suspend fun initiateTransfer(
        fowlId: String,
        toUserId: String
    ): Result<String>
    
    suspend fun verifyTransfer(
        transferId: String,
        verificationCode: String
    ): Result<Unit>
    
    suspend fun approveTransfer(transferId: String): Result<Unit>
    suspend fun rejectTransfer(
        transferId: String,
        reason: String
    ): Result<Unit>
    
    fun getTransferStatus(id: String): Flow<Result<TransferStatus>>
    fun getTransferHistory(userId: String): Flow<Result<List<Transfer>>>
    fun getAuditLog(transferId: String): Flow<Result<List<AuditEntry>>>
}
```

### Pattern C: Analytics Repository

Used for: Analytics, Reporting, Aggregation

```kotlin
interface AnalyticsRepository {
    fun getDailyMetrics(): Flow<Result<DailyMetrics>>
    fun getWeeklyTrends(): Flow<Result<WeeklyTrends>>
    suspend fun aggregateDailyStats(): Result<Unit>
    suspend fun generateReport(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Report>
    suspend fun exportToCsv(
        data: List<AnalyticsData>
    ): Result<File>
}
```

---

**Continue to PART 2 for Fetcher Architecture, Integration Patterns, and State Management**