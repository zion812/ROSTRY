# ADR-002: Offline-First Synchronization

> **Note**: This ADR supersedes any draft versions. For implementation details, see SyncManager.kt and related workers.

- Status: Accepted
- Date: 2025-10-16

## Context
ROSTRY must function reliably with intermittent connectivity and provide a seamless user experience offline. Agricultural settings often have poor or intermittent network access, making offline-first design essential for usability.

## Decision
Adopt an offline-first approach using Room as the source of truth with background sync to remote services. Implement incremental sync with conflict resolution strategies tailored to entity types.

## Consequences
- Positive: Resilient UX; reduced perceived latency; robust offline UX, reduced data usage, better performance.
- Negative: Conflict resolution complexity; storage overhead.
- Mitigation: Clear conflict policy and background job monitoring. Server-wins for most conflicts (e.g., terminal transfer states); user notification for critical conflicts. Implement per-entity merge strategies in SyncManager (e.g., last-write-wins for general fields, lineage verification for products, monotonic updates for age/stage).

## Conflict Resolution Strategies
- **Products**: Custom merge logic in `mergeProductRemoteLocal()` - last-write-wins for most fields, but lineage fields (parent IDs) require verification via TraceabilityRepository before accepting remote changes. Age weeks are monotonic non-decreasing. Stage transitions prefer recency of `lastStageTransitionAt`.
- **Transfers**: Server-wins if remote has terminal status (COMPLETED/CANCELLED/TIMED_OUT); otherwise push local changes.
- **General Entities**: Last-write-wins by `updatedAt` timestamp for simple fields.
- **Lineage Protection**: `protectLineageOnPush()` prevents overwriting remote lineage data with local dirty changes if remote has newer/present lineage fields.

## References
- Sync coordination: `app/src/main/java/com/rio/rostry/data/sync/SyncManager.kt`
- Workers: `app/src/main/java/com/rio/rostry/workers/SyncWorker.kt`
- `docs/architecture.md`
- `docs/adrs/adr-003-worker-scheduling.md`
