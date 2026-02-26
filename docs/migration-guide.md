# ROSTRY Migration Guide

## Overview

This guide documents breaking changes and migration steps for the production readiness update.

---

## Deprecated Code Removal

### OutgoingMessageWorker → OutboxSyncWorker
**Status**: ✅ Migrated

The `OutgoingMessageWorker` has been removed. All message delivery is now handled by `OutboxSyncWorker`.

**Before**:
```kotlin
// Old: Separate worker for outgoing messages
OutgoingMessageWorker.enqueue(context, messageId)
```

**After**:
```kotlin
// New: OutboxSyncWorker handles all message syncing
// Messages are saved to OutgoingMessageEntity, then synced automatically
outgoingMessageDao.upsert(message)
// OutboxSyncWorker picks up pending messages on schedule
```

### Phone Authentication → Firebase Auth
**Status**: ✅ Migrated

Phone authentication methods have been replaced with stub implementations that return `UnsupportedOperationException`. The new authentication flow uses `AuthRepositoryImplNew` with `FirebaseAuthDataSource`.

**Migration Steps**:
1. Use `MultiProviderAuthScreen` for all authentication flows
2. Phone OTP flow is deprecated — use email/Google sign-in
3. `StartPhoneVerificationUseCase`, `VerifyOtpUseCase`, `ResendOtpUseCase`, `LinkPhoneUseCase` are stubs

### Daily Log Cloud Sync
**Status**: ✅ Removed

Daily log cloud sync has been removed. Logs are now stored locally with on-demand sync via `SyncManager`.

### Deprecated Preference Methods
**Status**: ✅ Removed

All deprecated preference methods have been removed. Use `UserPreferencesRepository` current API.

---

## Database Migrations

### Migration Path: v1 → v91

All migrations are incremental and non-destructive. Key migrations:

| Version | Change |
|---------|--------|
| v85-v86 | Added `transfer_analytics` table |
| v86-v87 | Added `hub_assignments` table |
| v87-v88 | Added KYC verification fields |
| v88-v89 | Added `profitability_metrics` table |
| v89-v90 | Added audit log indices |
| v90-v91 | Added `moderation_blocklist` table |

### Room Auto-Migration Support
Migrations use `Migration(from, to)` pattern. Each migration:
1. Creates new tables if needed
2. Adds columns with defaults
3. Creates indices
4. Seeds initial data where appropriate

---

## Configuration Migration

### Before: Hardcoded Values
```kotlin
private const val MAX_HUB_CAPACITY = 1000
private const val MAX_DELIVERY_RADIUS = 100.0
```

### After: Centralized Configuration
```kotlin
val config = configManager.get()
val capacity = config.thresholds.hubCapacity      // default: 1000
val radius = config.thresholds.deliveryRadiusKm   // default: 50.0
```

All configuration values are now in `AppConfiguration` with type-safe access and Firebase Remote Config support.

---

## New Dependencies

### Added Libraries
| Library | Purpose | Version |
|---------|---------|---------|
| OpenCSV | CSV report generation | 5.x |
| Firebase Remote Config | Dynamic configuration | Latest BOM |

### WorkManager Workers
| Worker | Schedule | Purpose |
|--------|----------|---------|
| `LifecycleUpdateWorker` | Daily (24h) | Bird lifecycle stage updates |
| `AnalyticsAggregationWorker` | Daily (midnight) | Pre-calculate profitability metrics |
| `TransferTimeoutWorker` | Periodic | Cancel timed-out transfers |
| `NotificationFlushWorker` | Periodic | Flush batched notifications |
| `DataCleanupWorker` | Weekly | Database cleanup and optimization |
