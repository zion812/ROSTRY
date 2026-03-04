package com.rio.rostry.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for storing pre-calculated profitability metrics.
 * Supports analytics and reporting for products, categories, and time periods.
 */
@Entity(
    tableName = "profitability_metrics",
    indices = [
        Index(value = ["entity_id", "entity_type"]),
        Index(value = ["period_start", "period_end"])
    ]
)
data class ProfitabilityMetricsEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "entity_id")
    val entityId: String,

    @ColumnInfo(name = "entity_type")
    val entityType: String, // "PRODUCT", "CATEGORY", "USER"

    @ColumnInfo(name = "period_start")
    val periodStart: Long,

    @ColumnInfo(name = "period_end")
    val periodEnd: Long,

    val revenue: Double,

    val costs: Double,

    val profit: Double,

    @ColumnInfo(name = "profit_margin")
    val profitMargin: Double,

    @ColumnInfo(name = "order_count")
    val orderCount: Int,

    @ColumnInfo(name = "calculated_at")
    val calculatedAt: Long
)
