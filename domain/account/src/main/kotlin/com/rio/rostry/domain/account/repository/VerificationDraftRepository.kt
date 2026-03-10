package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.VerificationDraft
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing verification draft data.
 * 
 * Handles saving, loading, and observing verification form state
 * for farmer/enthusiast verification processes.
 */
interface VerificationDraftRepository {
    /**
     * Saves a verification draft for a user.
     */
    suspend fun saveDraft(userId: String, draft: VerificationDraft): Result<Unit>
    
    /**
     * Loads a verification draft for a user.
     */
    suspend fun loadDraft(userId: String): Result<VerificationDraft?>
    
    /**
     * Deletes a verification draft for a user.
     */
    suspend fun deleteDraft(userId: String): Result<Unit>
    
    /**
     * Observes verification draft changes for a user.
     */
    fun observeDraft(userId: String): Flow<VerificationDraft?>
}

