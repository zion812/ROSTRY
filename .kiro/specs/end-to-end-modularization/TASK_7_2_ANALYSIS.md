# Task 7.2 Analysis: Move ViewModels from App Module to Feature Modules

## Overview
This analysis identifies ViewModels remaining in the app module after Task 7.1.9 screen migrations and determines which should be moved to feature modules.

## Current State
After Task 7.1.9, many screens were moved to feature modules, but their ViewModels may have been left behind or moved with them. This task focuses on identifying and moving any remaining ViewModels.

## ViewModels Identified in App Module

### Category 1: Monitoring ViewModels (12 ViewModels) - HIGH PRIORITY
**Location**: `app/ui/monitoring/vm/`
**Target**: `feature/monitoring/ui/`
**Status**: Screens already in app module (Task 7.1.5 skipped due to complexity)

1. BreedingManagementViewModel.kt
2. BreedingPerformanceViewModel.kt
3. DailyLogViewModel.kt
4. FarmMonitoringViewModel.kt
5. FarmPerformanceViewModel.kt
6. FCRCalculatorViewModel.kt
7. GrowthViewModel.kt
8. HatchingViewModel.kt
9. MortalityViewModel.kt
10. QuarantineViewModel.kt
11. TasksViewModel.kt
12. VaccinationViewModel.kt

**Additional Monitoring ViewModels** (co-located with screens):
13. BatchHierarchyViewModel.kt
14. BatchSplitViewModel.kt
15. GrowthRecordDetailViewModel.kt
16. MortalityDetailViewModel.kt
17. VaccinationDetailViewModel.kt

**Decision**: MOVE ALL - These belong to the monitoring feature module

### Category 2: Profile ViewModels (3 ViewModels) - HIGH PRIORITY
**Location**: `app/ui/profile/`
**Target**: `feature/profile/ui/`
**Status**: Screens blocked by circular dependencies (Task 7.1.8)

1. ProfileViewModel.kt
2. ProfileEditViewModel.kt
3. StorageQuotaViewModel.kt

**Decision**: MOVE ALL - These belong to the profile feature module

### Category 3: Social ViewModels (3 ViewModels) - HIGH PRIORITY
**Location**: `app/ui/social/`
**Target**: `feature/social-feed/ui/`
**Status**: Screens blocked by circular dependencies (Task 7.1.7)

1. SocialFeedViewModel.kt
2. LeaderboardViewModel.kt
3. SocialProfileViewModel.kt (in social/profile/)

**Decision**: MOVE ALL - These belong to the social-feed feature module

### Category 4: Asset ViewModels (3 ViewModels) - HIGH PRIORITY
**Location**: `app/ui/asset/`
**Target**: `feature/asset-management/ui/`
**Status**: Screens still in app module

1. AssetManagementViewModel.kt (in asset/management/)
2. HealthRecordsViewModel.kt (in asset/health/)
3. DailyLoggingViewModel.kt (in asset/logging/)

**Decision**: MOVE ALL - These belong to the asset-management feature module

### Category 5: General User ViewModels (7 ViewModels) - MEDIUM PRIORITY
**Location**: `app/ui/general/`
**Target**: `feature/general/ui/` or `feature/enthusiast-tools/ui/`
**Status**: Complex structure, needs careful analysis

1. GeneralAnalyticsViewModel.kt (general/analytics/)
2. GeneralCartViewModel.kt (general/cart/)
3. GeneralCreateViewModel.kt (general/create/)
4. DiscoverViewModel.kt (general/discover/)
5. GeneralExploreViewModel.kt (general/explore/)
6. GeneralMarketViewModel.kt (general/market/)
7. GeneralProfileViewModel.kt (general/profile/)
8. WishlistViewModel.kt (general/wishlist/)

**Decision**: MOVE ALL to feature/general/ui/ - These are general user features

### Category 6: Shared Gallery ViewModel (1 ViewModel) - LOW PRIORITY
**Location**: `app/ui/shared/gallery/`
**Target**: Consider keeping in app or moving to core module
**Status**: Shared across multiple features

1. MediaGalleryViewModel.kt

**Decision**: KEEP IN APP - This is shared infrastructure used by multiple features

### Category 7: Standalone ViewModels (7 ViewModels) - MEDIUM PRIORITY
**Location**: Various `app/ui/` subdirectories
**Target**: Appropriate feature modules
**Status**: ViewModels without co-located screens

1. EventsViewModel.kt (ui/events/) → feature/events/ui/
2. ExpertViewModel.kt (ui/expert/) → feature/expert/ui/
3. FeedbackViewModel.kt (ui/feedback/) → feature/feedback/ui/
4. AchievementsViewModel.kt (ui/gamification/) → feature/achievements/ui/
5. InsightsViewModel.kt (ui/insights/) → feature/insights/ui/
6. NotificationsViewModel.kt (ui/notifications/) → feature/notifications/ui/
7. ProductSearchViewModel.kt (ui/search/) → feature/marketplace/ui/search/

**Decision**: MOVE ALL - Each belongs to its respective feature module

### Category 8: App Shell ViewModels (3 ViewModels) - KEEP IN APP
**Location**: App-level infrastructure
**Target**: Keep in app module
**Status**: Core app functionality

1. MainViewModel.kt (ui/main/) - App shell
2. SessionViewModel.kt (ui/session/) - App-level session management
3. StartViewModel.kt (ui/start/) - App startup flow
4. SyncStatusViewModel.kt (ui/components/) - App-level sync status

**Decision**: KEEP IN APP - These are app shell responsibilities

### Category 9: Base Classes (1 ViewModel) - KEEP IN APP
**Location**: `app/ui/base/`
**Target**: Keep in app or move to core module
**Status**: Base class for all ViewModels

1. BaseViewModel.kt

**Decision**: KEEP IN APP - This is shared infrastructure

## Migration Summary

### ViewModels to Move: 38 ViewModels

#### High Priority (21 ViewModels)
- **Monitoring**: 17 ViewModels → feature/monitoring/ui/
- **Profile**: 3 ViewModels → feature/profile/ui/
- **Social**: 3 ViewModels → feature/social-feed/ui/
- **Asset**: 3 ViewModels → feature/asset-management/ui/

#### Medium Priority (14 ViewModels)
- **General**: 8 ViewModels → feature/general/ui/
- **Standalone**: 7 ViewModels → Various feature modules

### ViewModels to Keep in App: 5 ViewModels
- MainViewModel.kt
- SessionViewModel.kt
- StartViewModel.kt
- SyncStatusViewModel.kt
- BaseViewModel.kt
- MediaGalleryViewModel.kt

## Migration Strategy

### Phase 1: High Priority (21 ViewModels)
1. Move monitoring ViewModels (17 files)
2. Move profile ViewModels (3 files)
3. Move social ViewModels (3 files)
4. Move asset ViewModels (3 files)

### Phase 2: Medium Priority (14 ViewModels)
1. Move general ViewModels (8 files)
2. Move standalone ViewModels (7 files)

### Phase 3: Verification
1. Update package declarations
2. Update imports in screens
3. Verify compilation
4. Run architecture tests

## Expected Impact
- **Files to Move**: 38 ViewModels
- **Lines of Code**: ~3,000-4,000 lines
- **App Module Reduction**: Significant reduction in business logic
- **Feature Module Completeness**: Most feature modules will have complete UI + ViewModel implementations

## Risks and Considerations

### Circular Dependencies
- Profile and social ViewModels may have circular dependencies
- May need to refactor dependencies before moving

### Shared Dependencies
- Some ViewModels may depend on repositories still in app module
- May need to move repositories first (Task 11)

### Import Updates
- All screens using these ViewModels will need import updates
- smartRelocate should handle this automatically

## Success Criteria
- [ ] All 38 ViewModels moved to appropriate feature modules
- [ ] Package declarations updated
- [ ] Import references updated
- [ ] Compilation successful
- [ ] Architecture tests pass
- [ ] No circular dependencies introduced
