# Enthusiast Role Guide

## Overview
The Enthusiast role in ROSTRY is tailored for poultry breeding hobbyists and specialists who want to manage their birds, track genetics, coordinate transfers, and engage with the community.

## Features

### 1. Home Dashboard
- **Fetcher Cards**: Quick KPIs for active pairs, eggs collected this week, hatching due in 7 days, transfers pending, disputed transfers count, breeder success rate, hatch rate (last 30 days), and top bloodlines engagement.
- **Navigation**: Tap cards to navigate to detailed screens (Breeding, Transfers, Marketplace, Analytics).
- **Weekly Refresh**: Dashboard data refreshes weekly via `EnthusiastPerformanceWorker`.

### 2. Breeding Flows
- **Pairs Tab**: Create and manage breeding pairs. Pairs are marked active/retired. Validations ensure male/female are not identical.
- **Mating Tab**: Log mating events per pair. View recent matings and a weekly chart. Validations prevent logging for inactive pairs.
- **Eggs Tab**: Collect egg counts per pair. Totals and CTAs to start incubation.
- **Incubation Tab**: Start batches with ETA. Candle eggs, update conditions, view logs.
- **Hatching Tab**: Log hatch outcomes (success/failure/culled). Per-batch success rate calculated.

### 3. Transfers & Verification
- **Initiate Transfers**: Specify buyer, amount, and optional photos/GPS.
- **Verify Transfers**: Seller uploads before/after photos with EXIF metadata. Buyer confirms GPS (within 100m radius or explain). Identity doc via digital signature.
- **Timeline & Audit Trail**: View all verification steps and audit logs. Disputes can be raised and resolved.
- **GPS Proximity Badge**: Visual indicator if buyer GPS is outside 100m of seller location; explanation required.

### 4. Family Tree & Traceability
- **Interactive Tree**: Pan/zoom family tree with Canvas drawing. Navigate to node details.
- **Lineage Audits**: `LifecycleWorker` verifies parent/partner existence and detects cycles; alerts created on inconsistencies.
- **Trait Milestones**: High-value traits (champion bloodline, rare color) trigger notifications at 6/12/24 months.

### 5. Explore Tabs
- **Nearby Farmers Tab**: Discover local farmers via horizontal scrolling cards or interactive map view.
  - **Map View**: Visualize farmer distribution geographically. Tap pins to preview and navigate to profiles.
  - **Profiles**: Direct navigation to full farmer profiles from avatars or map markers.
- **Educational Content**: Access curated guides and articles for poultry management.
- **Products Tab**: Browse marketplace products with verified seller badges and trust signals.
- **Events Tab**: Stream upcoming events. RSVP (Going/Maybe/Not Going). Exhibitor FAB to create events. View attendees and discussion.
- **Showcase Tab**: Stream showcase posts (PRODUCT_SHOWCASE, BREEDING_ACHIEVEMENT). Like, comment, share. Create showcase posts with media.

### 6. Offline-First Behavior
- **Dirty Flag**: All entities support `dirty` and `syncedAt` fields. Created/updated records are marked dirty.
- **Sync on Reconnect**: On network reconnect, sync worker uploads dirty records to backend and clears flags.
- **Local-first**: All CRUD operations work offline; UI reflects local state immediately.

### 7. Background Automation
- **LifecycleWorker**: Runs daily. Computes lifecycle stages, generates milestones (vaccination, growth, breeder eligibility). Audits lineage and emits trait milestone notifications.
- **EnthusiastPerformanceWorker**: Runs weekly. Aggregates KPIs (hatch rate, breeder success, pairs to mate, top bloodlines) and persists snapshot with dirty flag.
- **Notifications**: `EnthusiastNotifier` sends alerts for pairs to mate, eggs due, hatching due, breeder eligibility, lineage inconsistencies, and trait milestones.

## Troubleshooting

### Pairs not showing in "Pairs to Mate"
- Ensure pair status is ACTIVE.
- Verify last mating is > 7 days ago or null.
- Check `MatingLogDao.observeLastMatedByFarmer(userId)` returns correct data.

### Upload progress stuck
- Check `MediaUploadManager` events stream. If `UploadEvent.Failed` emitted, check network and retry.
- Compressed images stored in cache; original files in app-specific storage.

### GPS verification fails
- Ensure GPS permissions granted.
- If outside 100m radius, explanation field is required before submit button is enabled.
- Check `VerificationUtils.withinRadius(...)` logic.

### Lineage inconsistency alerts
- Navigate to Family Tree via deep link in alert.
- Verify breeding records for cycles or missing parents.
- Use `TraceabilityRepository.verifyPath(...)` to debug.

### Dirty records not syncing
- Confirm network connectivity.
- Check sync worker logs; worker should query `WHERE dirty = 1` and POST to backend.
- On success, worker calls `dao.upsert(entity.copy(dirty = false, syncedAt = now()))`.

## Best Practices
- **Weekly Review**: Check dashboard every week for KPIs and notifications.
- **Prompt Verification**: Complete transfer verification steps promptly to avoid timeouts.
- **Regular Mating Logs**: Log matings immediately after pairing to maintain accurate records.
- **Backup**: Offline data is encrypted locally; ensure device backups are enabled.
- **Community Engagement**: Use Showcase tab to share breeding achievements and connect with other enthusiasts.

## Release Checklist
Before shipping enthusiast features, review the checklist: see `ENTHUSIAST_RELEASE_CHECKLIST.md` at repo root.

## API & Schema
- **Entities**: `BreedingPairEntity`, `MatingLogEntity`, `EggCollectionEntity`, `HatchingBatchEntity`, `HatchingLogEntity`, `TransferEntity`, `TransferVerificationEntity`, `DisputeEntity`, `AuditLogEntity`, `EventEntity`, `EventRsvpEntity`, `PostEntity`.
- **Repositories**: `EnthusiastBreedingRepository`, `TransferWorkflowRepository`, `TraceabilityRepository`.
- **Workers**: `LifecycleWorker`, `EnthusiastPerformanceWorker`.
- **Utils**: `VerificationUtils`, `ImageCompressor`, `MediaUploadManager`, `EnthusiastAnalyticsTracker`, `EnthusiastNotifier`.

For detailed API docs, see `docs/api/`.

---

*Last updated: Sprint completion*
