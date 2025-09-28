# Troubleshooting Guide

Common issues and solutions.

## Build & Gradle

- Sync failures: check Gradle and plugin versions; clear caches; ensure JDK 17.
- KSP/Room errors: verify TypeConverters are imported in `@Database`.

## Firebase

- Missing `google-services.json`: place in `app/` and re-sync.

## Database

- Migration failures: enable Room migration testing; log schema diffs.
- Encryption: verify SQLCipher setup and keys.

## Network

- Timeouts: inspect interceptors; test with mock server.

## WorkManager

- Jobs not running: check constraints, battery optimizations, and logs.

## UI/Compose

- Recomposition issues: use keys appropriately; avoid heavy work in composables.

## Auth/Session

- Token issues: verify refresh flow and session timeouts.

## Testing

- Emulator flakiness: cold boot or switch images; run tests on CI images.

## Release/ProGuard

- Crashes in release: map stack traces; update keep rules.

## Getting Help

- Open an issue with logs, device info, and reproduction steps.
