package com.rio.rostry.data.repository.monitoring

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Analytics DTOs
data class MortalityStats(
    val total: Int,
    val totalCostInr: Double,
    val avgAgeWeeks: Double?
)

data class VaccinationStats(
    val totalScheduled: Int,
    val totalAdministered: Int,
    val overdueCount: Int,
    val coveragePct: Double
)

data class GrowthPoint(
    val week: Int,
    val actualWeightGrams: Double?,
    val expectedWeightGrams: Double,
    val varianceGrams: Double?
)

interface GrowthRepository {
    fun observe(productId: String): Flow<List<GrowthRecordEntity>>
    suspend fun upsert(record: GrowthRecordEntity)
    suspend fun analytics(productId: String, breed: String? = null): List<GrowthPoint>
}

class GrowthRepositoryImpl @Inject constructor(
    private val dao: GrowthRecordDao
): GrowthRepository {
    override fun observe(productId: String) = dao.observeForProduct(productId)
    override suspend fun upsert(record: GrowthRecordEntity) = dao.upsert(record)

    // Simple expected weight curve (grams) for weeks 1..20.
    private val defaultExpectedByWeek: Map<Int, Double> = (1..20).associateWith { wk ->
        // naive curve: start 120g at week1, +150g per week
        120.0 + (wk - 1) * 150.0
    }

    private fun expectedForWeek(breed: String?, week: Int): Double {
        // Placeholder: could switch on breed in future, fallback to default
        return defaultExpectedByWeek[week] ?: (120.0 + (week - 1) * 150.0)
    }

    override suspend fun analytics(productId: String, breed: String?): List<GrowthPoint> {
        val list = dao.listForProduct(productId).sortedBy { it.week }
        return list.map { rec ->
            val exp = expectedForWeek(breed, rec.week)
            val variance = rec.weightGrams?.let { it - exp }
            GrowthPoint(
                week = rec.week,
                actualWeightGrams = rec.weightGrams,
                expectedWeightGrams = exp,
                varianceGrams = variance
            )
        }
    }
}

interface QuarantineRepository {
    fun observe(productId: String): Flow<List<QuarantineRecordEntity>>
    fun observeByStatus(status: String): Flow<List<QuarantineRecordEntity>>
    suspend fun insert(record: QuarantineRecordEntity)
    suspend fun update(record: QuarantineRecordEntity)
}

class QuarantineRepositoryImpl @Inject constructor(
    private val dao: QuarantineRecordDao
): QuarantineRepository {
    override fun observe(productId: String) = dao.observeForProduct(productId)
    override fun observeByStatus(status: String) = dao.observeByStatus(status)
    override suspend fun insert(record: QuarantineRecordEntity) = dao.insert(record)
    override suspend fun update(record: QuarantineRecordEntity) = dao.update(record)
}

interface MortalityRepository {
    fun observeAll(): Flow<List<MortalityRecordEntity>>
    suspend fun insert(record: MortalityRecordEntity)

    // Analytics
    suspend fun stats(start: Long, end: Long): MortalityStats
    suspend fun trendByDay(start: Long, end: Long): List<DayCount>
    suspend fun distributionByCause(start: Long, end: Long): List<CategoryCount>
}

class MortalityRepositoryImpl @Inject constructor(
    private val dao: MortalityRecordDao
): MortalityRepository {
    override fun observeAll() = dao.observeAll()
    override suspend fun insert(record: MortalityRecordEntity) = dao.insert(record)

    override suspend fun stats(start: Long, end: Long): MortalityStats {
        val total = dao.countBetween(start, end)
        val totalCost = dao.sumFinancialImpactBetween(start, end)
        val avgAge = dao.avgAgeWeeksBetween(start, end)
        return MortalityStats(total = total, totalCostInr = totalCost, avgAgeWeeks = avgAge)
    }

    override suspend fun trendByDay(start: Long, end: Long): List<DayCount> = dao.countsByDay(start, end)

    override suspend fun distributionByCause(start: Long, end: Long): List<CategoryCount> = dao.countsByCategory(start, end)
}

interface VaccinationRepository {
    fun observe(productId: String): Flow<List<VaccinationRecordEntity>>
    suspend fun upsert(record: VaccinationRecordEntity)
    suspend fun dueReminders(byTime: Long): List<VaccinationRecordEntity>

    // Analytics
    suspend fun stats(start: Long, end: Long, now: Long = System.currentTimeMillis()): VaccinationStats
    suspend fun distributionByVaccineAdministered(start: Long, end: Long): List<CategoryCount>
}

class VaccinationRepositoryImpl @Inject constructor(
    private val dao: VaccinationRecordDao
): VaccinationRepository {
    override fun observe(productId: String) = dao.observeForProduct(productId)
    override suspend fun upsert(record: VaccinationRecordEntity) = dao.upsert(record)
    override suspend fun dueReminders(byTime: Long) = dao.dueReminders(byTime)

    override suspend fun stats(start: Long, end: Long, now: Long): VaccinationStats {
        val scheduled = dao.countScheduledBetween(start, end)
        val administered = dao.countAdministeredBetween(start, end)
        val overdue = dao.countOverdue(now)
        val coverage = if (scheduled > 0) administered * 100.0 / scheduled else 0.0
        return VaccinationStats(
            totalScheduled = scheduled,
            totalAdministered = administered,
            overdueCount = overdue,
            coveragePct = coverage
        )
    }

    override suspend fun distributionByVaccineAdministered(start: Long, end: Long): List<CategoryCount> =
        dao.countsByVaccineTypeAdministeredBetween(start, end)
}

interface HatchingRepository {
    fun observeBatches(): Flow<List<HatchingBatchEntity>>
    fun observeLogs(batchId: String): Flow<List<HatchingLogEntity>>
    suspend fun upsert(batch: HatchingBatchEntity)
    suspend fun insert(log: HatchingLogEntity)
}

class HatchingRepositoryImpl @Inject constructor(
    private val batchDao: HatchingBatchDao,
    private val logDao: HatchingLogDao
): HatchingRepository {
    override fun observeBatches() = batchDao.observeBatches()
    override fun observeLogs(batchId: String) = logDao.observeForBatch(batchId)
    override suspend fun upsert(batch: HatchingBatchEntity) = batchDao.upsert(batch)
    override suspend fun insert(log: HatchingLogEntity) = logDao.insert(log)
}

// Simple farm performance repository placeholder
interface FarmPerformanceRepository {
    fun observeLifecycle(productId: String): Flow<List<LifecycleEventEntity>>
}

class FarmPerformanceRepositoryImpl @Inject constructor(
    private val lifecycleDao: LifecycleEventDao
): FarmPerformanceRepository {
    override fun observeLifecycle(productId: String) = lifecycleDao.observeForProduct(productId)
}
