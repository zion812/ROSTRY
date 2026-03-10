package com.rio.rostry.data.commerce.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.ListingDraft
import com.rio.rostry.domain.commerce.repository.ListingDraftRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ListingDraftRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class ListingDraftRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ListingDraftRepository {

    private val draftsCollection = firestore.collection("listing_drafts")

    override suspend fun getDraft(farmerId: String): ListingDraft? {
        return try {
            val snapshot = draftsCollection
                .whereEqualTo("farmerId", farmerId)
                .limit(1)
                .get()
                .await()
            
            snapshot.documents.firstOrNull()?.toObject(ListingDraft::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveDraft(draft: ListingDraft) {
        try {
            val draftId = draft.draftId.ifEmpty { 
                draftsCollection.document().id 
            }
            val updatedDraft = draft.copy(
                draftId = draftId,
                updatedAt = System.currentTimeMillis()
            )
            draftsCollection.document(draftId).set(updatedDraft).await()
        } catch (e: Exception) {
            // Log error or handle as needed
            throw e
        }
    }

    override suspend fun deleteDraft(draftId: String) {
        try {
            draftsCollection.document(draftId).delete().await()
        } catch (e: Exception) {
            // Log error or handle as needed
            throw e
        }
    }

    override suspend fun cleanupExpired() {
        try {
            val now = System.currentTimeMillis()
            val snapshot = draftsCollection
                .whereLessThan("expiresAt", now)
                .get()
                .await()
            
            snapshot.documents.forEach { document ->
                document.reference.delete()
            }
        } catch (e: Exception) {
            // Log error or handle as needed
            throw e
        }
    }
}
