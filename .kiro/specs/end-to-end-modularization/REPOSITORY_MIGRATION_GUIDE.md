# Repository Migration Guide - Phase 2 Completion

**Date**: 2025-01-XX  
**Status**: IMPLEMENTATION GUIDE  
**Estimated Effort**: 2-3 weeks

## Overview

This guide provides a systematic approach to completing Phase 2 of the modularization effort by migrating 80+ repositories from the app module to data modules and ensuring feature modules use domain interfaces.

## Current State Analysis

### Problem Summary

1. **80+ repositories in app module** - Should be in data modules
2. **Repository interfaces in wrong location** - Currently in `core:database`, should be in `domain:*` modules
3. **Feature modules coupled to data layer** - Directly import data layer classes
4. **Missing domain models** - Features use database entities directly

### Architecture Violations

```kotlin
// ❌ CURRENT (WRONG)
// Interface location
core/database/src/.../ProductRepository.kt

// Implementation location  
app/src/.../ProductRepositoryImpl.kt

// Feature usage
feature/marketplace/ProductDetailsViewModel.kt:
  import com.rio.rostry.data.repository.ProductRepository  // Data layer!
  import com.rio.rostry.data.database.entity.ProductEntity // Entity!
```

```kotlin
// ✅ TARGET (CORRECT)
// Interface location
domain/commerce/src/.../ProductRepository.kt

// Implementation location
data/commerce/src/.../ProductRepositoryImpl.kt

// Feature usage
feature/marketplace/ProductDetailsViewModel.kt:
  import com.rio.rostry.domain.commerce.repository.ProductRepository  // Domain!
  import com.rio.rostry.core.model.Product                           // Model!
```

## Migration Strategy

### Phase 1: Preparation (1 day)

1. **Create migration tracking spreadsheet**
   - List all 80+ repositories
   - Categorize by business domain
   - Identify dependencies
   - Prioritize by feature module usage

2. **Set up domain model structure**
   - Create `core:model` package structure
   - Define base domain models
   - Create mapper interfaces

### Phase 2: Domain Layer Creation (1 week)

#### Step 1: Create Domain Interfaces

For each repository, create a domain interface in the appropriate domain module:

**Template**:
```kotlin
// domain/{area}/src/main/kotlin/com/rio/rostry/domain/{area}/repository/{Name}Repository.kt
package com.rio.rostry.domain.{area}.repository

import com.rio.rostry.core.model.{DomainModel}
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for {description}.
 */
interface {Name}Repository {
    /**
     * {Method description}
     */
    suspend fun {methodName}({params}): Result<{ReturnType}>
    
    fun observe{Something}(): Flow<List<{DomainModel}>>
}
```

**Key Principles**:
- Use domain models (not entities)
- Use `Result<T>` (not `Resource<T>`)
- No Android framework dependencies
- Clear documentation

#### Step 2: Create Domain Models

For each entity used by features, create a domain model:

**Template**:
```kotlin
// core/model/src/main/kotlin/com/rio/rostry/core/model/{Name}.kt
package com.rio.rostry.core.model

/**
 * Domain model for {description}.
 * 
 * This is the public API model used by feature modules.
 * It is independent of database implementation details.
 */
data class {Name}(
    val id: String,
    val {field}: {Type},
    // ... other fields
)
```

**Mapping Strategy**:
- Flatten complex nested structures
- Use domain-appropriate types (not database types)
- Include only fields needed by features
- Add computed properties if helpful

### Phase 3: Data Layer Migration (1 week)

#### Step 1: Move Repository Implementations

For each repository:

1. **Create implementation in data module**:
```bash
# Move from
app/src/main/java/com/rio/rostry/data/repository/{Name}RepositoryImpl.kt

# To
data/{area}/src/main/java/com/rio/rostry/data/{area}/repository/{Name}RepositoryImpl.kt
```

2. **Update package and imports**:
```kotlin
package com.rio.rostry.data.{area}.repository

import com.rio.rostry.domain.{area}.repository.{Name}Repository
import com.rio.rostry.core.model.{DomainModel}
import com.rio.rostry.data.{area}.mapper.to{DomainModel}
import com.rio.rostry.data.{area}.mapper.toEntity
```

3. **Implement domain interface**:
```kotlin
class {Name}RepositoryImpl @Inject constructor(
    private val dao: {Name}Dao,
    // ... other dependencies
) : {Name}Repository {
    
    override suspend fun {method}({params}): Result<{Type}> {
        return try {
            val entity = dao.{daoMethod}({params})
            val domainModel = entity.to{DomainModel}()
            Result.Success(domainModel)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

#### Step 2: Create Mappers

For each entity/model pair:

```kotlin
// data/{area}/src/main/java/com/rio/rostry/data/{area}/mapper/{Name}Mapper.kt
package com.rio.rostry.data.{area}.mapper

import com.rio.rostry.core.model.{DomainModel}
import com.rio.rostry.data.database.entity.{Name}Entity

/**
 * Converts {Name}Entity to domain model.
 */
fun {Name}Entity.to{DomainModel}(): {DomainModel} {
    return {DomainModel}(
        id = this.id,
        field = this.field,
        // ... map all fields
    )
}

/**
 * Converts domain model to {Name}Entity.
 */
fun {DomainModel}.toEntity(): {Name}Entity {
    return {Name}Entity(
        id = this.id,
        field = this.field,
        // ... map all fields
    )
}
```

#### Step 3: Create Hilt Bindings

In each data module:

```kotlin
// data/{area}/src/main/java/com/rio/rostry/data/{area}/di/{Area}DataModule.kt
package com.rio.rostry.data.{area}.di

import com.rio.rostry.data.{area}.repository.{Name}RepositoryImpl
import com.rio.rostry.domain.{area}.repository.{Name}Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class {Area}DataModule {
    
    @Binds
    @Singleton
    abstract fun bind{Name}Repository(
        impl: {Name}RepositoryImpl
    ): {Name}Repository
    
    // ... other bindings
}
```

### Phase 4: Feature Layer Updates (3-4 days)

#### Step 1: Update Feature Module Dependencies

For each feature module with data dependencies:

```kotlin
// feature/{name}/build.gradle.kts

dependencies {
    // ✅ KEEP: Domain dependencies
    implementation(project(":domain:commerce"))
    implementation(project(":domain:account"))
    
    // ❌ REMOVE: Data dependencies
    // implementation(project(":data:commerce"))  // DELETE
    // implementation(project(":data:account"))   // DELETE
    
    // ✅ KEEP: Core dependencies
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
}
```

#### Step 2: Update ViewModels

For each ViewModel:

```kotlin
// Before
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.database.entity.ProductEntity

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,  // Data layer
) : ViewModel() {
    val product: StateFlow<ProductEntity?> = ...       // Entity
}

// After
import com.rio.rostry.domain.commerce.repository.ProductRepository
import com.rio.rostry.core.model.Product

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,  // Domain interface
) : ViewModel() {
    val product: StateFlow<Product?> = ...             // Domain model
}
```

#### Step 3: Update Screens

Update UI code to use domain models:

```kotlin
// Before
@Composable
fun ProductDetailsScreen(product: ProductEntity) {
    Text(product.productName)
    Text(product.sellerId)  // Direct entity field access
}

// After
@Composable
fun ProductDetailsScreen(product: Product) {
    Text(product.name)
    Text(product.seller.id)  // Domain model with better structure
}
```

### Phase 5: Cleanup (2-3 days)

1. **Delete old repository implementations from app module**
2. **Delete old Hilt modules from app/di/**
3. **Move remaining interfaces from core:database to domain modules**
4. **Run architecture tests**
5. **Fix any remaining compilation errors**
6. **Run integration tests**

## Repository Categorization

### Commerce Repositories (data:commerce)

**Priority: HIGH** (heavily used by feature:marketplace)

- ProductRepository ✅ (interface exists in core:database)
- ProductRepositoryImpl
- CartRepository
- WishlistRepository
- AuctionRepository
- OrderRepository ✅ (domain interface exists)
- OrderRepositoryImpl
- DisputeRepository
- DisputeRepositoryImpl
- TransactionRepository
- TransactionRepositoryImpl
- PaymentRepository
- InvoiceRepository
- ReviewRepository
- ProductMarketplaceRepository
- ProductSearchRepository
- SaleCompletionService
- EvidenceOrderRepository
- LogisticsRepository
- OrderManagementRepository
- MarketListingRepository
- MarketListingRepositoryImpl

### Farm Repositories (data:farm)

**Priority: HIGH** (used by feature:farmer-tools)

- InventoryRepository ✅ (already migrated)
- InventoryRepositoryImpl ✅ (already migrated)
- FarmAssetRepository
- FarmAssetRepositoryImpl
- AssetBatchOperationRepositoryImpl
- AssetLifecycleRepositoryImpl
- AssetDocumentationService
- FarmDocumentationService
- TraceabilityRepository
- TrackingRepository
- TransferRepository
- TransferWorkflowRepository
- WatchedLineagesRepository
- PublicBirdRepositoryImpl
- ExpenseRepository

### Monitoring Repositories (data:monitoring)

**Priority: MEDIUM**

Already migrated:
- TaskRepositoryImpl ✅
- FarmOnboardingRepositoryImpl ✅
- FarmerDashboardRepositoryImpl ✅
- FarmAlertRepositoryImpl ✅
- BreedingRepositoryImpl ✅
- AnalyticsRepositoryImpl ✅

Still in app module:
- BiosecurityRepository
- BiosecurityRepositoryImpl
- BreedRepository
- BreedRepositoryImpl
- BreedingPlanRepository
- BreedingPlanRepositoryImpl
- FarmActivityLogRepository
- FarmVerificationRepository
- FarmVerificationRepositoryImpl
- ShowRecordRepository
- EnhancedDailyLogRepositoryImpl
- HatchabilityRepository
- BirdHealthRepository
- FarmEventRepository
- FarmFinancialsRepository
- FarmProfileRepository
- OnboardingChecklistRepository
- EnthusiastBreedingRepository
- ReportGenerationRepository
- ReportRepository
- TaskSchedulingRepositoryImpl
- WeatherRepository
- AlertRepository
- AlertRepositoryImpl

### Account Repositories (data:account)

**Priority: HIGH** (used by multiple features)

Already migrated:
- UserRepositoryImpl ✅

Still in app module:
- UserRepository (interface in core:database)
- CoinRepository
- EnthusiastVerificationRepository
- EnthusiastVerificationRepositoryImpl
- RoleMigrationRepository
- RoleUpgradeRequestRepository
- StorageRepository
- StorageUsageRepository
- VerificationDraftRepository
- VerificationDraftRepositoryImpl
- FeedbackRepository
- FeedbackRepositoryImpl

### Social Repositories (data:social)

**Priority: MEDIUM**

Already migrated:
- SocialRepositories ✅

Still in app module:
- ChatRepository
- CommunityRepository
- LikesRepository
- MediaGalleryRepositoryImpl
- VirtualArenaRepository

### Admin Repositories (data:admin)

**Priority: LOW** (admin features less critical)

Already migrated:
- AdminRepositoryImpl ✅
- ModerationRepositoryImpl ✅

Still in app module:
- AdminRepository (interface)
- AdminProductRepository
- AuditRepository
- AuditRepositoryImpl
- ModerationRepository (interface)
- SystemConfigRepository
- AdminMortalityRepository
- AdminMortalityRepositoryImpl

### Analytics Repositories (data:monitoring or new data:analytics)

**Priority: MEDIUM**

- AnalyticsRepository
- ProfitabilityRepository
- ProfitabilityRepositoryImpl

### Reference Data (consider data:reference or core:database)

**Priority: LOW**

- BreedStandardRepository

## Migration Order

### Week 1: High-Priority Commerce & Farm

1. **Day 1-2**: Commerce repositories
   - ProductRepository + domain interface + model
   - CartRepository + domain interface + model
   - WishlistRepository + domain interface + model
   - OrderRepository (complete migration)

2. **Day 3-4**: Farm repositories
   - FarmAssetRepository + domain interface + model
   - TraceabilityRepository + domain interface + model
   - TransferRepository + domain interface + model

3. **Day 5**: Update feature:marketplace
   - Remove data dependencies
   - Update ViewModels to use domain interfaces
   - Update screens to use domain models
   - Test compilation

### Week 2: Monitoring & Account

1. **Day 1-3**: Monitoring repositories (20+ repositories)
   - Create domain interfaces for all monitoring repos
   - Create domain models
   - Migrate implementations
   - Create mappers

2. **Day 4-5**: Account repositories
   - Complete UserRepository migration
   - Migrate verification repositories
   - Migrate storage repositories
   - Update feature:profile

### Week 3: Social, Admin, Cleanup

1. **Day 1-2**: Social repositories
   - Complete social repository migrations
   - Update feature:social-feed

2. **Day 3**: Admin & Analytics
   - Complete admin repository migrations
   - Complete analytics repository migrations

3. **Day 4-5**: Cleanup & Testing
   - Delete old implementations from app module
   - Delete old Hilt modules
   - Run architecture tests
   - Fix compilation errors
   - Run integration tests
   - Update documentation

## Testing Strategy

### Unit Tests

For each repository implementation:

```kotlin
@Test
fun `repository returns domain model not entity`() {
    // Arrange
    val entity = createTestEntity()
    whenever(dao.getById(any())).thenReturn(entity)
    
    // Act
    val result = repository.getById("test-id")
    
    // Assert
    assertTrue(result is Result.Success)
    val model = (result as Result.Success).data
    assertTrue(model is DomainModel)  // Not Entity!
}
```

### Integration Tests

For each feature module:

```kotlin
@Test
fun `feature module uses domain interface`() {
    // Verify ViewModel constructor uses domain interface
    val viewModel = ProductDetailsViewModel(mockRepository)
    
    // Verify no data layer imports
    assertFalse(viewModel::class.java.name.contains("data.repository"))
}
```

### Architecture Tests

```kotlin
@Test
fun `feature modules do not depend on data modules`() {
    noClasses()
        .that().resideInAPackage("..feature..")
        .should().dependOnClassesThat().resideInAPackage("..data..")
        .check(classes)
}

@Test
fun `app module does not contain repository implementations`() {
    noClasses()
        .that().resideInAPackage("..app..")
        .should().haveSimpleNameEndingWith("RepositoryImpl")
        .check(classes)
}

@Test
fun `all repository implementations are in data modules`() {
    classes()
        .that().haveSimpleNameEndingWith("RepositoryImpl")
        .should().resideInAPackage("..data..")
        .check(classes)
}

@Test
fun `domain interfaces do not depend on entities`() {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAPackage("..database.entity..")
        .check(classes)
}
```

## Common Pitfalls & Solutions

### Pitfall 1: Circular Dependencies

**Problem**: Domain module needs entity type, but entities are in core:database

**Solution**: Use domain models in domain layer, map in data layer

### Pitfall 2: Complex Entity Relationships

**Problem**: Entity has complex relationships (nested entities, joins)

**Solution**: Flatten in domain model, handle complexity in mapper

### Pitfall 3: Breaking Existing Code

**Problem**: Changing repository interface breaks 50+ call sites

**Solution**: 
1. Create new domain interface alongside old interface
2. Migrate call sites incrementally
3. Delete old interface when all migrated

### Pitfall 4: Hilt Binding Conflicts

**Problem**: Multiple bindings for same interface

**Solution**:
1. Use qualifiers if needed
2. Ensure old bindings are deleted
3. Check app module doesn't provide same binding

## Success Criteria

Phase 2 is complete when:

- [ ] All 80+ repositories migrated to data modules
- [ ] All domain interfaces in domain modules (not core:database)
- [ ] All domain models in core:model
- [ ] Feature modules depend only on domain modules
- [ ] No repository implementations in app module
- [ ] Architecture tests passing
- [ ] No compilation errors
- [ ] Integration tests passing
- [ ] App runs successfully

## Estimated Effort Summary

| Phase | Tasks | Effort |
|-------|-------|--------|
| Preparation | Planning, setup | 1 day |
| Domain Layer | 80+ interfaces, 40+ models | 1 week |
| Data Layer | 80+ implementations, mappers, Hilt | 1 week |
| Feature Updates | 4 modules, ViewModels, screens | 3-4 days |
| Cleanup | Delete old code, tests | 2-3 days |
| **TOTAL** | | **2-3 weeks** |

## Next Steps

1. Review this guide with team
2. Create tracking spreadsheet
3. Assign repositories to team members
4. Start with Week 1 high-priority migrations
5. Review progress daily
6. Adjust plan as needed

---

**Document Owner**: Kiro AI Agent  
**Last Updated**: 2025-01-XX  
**Status**: Ready for Implementation
