**Version:** 2.2  
**Last Updated:** 2025-10-29  
**Audience:** New developers  
**Estimated Time:** 2–3 hours for setup and first PR

# Developer Onboarding Guide

**Setup:** see **../QUICK_START.md**  
**Commands:** see **../CHEAT_SHEET.md**

## First Week Plan

Day 1: Environment setup — follow `../QUICK_START.md`; use `../CHEAT_SHEET.md` for commands.

Day 2–3: Understand architecture — read `architecture.md` and focused guides: `state-management.md`, `dependency-injection.md`, `error-handling.md`.

Day 4: Make a small contribution — pick a good-first-issue, follow `../CONTRIBUTING.md`.

Day 5: Testing & docs — review `testing-strategy.md`, `../CHEAT_SHEET.md`, and update docs if you changed behavior.

---

Welcome to ROSTRY! This comprehensive guide will help you set up your development environment, understand the architecture, and make your first contribution.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Environment Setup](#environment-setup)
- [First Week Plan](#first-week-plan)
- [Architecture Overview](#architecture-overview)
- [Key Concepts](#key-concepts)
- [Code Walkthroughs](#code-walkthroughs)
- [Common Tasks](#common-tasks)
- [Development Workflow](#development-workflow)
- [Debugging & Testing](#debugging--testing)
- [Troubleshooting](#troubleshooting)
- [Learning Resources](#learning-resources)
- [Onboarding Checklist](#onboarding-checklist)
- [Next Steps](#next-steps)

---

## Prerequisites

### System Requirements

**Minimum**:
- CPU: 64-bit processor (Intel i5 or AMD Ryzen 5)
- RAM: 8GB (16GB recommended for emulator)
- Storage: 10GB free space
- OS: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)

### Required Software

| Software | Version | Purpose |
|----------|---------|----------|
| **Android Studio** | Hedgehog (2023.1.1)+ | Primary IDE |
| **JDK** | OpenJDK 17 | Java runtime |
| **Git** | Latest stable | Version control |
| **Gradle** | 8.2+ | Build system (managed by wrapper) |

### Optional Tools

- **Firebase CLI**: Backend development (`npm install -g firebase-tools`)
- **Postman**: API testing
- **DB Browser for SQLite**: Database inspection
- **Scrcpy**: Device mirroring

### Accounts & Access

- **GitHub Account**: For repository access
- **Firebase Console Access**: Contact admin for project invitation
- **Google Cloud Console**: For Maps API key

---

## Environment Setup

- Use `../QUICK_START.md` for cloning, keys, and first run.
- Use `../CHEAT_SHEET.md` for common commands (build, test, lint) and patterns.
- For Firebase and API keys, see `firebase-setup.md` and `api-keys-setup.md`.

---

## Architecture Overview

ROSTRY follows **Clean Architecture** with **MVVM** pattern.

### Architectural Layers

```
┌─────────────────────────────────────────┐
│      UI Layer (Jetpack Compose)         │
│  • Composables (Screens)                │
│  • ViewModels (State Management)        │
└─────────────────┬───────────────────────┘
                  │ StateFlow
┌─────────────────┴───────────────────────┐
│        Domain Layer (Optional)          │
│  • Use Cases                            │
│  • Business Logic                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────┴───────────────────────┐
│           Data Layer                    │
│  • Repositories                         │
│  • Data Sources (Room, Firebase)        │
└─────────────────────────────────────────┘
```

### Key Patterns

- **MVVM**: UI observes ViewModel via StateFlow
- **Repository**: Abstracts data sources
- **DI**: Hilt manages dependencies
- **Offline-First**: Room as source of truth
- **Reactive**: Kotlin Flow for streams
- **Data Contexts**: Products have strict visibility scopes (Public vs Private). See `marketplace-architecture.md`.

**Full Details**: [docs/architecture.md](architecture.md)

### Navigation Structure

```
AppNavHost
├── AuthNavGraph
├── RoleNavGraph (Role-based)
│   ├── GeneralNavGraph
│   ├── FarmerNavGraph
│   └── EnthusiastNavGraph
└── MainScreen (Bottom nav)
```

**Callback Pattern**: We use a callback-based navigation pattern. Screens expose events (`onItemClick`) instead of requesting navigation directly. See `docs/NAVIGATION_INTEGRATION_GUIDE.md` for details.

### Dependency Injection

**Hilt Modules**:
- `di/AppModule.kt` - App-level
- `di/NetworkModule.kt` - Retrofit, Firebase
- `di/DatabaseModule.kt` - Room
- `di/RepositoryModule.kt` - Repositories

**Scopes**:
- `@Singleton` - App lifetime
- `@ViewModelScoped` - ViewModel lifetime
- `@ActivityRetainedScoped` - Survives config changes

---

## Key Concepts

This section is now summarized to keep onboarding focused. For detailed patterns and runnable examples, see:
- `state-management.md`, `dependency-injection.md`, `error-handling.md`
- `architecture.md`, `testing-strategy.md`
- Quick patterns: `../CHEAT_SHEET.md`

---

## Code Walkthroughs

Walkthroughs have moved to focused tutorials to reduce onboarding length.
- See `../CHEAT_SHEET.md` for copy-paste patterns
- See feature docs under `docs/` for end-to-end guides

### Database Migration

Database migrations are essential for evolving the Room database schema while preserving user data across app updates. They handle changes like adding tables, columns, or modifying data types without losing existing information.

For complete migration guide including examples, testing, and troubleshooting, see `database-migrations.md`.

---

## Common Tasks

Common commands and step-by-step snippets have moved to:
- `../CHEAT_SHEET.md` (commands and patterns)
- `testing-strategy.md` (testing)
- `database-migrations.md` (Room)

---

## Development Workflow (Concise)

- Branches: `feat/*`, `fix/*`, `refactor/*`, `docs/*`
- Commits: follow Conventional Commits
- PRs: use the template; pass ktlint, detekt, and tests
- Reviews: see `../CONTRIBUTING.md`

Pre-PR checks:
```bash
./gradlew ktlintCheck detekt test
```

Release build:
```bash
./gradlew assembleRelease
```

Debugging & Testing: see `testing-strategy.md` and `troubleshooting.md`.

## Onboarding Checklist (Short)

Day 1
- [ ] Setup from `../QUICK_START.md`
- [ ] Add `google-services.json`
- [ ] First build and run

Week 1
- [ ] Read `architecture.md`, `state-management.md`, `dependency-injection.md`
- [ ] Open a small PR following `../CONTRIBUTING.md`
- [ ] Run tests locally and update docs as needed

Getting Help: `docs/README-docs.md`, `troubleshooting.md`, Discussions/Issues.

---

**Welcome to the ROSTRY team!** 🚀 For feedback on this guide, open an issue or PR.
