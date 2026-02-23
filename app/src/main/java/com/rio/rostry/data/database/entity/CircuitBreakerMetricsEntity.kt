package com.rio.rostry.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for persisting circuit breaker metrics.
 */
@Entity(tableName = "circuit_breaker_metrics")
data class CircuitBreakerMetricsEntity(
    @PrimaryKey
    @ColumnInfo(name = "service_name")
    val serviceName: String,

    val state: String,

    @ColumnInfo(name = "failure_rate")
    val failureRate: Double,

    @ColumnInfo(name = "total_calls")
    val totalCalls: Int,

    @ColumnInfo(name = "failed_calls")
    val failedCalls: Int,

    @ColumnInfo(name = "last_state_change")
    val lastStateChange: Long,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long
)
