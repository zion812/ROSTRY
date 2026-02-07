package com.rio.rostry.ui.upgrade

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.ui.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun RoleUpgradePostOnboardingScreen(
    newRole: UserType,
    onComplete: () -> Unit,
    onNavigateToFeature: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { getFeaturesForRole(newRole).size })
    var showCelebration by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Section
        AnimatedVisibility(
            visible = showCelebration,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val scale by animateFloatAsState(
                    targetValue = 1.2f,
                    animationSpec = tween(1000),
                    label = "celebration_scale"
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Celebration",
                    modifier = Modifier
                        .size(64.dp)
                        .scale(scale),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome to ${newRole.displayName}!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Congratulations! You now have access to advanced features like ${newRole.primaryFeatures.joinToString(", ")}.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // New Features Showcase
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val feature = getFeaturesForRole(newRole)[page]
            FeatureCard(
                feature = feature,
                onTryItNow = { onNavigateToFeature(feature.route) }
            )
        }

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            getQuickActionsForRole(newRole).forEach { action ->
                OutlinedButton(
                    onClick = { onNavigateToFeature(action.route) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(action.title)
                }
            }
        }

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onComplete,
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip")
            }
            if (pagerState.currentPage < pagerState.pageCount - 1) {
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Started")
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: Feature,
    onTryItNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = feature.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = onTryItNow) {
                Text("Try it now")
            }
        }
    }
}

private data class Feature(
    val title: String,
    val description: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private data class QuickAction(
    val title: String,
    val route: String
)

private fun getFeaturesForRole(role: UserType): List<Feature> {
    return when (role) {
        UserType.FARMER -> listOf(
            Feature(
                title = "List Products",
                description = "Start selling your farm products on the marketplace.",
                route = Routes.FarmerNav.CREATE,
                icon = androidx.compose.material.icons.Icons.Filled.Store
            ),
            Feature(
                title = "Track Farm Health",
                description = "Monitor and manage your farm's health metrics.",
                route = Routes.MONITORING_DASHBOARD,
                icon = androidx.compose.material.icons.Icons.Filled.MonitorHeart
            ),
            Feature(
                title = "Manage Sales",
                description = "View analytics and manage your sales performance.",
                route = Routes.ANALYTICS_FARMER,
                icon = androidx.compose.material.icons.Icons.Filled.TrendingUp
            )
        )
        UserType.ENTHUSIAST -> listOf(
            Feature(
                title = "Breeding Records",
                description = "Keep detailed records of your breeding activities.",
                route = Routes.MONITORING_BREEDING,
                icon = androidx.compose.material.icons.Icons.Filled.Favorite
            ),
            Feature(
                title = "Transfer System",
                description = "Initiate and manage bird transfers securely.",
                route = Routes.TRANSFER_LIST,
                icon = androidx.compose.material.icons.Icons.Filled.SwapHoriz
            ),
            Feature(
                title = "Advanced Analytics",
                description = "Access in-depth analytics for your birds and activities.",
                route = Routes.ANALYTICS_ENTHUSIAST,
                icon = androidx.compose.material.icons.Icons.Filled.Insights
            )
        )
        UserType.GENERAL, UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> emptyList() // Not applicable
    }
}

private fun getQuickActionsForRole(role: UserType): List<QuickAction> {
    return when (role) {
        UserType.FARMER -> listOf(
            QuickAction("Add Your First Product", Routes.FarmerNav.CREATE),
            QuickAction("Verify Your Location", Routes.VERIFY_FARMER_LOCATION),
            QuickAction("Explore Marketplace", Routes.PRODUCT_MARKET)
        )
        UserType.ENTHUSIAST -> listOf(
            QuickAction("Complete KYC", Routes.VERIFY_ENTHUSIAST_KYC),
            QuickAction("Add Your First Bird", Routes.Builders.onboardingFarmBird("enthusiast")),
            QuickAction("View Analytics", Routes.ANALYTICS_ENTHUSIAST)
        )
        UserType.GENERAL, UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> emptyList() // Not applicable
    }
}