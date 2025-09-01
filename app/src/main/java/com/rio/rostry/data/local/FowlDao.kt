package com.rio.rostry.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.paging.PagingSource
import com.rio.rostry.data.models.Fowl
import kotlinx.coroutines.flow.Flow

@Dao
interface FowlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFowl(fowl: Fowl)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFowls(fowls: List<Fowl>)

    @Query("SELECT * FROM fowl WHERE id = :fowlId AND userId = :userId")
    fun getFowlById(fowlId: String, userId: String): Flow<Fowl?>

    @Query("SELECT * FROM fowl WHERE userId = :userId")
    fun getFowls(userId: String): PagingSource<Int, Fowl>

    @Query("SELECT * FROM fowl WHERE (sireId = :fowlId OR damId = :fowlId) AND userId = :userId")
    fun getOffspring(fowlId: String, userId: String): Flow<List<Fowl>>

    @Query("UPDATE fowl SET userId = :newOwnerId WHERE id = :fowlId")
    suspend fun updateFowlOwner(fowlId: String, newOwnerId: String)
}
