package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rio.rostry.domain.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : BaseViewModel()