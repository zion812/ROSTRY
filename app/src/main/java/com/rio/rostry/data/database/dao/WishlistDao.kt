package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE userId = :userId AND productId = :productId")
    suspend fun remove(userId: String, productId: String)

    @Query("SELECT * FROM wishlist WHERE userId = :userId ORDER BY addedAt DESC")
    fun observe(userId: String): Flow<List<WishlistEntity>>
}
