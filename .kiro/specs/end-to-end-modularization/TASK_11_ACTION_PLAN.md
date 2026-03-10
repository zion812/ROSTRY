# Task 11: Action Plan and Findings

**Date**: 2024
**Status**: PARTIALLY COMPLETE - Critical Issues Identified

## Summary

Task 11 aimed to migrate repositories from app module to data modules and ensure proper dependency injection. Investigation reveals:

1. ✅ **Sub-task 11.1 COMPLETE**: App module has all data module dependencies
2. ⚠️ **Sub-task 11.2 BLOCKED**: Feature modules have deep coupling to data layer
3. ⚠️ **Sub-task 11.3 BLOCKED**: Cannot verify Hilt until repositories are migrated

## Critical Finding: Feature Modules Directly Use Data Layer

### The Problem

Feature modules are not just depending on data modules in build.gradle - they are **directly importing and using data layer classes**:

```kotlin
// feature/marketplace/src/.../ProductDetailsViewModel.kt
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.WishlistRepository
import com.rio.rostry.data.repository.TraceabilityRepository

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,  // Data layer class!
    private val cartRepository: CartRepository,        // Data layer class!
    // ...
) : ViewModel() {
    // Uses ProductEntity directly
    val product: StateFlow<ProductEntity?> = ...
}
```

### Why This Is Wrong

According to the architecture design:
1. Feature modules should depend ONLY on **domain interfaces**
2. Feature modules should use **domain models**, not database entities
3. Data implementations should be injected via Hilt at runtime
4. Feature modules should never import from `com.rio.rostry.data.*`

### Current State

**Affected Feature Modules**:
- `feature:marketplace` - Uses ProductRepository, CartRepository, WishlistRepository, DisputeRepository, AuctionRepository, FarmAssetRepository, TraceabilityRepository
- `feature:analytics` - Uses monitoring repositories directly
- `feature:farmer-tools` - Uses farm, monitoring, commerce repositories directly
- `feature:social-feed` - Uses social repositories directly

**What They Should Use Instead**:
- Domain interfaces from `domain:commerce`, `domain:farm`, `domain:monitoring`, `domain:social`
- Domain models from `core:model`
- Repositories injected as domain interfaces

## Why Simply Removing Dependencies Breaks Compilation

Removing data module dependencies from feature module build.gradle files causes compilation errors because:

1. ViewModels directly reference data layer repository classes
2. Screens and ViewModels use database entities (ProductEntity, AuctionEntity, etc.)
3. No domain interfaces exist for many repositories
4. No domain models exist for many entities

## What Needs to Happen

### Option 1: Complete Domain Layer Migration (RECOMMENDED)

**Effort**: 1-2 weeks
**Impact**: Proper architecture compliance

1. **Create Missing Domain Interfaces**
   - Define repository interfaces in domain modules for all repositories used by features
   - Example: `domain:commerce/repository/ProductRepository.kt`

2. **Create Domain Models**
   - Define domain models in `core:model` for all entities used by features
   - Example: `core:model/Product.kt` (not ProductEntity)

3. **Update Data Layer**
   - Implement domain interfaces in data modules
   - Add mappers to convert between entities and domain models

4. **Update Feature Layer**
   - Change ViewModels to use domain interfaces
   - Change UI to use domain models
   - Remove all imports from `com.rio.rostry.data.*`

5. **Update Hilt Bindings**
   - Bind implementations to domain interfaces in data modules
   - Remove bindings from app module

### Option 2: Temporary Workaround (NOT RECOMMENDED)

**Effort**: 1 day
**Impact**: Technical debt, architecture violation

1. Keep data module dependencies in feature modules
2. Add architecture test exceptions for these modules
3. Document as technical debt
4. Plan proper migration for future sprint

## Recommendation

**DO NOT proceed with Option 2**. The architecture design explicitly requires feature modules to depend only on domain contracts. Taking shortcuts now will:
- Violate the core architectural principle
- Make future refactoring harder
- Create confusion about the "correct" way to structure code
- Undermine the entire modularization effort

**INSTEAD**: 
1. Acknowledge that Task 11 revealed a larger issue
2. Create a new task for "Complete Domain Layer Migration"
3. Prioritize this work before marking Phase 2 complete

## Immediate Actions

### 1. Revert Feature Module Build Changes

The build.gradle changes I made should be reverted because they break compilation:

```bash
git checkout feature/marketplace/build.gradle.kts
git checkout feature/analytics/build.gradle.kts
git checkout feature/farmer-tools/build.gradle.kts
git checkout feature/social-feed/build.gradle.kts
```

### 2. Document Current State

Update tasks.md to reflect:
- Task 11.1: ✅ COMPLETE
- Task 11.2: ⚠️ BLOCKED - Requires domain layer migration
- Task 11.3: ⚠️ BLOCKED - Requires repository migration

### 3. Create New Task

Add to tasks.md:
```markdown
- [ ] 11.4 Complete domain layer migration for feature modules
  - [ ] 11.4.1 Create missing domain repository interfaces
  - [ ] 11.4.2 Create domain models for all entities used by features
  - [ ] 11.4.3 Implement domain interfaces in data modules
  - [ ] 11.4.4 Update feature ViewModels to use domain interfaces
  - [ ] 11.4.5 Remove data layer imports from feature modules
```

## Repository Migration Status

### Already Migrated to Data Modules

From verification report, these repositories are already in data modules:
- `data:account` - UserRepositoryImpl ✅
- `data:commerce` - MarketplaceRepositoryImpl ✅
- `data:farm` - InventoryRepositoryImpl ✅
- `data:monitoring` - TaskRepositoryImpl, FarmOnboardingRepositoryImpl, FarmerDashboardRepositoryImpl, FarmAlertRepositoryImpl, BreedingRepositoryImpl, AnalyticsRepositoryImpl ✅
- `data:social` - SocialRepositories ✅
- `data:admin` - AdminRepositoryImpl, ModerationRepositoryImpl ✅

### Still in App Module (80+ repositories)

These need to be migrated:
- See TASK_11_VERIFICATION.md for complete list
- Includes: ProductRepository, CartRepository, OrderRepository, AuctionRepository, DisputeRepository, FarmAssetRepository, and 70+ more

## Hilt Binding Verification

Cannot be completed until:
1. All repositories are migrated to data modules
2. All Hilt binding modules are moved to data modules
3. Feature modules use domain interfaces

## Conclusion

Task 11 revealed that the modularization is not as complete as initially thought:

1. **Good News**: Infrastructure is in place (domain modules, data modules, Hilt setup)
2. **Bad News**: Feature modules are tightly coupled to data layer
3. **Blocker**: Cannot complete Task 11 without significant refactoring

**Recommendation**: Create a comprehensive "Domain Layer Migration" task and prioritize it before marking Phase 2 complete. This is essential for achieving the architectural goals of the modularization effort.

## Next Steps

1. Revert the build.gradle changes (they break compilation)
2. Update tasks.md with accurate status
3. Create detailed plan for domain layer migration
4. Estimate effort and prioritize
5. Consider this a "discovery" task that revealed the true scope of work remaining
