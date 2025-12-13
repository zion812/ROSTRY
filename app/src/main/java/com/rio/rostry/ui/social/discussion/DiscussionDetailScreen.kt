package com.rio.rostry.ui.social.discussion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.ui.social.SocialFeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionDetailScreen(
    postId: String,
    onBack: () -> Unit,
    onProfileClick: (String) -> Unit,
    vm: SocialFeedViewModel = hiltViewModel()
) {
    // Note: Ideally we'd have a specific DiscussionViewModel, but reusing SocialFeedViewModel for simplicity
    // as it has getReplies and like/unlike logic.
    
    // We need to fetch the main post. For now, we might need to pass the post object or fetch it.
    // Assuming we fetch it or it's passed. Let's assume we fetch it by ID (need to add getPost to VM/Repo if not there).
    // For MVP, let's assume the VM can provide it or we just show replies if we can't fetch the parent easily without a new call.
    // Actually, SocialRepository has getById but it's suspend.
    
    val replies by vm.getReplies(postId).collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thread") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Quick reply bar
            Surface(tonalElevation = 2.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Post your reply") }
                    )
                    TextButton(
                        onClick = {
                            if (text.isNotBlank()) {
                                // vm.reply(postId, text) // Add reply logic to VM
                                text = ""
                            }
                        }
                    ) {
                        Text("Reply")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            // Main Post (Placeholder if we don't have the entity passed)
            item {
                // In a real app, we'd fetch the post details here.
                // For now, showing a placeholder or we'd need to refactor navigation to pass the Post object.
                Text("Main Post Content Loading...", Modifier.padding(16.dp)) 
            }
            
            items(replies) { reply ->
                DiscussionItem(post = reply, onProfileClick = onProfileClick)
                Divider()
            }
        }
    }
}

@Composable
fun DiscussionItem(
    post: PostEntity,
    onProfileClick: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to detail of this reply? */ }
            .padding(16.dp)
    ) {
        Row {
            // Avatar
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onProfileClick(post.authorId) }
            ) {
                // Image
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorId.take(10), // Name
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "@${post.authorId.take(5)}", // Handle
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "2h", // Time
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = post.text ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(Modifier.height(12.dp))
                
                // Actions
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActionIcon(Icons.Default.ChatBubbleOutline, "12")
                    ActionIcon(Icons.Default.Repeat, "5")
                    ActionIcon(Icons.Default.FavoriteBorder, "34")
                    ActionIcon(Icons.Default.Share, "")
                }
            }
        }
    }
}

@Composable
fun ActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (count.isNotEmpty()) {
            Spacer(Modifier.width(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
