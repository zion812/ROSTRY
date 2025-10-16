> **ARCHIVED**: This draft ADR has been superseded by `adrs/adr-002-offline-first-sync.md` (Accepted).
> Kept for historical reference only.

# ADR-002: Offline-First Strategy

- Status: Superseded
- Date: 2025-10-10

## Context
ROSTRY must function reliably with intermittent connectivity and provide a seamless user experience offline.

## Decision
Adopt an offline-first approach using Room as the source of truth with background sync to remote services.

## Consequences
- Positive: Resilient UX; reduced perceived latency
- Negative: Conflict resolution complexity; storage overhead
- Mitigation: Clear conflict policy and background job monitoring

## References
- `docs/architecture.md`
- `docs/adrs/adr-003-worker-scheduling.md`
