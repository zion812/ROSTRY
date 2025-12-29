---
Version: 2.0
Last Updated: 2025-12-29
Audience: Developers, QA
Status: Active
---

# Troubleshooting Guide

Actionable fixes for common issues. For environment setup see `../QUICK_START.md` and `developer-onboarding.md`.

---

## Build & Gradle

- Problem: Sync fails or build errors
  - Cause: Plugin/version mismatch, stale caches, wrong JDK
  - Solution:
    1. Ensure JDK 17 (`java -version`)
    2. Gradle clean and refresh deps:
       ```bash
       ./gradlew clean --refresh-dependencies
       ```
    3. Android Studio → File → Invalidate Caches / Restart
  - Prevention: Pin versions via `gradle/libs.versions.toml`

Flowchart:
```mermaid
flowchart TD
    A[Sync fails / Build error] --> B{JDK 17?}
    B -- No --> B1[Install/Point to JDK 17]
    B -- Yes --> C[./gradlew clean --refresh-dependencies]
    C --> D{Android Studio caches cleared?}
    D -- No --> D1[Invalidate Caches / Restart]
    D -- Yes --> E[Check versions in libs.versions.toml]
    E --> F[Re-sync]
```

- Problem: KSP/Room "Cannot figure out how to save this field"
  - Cause: Missing `@TypeConverters` import on `@Database`
  - Solution: Import the converters class in the database file and rebuild

## Firebase

- Problem: `File google-services.json is missing`
  - Solution: Place file at `app/google-services.json` and sync Gradle
  - Also check Firebase project ID matches

Flowchart:
```mermaid
flowchart TD
    A[Firebase error] --> B{google-services.json present?}
    B -- No --> C[Place file at app/google-services.json]
    B -- Yes --> D{Project ID matches?}
    D -- No --> E[Update google-services.json]
    D -- Yes --> F[Check Firebase console settings]
```

## Database (Room/SQLCipher)

- Problem: Migration crashes on launch
  - Solution: Add/verify migration specs; run migration tests; log schema

Flowchart:
```mermaid
flowchart TD
    A[Crash on launch after schema change] --> B[Increment DB version]
    B --> C[Create Migration object]
    C --> D[Add to Room.databaseBuilder(..., addMigrations)]
    D --> E[Write MigrationTest with helper]
    E --> F[Run tests and retry]
```

- Problem: Encryption errors
  - Solution: Verify SQLCipher factory and passphrase; keep ProGuard rules for SQLCipher

## Network

- Problem: Timeouts/403/SSL
  - Solution: Check base URL (https), interceptors, auth headers; certificate pinning only in release; try mock source

## WorkManager

- Problem: Jobs not running
  - Solution: Validate constraints, battery optimizations, unique names; inspect `adb shell dumpsys activity service WorkManager`

Cross-links: `background-jobs.md`, `architecture.md`

## UI/Compose

- Problem: Excess recompositions or jank
  - Solution: Use `Lazy*` lists with keys, hoist state, avoid heavy work in composables, leverage `remember`

## Auth/Session

- Problem: Token or demo mode issues
  - Solution: Verify auth mode in `SessionManager`; re-login; check Firebase Auth settings and test numbers

## Testing

- Problem: Emulator flakiness
  - Solution: Cold boot or switch images; use CI images; prefer in-memory DB for tests

## Release/ProGuard

- Problem: Crash only in release
  - Solution: Upload mapping, review keep rules for Hilt/Room/Compose/SQLCipher; reproduce with minify enabled

Flowchart:
```mermaid
flowchart TD
    A[Release-only crash] --> B[Enable minify locally]
    B --> C[Reproduce and capture stacktrace]
    C --> D[Upload mapping file]
    D --> E[Review proguard rules: Hilt/Room/Compose/SQLCipher]
    E --> F[Iterate rules and retest]
```

## Marketplace Issues
- **Problem**: Products not appearing
  - **Cause**: Visibility filter (`isPublic=false`), Sync delay
  - **Solution**: Check `isPublic` flag in DB; Verify sync status (green check)
- **Problem**: Filtering broken
  - **Cause**: ViewModel logic mismatch
  - **Solution**: Verify `applyFilters` in `GeneralMarketViewModel`

## Navigation Issues
- **Problem**: Callback not firing or crash on nav
  - **Cause**: Lambda not wired in `AppNavHost`
  - **Solution**: Ensure `composable` passes the callback (e.g., `onMessageClick`) correctly to the screen. Check `NAVIGATION_INTEGRATION_GUIDE.md`.

## Feature Specific Issues

### Digital Farm
- **Problem**: Canvas jank during weather effects
  - **Cause**: Heavy drawing operations in `drawRainEffect()`
  - **Solution**: Enable `shouldRenderElement` culling; Check `isHardwareAccelerated`
- **Problem**: Birds overlapping unnaturally
  - **Cause**: Y-sorting logic failed or same seed used for positions
  - **Solution**: Verify `groupProductsByLifecycle` Y-sorting; Check `randomPositionInZone` seeds

### Evidence Orders
- **Problem**: OTP verification fails consistently
  - **Cause**: Clock drift between devices or expired secret
  - **Solution**: Resend OTP; Check `EvidenceOrderRepository.verifyDeliveryOtp`
- **Problem**: Evidence upload fails
  - **Cause**: Missing Firestore composite index for `order_evidence`
  - **Solution**: Deploy indexes via Firebase CLI; Check `MediaUploadWorker` logs

### Community Hub
- **Problem**: Recommendations are empty
  - **Cause**: `CommunityEngagementWorker` hasn't run or interests not set
  - **Solution**: Manually trigger worker via WorkManager debug; Check `UserInterestEntity` in DB
- **Problem**: Messaging threads missing context
  - **Cause**: Legacy threads without `ThreadMetadataEntity`
  - **Solution**: Run `Migration15_16_Backfill`; Check `ThreadViewModel` initialization

---

## Debug Tools
- Logcat filters, Database Inspector, Network Inspector
- Timing and traces via Firebase Performance

## Still Stuck?
- Open an issue with:
  - Steps to reproduce
  - Expected vs actual behavior
  - Device/Android version, app version, build type
  - Relevant logs/stack traces

Related docs: `../CODE_STYLE.md`, `code-style-quick-reference.md`, `testing-strategy.md`, `security-encryption.md`, `background-jobs.md`, `architecture.md`
