# ROSTRY Demo Mode – Field Testing Guide

This guide explains how to run ROSTRY in Demo Mode for robust field testing without real financial transactions or external dependencies.

## Overview
- Demo Mode provides realistic sample data, simulated payments, offline/network controls, location simulation for Andhra Pradesh, guided tours, and a lightweight feedback channel.
- Use the in‑app Demo & Testing tools to toggle features and execute testing scenarios.

## Key Components
- **DemoModeManager**: Global on/off and per‑feature flags.
- **MockPaymentSystem**: Simulates UPI, Card, and COD payment flows and generates invoices.
- **DemoNetworkController** + **NetworkQualityMonitor**: Toggle Offline/2G/3G/4G/5G/Wi‑Fi.
- **LocationSimulation**: Mock GPS route across AP waypoints; radius checks.
- **DemoDataCoordinator**: Seeds realistic profiles, listings, posts, transfers.
- **GuidedTourManager**: Walkthrough for main workflows.
- **FeedbackManager**: In‑app feedback (BUG/SUGGESTION/PERFORMANCE).

## Persistence & Environment
- Demo settings are persisted across app restarts via `DemoModeManager` (SharedPreferences).
- Environment can be switched between **LIVE** and **DEMO**; switching to DEMO auto‑enables Demo Mode for convenience.
- You can reset all demo settings from the tools screen (resets flags, enabled state, and environment).

## Getting Started
1. Open the app and sign in (demo or normal auth). 
2. Open the **Demo & Testing** tools (debug menu or account menu entry, depending on build).
3. Toggle **Enable Demo Mode**.

## Feature Flags
Enable/disable demo subsystems independently:
- Payments, Social, Marketplace, Transfers, Monitoring, Analytics, Location, Offline.

## Seeding Data
- Tap **Seed All Profiles** to load realistic demo users and content.
- Or seed **General**, **Farmer**, **Enthusiast** individually.

## Payments (Mock)
- In Demo Mode, all payment initiations are simulated by the mock system:
  - UPI, Card, and COD will create `PaymentEntity` with `SUCCESS` by default.
  - A simple `InvoiceEntity` is generated automatically.
- Use your existing order/checkout flows to validate UI and downstream states.

## Offline & Network Simulation
- Use **Go Offline** to disable connectivity in‑app (no OS changes).
- Switch to **Wi‑Fi** or specific qualities **2G/3G/4G/5G** to test media quality/autoplay.
- Under **Offline Testing**:
  - **Run Sync Now**: triggers the one‑shot `SyncWorker`.
  - **Run Outgoing (no net)**: enqueues `OutgoingMessageWorker` without requiring network.
  - **Run Outgoing (net)**: enqueues `OutgoingMessageWorker` requiring network.

### Recommended Offline Scenario
1. Go Offline.
2. Browse cached content (feeds, marketplace) to validate resilience.
3. Compose a direct message or group message; it will queue locally.
4. Switch to Wi‑Fi.
5. Tap **Run Outgoing (net)** to flush queued messages.
6. Tap **Run Sync Now** to sync other data.

## Location Simulation (Andhra Pradesh)
- The app provides a mock route through AP waypoints: Vijayawada, Guntur, Visakhapatnam, Rajahmundry area, Kurnool.
- Use this to validate delivery radius checks, region‑based content, and hub tests.
- (Optional) Integrate a map or coordinate display if needed for demos.

## Guided Tour
- Start/prev/next/stop a default tour that highlights primary workflows:
  - Marketplace, Payments, Offline, Social, Monitoring/Analytics.

## Feedback
- Submit quick feedback events during field tests:
  - Types: **BUG**, **SUGGESTION**, **PERFORMANCE**.
  - These are captured in‑app for later aggregation.

## Field Testing Scenarios
- **All roles complete primary workflows**
  - General buyer: browse, order, (mock) pay, message, track.
  - Farmer: list, manage, community, transfers.
  - Enthusiast: analytics, transfers, traceability, community.
- **Payments**
  - UPI/CARD/COD simulated flows; verify status transitions and invoices.
- **Offline**
  - Cached browsing, queued actions, successful sync when online.
- **Social engagement**
  - Create/like/comment; verify rank/paging; test media autoplayer under different network qualities.
- **Farm monitoring**
  - Validate dashboard and analytics screens with seeded data.
- **Transfer system**
  - Try demo transfers and verify status/trust interactions.

## Troubleshooting
- If seeded content doesn’t appear:
  - Ensure **Enable Demo Mode** is on.
  - Tap **Seed All Profiles** again.
  - **Run Sync Now** to refresh local‑to‑UI pipelines.
- If network toggle seems ineffective:
  - Tap **Clear** under Network Simulation, then apply the desired quality again.
- If payment flows look real:
  - Confirm **Enable Demo Mode**; demo mode routes PaymentRepository to MockPaymentSystem.

## Notes
- Demo Mode is a runtime toggle; no separate build flavor is required.
- You may optionally add a `demo` product flavor later (applicationIdSuffix/versionNameSuffix) to isolate analytics/crashlytics.

## File References
- `app/src/main/java/com/rio/rostry/demo/DemoModeManager.kt`
- `app/src/main/java/com/rio/rostry/demo/MockPaymentSystem.kt`
- `app/src/main/java/com/rio/rostry/demo/DemoNetworkController.kt`
- `app/src/main/java/com/rio/rostry/demo/LocationSimulation.kt`
- `app/src/main/java/com/rio/rostry/demo/DemoDataCoordinator.kt`
- `app/src/main/java/com/rio/rostry/demo/GuidedTourManager.kt`
- `app/src/main/java/com/rio/rostry/demo/FeedbackManager.kt`
- `app/src/main/java/com/rio/rostry/demo/DemoWorkController.kt`
- `app/src/main/java/com/rio/rostry/ui/demo/DemoToolsViewModel.kt`
- `app/src/main/java/com/rio/rostry/ui/demo/DemoToolsScreen.kt`
  
