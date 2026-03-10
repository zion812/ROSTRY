package com.rio.rostry.core.common.sync

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import com.rio.rostry.core.common.sync.SyncConflict

data class SyncStats(
    val pushed: Int = 0,
    val pulled: Int = 0,
    val outboxProcessed: Int = 0,
    val timedOutDomains: List<String> = emptyList()
)

data class SyncProgress(
    val totalPending: Int,
    val processed: Int,
    val currentEntityType: String,
    val errors: List<String>
)

interface SyncManager {
    suspend fun syncAll(now: Long = System.currentTimeMillis()): Resource<SyncStats>
    suspend fun syncFarmerDigitalFarm(now: Long = System.currentTimeMillis()): Resource<SyncStats>
    val syncProgressFlow: Flow<SyncProgress>
    
    suspend fun detectConflicts(): List<SyncConflict>
    suspend fun resolveConflict(entityId: String, entityType: String, useLocal: Boolean)
}
