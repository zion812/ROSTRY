package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rio.rostry.domain.repository.TransferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferRepository: TransferRepository
) : BaseViewModel()