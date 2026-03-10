package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.HatchingBatch
import com.rio.rostry.core.model.HatchingLog
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity

object HatchingMapper {
    fun batchToDomain(entity: HatchingBatchEntity): HatchingBatch =
        TODO("Temporary mapper stub during modularization")

    fun batchToEntity(model: HatchingBatch): HatchingBatchEntity =
        TODO("Temporary mapper stub during modularization")

    fun logToDomain(entity: HatchingLogEntity): HatchingLog =
        TODO("Temporary mapper stub during modularization")

    fun logToEntity(model: HatchingLog): HatchingLogEntity =
        TODO("Temporary mapper stub during modularization")
}
