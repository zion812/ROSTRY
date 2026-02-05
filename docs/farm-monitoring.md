# Farm Monitoring System

Comprehensive monitoring across growth, vaccination, quarantine, mortality, hatching, breeding, and performance.

## Architecture

- `MonitoringRepositories.kt` aggregates repositories for each module.
- DAOs manage records per module (Growth, Vaccination, Mortality, Quarantine, Hatching, Breeding).
- Workers automate reminders and analytics.

## Monitoring Modules

### 1. Growth Tracking (`ui/monitoring/GrowthTrackingScreen.kt`)
- **Purpose**: Monitor and analyze growth patterns of birds
- **Features**:
  - Weight tracking over time
  - Growth curve visualization
  - Expected vs actual growth comparison
  - Performance metrics and analytics
- **Data Model**: `GrowthRecordEntity` with weight, date, bird identifier
- **Repository**: `GrowthRepositoryImpl.kt`
- **Worker**: `FarmMonitoringWorker.kt` for daily health checks
- **ViewModel**: `GrowthViewModel.kt` and `GrowthRecordDetailViewModel.kt`

### 2. Vaccination Management (`ui/monitoring/VaccinationScheduleScreen.kt`)
- **Purpose**: Schedule and track vaccination records
- **Features**:
  - Vaccination schedule management
  - Reminder notifications
  - Due date tracking
  - Vaccination history
- **Data Model**: `VaccinationRecordEntity` with vaccine type, date, bird identifier
- **Repository**: `VaccinationRepositoryImpl.kt`
- **Worker**: `VaccinationReminderWorker.kt` for automated reminders
- **ViewModel**: `VaccinationViewModel.kt` and `VaccinationDetailViewModel.kt`

### 3. Mortality Tracking (`ui/monitoring/MortalityTrackingScreen.kt`)
- **Purpose**: Record and analyze mortality events
- **Features**:
  - Cause of death recording
  - Age and time tracking
  - Trend analysis
  - Statistical reporting
- **Data Model**: `MortalityRecordEntity` with cause, age, date, bird identifier
- **Repository**: `MortalityRepositoryImpl.kt`
- **Worker**: `FarmMonitoringWorker.kt` for daily health checks
- **ViewModel**: `MortalityViewModel.kt` and `MortalityDetailViewModel.kt`

### 4. Quarantine Management (`ui/monitoring/QuarantineManagementScreen.kt`)
- **Purpose**: Track and manage quarantine periods
- **Features**:
  - Quarantine initiation and tracking
  - Treatment logs
  - Recovery monitoring
  - Status updates
- **Data Model**: `QuarantineRecordEntity` with start/end dates, reason, treatment
- **Repository**: `QuarantineRepositoryImpl.kt`
- **Worker**: `QuarantineReminderWorker.kt` for health monitoring alerts
- **ViewModel**: `QuarantineViewModel.kt`

### 5. Hatching Process (`ui/monitoring/HatchingProcessScreen.kt`)
- **Purpose**: Monitor hatching and incubation cycles
- **Features**:
  - Incubation cycle tracking
  - Success rate monitoring
  - Batch management
  - Environmental condition logging
- **Data Model**: `HatchingBatchEntity` and `HatchingLogEntity`
- **Repository**: `HatchingRepositoryImpl.kt`
- **Worker**: `FarmMonitoringWorker.kt` for daily health checks
- **ViewModel**: `HatchingViewModel.kt`

### 6. Breeding Management (`ui/monitoring/BreedingManagementScreen.kt`)
- **Purpose**: Manage breeding pairs and genetics
- **Features**:
  - Pair management
  - Genetics tracking
  - Performance analytics
  - Breeding schedule
- **Data Model**: `BreedingPairEntity` and related breeding entities
- **Repository**: `BreedingRepositoryImpl.kt`
- **Worker**: `FarmMonitoringWorker.kt` for daily health checks
- **ViewModel**: `BreedingManagementViewModel.kt`

### 7. Farm Performance (`ui/monitoring/FarmPerformanceScreen.kt`)
- **Purpose**: Aggregate farm performance metrics
- **Features**:
  - KPI dashboard
  - Efficiency metrics
  - Comparative analytics
  - Performance trends
- **Data Model**: Aggregated performance data
- **Repository**: `FarmPerformanceRepositoryImpl.kt`
- **Worker**: `FarmPerformanceWorker.kt` for weekly performance reports
- **ViewModel**: `FarmPerformanceViewModel.kt`

### 8. Daily Logging (`ui/monitoring/DailyLogScreen.kt`)
- **Purpose**: Record daily farm activities and observations
- **Features**:
  - Daily activity logs
  - Weight recordings
  - Feed and medication tracking
  - Photo documentation
- **Data Model**: `DailyLogEntity` with comprehensive daily data
- **Repository**: `DailyLogRepositoryImpl.kt`
- **Worker**: `FarmMonitoringWorker.kt` for daily health checks
- **ViewModel**: `DailyLogViewModel.kt`

### 9. Task Management (`ui/monitoring/TasksScreen.kt`)
- **Purpose**: Manage farm-related tasks and reminders
- **Features**:
  - Task scheduling
  - Priority management
  - Recurring tasks
  - Completion tracking
- **Data Model**: `TaskEntity` with task type, priority, due date
- **Repository**: `TaskRepositoryImpl.kt`
- **Worker**: `FarmMonitoringWorker.kt` for daily health checks
- **ViewModel**: `TasksViewModel.kt`

### 10. FCRCalculator (`ui/monitoring/FCRCalculatorScreen.kt`)
- **Purpose**: Calculate Feed Conversion Ratio for performance analysis
- **Features**:
  - FCR calculation
  - Performance metrics
  - Historical comparisons
- **Data Model**: Feed and weight data for FCR calculation
- **Repository**: `FarmPerformanceRepositoryImpl.kt`
- **ViewModel**: `FCRCalculatorViewModel.kt`

### 11. Batch Splitting (`ui/monitoring/BatchSplitViewModel.kt`)
- **Purpose**: Manage splitting of bird batches
- **Features**:
  - Batch division tracking
  - New batch creation
  - Lineage preservation
- **Data Model**: Batch splitting metadata
- **Repository**: `FarmOnboardingRepositoryImpl.kt`
- **ViewModel**: `BatchSplitViewModel.kt`

### 12. Batch Hierarchy (`ui/monitoring/BatchHierarchyViewModel.kt`)
- **Purpose**: Visualize relationships between batches
- **Features**:
  - Hierarchical batch view
  - Relationship tracking
  - Split history
- **Data Model**: Batch relationship data
- **Repository**: `FarmOnboardingRepositoryImpl.kt`
- **ViewModel**: `BatchHierarchyViewModel.kt`

## Performance Analytics

- `FarmPerformanceWorker.kt` aggregates KPIs (mortality rate, growth efficiency, vaccination coverage, hatching success).
- Weekly reports persisted and surfaced in analytics dashboards.

## Lifecycle Integration

- `LifecycleRules.kt` defines milestone rules consumed by `LifecycleWorker.kt` and module workers.

## Automation & Alerts

- Daily checks for due vaccinations and quarantine status changes.
- Notifications for critical health alerts and overdue tasks.

## Data & Validation

- Strict enums and type converters in Room.
- Input validation with meaningful error messages.

## Reporting & Compliance

- Exportable CSV/PDF summaries for audits.
- Historical trend charts in analytics.

## UI

- Compose screens per module with ViewModels and state flows.

---

## Daily Logs

- `DailyLogEntity` records unified per-day observations per bird/batch: weight, feed, medication, symptoms, activity, photos, notes, environment (temp/humidity), author.
- Indexed for `productId`, `farmerId`, `logDate`, `createdAt` to enable fast queries.
- Offline-first with `dirty` and `syncedAt`. Captures `deviceTimestamp` for audit.
- UI: `DailyLogScreen` supports quick chips (weight/feed/activity), inline inputs, history list.
- ViewModel: `DailyLogViewModel` composes active products and recent logs; ensures `dirty` and `updatedAt` set on save.

## Task Management

- `TaskEntity` provides a unified reminder system across modules with fields for `taskType`, `priority`, `dueAt`, `recurrence`, `snoozeUntil`, and JSON `metadata`.
- Indexed for `farmerId`, `taskType`, `dueAt`, `completedAt`.
- DAO provides reactive flows for due/overdue and recent completed tasks.
- UI: `TasksScreen` with tabs (Due, Overdue, Completed) and completion checkbox.
- ViewModel: `TasksViewModel` exposes reactive lists and provides `markComplete`/`delete` helpers.

## Lifecycle Tracking

- `products` table extended with lifecycle fields: `stage` (CHICK, JUVENILE, ADULT, BREEDER), `lifecycleStatus` (ACTIVE, QUARANTINE, DECEASED, TRANSFERRED), `parentMaleId`, `parentFemaleId`, `ageWeeks`, `lastStageTransitionAt`, `breederEligibleAt`, `isBatch`.
- `LifecycleWorker` computes stage/age, persists transitions, creates `STAGE_TRANSITION` events, and generates tasks for milestones (vaccinations, growth updates). Sets breeder eligibility at week 52.

## Background Automation (updated)

- `VaccinationReminderWorker` now reads due vaccination tasks via `TaskRepository` and notifies; marks `notifiedAt` in task metadata.
- `FarmPerformanceWorker` aggregates daily log insights: `avgFeedKg`, `medicationUsageCount`, `dailyLogComplianceRate`, and suggests actions.

## Sync

- `SyncManager` pushes/pulls `daily_logs` and `tasks` with dirty clearing.
- Product pull uses last-write-wins on lifecycle fields and validates lineage via `TraceabilityRepository.verifyParentage` before accepting remote parents.

## Navigation

- New routes: `monitoring/daily_log`, `monitoring/daily_log/{productId}`, `monitoring/tasks`.
- Farmer Home fetcher grid prioritizes Daily Log and Tasks.

## Listing & Verification (preview)

- Listing validation to prevent publishing quarantined/deceased/transferred birds and to enforce traceable adoptions (family tree/parent IDs) is planned.
- Transfer verification screens surface immutable audit logs and trust scores (see transfer module docs).

---

# Sprint 2 Summary

## Onboarding (Farmer & Enthusiast)

- Entry points: FAB on `FarmerHomeScreen` and `EnthusiastHomeScreen` opens `AddToFarmDialog` with two options (Individual, Batch).
- Screens: `OnboardFarmBirdScreen`, `OnboardFarmBatchScreen` (multi-step wizards).
- ViewModels: `OnboardFarmBirdViewModel`, `OnboardFarmBatchViewModel` (wizard state, validation, save flow).
- Routes: `onboard/farm/bird?role={role}`, `onboard/farm/batch?role={role}`. Enthusiasts pass `role=enthusiast`.
- Post-save navigation: `monitoring/daily_log` (stay in farm context).

### Wizard Steps
1. Path Selection: Traceable vs Non-Traceable.
2. Age Group: Chick, Juvenile, Adult, Breeder.
3. Core Details: Name, birth date, gender, weight, height, breed, colors, vaccination records (age-conditional).
4. Lineage (Traceable): Parent selection with future QR scan support.
5. Proofs & Media: Photos required; documents required for traceable.
6. Review & Submit.

### Monitoring Integration
- Creates `ProductEntity` with lifecycle fields (`stage`, `lifecycleStatus`, `isBatch`, `parentMaleId`, `parentFemaleId`).
- Seeds monitoring via `FarmOnboardingRepository.addProductToFarmMonitoring()`:
  - Initial `GrowthRecordEntity` (week 0)
  - Vaccination schedule based on age
  - Initial `DailyLogEntity` (onboarding note)
  - Initial `TaskEntity` reminders: early vaccination (if applicable), weekly growth

### Batch Enhancements
- `ProductEntity` adds `splitAt` and `splitIntoIds` metadata for batch splitting; `LifecycleWorker` surfaces a `BATCH_SPLIT_DUE` alert at ≥12 weeks for active batches.

### Media & Lineage (Upcoming wiring)
- `ParentSelectorDialog` is scaffolded for lineage selection and QR scan hooks.
- `MediaUploadManager` will be used to upload photos/documents and populate `imageUrls`/`documentUrls`.

## Pair Manager Enhancements

- `BreedingManagementViewModel` now validates:
  - Prevent self-pairing.
  - Products must exist and be `ACTIVE`.
  - No duplicate active pair (order-insensitive).
- Error surfaced via `error: StateFlow<String?>` and banner in `BreedingManagementScreen` with `clearError()`.

## Egg Collection (Enthusiast)

- Entity: `EggCollectionEntity` with `dirty` for offline-first syncing.
- DAO: `EggCollectionDao` supports upsert, recent, and totals.
- ViewModel: `EggCollectionViewModel` (offline-first save, validations).
- Screen: `EggCollectionScreen` (pairId, eggs, grade, weight, notes).
- Route: `enthusiast/egg_collection` under `EnthusiastNav`.
- Navigation:
  - Button added on `EnthusiastDashboardScreen` → "Log Egg Collection".
  - Wired via `EnthusiastDashboardTabs` to navigate to `enthusiast/egg_collection`.

## Family Tree Route

- Added explicit route `product/{productId}/family_tree` mapping to `TraceabilityScreen` for full-view family tree.

## Listing Lifecycle Validation

- `FarmerCreateViewModel` submit-time guards:
  - Prevent listing when prefilled product is in `QUARANTINE` (ACTIVE), `DECEASED`, or `TRANSFERRED`.

## Navigation

- New:
  - `Routes.Onboarding.FARM_BIRD`, `Routes.Onboarding.FARM_BATCH` → onboarding screens.
  - `Routes.EnthusiastNav.EGG_COLLECTION` → Egg Collection.
  - `Routes.Product.FAMILY_TREE` → Family Tree (aliases TraceabilityScreen).

## Notes

- Sync coverage for egg collections exists; no new sync changes required.
- Further UX polish recommended: pair picker for egg collection, success snackbars, optional deep links.
