package com.rio.rostry.ui.gamification

import androidx.lifecycle.ViewModel
import com.rio.rostry.data.database.dao.AchievementDao
import com.rio.rostry.data.database.dao.UserProgressDao
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

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
