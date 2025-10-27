package com.rio.rostry.transfer

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.TransferWorkflowRepositoryImpl
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

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
        override suspend fun validateProductLineage(productId: String, expectedParentMaleId: String?, expectedParentFemaleId: String?) = Resource.Success(true)
        override suspend fun getProductHealthScore(productId: String) = Resource.Success(100)
        override suspend fun getTransferEligibilityReport(productId: String): Resource<Map<String, Any>> =
            Resource.Success(mapOf("eligible" to true, "reasons" to emptyList<String>()))
        override suspend fun getNodeMetadata(productId: String): Resource<com.rio.rostry.data.repository.NodeMetadata> =
            Resource.Success(
                com.rio.rostry.data.repository.NodeMetadata(
                    productId = productId,
                    name = "Test",
                    breed = null,
                    stage = null,
                    ageWeeks = null,
                    healthScore = 100,
                    lifecycleStatus = null
                )
            )
        override suspend fun getNodeMetadataBatch(productIds: List<String>): Resource<Map<String, com.rio.rostry.data.repository.NodeMetadata>> =
            Resource.Success(
                productIds.associateWith { id ->
                    com.rio.rostry.data.repository.NodeMetadata(
                        productId = id,
                        name = "Test",
                        breed = null,
                        stage = null,
                        ageWeeks = null,
                        healthScore = 100,
                        lifecycleStatus = null
                    )
                }
            )
        override fun createFamilyTree(maleId: String?, femaleId: String?, pairId: String?): String? =
            when {
                !maleId.isNullOrBlank() && !femaleId.isNullOrBlank() -> "FT_${'$'}maleId_${'$'}femaleId"
                !pairId.isNullOrBlank() -> "FT_PAIR_${'$'}pairId"
                else -> null
            }
        override suspend fun getEligibleProductsCount(farmerId: String) = Resource.Success(0)
        override suspend fun getComplianceAlerts(farmerId: String) = Resource.Success(emptyList<Pair<String, List<String>>>())
        override fun observeKycStatus(userId: String) = flowOf(true)
        override fun observeComplianceAlertsCount(farmerId: String) = flowOf(0)
        override fun observeEligibleProductsCount(farmerId: String) = flowOf(0)
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
        val productId = "p1"
        val seller = "s1"
        val buyer = "b1"

        val productValidator = com.rio.rostry.marketplace.validation.ProductValidator(
            traceabilityRepository = FakeTraceabilityRepo(),
            vaccinationDao = db.vaccinationRecordDao(),
            growthDao = db.growthRecordDao(),
            dailyLogDao = db.dailyLogDao(),
            quarantineDao = db.quarantineRecordDao()
        )
        val rbacGuard = mock(RbacGuard::class.java).also {
            `when`(it.canInitiateTransfer()).thenReturn(true)
        }
        val userRepository = mock(UserRepository::class.java).also {
            `when`(it.getUserById(seller)).thenReturn(
                flowOf(Resource.Success(com.rio.rostry.data.database.entity.UserEntity(
                    userId = seller,
                    verificationStatus = VerificationStatus.VERIFIED
                )))
            )
        }
        val currentUserProvider = mock(CurrentUserProvider::class.java).also {
            `when`(it.userIdOrNull()).thenReturn(seller)
        }
        val intel = mock(com.rio.rostry.notifications.IntelligentNotificationService::class.java)
        val transferRepo = TransferWorkflowRepositoryImpl(
            transferDao = db.transferDao(),
            verificationDao = db.transferVerificationDao(),
            disputeDao = db.disputeDao(),
            auditLogDao = db.auditLogDao(),
            notifier = FakeNotifier(),
            intelligentNotificationService = intel,
            traceabilityRepository = FakeTraceabilityRepo(),
            productValidator = productValidator,
            productDao = db.productDao(),
            quarantineDao = db.quarantineRecordDao(),
            rbacGuard = rbacGuard,
            userRepository = userRepository,
            currentUserProvider = currentUserProvider
        )

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
