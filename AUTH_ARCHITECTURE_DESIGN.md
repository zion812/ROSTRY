# Authentication & User Types Architecture Design

## Overview

This document describes the comprehensive authentication architecture for ROSTRY, supporting:
- **3 Primary User Types**: Farmer, Enthusiast, General
- **2 Authentication Methods**: Google Sign-In, Email/Password
- **Guest Mode**: Preview without login
- **Granular Permissions**: Feature-level access control

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         AUTHENTICATION FLOW                              │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │   Splash     │
    │   Screen     │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │  Session     │◄──── Check DataStore for existing session
    │  Manager     │
    └──────┬───────┘
           │
    ┌──────┴───────┐
    │              │
    ▼              ▼
┌────────┐    ┌────────────┐
│ Guest  │    │ Authenticated │
│ Session│    │   Session    │
└────────┘    └──────┬───────┘
                     │
                     ▼
           ┌─────────────────┐
           │  AuthWelcome    │
           │     Screen      │
           └────────┬────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
┌─────────┐   ┌──────────┐   ┌──────────┐
│  Google │   │  Email/  │   │  Guest   │
│  Sign-In│   │ Password │   │  Preview │
└────┬────┘   └────┬─────┘   └────┬─────┘
     │             │              │
     │             │              │
     ▼             ▼              ▼
┌─────────────────────────────────────────────────────────────┐
│                    AuthRepository                            │
│  • signInWithGoogle()                                        │
│  • signUpWithEmail() / signInWithEmail()                    │
│  • sendPasswordResetEmail()                                  │
│  • sendEmailVerification() / isEmailVerified()              │
│  • observeCurrentUser()                                      │
└─────────────────────────────────────────────────────────────┘
                    │
                    ▼
           ┌─────────────────┐
           │  SessionManager │
           │  • markAuthenticated()                               │
           │  • markGuestSession()                                │
           │  • upgradeGuestToAuthenticated()                     │
           └────────┬────────┘
                    │
                    ▼
           ┌─────────────────┐
           │ PermissionChecker│
           │  • hasPermission()                                    │
           │  • requiresLogin()                                    │
           └────────┬────────┘
                    │
                    ▼
           ┌─────────────────┐
           │  Role-Based     │
           │  Navigation     │
           └─────────────────┘
```

## User Types

### 1. GENERAL (Default)
- **Display Name**: General User
- **Features**: Market browsing, ordering, basic social feed, cart management
- **Permissions**: Read-only marketplace, limited social interaction

### 2. FARMER
- **Display Name**: Farmer
- **Features**: Market participation, product listing, farm management, sales analytics
- **Permissions**: Full marketplace access, farm dashboard, QR generation

### 3. ENTHUSIAST
- **Display Name**: Enthusiast
- **Features**: Advanced tracking, breeding records, transfer management, analytics
- **Permissions**: Farm access, transfer verification, analytics dashboard

## Authentication Methods

### Google Sign-In
- Uses Firebase Google Auth Provider
- Returns ID token for server-side verification
- Automatic email verification for Google accounts

### Email/Password
- Firebase Email/Password authentication
- Minimum 6 character password requirement
- Email verification required on signup
- Password reset via email

## Guest Mode

### Features
- Browse marketplace (read-only)
- View product details
- Explore social feed (read-only)
- Search products
- View traceability information

### Limitations
- Cannot add to cart
- Cannot place orders
- Cannot create listings
- Cannot access farm dashboard
- Cart/wishlist not persisted

### Session Management
- 7-day session timeout
- Stored in DataStore
- Can upgrade to authenticated session

## Permission System

### Permission Categories

| Category | Permissions |
|----------|-------------|
| Marketplace | Browse, View Details, Add to Cart, Place Order, Create Listing |
| Social | View Feed, Create Post, Like, Comment, Follow, Message |
| Farm | View Dashboard, Add/Edit Birds, Transfer, Analytics |
| Traceability | View, Generate QR, Scan QR |
| Profile | View, Edit, View Others |
| Admin/Moderation | Dashboard, Manage Users, Moderate Content |

### Permission Checker Usage

```kotlin
// In ViewModel
@Inject lateinit var permissionChecker: PermissionChecker

// Check single permission
val canPlaceOrder by permissionChecker.hasPermission(Permission.MARKETPLACE_PLACE_ORDER)
    .collectAsState(false)

// Check if feature requires login
val requiresLogin = permissionChecker.requiresLogin(Permission.MARKETPLACE_PLACE_ORDER)

// Get all permissions for user type
val permissions = permissionChecker.getPermissionsForUserType(UserType.FARMER)

// Check if guest
val isGuest by permissionChecker.isGuestSession().collectAsState(false)
```

## File Structure

```
core/common/
├── session/
│   └── SessionManager.kt          # Session state management
└── permissions/
    ├── Permission.kt              # Permission definitions
    └── PermissionChecker.kt       # Permission checking service

domain/account/
└── repository/
    └── AuthRepository.kt          # Auth interface

data/account/
└── repository/
    └── AuthRepositoryImpl.kt      # Firebase auth implementation

feature/login/
└── ui/
    ├── AuthViewModel.kt           # Auth logic
    └── AuthWelcomeScreen.kt       # Role selection & auth UI
```

## Session Flow

### App Launch
1. Show splash screen
2. Check DataStore for existing session
3. If authenticated → Load role → Navigate to home
4. If guest → Navigate to home with guest banner
5. If no session → Show AuthWelcomeScreen

### Authentication Flow
1. User selects role (Farmer/Enthusiast/General)
2. User chooses auth method (Google/Email/Guest)
3. If Google → Firebase auth → Mark session → Navigate
4. If Email Sign-In → Firebase auth → Mark session → Navigate
5. If Email Sign-Up → Create account → Send verification → Navigate to verification
6. If Guest → Mark guest session → Navigate

### Guest to Authenticated Upgrade
1. User attempts restricted action
2. Show sign-in dialog
3. User authenticates
4. Call `sessionManager.upgradeGuestToAuthenticated(role, timestamp)`
5. Migrate guest data (cart, preferences)
6. Continue with authenticated session

## Error Handling

### Auth Errors
- `FirebaseAuthInvalidUserException` → "No account found"
- `FirebaseAuthInvalidCredentialsException` → "Invalid email or password"
- `FirebaseAuthWeakPasswordException` → "Password too weak"
- `FirebaseAuthUserCollisionException` → "Account already exists"

### Permission Errors
- Show login dialog for restricted actions
- Display feature lock icon on restricted UI
- Provide clear upgrade path

## Security Considerations

1. **Token Management**: Firebase ID tokens refreshed automatically
2. **Session Timeout**: 7 days for guests, 30 days for authenticated users
3. **Password Requirements**: Minimum 6 characters
4. **Email Verification**: Required for email sign-up
5. **Re-authentication**: Required for sensitive operations

## Testing Recommendations

1. Unit tests for PermissionChecker
2. Integration tests for AuthRepository
3. UI tests for AuthWelcomeScreen
4. Session persistence tests
5. Permission flow tests

## Future Enhancements

1. Social login (Facebook, Apple)
2. Two-factor authentication
3. Biometric authentication
4. Session activity tracking
5. Force logout on password change
6. Device-based session management