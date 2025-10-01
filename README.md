# ROSTRY Android Application

ROSTRY is an AgriTech marketplace application built with modern Android development practices.

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-—-informational)
![License](https://img.shields.io/badge/license-Apache--2.0-blue)

## Project Setup

1.  **Clone the repository.**
2.  **Firebase Setup**: Download your project's `google-services.json` file from the Firebase console and place it in the `ROSTRY/app/` directory.
3.  **Build the project**: Open the project in Android Studio. It should sync Gradle automatically. If not, trigger a manual Gradle sync.

## Documentation

Refer to the [Documentation Index](docs/README-docs.md) for detailed guides on architecture, feature domains, data contracts, and operational practices.

Key docs:
- `docs/feature-toggles.md`
- `docs/security-encryption.md`
- `docs/background-jobs.md`
- `docs/notification-system.md`
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

See `docs/architecture.md` for comprehensive architecture documentation.

## Features Overview

- **Social Platform**: Posts, comments, messaging, groups, events, community engagement hub.
- **Marketplace**: Listings, auctions, payments, refunds, filter presets.
- **Transfers**: Secure ownership transfers with guided flow, verification, and audit.
- **Farm Monitoring**: Growth, vaccination, hatching, analytics, quick actions, alerts.
- **Advanced Analytics**: Dashboards, performance insights, AI recommendations, exports.
- **Notifications**: Push and local notifications with deep links.
- **UX Enhancements**: Multi-step wizards, tooltips, animations, contextual help.

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

## Recent Major Features

See `CHANGELOG.md` for detailed release notes.

- **Community Engagement System**: Messenger-like hub with context-aware messaging and intelligent recommendations.
- **UX Enhancements**: Multi-step wizards, filter presets, guided flows, tooltips, animations.
- **Database Migration 15→16**: Added community features with 4 new entities.
- **Enhanced Farm Monitoring**: Quick actions, alert system, real-time metrics.
- **Improved Testing**: Comprehensive test coverage with Mockito and MockK.

## Contributing & Code Style

- See `CONTRIBUTING.md` for workflow and expectations.
- See `CODE_STYLE.md` for style conventions.

## Troubleshooting

See `docs/troubleshooting.md` for common issues and fixes.

## License

This project is licensed under the Apache 2.0 License.