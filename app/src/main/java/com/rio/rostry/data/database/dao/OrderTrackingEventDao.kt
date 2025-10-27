package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.OrderTrackingEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderTrackingEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: OrderTrackingEventEntity)

    @Query("SELECT * FROM order_tracking_events WHERE orderId = :orderId ORDER BY timestamp ASC")
    fun observeByOrder(orderId: String): Flow<List<OrderTrackingEventEntity>>
}
