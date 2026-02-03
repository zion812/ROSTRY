package com.rio.rostry.ui.admin.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAnalyticsViewModel @Inject constructor(
    private val userRepository: com.rio.rostry.data.repository.UserRepository
) : ViewModel() {

    data class RegionStat(val name: String, val userCount: Int)

    data class UiState(
        val isLoading: Boolean = true,
        val totalUsers: Int = 0,
        val newThisWeek: Int = 0,
        val newThisMonth: Int = 0,
        val enthusiastCount: Int = 0,
        val farmerCount: Int = 0,
        val adminCount: Int = 0,
        val activeToday: Int = 0,
        val activeWeek: Int = 0,
        val topRegions: List<RegionStat> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val now = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L
                val oneWeek = 7 * oneDay
                val oneMonth = 30 * oneDay
                
                val startOfToday = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.timeInMillis

                val totalUsers = userRepository.getAudienceSize(null)
                val newThisWeek = userRepository.getNewUsersCount(now - oneWeek)
                val newThisMonth = userRepository.getNewUsersCount(now - oneMonth)
                
                val enthusiastCount = userRepository.getAudienceSize(com.rio.rostry.domain.model.UserType.ENTHUSIAST)
                val farmerCount = userRepository.getAudienceSize(com.rio.rostry.domain.model.UserType.FARMER)
                val adminCount = userRepository.getAudienceSize(com.rio.rostry.domain.model.UserType.ADMIN)
                
                val activeToday = userRepository.getActiveUsersCount(startOfToday)
                val activeWeek = userRepository.getActiveUsersCount(now - oneWeek)

                // TODO: Implement Region Analytics
                val topRegions = emptyList<RegionStat>() 

                _state.update { it.copy(
                    isLoading = false,
                    totalUsers = totalUsers,
                    newThisWeek = newThisWeek,
                    newThisMonth = newThisMonth,
                    enthusiastCount = enthusiastCount,
                    farmerCount = farmerCount,
                    adminCount = adminCount,
                    activeToday = activeToday,
                    activeWeek = activeWeek,
                    topRegions = topRegions
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
