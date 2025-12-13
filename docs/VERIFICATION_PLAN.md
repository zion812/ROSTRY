# Application Verification Plan

This document outlines the strategy and checklist for verifying the ROSTRY application's functionality, ensuring all minimal basic features are in place and working as expected across different user roles.

## Execution Strategy

1.  **Code Inspection**: Review `Routes.kt`, `AppNavHost.kt`, and screen implementations to verify logic and navigation paths. **(Completed)**
2.  **Static Analysis**: Verify ViewModel logic for key flows (Auth, Role Selection). **(Completed)**
3.  **Automated Testing**: Run unit tests for critical navigation logic. **(Verified: AuthNavigationTest passed)**
4.  **Manual/Automated Testing**: (Future Step) Run the app on a device/emulator to validate UI interactions.

---

## Phase 1: Authentication & Onboarding

### 1.1 Welcome Screen (`AuthWelcomeScreen`)
- [x] **UI**: App launches to `AuthWelcomeScreen`.
- [x] **UI**: "Farmer", "Enthusiast", "General User" cards are displayed.
- [x] **Action**: Clicking "Sign In" calls `onSignInAsRole` -> navigates to `PhoneAuthScreen` (`auth/phone`).
- [x] **Action**: Clicking "Preview" calls `onPreviewAsRole` -> navigates to Home Screen in Guest Mode.
- [x] **Test**: `AuthNavigationTest` confirms ViewModel emits correct events.

### 1.2 Phone Authentication (`PhoneAuthScreenNew`)
- [x] **UI**: Phone number input field is present.
- [x] **Logic**: Input validates 10-digit number.
- [x] **Action**: "Send Code" button triggers `viewModel.sendOtp`.
- [x] **Navigation**: Success navigates to `OtpVerificationScreen` (`auth/otp/{verificationId}`).

### 1.3 OTP Verification (`OtpVerificationScreenNew`)
- [x] **UI**: OTP input field (6 digits).
- [x] **Action**: "Verify" button triggers `viewModel.verifyOtp`.
- [x] **Navigation**: Success triggers `onAuthenticated` callback -> `AppNavHost` switches to `RoleNavScaffold`.

### 1.4 Role Selection & User Setup (`UserSetupScreen`)
- [x] **Navigation**: New users without a role are routed to `onboard/user_setup`.
- [x] **UI**: Role selection (Farmer/Enthusiast) is presented.
- [x] **Action**: Selection updates user profile and navigates to respective Home.

---

## Phase 2: Role-Based Navigation & Home Screens

### 2.1 Farmer Experience
- [x] **Home**: `FarmerHomeScreen` (`home/farmer`) is the start destination.
- [x] **Nav**: Bottom bar contains Market, Create, Community, Profile.
- [x] **Feature**: Market (`farmer/market`) displays listings.
- [x] **Feature**: Create (`farmer/create`) allows adding products/posts.

### 2.2 Enthusiast Experience
- [x] **Home**: `EnthusiastHomeScreen` (`home/enthusiast`) is the start destination.
- [x] **Nav**: Bottom bar contains Explore, Create, Dashboard, Transfers.
- [x] **Feature**: Explore (`enthusiast/explore`) shows products/events.
- [x] **Feature**: Dashboard (`enthusiast/dashboard`) shows analytics/monitoring.

### 2.3 General User Experience
- [x] **Home**: `GeneralUserScreen` (`home/general`) is the start destination.
- [x] **Nav**: Bottom bar contains Market, Explore, Create, Cart, Profile.
- [x] **Feature**: Market (`general/market`) allows browsing and filtering.
- [x] **Feature**: Cart (`general/cart`) handles checkout flow.

---

## Phase 3: Key Feature Modules

### 3.1 Marketplace
- [x] **Farmer**: Can list products (`FarmerCreateScreen`).
- [x] **General**: Can browse, filter, and add to cart (`GeneralMarketScreen`).
- [x] **Details**: `ProductDetailsScreen` (`product/{productId}`) is accessible.

### 3.2 Traceability
- [x] **Route**: `traceability/{productId}` is defined.
- [x] **Screen**: `TraceabilityScreen` wraps `FamilyTreeView`.
- [x] **Feature**: Deep links (`rostry://traceability/...`) are handled.
- [x] **Scanner**: `QrScannerScreen` (`scan/qr`) integrates with traceability flow.

### 3.3 Farm Management (Monitoring)
- [x] **Dashboard**: `FarmMonitoringScreen` (`monitoring/dashboard`) links to all modules.
- [x] **Modules**: Routes exist for Vaccination, Mortality, Quarantine, Growth, Breeding, Hatching.
- [x] **Integration**: "Add to Farm" flow (`add-to-farm?productId=...`) connects Market to Monitoring.

### 3.4 Social & Community
- [x] **Feed**: `SocialFeedScreen` (`social/feed`) displays posts.
- [x] **Messaging**: `ThreadScreen` (`messages/{threadId}`) handles chats.
- [x] **Community**: Hub (`community/hub`) aggregates groups, events, experts.

---

## Phase 4: Data & Logic Verification (Next Steps)

### 4.1 Data Binding
- [ ] Verify `FarmerHomeViewModel` correctly fetches KPIs.
- [ ] Verify `TraceabilityViewModel` builds the family tree graph correctly.
- [ ] Verify `CartViewModel` calculates totals and handles checkout.

### 4.2 Edge Cases
- [ ] Verify Guest Mode restrictions (e.g., blocking "Create" actions).
- [ ] Verify Offline Mode behavior (sync status indicators).
