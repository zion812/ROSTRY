package com.rio.rostry.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.PostEntity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

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
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onOpenGroups) { Text("Groups") }
            Button(onClick = onOpenEvents) { Text("Events") }
            Button(onClick = onOpenExpert) { Text("Experts") }
        }
        if (feed.itemCount == 0) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No posts yet",
                subtitle = "Follow people and groups to see updates here",
                modifier = Modifier.fillMaxSize().padding(24.dp)
            )
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(feed.itemCount) { index ->
                    val post = feed[index]
                    if (post != null) PostCard(post)
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
    Card(Modifier.fillMaxWidth().padding(12.dp)) {
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
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
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
                ) { Text(if (liked) "Unlike" else "Like") }
                Button(
                    onClick = {
                        if (isAuthenticated) {
                            showCommentDialog = true
                        } else {
                            showLoginPrompt = true
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = isAuthenticated
                ) { Text("Comment") }
                Button(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.text ?: post.mediaUrl ?: "Check out this post on ROSTRY")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
                }, modifier = Modifier.padding(start = 8.dp)) { Text("Share") }
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
