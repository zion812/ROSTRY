package com.rio.rostry.feature.enthusiast.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.WatchedLineageEntity
import com.rio.rostry.data.repository.WatchedLineagesRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WatchedLineagesFeedViewModel @Inject constructor(
    private val watchedLineagesRepository: WatchedLineagesRepository
) : ViewModel() {

    val watchedLineages: StateFlow<Resource<List<WatchedLineageEntity>>> = watchedLineagesRepository.getWatchedLineages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )
}
