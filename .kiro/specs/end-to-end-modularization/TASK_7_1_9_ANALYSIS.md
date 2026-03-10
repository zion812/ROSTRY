# Task 7.1.9 Analysis: Remaining Screens to Migrate

## Overview
This document analyzes all remaining screens in `app/src/main/java/com/rio/rostry/ui/` that haven't been migrated to feature modules yet.

## Migration Status Summary

### ✅ Already Migrated (Tasks 7.1.1-7.1.8)
- **7.1.1**: Farmer screens → feature/farmer-tools
- **7.1.2**: Enthusiast screens → feature/enthusiast-tools (116 files)
- **7.1.3**: Admin screens → feature/admin-dashboard (62 files)
- **7.1.4**: Analytics screens → feature/analytics (14 files)
- **7.1.5**: Monitoring screens → ⏭️ Skipped (load issues)
- **7.1.6**: Marketplace screens → feature/marketplace (5 files)
- **7.1.7**: Social screens → ⏭️ Blocked (circular dependencies)
- **7.1.8**: Profile screens → ⏭️ Blocked (circular dependencies)

## Remaining Screens Analysis

### Category 1: Auction Screens → feature/marketplace
**Location**: `app/ui/auction/`
**Files**: 5 files
- AuctionScreen.kt
- AuctionViewModel.kt
- CreateAuctionScreen.kt
- CreateAuctionViewModel.kt
- QuickBidComponents.kt

**Target Module**: feature/marketplace (auctions are part of commerce)
**Complexity**: Medium - Auction functionality is marketplace-related

### Category 2: Community Screens → feature/community
**Location**: `app/ui/community/`
**Files**: 5 files
- CommunityHubScreen.kt
- CommunityHubViewModel.kt
- EventDetailScreen.kt
- ExpertProfileScreen.kt
- GroupDetailScreen.kt

**Target Module**: feature/community (module already exists)
**Complexity**: Medium - Community features

### Category 3: Messaging Screens → feature/social-feed or new feature/messaging
**Location**: `app/ui/messaging/`
**Files**: 4 files
- GroupChatScreen.kt
- GroupChatViewModel.kt
- ThreadScreen.kt
- ThreadViewModel.kt

**Target Module**: feature/social-feed OR create new feature/messaging
**Complexity**: Medium - Social messaging functionality
**Note**: May have circular dependencies with social

### Category 4: Moderation Screens → feature/moderation
**Location**: `app/ui/moderation/`
**Files**: 2 files
- ModerationScreen.kt
- ModerationViewModel.kt

**Target Module**: feature/moderation (module already exists)
**Complexity**: Low - Admin moderation features

### Category 5: Monitoring Screens → feature/monitoring
**Location**: `app/ui/monitoring/`
**Files**: 22 files (LARGE)
- BatchHierarchyScreen.kt + ViewModel
- BatchSplitScreen.kt + ViewModel
- BreedingManagementScreen.kt
- BreedingPerformanceScreen.kt
- DailyLogScreen.kt
- FarmMonitoringScreen.kt
- FarmPerformanceScreen.kt
- FCRCalculatorScreen.kt
- GrowthRecordDetailScreen.kt + ViewModel
- GrowthTrackingScreen.kt
- HatchingProcessScreen.kt
- MortalityDetailScreen.kt + ViewModel
- MortalityTrackingScreen.kt
- QuarantineManagementScreen.kt
- TasksScreen.kt
- VaccinationDetailScreen.kt + ViewModel
- VaccinationScheduleScreen.kt

**Target Module**: feature/monitoring (module already exists)
**Complexity**: HIGH - Large number of screens, was skipped in 7.1.5 due to load issues
**Note**: This was intentionally skipped before - may need special handling

### Category 6: Order Screens → feature/orders
**Location**: `app/ui/order/`
**Files**: 4 files + evidence subdirectory
- NegotiationDialog.kt
- OrderTrackingScreen.kt
- OrderTrackingViewModel.kt
- QuickOrderSheet.kt
- evidence/ subdirectory (unknown contents)

**Target Module**: feature/orders (module already exists)
**Complexity**: Medium - Commerce order management

### Category 7: Product Screens → feature/marketplace
**Location**: `app/ui/product/`
**Files**: 2 files
- ProductDetailsScreen.kt
- ProductDetailsViewModel.kt

**Target Module**: feature/marketplace (product details are marketplace-related)
**Complexity**: Low - Product viewing functionality

### Category 8: Profile Screens → feature/profile
**Location**: `app/ui/profile/`
**Files**: 6 files
- EditProfileScreen.kt
- ProfileEditScreen.kt
- ProfileEditViewModel.kt
- ProfileScreen.kt
- ProfileViewModel.kt
- StorageQuotaScreen.kt
- StorageQuotaViewModel.kt

**Target Module**: feature/profile (module already exists)
**Complexity**: Medium - Was blocked in 7.1.8 due to circular dependencies
**Note**: May still have circular dependency issues

### Category 9: Public Access Screens → feature/general
**Location**: `app/ui/publicaccess/`
**Files**: 2 files
- PublicBirdLookupScreen.kt
- PublicBirdLookupViewModel.kt

**Target Module**: feature/general (public-facing features)
**Complexity**: Low - Public lookup functionality

### Category 10: Scan Screens → feature/traceability
**Location**: `app/ui/scan/`
**Files**: 2 files
- QrScannerScreen.kt
- QrScannerViewModel.kt

**Target Module**: feature/traceability (QR scanning for traceability)
**Complexity**: Low - QR code scanning

### Category 11: Settings Screens → feature/profile or new feature/settings
**Location**: `app/ui/settings/`
**Files**: 6 files
- AddressManagementScreen.kt
- AddressManagementViewModel.kt
- AddressSelectionWebViewScreen.kt
- BackupRestoreScreen.kt
- BackupRestoreViewModel.kt
- SettingsScreen.kt
- SettingsViewModel.kt

**Target Module**: feature/profile OR create new feature/settings
**Complexity**: Medium - User settings and preferences

### Category 12: Social Screens → feature/social-feed
**Location**: `app/ui/social/`
**Files**: 6 files + subdirectories
- LeaderboardViewModel.kt
- LiveBroadcastScreen.kt
- SocialFeedScreen.kt
- SocialFeedViewModel.kt
- StoriesRow.kt
- VideoPlayer.kt
- discussion/ subdirectory
- profile/ subdirectory (SocialProfileScreen.kt)
- stories/ subdirectory (StoryViewerScreen.kt)

**Target Module**: feature/social-feed (module needs to be created)
**Complexity**: HIGH - Was blocked in 7.1.7 due to circular dependencies
**Note**: May still have circular dependency issues

### Category 13: Splash Screen → app module (KEEP)
**Location**: `app/ui/splash/`
**Files**: 1 file
- SplashScreen.kt

**Target Module**: KEEP IN APP MODULE (app shell responsibility)
**Complexity**: N/A - Should remain in app
**Reason**: Splash screen is part of the app shell, not a feature

### Category 14: Start Flow → feature/onboarding or app module
**Location**: `app/ui/start/`
**Files**: 2 files
- StartFlow.kt
- StartViewModel.kt

**Target Module**: feature/onboarding OR keep in app module
**Complexity**: Low - Initial app flow
**Note**: May be app shell responsibility

### Category 15: Sync Screens → new feature/sync or feature/general
**Location**: `app/ui/sync/`
**Files**: 2 files
- SyncConflictScreen.kt
- SyncIssuesScreen.kt

**Target Module**: Create feature/sync OR feature/general
**Complexity**: Low - Sync conflict resolution

### Category 16: Traceability Screens → feature/traceability
**Location**: `app/ui/traceability/`
**Files**: 6 files
- FamilyTreeView.kt
- LineagePreviewScreen.kt
- LineagePreviewViewModel.kt
- NodeEventTimelineSheet.kt
- TraceabilityScreen.kt
- TraceabilityViewModel.kt

**Target Module**: feature/traceability (module already exists)
**Complexity**: Medium - Traceability and lineage tracking

### Category 17: Transfer Screens → feature/transfers
**Location**: `app/ui/transfer/`
**Files**: 5 files
- TransferCreateScreen.kt
- TransferCreateViewModel.kt
- TransferDetailsViewModel.kt
- TransferVerificationScreen.kt
- TransferVerificationViewModel.kt

**Target Module**: feature/transfers (module already exists)
**Complexity**: Medium - Asset transfer management

### Category 18: Upgrade Screens → feature/onboarding or feature/profile
**Location**: `app/ui/upgrade/`
**Files**: 4 files
- MigrationProgressDialog.kt
- RoleUpgradePostOnboardingScreen.kt
- RoleUpgradeScreen.kt
- RoleUpgradeViewModel.kt

**Target Module**: feature/onboarding (role upgrade is onboarding-related)
**Complexity**: Low - Role upgrade functionality

### Category 19: Verification Screens → feature/onboarding
**Location**: `app/ui/verification/`
**Files**: 4 files + subdirectories
- EnthusiastKycScreen.kt
- FarmerLocationVerificationScreen.kt
- VerificationConstants.kt
- VerificationStatusScreen.kt
- VerificationViewModel.kt
- components/ subdirectory
- state/ subdirectory

**Target Module**: feature/onboarding (verification is part of onboarding)
**Complexity**: Medium - KYC and verification flows

### Category 20: General Screens → feature/general
**Location**: `app/ui/general/`
**Files**: Multiple subdirectories
- analytics/ subdirectory
- cart/ subdirectory
- create/ subdirectory
- discover/ subdirectory
- explore/ subdirectory
- market/ subdirectory
- profile/ subdirectory
- tabs/ subdirectory
- wishlist/ subdirectory
- GeneralUserScreen.kt

**Target Module**: feature/general (module already exists)
**Complexity**: HIGH - Multiple subdirectories with general user features

### Category 21: Asset Screens → feature/asset-management
**Location**: `app/ui/asset/`
**Files**: Multiple subdirectories
- health/ subdirectory
- logging/ subdirectory
- management/ subdirectory
- screens/ subdirectory

**Target Module**: feature/asset-management (module already exists)
**Complexity**: Medium - Asset management features

### Category 22: Screens Directory → Various
**Location**: `app/ui/screens/`
**Files**: 4 files
- GeneralHomeViewModel.kt
- HomeEnthusiastScreen.kt
- HomeFarmerScreen.kt
- HomeGeneralScreen.kt
- PlaceholderScreen.kt

**Target Module**: Various (enthusiast-tools, farmer-tools, general)
**Complexity**: Low - Home screens for different user types

## Categories to SKIP (Keep in App Module)

### Infrastructure/Shared Components
- **app/ui/base/** - BaseViewModel (shared base class)
- **app/ui/components/** - Shared UI components (used across features)
- **app/ui/navigation/** - App-level navigation (AppNavHost, etc.)
- **app/ui/main/** - MainViewModel (app shell)
- **app/ui/security/** - AdminAccessGuard (app-level security)
- **app/ui/session/** - SessionViewModel (app-level session)
- **app/ui/shared/** - Shared gallery components
- **app/ui/utils/** - HapticFeedbackUtils (shared utilities)
- **app/ui/accessibility/** - Accessibility extensions (shared)
- **app/ui/animations/** - MicroInteractions (shared)

### ViewModels Without Screens
- **app/ui/events/** - EventsViewModel (no screen)
- **app/ui/expert/** - ExpertViewModel (no screen)
- **app/ui/feedback/** - FeedbackViewModel (no screen)
- **app/ui/gamification/** - AchievementsViewModel (no screen)
- **app/ui/insights/** - InsightsViewModel (no screen)
- **app/ui/notifications/** - NotificationsViewModel (no screen)
- **app/ui/search/** - ProductSearchViewModel (no screen)

## Migration Priority

### HIGH PRIORITY (Low Complexity, Clear Target)
1. ✅ Auction screens → feature/marketplace (5 files)
2. ✅ Product screens → feature/marketplace (2 files)
3. ✅ Moderation screens → feature/moderation (2 files)
4. ✅ Public access screens → feature/general (2 files)
5. ✅ Scan screens → feature/traceability (2 files)
6. ✅ Traceability screens → feature/traceability (6 files)
7. ✅ Transfer screens → feature/transfers (5 files)
8. ✅ Upgrade screens → feature/onboarding (4 files)
9. ✅ Verification screens → feature/onboarding (4+ files)
10. ✅ Sync screens → feature/general (2 files)

### MEDIUM PRIORITY (Medium Complexity)
11. ✅ Community screens → feature/community (5 files)
12. ✅ Messaging screens → feature/social-feed (4 files)
13. ✅ Order screens → feature/orders (4+ files)
14. ✅ Settings screens → feature/profile (6 files)
15. ✅ Home screens → Various feature modules (4 files)

### LOW PRIORITY (High Complexity or Blocked)
16. ⏭️ Monitoring screens → feature/monitoring (22 files) - SKIP (was skipped in 7.1.5)
17. ⏭️ Profile screens → feature/profile (6 files) - SKIP (blocked in 7.1.8)
18. ⏭️ Social screens → feature/social-feed (6+ files) - SKIP (blocked in 7.1.7)
19. ⏭️ General screens → feature/general (multiple subdirs) - SKIP (too complex)
20. ⏭️ Asset screens → feature/asset-management (multiple subdirs) - SKIP (too complex)

## Recommended Approach for Task 7.1.9

Execute HIGH PRIORITY migrations only (items 1-10):
- Total: ~34 files
- Clear target modules
- Low circular dependency risk
- Straightforward migrations

Skip MEDIUM and LOW PRIORITY items:
- Either blocked by circular dependencies
- Too complex for this task
- Already skipped in previous tasks
- Should be handled in separate focused tasks

## Expected Outcome

After completing HIGH PRIORITY migrations:
- ~34 files moved from app module to feature modules
- App module reduced by ~1,500-2,000 lines of code
- Clear progress toward thin shell architecture
- No new circular dependency issues introduced
