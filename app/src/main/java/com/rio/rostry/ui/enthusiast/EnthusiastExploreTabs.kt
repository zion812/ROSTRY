package com.rio.rostry.ui.enthusiast

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.rio.rostry.ui.components.ShimmerEffect
import com.rio.rostry.ui.enthusiast.components.CategoryPills
import com.rio.rostry.ui.enthusiast.components.CompactCategoryPills
import com.rio.rostry.ui.enthusiast.components.ExploreCategory
import com.rio.rostry.ui.enthusiast.components.AnimatedTabRow
import com.rio.rostry.ui.enthusiast.components.SwipeableFullScreenCard
import com.rio.rostry.ui.social.VideoPlayer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastExploreTabs(
    onOpenProduct: (String) -> Unit,
    onOpenEvent: (String) -> Unit,
    onShare: (String) -> Unit,
    viewModel: EnthusiastExploreTabsViewModel = hiltViewModel(),
    exploreViewModel: EnthusiastExploreViewModel = hiltViewModel()
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("For You", "Products", "Events", "Showcase")
    val state by viewModel.state.collectAsState()
    
    // Featured items for immersive explore (Comment 1)
    val featuredItems by exploreViewModel.featuredItems.collectAsState()
    val selectedCategory by exploreViewModel.selectedCategory.collectAsState()
    val isLoadingFeatured by exploreViewModel.isLoadingFeatured.collectAsState()
    
    // Bottom sheet for legacy filters
    var showFiltersSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    
    Column(Modifier.fillMaxSize()) {
        // Custom animated tab indicator
        AnimatedTabRow(
            selectedTabIndex = tabIndex,
            tabs = tabs,
            onTabSelected = { idx ->
                tabIndex = idx
                viewModel.loadTab(idx)
            }
        )
        when (tabIndex) {
            0 -> ImmersiveExploreFeed(
                featuredItems = featuredItems,
                selectedCategory = selectedCategory,
                isLoading = isLoadingFeatured,
                onCategorySelected = { exploreViewModel.selectCategory(it) },
                onPageChanged = { exploreViewModel.setCurrentPage(it) },
                onRespect = { exploreViewModel.respectBird(it) },
                onOpenProduct = onOpenProduct,
                onShare = onShare,
                onShowFilters = { showFiltersSheet = true }
            )
            1 -> ProductsTab(state.products, onOpenProduct, onShare, state.loading)
            2 -> EventsTab(state.events, state.rsvps, onOpenEvent, viewModel::rsvpToEvent, viewModel::createEvent, state.loading)
            3 -> ShowcaseTab(state.showcasePosts, onShare, viewModel::likePost, viewModel::commentOnPost, viewModel::createShowcase, state.loading)
        }
    }
    
    // Advanced filters bottom sheet (Comment 1)
    if (showFiltersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFiltersSheet = false },
            sheetState = sheetState
        ) {
            AdvancedFiltersSheet(
                onApply = {
                    scope.launch { sheetState.hide() }
                    showFiltersSheet = false
                },
                onDismiss = { showFiltersSheet = false }
            )
        }
    }
}

/**
 * TikTok-style immersive explore with VerticalPager (Comment 1).
 */
@Composable
private fun ImmersiveExploreFeed(
    featuredItems: List<com.rio.rostry.ui.enthusiast.components.FeaturedBird>,
    selectedCategory: ExploreCategory,
    isLoading: Boolean,
    onCategorySelected: (ExploreCategory) -> Unit,
    onPageChanged: (Int) -> Unit,
    onRespect: (String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onShare: (String) -> Unit,
    onShowFilters: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && featuredItems.isEmpty()) {
            // Shimmer skeleton loading (Comment 1)
            SkeletonSwipeCard()
        } else if (featuredItems.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "Nothing to explore yet",
                subtitle = "Check back soon for featured birds",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // VerticalPager for infinite swipe (Comment 1)
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { featuredItems.size }
            )
            
            // Track page changes for prefetching
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    onPageChanged(page)
                }
            }
            
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val bird = featuredItems[page]
                SwipeableFullScreenCard(
                    bird = bird,
                    onSwipeUp = {
                        // Auto-handled by pager
                    },
                    onRespect = { onRespect(bird.id) },
                    onComment = { /* Open comment sheet */ },
                    onShare = { onShare(bird.id) },
                    onFarmTap = { onOpenProduct(bird.id) }
                )
            }
            
            // Glassmorphic category pills overlay at top (Comment 1)
            CompactCategoryPills(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
            
            // Filter FAB (Comment 1)
            FloatingActionButton(
                onClick = onShowFilters,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp),
                containerColor = Color.Black.copy(alpha = 0.5f),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Loading indicator when prefetching
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

/**
 * Skeleton shimmer for loading state (Comment 1).
 */
@Composable
private fun SkeletonSwipeCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF2D2D2D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .size(120.dp, 20.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Box(Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)))
            }
            ShimmerEffect(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(80.dp, 16.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Box(Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)))
            }
            ShimmerEffect(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(60.dp, 12.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Box(Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)))
            }
        }
        
        // Shimmer engagement buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            repeat(3) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Box(Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.3f)))
                }
            }
        }
    }
}

/**
 * Advanced filters bottom sheet (Comment 1).
 */
@Composable
private fun AdvancedFiltersSheet(
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Advanced Filters",
            style = MaterialTheme.typography.titleMedium
        )
        
        // Filter options would go here
        Text(
            "Filter options coming soon...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) { Text("Cancel") }
            
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f)
            ) { Text("Apply") }
        }
    }
}

@Composable
private fun DiscoverTab(
    farmers: List<com.rio.rostry.data.database.entity.UserEntity>,
    learning: List<com.rio.rostry.ui.enthusiast.explore.LearningModule>,
    onFarmerClick: (String) -> Unit,
    onModuleClick: (String) -> Unit
) {
    if (farmers.isEmpty() && learning.isEmpty()) {
        com.rio.rostry.ui.components.EmptyState(
            title = "Nothing to discover yet",
            subtitle = "Check back soon for nearby farmers and content",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            item { 
                com.rio.rostry.ui.enthusiast.explore.NearbyFarmersSection(farmers, onFarmerClick) 
            }
            item {
                com.rio.rostry.ui.enthusiast.explore.LearningContentSection(learning, onModuleClick)
            }
        }
    }
}

@Composable
private fun ProductsTab(products: List<com.rio.rostry.data.database.entity.ProductEntity>, onOpenProduct: (String) -> Unit, onShare: (String) -> Unit, loading: Boolean) {
    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (loading) {
            com.rio.rostry.ui.components.LoadingOverlay()
        }
        if (!loading && products.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No products",
                subtitle = "New items will appear here",
                modifier = Modifier.fillMaxSize()
            )
        }
        // Staggered grid for products (Pinterest-like)
        androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid(
            columns = androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.size) { index ->
                val p = products[index]
                ElevatedCard(
                    onClick = { onOpenProduct(p.productId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Text("$${p.price}")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = { onShare(p.productId) }) { Text("Share") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsTab(
    events: List<com.rio.rostry.data.database.entity.EventEntity>,
    rsvps: List<com.rio.rostry.data.database.entity.EventRsvpEntity>,
    onOpenEvent: (String) -> Unit,
    onRsvp: (String, String) -> Unit,
    onCreate: (title: String, startTime: Long, endTime: Long?, location: String?, description: String?, bannerLocalUri: String?) -> Unit,
    loading: Boolean
) {
    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        var title by rememberSaveable { mutableStateOf("") }
        var location by rememberSaveable { mutableStateOf("") }
        var description by rememberSaveable { mutableStateOf("") }
        var bannerUri by rememberSaveable { mutableStateOf("") }
        val pickBanner = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            bannerUri = uri?.toString() ?: ""
        }
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { pickBanner.launch("image/*") }) { Text(if (bannerUri.isBlank()) "Pick Banner" else "Change Banner") }
            Button(onClick = {
                val now = System.currentTimeMillis()
                onCreate(title.trim(), now + 60 * 60 * 1000, now + 2 * 60 * 60 * 1000, location.ifBlank { null }, description.ifBlank { null }, bannerUri.ifBlank { null })
                title = ""; location = ""; description = ""; bannerUri = ""
            }, enabled = title.isNotBlank()) { Text("Create Event") }
        }
        if (loading) {
            com.rio.rostry.ui.components.LoadingOverlay()
        }
        if (!loading && events.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No events",
                subtitle = "Create one or check back later",
                modifier = Modifier.fillMaxSize()
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(events) { ev ->
                ElevatedCard(onClick = { onOpenEvent(ev.eventId) }) {
                    Column(Modifier.padding(12.dp)) {
                        Text(ev.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Text("Date: ${java.text.SimpleDateFormat("MMM d, yyyy").format(java.util.Date(ev.startTime))}")
                        Text("Location: ${ev.location ?: "TBD"}")
                        val userRsvp = rsvps.find { it.eventId == ev.eventId }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = { onRsvp(ev.eventId, "GOING") }, enabled = userRsvp?.status != "GOING") { Text("Going") }
                            OutlinedButton(onClick = { onRsvp(ev.eventId, "MAYBE") }, enabled = userRsvp?.status != "MAYBE") { Text("Maybe") }
                            OutlinedButton(onClick = { onRsvp(ev.eventId, "NOT_GOING") }, enabled = userRsvp?.status != "NOT_GOING") { Text("Not Going") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowcaseTab(
    posts: List<com.rio.rostry.data.database.entity.PostEntity>,
    onShare: (String) -> Unit,
    onLike: (String) -> Unit,
    onComment: (String, String) -> Unit,
    onCreate: (caption: String?, localUri: String, linkedProductId: String?) -> Unit,
    loading: Boolean
) {
    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        var caption by rememberSaveable { mutableStateOf("") }
        var mediaUri by rememberSaveable { mutableStateOf("") }
        val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            mediaUri = uri?.toString() ?: ""
        }
        OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Caption (optional)") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { pickMedia.launch("*/*") }) { Text(if (mediaUri.isBlank()) "Pick Media" else "Change Media") }
            Button(onClick = {
                onCreate(caption.ifBlank { null }, mediaUri, null)
                caption = ""; mediaUri = ""
            }, enabled = mediaUri.isNotBlank()) { Text("Create Showcase Post") }
        }
        if (loading) {
            com.rio.rostry.ui.components.LoadingOverlay()
        }
        if (!loading && posts.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No showcase posts",
                subtitle = "Share your work to get started",
                modifier = Modifier.fillMaxSize()
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(posts) { post ->
                ElevatedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text(post.text ?: "", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                        val url = post.mediaUrl
                        if (!url.isNullOrBlank()) {
                            val isVideo = url.endsWith(".mp4", true) || url.endsWith(".m3u8", true) || url.contains("/video", true)
                            if (isVideo) {
                                VideoPlayer(modifier = Modifier.fillMaxWidth(), url = url)
                            } else {
                                Image(painter = rememberAsyncImagePainter(url), contentDescription = null, modifier = Modifier.fillMaxWidth())
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = { onLike(post.postId) }) { Text("Like") }
                            OutlinedButton(onClick = { onComment(post.postId, "Great!") }) { Text("Comment") }
                            OutlinedButton(onClick = { onShare(post.postId) }) { Text("Share") }
                        }
                    }
                }
            }
        }
    }
}

