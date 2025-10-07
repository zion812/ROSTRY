# ROSTRY Android Application

ROSTRY is an AgriTech marketplace application built with modern Android development practices, combining social networking, e-commerce, and advanced farm management tools for the poultry industry.

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-—-informational)
![License](https://img.shields.io/badge/license-Apache--2.0-blue)
![Android](https://img.shields.io/badge/Android-13%2B-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange)

---

## Table of Contents

- [Features Overview](#features-overview)
- [Screenshots](#screenshots)
- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Quick Start (Developers)](#quick-start-developers)
- [Building](#building)
- [Testing](#testing)
- [Project Status](#project-status)
- [Recent Major Features](#recent-major-features)
- [Contributing & Code Style](#contributing--code-style)
- [Community Standards](#community-standards)
- [Support](#support)
- [Troubleshooting](#troubleshooting)
- [License](#license)
- [Acknowledgments](#acknowledgments)

---

## Features Overview

- **Social Platform**: Posts, comments, messaging, groups, events, community engagement hub
- **Marketplace**: Listings, auctions, payments, refunds, filter presets
- **Secure Transfers**: Ownership transfers with guided flow, verification, and audit
- **Farm Monitoring**: Growth, vaccination, hatching, analytics, quick actions, alerts
- **Advanced Analytics**: Dashboards, performance insights, AI recommendations, exports
- **Notifications**: Push and local notifications with deep links
- **UX Enhancements**: Multi-step wizards, tooltips, animations, contextual help

---

## Screenshots

> 📸 Screenshots coming soon!

Screenshots will showcase:
- **Home Dashboard**: Main interface for all user roles
- **Marketplace**: Product listings with filters
- **Farm Monitoring**: Real-time farm metrics and alerts
- **Analytics**: Performance dashboards and insights
- **Transfer System**: Secure ownership transfer flow
- **Social Features**: Community posts and messaging

*Screenshots will be added to* `docs/images/screenshots/` *directory*

---

## Prerequisites

Before you begin, ensure you have:

### Required Software

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: OpenJDK 17 or higher
- **Git**: Latest stable version
- **Gradle**: 8.2+ (managed by wrapper)

### Recommended

- **Android SDK**: API 33 (Android 13) or higher
- **Android Emulator**: Or physical device with Android 7.0+ (API 24+)
- **Minimum 8GB RAM**: For smooth development experience
- **10GB free disk space**: For SDK, dependencies, and build cache

### Accounts & Access

- **Firebase Account**: For backend services
- **Google Cloud Console Access**: For Maps API key
- **GitHub Account**: For contributing

---

## Project Setup

1.  **Clone the repository.**
2.  **Firebase Setup**: 
    - Download your `google-services.json` file from the [Firebase Console](https://console.firebase.google.com/)
    - Place it in the `app/` directory
    - See [firebase-setup.md](docs/firebase-setup.md) for detailed Firebase configuration

3.  **API Keys Setup**:
    - Copy `local.properties.template` to `local.properties`
    - Add your Google Maps API key: `MAPS_API_KEY=your_actual_key`
    - Get your Maps API key from [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/credentials)
    - **Never commit** `local.properties` to version control (already in .gitignore)

## Documentation

Refer to the [Documentation Index](docs/README-docs.md) for detailed guides on architecture, feature domains, data contracts, and operational practices.

Key docs:
- `docs/feature-toggles.md`
- `docs/security-encryption.md`
- `docs/background-jobs.md`
- `docs/payments-refunds.md`
- `docs/logistics-tracking.md`
- `docs/farm-monitoring.md`
- `docs/social-platform.md`
- `docs/user-experience-guidelines.md`

## Architecture

This project follows **Clean Architecture + MVVM**, leveraging Android Jetpack components and Hilt for dependency injection.

### Core Components:

*   **UI Layer (Jetpack Compose)**: Views are built using Jetpack Compose. Composable functions observe StateFlows from ViewModels to react to data changes.
*   **ViewModels (`androidx.lifecycle.ViewModel`)**: Responsible for preparing and managing data for the UI. They expose data via `StateFlow` and handle user interactions by calling methods on repositories or use cases.
*   **Repositories**: Manage data operations. They abstract data sources (network, local database) and provide a clean API for ViewModels to access data.
*   **Data Layer**:
    *   **Room Database (`androidx.room`)**: Local persistence library, acting as the single source of truth for offline-first capabilities.
    *   **Firebase**: Used for backend services (Authentication, Firestore, Storage, Functions, Cloud Messaging).
*   **Dependency Injection (Hilt)**: Manages dependencies throughout the application.
*   **Coroutines & Flow**: Used for asynchronous operations and reactive data streams.
*   **WorkManager**: Background tasks like data synchronization and monitoring workers.

See [architecture.md](docs/architecture.md) for comprehensive architecture documentation.

---

## Technology Stack

### UI & Presentation
- **Jetpack Compose** 1.5.0 - Modern declarative UI
- **Material Design 3** - Latest Material components
- **Coil** 2.6.0 - Image loading and caching
- **MPAndroidChart** - Data visualization

### Architecture & DI
- **MVVM** - Model-View-ViewModel pattern
- **Clean Architecture** - Separation of concerns
- **Hilt** 2.51.1 - Dependency injection
- **Jetpack Navigation** - Type-safe navigation

### Data & Persistence
- **Room** 2.6.1 - Local SQLite database
- **SQLCipher** - Database encryption
- **DataStore** - Preferences storage

### Backend & Networking
- **Firebase Auth** - Phone authentication
- **Cloud Firestore** - NoSQL cloud database
- **Cloud Storage** - Media storage
- **Cloud Functions** - Serverless logic
- **Firebase Cloud Messaging** - Push notifications
- **Retrofit** 2.9.0 - HTTP client
- **Gson** 2.10.1 - JSON parsing

### Async & Concurrency
- **Kotlin Coroutines** 1.8.1 - Structured concurrency
- **Flow** - Reactive streams
- **WorkManager** 2.9.0 - Background tasks

### Testing
- **JUnit 5** - Unit testing
- **Mockito** 5.12.0 & **MockK** 1.13.10 - Mocking
- **Espresso** - UI testing
- **Robolectric** 4.12.1 - Android unit tests
- **Turbine** - Flow testing

### Code Quality
- **ktlint** - Kotlin linter
- **detekt** - Static analysis
- **Dokka** - API documentation

---

## Quick Start (Developers)

1. Open in Android Studio; ensure JDK 17.
2. Add `app/google-services.json`.
3. Run app (debug). Use demo mode to explore key flows.
4. See `docs/developer-onboarding.md` for detailed setup and `docs/demo_quick_start.md` for demo instructions.

## Building

*   **Debug builds**: Standard build process from Android Studio.
*   **Release builds**: Minification is enabled. ProGuard rules are in `app/proguard-rules.pro`.

## Testing

- Unit tests live in `app/src/test/java/`.
- Instrumented tests live in `app/src/androidTest/java/`.
- Libraries: Mockito (core, inline, kotlin), MockK, kotlinx-coroutines-test, Robolectric.
- See `docs/testing-strategy.md` for comprehensive testing approach.

Run Dokka API docs:
```
./gradlew :app:dokkaHtml
```
Output is under `app/build/dokka/html`.

## Project Status

**Current Version**: 1.0.0 (Initial Production Release)  
**Development Status**: ✅ Active Development  
**Stability**: 🟢 Stable

### What's Working

- ✅ User authentication and role-based access
- ✅ Social platform with full feature set
- ✅ Marketplace with auctions and payments
- ✅ Secure transfer system
- ✅ Farm monitoring and analytics
- ✅ Offline-first architecture
- ✅ Push notifications

### Roadmap

See [ROADMAP.md](ROADMAP.md) for detailed future plans

---

## Recent Major Features

See [CHANGELOG.md](CHANGELOG.md) for detailed release notes.

- **Community Engagement System**: Messenger-like hub with context-aware messaging and intelligent recommendations
- **UX Enhancements**: Multi-step wizards, filter presets, guided flows, tooltips, animations
- **Database Migration 15→16**: Added community features with 4 new entities
- **Enhanced Farm Monitoring**: Quick actions, alert system, real-time metrics
- **Improved Testing**: Comprehensive test coverage with Mockito and MockK

---

## Contributing & Code Style

We welcome contributions! Please read our guidelines:

- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Development workflow, PR process, expectations
- **[CODE_STYLE.md](CODE_STYLE.md)** - Coding conventions and best practices

### Quick Contribution Guide

1. Fork the repository
2. Create a feature branch (`feat/your-feature-name`)
3. Make changes following [CODE_STYLE.md](CODE_STYLE.md)
4. Write tests for your changes
5. Submit a pull request with clear description

---

## Community Standards

ROSTRY follows industry-standard community guidelines:

### Code of Conduct

We are committed to providing a welcoming and inclusive environment. Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md).

**Key Principles**:
- Be respectful and inclusive
- Welcome newcomers
- Focus on constructive feedback
- Report unacceptable behavior

### Security

Security is a top priority. Please review our [Security Policy](SECURITY.md) for:
- Supported versions
- Reporting vulnerabilities privately
- Security best practices
- Known security features

**Report security issues**: Do NOT create public issues. Follow the private reporting process in [SECURITY.md](SECURITY.md).

### License

This project is licensed under the **Apache License 2.0**. See [LICENSE](LICENSE) for the full text.

**TL;DR**: You can use, modify, and distribute this software freely, with attribution and under the same license.

---

## Support

### Documentation

- 📚 [Documentation Index](docs/README-docs.md) - Complete documentation hub
- 🚀 [Developer Onboarding](docs/developer-onboarding.md) - Get started quickly
- 🏗️ [Architecture Guide](docs/architecture.md) - System design
- 🧪 [Testing Strategy](docs/testing-strategy.md) - Testing approach
- 🔒 [Security & Encryption](docs/security-encryption.md) - Security details
- 🔥 [Firebase Setup](docs/firebase-setup.md) - Firebase configuration
- 🗄️ [Database Migrations](docs/database-migrations.md) - Schema evolution
- 🚀 [CI/CD Pipeline](docs/ci-cd.md) - Deployment automation

### Getting Help

- **Documentation**: Browse our [comprehensive docs](docs/README-docs.md)
- **Troubleshooting**: Check [troubleshooting.md](docs/troubleshooting.md)
- **Issues**: Report bugs via [GitHub Issues](../../issues)
- **Discussions**: Ask questions in [GitHub Discussions](../../discussions)

### Community

- **Issues**: Bug reports and feature requests
- **Discussions**: Questions and ideas
- **Pull Requests**: Code contributions

---

## Troubleshooting

See [troubleshooting.md](docs/troubleshooting.md) for common issues and fixes.

**Quick Fixes**:

- **Build fails**: Clean project (Build → Clean) and rebuild
- **Gradle sync fails**: Invalidate caches (File → Invalidate Caches / Restart)
- **App crashes**: Check `google-services.json` is present
- **Firebase errors**: Verify Firebase configuration

---

## Acknowledgments

### Contributors

Thank you to all developers who have contributed to ROSTRY! See [Contributors](../../graphs/contributors) for the full list.

### Third-Party Libraries

Built with excellent open-source projects:

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Hilt](https://dagger.dev/hilt/) - Dependency injection
- [Room](https://developer.android.com/training/data-storage/room) - Local database
- [Firebase](https://firebase.google.com/) - Backend infrastructure
- [Retrofit](https://square.github.io/retrofit/) - HTTP client
- [Coil](https://coil-kt.github.io/coil/) - Image loading
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - Charts
- [SQLCipher](https://www.zetetic.net/sqlcipher/) - Encryption

And many more - see `build.gradle.kts` for the complete list.

### Inspiration

Built with ❤️ for the poultry farming community to enhance transparency, traceability, and collaboration.

---

**Made with 🐔 by the ROSTRY Team**