# ADR-002: Offline-First Synchronization

- Status: Accepted
- Date: 2025-09-28

## Context
Agricultural settings may have poor connectivity; reliability offline is essential.

## Decision
Adopt offline-first architecture using Room as source of truth and incremental sync.

## Consequences
- Positive: Robust offline UX, reduced data usage, better performance.
- Negative: Conflict resolution complexity, more storage.
- Mitigation: Server-wins for most conflicts; user notification for critical conflicts.

## References
- Sync coordination: `app/src/main/java/com/rio/rostry/data/sync/SyncManager.kt`
- Workers: `app/src/main/java/com/rio/rostry/workers/SyncWorker.kt`
