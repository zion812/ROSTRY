package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.model.ModerationAction
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.admin.model.ContentType
import com.rio.rostry.domain.admin.model.ModerationItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for moderation operations.
 */
interface ModerationRepository {
    fun getPendingItems(): Flow<List<String>>
    suspend fun approveItem(itemId: String, itemType: String): Result<Unit>
    suspend fun rejectItem(itemId: String, itemType: String, reason: String): Result<Unit>
    suspend fun logAction(action: ModerationAction): Result<Unit>
    fun getModerationHistory(moderatorId: String): Flow<List<ModerationAction>>
    suspend fun getQueueCount(): Result<Int>

    // Methods called by the admin-dashboard ModerationViewModel
    fun getModerationQueue(): Flow<List<ModerationItem>>
    suspend fun approve(id: String, type: ContentType)
    suspend fun reject(id: String, type: ContentType, reason: String)
}

