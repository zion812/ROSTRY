package com.rio.rostry.domain.social.service

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for the judging/scoring service.
 *
 * Manages competition judging queues and vote submission
 * for arena-style bird competitions.
 */
interface JudgingService {

    /**
     * Get the judging queue for a competition.
     * Returns participant-bird pairs for rendering.
     */
    fun getJudgingQueue(competitionId: String): Flow<List<Map<String, Any>>>

    /**
     * Submit a vote/score for a participant.
     */
    suspend fun submitVote(participantId: Long, score: Float)
}
