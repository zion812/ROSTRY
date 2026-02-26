package com.rio.rostry.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.HubAssignmentEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Test HubAssignmentDao operations.
 */
@RunWith(AndroidJUnit4::class)
class HubAssignmentDaoTest {

    private lateinit var hubAssignmentDao: HubAssignmentDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        hubAssignmentDao = db.hubAssignmentDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetHubAssignment() = runBlocking {
        val hubAssignment = HubAssignmentEntity(
            productId = "product1",
            hubId = "hub1",
            distanceKm = 15.5,
            assignedAt = System.currentTimeMillis(),
            sellerLocationLat = 12.9716,
            sellerLocationLon = 77.5946
        )

        hubAssignmentDao.insert(hubAssignment)
        val retrieved = hubAssignmentDao.getByProductId("product1")

        assertNotNull(retrieved)
        assertEquals(hubAssignment.productId, retrieved?.productId)
        assertEquals(hubAssignment.hubId, retrieved?.hubId)
        assertEquals(hubAssignment.distanceKm, retrieved?.distanceKm, 0.01)
    }

    @Test
    @Throws(Exception::class)
    fun getHubLoadCount() = runBlocking {
        val assignments = listOf(
            HubAssignmentEntity("product1", "hub1", 10.0, System.currentTimeMillis(), 12.0, 77.0),
            HubAssignmentEntity("product2", "hub1", 15.0, System.currentTimeMillis(), 12.1, 77.1),
            HubAssignmentEntity("product3", "hub2", 20.0, System.currentTimeMillis(), 12.2, 77.2)
        )

        hubAssignmentDao.insertAll(assignments)

        val hub1Count = hubAssignmentDao.getHubLoadCount("hub1")
        val hub2Count = hubAssignmentDao.getHubLoadCount("hub2")

        assertEquals(2, hub1Count)
        assertEquals(1, hub2Count)
    }

    @Test
    @Throws(Exception::class)
    fun deleteByProductId() = runBlocking {
        val hubAssignment = HubAssignmentEntity(
            productId = "product1",
            hubId = "hub1",
            distanceKm = 15.5,
            assignedAt = System.currentTimeMillis(),
            sellerLocationLat = 12.9716,
            sellerLocationLon = 77.5946
        )

        hubAssignmentDao.insert(hubAssignment)
        hubAssignmentDao.deleteByProductId("product1")
        val retrieved = hubAssignmentDao.getByProductId("product1")

        assertNull(retrieved)
    }
}
