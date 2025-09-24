package com.rio.rostry.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.rio.rostry.ui.screens.HomeFarmerScreen
import com.rio.rostry.ui.screens.HomeGeneralScreen
import com.rio.rostry.ui.screens.PlaceholderScreen
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.ui.traceability.FamilyTreeView
import com.rio.rostry.ui.traceability.TraceabilityViewModel
import com.rio.rostry.ui.transfer.TransferDetailsViewModel
import com.rio.rostry.ui.verification.EnthusiastKycScreen
import com.rio.rostry.ui.verification.FarmerLocationVerificationScreen
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
            .padding(24.dp),
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

    LaunchedEffect(navConfig.role) {
        navController.navigate(navConfig.startDestination) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            RoleBottomBar(
                navController = navController,
                navConfig = navConfig,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            if (state.authMode == SessionManager.AuthMode.DEMO) {
                FloatingActionButton(onClick = { showSwitcher = true }) {
                    Text("Demo")
                }
            }
        }
    ) { padding ->
        RoleNavGraph(navController = navController, navConfig = navConfig, modifier = Modifier.padding(padding))
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
            HomeGeneralScreen(onProfile = { navController.navigate(Routes.PROFILE) })
        }
        composable(Routes.HOME_FARMER) {
            HomeFarmerScreen(onProfile = { navController.navigate(Routes.PROFILE) })
        }
        composable(Routes.HOME_ENTHUSIAST) {
            HomeEnthusiastScreen(onProfile = { navController.navigate(Routes.PROFILE) })
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

        composable(
            route = Routes.TRANSFER_DETAILS,
            arguments = listOf(navArgument("transferId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transferId = backStackEntry.arguments?.getString("transferId") ?: ""
            TransferDetailsScreen(transferId = transferId)
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
            com.rio.rostry.ui.messaging.ThreadScreen(threadId = threadId, onBack = { navController.popBackStack() })
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
    }
}
@Composable
private fun RoleBottomBar(
    navController: NavHostController,
    navConfig: RoleNavigationConfig,
    currentRoute: String?
) {
    NavigationBar {
        navConfig.bottomNav.forEach { destination ->
            val selected = currentRoute?.startsWith(destination.route.substringBefore("/{")) == true
            val labelInitial = destination.label.firstOrNull()?.uppercaseChar()?.toString() ?: "•"
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
                icon = { Text(labelInitial) },
                label = { Text(destination.label) }
            )
        }
    }
}

@Composable
private fun TransferDetailsScreen(transferId: String) {
    val vm: com.rio.rostry.ui.transfer.TransferDetailsViewModel = hiltViewModel()
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state by vm.state.collectAsState()
    Column(Modifier.padding(16.dp)) {
        Text(text = "Transfer: $transferId", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        state.transfer?.let { t ->
            Text("Status: ${t.status}")
            Text("Amount: ${t.amount} ${t.currency}")
        }
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
private fun TraceabilityScreen(vm: TraceabilityViewModel, productId: String, onBack: () -> Unit) {
    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) vm.load(productId)
    }
    val state by vm.state.collectAsState()
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
