package com.rio.rostry.ui.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ModerationReportsDao
import com.rio.rostry.data.database.entity.ModerationReportEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val reportsDao: ModerationReportsDao,
) : ViewModel() {

    val openReports: StateFlow<List<ModerationReportEntity>> =
        reportsDao.streamByStatus("OPEN").stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateStatus(reportId: String, status: String) {
        viewModelScope.launch {
            reportsDao.updateStatus(reportId, status, System.currentTimeMillis())
        }
    }
}
