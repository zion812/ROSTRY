package com.rio.rostry.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getById(id: String): OrderEntity?

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun streamByBuyer(buyerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE buyerId = :userId OR sellerId = :userId ORDER BY createdAt DESC")
    fun streamByUser(userId: String): Flow<List<OrderEntity>>
}
