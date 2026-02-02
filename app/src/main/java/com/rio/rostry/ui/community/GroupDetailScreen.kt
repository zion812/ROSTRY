package com.rio.rostry.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor() : ViewModel() {
    
    data class GroupMember(val name: String, val role: String, val avatarInitial: Char)
    data class GroupPost(val author: String, val content: String, val timestamp: Date, val likes: Int)
    
    data class UiState(
        val isLoading: Boolean = true,
        val groupId: String = "",
        val groupName: String = "",
        val description: String = "",
        val memberCount: Int = 0,
        val postCount: Int = 0,
        val category: String = "",
        val isJoined: Boolean = false,
        val members: List<GroupMember> = emptyList(),
        val recentPosts: List<GroupPost> = emptyList(),
        val error: String? = null
    )
    
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    
    fun loadGroup(groupId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, groupId = groupId) }
            delay(500)
            
            // Mock data - would come from repository
            _state.update { it.copy(
                isLoading = false,
                groupName = "Kadaknath Breeders India",
                description = "A community for Kadaknath chicken breeders to share tips, discuss genetics, and connect with fellow enthusiasts.",
                memberCount = 256,
                postCount = 1847,
                category = "Breed Specific",
                isJoined = false,
                members = listOf(
                    GroupMember("Rajesh Sharma", "Admin", 'R'),
                    GroupMember("Priya Patel", "Moderator", 'P'),
                    GroupMember("Amit Kumar", "Member", 'A'),
                    GroupMember("Sunita Devi", "Member", 'S')
                ),
                recentPosts = listOf(
                    GroupPost("Rajesh Sharma", "Just hatched my first batch of pure Kadaknath! 85% hatch rate 🎉", Date(), 24),
                    GroupPost("Amit Kumar", "What's the ideal temperature for Kadaknath brooding?", Date(System.currentTimeMillis() - 3600000), 12),
                    GroupPost("Priya Patel", "Sharing my feeding schedule that improved egg production by 20%", Date(System.currentTimeMillis() - 86400000), 45)
                )
            ) }
        }
    }
    
    fun toggleJoin() {
        _state.update { it.copy(isJoined = !it.isJoined) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    
    LaunchedEffect(groupId) {
        viewModel.loadGroup(groupId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.groupName.ifEmpty { "Group" }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group Header
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(state.groupName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text(state.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(state.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatChip(Icons.Default.People, "${state.memberCount} members")
                                StatChip(Icons.Default.Article, "${state.postCount} posts")
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.toggleJoin() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = if (state.isJoined) ButtonDefaults.outlinedButtonColors() else ButtonDefaults.buttonColors()
                            ) {
                                Icon(if (state.isJoined) Icons.Default.Check else Icons.Default.Add, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (state.isJoined) "Joined" else "Join Group")
                            }
                        }
                    }
                }
                
                // Members Section
                item {
                    Text("Members", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.members.take(4).forEach { member ->
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(member.avatarInitial.toString(), fontWeight = FontWeight.Bold)
                                    Text(member.name.split(" ").first(), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                        if (state.memberCount > 4) {
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                                Box(Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text("+${state.memberCount - 4}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                // Recent Posts
                item {
                    Text("Recent Posts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(state.recentPosts) { post ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(post.author, fontWeight = FontWeight.Medium)
                                Text(dateFormatter.format(post.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(post.content, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ThumbUp, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(4.dp))
                                Text("${post.likes}", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelSmall)
        }
    }
}
