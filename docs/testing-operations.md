# Testing, Security, and Operations Guide

## Testing Strategy
- **Unit Tests** (`app/src/test/`)
  - Target ViewModels and repositories with `kotlinx-coroutines-test` and in-memory Room.
  - Use MockK/Robolectric for isolated logic (e.g., `TransferWorkflowRepositoryImpl`, `SessionManager`).
  - Provide fakes for Firebase services where feasible.
- **UI & Instrumentation Tests** (`app/src/androidTest/`)
  - Compose UI tests for critical flows (auth, transfers, social interactions).
  - WorkManager instrumentation tests using `WorkManagerTestInitHelper` to validate scheduling/constraints.
- **Migration Tests**
  - Add `AutoMigrationSpec` tests or custom test harness to migrate from each historical schema to latest using `Room.databaseBuilder(...).createFromAsset(...)`.
- **Continuous Integration**
  - Recommended pipeline steps:
    1. `./gradlew lint ktlintCheck detekt` (if configured).
    2. `./gradlew testDebugUnitTest`.
    3. `./gradlew connectedDebugAndroidTest` (via Firebase Test Lab for device matrix).
    4. Optional: `./gradlew :app:bundleRelease` for release builds.

## Security Practices
- **Database Encryption**: SQLCipher via `DatabaseModule.kt` ensures at-rest encryption. Manage passphrase securely (replace default in production with a key vault or user-derived secret).
- **Authentication**: Firebase phone auth; enforce re-authentication based on `SessionManager` role-specific TTLs.
- **Verification**: `VerificationUtils` for GPS and document validation; integrate external KYC APIs for high-value transfers.
- **Network**: Use HTTPS endpoints via Retrofit with certificate pinning when possible.
- **Secrets Management**: Keep API keys and Firebase configuration outside source control (`local.properties`, remote secret stores).
- **Logging**: Timber in debug builds; avoid logging PII. Employ Crashlytics for release crash reporting.
- **Rate Limiting**: Persist cooldowns in `RateLimitEntity`; enforce in repositories and Cloud Functions.
- **Compliance**: Store audit logs (`AuditLogEntity`) and disputes for regulatory review. Consider exporting to secure storage periodically.

## Operational Procedures
- **Firebase Setup**
  - Place `google-services.json` in `app/` (already highlighted in `README.md`).
  - Configure Firestore indexes for queries used by `SocialRepositoryImpl`, `TransferWorkflowRepositoryImpl`, etc.
  - Deploy Cloud Functions (`initiateTransfer`, `onTransferStatusChange`, analytics exporters). Document scripts in `/functions` (if separate repo).
- **Background Workers**
  - Monitor WorkManager job status via `adb shell dumpsys activity service WorkManager`.
  - Tune cadence in `RostryApp.kt` as usage patterns evolve.
- **Analytics Reporting**
  - Ensure `ReportingWorker` can access external storage (scoped storage guidelines) for exporting files.
  - Sync generated reports to Firebase Storage or remote server for archival.
- **Release Process**
  - Update versionCode/versionName in `app/build.gradle.kts`.
  - Run full test suite and manual verification of critical flows (auth, transfer, payments).
  - Generate signed bundle (`./gradlew bundleRelease`) and upload via Play Console.
- **Monitoring**
  - Use Firebase Crashlytics and Performance for runtime monitoring.
  - Implement Google Analytics / Firebase Analytics events to measure engagement.
  - Set up Cloud Function alerts (Stackdriver) for errors and high-latency functions.

## Incident Response Checklist
1. Review Crashlytics/Firebase alerts or Play Console ANRs.
2. Check `AuditLogEntity` for related user actions around the incident time.
3. Validate synchronization state (WorkManager logs, `SyncStateDao`).
4. If security incident:
   - Revoke/rotate secrets.
   - Force logout via Firebase token revocation.
   - Communicate with affected users as per compliance policy.

## Documentation Maintenance
- Keep this guide synchronized with code changes.
- Add ADRs for major architecture decisions (encryption, worker cadences, external integrations).
- Link new docs from onboarding materials for developer visibility.
