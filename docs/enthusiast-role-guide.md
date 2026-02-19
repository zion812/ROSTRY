# Enthusiast Role Guide

## Overview
The Enthusiast role in ROSTRY is tailored for poultry breeding hobbyists and specialists who want to manage their birds, track genetics, coordinate transfers, and engage with the community.

## Complete Enthusiast Features

### 1. Home Dashboard
- **Fetcher Cards**: Quick KPIs for active pairs, eggs collected this week, hatching due in 7 days, transfers pending, disputed transfers count, breeder success rate, hatch rate (last 30 days), and top bloodlines engagement.
- **Navigation**: Tap cards to navigate to detailed screens (Breeding, Transfers, Marketplace, Analytics).
- **Weekly Refresh**: Dashboard data refreshes weekly via `EnthusiastPerformanceWorker`.

### 2. Breeding Management
- **Breeding Calculator**: Advanced breeding prediction algorithms for genetic trait inheritance
- **Breeding Flows**:
  - **Pairs Tab**: Create and manage breeding pairs. Pairs are marked active/retired. Validations ensure male/female are not identical.
  - **Mating Tab**: Log mating events per pair. View recent matings and a weekly chart. Validations prevent logging for inactive pairs.
  - **Eggs Tab**: Collect egg counts per pair. Totals and CTAs to start incubation.
  - **Incubation Tab**: Start batches with ETA. Candle eggs, update conditions, view logs.
  - **Hatching Tab**: Log hatch outcomes (success/failure/culled). Per-batch success rate calculated.

### 3. Performance Journal
- **Performance Journal Screen**: Detailed performance tracking for breeding and hatching
- **Analytics**: Comprehensive metrics and trend analysis
- **Historical Data**: Long-term performance tracking

### 4. Virtual Arena
- **Competitive Events**: Virtual poultry competitions
- **Rankings**: Performance-based leaderboards
- **Prizes**: Achievement-based rewards

### 5. Egg Collection & Hatchability Tracking
- **Egg Collection Screen**: Track collected eggs with detailed metrics
- **Hatchability Tracker**: Analyze hatchability rates and factors
- **Egg Tray**: Visual grid of eggs with status indicators
- **Hatchability Analysis Screen**: Detailed analysis of hatching success rates

### 6. Rooster Cards & Showcase Cards
- **Rooster Card Screen**: Shareable bird cards with detailed information
- **Showcase Card Screen**: Bird showcase functionality for community sharing
- **Media Integration**: Rich media support for bird presentation

### 7. Transfer Management
- **Claim Transfer Screen**: Transfer claiming functionality
- **Transfers Tab**: Manage all transfer activities
- **Transfers Screen**: Detailed transfer management
- **Transfer Verification**: Secure transfer validation process

### 8. Show Records
- **Show Log**: Detailed show records and performance
- **Show Records Screen**: Comprehensive show history
- **Performance Tracking**: Show-specific metrics and achievements

### 9. Pedigree Management
- **Pedigree Screen**: Advanced lineage tracking
- **Pedigree Tree**: 7-node ancestry visualization with father/mother/grandparent connections
- **Family Tree Integration**: Seamless integration with traceability features

### 10. Digital Coop Features
- **Specialized Enthusiast Features**: Unique functionality for digital coop management
- **Coop Management**: Tools for managing digital coop environments
- **Coop Analytics**: Performance metrics for coop operations

### 11. Bird Studio V2 — Advanced Appearance Customization
- **6 Appearance Dimensions**: Stance (6 postures), Sheen (6 finishes), NeckStyle (6 shapes), BreastShape (5 types), SkinColor (5 tones), HeadShape (5 forms)
- **BirdAppearance Data Class**: 6 new fields integrated with backward-compatible JSON serialization
- **Studio UI Categories**: BUILD category (💪) for Stance/Neck/Breast/Skin; Sheen in Plumage; Head Shape in Head
- **Universal Breed Presets**: 15 common breed presets (RIR, Leghorn, Brahma, Plymouth Rock, Silkie, Cochin, Orpington, Wyandotte, Sussex, Malay, Shamo, Ayam Cemani, Cornish, Marans, Ameraucana)
- **Randomize Button**: One-tap random appearance generation
- **Canvas Rendering**: Real-time rendering of stance offset, sheen overlays, neck shapes (arched/muscular/hackle), breast proportions, head shape variants, and skin tints
- **Key Files**: `BirdAppearance.kt`, `BirdStudioScreen.kt`, `BirdPartRenderer.kt`

### 12. Transfers & Verification
- **Initiate Transfers**: Specify buyer, amount, and optional photos/GPS.
- **Verify Transfers**: Seller uploads before/after photos with EXIF metadata. Buyer confirms GPS (within 100m radius or explain). Identity doc via digital signature.
- **Timeline & Audit Trail**: View all verification steps and audit logs. Disputes can be raised and resolved.
- **GPS Proximity Badge**: Visual indicator if buyer GPS is outside 100m of seller location; explanation required.

### 13. Family Tree & Traceability
- **Interactive Tree**: Pan/zoom family tree with Canvas drawing. Navigate to node details.
- **Lineage Audits**: `LifecycleWorker` verifies parent/partner existence and detects cycles; alerts created on inconsistencies.
- **Trait Milestones**: High-value traits (champion bloodline, rare color) trigger notifications at 6/12/24 months.

### 14. Explore Tabs
- **Nearby Farmers Tab**: Discover local farmers via horizontal scrolling cards or interactive map view.
  - **Map View**: Visualize farmer distribution geographically. Tap pins to preview and navigate to profiles.
  - **Profiles**: Direct navigation to full profile from avatars or map markers.
- **Educational Content**: Access curated guides and articles for poultry management.
- **Products Tab**: Browse marketplace products with verified seller badges and trust signals.
- **Events Tab**: Stream upcoming events. RSVP (Going/Maybe/Not Going). Exhibitor FAB to create events. View attendees and discussion.
- **Showcase Tab**: Stream showcase posts (PRODUCT_SHOWCASE, BREEDING_ACHIEVEMENT). Like, comment, share. Create showcase posts with media.

### 15. Social Features
- **Community Hub**: Engage with other enthusiasts
- **Leaderboards**: Competitive rankings
- **Live Broadcasting**: Real-time community events
- **Messaging**: Direct communication with other enthusiasts
- **Social Feed**: Community content and updates

### 16. Gamification
- **Achievements**: Goal-based rewards
- **Badges**: Visual recognition system
- **Reputation Scoring**: Community trust metrics
- **Hall of Fame**: Top performers recognition
- **Competitions**: 6 competition types (Best Breeder, Top Seller, Healthiest Flock, Egg Champion, Fastest Growth, Showcase)

### 17. Analytics & Reporting
- **Performance Dashboards**: Role-specific analytics views
- **Breeding Insights**: Genetic performance analysis
- **AI-Powered Recommendations**: Predictive insights
- **Export Functionality**: CSV, PDF report generation
- **Data Visualization**: Charts and graphs for trends

### 18. Offline-First Behavior
- **Dirty Flag**: All entities support `dirty` and `syncedAt` fields. Created/updated records are marked dirty.
- **Sync on Reconnect**: On network reconnect, sync worker uploads dirty records to backend and clears flags.
- **Local-first**: All CRUD operations work offline; UI reflects local state immediately.

### 19. Background Automation
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
- **Entities**: `BreedingPairEntity`, `MatingLogEntity`, `EggCollectionEntity`, `HatchingBatchEntity`, `HatchingLogEntity`, `TransferEntity`, `TransferVerificationEntity`, `DisputeEntity`, `AuditLogEntity`, `EventEntity`, `EventRsvpEntity`, `PostEntity`, `RoosterCardEntity`, `ShowRecordEntity`, `PedigreeEntity`, `HatchabilityEntity`, `PerformanceJournalEntity`.
- **Repositories**: `EnthusiastBreedingRepository`, `TransferWorkflowRepository`, `TraceabilityRepository`, `VirtualArenaRepository`, `HatchabilityRepository`, `RoosterCardRepository`, `ShowRecordRepository`, `PedigreeRepository`, `PerformanceJournalRepository`.
- **Workers**: `LifecycleWorker`, `EnthusiastPerformanceWorker`, `HatchabilityWorker`, `PedigreeWorker`.
- **Utils**: `VerificationUtils`, `ImageCompressor`, `MediaUploadManager`, `EnthusiastAnalyticsTracker`, `EnthusiastNotifier`, `BreedingCalculatorUtils`, `HatchabilityAnalyzer`.

For detailed API docs, see `docs/api/`.

---

*Last updated: Sprint completion*

### 20. Digital Twin & Growth Analytics
- **Live Digital Twin**: Real-time 3D/2.5D representation of your bird based on genetics and age.
- **Lifecycle Engine**: 7-stage Aseel-specific lifecycle tracking (Egg -> Hatchling -> Chick -> Grower -> Sub-Adult -> Adult -> Mature Adult).
- **Growth Tracker**: Log weight and compare against breed-standard growth curves.
- **Morph Analysis**: Track physical development (spur growth, plumage, hackles) over time.
- **Valuation Radar**: 5-point score system (Morphology, Genetics, Performance, Health, Market Value).
- **Event Timeline**: Unified history of all bird events (vaccinations, fights, weight logs).
