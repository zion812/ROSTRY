@file:OptIn(ExperimentalMaterial3Api::class)

package com.rio.rostry.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.rio.rostry.ui.components.SkeletonListItem
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.rio.rostry.domain.model.VerificationStatus

@Composable
fun FarmerMarketScreen(
    onCreateListing: () -> Unit,
    onEditListing: (String) -> Unit,
    onBoostListing: (String) -> Unit,
    onPauseListing: (String) -> Unit,
    onOpenOrder: (String) -> Unit,
    onOpenProduct: (String) -> Unit = {},
    onApplyPriceBreed: (Double?, Double?, String?) -> Unit = { _, _, _ -> },
    onApplyDateFilter: (Long?, Long?) -> Unit = { _, _ -> },
    onClearDateFilter: () -> Unit = {},
    startDate: Long? = null,
    endDate: Long? = null,
    selectedTabIndex: Int = 0,
    onSelectTab: (Int) -> Unit = {},
    metricsRevenue: Double = 0.0,
    metricsOrders: Int = 0,
    metricsViews: Int = 0,
    isLoadingBrowse: Boolean = false,
    isLoadingMine: Boolean = false,
    browse: List<Listing> = emptyList(),
    mine: List<Listing> = emptyList(),
    onRefresh: () -> Unit = {},
    onSelectCategoryMeat: () -> Unit = {},
    onSelectCategoryAdoption: () -> Unit = {},
    onSelectTraceable: () -> Unit = {},
    onSelectNonTraceable: () -> Unit = {},
    categoryMeatSelected: Boolean = false,
    categoryAdoptionSelected: Boolean = false,
    traceableSelected: Boolean = false,
    nonTraceableSelected: Boolean = false,
    onScanQr: () -> Unit = {},
    verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED
) {
    var showVerificationPendingDialog by remember { mutableStateOf(false) }
    var showVerificationRejectedDialog by remember { mutableStateOf(false) }

    if (showVerificationPendingDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationPendingDialog = false },
            icon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            title = { Text("Verification Pending") },
            text = { Text("Market listing requires verification approval. You can continue using all other farm features while your verification is being reviewed (usually 24-48 hours).") },
            confirmButton = {
                Button(onClick = { showVerificationPendingDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }

    if (showVerificationRejectedDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationRejectedDialog = false },
            icon = { Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Verification Rejected") },
            text = { Text("Your verification request was rejected. Please check your profile for details and resubmit.") },
            confirmButton = {
                Button(onClick = { showVerificationRejectedDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = { 
                        Text(
                            "Marketplace", 
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    actions = {
                        IconButton(onClick = { /* Notifications */ }) {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { onSelectTab(0) },
                        text = { Text("Browse Market", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Outlined.Storefront, contentDescription = "Browse Market Tab") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { onSelectTab(1) },
                        text = { Text("My Listings", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Outlined.Inventory, contentDescription = "My Listings Tab") }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                ExtendedFloatingActionButton(
                    onClick = {
                        when (verificationStatus) {
                            VerificationStatus.VERIFIED -> onCreateListing()
                            VerificationStatus.REJECTED -> showVerificationRejectedDialog = true
                            else -> showVerificationPendingDialog = true
                        }
                    },
                    containerColor = if (verificationStatus == VerificationStatus.VERIFIED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (verificationStatus == VerificationStatus.VERIFIED) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New Listing") }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
            when (selectedTabIndex) {
                0 -> {
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = isLoadingBrowse,
                        onRefresh = onRefresh,
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BrowseMarket(
                            onOpenOrder = onOpenOrder,
                            onOpenProduct = onOpenProduct,
                            onApplyPriceBreed = onApplyPriceBreed,
                            onApplyDateFilter = onApplyDateFilter,
                            onClearDateFilter = onClearDateFilter,
                            startDate = startDate,
                            endDate = endDate,
                            isLoading = isLoadingBrowse,
                            items = browse,
                            onSelectCategoryMeat = onSelectCategoryMeat,
                            onSelectCategoryAdoption = onSelectCategoryAdoption,
                            onSelectTraceable = onSelectTraceable,
                            onSelectNonTraceable = onSelectNonTraceable,
                            categoryMeatSelected = categoryMeatSelected,
                            categoryAdoptionSelected = categoryAdoptionSelected,
                            traceableSelected = traceableSelected,
                            nonTraceableSelected = nonTraceableSelected,
                            onScanQr = onScanQr
                        )
                    }
                }
                else -> SellManager(
                    onCreateListing = onCreateListing,
                    onEditListing = onEditListing,
                    onBoostListing = onBoostListing,
                    onPauseListing = onPauseListing,
                    isLoading = isLoadingMine,
                    items = mine,
                    onRefresh = onRefresh,
                    metricsRevenue = metricsRevenue,
                    metricsOrders = metricsOrders,
                    metricsViews = metricsViews,
                    verificationStatus = verificationStatus,
                    onShowVerificationDialog = { 
                        if (verificationStatus == VerificationStatus.REJECTED) {
                            showVerificationRejectedDialog = true
                        } else {
                            showVerificationPendingDialog = true 
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BrowseMarket(
    onOpenOrder: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onApplyPriceBreed: (Double?, Double?, String?) -> Unit,
    onApplyDateFilter: (Long?, Long?) -> Unit,
    onClearDateFilter: () -> Unit,
    startDate: Long?,
    endDate: Long?,
    isLoading: Boolean,
    items: List<Listing>,
    onSelectCategoryMeat: () -> Unit,
    onSelectCategoryAdoption: () -> Unit,
    onSelectTraceable: () -> Unit,
    onSelectNonTraceable: () -> Unit,
    categoryMeatSelected: Boolean,
    categoryAdoptionSelected: Boolean,
    traceableSelected: Boolean,
    nonTraceableSelected: Boolean,
    onScanQr: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    
    // Filter logic
    val displayed = if (searchText.isBlank()) items else items.filter {
        it.title.contains(searchText, ignoreCase = true)
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search products, breeds...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = onScanQr) {
                                    Icon(
                                        Icons.Default.QrCodeScanner,
                                        contentDescription = "Scan QR",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { showFilters = !showFilters }) {
                                    Icon(
                                        Icons.Default.Tune, 
                                        contentDescription = "Filter",
                                        tint = if (showFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    if (showFilters) {
                        Spacer(Modifier.height(16.dp))
                        FilterPanel(
                            categoryMeatSelected, onSelectCategoryMeat,
                            categoryAdoptionSelected, onSelectCategoryAdoption,
                            traceableSelected, onSelectTraceable,
                            nonTraceableSelected, onSelectNonTraceable,
                            startDate, endDate, onApplyDateFilter, onClearDateFilter
                        )
                    }
                }
            }
        }

        // Listings Grid
        if (isLoading && displayed.isEmpty()) {
            items(5) {
                SkeletonListItem()
            }
        } else if (!isLoading && displayed.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.ShoppingBag,
                    title = "No listings found",
                    subtitle = "Try adjusting your search or filters"
                )
            }
        } else {
            items(displayed) { item ->
                MarketListingCard(
                    item = item,
                    onOpenProduct = onOpenProduct,
                    onMessage = { onOpenOrder(item.id) }
                )
            }
        }
    }
}

@Composable
private fun FilterPanel(
    meat: Boolean, onMeat: () -> Unit,
    adoption: Boolean, onAdoption: () -> Unit,
    traceable: Boolean, onTraceable: () -> Unit,
    nonTraceable: Boolean, onNonTraceable: () -> Unit,
    startDate: Long?, endDate: Long?,
    onDateFilter: (Long?, Long?) -> Unit,
    onClearDate: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Categories", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = meat, onClick = onMeat, label = { Text("Meat") })
            FilterChip(selected = adoption, onClick = onAdoption, label = { Text("Adoption") })
        }
        
        Text("Traceability", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = traceable, onClick = onTraceable, label = { Text("Verified Traceable") }, leadingIcon = {
                if (traceable) Icon(Icons.Default.Check, null)
            })
            FilterChip(selected = nonTraceable, onClick = onNonTraceable, label = { Text("Standard") })
        }
        
        // Simplified Date Filter for brevity in this polish pass
        Text("Date Range", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { /* Open Picker */ }) {
                Text(if (startDate != null) "Start: ..." else "Select Dates")
            }
            if (startDate != null) {
                TextButton(onClick = onClearDate) { Text("Clear") }
            }
        }
    }
}

@Composable
private fun MarketListingCard(
    item: Listing,
    onOpenProduct: (String) -> Unit,
    onMessage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onOpenProduct(item.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Image Placeholder
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    Icons.Outlined.Image, 
                    contentDescription = null, 
                    modifier = Modifier.padding(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    item.title, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "₹${item.price}", 
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (item.isBatch) {
                    Spacer(Modifier.height(2.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Batch of ${item.quantity}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text("${item.views} views", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            IconButton(
                onClick = onMessage,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(Icons.Outlined.Chat, contentDescription = "Message", tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
private fun SellManager(
    onCreateListing: () -> Unit,
    onEditListing: (String) -> Unit,
    onBoostListing: (String) -> Unit,
    onPauseListing: (String) -> Unit,
    isLoading: Boolean,
    items: List<Listing>,
    onRefresh: () -> Unit,
    metricsRevenue: Double,
    metricsOrders: Int,
    metricsViews: Int,
    verificationStatus: VerificationStatus,
    onShowVerificationDialog: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Dashboard Header
        item {
            DashboardMetricsCard(metricsRevenue, metricsOrders, metricsViews)
        }

        // Quick Actions
        item {
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Create",
                    onClick = {
                        if (verificationStatus == VerificationStatus.VERIFIED) {
                            onCreateListing()
                        } else {
                            onShowVerificationDialog()
                        }
                    },
                    enabled = true, // Always enable to show dialog
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.TrendingUp,
                    label = "Promote",
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Analytics,
                    label = "Reports",
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Listings List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Listings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Refresh")
                }
            }
        }

        if (isLoading && items.isEmpty()) {
            items(5) {
                SkeletonListItem()
            }
        } else if (items.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Storefront,
                    title = "No active listings",
                    subtitle = "Start selling by creating your first listing"
                )
            }
        } else {
            items(items) { item ->
                SellerListingItem(
                    item = item,
                    onEdit = { onEditListing(item.id) },
                    onPause = { onPauseListing(item.id) },
                    onBoost = { onBoostListing(item.id) }
                )
            }
        }
    }
}

@Composable
private fun DashboardMetricsCard(revenue: Double, orders: Int, views: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.width(8.dp))
                Text("Performance (This Week)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricItem("Revenue", "₹${"%.0f".format(revenue)}")
                MetricItem("Orders", "$orders")
                MetricItem("Views", "$views")
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector, 
    label: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shadowElevation = 1.dp,
        modifier = modifier.height(80.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SellerListingItem(
    item: Listing,
    onEdit: () -> Unit,
    onPause: () -> Unit,
    onBoost: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("₹${item.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "Active", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBadge(Icons.Default.Visibility, "${item.views}")
                StatBadge(Icons.Default.ChatBubbleOutline, "${item.inquiries}")
                StatBadge(Icons.Default.ShoppingBag, "${item.orders}")
            }
            Spacer(Modifier.height(16.dp))
            Divider()
            Row(Modifier.fillMaxWidth()) {
                TextButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Edit") }
                TextButton(onClick = onPause, modifier = Modifier.weight(1f)) { Text("Pause") }
                TextButton(onClick = onBoost, modifier = Modifier.weight(1f)) { Text("Boost") }
            }
        }
    }
}

@Composable
private fun StatBadge(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyState(icon: ImageVector, title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

data class Listing(
    val id: String,
    val title: String,
    val price: Double,
    val views: Int,
    val inquiries: Int,
    val orders: Int,
    val isBatch: Boolean = false,
    val quantity: Int = 1,
    val category: String = "",
    val isTraceable: Boolean = false
)
