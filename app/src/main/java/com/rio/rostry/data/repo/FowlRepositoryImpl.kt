package com.rio.rostry.data.repo

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.rio.rostry.data.local.FowlDao
import com.rio.rostry.data.local.FowlRecordDao
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.FowlRecord
import com.rio.rostry.utils.NetworkMonitor
import com.rio.rostry.utils.PerformanceLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.system.measureTimeMillis
import androidx.paging.Pager
import kotlinx.coroutines.flow.first
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FowlRepositoryImpl @Inject constructor(
    private val fowlDao: FowlDao,
    private val fowlRecordDao: FowlRecordDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val networkMonitor: NetworkMonitor,
    private val performanceLogger: PerformanceLogger
) : FowlRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: ""





    override suspend fun addFowl(fowl: Fowl, photoUri: android.net.Uri?) {
        // This is a placeholder implementation. The actual implementation should handle photo upload.
        val fowlWithUser = fowl.copy(userId = userId)
        fowlDao.insertFowl(fowlWithUser)
        if (networkMonitor.isConnected().first()) {
            var success = false
            val duration = measureTimeMillis {
                try {
                    firestore.collection("users").document(userId).collection("fowls").document(fowl.id).set(fowlWithUser).await()
                    success = true
                } catch (e: Exception) {
                    Log.e("FowlRepo", "Error adding fowl to Firestore", e)
                }
            }
            performanceLogger.logNetworkRequest("addFowl", duration, success)
        }
    }

    override fun getFowls(): Flow<PagingData<Fowl>> {
        val userId = auth.currentUser?.uid ?: return flowOf(PagingData.empty())
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { fowlDao.getFowls(userId) }
        ).flow
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
            if (networkMonitor.isConnected().first()) {
                var success = false
                val duration = measureTimeMillis {
                    try {
                        firestore.collection("users").document(userId)
                            .collection("fowls").document(recordWithUser.fowlId)
                            .collection("records").document(recordWithUser.id)
                            .set(recordWithUser).await()
                        success = true
                    } catch (e: Exception) {
                        Log.e("FowlRepo", "Error adding fowl record to Firestore", e)
                    }
                }
                performanceLogger.logNetworkRequest("addFowlRecord", duration, success)
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


    override fun getOffspring(fowlId: String): Flow<List<Fowl>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptyList())
        return fowlDao.getOffspring(fowlId, userId)
    }

    override suspend fun updateFowlOwner(fowlId: String, newOwnerId: String) {
        val currentOwnerId = auth.currentUser?.uid ?: return

        // Local update is applied immediately
        fowlDao.updateFowlOwner(fowlId, newOwnerId)

        if (networkMonitor.isConnected().first()) {
            val fowlRef = firestore.collection("users").document(currentOwnerId).collection("fowls").document(fowlId)
            var success = false
            val duration = measureTimeMillis {
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
                        success = true

                    } else {
                        Log.e("FowlRepo", "Fowl with id $fowlId not found for user $currentOwnerId")
                    }
                } catch (e: Exception) {
                    Log.e("FowlRepo", "Error updating fowl owner", e)
                    // The local change is already made, sync will handle it later.
                }
            }
            performanceLogger.logNetworkRequest("updateFowlOwner", duration, success)
        }
    }
}
