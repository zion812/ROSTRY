package com.rio.rostry.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rio.rostry.ui.notifications.NotificationsViewModel

@Composable
internal fun ErrorScreen(message: String, onBack: () -> Unit) {
    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = message)
        Button(onClick = onBack) { Text("Back") }
    }
}

@Composable
internal fun RoleBottomBar(
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
            val baseCurrent = currentRoute
                ?.substringBefore("/{")
                ?.substringBefore("?")
            val baseDest = destination.route
                .substringBefore("/{")
                .substringBefore("?")
            val selected = baseCurrent == baseDest
            val labelInitial = destination.label.firstOrNull()?.uppercaseChar()?.toString() ?: "*"
            val badgeCount = when (destination.route) {
                Routes.FarmerNav.MARKET -> notifState.pendingOrders
                Routes.FarmerNav.COMMUNITY -> notifState.unreadMessages
                else -> 0
            }
            NavigationBarItem(
                selected = selected,
                onClick = {
                    val isCreate = destination.route.endsWith("/create")
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

private fun iconForRoute(route: String): ImageVector? {
    val base = route.substringBefore("/{")
    return when (base) {
        Routes.FarmerNav.HOME, Routes.EnthusiastNav.HOME -> Icons.Filled.Home
        Routes.FarmerNav.FARM_ASSETS -> Icons.Filled.Pets
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
        Routes.Admin.DASHBOARD -> Icons.Filled.Dashboard
        Routes.Admin.VERIFICATION -> Icons.Filled.Person
        Routes.Admin.USER_MANAGEMENT -> Icons.Filled.Groups
        Routes.Admin.UPGRADE_REQUESTS -> Icons.Filled.SwapHoriz
        else -> null
    }
}

@Composable
internal fun NotificationsAction(navController: NavHostController) {
    val vm: NotificationsViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    LaunchedEffect(Unit) { vm.refresh() }
    BadgedBox(
        badge = { if (state.total > 0) Badge { Text(state.total.toString()) } }
    ) {
        IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications"
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AccountMenuAction(
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
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = {
                        expanded = false
                        navController.navigate(Routes.PROFILE)
                    }
                )
                DropdownMenuItem(
                    text = {
                        if (pendingCount > 0) Text("Settings ($pendingCount)") else Text("Settings")
                    },
                    onClick = {
                        expanded = false
                        navController.navigate(Routes.SETTINGS)
                    }
                )
            }
            DropdownMenuItem(
                text = { Text(if (isGuestMode) "Exit Guest Mode" else "Sign out") },
                onClick = {
                    expanded = false
                    onSignOut()
                }
            )
        }
    }
}

@Composable
internal fun GuestModeBanner(onSignInClick: () -> Unit) {
    Surface(
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

@Composable
internal fun GuestSignInDialog(
    onSignIn: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign in to continue") },
        text = {
            Text("This feature requires an account. Sign in to unlock all features and save your progress.")
        },
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

internal fun isWriteAction(route: String): Boolean {
    val writeRoutes = setOf(
        Routes.FarmerNav.CREATE,
        Routes.EnthusiastNav.CREATE,
        Routes.TRANSFER_CREATE,
        Routes.PRODUCT_CREATE
    )
    val baseRoute = route.substringBefore("?")
    return writeRoutes.any { baseRoute.contains(it) } || route.contains("/create")
}
