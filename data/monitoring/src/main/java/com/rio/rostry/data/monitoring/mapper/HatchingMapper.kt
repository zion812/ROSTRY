package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.HatchingBatch
import com.rio.rostry.core.model.HatchingLog
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity

object HatchingMapper {
    fun batchToDomain(entity: HatchingBatchEntity): HatchingBatch =
        HatchingBatch(
            id = entity.batchId,
            batchName = entity.name,
            startDate = entity.startedAt,
            expectedHatchDate = entity.expectedHatchAt ?: 0L,
            eggCount = entity.eggsCount ?: 0,
            status = entity.status,
            notes = entity.notes,
            createdAt = entity.startedAt,
            updatedAt = entity.updatedAt
        )

    fun batchToEntity(model: HatchingBatch): HatchingBatchEntity =
        HatchingBatchEntity(
            batchId = model.id,
            name = model.batchName,
            startedAt = model.startDate,
            expectedHatchAt = model.expectedHatchDate,
            eggsCount = model.eggCount,
            status = model.status,
            notes = model.notes,
            updatedAt = model.updatedAt
        )

    fun logToDomain(entity: HatchingLogEntity): HatchingLog =
        HatchingLog(
            id = entity.logId,
            batchId = entity.batchId,
            date = entity.createdAt,
            temperature = entity.temperatureC,
            humidity = entity.humidityPct,
            notes = entity.notes,
            createdAt = entity.createdAt
        )

    fun logToEntity(model: HatchingLog): HatchingLogEntity =
        HatchingLogEntity(
            logId = model.id,
            batchId = model.batchId,
            createdAt = model.date,
            temperatureC = model.temperature,
            humidityPct = model.humidity,
            notes = model.notes
        )
}
