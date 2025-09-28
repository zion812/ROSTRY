# Background Jobs & WorkManager

Scheduling, constraints, and reliability for background tasks.

## Integration

- `RostryApp.kt` schedules recurring jobs on app start.
- Workers include:
  - `SyncWorker.kt` (data sync)
  - `LifecycleWorker.kt` (milestones)
  - `TransferTimeoutWorker.kt` (SLA enforcement)
  - `ModerationWorker.kt` (content scanning)
  - `OutgoingMessageWorker.kt` (message delivery)
  - `AnalyticsAggregationWorker.kt`, `ReportingWorker.kt` (analytics)
  - `VaccinationReminderWorker.kt`, `FarmPerformanceWorker.kt` (farm monitoring)
  - `PrefetchWorker.kt` (caching)

## Constraints & Scheduling

- Tiered constraints by criticality; backoff for failures.

## Hilt & DI

- Workers injected with repositories/services; testable with Hilt test rules.

## Monitoring

- Track execution, success, and retries; expose debug UI in dev.

## Performance

- Batch work, respect battery saver, and coalesce network jobs.
