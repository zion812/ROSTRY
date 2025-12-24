---

[Unreleased]: https://github.com/your-org/ROSTRY/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/your-org/ROSTRY/releases/tag/v1.0.0
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

#### Digital Farm - Evolutionary Visuals (2025-12-25)
- Interactive 2.5D isometric farm visualization for Enthusiast users
- Canvas-based rendering with low-poly graphics (sky, clouds, trees, ground, fences)
- Zone-based bird grouping: Nursery, Breeding Unit, Free-Range, Grow-Out, Ready Display, Market Stand
- Idle animations: bird bobbing, cloud parallax, chick orbiting, gold star pulsing
- Hit testing system for tap interactions on birds, huts, and market stand
- Ghost Eggs reminder for breeding units needing egg logs
- Gold star badges for ready-for-sale birds
- Gamified stats bar showing birds, eggs, ready count, and coins
- BirdStatsBubble popup with weight, age, breed, and quick actions
- Database migration 53→54 with 6 new ProductEntity fields
- New files: `DigitalFarmScreen.kt`, `DigitalFarmViewModel.kt`, `FarmCanvasRenderer.kt`, `DigitalFarmModels.kt`
- Documentation: `digital-farm.md`

#### Evidence-Based Order System
- Complete order lifecycle with quotes, payments, delivery confirmation
- Buyer/seller negotiation flow with advance and balance payments
- Delivery OTP verification system
- Evidence collection for payment proofs and delivery photos
- Dispute resolution workflow with admin mediation
- Comprehensive audit logging
- Documentation: `EVIDENCE_ORDER_SYSTEM.md`

#### Community Engagement System
- Messenger-like community hub with 4-tab interface (Messages, Discover, Feed, My Groups)
- Context-aware messaging with 4 types: PRODUCT_INQUIRY, EXPERT_CONSULT, BREEDING_DISCUSSION, GENERAL
- Intelligent recommendation system (mentors, connections, groups, events, experts, posts)
- Thread metadata tracking with visual context badges
- Expert profiles with specialties, ratings, and booking integration
- Background worker (CommunityEngagementWorker) for 12-hour personalized recommendations
- User interest tracking for personalized content
- Community metrics and engagement scoring
- See `docs/social-platform.md` for details

#### UX Enhancements
- Multi-step wizard pattern for complex forms (FarmerCreateScreen with 4 steps: BASICS → DETAILS → MEDIA → REVIEW)
- Filter presets system for marketplace (Nearby & Verified, Traceable Only, Budget Friendly, Premium)
- Guided transfer flow with product/recipient pickers and confirmation steps
- Reusable validation utilities (`utils/FormValidationHelpers.kt`)
- Loading state components with skeleton loaders (`ui/components/LoadingStates.kt`)
- Onboarding tooltip system for first-time users (`ui/components/OnboardingTooltips.kt`)
- Success animations with haptic feedback (`ui/components/SuccessAnimations.kt`)
- Contextual help system with collapsible inline help (`ui/components/HelpComponents.kt`)
- Form field validation with real-time error feedback
- Celebration dialogs for milestone achievements
- See `docs/user-experience-guidelines.md` for patterns

#### Farm Monitoring Dashboard
- Quick action cards for common tasks
- Alert system with urgency indicators
- Real-time metrics display
- Enhanced filter and preset functionality
- Compact filter bar with active count badges

#### Infrastructure
- DefaultPaymentGateway: simple development/test implementation of PaymentGateway
- Hilt PaymentModule: provides PaymentGateway via DefaultPaymentGateway
- Dokka instructions in README

### Changed

#### Database
- Migration 15→16 adding 4 new entities:
  - `ThreadMetadataEntity` for thread context tracking
  - `CommunityRecommendationEntity` for personalized suggestions
  - `UserInterestEntity` for interest-based personalization
  - `ExpertProfileEntity` for expert information
- Added indices for query optimization on context types and timestamps

#### Repositories
- Enhanced `MessagingRepository` with context-aware thread creation methods
- Added `CommunityRepository` for groups, events, and community features
- Updated `CommunityEngagementService` with 20+ data aggregation methods

#### ViewModels
- `FarmerCreateViewModel`: Added wizard state management with step-by-step validation
- `GeneralMarketViewModel`: Added filter presets and auto-location detection
- `TransferCreateViewModel`: Added guided transfer flow with product/recipient selection
- `ThreadViewModel`: Enhanced with thread metadata streaming
- Added `FarmerCommunityViewModel` and `CommunityHubViewModel` for community features

#### UI Screens
- `FarmerCreateScreen`: Transformed from 500-line form to 4-step wizard
- `GeneralMarketRoute`: Added compact filter bar with quick presets
- `TransferCreateScreen`: Replaced plain fields with guided selector flow
- `ThreadScreen`: Added context badges and enhanced message display
- `FarmerCommunityScreen`: Integrated real ViewModels with loading/empty states

#### Navigation
- Added CommunityHub routes with parameterized helper functions
- Enhanced deep-link support for community features

#### README
- Refreshed architecture overview
- Updated testing stack documentation
- Added payments & DI section
- Consolidated recent changes

### Documentation
- Updated all documentation dates to 2025-10-29
- Removed "Coming soon" labels from completed feature docs (AI personalization, gamification, traceability, worker catalog, export utilities)
- Corrected PersonalizationWorker schedule documentation (6 hours, not 12 hours)
- Added implementation status notes to feature documentation
- Enhanced README.md with clearer feature guide descriptions
- Incremented documentation version to 3.1

### Deprecated
- (none)

### Removed
- (none)

### Fixed

#### Bug Fixes
- Infinite flow collections in `CommunityRepository` (added direct DAO query methods)
- Firebase bypass in thread creation (refactored to use `MessagingRepository.createThreadWithContext()`)
- Empty trending posts issue (implemented `PostsDao.getTrending()` with engagement-based SQL)
- Wizard state mutations causing validation errors
- Marketplace filter state management issues
- Transfer screen confirmation step state handling

#### Code Quality
- SyncManager.withRetry now throws on last attempt (removed illegal `break`)
- AppFirebaseMessagingService now uses concrete notifier implementations and proper methods
- FarmMonitoringScreen removed internal RowScope weight usage causing build failures
- FarmMonitoringViewModel field names aligned to MonitoringSummary (`vaccinationDue`, `vaccinationOverdue`) and simplified combine
- GeneralMarketViewModel implemented `observeProducts()` and `applyFilters()` and aligned age group mapping

#### Testing
- Added Mockito dependencies for legacy tests
- Supplied required `shippingAddress` in OrderEntity test cases
- Used TransferDao.getById for proper async testing
- Fixed stubbing suspend functions with runBlocking

### Security
- Enhanced transfer system with Cloud Functions for secure initiation
- Rate limiting for transfer requests (60-second cooldown)
- Duplicate transfer prevention and ownership verification
- Automated audit trail for all transfer actions

---

## [1.0.0] - 2024-09-30

Initial production release with core features:
- User authentication and role-based access control
- Social platform (posts, comments, groups, events)
- Marketplace with auctions and secure payments
- Secure fowl transfer system with verification
- Comprehensive farm monitoring tools
- Advanced analytics dashboards
- Real-time notifications
- Offline-first architecture

---

Prior to v1.0.0, development history is available in git commits.
