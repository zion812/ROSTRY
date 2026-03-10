package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result

/**
 * Repository contract for managing user feedback and grievances.
 * 
 * Handles submission of user feedback to the system.
 */
interface FeedbackRepository {
    /**
     * Submits user feedback.
     * 
     * @param userId The user ID
     * @param content The feedback content
     * @param type The feedback type (e.g., "BUG", "FEATURE_REQUEST", "COMPLAINT")
     * @return Result indicating success or failure
     */
    suspend fun submitFeedback(
        userId: String,
        content: String,
        type: String
    ): Result<Boolean>
}

