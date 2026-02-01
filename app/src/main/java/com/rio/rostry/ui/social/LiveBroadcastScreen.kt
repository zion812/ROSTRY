package com.rio.rostry.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Data class for chat messages in live broadcast.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystem: Boolean = false
)

/**
 * Audience type for the broadcast.
 */
enum class AudienceType(val label: String) {
    PUBLIC("Public"),
    FOLLOWERS("Followers Only"),
    VERIFIED("Verified Users")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveBroadcastScreen(
    onBack: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var isLive by remember { mutableStateOf(false) }
    var viewers by remember { mutableIntStateOf(0) }
    var selectedAudience by rememberSaveable { mutableStateOf(AudienceType.PUBLIC) }
    var chatInput by remember { mutableStateOf("") }
    var duration by remember { mutableIntStateOf(0) }
    
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    val chatListState = rememberLazyListState()
    
    // Simulate viewer count and chat activity when live
    LaunchedEffect(isLive) {
        if (isLive) {
            // Add system message when going live
            chatMessages.add(
                ChatMessage(
                    username = "System",
                    message = "🔴 Broadcast started! Welcome viewers.",
                    isSystem = true
                )
            )
            
            // Simulate viewers joining
            while (isLive) {
                delay(3000)
                if (viewers < 50) {
                    viewers += (1..5).random()
                    if ((1..3).random() == 1) {
                        chatMessages.add(
                            ChatMessage(
                                username = "Viewer${viewers}",
                                message = listOf(
                                    "Great stream! 🔥",
                                    "Love your farm setup!",
                                    "How many birds do you have?",
                                    "What breed is that?",
                                    "Amazing collection! 👏",
                                    "Keep up the good work!",
                                    "First time here, loving it!"
                                ).random()
                            )
                        )
                    }
                }
                duration++
            }
        } else {
            viewers = 0
            duration = 0
        }
    }
    
    // Auto-scroll chat to bottom
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Live Broadcasting")
                        if (isLive) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color.Red, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "LIVE",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (isLive) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.People, "Viewers", tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Text("$viewers", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stream Preview / Status Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isLive) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLive) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Videocam,
                                "Live",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                title.ifBlank { "Untitled Broadcast" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                formatDuration(duration),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.VideocamOff,
                                "Offline",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Camera Preview",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Stream Setup (only when not live)
            if (!isLive) {
                ElevatedCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Stream Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Broadcast Title") },
                            placeholder = { Text("e.g., 'Morning Farm Tour'") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Text(
                            "Audience",
                            style = MaterialTheme.typography.labelLarge
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AudienceType.entries.forEach { audience ->
                                FilterChip(
                                    selected = selectedAudience == audience,
                                    onClick = { selectedAudience = audience },
                                    label = { Text(audience.label) }
                                )
                            }
                        }
                        
                        Button(
                            onClick = { 
                                isLive = true
                                viewers = 1
                            },
                            enabled = title.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Icon(Icons.Default.Circle, "Live", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Go Live")
                        }
                    }
                }
            } else {
                // End Stream Button
                OutlinedButton(
                    onClick = { 
                        isLive = false
                        chatMessages.add(
                            ChatMessage(
                                username = "System",
                                message = "📴 Broadcast ended. Thanks for watching!",
                                isSystem = true
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("End Broadcast")
                }
            }
            
            // Chat Section
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Chat Header
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Live Chat",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    // Chat Messages
                    LazyColumn(
                        state = chatListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (chatMessages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (isLive) "Be the first to chat!"
                                        else "Chat will appear here when you go live",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(chatMessages) { msg ->
                                ChatMessageItem(msg)
                            }
                        }
                    }
                    
                    // Chat Input
                    if (isLive) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Say something...") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        chatMessages.add(
                                            ChatMessage(
                                                username = "You",
                                                message = chatInput
                                            )
                                        )
                                        chatInput = ""
                                    }
                                },
                                enabled = chatInput.isNotBlank()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send, 
                                    "Send",
                                    tint = if (chatInput.isNotBlank()) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (message.isSystem) 
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else if (message.username == "You")
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (message.isSystem) MaterialTheme.colorScheme.secondary
                        else if (message.username == "You") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.tertiary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
            
            Spacer(Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        message.username,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (message.isSystem) MaterialTheme.colorScheme.secondary
                        else if (message.username == "You") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        timeFormat.format(Date(message.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, secs)
}
