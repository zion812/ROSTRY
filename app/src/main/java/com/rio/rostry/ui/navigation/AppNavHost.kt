package com.rio.rostry.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.rio.rostry.ui.profile.StorageQuotaScreen
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.AuthWelcomeViewModel
import com.rio.rostry.ui.auth.AuthWelcomeScreen
import com.rio.rostry.ui.auth.PhoneAuthScreenNew
import com.rio.rostry.ui.auth.OtpVerificationScreenNew
import com.rio.rostry.ui.auth.PhoneVerificationScreen
import com.rio.rostry.ui.splash.SplashScreen
import com.rio.rostry.ui.product.ProductDetailsScreen
import com.rio.rostry.ui.profile.ProfileScreen
import com.rio.rostry.ui.profile.EditProfileScreen
import com.rio.rostry.ui.screens.HomeEnthusiastScreen
import com.rio.rostry.ui.farmer.FarmerHomeScreen
import com.rio.rostry.ui.farmer.FarmerMarketScreen
import com.rio.rostry.ui.farmer.FarmerCreateScreen
import com.rio.rostry.ui.farmer.FarmerCommunityScreen
import com.rio.rostry.ui.farmer.FarmerProfileScreen
import com.rio.rostry.ui.farmer.asset.FarmAssetListScreen
import com.rio.rostry.ui.farmer.asset.FarmAssetDetailScreen
import com.rio.rostry.ui.farmer.listing.CreateListingScreen
import com.rio.rostry.ui.screens.HomeGeneralScreen
import com.rio.rostry.ui.screens.PlaceholderScreen
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.ui.traceability.FamilyTreeView
import com.rio.rostry.ui.traceability.TraceabilityScreen
import com.rio.rostry.ui.traceability.TraceabilityViewModel
import com.rio.rostry.ui.transfer.TransferDetailsViewModel
import com.rio.rostry.ui.transfer.TransferVerificationScreen
import com.rio.rostry.ui.transfer.TransferCreateViewModel
import com.rio.rostry.ui.transfer.TransferCreateScreen
import com.rio.rostry.ui.verification.EnthusiastKycScreen
import com.rio.rostry.ui.verification.FarmerLocationVerificationScreen
import com.rio.rostry.ui.verification.VerificationViewModel
import com.rio.rostry.ui.notifications.NotificationsViewModel
import com.rio.rostry.ui.notifications.NotificationsScreen
import com.rio.rostry.ui.enthusiast.EnthusiastHomeScreen
import com.rio.rostry.ui.enthusiast.EnthusiastExploreScreen
// Tabs implementation is wrapped by EnthusiastExploreScreen
import com.rio.rostry.ui.enthusiast.EnthusiastCreateScreen
import com.rio.rostry.ui.enthusiast.EnthusiastDashboardHost
import com.rio.rostry.ui.enthusiast.dashboard.EnthusiastDashboardTabs
import com.rio.rostry.ui.scan.QrScannerScreen
import com.rio.rostry.ui.enthusiast.EnthusiastTransfersScreen
import com.rio.rostry.ui.enthusiast.breeding.BreedingFlowScreen
import com.rio.rostry.ui.social.LiveBroadcastScreen
import com.rio.rostry.ui.analytics.EnthusiastDashboardScreen
import com.rio.rostry.ui.monitoring.FarmPerformanceScreen
import com.rio.rostry.ui.components.OfflineBanner
import com.rio.rostry.ui.components.SyncStatusIndicator
import com.rio.rostry.ui.components.SyncStatusViewModel
import com.rio.rostry.ui.sync.SyncIssuesScreen
import com.rio.rostry.ui.social.profile.SocialProfileScreen
import com.rio.rostry.ui.social.stories.StoryViewerScreen
import com.rio.rostry.ui.social.stories.StoryCreatorScreen
import com.rio.rostry.ui.social.discussion.DiscussionDetailScreen
import androidx.compose.ui.Alignment
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.rio.rostry.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import com.rio.rostry.BuildConfig
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import kotlin.math.roundToInt
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.DisposableEffect
import androidx.annotation.VisibleForTesting
import android.app.Activity
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.utils.network.FeatureToggles

@Composable
fun AppNavHost(
    featureToggles: FeatureToggles,
    firebaseAuth: FirebaseAuth
) {
    val sessionVm: SessionViewModel = hiltViewModel()
    val syncViewModel: SyncStatusViewModel = hiltViewModel()
    val state by sessionVm.uiState.collectAsState()
    val navConfig = state.navConfig
    val context = LocalContext.current
    
    // Splash screen state
    var showSplash by remember { mutableStateOf(true) }

    // Capture incoming deep link and store while unauthenticated
    LaunchedEffect(state.isAuthenticated) {
        val data = (context as? android.app.Activity)?.intent?.data?.toString()
        if (!state.isAuthenticated && !data.isNullOrBlank()) {
            // Store the path portion for internal navigation e.g., rostry://traceability/ID -> traceability/ID
            val route = data.substringAfter("rostry://")
            sessionVm.setPendingDeepLink(route)
        }
    }

    // Session validity check
    if (state.isAuthenticated) {
        LaunchedEffect("session-expiry") {
            sessionVm.scheduleSessionExpiryCheck()
        }
    }

    // Show splash screen first
    if (showSplash) {
        SplashScreen(
            onSplashComplete = {
                showSplash = false
            }
        )
        return
    }

    when {
        state.isAuthenticated && navConfig != null -> {
            // Phone linking is now OPTIONAL, not forced
            // Users can add phone later from settings if they want
            val isGuestMode = state.authMode == SessionManager.AuthMode.GUEST
            RoleNavScaffold(navConfig, sessionVm, state, syncViewModel = syncViewModel, isGuestMode = isGuestMode)
        }
        state.isAuthenticated -> {
            if (state.error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Error loading profile",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { sessionVm.signOut() }) {
                            Text("Sign Out")
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { sessionVm.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                LaunchedEffect("await-config") { sessionVm.refresh() }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        else -> {
            val fromGuest = state.authMode == SessionManager.AuthMode.GUEST
            AuthFlow(
                state = state,
                onAuthenticated = { sessionVm.refresh() },
                fromGuest = fromGuest
            )
        }
    }
}

@VisibleForTesting
internal fun roleGraphRegisteredRoutesBasic(): Set<String> = setOf(
    // General root-level
    Routes.HOME_GENERAL,
    Routes.SOCIAL_FEED,
    Routes.MESSAGES_THREAD,
    Routes.MESSAGES_GROUP,
    Routes.MESSAGES_OUTBOX,
    Routes.NOTIFICATIONS,
    Routes.PRODUCT_DETAILS,
    Routes.TRACEABILITY,
    Routes.PRODUCT_FAMILY_TREE,
    // Farmer root-level
    Routes.HOME_FARMER,
    Routes.FarmerNav.MARKET,
    Routes.FarmerNav.CREATE,
    Routes.FarmerNav.COMMUNITY,
    Routes.FarmerNav.PROFILE,
    // Enthusiast root-level
    Routes.HOME_ENTHUSIAST,
    Routes.EnthusiastNav.EXPLORE,
    Routes.EnthusiastNav.CREATE,
    Routes.EnthusiastNav.DASHBOARD,
    Routes.EnthusiastNav.TRANSFERS,
    // Social/Community
    Routes.GROUPS,
    Routes.EVENTS,
    Routes.EXPERT_BOOKING,
    Routes.MODERATION,
    Routes.LEADERBOARD,
    Routes.LIVE_BROADCAST,
    Routes.COMMUNITY_HUB,
    Routes.GROUP_DETAILS,
    Routes.EVENT_DETAILS,
    Routes.EXPERT_PROFILE,
    // Transfers
    Routes.TRANSFER_DETAILS,
    Routes.TRANSFER_LIST,
    Routes.TRANSFER_VERIFY,
    Routes.TRANSFER_CREATE,
    // Monitoring
    Routes.MONITORING_DASHBOARD,
    Routes.MONITORING_VACCINATION,
    Routes.MONITORING_MORTALITY,
    Routes.MONITORING_QUARANTINE,
    Routes.MONITORING_BREEDING,
    Routes.MONITORING_GROWTH,
    Routes.MONITORING_HATCHING,
    Routes.MONITORING_HATCHING_BATCH,
    Routes.MONITORING_PERFORMANCE,
    Routes.MONITORING_DAILY_LOG,
    Routes.MONITORING_DAILY_LOG_PRODUCT,
    Routes.MONITORING_TASKS,
    // Analytics and Sandbox
    Routes.ANALYTICS_GENERAL,
    Routes.ANALYTICS_FARMER,
    Routes.ANALYTICS_ENTHUSIAST,
    Routes.REPORTS,
    Routes.PRODUCT_SANDBOX,
    Routes.SYNC_ISSUES)

@Composable
private fun AuthFlow(
    state: SessionViewModel.SessionUiState,
    onAuthenticated: () -> Unit,
    fromGuest: Boolean = false
) {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()
    val welcomeVm: AuthWelcomeViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = remember(context) {
        var ctx = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is Activity) return@remember ctx
            ctx = ctx.baseContext
        }
        ctx as? Activity
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error if present
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }
    }

    // FirebaseUI launcher for Google/Email
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = com.firebase.ui.auth.FirebaseAuthUIActivityResultContract()
    ) { res ->
        // Set the fromGuest flag in the AuthViewModel before handling the result
        authVm.setFromGuest(fromGuest)
        authVm.handleFirebaseUIResult(res.idpResponse, res.resultCode)
    }

    // Observe auth navigation events
    LaunchedEffect(Unit) {
        authVm.navigation.collectLatest { event ->
            when (event) {
                is AuthViewModel.NavAction.ToOtp -> navController.navigate("auth/otp/${event.verificationId}")
                is AuthViewModel.NavAction.ToHome -> onAuthenticated()
                is AuthViewModel.NavAction.ToUserSetup -> navController.navigate("onboard/user_setup")
            }
        }
    }

    // Observe welcome screen navigation events for guest mode
    LaunchedEffect(Unit) {
        welcomeVm.navigationEvent.collectLatest { event ->
            when (event) {
                is AuthWelcomeViewModel.NavigationEvent.ToGuestHome -> {
                    onAuthenticated()
                }
                is AuthWelcomeViewModel.NavigationEvent.ToAuth -> {
                    // Navigate to phone auth with role context
                    navController.navigate("auth/phone")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // Main navigation host - Instagram style with slide animations
        NavHost(
            navController = navController,
            startDestination = "auth/welcome",
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
        // Welcome screen - role-first entry point (fade only)
        composable(
            route = "auth/welcome",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            val isLoading by welcomeVm.isLoading.collectAsState()
            AuthWelcomeScreen(
                onPreviewAsRole = { role ->
                    welcomeVm.startGuestMode(role)
                },
                onSignInAsRole = { role ->
                    welcomeVm.startAuthentication(role)
                },
                isLoading = isLoading
            )
        }
        
        // Phone authentication flow - slide from right
        composable(
            route = "auth/phone",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            // FREE TIER: Phone Auth is disabled - show placeholder and redirect
            val FREE_TIER_MODE = true
            
            if (FREE_TIER_MODE) {
                // Show a message that phone auth is disabled
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Filled.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Phone Sign-In Unavailable",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            "Please use 'Sign in with Google' instead.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        // Google Sign-In Button
                        Button(
                            onClick = {
                                val providers = listOf(
                                    com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder().build(),
                                    com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder().build()
                                )
                                val intent = com.firebase.ui.auth.AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(false, true) // Disabled - causes hangs
                                    .build()
                                launcher.launch(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign in with Google")
                        }

                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
            } else {
                PhoneAuthScreenNew(
                    onCodeSent = { verificationId ->
                        val isGuest = fromGuest
                        navController.navigate("auth/otp/$verificationId?fromGuest=$isGuest")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        // OTP verification - slide from right
        composable(
            route = "auth/otp/{verificationId}?fromGuest={fromGuest}",
            arguments = listOf(
                navArgument("verificationId") { type = NavType.StringType },
                navArgument("fromGuest") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) { backStackEntry ->
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            val isFromGuest = backStackEntry.arguments?.getBoolean("fromGuest") ?: false

            // Set the fromGuest flag in the AuthViewModel before navigating
            val authVm: AuthViewModel = hiltViewModel()
            LaunchedEffect(isFromGuest) {
                authVm.setFromGuest(isFromGuest)
            }

            OtpVerificationScreenNew(
                verificationId = verificationId,
                fromGuest = isFromGuest,
                onVerified = {
                    onAuthenticated()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // User setup/onboarding
        composable("onboard/user_setup") {
            com.rio.rostry.ui.onboarding.UserSetupScreen(
                onRoleSelected = onAuthenticated
            )
        }
        
        // Onboarding tour - role-specific feature walkthrough
        composable(
            route = "onboard/tour/{role}",
            arguments = listOf(
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roleStr = backStackEntry.arguments?.getString("role") ?: "GENERAL"
            val role = try {
                com.rio.rostry.domain.model.UserType.valueOf(roleStr.uppercase())
            } catch (e: Exception) {
                com.rio.rostry.domain.model.UserType.GENERAL
            }
            
            com.rio.rostry.ui.onboarding.UserOnboardingTourScreen(
                userRole = role,
                onComplete = { onAuthenticated() },
                onSkip = { onAuthenticated() }
            )
        }
    }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleNavScaffold(
    navConfig: RoleNavigationConfig,
    sessionVm: SessionViewModel,
    state: SessionViewModel.SessionUiState,
    syncViewModel: SyncStatusViewModel,
    isGuestMode: Boolean = false
) {
    val navController = rememberNavController()
    val syncState by syncViewModel.syncState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val authViewModel: AuthViewModel = hiltViewModel()
    var showSignInDialog by remember { mutableStateOf(false) }
    var pendingGuestAction by remember { mutableStateOf<String?>(null) }

    // Consume and replay pending deep link once role is available
    LaunchedEffect(navConfig.role) {
        val pending = sessionVm.consumePendingDeepLink()
        if (!pending.isNullOrBlank()) {
            if (isGuestMode) {
                // In guest mode, show sign-in dialog for restricted deep links
                if (isWriteAction(pending)) {
                    pendingGuestAction = pending
                    showSignInDialog = true
                } else {
                    navController.navigate(pending)
                }
            } else {
                val allowed = navConfig.accessibleRoutes
                if (Routes.isRouteAccessible(allowed, pending)) {
                    navController.navigate(pending)
                } else {
                    // Store deep link for after authentication
                    pendingGuestAction = pending
                    showSignInDialog = true
                }
            }
        }
    }

    // Role-based route guard: bypass in GUEST mode
    if (!isGuestMode) {
        DisposableEffect(navController, authViewModel) {
            val allowed = navConfig.accessibleRoutes
            val listener: (androidx.navigation.NavController, androidx.navigation.NavDestination, android.os.Bundle?) -> Unit =
                { controller, destination, _ ->
                    val pattern = destination.route ?: ""
                    val base = pattern.substringBefore("?")
                    // Parse declared query keys from the destination pattern, not from runtime arguments
                    val declaredQueryKeys = pattern.substringAfter("?", "")
                        .takeIf { it.isNotBlank() }
                        ?.split("&")
                        ?.mapNotNull { part -> part.substringBefore("=", missingDelimiterValue = "").takeIf { it.isNotBlank() } }
                        ?.sorted()
                        .orEmpty()
                    val querySuffix = if (declaredQueryKeys.isNotEmpty()) "?" + declaredQueryKeys.joinToString("&") else ""
                    val concreteLike = base + querySuffix
                    if (concreteLike.isNotBlank() && !Routes.isRouteAccessible(allowed, concreteLike)) {
                        controller.navigate(navConfig.startDestination) {
                            launchSingleTop = true
                            popUpTo(controller.graph.startDestinationId) { inclusive = false }
                        }
                    } else if (concreteLike.isNotBlank() && isWriteAction(concreteLike) && authViewModel.uiState.value.pendingPhoneVerificationReason != null) {
                        // If this is a write action and phone verification is deferred, show verification dialog
                        pendingGuestAction = concreteLike // Store the intended action
                        showSignInDialog = true // Reuse the existing dialog functionality by showing it as a verification prompt
                    }
                }
            navController.addOnDestinationChangedListener(listener)
            onDispose { navController.removeOnDestinationChangedListener(listener) }
        }
    }

    LaunchedEffect(syncViewModel) {
        syncViewModel.events.collectLatest { event ->
            when (event) {
                SyncStatusViewModel.SyncEvent.ViewSyncDetails -> {
                    navController.navigate(Routes.SYNC_ISSUES)
                }
            }
        }
    }

    // Wrap Scaffold in a Box to overlay guest banner above all content
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ROSTRY") },
                    actions = {
                        // Show "Sign In" button in guest mode
                        if (isGuestMode) {
                            Button(
                                onClick = { 
                                    pendingGuestAction = null
                                    sessionVm.signOut() 
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Sign In")
                            }
                        }
                        SyncStatusIndicator(
                            syncState = syncState,
                            onClick = { syncViewModel.viewSyncDetails() }
                        )
                        AccountMenuAction(
                            navController = navController,
                            onSignOut = { sessionVm.signOut() },
                            isGuestMode = isGuestMode,
                            pendingCount = state.pendingVerificationCount
                        )
                        if (!isGuestMode) {
                            NotificationsAction(navController = navController)
                        }
                    }
                )
            },
            floatingActionButton = {
                val isFarmer = navConfig.role == UserType.FARMER
                val isEnthusiast = navConfig.role == UserType.ENTHUSIAST
                
                val showCreateFab = when {
                    isFarmer -> false
                    isEnthusiast -> currentRoute in setOf(
                        Routes.EnthusiastNav.HOME,
                        Routes.EnthusiastNav.EXPLORE,
                        Routes.Social.FEED,
                        Routes.EnthusiastNav.DASHBOARD
                    )
                    else -> false
                }

                if (showCreateFab && !isGuestMode) {
                    FloatingActionButton(
                        onClick = {
                            val route = if (isFarmer) Routes.FarmerNav.CREATE else Routes.EnthusiastNav.CREATE
                            navController.navigate(route)
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Create")
                    }
                }
            },
            bottomBar = {
                RoleBottomBar(
                    navController = navController,
                    navConfig = navConfig,
                    currentRoute = currentRoute,
                    isGuestMode = isGuestMode,
                    onGuestActionAttempt = { action ->
                        pendingGuestAction = action
                        showSignInDialog = true
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Guest mode banner
                if (isGuestMode) {
                    GuestModeBanner(
                        onSignInClick = {
                            // Set fromGuest flag for the auth flow
                            authViewModel.setFromGuest(true)
                            pendingGuestAction = null
                            sessionVm.signOut()
                        }
                    )
                }
                OfflineBanner(
                    onViewPending = { navController.navigate(Routes.SYNC_ISSUES) }
                )
                // Show verification banner when phone verification is deferred
                // FREE TIER: Hide this banner since phone auth is disabled
                val FREE_TIER_BANNER_HIDDEN = true
                if (!FREE_TIER_BANNER_HIDDEN && authViewModel.uiState.value.pendingPhoneVerificationReason != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Verify phone to unlock transfers and listings",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Button(
                                onClick = { navController.navigate(Routes.PROFILE) } // Navigate to profile to verify phone
                            ) {
                                Text("Verify")
                            }
                        }
                    }
                }
                RoleNavGraph(
                    navController = navController,
                    navConfig = navConfig,
                    sessionVm = sessionVm,
                    state = state,
                    isGuestMode = isGuestMode,
                    onGuestActionAttempt = { action ->
                        pendingGuestAction = action
                        showSignInDialog = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Guest mode "Sign in to continue" dialog
    if (showSignInDialog && isGuestMode) {
        GuestSignInDialog(
            onSignIn = {
                // Set fromGuest flag for the auth flow
                authViewModel.setFromGuest(true)
                // Store pending action and sign out to trigger auth flow
                pendingGuestAction?.let { sessionVm.setPendingDeepLink(it) }
                sessionVm.signOut()
                showSignInDialog = false
            },
            onDismiss = {
                pendingGuestAction = null
                showSignInDialog = false
            }
        )
    }

    // Phone verification required dialog for authenticated users with deferred verification
    if (showSignInDialog && !isGuestMode && authViewModel.uiState.value.pendingPhoneVerificationReason != null) {
        AlertDialog(
            onDismissRequest = {
                pendingGuestAction = null
                showSignInDialog = false
            },
            title = { Text("Verify Phone Number") },
            text = { Text("Please verify your phone number to access this feature.") },
            confirmButton = {
                Button(onClick = {
                    // Navigate to phone verification
                    navController.navigate(Routes.PROFILE)
                    showSignInDialog = false
                    pendingGuestAction = null
                }) {
                    Text("Verify Phone")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingGuestAction = null
                    showSignInDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RoleNavGraph(
    navController: NavHostController,
    navConfig: RoleNavigationConfig,
    sessionVm: SessionViewModel,
    state: SessionViewModel.SessionUiState,
    isGuestMode: Boolean = false,
    onGuestActionAttempt: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = navConfig.startDestination,
        modifier = modifier
    ) {
        // General navigation destinations
        composable(Routes.HOME_GENERAL) {
            com.rio.rostry.ui.general.GeneralUserScreen(

                onOpenProductDetails = { productId -> navController.navigate(Routes.Builders.productDetails(productId)) },
                onOpenTraceability = { productId -> navController.navigate(Routes.Builders.traceability(productId)) },
                onOpenSocialFeed = { navController.navigate(Routes.SOCIAL_FEED) },
                onOpenMessages = { threadId -> navController.navigate(Routes.Builders.messagesThread(threadId)) },
                onScanQr = { navController.navigate(Routes.Builders.scanQr("product_details")) }
            )
        }
        composable(Routes.SYNC_ISSUES) {
            SyncIssuesScreen(navController = navController)
        }
        composable(Routes.HOME_FARMER) {
            val viewModel: com.rio.rostry.ui.farmer.FarmerHomeViewModel = hiltViewModel()
            FarmerHomeScreen(
                viewModel = viewModel,
                onOpenAlerts = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavigateToAddBird = { navController.navigate(Routes.Builders.onboardingFarmBird("farmer")) },
                onNavigateToAddBatch = { navController.navigate(Routes.Builders.onboardingFarmBatch("farmer")) },
                onNavigateRoute = { route -> navController.navigate(route) }
            )
        }
        composable(Routes.FarmerNav.DIGITAL_FARM) {
            com.rio.rostry.ui.farmer.DigitalFarmScreen(
                onBack = { navController.popBackStack() },
                onManageBird = { id -> navController.navigate(Routes.Builders.dailyLog(id)) },
                onViewLineage = { id -> navController.navigate(Routes.Builders.traceability(id)) },
                onListForSale = { id -> navController.navigate(Routes.Builders.farmerCreateWithPrefill(id)) }
            )
        }
        composable(Routes.FarmerNav.MARKET) {
            val vm: com.rio.rostry.ui.farmer.FarmerMarketViewModel = hiltViewModel()
            val state by vm.ui.collectAsState()
            
            FarmerMarketScreen(
                // NEW: Route to asset selection first, then create listing from selected asset
                onCreateListing = { navController.navigate(Routes.FarmerNav.FARM_ASSETS) },
                onEditListing = { listingId -> 
                    // For edit, navigate to the listing details (could be asset-based in future)
                    navController.navigate(Routes.Builders.productDetails(listingId))
                },
                onBoostListing = { _ -> /* Could open promo screen */ Unit },
                onPauseListing = { _ -> /* Pause listing action */ Unit },
                onOpenOrder = { threadId -> navController.navigate(Routes.Builders.messagesThread(threadId)) },
                onOpenProduct = { productId -> navController.navigate(Routes.Builders.productDetails(productId)) },
                selectedTabIndex = state.selectedTabIndex,
                onSelectTab = { vm.setTab(it) },
                metricsRevenue = state.metricsRevenue,
                metricsOrders = state.metricsOrders,
                metricsViews = state.metricsViews,
                isLoadingBrowse = state.isLoadingBrowse,
                isLoadingMine = state.isLoadingMine,
                browse = state.filteredBrowse,
                mine = state.mine,
                onRefresh = { vm.refresh() },
                onApplyPriceBreed = { min, max, breed -> vm.applyPriceBreed(min, max, breed) },
                onApplyDateFilter = { s, e -> vm.applyDateFilter(s, e) },
                onClearDateFilter = { vm.clearDateFilter() },
                startDate = state.startDate,
                endDate = state.endDate,
                onSelectCategoryMeat = { vm.selectCategory(com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Meat) },
                onSelectCategoryAdoption = { vm.selectCategory(com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Adoption) },
                onSelectTraceable = { vm.selectTrace(com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.Traceable) },
                onSelectNonTraceable = { vm.selectTrace(com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.NonTraceable) },
                categoryMeatSelected = state.categoryFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Meat,
                categoryAdoptionSelected = state.categoryFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Adoption,
                traceableSelected = state.traceFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.Traceable,
                nonTraceableSelected = state.traceFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.NonTraceable,
                onScanQr = { navController.navigate(Routes.Builders.scanQr("product_details")) },
                verificationStatus = state.verificationStatus
            )
        }
        // Farmer Create route with optional prefill arguments
        composable(
            route = Routes.FarmerNav.CREATE + "?prefillProductId={prefillProductId}&pairId={pairId}",
            arguments = listOf(
                navArgument("prefillProductId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("pairId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val vvm: VerificationViewModel = hiltViewModel()
            val vstate by vvm.ui.collectAsState()
            val createVm: com.rio.rostry.ui.farmer.FarmerCreateViewModel = hiltViewModel()
            val createState by createVm.ui.collectAsState()
            
            // Extract prefillProductId and pairId from navigation arguments
            val prefillProductId = backStackEntry.arguments?.getString("prefillProductId")
            val pairId = backStackEntry.arguments?.getString("pairId")

            LaunchedEffect(createState.successProductId) {
                if (!createState.successProductId.isNullOrBlank()) {
                    // Navigate to Market after successful publish
                    navController.navigate(Routes.FarmerNav.MARKET) {
                        launchSingleTop = true
                    }
                }
            }

            FarmerCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                prefillProductId = prefillProductId,
                pairId = pairId
            )
        }
        // Explicit base route without query to avoid matching issues
        composable(Routes.FarmerNav.CREATE) {
            if (isGuestMode) {
                // Block write action in guest mode
                LaunchedEffect(Unit) {
                    onGuestActionAttempt(Routes.FarmerNav.CREATE)
                    navController.popBackStack()
                }
            } else {
                FarmerCreateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    prefillProductId = null,
                    pairId = null
                )
            }
        }
        composable(Routes.FarmerNav.COMMUNITY) {
            FarmerCommunityScreen(
                onOpenThread = { threadId -> navController.navigate(Routes.Builders.messagesThread(threadId)) },
                onOpenGroupDirectory = {
                    if (BuildConfig.DEBUG) navController.navigate(Routes.GROUPS)
                    else navController.navigate(Routes.LEADERBOARD)
                },
                onOpenExpertBooking = { navController.navigate(Routes.EXPERT_BOOKING) },
                onOpenRegionalNews = { navController.navigate(Routes.LEADERBOARD) }
            )
        }
        composable(Routes.FarmerNav.PROFILE) {
            FarmerProfileScreen(
                onEditProfile = { navController.navigate(Routes.PROFILE) },
                onManageCertifications = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
                onContactSupport = { /* open support */ }
            )
        }
        
        // ============ NEW: Farm Asset Management Routes (Phase 1 of Farm-Market Separation) ============
        
        // Farm Assets List - "My Farm" tab destination
        composable(Routes.FarmerNav.FARM_ASSETS) {
            FarmAssetListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAssetClick = { assetId -> 
                    navController.navigate(Routes.Builders.assetDetails(assetId))
                },
                onAddAsset = { 
                    navController.navigate(Routes.FarmerNav.CREATE_ASSET)
                }
            )
        }
        
        // Farm Asset Detail - view/manage individual asset
        composable(
            route = Routes.FarmerNav.ASSET_DETAILS,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId")
            if (assetId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid asset ID", onBack = { navController.popBackStack() })
            } else {
                FarmAssetDetailScreen(
                    assetId = assetId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateRoute = { route -> navController.navigate(route) },
                    onCreateListing = { 
                        navController.navigate(Routes.Builders.createListingFromAsset(assetId))
                    },
                    onCreateAuction = {
                        navController.navigate(Routes.Builders.createAuctionFromAsset(assetId))
                    },
                    onViewHistory = {
                        navController.navigate(Routes.Builders.birdHistory(assetId))
                    }
                )
            }
        }
        
        // Create Asset - add new farm asset
        composable(Routes.FarmerNav.CREATE_ASSET) {
            if (isGuestMode) {
                LaunchedEffect(Unit) {
                    onGuestActionAttempt(Routes.FarmerNav.CREATE_ASSET)
                    navController.popBackStack()
                }
            } else {
                // TODO: Create FarmAssetCreateScreen - for now route to existing create
                FarmerCreateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    prefillProductId = null,
                    pairId = null
                )
            }
        }
        
        // Create Listing from Asset - publish asset to marketplace
        composable(
            route = Routes.FarmerNav.CREATE_LISTING_FROM_ASSET,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId")
            if (assetId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid asset ID", onBack = { navController.popBackStack() })
            } else {
                if (isGuestMode) {
                    LaunchedEffect(Unit) {
                        onGuestActionAttempt(Routes.FarmerNav.CREATE_LISTING_FROM_ASSET)
                        navController.popBackStack()
                    }
                } else {
                    CreateListingScreen(
                        assetId = assetId,
                        onNavigateBack = { navController.popBackStack() },
                        onListingCreated = { 
                            navController.popBackStack(Routes.FarmerNav.FARM_ASSETS, inclusive = false)
                        }
                    )
                }
            }
        }
        
        // ============ END: Farm Asset Management Routes ============

        // Create Auction from Asset - publish asset to auction
        composable(
            route = Routes.FarmerNav.CREATE_AUCTION_FROM_ASSET,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId")
            if (assetId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid asset ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.auction.CreateAuctionScreen(
                    assetId = assetId,
                    onBack = { navController.popBackStack() },
                    onSuccess = { 
                        // Navigate back to Asset List or Dashboard
                        navController.popBackStack(Routes.FarmerNav.FARM_ASSETS, inclusive = false)
                    }
                )
            }
        }
        
        // ============ Action Detail Routes for Addressable Logs ============
        
        // Vaccination Detail Screen
        composable(
            route = Routes.Monitoring.VACCINATION_DETAIL,
            arguments = listOf(navArgument("vaccinationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vaccinationId = backStackEntry.arguments?.getString("vaccinationId")
            if (vaccinationId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid vaccination ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.monitoring.VaccinationDetailScreen(
                    vaccinationId = vaccinationId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Routes.Builders.productDetails(productId))
                    }
                )
            }
        }
        
        // Growth Record Detail Screen
        composable(
            route = Routes.Monitoring.GROWTH_DETAIL,
            arguments = listOf(navArgument("recordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")
            if (recordId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid record ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.monitoring.GrowthRecordDetailScreen(
                    recordId = recordId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Routes.Builders.productDetails(productId))
                    }
                )
            }
        }
        
        // Mortality Detail Screen
        composable(
            route = Routes.Monitoring.MORTALITY_DETAIL,
            arguments = listOf(navArgument("deathId") { type = NavType.StringType })
        ) { backStackEntry ->
            val deathId = backStackEntry.arguments?.getString("deathId")
            if (deathId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid death ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.monitoring.MortalityDetailScreen(
                    deathId = deathId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Routes.Builders.productDetails(productId))
                    }
                )
            }
        }
        
        // Farm Activity Detail Screen
        composable(
            route = Routes.Monitoring.FARM_ACTIVITY_DETAIL,
            arguments = listOf(navArgument("activityId") { type = NavType.StringType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")
            if (activityId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid activity ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.farmer.FarmActivityDetailScreen(
                    activityId = activityId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Routes.Builders.productDetails(productId))
                    }
                )
            }
        }
        
        // Bird History Screen - Unified timeline for a bird/batch
        composable(
            route = Routes.Monitoring.BIRD_HISTORY,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId")
            if (assetId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid asset ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.farmer.asset.BirdHistoryScreen(
                    assetId = assetId,
                    onNavigateBack = { navController.popBackStack() },
                    onEventClick = { eventId, eventType ->
                        when (eventType) {
                            "VACCINATION" -> navController.navigate(Routes.Builders.vaccinationDetail(eventId))
                            "GROWTH" -> navController.navigate(Routes.Builders.growthDetail(eventId))
                            "MORTALITY" -> navController.navigate(Routes.Builders.mortalityDetail(eventId))
                            "ACTIVITY" -> navController.navigate(Routes.Builders.farmActivityDetail(eventId))
                            else -> { /* Daily logs don't have detail screens yet */ }
                        }
                    }
                )
            }
        }
        
        // ============ END: Action Detail Routes ============
        
        // ============ Glass Box: Public Farm Profile ============
        composable(
            route = Routes.PublicFarm.PROFILE,
            arguments = listOf(navArgument("farmerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val farmerId = backStackEntry.arguments?.getString("farmerId")
            if (farmerId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid farmer ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.farmer.profile.PublicFarmProfileScreen(
                    onBack = { navController.popBackStack() },
                    onContact = { type ->
                        // Handle contact action (call or whatsapp)
                        timber.log.Timber.d("Contact requested: $type for farmer $farmerId")
                    }
                )
            }
        }
        
        // Shared breeding management route for enthusiasts
        composable(
            route = Routes.MONITORING_BREEDING,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/breeding" })
        ) {
            BreedingFlowScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenPairDetail = { pairId -> navController.navigate("breeding/pair/$pairId") },
                onOpenBatchDetail = { batchId -> navController.navigate("hatching/batch/$batchId") }
            )
        }

    // Hatching management route with deep link to support notifications
        composable(
            route = Routes.MONITORING_HATCHING,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://hatching" })
        ) {
            com.rio.rostry.ui.monitoring.HatchingProcessScreen()
        }
        // Hatching batch detail deep link (handled by HatchingProcessScreen for now)
        composable(
            route = Routes.MONITORING_HATCHING_BATCH,
            arguments = listOf(navArgument("batchId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://hatching/batch/{batchId}" })
        ) { _ ->
            com.rio.rostry.ui.monitoring.HatchingProcessScreen()
        }

        // Daily Log (list and single product)
        composable(
            route = Routes.MONITORING_DAILY_LOG,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/daily_log" })
        ) {
            com.rio.rostry.ui.monitoring.DailyLogScreen(
                onNavigateBack = { navController.popBackStack() },
                productId = null,
                onNavigateToAddBird = { navController.navigate(Routes.Builders.onboardingFarmBird("farmer")) },
                onNavigateToAddBatch = { navController.navigate(Routes.Builders.onboardingFarmBatch("farmer")) }
            )
        }
        composable(
            route = Routes.MONITORING_DAILY_LOG_PRODUCT,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/daily_log/{productId}" })
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getString("productId")
            if (pid.isNullOrBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.MONITORING_DAILY_LOG_PRODUCT}")
                ErrorScreen(message = "Invalid product ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.monitoring.DailyLogScreen(
                    onNavigateBack = { navController.popBackStack() },
                    productId = pid,
                    onNavigateToAddBird = { navController.navigate(Routes.Builders.onboardingFarmBird("farmer")) },
                    onNavigateToAddBatch = { navController.navigate(Routes.Builders.onboardingFarmBatch("farmer")) }
                )
            }
        }

        composable(
            route = Routes.MONITORING_TASKS,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/tasks" })
        ) {
            com.rio.rostry.ui.monitoring.TasksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { productId -> navController.navigate(Routes.Builders.productDetails(productId)) }
            )
        }
        
        // Farm Log - Comprehensive activity log screen
        composable(
            route = Routes.MONITORING_FARM_LOG,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/farm_log" })
        ) {
            com.rio.rostry.ui.farmer.FarmLogScreen(
                onBack = { navController.popBackStack() },
                onBirdClick = { productId -> navController.navigate(Routes.Builders.productDetails(productId)) },
                onActivityClick = { activity ->
                    // Map activity type to correct detail route
                    when (activity.activityType.uppercase()) {
                        "VACCINATION" -> navController.navigate(Routes.Builders.vaccinationDetail(activity.activityId))
                        "WEIGHT", "GROWTH" -> navController.navigate(Routes.Builders.growthDetail(activity.activityId))
                        "MORTALITY" -> navController.navigate(Routes.Builders.mortalityDetail(activity.activityId))
                        else -> navController.navigate(Routes.Builders.farmActivityDetail(activity.activityId))
                    }
                }
            )
        }
        
        composable(Routes.COMPLIANCE) {
            com.rio.rostry.ui.farmer.ComplianceScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.HOME_ENTHUSIAST) {
            EnthusiastHomeScreen(
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onOpenAnalytics = { navController.navigate(Routes.ANALYTICS_ENTHUSIAST) },
                onOpenPerformanceAnalytics = { navController.navigate(Routes.ANALYTICS_DASHBOARD) },
                onOpenFinancialAnalytics = { navController.navigate(Routes.ANALYTICS_FARMER) },
                onOpenTransfers = { navController.navigate(Routes.TRANSFER_LIST) },
                onOpenTraceability = { id -> navController.navigate(Routes.Builders.traceability(id)) },
                onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onVerifyKyc = { navController.navigate(Routes.VERIFY_ENTHUSIAST_KYC) },
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenMonitoringDashboard = { navController.navigate(Routes.MONITORING_DASHBOARD) },
                onOpenVaccination = { navController.navigate(Routes.MONITORING_VACCINATION) },
                onOpenMortality = { navController.navigate(Routes.MONITORING_MORTALITY) },
                onOpenQuarantine = { navController.navigate(Routes.MONITORING_QUARANTINE) },
                onOpenBreeding = { navController.navigate(Routes.MONITORING_BREEDING) },
                onNavigateToAddBird = { navController.navigate(Routes.Builders.onboardingFarmBird("enthusiast")) },
                onNavigateToAddBatch = { navController.navigate(Routes.Builders.onboardingFarmBatch("enthusiast")) },
                onOpenRoosterCard = { pid -> navController.navigate(Routes.Builders.roosterCard(pid)) },
                onOpenBreedingCalculator = { navController.navigate(Routes.Builders.breedingCalculator()) },
                onOpenPerformanceJournal = { navController.navigate(Routes.Builders.performanceJournal()) },
                onOpenVirtualArena = { navController.navigate(Routes.Builders.virtualArena()) }
            )
        }

        composable(Routes.EnthusiastNav.EXPLORE) {
            EnthusiastExploreScreen(
                onOpenProduct = { productId -> navController.navigate(Routes.Builders.productDetails(productId)) },
                onOpenEvent = { eventId -> navController.navigate(Routes.EVENT_DETAILS.replace("{eventId}", eventId)) },
                onShare = { _ -> /* share sheet */ Unit }
            )
        }

        composable(Routes.EnthusiastNav.CREATE) {
            if (isGuestMode) {
                // Block write action in guest mode
                LaunchedEffect(Unit) {
                    onGuestActionAttempt(Routes.EnthusiastNav.CREATE)
                    navController.popBackStack()
                }
            } else {
                EnthusiastCreateScreen(
                    onScheduleContent = { _ -> navController.navigate(Routes.SOCIAL_FEED) },
                    onStartLive = {
                        if (BuildConfig.DEBUG) navController.navigate(Routes.LIVE_BROADCAST)
                        else navController.navigate(Routes.SOCIAL_FEED)
                    },
                    onCreateShowcase = { _ -> navController.navigate(Routes.SOCIAL_FEED) },
                    onOpenRoosterCard = { pid -> navController.navigate(Routes.Builders.roosterCard(pid)) }
                )
            }
        }

        composable(
            route = Routes.EnthusiastNav.ROOSTER_CARD,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getString("productId") ?: ""
            com.rio.rostry.ui.enthusiast.creation.ShowcaseCardGeneratorScreen(
                birdId = pid,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EnthusiastNav.BREEDING_CALCULATOR) {
            com.rio.rostry.ui.enthusiast.breeding.BreedingCalculatorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EnthusiastNav.PERFORMANCE_JOURNAL) {
            com.rio.rostry.ui.enthusiast.journal.PerformanceJournalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EnthusiastNav.VIRTUAL_ARENA) {
            com.rio.rostry.ui.enthusiast.arena.VirtualArenaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EnthusiastNav.DASHBOARD) {
            EnthusiastDashboardTabs(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) },
                onOpenTraceability = { id -> navController.navigate(Routes.Builders.traceability(id)) },
                navController = navController
            )
        }

        composable(Routes.EnthusiastNav.TRANSFERS) {
            EnthusiastTransfersScreen(
                onOpenTransfer = { id -> navController.navigate(Routes.Builders.transferDetails(id)) },
                onVerifyTransfer = { id -> navController.navigate(Routes.Builders.transferVerify(id)) },
                onCreateTransfer = { navController.navigate(Routes.TRANSFER_CREATE) },
                onOpenTraceability = { id -> navController.navigate(Routes.Builders.traceability(id)) }
            )
        }

        // Enthusiast Egg Collection route (optional pairId argument via query)
        composable(Routes.EnthusiastNav.EGG_COLLECTION) {
            com.rio.rostry.ui.enthusiast.breeding.EggCollectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Enthusiast Digital Farm - Evolutionary Visuals
        composable(Routes.EnthusiastNav.DIGITAL_FARM) {
            com.rio.rostry.ui.enthusiast.digitalfarm.DigitalFarmScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { productId -> 
                    navController.navigate(Routes.Builders.productDetails(productId)) 
                },
                onNavigateToListProduct = { productId ->
                    // Navigate to create listing with pre-filled product
                    navController.navigate(Routes.FarmerNav.CREATE_WITH_PREFILL.replace("{prefillProductId}", productId))
                },
                onNavigateToLogEggs = { unitId ->
                    navController.navigate(Routes.EnthusiastNav.EGG_COLLECTION)
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onVerifyFarmerLocation = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
                onVerifyEnthusiastKyc = { navController.navigate(Routes.VERIFY_ENTHUSIAST_KYC) },
                onNavigateToAnalytics = { navController.navigate(Routes.ANALYTICS_FARMER) },
                onNavigateToStorageQuota = { navController.navigate(Routes.STORAGE_QUOTA) },
                onNavigateToAdminDashboard = { navController.navigate(Routes.ADMIN_VERIFICATION) },
                onUpgradeClick = { type -> navController.navigate(Routes.Builders.upgradeWizard(type)) },
                isAdmin = state.isAdmin
            )
        }

        composable(Routes.STORAGE_QUOTA) {
            StorageQuotaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Upgrade Wizard Route (Comment 6 & 7)
        composable(
            route = Routes.UPGRADE_WIZARD,
            arguments = listOf(navArgument("targetRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val targetRoleStr = backStackEntry.arguments?.getString("targetRole") ?: ""
            val targetRole = try {
                UserType.valueOf(targetRoleStr.uppercase())
            } catch (e: Exception) {
                null
            }

            if (targetRole == null) {
                ErrorScreen(
                    message = "Invalid target role: $targetRoleStr",
                    onBack = { navController.popBackStack() }
                )
            } else {
                com.rio.rostry.ui.upgrade.RoleUpgradeScreen(
                    targetRole = targetRole,
                    onNavigateBack = { navController.popBackStack() },
                    onUpgradeComplete = {
                        navController.navigate(Routes.Builders.upgradePostOnboarding(targetRole)) {
                            popUpTo(Routes.UPGRADE_WIZARD) { inclusive = true }
                        }
                    },
                    onNavigateToVerification = { upgradeType ->
                        navController.navigate(Routes.Builders.verificationWithType(upgradeType))
                    },
                    onNavigateToProfileEdit = { _ ->
                        navController.navigate(Routes.User.PROFILE_EDIT)
                    }
                )
            }
        }

        composable(Routes.User.PROFILE_EDIT) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Post-Onboarding Route (Comment 6 & 7)
        composable(
            route = Routes.UPGRADE_POST_ONBOARDING,
            arguments = listOf(navArgument("newRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val newRoleStr = backStackEntry.arguments?.getString("newRole") ?: ""
            val newRole = try {
                UserType.valueOf(newRoleStr.uppercase())
            } catch (e: Exception) {
                null
            }

            if (newRole == null) {
                ErrorScreen(
                    message = "Invalid role: $newRoleStr",
                    onBack = { navController.popBackStack() }
                )
            } else {
                com.rio.rostry.ui.upgrade.RoleUpgradePostOnboardingScreen(
                    newRole = newRole,
                    onComplete = {
                        val roleConfig = Routes.configFor(newRole)
                        navController.navigate(roleConfig.startDestination) {
                            popUpTo(Routes.ROOT) { inclusive = false }
                        }
                    },
                    onNavigateToFeature = { route ->
                        navController.navigate(route)
                    }
                )
            }
        }
        composable(
            route = Routes.USER_PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://user/{userId}" })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("userId") ?: ""
            if (uid.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.USER_PROFILE}")
                ErrorScreen(message = "Invalid user ID", onBack = { navController.popBackStack() })
            } else {
                SocialProfileScreen(
                    userId = uid,
                    onBack = { navController.popBackStack() },
                    onPostClick = { postId -> navController.navigate(Routes.Builders.discussionDetail(postId)) }
                )
            }
        }
        composable(Routes.SETTINGS) {
            val handle = navController.currentBackStackEntry?.savedStateHandle
            val selectedFlow = remember(handle) { handle?.getStateFlow("address_selection_result", null as String?) }
            val lastSelected = selectedFlow?.collectAsState()?.value
            com.rio.rostry.ui.settings.SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenAddressSelection = { navController.navigate(Routes.ADDRESS_SELECTION) },
                onNavigateToAdminVerification = { navController.navigate(Routes.ADMIN_VERIFICATION) },
                onNavigateToBackupRestore = { navController.navigate(Routes.Settings.BACKUP_RESTORE) },
                lastSelectedAddressJson = lastSelected,
                isAdmin = state.isAdmin,
                pendingCount = state.pendingVerificationCount
            )
        }
        composable(Routes.VERIFY_FARMER_LOCATION) {
            FarmerLocationVerificationScreen(onDone = { navController.popBackStack() })
        }
        
        // ============ NEW: Missing Screen Registrations ============
        
        // FCR Calculator Screen (Financial Analytics)
        composable(
            route = Routes.Monitoring.FCR_CALCULATOR,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId")
            if (assetId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid asset ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.monitoring.FCRCalculatorScreen(
                    assetId = assetId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        
        // Backup & Restore Screen
        composable(Routes.Settings.BACKUP_RESTORE) {
            com.rio.rostry.ui.settings.BackupRestoreScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Monthly Report Screen (Analytics)
        composable(Routes.Analytics.MONTHLY_REPORT) {
            com.rio.rostry.ui.analytics.MonthlyReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Hatchability Tracker Dashboard
        composable(Routes.Hatchability.TRACKER) {
            com.rio.rostry.ui.enthusiast.hatchability.HatchabilityTrackerScreen(
                onNavigateBack = { navController.popBackStack() },
                onPairClick = { pairId ->
                    navController.navigate(Routes.Hatchability.analysis(pairId))
                }
            )
        }
        
        // Hatchability Analysis per Pair
        composable(
            route = Routes.Hatchability.ANALYSIS,
            arguments = listOf(navArgument("pairId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pairId = backStackEntry.arguments?.getString("pairId")
            if (pairId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid pair ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.enthusiast.hatchability.HatchabilityAnalysisScreen(
                    pairId = pairId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        
        // Egg Tray Visual Grid
        composable(
            route = Routes.Hatchability.EGG_TRAY,
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")
            if (collectionId.isNullOrBlank()) {
                ErrorScreen(message = "Invalid collection ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.enthusiast.hatchability.EggTrayScreen(
                    collectionId = collectionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        
        // ============ END: Missing Screen Registrations ============
        
        composable(Routes.VERIFY_ENTHUSIAST_KYC) {
            EnthusiastKycScreen(onDone = { navController.popBackStack() })
        }

        composable(Routes.ADMIN_VERIFICATION) {
            com.rio.rostry.ui.admin.AdminVerificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.Common.VERIFICATION_WITH_TYPE,
            arguments = listOf(navArgument("upgradeType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString("upgradeType") ?: ""
            val upgradeType = try {
                com.rio.rostry.domain.model.UpgradeType.valueOf(typeStr)
            } catch (e: Exception) {
                null
            }

            if (upgradeType == null) {
                ErrorScreen(message = "Invalid upgrade type", onBack = { navController.popBackStack() })
            } else {
                // Dispatch to correct screen based on type
                when (upgradeType) {
                    com.rio.rostry.domain.model.UpgradeType.GENERAL_TO_FARMER,
                    com.rio.rostry.domain.model.UpgradeType.FARMER_VERIFICATION -> {
                        val vm: VerificationViewModel = hiltViewModel()
                        LaunchedEffect(upgradeType) {
                            vm.setUpgradeType(upgradeType)
                        }
                        FarmerLocationVerificationScreen(onDone = { navController.popBackStack() })
                    }
                    com.rio.rostry.domain.model.UpgradeType.FARMER_TO_ENTHUSIAST -> {
                         val vm: VerificationViewModel = hiltViewModel()
                        LaunchedEffect(upgradeType) {
                             vm.setUpgradeType(upgradeType)
                        }
                        EnthusiastKycScreen(onDone = { navController.popBackStack() })
                    }
                }
            }
        }

        
        composable(
            route = Routes.Onboarding.FARM_BIRD + "?role={role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val auth by sessionVm.uiState.collectAsState()
            if (!auth.isAuthenticated) {
                navController.navigate(Routes.AUTH_PHONE) { launchSingleTop = true }
                return@composable
            }
            val role = backStackEntry.arguments?.getString("role")
            com.rio.rostry.ui.onboarding.OnboardFarmBirdScreen(
                onNavigateRoute = { navController.navigate(it) },
                onBack = { navController.popBackStack() },
                role = role
            )
        }

        composable(
            route = Routes.Onboarding.FARM_BATCH + "?role={role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val auth by sessionVm.uiState.collectAsState()
            if (!auth.isAuthenticated) {
                navController.navigate(Routes.AUTH_PHONE) { launchSingleTop = true }
                return@composable
            }
            val role = backStackEntry.arguments?.getString("role")
            com.rio.rostry.ui.onboarding.OnboardFarmBatchScreen(
                onNavigateRoute = { navController.navigate(it) },
                onBack = { navController.popBackStack() },
                role = role
            )
        }

        composable(
            route = Routes.PRODUCT_DETAILS,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "rostry://product/{productId}" }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            if (productId.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.PRODUCT_DETAILS}")
                ErrorScreen(message = "Invalid product ID", onBack = { navController.popBackStack() })
            } else {
                ProductDetailsScreen(
                    productId = productId,
                    onOpenTraceability = { navController.navigate(Routes.Builders.traceability(productId)) },
                    onOpenSellerProfile = { userId -> navController.navigate(Routes.USER_PROFILE.replace("{userId}", userId)) },
                    onChatWithSeller = { sellerId -> navController.navigate(Routes.Builders.messagesThread(sellerId)) },
                    onNavigateToAuction = { auctionId -> navController.navigate(Routes.Builders.auction(auctionId)) }
                )
            }
        }

        composable(
            route = Routes.Product.AUCTION,
            arguments = listOf(navArgument("auctionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val auctionId = backStackEntry.arguments?.getString("auctionId") ?: ""
            com.rio.rostry.ui.auction.AuctionScreen(
                auctionId = auctionId,
                onBack = { navController.popBackStack() }
            )
        }

        // Canonical deep-link format for lineage QR codes: rostry://traceability/{productId}
        composable(
            route = Routes.TRACEABILITY,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "rostry://traceability/{productId}" },
                navDeepLink { uriPattern = "https://rostry.app/traceability/{productId}" },
                navDeepLink { uriPattern = "rostry://product/{productId}/lineage" },
                navDeepLink { uriPattern = "https://rostry.app/product/{productId}/lineage" }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val vm: TraceabilityViewModel = hiltViewModel()
            if (productId.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.TRACEABILITY}")
                ErrorScreen(message = "Invalid product ID", onBack = { navController.popBackStack() })
            } else {
                TraceabilityScreen(
                    vm = vm,
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onScanQr = { navController.navigate(Routes.Builders.scanQr("family_tree")) }
                )
            }
        }

        // Lightweight lineage preview (deep link target for https links)
        composable(
            route = Routes.LINEAGE_PREVIEW,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://rostry.app/lineage/{productId}" }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            com.rio.rostry.ui.traceability.LineagePreviewScreen(
                productId = productId,
                onOpenFullTree = { pid -> navController.navigate(Routes.Builders.traceability(pid)) },
                onBack = { navController.popBackStack() }
            )
        }

        // Family Tree explicit route (redirect/alias to Traceability full view)
        composable(
            route = Routes.PRODUCT_FAMILY_TREE,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            LaunchedEffect(productId) {
                if (productId.isNotBlank()) {
                    navController.navigate(Routes.Builders.traceability(productId))
                }
                navController.popBackStack()
            }
        }

        // Scanner route with typed arguments and default context
        composable(
            route = Routes.SCAN_QR + "?context={context}&transferId={transferId}",
            arguments = listOf(
                navArgument("context") { type = NavType.StringType; nullable = false; defaultValue = "traceability" },
                navArgument("transferId") { type = NavType.StringType; nullable = true; defaultValue = null }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "rostry://scan?context={context}&transferId={transferId}" }
            )
        ) { backStackEntry ->
            val ctx = backStackEntry.arguments?.getString("context")
            val tid = backStackEntry.arguments?.getString("transferId")
            val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
            val scope = rememberCoroutineScope()
            androidx.compose.material3.Scaffold(
                snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) }
            ) { padding ->
                QrScannerScreen(
                    onResult = { productId ->
                        when (ctx) {
                            "family_tree" -> {
                                navController.navigate(Routes.Builders.traceability(productId))
                            }
                            "transfer_verify" -> {
                                if (!tid.isNullOrBlank()) {
                                    navController.navigate(Routes.Builders.transferVerify(tid))
                                } else {
                                    android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.SCAN_QR} missing transferId")
                                    scope.launch { snackbarHostState.showSnackbar("Invalid scan context") }
                                }
                            }
                            "traceability" -> {
                                navController.navigate(Routes.Builders.traceability(productId))
                            }
                            "product_details" -> {
                                navController.navigate(Routes.Builders.productDetails(productId))
                            }
                            else -> {
                                scope.launch { snackbarHostState.showSnackbar("Invalid scan context") }
                            }
                        }
                    },
                    onValidate = { candidate -> candidate.isNotBlank() && candidate.length in 3..64 && candidate.none { it.isWhitespace() } },
                    hint = "Scan or enter ROSTRY product ID",
                    onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                )
            }
        }

        // Marketplace sandbox for QA/demo to exercise product validation and payments
        composable(Routes.PRODUCT_SANDBOX) {
            com.rio.rostry.ui.marketplace.MarketplaceSandboxScreen()
        }

        composable(
            route = Routes.TRANSFER_DETAILS,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://transfer/{transferId}" })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            if (transferId.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.TRANSFER_DETAILS}")
                ErrorScreen(message = "Invalid transfer ID", onBack = { navController.popBackStack() })
            } else {
                TransferDetailsScreen(transferId = transferId, onOpenVerify = { id -> navController.navigate(Routes.Builders.transferVerify(id)) })
            }
        }

        composable(
            route = Routes.TRANSFER_VERIFY,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://transfer/{transferId}/verify" })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            if (transferId.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.TRANSFER_VERIFY}")
                ErrorScreen(message = "Invalid transfer ID", onBack = { navController.popBackStack() })
            } else {
                TransferVerificationScreen(
                    transferId = transferId,
                    onScanProduct = { navController.navigate(Routes.Builders.scanQr("transfer_verify", transferId)) }
                )
            }
        }

        // Generic transfer list route used across notifications etc.
        composable(Routes.TRANSFER_LIST) {
            EnthusiastTransfersScreen(
                onOpenTransfer = { id -> navController.navigate(Routes.Builders.transferDetails(id)) },
                onVerifyTransfer = { id -> navController.navigate(Routes.Builders.transferVerify(id)) },
                onCreateTransfer = { navController.navigate(Routes.TRANSFER_CREATE) },
                onOpenTraceability = { id -> navController.navigate(Routes.Builders.traceability(id)) }
            )
        }

        composable(
            route = Routes.ORDER_DETAILS,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://order/{orderId}" })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            if (orderId.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.ORDER_DETAILS}")
                ErrorScreen(message = "Invalid order ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.order.OrderTrackingScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderCancelled = { navController.popBackStack() },
                    onRateOrder = { _ -> /* Rating handled in screen */ },
                    onContactSupport = { /* Navigate to support */ }
                )
            }
        }

        // Create transfer route
        composable(Routes.TRANSFER_CREATE) {
            if (isGuestMode) {
                // Block write action in guest mode
                LaunchedEffect(Unit) {
                    onGuestActionAttempt(Routes.TRANSFER_CREATE)
                    navController.popBackStack()
                }
            } else {
                val vm: TransferCreateViewModel = hiltViewModel()
                val state by vm.state.collectAsState()
                // Navigate to details when created
                LaunchedEffect(state.successTransferId) {
                    if (!state.successTransferId.isNullOrBlank()) {
                        navController.navigate(Routes.Builders.transferDetails(state.successTransferId!!)) {
                            launchSingleTop = true
                        }
                    }
                }
                TransferCreateScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Routes.SOCIAL_FEED + "?postId={postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType; nullable = true; defaultValue = null }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "rostry://social/feed" },
                navDeepLink { uriPattern = "rostry://social/feed?postId={postId}" }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            com.rio.rostry.ui.social.SocialFeedScreen(
                onOpenThread = { threadId -> navController.navigate(Routes.Builders.discussionDetail(threadId)) },
                onOpenGroups = {
                    if (BuildConfig.DEBUG) navController.navigate(Routes.GROUPS)
                    else navController.navigate(Routes.LEADERBOARD)
                },
                onOpenEvents = { navController.navigate(Routes.EVENTS) },
                onOpenExpert = { navController.navigate(Routes.EXPERT_BOOKING) },
                onOpenProfile = { userId -> navController.navigate(Routes.Builders.userProfile(userId)) },
                onOpenStoryViewer = { index -> navController.navigate(Routes.Builders.storyViewer(index)) },
                onOpenStoryCreator = { navController.navigate(Routes.Builders.storyCreator()) }
            )
        }

        composable(
            route = Routes.Social.STORY_VIEWER,
            arguments = listOf(navArgument("initialIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("initialIndex") ?: 0
            StoryViewerScreen(
                initialIndex = index,
                onFinished = { navController.popBackStack() }
            )
        }

        composable(Routes.Social.STORY_CREATOR) {
            StoryCreatorScreen(onBack = { navController.popBackStack() })
        }



        composable(
            route = Routes.Social.DISCUSSION_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            DiscussionDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() },
                onProfileClick = { userId -> navController.navigate(Routes.Builders.userProfile(userId)) }
            )
        }

        // Analytics dashboard (Enthusiast)
        composable(Routes.ANALYTICS_DASHBOARD) {
            EnthusiastDashboardScreen(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
            )
        }

        composable(
            route = Routes.MESSAGES_THREAD,
            arguments = listOf(navArgument("threadId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://messages/{threadId}" })
        ) { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
            if (threadId.isBlank()) {
                android.util.Log.w("AppNavHost", "Invalid argument for route: ${Routes.MESSAGES_THREAD}")
                ErrorScreen(message = "Invalid thread ID", onBack = { navController.popBackStack() })
            } else {
                com.rio.rostry.ui.messaging.ThreadScreen(threadId = threadId, onBack = { navController.popBackStack() })
            }
        }

        // Monitoring performance summary screen
        composable(Routes.MONITORING_PERFORMANCE) {
            FarmPerformanceScreen()
        }

        composable(
            route = Routes.MESSAGES_GROUP,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            com.rio.rostry.ui.messaging.GroupChatScreen(groupId = groupId, onBack = { navController.popBackStack() })
        }

        if (BuildConfig.DEBUG) {
            composable(Routes.GROUPS) { PlaceholderScreen(title = "Groups") }
        }
        composable(Routes.EVENTS) { com.rio.rostry.ui.events.EventsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.EXPERT_BOOKING) { com.rio.rostry.ui.expert.ExpertBookingScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.MODERATION) { 
            com.rio.rostry.ui.moderation.ModerationScreen(
                onOpenVerifications = { navController.navigate(Routes.Social.MODERATION_VERIFICATIONS) }
            ) 
        }
        composable(Routes.Social.MODERATION_VERIFICATIONS) {
             // Re-use ModerationScreen or a specific sub-screen if available. 
             // For now, assuming ModerationScreen handles tabs or we pass a flag.
             // Or if ModerationScreen IS the list, we might just need to ensure it fetches verifications.
             // Let's assume ModerationScreen has a tab for verifications.
             com.rio.rostry.ui.moderation.ModerationScreen(initialTab = 1) // Assuming 1 is verifications
        }
        composable(Routes.LEADERBOARD) { com.rio.rostry.ui.social.LeaderboardScreen() }
        composable(Routes.LIVE_BROADCAST) {
            if (BuildConfig.DEBUG) {
                LiveBroadcastScreen(onBack = { navController.popBackStack() })
            } else {
                PlaceholderScreen(title = "Live (Beta)")
            }
        }

        // Community Hub destinations
        composable(Routes.COMMUNITY_HUB) {
            val sessionVm: SessionViewModel = hiltViewModel()
            val sessionState by sessionVm.uiState.collectAsState()
            val userType = sessionState.role ?: com.rio.rostry.domain.model.UserType.GENERAL
            com.rio.rostry.ui.community.CommunityHubScreen(
                userType = userType,
                onNavigateToThread = { threadId -> navController.navigate(Routes.Builders.messagesThread(threadId)) },
                onNavigateToGroup = { groupId -> navController.navigate(Routes.CommunityHub.createGroupRoute(groupId)) },
                onNavigateToEvent = { eventId -> navController.navigate(Routes.CommunityHub.createEventRoute(eventId)) },
                onNavigateToExpert = { expertId -> navController.navigate(Routes.CommunityHub.createExpertRoute(expertId)) },
                onNavigateToPost = { postId -> navController.navigate(Routes.Builders.productDetails(postId)) }
            )
        }

        if (BuildConfig.DEBUG) {
            composable(
                route = Routes.GROUP_DETAILS,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                PlaceholderScreen(title = "Group: $groupId")
            }
        }

        composable(
            route = Routes.EVENT_DETAILS,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            PlaceholderScreen(title = "Event: $eventId")
        }

        composable(
            route = Routes.EXPERT_PROFILE,
            arguments = listOf(navArgument("expertId") { type = NavType.StringType })
        ) { backStackEntry ->
            val expertId = backStackEntry.arguments?.getString("expertId") ?: return@composable
            PlaceholderScreen(title = "Expert: $expertId")
        }

        composable(Routes.NOTIFICATIONS) {
            val vm: NotificationsViewModel = hiltViewModel()
            NotificationsScreen(
                vm = vm,
                onOpenMessages = { navController.navigate(Routes.MESSAGES_OUTBOX) },
                onOpenOrders = { navController.navigate(Routes.TRANSFER_LIST) },
                onBack = { navController.popBackStack() },
                onOpenRoute = { route -> navController.navigate(route) }
            )
        }

        // Address Selection (Web-based via WebView)
        composable(Routes.ADDRESS_SELECTION) {
            com.rio.rostry.ui.settings.AddressSelectionWebViewScreen(
                onBack = { navController.popBackStack() },
                onSubmit = { json ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "address_selection_result",
                        json
                    )
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ANALYTICS_GENERAL) {
            com.rio.rostry.ui.analytics.GeneralDashboardScreen(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
            )
        }
        composable(Routes.ANALYTICS_FARMER) {
            com.rio.rostry.ui.analytics.FarmerDashboardScreen(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
            )
        }
        composable(Routes.ANALYTICS_ENTHUSIAST) {
            com.rio.rostry.ui.analytics.EnthusiastDashboardScreen(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
            )
        }
        composable(Routes.REPORTS) { com.rio.rostry.ui.analytics.ReportsScreen() }

        // Monitoring routes (wired to monitoring module screens)
        composable(
            route = Routes.MONITORING_VACCINATION,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/vaccination" })
        ) {
            com.rio.rostry.ui.monitoring.VaccinationScheduleScreen(
                onListProduct = { productId ->
                    navController.navigate(Routes.Builders.farmerCreateWithPrefill(productId))
                }
            )
        }
        composable(
            route = Routes.MONITORING_MORTALITY,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/mortality" })
        ) {
            com.rio.rostry.ui.monitoring.MortalityTrackingScreen()
        }
        composable(
            route = Routes.MONITORING_QUARANTINE,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/quarantine" })
        ) {
            com.rio.rostry.ui.monitoring.QuarantineManagementScreen()
        }
        composable(
            route = Routes.MONITORING_GROWTH,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/growth" })
        ) {
            com.rio.rostry.ui.monitoring.GrowthTrackingScreen(
                onListProduct = { productId ->
                    navController.navigate(Routes.Builders.farmerCreateWithPrefill(productId))
                }
            )
        }
        composable(
            route = Routes.MONITORING_HATCHING,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/hatching" })
        ) {
            com.rio.rostry.ui.monitoring.HatchingProcessScreen()
        }
        composable(
            route = Routes.MONITORING_DASHBOARD,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/dashboard" })
        ) {
            com.rio.rostry.ui.monitoring.FarmMonitoringScreen(
                onOpenGrowth = { navController.navigate(Routes.MONITORING_GROWTH) },
                onOpenVaccination = { navController.navigate(Routes.MONITORING_VACCINATION) },
                onOpenBreeding = { navController.navigate(Routes.MONITORING_BREEDING) },
                onOpenQuarantine = { navController.navigate(Routes.MONITORING_QUARANTINE) },
                onOpenMortality = { navController.navigate(Routes.MONITORING_MORTALITY) },
                onOpenHatching = { navController.navigate(Routes.MONITORING_HATCHING) },
                onOpenPerformance = { navController.navigate(Routes.MONITORING_PERFORMANCE) }
            )
        }
        composable(
            route = Routes.MONITORING_PERFORMANCE,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/performance" })
        ) {
            com.rio.rostry.ui.monitoring.FarmPerformanceScreen()
        }
        
        // Farm-Marketplace Bridge: Deep link to add product to farm monitoring
        composable(
            route = "add-to-farm?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://add-to-farm?productId={productId}" })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val cartVm: com.rio.rostry.ui.general.cart.GeneralCartViewModel = hiltViewModel()
            
            // Trigger the dialog when this route is navigated to
            LaunchedEffect(productId) {
                if (productId != null) {
                    cartVm.showAddToFarmDialogForProduct(productId)
                }
            }
            
            // Use GeneralCartRoute to display the dialog
            com.rio.rostry.ui.general.cart.GeneralCartRoute(
                onCheckoutComplete = { navController.popBackStack() },
                viewModel = cartVm
            )
        }

        // Loveable product features
        composable(Routes.ACHIEVEMENTS) {
            com.rio.rostry.ui.gamification.AchievementsScreen()
        }
        composable(Routes.INSIGHTS) {
            com.rio.rostry.ui.insights.InsightsScreen()
        }
        composable(Routes.FEEDBACK) {
            com.rio.rostry.ui.feedback.FeedbackScreen()
        }
        composable(Routes.HELP) {
            com.rio.rostry.ui.support.HelpScreen()
        }
        composable(Routes.ONBOARD_GENERAL) {
            com.rio.rostry.ui.onboarding.OnboardingScreen(role = com.rio.rostry.domain.model.UserType.GENERAL, onComplete = { navController.popBackStack() })
        }
        composable(Routes.ONBOARD_FARMER) {
            com.rio.rostry.ui.onboarding.OnboardingScreen(role = com.rio.rostry.domain.model.UserType.FARMER, onComplete = { navController.popBackStack() })
        }
        composable(Routes.ONBOARD_ENTHUSIAST) {
            com.rio.rostry.ui.onboarding.OnboardingScreen(role = com.rio.rostry.domain.model.UserType.ENTHUSIAST, onComplete = { navController.popBackStack() })
        }



        // Sync issues screen
        composable(Routes.SYNC_ISSUES) {
            SyncIssuesScreen(navController = navController)
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onBack: () -> Unit) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = message)
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
private fun RoleBottomBar(
    navController: NavHostController,
    navConfig: RoleNavigationConfig,
    currentRoute: String?,
    isGuestMode: Boolean = false,
    onGuestActionAttempt: (String) -> Unit = {}
) {
    if (navConfig.bottomNav.isNotEmpty()) {
        val notifVm: com.rio.rostry.ui.notifications.NotificationsViewModel = hiltViewModel()
        val notifState by notifVm.ui.collectAsState()
        LaunchedEffect(Unit) { notifVm.refresh() }
        NavigationBar {
            navConfig.bottomNav.forEach { destination ->
                val baseCurrent = currentRoute
                    ?.substringBefore("/{")
                    ?.substringBefore("?")
                val baseDest = destination.route
                    .substringBefore("/{")
                    .substringBefore("?")
                val selected = baseCurrent == baseDest
                val labelInitial = destination.label.firstOrNull()?.uppercaseChar()?.toString() ?: "•"
                val badgeCount = when (destination.route) {
                    Routes.FarmerNav.MARKET -> notifState.pendingOrders
                    Routes.FarmerNav.COMMUNITY -> notifState.unreadMessages
                    else -> 0
                }
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        val isCreate = destination.route.endsWith("/create")
                        // Block write actions in guest mode
                        if (isGuestMode && isCreate) {
                            onGuestActionAttempt(destination.route)
                        } else {
                            navController.navigate(destination.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    },
                    icon = {
                        val icon = iconForRoute(destination.route)
                        BadgedBox(badge = {
                            if (badgeCount > 0) Badge { Text(badgeCount.toString()) }
                        }) {
                            if (icon != null) {
                                Icon(imageVector = icon, contentDescription = destination.label)
                            } else {
                                Text(labelInitial)
                            }
                        }
                    },
                    label = { Text(destination.label) }
                )
            }
        }
    }
}

// Map bottom navigation routes to Material icons
private fun iconForRoute(route: String): androidx.compose.ui.graphics.vector.ImageVector? {
    val base = route.substringBefore("/{")
    return when (base) {
        Routes.FarmerNav.HOME, Routes.EnthusiastNav.HOME -> Icons.Filled.Home
        Routes.FarmerNav.FARM_ASSETS -> Icons.Filled.Pets // My Farm tab icon
        Routes.FarmerNav.MARKET -> Icons.Filled.Store
        Routes.FarmerNav.CREATE, Routes.EnthusiastNav.CREATE -> Icons.Filled.Add
        Routes.FarmerNav.COMMUNITY -> Icons.Filled.Groups
        Routes.FarmerNav.PROFILE -> Icons.Filled.Person
        Routes.EnthusiastNav.EXPLORE -> Icons.Filled.Search
        Routes.EnthusiastNav.DASHBOARD -> Icons.Filled.Dashboard
        Routes.EnthusiastNav.TRANSFERS -> Icons.Filled.SwapHoriz
        Routes.GeneralNav.MARKET -> Icons.Filled.Store
        Routes.GeneralNav.EXPLORE -> Icons.Filled.Search
        Routes.GeneralNav.CREATE -> Icons.Filled.Add
        Routes.GeneralNav.CART -> Icons.Filled.Store
        Routes.GeneralNav.PROFILE -> Icons.Filled.Person
        else -> null
    }
}

@Composable
private fun NotificationsAction(navController: NavHostController) {
    val vm: NotificationsViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }
    BadgedBox(
        badge = { if (state.total > 0) Badge { Text(state.total.toString()) } }
    ) {
        IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) {
            Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AccountMenuAction(
    navController: NavHostController,
    onSignOut: () -> Unit,
    isGuestMode: Boolean = false,
    pendingCount: Int = 0
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = { expanded = true },
                onLongClick = { expanded = true }
            )
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Icon(imageVector = Icons.Filled.Person, contentDescription = "Account")
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Account menu")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (!isGuestMode) {
                DropdownMenuItem(text = { Text("Profile") }, onClick = {
                    expanded = false
                    navController.navigate(Routes.PROFILE)
                })
                DropdownMenuItem(text = { 
                    if (pendingCount > 0) Text("Settings ($pendingCount)") else Text("Settings") 
                }, onClick = {
                    expanded = false
                    navController.navigate(Routes.SETTINGS)
                })
            }
            DropdownMenuItem(text = { Text(if (isGuestMode) "Exit Guest Mode" else "Sign out") }, onClick = {
                expanded = false
                onSignOut()
            })
        }
    }
}

/**
 * Guest mode banner shown at top of screen
 */
@Composable
private fun GuestModeBanner(onSignInClick: () -> Unit) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "You're browsing as a guest. Sign in to unlock all features.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSignInClick) {
                Text("Sign In")
            }
        }
    }
}

/**
 * Dialog shown when guest user attempts write action
 */
@Composable
private fun GuestSignInDialog(
    onSignIn: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign in to continue") },
        text = { Text("This feature requires an account. Sign in to unlock all features and save your progress.") },
        confirmButton = {
            Button(onClick = onSignIn) {
                Text("Sign In")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Helper to determine if a route is a write action (create, edit, delete)
 */
private fun isWriteAction(route: String): Boolean {
    val writeRoutes = setOf(
        Routes.FarmerNav.CREATE,
        Routes.EnthusiastNav.CREATE,
        Routes.TRANSFER_CREATE,
        Routes.PRODUCT_CREATE
    )
    val baseRoute = route.substringBefore("?")
    return writeRoutes.any { baseRoute.contains(it) } || route.contains("/create")
}

@Composable
private fun TransferDetailsScreen(transferId: String, onOpenVerify: (String) -> Unit) {
    val vm: com.rio.rostry.ui.transfer.TransferDetailsViewModel = hiltViewModel()
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state by vm.state.collectAsState()
    Column(Modifier.padding(16.dp)) {
        Text(text = "Transfer: $transferId", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        state.transfer?.let { t ->
            Text("Status: ${t.status}")
            Text("Amount: ${t.amount} ${t.currency}")
        }
        TextButton(onClick = { onOpenVerify(transferId) }, modifier = Modifier.padding(top = 8.dp)) { Text("Open Verification Flow") }
        Text("Verifications", modifier = Modifier.padding(top = 12.dp))
        LazyColumn {
            items(state.verifications) { v ->
                Text("${v.step} • ${v.status}")
            }
        }
        Text("Disputes", modifier = Modifier.padding(top = 12.dp))
        LazyColumn {
            items(state.disputes) { d ->
                Text("${d.status} • ${d.reason}")
            }
        }
        Text("Audit Logs", modifier = Modifier.padding(top = 12.dp))
        LazyColumn {
            items(state.logs) { l ->
                Text("${l.action} • ${l.createdAt}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceabilityScreen(
    vm: TraceabilityViewModel,
    productId: String,
    onBack: () -> Unit,
    onScanQr: () -> Unit
) {
    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) vm.load(productId)
    }
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var showEventSheet by remember { mutableStateOf(false) }
    val selectedNode = state.selectedNodeId
    LaunchedEffect(selectedNode, state.selectedNodeEvents) {
        showEventSheet = selectedNode != null && state.selectedNodeEvents != null
    }
    // If needed, handle scannedProductId at the caller level and pass via productId
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            TopAppBar(
                title = { Text(text = "Traceability") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            Text(text = "For: ${state.rootId}", modifier = Modifier.padding(top = 8.dp))
            val scope = rememberCoroutineScope()
            var exporting by remember { mutableStateOf(false) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onScanQr, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Scan QR")
                }
                OutlinedButton(
                    onClick = {
                        if (!exporting) {
                            exporting = true
                            scope.launch {
                                val uri = vm.generateLineageProof(state.rootId)
                                exporting = false
                                val share = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(share, "Share Lineage Proof"))
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    enabled = !exporting
                ) {
                    if (exporting) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.height(18.dp))
                            Text("Exporting…")
                        }
                    } else {
                        Text("Export Proof")
                    }
                }
            }
            var resetKey by remember { mutableIntStateOf(0) }
            OutlinedButton(onClick = { resetKey++ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Reset Zoom & Center")
            }
            FamilyTreeView(
                rootId = state.rootId,
                layersUp = state.layersUp,
                layersDown = state.layersDown,
                edges = state.edges,
                resetKey = resetKey,
                modifier = Modifier.padding(top = 12.dp),
                onNodeClick = { nodeId -> vm.selectNode(nodeId) },
                onLoadMoreUp = { vm.loadMoreAncestors() },
                onLoadMoreDown = { vm.loadMoreDescendants() },
                nodeMetadata = state.nodeMetadata
            )
            if (state.transferChain.isNotEmpty()) {
                Text(text = "Transfer Chain", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                LazyColumn {
                    items(state.transferChain) { item ->
                        when (item) {
                            is com.rio.rostry.data.database.entity.TransferEntity -> {
                                Text("Transfer • ${item.type} • ${item.status} • ${item.initiatedAt}")
                            }
                            is com.rio.rostry.data.database.entity.ProductTrackingEntity -> {
                                Text("Tracking • ${item.status} • ${item.timestamp}")
                            }
                            else -> Text(item.toString())
                        }
                    }
                }
            }
        }
        if (showEventSheet && state.selectedNodeEvents != null) {
            val selectedId = state.selectedNodeId
            val md = if (selectedId != null) state.nodeMetadata[selectedId] else null
            if (md != null) {
                com.rio.rostry.ui.traceability.NodeEventTimelineSheet(
                    nodeMetadata = md,
                    events = state.selectedNodeEvents!!,
                    onDismiss = { vm.clearSelection(); showEventSheet = false }
                )
            }
        }
        if (state.loadingMore) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
        }
    }
}

 
