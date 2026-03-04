package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rio.rostry.data.database.entity.ArenaParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArenaParticipantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ArenaParticipantEntity): Long

    @Query("SELECT * FROM arena_participants WHERE competitionId = :competitionId ORDER BY totalVotes DESC")
    fun getParticipantsForCompetition(competitionId: String): Flow<List<ArenaParticipantEntity>>

    @Query("SELECT * FROM arena_participants WHERE competitionId = :competitionId AND ownerId = :ownerId")
    suspend fun getParticipantByOwner(competitionId: String, ownerId: String): ArenaParticipantEntity?

    @Query("SELECT COUNT(*) FROM arena_participants WHERE competitionId = :competitionId")
    suspend fun getParticipantCount(competitionId: String): Int
    
    @Query("DELETE FROM arena_participants WHERE competitionId = :competitionId AND birdId = :birdId")
    suspend fun removeParticipant(competitionId: String, birdId: String)

    @Query("SELECT * FROM arena_participants WHERE id = :id")
    suspend fun getParticipantById(id: Long): ArenaParticipantEntity?

    @androidx.room.Update
    suspend fun updateParticipant(participant: ArenaParticipantEntity)
}
