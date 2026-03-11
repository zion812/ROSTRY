package com.rio.rostry.data.monitoring.mapper

import com.rio.rostry.core.model.GrowthRecord
import com.rio.rostry.data.database.entity.GrowthRecordEntity

fun GrowthRecordEntity.toGrowthRecord(): GrowthRecord =
    GrowthRecord(
        recordId = recordId,
        productId = productId,
        weight = weightGrams,
        height = heightCm,
        notes = milestone,
        recordedAt = createdAt,
        createdAt = createdAt
    )

fun GrowthRecord.toEntity(): GrowthRecordEntity =
    GrowthRecordEntity(
        recordId = recordId,
        productId = productId,
        weightGrams = weight,
        heightCm = height,
        milestone = notes,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
