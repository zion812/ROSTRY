@file:OptIn(ExperimentalMaterial3Api::class)

package com.rio.rostry.ui.general.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.ui.general.explore.GeneralExploreViewModel.ExploreFilter
import com.rio.rostry.ui.general.explore.GeneralExploreViewModel.UserPreview
import com.rio.rostry.ui.social.VideoPlayer
import com.rio.rostry.ui.general.explore.EducationalContentRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralExploreRoute(
    onOpenSocialFeed: () -> Unit,
    onOpenMessages: (String) -> Unit,
    viewModel: GeneralExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val feedItems = viewModel.feed.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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

    Scaffold(
        topBar = {
            ExploreTopBar(
                query = uiState.query,
                onQueryChange = viewModel::updateQuery,
                onClearQuery = { viewModel.updateQuery("") },
                onOpenSocial = onOpenSocialFeed
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ExploreFilterRow(
                selected = uiState.filter,
                onSelected = viewModel::setFilter,
                tokensSummary = uiState.tokens
            )

            val eduContent by viewModel.educationalContent.collectAsStateWithLifecycle()
            var selectedContentId by rememberSaveable { mutableStateOf<String?>(null) }

            if (selectedContentId != null) {
                // Simple overlay navigation for demo purposes
                // In a real app, use NavController
                androidx.compose.ui.window.Dialog(onDismissRequest = { selectedContentId = null }) {
                    androidx.compose.material3.Surface(modifier = Modifier.fillMaxSize()) {
                        if (selectedContentId == "2") {
                            MeatSelectionToolScreen(onBack = { selectedContentId = null })
                        } else {
                            EducationalContentDetailScreen(
                                contentId = selectedContentId!!,
                                onBack = { selectedContentId = null }
                            )
                        }
                    }
                }
            }

            if (eduContent.isNotEmpty()) {
                EducationalContentRow(
                    content = eduContent,
                    onItemClick = { item ->
                        selectedContentId = item.id
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            if (feedItems.itemCount == 0 && feedItems.loadState.refresh is androidx.paging.LoadState.Loading) {
                Box(Modifier.fillMaxSize()) {
                    com.rio.rostry.ui.components.LoadingOverlay()
                }
            } else if (feedItems.itemCount == 0) {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No results",
                    subtitle = "Try changing filters or search terms",
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(feedItems.itemCount) { index ->
                        val post = feedItems[index]
                        if (post != null) {
                            ExplorePostCard(
                                post = post,
                                onAuthorClick = { viewModel.openProfilePreview(post.authorId) },
                                onLikeToggle = { liked ->
                                    if (liked) viewModel.like(post.postId) else viewModel.unlike(post.postId)
                                },
                                onOpenMessages = onOpenMessages,
                                onOpenSocialFeed = onOpenSocialFeed
                            )
                        }
                    }
                    when (val state = feedItems.loadState.append) {
                        is androidx.paging.LoadState.Loading -> {
                            item {
                                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                        is androidx.paging.LoadState.Error -> {
                            item {
                                TextButton(onClick = { feedItems.retry() }, modifier = Modifier.padding(16.dp)) {
                                    Text("Retry loading posts")
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    if (showProfileSheet) {
        val preview = uiState.profilePreview
        if (preview != null) {
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
}

@Composable
private fun ExploreTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onOpenSocial: () -> Unit
) {
    var showSearchTips by rememberSaveable { mutableStateOf(false) }
    
    TopAppBar(
        title = { Text("Explore community", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        actions = {
            IconButton(onClick = { showSearchTips = true }) {
                Icon(Icons.Filled.Help, contentDescription = "Search tips")
            }
            TextButton(onClick = onOpenSocial) {
                Text("Go to feed")
            }
        }
    )
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.isNotBlank()) {
                    TextButton(onClick = onClearQuery) { Text("Clear") }
                }
            },
            placeholder = { Text("Search @users, #hashtags, loc:city, breed:asil") },
            singleLine = true
        )
        
        if (query.isEmpty()) {
            SearchHintChips(
                onChipClick = { hint ->
                    onQueryChange(hint)
                }
            )
        }
    }
    
    if (showSearchTips) {
        SearchTipsDialog(onDismiss = { showSearchTips = false })
    }
}

@Composable
private fun ExploreFilterRow(
    selected: ExploreFilter,
    onSelected: (ExploreFilter) -> Unit,
    tokensSummary: GeneralExploreViewModel.QueryTokens
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = selected == ExploreFilter.RECENT,
                onClick = { onSelected(ExploreFilter.RECENT) },
                label = { Text("Recent") },
                leadingIcon = { Icon(Icons.Filled.Public, contentDescription = "Recent posts") }
            )
            FilterChip(
                selected = selected == ExploreFilter.POPULAR,
                onClick = { onSelected(ExploreFilter.POPULAR) },
                label = { Text("Popular") },
                leadingIcon = { Icon(Icons.Filled.FilterList, contentDescription = "Popular filter") }
            )
            FilterChip(
                selected = selected == ExploreFilter.NEARBY,
                onClick = { onSelected(ExploreFilter.NEARBY) },
                label = { Text("Nearby") },
                leadingIcon = { Icon(Icons.Outlined.NearMe, contentDescription = "Nearby") }
            )
            FilterChip(
                selected = selected == ExploreFilter.FOLLOWING,
                onClick = { onSelected(ExploreFilter.FOLLOWING) },
                label = { Text("Following") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Following") }
            )
        }
        if (!tokensSummary.isEmpty) {
            Spacer(Modifier.height(12.dp))
            AssistChip(
                onClick = { /* informational only */ },
                label = { Text("Filters applied: ${tokensSummary.keywords.size + tokensSummary.hashtags.size + tokensSummary.mentions.size}") },
                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ExplorePostCard(
    post: PostEntity,
    onAuthorClick: () -> Unit,
    onLikeToggle: (Boolean) -> Unit,
    onOpenMessages: (String) -> Unit,
    onOpenSocialFeed: () -> Unit
) {
    var liked by rememberSaveable(post.postId) { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    TextButton(onClick = onAuthorClick, contentPadding = PaddingValues(0.dp)) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(text = post.authorId.take(10), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Tap to preview profile", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                IconButton(onClick = { onOpenMessages(post.postId) }) {
                    Icon(Icons.Filled.Message, contentDescription = "Discuss")
                }
            }

            Spacer(Modifier.height(8.dp))
            if (!post.text.isNullOrBlank()) {
                Text(post.text!!, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
            }

            when {
                post.mediaUrl.isNullOrBlank() -> Unit
                post.type.equals("video", ignoreCase = true) || post.mediaUrl!!.endsWith(".mp4", true) -> {
                    VideoPlayer(modifier = Modifier.fillMaxWidth().height(220.dp), url = post.mediaUrl!!)
                    Spacer(Modifier.height(8.dp))
                }
                else -> {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = {
                    liked = !liked
                    onLikeToggle(liked)
                }) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.Person else Icons.Filled.Public,
                        contentDescription = "Like"
                    )
                }
                TextButton(onClick = onOpenSocialFeed) { Text("View details") }
            }
        }
    }
}

@Composable
private fun SearchHintChips(onChipClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Try:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { onChipClick("#poultry") },
                label = { Text("#poultry") },
                leadingIcon = { Icon(Icons.Filled.Public, contentDescription = "Hashtag", modifier = Modifier.size(16.dp)) }
            )
            AssistChip(
                onClick = { onChipClick("breed:asil") },
                label = { Text("breed:asil") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", modifier = Modifier.size(16.dp)) }
            )
            AssistChip(
                onClick = { onChipClick("loc:bangalore") },
                label = { Text("loc:bangalore") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@Composable
private fun SearchTipsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Tips") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SearchTipItem(
                    icon = Icons.Filled.Public,
                    title = "Hashtags",
                    description = "#poultry, #organic, #freerange",
                    example = "Find posts with specific topics"
                )
                SearchTipItem(
                    icon = Icons.Filled.Person,
                    title = "Mentions",
                    description = "@username",
                    example = "Find posts by specific users"
                )
                SearchTipItem(
                    icon = Icons.Filled.LocationOn,
                    title = "Location",
                    description = "loc:city or loc:state",
                    example = "loc:bangalore, loc:karnataka"
                )
                SearchTipItem(
                    icon = Icons.Filled.Pets,
                    title = "Breed",
                    description = "breed:name",
                    example = "breed:asil, breed:kadaknath"
                )
                SearchTipItem(
                    icon = Icons.Filled.ColorLens,
                    title = "Color",
                    description = "color:name",
                    example = "color:black, color:white"
                )
                Divider()
                Text(
                    "💡 Combine multiple filters:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "\"#organic breed:asil loc:bangalore\"",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@Composable
private fun SearchTipItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    example: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = example,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfilePreviewSheet(preview: UserPreview) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (preview.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(24.dp))
            return@Column
        }
        Card {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = preview.avatarUrl,
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .height(72.dp)
                )
                Text(preview.displayName ?: "Community member", style = MaterialTheme.typography.titleMedium)
                preview.headline?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                preview.location?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("Based in $it", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { /* future follow/unfollow */ }) {
                    Text("Follow")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Divider()
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { /* placeholder for report */ }) {
            Text("Report user")
        }
    }
}
