package com.rio.rostry.domain.admin.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for moderating content (posts, listings, etc.).
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface ModerateContentUseCase {
    /**
     * Approve a pending content item.
     * @param itemId The item ID to approve
     * @param itemType The type of item (post, listing, user, etc.)
     * @return Result indicating success or error
     */
    suspend fun approveItem(itemId: String, itemType: String): Result<Unit>

    /**
     * Reject a pending content item.
     * @param itemId The item ID to reject
     * @param itemType The type of item
     * @param reason The reason for rejection
     * @return Result indicating success or error
     */
    suspend fun rejectItem(itemId: String, itemType: String, reason: String): Result<Unit>
}
