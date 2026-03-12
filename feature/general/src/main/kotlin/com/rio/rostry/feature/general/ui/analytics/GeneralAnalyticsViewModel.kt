package com.rio.rostry.feature.general.ui.analytics
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GeneralAnalyticsViewModel @Inject constructor(
    val tracker: GeneralAnalyticsTracker
) : ViewModel()
