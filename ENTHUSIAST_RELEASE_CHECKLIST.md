# Enthusiast Release Checklist

Use this checklist before shipping any Enthusiast features.

- [ ] Firestore rules updated for `enthusiasts/{userId}` subtree
  - Files: `firebase/firestore.rules`
  - Deploy with: `firebase deploy --only firestore:rules`
- [ ] Firestore indexes for enthusiast collections deployed
  - Files: `firebase/firestore_enthusiast_indexes.json`
  - Deploy with: `firebase deploy --only firestore:indexes`
- [ ] Room migrations up to v25 applied and tested
  - Files: `AppDatabase.kt` (version = 25), `DatabaseModule.kt` (registered `MIGRATION_24_25`)
  - Verify migration path from last prod version
- [ ] Enthusiast-specific sync windows used
  - File: `SyncManager.kt`
  - Fields: `lastEnthusiastBreedingSyncAt`, `lastEnthusiastDashboardSyncAt`
  - Verify deltas use `updatedAt`
- [ ] Dashboard snapshots write `updatedAt`
  - File: `FirestoreService.kt` (`pushEnthusiastSnapshots` sets `updatedAt`)
- [ ] Hatching DAO optimizations in place
  - File: `FarmMonitoringDaos.kt` (`observeActiveBatchesForFarmer`, `observeHatchingDue`)
  - Repos updated to use SQL flows
- [ ] Analytics wiring for key interactions
  - File: `EnthusiastHomeViewModel.kt` (refresh, egg collect)
  - Optional: breeding flow steps
- [ ] UI enhancements verified
  - File: `EnthusiastDashboardTabs.kt` (Family Tree controls, charts, filters)
- [ ] Background workers healthy
  - `EnthusiastPerformanceWorker` creates snapshots with `dirty`; ensure `updatedAt` logic
- [ ] Manual QA scenarios
  - Create pair, log mating, collect eggs, start incubation, log hatch
  - Traceability deep link from Family Tree tools
  - Transfer verification happy-path and dispute
  - Offline create -> online sync

Notes:
- Time source: use `System.currentTimeMillis()` consistently; consider server timestamps on backend.
- Avoid destructive migrations in production builds.
