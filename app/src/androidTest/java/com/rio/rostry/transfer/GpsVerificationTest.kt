package com.rio.rostry.transfer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.TransferWorkflowRepositoryImpl
import com.rio.rostry.utils.Resource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GpsVerificationTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    private lateinit var context: Context
    private lateinit var db: AppDatabase

    private class FakeNotifier : com.rio.rostry.utils.notif.TransferNotifier {
        override fun notifyInitiated(transferId: String, toUserId: String?) {}
        override fun notifyBuyerVerified(transferId: String) {}
        override fun notifyCompleted(transferId: String) {}
        override fun notifyCancelled(transferId: String) {}
        override fun notifyTimedOut(transferId: String) {}
        override fun notifyDisputeOpened(transferId: String) {}
    }

    private class FakeTraceabilityRepo : TraceabilityRepository {
        override suspend fun addBreedingRecord(record: com.rio.rostry.data.database.entity.BreedingRecordEntity) = Resource.Success(Unit)
        override suspend fun ancestors(productId: String, maxDepth: Int) = Resource.Success(emptyMap<Int,List<String>>())
        override suspend fun descendants(productId: String, maxDepth: Int) = Resource.Success(emptyMap<Int,List<String>>())
        override suspend fun breedingSuccess(parentId: String, partnerId: String) = Resource.Success(0 to 0)
        override suspend fun addLifecycleEvent(event: com.rio.rostry.data.database.entity.LifecycleEventEntity) = Resource.Success(Unit)
        override suspend fun verifyPath(productId: String, ancestorId: String, maxDepth: Int) = Resource.Success(true)
        override suspend fun verifyParentage(childId: String, parentId: String, partnerId: String) = Resource.Success(true)
        override suspend fun getTransferChain(productId: String) = Resource.Success<List<Any>>(emptyList())
        override fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String? =
            when {
                !maleId.isNullOrBlank() && !femaleId.isNullOrBlank() -> "FT_${'$'}maleId_${'$'}femaleId"
                !pairId.isNullOrBlank() -> "FT_PAIR_${'$'}pairId"
                else -> null
            }
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() { db.close() }

    @Test
    fun gps_over_100m_requires_explanation_and_persists_notes() = runBlocking {
        val transferRepo = TransferWorkflowRepositoryImpl(
            transferDao = db.transferDao(),
            verificationDao = db.transferVerificationDao(),
            disputeDao = db.disputeDao(),
            auditLogDao = db.auditLogDao(),
            notifier = FakeNotifier(),
            traceabilityRepository = FakeTraceabilityRepo()
        )

        val productId = "p1"
        val seller = "s1"
        val buyer = "b1"
        // Initiate with seller GPS at (0, 0)
        val initRes = transferRepo.initiate(
            productId = productId,
            fromUserId = seller,
            toUserId = buyer,
            amount = 100.0,
            currency = "INR",
            sellerPhotoUrl = null,
            gpsLat = 0.0,
            gpsLng = 0.0,
            conditionsJson = null,
            timeoutAt = null
        )
        require(initRes is Resource.Success)
        val transferId = initRes.data!!

        // Buyer location far away (>100m), no explanation => REJECTED
        val farRes = transferRepo.buyerVerify(
            transferId = transferId,
            buyerPhotoUrl = null,
            buyerGpsLat = 0.002, // ~222m latitude delta
            buyerGpsLng = 0.0,
            identityDocType = null,
            identityDocRef = null,
            identityDocNumber = null,
            buyerPhotoMetaJson = null,
            gpsExplanation = null
        )
        require(farRes is Resource.Success)
        val verifications1: List<TransferVerificationEntity> = db.transferVerificationDao().getByTransfer(transferId)
        val last1 = verifications1.last()
        assertEquals("REJECTED", last1.status)
        assertTrue(last1.notes?.contains("gpsExplanation=") != true)

        // Retry with explanation => persisted in notes
        val farExplainRes = transferRepo.buyerVerify(
            transferId = transferId,
            buyerPhotoUrl = null,
            buyerGpsLat = 0.002,
            buyerGpsLng = 0.0,
            identityDocType = null,
            identityDocRef = null,
            identityDocNumber = null,
            buyerPhotoMetaJson = null,
            gpsExplanation = "Traffic diversion, police checkpoint"
        )
        require(farExplainRes is Resource.Success)
        val verifications2: List<TransferVerificationEntity> = db.transferVerificationDao().getByTransfer(transferId)
        val last2 = verifications2.last()
        assertEquals("REJECTED", last2.status)
        assertNotNull(last2.notes)
        assertTrue(last2.notes!!.contains("gpsExplanation=Traffic diversion, police checkpoint"))
    }
}
