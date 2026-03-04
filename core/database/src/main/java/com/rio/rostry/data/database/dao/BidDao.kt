package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.BidEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bid: BidEntity)

    @Query("SELECT * FROM bids WHERE auctionId = :auctionId ORDER BY amount DESC, placedAt DESC")
    fun observeBids(auctionId: String): Flow<List<BidEntity>>

    @Query("SELECT * FROM bids WHERE auctionId = :auctionId ORDER BY amount DESC, placedAt DESC LIMIT 1")
    suspend fun getHighestBid(auctionId: String): BidEntity?
}
