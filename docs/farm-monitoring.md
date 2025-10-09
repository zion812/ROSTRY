# Farm Monitoring System

Comprehensive monitoring across growth, vaccination, quarantine, mortality, hatching, breeding, and performance.

## Architecture

- `MonitoringRepositories.kt` aggregates repositories for each module.
- DAOs manage records per module (Growth, Vaccination, Mortality, Quarantine, Hatching, Breeding).
- Workers automate reminders and analytics.

## Modules

- **Vaccination**: `VaccinationRecordDao` and `VaccinationReminderWorker.kt` schedule reminders and detect overdue records.
- **Growth**: `GrowthRecordDao` stores weights; screens visualize curves and expected vs actual.
- **Mortality**: `MortalityRecordDao` records cause, age, and trends for analysis.
- **Quarantine**: `QuarantineRecordDao` tracks initiation, treatment, and recovery.
- **Hatching**: `HatchingBatchDao` and `HatchingLogDao` manage incubation cycles and success rates.
- **Breeding**: pair management, genetics tracking, and performance analytics.

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
