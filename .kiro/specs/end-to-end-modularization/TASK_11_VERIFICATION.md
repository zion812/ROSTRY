# Task 11: Repository Migration Verification Report

**Date**: 2024
**Task**: Migrate repositories from app module to data modules
**Status**: IN PROGRESS - Issues Found

## Executive Summary

Task 11 aims to complete the migration of repositories from the app module to data modules and ensure proper dependency injection across module boundaries. This verification reveals:

- ✅ **Sub-task 11.1 COMPLETE**: App module already includes all data module dependencies
- ⚠️ **Sub-task 11.2 ISSUES FOUND**: Several feature modules incorrectly depend on data modules
- ⚠️ **Sub-task 11.3 BLOCKED**: Cannot verify Hilt bindings until repository migration is complete
- ⚠️ **CRITICAL**: 80+ repository implementations still remain in app module

## Sub-task 11.1: App Module Dependencies ✅ COMPLETE

### Verification

The app module's `build.gradle.kts` correctly includes all data module dependencies:

```kotlin
// Data modules
implementation(project(":data:account"))
implementation(project(":data:commerce"))
implementation(project(":data:farm"))
implementation(project(":data:monitoring"))
implementation(project(":data:social"))
implementation(project(":data:admin"))
```

**Status**: ✅ COMPLETE - No action needed

## Sub-task 11.2: Feature Module Dependencies ⚠️ ISSUES FOUND

### Architecture Requirement

According to the design document:
- Feature modules should depend ONLY on domain modules (contracts)
- Feature modules should NOT depend on data modules (implementations)
- Data modules are injected at runtime via Hilt

### Current State Analysis

#### ✅ Correct Dependencies (2 modules)

These feature modules correctly depend only on domain modules:

1. **feature:profile**
   ```kotlin
   implementation(project(":domain:account"))
   // No data module dependencies ✅
   ```

2. **feature:monitoring**
   ```kotlin
   implementation(project(":domain:monitoring"))
   // No data module dependencies ✅
   ```

#### ⚠️ Incorrect Dependencies (4 modules)

These feature modules incorrectly depend on BOTH domain AND data modules:

1. **feature:marketplace**
   ```kotlin
   implementation(project(":domain:commerce"))
   implementation(project(":data:commerce"))  // ❌ Should not depend on data
   implementation(project(":data:account"))   // ❌ Should not depend on data
   implementation(project(":data:farm"))      // ❌ Should not depend on data
   ```

2. **feature:analytics**
   ```kotlin
   implementation(project(":domain:monitoring"))
   implementation(project(":data:monitoring")) // ❌ Should not depend on data
   implementation(project(":data:farm"))       // ❌ Should not depend on data
   implementation(project(":data:commerce"))   // ❌ Should not depend on data
   implementation(project(":data:account"))    // ❌ Should not depend on data
   ```

3. **feature:farmer-tools**
   ```kotlin
   implementation(project(":domain:farm"))
   implementation(project(":domain:monitoring"))
   implementation(project(":domain:commerce"))
   implementation(project(":domain:account"))
   implementation(project(":domain:social"))
   implementation(project(":data:farm"))       // ❌ Should not depend on data
   implementation(project(":data:monitoring")) // ❌ Should not depend on data
   implementation(project(":data:commerce"))   // ❌ Should not depend on data
   implementation(project(":data:account"))    // ❌ Should not depend on data
   implementation(project(":data:social"))     // ❌ Should not depend on data
   ```

4. **feature:social-feed**
   ```kotlin
   implementation(project(":domain:social"))
   implementation(project(":data:social"))     // ❌ Should not depend on data
   implementation(project(":data:account"))    // ❌ Should not depend on data
   ```

### Required Actions for Sub-task 11.2

1. Remove data module dependencies from:
   - feature:marketplace
   - feature:analytics
   - feature:farmer-tools
   - feature:social-feed

2. Ensure these modules use domain interfaces injected via Hilt

3. Verify no compilation errors after removing data dependencies

## Sub-task 11.3: Hilt Dependency Injection ⚠️ BLOCKED

### Blocker

Cannot fully verify Hilt bindings until repository implementations are migrated from app module to data modules.

### Current Issues

The app module still contains 80+ repository implementations that should be in data modules:

#### Repository Implementations in App Module

**Core Repositories** (should be in data:account, data:farm, data:commerce):
- `UserRepositoryImpl` - Should be in data:account
- `ProductRepositoryImpl` - Should be in data:commerce
- `InventoryRepositoryImpl` - Should be in data:farm
- `MarketListingRepositoryImpl` - Should be in data:commerce
- `OrderRepositoryImpl` - Should be in data:commerce

**Monitoring Repositories** (should be in data:monitoring):
- `FarmAssetRepositoryImpl`
- `BiosecurityRepositoryImpl`
- `BreedRepositoryImpl`
- `BreedingPlanRepositoryImpl`
- `FarmActivityLogRepositoryImpl`
- `FarmVerificationRepositoryImpl`
- `ShowRecordRepositoryImpl`
- `EnhancedDailyLogRepositoryImpl`
- `HatchabilityRepository`
- `BirdHealthRepository`
- `FarmEventRepository`
- `FarmFinancialsRepository`
- `FarmProfileRepository`
- `OnboardingChecklistRepository`

**Commerce Repositories** (should be in data:commerce):
- `AuctionRepository`
- `CartRepository`
- `DisputeRepositoryImpl`
- `EvidenceOrderRepository`
- `InvoiceRepository`
- `LogisticsRepository`
- `OrderManagementRepository`
- `PaymentRepository`
- `ProductMarketplaceRepository`
- `ProductSearchRepository`
- `ReviewRepository`
- `SaleCompletionService`
- `TransactionRepositoryImpl`
- `WishlistRepository`

**Social Repositories** (should be in data:social):
- `ChatRepository` (interface and impl)
- `CommunityRepository`
- `LikesRepository`
- Files in `app/src/main/java/com/rio/rostry/data/repository/social/`

**Admin Repositories** (should be in data:admin):
- `AdminRepositoryImpl`
- `AdminProductRepository`
- `AuditRepositoryImpl`
- `ModerationRepository`
- `AdminMortalityRepositoryImpl`
- Files in `app/src/main/java/com/rio/rostry/data/repository/admin/`

**Analytics Repositories** (should be in data:monitoring or new data:analytics):
- `AnalyticsRepository`
- `ProfitabilityRepositoryImpl`
- Files in `app/src/main/java/com/rio/rostry/data/repository/analytics/`

**Other Repositories**:
- `AlertRepositoryImpl` - Should be in data:monitoring
- `AssetBatchOperationRepositoryImpl` - Should be in data:farm
- `AssetLifecycleRepositoryImpl` - Should be in data:farm
- `AssetDocumentationService` - Should be in data:farm
- `CoinRepository` - Should be in data:account
- `EnthusiastBreedingRepository` - Should be in data:monitoring
- `EnthusiastVerificationRepositoryImpl` - Should be in data:account
- `ExpenseRepository` - Should be in data:farm
- `FarmDocumentationService` - Should be in data:farm
- `FeedbackRepositoryImpl` - Should be in data:account or new data:feedback
- `MediaGalleryRepositoryImpl` - Should be in data:social
- `PublicBirdRepositoryImpl` - Should be in data:farm
- `ReportGenerationRepository` - Should be in data:monitoring
- `ReportRepository` - Should be in data:monitoring
- `RoleMigrationRepository` - Should be in data:account
- `RoleUpgradeRequestRepository` - Should be in data:account
- `StorageRepository` - Should be in data:account
- `StorageUsageRepository` - Should be in data:account
- `SystemConfigRepository` - Should be in data:admin
- `TaskSchedulingRepositoryImpl` - Should be in data:monitoring
- `TraceabilityRepository` - Should be in data:farm
- `TrackingRepository` - Should be in data:farm
- `TransferRepository` - Should be in data:farm
- `TransferWorkflowRepository` - Should be in data:farm
- `VerificationDraftRepositoryImpl` - Should be in data:account
- `VirtualArenaRepository` - Should be in data:social
- `WatchedLineagesRepository` - Should be in data:farm
- `WeatherRepository` - Should be in data:monitoring

### Hilt Module Issues

The app module contains Hilt binding modules that should be in data modules:

1. **app/src/main/java/com/rio/rostry/di/CoreRepositoryModule.kt**
   - Binds: UserRepository, ProductRepository, AuthRepository, TrackingRepository, FamilyTreeRepository, ChatRepository, TraceabilityRepository, WatchedLineagesRepository, PublicBirdRepository, PedigreeRepository
   - Should be split across data modules

2. **app/src/main/java/com/rio/rostry/di/MonitoringRepositoryModule.kt**
   - Binds: BiosecurityRepository, BreedRepository, CommunityRepository, EnthusiastVerificationRepository, FarmActivityLogRepository, FarmAssetRepository, FarmVerificationRepository, ShowRecordRepository, ProfitabilityRepository, BreedingRepository, DailyLogRepository, FarmAlertRepository, FarmOnboardingRepository, FarmPerformanceRepository, FarmerDashboardRepository, GrowthRepository, HatchingRepository, ListingDraftRepository, MortalityRepository, QuarantineRepository
   - Should be in data:monitoring module

## Recommendations

### Priority 1: Complete Repository Migration (CRITICAL)

**Estimated Effort**: 2-3 days
**Impact**: Blocks Phase 2 completion

1. Create a migration plan for 80+ repositories
2. Group repositories by business domain
3. Migrate repositories to appropriate data modules:
   - data:account (user, auth, verification, storage, roles)
   - data:commerce (marketplace, orders, payments, transactions)
   - data:farm (assets, inventory, tracking, transfers)
   - data:monitoring (tasks, alerts, analytics, breeding, health)
   - data:social (chat, community, media, likes)
   - data:admin (admin, moderation, audit, system config)

4. Move Hilt binding modules to data modules
5. Update imports across the codebase
6. Verify compilation after each migration batch

### Priority 2: Fix Feature Module Dependencies

**Estimated Effort**: 2-4 hours
**Impact**: Architecture violation

1. Remove data module dependencies from:
   - feature:marketplace
   - feature:analytics
   - feature:farmer-tools
   - feature:social-feed

2. Ensure ViewModels use domain interfaces via constructor injection
3. Run architecture tests to verify compliance

### Priority 3: Verify Hilt Bindings

**Estimated Effort**: 1-2 hours
**Impact**: Runtime dependency injection

After repository migration:
1. Verify all domain interfaces have Hilt bindings in data modules
2. Test dependency injection across module boundaries
3. Run integration tests to verify runtime behavior

## Architecture Test Recommendations

Add these architecture tests to prevent regression:

```kotlin
@Test
fun `feature modules should not depend on data modules`() {
    noClasses()
        .that().resideInAPackage("..feature..")
        .should().dependOnClassesThat().resideInAPackage("..data..")
        .because("Feature modules should depend on domain contracts, not implementations")
        .check(classes)
}

@Test
fun `app module should not contain repository implementations`() {
    noClasses()
        .that().resideInAPackage("..app..")
        .should().haveSimpleNameEndingWith("RepositoryImpl")
        .because("Repository implementations should be in data modules")
        .check(classes)
}

@Test
fun `all repository implementations should be in data modules`() {
    classes()
        .that().haveSimpleNameEndingWith("RepositoryImpl")
        .should().resideInAPackage("..data..")
        .because("Repository implementations belong in data layer")
        .check(classes)
}
```

## Conclusion

Task 11 cannot be marked complete until:

1. ✅ Sub-task 11.1: App module dependencies (COMPLETE)
2. ⚠️ Sub-task 11.2: Feature module dependencies (4 modules need fixing)
3. ⚠️ Sub-task 11.3: Hilt bindings verification (blocked by repository migration)

**Critical Blocker**: 80+ repository implementations remain in app module and must be migrated to data modules before Phase 2 can be considered complete.

**Estimated Total Effort**: 3-4 days
- Repository migration: 2-3 days
- Feature module dependency fixes: 2-4 hours
- Hilt binding verification: 1-2 hours
- Testing and validation: 4-6 hours
