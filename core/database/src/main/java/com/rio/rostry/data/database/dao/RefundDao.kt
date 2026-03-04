package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.RefundEntity

@Dao
interface RefundDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(refund: RefundEntity)

    @Query("SELECT * FROM refunds WHERE paymentId = :paymentId")
    suspend fun listByPayment(paymentId: String): List<RefundEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM refunds WHERE paymentId = :paymentId")
    suspend fun totalRefundedForPayment(paymentId: String): Double
}
