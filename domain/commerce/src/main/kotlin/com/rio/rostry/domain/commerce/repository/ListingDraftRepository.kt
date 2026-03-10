package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.ListingDraft

/**
 * Repository for marketplace listing drafts.
 */
interface ListingDraftRepository {
    suspend fun getDraft(farmerId: String): ListingDraft?
    suspend fun saveDraft(draft: ListingDraft)
    suspend fun deleteDraft(draftId: String)
    suspend fun cleanupExpired()
}
