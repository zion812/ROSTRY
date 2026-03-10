package com.rio.rostry.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

internal fun NavGraphBuilder.sharedFeatureNavGraph(
    navController: NavHostController
) {
    composable(Routes.ANALYTICS_GENERAL) {
        com.rio.rostry.feature.analytics.GeneralDashboardScreen(
            onOpenReports = { navController.navigate(Routes.REPORTS) },
            onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
        )
    }
    composable(Routes.ANALYTICS_FARMER) {
        com.rio.rostry.feature.analytics.FarmerDashboardScreen(
            onOpenReports = { navController.navigate(Routes.REPORTS) },
            onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
        )
    }
    composable(Routes.ANALYTICS_ENTHUSIAST) {
        com.rio.rostry.feature.analytics.EnthusiastDashboardScreen(
            onOpenReports = { navController.navigate(Routes.REPORTS) },
            onOpenFeed = { navController.navigate(Routes.SOCIAL_FEED) }
        )
    }
    composable(Routes.REPORTS) {
        com.rio.rostry.feature.analytics.ReportsScreen()
    }

    composable(
        route = Routes.MONITORING_VACCINATION,
        deepLinks = listOf(navDeepLink { uriPattern = "rostry://monitoring/vaccination" })
    ) {
        com.rio.rostry.ui.monitoring.VaccinationScheduleScreen(
            onListProduct = { productId ->
                navController.navigate(Routes.Builders.farmerCreateWithPrefill(productId))
            },
            isPremium = true
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
            },
            isPremium = true
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

    composable(Routes.Monitoring.BREEDING_UNIT) {
        com.rio.rostry.ui.farmer.breeding.BreedingUnitScreen(
            onBack = { navController.popBackStack() }
        )
    }

    composable(Routes.Monitoring.BREEDING_PERFORMANCE) {
        com.rio.rostry.ui.monitoring.BreedingPerformanceScreen(
            onBack = { navController.popBackStack() }
        )
    }

    composable(
        route = Routes.MONITORING_DAILY_LOG_PRODUCT,
        arguments = listOf(navArgument("productId") { type = NavType.StringType })
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: ""
        com.rio.rostry.ui.monitoring.DailyLogScreen(
            productId = productId,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAddBird = { navController.navigate(Routes.Builders.onboardingFarmBird()) },
            onNavigateToAddBatch = { navController.navigate(Routes.Builders.onboardingFarmBatch()) }
        )
    }

    composable(
        route = Routes.Monitoring.FARM_ACTIVITY_DETAIL,
        arguments = listOf(navArgument("activityId") { type = NavType.StringType })
    ) { backStackEntry ->
        val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
        com.rio.rostry.ui.farmer.log.FarmActivityDetailScreen(
            activityId = activityId,
            onBack = { navController.popBackStack() }
        )
    }

    composable(Routes.MONITORING_FARM_LOG) {
        com.rio.rostry.ui.farmer.FarmLogScreen(
            onBack = { navController.popBackStack() },
            onNavigateRoute = { route -> navController.navigate(route) },
            onActivityClick = { activity ->
                navController.navigate(Routes.Builders.farmActivityDetail(activity.activityId))
            }
        )
    }

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

        LaunchedEffect(productId) {
            if (productId != null) {
                cartVm.showAddToFarmDialogForProduct(productId)
            }
        }

        com.rio.rostry.ui.general.cart.GeneralCartRoute(
            onCheckoutComplete = { navController.popBackStack() },
            viewModel = cartVm
        )
    }

    composable(Routes.ACHIEVEMENTS) {
        val viewModel: com.rio.rostry.ui.gamification.AchievementsViewModel = hiltViewModel()
        val achievements by viewModel.achievementsWithProgress.collectAsState(initial = emptyList())
        val totalPoints by viewModel.totalPoints.collectAsState(initial = 0)
        com.rio.rostry.ui.gamification.AchievementsScreen(
            achievements = achievements,
            totalPoints = totalPoints,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(Routes.LEADERBOARD) {
        val viewModel: com.rio.rostry.ui.social.LeaderboardViewModel = hiltViewModel()
        val top by viewModel.top.collectAsState()
        com.rio.rostry.ui.social.LeaderboardScreen(
            top = top,
            onPeriodSelected = viewModel::setPeriod
        )
    }
    composable(Routes.INSIGHTS) {
        val viewModel: com.rio.rostry.ui.insights.InsightsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()
        com.rio.rostry.ui.insights.InsightsScreen(
            state = state,
            onRefresh = viewModel::refresh,
            onCategorySelected = viewModel::filterByCategory,
            onNavigateToRoute = { route -> navController.navigate(route) }
        )
    }
    composable(Routes.FEEDBACK) {
        val viewModel: com.rio.rostry.ui.feedback.FeedbackViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        com.rio.rostry.ui.feedback.FeedbackScreen(
            uiState = uiState,
            onBack = { navController.popBackStack() },
            onContentChange = viewModel::updateContent,
            onTypeChange = viewModel::updateType,
            onSubmit = viewModel::submit,
            onErrorConsumed = viewModel::clearError
        )
    }
    composable(Routes.NOTIFICATIONS) {
        val viewModel: com.rio.rostry.ui.notifications.NotificationsViewModel = hiltViewModel()
        val uiState by viewModel.ui.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.refresh()
        }

        com.rio.rostry.ui.notifications.NotificationsScreen(
            state = uiState,
            onOpenMessages = { navController.navigate(Routes.MESSAGES_OUTBOX) },
            onOpenOrders = { navController.navigate(Routes.TRANSFER_LIST) },
            onBack = { navController.popBackStack() },
            onOpenRoute = { route -> navController.navigate(route) }
        )
    }
    composable(Routes.HELP) {
        com.rio.rostry.ui.support.HelpScreen()
    }
    composable(Routes.EVENTS) {
        val viewModel: com.rio.rostry.ui.events.EventsViewModel = hiltViewModel()
        val events by viewModel.upcoming.collectAsState()
        com.rio.rostry.ui.events.EventsScreen(
            events = events,
            onRsvpGoing = { eventId -> viewModel.rsvpSelf(eventId, "GOING") },
            onRsvpInterested = { eventId -> viewModel.rsvpSelf(eventId, "INTERESTED") },
            onCreateEvent = { title, description, location ->
                viewModel.createEvent(
                    title = title,
                    description = description,
                    location = location,
                    startTime = System.currentTimeMillis() + 86_400_000L,
                    endTime = null
                )
            },
            onBack = { navController.popBackStack() }
        )
    }
    composable(Routes.EXPERT_BOOKING) {
        val viewModel: com.rio.rostry.ui.expert.ExpertViewModel = hiltViewModel()
        val bookings by viewModel.bookings.collectAsState()
        com.rio.rostry.ui.expert.ExpertBookingScreen(
            bookings = bookings,
            onConfirm = { bookingId -> viewModel.updateStatus(bookingId, "CONFIRMED") },
            onComplete = { bookingId -> viewModel.updateStatus(bookingId, "COMPLETED") },
            onRequest = { topic, details ->
                viewModel.createRequest(
                    expertId = "expert-1",
                    userId = "me",
                    topic = topic,
                    details = details
                )
            },
            onBack = { navController.popBackStack() }
        )
    }
    composable(Routes.ONBOARD_GENERAL) {
        com.rio.rostry.ui.onboarding.OnboardingScreen(
            role = com.rio.rostry.domain.model.UserType.GENERAL,
            onComplete = { navController.popBackStack() }
        )
    }
    composable(Routes.ONBOARD_FARMER) {
        com.rio.rostry.ui.onboarding.OnboardingScreen(
            role = com.rio.rostry.domain.model.UserType.FARMER,
            onComplete = { navController.popBackStack() }
        )
    }
    composable(Routes.ONBOARD_ENTHUSIAST) {
        com.rio.rostry.ui.onboarding.OnboardingScreen(
            role = com.rio.rostry.domain.model.UserType.ENTHUSIAST,
            onComplete = { navController.popBackStack() }
        )
    }
}
