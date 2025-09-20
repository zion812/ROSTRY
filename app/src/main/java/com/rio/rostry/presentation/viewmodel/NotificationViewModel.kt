package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rio.rostry.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : BaseViewModel()