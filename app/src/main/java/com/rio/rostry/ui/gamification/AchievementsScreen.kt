package com.rio.rostry.ui.gamification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.UserProgressDao
import com.rio.rostry.data.database.entity.UserProgressEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    val achievements: Flow<List<UserProgressEntity>> = currentUserProvider.userIdOrNull()?.let { uid ->
        userProgressDao.forUser(uid)
    } ?: flowOf(emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(vm: AchievementsViewModel = hiltViewModel()) {
    val achievements by vm.achievements.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("All", "Unlocked", "In Progress", "Locked")

    val filteredAchievements = when (selectedTab) {
        0 -> achievements
        1 -> achievements.filter { it.unlockedAt != null }
        2 -> achievements.filter { it.unlockedAt == null && it.progress > 0 }
        3 -> achievements.filter { it.unlockedAt == null && it.progress == 0 }
        else -> achievements
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Achievements", style = MaterialTheme.typography.headlineSmall)

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        if (filteredAchievements.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No achievements yet.")
            }
        } else {
            LazyColumn {
                items(filteredAchievements) { achievement ->
                    AchievementCard(achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: UserProgressEntity) {
    val isUnlocked = achievement.unlockedAt != null
    val cardColor = if (isUnlocked) Color.Green.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Achievement: ${achievement.achievementId}", style = MaterialTheme.typography.titleMedium)
            Text("Target: ${achievement.target}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (isUnlocked) {
                Text("Unlocked", color = Color.Green, style = MaterialTheme.typography.bodyLarge)
            } else {
                LinearProgressIndicator(
                    progress = achievement.progress / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${achievement.progress}%", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
