package com.rio.rostry.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.FarmerDashboard
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FarmerDashboardViewModel @Inject constructor(
    repo: AnalyticsRepository,
    currentUserProvider: CurrentUserProvider
) : ViewModel() {
    private val empty = FarmerDashboard(revenue = 0.0, orders = 0, productViews = 0, engagementScore = 0.0)
    private val uid = currentUserProvider.userIdOrNull()
    val dashboard: StateFlow<FarmerDashboard> =
        (uid?.let { repo.farmerDashboard(it) } ?: MutableStateFlow(empty))
            .stateIn(viewModelScope, SharingStarted.Eagerly, empty)
}
