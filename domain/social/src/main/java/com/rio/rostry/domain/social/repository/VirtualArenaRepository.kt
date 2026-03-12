package com.rio.rostry.domain.social.repository

import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import com.rio.rostry.data.database.entity.ArenaParticipantEntity
import com.rio.rostry.data.database.entity.MyVotesEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.CompetitionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for Virtual Arena operations.
 * Migrated from app module as part of Phase 1 repository migration.
 */
interface VirtualArenaRepository {
    fun getCompetitions(status: CompetitionStatus): Flow<List<CompetitionEntryEntity>>
    suspend fun getCompetitionsByStatus(status: CompetitionStatus): List<CompetitionEntryEntity>
    suspend fun insertCompetition(competition: CompetitionEntryEntity)
    fun getMyVotes(competitionId: String): Flow<List<MyVotesEntity>>
    suspend fun castVote(competitionId: String, participantId: String, points: Int = 1)
    suspend fun getUnsyncedVotes(): List<MyVotesEntity>
    suspend fun markVotesSynced(ids: List<Long>)
    suspend fun enterCompetition(competitionId: String, bird: ProductEntity, ownerId: String)
    fun getParticipants(competitionId: String): Flow<List<ArenaParticipantEntity>>
    suspend fun getEligibleBirds(ownerId: String): List<ProductEntity>
}
