package com.rio.rostry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.TransferLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransferViewModel : ViewModel() {
    private val _transfers = MutableStateFlow<List<TransferLog>>(emptyList())
    val transfers: StateFlow<List<TransferLog>> = _transfers.asStateFlow()

    // Mock data for demonstration
    init {
        viewModelScope.launch {
            val mockTransfers = listOf(
                TransferLog(
                    transferId = "1",
                    fowlId = "1",
                    giverId = "123",
                    receiverId = "124",
                    timestamp = System.currentTimeMillis(),
                    status = com.rio.rostry.data.models.TransferStatus.PENDING,
                    proofUrls = emptyList(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            _transfers.value = mockTransfers
        }
    }
}