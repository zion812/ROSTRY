# Phase Breakdown

## Task 1: Implement Farm Asset to Market Listing Bridge - Data Layer

Create the data layer for converting farm assets to market listings:
Add `createListingFromAsset()` method in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\data\repository\MarketListingRepository.kt`
Implement conversion logic in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\data\repository\MarketListingRepositoryImpl.kt` with dirty flag pattern
Add `toListingData()` extension function in app/src/main/java/com/rio/rostry/domain/model/FarmAssetExtensions.kt
Write to Room with `dirty=true`, no direct Firestore calls
Add unit tests for conversion logic and dirty flag persistence


## Task 2: Implement Farm Asset to Market Listing Bridge - UI Layer

Build the UI for creating market listings from farm assets:
Create `CreateListingFromAssetScreen.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\farmer\listing`
Create `CreateListingViewModel.kt` with `GetTodayTasksUseCase` integration
Add "Sell This" button in app/src/main/java/com/rio/rostry/ui/farmer/asset/FarmAssetDetailsScreen.kt
Pre-fill form fields from `FarmAssetEntity` data
Add offline indicator for pending sync
Use Coil for aggressive image caching
Wire up navigation route `Routes.FarmerNav.CREATE_LISTING_FROM_ASSET`


## Task 3: Implement Auction Creation from Farm Assets

Enable farmers to create auctions directly from farm assets:
Add `createAuctionFromAsset()` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\data\repository\AuctionRepository.kt`
Create `CreateAuctionFromAssetScreen.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\farmer\listing`
Add auction-specific fields (starting price, duration, minimum bid increment)
Wire up route `Routes.FarmerNav.CREATE_AUCTION_FROM_ASSET`
Add "Create Auction" option in asset details screen
Implement offline-first with dirty flag pattern


## Task 4: Implement Digital Farm 2.5D Canvas Foundation

Build the core rendering engine for Digital Farm 2.5D visualization:
Create `DigitalFarmCanvas.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\farmer\digital`
Implement isometric projection utilities in app/src/main/java/com/rio/rostry/ui/farmer/digital/IsometricUtils.kt
Add zone rendering (coops, feeding areas, breeding pens) with depth sorting
Implement camera controls (pan, zoom, rotation)
Create `ZoneEntity` data class for zone definitions
Add basic bird rendering with isometric sprites
Use Canvas API for hardware-accelerated drawing


## Task 5: Implement Flocking Behavior and Bird Animation

Add realistic bird movement using boids algorithm:
Create `FlockingEngine.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\farmer\digital`
Implement boids rules (separation, alignment, cohesion) with configurable weights
Add obstacle avoidance for zone boundaries
Create bird animation states (idle, walking, pecking, flying)
Integrate with `FarmAssetEntity` health status for color-coding
Optimize performance for 50+ birds using spatial partitioning
Add frame rate limiting for battery efficiency


## Task 6: Implement Weather Effects and Environmental Rendering

Add dynamic weather effects to Digital Farm visualization:
Create `WeatherEffectsRenderer.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\farmer\digital`
Implement rain particles with physics (gravity, wind)
Add sun rays with volumetric lighting effect
Create cloud shadows that move across the farm
Integrate with `WeatherRepository` for real-time data
Add day/night cycle with ambient lighting changes
Optimize particle systems for low-end devices


## Task 7: Enhance Digital Farm with Breeding Zones for Enthusiasts

Add enthusiast-specific features to Digital Farm:
Create `BreedingZoneRenderer.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\enthusiast\digitalfarm`
Add visual representation of breeding pairs with connecting lines
Implement egg collection zone with tray visualization
Add pedigree indicators (champion bloodline badges)
Create interactive tooltips showing bird genetics on tap
Integrate with `EnthusiastBreedingRepository` for real-time data
Add evolutionary visuals (growth stages, color mutations)


## Task 8: Implement RBAC Enforcement in Domain Layer Use Cases

Add role-based access control to domain use cases:
Create `RbacValidator.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\domain\validation`
Add permission checks in `CreateListingUseCase`, `CreateAssetUseCase`, `InitiateTransferUseCase`
Update `GetTodayTasksUseCase` to filter tasks by user role
Add `@RequiresRole` annotation for use case methods
Return `Resource.Error` with "Insufficient permissions" for unauthorized access
Add comprehensive unit tests for all permission scenarios
Document RBAC rules in KDoc comments


## Task 9: Implement Firestore Security Rules for Free Tier

Create and test Firestore security rules without Cloud Functions:
Update `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\firebase\firestore.rules` with role-based read/write rules
Add rules for `farm_assets`, `market_listings`, `breeding_pairs`, `transfers` collections
Implement custom claims validation (`request.auth.token.role`)
Add ownership checks (`request.auth.uid == resource.data.farmerId`)
Create test suite in firebase/firestore.rules.test.js using Firebase Emulator
Test all CRUD operations for each user role (General, Farmer, Enthusiast)
Document rule patterns in comments


## Task 10: Implement SyncWorker Conflict Resolution and Testing

Enhance SyncWorker with robust conflict resolution:
Update `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\workers\SyncWorker.kt` with last-write-wins logic
Add `syncedAt` timestamp comparison for conflict detection
Implement retry logic with exponential backoff for failed syncs
Add batch size limits (50 records per sync) to avoid quota overruns
Create comprehensive tests in app/src/test/java/com/rio/rostry/workers/SyncWorkerTest.kt
Test scenarios: offline edit vs remote update, poor network conditions, SQLCipher passphrase validation
Add progress indicators in UI for pending syncs


## Task 11: Implement Media Upload Worker with Compression

Optimize photo uploads for rural connectivity:
Update `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\workers\MediaUploadWorker.kt` with compression logic
Integrate `ImageCompressor` utility for aggressive compression (max 500KB per image)
Add EXIF metadata preservation for verification photos
Implement sequential upload with progress tracking
Add WiFi-preferred constraint for large uploads
Cache compressed images locally in app/src/main/java/com/rio/rostry/data/cache/
Add retry logic for failed uploads with exponential backoff
Test with 2G network simulation


## Task 12: Add Offline Indicators and Sync Status UI

Implement comprehensive sync status visualization:
Create `SyncStatusIndicator.kt` composable in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\components`
Add top bar indicator showing pending syncs count
Create `SyncStatusScreen.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\sync` for detailed sync status
Show per-entity sync progress (assets, listings, orders)
Add manual sync trigger button
Display last sync timestamp and error messages
Add "Offline Mode" banner when network unavailable
Integrate with `SyncWorker` progress updates


## Task 13: Implement Evidence-Based Order Workflow UI

Build the complete evidence-based order flow for COD transactions:
Create `EnquiryScreen.kt`, `QuoteNegotiationScreen.kt`, `PaymentProofScreen.kt` in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\order`
Implement photo capture with compression for payment proofs
Add OTP generation and verification for delivery confirmation
Create timeline view showing order progression with evidence
Add dispute raising UI with photo evidence upload
Integrate with `EvidenceOrderRepository` for offline-first workflow
Wire up all evidence order routes in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\navigation\AppNavHost.kt`


## Task 14: Add Telugu Localization Infrastructure

Prepare the app for Telugu language support:
Create `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\res\values-te\strings.xml` for Telugu strings
Extract all hardcoded strings to `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\res\values\strings.xml`
Add language selector in `c:\Users\rowdy\AndroidStudioProjects\ROSTRY\app\src\main\java\com\rio\rostry\ui\settings\SettingsScreen.kt`
Implement RTL layout support for future languages
Add date/number formatting for Telugu locale
Create translation guide in docs/localization-guide.md
Test UI with Telugu strings (placeholder translations)


## Task 15: Implement Integration Tests for Critical User Journeys

Create end-to-end tests for core workflows:
Create test suite in app/src/androidTest/java/com/rio/rostry/integration/
Test Farmer journey: Create asset → Create listing → Receive order → Fulfill with evidence
Test Enthusiast journey: Create breeding pair → Log mating → Collect eggs → Initiate transfer
Test offline scenarios: Create records offline → Sync on reconnect → Verify data integrity
Test RBAC: Verify General user cannot create assets, Farmer cannot access breeding features
Use Hilt test modules for dependency injection
Run tests on Firebase Test Lab with various devices