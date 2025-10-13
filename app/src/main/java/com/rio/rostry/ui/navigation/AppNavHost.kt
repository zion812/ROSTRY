package com.rio.rostry.ui.navigation

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Notifications
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
import com.rio.rostry.ui.showcase.ComponentGalleryScreen
import com.rio.rostry.BuildConfig
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.rio.rostry.utils.export.PdfExporter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import kotlin.math.roundToInt
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun AppNavHost() {
    val sessionVm: SessionViewModel = hiltViewModel()
    val state by sessionVm.uiState.collectAsState()
    val navConfig = state.navConfig

    when {
        state.isAuthenticated && navConfig != null -> RoleNavScaffold(navConfig, sessionVm, state)
        state.isAuthenticated -> {
            LaunchedEffect("await-config") { sessionVm.refresh() }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
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
    val authVm: AuthViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showOtp by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authVm.navigation.collectLatest { event ->
            when (event) {
                is AuthViewModel.NavAction.ToOtp -> navController.navigate("auth/otp/${event.verificationId}")
                is AuthViewModel.NavAction.ToHome -> onAuthenticated()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Field test demo login", modifier = Modifier.fillMaxWidth())

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        val errorMessage = state.error
        if (!errorMessage.isNullOrBlank()) {
            Text("Error: $errorMessage")
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        }

        if (state.demoProfiles.isNotEmpty()) {
            Divider()
            QuickSelectDemoList(
                profiles = state.demoProfiles,
                onSelect = onQuickSelect,
                currentId = state.currentDemoProfile?.id
            )
        }

        Divider()
        TextButton(onClick = { showOtp = !showOtp }) {
            Text(if (showOtp) "Hide phone verification" else "Use phone verification")
        }

        if (showOtp) {
            NavHost(
                navController = navController,
                startDestination = Routes.AUTH_PHONE
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
            }
            Spacer(modifier = Modifier.height(8.dp))
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

    // Wrap Scaffold in a Box to overlay a draggable FAB above all content
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ROSTRY") },
                    actions = {
                        AccountMenuAction(
                            navController = navController,
                            onSignOut = { sessionVm.signOut() }
                        )
                        NotificationsAction(navController = navController)
                    }
                )
            },
            bottomBar = {
                RoleBottomBar(
                    navController = navController,
                    navConfig = navConfig,
                    currentRoute = currentRoute
                )
            }
        ) { padding ->
            RoleNavGraph(navController = navController, navConfig = navConfig, modifier = Modifier.padding(padding))
        }

        if (state.authMode == SessionManager.AuthMode.DEMO) {
            DraggableDemoFab(onClick = { showSwitcher = true })
        }
    }

    if (showSwitcher && state.authMode == SessionManager.AuthMode.DEMO) {
        DemoProfilePickerDialog(
            profiles = state.demoProfiles,
            currentId = state.currentDemoProfile?.id,
            onSelect = {
                sessionVm.activateDemoProfile(it)
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
private fun DraggableDemoFab(onClick: () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val maxW = with(density) { maxWidth.toPx() }
        val maxH = with(density) { maxHeight.toPx() }
        val fabSizePx = with(density) { 56.dp.toPx() }
        val paddingPx = with(density) { 16.dp.toPx() }

        var offsetX by rememberSaveable { mutableStateOf(maxW - fabSizePx - paddingPx) }
        var offsetY by rememberSaveable { mutableStateOf(maxH - fabSizePx - paddingPx) }

        fun clampX(x: Float) = x.coerceIn(0f, maxW - fabSizePx)
        fun clampY(y: Float) = y.coerceIn(0f, maxH - fabSizePx)

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(onDrag = { change, drag ->
                        change.consume()
                        offsetX = clampX(offsetX + drag.x)
                        offsetY = clampY(offsetY + drag.y)
                    })
                }
        ) {
            Text("Demo")
        }
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
            val viewModel: com.rio.rostry.ui.farmer.FarmerHomeViewModel = hiltViewModel()
            FarmerHomeScreen(
                viewModel = viewModel,
                onOpenDailyLog = { navController.navigate(Routes.MONITORING_DAILY_LOG) },
                onOpenTasks = { navController.navigate(Routes.MONITORING_TASKS) },
                onOpenVaccination = { navController.navigate(Routes.MONITORING_VACCINATION) },
                onOpenGrowth = { navController.navigate(Routes.MONITORING_GROWTH) },
                onOpenQuarantine = { navController.navigate(Routes.MONITORING_QUARANTINE) },
                onOpenHatching = { navController.navigate(Routes.MONITORING_HATCHING) },
                onOpenMortality = { navController.navigate(Routes.MONITORING_MORTALITY) },
                onOpenBreeding = { navController.navigate(Routes.MONITORING_BREEDING) },
                onOpenListing = { navController.navigate(Routes.FarmerNav.CREATE) },
                onOpenAlerts = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavigateToAddBird = { navController.navigate(Routes.Onboarding.FARM_BIRD) },
                onNavigateToAddBatch = { navController.navigate(Routes.Onboarding.FARM_BATCH) },
                onNavigateRoute = { route -> navController.navigate(route) }
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
                nonTraceableSelected = state.traceFilter == com.rio.rostry.ui.farmer.FarmerMarketViewModel.TraceFilter.NonTraceable
            )
        }
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
        // Shared breeding management route for enthusiasts
        composable(Routes.MONITORING_BREEDING) {
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
        composable(Routes.MONITORING_DAILY_LOG) {
            com.rio.rostry.ui.monitoring.DailyLogScreen(
                onNavigateBack = { navController.popBackStack() },
                productId = null
            )
        }
        composable(
            route = Routes.MONITORING_DAILY_LOG_PRODUCT,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getString("productId")
            com.rio.rostry.ui.monitoring.DailyLogScreen(
                onNavigateBack = { navController.popBackStack() },
                productId = pid
            )
        }

        // Tasks
        composable(Routes.MONITORING_TASKS) {
            com.rio.rostry.ui.monitoring.TasksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProduct = { productId -> navController.navigate("product/$productId") }
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
                onOpenBreeding = { navController.navigate(Routes.MONITORING_BREEDING) },
                onNavigateToAddBird = { navController.navigate(Routes.Onboarding.FARM_BIRD + "?role=enthusiast") },
                onNavigateToAddBatch = { navController.navigate(Routes.Onboarding.FARM_BATCH + "?role=enthusiast") }
            )
        }

        composable(Routes.EnthusiastNav.EXPLORE) {
            EnthusiastExploreScreen(
                onOpenProduct = { productId -> navController.navigate("product/$productId") },
                onOpenEvent = { eventId -> navController.navigate(Routes.EVENT_DETAILS.replace("{eventId}", eventId)) },
                onShare = { _ -> /* share sheet */ Unit }
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
            EnthusiastDashboardTabs(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) },
                onOpenTraceability = { id -> navController.navigate("traceability/$id") },
                navController = navController
            )
        }

        composable(Routes.EnthusiastNav.TRANSFERS) {
            EnthusiastTransfersScreen(
                onOpenTransfer = { id -> navController.navigate("transfer/$id") },
                onVerifyTransfer = { id -> navController.navigate("transfer/$id/verify") },
                onCreateTransfer = { navController.navigate(Routes.TRANSFER_CREATE) },
                onOpenTraceability = { id -> navController.navigate("traceability/$id") }
            )
        }

        // Enthusiast Egg Collection route (optional pairId argument via query)
        composable(Routes.EnthusiastNav.EGG_COLLECTION) {
            com.rio.rostry.ui.enthusiast.breeding.EggCollectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onVerifyFarmerLocation = { navController.navigate(Routes.VERIFY_FARMER_LOCATION) },
                onVerifyEnthusiastKyc = { navController.navigate(Routes.VERIFY_ENTHUSIAST_KYC) }
            )
        }
        composable(Routes.VERIFY_FARMER_LOCATION) {
            FarmerLocationVerificationScreen(onDone = { navController.popBackStack() })
        }
        composable(Routes.VERIFY_ENTHUSIAST_KYC) {
            EnthusiastKycScreen(onDone = { navController.popBackStack() })
        }

        composable(Routes.ONBOARD_GENERAL) { PlaceholderScreen(title = "Onboarding - General") }
        composable(Routes.ONBOARD_FARMER) { PlaceholderScreen(title = "Onboarding - Farmer") }
        composable(Routes.ONBOARD_ENTHUSIAST) { PlaceholderScreen(title = "Onboarding - Enthusiast") }
        composable(
            route = Routes.Onboarding.FARM_BIRD + "?role={role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            com.rio.rostry.ui.onboarding.OnboardFarmBirdScreen(
                onDone = { _ ->
                    val popTarget = if (role == "enthusiast") Routes.HOME_ENTHUSIAST else Routes.HOME_FARMER
                    navController.navigate(Routes.MONITORING_DAILY_LOG) { popUpTo(popTarget) }
                },
                onBack = { navController.popBackStack() },
                role = role
            )
        }
        composable(
            route = Routes.Onboarding.FARM_BATCH + "?role={role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            com.rio.rostry.ui.onboarding.OnboardFarmBatchScreen(
                onDone = { _ ->
                    val popTarget = if (role == "enthusiast") Routes.HOME_ENTHUSIAST else Routes.HOME_FARMER
                    navController.navigate(Routes.MONITORING_DAILY_LOG) { popUpTo(popTarget) }
                },
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
            ProductDetailsScreen(
                productId = productId,
                onOpenTraceability = { navController.navigate("traceability/$productId") }
            )
        }

        composable(
            route = Routes.TRACEABILITY,
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "rostry://traceability/{productId}" },
                // Alias for family tree deep link used by notifications
                navDeepLink { uriPattern = "rostry://family-tree/{productId}" }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val vm: TraceabilityViewModel = hiltViewModel()
            // If we just returned from scanner with a result, navigate to that product's tree
            val scanned = navController.currentBackStackEntry?.savedStateHandle?.get<String>("scannedProductId")
            if (!scanned.isNullOrBlank() && scanned != productId) {
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("scannedProductId")
                LaunchedEffect(scanned) { navController.navigate("traceability/$scanned") }
            }
            TraceabilityScreen(
                vm = vm,
                productId = productId,
                onBack = { navController.popBackStack() },
                onScanQr = { navController.navigate(Routes.SCAN_QR + "?context=family_tree") }
            )
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
                onOpenFullTree = { pid -> navController.navigate("traceability/$pid") },
                onBack = { navController.popBackStack() }
            )
        }

        // Family Tree explicit route (aliases to TraceabilityScreen full view)
        composable(
            route = Routes.PRODUCT_FAMILY_TREE,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val vm: TraceabilityViewModel = hiltViewModel()
            TraceabilityScreen(
                vm = vm,
                productId = productId,
                onBack = { navController.popBackStack() },
                onScanQr = { navController.navigate(Routes.SCAN_QR + "?context=family_tree") }
            )
        }

        // Scanner route (placeholder implementation) with optional context
        composable(
            route = Routes.SCAN_QR + "?context={context}&transferId={transferId}",
            arguments = listOf(
                navArgument("context") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("transferId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val ctx = backStackEntry.arguments?.getString("context")
            val tid = backStackEntry.arguments?.getString("transferId")
            QrScannerScreen(onResult = { productId ->
                when (ctx) {
                    "family_tree" -> {
                        navController.previousBackStackEntry?.savedStateHandle?.set("scannedProductId", productId)
                        navController.popBackStack()
                    }
                    "transfer_verify" -> {
                        // For transfer flows, open traceability for quick identity check
                        navController.navigate("traceability/$productId")
                    }
                    else -> {
                        // Default: open product details
                        navController.navigate("product/$productId")
                    }
                }
            })
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
            arguments = listOf(navArgument("transferId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://transfer/{transferId}/verify" })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            TransferVerificationScreen(
                transferId = transferId,
                onScanProduct = { navController.navigate(Routes.SCAN_QR + "?context=transfer_verify&transferId=$transferId") }
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

        // Analytics dashboard (Enthusiast)
        composable(Routes.ANALYTICS_DASHBOARD) {
            EnthusiastDashboardScreen(
                onOpenReports = { navController.navigate(Routes.REPORTS) },
                onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
            )
        }

        composable(
            route = Routes.MESSAGES_THREAD,
            arguments = listOf(navArgument("threadId") { type = NavType.StringType })
        ) { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
            com.rio.rostry.ui.messaging.ThreadScreen(threadId = threadId, onBack = { navController.popBackStack() })
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

        composable(Routes.GROUPS) { PlaceholderScreen(title = "Groups") }
        composable(Routes.EVENTS) { com.rio.rostry.ui.events.EventsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.EXPERT_BOOKING) { com.rio.rostry.ui.expert.ExpertBookingScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.MODERATION) { com.rio.rostry.ui.moderation.ModerationScreen() }
        composable(Routes.LEADERBOARD) { com.rio.rostry.ui.social.LeaderboardScreen() }
        composable(Routes.LIVE_BROADCAST) { LiveBroadcastScreen(onBack = { navController.popBackStack() }) }

        // Community Hub destinations
        composable(Routes.COMMUNITY_HUB) {
            val sessionVm: SessionViewModel = hiltViewModel()
            val sessionState by sessionVm.uiState.collectAsState()
            val userType = sessionState.role ?: com.rio.rostry.domain.model.UserType.GENERAL
            com.rio.rostry.ui.community.CommunityHubScreen(
                userType = userType,
                onNavigateToThread = { threadId -> navController.navigate("messages/$threadId") },
                onNavigateToGroup = { groupId -> navController.navigate(Routes.CommunityHub.createGroupRoute(groupId)) },
                onNavigateToEvent = { eventId -> navController.navigate(Routes.CommunityHub.createEventRoute(eventId)) },
                onNavigateToExpert = { expertId -> navController.navigate(Routes.CommunityHub.createExpertRoute(expertId)) },
                onNavigateToPost = { postId -> navController.navigate("product/$postId") }
            )
        }

        composable(
            route = Routes.GROUP_DETAILS,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
            PlaceholderScreen(title = "Group: $groupId")
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
                    navController.navigate("${Routes.FarmerNav.CREATE}?prefillProductId=$productId")
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
                    navController.navigate("${Routes.FarmerNav.CREATE}?prefillProductId=$productId")
                }
            )
        }
        composable(
            route = Routes.MONITORING_HATCHING,
            deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/hatching" })
        ) {
            com.rio.rostry.ui.monitoring.HatchingProcessScreen()
        }
        composable(Routes.MONITORING_DASHBOARD) {
            val vm: com.rio.rostry.ui.monitoring.vm.FarmMonitoringViewModel = hiltViewModel()
            val summary by vm.summary.collectAsState()
            com.rio.rostry.ui.monitoring.FarmMonitoringScreen(
                onOpenGrowth = { navController.navigate(Routes.MONITORING_GROWTH) },
                onOpenVaccination = { navController.navigate(Routes.MONITORING_VACCINATION) },
                onOpenBreeding = { navController.navigate(Routes.MONITORING_BREEDING) },
                onOpenQuarantine = { navController.navigate(Routes.MONITORING_QUARANTINE) },
                onOpenMortality = { navController.navigate(Routes.MONITORING_MORTALITY) },
                onOpenHatching = { navController.navigate(Routes.MONITORING_HATCHING) },
                onOpenPerformance = { navController.navigate(Routes.MONITORING_PERFORMANCE) },
                summary = summary
            )
        }
        composable(Routes.MONITORING_PERFORMANCE) {
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

        // Dev/Showcase: Component Gallery (available only in debug builds)
        if (BuildConfig.DEBUG) {
            composable(Routes.COMPONENT_GALLERY) {
                ComponentGalleryScreen()
            }
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
                val labelInitial = destination.label.firstOrNull()?.uppercaseChar()?.toString() ?: "•"
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
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Profile") }, onClick = {
                expanded = false
                navController.navigate(Routes.PROFILE)
            })
            DropdownMenuItem(text = { Text("Settings") }, onClick = {
                expanded = false
                // TODO: Replace with Settings route when available
                navController.navigate(Routes.PROFILE)
            })
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onScanQr, modifier = Modifier.padding(top = 8.dp)) {
                Text("Scan QR")
            }
            OutlinedButton(onClick = {
                // Generate via ViewModel method
                val uri = kotlinx.coroutines.runBlocking { vm.generateLineageProof(state.rootId) }
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(share, "Share Lineage Proof"))
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Export Proof")
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
            modifier = Modifier.padding(top = 12.dp)
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
}
