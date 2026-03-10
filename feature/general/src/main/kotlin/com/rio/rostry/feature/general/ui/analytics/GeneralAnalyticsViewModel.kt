package com.rio.rostry.feature.general.ui.analytics

import androidx.lifecycle.ViewModel
import com.rio.rostry.core.common.analytics.GeneralAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GeneralAnalyticsViewModel @Inject constructor(
    val tracker: GeneralAnalyticsTracker
) : ViewModel()
