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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.community.CommunityEngagementService
import com.rio.rostry.data.database.entity.GroupEntity
import com.rio.rostry.data.database.entity.EventEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerCommunityScreen(
    onOpenThread: (String) -> Unit,
    onOpenGroupDirectory: () -> Unit,
    onOpenExpertBooking: () -> Unit,
    onOpenRegionalNews: () -> Unit,
    vm: FarmerCommunityViewModel = hiltViewModel()
) {
    val activeThreads by vm.activeThreads.collectAsState()
    val suggestedGroups by vm.suggestedGroups.collectAsState()
    val upcomingEvents by vm.upcomingEvents.collectAsState()
    val availableExperts by vm.availableExperts.collectAsState()
    val trendingPosts by vm.trendingPosts.collectAsState()
    val unreadCount by vm.unreadCount.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    val searchQuery by vm.searchQuery.collectAsState()
    val searchResults by vm.searchResults.collectAsState()
    var isSearchActive by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = vm::onSearchQueryChanged,
                            placeholder = { Text("Search users, groups...", style = MaterialTheme.typography.bodyLarge) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSearchActive = false
                            vm.clearSearch()
                        }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { vm.clearSearch() }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Community Hub", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { /* Messages */ }) {
                            BadgedBox(badge = { if (unreadCount > 0) Badge { Text("$unreadCount") } }) {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Messages")
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (searchQuery.isNotEmpty()) {
                    // Search Results
                    when (val result = searchResults) {
                        is FarmerCommunityViewModel.SearchResult.Empty -> {
                            item {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No results found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        is FarmerCommunityViewModel.SearchResult.Success -> {
                            if (result.users.isNotEmpty()) {
                                item { SectionHeader("People", null) {} }
                                items(result.users) { user ->
                                    UserSearchResultItem(user) { 
                                        vm.startChat(user.userId) { threadId -> onOpenThread(threadId) }
                                    }
                                }
                            }
                            if (result.groups.isNotEmpty()) {
                                item { SectionHeader("Groups", null) {} }
                                items(result.groups) { group ->
                                    GroupSearchResultItem(group) { vm.joinGroup(group.groupId) }
                                }
                            }
                            if (result.users.isEmpty() && result.groups.isEmpty()) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No matching users or groups", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Default Dashboard Content
                    // 1. Quick Access Grid
                    item {
                        QuickAccessSection(
                            onOpenGroupDirectory,
                            onOpenExpertBooking,
                            onOpenRegionalNews
                        )
                    }
    
                    // 2. Active Discussions
                    item {
                        SectionHeader("Active Discussions", "See All") {}
                    }
    
                    if (activeThreads.isEmpty()) {
                        item {
                            EmptyStateCard("No active conversations", "Start a discussion to connect with others.")
                        }
                    } else {
                        items(activeThreads) { thread ->
                            DiscussionItem(thread, onOpenThread)
                        }
                    }
    
                    // 3. Trending Posts / News
                    if (trendingPosts.isNotEmpty()) {
                        item {
                            SectionHeader("Community Highlights", "View All") { onOpenRegionalNews() }
                        }
                        items(trendingPosts) { post ->
                            PostCard(post)
                        }
                    }
    
                    // 4. Expert Corner
                    if (availableExperts.isNotEmpty()) {
                        item {
                            SectionHeader("Expert Corner", "View All") { onOpenExpertBooking() }
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(availableExperts) { expert ->
                                    ExpertCard(expert) { 
                                        vm.bookExpert(expert.userId, expert.specialties.firstOrNull() ?: "", System.currentTimeMillis()) 
                                    }
                                }
                            }
                        }
                    }
    
                    // 5. Suggested Groups
                    if (suggestedGroups.isNotEmpty()) {
                        item {
                            SectionHeader("Groups for You", "Directory") { onOpenGroupDirectory() }
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(suggestedGroups) { group ->
                                    GroupCard(group) { vm.joinGroup(group.groupId) }
                                }
                            }
                        }
                    }
    
                    // 6. Upcoming Events
                    if (upcomingEvents.isNotEmpty()) {
                        item {
                            SectionHeader("Upcoming Events", null) {}
                            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                upcomingEvents.take(3).forEach { event ->
                                    EventCard(event) { vm.rsvpToEvent(event.eventId, "GOING") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSearchResultItem(user: com.rio.rostry.data.database.entity.UserEntity, onChat: () -> Unit) {
    ListItem(
        headlineContent = { Text(user.fullName ?: "Unknown User", fontWeight = FontWeight.Bold) },
        supportingContent = { 
            val location = if (user.farmCity != null && user.farmCountry != null) "${user.farmCity}, ${user.farmCountry}" else user.address ?: "No location"
            Text(location, maxLines = 1, overflow = TextOverflow.Ellipsis) 
        },
        leadingContent = {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text((user.fullName ?: "U").take(1).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
        },
        trailingContent = {
            Button(onClick = onChat, contentPadding = PaddingValues(horizontal = 12.dp)) {
                Icon(Icons.Outlined.Chat, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Chat")
            }
        },
        modifier = Modifier.clickable { onChat() } 
    )
}

@Composable
private fun GroupSearchResultItem(group: GroupEntity, onJoin: () -> Unit) {
    ListItem(
        headlineContent = { Text(group.name, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(group.description ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = {
             Icon(Icons.Default.Group, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.tertiary)
        },
        trailingContent = {
            OutlinedButton(onClick = onJoin) {
                Text("Join")
            }
        }
    )
}

@Composable
private fun PostCard(post: com.rio.rostry.data.database.entity.PostEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("A", fontWeight = FontWeight.Bold) // Placeholder for Author Initial
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Community Member", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(getRelativeTime(post.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(post.text ?: "", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ThumbUp, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text("0", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.Comment, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text("0", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun QuickAccessSection(
    onGroups: () -> Unit,
    onExperts: () -> Unit,
    onNews: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAccessCard(
            title = "Groups",
            icon = Icons.Default.Group,
            color = MaterialTheme.colorScheme.primaryContainer,
            onClick = onGroups,
            modifier = Modifier.weight(1f)
        )
        QuickAccessCard(
            title = "Experts",
            icon = Icons.Default.School,
            color = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onExperts,
            modifier = Modifier.weight(1f)
        )
        QuickAccessCard(
            title = "News",
            icon = Icons.Default.Newspaper,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = onNews,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (action != null) {
            TextButton(onClick = onAction) {
                Text(action)
            }
        }
    }
}

@Composable
private fun DiscussionItem(thread: CommunityEngagementService.ThreadPreview, onClick: (String) -> Unit) {
    Column(modifier = Modifier.clickable { onClick(thread.threadId) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(thread.title.take(1).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        thread.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(getRelativeTime(thread.lastMessageAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    thread.snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                thread.context?.type?.let { type ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(type.replace("_", " ").lowercase().capitalize()) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
        Divider(modifier = Modifier.padding(start = 80.dp))
    }
}

@Composable
private fun ExpertCard(expert: CommunityEngagementService.ExpertInfo, onBook: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(expert.name.take(1), style = MaterialTheme.typography.headlineSmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(expert.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                expert.specialties.firstOrNull() ?: "General",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                Text(" ${expert.rating}", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onBook, modifier = Modifier.fillMaxWidth(), enabled = expert.availability) {
                Text("Book")
            }
        }
    }
}

@Composable
private fun GroupCard(group: GroupEntity, onJoin: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(group.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                group.description ?: "No description",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                minLines = 2
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onJoin, modifier = Modifier.fillMaxWidth()) {
                Text("Join Group")
            }
        }
    }
}

@Composable
private fun EventCard(event: EventEntity, onRsvp: () -> Unit) {
    val date = java.util.Date(event.startTime)
    val monthFormat = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
    val dayFormat = java.text.SimpleDateFormat("dd", java.util.Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(monthFormat.format(date).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(dayFormat.format(date), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(event.location ?: "Online", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onRsvp) {
                Text("RSVP")
            }
        }
    }
}

@Composable
private fun EmptyStateCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private fun getRelativeTime(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}
