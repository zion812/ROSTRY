package com.rio.rostry.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHubScreen(
    userType: UserType,
    onNavigateToThread: (String) -> Unit,
    onNavigateToGroup: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToExpert: (String) -> Unit,
    onNavigateToPost: (String) -> Unit,
    vm: CommunityHubViewModel = hiltViewModel()
) {
    val selectedTab by vm.selectedTab.collectAsState()
    val activeThreads by vm.activeThreads.collectAsState()
    val suggestedConnections by vm.suggestedConnections.collectAsState()
    val suggestedGroups by vm.suggestedGroups.collectAsState()
    val upcomingEvents by vm.upcomingEvents.collectAsState()
    val availableExperts by vm.availableExperts.collectAsState()
    val trendingPosts by vm.trendingPosts.collectAsState()
    val userGroups by vm.userGroups.collectAsState()
    val unreadCount by vm.unreadCount.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    
    var showSearch by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Community") },
                    actions = {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { showFilter = !showFilter }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge { Text("$unreadCount") }
                                }
                            }
                        ) {
                            IconButton(onClick = { /* Notifications */ }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                    }
                )
                if (showSearch) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClose = { showSearch = false; searchQuery = "" }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* New Message */ }) {
                Icon(Icons.Default.Add, contentDescription = "New Message")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { vm.selectTab(0) },
                    text = { Text("Messages") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { vm.selectTab(1) },
                    text = { Text("Discover") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { vm.selectTab(2) },
                    text = { Text("Feed") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { vm.selectTab(3) },
                    text = { Text("My Groups") }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> MessagesTab(
                    threads = activeThreads.filter { thread ->
                        if (searchQuery.isBlank()) true
                        else thread.threadId.contains(searchQuery, ignoreCase = true)
                    },
                    onThreadClick = onNavigateToThread,
                    isLoading = isLoading
                )
                1 -> DiscoverTab(
                    connections = suggestedConnections,
                    groups = suggestedGroups,
                    events = upcomingEvents,
                    experts = availableExperts,
                    userType = userType,
                    onJoinGroup = vm::joinGroup,
                    onRsvpEvent = vm::rsvpToEvent,
                    onBookExpert = vm::bookExpert,
                    onMessageUser = { userId -> vm.startThread(userId, "GENERAL", null) },
                    onNavigateToGroup = onNavigateToGroup,
                    onNavigateToEvent = onNavigateToEvent,
                    onNavigateToExpert = onNavigateToExpert
                )
                2 -> FeedTab(
                    posts = trendingPosts,
                    onPostClick = onNavigateToPost
                )
                3 -> MyGroupsTab(
                    groups = userGroups,
                    onGroupClick = onNavigateToGroup,
                    onLeaveGroup = vm::leaveGroup
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        placeholder = { Text("Search messages, groups, events...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close search")
            }
        },
        singleLine = true
    )
}

@Composable
private fun MessagesTab(
    threads: List<com.rio.rostry.data.repository.social.MessagingRepository.ThreadWithMetadata>,
    onThreadClick: (String) -> Unit,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (threads.isEmpty()) {
        EmptyMessagesState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = threads,
                key = { it.threadId }
            ) { threadData ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onThreadClick(threadData.threadId) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Thread ${threadData.threadId.take(8)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            threadData.metadata?.context?.type?.let { contextType ->
                                Badge {
                                    Text(
                                        when (contextType) {
                                            "PRODUCT_INQUIRY" -> "Product"
                                            "EXPERT_CONSULT" -> "Expert"
                                            "BREEDING_DISCUSSION" -> "Breeding"
                                            else -> "Chat"
                                        }
                                    )
                                }
                            }
                        }
                        if (threadData.unreadCount > 0) {
                            Spacer(Modifier.height(4.dp))
                            Badge { Text("${threadData.unreadCount} unread") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverTab(
    connections: List<com.rio.rostry.community.CommunityEngagementService.ConnectionSuggestion>,
    groups: List<com.rio.rostry.data.database.entity.GroupEntity>,
    events: List<com.rio.rostry.data.database.entity.EventEntity>,
    experts: List<com.rio.rostry.community.CommunityEngagementService.ExpertInfo>,
    userType: UserType,
    onJoinGroup: (String) -> Unit,
    onRsvpEvent: (String, String) -> Unit,
    onBookExpert: (String, String, Long) -> Unit,
    onMessageUser: (String) -> Unit,
    onNavigateToGroup: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToExpert: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Suggested Connections
        if (connections.isNotEmpty()) {
            item {
                Text(
                    "Suggested Connections",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = connections.take(5), key = { it.userId }) { connection ->
                        Card(modifier = Modifier.width(200.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(connection.name, style = MaterialTheme.typography.titleSmall)
                                Text("Match: ${(connection.matchScore * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                Text(connection.reason, style = MaterialTheme.typography.bodySmall)
                                Button(
                                    onClick = { onMessageUser(connection.userId) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Message")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recommended Groups
        if (groups.isNotEmpty()) {
            item {
                Text(
                    "Recommended Groups",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(items = groups.take(5), key = { it.groupId }) { group ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToGroup(group.groupId) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(group.name, style = MaterialTheme.typography.titleSmall)
                            group.description?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Button(onClick = { onJoinGroup(group.groupId) }) {
                            Text("Join")
                        }
                    }
                }
            }
        }

        // Upcoming Events
        if (events.isNotEmpty()) {
            item {
                Text(
                    "Upcoming Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(items = events.take(3), key = { it.eventId }) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToEvent(event.eventId) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(event.title, style = MaterialTheme.typography.titleSmall)
                            event.location?.let {
                                Text("Location: $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Button(onClick = { onRsvpEvent(event.eventId, "GOING") }) {
                            Text("RSVP")
                        }
                    }
                }
            }
        }

        // Available Experts
        if (experts.isNotEmpty()) {
            item {
                Text(
                    "Available Experts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(items = experts.take(3), key = { it.userId }) { expert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToExpert(expert.userId) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(expert.name, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Specialties: ${expert.specialties.joinToString()}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text("Rating: ${expert.rating}/5.0", style = MaterialTheme.typography.bodySmall)
                        }
                        Button(
                            onClick = {
                                onBookExpert(expert.userId, expert.specialties.firstOrNull() ?: "", System.currentTimeMillis())
                            },
                            enabled = expert.availability
                        ) {
                            Text("Book")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMessagesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Message,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No conversations yet",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Start connecting with farmers, enthusiasts, and experts",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = { /* Navigate to discover */ }) {
            Text("Browse Discover")
        }
    }
}

@Composable
private fun FeedTab(
    posts: List<com.rio.rostry.data.database.entity.PostEntity>,
    onPostClick: (String) -> Unit
) {
    if (posts.isEmpty()) {
        EmptyFeedState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = posts, key = { it.postId }) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPostClick(post.postId) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(post.type, style = MaterialTheme.typography.labelSmall)
                        post.text?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFeedState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "📰",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No trending posts",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Be the first to share something!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = { /* Create post */ }) {
            Text("Create Post")
        }
    }
}

@Composable
private fun MyGroupsTab(
    groups: List<com.rio.rostry.data.database.entity.GroupEntity>,
    onGroupClick: (String) -> Unit,
    onLeaveGroup: (String) -> Unit
) {
    if (groups.isEmpty()) {
        EmptyGroupsState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = groups, key = { it.groupId }) { group ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onGroupClick(group.groupId) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(group.name, style = MaterialTheme.typography.titleMedium)
                            group.description?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        OutlinedButton(onClick = { onLeaveGroup(group.groupId) }) {
                            Text("Leave")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGroupsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Groups,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "You haven't joined any groups",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Join groups to connect with like-minded people",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = { /* Navigate to discover */ }) {
            Text("Discover Groups")
        }
    }
}
