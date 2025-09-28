# Developer Onboarding Guide

Welcome to ROSTRY! This guide helps you get productive quickly.

## Setup Checklist

- Android Studio (latest stable), JDK 17.
- Clone repo; sync Gradle.
- Firebase: add `app/google-services.json`.
- Optional: enable demo mode to explore features.

## IDE & Tools

- Recommended plugins: Kotlin, Jetpack Compose tooling, Ktlint.
- Run configurations for debug/staging.

## Key Concepts

- MVVM, Hilt DI, Room, WorkManager, Firebase integrations.
- Navigation and role-based start destinations.

## First Run

- Build and run on emulator/device.
- Use seeded/demo data where available.

## Common Tasks

- Add a screen: create composable + ViewModel, add route.
- Add a repository: define interface, implement, bind in Hilt.
- Add a worker: extend CoroutineWorker, add Hilt injection, schedule.

## Debugging & Testing

- Use Timber logs and network inspector.
- Run unit and instrumentation tests.

## Next Steps

- Read `docs/README-docs.md` and `README.md`.
- Follow `CONTRIBUTING.md` and `CODE_STYLE.md`.
