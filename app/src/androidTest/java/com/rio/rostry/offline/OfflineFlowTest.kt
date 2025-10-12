package com.rio.rostry.offline

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.DailyLogEntity
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

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OfflineFlowTest {
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
    fun airplaneMode_offlineDailyLog_autosaves_dirty_true_and_unique_per_day() = runBlocking {
        val dailyDao = db.dailyLogDao()
        val now = System.currentTimeMillis()
        val midnight = now - (now % (24L*60*60*1000))
        val entity = DailyLogEntity(
            logId = "dlog_P1_$midnight",
            productId = "P1",
            farmerId = "F1",
            logDate = midnight,
            deviceTimestamp = now,
            author = "F1",
            dirty = true,
            notes = "offline notes",
            createdAt = now,
            updatedAt = now
        )
        // First insert
        dailyDao.upsert(entity)
        val first = dailyDao.getByProductAndDate("P1", midnight)
        assertTrue(first?.dirty == true)
        // Second insert same day should not duplicate due to UNIQUE(productId, logDate)
        val entity2 = entity.copy(notes = "edit", updatedAt = now + 1000)
        dailyDao.upsert(entity2)
        val again = dailyDao.getByProductAndDate("P1", midnight)
        assertTrue(again != null && again.notes == "edit")
    }

    @Test
    fun reconnection_within_5m_marks_clean_and_sets_syncedAt() = runBlocking {
        val dailyDao = db.dailyLogDao()
        val now = System.currentTimeMillis()
        val midnight = now - (now % (24L*60*60*1000))
        val entity = DailyLogEntity(
            logId = "dlog_P2_$midnight",
            productId = "P2",
            farmerId = "F1",
            logDate = midnight,
            deviceTimestamp = now,
            author = "F1",
            dirty = true,
            notes = "queued",
            createdAt = now,
            updatedAt = now
        )
        dailyDao.upsert(entity)
        val syncedAt = now + 60_000 // simulate < 5 minutes
        dailyDao.clearDirty(listOf("dlog_P2_$midnight"), syncedAt)
        val after = dailyDao.getByProductAndDate("P2", midnight)
        assertTrue(after?.dirty == false)
        assertTrue((after?.syncedAt ?: 0L) == syncedAt)
    }
}
