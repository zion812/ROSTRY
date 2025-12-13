# ROSTRY Codebase Structure

**Version:** 1.0
**Last Updated:** 2025-01-15
**Audience:** Developers
**Purpose:** Detailed package-by-package navigation guide with file locations, naming conventions, and implementation details. Complements SYSTEM_BLUEPRINT.md which covers high-level architecture.

## Overview

**Scope:** This document provides detailed package-by-package navigation of the ROSTRY codebase, including file locations, naming conventions, and detailed descriptions. For high-level architecture, design patterns, and system-wide concerns, see `SYSTEM_BLUEPRINT.md`.

This document provides a comprehensive breakdown of the ROSTRY codebase structure, explaining the purpose and contents of each package and major file. This complements the SYSTEM_BLUEPRINT.md by providing detailed navigation information.

## Project Root Structure

```
ROSTRY/
├── app/                          # Main application module
├── docs/                         # Documentation
├── firebase/                     # Firebase configuration
├── gradle/                       # Gradle wrapper and version catalog
├── .github/                      # GitHub workflows and templates
├── build.gradle.kts             # Root build configuration
├── settings.gradle.kts          # Gradle settings
├── gradle.properties            # Gradle properties
├── local.properties.template    # Template for local configuration
├── README.md                    # Main project README
├── SYSTEM_BLUEPRINT.md          # Comprehensive SINF document
└── [Other root documentation files]
```

## Application Module Structure

### Source Code (`app/src/main/java/com/rio/rostry/`)

#### Top-Level Files

**RostryApp.kt**
- Application class
- Hilt initialization
- WorkManager scheduling
- Firebase initialization
- Timber logging setup
- Root detection integration

**MainActivity.kt**
- Single activity architecture
- Hosts Compose navigation
- Theme application
- Deep link handling

#### Package Breakdown

### `accessibility/`
**Purpose:** Accessibility features and TalkBack support

**Key Files:**
- Accessibility utilities
- Screen reader support
- High contrast themes
- Font scaling support

**Related Documentation:** `docs/accessibility-implementation.md`

---

### `ai/`
**Purpose:** AI-powered personalization and recommendations

**Key Files:**
- Recommendation engine
- Interest scoring algorithms
- Content personalization
- ML model integration

**Related Documentation:** `docs/ai-personalization.md`

---

### `community/`
**Purpose:** Community engagement features

**Key Files:**
- Community engagement service
- Recommendation generation
- Interest tracking
- Expert matching

**Related Documentation:** `docs/social-platform.md`

---

### `data/`
**Purpose:** Data layer - repositories, database, synchronization

#### `data/auth/`
**Purpose:** Authentication data sources and repositories

**Key Files:**
- `AuthRepository.kt` - Interface
- `AuthRepositoryImpl.kt` - Implementation
- Firebase Auth integration
- Phone OTP handling
- Session token management

#### `data/base/`
**Purpose:** Base classes for data layer

**Key Files:**
- `BaseRepository.kt` - Common repository functionality
- Resource wrappers
- Error handling patterns

#### `data/database/`
**Purpose:** Room database, entities, DAOs, migrations

**Structure:**
```
database/
├── AppDatabase.kt              # Main database class
├── LifecycleConverters.kt      # Type converters
├── entity/                     # All entity classes (60+)
│   ├── UserEntity.kt
│   ├── ProductEntity.kt
│   ├── OrderEntity.kt
│   ├── TransferEntity.kt
│   ├── PostEntity.kt
│   ├── [50+ more entities]
├── dao/                        # Data Access Objects
│   ├── UserDao.kt
│   ├── ProductDao.kt
│   ├── OrderDao.kt
│   ├── [20+ more DAOs]
└── migrations/                 # Database migrations (v1-v40)
```

**Key Entities:**
- User management: `UserEntity`, `UserProfileEntity`
- Products: `ProductEntity`, `ProductTrackingEntity`
- Orders: `OrderEntity`, `OrderItemEntity`, `PaymentEntity`
- Transfers: `TransferEntity`, `TransferVerificationEntity`, `DisputeEntity`
- Social: `PostEntity`, `CommentEntity`, `LikeEntity`, `GroupEntity`
- Farm: `FarmMonitoringEntities`, `VaccinationRecord`, `MortalityRecord`
- Analytics: `AnalyticsDailyEntity`, `ReportEntity`
- Traceability: `FamilyTreeEntity`, `ProductTrackingEntity`

**Related Documentation:** `docs/data-contracts.md`, `docs/database-migrations.md`

#### `data/repository/`
**Purpose:** Repository implementations

**Key Repositories:**
- `UserRepository` / `UserRepositoryImpl`
- `ProductRepository` / `ProductRepositoryImpl`
- `OrderRepository` / `OrderRepositoryImpl`
- `TransferRepository` / `TransferRepositoryImpl`
- `SocialRepository` / `SocialRepositoryImpl`
- `FarmMonitoringRepository` / `FarmMonitoringRepositoryImpl`
- `AnalyticsRepository` / `AnalyticsRepositoryImpl`
- [15+ more repositories]

**Pattern:** Interface + Implementation for testability

#### `data/session/`
**Purpose:** Session management

**Key Files:**
- `SessionManager.kt` - Session state management
- `SessionDataStore.kt` - Persistent session storage
- Token refresh logic
- Session validation

#### `data/sync/`
**Purpose:** Offline-first synchronization

**Key Files:**
- `SyncManager.kt` - Orchestrates sync operations
- `FirestoreService.kt` - Firebase sync with timeouts
- `ConflictResolver.kt` - Handles sync conflicts
- Sync strategies and policies

**Related Documentation:** `docs/adrs/adr-002-offline-first-sync.md`

---

### `di/`
**Purpose:** Hilt dependency injection modules

**Key Modules:**
- `AppModule.kt` - Application-level dependencies
- `NetworkModule.kt` - Retrofit, OkHttp, Firebase
- `DatabaseModule.kt` - Room database, DAOs
- `RepositoryModule.kt` - Repository bindings
- `ViewModelModule.kt` - ViewModel factories
- `WorkerModule.kt` - WorkManager dependencies
- `HttpModule.kt` - HTTP client with certificate pinning
- `FirebaseModule.kt` - Firebase services
- `PlacesModule.kt` - Google Places SDK (lazy initialization)
- `CoilModule.kt` - Image loading configuration

**Related Documentation:** `docs/dependency-injection.md`

---

### `domain/`
**Purpose:** Domain layer - use cases and business logic

**Structure:**
```
domain/
├── auth/                       # Authentication use cases
├── product/                    # Product business logic
├── order/                      # Order processing
├── transfer/                   # Transfer workflows
└── [other domain packages]
```

---

### `feedback/`
**Purpose:** User feedback system

**Key Files:**
- Feedback collection
- Bug reporting
- Feature requests
- User satisfaction surveys

---

### `gamification/`
**Purpose:** Achievement and reward system

**Key Files:**
- Achievement engine
- Badge management
- Reputation scoring
- Leaderboard logic

**Related Documentation:** `docs/gamification.md`

---

### `insights/`
**Purpose:** Analytics and insights generation

**Key Files:**
- Data aggregation
- Insight generation
- Trend analysis
- Recommendation algorithms

---

### `monitoring/`
**Purpose:** Farm monitoring features

**Key Files:**
- Health monitoring
- Growth tracking
- Alert generation
- Performance metrics

**Related Documentation:** `docs/farm-monitoring.md`

---

### `notifications/`
**Purpose:** Notification system

**Key Files:**
- Notification manager
- FCM integration
- Local notifications
- Deep link handling
- Notification preferences

**Related Documentation:** `docs/notification-system.md`

---

### `performance/`
**Purpose:** Performance monitoring and optimization

**Key Files:**
- Performance tracking
- Metrics collection
- Firebase Performance integration
- Startup optimization

---

### `security/`
**Purpose:** Security utilities and features

**Key Files:**
- `RootDetection.kt` - Device root detection
- `EncryptionUtils.kt` - Encryption helpers
- `BiometricAuth.kt` - Biometric authentication
- Certificate pinning configuration

**Related Documentation:** `docs/security-encryption.md`

---

### `services/`
**Purpose:** Android services

**Key Files:**
- `AppFirebaseMessagingService.kt` - FCM message handling
- Background services
- Foreground services

---

### `session/`
**Purpose:** Session management (duplicate of data/session?)

**Note:** May be consolidated with `data/session/`

---

### `support/`
**Purpose:** User support features

**Key Files:**
- Help center
- FAQ
- Contact support
- Troubleshooting guides

---

### `ui/`
**Purpose:** UI layer - Compose screens and ViewModels

**Structure:**
```
ui/
├── accessibility/              # Accessibility screens
├── analytics/                  # Analytics dashboards
├── animations/                 # Animation components
├── auction/                    # Auction screens
├── auth/                       # Authentication screens
│   ├── WelcomeScreen.kt
│   ├── PhoneInputScreen.kt
│   ├── OtpVerificationScreen.kt
│   └── AuthViewModel.kt
├── base/                       # Base classes
│   └── BaseViewModel.kt
├── community/                  # Community screens
├── components/                 # Reusable UI components
│   ├── FormValidationHelpers.kt
│   ├── LoadingStates.kt
│   ├── OnboardingTooltips.kt
│   ├── SuccessAnimations.kt
│   └── HelpComponents.kt
├── enthusiast/                 # Enthusiast-specific screens
├── events/                     # Event screens
├── expert/                     # Expert consultation screens
├── farmer/                     # Farmer-specific screens
├── feedback/                   # Feedback screens
├── gamification/               # Gamification UI
├── general/                    # General user screens
├── insights/                   # Insights screens
├── main/                       # Main app screens
├── marketplace/                # Marketplace screens
├── messaging/                  # Messaging screens
│   ├── ThreadViewModel.kt
│   ├── GroupChatViewModel.kt
│   └── [messaging screens]
├── moderation/                 # Moderation screens
├── monitoring/                 # Farm monitoring screens
├── navigation/                 # Navigation
│   ├── AppNavHost.kt
│   ├── Routes.kt
│   └── [navigation graphs]
├── notifications/              # Notification screens
├── onboarding/                 # Onboarding flows
├── order/                      # Order screens
├── product/                    # Product screens
├── profile/                    # Profile screens
├── scan/                       # QR scanning screens
├── screens/                    # General screens
├── session/                    # Session screens
├── settings/                   # Settings screens
├── social/                     # Social platform screens
├── splash/                     # Splash screen
├── start/                      # Start screen
├── support/                    # Support screens
├── sync/                       # Sync status screens
├── theme/                      # Material 3 theme
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── traceability/               # Traceability screens
├── transfer/                   # Transfer screens
├── upgrade/                    # App upgrade screens
└── verification/               # Verification screens
```

**Key Patterns:**
- Each feature has its own package
- ViewModels co-located with screens
- Reusable components in `components/`
- Navigation centralized in `navigation/`
- Theme in `theme/`

**Related Documentation:** `docs/user-experience-guidelines.md`

---

### `util/` and `utils/`
**Purpose:** Utility classes and helper functions

**Note:** These two packages may need consolidation

**Key Utilities:**
- Validation utilities
- Date/time formatters
- String extensions
- Network utilities
- File utilities
- Compression utilities
- Encryption helpers

---

### `workers/`
**Purpose:** WorkManager background jobs

**Key Workers:**
- `SyncWorker.kt` - Data synchronization
- `FarmMonitoringWorker.kt` - Daily farm checks
- `VaccinationReminderWorker.kt` - Vaccination alerts
- `PersonalizationWorker.kt` - Recommendation updates (6-hour intervals)
- `ModerationWorker.kt` - Content moderation
- `MediaUploadWorker.kt` - Background media uploads
- `AnalyticsAggregationWorker.kt` - Data aggregation
- `OutboxSyncWorker.kt` - Outbox pattern sync
- `PullSyncWorker.kt` - Pull-only sync
- [10+ more workers]

**Related Documentation:** `docs/worker-catalog.md`, `docs/background-jobs.md`

---

## Resources (`app/src/main/res/`)

```
res/
├── drawable/                   # Vector drawables and images
├── layout/                     # XML layouts (if any)
├── mipmap/                     # App icons
├── values/                     # Resource values
│   ├── strings.xml            # String resources
│   ├── colors.xml             # Color definitions
│   ├── themes.xml             # Material themes
│   └── dimens.xml             # Dimension values
└── xml/                        # XML configurations
    ├── network_security_config.xml
    └── backup_rules.xml
```

---

## Test Structure

### Unit Tests (`app/src/test/java/`)

```
test/
├── ui/                         # ViewModel tests
│   ├── ProductViewModelTest.kt
│   └── [other ViewModel tests]
├── data/repository/            # Repository tests
│   ├── ProductRepositoryTest.kt
│   └── [other repository tests]
└── util/                       # Utility tests
    └── ValidationUtilsTest.kt
```

### Instrumented Tests (`app/src/androidTest/java/`)

```
androidTest/
├── ui/                         # UI tests
│   └── ProductListScreenTest.kt
├── data/                       # Database tests
│   └── MigrationTest.kt
└── navigation/                 # Navigation tests
    └── NavigationTest.kt
```

**Related Documentation:** `docs/testing-strategy.md`

---

## Build Configuration

### Root Level

**build.gradle.kts**
- Plugin configuration
- Dokka setup for API documentation
- Multi-module configuration

**settings.gradle.kts**
- Module inclusion
- Plugin management
- Dependency resolution

**gradle.properties**
- Build properties
- JVM settings
- Android build options

### App Module

**app/build.gradle.kts**
- Dependencies (60+ libraries)
- Build variants (debug, release)
- ProGuard configuration
- ABI splits
- Version management
- JaCoCo coverage
- Custom tasks (version bump, APK size check)

**app/proguard-rules.pro**
- ProGuard/R8 rules
- Keep rules for Hilt, Room, Compose
- Obfuscation rules

### Version Catalog

**gradle/libs.versions.toml**
- Centralized version management
- Library definitions
- Plugin definitions

---

## Firebase Configuration

**firebase/**
- `firebase.json` - Firebase project configuration
- `firestore.indexes.json` - Firestore index definitions
- `firestore.rules` - Security rules
- `storage.rules` - Storage security rules

**app/google-services.json**
- Firebase project credentials
- API keys
- Project IDs

**Note:** Use `google-services.json.template` for version control

---

## Documentation Structure

See `docs/README-docs.md` for complete documentation organization.

**Key Documentation:**
- `SYSTEM_BLUEPRINT.md` - Comprehensive SINF document
- `docs/architecture.md` - Architecture details
- `docs/README-docs.md` - Documentation index
- `docs/developer-onboarding.md` - Getting started
- Feature-specific guides in `docs/`
- ADRs in `docs/adrs/`

---

## GitHub Configuration

**.github/**
- `workflows/` - CI/CD pipelines
- `ISSUE_TEMPLATE/` - Issue templates
- `PULL_REQUEST_TEMPLATE.md` - PR template

---

## Navigation Map

### Finding Specific Functionality

**Authentication:**
- UI: `ui/auth/`
- Data: `data/auth/`
- ViewModel: `ui/auth/AuthViewModel.kt`

**Products:**
- UI: `ui/product/`, `ui/marketplace/`
- Data: `data/repository/ProductRepository.kt`
- Entity: `data/database/entity/ProductEntity.kt`
- DAO: `data/database/dao/ProductDao.kt`

**Transfers:**
- UI: `ui/transfer/`
- Data: `data/repository/TransferRepository.kt`
- Entity: `data/database/entity/TransferEntity.kt`
- Workflow: See `docs/transfer-workflow.md`

**Social Features:**
- UI: `ui/social/`, `ui/messaging/`, `ui/community/`
- Data: `data/repository/SocialRepository.kt`
- Entities: `PostEntity`, `CommentEntity`, `GroupEntity`, etc.

**Farm Monitoring:**
- UI: `ui/monitoring/`
- Data: `data/repository/FarmMonitoringRepository.kt`
- Workers: `workers/FarmMonitoringWorker.kt`
- Entities: `FarmMonitoringEntities.kt`

---

## File Naming Conventions

**Entities:** `*Entity.kt` (e.g., `UserEntity.kt`)
**DAOs:** `*Dao.kt` (e.g., `UserDao.kt`)
**Repositories:** `*Repository.kt` (interface), `*RepositoryImpl.kt` (implementation)
**ViewModels:** `*ViewModel.kt` (e.g., `ProductListViewModel.kt`)
**Screens:** `*Screen.kt` (e.g., `ProductListScreen.kt`)
**Workers:** `*Worker.kt` (e.g., `SyncWorker.kt`)
**Modules:** `*Module.kt` (e.g., `NetworkModule.kt`)

---

## Code Organization Principles

1. **Feature-based packaging** in UI layer
2. **Layer-based packaging** in data layer
3. **Separation of concerns** - clear boundaries between layers
4. **Interface-based design** - repositories use interfaces
5. **Dependency injection** - Hilt manages all dependencies
6. **Offline-first** - Room as source of truth
7. **Reactive** - Flow and StateFlow for data streams

---

## Related Documentation

- **SYSTEM_BLUEPRINT.md** - High-level architecture, design patterns, feature catalog, and data flows (complements this document's detailed package navigation)
- **docs/architecture.md` - Architecture patterns and principles
- **docs/data-contracts.md** - Database schema and API contracts
- **docs/dependency-injection.md** - DI structure and patterns
- **docs/README-docs.md** - Documentation index

---

## Maintenance Notes

**When adding new features:**
1. Create feature package in appropriate layer
2. Follow existing naming conventions
3. Update this document
4. Update SYSTEM_BLUEPRINT.md
5. Create feature documentation in `docs/`

**When refactoring:**
1. Update affected documentation
2. Update diagrams if architecture changes
3. Create ADR for significant decisions
4. Update CHANGELOG.md

---

**Last Updated:** 2025-01-15
**Maintainers:** ROSTRY Development Team
**Questions:** See `docs/troubleshooting.md` or open an issue
