package com.rio.rostry.ui.farmer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.FarmProfileEntity
import com.rio.rostry.data.database.entity.FarmTimelineEventEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicFarmProfileScreen(
    viewModel: PublicFarmProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onContact: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.profile?.farmName ?: "Farm Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.error ?: "Error loading profile")
                }
            }
            uiState.profile != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Header
                    item {
                        ProfileHeader(uiState.profile!!, viewModel.getBadges())
                    }
                    
                    // Trust Score Card
                    item {
                        TrustScoreCard(uiState.profile!!)
                    }
                    
                    // Contact Buttons
                    item {
                        ContactButtons(uiState.profile!!, onContact)
                    }
                    
                    // Timeline Header
                    item {
                        Text(
                            "Farm Journey",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Timeline Events
                    items(uiState.timeline) { event ->
                        TimelineEventCard(event)
                    }
                    
                    if (uiState.timeline.isEmpty()) {
                        item {
                            EmptyTimelineMessage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(profile: FarmProfileEntity, badges: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (profile.logoUrl != null) {
                    AsyncImage(
                        model = profile.logoUrl,
                        contentDescription = "Farm Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        profile.farmName.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Farm Name + Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    profile.farmName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Location
            if (profile.locationName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        profile.locationName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Member Since
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Member since ${formatDate(profile.memberSince)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Bio
            if (!profile.farmBio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    profile.farmBio,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            
            // Badges
            if (badges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    badges.forEach { badge ->
                        BadgeChip(badge)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustScoreCard(profile: FarmProfileEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Trust Score: ${profile.trustScore}/100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { profile.trustScore / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = when {
                    profile.trustScore >= 80 -> Color(0xFF4CAF50)
                    profile.trustScore >= 50 -> Color(0xFFFFC107)
                    else -> Color(0xFFFF5722)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("${profile.totalBirdsSold}", "Birds Sold")
                StatItem("${profile.totalOrdersCompleted}", "Orders")
                StatItem("${profile.vaccinationRate ?: 0}%", "Vaccinated")
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ContactButtons(profile: FarmProfileEntity, onContact: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (profile.isCallEnabled) {
            Button(
                onClick = { onContact("call") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call")
            }
        }
        if (profile.isWhatsappEnabled && profile.whatsappNumber != null) {
            Button(
                onClick = { onContact("whatsapp") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366)
                )
            ) {
                Icon(Icons.Default.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("WhatsApp")
            }
        }
    }
}

@Composable
private fun TimelineEventCard(event: FarmTimelineEventEntity) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getEventColor(event.eventType).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    getEventEmoji(event.eventType),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    formatDate(event.eventDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun BadgeChip(badge: String) {
    val (emoji, label) = when (badge) {
        "VERIFIED" -> "✓" to "Verified"
        "FULLY_VACCINATED" -> "💉" to "Vaccinated"
        "FAST_RESPONDER" -> "⚡" to "Fast"
        "TOP_SELLER" -> "⭐" to "Top Seller"
        "VETERAN_FARMER" -> "🏆" to "Veteran"
        else -> "🏷️" to badge
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun EmptyTimelineMessage() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Timeline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "No activity yet",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun getEventColor(eventType: String): Color = when (eventType) {
    FarmTimelineEventEntity.TYPE_VACCINATION -> Color(0xFF4CAF50)
    FarmTimelineEventEntity.TYPE_SANITATION -> Color(0xFF2196F3)
    FarmTimelineEventEntity.TYPE_BATCH_ADDED -> Color(0xFFFFC107)
    FarmTimelineEventEntity.TYPE_SALE -> Color(0xFFFF9800)
    FarmTimelineEventEntity.TYPE_MILESTONE -> Color(0xFF9C27B0)
    else -> Color.Gray
}

private fun getEventEmoji(eventType: String): String = when (eventType) {
    FarmTimelineEventEntity.TYPE_VACCINATION -> "💉"
    FarmTimelineEventEntity.TYPE_SANITATION -> "🧹"
    FarmTimelineEventEntity.TYPE_BATCH_ADDED -> "🐣"
    FarmTimelineEventEntity.TYPE_SALE -> "💰"
    FarmTimelineEventEntity.TYPE_MILESTONE -> "🏆"
    FarmTimelineEventEntity.TYPE_WEIGHT_CHECK -> "⚖️"
    FarmTimelineEventEntity.TYPE_BUYER_FEEDBACK -> "⭐"
    FarmTimelineEventEntity.TYPE_FEED -> "🌾"
    else -> "📋"
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
