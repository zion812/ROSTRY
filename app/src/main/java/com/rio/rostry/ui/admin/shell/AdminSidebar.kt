package com.rio.rostry.ui.admin.shell

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.admin.navigation.AdminRoutes

/**
 * Admin Sidebar - Hierarchical navigation menu for the Admin Portal.
 * 
 * Features:
 * - Categorized sections with icons
 * - Active route highlighting
 * - Badge counts for pending items
 * - Collapsible design
 * - Exit admin button
 */
@Composable
fun AdminSidebar(
    selectedSection: AdminRoutes.SidebarSection,
    onSectionClick: (AdminRoutes.SidebarSection) -> Unit,
    onCollapse: () -> Unit,
    onExitAdmin: () -> Unit,
    pendingVerificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(AdminColors.SidebarBackground)
            .padding(vertical = 16.dp)
    ) {
        // Header with logo and collapse button
        SidebarHeader(onCollapse = onCollapse)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Navigation sections
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sidebarItems) { item ->
                val badgeCount = when (item.section) {
                    AdminRoutes.SidebarSection.VERIFICATION -> pendingVerificationsCount
                    else -> 0
                }
                
                SidebarItem(
                    item = item,
                    isSelected = selectedSection == item.section,
                    badgeCount = badgeCount,
                    onClick = { onSectionClick(item.section) }
                )
            }
        }
        
        // Footer with exit button
        SidebarFooter(onExitAdmin = onExitAdmin)
    }
}

@Composable
private fun SidebarHeader(onCollapse: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = AdminColors.Secondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ROSTRY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Admin Portal",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        
        IconButton(onClick = onCollapse) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Collapse sidebar",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SidebarItem(
    item: SidebarItemData,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> AdminColors.SelectedItem
            else -> Color.Transparent
        },
        label = "backgroundColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            else -> Color.White.copy(alpha = 0.7f)
        },
        label = "contentColor"
    )
    
    val startPadding by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        label = "startPadding"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = startPadding)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (isSelected) AdminColors.Secondary else contentColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        
        if (badgeCount > 0) {
            Badge(
                containerColor = Color(0xFFE91E63)
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        
        if (item.hasSubmenu) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SidebarFooter(onExitAdmin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Admin info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = AdminColors.Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Administrator",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Super Admin",
                    style = MaterialTheme.typography.bodySmall,
                    color = AdminColors.Secondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Exit admin button
        OutlinedButton(
            onClick = onExitAdmin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFF44336)
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).let {
                androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF44336).copy(alpha = 0.5f))
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exit Admin Mode")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Data class for sidebar navigation items.
 */
data class SidebarItemData(
    val section: AdminRoutes.SidebarSection,
    val label: String,
    val icon: ImageVector,
    val hasSubmenu: Boolean = false
)

/**
 * Predefined sidebar items with icons and labels.
 */
private val sidebarItems = listOf(
    SidebarItemData(
        section = AdminRoutes.SidebarSection.DASHBOARD,
        label = "Dashboard",
        icon = Icons.Default.Dashboard
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.VERIFICATION,
        label = "Verification",
        icon = Icons.Default.VerifiedUser,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.USERS,
        label = "User Management",
        icon = Icons.Default.People,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.COMMERCE,
        label = "Commerce",
        icon = Icons.Default.Store,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.MODERATION,
        label = "Content Moderation",
        icon = Icons.Default.Shield
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.MONITORING,
        label = "Monitoring",
        icon = Icons.Default.BarChart,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.ANALYTICS,
        label = "Analytics",
        icon = Icons.Default.Analytics,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.REPORTS,
        label = "Reports",
        icon = Icons.Default.Description,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.AUDIT,
        label = "Audit Logs",
        icon = Icons.Default.History
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.SYSTEM,
        label = "System",
        icon = Icons.Default.Settings,
        hasSubmenu = true
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.COMMUNICATION,
        label = "Communication",
        icon = Icons.Default.Campaign
    ),
    SidebarItemData(
        section = AdminRoutes.SidebarSection.BULK,
        label = "Bulk Operations",
        icon = Icons.Default.BatchPrediction
    )
)
