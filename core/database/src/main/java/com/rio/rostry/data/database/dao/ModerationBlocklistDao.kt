package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.ModerationBlocklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModerationBlocklistDao {
    @Query("SELECT * FROM moderation_blocklist")
    fun getAllTerms(): Flow<List<ModerationBlocklistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerm(term: ModerationBlocklistEntity)

    @Delete
    suspend fun deleteTerm(term: ModerationBlocklistEntity)
    
    @Query("SELECT term FROM moderation_blocklist")
    suspend fun getTermsList(): List<String>
}
