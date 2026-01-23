package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DisputeRepository {
    
    suspend fun createDispute(dispute: DisputeEntity): Resource<Unit>
    
    fun getDisputesForUser(userId: String): Flow<Resource<List<DisputeEntity>>>
    
    fun getAllOpenDisputes(): Flow<Resource<List<DisputeEntity>>>
    
    suspend fun resolveDispute(disputeId: String, status: DisputeStatus, resolution: String, adminId: String): Resource<Unit>
    
    suspend fun getDisputeById(disputeId: String): Resource<DisputeEntity?>
}
