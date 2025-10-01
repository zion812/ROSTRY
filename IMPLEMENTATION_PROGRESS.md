# General User Production Release - Implementation Progress

## Sprint 1: Core Flow Completion & Integration (Weeks 1-2)

### ✅ Completed Tasks

#### Database & DAO Layer (100%)
- [x] Created `OutboxEntity` for offline-first queued mutations
- [x] Created `OutboxDao` with CRUD and query methods
- [x] Extended `OrderDao` with additional queries for General user flows:
  - `getOrdersByStatus()` for notification queries
  - `getRecentOrders()` for profile order history pagination
  - `update()` for status transitions
- [x] Updated `AppDatabase` to version 18 with `OutboxEntity`
- [x] Created `MIGRATION_17_18` for outbox table creation
- [x] Updated `DatabaseModule` with migration and OutboxDao provider

#### Repository Layer (100%)
- [x] Extended `OrderRepositoryImpl` (formerly `AdvancedOrderService`) with:
  - `updateOrderStatus()` with state transition validation
  - `getOrdersForNotification()` for push notification queries
  - `getRecentOrdersForUser()` for profile history

#### Sync & Offline-First (100%)
- [x] Integrated outbox processing into `SyncManager.syncAll()`:
  - Added OutboxDao injection
  - Added Gson injection for JSON serialization
  - Implemented FIFO processing of pending entries (limit 50)
  - Added retry logic with max 3 attempts
  - Added purging of completed entries older than 7 days
  - Updated `SyncStats` to include `outboxProcessed` count
- [x] Modified `GeneralCartViewModel` for offline checkout:
  - Injected `OutboxDao`, `ConnectivityManager`, and `Gson`
  - Added `hasPendingOutbox` field to `CartUiState`
  - Observed pending outbox entries for current user
  - Modified `checkout()` to queue orders in outbox when offline
  - Added "Order queued for submission when online" message
  - Maintained existing online flow for COD and demo payment

#### Background Workers (100%)
- [x] Created `OrderStatusWorker` for polling order status updates:
  - Hilt-injected worker with proper dependencies
  - Polls active orders every 30 minutes
  - Fetches latest status from Firestore
  - Updates local orders via repository
  - Creates local notifications with deep links
  - Stores notifications in NotificationDao
  - Scheduled in companion object `schedule()` method

#### Localization (100%)
- [x] Populated `values/strings.xml` with English strings for General flows
- [x] Created `values-te/strings.xml` with Telugu translations
- [x] 90+ strings covering Market, Cart, Explore, Create, Profile

#### Build Configuration (100%)
- [x] Added ProGuard rules in `proguard-rules.pro` for General features
- [x] Keep rules for ViewModels, OutboxEntity, Routes, session managers

### 🚧 In Progress

#### ViewModels (Remaining)
- [ ] `GeneralCreateViewModel` - Post creation with offline support
- [ ] `GeneralExploreViewModel` - Feed discovery and search
- [ ] `GeneralProfileViewModel` - Profile display and editing
- [ ] `GeneralMarketViewModel` - Already exists, verify completeness

#### UI Screens (Modifications Needed)
- [ ] `GeneralCartRoute` - Add outbox pending banner, deep link support
- [ ] `GeneralMarketRoute` - Accessibility enhancements
- [ ] `GeneralExploreRoute` - Accessibility enhancements
- [ ] `GeneralCreateRoute` - Accessibility enhancements
- [ ] `GeneralProfileRoute` - Accessibility enhancements

#### Notification & Deep Links
- [ ] Modify `AppFirebaseMessagingService` for order notifications
- [ ] Update `AppNavHost` for deep link handling (rostry://order/{orderId})
- [ ] Create `OrderStatusWorker` for polling order status updates
- [ ] Update `RostryApp` to schedule `OrderStatusWorker`

#### Localization
- [ ] Populate `values/strings.xml` with English strings for General flows
- [ ] Create `values-te/strings.xml` with Telugu translations

### 📋 Pending Tasks

#### Testing & Quality (Sprint 2)
- [ ] Create `GeneralUserFlowTest` for end-to-end acceptance tests
- [ ] Create `GeneralMarketViewModelTest` unit tests
- [ ] Create `GeneralCartViewModelTest` unit tests
- [ ] Create `OutboxSyncTest` integration tests
- [ ] Add accessibility content descriptions and semantic properties
- [ ] Verify ProGuard rules for General user features

#### Demo Data & Seeding
- [ ] Create `DemoProductSeeder` with 60-100 sample products
- [ ] Integrate seeding in `RostryApp.onCreate()` for debug builds

#### Firebase Configuration
- [ ] Create `firestore.rules` with security constraints
- [ ] Create `firestore.indexes.json` for composite indexes
- [ ] Deploy rules and indexes via Firebase CLI

#### Release Configuration
- [ ] Update `app/build.gradle.kts` with release signing config
- [ ] Add ProGuard rules in `proguard-rules.pro` for General features
- [ ] Create `.github/workflows/release.yml` workflow
- [ ] Create `docs/general-user-release-checklist.md`

## Architecture Notes

### Offline-First Flow

```
User Action (Offline) → Queue in Outbox → SyncManager Processes → Firestore Push
                       ↓
                   Local DB (dirty=true)
```

### Order Status Transitions

```
PLACED → CONFIRMED → OUT_FOR_DELIVERY → DELIVERED
  ↓          ↓              ↓
CANCELLED  CANCELLED    CANCELLED
```

### Outbox Entry Lifecycle

```
PENDING → IN_PROGRESS → COMPLETED (purged after 7 days)
            ↓ (on failure)
        Retry (max 3) → FAILED
```

## Key Implementation Decisions

1. **Outbox Pattern**: Chosen over event sourcing for simplicity and alignment with existing dirty flag pattern
2. **Gson Serialization**: Reused existing Gson provider for JSON serialization in outbox
3. **Retry Strategy**: Exponential backoff handled by SyncManager's withRetry() helper
4. **State Transitions**: Enforced at repository level with validation map
5. **Network Detection**: Using ConnectivityManager injected via Hilt

## Next Steps

1. Complete remaining ViewModels (Create, Explore, Profile)
2. Add deep link support and notification handling
3. Implement accessibility enhancements across all General screens
4. Create comprehensive test suites
5. Add localization strings
6. Configure Firebase security and indexes
7. Set up release workflow and documentation

## Testing Strategy

### Unit Tests
- ViewModel state management
- Repository business logic
- Outbox serialization/deserialization

### Integration Tests
- Outbox sync flow
- Order state transitions
- Offline → Online reconciliation

### UI Tests
- Complete user journey (browse → cart → checkout)
- Offline checkout scenario
- Deep link navigation
- Accessibility validation

### Performance Baselines
- Product list scroll: 60 FPS on 2GB RAM device
- Image load: < 2s on 3G
- Checkout flow: < 5s end-to-end
- Cold start: < 3s on Android 9+

## References

- Plan document: `VERIFICATION_FIXES_IMPLEMENTATION.md`
- Offline-first ADR: `docs/adrs/adr-002-offline-first-sync.md`
- Architecture: `docs/architecture.md`
- Testing strategy: `docs/testing-strategy.md`
