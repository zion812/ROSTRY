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
    private val dao: VirtualArenaDao,
    private val participantDao: com.rio.rostry.data.database.dao.ArenaParticipantDao,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao
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

    // --- Entry & Participants ---
    
    suspend fun enterCompetition(
        competitionId: String, 
        bird: com.rio.rostry.data.database.entity.ProductEntity, 
        ownerId: String
    ) {
        val entry = com.rio.rostry.data.database.entity.ArenaParticipantEntity(
            competitionId = competitionId,
            birdId = bird.productId,
            ownerId = ownerId,
            birdName = bird.name,
            birdImageUrl = bird.imageUrls.firstOrNull(),
            breed = bird.breed ?: "Unknown",
            entryTime = System.currentTimeMillis()
        )
        participantDao.insertParticipant(entry)
        
        // Update participant count in competition
        // Note: Ideally this should be a transaction or aggregation, 
        // but for now we'll just rely on the count from participants table when querying
    }
    
    fun getParticipants(competitionId: String): Flow<List<com.rio.rostry.data.database.entity.ArenaParticipantEntity>> {
        return participantDao.getParticipantsForCompetition(competitionId)
    }
    
    suspend fun getEligibleBirds(ownerId: String): List<com.rio.rostry.data.database.entity.ProductEntity> {
        // Return birds owned by user, alive, and maybe adult
        // For now, simpler filter: all owned birds except eggs/dead
        return productDao.getProductsBySellerSuspend(ownerId).filter { 
            // Add more specific filtering logic here if needed (age, health, etc during selection)
             it.condition != "Dead" && it.category != "Egg"
        }
    }
}
