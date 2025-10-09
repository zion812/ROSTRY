package com.rio.rostry.data.repository.monitoring

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface DailyLogRepository {
    fun observe(productId: String): Flow<List<DailyLogEntity>>
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<DailyLogEntity>>
    suspend fun upsert(log: DailyLogEntity)
    suspend fun delete(logId: String)
    suspend fun getTodayLog(farmerId: String, productId: String): DailyLogEntity?
}

@Singleton
class DailyLogRepositoryImpl @Inject constructor(
    private val dao: DailyLogDao
) : DailyLogRepository {
    override fun observe(productId: String): Flow<List<DailyLogEntity>> = dao.observeForProduct(productId)

    override fun observeForFarmerBetween(
        farmerId: String,
        start: Long,
        end: Long
    ): Flow<List<DailyLogEntity>> = dao.observeForFarmerBetween(farmerId, start, end)

    override suspend fun upsert(log: DailyLogEntity) {
        val now = System.currentTimeMillis()
        // Merge with existing (productId, logDate) if present
        val existing = dao.getByProductAndDate(log.productId, log.logDate)
        if (existing == null) {
            try {
                dao.upsert(log.copy(dirty = true, updatedAt = now))
                return
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                // Another writer inserted between our read and write; fetch and merge
                val cur = dao.getByProductAndDate(log.productId, log.logDate)
                if (cur != null) {
                    val mergedOnConflict = mergeLogs(cur, log).copy(dirty = true, updatedAt = now)
                    dao.upsert(mergedOnConflict)
                    return
                } else {
                    // Fallback retry once
                    dao.upsert(log.copy(dirty = true, updatedAt = now))
                    return
                }
            }
        }
        val merged = mergeLogs(existing, log).copy(dirty = true, updatedAt = now)
        dao.upsert(merged)
    }

    override suspend fun delete(logId: String) {
        dao.delete(logId)
    }

    override suspend fun getTodayLog(farmerId: String, productId: String): DailyLogEntity? {
        val midnight = todayMidnight()
        return dao.getByFarmerAndDate(farmerId, midnight).firstOrNull { it.productId == productId }
    }

    private fun mergeLogs(base: DailyLogEntity, incoming: DailyLogEntity): DailyLogEntity {
        fun <T> pick(a: T?, b: T?): T? = b ?: a
        fun mergeStringListsJson(a: String?, b: String?): String? {
            val listType = object : TypeToken<MutableList<String>>() {}.type
            val ga = runCatching { if (a.isNullOrBlank()) mutableListOf<String>() else Gson().fromJson<MutableList<String>>(a, listType) }.getOrElse { mutableListOf() }
            val gb = runCatching { if (b.isNullOrBlank()) mutableListOf<String>() else Gson().fromJson<MutableList<String>>(b, listType) }.getOrElse { mutableListOf() }
            val merged = (ga + gb).distinct()
            return if (merged.isEmpty()) null else Gson().toJson(merged)
        }
        // For medication/symptoms JSON, apply same merge strategy (string-array JSON)
        val medication = mergeStringListsJson(base.medicationJson, incoming.medicationJson)
        val symptoms = mergeStringListsJson(base.symptomsJson, incoming.symptomsJson)
        val photos = mergeStringListsJson(base.photoUrls, incoming.photoUrls)
        val notes = when {
            base.notes.isNullOrBlank() -> incoming.notes
            incoming.notes.isNullOrBlank() -> base.notes
            base.notes == incoming.notes -> base.notes
            else -> base.notes + "\n" + incoming.notes
        }
        return base.copy(
            weightGrams = pick(base.weightGrams, incoming.weightGrams),
            feedKg = pick(base.feedKg, incoming.feedKg),
            medicationJson = medication,
            symptomsJson = symptoms,
            activityLevel = pick(base.activityLevel, incoming.activityLevel),
            photoUrls = photos,
            notes = notes,
            temperature = pick(base.temperature, incoming.temperature),
            humidity = pick(base.humidity, incoming.humidity),
            author = pick(base.author, incoming.author),
            deviceTimestamp = maxOf(base.deviceTimestamp, incoming.deviceTimestamp),
        )
    }

    private fun todayMidnight(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
