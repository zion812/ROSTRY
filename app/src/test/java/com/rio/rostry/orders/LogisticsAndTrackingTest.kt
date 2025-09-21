package com.rio.rostry.orders

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.DeliveryHubDao
import com.rio.rostry.data.database.dao.OrderTrackingEventDao
import com.rio.rostry.data.database.entity.DeliveryHubEntity
import com.rio.rostry.data.repository.LogisticsRepositoryImpl
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LogisticsAndTrackingTest {
    private lateinit var db: AppDatabase
    private lateinit var hubDao: DeliveryHubDao
    private lateinit var trackingDao: OrderTrackingEventDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        hubDao = db.deliveryHubDao()
        trackingDao = db.orderTrackingEventDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun hub_assignment_and_timeline_order() = runBlocking {
        // Seed hubs
        hubDao.upsert(DeliveryHubEntity("h1", "Hub1", 12.0, 77.0, null))
        hubDao.upsert(DeliveryHubEntity("h2", "Hub2", 13.0, 78.0, null))
        val repo = LogisticsRepositoryImpl(hubDao, trackingDao)
        val hubRes = repo.assignNearestHub(12.5, 77.5, 12.9, 77.9)
        assertTrue(hubRes is Resource.Success)

        // Timeline
        val orderId = "oTrack"
        repo.startDelivery(orderId, (hubRes as Resource.Success).data?.hubId)
        repo.markDelivered(orderId)
        val events = trackingDao.observeByOrder(orderId).first()
        assertEquals(2, events.size)
        assertEquals("OUT_FOR_DELIVERY", events[0].status)
        assertEquals("DELIVERED", events[1].status)
    }
}
