---
Version: 4.2
Last Updated: 2025-12-29
Audience: Developers
Status: Active
---

# ROSTRY Codebase Structure

**Document Type**: Developer Reference  
**Version**: 4.2  
**Last Updated**: 2025-12-29  
**Purpose**: Comprehensive code navigation guide for developers

---

## Overview

This document provides a complete map of the ROSTRY codebase, organized by architectural layer and feature domain. Use this as your primary reference for code navigation.

---

## Root Structure

```
ROSTRY/
├── app/                          # Android application module
├── docs/                         # Documentation (82 files)
├── firebase/                     # Firebase configuration
├── .github/                      # CI/CD workflows
├── gradle/                       # Gradle wrapper
├── README.md                     # Project overview
├── SYSTEM_BLUEPRINT.md           # Complete system reference
├── CHANGELOG.md                  # Release history
├── CODE_STYLE.md                 # Coding standards
├── CONTRIBUTING.md               # Contribution guide
├── SECURITY.md                   # Security policy
├── ROADMAP.md                    # Future plans
└── firestore.rules               # Firestore security rules
```

---

## Application Module (`app/src/main/java/com/rio/rostry/`)

### Entry Points

| File | Purpose | Key Responsibilities |
|------|---------|---------------------|
| `RostryApp.kt` | Application class | Hilt, Timber, WorkManager, Firebase init |
| `MainActivity.kt` | Single Activity | NavHost, theme, session bootstrap |

---

## Presentation Layer (`ui/`)

### Navigation (`ui/navigation/`)

| File | Purpose |
|------|---------|
| `Routes.kt` | All route definitions, deep links |
| `AppNavHost.kt` | Navigation graph, composable destinations |
| `NavExtensions.kt` | Navigation utility functions |
| `EvidenceOrderNavGraph.kt` | Order sub-navigation |

### Theme (`ui/theme/`)

| File | Purpose |
|------|---------|
| `Theme.kt` | Material 3 theme configuration |
| `Color.kt` | Color palette |
| `Typography.kt` | Text styles |
| `Shapes.kt` | Shape definitions |

### Components (`ui/components/`)

Reusable UI components (22 files):

| Component | Description |
|-----------|-------------|
| `LoadingIndicator.kt` | Loading states |
| `ErrorView.kt` | Error display |
| `EmptyState.kt` | Empty content |
| `ProductCard.kt` | Product display card |
| `SearchBar.kt` | Search input |
| `FilterChips.kt` | Filter selection |
| `BirdSelectionSheet.kt` | Bird/batch selection |
| `StatCard.kt` | Statistics display |
| `Timeline.kt` | Event timeline |
| `WizardStepper.kt` | Multi-step wizard |

### Feature Screens

#### Authentication (`ui/auth/`)
```
ui/auth/
├── AuthViewModel.kt
├── AuthWelcomeScreen.kt
├── AuthWelcomeViewModel.kt
├── OtpVerificationScreenNew.kt
├── PhoneAuthScreenNew.kt
└── ...
```

#### Farmer Features (`ui/farmer/`)
```
ui/farmer/
├── FarmerHomeScreen.kt
├── FarmerHomeViewModel.kt
├── FarmerMarketScreen.kt
├── FarmerCreateScreen.kt
├── FarmerCreateViewModel.kt
├── FarmerCommunityScreen.kt
├── FarmerProfileScreen.kt
├── DigitalFarmScreen.kt
├── FarmLogScreen.kt
├── ComplianceScreen.kt
├── asset/
│   ├── FarmAssetListScreen.kt
│   ├── FarmAssetDetailScreen.kt
│   ├── FarmAssetListViewModel.kt
│   ├── FarmAssetDetailViewModel.kt
│   ├── BirdHistoryScreen.kt
│   └── BirdHistoryViewModel.kt
├── listing/
│   ├── CreateListingScreen.kt
│   └── CreateListingViewModel.kt
├── verification/
│   ├── FarmVerificationScreen.kt
│   └── FarmVerificationViewModel.kt
└── ...
```

#### Enthusiast Features (`ui/enthusiast/`)
```
ui/enthusiast/
├── EnthusiastHomeScreen.kt
├── EnthusiastHomeViewModel.kt
├── EnthusiastExploreScreen.kt
├── EnthusiastExploreViewModel.kt
├── EnthusiastCreateScreen.kt
├── EnthusiastTransfersScreen.kt
├── EnthusiastFlockViewModel.kt
├── digitalfarm/
│   ├── DigitalFarmScreen.kt          # Canvas-based farm visualization
│   ├── DigitalFarmViewModel.kt       # Zone grouping, weather/time state
│   └── PipelineViewModel.kt          # Digital farm pipeline processing
├── breeding/
│   ├── BreedingFlowScreen.kt
│   ├── BreedingCalculatorScreen.kt
│   ├── BreedingCalculatorViewModel.kt
│   ├── EggCollectionScreen.kt
│   └── EggCollectionViewModel.kt
├── dashboard/
├── pedigree/
│   ├── PedigreeScreen.kt
│   └── PedigreeViewModel.kt
├── cards/
│   ├── RoosterCardScreen.kt
│   └── RoosterCardViewModel.kt
├── showcase/
│   ├── ShowcaseCardPreviewScreen.kt
│   └── ShowcaseCardViewModel.kt
├── journal/
│   ├── PerformanceJournalScreen.kt
│   └── PerformanceJournalViewModel.kt
├── arena/
│   ├── VirtualArenaScreen.kt
│   └── VirtualArenaViewModel.kt
├── halloffame/
│   ├── HallOfFameScreen.kt
│   └── HallOfFameViewModel.kt
├── hatchability/
│   ├── HatchabilityTrackerScreen.kt
│   └── HatchabilityAnalysisScreen.kt
├── showrecords/
│   ├── ShowRecordsScreen.kt
│   └── ShowRecordsViewModel.kt
├── transfer/
│   ├── TransferCodeScreen.kt
│   ├── ClaimTransferScreen.kt
│   └── TransferCodeViewModel.kt
└── ...
```

#### Monitoring (`ui/monitoring/`)
```
ui/monitoring/
├── FarmMonitoringScreen.kt
├── FarmMonitoringViewModel.kt
├── VaccinationScheduleScreen.kt
├── VaccinationViewModel.kt
├── VaccinationDetailScreen.kt
├── VaccinationDetailViewModel.kt
├── GrowthTrackingScreen.kt
├── GrowthViewModel.kt
├── GrowthRecordDetailScreen.kt
├── GrowthRecordDetailViewModel.kt
├── MortalityTrackingScreen.kt
├── MortalityViewModel.kt
├── MortalityDetailScreen.kt
├── MortalityDetailViewModel.kt
├── QuarantineManagementScreen.kt
├── QuarantineViewModel.kt
├── BreedingManagementScreen.kt
├── BreedingManagementViewModel.kt
├── HatchingProcessScreen.kt
├── HatchingViewModel.kt
├── FarmPerformanceScreen.kt
├── DailyLogScreen.kt
├── DailyLogViewModel.kt
├── TasksScreen.kt
├── TasksViewModel.kt
├── FCRCalculatorScreen.kt
├── FCRCalculatorViewModel.kt
├── BatchSplitViewModel.kt
├── BatchHierarchyViewModel.kt
└── ...
```

#### Social (`ui/social/`)
```
ui/social/
├── SocialFeedScreen.kt
├── SocialFeedViewModel.kt
├── LeaderboardScreen.kt
├── LeaderboardViewModel.kt
├── LiveBroadcastScreen.kt
├── profile/
│   ├── SocialProfileScreen.kt
│   └── SocialProfileViewModel.kt
├── stories/
│   ├── StoryViewerScreen.kt
│   ├── StoryCreatorScreen.kt
│   └── StoryCreatorViewModel.kt
├── discussion/
│   ├── DiscussionDetailScreen.kt
│   └── DiscussionDetailViewModel.kt
└── ...
```

#### Orders (`ui/order/`)
```
ui/order/
├── OrderTrackingScreen.kt
├── OrderTrackingViewModel.kt
├── evidence/
│   ├── MyOrdersScreen.kt
│   ├── EvidenceOrderViewModel.kt
│   └── MyOrdersViewModel.kt
└── ...
```

#### Analytics (`ui/analytics/`)
```
ui/analytics/
├── GeneralDashboardScreen.kt
├── GeneralDashboardViewModel.kt
├── FarmerDashboardScreen.kt
├── FarmerDashboardViewModel.kt
├── EnthusiastDashboardScreen.kt
├── EnthusiastDashboardViewModel.kt
├── ReportsScreen.kt
├── ReportsViewModel.kt
├── MonthlyReportScreen.kt
├── MonthlyReportViewModel.kt
└── ...
```

#### Traceability (`ui/traceability/`)
```
ui/traceability/
├── TraceabilityScreen.kt
├── TraceabilityViewModel.kt
├── LineagePreviewScreen.kt
├── LineagePreviewViewModel.kt
├── FamilyTreeView.kt
└── NodeEventTimelineSheet.kt
```

#### Verification (`ui/verification/`)
```
ui/verification/
├── FarmerLocationVerificationScreen.kt
├── VerificationViewModel.kt
├── EnthusiastVerificationScreen.kt
├── EnthusiastVerificationViewModel.kt
└── ...
```

#### Admin Features (`ui/admin/`)
```
ui/admin/
├── AdminVerificationScreen.kt        # Admin Dashboard for KYC reviews
├── AdminVerificationViewModel.kt     # Verification logic and approval flow
└── ...
```

#### Transfer Features (`ui/transfer/`)
```
ui/transfer/
├── TransferDetailsScreen.kt
├── TransferDetailsViewModel.kt
├── TransferVerificationScreen.kt
├── TransferVerificationViewModel.kt
├── TransferCreateScreen.kt
├── TransferCreateViewModel.kt
└── ...
```

#### Marketplace (`ui/marketplace/`)
```
ui/marketplace/
├── MarketplaceSandboxScreen.kt
├── MarketplaceSandboxViewModel.kt
└── ...
```

#### Product (`ui/product/`)
```
ui/product/
├── ProductDetailsScreen.kt
├── ProductDetailsViewModel.kt
└── ...
```

#### General (`ui/general/`)
```
ui/general/
├── GeneralHomeScreen.kt
├── GeneralHomeViewModel.kt
├── GeneralHomeViewModel.kt
├── cart/
│   ├── GeneralCartScreen.kt
│   └── GeneralCartViewModel.kt
├── market/
│   ├── GeneralMarketScreen.kt
│   └── GeneralMarketViewModel.kt
├── explore/
│   ├── GeneralExploreScreen.kt
│   └── GeneralExploreViewModel.kt
├── wishlist/
│   ├── WishlistScreen.kt
│   └── WishlistViewModel.kt
├── profile/
│   ├── GeneralProfileScreen.kt
│   └── GeneralProfileViewModel.kt
└── ...
```

#### Settings (`ui/settings/`)
```
ui/settings/
├── SettingsScreen.kt
├── SettingsViewModel.kt
├── BackupRestoreScreen.kt
├── BackupRestoreViewModel.kt
├── AddressSelectionWebViewScreen.kt
└── ...
```

#### Messaging (`ui/messaging/`)
```
ui/messaging/
├── ThreadScreen.kt
├── ThreadViewModel.kt
├── GroupChatScreen.kt
└── GroupChatViewModel.kt
```

#### Notifications (`ui/notifications/`)
```
ui/notifications/
├── NotificationsScreen.kt
├── NotificationsViewModel.kt
└── ...
```

#### Components (`ui/components/`)
```
ui/components/
├── SyncStatusIndicator.kt
├── SyncStatusViewModel.kt
├── OfflineBanner.kt
├── LoadingIndicator.kt
├── ErrorView.kt
├── EmptyState.kt
├── ProductCard.kt
├── SearchBar.kt
├── FilterChips.kt
├── BirdSelectionSheet.kt
├── StatCard.kt
├── Timeline.kt
├── WizardStepper.kt
└── ...
```

#### Onboarding (`ui/onboarding/`)
```
ui/onboarding/
├── OnboardFarmBirdScreen.kt
├── OnboardFarmBirdViewModel.kt
├── OnboardFarmBatchScreen.kt
├── OnboardFarmBatchViewModel.kt
├── OnboardingScreen.kt
├── OnboardingViewModel.kt
├── OnboardingChecklistViewModel.kt
└── ...
```

#### Auction (`ui/auction/`)
```
ui/auction/
├── AuctionScreen.kt
├── AuctionViewModel.kt
├── CreateAuctionScreen.kt
└── CreateAuctionViewModel.kt
```

#### Events (`ui/events/`)
```
ui/events/
├── EventsScreen.kt
└── EventsViewModel.kt
```

#### Expert (`ui/expert/`)
```
ui/expert/
├── ExpertBookingScreen.kt
└── ExpertViewModel.kt
```

#### Moderation (`ui/moderation/`)
```
ui/moderation/
├── ModerationScreen.kt
└── ModerationViewModel.kt
```

#### Gamification (`ui/gamification/`)
```
ui/gamification/
├── AchievementsScreen.kt
└── AchievementsViewModel.kt
```

#### Insights (`ui/insights/`)
```
ui/insights/
├── InsightsScreen.kt
└── InsightsViewModel.kt
```

#### Feedback (`ui/feedback/`)
```
ui/feedback/
├── FeedbackScreen.kt
└── FeedbackViewModel.kt
```

#### Support (`ui/support/`)
```
ui/support/
├── HelpScreen.kt
└── ...
```

#### Community (`ui/community/`)
```
ui/community/
├── CommunityHubScreen.kt
└── CommunityHubViewModel.kt
```

#### Search (`ui/search/`)
```
ui/search/
├── ProductSearchScreen.kt
└── ProductSearchViewModel.kt
```

#### Farm (`ui/farm/`)
```
ui/farm/
├── FarmActivityDetailScreen.kt
└── FarmActivityDetailViewModel.kt
```

#### IoT (`ui/iot/`)
```
ui/iot/
├── IoTDataScreen.kt
└── ...
```

#### Accessibility (`ui/accessibility/`)
```
ui/accessibility/
├── AccessibilitySettingsScreen.kt
└── ...
```

#### Main (`ui/main/`)
```
ui/main/
├── MainViewModel.kt
└── ...
```

#### Profile (`ui/profile/`)
```
ui/profile/
├── ProfileScreen.kt
├── ProfileViewModel.kt
├── EditProfileScreen.kt
├── ProfileEditViewModel.kt
├── StorageQuotaScreen.kt
└── StorageQuotaViewModel.kt
```

#### Scan (`ui/scan/`)
```
ui/scan/
├── QrScannerScreen.kt
└── QrScannerViewModel.kt
```

#### Session (`ui/session/`)
```
ui/session/
├── SessionViewModel.kt
└── ...
```

#### Splash (`ui/splash/`)
```
ui/splash/
├── SplashScreen.kt
└── ...
```

#### Start (`ui/start/`)
```
ui/start/
├── StartScreen.kt
└── StartViewModel.kt
```

#### Sync (`ui/sync/`)
```
ui/sync/
├── SyncIssuesScreen.kt
└── ...
```

#### Theme (`ui/theme/`)
```
ui/theme/
├── Theme.kt
├── Color.kt
├── Typography.kt
└── Shapes.kt
```

#### Utils (`ui/utils/`)
```
ui/utils/
├── Extensions.kt
└── ...
```

#### Animations (`ui/animations/`)
```
ui/animations/
├── AnimationUtils.kt
└── ...
```

---

## Domain Layer (`domain/`)

### Models (`domain/model/`)

| File | Contents |
|------|----------|
| `User.kt` | User domain models |
| `Product.kt` | Product domain models |
| `Order.kt` | Order domain models |
| `Transfer.kt` | Transfer domain models |
| `DigitalFarmModels.kt` | Farm visualization models (~510 lines) |
| `SocialModels.kt` | Social platform models |
| `AnalyticsModels.kt` | Analytics domain models |

### Use Cases (`domain/usecase/`)

| File | Purpose |
|------|---------|
| `AuthUseCase.kt` | Authentication logic |
| `ProductUseCase.kt` | Product operations |

### Auth (`domain/auth/`)

| File | Purpose |
|------|---------|
| `AuthState.kt` | Authentication state |
| `AuthResult.kt` | Auth operation results |
| `Credentials.kt` | User credentials |

### RBAC (`domain/rbac/`)

| File | Purpose |
|------|---------|
| `Permission.kt` | Permission definitions |
| `RolePermissions.kt` | Role-permission mapping |

---

## Data Layer (`data/`)

### Database (`data/database/`)

#### Entities (`data/database/entity/`)

| File | Tables |
|------|--------|
| `UserEntity.kt` | users |
| `ProductEntity.kt` | products (60+ fields) |
| `OrderEntity.kt` | orders |
| `SocialEntities.kt` | posts, comments, follows, messages |
| `EvidenceOrderEntities.kt` | order_quotes, order_payments, delivery_confirmations, order_evidence, order_disputes, order_audit_logs |
| `FarmAssetEntity.kt` | farm_assets |
| `VaccinationRecordEntity.kt` | vaccination_records |
| `DailyLogEntity.kt` | daily_logs |
| `TaskEntity.kt` | tasks |
| `FamilyTreeEntity.kt` | family_trees |
| `TransferEntity.kt` | transfers |
| `AnalyticsEntities.kt` | analytics tables |
| `CommunityEntities.kt` | community features |
| `AuctionEntity.kt` | auctions |
| `BidEntity.kt` | bids |
| `CartItemEntity.kt` | cart_items |
| `ChatMessageEntity.kt` | chat_messages |
| `CoinEntity.kt` | coins |
| `CoinLedgerEntity.kt` | coin_ledger |
| `CompetitionEntryEntity.kt` | competition_entries |
| `DailyBirdLogEntity.kt` | daily_bird_logs |
| `DashboardCacheEntity.kt` | dashboard_cache |
| `DeliveryHubEntity.kt` | delivery_hubs |
| `EnthusiastBreedingEntities.kt` | enthusiast breeding tables |
| `EnthusiastVerificationEntity.kt` | enthusiast_verification |
| `EventRsvpEntity.kt` | event_rsvps |
| `FamilyTreeEntity.kt` | family_trees |
| `FarmActivityLogEntity.kt` | farm_activity_logs |
| `FarmMonitoringEntities.kt` | farm monitoring tables |
| `FarmProfileEntity.kt` | farm_profiles |
| `FarmTimelineEventEntity.kt` | farm_timeline_events |
| `FarmVerificationEntity.kt` | farm_verification |
| `GamificationEntities.kt` | gamification tables |
| `GeneticAnalysisEntity.kt` | genetic_analysis |
| `InventoryItemEntity.kt` | inventory_items |
| `InvoiceEntities.kt` | invoice tables |
| `IoTDataEntity.kt` | iot_data |
| `IoTDeviceEntity.kt` | iot_devices |
| `MarketListingEntity.kt` | market_listings |
| `MyVotesEntity.kt` | my_votes |
| `NewFarmMonitoringEntities.kt` | new farm monitoring tables |
| `NotificationEntity.kt` | notifications |
| `OrderItemEntity.kt` | order_items |
| `OrderTrackingEventEntity.kt` | order_tracking_events |
| `OutboxEntity.kt` | outbox |
| `PaymentEntity.kt` | payments |
| `ProductFtsEntity.kt` | product_fts (full-text search) |
| `ProductTrackingEntity.kt` | product_tracking |
| `RateLimitEntity.kt` | rate_limits |
| `RefundEntity.kt` | refunds |
| `ReviewEntity.kt` | reviews |
| `RoleMigrationEntity.kt` | role_migrations |
| `ShowRecordEntity.kt` | show_records |
| `StorageQuotaEntity.kt` | storage_quota |
| `SyncStateEntity.kt` | sync_state |
| `TraceabilityEntities.kt` | traceability tables |
| `TransferWorkflowEntities.kt` | transfer_workflow tables |
| `UploadTaskEntity.kt` | upload_tasks |
| `VerificationDraftEntity.kt` | verification_drafts |
| `VerificationRequestEntity.kt` | verification_requests |
| `WishlistEntity.kt` | wishlists |
| `BreedEntity.kt` | breeds |
| `BreedingPairEntity.kt` | breeding_pairs |
| `BatchSummaryEntity.kt` | batch_summaries |

#### DAOs (`data/database/dao/`)

| File | Operations |
|------|------------|
| `UserDao.kt` | User CRUD |
| `ProductDao.kt` | Product queries |
| `OrderDao.kt` | Order management |
| `SocialDaos.kt` | Social platform |
| `EvidenceOrderDaos.kt` | Evidence-based orders |
| `FarmAssetDao.kt` | Farm assets |
| `VaccinationDao.kt` | Vaccination records |
| `DailyLogDao.kt` | Daily logs |
| `TaskDao.kt` | Task management |
| `AnalyticsDaos.kt` | Analytics queries |

#### Database Configuration

| File | Purpose |
|------|---------|
| `AppDatabase.kt` | Database definition, migrations (v2→65), schemas in `app/schemas` |
| `Converters.kt` | Type converters |

### Repositories (`data/repository/`)

57+ repository implementations across multiple subdirectories:

| Repository | Domain |
|------------|--------|
| `AuthRepositoryImpl.kt` | Authentication |
| `UserRepositoryImpl.kt` | User management |
| `ProductRepositoryImpl.kt` | Products |
| `OrderRepositoryImpl.kt` | Orders |
| `EvidenceOrderRepositoryImpl.kt` | Evidence-based orders |
| `SocialRepositoryImpl.kt` | Social platform |
| `TransferWorkflowRepositoryImpl.kt` | Ownership transfers |
| `TransferRepositoryImpl.kt` | Transfer operations |
| `FarmAssetRepositoryImpl.kt` | Farm assets |
| `VaccinationRepositoryImpl.kt` | Vaccinations |
| `AnalyticsRepositoryImpl.kt` | Analytics |
| `TraceabilityRepositoryImpl.kt` | Lineage tracking |
| `GamificationRepositoryImpl.kt` | Achievements |
| `BreedingRepositoryImpl.kt` | Breeding management |
| `CommunityRepositoryImpl.kt` | Community features |
| `AuctionRepositoryImpl.kt` | Auction functionality |
| `CartRepositoryImpl.kt` | Shopping cart |
| `ChatRepositoryImpl.kt` | Chat messaging |
| `CoinRepositoryImpl.kt` | Coin wallet system |
| `EnthusiastBreedingRepositoryImpl.kt` | Enthusiast breeding |
| `EnthusiastVerificationRepositoryImpl.kt` | Enthusiast verification |
| `FamilyTreeRepositoryImpl.kt` | Family tree management |
| `FarmActivityLogRepositoryImpl.kt` | Farm activity logs |
| `FarmVerificationRepositoryImpl.kt` | Farm verification |
| `FeedbackRepositoryImpl.kt` | Feedback system |
| `HatchabilityRepository.kt` | Hatchability tracking |
| `InventoryRepositoryImpl.kt` | Inventory management |
| `InvoiceRepositoryImpl.kt` | Invoice management |
| `LikesRepositoryImpl.kt` | Social likes |
| `LogisticsRepositoryImpl.kt` | Logistics |
| `MarketListingRepositoryImpl.kt` | Market listings |
| `PaymentRepositoryImpl.kt` | Payment processing |
| `ProductMarketplaceRepositoryImpl.kt` | Product marketplace |
| `ReviewRepositoryImpl.kt` | Reviews and ratings |
| `SaleCompletionService.kt` | Sale completion |
| `StorageRepositoryImpl.kt` | Storage management |
| `StorageUsageRepositoryImpl.kt` | Storage usage tracking |
| `TrackingRepositoryImpl.kt` | Tracking |
| `VerificationDraftRepositoryImpl.kt` | Verification drafts |
| `VirtualArenaRepositoryImpl.kt` | Virtual arena |
| `WeatherRepositoryImpl.kt` | Weather data |
| `WishlistRepositoryImpl.kt` | Wishlist |
| `BirdHealthRepositoryImpl.kt` | Bird health |
| `FarmFinancialsRepositoryImpl.kt` | Farm financials |
| `FarmOnboardingRepositoryImpl.kt` | Farm onboarding |
| `RoleUpgradeMigrationRepositoryImpl.kt` | Role upgrade migrations |
| `OnboardingChecklistRepositoryImpl.kt` | Onboarding checklists |
| `OrderManagementRepositoryImpl.kt` | Order management |
| `ReportGenerationRepositoryImpl.kt` | Report generation |
| `TaskRepositoryImpl.kt` | Task management |
| `DailyLogRepositoryImpl.kt` | Daily logs |
| `GrowthRepositoryImpl.kt` | Growth tracking |
| `MortalityRepositoryImpl.kt` | Mortality tracking |
| `QuarantineRepositoryImpl.kt` | Quarantine management |
| `HatchingRepositoryImpl.kt` | Hatching process |
| `FarmPerformanceRepositoryImpl.kt` | Farm performance |
| `MessagingRepositoryImpl.kt` | Messaging |
| `PedigreeRepositoryImpl.kt` | Pedigree tracking |
| `BreedRepositoryImpl.kt` | Breed information |

#### Monitoring Repositories (`data/repository/monitoring/`)
| Repository | Domain |
|------------|--------|
| `DailyLogRepositoryImpl.kt` | Daily logs |
| `FarmOnboardingRepositoryImpl.kt` | Farm onboarding |
| `TaskRepositoryImpl.kt` | Task management |
| `GrowthRepositoryImpl.kt` | Growth tracking |
| `VaccinationRepositoryImpl.kt` | Vaccination tracking |
| `MortalityRepositoryImpl.kt` | Mortality tracking |
| `QuarantineRepositoryImpl.kt` | Quarantine management |
| `HatchingRepositoryImpl.kt` | Hatching process |
| `FarmPerformanceRepositoryImpl.kt` | Farm performance |
| `BreedingRepositoryImpl.kt` | Breeding management |
| `FarmAlertRepositoryImpl.kt` | Farm alerts |
| `ListingDraftRepositoryImpl.kt` | Listing drafts |
| `FarmerDashboardRepositoryImpl.kt` | Farmer dashboard |

#### Social Repositories (`data/repository/social/`)
| Repository | Domain |
|------------|--------|
| `SocialRepositoryImpl.kt` | Social platform |
| `MessagingRepositoryImpl.kt` | Messaging |
| `RepositoriesSocial.kt` | Social repositories |

#### Enthusiast Repositories (`data/repository/enthusiast/`)
| Repository | Domain |
|------------|--------|
| `EnthusiastBreedingRepositoryImpl.kt` | Enthusiast breeding features |

### Auth (`data/auth/`)

| File | Purpose |
|------|---------|
| `AuthRepositoryImpl.kt` | Firebase Auth integration |
| `PhoneAuthManager.kt` | Phone verification |
| `SessionStore.kt` | Session persistence |

### Sync (`data/sync/`)

| File | Purpose |
|------|---------|
| `SyncManager.kt` | Sync orchestration |
| `Outbox.kt` | Offline queue |

---

## Dependency Injection (`di/`)

21 Hilt modules:

| Module | Bindings |
|--------|----------|
| `AnalyticsModule.kt` | Analytics and reporting dependencies |
| `AppEntryPoints.kt` | Hilt entry points for application components |
| `AppModule.kt` | App-level singletons |
| `AuthModuleNew.kt` | Authentication-related dependencies |
| `CoilModule.kt` | Image loading dependencies |
| `DatabaseModule.kt` | Room database, DAOs, migrations |
| `HttpModule.kt` | HTTP client dependencies |
| `LocationModule.kt` | Location services dependencies |
| `LoveabilityModule.kt` | Gamification and engagement dependencies |
| `MediaUploadInitializer.kt` | Media upload initialization |
| `NetworkModule.kt` | Network client dependencies |
| `NotifModule.kt` | Notification dependencies |
| `PlacesModule.kt` | Google Places API dependencies |
| `RemoteModule.kt` | Remote data source dependencies |
| `RepositoryModule.kt` | Repository bindings |
| `SessionModule.kt` | Session management dependencies |
| `UpgradeModule.kt` | Role upgrade dependencies |
| `UtilsModule.kt` | Utility dependencies |
| `VerificationModule.kt` | Verification dependencies |
| `ViewModelModule.kt` | ViewModel dependencies |
| `WorkerBaseHelper.kt` | Worker base helper dependencies |

---

## Workers (`workers/`)

30+ background workers:

| Worker | Schedule | Purpose |
|--------|----------|---------|
| `SyncWorker.kt` | 8 hours | Room/Firebase sync (reduced from 6h for quota optimization) |
| `OutboxSyncWorker.kt` | On connectivity | Pending uploads and batched operations |
| `PullSyncWorker.kt` | On demand | Pull remote changes |
| `FarmMonitoringWorker.kt` | Daily | Health checks |
| `VaccinationReminderWorker.kt` | Daily | Vaccine reminders |
| `LifecycleWorker.kt` | Daily | Milestone reminders |
| `ModerationWorker.kt` | Periodic | Content scanning |
| `OutgoingMessageWorker.kt` | On demand | Message delivery (now replaced by OutboxSyncWorker) |
| `TransferTimeoutWorker.kt` | Periodic | SLA enforcement |
| `AnalyticsAggregationWorker.kt` | Daily | Metrics aggregation |
| `MediaUploadWorker.kt` | On demand | Media uploads |
| `EvidenceOrderWorker.kt` | On demand | Quote expiry, payment reminders, delivery confirmations |
| `PrefetchWorker.kt` | Conditional | Content caching |
| `CommunityEngagementWorker.kt` | 12 hours | Recommendations |
| `QuarantineReminderWorker.kt` | Daily | Health monitoring alerts |
| `PersonalizationWorker.kt` | Periodic | AI-driven recommendations |
| `StorageQuotaMonitorWorker.kt` | Periodic | Storage usage tracking |
| `AuctionCloserWorker.kt` | Periodic | Automatic auction completion |
| `AutoBackupWorker.kt` | Daily | Automatic data backup |
| `DatabaseMaintenanceWorker.kt` | Weekly | Database optimization |
| `RoleUpgradeMigrationWorker.kt` | On demand | Role-based data migration |
| `VerificationUploadWorker.kt` | On demand | Verification document processing |
| `ReportingWorker.kt` | Daily | Report generation |
| `FarmPerformanceWorker.kt` | Daily | Farm performance aggregation |
| `OrderStatusWorker.kt` | Periodic | Order status updates |
| `LegacyProductMigrationWorker.kt` | One-time | Migration from old architecture |
| `EnthusiastPerformanceWorker.kt` | Daily | Enthusiast performance metrics |
| `NotificationFlushWorker.kt` | On demand | Batch notification delivery |

---

## Utilities (`utils/`)

48 utility files:

### Validation
| File | Purpose |
|------|---------|
| `ValidationUtils.kt` | Input validation |
| `PhoneValidator.kt` | Phone number validation |
| `EmailValidator.kt` | Email validation |

### Media
| File | Purpose |
|------|---------|
| `ImageCompressor.kt` | Image compression |
| `MediaUploadManager.kt` | Upload orchestration |
| `ThumbnailGenerator.kt` | Thumbnail creation |

### Export
| File | Purpose |
|------|---------|
| `CsvExporter.kt` | CSV generation |
| `PdfExporter.kt` | PDF generation |
| `ExportManager.kt` | Export orchestration |

### Common
| File | Purpose |
|------|---------|
| `DateUtils.kt` | Date formatting |
| `CurrencyUtils.kt` | Currency formatting |
| `NetworkUtils.kt` | Connectivity checks |
| `Extensions.kt` | Kotlin extensions |

---

## Security (`security/`)

| File | Purpose |
|------|---------|
| `RootDetector.kt` | Device compromise detection |
| `SecurePreferences.kt` | Encrypted preferences |

---

## Session (`session/`)

| File | Purpose |
|------|---------|
| `SessionManager.kt` | Session lifecycle |
| `CurrentUserProvider.kt` | Current user access |
| `TokenManager.kt` | Token management |

---

## Key Configuration Files

### Build Configuration

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | App module config, dependencies |
| `build.gradle.kts` | Project-level config |
| `settings.gradle.kts` | Module settings |
| `gradle.properties` | Gradle properties |

### Firebase

| File | Purpose |
|------|---------|
| `app/google-services.json` | Firebase config (gitignored) |
| `firebase/firestore.indexes.json` | Firestore indexes |
| `firestore.rules` | Security rules |

### ProGuard

| File | Purpose |
|------|---------|
| `app/proguard-rules.pro` | Release obfuscation rules |

---

## Documentation (`docs/`)

### Architecture
- `architecture.md` - Technical architecture
- `PROJECT_OVERVIEW.md` - Enterprise overview
- `CODEBASE_STRUCTURE.md` - This file
- `state-management.md` - State patterns
- `dependency-injection.md` - Hilt guide
- `error-handling.md` - Error patterns

### Features
- `EVIDENCE_ORDER_SYSTEM.md` - Order system
- `social-platform.md` - Social features
- `farm-monitoring.md` - Farm management
- `traceability.md` - Lineage tracking
- `gamification.md` - Achievements
- `ai-personalization.md` - AI features

### Operations
- `firebase-setup.md` - Firebase config
- `database-migrations.md` - Migration guide
- `deployment.md` - Deployment process
- `testing-strategy.md` - Testing approach
- `troubleshooting.md` - Common issues

---

## File Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Screen | `<Feature>Screen.kt` | `FarmerHomeScreen.kt` |
| ViewModel | `<Feature>ViewModel.kt` | `FarmerHomeViewModel.kt` |
| Entity | `<Name>Entity.kt` | `ProductEntity.kt` |
| DAO | `<Name>Dao.kt` | `ProductDao.kt` |
| Repository | `<Name>RepositoryImpl.kt` | `ProductRepositoryImpl.kt` |
| Worker | `<Name>Worker.kt` | `SyncWorker.kt` |
| Module | `<Name>Module.kt` | `DatabaseModule.kt` |

---

## Search Shortcuts

### Find by functionality

```bash
# Find all ViewModels
grep -r "class.*ViewModel" --include="*.kt"

# Find all Workers
grep -r "class.*Worker" --include="*.kt"

# Find all Entities
grep -r "@Entity" --include="*.kt"

# Find all DAOs
grep -r "@Dao" --include="*.kt"

# Find all Repositories
grep -r "interface.*Repository" --include="*.kt"
```

---

## Document History

| Version | Date | Changes |
|---------|------|---------|
| 4.3 | 2026-01-13 | Updated counts and structure to reflect current implementation: 114+ ViewModels, 57+ Repositories, 30+ Workers, 61+ Entity files, 21 DI Modules |
| 4.2 | 2025-12-29 | Documentation Audit & Update (Phase 1-10) |
| 4.1 | 2025-12-27 | Digital Farm 2.0 (weather, buildings, gamification) |
| 4.0 | 2025-12-25 | Complete restructure, Digital Farm, Evidence Orders |
| 3.0 | 2025-01-15 | Social platform, community features |
| 2.0 | 2024-12-01 | Initial comprehensive mapping |

---

*Navigate with confidence. When in doubt, check the architecture docs.*
