package com.rio.rostry.data.sync

import androidx.room.Room
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.SyncStateEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SyncManagerTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun syncAll_clearsDirtyProducts_and_updatesWatermarks() = runBlocking {
        val productDao = db.productDao()
        val stateDao = db.syncStateDao()

        // Insert a dirty product
        val p = ProductEntity(
            productId = "p1",
            sellerId = "s1",
            name = "name",
            description = "desc",
            category = "cat",
            price = 1.0,
            quantity = 1.0,
            unit = "kg",
            location = "loc",
            dirty = true,
            updatedAt = 1L,
            lastModifiedAt = 1L
        )
        productDao.insertProduct(p)
        stateDao.upsert(SyncStateEntity())

        val sync = SyncManager(
            userDao = db.userDao(),
            productDao = productDao,
            orderDao = db.orderDao(),
            productTrackingDao = db.productTrackingDao(),
            familyTreeDao = db.familyTreeDao(),
            chatMessageDao = db.chatMessageDao(),
            transferDao = db.transferDao(),
            syncStateDao = stateDao
        )

        val res = sync.syncAll()
        // Ensure product dirties are cleared
        val updated = productDao.findById("p1")
        assertEquals(false, updated?.dirty)

        // Ensure watermarks are bumped
        val state = stateDao.get()
        // lastProductSyncAt should be > 0
        assertEquals(true, (state?.lastProductSyncAt ?: 0L) > 0L)
    }
}
