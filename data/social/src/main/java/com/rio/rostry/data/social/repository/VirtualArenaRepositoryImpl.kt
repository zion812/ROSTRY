package com.rio.rostry.data.social.repository

import com.rio.rostry.data.database.dao.VirtualArenaDao
import com.rio.rostry.data.database.dao.ArenaParticipantDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import com.rio.rostry.data.database.entity.ArenaParticipantEntity
import com.rio.rostry.data.database.entity.MyVotesEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.CompetitionStatus
import com.rio.rostry.domain.social.repository.VirtualArenaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualArenaRepositoryImpl @Inject constructor(
    private val dao: VirtualArenaDao,
    private val participantDao: ArenaParticipantDao,
    private val productDao: ProductDao
) : VirtualArenaRepository {

    override fun getCompetitions(status: CompetitionStatus): Flow<List<CompetitionEntryEntity>> =
        dao.getCompetitionsByStatus(status)

    override suspend fun getCompetitionsByStatus(status: CompetitionStatus): List<CompetitionEntryEntity> =
        dao.getCompetitionsByStatusOneShot(status)

    override suspend fun insertCompetition(competition: CompetitionEntryEntity) =
        dao.insertCompetition(competition)

    override fun getMyVotes(competitionId: String): Flow<List<MyVotesEntity>> =
        dao.getMyVotesForCompetition(competitionId)

    override suspend fun castVote(competitionId: String, participantId: String, points: Int) {
        val vote = MyVotesEntity(competitionId = competitionId, participantId = participantId, points = points, synced = false)
        dao.castVote(vote)
    }

    override suspend fun getUnsyncedVotes(): List<MyVotesEntity> = dao.getUnsyncedVotes()
    override suspend fun markVotesSynced(ids: List<Long>) = dao.markVotesAsSynced(ids)

    override suspend fun enterCompetition(competitionId: String, bird: ProductEntity, ownerId: String) {
        val entry = ArenaParticipantEntity(
            competitionId = competitionId,
            birdId = bird.productId,
            ownerId = ownerId,
            birdName = bird.name,
            birdImageUrl = bird.imageUrls.firstOrNull(),
            breed = bird.breed ?: "Unknown",
            entryTime = System.currentTimeMillis()
        )
        participantDao.insertParticipant(entry)
    }

    override fun getParticipants(competitionId: String): Flow<List<ArenaParticipantEntity>> =
        participantDao.getParticipantsForCompetition(competitionId)

    override suspend fun getEligibleBirds(ownerId: String): List<ProductEntity> =
        productDao.getProductsBySellerSuspend(ownerId).filter {
            it.condition != "Dead" && it.category != "Egg"
        }
}
