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
