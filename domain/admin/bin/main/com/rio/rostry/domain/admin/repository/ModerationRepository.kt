package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.model.ModerationAction
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for moderation operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface ModerationRepository {
    /**
     * Get pending moderation items.
     * @return Flow of items pending moderation
     */
    fun getPendingItems(): Flow<List<String>>

    /**
     * Approve an item.
     * @param itemId The item ID
     * @param itemType The type of item (post, listing, user, etc.)
     * @return Result indicating success or error
     */
    suspend fun approveItem(itemId: String, itemType: String): Result<Unit>

    /**
     * Reject an item.
     * @param itemId The item ID
     * @param itemType The type of item
     * @param reason The rejection reason
     * @return Result indicating success or error
     */
    suspend fun rejectItem(itemId: String, itemType: String, reason: String): Result<Unit>

    /**
     * Log a moderation action.
     * @param action The moderation action
     * @return Result indicating success or error
     */
    suspend fun logAction(action: ModerationAction): Result<Unit>

    /**
     * Get moderation history.
     * @param moderatorId The moderator ID
     * @return Flow of moderation actions
     */
    fun getModerationHistory(moderatorId: String): Flow<List<ModerationAction>>

    /**
     * Get moderation queue count.
     * @return Result containing the queue count or error
     */
    suspend fun getQueueCount(): Result<Int>
}
