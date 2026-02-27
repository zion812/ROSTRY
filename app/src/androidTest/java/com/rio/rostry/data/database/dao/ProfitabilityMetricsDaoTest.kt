package com.rio.rostry.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProfitabilityMetricsEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Test ProfitabilityMetricsDao operations.
 */
@RunWith(AndroidJUnit4::class)
class ProfitabilityMetricsDaoTest {

    private lateinit var profitabilityMetricsDao: ProfitabilityMetricsDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        profitabilityMetricsDao = db.profitabilityMetricsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetMetrics() = runBlocking {
        val metrics = ProfitabilityMetricsEntity(
            id = "metrics1",
            entityId = "product1",
            entityType = "PRODUCT",
            periodStart = 1000L,
            periodEnd = 2000L,
            revenue = 10000.0,
            costs = 6000.0,
            profit = 4000.0,
            profitMargin = 40.0,
            orderCount = 10,
            calculatedAt = System.currentTimeMillis()
        )

        profitabilityMetricsDao.insert(metrics)
        val retrieved = profitabilityMetricsDao.getLatestMetrics("product1", "PRODUCT")

        assertNotNull(retrieved)
        assertEquals(metrics.id, retrieved?.id)
        assertEquals(metrics.revenue, retrieved!!.revenue, 0.01)
        assertEquals(metrics.profit, retrieved.profit, 0.01)
        assertEquals(metrics.profitMargin, retrieved.profitMargin, 0.01)
    }

    @Test
    @Throws(Exception::class)
    fun getMetricsInPeriod() = runBlocking {
        val metrics1 = ProfitabilityMetricsEntity(
            id = "metrics1",
            entityId = "product1",
            entityType = "PRODUCT",
            periodStart = 1000L,
            periodEnd = 2000L,
            revenue = 10000.0,
            costs = 6000.0,
            profit = 4000.0,
            profitMargin = 40.0,
            orderCount = 10,
            calculatedAt = System.currentTimeMillis()
        )

        val metrics2 = ProfitabilityMetricsEntity(
            id = "metrics2",
            entityId = "product1",
            entityType = "PRODUCT",
            periodStart = 2000L,
            periodEnd = 3000L,
            revenue = 15000.0,
            costs = 8000.0,
            profit = 7000.0,
            profitMargin = 46.67,
            orderCount = 15,
            calculatedAt = System.currentTimeMillis()
        )

        profitabilityMetricsDao.insertAll(listOf(metrics1, metrics2))

        val retrieved = profitabilityMetricsDao.getMetrics(
            entityId = "product1",
            entityType = "PRODUCT",
            periodStart = 1000L,
            periodEnd = 3000L
        )

        assertEquals(2, retrieved.size)
    }

    @Test
    @Throws(Exception::class)
    fun getTopPerformers() = runBlocking {
        val metrics1 = ProfitabilityMetricsEntity(
            id = "metrics1",
            entityId = "product1",
            entityType = "PRODUCT",
            periodStart = 1000L,
            periodEnd = 2000L,
            revenue = 10000.0,
            costs = 6000.0,
            profit = 4000.0,
            profitMargin = 40.0,
            orderCount = 10,
            calculatedAt = System.currentTimeMillis()
        )

        val metrics2 = ProfitabilityMetricsEntity(
            id = "metrics2",
            entityId = "product2",
            entityType = "PRODUCT",
            periodStart = 1000L,
            periodEnd = 2000L,
            revenue = 15000.0,
            costs = 8000.0,
            profit = 7000.0,
            profitMargin = 46.67,
            orderCount = 15,
            calculatedAt = System.currentTimeMillis()
        )

        profitabilityMetricsDao.insertAll(listOf(metrics1, metrics2))

        val topPerformers = profitabilityMetricsDao.getTopPerformers(
            entityType = "PRODUCT",
            periodStart = 1000L,
            periodEnd = 2000L,
            limit = 10
        )

        assertEquals(2, topPerformers.size)
        // Should be ordered by profit DESC
        assertEquals("product2", topPerformers[0].entityId)
        assertEquals("product1", topPerformers[1].entityId)
    }
}
