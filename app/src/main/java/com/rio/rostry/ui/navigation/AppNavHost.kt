package com.rio.rostry.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.rio.rostry.data.demo.DemoUserProfile
import com.rio.rostry.ui.auth.AuthViewModel
import com.rio.rostry.ui.auth.OtpScreen
import com.rio.rostry.ui.auth.PhoneInputScreen
import com.rio.rostry.ui.product.ProductDetailsScreen
import com.rio.rostry.ui.profile.ProfileScreen
import com.rio.rostry.ui.screens.HomeEnthusiastScreen
import com.rio.rostry.ui.farmer.FarmerHomeScreen
import com.rio.rostry.ui.farmer.FarmerMarketScreen
import com.rio.rostry.ui.farmer.FarmerCreateScreen
import com.rio.rostry.ui.farmer.FarmerCommunityScreen
import com.rio.rostry.ui.farmer.FarmerProfileScreen
import com.rio.rostry.ui.screens.HomeGeneralScreen
import com.rio.rostry.ui.screens.PlaceholderScreen
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.ui.traceability.FamilyTreeView
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
import com.rio.rostry.ui.enthusiast.EnthusiastCreateScreen
import com.rio.rostry.ui.enthusiast.EnthusiastDashboardHost
import com.rio.rostry.ui.enthusiast.EnthusiastTransfersScreen
import com.rio.rostry.ui.social.LiveBroadcastScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.rio.rostry.session.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.foundation.combinedClickable
import com.rio.rostry.ui.demo.DemoToolsScreen
import com.rio.rostry.ui.theme.LocalSpacing
import com.rio.rostry.ui.components.LoadingState
import com.rio.rostry.ui.onboarding.OnboardingGeneralScreen
import com.rio.rostry.ui.onboarding.OnboardingFarmerScreen
import com.rio.rostry.ui.onboarding.OnboardingEnthusiastScreen
import com.rio.rostry.ui.onboarding.RoleSelectionScreen

@Composable
fun AppNavHost() {
    val sessionVm: SessionViewModel = hiltViewModel()
    val state by sessionVm.uiState.collectAsState()
    val navConfig = state.navConfig

    when {
        state.isAuthenticated && navConfig != null -> RoleNavScaffold(navConfig, sessionVm, state)
        else -> AuthFlow(
            state = state,
            onAuthenticated = { sessionVm.refresh() },
            onDemoLogin = { u, p -> sessionVm.signInDemo(u, p) },
            onQuickSelect = { sessionVm.activateDemoProfile(it) }
        )
    }
}

@Composable
private fun AuthFlow(
    state: SessionViewModel.SessionUiState,
    onAuthenticated: () -> Unit,
    onDemoLogin: (String, String) -> Unit,
    onQuickSelect: (String) -> Unit
) {
    val navController = rememberNavController()
    val sessionVm: SessionViewModel = hiltViewModel()
    val authVm: AuthViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val sp = LocalSpacing.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showOtp by rememberSaveable { mutableStateOf(false) }
    var pendingOnboardingRoute by rememberSaveable { mutableStateOf<String?>(null) }
    val preferReal by sessionVm.preferRealAuth.collectAsState()

    LaunchedEffect(Unit) {
        authVm.navigation.collectLatest { event ->
            when (event) {
                is AuthViewModel.NavAction.ToOtp -> navController.navigate("auth/otp/${event.verificationId}")
                is AuthViewModel.NavAction.ToOnboarding -> {
                    // Ensure the NavHost (which hosts onboarding routes) is mounted first
                    showOtp = true
                    pendingOnboardingRoute = when (event.userType) {
                        null -> Routes.ROLE_SELECTION
                        com.rio.rostry.domain.model.UserType.GENERAL -> Routes.ONBOARD_GENERAL
                        com.rio.rostry.domain.model.UserType.FARMER -> Routes.ONBOARD_FARMER
                        com.rio.rostry.domain.model.UserType.ENTHUSIAST -> Routes.ONBOARD_ENTHUSIAST
                    }
                    Log.d("RostryNav", "Received ToOnboarding(userType=${event.userType}) showOtp=$showOtp pending=$pendingOnboardingRoute")
                }
                is AuthViewModel.NavAction.ToHome -> {
                    sessionVm.clearPreferRealSignIn()
                    onAuthenticated()
                }
            }
        }
    }

    // Navigate to onboarding route only after the NavHost is actually composed
    LaunchedEffect(showOtp, pendingOnboardingRoute) {
        val route = pendingOnboardingRoute
        if (showOtp && route != null) {
            Log.d("RostryNav", "Navigating to onboarding route=$route (mounted=$showOtp)")
            navController.navigate(route)
            pendingOnboardingRoute = null
        }
    }

    LaunchedEffect(preferReal) {
        if (preferReal) {
            showOtp = true
            // Ensure we are on the phone input route
            navController.navigate(Routes.AUTH_PHONE) {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.md)
    ) {
        if (!preferReal) {
            Text("Field test demo login", modifier = Modifier.fillMaxWidth())
        }

        if (state.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(sp.sm, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
                Text("Loading…")
            }
        }
        val errorMessage = state.error
        if (!errorMessage.isNullOrBlank()) {
            Text("Error: $errorMessage")
        }

        if (!preferReal) {
        Column(verticalArrangement = Arrangement.spacedBy(sp.sm)) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Demo username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Button(
                onClick = { scope.launch { onDemoLogin(username.trim(), password) } },
                enabled = username.isNotBlank() && password.isNotBlank() && !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign in with demo credentials")
            }
            state.currentDemoProfile?.let { current ->
                Text("Active demo user: ${current.fullName} (${current.role.displayName})")
            }
        } }

        if (!preferReal && state.demoProfiles.isNotEmpty()) {
            Divider()
            QuickSelectDemoList(
                profiles = state.demoProfiles,
                onSelect = onQuickSelect,
                currentId = state.currentDemoProfile?.id
            )
        }

        if (!preferReal) {
            Divider()
            TextButton(onClick = { showOtp = !showOtp }) {
                Text(if (showOtp) "Hide phone verification" else "Use phone verification")
            }
        } else {
            // In preferReal mode, we force OTP screen to be shown
            if (!showOtp) showOtp = true
        }

        if (showOtp) {
            NavHost(
                navController = navController,
                startDestination = Routes.AUTH_PHONE,
                modifier = Modifier.weight(1f)
            ) {
                composable(Routes.AUTH_PHONE) {
                    PhoneInputScreen(onNavigateToOtp = { verificationId ->
                        navController.navigate("auth/otp/$verificationId")
                    })
                }
                composable(
                    route = Routes.AUTH_OTP,
                    arguments = listOf(navArgument("verificationId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                    OtpScreen(verificationId = verificationId, onNavigateHome = onAuthenticated)
                }
                composable(Routes.ROLE_SELECTION) {
                    RoleSelectionScreen(
                        onContinue = { role ->
                            when (role) {
                                com.rio.rostry.domain.model.UserType.GENERAL -> navController.navigate(Routes.ONBOARD_GENERAL)
                                com.rio.rostry.domain.model.UserType.FARMER -> navController.navigate(Routes.ONBOARD_FARMER)
                                com.rio.rostry.domain.model.UserType.ENTHUSIAST -> navController.navigate(Routes.ONBOARD_ENTHUSIAST)
                            }
                        }
                    )
                }
                composable(Routes.ONBOARD_GENERAL) {
                    OnboardingGeneralScreen(
                        onDone = { _ -> onAuthenticated() }
                    )
                }
                composable(Routes.ONBOARD_FARMER) {
                    OnboardingFarmerScreen(
                        onDone = { _ -> onAuthenticated() }
                    )
                }
                composable(Routes.ONBOARD_ENTHUSIAST) {
                    OnboardingEnthusiastScreen(
                        onDone = { _ -> onAuthenticated() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(sp.xs))
        }
    }
}

@Composable
private fun QuickSelectDemoList(
    profiles: List<DemoUserProfile>,
    onSelect: (String) -> Unit,
    currentId: String?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Quick select demo profiles")
        profiles.groupBy { it.role }.forEach { (role, roleProfiles) ->
            Text(role.displayName, modifier = Modifier.padding(top = 4.dp))
            roleProfiles.forEach { profile ->
                OutlinedButton(onClick = { onSelect(profile.id) }, modifier = Modifier.fillMaxWidth()) {
                    val suffix = if (profile.id == currentId) " (active)" else ""
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${profile.fullName}$suffix")
                        Text(profile.headline)
                        Text(profile.location)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleNavScaffold(
    navConfig: RoleNavigationConfig,
    sessionVm: SessionViewModel,
    state: SessionViewModel.SessionUiState
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var showSwitcher by remember { mutableStateOf(false) }

    // Removed premature manual navigate. NavHost below already uses startDestination = navConfig.startDestination.

    val preferReal by sessionVm.preferRealAuth.collectAsState()
    val isDemoEffective = (state.authMode == SessionManager.AuthMode.DEMO) && !preferReal && (com.rio.rostry.BuildConfig.FLAVOR == "demo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.combinedClickable(
                            onClick = { navController.navigate(Routes.PROFILE) },
                            onLongClick = { sessionVm.signOut() }
                        )
                    ) {
                        Text("Profile")
                    }
                    AccountMenuAction(
                        navController = navController,
                        isDemo = isDemoEffective,
                        onSignOut = { sessionVm.signOut() }
                    )
                }
            )
        },
        bottomBar = {
            RoleBottomBar(
                navController = navController,
                navConfig = navConfig,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            if (isDemoEffective) {
                FloatingActionButton(onClick = { showSwitcher = true }) {
                    Text("Demo")
                }
            }
        }
    ) { padding ->
        RoleNavGraph(
            navController = navController,
            navConfig = navConfig,
            isDemo = isDemoEffective,
            onSwitchToReal = {
                sessionVm.preferRealSignIn()
                sessionVm.signOut()
            },
            modifier = Modifier.padding(padding)
        )
    }
    if (showSwitcher && isDemoEffective) {
        DemoProfilePickerDialog(
            profiles = state.demoProfiles,
            currentId = state.currentDemoProfile?.id,
            onSelect = { id ->
                sessionVm.activateDemoProfile(id)
                showSwitcher = false
            },
            onSignOut = {
                sessionVm.signOut()
                showSwitcher = false
            },
            onDismiss = { showSwitcher = false }
        )
    }
}

@Composable
private fun DemoProfilePickerDialog(
    profiles: List<DemoUserProfile>,
    currentId: String?,
    onSelect: (String) -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Switch demo profile") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(profiles) { profile ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        val suffix = if (profile.id == currentId) " (active)" else ""
                        Text("${profile.fullName}$suffix")
                        Text(profile.headline)
                        Text(profile.location)
                        TextButton(onClick = { onSelect(profile.id) }) {
                            Text("Activate")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSignOut) { Text("Sign out") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun RoleNavGraph(
    navController: NavHostController,
    navConfig: RoleNavigationConfig,
    isDemo: Boolean,
    onSwitchToReal: () -> Unit,
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
                onOpenProductDetails = { productId -> navController.navigate("product/$productId") },
                onOpenTraceability = { productId -> navController.navigate("traceability/$productId") },
                onOpenSocialFeed = { navController.navigate(Routes.SOCIAL_FEED) },
                onOpenMessages = { threadId -> navController.navigate("messages/$threadId") }
            )
        }
        // General market destination to avoid 'not a direct child' errors when navigating to general/market
        composable(Routes.GeneralNav.MARKET) {
            PlaceholderScreen(title = "General Market")
        }
        composable(Routes.HOME_FARMER) {
            FarmerHomeScreen(
                onListProduct = { navController.navigate(Routes.FarmerNav.CREATE) },
                onCheckOrders = { navController.navigate(Routes.TRANSFER_LIST) },
                onMessageBuyers = { navController.navigate(Routes.SOCIAL_FEED) }
            )
        }
        composable(Routes.FarmerNav.MARKET) {
            val vm: com.rio.rostry.ui.farmer.FarmerMarketViewModel = hiltViewModel()
            val state by vm.ui.collectAsState()
            fun map(e: com.rio.rostry.data.database.entity.ProductEntity): com.rio.rostry.ui.farmer.Listing =
                com.rio.rostry.ui.farmer.Listing(
                    id = e.productId,
                    title = e.name.ifBlank { e.category },
                    price = e.price,
                    views = 0,
                    inquiries = 0,
                    orders = 0
                )
            FarmerMarketScreen(
                onCreateListing = { navController.navigate(Routes.FarmerNav.CREATE) },
                onEditListing = { id -> navController.navigate(Routes.FarmerNav.CREATE) },
                onBoostListing = { _ -> /* Could open promo screen */ Unit },
                onPauseListing = { _ -> /* Pause listing action */ Unit },
                onOpenOrder = { threadId -> navController.navigate("messages/$threadId") },
                onOpenProduct = { productId -> navController.navigate("product/$productId") },
                selectedTabIndex = state.selectedTabIndex,
                onSelectTab = { vm.setTab(it) },
                metricsRevenue = state.metricsRevenue,
                metricsOrders = state.metricsOrders,
                metricsViews = state.metricsViews,
                isLoadingBrowse = state.isLoadingBrowse,
                isLoadingMine = state.isLoadingMine,
                browse = state.filteredBrowse.map(::map),
                mine = state.mine.map(::map),
                onRefresh = { vm.refresh() },
                onApplyPriceBreed = { min, max, breed -> vm.applyPriceBreed(min, max, breed) },
                onSelectCategoryMeat = { vm.selectCategory(com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Meat) },
                onSelectCategoryAdoption = { vm.selectCategory(com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Adoption) },
                onSelectTraceable = { vm.selectTrace(com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.Traceable) },
                onSelectNonTraceable = { vm.selectTrace(com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.NonTraceable) },
                categoryMeatSelected = state.categoryFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Meat,
                categoryAdoptionSelected = state.categoryFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.CategoryFilter.Adoption,
                traceableSelected = state.traceFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.Traceable,
                nonTraceableSelected = state.traceFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.NonTraceable
            )
        }
        composable(Routes.FarmerNav.CREATE) {
            val vvm: VerificationViewModel = hiltViewModel()
            val vstate by vvm.ui.collectAsState()
            val createVm: com.rio.rostry.ui.farmer.FarmerCreateViewModel = hiltViewModel()
            val createState by createVm.ui.collectAsState()

            LaunchedEffect(createState.successProductId) {
                if (!createState.successProductId.isNullOrBlank()) {
                    // Navigate to Market after successful publish
                    navController.navigate(Routes.FarmerNav.MARKET) {
                        launchSingleTop = true
                    }
                }
            }

            FarmerCreateScreen(
                locationVerified = vstate.user?.locationVerified == true,
                onRequestVerifyLocation = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
                onSubmitListing = { form -> createVm.submitListing(form) },
                onCreatePost = { _ -> navController.navigate(Routes.SOCIAL_FEED) }
            )
        }
        composable(Routes.FarmerNav.COMMUNITY) {
            FarmerCommunityScreen(
                onOpenThread = { threadId -> navController.navigate("messages/$threadId") },
                onOpenGroupDirectory = { navController.navigate(Routes.GROUPS) },
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
        composable(Routes.HOME_ENTHUSIAST) {
            EnthusiastHomeScreen(
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onOpenAnalytics = { navController.navigate(Routes.ANALYTICS_ENTHUSIAST) },
                onOpenPerformanceAnalytics = { navController.navigate(Routes.ANALYTICS_DASHBOARD) },
                onOpenFinancialAnalytics = { navController.navigate(Routes.ANALYTICS_FARMER) },
                onOpenTransfers = { navController.navigate(Routes.TRANSFER_LIST) },
                onOpenTraceability = { id -> navController.navigate("traceability/$id") },
                onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onVerifyKyc = { navController.navigate(Routes.VERIFY_ENTHUSIAST_KYC) },
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenMonitoringDashboard = { navController.navigate(Routes.MONITORING_DASHBOARD) },
                onOpenVaccination = { navController.navigate(Routes.MONITORING_VACCINATION) },
                onOpenMortality = { navController.navigate(Routes.MONITORING_MORTALITY) },
                onOpenQuarantine = { navController.navigate(Routes.MONITORING_QUARANTINE) },
                onOpenBreeding = { navController.navigate(Routes.MONITORING_BREEDING) }
            )
        }

        composable(Routes.EnthusiastNav.EXPLORE) {
            EnthusiastExploreScreen(
                onOpenProfile = { _ -> navController.navigate(Routes.PROFILE) },
                onOpenDiscussion = { navController.navigate(Routes.SOCIAL_FEED) },
                onShare = { _ -> /* Share via system sheet in future */ Unit }
            )
        }

        composable(Routes.EnthusiastNav.CREATE) {
            EnthusiastCreateScreen(
                onScheduleContent = { _ -> navController.navigate(Routes.SOCIAL_FEED) },
                onStartLive = { navController.navigate(Routes.LIVE_BROADCAST) },
                onCreateShowcase = { _ -> navController.navigate(Routes.SOCIAL_FEED) }
            )
        }

        composable(Routes.EnthusiastNav.DASHBOARD) {
            EnthusiastDashboardHost(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) },
                onOpenTraceability = { id -> navController.navigate("traceability/$id") }
            )
        }

        // Demo & Testing tools (available only in demo session)
        if (isDemo) {
            composable(Routes.DEMO_TOOLS) {
                DemoToolsScreen()
            }
        }

        composable(Routes.EnthusiastNav.TRANSFERS) {
            EnthusiastTransfersScreen(
                onOpenTransfer = { id -> navController.navigate("transfer/$id") },
                onVerifyTransfer = { id -> navController.navigate("transfer/$id/verify") },
                onCreateTransfer = { navController.navigate(Routes.TRANSFER_CREATE) },
                onOpenTraceability = { id -> navController.navigate("traceability/$id") }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onVerifyFarmerLocation = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
                onVerifyEnthusiastKyc = { navController.navigate(Routes.VERIFY_ENTHUSIAST_KYC) },
                onSignIn = onSwitchToReal, // treat sign in/up as switch to real path
                onSignUp = onSwitchToReal,
                isDemo = isDemo,
                onSwitchToReal = onSwitchToReal
            )
        }
        composable(Routes.VERIFY_FARMER_LOCATION) {
            FarmerLocationVerificationScreen(onDone = { navController.popBackStack() })
        }
        composable(Routes.VERIFY_ENTHUSIAST_KYC) {
            EnthusiastKycScreen(onDone = { navController.popBackStack() })
        }

        

        composable(
            route = Routes.PRODUCT_DETAILS,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailsScreen(
                productId = productId,
                onOpenTraceability = { navController.navigate("traceability/$productId") }
            )
        }

        composable(
            route = Routes.TRACEABILITY,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://traceability/{productId}" })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val vm: TraceabilityViewModel = hiltViewModel()
            TraceabilityScreen(vm = vm, productId = productId, onBack = { navController.popBackStack() })
        }

        // Marketplace sandbox for QA/demo to exercise product validation and payments
        composable(Routes.PRODUCT_SANDBOX) {
            com.rio.rostry.ui.marketplace.MarketplaceSandboxScreen()
        }

        composable(
            route = Routes.TRANSFER_DETAILS,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            TransferDetailsScreen(transferId = transferId, onOpenVerify = { id -> navController.navigate("transfer/$id/verify") })
        }

        composable(
            route = Routes.TRANSFER_VERIFY,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            TransferVerificationScreen(
                transferId = transferId
            )
        }

        // Generic transfer list route used across notifications etc.
        composable(Routes.TRANSFER_LIST) {
            EnthusiastTransfersScreen(
                onOpenTransfer = { id -> navController.navigate("transfer/$id") },
                onVerifyTransfer = { id -> navController.navigate("transfer/$id/verify") },
                onCreateTransfer = { navController.navigate(Routes.TRANSFER_CREATE) },
                onOpenTraceability = { id -> navController.navigate("traceability/$id") }
            )
        }

        // Create transfer route
        composable(Routes.TRANSFER_CREATE) {
            val vm: TransferCreateViewModel = hiltViewModel()
            val state by vm.state.collectAsState()
            // Navigate to details when created
            LaunchedEffect(state.successTransferId) {
                if (!state.successTransferId.isNullOrBlank()) {
                    navController.navigate("transfer/${state.successTransferId}") {
                        launchSingleTop = true
                    }
                }
            }
            TransferCreateScreen(
                state = state,
                onUpdate = { f, v -> vm.update(f, v) },
                onSubmit = { vm.create() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SOCIAL_FEED) {
            com.rio.rostry.ui.social.SocialFeedScreen(
                onOpenThread = { threadId -> navController.navigate("messages/$threadId") },
                onOpenGroups = { navController.navigate(Routes.GROUPS) },
                onOpenEvents = { navController.navigate(Routes.EVENTS) },
                onOpenExpert = { navController.navigate(Routes.EXPERT_BOOKING) }
            )
        }

        composable(
            route = Routes.MESSAGES_THREAD,
            arguments = listOf(navArgument("threadId") { type = NavType.StringType })
        ) { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
            com.rio.rostry.ui.messaging.DirectChatScreen(
                threadId = threadId,
                meUserId = "me",
                peerUserId = "them",
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.MESSAGES_GROUP,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            com.rio.rostry.ui.messaging.GroupChatScreen(groupId = groupId, onBack = { navController.popBackStack() })
        }

        composable(Routes.GROUPS) { com.rio.rostry.ui.groups.GroupsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.EVENTS) { com.rio.rostry.ui.events.EventsScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = Routes.EVENTS_DETAILS,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            com.rio.rostry.ui.events.EventDetailsScreen(eventId = eventId, onBack = { navController.popBackStack() })
        }
        composable(Routes.EXPERT_BOOKING) { com.rio.rostry.ui.expert.ExpertBookingScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.MODERATION) { com.rio.rostry.ui.moderation.ModerationScreen() }
        composable(Routes.LEADERBOARD) { com.rio.rostry.ui.social.LeaderboardScreen() }
        composable(Routes.LIVE_BROADCAST) { LiveBroadcastScreen(onBack = { navController.popBackStack() }) }

        // Transfers
        composable(Routes.TRANSFER_CREATE) { com.rio.rostry.ui.transfer.TransferInitiationScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = Routes.TRANSFER_VERIFY,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://transfer/verify/{transferId}" })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            com.rio.rostry.ui.transfer.VerificationWorkflowScreen(transferId = transferId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.TRANSFER_DISPUTE,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://transfer/dispute/{transferId}" })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            com.rio.rostry.ui.transfer.DisputeManagementScreen(transferId = transferId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.TRANSFER_TRAIL,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            com.rio.rostry.ui.transfer.OwnershipTrailScreen(transferId = transferId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.TRANSFER_DOCS,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://transfer/docs/{transferId}" })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            com.rio.rostry.ui.transfer.TransferDocumentationScreen(transferId = transferId, onBack = { navController.popBackStack() })
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
        composable(Routes.MONITORING_VACCINATION) {
            com.rio.rostry.ui.monitoring.VaccinationScheduleScreen()
        }
        composable(Routes.MONITORING_MORTALITY) {
            com.rio.rostry.ui.monitoring.MortalityTrackingScreen()
        }
        composable(Routes.MONITORING_QUARANTINE) {
            com.rio.rostry.ui.monitoring.QuarantineManagementScreen()
        }
        composable(Routes.MONITORING_BREEDING) {
            com.rio.rostry.ui.monitoring.BreedingManagementScreen()
        }
        composable(Routes.MONITORING_GROWTH) {
            com.rio.rostry.ui.monitoring.GrowthTrackingScreen()
        }
        composable(Routes.MONITORING_HATCHING) {
            com.rio.rostry.ui.monitoring.HatchingProcessScreen()
        }
        composable(Routes.MONITORING_DASHBOARD) {
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
        composable(Routes.MONITORING_PERFORMANCE) {
            com.rio.rostry.ui.monitoring.FarmPerformanceScreen()
        }
    }
}
@Composable
private fun RoleBottomBar(
    navController: NavHostController,
    navConfig: RoleNavigationConfig,
    currentRoute: String?
) {
    if (navConfig.bottomNav.isNotEmpty()) {
        val notifVm: com.rio.rostry.ui.notifications.NotificationsViewModel = hiltViewModel()
        val notifState by notifVm.ui.collectAsState()
        LaunchedEffect(Unit) { notifVm.refresh() }
        NavigationBar {
            navConfig.bottomNav.forEach { destination ->
                val selected = currentRoute?.startsWith(destination.route.substringBefore("/{")) == true
                val badgeCount = when (destination.route) {
                    Routes.FarmerNav.MARKET -> notifState.pendingOrders
                    Routes.FarmerNav.COMMUNITY -> notifState.unreadMessages
                    else -> 0
                }
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        BadgedBox(badge = {
                            if (badgeCount > 0) Badge { Text(badgeCount.toString()) }
                        }) {
                            val icon = when (destination.route.substringBefore("/")) {
                                "home" -> Icons.Filled.Home
                                "farmer" -> when (destination.route) {
                                    Routes.FarmerNav.HOME -> Icons.Filled.Home
                                    Routes.FarmerNav.MARKET -> Icons.Filled.Store
                                    Routes.FarmerNav.CREATE -> Icons.Filled.AddCircle
                                    Routes.FarmerNav.COMMUNITY -> Icons.Filled.Groups
                                    Routes.FarmerNav.PROFILE -> Icons.Filled.Person
                                    else -> Icons.Filled.Home
                                }
                                "enthusiast" -> when (destination.route) {
                                    com.rio.rostry.ui.navigation.Routes.EnthusiastNav.HOME -> Icons.Filled.Home
                                    com.rio.rostry.ui.navigation.Routes.EnthusiastNav.EXPLORE -> Icons.Filled.Explore
                                    com.rio.rostry.ui.navigation.Routes.EnthusiastNav.CREATE -> Icons.Filled.AddCircle
                                    com.rio.rostry.ui.navigation.Routes.EnthusiastNav.DASHBOARD -> Icons.Filled.Dashboard
                                    com.rio.rostry.ui.navigation.Routes.EnthusiastNav.TRANSFERS -> Icons.Filled.SwapHoriz
                                    else -> Icons.Filled.Home
                                }
                                else -> Icons.Filled.Home
                            }
                            val tint = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary
                            else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            Icon(imageVector = icon, contentDescription = destination.label, tint = tint)
                        }
                    },
                    label = { Text(destination.label) }
                )
            }
        }
    }
}

@Composable
private fun NotificationsAction(navController: NavHostController) {
    val vm: com.rio.rostry.ui.notifications.NotificationsViewModel = hiltViewModel()
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
    isDemo: Boolean,
    onSignOut: () -> Unit
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
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(text = { Text("Profile") }, onClick = {
                expanded = false
                navController.navigate(Routes.PROFILE)
            })
            DropdownMenuItem(text = { Text("Settings") }, onClick = {
                expanded = false
                // TODO: Replace with Settings route when available
                navController.navigate(Routes.PROFILE)
            })
            if (isDemo) {
                DropdownMenuItem(text = { Text("Demo & Testing") }, onClick = {
                    expanded = false
                    navController.navigate(Routes.DEMO_TOOLS)
                })
            }
            DropdownMenuItem(text = { Text("Sign out") }, onClick = {
                expanded = false
                onSignOut()
            })
        }
    }
}

@Composable
private fun TransferDetailsScreen(transferId: String, onOpenVerify: (String) -> Unit) {
    val vm: com.rio.rostry.ui.transfer.TransferDetailsViewModel = hiltViewModel()
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state by vm.state.collectAsState()
    run {
        val sp = LocalSpacing.current
        Column(Modifier.padding(sp.md)) {
        Text(text = "Transfer: $transferId", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        state.transfer?.let { t ->
            Text("Status: ${t.status}")
            Text("Amount: ${t.amount} ${t.currency}")
        }
        TextButton(onClick = { onOpenVerify(transferId) }, modifier = Modifier.padding(top = sp.xs)) { Text("Open Verification Flow") }
        Text("Verifications", modifier = Modifier.padding(top = sp.sm))
        LazyColumn {
            items(state.verifications) { v ->
                Text("${v.step} • ${v.status}")
            }
        }
        Text("Disputes", modifier = Modifier.padding(top = sp.sm))
        LazyColumn {
            items(state.disputes) { d ->
                Text("${d.status} • ${d.reason}")
            }
        }
        Text("Audit Logs", modifier = Modifier.padding(top = sp.sm))
        LazyColumn {
            items(state.logs) { l ->
                Text("${l.action} • ${l.createdAt}")
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceabilityScreen(vm: TraceabilityViewModel, productId: String, onBack: () -> Unit) {
    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) vm.load(productId)
    }
    val state by vm.state.collectAsState()
    run {
        val sp = LocalSpacing.current
        Column(Modifier.padding(sp.md)) {
        TopAppBar(
            title = { Text(text = "Traceability") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Text(text = "For: ${state.rootId}", modifier = Modifier.padding(top = sp.xs))
        var resetKey by remember { mutableIntStateOf(0) }
        OutlinedButton(onClick = { resetKey++ }, modifier = Modifier.padding(top = sp.xs)) {
            Text("Reset Zoom & Center")
        }
        FamilyTreeView(
            rootId = state.rootId,
            layersUp = state.layersUp,
            layersDown = state.layersDown,
            edges = state.edges,
            resetKey = resetKey,
            modifier = Modifier.padding(top = sp.sm)
        )
        if (state.transferChain.isNotEmpty()) {
            Text(text = "Transfer Chain", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = sp.md))
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
    }
}
