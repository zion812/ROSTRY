package com.rio.rostry.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
    val discoveryVm: ContentDiscoveryViewModel = hiltViewModel()
    val trending by discoveryVm.trending.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onOpenGroups) { Text("Groups") }
            Button(onClick = onOpenEvents) { Text("Events") }
            Button(onClick = onOpenExpert) { Text("Experts") }
        }
        if (trending.topHashtags.isNotEmpty()) {
            TrendingHashtagsRow(tags = trending.topHashtags.map { it.first })
            Spacer(Modifier.height(4.dp))
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(feed.itemCount) { index ->
                val post = feed[index]
                if (post != null) PostCard(post)
            }
        }
    }
}

@Composable
private fun TrendingHashtagsRow(tags: List<String>) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(text = "Trending", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.take(6).forEach { tag ->
                    androidx.compose.material3.AssistChip(onClick = { /* TODO: hook to discovery screen */ }, label = { Text(tag) })
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: PostEntity, vm: SocialFeedViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showCommentDialog by remember { mutableStateOf(false) }
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
                // Persisted reactions using ReactionDao via ViewModel
                val reactionTypes = listOf("LIKE" to "Like", "CARE" to "Care", "CROWN" to "Crown", "HATCH" to "Hatch", "FEED" to "Feed")
                val current = vm.userReaction(post.postId, "me").collectAsStateWithLifecycle(initialValue = null).value
                val counts = vm.reactionCounts(post.postId).collectAsStateWithLifecycle(initialValue = emptyList()).value
                val countMap = counts.associate { it.type to it.c }
                reactionTypes.forEach { (type, label) ->
                    val selected = current?.type == type
                    Button(
                        onClick = {
                            scope.launch {
                                if (selected) vm.clearReaction(post.postId, "me") else vm.setReaction(post.postId, "me", type)
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) { Text(text = (if (selected) "✔ $label" else label) + " ${countMap[type] ?: 0}") }
                }
                Button(onClick = { showCommentDialog = true }, modifier = Modifier.padding(start = 8.dp)) { Text("Comment") }
                Button(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.text ?: post.mediaUrl ?: "Check out this post on ROSTRY")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
                }, modifier = Modifier.padding(start = 8.dp)) { Text("Share") }
                var showReport by remember { mutableStateOf(false) }
                Button(onClick = { showReport = true }, modifier = Modifier.padding(start = 8.dp)) { Text("Report") }

                if (showReport) {
                    var reason by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = { showReport = false },
                        title = { Text("Report Post") },
                        text = {
                            androidx.compose.material3.OutlinedTextField(
                                value = reason,
                                onValueChange = { reason = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                label = { Text("Reason") }
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    if (reason.isNotBlank()) vm.reportPost(post.postId, "me", reason)
                                    showReport = false
                                }
                            }) { Text("Submit") }
                        },
                        dismissButton = { TextButton(onClick = { showReport = false }) { Text("Cancel") } }
                    )
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
                                if (commentText.isNotBlank()) vm.addComment(post.postId, "me", commentText)
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

            // Live comments section (expandable) with nested replies
            var expanded by remember { mutableStateOf(true) }
            val commentsFlow = vm.comments(post.postId)
            val comments = commentsFlow.collectAsStateWithLifecycle(initialValue = emptyList()).value
            val topLevel = comments.filter { it.parentCommentId == null }
            val childrenByParent = comments.filter { it.parentCommentId != null }.groupBy { it.parentCommentId }
            androidx.compose.material3.Divider(modifier = Modifier.padding(top = 8.dp))
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Comments (${comments.size})",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Hide" else "Show") }
            }
            if (expanded) {
                val toShow = topLevel.takeLast(3)
                toShow.forEach { c ->
                    // Top-level comment
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${c.authorId.take(8)}: ${c.text}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            commentText = "@${c.authorId.take(12)} "
                            showCommentDialog = true
                        }) { Text("Reply") }
                    }
                    // Children (nested)
                    val kids = childrenByParent[c.commentId].orEmpty()
                    kids.forEach { child ->
                        Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${child.authorId.take(8)}: ${child.text}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                commentText = "@${child.authorId.take(12)} "
                                showCommentDialog = true
                            }) { Text("Reply") }
                        }
                    }
                }
                if (topLevel.size > toShow.size) {
                    Text(text = "View more...", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}
