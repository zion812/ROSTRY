package com.rio.rostry.ui.enthusiast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedTextField
import android.net.Uri
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import com.rio.rostry.ui.social.VideoPlayer

@Composable
fun EnthusiastExploreTabs(
    onOpenProduct: (String) -> Unit,
    onOpenEvent: (String) -> Unit,
    onShare: (String) -> Unit,
    viewModel: EnthusiastExploreTabsViewModel = hiltViewModel()
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Discover", "Products", "Events", "Showcase")
    val state by viewModel.state.collectAsState()
    
    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { idx, title ->
                Tab(selected = tabIndex == idx, onClick = { tabIndex = idx; viewModel.loadTab(idx) }, text = { Text(title) })
            }
        }
        when (tabIndex) {
            0 -> DiscoverTab(
                farmers = state.nearbyFarmers,
                learning = state.learningModules,
                onFarmerClick = { /* Navigate to profile */ },
                onModuleClick = { /* Open content */ }
            )
            1 -> ProductsTab(state.products, onOpenProduct, onShare, state.loading)
            2 -> EventsTab(state.events, state.rsvps, onOpenEvent, viewModel::rsvpToEvent, viewModel::createEvent, state.loading)
            3 -> ShowcaseTab(state.showcasePosts, onShare, viewModel::likePost, viewModel::commentOnPost, viewModel::createShowcase, state.loading)
        }
    }
}

@Composable
private fun DiscoverTab(
    farmers: List<com.rio.rostry.data.database.entity.UserEntity>,
    learning: List<com.rio.rostry.ui.enthusiast.explore.LearningModule>,
    onFarmerClick: (String) -> Unit,
    onModuleClick: (String) -> Unit
) {
    if (farmers.isEmpty() && learning.isEmpty()) {
        com.rio.rostry.ui.components.EmptyState(
            title = "Nothing to discover yet",
            subtitle = "Check back soon for nearby farmers and content",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            item { 
                com.rio.rostry.ui.enthusiast.explore.NearbyFarmersSection(farmers, onFarmerClick) 
            }
            item {
                com.rio.rostry.ui.enthusiast.explore.LearningContentSection(learning, onModuleClick)
            }
        }
    }
}

@Composable
private fun ProductsTab(products: List<com.rio.rostry.data.database.entity.ProductEntity>, onOpenProduct: (String) -> Unit, onShare: (String) -> Unit, loading: Boolean) {
    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (loading) {
            com.rio.rostry.ui.components.LoadingOverlay()
        }
        if (!loading && products.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No products",
                subtitle = "New items will appear here",
                modifier = Modifier.fillMaxSize()
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products) { p ->
                ElevatedCard(onClick = { onOpenProduct(p.productId) }) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Text("$${p.price}")
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = { onShare(p.productId) }) { Text("Share") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsTab(
    events: List<com.rio.rostry.data.database.entity.EventEntity>,
    rsvps: List<com.rio.rostry.data.database.entity.EventRsvpEntity>,
    onOpenEvent: (String) -> Unit,
    onRsvp: (String, String) -> Unit,
    onCreate: (title: String, startTime: Long, endTime: Long?, location: String?, description: String?, bannerLocalUri: String?) -> Unit,
    loading: Boolean
) {
    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Simple inline creation UI
        var title by rememberSaveable { mutableStateOf("") }
        var location by rememberSaveable { mutableStateOf("") }
        var description by rememberSaveable { mutableStateOf("") }
        var bannerUri by rememberSaveable { mutableStateOf("") }
        val pickBanner = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            bannerUri = uri?.toString() ?: ""
        }
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { pickBanner.launch("image/*") }) { Text(if (bannerUri.isBlank()) "Pick Banner" else "Change Banner") }
            Button(onClick = {
                val now = System.currentTimeMillis()
                onCreate(title.trim(), now + 60 * 60 * 1000, now + 2 * 60 * 60 * 1000, location.ifBlank { null }, description.ifBlank { null }, bannerUri.ifBlank { null })
                title = ""; location = ""; description = ""; bannerUri = ""
            }, enabled = title.isNotBlank()) { Text("Create Event") }
        }
        if (loading) {
            com.rio.rostry.ui.components.LoadingOverlay()
        }
        if (!loading && events.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No events",
                subtitle = "Create one or check back later",
                modifier = Modifier.fillMaxSize()
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(events) { ev ->
                ElevatedCard(onClick = { onOpenEvent(ev.eventId) }) {
                    Column(Modifier.padding(12.dp)) {
                        Text(ev.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Text("Date: ${java.text.SimpleDateFormat("MMM d, yyyy").format(java.util.Date(ev.startTime))}")
                        Text("Location: ${ev.location ?: "TBD"}")
                        val userRsvp = rsvps.find { it.eventId == ev.eventId }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = { onRsvp(ev.eventId, "GOING") }, enabled = userRsvp?.status != "GOING") { Text("Going") }
                            OutlinedButton(onClick = { onRsvp(ev.eventId, "MAYBE") }, enabled = userRsvp?.status != "MAYBE") { Text("Maybe") }
                            OutlinedButton(onClick = { onRsvp(ev.eventId, "NOT_GOING") }, enabled = userRsvp?.status != "NOT_GOING") { Text("Not Going") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowcaseTab(
    posts: List<com.rio.rostry.data.database.entity.PostEntity>,
    onShare: (String) -> Unit,
    onLike: (String) -> Unit,
    onComment: (String, String) -> Unit,
    onCreate: (caption: String?, localUri: String, linkedProductId: String?) -> Unit,
    loading: Boolean
) {
    Column(Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Simple inline showcase creation
        var caption by rememberSaveable { mutableStateOf("") }
        var mediaUri by rememberSaveable { mutableStateOf("") }
        val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            mediaUri = uri?.toString() ?: ""
        }
        OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Caption (optional)") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { pickMedia.launch("*/*") }) { Text(if (mediaUri.isBlank()) "Pick Media" else "Change Media") }
            Button(onClick = {
                onCreate(caption.ifBlank { null }, mediaUri, null)
                caption = ""; mediaUri = ""
            }, enabled = mediaUri.isNotBlank()) { Text("Create Showcase Post") }
        }
        if (loading) {
            com.rio.rostry.ui.components.LoadingOverlay()
        }
        if (!loading && posts.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No showcase posts",
                subtitle = "Share your work to get started",
                modifier = Modifier.fillMaxSize()
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(posts) { post ->
                ElevatedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text(post.text ?: "", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                        val url = post.mediaUrl
                        if (!url.isNullOrBlank()) {
                            val isVideo = url.endsWith(".mp4", true) || url.endsWith(".m3u8", true) || url.contains("/video", true)
                            if (isVideo) {
                                VideoPlayer(modifier = Modifier.fillMaxWidth(), url = url)
                            } else {
                                Image(painter = rememberAsyncImagePainter(url), contentDescription = null, modifier = Modifier.fillMaxWidth())
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = { onLike(post.postId) }) { Text("Like") }
                            OutlinedButton(onClick = { onComment(post.postId, "Great!") }) { Text("Comment") }
                            OutlinedButton(onClick = { onShare(post.postId) }) { Text("Share") }
                        }
                    }
                }
            }
        }
    }
}
