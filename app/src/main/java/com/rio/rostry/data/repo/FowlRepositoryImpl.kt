package com.rio.rostry.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.rio.rostry.data.local.FowlDao
import com.rio.rostry.data.local.FowlRecordDao
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.FowlRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FowlRepositoryImpl @Inject constructor(
    private val fowlDao: FowlDao,
    private val fowlRecordDao: FowlRecordDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FowlRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""


    private suspend fun syncFowlData() {
        if (userId.isEmpty()) return
        try {
            val snapshot = firestore.collection("users").document(userId).collection("fowls").get().await()
            val fowls = snapshot.toObjects(Fowl::class.java)
            fowlDao.insertFowls(fowls)
        } catch (e: Exception) {
            Log.e("FowlRepo", "Error syncing fowl data", e)
        }
    }



    override suspend fun addFowl(fowl: Fowl, photoUri: android.net.Uri?) {
        // This is a placeholder implementation. The actual implementation should handle photo upload.
        val fowlWithUser = fowl.copy(userId = userId)
        fowlDao.insertFowl(fowlWithUser)
        try {
            firestore.collection("users").document(userId).collection("fowls").document(fowl.id).set(fowlWithUser).await()
        } catch (e: Exception) {
            Log.e("FowlRepo", "Error adding fowl to Firestore", e)
        }
    }

    override fun getFowls(): Flow<List<Fowl>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return fowlDao.getFowls(userId)
    }

    override fun getFowlById(fowlId: String): Flow<Fowl?> {
        val userId = auth.currentUser?.uid ?: return flowOf(null)
        return fowlDao.getFowlById(fowlId, userId)
    }

    override suspend fun addFowlRecord(fowlRecord: FowlRecord) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val recordWithUser = fowlRecord.copy(userId = userId)
            fowlRecordDao.insertRecord(recordWithUser)
            try {
                firestore.collection("users").document(userId)
                    .collection("fowls").document(recordWithUser.fowlId)
                    .collection("records").document(recordWithUser.id)
                    .set(recordWithUser).await()
            } catch (e: Exception) {
                Log.e("FowlRepo", "Error adding fowl record to Firestore", e)
            }
        } else {
            Log.e("FowlRepositoryImpl", "User not logged in, cannot add record")
        }
    }

    override fun getFowlRecords(fowlId: String): Flow<List<FowlRecord>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        // TODO: Implement fetching records from the fowls/{fowlId}/records subcollection in Firestore
        return fowlRecordDao.getFowlRecords(fowlId, userId)
    }

    override suspend fun sync() {
        syncFowlData()
        syncAllFowlRecords()
    }

    private suspend fun syncAllFowlRecords() {
        if (userId.isEmpty()) return
        try {
            val fowlsSnapshot = firestore.collection("users").document(userId).collection("fowls").get().await()
            for (fowlDocument in fowlsSnapshot.documents) {
                val fowlId = fowlDocument.id
                val recordsSnapshot = firestore.collection("users").document(userId)
                    .collection("fowls").document(fowlId)
                    .collection("records").get().await()
                val records = recordsSnapshot.toObjects(FowlRecord::class.java).map { it.copy(userId = userId) }
                fowlRecordDao.insertRecords(records)
            }
        } catch (e: Exception) {
            Log.e("FowlRepo", "Error syncing all fowl records", e)
        }
    }

    override fun getOffspring(fowlId: String): Flow<List<Fowl>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return fowlDao.getOffspring(fowlId, userId)
    }

    override suspend fun updateFowlOwner(fowlId: String, newOwnerId: String) {
        val currentOwnerId = auth.currentUser?.uid ?: return

        val fowlRef = firestore.collection("users").document(currentOwnerId).collection("fowls").document(fowlId)

        try {
            val fowlDocument = fowlRef.get().await()
            val fowl = fowlDocument.toObject<Fowl>()

            if (fowl != null) {
                val newFowl = fowl.copy(userId = newOwnerId)
                val newFowlRef = firestore.collection("users").document(newOwnerId).collection("fowls").document(fowlId)

                firestore.runBatch {
                    it.set(newFowlRef, newFowl)
                    it.delete(fowlRef)
                }.await()

                // Update local database
                fowlDao.updateFowlOwner(fowlId, newOwnerId)

            } else {
                Log.e("FowlRepo", "Fowl with id $fowlId not found for user $currentOwnerId")
            }
        } catch (e: Exception) {
            Log.e("FowlRepo", "Error updating fowl owner", e)
            throw e // Re-throw to be handled by the caller
        }
    }
}
