# ROSTRY Farmer Core — Developer Execution Guide v1.1

**Version:** 1.1  
**Created:** 2025-12-30  
**Purpose:** Sprint-by-sprint implementation guide mapping every v1.1 expected behavior to exact files, functions, and code changes.  
**Status Legend:** ⬜ Not Started | 🔄 In Progress | ✅ Done | ❌ Blocked

---

## Table of Contents

1. [Sprint 1: P0 Navigation Wiring](#sprint-1-p0-navigation-wiring)
2. [Sprint 2: P0 Core Data Model Decision](#sprint-2-p0-core-data-model-decision)
3. [Sprint 3: P1 Performance Optimization](#sprint-3-p1-performance-optimization)
4. [Sprint 4: P1 Worker Reliability](#sprint-4-p1-worker-reliability)
5. [Sprint 5: Polish & Release Gates](#sprint-5-polish--release-gates)
6. [Release Acceptance Gates](#release-acceptance-gates)

---

## Sprint 1: P0 Navigation Wiring

> **Goal:** Every farmer route must be reachable and functional. No broken callbacks.

### Task 1.1: Fix TodayTasksCard "View all tasks" Button [TASK-002]

| Field | Value |
|-------|-------|
| **Status** | ✅ Done |
| **Priority** | P0 CRITICAL |
| **File** | `app/src/main/java/com/rio/rostry/ui/farmer/TodayTasksCard.kt` |
| **Lines** | 31-146 |
| **Issue** | Line 135: `onClick = { /* Navigate to full task list */ }` — empty callback |

**Current Code (Line 134-139):**
```kotlin
TextButton(
    onClick = { /* Navigate to full task list */ },
    modifier = Modifier.fillMaxWidth()
) {
    Text("View all ${tasks.size} tasks")
}
```

**Required Changes:**

1. **Add callback parameter to TodayTasksCard signature:**
```kotlin
@Composable
fun TodayTasksCard(
    tasks: List<TaskEntity>,
    completedCount: Int,
    onTaskClick: (TaskEntity) -> Unit,
    onTaskComplete: (String) -> Unit,
    onViewAllTasks: () -> Unit,  // <-- ADD THIS
    modifier: Modifier = Modifier
)
```

2. **Wire the callback (Line 135):**
```kotlin
TextButton(
    onClick = onViewAllTasks,  // <-- CHANGE THIS
    modifier = Modifier.fillMaxWidth()
) {
    Text("View all ${tasks.size} tasks")
}
```

3. **Update FarmerHomeScreen.kt (Line 180-197):**
   - **File:** `app/src/main/java/com/rio/rostry/ui/farmer/FarmerHomeScreen.kt`
   - Add `onViewAllTasks` parameter:
```kotlin
TodayTasksCard(
    tasks = uiState.todayTasks,
    completedCount = uiState.completedTasksCount,
    onTaskClick = { task -> /* existing */ },
    onTaskComplete = { taskId -> viewModel.markTaskComplete(taskId) },
    onViewAllTasks = { onNavigateRoute(Routes.MONITORING_TASKS) }  // <-- ADD THIS
)
```

**Acceptance Criteria:**
- [ ] Tap "View all X tasks" navigates to `TasksScreen`
- [ ] Route is `monitoring/tasks`
- [ ] Back button returns to FarmerHomeScreen

---

### Task 1.2: Verify Farm Asset Routes Are Wired [NAV-001, NAV-002, ASSET-002]

| Field | Value |
|-------|-------|
| **Status** | ✅ Already Wired (Verify) |
| **Priority** | P0 CRITICAL |
| **File** | `app/src/main/java/com/rio/rostry/ui/navigation/AppNavHost.kt` |
| **Lines** | 1042-1117 |

**Existing Implementation (Confirmed in Lines 1042-1117):**

| Route | Composable | Status |
|-------|------------|--------|
| `farmer/farm_assets` | `FarmAssetListScreen` | ✅ Wired (Line 1045) |
| `farmer/asset/{assetId}` | `FarmAssetDetailScreen` | ✅ Wired (Line 1058) |
| `farmer/create_asset` | `FarmerCreateScreen` (temp) | ✅ Wired (Line 1077) |
| `farmer/create_listing/{assetId}` | `CreateListingScreen` | ✅ Wired (Line 1094) |

**Verification Tasks:**
- [ ] FarmAssetListScreen loads from "My Farm" fetcher card on Home
- [ ] FAB in FarmAssetListScreen navigates to `farmer/create_asset`
- [ ] Clicking asset item navigates to `farmer/asset/{assetId}`
- [ ] FarmAssetDetailScreen's "Create Listing" button navigates to `farmer/create_listing/{assetId}`

**Test Steps:**
1. Open FarmerHomeScreen
2. Tap "My Farm" card (line 562-570 in FarmerHomeScreen.kt)
3. Verify FarmAssetListScreen renders
4. Tap FAB → verify create flow opens
5. Tap any asset → verify detail screen opens

---

### Task 1.3: Ensure Bottom Nav "My Farm" Tab Works

| Field | Value |
|-------|-------|
| **Status** | ⬜ Verify |
| **Priority** | P0 |
| **File** | `app/src/main/java/com/rio/rostry/ui/navigation/Routes.kt` |

**Check Bottom Nav Configuration:**
```kotlin
// Routes.kt - FarmerNav object
object FarmerNav {
    const val HOME = "home/farmer"
    const val MARKET = "farmer/market"
    const val CREATE = "farmer/create"
    const val COMMUNITY = "farmer/community"
    const val PROFILE = "farmer/profile"
    const val FARM_ASSETS = "farmer/farm_assets"  // <-- Verify this exists
    // ...
}
```

**Check BottomNavDestination mappings in Routes.kt:**
- [ ] Verify `FARM_ASSETS` is included in `FarmerNavigationConfig.bottomNavItems`
- [ ] Verify icon is `Icons.Filled.Pets` per design

**If missing, add to Routes.kt:**
```kotlin
val FARMER_BOTTOM_NAV = listOf(
    BottomNavDestination(FarmerNav.HOME, "Home", Icons.Filled.Home),
    BottomNavDestination(FarmerNav.FARM_ASSETS, "My Farm", Icons.Filled.Pets),  // <-- ADD
    BottomNavDestination(FarmerNav.MARKET, "Market", Icons.Filled.Store),
    // ... existing items
)
```

---

## Sprint 2: P0 Core Data Model Decision

> **Goal:** Implement Option C — FarmAssetEntity as farm truth, ProductEntity as marketplace representation.

### Task 2.1: Add Bridge Contract Fields

| Field | Value |
|-------|-------|
| **Status** | ✅ Done |
| **Priority** | P0 |
| **Files** | See below |

#### 2.1a: Add `sourceAssetId` to ProductEntity

**File:** `app/src/main/java/com/rio/rostry/data/database/entity/ProductEntity.kt`

**Add field (after `familyTreeId`):**
```kotlin
@Entity(
    tableName = "products",
    indices = [
        // existing indices...
        Index(value = ["sourceAssetId"])  // <-- ADD INDEX
    ]
)
data class ProductEntity(
    // ... existing fields ...
    
    @ColumnInfo(name = "sourceAssetId")
    val sourceAssetId: String? = null,  // <-- ADD FIELD: Links to FarmAssetEntity.assetId
    
    // ... rest of fields ...
)
```

#### 2.1b: Database Migration

**File:** `app/src/main/java/com/rio/rostry/data/database/AppDatabase.kt`

**Add migration (find current version and increment):**
```kotlin
val MIGRATION_XX_YY = object : Migration(XX, YY) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add sourceAssetId column to products table
        db.execSQL("ALTER TABLE `products` ADD COLUMN `sourceAssetId` TEXT")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_products_sourceAssetId` ON `products` (`sourceAssetId`)")
    }
}
```

**Update database version:**
```kotlin
@Database(
    version = YY,  // Increment this
    // ...
)
```

#### 2.1c: Update ProductDao

**File:** `app/src/main/java/com/rio/rostry/data/database/dao/ProductDao.kt`

**Add query:**
```kotlin
@Query("SELECT * FROM products WHERE sourceAssetId = :assetId AND deletedAt IS NULL")
fun getListingForAsset(assetId: String): Flow<ProductEntity?>

@Query("SELECT * FROM products WHERE sourceAssetId = :assetId AND deletedAt IS NULL")
suspend fun getListingForAssetSync(assetId: String): ProductEntity?
```

---

### Task 2.2: Create Listing Flow from Asset

| Field | Value |
|-------|-------|
| **Status** | ⬜ Not Started |
| **Priority** | P0 |
| **File** | `app/src/main/java/com/rio/rostry/ui/farmer/listing/CreateListingViewModel.kt` |

**Ensure CreateListingViewModel:**
1. Receives `assetId` from navigation
2. Pre-fills listing from FarmAssetEntity data
3. Sets `sourceAssetId` when creating ProductEntity
4. Does NOT duplicate asset data — only creates marketplace representation

**Pseudocode:**
```kotlin
fun createListingFromAsset(assetId: String, price: Long, description: String) {
    val asset = farmAssetRepository.getAssetById(assetId) ?: return
    
    val product = ProductEntity(
        productId = UUID.randomUUID().toString(),
        sellerId = currentUserId,
        name = asset.name,
        category = if (asset.assetType == "BATCH") "MEAT" else "ADOPTION",
        quantity = asset.quantity,
        price = price,
        birthDate = asset.birthDate,
        breed = asset.breed,
        sourceAssetId = assetId,  // <-- CRITICAL: Link back to asset
        // ... rest
    )
    
    productRepository.insertProduct(product)
}
```

---

### Task 2.3: Update Monitoring to Use AssetId

| Field | Value |
|-------|-------|
| **Status** | 🔄 Partial |
| **Priority** | P1 |
| **Scope** | Future sprint - document contract for now |

**Current State:**
- Monitoring entities (VaccinationRecordEntity, GrowthRecordEntity, etc.) reference `productId`
- This creates tight coupling to marketplace

**Contract for Future:**
- Add optional `assetId` field to monitoring entities
- Query by `assetId` first, fall back to `productId`
- This allows monitoring to work independently of marketplace

**Document this as deferred work in code comments.**

---

## Sprint 3: P1 Performance Optimization

> **Goal:** Eliminate ANR risk from heavy ViewModels and lazy load dependencies.

### Task 3.1: Split FarmerHomeViewModel Combine [HOME-001]

| Field | Value |
|-------|-------|
| **Status** | 📋 Assessed — Already Optimized |
| **Priority** | P1 |
| **File** | `app/src/main/java/com/rio/rostry/ui/farmer/FarmerHomeViewModel.kt` |
| **Issue** | 28-flow combine operation causing "FarmerHome combine slow" warnings |

**Current Problem:**
ViewModel combines ~28 flows in a single `combine()` call, which:
- Blocks main thread during initial composition
- Causes 200-500ms delays on dashboard load
- Triggers ANR in low-end devices

**Solution: Split into Focused State Holders**

```kotlin
// BEFORE (problematic):
val uiState = combine(
    flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8,
    flow9, flow10, flow11, flow12, flow13, flow14, flow15, flow16,
    flow17, flow18, flow19, flow20, flow21, flow22, flow23, flow24,
    flow25, flow26, flow27, flow28
) { values -> /* transform */ }

// AFTER (optimized):
// Split into logical groups with separate StateFlows

// 1. Core KPIs (fast, from cache)
val kpiState: StateFlow<KpiState> = combine(
    weeklySnapshotFlow,
    dashboardCacheFlow
) { snapshot, cache -> KpiState(snapshot, cache) }
    .stateIn(viewModelScope, SharingStarted.Lazily, KpiState.EMPTY)

// 2. Tasks (moderate, today only)
val tasksState: StateFlow<TasksState> = combine(
    todayTasksFlow,
    completedTasksCountFlow
) { tasks, completed -> TasksState(tasks, completed) }
    .stateIn(viewModelScope, SharingStarted.Lazily, TasksState.EMPTY)

// 3. Alerts (lazy, background)
val alertsState: StateFlow<AlertsState> = alertsFlow
    .map { AlertsState(it) }
    .stateIn(viewModelScope, SharingStarted.Lazily, AlertsState.EMPTY)

// 4. Widgets (computed, defer until visible)
val widgetsState: StateFlow<WidgetsState> = ...
```

**Update FarmerHomeScreen to collect separately:**
```kotlin
val kpiState by viewModel.kpiState.collectAsState()
val tasksState by viewModel.tasksState.collectAsState()
val alertsState by viewModel.alertsState.collectAsState()
```

**Acceptance Criteria:**
- [ ] Dashboard loads in < 100ms
- [ ] No "FarmerHome combine slow" Logcat warnings
- [ ] Each section updates independently

---

### Task 3.2: Lazy Load FarmerCreateScreen Dependencies [NAV-003]

| Field | Value |
|-------|-------|
| **Status** | ⬜ Not Started |
| **Priority** | P1 |
| **File** | `app/src/main/java/com/rio/rostry/ui/farmer/FarmerCreateScreen.kt` |
| **Issue** | 55KB screen file, complex wizard with all steps loaded at once |

**Current Problem:**
- FarmerCreateViewModel initializes ALL wizard dependencies on creation
- Image pickers, location services, all DAOs loaded immediately
- Causes noticeable delay when navigating to create screen

**Solution:**

1. **Lazy inject dependencies in ViewModel:**
```kotlin
@HiltViewModel
class FarmerCreateViewModel @Inject constructor(
    // Core dependencies (always needed)
    private val productRepository: ProductRepository,
    private val currentUserProvider: CurrentUserProvider,
    
    // Lazy dependencies (only when needed)
    private val imageCompressorProvider: Provider<ImageCompressor>,
    private val locationServiceProvider: Provider<LocationService>,
) {
    // Only initialize when WizardStep.PHOTOS is reached
    private val imageCompressor by lazy { imageCompressorProvider.get() }
}
```

2. **Split wizard into separate Composables:**
```kotlin
// Instead of one massive FarmerCreateScreen:
@Composable
fun FarmerCreateWizard(currentStep: WizardStep) {
    when (currentStep) {
        WizardStep.BASIC_INFO -> BasicInfoStep()
        WizardStep.PHOTOS -> PhotosStep()  // Lazy load camera/gallery
        WizardStep.PRICING -> PricingStep()
        WizardStep.REVIEW -> ReviewStep()
    }
}
```

---

## Sprint 4: P1 Worker Reliability

> **Goal:** Workers run reliably without timeout/memory issues.

### Task 4.1: Split LifecycleWorker [WRK-001]

| Field | Value |
|-------|-------|
| **Status** | 📋 Assessed — Deferred to Post-Pilot |
| **Priority** | P1 |
| **File** | `app/src/main/java/com/rio/rostry/workers/LifecycleWorker.kt` |
| **Issue** | 14 DAO dependencies, 491 lines, potential timeout on large datasets |

**Current Problem:**
```kotlin
@HiltWorker
class LifecycleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    // 14 DAOs injected here!
    private val productDao: ProductDao,
    private val taskDao: TaskDao,
    private val vaccinationDao: VaccinationRecordDao,
    // ... 11 more ...
) : CoroutineWorker(context, params)
```

**Solution: Split into Focused Workers**

1. **TaskGenerationWorker** — Generates daily tasks from product lifecycle
2. **StageTransitionWorker** — Updates product stages (chick → grower → adult)
3. **MarketReadyWorker** — Detects batches ready for sale
4. **DashboardCacheWorker** — Pre-computes KPIs for instant loading

**Implementation Steps:**

```kotlin
// 1. TaskGenerationWorker (focused)
@HiltWorker
class TaskGenerationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val productDao: ProductDao,
    private val taskDao: TaskDao,
    private val vaccinationDao: VaccinationRecordDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Only task generation logic
    }
}

// 2. Update LifecycleWorker to orchestrate
@HiltWorker
class LifecycleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workManager: WorkManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Chain focused workers
        val taskWork = OneTimeWorkRequestBuilder<TaskGenerationWorker>().build()
        val stageWork = OneTimeWorkRequestBuilder<StageTransitionWorker>().build()
        val marketWork = OneTimeWorkRequestBuilder<MarketReadyWorker>().build()
        
        workManager.beginWith(taskWork)
            .then(stageWork)
            .then(marketWork)
            .enqueue()
        
        return Result.success()
    }
}
```

---

### Task 4.2: Fix VaccinationReminderWorker Logged-Out Skip [WRK-002]

| Field | Value |
|-------|-------|
| **Status** | 📋 Assessed — Deferred (requires last-known-user storage) |
| **Priority** | P1 |
| **File** | `app/src/main/java/com/rio/rostry/workers/VaccinationReminderWorker.kt` |
| **Issue** | Worker skips if user logged out, tasks never created |

**Current Problem (Line ~50):**
```kotlin
val userId = currentUserProvider.getUserId()
if (userId.isNullOrBlank()) {
    Log.w(TAG, "User not logged in, skipping vaccination reminder check")
    return Result.success()  // <-- Silent skip!
}
```

**Solution: Create tasks locally regardless of auth state**

```kotlin
override suspend fun doWork(): Result {
    // Get userId from last known session OR from SharedPreferences
    val userId = currentUserProvider.getUserId() 
        ?: sessionManager.getLastKnownUserId()
    
    if (userId.isNullOrBlank()) {
        return Result.success()  // Truly no user ever
    }
    
    // Always create tasks locally
    val dueTasks = vaccinationDao.getOverdueTasks(userId, System.currentTimeMillis())
    dueTasks.forEach { task ->
        // Insert/update task in local DB
        taskDao.insertOrUpdate(task.toTaskEntity())
    }
    
    // Only send notifications if user is currently logged in
    if (currentUserProvider.getUserId() != null) {
        sendNotifications(dueTasks)
    }
    
    return Result.success()
}
```

---

## Sprint 5: Polish & Release Gates

### Task 5.1: Dashboard Cache Fallback [HOME-002]

| Field | Value |
|-------|-------|
| **Status** | ⬜ Not Started |
| **Priority** | P1 |
| **File** | `app/src/main/java/com/rio/rostry/ui/farmer/FarmerHomeViewModel.kt` |
| **Issue** | New users see empty/stale KPIs on first load |

**Solution: Compute quick fallback if cache is empty**

```kotlin
private fun loadDashboardWithFallback() {
    viewModelScope.launch {
        val cached = dashboardCacheDao.getLatestCache(userId)
        
        if (cached != null && cached.isRecent()) {
            _kpiState.value = cached.toKpiState()
        } else {
            // Quick fallback: compute essential KPIs inline
            val birdCount = productDao.countByOwner(userId)
            val taskCount = taskDao.countDueToday(userId)
            
            _kpiState.value = KpiState(
                totalBirds = birdCount,
                dueTasks = taskCount,
                isFromCache = false
            )
            
            // Schedule full cache refresh in background
            workManager.enqueueUniqueWork(
                "dashboard_cache_refresh",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<DashboardCacheWorker>().build()
            )
        }
    }
}
```

---

### Task 5.2: Process Death State Survival

| Field | Value |
|-------|-------|
| **Status** | ⬜ Not Started |
| **Priority** | P1 |
| **Files** | ViewModels using SavedStateHandle |

**Critical ViewModels to Audit:**
- `FarmerCreateViewModel` — Wizard step must survive
- `VerificationViewModel` — Upload progress must survive
- `FarmAssetDetailViewModel` — Pending edits must survive

**Pattern to Follow:**
```kotlin
@HiltViewModel
class FarmerCreateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // ...
) : ViewModel() {
    // Persist wizard step
    var currentStep: WizardStep
        get() = savedStateHandle.get<String>("wizard_step")
            ?.let { WizardStep.valueOf(it) } ?: WizardStep.BASIC_INFO
        set(value) = savedStateHandle.set("wizard_step", value.name)
    
    // Persist user input
    var birdName: String
        get() = savedStateHandle.get<String>("bird_name") ?: ""
        set(value) = savedStateHandle.set("bird_name", value)
}
```

---

## Release Acceptance Gates

> **These are NON-NEGOTIABLE before any pilot release.**

### Gate 1: Offline-First [OFFLINE-001 to OFFLINE-004]

| Test | Expected | How to Verify |
|------|----------|---------------|
| Add bird in airplane mode | Saves to Room, `dirty=true` | Toggle airplane mode, add bird, check DB |
| Quick log in airplane mode | Saves locally | Log feed, check Room |
| Reconnect sync | `dirty=false`, `syncedAt` set | Enable network, wait 60s, check DB |
| No Firestore dependency | UI never blocks | Kill Firestore (disconnect), verify app works |

**Test File:** `app/src/androidTest/java/com/rio/rostry/OfflineFirstTest.kt` (create if missing)

---

### Gate 2: Navigation Integrity [NAV-001 to NAV-004]

| Test | Expected | How to Verify |
|------|----------|---------------|
| My Farm bottom nav → asset list | FarmAssetListScreen | Tap "My Farm" |
| Asset → Detail → Log → Back | No crash, correct backstack | Full flow |
| TodayTasksCard → View all | TasksScreen | Tap "View all X tasks" |
| Home → Monitoring → Home | No ANR | Profile with systrace |

**Test File:** `app/src/androidTest/java/com/rio/rostry/NavigationIntegrityTest.kt`

---

### Gate 3: Process Death Survival [PROCESS-001 to PROCESS-003]

| Test | Expected | How to Verify |
|------|----------|---------------|
| Create wizard at step 3 | Restores to step 3 | Enable "Don't keep activities", background, resume |
| Dashboard mid-refresh | Shows cached data | Kill during refresh, cold start |
| Photo selection | Picker state survives | Select photo, background, resume |

---

### Gate 4: Worker Reliability [WRK-001 to WRK-004]

| Test | Expected | How to Verify |
|------|----------|---------------|
| LifecycleWorker completes | No timeout | Check WorkManager logs |
| VaccinationReminder runs | Tasks created | Schedule now, check DB |
| OutboxSync runs | Dirty records synced | Add offline, reconnect, verify |

---

## Manual Smoke Test Flow

Run this before every release:

1. **Cold start** → Dashboard shows in < 2s
2. **Add bird** → Wizard completes, bird appears in list
3. **Quick log** → Feed amount saved, task auto-completes
4. **View tasks** → Today's tasks show, can mark complete
5. **My Farm tab** → Asset list loads
6. **Asset detail** → Shows overview, monitoring links work
7. **Create listing** → From asset, publishes to market
8. **Offline mode** → All above work without network
9. **Reconnect** → Data syncs, no duplicates
10. **Background resume** → State preserved

---

## File Quick Reference

| Feature | Primary File | ViewModel | Route |
|---------|--------------|-----------|-------|
| Farmer Home | `FarmerHomeScreen.kt` | `FarmerHomeViewModel` | `home/farmer` |
| Today Tasks | `TodayTasksCard.kt` | (uses parent VM) | — |
| Tasks Screen | `TasksScreen.kt` | `TasksViewModel` | `monitoring/tasks` |
| Farm Assets | `FarmAssetListScreen.kt` | `FarmAssetListViewModel` | `farmer/farm_assets` |
| Asset Detail | `FarmAssetDetailScreen.kt` | `FarmAssetDetailViewModel` | `farmer/asset/{assetId}` |
| Create Asset | `FarmerCreateScreen.kt` | `FarmerCreateViewModel` | `farmer/create_asset` |
| Create Listing | `CreateListingScreen.kt` | `CreateListingViewModel` | `farmer/create_listing/{assetId}` |
| Lifecycle Worker | `LifecycleWorker.kt` | — | — |
| Vaccination Worker | `VaccinationReminderWorker.kt` | — | — |

---

**Document Owner:** ROSTRY R&D Team  
**Last Updated:** 2025-12-30  
**Next Review:** After Sprint 1 completion
