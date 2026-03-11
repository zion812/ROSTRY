# ROSTRY Modularization Analysis & Fetcher Dependencies

**Date**: 2026-03-10  
**Status**: Comprehensive Analysis Complete  
**Purpose**: Document fetcher system dependencies and identify missing repository implementations

---

## Executive Summary

### ✅ Good News: All 4 Entities EXIST in Database Layer

The entities mentioned as "missing" are actually **fully implemented** in the database layer:

1. **BreedingRecordEntity** ✅ - `core/database/entity/TraceabilityEntities.kt`
2. **AuditLogEntity** ✅ - `core/database/entity/TransferWorkflowEntities.kt`
3. **FarmAlertEntity** ✅ - `core/database/entity/NewFarmMonitoringEntities.kt`
4. **EggCollectionEntity** ✅ - `core/database/entity/EnthusiastBreedingEntities.kt`

### ⚠️ Actual Problem: Missing Repository Layer Integration

The build errors are NOT due to missing entities, but due to **incomplete repository layer** that bridges these entities to the domain layer through the fetcher system.

---

## Architecture Overview

### Current Modularization Structure

```
ROSTRY/
├── app/                          # Main application module
├── core/                         # Reusable infrastructure (8 modules)
│   ├── common/
│   ├── database/                 # ✅ Room entities & DAOs (97 entities, 94 DAOs)
│   ├── designsystem/
│   ├── domain/
│   ├── model/
│   ├── navigation/
│   ├── network/
│   └── testing/
├── data/                         # Domain-specific data layer (6 modules)
│   ├── account/
│   ├── admin/
│   ├── commerce/
│   ├── farm/                     # ⚠️ Missing enthusiast breeding repos
│   ├── monitoring/               # ✅ Has FarmAlertRepositoryImpl
│   └── social/
├── domain/                       # Business logic interfaces (9 modules)
│   ├── account/
│   ├── admin/
│   ├── commerce/
│   ├── farm/                     # ⚠️ Missing TraceabilityRepository interface
│   ├── health/
│   ├── monitoring/               # ✅ Has FarmAlertRepository interface
│   ├── recommendation/
│   ├── service/
│   └── social/
└── feature/                      # UI/presentation (20+ modules)
    ├── breeding/
    ├── enthusiast-tools/
    ├── farmer-tools/
    ├── monitoring/
    ├── transfers/
    └── ...
```

---

## Fetcher System Architecture

### Core Components

Located in `docs/fetcher-system.md` (comprehensive documentation exists)

1. **FetcherRegistry** - Central registry for type-safe fetcher registration
2. **FetcherCoordinator** - Orchestrates fetch operations with cache management
3. **RequestCoalescer** - Deduplicates concurrent requests
4. **ContextualLoader** - Context-aware data loading with priority
5. **FetcherHealthCheck** - Performance and availability monitoring

### Integration Pattern

```kotlin
// Standard Repository Pattern with Fetcher
class SomeRepositoryImpl @Inject constructor(
    private val dao: SomeDao,
    private val remoteDataSource: RemoteDataSource,
    private val fetcherCoordinator: FetcherCoordinator,
    private val cacheManager: CacheManager
) : SomeRepository {
    
    override suspend fun getData(id: String): Result<Data> {
        val request = ClientRequest(
            key = "data:$id",
            fetcher = { remoteDataSource.fetchData(id) },
            cachePolicy = CachePolicy.CACHE_FIRST,
            ttl = 5.minutes
        )
        return fetcherCoordinator.fetch(request)
    }
}
```

### Cache Policies

- **CACHE_FIRST** - Check cache, fallback to network
- **NETWORK_ONLY** - Always fetch from network
- **CACHE_ELSE_NETWORK** - Cache or network, no refresh
- **CACHE_AND_NETWORK** - Return cache immediately, refresh in background
- **CACHE_ONLY** - Only use cached data

---

## Entity Status & DAO Implementation

### 1. BreedingRecordEntity ✅

**Location**: `core/database/entity/TraceabilityEntities.kt`

```kotlin
@Entity(
    tableName = "breeding_records",
    foreignKeys = [
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["parentId"]),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["partnerId"]),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["childId"])
    ],
    indices = [Index("parentId"), Index("partnerId"), Index("childId")]
)
data class BreedingRecordEntity(
    @PrimaryKey val recordId: String,
    val parentId: String,
    val partnerId: String,
    val childId: String,
    val success: Boolean,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

**DAO**: `BreedingRecordDao` ✅ (`core/database/dao/TraceabilityDaos.kt`)
- `insert()`, `recordsByChild()`, `recordsByParent()`
- `successfulBreedings()`, `totalBreedings()`
- `recordsByChildren()`, `recordsByParents()`

**Registered in AppDatabase**: ✅ Line 52
**DAO Method**: `abstract fun breedingRecordDao(): BreedingRecordDao` ✅ Line 237

---

### 2. AuditLogEntity ✅

**Location**: `core/database/entity/TransferWorkflowEntities.kt`

```kotlin
@Entity(
    tableName = "audit_logs",
    indices = [Index("refId"), Index("type")]
)
data class AuditLogEntity(
    @PrimaryKey val logId: String,
    val type: String,  // TRANSFER, VERIFICATION, DISPUTE, VALIDATION_FAILURE
    val refId: String,
    val action: String,
    val actorUserId: String?,
    val detailsJson: String?,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun createValidationFailureLog(
            refId: String,
            action: String,
            actorUserId: String?,
            reasons: List<String>
        ): AuditLogEntity
    }
}
```

**DAO**: `AuditLogDao` ✅ (`core/database/dao/TransferWorkflowDaos.kt`)
- `insert()`, `getAll()`, `getByRef()`, `streamByRef()`
- `getByType()`, `getByActor()`

**Registered in AppDatabase**: ✅ Line 58
**DAO Method**: `abstract fun auditLogDao(): AuditLogDao` ✅ Line 239

**Usage**: Already used in 6+ feature modules:
- `feature/transfers/` - TransferDetailsViewModel, TransferVerificationViewModel
- `feature/onboarding/` - VerificationViewModel
- `feature/orders/` - EvidenceOrderViewModel
- `feature/farmer-tools/` - FarmerCreateViewModel
- `feature/enthusiast-tools/` - TransferResponseViewModel

---

### 3. FarmAlertEntity ✅

**Location**: `core/database/entity/NewFarmMonitoringEntities.kt`

```kotlin
@Entity(
    tableName = "farm_alerts",
    indices = [Index("farmerId"), Index("isRead"), Index("createdAt")]
)
data class FarmAlertEntity(
    @PrimaryKey val alertId: String = "",
    val farmerId: String = "",
    val alertType: String = "",  // VACCINATION_DUE, QUARANTINE_UPDATE, MORTALITY_SPIKE, HATCHING_DUE
    val severity: String = "",   // INFO, WARNING, URGENT
    val message: String = "",
    val actionRoute: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
```

**DAO**: `FarmAlertDao` ✅ (`core/database/dao/NewFarmMonitoringDaos.kt`)
- `insert()`, `upsert()`, `observeUnread()`, `countUnread()`
- `markRead()`, `deleteExpired()`, `getByFarmer()`
- `getDirty()`, `clearDirty()`, `deleteReadAlerts()`
- `getByTypeForProduct()` - for deduplication

**Registered in AppDatabase**: ✅ Line 103
**DAO Method**: `abstract fun farmAlertDao(): FarmAlertDao` ✅ Line 355

**Repository**: `FarmAlertRepositoryImpl` ✅ (`data/monitoring/repository/FarmAlertRepositoryImpl.kt`)
- Uses Firebase Firestore (not Room DAO yet)
- Implements `FarmAlertRepository` interface from `domain/monitoring/`

**Usage**: Already used in 4+ feature modules:
- `feature/monitoring/` - QuarantineViewModel
- `feature/farmer-tools/` - FarmerHomeViewModel
- `feature/enthusiast-tools/` - EnthusiastHomeViewModel

---

### 4. EggCollectionEntity ✅

**Location**: `core/database/entity/EnthusiastBreedingEntities.kt`

```kotlin
@Entity(
    tableName = "egg_collections",
    foreignKeys = [
        ForeignKey(entity = BreedingPairEntity::class, parentColumns = ["pairId"], childColumns = ["pairId"])
    ],
    indices = [Index("pairId"), Index("farmerId"), Index("collectedAt")]
)
data class EggCollectionEntity(
    @PrimaryKey val collectionId: String,
    val pairId: String,
    val farmerId: String,
    val eggsCollected: Int,
    val collectedAt: Long,
    val qualityGrade: String,  // A/B/C
    val weight: Double? = null,
    val notes: String? = null,
    
    // Enhanced Egg Log Fields
    val goodCount: Int = 0,
    val damagedCount: Int = 0,
    val brokenCount: Int = 0,
    val trayLayoutJson: String? = null,
    
    // Hatchability Tracking Fields
    val setForHatching: Boolean = false,
    val linkedBatchId: String? = null,
    val setForHatchingAt: Long? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)
```

**DAO**: `EggCollectionDao` ✅ (`core/database/dao/EnthusiastBreedingDaos.kt`)
- `upsert()`, `observeByPair()`, `getTotalEggsByPair()`
- `getCollectionsByPair()`, `getCollectionsDueBetween()`
- `countEggsForFarmerBetween()`, `observeRecentByFarmer()`
- `getById()`, `getDirty()`, `clearDirty()`

**Registered in AppDatabase**: ✅ Line 106
**DAO Method**: `abstract fun eggCollectionDao(): EggCollectionDao` ✅ Line 358

**Usage**: Already used in 3+ feature modules:
- `feature/enthusiast-tools/` - EggCollectionViewModel, BreedingFlowScreen, HatchabilityAnalysisViewModel

---

## Missing Repository Implementations

### Problem Analysis

The entities and DAOs exist, but the **repository layer** that connects them to the domain layer through the fetcher system is incomplete or missing.

### Current Repository Status

#### ✅ Implemented Repositories

**data/monitoring/repository/**
- `FarmAlertRepositoryImpl` - Uses Firebase (should also use Room DAO)
- `BreedingRepositoryImpl` - Uses Firebase for BreedingPair (not BreedingRecord)

**data/farm/repository/**
- `TraceabilityRepositoryImpl` - **STUB ONLY** (returns empty/default values)

#### ⚠️ Missing/Incomplete Repositories

1. **EnthusiastBreedingRepository** - For EggCollectionEntity
   - Should handle egg collection CRUD
   - Should integrate with fetcher system
   - Should use EggCollectionDao + remote data source

2. **TraceabilityRepository** - For BreedingRecordEntity
   - Currently a stub in `data/farm/repository/TraceabilityRepositoryImpl.kt`
   - Needs full implementation with BreedingRecordDao
   - Should handle lineage tracking, ancestry graphs

3. **AuditLogRepository** - For AuditLogEntity
   - Currently accessed directly via DAO in ViewModels (anti-pattern)
   - Should have proper repository abstraction
   - Should integrate with fetcher system for audit trail queries

4. **TransferWorkflowRepository** - Enhanced for AuditLogEntity
   - Exists as `TransferWorkflowRepositoryImpl` in `data/farm/`
   - May need enhancement to properly handle audit logs

---

## Fetcher System Dependencies

### Components That Depend on Fetchers

1. **All Repository Implementations** (57+ repositories)
   - Inject `FetcherCoordinator`
   - Use `ClientRequest` for data fetching
   - Specify cache policies and TTL

2. **ViewModels** (indirect)
   - Access data through repositories
   - Repositories use fetcher system internally

3. **Data Sources**
   - Local: Room DAOs
   - Remote: Firebase, REST APIs
   - Fetcher coordinates between them

### Fetcher Integration Pattern

```kotlin
// Example: Complete Repository with Fetcher
@Singleton
class EggCollectionRepositoryImpl @Inject constructor(
    private val eggCollectionDao: EggCollectionDao,
    private val remoteDataSource: EnthusiastBreedingRemoteDataSource,
    private val fetcherCoordinator: FetcherCoordinator,
    private val cacheManager: CacheManager,
    private val mapper: EggCollectionMapper
) : EggCollectionRepository {
    
    override fun observeByPair(pairId: String): Flow<List<EggCollection>> {
        return eggCollectionDao.observeByPair(pairId)
            .map { entities -> entities.map { mapper.toDomain(it) } }
    }
    
    override suspend fun upsert(collection: EggCollection): Result<Unit> {
        return try {
            val entity = mapper.toEntity(collection)
            eggCollectionDao.upsert(entity)
            
            // Sync to remote via fetcher
            val request = ClientRequest(
                key = "egg_collection:${collection.collectionId}",
                fetcher = { remoteDataSource.syncEggCollection(entity) },
                cachePolicy = CachePolicy.NETWORK_ONLY
            )
            fetcherCoordinator.fetch(request)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getCollectionsByPair(pairId: String): Result<List<EggCollection>> {
        val request = ClientRequest(
            key = "egg_collections:pair:$pairId",
            fetcher = {
                // Try local first
                val local = eggCollectionDao.getCollectionsByPair(pairId)
                if (local.isNotEmpty()) {
                    local.map { mapper.toDomain(it) }
                } else {
                    // Fetch from remote and cache
                    val remote = remoteDataSource.fetchEggCollections(pairId)
                    remote.forEach { eggCollectionDao.upsert(mapper.toEntity(it)) }
                    remote
                }
            },
            cachePolicy = CachePolicy.CACHE_FIRST,
            ttl = 5.minutes
        )
        
        return fetcherCoordinator.fetch(request)
    }
}
```

---

## Recommended Implementation Plan

### Phase 1: Create Missing Repository Interfaces (Domain Layer)

**domain/monitoring/repository/**

```kotlin
// EnthusiastBreedingRepository.kt
interface EnthusiastBreedingRepository {
    fun observeEggCollectionsByPair(pairId: String): Flow<List<EggCollection>>
    suspend fun upsertEggCollection(collection: EggCollection): Result<Unit>
    suspend fun getEggCollectionsByPair(pairId: String): Result<List<EggCollection>>
    suspend fun getTotalEggsByPair(pairId: String): Result<Int>
    suspend fun getEggCollectionsDueBetween(start: Long, end: Long): Result<List<EggCollection>>
}
```

**domain/farm/repository/**

```kotlin
// AuditLogRepository.kt
interface AuditLogRepository {
    suspend fun insert(log: AuditLog): Result<Unit>
    suspend fun getByRef(refId: String): Result<List<AuditLog>>
    fun streamByRef(refId: String): Flow<List<AuditLog>>
    suspend fun getByType(type: String, limit: Int): Result<List<AuditLog>>
    suspend fun getByActor(userId: String, limit: Int): Result<List<AuditLog>>
}
```

### Phase 2: Implement Repositories (Data Layer)

**data/monitoring/repository/**

Create `EnthusiastBreedingRepositoryImpl.kt`:
- Inject `EggCollectionDao`, `FetcherCoordinator`, `CacheManager`
- Implement all interface methods
- Use fetcher system for remote sync
- Map entities to domain models

**data/farm/repository/**

1. Complete `TraceabilityRepositoryImpl.kt`:
   - Replace stub implementation
   - Inject `BreedingRecordDao`, `FetcherCoordinator`
   - Implement lineage tracking logic

2. Create `AuditLogRepositoryImpl.kt`:
   - Inject `AuditLogDao`, `FetcherCoordinator`
   - Implement audit trail queries
   - Support filtering by type, actor, ref

### Phase 3: Create Mappers

**data/monitoring/mapper/**

```kotlin
// EggCollectionMapper.kt
class EggCollectionMapper @Inject constructor() {
    fun toDomain(entity: EggCollectionEntity): EggCollection {
        return EggCollection(
            collectionId = entity.collectionId,
            pairId = entity.pairId,
            farmerId = entity.farmerId,
            eggsCollected = entity.eggsCollected,
            collectedAt = entity.collectedAt,
            qualityGrade = entity.qualityGrade,
            // ... map all fields
        )
    }
    
    fun toEntity(domain: EggCollection): EggCollectionEntity {
        return EggCollectionEntity(
            collectionId = domain.collectionId,
            pairId = domain.pairId,
            farmerId = domain.farmerId,
            // ... map all fields
        )
    }
}
```

### Phase 4: Register with Hilt

**data/monitoring/di/MonitoringDataModule.kt**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringDataModule {
    
    @Binds
    @Singleton
    abstract fun bindEnthusiastBreedingRepository(
        impl: EnthusiastBreedingRepositoryImpl
    ): EnthusiastBreedingRepository
}
```

**data/farm/di/FarmDataModule.kt**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class FarmDataModule {
    
    @Binds
    @Singleton
    abstract fun bindAuditLogRepository(
        impl: AuditLogRepositoryImpl
    ): AuditLogRepository
    
    // TraceabilityRepository already bound, just needs implementation
}
```

### Phase 5: Update ViewModels

Replace direct DAO access with repository calls:

**Before**:
```kotlin
class SomeViewModel @Inject constructor(
    private val auditLogDao: AuditLogDao
) {
    fun loadAuditLogs(refId: String) {
        viewModelScope.launch {
            val logs = auditLogDao.getByRef(refId)
            // ...
        }
    }
}
```

**After**:
```kotlin
class SomeViewModel @Inject constructor(
    private val auditLogRepository: AuditLogRepository
) {
    fun loadAuditLogs(refId: String) {
        viewModelScope.launch {
            when (val result = auditLogRepository.getByRef(refId)) {
                is Result.Success -> {
                    val logs = result.data
                    // ...
                }
                is Result.Error -> {
                    // Handle error
                }
            }
        }
    }
}
```

---

## Key Architectural Patterns

### 1. Repository Pattern

```
ViewModel → Repository Interface (domain/) → Repository Impl (data/) → DAO + Fetcher → Database/Network
```

### 2. Offline-First with Fetcher

```
Request → FetcherCoordinator → CacheManager (check cache)
                              ↓
                         Cache Hit? → Return cached data
                              ↓
                         Cache Miss → Fetch from network
                              ↓
                         Update cache → Return fresh data
```

### 3. Dependency Injection (Hilt)

```
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: RepositoryImpl): Repository
}
```

---

## Summary

### What Exists ✅

1. All 4 entities fully implemented in `core/database/entity/`
2. All 4 DAOs fully implemented in `core/database/dao/`
3. All entities registered in AppDatabase
4. Fetcher system fully documented and operational
5. Some repositories partially implemented

### What's Missing ⚠️

1. **EnthusiastBreedingRepository** - For EggCollectionEntity
2. **AuditLogRepository** - For AuditLogEntity (currently accessed via DAO directly)
3. **TraceabilityRepository** - Full implementation (currently stub)
4. **Mappers** - Entity ↔ Domain model conversion
5. **Hilt bindings** - For new repositories

### Next Steps

1. Create missing repository interfaces in `domain/` modules
2. Implement repositories in `data/` modules with fetcher integration
3. Create mappers for entity-domain conversion
4. Register repositories with Hilt
5. Update ViewModels to use repositories instead of DAOs
6. Test end-to-end data flow

---

## References

- **Fetcher System**: `docs/fetcher-system.md`
- **Data Layer Architecture**: `docs/data-layer-architecture.md`
- **Codebase Structure**: `docs/CODEBASE_STRUCTURE.md`
- **System Blueprint**: `docs/SYSTEM_BLUEPRINT.md`
