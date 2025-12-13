package com.rio.rostry.data.repository

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.utils.notif.TransferNotifier
import com.rio.rostry.marketplace.validation.ProductValidator
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransferWorkflowRepositoryTest {

    private val transferDao = mockk<TransferDao>(relaxed = true)
    private val transferVerificationDao = mockk<TransferVerificationDao>(relaxed = true)
    private val disputeDao = mockk<DisputeDao>(relaxed = true)
    private val auditLogDao = mockk<AuditLogDao>(relaxed = true)
    private val notifier = mockk<TransferNotifier>(relaxed = true)
    private val intelligentNotificationService = mockk<IntelligentNotificationService>(relaxed = true)
    private val traceabilityRepository = mockk<TraceabilityRepository>(relaxed = true)
    private val productValidator = mockk<ProductValidator>(relaxed = true)
    private val productDao = mockk<ProductDao>(relaxed = true)
    private val quarantineDao = mockk<QuarantineRecordDao>(relaxed = true)
    private val rbacGuard = mockk<RbacGuard>(relaxed = true)
    private val outboxDao = mockk<OutboxDao>(relaxed = true)
    private val gson = Gson()
    private val currentUserProvider = mockk<CurrentUserProvider>(relaxed = true)

    private lateinit var repository: TransferWorkflowRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = TransferWorkflowRepositoryImpl(
            transferDao = transferDao,
            verificationDao = transferVerificationDao,
            disputeDao = disputeDao,
            auditLogDao = auditLogDao,
            notifier = notifier,
            intelligentNotificationService = intelligentNotificationService,
            traceabilityRepository = traceabilityRepository,
            productValidator = productValidator,
            productDao = productDao,
            quarantineDao = quarantineDao,
            rbacGuard = rbacGuard,
            outboxDao = outboxDao,
            gson = gson,
            currentUserProvider = currentUserProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `complete updates product ownership`() = runTest(testDispatcher) {
        val transferId = "t1"
        val productId = "p1"
        val sellerId = "seller1"
        val buyerId = "buyer1"

        val transfer = TransferEntity(
            transferId = transferId,
            productId = productId,
            fromUserId = sellerId,
            toUserId = buyerId,
            status = "PENDING",
            amount = 100.0,
            type = "OWNERSHIP"
        )
        val product = ProductEntity(
            productId = productId,
            sellerId = sellerId,
            status = "available"
        )

        coEvery { transferDao.getById(transferId) } returns transfer
        coEvery { productDao.findById(productId) } returns product
        coEvery { traceabilityRepository.getTransferEligibilityReport(any()) } returns Resource.Success(mapOf("eligible" to true))
        every { currentUserProvider.userIdOrNull() } returns sellerId
        coEvery { rbacGuard.canInitiateTransfer() } returns true

        val result = repository.complete(transferId)

        assertTrue(result is Resource.Success)
        
        // Verify transfer status update
        coVerify { transferDao.updateStatusAndTimestamps(transferId, "COMPLETED", any(), any()) }

        // Verify product ownership update
        val slot = slot<ProductEntity>()
        coVerify { productDao.upsert(capture(slot)) }
        assertEquals(buyerId, slot.captured.sellerId)
        assertEquals("private", slot.captured.status)
        assertTrue(slot.captured.dirty)

        // Verify outbox queueing for product
        coVerify { outboxDao.insert(match { it.entityType == "PRODUCT" && it.entityId == productId }) }
    }
}
