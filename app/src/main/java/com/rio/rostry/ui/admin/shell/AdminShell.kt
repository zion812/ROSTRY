package com.rio.rostry.ui.admin.shell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rio.rostry.ui.admin.navigation.AdminNavHost
import com.rio.rostry.ui.admin.navigation.AdminRoutes
import com.rio.rostry.session.CurrentUserProvider

/**
 * Admin Shell - The main container for the Admin Portal.
 * 
 * Provides a dedicated admin experience with:
 * - Persistent sidebar navigation
 * - Admin-specific top bar with system status
 * - Quick actions toolbar
 * - Distinct visual theme for admin mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminShell(
    onExitAdmin: () -> Unit,
    currentUserProvider: CurrentUserProvider,
    pendingVerificationsCount: Int = 0,
    syncStatus: SyncStatus = SyncStatus.SYNCED,
    alertCount: Int = 0
) {
    val navController = rememberNavController()
    var isSidebarExpanded by remember { mutableStateOf(true) }
    var selectedSection by remember { mutableStateOf(AdminRoutes.SidebarSection.DASHBOARD) }
    
    // Track current route for highlighting
    val currentRoute by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    val currentDestination = currentRoute?.destination?.route
    
    // Update selected section based on current route
    LaunchedEffect(currentDestination) {
        selectedSection = when {
            currentDestination?.startsWith("admin_portal/dashboard") == true -> AdminRoutes.SidebarSection.DASHBOARD
            currentDestination?.startsWith("admin_portal/users") == true -> AdminRoutes.SidebarSection.USERS
            currentDestination?.startsWith("admin_portal/verification") == true -> AdminRoutes.SidebarSection.VERIFICATION
            currentDestination?.startsWith("admin_portal/commerce") == true -> AdminRoutes.SidebarSection.COMMERCE
            currentDestination?.startsWith("admin_portal/moderation") == true -> AdminRoutes.SidebarSection.MODERATION
            currentDestination?.startsWith("admin_portal/system") == true -> AdminRoutes.SidebarSection.SYSTEM
            currentDestination?.startsWith("admin_portal/monitoring") == true -> AdminRoutes.SidebarSection.MONITORING
            currentDestination?.startsWith("admin_portal/analytics") == true -> AdminRoutes.SidebarSection.ANALYTICS
            currentDestination?.startsWith("admin_portal/reports") == true -> AdminRoutes.SidebarSection.REPORTS
            currentDestination?.startsWith("admin_portal/audit") == true -> AdminRoutes.SidebarSection.AUDIT
            currentDestination?.startsWith("admin_portal/communication") == true -> AdminRoutes.SidebarSection.COMMUNICATION
            currentDestination?.startsWith("admin_portal/bulk") == true -> AdminRoutes.SidebarSection.BULK
            else -> AdminRoutes.SidebarSection.DASHBOARD
        }
    }
    
    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        AnimatedVisibility(
            visible = isSidebarExpanded,
            enter = slideInHorizontally(),
            exit = slideOutHorizontally()
        ) {
            AdminSidebar(
                selectedSection = selectedSection,
                onSectionClick = { section ->
                    selectedSection = section
                    navigateToSection(navController, section)
                },
                onCollapse = { isSidebarExpanded = false },
                onExitAdmin = onExitAdmin,
                pendingVerificationsCount = pendingVerificationsCount
            )
        }
        
        // Main content area
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Top Bar
            AdminTopBar(
                isSidebarExpanded = isSidebarExpanded,
                onToggleSidebar = { isSidebarExpanded = !isSidebarExpanded },
                syncStatus = syncStatus,
                alertCount = alertCount,
                onExitAdmin = onExitAdmin
            )
            
            // Navigation content
            AdminNavHost(
                navController = navController,
                onExitAdmin = onExitAdmin,
                currentUserProvider = currentUserProvider,
                pendingVerificationsCount = pendingVerificationsCount,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminTopBar(
    isSidebarExpanded: Boolean,
    onToggleSidebar: () -> Unit,
    syncStatus: SyncStatus,
    alertCount: Int,
    onExitAdmin: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Admin Portal",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Sync status indicator
                SyncStatusIndicator(syncStatus)
            }
        },
        navigationIcon = {
            IconButton(onClick = onToggleSidebar) {
                Icon(
                    imageVector = if (isSidebarExpanded) Icons.Default.MenuOpen else Icons.Default.Menu,
                    contentDescription = "Toggle sidebar"
                )
            }
        },
        actions = {
            // Alerts badge
            if (alertCount > 0) {
                BadgedBox(
                    badge = {
                        Badge { Text(alertCount.toString()) }
                    }
                ) {
                    IconButton(onClick = { /* Navigate to alerts */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                    }
                }
            } else {
                IconButton(onClick = { /* Navigate to alerts */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                }
            }
            
            // Quick actions
            IconButton(onClick = { /* Quick search */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            
            // Exit admin mode
            IconButton(onClick = onExitAdmin) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit Admin")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AdminColors.TopBarBackground,
            titleContentColor = AdminColors.OnTopBar
        )
    )
}

@Composable
private fun SyncStatusIndicator(status: SyncStatus) {
    val (color, icon, label) = when (status) {
        SyncStatus.SYNCED -> Triple(Color(0xFF4CAF50), Icons.Default.CloudDone, "Synced")
        SyncStatus.SYNCING -> Triple(Color(0xFFFFC107), Icons.Default.CloudSync, "Syncing")
        SyncStatus.ERROR -> Triple(Color(0xFFF44336), Icons.Default.CloudOff, "Sync Error")
        SyncStatus.OFFLINE -> Triple(Color(0xFF9E9E9E), Icons.Default.CloudOff, "Offline")
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

enum class SyncStatus {
    SYNCED, SYNCING, ERROR, OFFLINE
}

private fun navigateToSection(navController: NavHostController, section: AdminRoutes.SidebarSection) {
    val route = when (section) {
        AdminRoutes.SidebarSection.DASHBOARD -> AdminRoutes.DASHBOARD
        AdminRoutes.SidebarSection.USERS -> AdminRoutes.Users.LIST
        AdminRoutes.SidebarSection.VERIFICATION -> AdminRoutes.Verification.PENDING
        AdminRoutes.SidebarSection.COMMERCE -> AdminRoutes.Commerce.PRODUCTS
        AdminRoutes.SidebarSection.MODERATION -> AdminRoutes.Moderation.QUEUE
        AdminRoutes.SidebarSection.SYSTEM -> AdminRoutes.System.CONFIG
        AdminRoutes.SidebarSection.MONITORING -> AdminRoutes.Monitoring.BIOSECURITY
        AdminRoutes.SidebarSection.ANALYTICS -> AdminRoutes.Analytics.OVERVIEW
        AdminRoutes.SidebarSection.REPORTS -> AdminRoutes.Reports.GENERATOR
        AdminRoutes.SidebarSection.AUDIT -> AdminRoutes.Audit.LOGS
        AdminRoutes.SidebarSection.COMMUNICATION -> AdminRoutes.Communication.BROADCAST
        AdminRoutes.SidebarSection.BULK -> AdminRoutes.Bulk.OPERATIONS
    }
    
    navController.navigate(route) {
        popUpTo(AdminRoutes.DASHBOARD) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Admin-specific color scheme for visual distinction from user mode.
 */
object AdminColors {
    val Primary = Color(0xFF1A237E) // Deep Indigo
    val PrimaryVariant = Color(0xFF0D47A1)
    val Secondary = Color(0xFF00BCD4) // Cyan accent
    val Background = Color(0xFF121212)
    val Surface = Color(0xFF1E1E1E)
    val SidebarBackground = Color(0xFF1A1A2E)
    val TopBarBackground = Color(0xFF0F0F1A)
    val OnPrimary = Color.White
    val OnBackground = Color(0xFFE0E0E0)
    val OnSurface = Color(0xFFE0E0E0)
    val OnTopBar = Color.White
    val SelectedItem = Color(0xFF3949AB)
    val HoverItem = Color(0xFF2A2A4A)
}
