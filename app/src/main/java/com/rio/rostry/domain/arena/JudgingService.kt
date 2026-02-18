package com.rio.rostry.domain.arena

import com.rio.rostry.data.database.dao.ArenaParticipantDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ArenaParticipantEntity
import com.rio.rostry.data.database.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class JudgingService @Inject constructor(
    private val participantDao: ArenaParticipantDao,
    private val productDao: ProductDao
) {

    /**
     * Retrieves a list of participants for a given competition, 
     * including their full product details (for rendering).
     */
    fun getJudgingQueue(competitionId: String): Flow<List<Pair<ArenaParticipantEntity, ProductEntity>>> = flow {
        // 1. Get all participants for this competition (observing simple flow)
        participantDao.getParticipantsForCompetition(competitionId).collect { participants ->
            // 2. For each participant, fetch the full bird details
            // In a real app with backend, this would likely be a joined query or specialized endpoint.
            // Here we simulate it by fetching from local DB.
            val queue = participants.mapNotNull { participant ->
                // Use findById (suspend) instead of getProductByIdSync
                val product = productDao.findById(participant.birdId)
                if (product != null) {
                    participant to product
                } else {
                    null
                }
            }
            emit(queue)
        }
    }

    /**
     * Submits a vote for a participant.
     * In a real app, this would send data to a backend.
     * For now, we'll update the local entity to simulate "live" stats.
     */
    suspend fun submitVote(participantId: Long, score: Float) {
        val participant = participantDao.getParticipantById(participantId) ?: return
        
        // Simple moving average update or total accumulation
        val newTotalVotes = participant.totalVotes + 1
        // (CurrentAvg * CurrentCount + NewScore) / NewCount
        val newAverage = ((participant.averageScore * participant.totalVotes) + score) / newTotalVotes
        
        val updatedParticipant = participant.copy(
            totalVotes = newTotalVotes,
            averageScore = newAverage
        )
        
        participantDao.updateParticipant(updatedParticipant)
    }
}
