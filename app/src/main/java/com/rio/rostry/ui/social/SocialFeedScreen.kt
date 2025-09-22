package com.rio.rostry.ui.social

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.PostEntity

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
        LazyColumn(Modifier.fillMaxSize()) {
            items(feed, key = { it.postId }) { post ->
                if (post != null) PostCard(post)
            }
        }
    }
}

@Composable
private fun PostCard(post: PostEntity) {
    Card(Modifier.fillMaxWidth().padding(12.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(text = post.authorId.take(10), style = MaterialTheme.typography.labelSmall)
            if (!post.text.isNullOrBlank()) {
                Text(text = post.text!!, style = MaterialTheme.typography.bodyMedium, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
            if (!post.mediaUrl.isNullOrBlank()) {
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* like */ }) { /* like icon */ }
                IconButton(onClick = { /* comment */ }) { /* comment icon */ }
                IconButton(onClick = { /* share */ }) { /* share icon */ }
            }
        }
    }
}
