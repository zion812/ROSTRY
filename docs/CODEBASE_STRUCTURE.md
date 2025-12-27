# ROSTRY Codebase Structure

**Document Type**: Developer Reference  
**Version**: 4.1  
**Last Updated**: 2025-12-27  
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
├── LoginScreen.kt
├── OtpScreen.kt
├── AuthViewModel.kt
├── PhoneLinkScreen.kt
└── ...
```

#### Farmer Features (`ui/farmer/`)
```
ui/farmer/
├── FarmerHomeScreen.kt
├── FarmerHomeViewModel.kt
├── FarmerMarketScreen.kt
├── asset/
│   ├── FarmAssetDetailScreen.kt
│   └── FarmAssetDetailViewModel.kt
├── compliance/
├── dashboard/
├── inventory/
├── listing/
└── ...
```

#### Enthusiast Features (`ui/enthusiast/`)
```
ui/enthusiast/
├── EnthusiastHomeScreen.kt
├── EnthusiastHomeViewModel.kt
├── digitalfarm/
│   ├── DigitalFarmScreen.kt          # Canvas-based farm visualization
│   ├── DigitalFarmViewModel.kt       # Zone grouping, weather/time state
│   └── FarmCanvasRenderer.kt         # 2.5D rendering engine (~2040 lines)
│       ├── Day/night cycle rendering
│       ├── Weather effects (rain, wind, clouds)
│       ├── Flocking algorithm for birds
│       ├── Age-based visual progression
│       ├── 8 placeable building types
│       ├── Resource bars, daily tasks
│       ├── Performance charts (line/bar/pie)
│       ├── Pedigree tree visualization
│       ├── Leaderboards, competitions
│       └── Offline indicators
├── breeding/
├── eggcollection/
├── shows/
└── ...
```

#### Monitoring (`ui/monitoring/`)
```
ui/monitoring/
├── GrowthTrackingScreen.kt
├── VaccinationScheduleScreen.kt
├── HatcheryManagementScreen.kt
├── BreedingManagementScreen.kt
├── QuarantineManagementScreen.kt
├── WeightLogScreen.kt
└── ...
```

#### Social (`ui/social/`)
```
ui/social/
├── feed/
│   ├── FeedScreen.kt
│   └── FeedViewModel.kt
├── profile/
│   ├── SocialProfileScreen.kt
│   └── SocialProfileViewModel.kt
├── groups/
├── events/
├── messaging/
└── ...
```

#### Orders (`ui/order/`)
```
ui/order/
├── MyOrdersScreen.kt
├── OrderDetailScreen.kt
├── evidence/
│   ├── CreateOrderScreen.kt
│   ├── OrderChatIntegration.kt
│   ├── PaymentDeliveryScreens.kt
│   ├── PaymentVerifyScreen.kt
│   └── DisputeScreens.kt
└── ...
```

#### Analytics (`ui/analytics/`)
```
ui/analytics/
├── AnalyticsDashboardScreen.kt
├── PerformanceInsightsScreen.kt
├── ExportScreen.kt
├── RecommendationsScreen.kt
└── ...
```

#### Traceability (`ui/traceability/`)
```
ui/traceability/
├── FamilyTreeScreen.kt
├── LineageDetailScreen.kt
├── TransferHistoryScreen.kt
└── ...
```

#### Verification (`ui/verification/`)
```
ui/verification/
├── FarmerLocationVerificationScreen.kt
├── VerificationViewModel.kt
├── DocumentUploadScreen.kt
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
| `AppDatabase.kt` | Database definition, migrations (v2→54) |
| `Converters.kt` | Type converters |

### Repositories (`data/repository/`)

46+ repository implementations:

| Repository | Domain |
|------------|--------|
| `AuthRepositoryImpl.kt` | Authentication |
| `UserRepositoryImpl.kt` | User management |
| `ProductRepositoryImpl.kt` | Products |
| `OrderRepositoryImpl.kt` | Orders |
| `EvidenceOrderRepositoryImpl.kt` | Evidence-based orders |
| `SocialRepositoryImpl.kt` | Social platform |
| `TransferWorkflowRepositoryImpl.kt` | Ownership transfers |
| `FarmAssetRepositoryImpl.kt` | Farm assets |
| `VaccinationRepositoryImpl.kt` | Vaccinations |
| `AnalyticsRepositoryImpl.kt` | Analytics |
| `TraceabilityRepositoryImpl.kt` | Lineage tracking |
| `GamificationRepositoryImpl.kt` | Achievements |
| `BreedingRepositoryImpl.kt` | Breeding management |
| `CommunityRepositoryImpl.kt` | Community features |

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

20 Hilt modules:

| Module | Bindings |
|--------|----------|
| `AppModule.kt` | App-level singletons |
| `DatabaseModule.kt` | Room, DAOs, migrations |
| `NetworkModule.kt` | Retrofit, OkHttp, Firebase |
| `RepositoryModule.kt` | Repository bindings |
| `AuthModule.kt` | Auth components |
| `WorkerModule.kt` | WorkManager |
| `PlacesModule.kt` | Google Places |
| `LocationModule.kt` | Location services |
| `AnalyticsModule.kt` | Analytics bindings |
| `SocialModule.kt` | Social bindings |

---

## Workers (`workers/`)

24 background workers:

| Worker | Schedule | Purpose |
|--------|----------|---------|
| `SyncWorker.kt` | 6 hours | Room/Firebase sync |
| `OutboxSyncWorker.kt` | On connectivity | Pending uploads |
| `FarmMonitoringWorker.kt` | Daily | Health checks |
| `VaccinationReminderWorker.kt` | Daily | Vaccine reminders |
| `LifecycleWorker.kt` | Daily | Milestone reminders |
| `ModerationWorker.kt` | Periodic | Content scanning |
| `OutgoingMessageWorker.kt` | On demand | Message delivery |
| `TransferTimeoutWorker.kt` | Periodic | SLA enforcement |
| `AnalyticsAggregationWorker.kt` | Daily | Metrics aggregation |
| `MediaUploadWorker.kt` | On demand | Media uploads |
| `EvidenceOrderWorker.kt` | On demand | Order processing |
| `PrefetchWorker.kt` | Conditional | Content caching |
| `CommunityEngagementWorker.kt` | 12 hours | Recommendations |

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
| 4.1 | 2025-12-27 | Digital Farm 2.0 (weather, buildings, gamification) |
| 4.0 | 2025-12-25 | Complete restructure, Digital Farm, Evidence Orders |
| 3.0 | 2025-01-15 | Social platform, community features |
| 2.0 | 2024-12-01 | Initial comprehensive mapping |

---

*Navigate with confidence. When in doubt, check the architecture docs.*
