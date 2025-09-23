package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.GeneralDashboard
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GeneralDashboardViewModel @Inject constructor(
    repo: AnalyticsRepository,
    currentUserProvider: CurrentUserProvider
) : ViewModel() {
    private val empty = GeneralDashboard(totalOrders = 0, totalSpend = 0.0, favoriteProducts = emptyList(), recentEngagement = 0)
    private val uid = currentUserProvider.userIdOrNull()
    val dashboard: StateFlow<GeneralDashboard> =
        (uid?.let { repo.generalDashboard(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)
}
