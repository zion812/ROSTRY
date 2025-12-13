# Implementation Roadmap: ROSTRY "Lovable" Edition

## Phase 1: The Social Core (Weeks 1-2)
**Goal:** Establish the "Twitter-like" public square and role-based feeds.

### 1.1 Community Infrastructure (Lite)
- [x] **Schema Update:** Add `hashtags`, `mentions`, `parentPostId` to `PostEntity`.
- [x] **Backend Logic:** Simple query-based threading (1 level deep).
- [x] **UI Components:** Standard `LazyColumn` with optimized view holders.

### 1.2 Role-Based Home Feed
- [x] **Logic:** Simple `UserType` check in ViewModel.
- [x] **Farmer View:** Re-use existing `Dashboard` components.
- [x] **Enthusiast View:** Re-use `PostList` with larger image aspect ratio.

### 1.3 "Flock Stories" (MVP)
- [x] **Data:** `StoryEntity` (Image URL + Timestamp).
- [x] **UI:** Simple horizontal `LazyRow` of circular avatars.

## Phase 2: Trust & Commerce (Weeks 3-4)
**Goal:** Enhance marketplace trust and transaction experience.

### 2.1 Rich Profiles
- [x] **Metrics Engine:** Implement `ReputationService` to calculate "Health Score", "Reliability", etc.
- [x] **Profile UI:** Redesign `UserProfileScreen` to showcase badges and stats prominently.

### 2.2 Marketplace Groups
- [x] **Group Type:** Add `isMarketplace` flag to `GroupEntity`.
- [x] **Listing Integration:** Allow attaching [ProductEntity](file:///c:/Users/rowdy/AndroidStudioProjects/ROSTRY/app/src/main/java/com/rio/rostry/data/database/entity/ProductEntity.kt#10-109) to group posts.

## Phase 3: Delight & Polish (Week 5)
**Goal:** Add the "premium" feel.

### 3.1 Micro-interactions (Low Cost)
- [x] **Haptics:** Use `HapticFeedbackConstants.KEYBOARD_TAP`.
- [ ] **Animations:** Standard Android `AnimatedVisibility` (Fade/Scale).

### 3.2 Visual Overhaul (Performance)
- [x] **Theme:** High-contrast Dark Mode (OLED friendly). No heavy blurs.

## Phase 4: Farm Management & Profiles (Week 6)
**Goal:** Professionalize the farmer's workflow and public image.

### 4.1 Visual Logging
- [x] **UI:** "Quick Log" Grid with icons for Weather, Feed, Health.
- [x] **Logic:** Auto-fill timestamps and location.

### 4.2 Farm Health Engine
- [x] **Logic:** Calculate `HealthScore` based on [MortalityRecord](file:///c:/Users/rowdy/AndroidStudioProjects/ROSTRY/app/src/main/java/com/rio/rostry/data/database/entity/FarmMonitoringEntities.kt#70-96) and [VaccinationRecord](file:///c:/Users/rowdy/AndroidStudioProjects/ROSTRY/app/src/main/java/com/rio/rostry/data/database/entity/FarmMonitoringEntities.kt#98-127) consistency.
- [x] **Alerts:** Simple local notification triggers (e.g., "Vaccination Due Tomorrow").

### 4.3 Public Farm Profile
- [x] **UI:** `FarmProfileScreen` with "Health Score" gauge and "Flock Gallery".
- [x] **Data:** Aggregation query to show "Birds Sold" and "Years Active".

## Phase 5: Commerce & Workflows (Week 7)
**Goal:** Smooth, secure, and controlled transactions.

### 5.1 Order Workflow
- [x] **Approvals:** Add `status` (PENDING_APPROVAL) to `OrderEntity`. Build `OrderRequestScreen` for farmers.
- [x] **Custom Status:** Add `customStatus` string to [ProductEntity](file:///c:/Users/rowdy/AndroidStudioProjects/ROSTRY/app/src/main/java/com/rio/rostry/data/database/entity/ProductEntity.kt#10-109). Build `StatusPicker` sheet.

### 5.2 Transfer & Bidding
- [x] **Transfer:** Implement QR Code generation/scanning for `TransferRepository`.
- [x] **Bidding UI:** Add "Quick Bid" chips and "Outbid" snackbar notifications.
- [ ] **UI Polish:** Revamp `OrderDetailsScreen` and `AuctionScreen` with cleaner layout.
