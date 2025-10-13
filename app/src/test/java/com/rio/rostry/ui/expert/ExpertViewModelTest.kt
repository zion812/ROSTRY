package com.rio.rostry.ui.expert

import com.rio.rostry.data.database.dao.ExpertBookingsDao
import com.rio.rostry.data.database.entity.ExpertBookingEntity
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakeExpertBookingsDao : ExpertBookingsDao {
    var lastUserId: String? = null
    private val map = mutableMapOf<String, MutableStateFlow<List<ExpertBookingEntity>>>()
    override suspend fun upsert(booking: ExpertBookingEntity) {
        val flow = map.getOrPut(booking.userId) { MutableStateFlow(emptyList()) }
        flow.value = flow.value + booking
    }
    override fun streamUserBookings(userId: String): Flow<List<ExpertBookingEntity>> {
        lastUserId = userId
        return map.getOrPut(userId) { MutableStateFlow(emptyList()) }
    }
}

private class FakeUserProvider(private val id: String?) : CurrentUserProvider { override fun userIdOrNull(): String? = id }

class ExpertViewModelTest {
    @Test
    fun usesCurrentUserProvider_forStream() = runBlocking {
        val dao = FakeExpertBookingsDao()
        val vm = ExpertViewModel(dao, FakeUserProvider("expert_user"))
        // Access the StateFlow to trigger subscription
        val list = vm.bookings.first()
        assertEquals(emptyList<ExpertBookingEntity>(), list)
        assertEquals("expert_user", dao.lastUserId)
    }
}
