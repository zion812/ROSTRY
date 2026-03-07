# Implementation Plan: End-to-End Modularization

## Overview

This implementation plan transforms ROSTRY from a hybrid monolith Android application into a hybrid-vertical modular architecture through six phases. Each phase produces a shippable application with incremental progress toward the target architecture. The plan includes 40+ modules organized into core, domain, data, and feature layers with strict dependency rules enforced by architecture tests.

## Tasks

### Phase 0: Guardrails First

- [x] 1. Create core:navigation module
  - [x] 1.1 Set up core:navigation module structure and build configuration
    - Create module directory structure
    - Configure build.gradle.kts with Android library plugin
    - Add dependencies on core:common and androidx.navigation.compose
    - _Requirements: 1.7, 12.1_
  
  - [x] 1.2 Implement NavigationRegistry interface and NavigationProvider
    - Create NavigationRegistry interface with register(), getProviders(), and buildGraphs() methods
    - Create NavigationProvider interface with featureId, buildGraph(), and getDeepLinks()
    - Implement NavigationRegistryImpl with provider list management
    - Create NavigationRoute sealed interface for type-safe routes
    - _Requirements: 2.1, 2.2, 2.5_
  
  - [ ]* 1.3 Write unit tests for NavigationRegistry
    - Test provider registration and retrieval
    - Test buildGraphs() invokes all providers
    - Test empty registry behavior
    - _Requirements: 2.1, 2.2_

- [x] 2. Create core:testing module
  - [x] 2.1 Set up core:testing module structure and build configuration
    - Create module directory structure
    - Configure build.gradle.kts with Android library plugin
    - Add test framework dependencies (JUnit, Kotest, MockK, Turbine, ArchUnit)
    - _Requirements: 1.6, 12.2_
  
  - [x] 2.2 Implement test utilities and fixtures
    - Create TestDispatchers class for coroutine testing
    - Create FakeData object with factory methods for test data
    - Add fake implementations for common interfaces
    - _Requirements: 1.6, 12.2_

- [x] 3. Implement architecture tests
  - [x] 3.1 Create ModularArchitectureTest with ArchUnit
    - Test feature modules only depend on domain and core modules
    - Test domain modules don't depend on data or feature modules
    - Test data modules only depend on domain and core modules
    - Test domain modules don't depend on Android framework
    - Test app module doesn't contain repositories or use cases
    - _Requirements: 1.1, 1.2, 1.3, 1.4_
  
  - [ ]* 3.2 Write property test for architecture boundary enforcement
    - **Property 1: App Shell Isolation**
    - **Validates: Requirements 1.4, 3.2**
  
  - [ ]* 3.3 Write property test for module dependency constraints
    - **Property 3: Feature Module Dependency Constraint**
    - **Property 4: Domain Module Isolation**
    - **Property 5: Data Module Dependency Constraint**
    - **Validates: Requirements 1.1, 1.2, 1.3**

- [x] 4. Checkpoint - Verify Phase 0 foundation
  - Ensure all architecture tests pass
  - Verify core:navigation and core:testing modules build successfully
  - Ensure all tests pass, ask the user if questions arise

### Phase 1: Shell and Navigation Extraction

- [-] 5. Extract navigation to feature modules
  - [x] 5.1 Create NavigationProvider for existing feature modules
    - Identify existing feature modules (achievements, leaderboard, etc.)
    - Create NavigationProvider implementation for each feature
    - Define feature-specific routes using NavigationRoute sealed classes
    - Implement buildGraph() with composable navigation destinations
    - _Requirements: 2.1, 3.4_
  
  - [x] 5.2 Register NavigationProviders in Application class
    - Inject NavigationRegistry in RostryApp
    - Register all NavigationProviders in onCreate()
    - Provide NavigationRegistry to MainActivity
    - _Requirements: 2.1_
  
  - [ ]* 5.3 Write integration tests for navigation registration
    - Test all feature routes are accessible
    - Test deep links navigate to correct features
    - _Requirements: 2.4_

- [~] 6. Refactor AppNavHost to use NavigationRegistry
  - [~] 6.1 Update AppNavHost to compose registered navigation graphs
    - Replace direct composable definitions with NavigationRegistry.buildGraphs()
    - Remove centralized route definitions
    - Verify all navigation flows still work
    - _Requirements: 2.2, 3.4_
  
  - [~] 6.2 Remove centralized Routes.kt file
    - Verify all routes are now defined in feature modules
    - Delete Routes.kt
    - Update any remaining references to use feature-specific routes
    - _Requirements: 3.3_
  
  - [~] 6.3 Verify AppNavHost is under 500 lines
    - Measure AppNavHost line count
    - Refactor if necessary to meet size target
    - _Requirements: 2.3_

- [~] 7. Reduce app module to thin shell
  - [~] 7.1 Move feature screens from app module to feature modules
    - Identify screens still in app module
    - Move each screen to appropriate feature module
    - Update navigation references
    - _Requirements: 3.1, 3.2_
  
  - [~] 7.2 Move ViewModels from app module to feature modules
    - Identify ViewModels still in app module
    - Move each ViewModel to appropriate feature module
    - Update Hilt modules and injection points
    - _Requirements: 3.2_
  
  - [ ]* 7.3 Write property test for navigation delegation
    - **Property 15: Navigation Delegation**
    - **Validates: Requirements 3.4**

- [~] 8. Checkpoint - Verify Phase 1 completion
  - Ensure app shell contains no feature screens or ViewModels
  - Verify all navigation flows work correctly
  - Test deep linking for all features
  - Ensure all tests pass, ask the user if questions arise

### Phase 2: Domain and Data Decoupling

- [~] 9. Create domain modules for all business areas
  - [~] 9.1 Create domain:account module
    - Set up module structure with Java library plugin
    - Define AuthRepository interface with authentication methods
    - Define UserRepository interface for user management
    - Create domain models for User, AuthState
    - _Requirements: 4.1, 4.4_
  
  - [~] 9.2 Create domain:commerce module
    - Set up module structure with Java library plugin
    - Define MarketplaceRepository interface
    - Define OrderRepository interface
    - Define CreateListingUseCase interface
    - Create domain models for MarketListing, Order
    - _Requirements: 4.1, 4.4_
  
  - [~] 9.3 Create domain:farm module
    - Set up module structure with Java library plugin
    - Define FarmAssetRepository interface
    - Define InventoryRepository interface
    - Create domain models for FarmAsset, InventoryItem
    - _Requirements: 4.1, 4.4_
  
  - [~] 9.4 Create domain:monitoring module
    - Set up module structure with Java library plugin
    - Define HealthTrackingRepository interface
    - Define TaskRepository interface
    - Create domain models for HealthRecord, Task
    - _Requirements: 4.1, 4.4_
  
  - [~] 9.5 Create domain:social module
    - Set up module structure with Java library plugin
    - Define SocialFeedRepository interface
    - Define MessagingRepository interface
    - Create domain models for Post, Message
    - _Requirements: 4.1, 4.4_
  
  - [~] 9.6 Create domain:admin module
    - Set up module structure with Java library plugin
    - Define AdminRepository interface
    - Define ModerationRepository interface
    - Create domain models for AdminMetrics, ModerationAction
    - _Requirements: 4.1, 4.4_
  
  - [ ]* 9.7 Write property test for domain interface purity
    - **Property 7: Domain Interface Purity**
    - **Validates: Requirements 4.1**
  
  - [ ]* 9.8 Write property test for domain framework independence
    - **Property 6: Domain Framework Independence**
    - **Validates: Requirements 4.4**

- [~] 10. Create data modules implementing domain contracts
  - [~] 10.1 Create data:account module
    - Set up module structure with Android library plugin
    - Implement AuthRepositoryImpl using Firebase Authentication
    - Implement UserRepositoryImpl using Room and Firestore
    - Create Hilt module binding implementations to interfaces
    - Create data source classes and mappers
    - _Requirements: 4.2, 4.5_
  
  - [~] 10.2 Create data:commerce module
    - Set up module structure with Android library plugin
    - Implement MarketplaceRepositoryImpl
    - Implement OrderRepositoryImpl
    - Implement CreateListingUseCaseImpl
    - Create Hilt module with @Binds methods
    - _Requirements: 4.2, 4.5_
  
  - [~] 10.3 Create data:farm module
    - Set up module structure with Android library plugin
    - Implement FarmAssetRepositoryImpl
    - Implement InventoryRepositoryImpl
    - Create Hilt module with @Binds methods
    - _Requirements: 4.2, 4.5_
  
  - [~] 10.4 Create data:monitoring module
    - Set up module structure with Android library plugin
    - Implement HealthTrackingRepositoryImpl
    - Implement TaskRepositoryImpl
    - Create Hilt module with @Binds methods
    - _Requirements: 4.2, 4.5_
  
  - [~] 10.5 Create data:social module
    - Set up module structure with Android library plugin
    - Implement SocialFeedRepositoryImpl
    - Implement MessagingRepositoryImpl
    - Create Hilt module with @Binds methods
    - _Requirements: 4.2, 4.5_
  
  - [~] 10.6 Create data:admin module
    - Set up module structure with Android library plugin
    - Implement AdminRepositoryImpl
    - Implement ModerationRepositoryImpl
    - Create Hilt module with @Binds methods
    - _Requirements: 4.2, 4.5_
  
  - [ ]* 10.7 Write property test for data implementation completeness
    - **Property 8: Data Implementation Completeness**
    - **Validates: Requirements 4.2**
  
  - [ ]* 10.8 Write property test for Hilt binding presence
    - **Property 9: Hilt Binding Presence**
    - **Validates: Requirements 4.5**

- [~] 11. Migrate repositories from app module to data modules
  - [~] 11.1 Update app module dependencies to include data modules
    - Add implementation dependencies for all data modules
    - Remove direct repository implementations from app/di/
    - _Requirements: 4.3_
  
  - [~] 11.2 Update feature modules to depend on domain modules
    - Add domain module dependencies to feature modules
    - Remove any direct data module dependencies
    - Update imports to use domain interfaces
    - _Requirements: 4.3_
  
  - [~] 11.3 Verify dependency injection works across modules
    - Test that Hilt correctly provides implementations
    - Verify no runtime injection errors
    - _Requirements: 4.5, 10.1_

- [~] 12. Checkpoint - Verify Phase 2 completion
  - Ensure all domain modules are framework-independent
  - Verify all data modules implement domain interfaces
  - Test that app module contains no repository implementations
  - Ensure all tests pass, ask the user if questions arise

### Phase 3: ADR-004 Inside Modularization

- [ ] 13. Define 3-tier asset entities in core:database
  - [~] 13.1 Create FarmAssetEntity
    - Define entity with Room annotations
    - Add fields: id, farmerId, assetType, birthDate, breed, gender, healthStatus, location, biologicalData, lifecycleStage, parentIds, timestamps
    - Create enums: AssetType, HealthStatus, LifecycleStage
    - _Requirements: 5.1_
  
  - [~] 13.2 Create InventoryItemEntity
    - Define entity with Room annotations
    - Add foreign key to FarmAssetEntity with CASCADE delete
    - Add fields: id, farmAssetId, farmerId, quantity, unit, harvestDate, storageLocation, qualityGrade, expiryDate, availableQuantity, reservedQuantity, timestamps
    - Create QualityGrade enum
    - _Requirements: 5.2_
  
  - [~] 13.3 Create MarketListingEntity
    - Define entity with Room annotations
    - Add foreign key to InventoryItemEntity with CASCADE delete
    - Add fields: id, inventoryItemId, sellerId, price, currency, minimumOrderQuantity, description, images, status, shippingOptions, category, tags, viewsCount, timestamps
    - Create ListingStatus enum
    - _Requirements: 5.3_
  
  - [ ]* 13.4 Write property test for referential integrity cascade
    - **Property 18: Referential Integrity Cascade**
    - **Validates: Requirements 5.6**

- [ ] 14. Create DAOs for 3-tier asset model
  - [~] 14.1 Create FarmAssetDao
    - Define DAO interface with CRUD operations
    - Add queries for filtering by farmerId, assetType, lifecycleStage
    - Add Flow-based observation methods
    - _Requirements: 5.1, 8.4_
  
  - [~] 14.2 Create InventoryItemDao
    - Define DAO interface with CRUD operations
    - Add queries for filtering by farmerId, farmAssetId
    - Add methods for quantity management
    - _Requirements: 5.2, 8.4_
  
  - [~] 14.3 Create MarketListingDao
    - Define DAO interface with CRUD operations
    - Add queries for filtering by sellerId, status, category
    - Add methods for listing management
    - _Requirements: 5.3, 8.4_
  
  - [ ]* 14.4 Write integration tests for DAO operations
    - Test CRUD operations for all DAOs
    - Test foreign key constraints
    - Test cascade delete behavior
    - _Requirements: 5.6_

- [ ] 15. Implement asset lifecycle transitions
  - [~] 15.1 Create asset transition logic in data:farm
    - Implement method to transition FarmAsset to harvested state
    - Create InventoryItem when asset is harvested
    - Update FarmAsset lifecycleStage
    - _Requirements: 5.4_
  
  - [~] 15.2 Create listing creation logic in data:commerce
    - Implement method to create MarketListing from InventoryItem
    - Validate InventoryItem exists and has available quantity
    - Update InventoryItem reserved quantity
    - _Requirements: 5.5_
  
  - [ ]* 15.3 Write property test for asset transition creates inventory
    - **Property 16: Asset Transition Creates Inventory**
    - **Validates: Requirements 5.4**
  
  - [ ]* 15.4 Write property test for listing references inventory
    - **Property 17: Listing References Inventory**
    - **Validates: Requirements 5.5**

- [ ] 16. Migrate from legacy ProductEntity to 3-tier model
  - [~] 16.1 Create database migration script
    - Define Room migration from current version to new version
    - Map ProductEntity records to appropriate tier (FarmAsset, Inventory, or Listing)
    - Preserve all existing data
    - _Requirements: 5.7, 8.5_
  
  - [~] 16.2 Update repositories to use new entities
    - Update FarmAssetRepository to use FarmAssetEntity
    - Update InventoryRepository to use InventoryItemEntity
    - Update MarketplaceRepository to use MarketListingEntity
    - _Requirements: 5.7_
  
  - [~] 16.3 Remove ProductEntity after migration complete
    - Verify all code uses new entities
    - Delete ProductEntity class and related code
    - _Requirements: 5.7_
  
  - [ ]* 16.4 Write integration test for database migration
    - Test migration from ProductEntity to 3-tier model
    - Verify data integrity after migration
    - Test rollback scenarios
    - _Requirements: 5.7, 8.5_

- [ ] 17. Checkpoint - Verify Phase 3 completion
  - Ensure all three entities are defined with proper relationships
  - Verify foreign key constraints maintain referential integrity
  - Test asset lifecycle transitions work correctly
  - Ensure all tests pass, ask the user if questions arise

### Phase 4: Vertical Feature Migration Waves

#### Wave A: Authentication & Onboarding

- [ ] 18. Migrate authentication features
  - [~] 18.1 Create feature:login module
    - Set up module structure with Compose and Hilt
    - Create LoginScreen, PhoneAuthScreen, OtpVerificationScreen composables
    - Create LoginViewModel with authentication logic
    - Create LoginNavigationProvider and register routes
    - Depend on domain:account for AuthRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 18.2 Create feature:onboarding module
    - Set up module structure with Compose and Hilt
    - Create OnboardingScreen, ProfileSetupScreen, RoleSelectionScreen composables
    - Create OnboardingViewModel
    - Create OnboardingNavigationProvider and register routes
    - Depend on domain:account for UserRepository
    - _Requirements: 6.4, 10.4_
  
  - [ ]* 18.3 Write property test for feature module ownership
    - **Property 10: Feature Module Ownership**
    - **Validates: Requirements 6.4**
  
  - [~] 18.4 Create compatibility adapters for Wave A
    - Create adapters to bridge old app-module code with new feature modules
    - Ensure existing functionality continues to work
    - _Requirements: 6.3, 9.3_

- [ ] 19. Checkpoint - Verify Wave A completion
  - Test login and onboarding flows end-to-end
  - Verify navigation works correctly
  - Ensure application remains shippable
  - Ensure all tests pass, ask the user if questions arise

#### Wave B: Core Farm Management

- [ ] 20. Migrate core farm features
  - [~] 20.1 Create feature:farm-dashboard module
    - Set up module structure with Compose and Hilt
    - Create FarmDashboardScreen with overview and quick actions
    - Create FarmDashboardViewModel
    - Create FarmDashboardNavigationProvider
    - Depend on domain:farm for FarmAssetRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 20.2 Create feature:asset-management module
    - Set up module structure with Compose and Hilt
    - Create AssetListScreen, AssetDetailScreen, AssetCreateScreen, AssetEditScreen
    - Create AssetManagementViewModel
    - Create AssetManagementNavigationProvider
    - Depend on domain:farm for FarmAssetRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 20.3 Create feature:farm-profile module
    - Set up module structure with Compose and Hilt
    - Create FarmProfileScreen, FarmSettingsScreen, FarmVerificationScreen
    - Create FarmProfileViewModel
    - Create FarmProfileNavigationProvider
    - Depend on domain:farm and domain:account
    - _Requirements: 6.4, 10.4_
  
  - [~] 20.4 Create compatibility adapters for Wave B
    - Create adapters to bridge old and new farm management code
    - Ensure existing functionality continues to work
    - _Requirements: 6.3, 9.3_

- [ ] 21. Checkpoint - Verify Wave B completion
  - Test farm dashboard and asset management flows
  - Verify all CRUD operations work correctly
  - Ensure application remains shippable
  - Ensure all tests pass, ask the user if questions arise

#### Wave C: Monitoring & Health

- [ ] 22. Migrate monitoring features
  - [~] 22.1 Create feature:monitoring module
    - Set up module structure with Compose and Hilt
    - Create MonitoringDashboardScreen, DailyLogScreen, TaskListScreen, HealthTrackingScreen
    - Create MonitoringViewModel
    - Create MonitoringNavigationProvider
    - Depend on domain:monitoring for HealthTrackingRepository and TaskRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 22.2 Create feature:breeding module
    - Set up module structure with Compose and Hilt
    - Create BreedingPlanScreen, PedigreeScreen, GeneticsScreen
    - Create BreedingViewModel
    - Create BreedingNavigationProvider
    - Depend on domain:farm for breeding-related repositories
    - _Requirements: 6.4, 10.4_
  
  - [~] 22.3 Create feature:analytics module
    - Set up module structure with Compose and Hilt
    - Create AnalyticsDashboardScreen, ReportsScreen, InsightsScreen
    - Create AnalyticsViewModel
    - Create AnalyticsNavigationProvider
    - Depend on domain:monitoring and domain:farm
    - _Requirements: 6.4, 10.4_
  
  - [~] 22.4 Create compatibility adapters for Wave C
    - Create adapters to bridge old and new monitoring code
    - Ensure existing functionality continues to work
    - _Requirements: 6.3, 9.3_

- [ ] 23. Checkpoint - Verify Wave C completion
  - Test monitoring and health tracking flows
  - Verify breeding and analytics features work
  - Ensure application remains shippable
  - Ensure all tests pass, ask the user if questions arise

#### Wave D: Marketplace & Commerce

- [ ] 24. Migrate marketplace features
  - [~] 24.1 Create feature:marketplace module
    - Set up module structure with Compose and Hilt
    - Create MarketplaceBrowseScreen, ListingDetailScreen, SearchScreen, FilterScreen
    - Create MarketplaceViewModel
    - Create MarketplaceNavigationProvider
    - Depend on domain:commerce for MarketplaceRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 24.2 Create feature:listing-management module
    - Set up module structure with Compose and Hilt
    - Create CreateListingScreen, EditListingScreen, MyListingsScreen, InventoryScreen
    - Create ListingManagementViewModel
    - Create ListingManagementNavigationProvider
    - Depend on domain:commerce and domain:farm
    - _Requirements: 6.4, 10.4_
  
  - [~] 24.3 Create feature:orders module
    - Set up module structure with Compose and Hilt
    - Create OrderListScreen, OrderDetailScreen, OrderTrackingScreen, DisputeScreen
    - Create OrdersViewModel
    - Create OrdersNavigationProvider
    - Depend on domain:commerce for OrderRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 24.4 Create compatibility adapters for Wave D
    - Create adapters to bridge old and new commerce code
    - Ensure existing functionality continues to work
    - _Requirements: 6.3, 9.3_

- [ ] 25. Checkpoint - Verify Wave D completion
  - Test marketplace browsing and listing creation
  - Verify order management flows work correctly
  - Ensure application remains shippable
  - Ensure all tests pass, ask the user if questions arise

#### Wave E: Social & Community

- [ ] 26. Migrate social features
  - [~] 26.1 Create feature:social-feed module
    - Set up module structure with Compose and Hilt
    - Create SocialFeedScreen, PostDetailScreen, CreatePostScreen, StoriesScreen
    - Create SocialFeedViewModel
    - Create SocialFeedNavigationProvider
    - Depend on domain:social for SocialFeedRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 26.2 Create feature:community module
    - Set up module structure with Compose and Hilt
    - Create CommunityScreen, GroupListScreen, GroupDetailScreen, EventsScreen
    - Create CommunityViewModel
    - Create CommunityNavigationProvider
    - Depend on domain:social for community repositories
    - _Requirements: 6.4, 10.4_
  
  - [~] 26.3 Create feature:messaging module
    - Set up module structure with Compose and Hilt
    - Create ChatListScreen, ChatScreen, NotificationsScreen
    - Create MessagingViewModel
    - Create MessagingNavigationProvider
    - Depend on domain:social for MessagingRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 26.4 Create compatibility adapters for Wave E
    - Create adapters to bridge old and new social code
    - Ensure existing functionality continues to work
    - _Requirements: 6.3, 9.3_

- [ ] 27. Checkpoint - Verify Wave E completion
  - Test social feed and community features
  - Verify messaging functionality works correctly
  - Ensure application remains shippable
  - Ensure all tests pass, ask the user if questions arise

#### Wave F: Admin & Support

- [ ] 28. Migrate admin features
  - [~] 28.1 Create feature:admin-dashboard module
    - Set up module structure with Compose and Hilt
    - Create AdminDashboardScreen, MetricsScreen, UserManagementScreen
    - Create AdminDashboardViewModel
    - Create AdminDashboardNavigationProvider
    - Depend on domain:admin for AdminRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 28.2 Create feature:moderation module
    - Set up module structure with Compose and Hilt
    - Create ModerationQueueScreen, ContentReviewScreen, UserActionsScreen
    - Create ModerationViewModel
    - Create ModerationNavigationProvider
    - Depend on domain:admin for ModerationRepository
    - _Requirements: 6.4, 10.4_
  
  - [~] 28.3 Create feature:support module
    - Set up module structure with Compose and Hilt
    - Create HelpScreen, FeedbackScreen, SettingsScreen
    - Create SupportViewModel
    - Create SupportNavigationProvider
    - Depend on domain:account and domain:admin
    - _Requirements: 6.4, 10.4_
  
  - [~] 28.4 Create compatibility adapters for Wave F
    - Create adapters to bridge old and new admin code
    - Ensure existing functionality continues to work
    - _Requirements: 6.3, 9.3_

- [ ] 29. Checkpoint - Verify Wave F completion
  - Test admin dashboard and moderation features
  - Verify support and settings functionality works
  - Ensure application remains shippable
  - Ensure all tests pass, ask the user if questions arise

- [ ] 30. Verify Phase 4 completion
  - [~] 30.1 Test all feature modules independently
    - Verify each feature module builds independently
    - Test navigation between all features
    - Verify no feature-to-feature dependencies exist
    - _Requirements: 6.2, 9.4_
  
  - [ ]* 30.2 Write property test for module migration independence
    - **Property 26: Module Migration Independence**
    - **Validates: Requirements 9.4**
  
  - [ ]* 30.3 Write property test for backward compatibility
    - **Property 27: Backward Compatibility During Migration**
    - **Validates: Requirements 9.3**

### Phase 5: App Shell Reduction

- [ ] 31. Remove all feature code from app module
  - [~] 31.1 Remove all compatibility adapters
    - Identify all compatibility adapter classes
    - Verify all features are fully migrated
    - Delete adapter classes
    - Update references to use feature modules directly
    - _Requirements: 6.5, 7.1_
  
  - [~] 31.2 Remove empty packages and dead code
    - Identify empty packages in app module
    - Remove unused imports and classes
    - Clean up DI modules
    - _Requirements: 7.1_
  
  - [~] 31.3 Verify app module contains only shell code
    - Audit app module for any remaining feature code
    - Ensure only Application, MainActivity, and root navigation remain
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ] 32. Optimize app module build configuration
  - [~] 32.1 Update app module dependencies
    - Remove all data module dependencies
    - Remove all domain module dependencies
    - Keep only feature module and core module dependencies
    - _Requirements: 7.5_
  
  - [~] 32.2 Verify app module size target
    - Measure total lines of code in app module
    - Ensure app module is under 2000 lines
    - _Requirements: 7.6_
  
  - [ ]* 32.3 Write property test for app shell dependency constraint
    - **Property 2: App Shell Dependency Constraint**
    - **Validates: Requirements 3.5, 7.5**

- [ ] 33. Final architecture validation
  - [~] 33.1 Run all architecture tests
    - Execute ModularArchitectureTest suite
    - Verify all dependency rules pass
    - Verify naming conventions are followed
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_
  
  - [ ]* 33.2 Write property tests for module naming conventions
    - **Property 19: Core Module Naming Convention**
    - **Property 20: Domain Module Naming Convention**
    - **Property 21: Data Module Naming Convention**
    - **Property 22: Feature Module Naming Convention**
    - **Validates: Requirements 11.1, 11.2, 11.3, 11.4, 11.5**
  
  - [ ]* 33.3 Write property tests for infrastructure integration
    - **Property 23: Hilt Usage Consistency**
    - **Property 24: WorkManager Worker Location**
    - **Property 25: Compose UI Consistency**
    - **Validates: Requirements 10.1, 10.4, 10.6**
  
  - [ ]* 33.4 Write property tests for database properties
    - **Property 28: DAO Interface Availability**
    - **Property 29: Database Migration Centralization**
    - **Validates: Requirements 8.4, 8.5**

- [ ] 34. Final checkpoint - Verify Phase 5 completion
  - Ensure app module contains zero feature code
  - Verify all architecture tests pass
  - Test complete application end-to-end
  - Measure and document final metrics
  - Ensure all tests pass, ask the user if questions arise

## Notes

- Tasks marked with `*` are optional property-based tests and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at phase boundaries
- Property tests validate universal correctness properties across all inputs
- Unit tests validate specific examples and edge cases
- The modularization maintains a shippable application after each phase
- Compatibility adapters enable gradual migration without breaking existing functionality
- Architecture tests enforce module boundaries and prevent regression
- All feature modules follow the pattern: feature:{feature-name}
- All domain modules follow the pattern: domain:{business-area}
- All data modules follow the pattern: data:{business-area}
- All core modules follow the pattern: core:{capability}
