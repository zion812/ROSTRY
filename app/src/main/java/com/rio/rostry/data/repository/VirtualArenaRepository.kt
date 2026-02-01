package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.VirtualArenaDao
import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import com.rio.rostry.data.database.entity.MyVotesEntity
import com.rio.rostry.domain.model.CompetitionStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class VirtualArenaRepository @Inject constructor(
    private val dao: VirtualArenaDao
) {
    fun getCompetitions(status: CompetitionStatus): Flow<List<CompetitionEntryEntity>> {
        return dao.getCompetitionsByStatus(status)
    }
    
    /**
     * Get competitions by status as a one-shot suspend function.
     */
    suspend fun getCompetitionsByStatus(status: CompetitionStatus): List<CompetitionEntryEntity> {
        return dao.getCompetitionsByStatusOneShot(status)
    }
    
    /**
     * Insert a new competition.
     */
    suspend fun insertCompetition(competition: CompetitionEntryEntity) {
        dao.insertCompetition(competition)
    }

    fun getMyVotes(competitionId: String): Flow<List<MyVotesEntity>> {
        return dao.getMyVotesForCompetition(competitionId)
    }

    suspend fun castVote(competitionId: String, participantId: String, points: Int = 1) {
        val vote = MyVotesEntity(
            competitionId = competitionId,
            participantId = participantId,
            points = points,
            synced = false
        )
        dao.castVote(vote)
        // Note: SyncWorker will pick this up later for batch syncing
    }

    // This method would be called by the SyncWorker
    suspend fun getUnsyncedVotes(): List<MyVotesEntity> {
        return dao.getUnsyncedVotes()
    }

    suspend fun markVotesSynced(ids: List<Long>) {
        dao.markVotesAsSynced(ids)
    }
}
