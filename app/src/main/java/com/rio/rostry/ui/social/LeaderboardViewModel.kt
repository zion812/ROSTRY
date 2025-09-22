package com.rio.rostry.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ReputationDao
import com.rio.rostry.data.database.entity.ReputationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val reputationDao: ReputationDao,
) : ViewModel() {
    val top: StateFlow<List<ReputationEntity>> = reputationDao.top().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
}
