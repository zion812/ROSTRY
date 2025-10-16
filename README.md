# ROSTRY Android Application

## ⚡ Quick Links
- 🚀 [5-Minute Quick Start](QUICK_START.md)
- 🛠️ [Developer Cheat Sheet](CHEAT_SHEET.md)
- 📚 [Documentation Index](docs/README-docs.md)
- 🏗️ [Architecture Guide](docs/architecture.md)


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

Temporarily hidden until assets are added. Target path: `docs/images/screenshots/`.

---

## Getting Started

- Use [QUICK_START.md](QUICK_START.md) for 5‑minute local setup and first run.
- Firebase and keys: see `docs/firebase-setup.md` and `docs/api-keys-setup.md`.
- Commands and patterns: see [CHEAT_SHEET.md](CHEAT_SHEET.md).

## Documentation

📚 **Documentation recently reorganized for better navigation.** See `docs/README-docs.md` for the complete index.

- Central index: `docs/README-docs.md`
- Architecture: `docs/architecture.md`
- Testing: `docs/testing-strategy.md`
- Security: `docs/security-encryption.md`
- Feature guides: `docs/ai-personalization.md`, `docs/gamification.md`, `docs/traceability.md`, `docs/worker-catalog.md`, `docs/export-utilities.md`

## Architecture (highlights)

- Clean Architecture + MVVM, Jetpack Compose UI
- Hilt DI, StateFlow for state, Navigation for flows
- Room (SQLCipher) as source of truth; offline‑first
- Firebase (Auth, Firestore, Storage, Functions, FCM)
- WorkManager for background jobs
- More in `docs/architecture.md`

---

## Technology (highlights)

- Compose UI (Material 3), Coil, MPAndroidChart
- Hilt DI, Navigation, Coroutines/Flow
- Room + SQLCipher, DataStore
- Firebase (Auth, Firestore, Storage, Functions, FCM)
- Retrofit/Gson
- Quality: ktlint, detekt, Dokka

---

## Quick Start (Developers)

See [QUICK_START.md](QUICK_START.md). For demo accounts, see `docs/demo_quick_start.md`.

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

- Version: 1.0.0 (stable)
- Active development; see [CHANGELOG.md](CHANGELOG.md)
- Future plans: see [ROADMAP.md](ROADMAP.md)

---

## Recent Major Features

See [CHANGELOG.md](CHANGELOG.md) for detailed release notes.

- **Community Engagement System**: Messenger-like hub with context-aware messaging and intelligent recommendations
- **UX Enhancements**: Multi-step wizards, filter presets, guided flows, tooltips, animations
- **Database Migration 15→16**: Added community features with 4 new entities
- **Enhanced Farm Monitoring**: Quick actions, alert system, real-time metrics
- **Improved Testing**: Comprehensive test coverage with Mockito and MockK
- **New Feature Documentation**: Comprehensive guides for AI/personalization, gamification, traceability, worker catalog, and export utilities

---

## Contributing & Code Style

- Read [CONTRIBUTING.md](CONTRIBUTING.md) for workflow and PR process
- Follow [CODE_STYLE.md](CODE_STYLE.md) and `docs/code-style-quick-reference.md`

---

## Community Standards

We commit to a welcoming and inclusive environment. Please follow the guidelines in
[`CONTRIBUTING.md`](CONTRIBUTING.md) (Code of Conduct summary included).

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

- Docs hub: `docs/README-docs.md`
- Troubleshooting: `docs/troubleshooting.md`
- Issues/Discussions: GitHub

---

## Troubleshooting

See [troubleshooting.md](docs/troubleshooting.md) for common issues and fixes.

**Quick Fixes**:

- **Build fails**: Clean project (Build → Clean) and rebuild
- **Gradle sync fails**: Invalidate caches (File → Invalidate Caches / Restart)
- **App crashes**: Check `google-services.json` is present
- **Firebase errors**: Verify Firebase configuration

---

## Credits

- Contributors: see GitHub contributors graph
- Libraries: see `build.gradle.kts`

---

Made with 🐔 by the ROSTRY Team
