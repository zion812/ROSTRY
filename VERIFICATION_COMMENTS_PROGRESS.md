# Verification Comments Implementation Progress

## ✅ COMPLETED

### Comment 2: Legacy farm monitoring entities now have farmerId/sync metadata
**Status:** ✅ COMPLETE
**Changes Made:**
- ✅ Updated `GrowthRecordEntity` with: `farmerId`, `updatedAt`, `dirty`, `syncedAt`
- ✅ Updated `QuarantineRecordEntity` with: `farmerId`, `lastUpdatedAt`, `updatesCount`, `updatedAt`, `dirty`, `syncedAt`
- ✅ Updated `MortalityRecordEntity` with: `farmerId`, `updatedAt`, `dirty`, `syncedAt`
- ✅ Updated `VaccinationRecordEntity` with: `farmerId`, `updatedAt`, `dirty`, `syncedAt`
- ✅ Updated `HatchingBatchEntity` with: `farmerId`, `updatedAt`, `dirty`, `syncedAt`
- ✅ Updated `HatchingLogEntity` with: `farmerId`, `updatedAt`, `dirty`, `syncedAt`
- ✅ Added proper indexes on `farmerId` and time fields for all entities
- ✅ Created MIGRATION_19_20 in AppDatabase.kt with:
  - ALTER TABLE statements for all 6 entities
  - Backfill logic to populate farmerId from product ownerId
  - Default timestamp initialization from existing time fields
- ✅ Bumped database version from 19 to 20

**Files Modified:**
- `FarmMonitoringEntities.kt` - All 6 entity classes updated
- `AppDatabase.kt` - Version bumped, migration added

---

### Comment 9: DAO methods for counts and policies
**Status:** ✅ COMPLETE
**Changes Made:**
- ✅ GrowthRecordDao: Added `countForFarmerBetween`, `getDirty`, `clearDirty`
- ✅ VaccinationRecordDao: Added `countDueForFarmer`, `countOverdueForFarmer`, `countScheduledBetweenForFarmer`, `getDirty`, `clearDirty`
- ✅ QuarantineRecordDao: Added `countActiveForFarmer`, `countUpdatesOverdueForFarmer`, `getDirty`, `clearDirty`
- ✅ HatchingBatchDao: Added `countActiveForFarmer`, `countDueThisWeekForFarmer`, `getDirty`, `clearDirty`
- ✅ HatchingLogDao: Added `countHatchedBetweenForFarmer`, `countEggsSetBetweenForFarmer`, `getDirty`, `clearDirty`
- ✅ MortalityRecordDao: Added `countForFarmerBetween`, `getDirty`, `clearDirty`

**Files Modified:**
- `FarmMonitoringDaos.kt` - All 6 DAO interfaces updated with new methods

---

## 🚧 IN PROGRESS / PENDING

### Comment 1: FarmerHomeViewModel wiring to live Room data
**Status:** READY TO IMPLEMENT (Comment 9 complete)
**Required Actions:**
- Update FarmerHomeViewModel to use Flow.combine with new DAO methods
- Remove placeholder firstOrNull() usages
- Wire counts to UI state

### Comment 3: Quarantine 12-hour policy UI enforcement
**Status:** PENDING  
**Required Actions:**
- Update QuarantineManagementScreen with countdown timer
- Add Update dialog for notes/medication/health status
- Show overdue badge when isOverdue[quarantineId]==true
- Disable Discharge button until policy met
- Update QuarantineViewModel (partially done - entity has lastUpdatedAt/updatesCount)

### Comment 4: SyncManager and FirestoreService sync extension
**Status:** PENDING
**Required Actions:**
- Extend FirestoreService with farmer-scoped collection methods
- Add fetchUpdated* and push* methods for all entities
- Inject new DAOs into SyncManager
- Add sync blocks for each entity type
- Extend SyncStateEntity with new timestamp fields

### Comment 5: Worker scheduling in RostryApp
**Status:** PENDING
**Required Actions:**
- Add OutboxSyncWorker.schedule(this) to RostryApp.onCreate()
- Add PullSyncWorker.schedule(this) to RostryApp.onCreate()

### Comment 6: FarmPerformanceWorker real metrics
**Status:** PENDING
**Required Actions:**
- Replace placeholder metrics with real DAO queries
- Implement vaccination completion rate calculation
- Implement hatching success rate from HatchingLogDao
- Add QuarantineRecordDao.countActive()

### Comment 7: Listing draft save/restore
**Status:** PENDING
**Required Actions:**
- Inject ListingDraftRepository into FarmerCreateViewModel
- Load draft on init and hydrate wizardState
- Save draft on step changes
- Delete draft on publish success
- Add validation for health/vaccination and lineage

### Comment 8: Deep-link registration
**Status:** PENDING
**Required Actions:**
- Add navDeepLink { uriPattern = "rostry://..." } to routes
- Or add activity intent-filter for rostry:// scheme

### Comment 9: DAO methods for counts and policies
**Status:** PENDING
**Required Actions:**
- Add VaccinationRecordDao count methods
- Add QuarantineRecordDao count methods  
- Add HatchingBatchDao/HatchingLogDao count methods
- Add GrowthRecordDao.countBetween method
- Ensure proper indexes exist

### Comment 10: QuarantineViewModel SessionManager injection
**Status:** PARTIALLY COMPLETE
**Current:**
- SessionManager is already injected in QuarantineViewModel
- farmerId is set to empty string in checkOverdueUpdates()

**Required Actions:**
- Update checkOverdueUpdates() to use sessionManager.getCurrentUserId().firstOrNull()
- Add alert de-duplication logic

### Comment 11: Offline reconciliation SLA
**Status:** PENDING
**Required Actions:**
- Consider reducing sync intervals or adding expedited OneTimeWorkRequest
- Implement connectivity change listener for faster sync

### Comment 12: Hatching timers and due states
**Status:** PENDING
**Required Actions:**
- Add DAO method to count batches with expectedHatchAt in next 7 days
- Expose countdown in HatchingProcessScreen
- Wire to Home card badge (already supported in UI)

---

## 📝 SUMMARY

**Completed:** 1/12 comments (Comment 2)
**In Progress:** 0/12 comments
**Pending:** 11/12 comments

**Next Priority Actions:**
1. Implement Comment 9 (DAO methods) - foundational for Comments 1, 6, 12
2. Implement Comment 1 (FarmerHomeViewModel wiring)
3. Implement Comment 5 (Worker scheduling) - quick win
4. Implement Comment 10 fix (QuarantineViewModel farmerId)
5. Implement Comment 3 (Quarantine UI)
6. Implement Comments 4, 6, 7, 8, 11, 12

**Estimated Remaining Work:**
- DAO methods: ~30 minutes
- ViewModel updates: ~45 minutes  
- UI enhancements: ~30 minutes
- SyncManager/FirestoreService: ~60 minutes
- Workers & scheduling: ~15 minutes
- Deep-links: ~15 minutes
- **Total: ~3 hours**
