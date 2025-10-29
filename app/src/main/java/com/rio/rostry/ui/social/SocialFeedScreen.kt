package com.rio.rostry.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.PostEntity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Share

@Composable
fun SocialFeedScreen(
    onOpenThread: (String) -> Unit,
    onOpenGroups: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenExpert: () -> Unit,
    vm: SocialFeedViewModel = hiltViewModel(),
) {
    // NOTE: Repo actions should be exposed via a dedicated VM; for now show feed only
    val feed = vm.feed().collectAsLazyPagingItems()
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(onClick = onOpenGroups, modifier = Modifier.weight(1f)) { Text("Groups") }
            FilledTonalButton(onClick = onOpenEvents, modifier = Modifier.weight(1f)) { Text("Events") }
            FilledTonalButton(onClick = onOpenExpert, modifier = Modifier.weight(1f)) { Text("Experts") }
        }
        val isRefreshing = feed.loadState.refresh is LoadState.Loading
        val refreshError = (feed.loadState.refresh as? LoadState.Error)?.error

        when {
            isRefreshing && feed.itemCount == 0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) { idx ->
                        com.rio.rostry.ui.components.PostCardSkeleton(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            refreshError != null && feed.itemCount == 0 -> {
                com.rio.rostry.ui.components.ErrorState(
                    error = refreshError.localizedMessage ?: "Failed to load feed",
                    retryAction = { feed.retry() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            feed.itemCount == 0 -> {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No posts yet",
                    subtitle = "Follow people and groups to see updates here",
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                )
            }
            else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    count = feed.itemCount,
                    key = { index ->
                        val p = feed[index]
                        // Prefer stable postId for better list diffing
                        p?.postId ?: "post-$index"
                    }
                ) { index ->
                    val post = feed[index]
                    if (post != null) PostCard(post)
                }
                // Append indicators
                when (val append = feed.loadState.append) {
                    is LoadState.Loading -> {
                        items(2) { _ ->
                            com.rio.rostry.ui.components.PostCardSkeleton(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            com.rio.rostry.ui.components.ErrorState(
                                error = append.error.localizedMessage ?: "Failed to load more",
                                retryAction = { feed.retry() }
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
        }
    }
}

@Composable
private fun PostCard(post: PostEntity, vm: SocialFeedViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isAuthenticated = vm.isAuthenticated
    var showCommentDialog by remember { mutableStateOf(false) }
    var showLoginPrompt by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var liked by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(text = post.authorId.take(10), style = MaterialTheme.typography.labelSmall)
            if (!post.text.isNullOrBlank()) {
                Text(text = post.text!!, style = MaterialTheme.typography.bodyMedium, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
            if (!post.mediaUrl.isNullOrBlank()) {
                val isVideo = (post.type.equals("VIDEO", ignoreCase = true)) || post.mediaUrl.endsWith(".mp4", true)
                if (isVideo) {
                    VideoPlayer(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), url = post.mediaUrl!!)
                } else {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .heightIn(min = 180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = {
                        if (isAuthenticated) {
                            scope.launch {
                                if (!liked) vm.like(post.postId) else vm.unlike(post.postId)
                                liked = !liked
                            }
                        } else {
                            showLoginPrompt = true
                        }
                    },
                    enabled = isAuthenticated
                ) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = if (liked) "Unlike" else "Like")
                    Text(if (liked) "Unlike" else "Like", modifier = Modifier.padding(start = 6.dp))
                }
                TextButton(
                    onClick = {
                        if (isAuthenticated) {
                            showCommentDialog = true
                        } else {
                            showLoginPrompt = true
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = isAuthenticated
                ) {
                    Icon(imageVector = Icons.Filled.ChatBubble, contentDescription = "Comment")
                    Text("Comment", modifier = Modifier.padding(start = 6.dp))
                }
                TextButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.text ?: post.mediaUrl ?: "Check out this post on ROSTRY")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
                }, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                    Text("Share", modifier = Modifier.padding(start = 6.dp))
                }
            }

            if (showCommentDialog) {
                AlertDialog(
                    onDismissRequest = { showCommentDialog = false },
                    title = { Text("Add Comment") },
                    text = {
                        androidx.compose.material3.OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 4
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                if (commentText.isNotBlank()) vm.addComment(post.postId, commentText)
                                commentText = ""
                                showCommentDialog = false
                            }
                        }) { Text("Post") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCommentDialog = false }) { Text("Cancel") }
                    }
                )
            }

            if (showLoginPrompt) {
                AlertDialog(
                    onDismissRequest = { showLoginPrompt = false },
                    title = { Text("Authentication Required") },
                    text = { Text("Please log in to interact with posts. You need to be authenticated to like, comment, or perform other social actions.") },
                    confirmButton = {
                        TextButton(onClick = { showLoginPrompt = false }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
