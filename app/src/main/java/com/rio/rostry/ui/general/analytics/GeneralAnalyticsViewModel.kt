package com.rio.rostry.ui.general.analytics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GeneralAnalyticsViewModel @Inject constructor(
    val tracker: GeneralAnalyticsTracker
) : ViewModel()
