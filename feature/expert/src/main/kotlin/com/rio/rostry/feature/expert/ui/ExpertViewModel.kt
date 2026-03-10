package com.rio.rostry.feature.expert.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class ExpertBookingItem(
    val bookingId: String,
    val expertId: String,
    val userId: String,
    val topic: String,
    val startTime: Long,
    val endTime: Long,
    val status: String
)

class ExpertViewModel : ViewModel() {

    private val _bookings = MutableStateFlow<List<ExpertBookingItem>>(emptyList())
    val bookings: StateFlow<List<ExpertBookingItem>> = _bookings.asStateFlow()

    fun createRequest(expertId: String, userId: String, topic: String?, details: String?) {
        val now = System.currentTimeMillis()
        val booking = ExpertBookingItem(
            bookingId = UUID.randomUUID().toString(),
            expertId = expertId,
            userId = userId,
            topic = topic ?: details ?: "Consultation",
            startTime = now + 86_400_000L,
            endTime = now + 90_000_000L,
            status = "REQUESTED"
        )
        _bookings.value = _bookings.value + booking
    }

    fun updateStatus(bookingId: String, status: String) {
        _bookings.value = _bookings.value.map { item ->
            if (item.bookingId == bookingId) item.copy(status = status) else item
        }
    }
}
