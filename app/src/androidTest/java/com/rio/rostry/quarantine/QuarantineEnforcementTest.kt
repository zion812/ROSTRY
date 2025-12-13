package com.rio.rostry.quarantine

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import com.rio.rostry.domain.model.LifecycleStage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class QuarantineEnforcementTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        hiltRule.inject()
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun overdue_badge_when_last_update_over_12h() = runBlocking {
        val productId = "PX1"
        val farmerId = "F1"
        val now = System.currentTimeMillis()
        // Seed product
        db.productDao().upsert(
            ProductEntity(
                productId = productId,
                sellerId = farmerId,
                name = "Bird",
                description = "",
                category = "BIRD",
                price = 0.0,
                quantity = 1.0,
                unit = "unit",
                location = "",
                status = "available",
                createdAt = now,
                updatedAt = now,
                lastModifiedAt = now,
                isDeleted = false,
                dirty = true,
                stage = LifecycleStage.BREEDER,
                lifecycleStatus = "ACTIVE"
            )
        )
        // Seed quarantine with lastUpdatedAt older than 12h
        val twelveHrs = 12L * 60 * 60 * 1000
        val rec = QuarantineRecordEntity(
            quarantineId = UUID.randomUUID().toString(),
            productId = productId,
            farmerId = farmerId,
            reason = "Coughing",
            lastUpdatedAt = now - (twelveHrs + 1),
            status = "ACTIVE",
            updatedAt = now,
            dirty = true
        )
        db.quarantineRecordDao().upsert(rec)
        val overdueRecords = db.quarantineRecordDao().getUpdatesOverdueForFarmer(farmerId, now - twelveHrs)
        val overdue = overdueRecords.size
        assertTrue(overdue > 0)
    }

    @Test
    fun discharge_policy_simulated_two_healthy_updates_sets_recovered() = runBlocking {
        val farmerId = "F2"
        val productId = "PX2"
        val now = System.currentTimeMillis()
        db.productDao().upsert(
            ProductEntity(
                productId = productId,
                sellerId = farmerId,
                name = "Bird2",
                description = "",
                category = "BIRD",
                price = 0.0,
                quantity = 1.0,
                unit = "unit",
                location = "",
                status = "available",
                createdAt = now,
                updatedAt = now,
                lastModifiedAt = now,
                isDeleted = false,
                dirty = true,
                stage = LifecycleStage.BREEDER,
                lifecycleStatus = "ACTIVE"
            )
        )
        val qId = UUID.randomUUID().toString()
        var rec = QuarantineRecordEntity(
            quarantineId = qId,
            productId = productId,
            farmerId = farmerId,
            reason = "Injury",
            updatesCount = 0,
            status = "ACTIVE",
            startedAt = now - (3L*24*60*60*1000),
            lastUpdatedAt = now - (2L*24*60*60*1000),
            updatedAt = now - (2L*24*60*60*1000),
            dirty = true
        )
        val dao = db.quarantineRecordDao()
        dao.upsert(rec)
        // Two healthy updates -> discharge
        rec = rec.copy(updatesCount = rec.updatesCount + 1, lastUpdatedAt = now - (24*60*60*1000), updatedAt = now - (24*60*60*1000))
        dao.upsert(rec)
        rec = rec.copy(updatesCount = rec.updatesCount + 1, status = "RECOVERED", endedAt = now, updatedAt = now)
        dao.upsert(rec)
        val active = dao.getAllActiveForFarmer(farmerId)
        assertEquals(0, active.size)
    }
}
