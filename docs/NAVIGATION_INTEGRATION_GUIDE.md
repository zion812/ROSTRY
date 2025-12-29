# Navigation Integration Guide - New Auth System

This guide shows how to integrate the new authentication screens into your existing navigation.

---

## Step 1: Update Routes.kt

Add new routes for the new auth screens:

```kotlin
object Routes {
    // ... existing routes
    
    // New auth routes
    const val AUTH_PHONE_NEW = "auth/phone_new"
    const val AUTH_OTP_NEW = "auth/otp_new/{verificationId}"
    const val AUTH_PHONE_LINKING_NEW = "auth/phone_linking_new"
    
    // Admin routes
    const val ADMIN_VERIFICATION = "admin/verification"
    
    // Helper function to create OTP route with argument
    fun authOtpNew(verificationId: String) = "auth/otp_new/$verificationId"
}
```

---

## Step 2: Update AppNavHost.kt

Add composable destinations for new screens:

```kotlin
@Composable
fun AppNavHost(...) {
    // ... existing code
    
    NavHost(...) {
        // ... existing routes
        
        // New Phone Auth Screen
        composable(Routes.AUTH_PHONE_NEW) {
            PhoneAuthScreenNew(
                onCodeSent = { verificationId ->
                    navController.navigate(Routes.authOtpNew(verificationId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // New OTP Verification Screen
        composable(
            route = Routes.AUTH_OTP_NEW,
            arguments = listOf(
                navArgument("verificationId") { type = NavType.StringType }
            )
        ) {
            OtpVerificationScreenNew(
                onVerified = {
                    // Navigate to home or onboarding based on user state
                    val dest = navConfig?.startDestination ?: Routes.HOME_GENERAL
                    navController.navigate(dest) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { 
                            inclusive = true 
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // New Phone Linking Screen (for Google/Email users)
        composable(Routes.AUTH_PHONE_LINKING_NEW) {
            PhoneLinkingScreenNew(
                onLinked = {
                    // Navigate to home
                    val dest = navConfig?.startDestination ?: Routes.HOME_GENERAL
                    navController.navigate(dest) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { 
                            inclusive = true 
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
```

---

## Step 3: Migration Strategy

### Option A: Gradual Migration (Recommended)

Keep both old and new routes during testing:

```kotlin
// Old route (keep working)
composable(Routes.AUTH_PHONE) { /* old screen */ }

// New route (test in parallel)
composable(Routes.AUTH_PHONE_NEW) { /* new screen */ }

// Use feature flag to switch:
val useNewAuth = featureToggles.isNewAuthEnabled()
val authRoute = if (useNewAuth) Routes.AUTH_PHONE_NEW else Routes.AUTH_PHONE
```

### Option B: Direct Replacement

Replace old routes with new ones:

```kotlin
// Replace this:
composable(Routes.AUTH_PHONE) { PhoneInputScreen(...) }

// With this:
composable(Routes.AUTH_PHONE) { PhoneAuthScreenNew(...) }
```

---

## Step 4: Update Entry Points

Update places that navigate to auth:

```kotlin
// Old
navController.navigate(Routes.AUTH_PHONE)

// New
navController.navigate(Routes.AUTH_PHONE_NEW)
```

Common entry points:
- `MainActivity.kt` - Initial app launch
- `AppNavHost.kt` - Start destination
- `ProfileScreen.kt` - Sign out button
- `SettingsScreen.kt` - Account management

---

## Step 5: Handle Deep Links

Update deep link handling for new screens:

```kotlin
// In AndroidManifest.xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="rostry"
          android:host="auth"
          android:pathPrefix="/phone_new" />
</intent-filter>

// In AppNavHost.kt
val deepLinkHandler = remember {
    { uri: Uri ->
        when (uri.path) {
            "/phone_new" -> Routes.AUTH_PHONE_NEW
            "/otp_new" -> Routes.authOtpNew(uri.getQueryParameter("vid") ?: "")
            else -> null
        }
    }
}
```

---

## Step 6: Testing Checklist

- [ ] Phone auth flow works end-to-end
- [ ] OTP verification successful
- [ ] OTP resend works with cooldown
- [ ] Phone linking for Google users
- [ ] Error messages display correctly
- [ ] Back button navigation works
- [ ] Deep links work
- [ ] Process death recovery works
- [ ] Rate limiting works
- [ ] Emulator test mode works

---

## Step 7: Cleanup (After Migration)

Once new auth is stable:

1. Remove old routes from `Routes.kt`
2. Delete old screen files
3. Remove old ViewModel
4. Remove old repository if not used elsewhere
5. Update all navigation references
6. Remove feature flag (if used)

---

## Complete Example

Here's a complete minimal integration:

```kotlin
// Routes.kt
object Routes {
    const val AUTH_PHONE_NEW = "auth/phone_new"
    const val AUTH_OTP_NEW = "auth/otp_new/{verificationId}"
    fun authOtpNew(vid: String) = "auth/otp_new/$vid"
}

// AppNavHost.kt
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.AUTH_PHONE_NEW
    ) {
        composable(Routes.AUTH_PHONE_NEW) {
            PhoneAuthScreenNew(
                onCodeSent = { vid -> 
                    navController.navigate(Routes.authOtpNew(vid)) 
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Routes.AUTH_OTP_NEW,
            arguments = listOf(
                navArgument("verificationId") { 
                    type = NavType.StringType 
                }
            )
        ) {
            OtpVerificationScreenNew(
                onVerified = { 
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH_PHONE_NEW) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

---

## Troubleshooting

### ViewModel not found
**Problem:** `IllegalArgumentException: No ViewModel found`  
**Solution:** Ensure Hilt is properly configured and `@HiltViewModel` annotation is present

### verificationId null
**Problem:** OTP screen receives null verificationId  
**Solution:** Check navigation argument name matches exactly: `"verificationId"`

### Back button clears state
**Problem:** State lost on back navigation  
**Solution:** Use `rememberSaveable` for UI state or ViewModel survives config changes

### Activity null
**Problem:** `activity` parameter is null in screens  
**Solution:** Cast context correctly: `LocalContext.current as? Activity`

---

## Need Help?

Check these files for reference:
- `PhoneAuthScreenNew.kt` - Example screen implementation
- `PhoneAuthViewModel.kt` - Example ViewModel
- `FIREBASE_PHONE_AUTH_SETUP.md` - Firebase phone authentication setup guide
- `FIREBASE_AUTH_ANALYSIS.md` - Authentication analysis and architecture

**Good luck with integration! 🚀**

---

## Appendix: Callback-Based Navigation Pattern

The project is moving towards a callback-based navigation pattern to improve testability and decoupling.

### Pattern Overview
Instead of injecting `NavController` into feature screens, expose navigation events as lambda parameters.

**Old Way (Coupled):**
```kotlin
@Composable
fun FarmerProfileScreen(navController: NavController) {
    Button(onClick = { navController.navigate("chat/${farmer.id}") }) { ... }
}
```

**New Way (Decoupled):**
```kotlin
@Composable
fun FarmerProfileScreen(
    onMessageClick: (String) -> Unit
) {
    Button(onClick = { onMessageClick(farmer.id) }) { ... }
}
```

### Implementation in `AppNavHost`
The `AppNavHost` acts as the coordinator, supplying the concrete navigation logic:

```kotlin
composable(Routes.EXPLORE_FARMER_PROFILE) {
    FarmerProfileScreen(
        onMessageClick = { userId ->
            navController.navigate(Routes.socialMessage(userId))
        }
    )
}
```

### Benefits
1.  **Testability**: Testing `FarmerProfileScreen` no longer requires mocking `NavController`.
2.  **Previews**: `@Preview` functions can pass empty lambdas (`{}`) easily.
3.  **Flexibility**: The parent can decide where to navigate (e.g., in a different flow or tablet layout).

This pattern is currently used in `GeneralExploreRoute`, `SocialProfileScreen`, and `MarketplaceScreen`.
