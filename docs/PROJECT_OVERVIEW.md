# ROSTRY - Enterprise Project Overview

**Document Type**: Technical Overview  
**Version**: 4.0  
**Last Updated**: 2025-12-25  
**Classification**: Internal / Developer Documentation  
**Maintainer**: ROSTRY Engineering Team

---

## Executive Summary

ROSTRY is an enterprise-grade **AgriTech Platform** purpose-built for the poultry industry. It combines:

- 🛒 **Marketplace** – B2B/B2C transactions with escrow & evidence-based orders
- 🐔 **Farm Management** – Digital farm visualization, health tracking, breeding management
- 💬 **Social Platform** – Community hub, messaging, expert consultations
- 📊 **Analytics** – AI-powered insights, traceability, performance dashboards
- 🔒 **Enterprise Security** – SQLCipher encryption, RBAC, audit logging

**Platform**: Android (Kotlin + Jetpack Compose)  
**Architecture**: Clean Architecture + MVVM + Offline-First  
**Backend**: Firebase (Auth, Firestore, Storage, Functions, FCM)

---

## Table of Contents

1. [System Architecture](#1-system-architecture)
2. [Module Structure](#2-module-structure)
3. [Feature Domains](#3-feature-domains)
4. [Data Architecture](#4-data-architecture)
5. [Security Framework](#5-security-framework)
6. [Integration Points](#6-integration-points)
7. [Quality Standards](#7-quality-standards)
8. [Deployment Pipeline](#8-deployment-pipeline)

---

## 1. System Architecture

### 1.1 Architectural Pattern

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  Compose UI     │  │   ViewModels    │  │   Navigation    │ │
│  │  (Screens)      │  │   (StateFlow)   │  │   (NavHost)     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                      DOMAIN LAYER                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Use Cases     │  │  Domain Models  │  │  Repositories   │ │
│  │  (Interactors)  │  │  (Pure Kotlin)  │  │  (Interfaces)   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                       DATA LAYER                                │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Room + DAO    │  │    Firebase     │  │   Retrofit      │ │
│  │  (SQLCipher)    │  │  (Cloud Sync)   │  │   (REST APIs)   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                   INFRASTRUCTURE LAYER                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   WorkManager   │  │  Hilt DI        │  │   DataStore     │ │
│  │  (Background)   │  │  (Injection)    │  │  (Preferences)  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Core Principles

| Principle | Implementation |
|-----------|----------------|
| **Offline-First** | Room as source of truth, background sync with Firebase |
| **Reactive** | StateFlow/SharedFlow for state, Flow for data streams |
| **Modular** | Feature packages with clear boundaries |
| **Testable** | Repository pattern, dependency injection, fake implementations |
| **Secure** | SQLCipher encryption, RBAC, secure token storage |

### 1.3 Technology Stack

| Category | Technologies |
|----------|--------------|
| **UI** | Jetpack Compose, Material 3, Coil, MPAndroidChart |
| **State** | StateFlow, SavedStateHandle, DataStore |
| **DI** | Hilt (Dagger) |
| **Database** | Room + SQLCipher |
| **Network** | Retrofit, OkHttp, Firebase SDK |
| **Backend** | Firebase Auth, Firestore, Storage, Functions, FCM |
| **Background** | WorkManager, CoroutineWorker |
| **Quality** | ktlint, detekt, Dokka, Timber |

---

## 2. Module Structure

### 2.1 Package Organization

```
com.rio.rostry/
├── RostryApp.kt              # Application class - DI initialization, worker scheduling
├── MainActivity.kt           # Single activity host
│
├── ui/                       # PRESENTATION LAYER (45+ feature packages)
│   ├── navigation/           # Routes, NavHost, deep links
│   ├── theme/                # Material 3 theming
│   ├── components/           # Reusable UI components
│   ├── farmer/               # Farmer role screens
│   ├── enthusiast/           # Enthusiast role screens
│   ├── general/              # General user screens
│   ├── social/               # Social platform UI
│   ├── order/                # Order management UI
│   ├── monitoring/           # Farm monitoring UI
│   ├── analytics/            # Analytics dashboards
│   ├── traceability/         # Lineage tracking
│   └── ...
│
├── domain/                   # DOMAIN LAYER
│   ├── model/                # Domain entities (pure Kotlin)
│   ├── usecase/              # Business logic interactors
│   ├── auth/                 # Auth domain models
│   ├── rbac/                 # Role-based access control
│   └── service/              # Domain services
│
├── data/                     # DATA LAYER
│   ├── database/
│   │   ├── entity/           # Room entities (60+ tables)
│   │   ├── dao/              # Data Access Objects
│   │   └── AppDatabase.kt    # Database configuration
│   ├── repository/           # Repository implementations (46+ repos)
│   ├── auth/                 # Auth implementation
│   └── sync/                 # Sync infrastructure
│
├── di/                       # DEPENDENCY INJECTION (20 modules)
│   ├── AppModule.kt          # App-level bindings
│   ├── DatabaseModule.kt     # Room, DAOs
│   ├── NetworkModule.kt      # Retrofit, Firebase
│   ├── RepositoryModule.kt   # Repository bindings
│   └── ...
│
├── workers/                  # BACKGROUND JOBS (24 workers)
│   ├── SyncWorker.kt
│   ├── FarmMonitoringWorker.kt
│   ├── VaccinationReminderWorker.kt
│   └── ...
│
├── utils/                    # UTILITIES (48 utilities)
│   ├── validation/
│   ├── compression/
│   └── ...
│
└── security/                 # SECURITY
    ├── RootDetector.kt
    └── ...
```

### 2.2 Feature Package Structure

Each feature follows a consistent structure:

```
ui/<feature>/
├── <Feature>Screen.kt        # Main composable
├── <Feature>ViewModel.kt     # ViewModel with StateFlow
├── components/               # Feature-specific components
└── models/                   # UI models (if needed)
```

---

## 3. Feature Domains

### 3.1 User Roles

| Role | Capabilities |
|------|--------------|
| **General** | Browse marketplace, basic social features |
| **Farmer** | Farm management, product listings, analytics, transfers |
| **Enthusiast** | Breeding management, shows, digital farm, advanced tracking |
| **Admin** | Moderation, verification approvals, system management |

### 3.2 Core Features Matrix

| Feature | General | Farmer | Enthusiast | Admin |
|---------|---------|--------|------------|-------|
| Marketplace Browse | ✅ | ✅ | ✅ | ✅ |
| Create Listings | ❌ | ✅ | ✅ | ✅ |
| Farm Management | ❌ | ✅ | ✅ | - |
| Digital Farm | ❌ | ❌ | ✅ | - |
| Breeding Management | ❌ | ✅ | ✅ | - |
| Analytics Dashboard | ❌ | ✅ | ✅ | ✅ |
| Traceability | ❌ | ✅ | ✅ | ✅ |
| Social Platform | ✅ | ✅ | ✅ | ✅ |
| Moderation | ❌ | ❌ | ❌ | ✅ |

### 3.3 Feature Documentation

| Feature | Documentation |
|---------|---------------|
| Digital Farm | Evolutionary visuals, Canvas rendering, zone-based grouping |
| Evidence-Based Orders | `EVIDENCE_ORDER_SYSTEM.md` |
| Social Platform | `social-platform.md` |
| Farm Monitoring | `farm-monitoring.md` |
| Traceability | `traceability.md` |
| Gamification | `gamification.md` |
| AI Recommendations | `ai-personalization.md` |

---

## 4. Data Architecture

### 4.1 Database Schema

**Version**: 65
**Encryption**: SQLCipher
**Tables**: 133+
**Schema Export**: `app/schemas` (JSON via KSP)

#### Key Entity Groups

| Group | Tables | Purpose |
|-------|--------|---------|
| **Core** | users, products, farmers | Primary entities |
| **Transactions** | orders, order_quotes, order_payments, delivery_confirmations | Order lifecycle |
| **Farm** | daily_logs, tasks, vaccination_records, farm_assets | Farm management |
| **Social** | posts, comments, messages, threads, follows | Social platform |
| **Traceability** | family_trees, transfers, lineage_events | Lineage tracking |
| **Analytics** | analytics_daily, dashboard_snapshots | Metrics & insights |

### 4.2 Offline-First Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                    LOCAL-FIRST FLOW                         │
├─────────────────────────────────────────────────────────────┤
│  1. User Action → Write to Room (immediate)                 │
│  2. Room Entity → Mark as "dirty" (needs sync)              │
│  3. WorkManager → Background upload when online             │
│  4. Firebase → Confirm write, clear dirty flag              │
│  5. If offline → Retry on connectivity                      │
├─────────────────────────────────────────────────────────────┤
│                    REMOTE SYNC FLOW                         │
├─────────────────────────────────────────────────────────────┤
│  1. SyncWorker → Periodic (every 6 hours)                   │
│  2. Firestore → Fetch changes since lastSyncAt              │
│  3. Room → Upsert with conflict resolution                  │
│  4. UI → Flow emission triggers recomposition               │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 Migration Strategy

- **Current Version**: 65
- **Migration Path**: Sequential (v2→65)
- **Testing**: Migration tests in `androidTest/`
- **Fallback**: Destructive migration in DEBUG only

---

## 5. Security Framework

### 5.1 Security Layers

| Layer | Implementation |
|-------|----------------|
| **Data at Rest** | SQLCipher encryption |
| **Data in Transit** | HTTPS, certificate pinning |
| **Authentication** | Firebase Phone Auth, OTP |
| **Authorization** | RBAC with permission guards |
| **Session** | Encrypted DataStore, auto-refresh |
| **Audit** | Comprehensive logging, Crashlytics |

### 5.2 Root Detection

```kotlin
// RootDetector.kt
fun isDeviceCompromised(): Boolean {
    return checkRootBinary() || checkSuPermission() || checkMagiskHide()
}
// App shows warning but allows limited functionality
```

### 5.3 Firestore Security Rules

```javascript
// firestore.rules
match /users/{userId} {
  allow read, update: if request.auth.uid == userId;
  allow create: if request.auth != null 
    && request.auth.token.phone_number != null;
}

match /products/{productId} {
  allow read: if true;  // Public browse
  allow write: if request.auth != null 
    && resource.data.sellerId == request.auth.uid;
}
```

---

## 6. Integration Points

### 6.1 Firebase Services

| Service | Usage |
|---------|-------|
| **Auth** | Phone OTP, Google, Email/Password |
| **Firestore** | Primary cloud database |
| **Storage** | Media files (images, videos) |
| **Functions** | Server-side logic, notifications |
| **FCM** | Push notifications |
| **Crashlytics** | Error reporting |
| **Performance** | App performance monitoring |

### 6.2 Third-Party SDKs

| SDK | Purpose |
|-----|---------|
| Google Maps/Places | Location services |
| Coil | Image loading |
| MPAndroidChart | Charts & graphs |
| ExoPlayer | Video playback |
| Timber | Logging |

### 6.3 Background Workers

| Worker | Schedule | Purpose |
|--------|----------|---------|
| SyncWorker | Every 6 hours | Room/Firebase sync |
| FarmMonitoringWorker | Daily | Health checks, alerts |
| VaccinationReminderWorker | Daily | Schedule notifications |
| OutboxSyncWorker | On connectivity | Pending uploads |
| ModerationWorker | Periodic | Content scanning |
| LifecycleWorker | Daily | Milestone reminders |

---

## 7. Quality Standards

### 7.1 Code Quality

| Tool | Purpose | Config |
|------|---------|--------|
| **ktlint** | Kotlin formatting | `ktlint.gradle.kts` |
| **detekt** | Static analysis | `detekt.yml` |
| **Dokka** | API documentation | `dokka.gradle.kts` |
| **Timber** | Structured logging | `RostryApp.kt` |

### 7.2 Testing Strategy

| Type | Location | Coverage |
|------|----------|----------|
| **Unit Tests** | `test/` | ViewModels, Repositories, Utils |
| **Integration** | `androidTest/` | Database, Navigation |
| **UI Tests** | `androidTest/` | Compose, User flows |

### 7.3 Documentation Standards

| Document | Purpose | Location |
|----------|---------|----------|
| README.md | Project overview | Root |
| SYSTEM_BLUEPRINT.md | Complete SINF | Root |
| architecture.md | Technical architecture | `docs/` |
| README-docs.md | Documentation index | `docs/` |
| Feature guides | Feature-specific | `docs/` |

---

## 8. Deployment Pipeline

### 8.1 Build Variants

| Variant | Purpose | Features |
|---------|---------|----------|
| **debug** | Development | Debug logging, test keys |
| **release** | Production | ProGuard, real keys |

### 8.2 CI/CD

```yaml
# .github/workflows/
├── android-ci.yml        # Build & test on PR
├── release.yml           # Release automation
└── codeql-analysis.yml   # Security scanning
```

### 8.3 Release Process

1. Version bump in `build.gradle.kts`
2. Update `CHANGELOG.md`
3. Create release branch
4. CI builds and tests
5. Code review and approval
6. Merge to main
7. Tag and deploy

---

## Quick Reference

### Key Files

| File | Purpose |
|------|---------|
| `RostryApp.kt` | Application initialization |
| `MainActivity.kt` | Single activity host |
| `AppNavHost.kt` | Navigation graph |
| `Routes.kt` | Route definitions |
| `AppDatabase.kt` | Room database |
| `DatabaseModule.kt` | DI configuration |

### Commands

```bash
# Build
./gradlew :app:assembleDebug

# Test
./gradlew :app:testDebugUnitTest

# Lint
./gradlew :app:lintDebug

# Docs
./gradlew :app:dokkaHtml
```

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 4.0 | 2025-12-25 | Engineering | Added Digital Farm, Evidence Orders, comprehensive refactor |
| 3.0 | 2025-01-15 | Engineering | Social platform, community features |
| 2.0 | 2024-12-01 | Engineering | Marketplace, farm management |
| 1.0 | 2024-06-01 | Engineering | Initial release |

---

*This document is the authoritative technical overview. For implementation details, see linked documentation.*
