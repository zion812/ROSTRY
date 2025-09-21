package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.AuctionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuctionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(auction: AuctionEntity)

    @Update
    suspend fun update(auction: AuctionEntity)

    @Query("SELECT * FROM auctions WHERE auctionId = :id")
    fun observeById(id: String): Flow<AuctionEntity?>

    @Query("SELECT * FROM auctions WHERE isActive = 1 AND startsAt <= :now AND endsAt >= :now")
    fun activeAuctions(now: Long = System.currentTimeMillis()): Flow<List<AuctionEntity>>

    @Query("SELECT * FROM auctions WHERE productId = :productId LIMIT 1")
    suspend fun findByProduct(productId: String): AuctionEntity?
}
