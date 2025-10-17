package com.rio.rostry.data.repository.monitoring

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.components.SyncState
import com.rio.rostry.ui.components.getSyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

data class ConflictEvent(
    val logId: String,
    val productId: String,
    val conflictFields: List<String>,
    val mergedAt: Long
)

interface DailyLogRepository {
    val conflictEvents: Flow<ConflictEvent>
    fun observe(productId: String): Flow<List<DailyLogEntity>>
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<DailyLogEntity>>
    suspend fun upsert(log: DailyLogEntity)
    suspend fun delete(logId: String)
    suspend fun getTodayLog(farmerId: String, productId: String): DailyLogEntity?
    suspend fun getSyncState(logId: String): SyncState
}

@Singleton
class DailyLogRepositoryImpl @Inject constructor(
    private val dao: DailyLogDao,
    private val outboxDao: OutboxDao,
    private val gson: Gson,
    private val currentUserProvider: CurrentUserProvider
) : DailyLogRepository {
    private val _conflictEvents = MutableSharedFlow<ConflictEvent>()
    override val conflictEvents = _conflictEvents.asSharedFlow()
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
                // Queue to outbox for sync after local upsert
                queueToOutbox(log.copy(dirty = true, updatedAt = now))
                return
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                // Another writer inserted between our read and write; fetch and merge
                val cur = dao.getByProductAndDate(log.productId, log.logDate)
                if (cur != null) {
                    val mergedOnConflict = mergeLogs(cur, log).copy(dirty = true, updatedAt = now)
                    dao.upsert(mergedOnConflict)
                    // Queue to outbox for sync after local upsert
                    queueToOutbox(mergedOnConflict)
                    return
                } else {
                    // Fallback retry once
                    dao.upsert(log.copy(dirty = true, updatedAt = now))
                    // Queue to outbox for sync after local upsert
                    queueToOutbox(log.copy(dirty = true, updatedAt = now))
                    return
                }
            }
        }
        val merged = mergeLogs(existing, log).copy(dirty = true, updatedAt = now)
        dao.upsert(merged)
        // Queue to outbox for sync after local upsert
        queueToOutbox(merged)
    }

    private suspend fun queueToOutbox(log: DailyLogEntity) {
        val outboxEntry = OutboxEntity(
            outboxId = log.logId, // Use logId as unique outboxId
            userId = currentUserProvider.userIdOrNull() ?: log.farmerId,
            entityType = OutboxEntity.TYPE_DAILY_LOG,
            entityId = log.logId,
            operation = "UPSERT",
            payloadJson = gson.toJson(log),
            createdAt = System.currentTimeMillis(),
            priority = "HIGH"
        )
        outboxDao.insert(outboxEntry)
    }

    override suspend fun delete(logId: String) {
        dao.delete(logId)
    }

    override suspend fun getTodayLog(farmerId: String, productId: String): DailyLogEntity? {
        val midnight = todayMidnight()
        return dao.getByFarmerAndDate(farmerId, midnight).firstOrNull { it.productId == productId }
    }

    private suspend fun mergeLogs(base: DailyLogEntity, incoming: DailyLogEntity): DailyLogEntity {
        var conflictCount = 0
        val conflictFields = mutableListOf<String>()

        fun logConflict(field: String, baseVal: Any?, incomingVal: Any?) {
            Timber.w("Merge conflict in $field: base=$baseVal, incoming=$incomingVal")
            conflictCount++
            conflictFields.add(field)
        }

        fun <T> pick(a: T?, b: T?): T? = b ?: a

        fun <T> pickCritical(a: T?, b: T?, aTs: Long, bTs: Long): T? {
            return when {
                a == null -> b
                b == null -> a
                aTs >= bTs -> a
                else -> b
            }
        }

        fun mergeStringListsJson(a: String?, b: String?): String? {
            val listType = object : TypeToken<MutableList<String>>() {}.type
            val ga = runCatching { if (a.isNullOrBlank()) mutableListOf<String>() else Gson().fromJson<MutableList<String>>(a, listType) }.getOrElse { mutableListOf() }
            val gb = runCatching { if (b.isNullOrBlank()) mutableListOf<String>() else Gson().fromJson<MutableList<String>>(b, listType) }.getOrElse { mutableListOf() }
            val merged = (ga + gb).distinct()
            return if (merged.isEmpty()) null else Gson().toJson(merged)
        }

        // Detect conflicts
        if (base.weightGrams != null && incoming.weightGrams != null && base.weightGrams != incoming.weightGrams) logConflict("weightGrams", base.weightGrams, incoming.weightGrams)
        if (base.feedKg != null && incoming.feedKg != null && base.feedKg != incoming.feedKg) logConflict("feedKg", base.feedKg, incoming.feedKg)
        if (base.activityLevel != null && incoming.activityLevel != null && base.activityLevel != incoming.activityLevel) logConflict("activityLevel", base.activityLevel, incoming.activityLevel)
        if (base.temperature != null && incoming.temperature != null && base.temperature != incoming.temperature) logConflict("temperature", base.temperature, incoming.temperature)
        if (base.humidity != null && incoming.humidity != null && base.humidity != incoming.humidity) logConflict("humidity", base.humidity, incoming.humidity)
        if (base.author != null && incoming.author != null && base.author != incoming.author) logConflict("author", base.author, incoming.author)
        if (base.notes != null && incoming.notes != null && base.notes != incoming.notes) logConflict("notes", base.notes, incoming.notes)

        // Merge
        val medication = mergeStringListsJson(base.medicationJson, incoming.medicationJson)
        val symptoms = mergeStringListsJson(base.symptomsJson, incoming.symptomsJson)
        val photos = mergeStringListsJson(base.photoUrls, incoming.photoUrls)
        val notes = when {
            base.notes.isNullOrBlank() -> incoming.notes
            incoming.notes.isNullOrBlank() -> base.notes
            base.notes == incoming.notes -> base.notes
            else -> "${base.notes}\n--- Merged at ${System.currentTimeMillis()} ---\n${incoming.notes}"
        }
        val now = System.currentTimeMillis()
        val merged = base.copy(
            weightGrams = pickCritical(base.weightGrams, incoming.weightGrams, base.deviceTimestamp, incoming.deviceTimestamp),
            feedKg = pickCritical(base.feedKg, incoming.feedKg, base.deviceTimestamp, incoming.deviceTimestamp),
            medicationJson = medication,
            symptomsJson = symptoms,
            activityLevel = pick(base.activityLevel, incoming.activityLevel),
            photoUrls = photos,
            notes = notes,
            temperature = pick(base.temperature, incoming.temperature),
            humidity = pick(base.humidity, incoming.humidity),
            author = pick(base.author, incoming.author),
            deviceTimestamp = maxOf(base.deviceTimestamp, incoming.deviceTimestamp),
            mergedAt = now,
            mergeCount = (base.mergeCount ?: 0) + 1,
            conflictResolved = conflictCount > 0
        )
        // Emit conflict event if conflicts were detected
        if (conflictCount > 0) {
            _conflictEvents.emit(
                ConflictEvent(
                    logId = merged.logId,
                    productId = merged.productId,
                    conflictFields = conflictFields,
                    mergedAt = now
                )
            )
        }
        return merged
    }

    override suspend fun getSyncState(logId: String): SyncState {
        val log = dao.getById(logId) ?: return SyncState.SYNCED // Default if not found
        return getSyncState(log)
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
