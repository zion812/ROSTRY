package com.rio.rostry.ui.gamification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.rio.rostry.data.database.dao.AchievementDao
import com.rio.rostry.data.database.dao.UserProgressDao
import com.rio.rostry.data.database.entity.AchievementEntity
import com.rio.rostry.data.database.entity.UserProgressEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Data class combining achievement definition with user progress.
 */
data class AchievementWithProgress(
    val achievement: AchievementEntity,
    val progress: UserProgressEntity?
) {
    val isUnlocked: Boolean get() = progress?.unlockedAt != null
    val progressPercent: Float get() = progress?.let { 
        if (it.target > 0) (it.progress.toFloat() / it.target) else 0f 
    } ?: 0f
    val pointsEarned: Int get() = if (isUnlocked) achievement.points else 0
}

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val achievementDao: AchievementDao,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    val achievementsWithProgress: Flow<List<AchievementWithProgress>> = 
        currentUserProvider.userIdOrNull()?.let { uid ->
            combine(
                achievementDao.all(),
                userProgressDao.forUser(uid)
            ) { definitions, userProgress ->
                definitions.map { def ->
                    AchievementWithProgress(
                        achievement = def,
                        progress = userProgress.find { it.achievementId == def.achievementId }
                    )
                }
            }
        } ?: flowOf(emptyList())
    
    val totalPoints: Flow<Int> = currentUserProvider.userIdOrNull()?.let { uid ->
        userProgressDao.forUser(uid).combine(achievementDao.all()) { progress, defs ->
            progress.filter { it.unlockedAt != null }.sumOf { prog ->
                defs.find { it.achievementId == prog.achievementId }?.points ?: 0
            }
        }
    } ?: flowOf(0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit = {},
    vm: AchievementsViewModel = hiltViewModel()
) {
    val achievements by vm.achievementsWithProgress.collectAsState(initial = emptyList())
    val totalPoints by vm.totalPoints.collectAsState(initial = 0)
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("All", "Unlocked", "In Progress", "Locked")

    val filteredAchievements = when (selectedTab) {
        0 -> achievements
        1 -> achievements.filter { it.isUnlocked }
        2 -> achievements.filter { !it.isUnlocked && it.progressPercent > 0 }
        3 -> achievements.filter { !it.isUnlocked && it.progressPercent == 0f }
        else -> achievements
    }
    
    val unlockedCount = achievements.count { it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Achievements") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Summary Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn(
                        icon = Icons.Default.EmojiEvents,
                        value = "$unlockedCount/${achievements.size}",
                        label = "Unlocked"
                    )
                    StatColumn(
                        icon = Icons.Default.Star,
                        value = totalPoints.toString(),
                        label = "Points"
                    )
                    StatColumn(
                        icon = Icons.Default.Whatshot,
                        value = "${(unlockedCount * 100 / achievements.size.coerceAtLeast(1))}%",
                        label = "Progress"
                    )
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }

            if (filteredAchievements.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No achievements in this category",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAchievements) { achievement ->
                        AchievementCard(achievement)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.height(4.dp))
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
fun AchievementCard(achievement: AchievementWithProgress) {
    val isUnlocked = achievement.isUnlocked
    
    // Animate newly unlocked achievements
    var showUnlockAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showUnlockAnimation) 1.05f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "scale"
    )
    
    // Glow animation for unlocked
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    // Trigger unlock animation once
    LaunchedEffect(isUnlocked) {
        if (isUnlocked) {
            showUnlockAnimation = true
            delay(500)
            showUnlockAnimation = false
        }
    }
    
    val cardColor = when {
        isUnlocked -> MaterialTheme.colorScheme.primaryContainer
        achievement.progressPercent > 0 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val borderBrush = if (isUnlocked) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD700), // Gold
                Color(0xFFFFA500), // Orange
                Color(0xFFFFD700)  // Gold
            )
        )
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) {
                        getAchievementIcon(achievement.achievement.category)
                    } else {
                        Icons.Default.Lock
                    },
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = if (isUnlocked) Color.White else MaterialTheme.colorScheme.outline
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        achievement.achievement.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isUnlocked) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Verified,
                            "Unlocked",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFFFD700)
                        )
                    }
                }
                
                achievement.achievement.description?.let { desc ->
                    Text(
                        desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2
                    )
                }
                
                Spacer(Modifier.height(8.dp))
                
                if (isUnlocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFD700)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "+${achievement.achievement.points} points",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Progress bar
                    val progress = achievement.progressPercent
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${(progress * 100).toInt()}% complete",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun getAchievementIcon(category: String?): ImageVector {
    return when (category?.lowercase()) {
        "breeding" -> Icons.Default.Whatshot
        "sales" -> Icons.Default.Star
        "community" -> Icons.Default.Verified
        else -> Icons.Default.EmojiEvents
    }
}

/**
 * Compact badge display for profile headers.
 */
@Composable
fun ProfileBadgeRow(
    badges: List<AchievementWithProgress>,
    modifier: Modifier = Modifier,
    maxDisplay: Int = 5
) {
    val unlockedBadges = badges.filter { it.isUnlocked }.take(maxDisplay)
    
    if (unlockedBadges.isEmpty()) return
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((-8).dp)
    ) {
        unlockedBadges.forEach { badge ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getAchievementIcon(badge.achievement.category),
                    null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
        }
        
        val remaining = badges.count { it.isUnlocked } - maxDisplay
        if (remaining > 0) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "+$remaining",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
