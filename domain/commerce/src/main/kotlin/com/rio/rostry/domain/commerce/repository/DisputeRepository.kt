package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Dispute
import com.rio.rostry.core.model.DisputeStatus
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

interface DisputeRepository {
    suspend fun createDispute(dispute: Dispute): Result<Unit>
    fun getDisputesForUser(userId: String): Flow<Result<List<Dispute>>>
    fun getAllOpenDisputes(): Flow<Result<List<Dispute>>>
    
    suspend fun resolveDispute(
        disputeId: String,
        status: DisputeStatus,
        resolution: String,
        adminId: String
    ): Result<Unit>
    
    suspend fun respondToDispute(
        disputeId: String,
        sellerId: String,
        evidence: String
    ): Result<Unit>
    
    suspend fun getDisputeById(disputeId: String): Result<Dispute?>
    fun getResolvedDisputes(): Flow<Result<List<Dispute>>>
}
