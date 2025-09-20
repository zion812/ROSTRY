package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.rio.rostry.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : BaseViewModel()