package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payment: PaymentEntity)

    @Update
    suspend fun update(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    suspend fun findById(paymentId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE idempotencyKey = :key LIMIT 1")
    suspend fun findByIdempotencyKey(key: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt ASC")
    fun observeByOrder(orderId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeByUser(userId: String): Flow<List<PaymentEntity>>
}
