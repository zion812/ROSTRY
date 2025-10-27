# Demo Mode Quick Start Guide

This comprehensive guide explains how to use ROSTRY's built-in demo mode to explore the app's features without network setup or real data. Demo mode provides pre-seeded data for testing, onboarding, and development, simulating a fully functional poultry farming marketplace with realistic scenarios.

## Contents
- Overview
- Demo Mode Architecture
- Available Demo Profiles
- Getting Started
- Use Cases
- Feature Coverage
- Troubleshooting
- Developer Notes
- Resetting and Maintenance

## Overview

Demo mode allows users and developers to experience ROSTRY's full feature set using local, pre-seeded data. It bypasses Firebase authentication and data sync, enabling offline exploration of marketplace listings, transfers, social features, and more. This mode is ideal for:
- New users onboarding to understand app workflows
- Developers testing features without backend dependencies
- Stakeholders evaluating the app's capabilities

Demo mode is controlled by the `DemoModeManager` and integrates with `SessionManager.AuthMode` to toggle between DEMO and LIVE environments.

## Demo Mode Architecture

### Core Components
- **DemoModeManager**: A singleton class managing demo state, feature flags, and environment switching. It uses SharedPreferences to persist settings like enabled status and feature toggles (e.g., payments, marketplace). Key methods include `setEnabled()`, `updateFlags()`, and `setEnvironment()`.
- **SessionManager.AuthMode**: Enum with DEMO and LIVE modes. When set to DEMO, the app uses local Room databases instead of Firebase, ensuring offline functionality.
- **DemoSeeders**: Responsible for populating local databases with realistic demo data. It seeds tables like ProductDao, TransferDao, and PostsDao with role-specific content.
- **MockPaymentSystem**: Simulates payment processing for demo transactions, supporting methods like UPI, COD, and CARD. It ensures idempotency and stores mock PaymentEntity records.

### Mocked vs. Real Features
In demo mode:
- **Mocked**: Payments (via MockPaymentSystem), Firebase auth/data sync, external APIs
- **Real**: Local database operations, UI navigation, offline storage, feature logic (e.g., gamification, traceability)
- **Data Seeding Process**: DemoSeeders now populates 50+ entity types across 12 feature areas, with role-specific data tailored to each profile (farmers get farm monitoring data, enthusiasts get breeding and gamification data, general users get marketplace and social data). Analytics aggregation entities (AnalyticsDailyEntity, FarmerDashboardSnapshotEntity) are seeded last, computing realistic metrics from all previously seeded transactional data. This ensures dashboards display meaningful insights without requiring manual data entry.

This architecture ensures demo mode mirrors production behavior while remaining self-contained.

## Available Demo Profiles

Each demo profile is preloaded with role-specific data to showcase different user journeys. Profiles include marketplace listings, transfer histories, social posts, and feature-specific content. Use these from the auth screen or demo switcher.

| Username          | Password     | Role       | Pre-Seeded Data Highlights | Key Features to Explore | Sample Workflow |
|-------------------|--------------|------------|----------------------------|--------------------------|-----------------|
| `demo_buyer1`     | `password123`| General Buyer | 15 curated marketplace listings (eggs, chicks, feed), 8 completed transfers, 12 social posts from connections, 5 orders (2 delivered, 2 in-transit, 1 pending), 3 cart items, 8 wishlist items, participated in 2 auctions, 12 notifications, 15 social interactions (comments, likes). Dashboard shows 30-day purchase history, engagement trends, spending analytics | Marketplace browsing, transfer receipts, social feed | Search for "organic eggs", view product details, initiate a mock transfer, check transfer history |
| `demo_farmer1`    | `password123`| Farmer     | 10 active live-bird listings, 5 pending transfers, farm monitoring data, breeding records, 30 daily logs, 8 tasks (3 overdue), 15 growth records, 2 quarantine cases, 5 mortality records, 12 vaccination schedules, 2 hatching batches, 4 breeding pairs, 10 orders as seller. Dashboard displays revenue trends (4 weeks), mortality rate chart, hatch success progression, vaccination compliance | Product creation, transfer management, farm analytics | Create a new product listing, monitor farm performance, review breeding lineage, manage outgoing transfers |
| `demo_enthusiast2`| `password123`| Enthusiast | Premium breeder metrics, 20 social connections, gamification achievements, traceability views, 6 mating logs, 8 egg collections, 5 unlocked achievements, 18,500 coin balance with 10 transactions, rank #3 on monthly leaderboard, 4 family trees (3 generations), 2 expert bookings. Dashboard shows breeding success rate (65-85%), transfer analytics, engagement score trends | Community engagement, gamification, advanced traceability | Browse community posts, view achievement badges, explore family trees, connect with breeders |

Each profile's data is tailored to demonstrate role-specific workflows, with realistic relationships (e.g., demo_farmer1's listings appear in demo_buyer1's search results).

## Getting Started

### Signing In with a Demo Account
1. Launch the app and navigate to the authentication screen.
2. Enter one of the demo credentials from the table above.
3. Tap **Sign in with demo credentials**. The app will automatically enable demo mode and seed data.
4. You'll be logged in as the selected profile with pre-loaded data.

### Switching Between Demo Profiles
1. Once signed in, look for the floating **Demo** button (usually in the bottom-right corner of the main screen).
2. Tap it to open the demo switcher overlay.
3. Select **Quick select demo profiles** from the menu.
4. Choose a different profile (e.g., switch from buyer to farmer). The app will sign out the current session and sign in with the new profile, reseeding data accordingly.
5. Alternatively, tap **Sign out** in the switcher to return to the auth screen for manual login.

### Visual Walkthrough of Key Features
- **Marketplace Exploration**: After login, tap the Marketplace tab. Filter by category (e.g., "Chicks"). Open a product detail page to see traceability info and mock "Buy Now" buttons.
- **Transfer Management**: Access the Transfers dashboard. View seeded receipts for completed transactions. As a farmer, initiate a new transfer to simulate logistics.
- **Social Engagement**: Open the Social tab. Browse posts from demo connections. As an enthusiast, view gamification badges in your profile.
- **Farm Monitoring (Farmer Profile)**: Navigate to Farm Analytics. Review seeded performance metrics, vaccination schedules, and breeding data.

Screenshots are not included here; refer to the app's UI for visual guidance during exploration.

## Use Cases

### Exploring the Marketplace as a Buyer
Log in as `demo_buyer1` to experience curated shopping:
- Browse listings for eggs, chicks, and feed.
- Use filters to narrow by price, location, or breeder reputation.
- View product details, including lineage traceability.
- Simulate purchases with mock payments (no real transactions occur).
- Check transfer history for completed deliveries.
- View order history with tracking updates, manage cart and wishlist, participate in live auctions, check coin balance and rewards.

### Managing a Farm as a Farmer
Use `demo_farmer1` for operational workflows:
- Create and manage live-bird listings with photos and descriptions.
- Monitor farm performance via analytics dashboards.
- Review breeding records and family trees for traceability.
- Handle transfer logistics for outgoing shipments.
- Access vaccination reminders and health tracking.
- Log daily activities (feed, water, health checks), complete scheduled tasks, track growth metrics per bird, manage quarantine cases, record mortality events, schedule vaccinations, monitor hatching batches, manage breeding pairs.

### Engaging with Community as an Enthusiast
Switch to `demo_enthusiast2` for social and advanced features:
- Connect with breeders and view premium metrics.
- Earn and display gamification achievements.
- Explore detailed family trees for poultry lineage.
- Participate in community posts and discussions.
- Access personalized recommendations based on interests.
- Track breeding performance (mating logs, egg collections), view unlocked achievements and badges, check leaderboard ranking, explore detailed family trees with lifecycle events, book expert consultations.

### Viewing Analytics Dashboards
Explore role-specific analytics dashboards populated with demo data:
- General users: Navigate to Dashboard to see total orders, spending, engagement score, and AI-powered suggestions
- Farmers: View Dashboard to see revenue trends, farm performance metrics, weekly KPI charts, and actionable insights
- Enthusiasts: Check Dashboard for breeding success rate, transfer count, engagement score, and leaderboard ranking
- All roles: Export dashboard data as CSV for offline analysis

These scenarios demonstrate end-to-end user journeys, highlighting how demo mode supports testing without real data risks.

## Dashboard Metrics Explained

- **General Dashboard**: totalOrders, totalSpend, recentEngagement, suggestions
- **Farmer Dashboard**: revenue, orders, productViews, engagementScore, plus trend charts (revenue, mortality rate, hatch success rate)
- **Enthusiast Dashboard**: breedingSuccessRate, transfers, engagementScore, plus KPI overview bar chart
- Note: All metrics are computed from seeded transactional data (orders, posts, likes, breeding records, etc.)

## Feature Coverage

Demo mode now includes comprehensive seeding for all major app features, providing realistic data across 50+ entity types:

- **Marketplace**: Orders, Cart, Wishlist, Auctions, Bids, Invoices, Payments
- **Farm Monitoring**: Daily Logs, Tasks, Growth Records, Quarantine, Mortality, Vaccination, Hatching, Breeding Pairs
- **Gamification**: Achievements, Coins, Leaderboards
- **Community**: Comments, Likes, Follows, Groups, Events, Chat Messages, Expert Bookings
- **Traceability**: Family Trees, Lifecycle Events
- **Breeding**: Mating Logs, Egg Collections
- **Notifications**: Multi-domain notifications
- **Analytics & Dashboards**
  - AnalyticsDailyEntity: 30 days of aggregated metrics (orders, revenue, engagement, breeding success)
  - FarmerDashboardSnapshotEntity: 4 weeks of trend data (revenue, mortality, hatch success)
  - Role-specific dashboards: General (marketplace metrics), Farmer (farm KPIs + trends), Enthusiast (breeding metrics)
  - ReportEntity: Sample report metadata

This ensures users can explore the full breadth of ROSTRY's capabilities with pre-seeded, interconnected data.

## Troubleshooting

### Common Issues and Solutions
- **Demo Switcher Not Visible**: Ensure you're on the auth screen or signed in with a demo profile. If using a live account, switch via Settings > Demo Mode toggle.
- **Demo Data Not Appearing**: Sign out via the demo switcher, then sign back in with a demo account to trigger reseeding. Check that DemoModeManager.enabled is true.
- **Switching Between Demo and Real Accounts**: Use SessionManager to toggle AuthMode. Demo data persists locally but won't sync to Firebase. Clear app data if conflicts occur.
- **Clearing Demo Data**: Tap Reset in the demo switcher or call DemoModeManager.resetAll() programmatically. This clears SharedPreferences and reseeds on next login.
- **Demo Mode Limitations**: Offline-only; no real payments, external API calls, or data persistence beyond the session. Features like real-time sync are mocked.
- **Crash on Launch**: Verify `app/google-services.json` exists (required for Firebase init, even in demo). Check logs for DemoSeeders errors during seeding.
- **Feature Flags Not Working**: Use DemoModeManager.updateFlags() to toggle (e.g., disable payments for testing). Reset via resetFlags() if issues persist.
- **Demo seeding now takes 2-3 seconds due to comprehensive data population. A loading indicator is shown during profile activation.**
- **If dashboards show zero metrics, ensure you're using the latest demo seeding implementation that includes analytics entity seeding (added in Sprint X).**

If problems continue, consult developer notes or check the codebase for DemoModeManager logs.

## Developer Notes

### Adding New Demo Data
1. Locate `DemoSeeders.kt` in the `data/demo/` package.
2. Update JSON datasets (e.g., add new products or transfers) in the seeder methods.
3. Modify seeding logic to populate additional Room entities (e.g., via ProductDao.insert()).
4. Test by signing out/in to verify reseeding.

### Modifying Demo Seeding Logic
- DemoSeeders uses coroutines to batch-insert data. Customize per-profile seeding in methods like `seedBuyerData()`.
- Integrate with new entities by adding DAO dependencies and insert calls.
- Ensure idempotency: seeding should be safe to rerun without duplicates.

### Testing with Demo Mode
- Enable demo in tests via DemoModeManager.setEnabled(true) and setEnvironment(Environment.DEMO).
- Mock external dependencies (e.g., Firebase) while using real Room operations.
- Verify feature flags with updateFlags() for isolated testing.

### Code References
- **DemoModeManager.kt**: Core demo state management, including Environment enum and FeatureFlags.
- **DemoSeeders.kt**: Data seeding implementation for local databases.
- **MockPaymentSystem.kt**: Payment simulation with idempotency via PaymentDao.
- **SessionManager**: AuthMode toggling for DEMO/LIVE switching.

Demo mode is designed for development; avoid using it in production builds.

## Resetting and Maintenance

### Resetting Demo Data
- **In-App**: Use the demo switcher > Reset to clear all demo state and return to live mode.
- **Programmatic**: Call DemoModeManager.resetAll() to wipe SharedPreferences and local data.
- **Full Reset**: Clear app data from device settings for a complete clean slate.

### Maintenance Notes
- Demo data is local-only and never syncs to production.
- Regularly update seeders to reflect new features (e.g., add gamification data).
- Monitor DemoModeManager logs for seeding performance issues.

For more advanced usage, see the main documentation index at `docs/README-docs.md`.
