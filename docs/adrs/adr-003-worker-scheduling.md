# ADR-003: Background Worker Scheduling

- Status: Draft
- Date: 2025-10-10

## Context
Need reliable background execution within Android system constraints and battery optimizations.

## Decision
Use WorkManager with differentiated constraints and intervals by task criticality and user impact.

## Consequences
- Positive: System-aware scheduling, reliability, battery efficiency.
- Negative: Complexity in constraints; potential delays.
- Mitigation: Tiered scheduling with relaxed constraints for critical tasks and deferrable windows for non-critical.

## References
- Scheduling: `app/src/main/java/com/rio/rostry/RostryApp.kt`
- Workers: `docs/background-jobs.md`
