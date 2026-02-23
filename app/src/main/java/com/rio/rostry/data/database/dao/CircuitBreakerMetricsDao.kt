package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.CircuitBreakerMetricsEntity

@Dao
interface CircuitBreakerMetricsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CircuitBreakerMetricsEntity)

    @Query("SELECT * FROM circuit_breaker_metrics WHERE service_name = :serviceName")
    suspend fun get(serviceName: String): CircuitBreakerMetricsEntity?

    @Query("SELECT * FROM circuit_breaker_metrics")
    suspend fun getAll(): List<CircuitBreakerMetricsEntity>

    @Query("DELETE FROM circuit_breaker_metrics WHERE service_name = :serviceName")
    suspend fun delete(serviceName: String)
}
