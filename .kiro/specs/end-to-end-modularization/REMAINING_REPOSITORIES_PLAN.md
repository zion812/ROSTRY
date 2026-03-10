# Remaining Repositories Migration Plan

**Date**: 2026-03-09  
**Status**: READY FOR IMPLEMENTATION  
**Estimated Effort**: 3-4 hours

## Overview

9 repositories remain to be migrated from the app module to data modules. This document provides a detailed plan for each migration.

## Repositories to Migrate

### High Priority - Commerce Domain (3 repositories)

#### 1. DisputeRepositoryImpl → data:commerce
**Complexity**: High (Firebase + Notifications + Audit Logging)
**Estimated Time**: 45 minutes

**Steps**:
1. Create domain interface: `domain/commerce/src/.../DisputeRepository.kt`
2. Create domain model: `core/model/src/.../Dispute.kt`
3. Create mapper: `data/commerce/src/.../mapper/DisputeMapper.kt`
4. Move implementation: `data/commerce/src/.../repository/DisputeRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in CommerceDataModule

**Special Considerations**:
- Firebase Firestore integration
- Notification service integration
- Audit logging
- Complex dispute resolution workflow

#### 2. MarketListingRepositoryImpl → data:commerce
**Complexity**: High (Firebase + Asset Lifecycle)
**Estimated Time**: 45 minutes

**Steps**:
1. Create domain interface: `domain/commerce/src/.../MarketListingRepository.kt`
2. Create domain model: `core/model/src/.../MarketListing.kt`
3. Create mapper: `data/commerce/src/.../mapper/MarketListingMapper.kt`
4. Move implementation: `data/commerce/src/.../repository/MarketListingRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in CommerceDataModule

**Special Considerations**:
- Firebase Firestore integration
- Asset lifecycle management (LISTED, SOLD states)
- Inventory item creation
- Partial listing support (asset splitting)
- Deprecated method handling

#### 3. TransactionRepositoryImpl → data:commerce
**Complexity**: Medium
**Estimated Time**: 30 minutes

**Steps**:
1. Create domain interface: `domain/commerce/src/.../TransactionRepository.kt`
2. Create domain model: `core/model/src/.../Transaction.kt`
3. Create mapper: `data/commerce/src/.../mapper/TransactionMapper.kt`
4. Move implementation: `data/commerce/src/.../repository/TransactionRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in CommerceDataModule

### Medium Priority - Farm Domain (2 repositories)

#### 4. AssetBatchOperationRepositoryImpl → data:farm
**Complexity**: Low
**Estimated Time**: 20 minutes

**Steps**:
1. Create domain interface: `domain/farm/src/.../AssetBatchOperationRepository.kt`
2. Create domain model (if needed): `core/model/src/.../AssetBatchOperation.kt`
3. Create mapper: `data/farm/src/.../mapper/AssetBatchOperationMapper.kt`
4. Move implementation: `data/farm/src/.../repository/AssetBatchOperationRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in FarmDataModule

#### 5. AssetLifecycleRepositoryImpl → data:farm
**Complexity**: Low
**Estimated Time**: 20 minutes

**Steps**:
1. Create domain interface: `domain/farm/src/.../AssetLifecycleRepository.kt`
2. Create domain model (if needed): `core/model/src/.../AssetLifecycleEvent.kt`
3. Create mapper: `data/farm/src/.../mapper/AssetLifecycleMapper.kt`
4. Move implementation: `data/farm/src/.../repository/AssetLifecycleRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in FarmDataModule

### Medium Priority - Monitoring Domain (2 repositories)

#### 6. AlertRepositoryImpl → data:monitoring
**Complexity**: Medium
**Estimated Time**: 30 minutes

**Steps**:
1. Create domain interface: `domain/monitoring/src/.../AlertRepository.kt`
2. Create domain model: `core/model/src/.../Alert.kt`
3. Create mapper: `data/monitoring/src/.../mapper/AlertMapper.kt`
4. Move implementation: `data/monitoring/src/.../repository/AlertRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in MonitoringDataModule

#### 7. TaskSchedulingRepositoryImpl → data:monitoring
**Complexity**: Low
**Estimated Time**: 20 minutes

**Steps**:
1. Create domain interface: `domain/monitoring/src/.../TaskSchedulingRepository.kt`
2. Create domain model (if needed): `core/model/src/.../TaskScheduling.kt`
3. Create mapper: `data/monitoring/src/.../mapper/TaskSchedulingMapper.kt`
4. Move implementation: `data/monitoring/src/.../repository/TaskSchedulingRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in MonitoringDataModule

### Low Priority - Other (2 repositories)

#### 8. MediaGalleryRepositoryImpl → data:social
**Complexity**: Medium (Firebase Storage)
**Estimated Time**: 30 minutes

**Steps**:
1. Create domain interface: `domain/social/src/.../MediaGalleryRepository.kt`
2. Create domain model: `core/model/src/.../MediaItem.kt`
3. Create mapper: `data/social/src/.../mapper/MediaGalleryMapper.kt`
4. Move implementation: `data/social/src/.../repository/MediaGalleryRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in SocialDataModule

**Special Considerations**:
- Firebase Storage integration
- Media tagging
- Gallery management

#### 9. ProfitabilityRepositoryImpl → data:monitoring
**Complexity**: Medium (Analytics)
**Estimated Time**: 30 minutes

**Steps**:
1. Create domain interface: `domain/monitoring/src/.../ProfitabilityRepository.kt`
2. Create domain model: `core/model/src/.../ProfitabilityReport.kt`
3. Create mapper: `data/monitoring/src/.../mapper/ProfitabilityMapper.kt`
4. Move implementation: `data/monitoring/src/.../repository/ProfitabilityRepositoryImpl.kt`
5. Update to use `Result<T>` instead of `Resource<T>`
6. Add Hilt binding in MonitoringDataModule

**Special Considerations**:
- Complex analytics calculations
- Multiple DAO dependencies

## Migration Checklist Template

For each repository:

- [ ] Read existing implementation from app module
- [ ] Create domain interface in appropriate domain module
- [ ] Create domain models in core:model
- [ ] Create mapper in data module
- [ ] Create implementation in data module
  - [ ] Update package declaration
  - [ ] Change `Resource<T>` to `Result<T>`
  - [ ] Ensure `@Singleton` annotation
  - [ ] Verify all dependencies are injectable
- [ ] Add Hilt binding in DataModule
- [ ] Verify compilation
- [ ] Delete old implementation from app module
- [ ] Test Hilt injection works

## Common Patterns

### Domain Interface Template
```kotlin
package com.rio.rostry.domain.{area}.repository

import com.rio.rostry.core.model.{Model}
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

interface {Name}Repository {
    suspend fun {method}({params}): Result<{Type}>
    fun observe{Something}(): Flow<Result<List<{Model}>>>
}
```

### Domain Model Template
```kotlin
package com.rio.rostry.core.model

data class {Name}(
    val id: String,
    val {field}: {Type},
    // ... other fields
)
```

### Mapper Template
```kotlin
package com.rio.rostry.data.{area}.mapper

import com.rio.rostry.core.model.{Model}
import com.rio.rostry.data.database.entity.{Name}Entity

fun {Name}Entity.to{Model}(): {Model} {
    return {Model}(
        id = this.id,
        field = this.field,
        // ... map all fields
    )
}

fun {Model}.toEntity(): {Name}Entity {
    return {Name}Entity(
        id = this.id,
        field = this.field,
        // ... map all fields
    )
}
```

### Implementation Template
```kotlin
package com.rio.rostry.data.{area}.repository

import com.rio.rostry.domain.{area}.repository.{Name}Repository
import com.rio.rostry.core.model.{Model}
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.{area}.mapper.to{Model}
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class {Name}RepositoryImpl @Inject constructor(
    private val dao: {Name}Dao,
    // ... other dependencies
) : {Name}Repository {
    
    override suspend fun {method}({params}): Result<{Type}> {
        return try {
            val entity = dao.{daoMethod}({params})
            val model = entity.to{Model}()
            Result.Success(model)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

### Hilt Binding Template
```kotlin
@Binds
@Singleton
abstract fun bind{Name}Repository(
    impl: {Name}RepositoryImpl
): {Name}Repository
```

## Execution Order

**Recommended order** (by priority and dependencies):

1. TransactionRepositoryImpl (simple, no complex dependencies)
2. AlertRepositoryImpl (needed by monitoring features)
3. TaskSchedulingRepositoryImpl (simple)
4. AssetBatchOperationRepositoryImpl (simple)
5. AssetLifecycleRepositoryImpl (simple)
6. ProfitabilityRepositoryImpl (analytics)
7. MediaGalleryRepositoryImpl (Firebase Storage)
8. MarketListingRepositoryImpl (complex, asset lifecycle)
9. DisputeRepositoryImpl (complex, notifications)

## Post-Migration Verification

After all migrations:

1. **Compile Check**:
   ```bash
   ./gradlew :data:commerce:compileDebugKotlin
   ./gradlew :data:farm:compileDebugKotlin
   ./gradlew :data:monitoring:compileDebugKotlin
   ./gradlew :data:social:compileDebugKotlin
   ```

2. **Verify Hilt Bindings**:
   ```bash
   ./gradlew :app:kaptDebugKotlin
   ```

3. **Architecture Tests**:
   ```bash
   ./gradlew :app:testDebugUnitTest --tests "*ModularArchitectureTest"
   ```

4. **Verify No Repositories in App Module**:
   ```bash
   Get-ChildItem -Path "app/src/main/java/com/rio/rostry/data/repository" -Filter "*RepositoryImpl.kt" -Recurse
   # Should return empty or only non-migrated files
   ```

## Success Criteria

- [ ] All 9 repositories migrated to data modules
- [ ] All domain interfaces created
- [ ] All domain models created
- [ ] All mappers created
- [ ] All Hilt bindings added
- [ ] Zero compilation errors
- [ ] Architecture tests passing
- [ ] App module contains zero repository implementations

## Estimated Timeline

- **High Priority (3 repos)**: 2 hours
- **Medium Priority (4 repos)**: 1.5 hours
- **Low Priority (2 repos)**: 1 hour
- **Verification & Testing**: 30 minutes
- **Total**: 4-5 hours

---

**Status**: READY FOR IMPLEMENTATION
**Next Action**: Begin with TransactionRepositoryImpl (simplest)
**Final Goal**: 100% repository migration (60/60 repositories)

