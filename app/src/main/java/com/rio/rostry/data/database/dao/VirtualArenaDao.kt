package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import com.rio.rostry.data.database.entity.MyVotesEntity
import com.rio.rostry.domain.model.CompetitionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VirtualArenaDao {

    // --- Competitions ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetition(competition: CompetitionEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetitions(competitions: List<CompetitionEntryEntity>)

    @Query("SELECT * FROM competitions WHERE status = :status ORDER BY endTime ASC")
    fun getCompetitionsByStatus(status: CompetitionStatus): Flow<List<CompetitionEntryEntity>>

    @Query("SELECT * FROM competitions WHERE competitionId = :id")
    suspend fun getCompetitionById(id: String): CompetitionEntryEntity?

    // --- Voting ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun castVote(vote: MyVotesEntity)

    @Query("SELECT * FROM my_votes WHERE competitionId = :competitionId")
    fun getMyVotesForCompetition(competitionId: String): Flow<List<MyVotesEntity>>

    @Query("SELECT * FROM my_votes WHERE synced = 0")
    suspend fun getUnsyncedVotes(): List<MyVotesEntity>

    @Query("UPDATE my_votes SET synced = 1, syncError = null WHERE id IN (:ids)")
    suspend fun markVotesAsSynced(ids: List<Long>)

    @Query("SELECT SUM(points) FROM my_votes WHERE competitionId = :competitionId AND participantId = :participantId")
    fun getMyTotalPointsForParticipant(competitionId: String, participantId: String): Flow<Int?>
}
