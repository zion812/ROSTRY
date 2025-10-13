package com.rio.rostry.ui.expert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ExpertBookingsDao
import com.rio.rostry.data.database.entity.ExpertBookingEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExpertViewModel @Inject constructor(
    private val expertBookingsDao: ExpertBookingsDao,
    private val currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    private val userId: String = currentUserProvider.userIdOrNull() ?: ""
    val bookings: StateFlow<List<ExpertBookingEntity>> =
        expertBookingsDao.streamUserBookings(userId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createRequest(expertId: String, userId: String, topic: String?, details: String?) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val e = ExpertBookingEntity(
                bookingId = UUID.randomUUID().toString(),
                expertId = expertId,
                userId = userId,
                topic = topic ?: details ?: "Consultation",
                startTime = now + 86_400_000L,
                endTime = now + 86_400_000L + 3_600_000L,
                status = "REQUESTED",
            )
            expertBookingsDao.upsert(e)
        }
    }

    fun updateStatus(bookingId: String, status: String) {
        // For simplicity, fetch current list and update the matching one
        viewModelScope.launch {
            val current = bookings.value.firstOrNull { it.bookingId == bookingId } ?: return@launch
            expertBookingsDao.upsert(current.copy(status = status))
        }
    }
}
