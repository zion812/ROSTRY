package com.rio.rostry.ui.navigation

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.BuildConfig
import com.rio.rostry.core.common.session.SessionManager
import com.rio.rostry.core.navigation.NavigationRegistry
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.AuthWelcomeScreen
import com.rio.rostry.ui.components.OfflineBanner
import com.rio.rostry.ui.components.SyncStatusIndicator
import com.rio.rostry.ui.components.SyncStatusViewModel
import com.rio.rostry.ui.notifications.NotificationsViewModel
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.ui.splash.SplashScreen
import com.rio.rostry.ui.sync.SyncIssuesScreen
import com.rio.rostry.utils.network.FeatureToggles
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavHost(
    featureToggles: FeatureToggles,
    firebaseAuth: FirebaseAuth,
    navigationRegistry: NavigationRegistry
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
            val isGuestMode = state.authMode == SessionManager.AuthMode.GUEST
            RoleNavScaffold(
                navConfig = navConfig,
                sessionVm = sessionVm,
                state = state,
                syncViewModel = syncViewModel,
                isGuestMode = isGuestMode,
                navigationRegistry = navigationRegistry
            )
        }
        state.isAuthenticated && state.error != null -> {
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
        }
        else -> {
            AuthFlow(
                state = state,
                onAuthenticated = { sessionVm.refresh() },
                navigationRegistry = navigationRegistry
            )
        }
    }
}

// Routes are now registered by NavigationProviders
// See: FarmerToolsNavigationProvider, EnthusiastToolsNavigationProvider, MonitoringNavigationProvider,
// MarketplaceNavigationProvider, SocialNavigationProvider, ProfileNavigationProvider,
// AdminNavigationProvider, AnalyticsNavigationProvider

@Composable
private fun RoleNavScaffold(
    navConfig: RoleNavigationConfig,
    sessionVm: SessionViewModel,
    state: SessionViewModel.SessionUiState,
    syncViewModel: SyncStatusViewModel,
    isGuestMode: Boolean = false,
    navigationRegistry: NavigationRegistry
) {
    val navController = rememberNavController()
    val syncState by syncViewModel.syncState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val authViewModel: AuthViewModel = hiltViewModel()
    var showSignInDialog by remember { mutableStateOf(false) }
    var pendingGuestAction by remember { mutableStateOf<String?>(null) }

    // Deep link handling
    LaunchedEffect(navConfig.role) {
        val pending = sessionVm.consumePendingDeepLink()
        if (!pending.isNullOrBlank()) {
            if (isGuestMode && isWriteAction(pending)) {
                pendingGuestAction = pending
                showSignInDialog = true
            } else if (!isGuestMode) {
                val allowed = navConfig.accessibleRoutes
                if (Routes.isRouteAccessible(allowed, pending)) {
                    navController.navigate(pending)
                } else {
                    pendingGuestAction = pending
                    showSignInDialog = true
                }
            } else {
                navController.navigate(pending)
            }
        }
    }

    // Route guard
    if (!isGuestMode) {
        DisposableEffect(navController, authViewModel) {
            val listener: (androidx.navigation.NavController, androidx.navigation.NavDestination, android.os.Bundle?) -> Unit =
                { controller, destination, _ ->
                    val pattern = destination.route ?: ""
                    val base = pattern.substringBefore("?")
                    val querySuffix = pattern.substringAfter("?", "").takeIf { it.isNotBlank() }
                        ?.split("&")?.mapNotNull { it.substringBefore("=").takeIf { k -> k.isNotBlank() } }
                        ?.sorted()?.joinToString("&")?.let { "?$it" } ?: ""
                    val route = base + querySuffix
                    if (route.isNotBlank() && !Routes.isRouteAccessible(navConfig.accessibleRoutes, route)) {
                        controller.navigate(navConfig.startDestination) {
                            launchSingleTop = true
                            popUpTo(controller.graph.startDestinationId) { inclusive = false }
                        }
                    }
                }
            navController.addOnDestinationChangedListener(listener)
            onDispose { navController.removeOnDestinationChangedListener(listener) }
        }
    }

    LaunchedEffect(syncViewModel) {
        syncViewModel.events.collectLatest { event ->
            when (event) {
                SyncStatusViewModel.SyncEvent.ViewSyncDetails -> navController.navigate(Routes.SYNC_ISSUES)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                val isHomeScreen = currentRoute in setOf(
                    Routes.FarmerNav.HOME, Routes.EnthusiastNav.HOME, Routes.GeneralNav.HOME,
                    Routes.Social.FEED, Routes.EnthusiastNav.EXPLORE, Routes.EnthusiastNav.DASHBOARD
                )
                if (isHomeScreen) {
                    TopAppBar(
                        title = { Text("ROSTRY") },
                        actions = {
                            if (isGuestMode) {
                                Button(onClick = { sessionVm.signOut() }, modifier = Modifier.padding(end = 8.dp)) {
                                    Text("Sign In")
                                }
                            }
                            SyncStatusIndicator(syncState = syncState, onClick = { syncViewModel.viewSyncDetails() })
                            AccountMenuAction(navController, { sessionVm.signOut() }, isGuestMode, state.pendingVerificationCount)
                            if (!isGuestMode) NotificationsAction(navController)
                        }
                    )
                }
            },
            floatingActionButton = {
                val showFab = !isGuestMode && navConfig.role == UserType.ENTHUSIAST && 
                    currentRoute in setOf(Routes.EnthusiastNav.EXPLORE, Routes.Social.FEED, Routes.EnthusiastNav.DASHBOARD)
                if (showFab) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = { navController.navigate(Routes.EnthusiastNav.CREATE) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Create")
                    }
                }
            },
            bottomBar = {
                RoleBottomBar(navController, navConfig, currentRoute, isGuestMode) { action ->
                    pendingGuestAction = action
                    showSignInDialog = true
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (isGuestMode) {
                    GuestModeBanner { authViewModel.setFromGuest(true); sessionVm.signOut() }
                }
                OfflineBanner { navController.navigate(Routes.SYNC_ISSUES) }
                RoleNavGraph(navController, navConfig, sessionVm, state, isGuestMode, 
                    { action -> pendingGuestAction = action; showSignInDialog = true }, 
                    Modifier.weight(1f), navigationRegistry)
            }
        }
    }

    if (showSignInDialog) {
        if (isGuestMode) {
            GuestSignInDialog(
                onSignIn = {
                    authViewModel.setFromGuest(true)
                    pendingGuestAction?.let { sessionVm.setPendingDeepLink(it) }
                    sessionVm.signOut()
                    showSignInDialog = false
                },
                onDismiss = { pendingGuestAction = null; showSignInDialog = false }
            )
        } else if (authViewModel.uiState.value.needsPhoneVerificationBanner) {
            AlertDialog(
                onDismissRequest = { pendingGuestAction = null; showSignInDialog = false },
                title = { Text("Verify Phone Number") },
                text = { Text("Please verify your phone number to access this feature.") },
                confirmButton = {
                    Button(onClick = {
                        navController.navigate(Routes.PROFILE)
                        showSignInDialog = false
                        pendingGuestAction = null
                    }) { Text("Verify Phone") }
                },
                dismissButton = {
                    TextButton(onClick = { pendingGuestAction = null; showSignInDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
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
    modifier: Modifier = Modifier,
    navigationRegistry: NavigationRegistry
) {
    NavHost(
        navController = navController,
        startDestination = navConfig.startDestination,
        modifier = modifier
    ) {
        // Build registered navigation graphs from feature modules
        // All 8 NavigationProviders are registered and provide 118 routes total
        navigationRegistry.buildGraphs(navController = navController, navGraphBuilder = this)
        
        // Core app navigation destinations remain here temporarily
        // These will be migrated to feature modules in future phases
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
    if (navConfig.bottomNav.isEmpty()) return
    
    val notifVm: NotificationsViewModel = hiltViewModel()
    val notifState by notifVm.ui.collectAsState()
    LaunchedEffect(Unit) { notifVm.refresh() }
    
    NavigationBar {
        navConfig.bottomNav.forEach { destination ->
            val baseCurrent = currentRoute?.substringBefore("/{")?.substringBefore("?")
            val baseDest = destination.route.substringBefore("/{").substringBefore("?")
            val selected = baseCurrent == baseDest
            val badgeCount = when (destination.route) {
                Routes.FarmerNav.MARKET -> notifState.pendingOrders
                Routes.FarmerNav.COMMUNITY -> notifState.unreadMessages
                else -> 0
            }
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (isGuestMode && destination.route.endsWith("/create")) {
                        onGuestActionAttempt(destination.route)
                    } else {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                },
                icon = {
                    BadgedBox(badge = { if (badgeCount > 0) Badge { Text(badgeCount.toString()) } }) {
                        val icon = when (destination.route.substringBefore("/{")) {
                            Routes.FarmerNav.HOME, Routes.EnthusiastNav.HOME -> Icons.Filled.Home
                            Routes.FarmerNav.FARM_ASSETS -> Icons.Filled.Pets
                            Routes.FarmerNav.MARKET, Routes.GeneralNav.MARKET, Routes.GeneralNav.CART -> Icons.Filled.Store
                            Routes.FarmerNav.CREATE, Routes.EnthusiastNav.CREATE, Routes.GeneralNav.CREATE -> Icons.Filled.Add
                            Routes.FarmerNav.COMMUNITY -> Icons.Filled.Groups
                            Routes.FarmerNav.PROFILE, Routes.GeneralNav.PROFILE, Routes.Admin.VERIFICATION -> Icons.Filled.Person
                            Routes.EnthusiastNav.EXPLORE, Routes.GeneralNav.EXPLORE -> Icons.Filled.Search
                            Routes.EnthusiastNav.DASHBOARD, Routes.Admin.DASHBOARD -> Icons.Filled.Dashboard
                            Routes.EnthusiastNav.TRANSFERS, Routes.Admin.UPGRADE_REQUESTS -> Icons.Filled.SwapHoriz
                            Routes.Admin.USER_MANAGEMENT -> Icons.Filled.Groups
                            else -> null
                        }
                        if (icon != null) {
                            Icon(imageVector = icon, contentDescription = destination.label)
                        } else {
                            Text(destination.label.firstOrNull()?.uppercaseChar()?.toString() ?: "•")
                        }
                    }
                },
                label = { Text(destination.label) }
            )
        }
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

@Composable
private fun AccountMenuAction(
    navController: NavHostController,
    onSignOut: () -> Unit,
    isGuestMode: Boolean = false,
    pendingCount: Int = 0
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.combinedClickable(onClick = { expanded = true }, onLongClick = { expanded = true }).padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Icon(imageVector = Icons.Filled.Person, contentDescription = "Account")
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Account menu")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (!isGuestMode) {
                DropdownMenuItem(text = { Text("Profile") }, onClick = { expanded = false; navController.navigate(Routes.PROFILE) })
                DropdownMenuItem(
                    text = { if (pendingCount > 0) Text("Settings ($pendingCount)") else Text("Settings") }, 
                    onClick = { expanded = false; navController.navigate(Routes.SETTINGS) }
                )
            }
            DropdownMenuItem(
                text = { Text(if (isGuestMode) "Exit Guest Mode" else "Sign out") }, 
                onClick = { expanded = false; onSignOut() }
            )
        }
    }
}

@Composable
private fun GuestModeBanner(onSignInClick: () -> Unit) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "You're browsing as a guest. Sign in to unlock all features.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSignInClick) { Text("Sign In") }
        }
    }
}

@Composable
private fun GuestSignInDialog(onSignIn: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign in to continue") },
        text = { Text("This feature requires an account. Sign in to unlock all features and save your progress.") },
        confirmButton = { Button(onClick = onSignIn) { Text("Sign In") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

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
private fun AuthFlow(
    state: SessionViewModel.SessionUiState,
    onAuthenticated: () -> Unit,
    navigationRegistry: NavigationRegistry
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "auth_welcome"
    ) {
        androidx.navigation.compose.composable("auth_welcome") {
            AuthWelcomeScreen(
                onAuthenticated = onAuthenticated
            )
        }
    }
}
