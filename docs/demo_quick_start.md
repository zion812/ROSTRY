# Demo Mode Quick Start Guide

This guide explains how to use the built-in demo accounts to explore the ROSTRY app without requiring a network connection.

## Available Demo Profiles

Every profile is preloaded with role-specific marketplace listings, transfer history, and social activity. Use these logins from the authentication screen or the in-app demo switcher.

| Username          | Password     | Role       | Highlights                                      |
|-------------------|--------------|------------|--------------------------------------------------|
| `demo_buyer1`     | `password123`| General    | Curated marketplace finds and transfer receipts |
| `demo_farmer1`    | `password123`| Farmer     | Active live-bird listings and logistics notes    |
| `demo_enthusiast2`| `password123`| Enthusiast | Premium breeder metrics and social connections   |

## Starting the Demo

1. Launch the app. On the auth screen, enter any of the demo credentials above and tap **Sign in with demo credentials**.
2. Alternatively, open the **Quick select demo profiles** list to jump directly into a profile without typing.
3. Once signed in, use the floating **Demo** button to switch between profiles or sign out.

## Offline Experience

Demo mode seeds local Room tables with:

- **Marketplace data** (`ProductDao` via `DemoSeeders`) for listings, categories, and detail views.
- **Transfer history** (`TransferDao`) showcasing completed and pending transactions.
- **Social feed** (`PostsDao`) with contextual posts derived from demo connections.

All data stays available offline; Firebase calls are bypassed while `SessionManager.AuthMode` is `DEMO`.

## What to Explore

- **Marketplace tabs**: Filter listings, open product detail pages, and observe traceability hooks.
- **Transfers dashboard**: Review past transactions and states seeded for each role.
- **Social and messaging**: Browse seeded posts and confirm role-aware navigation works.

## Resetting the Demo

Use the **sign out** option in the demo switcher to clear the current session. Logging back in with a demo profile re-seeds the data so you always start from a clean, consistent snapshot.

## Notes

- Demo content stays local to the device and never syncs to production Firebase projects.
- Switching to a real Firebase authentication flow will use live data sources again.
