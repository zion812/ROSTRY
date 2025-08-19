package com.rio.rostry.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

open class FirestoreRepository {
    protected val firestore = FirebaseFirestore.getInstance()

    protected suspend fun <T : Any> addDocument(
        collection: String,
        data: T
    ): Flow<Result<String>> = flow {
        try {
            val document = firestore.collection(collection).add(data).await()
            emit(Result.success(document.id))
        } catch (e: FirebaseFirestoreException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    protected suspend fun <T : Any> updateDocument(
        collection: String,
        documentId: String,
        data: T
    ): Flow<Result<Unit>> = flow {
        try {
            firestore.collection(collection).document(documentId).set(data).await()
            emit(Result.success(Unit))
        } catch (e: FirebaseFirestoreException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    protected suspend fun <T> getDocument(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): Flow<Result<T?>> = flow {
        try {
            val document = firestore.collection(collection).document(documentId).get().await()
            if (document.exists()) {
                val data = document.toObject(clazz)
                emit(Result.success(data))
            } else {
                emit(Result.success(null))
            }
        } catch (e: FirebaseFirestoreException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    protected suspend fun <T> getDocuments(
        collection: String,
        clazz: Class<T>
    ): Flow<Result<List<T>>> = flow {
        try {
            val querySnapshot = firestore.collection(collection).get().await()
            val documents = querySnapshot.mapNotNull { it.toObject(clazz) }
            emit(Result.success(documents))
        } catch (e: FirebaseFirestoreException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    protected suspend fun <T> getDocumentsWhereEqualTo(
        collection: String,
        field: String,
        value: Any,
        clazz: Class<T>
    ): Flow<Result<List<T>>> = flow {
        try {
            val querySnapshot = firestore.collection(collection).whereEqualTo(field, value).get().await()
            val documents = querySnapshot.mapNotNull { it.toObject(clazz) }
            emit(Result.success(documents))
        } catch (e: FirebaseFirestoreException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Helper method to convert DocumentSnapshot to a specific class
     */
    private fun <T> DocumentSnapshot.toObject(clazz: Class<T>): T? {
        return try {
            this.toObject(clazz)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Helper method to convert QuerySnapshot to a list of objects
     */
    private fun <T> QuerySnapshot.mapNotNull(transform: (DocumentSnapshot) -> T?): List<T> {
        return this.documents.mapNotNull { transform(it) }
    }
}