# Code Review Implementation Status

## ✅ COMPLETED (High Priority)

### Comment 1: Add guardrails for null/blank productId ✅
**File:** `FarmerCreateViewModel.kt`
- Added validation in `loadPrefillData()` to check for null/blank productId
- Shows user-facing error: "Invalid product selection. Please list manually or try again."
- Prevents silent failures

### Comment 2: Re-check quarantine status at submit time ✅
**File:** `FarmerCreateViewModel.kt`
- Added quarantine re-check in `submitListing()` before product mapping
- Prevents listing products that entered quarantine after initial check
- Shows error: "Cannot list products in quarantine. Complete quarantine protocol first."

### Comment 6: Add error banner in FarmerCreateScreen ✅
**File:** `FarmerCreateScreen.kt`
- Added error banner Card with error icon when `uiState.error` is non-null
- Includes "Dismiss" button that calls `viewModel.clearError()`
- Added `clearError()` method to ViewModel
- Error banner uses `errorContainer` color scheme

### Comment 8: Add authN/authZ checks before farm onboarding ✅
**File:** `GeneralCartViewModel.kt`
- Validates user role is FARMER before allowing farm add
- Checks for recent order (within 30 days) containing the productId
- Shows error: "Only farmers can add products to farm monitoring."
- Shows error: "You must purchase this product before adding it to farm monitoring."
- Logs security event on unauthorized attempts

### Comment 9: Offline messaging for Marketplace → Farm bridge ✅
**File:** `GeneralCartViewModel.kt`
- Checks connectivity via `connectivityManager.isConnected()`
- Shows different messages:
  - Online: "Added to farm monitoring! Track growth and vaccinations in the monitoring section."
  - Offline: "Added to farm monitoring. Records will sync when you're back online."

### Comment 10: Secure deep-link validation ✅
**File:** `GeneralCartViewModel.kt`
- Added validation in `showAddToFarmDialogForProduct()`
- Verifies current user is FARMER
- Checks for recent purchase of productId
- Logs security events:
  - `unauthorized_farm_dialog_attempt` - Non-farmer tries to access
  - `farm_dialog_no_purchase` - Farmer tries without purchase
- Added `trackSecurityEvent()` to `AnalyticsRepository`

### Comment 13: Handle Resource.Loading properly ✅
**File:** `FarmerCreateViewModel.kt`
- Changed from early return on Loading to waiting for Success/Error
- Uses `.first { it !is Resource.Loading }` to wait for actual result
- Shows loading spinner while waiting (`isSubmitting = true`)
- Prevents silent no-ops from premature returns

---

## 📋 REMAINING (Medium Priority)

### Comment 3: Refactor GeneralCartViewModel checkout
- Extract `maybePromptAddToFarm()` helper
- Consolidate duplicated prompt logic
- Clean up `dismissAddToFarmDialog()` wiring

### Comment 4: Split FarmerMarketViewModel concerns
- Create separate ViewModels for Browse vs Sell
- Or split UiState into nested `browse` and `sell` sections

### Comment 5: Unified MarketplaceQuery data class
- Create `MarketplaceQuery` consolidating all filters
- Add `ProductMarketplaceRepository.query()` method
- Eliminate client-side post-filtering

### Comment 7: Complete analytics coverage
- Audit all analytics calls
- Add missing events: `prefill_initiated/success/fail`, `listing_submitted`, `marketplace_to_farm_dialog_shown`
- Normalize event names with docs

### Comment 11: Ready to List metric worker
- Implement `FarmPerformanceWorker` to compute `productsReadyToListCount`
- Add fallback estimate in `FarmerHomeViewModel` to avoid 0 flicker

### Comment 12: Clarify IA and navigation
- Update tab labels to "Browse" and "My Listings"
- Add `selectedTabIndex` parameter to route
- Set default tab based on navigation source

### Comment 14: Fix LazyVerticalGrid overdraw
- Remove manual height heuristic
- Use `Modifier.fillMaxSize()` for natural sizing
- Verify nested scrolling

### Comment 15: Add unit tests
- `FarmerCreateViewModelTest` (prefill, quarantine)
- `FarmOnboardingRepositoryTest` (idempotency, schedules)
- `GeneralCartViewModelTest` (prompt logic, addToFarm)
- Navigation test for deep-link permissions

---

## 🔧 IMPLEMENTATION NOTES

### Security Events Logged
1. `unauthorized_farm_add_attempt` - Non-farmer or no purchase tries to add to farm
2. `unauthorized_farm_dialog_attempt` - Non-farmer accesses deep-link
3. `farm_dialog_no_purchase` - Farmer accesses deep-link without purchase

### Error Messages Added
1. "Invalid product selection. Please list manually or try again."
2. "Product not found. Please try again or list manually."
3. "Cannot list products in quarantine. Complete quarantine protocol first."
4. "Only farmers can add products to farm monitoring."
5. "You must purchase this product before adding it to farm monitoring."

### New Methods Added
- `FarmerCreateViewModel.clearError()`
- `AnalyticsRepository.trackSecurityEvent()`

---

## 📊 PROGRESS SUMMARY

**Completed:** 7/15 (47%)
**High Priority Completed:** 7/7 (100%) ✅
**Medium Priority Remaining:** 8/15

All security-critical and data integrity issues have been addressed. Remaining items are architectural improvements and UX enhancements.
