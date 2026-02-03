package com.rio.rostry.ui.social.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.PostEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialProfileScreen(
    userId: String?,
    onBack: () -> Unit,
    onPostClick: (String) -> Unit,
    onMessage: (String) -> Unit = {}, // Navigate to messaging with userId
    onSettingsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit,
    vm: SocialProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(userId) {
        vm.bind(userId)
    }

    val user by vm.user.collectAsState()
    val posts = vm.posts.collectAsLazyPagingItems()
    val followers by vm.followersCount.collectAsState(initial = 0)
    val following by vm.followingCount.collectAsState(initial = 0)
    val postsCount by vm.postsCount.collectAsState(initial = 0)

    var selectedTab by remember { mutableIntStateOf(0) }

    val isOwnProfile by vm.isOwnProfile.collectAsState()
    val canEditProfile by vm.canEditProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user?.fullName ?: "Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (userId == null || canEditProfile) { // Current user OR Admin
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Header
            user?.let { u ->
                ProfileHeader(
                    user = u,
                    postsCount = postsCount,
                    followersCount = followers,
                    followingCount = following,
                    isCurrentUser = isOwnProfile,
                    canEdit = canEditProfile,
                    onEditProfile = onEditProfileClick,
                    onFollow = { vm.follow() },
                    onMessage = { onMessage(u.userId) }
                )
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.GridOn, contentDescription = "Posts") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.PersonPin, contentDescription = "Tagged") }
                )
            }

            // Content
            if (selectedTab == 0) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(posts.itemCount) { index ->
                        val post = posts[index]
                        if (post != null) {
                            PostThumbnail(post = post, onClick = { onPostClick(post.postId) })
                        }
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tagged posts yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: com.rio.rostry.data.database.entity.UserEntity,
    postsCount: Int,
    followersCount: Int,
    followingCount: Int,
    isCurrentUser: Boolean,
    canEdit: Boolean, // New parameter
    onEditProfile: () -> Unit,
    onFollow: () -> Unit,
    onMessage: () -> Unit = {}
) {
    Column(Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar
            AsyncImage(
                model = user.profilePictureUrl ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(24.dp))

            // Stats
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Posts", postsCount)
                StatItem("Followers", followersCount)
                StatItem("Following", followingCount)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Bio
        Text(user.fullName ?: "User", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (!user.bio.isNullOrBlank()) {
            Text(user.bio!!, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(16.dp))

        // Actions
        // "Edit Profile" if allow (Own or Admin)
        if (canEdit) {
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text(if (isCurrentUser) "Edit Profile" else "Edit Profile (Admin)")
            }
            
            // If admin but not me, also show message/follow options below?
            if (!isCurrentUser) {
                 Spacer(Modifier.height(8.dp))
                 Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onFollow,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Follow")
                    }
                    Button(
                        onClick = onMessage,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text("Message")
                    }
                }
            }
        } else {
            // Not editable (Visitor view)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onFollow,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Follow")
                }
                Button(
                    onClick = onMessage,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("Message")
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PostThumbnail(post: PostEntity, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
    ) {
        if (!post.mediaUrl.isNullOrBlank()) {
            AsyncImage(
                model = post.mediaUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Text post fallback
            Text(
                text = post.text ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp),
                maxLines = 4
            )
        }
    }
}
