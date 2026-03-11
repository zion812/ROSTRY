# ROSTRY INVESTIGATION REPORT - PART 4
## Farmer Tools, Monitoring & More Loose Files

---

## 🟠 HIGH PRIORITY ISSUE #1 (CONTINUED): LOOSE FILES

### Farmer Tools (60+ files)
```
Should be: feature/farmer-tools/src/.../

Files:
- FarmerHomeScreen.kt
- FarmerHomeViewModel.kt
- FarmerHomeWidgets.kt
- FarmerMarketScreen.kt
- FarmerMarketViewModel.kt
- FarmerCreateScreen.kt
- FarmerCreateViewModel.kt
- FarmerCreateModels.kt
- FarmerCreateUiStateAlias.kt
- FarmerProfileScreen.kt
- FarmerProfileViewModel.kt
- FarmerCommunityScreen.kt
- FarmerCommunityViewModel.kt
- FarmerTransfersScreen.kt
- FarmerTransfersViewModel.kt
- FarmerNavigation.kt
- FarmerToolsNavigationProvider.kt
- FarmerEditProfileSheet.kt
- FarmerLocationVerificationScreen.kt
- AssetManagementScreen.kt
- AssetManagementViewModel.kt
- AssetManagementNavigation.kt
- AssetDocumentScreen.kt
- AssetDocumentViewModel.kt
- CreateListingFromAssetScreen.kt
- CreateListingScreen.kt
- CreateListingViewModel.kt
- DigitalFarmScreen.kt
- DigitalFarmViewModel.kt
- DigitalFarmPipeline.kt
- FarmCanvasRenderer.kt
- IsometricFarmCanvas.kt
- IsometricGrid.kt
- ZoneManager.kt
- WeatherEffectsRenderer.kt
- FlockingEngine.kt
- DragInteractionHandler.kt
- PublicFarmProfileScreen.kt
- PublicFarmProfileViewModel.kt
- FarmVerificationScreen.kt
- FarmVerificationViewModel.kt
- (and more...)
```

### Monitoring Features (40+ files)
```
Should be: feature/monitoring/src/.../

Files:
- FarmCalendarScreen.kt
- FarmCalendarViewModel.kt
- FarmLogScreen.kt
- FarmLogViewModel.kt
- FarmMonitoringViewModel.kt
- FarmPerformanceViewModel.kt
- FarmActivityDetailScreen.kt
- FarmActivityDetailViewModel.kt
- DailyLoggingViewModel.kt
- DailyLogViewModel.kt
- GrowthViewModel.kt
- GrowthRecordDetailViewModel.kt
- HealthLogScreen.kt
- HealthLogViewModel.kt
- HealthRecordsViewModel.kt
- VaccinationViewModel.kt
- VaccinationDetailViewModel.kt
- MortalityViewModel.kt
- MortalityDetailViewModel.kt
- QuarantineViewModel.kt
- HatchingViewModel.kt
- FCRCalculatorViewModel.kt
- BatchHierarchyViewModel.kt
- BatchSplitViewModel.kt
- BatchTaggingDialog.kt
- OnboardFarmBatchScreen.kt
- OnboardFarmBatchViewModel.kt
- OnboardFarmBirdScreen.kt
- OnboardFarmBirdViewModel.kt
- MonitoringNavigationProvider.kt
- (and more...)
```

### Social & Community (25+ files)
```
Should be: feature/social-feed/src/.../

Files:
- GroupChatScreen.kt
- GroupChatViewModel.kt
- GroupsScreen.kt
- ThreadScreen.kt
- ThreadViewModel.kt
- SocialFeedViewModel.kt
- SocialProfileViewModel.kt
- SocialNavigationProvider.kt
- FarmerCommunityScreen.kt
- FarmerCommunityViewModel.kt
- UserDiscoverySheet.kt
- (and more...)
```

### Transfers & Traceability (20+ files)
```
Should be: feature/transfers/ and feature/traceability/

Files:
- ClaimTransferScreen.kt
- ClaimTransferViewModel.kt
- TransferCodeScreen.kt
- TransferCodeViewModel.kt
- TransferResponseScreen.kt
- TransferResponseViewModel.kt
- TransferFilterCard.kt
- TransferItemCard.kt
- TransferStatsCard.kt
- LineageExplorerScreen.kt
- LineageExplorerViewModel.kt
- (and more...)
```

### Orders & Marketplace (15+ files)
```
Should be: feature/orders/ and feature/marketplace/

Files:
- CreateEnquiryScreen.kt
- OrderTrackingScreen.kt
- OrderTrackingViewModel.kt
- PaymentDeliveryScreens.kt
- PaymentVerifyScreen.kt
- OrderReviewScreen.kt
- DisputeScreens.kt
- (and more...)
```

### Onboarding & Verification (20+ files)
```
Should be: feature/onboarding/

Files:
- OnboardingScreen.kt
- OnboardingViewModel.kt
- OnboardingNavigation.kt
- OnboardingChecklistViewModel.kt
- UserOnboardingTourScreen.kt
- VerificationStatusScreen.kt
- VerificationViewModel.kt
- VerificationConstants.kt
- VerificationFormState.kt
- VerificationUploadComponents.kt
- RoleUpgradePostOnboardingScreen.kt
- (and more...)
```

### UI Components & Utilities (30+ files)
```
Should be: core/designsystem/ or feature-specific

Files:
- AnimatedTabRow.kt
- CategoryPills.kt
- CollapsibleSection.kt
- ContextualActionBar.kt
- DraftIndicator.kt
- SwipeableFullScreenCard.kt
- TagGroup.kt
- SparklineChart.kt
- SpeedDialActions.kt
- CardTemplateThemes.kt
- DigitalCertificateComposable.kt
- CertificateGenerator.kt
- CalendarUtils.kt
- (and more...)
```

### Impact of Poor Organization
1. **Build Performance:** Slower incremental builds
2. **Navigation:** Difficult to find files
3. **Maintenance:** Hard to understand module boundaries
4. **Testing:** Unclear test organization
5. **Onboarding:** New developers confused
6. **Refactoring:** Risky to move files later
7. **Dependencies:** Circular dependency risks
8. **Code Review:** Harder to review changes

### Recommended File Organization
```
feature/
├── profile/
│   └── src/main/kotlin/com/rio/rostry/feature/profile/
│       ├── ui/
│       │   ├── AddressManagementScreen.kt
│       │   ├── BackupRestoreScreen.kt
│       │   └── SettingsScreen.kt
│       ├── viewmodel/
│       │   ├── AddressManagementViewModel.kt
│       │   └── ProfileEditViewModel.kt
│       └── navigation/
│           └── ProfileNavigation.kt
├── enthusiast-tools/
│   └── src/main/kotlin/com/rio/rostry/feature/enthusiast/
│       ├── ui/
│       │   ├── breeding/
│       │   ├── studio/
│       │   ├── arena/
│       │   └── pedigree/
│       └── viewmodel/
├── farmer-tools/
│   └── src/main/kotlin/com/rio/rostry/feature/farmer/
│       ├── ui/
│       │   ├── home/
│       │   ├── digital/
│       │   └── assets/
│       └── viewmodel/
└── monitoring/
    └── src/main/kotlin/com/rio/rostry/feature/monitoring/
        ├── ui/
        │   ├── growth/
        │   ├── health/
        │   └── vaccination/
        └── viewmodel/
```

---

## 🟠 HIGH PRIORITY ISSUE #2: HARDCODED MOCK LOCATION

**Severity:** HIGH | **Impact:** Feature Useless | **Fix Time:** 1 day

### Problem
All users without location see Bangalore, India as their location.

### Location
`feature/general/src/main/kotlin/com/rio/rostry/feature/general/ui/GeneralHomeViewModel.kt`

### Code
```kotlin
// Mock user location for 'Nearby' if not set (Center of Bangalore for demo)
val userLat = user?.farmLocationLat ?: 12.9716
val userLng = user?.farmLocationLng ?: 77.5946
```

### Impact
- "Nearby Farmers" feature shows wrong results
- Users in USA see farmers in India
- Location-based features broken
- Trust issues

### Recommendation
1. Prompt user for location on first use
2. Use device GPS with permission
3. Show empty state if no location
4. Never use hardcoded fallback location
