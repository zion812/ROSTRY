# Deployment & Release Management

Procedures for preparing and shipping releases.

## Build Types

- Debug, Staging, Release with distinct Firebase projects and configs.

## Signing & ProGuard

- Configure signing configs securely; store keys outside VCS.
- Optimize ProGuard; keep rules for reflection/Room/Parcelable.

## Play Store

- Generate App Bundle (AAB); upload via Play Console.
- Track version codes/names and release notes.

## Testing Before Release

- Run unit, instrumentation, and smoke tests.
- Verify critical flows: auth, payments, transfers, notifications.

## Rollback & Hotfix

- Maintain ability to roll back; use staged rollouts.

## Monitoring

- Crashlytics, performance monitoring, and analytics dashboards.

## Migrations

- Plan Room migrations; test upgrade paths.

## Feature Flags

- Use toggles for controlled rollouts; default safe values.
