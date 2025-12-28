@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.rio.rostry.ui.general.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.ui.general.explore.GeneralExploreViewModel.ExploreFilter
import com.rio.rostry.ui.general.explore.GeneralExploreViewModel.UserPreview
import com.rio.rostry.ui.social.VideoPlayer
import com.rio.rostry.ui.components.SkeletonCard
import com.rio.rostry.data.database.entity.BreedEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.ui.enthusiast.explore.NearbyFarmersSection
import com.rio.rostry.ui.enthusiast.explore.LearningContentSection
import com.rio.rostry.ui.enthusiast.explore.LearningModule
import kotlinx.coroutines.launch

@Composable
fun GeneralExploreRoute(
    onOpenSocialFeed: () -> Unit,
    onOpenMessages: (String) -> Unit,
    onFarmerProfileClick: (String) -> Unit = {}, // Navigate to full farmer profile
    onViewFarmersMap: () -> Unit = {}, // Open map view of farmers
    onScanQr: () -> Unit = {},
    viewModel: GeneralExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val feedItems = viewModel.feed.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearError()
        }
    }

    var showProfileSheet by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(uiState.profilePreview) {
        showProfileSheet = uiState.profilePreview != null
    }

    var showWizard by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f) // Slightly off-white/grey for depth
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. Header & Search
            item {
                ExploreHeader(
                    query = uiState.query,
                    onQueryChange = viewModel::updateQuery,

                    onClearQuery = { viewModel.updateQuery("") },
                    onScanQr = onScanQr
                )
            }

            // 2. Visual Categories
            item {
                CategoryRail(
                    onCategoryClick = { cat -> viewModel.updateQuery("category:$cat") }
                )
            }

            // 2.2 Nearby Farmers
            if (uiState.nearbyFarmers.isNotEmpty()) {
                item {
                    NearbyFarmersSection(
                        farmers = uiState.nearbyFarmers,
                        onFarmerClick = { farmerId -> 
                            // Show profile preview, then navigate on tap
                            viewModel.openProfilePreview(farmerId)
                            onFarmerProfileClick(farmerId)
                        },
                        onViewMap = onViewFarmersMap
                    )
                }
            }
            
            // 2.3 Seasonal Picks
            item {
                SeasonalSection()
            }

            // 2.5 Help Me Choose Wizard Entry
            item {
                HelpMeChooseEntry(onClick = { showWizard = true })
            }

            // 2.6 Recommended Breeds (if any)
            if (uiState.recommendedBreeds.isNotEmpty()) {
                item {
                    RecommendedBreedsSection(breeds = uiState.recommendedBreeds)
                }
            }

            // 2.7 Starter Kits (if any)
            if (uiState.starterKits.isNotEmpty()) {
                item {
                    StarterKitsSection(kits = uiState.starterKits)
                }
            }

            // 3. Featured / Trending (Horizontal Pager)
            item {
                FeaturedSection()
            }

            // 4. Filters (Pills)
            item {
                FilterRow(
                    selected = uiState.filter,
                    onSelected = viewModel::setFilter
                )
            }
            
            // 2.4 Daily Tip
            if (uiState.dailyTip != null) {
                item {
                    DailyTipCard(content = uiState.dailyTip!!)
                }
            }

            // 5. Learn & Grow
            if (uiState.educationalContent.isNotEmpty()) {
                item {
                    val modules = uiState.educationalContent.map { ec ->
                        val durationText = ec.duration?.let { "$it min" } ?: "5 min"
                        LearningModule(
                            id = ec.id,
                            title = ec.title,
                            type = ec.type.name,
                            durationOrReadTime = durationText, 
                            thumbnailUrl = ec.imageUrl ?: ""
                        )
                    }
                    LearningContentSection(
                        modules = modules,
                        onModuleClick = { /* Navigate to content */ }
                    )
                }
            }

            // 6. Feed Content
            if (feedItems.itemCount == 0 && feedItems.loadState.refresh is LoadState.Loading) {
                items(3) {
                    SkeletonCard(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        height = 350.dp
                    )
                }
            } else if (feedItems.itemCount == 0) {
                item {
                    EmptyStateView()
                }
            } else {
                items(feedItems.itemCount) { index ->
                    val post = feedItems[index]
                    if (post != null) {
                        ModernPostCard(
                            post = post,
                            onAuthorClick = { viewModel.openProfilePreview(post.authorId) },
                            onLikeToggle = { liked ->
                                if (liked) viewModel.like(post.postId) else viewModel.unlike(post.postId)
                            },
                            onOpenMessages = onOpenMessages
                        )
                    }
                }
                
                // Loading footer
                if (feedItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }

    if (showProfileSheet) {
        uiState.profilePreview?.let { preview ->
            ModalBottomSheet(
                onDismissRequest = {
                    showProfileSheet = false
                    viewModel.closeProfilePreview()
                },
                sheetState = sheetState
            ) {
                ProfilePreviewSheet(preview = preview)
            }
        }
    }

    if (showWizard) {
        ModalBottomSheet(
            onDismissRequest = { showWizard = false },
            sheetState = sheetState
        ) {
            HelpMeChooseSheet(
                onDismiss = { showWizard = false },
                onTasteSelected = { profile ->
                    viewModel.filterBreedsByTaste(profile)
                },
                onFarmingSelected = {
                    viewModel.fetchStarterKits()
                }
            )
        }
    }
}

@Composable
private fun ExploreHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onScanQr: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Explore",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))

        // Modern Search Bar
        Surface(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            tonalElevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            "Search breeds, farms, tips...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurface)
                    }
                } else {
                    IconButton(onClick = onScanQr) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
        }
    }
}
}

    @Composable
    private fun CategoryRail(onCategoryClick: (String) -> Unit) {
        val categories = listOf(
            "Poultry" to Icons.Default.Egg,
            "Livestock" to Icons.Default.Pets,
            "Feed" to Icons.Default.Grass,
            "Health" to Icons.Default.MedicalServices,
            "Equip" to Icons.Default.Build,
            "Events" to Icons.Default.Event
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(categories) { (name, icon) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onCategoryClick(name) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    @Composable
    private fun FeaturedSection() {
        val pagerState = rememberPagerState(pageCount = { 3 })

        Column(Modifier.padding(bottom = 24.dp)) {
            PaddingValues(horizontal = 20.dp)
            Text(
                text = "Trending Now",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 20.dp),
                pageSpacing = 16.dp
            ) { page ->
                FeaturedCard(page)
            }
        }
    }

    @Composable
    private fun FeaturedCard(index: Int) {
        val colors = listOf(
            Color(0xFF1E88E5) to Color(0xFF1565C0),
            Color(0xFF43A047) to Color(0xFF2E7D32),
            Color(0xFFFB8C00) to Color(0xFFEF6C00)
        )
        val (startColor, endColor) = colors[index % colors.size]
        val titles = listOf("Premium Asil Breeds", "Organic Feed Guide", "Upcoming Expos")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(startColor, endColor)))
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "FEATURED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = titles[index],
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    private fun FilterRow(
        selected: ExploreFilter,
        onSelected: (ExploreFilter) -> Unit
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(ExploreFilter.values()) { filter ->
                val isSelected = selected == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(filter) },
                    label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }

    @Composable
    private fun LearnAndGrowSection(
        content: List<com.rio.rostry.domain.model.EducationalContent>,
        onItemClick: (com.rio.rostry.domain.model.EducationalContent) -> Unit
    ) {
        Column(Modifier.padding(bottom = 24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Learn & Grow",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* See all */ }) {
                    Text("See All")
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(content) { item ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .height(240.dp)
                            .clickable { onItemClick(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                // Placeholder for image
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.Center),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = item.type.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "5 min read",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ModernPostCard(
        post: PostEntity,
        onAuthorClick: () -> Unit,
        onLikeToggle: (Boolean) -> Unit,
        onOpenMessages: (String) -> Unit
    ) {
        var liked by rememberSaveable(post.postId) { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp).clickable(onClick = onAuthorClick)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = post.authorId.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "User ${post.authorId.take(4)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "2 hours ago", // Placeholder
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "More")
                    }
                }

                // Media
                if (!post.mediaUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.Black)
                    ) {
                        if (post.type.equals(
                                "video",
                                ignoreCase = true
                            ) || post.mediaUrl!!.endsWith(".mp4", true)
                        ) {
                            VideoPlayer(modifier = Modifier.fillMaxSize(), url = post.mediaUrl!!)
                        } else {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Actions & Content
                Column(Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            IconToggleButton(checked = liked, onCheckedChange = {
                                liked = it
                                onLikeToggle(it)
                            }) {
                                Icon(
                                    imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (liked) Color.Red else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = { onOpenMessages(post.postId) }) {
                                Icon(
                                    Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = "Comment"
                                )
                            }
                            IconButton(onClick = { /* Share */ }) {
                                Icon(Icons.Outlined.Send, contentDescription = "Share")
                            }
                        }
                        IconButton(onClick = { /* Bookmark */ }) {
                            Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save")
                        }
                    }

                    if (!post.text.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = post.text!!,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun EmptyStateView() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No results found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Try adjusting your search or filters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    private fun ProfilePreviewSheet(preview: UserPreview) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(80.dp)
            ) {
                if (preview.avatarUrl != null) {
                    AsyncImage(
                        model = preview.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (preview.displayName ?: "U").take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = preview.displayName ?: "Community Member",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            preview.location?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { /* Follow */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Follow")
                }
                OutlinedButton(
                    onClick = { /* Message */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Message")
                }
            }
        }
    }


@Composable
fun HelpMeChooseEntry(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Confused? Let us help!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Find the perfect bird for your taste or farm.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun RecommendedBreedsSection(breeds: List<BreedEntity>) {
    Column(Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Recommended for You",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(breeds) { breed ->
                Card(
                    modifier = Modifier
                        .width(240.dp)
                        .height(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = breed.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = breed.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            breed.tags.take(2).forEach { tag ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StarterKitsSection(kits: List<ProductEntity>) {
    Column(Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Farmhouse Starter Kits",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(kits) { kit ->
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .height(220.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        ) {
                            // Placeholder image
                        }
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = kit.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${kit.price}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Includes: 5 Hens, 1 Rooster...", // Placeholder, should come from description
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
        
        // Remote Monitoring Reassurance
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "We've got your back!",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ROSTRY will remind you when to water and medicate your flock.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun SeasonalSection() {
    Column(Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Seasonal Picks: Summer",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        Text(
            text = "Fresh items arriving this week!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val seasonalItems = listOf("Mangoes", "Watermelon", "Cucumber", "Mint", "Corn", "Berries")
            items(seasonalItems) { item ->
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(36.dp).clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyTipCard(content: com.rio.rostry.domain.model.EducationalContent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(28.dp).padding(top = 2.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = content.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                )
            }
        }
    }
}
