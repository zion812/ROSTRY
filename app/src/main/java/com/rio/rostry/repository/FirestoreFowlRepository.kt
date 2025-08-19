package com.rio.rostry.repository

import com.rio.rostry.data.models.Fowl
import kotlinx.coroutines.flow.Flow

class FirestoreFowlRepository : FirestoreRepository() {
    private val fowlsCollection = "fowls"

    suspend fun createFowl(fowl: Fowl): Flow<Result<String>> {
        return addDocument(fowlsCollection, fowl)
    }

    suspend fun updateFowl(fowlId: String, fowl: Fowl): Flow<Result<Unit>> {
        return updateDocument(fowlsCollection, fowlId, fowl)
    }

    suspend fun getFowl(fowlId: String): Flow<Result<Fowl?>> {
        return getDocument(fowlsCollection, fowlId, Fowl::class.java)
    }

    suspend fun getFowlsByOwner(ownerId: String): Flow<Result<List<Fowl>>> {
        return getDocumentsWhereEqualTo(fowlsCollection, "ownerId", ownerId, Fowl::class.java)
    }
    
    suspend fun getAllFowls(): Flow<Result<List<Fowl>>> {
        return getDocuments(fowlsCollection, Fowl::class.java)
    }
}