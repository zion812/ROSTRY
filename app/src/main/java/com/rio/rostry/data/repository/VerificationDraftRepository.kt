package com.rio.rostry.data.repository

import com.rio.rostry.ui.verification.state.VerificationFormState
import kotlinx.coroutines.flow.Flow

interface VerificationDraftRepository {
    suspend fun saveDraft(userId: String, formState: VerificationFormState)
    suspend fun loadDraft(userId: String): VerificationFormState?
    suspend fun deleteDraft(userId: String)
    fun observeDraft(userId: String): Flow<VerificationFormState?>
}
