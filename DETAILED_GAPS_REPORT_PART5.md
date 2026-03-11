# ROSTRY INVESTIGATION REPORT - PART 5
## Disabled Features & Documentation Gaps

---

## 🟠 HIGH PRIORITY ISSUE #3: DISABLED FARM DASHBOARD FEATURES

**Severity:** HIGH | **Impact:** Missing Features | **Fix Time:** 5-7 days

### Problem
8 critical features are disabled in Farm Dashboard with TODO comments.

### Location
`feature/farm-dashboard/src/main/java/com/rio/rostry/feature/farm/dashboard/FarmDashboardScreen.kt`

### Disabled Features

#### 1. Weather Card - Heat Stress Warning (Line 263)
```kotlin
// Weather Card - Heat Stress Warning (Open-Meteo API)
// Temporarily disabled due to unresolved UI dependencies during modularization.
// TODO(modularization): re-enable once WeatherCard + weather model are stabilized.
```
**Impact:** Farmers miss critical heat stress warnings that could kill birds

#### 2. Market Timing Widget (Line 260)
```kotlin
// Market Timing Widget (disabled - module dependency not available)
```
**Impact:** Farmers don't know optimal selling times

#### 3. Stage Transition Dialog (Line 315)
```kotlin
// Stage Transition Dialog logic - temporarily disabled during modularization
// TODO(modularization): restore once task model is decoupled from app/data.
```
**Impact:** Cannot transition birds between lifecycle stages

#### 4. Flock Value Widget (Line 362)
```kotlin
// Flock Value Widget (Profitability) - temporarily disabled during modularization
// TODO(modularization): re-enable once FlockValueCard UI is stabilized.
```
**Impact:** Farmers can't see their flock's market value

#### 5. Enthusiast Upgrade Banner (Line 365)
```kotlin
// Enthusiast Upgrade Recommendation Banner - temporarily disabled during modularization
// TODO(modularization): re-enable once upgrade flow is stabilized.
```
**Impact:** Lost revenue from role upgrades

#### 6. Onboarding Checklist (Line 77)
```kotlin
// Onboarding checklist is temporarily disabled in this module build.
// val onboardingViewModel: OnboardingChecklistViewModel = hiltViewModel()
// val checklistState by onboardingViewModel.uiState.collectAsState()
```
**Impact:** New users don't get guided onboarding

#### 7. Onboarding Checklist Card (Line 558)
```kotlin
// Onboarding Checklist Card (disabled)
```
**Impact:** No task guidance for new farmers

#### 8. Enthusiast Upgrade Sheet (Line 1046)
```kotlin
// Enthusiast Upgrade Sheet - Request form for role upgrade (disabled)
```
**Impact:** Users can't request role upgrades

### Additional Disabled Features

#### Video Uploads (FREE TIER)
**Location:** Multiple files
```kotlin
// FREE TIER: Video uploads are disabled to save storage quota
val FREE_TIER_MODE = true

if (FREE_TIER_MODE) {
    Text("📸 Photos Only Mode - Video uploads are disabled to optimize storage")
}
```
**Files:**
- `feature/farmer-tools/src/.../FarmerCreateScreen.kt`
- `app/src/main/java/com/rio/rostry/data/repository/StorageRepository.kt`
- `app/src/main/java/com/rio/rostry/utils/images/ImageCompressor.kt`

**Impact:** Users cannot upload videos, limiting product showcasing

#### Daily Logs Push (Split-Brain Architecture)
**Location:** `app/src/main/java/com/rio/rostry/data/sync/FirestoreService.kt`
```kotlin
override suspend fun pushDailyLogs(userId: String, role: UserType, entities: List<DailyLogEntity>): Int {
    Timber.w("pushDailyLogs called but disabled in Split-Brain architecture. Use BatchSummary instead.")
    return 0
}
```
**Impact:** Daily logs not synced to cloud

---

## 🟠 HIGH PRIORITY ISSUE #4: ONBOARDING STUB SCREEN

**Severity:** HIGH | **Impact:** Poor UX | **Fix Time:** 2-3 days

### Problem
OnboardFarmBirdScreen shows stub text instead of proper UI.

### Location
`feature/onboarding/src/main/java/com/rio/rostry/feature/onboarding/ui/OnboardFarmBirdScreen.kt`

### Code
```kotlin
/**
 * Temporary stub to keep the module compiling during modularization.
 * TODO(modularization): restore full onboarding UI and wire dependencies via domain contracts.
 */
@Composable
fun OnboardFarmBirdScreen(
    onNavigateRoute: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    Text("OnboardFarmBirdScreen (stub)")
}
```

### Impact
- New farmers see "stub" text
- Cannot onboard birds properly
- Poor first impression
- Incomplete onboarding flow

---

## 🟠 HIGH PRIORITY ISSUE #5: DOCUMENTATION GAPS

**Severity:** HIGH | **Impact:** Maintainability | **Fix Time:** 5-7 days

### Statistics from `docs/gap-analysis.md`

#### ViewModels
- **Documented:** ~90
- **Actual:** 114
- **Gap:** 24 undocumented (21%)

#### Repositories
- **Documented:** ~47
- **Actual:** 57
- **Gap:** 10 undocumented (18%)

#### Workers
- **Documented:** ~26
- **Actual:** 30
- **Gap:** 4 undocumented (13%)

### Undocumented ViewModels (24)
1. OnboardFarmBirdViewModel
2. OnboardFarmBatchViewModel
3. BatchSplitViewModel
4. BatchHierarchyViewModel
5. FarmMonitoringScreen ViewModel
6. PipelineViewModel
7. StoryCreatorScreen ViewModel
8. SyncConflictScreen ViewModel
9. OnboardingChecklistViewModel
10. AddressManagementViewModel
11. BackupRestoreViewModel
12. StorageQuotaViewModel
13. TransferVerificationViewModel
14. TransferDetailsViewModel
15. TransferCreateViewModel
16. TraceabilityViewModel
17. EvidenceOrderViewModel
18. OrderTrackingViewModel
19. GroupChatViewModel
20. ThreadViewModel
21. NotificationsViewModel
22. VerificationViewModel
23. RoleUpgradeViewModel
24. (and more...)

### Undocumented Repositories (10)
1. BirdHealthRepository
2. FarmFinancialsRepository
3. FarmOnboardingRepository
4. HatchabilityRepository
5. VirtualArenaRepository
6. WeatherRepository
7. SaleCompletionService
8. VerificationDraftRepository
9. StorageUsageRepository
10. AnalyticsAggregationRepository

### Undocumented Workers (4)
1. AuctionCloserWorker
2. AutoBackupWorker
3. DatabaseMaintenanceWorker
4. RoleUpgradeMigrationWorker

### Impact
- New developers struggle to understand code
- Maintenance becomes difficult
- Knowledge loss when developers leave
- Harder to onboard contributors
- API contracts unclear

---

## 🟡 MEDIUM PRIORITY ISSUE #1: PLACEHOLDER UI SECTIONS

**Severity:** MEDIUM | **Impact:** Incomplete Features | **Fix Time:** 3-5 days

### Problem
Multiple screens have placeholder sections instead of functional features.

### Locations

#### Product Details - Reviews Placeholder
**File:** `feature/marketplace/src/.../ProductDetailsScreen.kt` (Line 327)
```kotlin
// Reviews Placeholder
item {
    ReviewsPlaceholderSection()
}
```

#### Product Details - Q&A Placeholder
**File:** Same file (Line 332)
```kotlin
// Q&A Placeholder
item {
    QAPlaceholderSection()
}
```

### Impact
- Users see empty placeholder sections
- Cannot read/write reviews
- Cannot ask questions about products
- Looks unfinished

### Recommendation
1. Implement reviews system with database tables
2. Implement Q&A system
3. OR remove placeholder sections until ready
