package com.rio.rostry.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.local.entities.TransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transfer: TransferEntity)

    @Query("SELECT * FROM transfers WHERE id = :id")
    suspend fun getById(id: String): TransferEntity?

    @Query("SELECT * FROM transfers WHERE fromUserId = :userId OR toUserId = :userId ORDER BY createdAt DESC")
    fun streamByUser(userId: String): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers WHERE productId = :productId ORDER BY createdAt ASC")
    suspend fun listByProduct(productId: String): List<TransferEntity>
}
