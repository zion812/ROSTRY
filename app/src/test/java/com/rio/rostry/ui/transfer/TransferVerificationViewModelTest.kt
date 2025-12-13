package com.rio.rostry.ui.transfer

import android.content.Context
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.repository.TransferWorkflowRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.rio.rostry.utils.media.MediaUploadManager

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TransferVerificationViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private fun makeVm(transferId: String = "t-1"): Pair<TransferVerificationViewModel, TransferWorkflowRepository> {
        val tDao = mockk<TransferDao>()
        val vDao = mockk<TransferVerificationDao>()
        val aDao = mockk<AuditLogDao>()
        val dDao = mockk<DisputeDao>()
        val app = mockk<Context>(relaxed = true)
        val gson = com.google.gson.Gson()
        val workflow = mockk<TransferWorkflowRepository>()
        val uploadMgr = mockk<MediaUploadManager>()
        val analytics = mockk<EnthusiastAnalyticsTracker>(relaxed = true)
        val currentUser = mockk<CurrentUserProvider>()
        every { currentUser.userIdOrNull() } returns "u-1"
        val connectivity = mockk<ConnectivityManager>()
        every { connectivity.isOnWifi() } returns true
        val uploadTaskDao = mockk<com.rio.rostry.data.database.dao.UploadTaskDao>(relaxed = true)

        val transfer = TransferEntity(
            transferId = transferId,
            productId = "p-1",
            fromUserId = "u-1",
            toUserId = "u-2",
            orderId = null,
            amount = 0.0,
            currency = "USD",
            type = "SALE",
            status = "PENDING",
            initiatedAt = 1L,
            updatedAt = 1L,
            lastModifiedAt = 1L,
            dirty = true
        )
        every { tDao.getTransferById(transferId) } returns flowOf(transfer)
        every { vDao.observeByTransferId(transferId) } returns flowOf(emptyList())
        every { aDao.streamByRef(transferId) } returns flowOf(emptyList())
        every { dDao.observeByTransferId(transferId) } returns flowOf(emptyList())
        coEvery { workflow.computeTrustScore(transferId) } returns Resource.Success(40)
        every { uploadMgr.events } returns MutableSharedFlow()

        val vm = TransferVerificationViewModel(
            transferDao = tDao,
            verificationDao = vDao,
            auditLogDao = aDao,
            disputeDao = dDao,
            appContext = app,
            gson = gson,
            workflow = workflow,
            uploadManager = uploadMgr,
            analytics = analytics,
            currentUserProvider = currentUser,
            uploadTaskDao = uploadTaskDao,
            connectivityManager = connectivity,
        )
        return vm to workflow
    }

    private fun makeVmForEnqueue(transferId: String = "t-up"): Pair<TransferVerificationViewModel, com.rio.rostry.utils.media.MediaUploadManager> {
        val tDao = mockk<TransferDao>()
        val vDao = mockk<TransferVerificationDao>()
        val aDao = mockk<AuditLogDao>()
        val dDao = mockk<DisputeDao>()
        val app = mockk<Context>(relaxed = true)
        val gson = com.google.gson.Gson()
        val workflow = mockk<TransferWorkflowRepository>()
        val uploadMgr = mockk<com.rio.rostry.utils.media.MediaUploadManager>(relaxed = true)
        val analytics = mockk<EnthusiastAnalyticsTracker>(relaxed = true)
        val currentUser = mockk<CurrentUserProvider>()
        every { currentUser.userIdOrNull() } returns "u-1"
        val connectivity = mockk<ConnectivityManager>()
        every { connectivity.isOnWifi() } returns true
        val uploadTaskDao = mockk<com.rio.rostry.data.database.dao.UploadTaskDao>(relaxed = true)

        val transfer = TransferEntity(
            transferId = transferId,
            productId = "p-1",
            fromUserId = "u-1",
            toUserId = "u-2",
            orderId = null,
            amount = 0.0,
            currency = "USD",
            type = "SALE",
            status = "PENDING",
            initiatedAt = 1L,
            updatedAt = 1L,
            lastModifiedAt = 1L,
            dirty = true
        )
        every { tDao.getTransferById(transferId) } returns flowOf(transfer)
        every { vDao.observeByTransferId(transferId) } returns flowOf(emptyList())
        every { aDao.streamByRef(transferId) } returns flowOf(emptyList())
        every { dDao.observeByTransferId(transferId) } returns flowOf(emptyList())
        every { uploadMgr.events } returns MutableSharedFlow()

        val vm = TransferVerificationViewModel(
            transferDao = tDao,
            verificationDao = vDao,
            auditLogDao = aDao,
            disputeDao = dDao,
            appContext = app,
            gson = gson,
            workflow = workflow,
            uploadManager = uploadMgr,
            analytics = analytics,
            currentUserProvider = currentUser,
            uploadTaskDao = uploadTaskDao,
            connectivityManager = connectivity,
        )
        return vm to uploadMgr
    }

    private fun makeVmWithPlugs(transferId: String = "t-1"): Triple<TransferVerificationViewModel, MutableSharedFlow<MediaUploadManager.UploadEvent>, TransferWorkflowRepository> {
        val tDao = mockk<TransferDao>()
        val vDao = mockk<TransferVerificationDao>()
        val aDao = mockk<AuditLogDao>()
        val dDao = mockk<DisputeDao>()
        val app = mockk<android.content.Context>(relaxed = true)
        val gson = com.google.gson.Gson()
        val workflow = mockk<TransferWorkflowRepository>()
        val uploadMgr = mockk<MediaUploadManager>()
        val analytics = mockk<com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker>(relaxed = true)
        val currentUser = mockk<CurrentUserProvider>()
        every { currentUser.userIdOrNull() } returns "u-1"
        val connectivity = mockk<ConnectivityManager>()
        every { connectivity.isOnWifi() } returns true
        val uploadTaskDao = mockk<com.rio.rostry.data.database.dao.UploadTaskDao>(relaxed = true)

        val transfer = TransferEntity(
            transferId = transferId,
            productId = "p-1",
            fromUserId = "u-1",
            toUserId = "u-2",
            orderId = null,
            amount = 0.0,
            currency = "USD",
            type = "SALE",
            status = "PENDING",
            initiatedAt = 1L,
            updatedAt = 1L,
            lastModifiedAt = 1L,
            dirty = true
        )
        every { tDao.getTransferById(transferId) } returns flowOf(transfer)
        every { vDao.observeByTransferId(transferId) } returns flowOf(emptyList())
        every { aDao.streamByRef(transferId) } returns flowOf(emptyList())
        every { dDao.observeByTransferId(transferId) } returns flowOf(emptyList())
        coEvery { workflow.computeTrustScore(transferId) } returns Resource.Success(40)
        val events = MutableSharedFlow<MediaUploadManager.UploadEvent>(replay = 0)
        every { uploadMgr.events } returns events

        val vm = TransferVerificationViewModel(
            transferDao = tDao,
            verificationDao = vDao,
            auditLogDao = aDao,
            disputeDao = dDao,
            appContext = app,
            gson = gson,
            workflow = workflow,
            uploadManager = uploadMgr,
            analytics = analytics,
            currentUserProvider = currentUser,
            uploadTaskDao = uploadTaskDao,
            connectivityManager = connectivity,
        )
        return Triple(vm, events, workflow)
    }

    @Test
    fun load_transfer_and_computes_trust_score() = runTest(dispatcher) {
        val (vm, _) = makeVm()
        vm.load("t-1")
        advanceUntilIdle()
        assertNotNull(vm.state.value.transfer)
        assertEquals(40, vm.state.value.trustScore)
    }

    @Test
    fun gps_confirmation_with_invalid_coords_sets_error() = runTest(dispatcher) {
        val (vm, _) = makeVm()
        vm.load("t-1"); advanceUntilIdle()
        vm.submitGpsConfirm()
        advanceUntilIdle()
        assertTrue(vm.state.value.error?.contains("GPS") == true)
    }

    @Test
    fun dispute_raise_and_platform_review_flow() = runTest(dispatcher) {
        val transferId = "t-1"
        val (vm, w) = makeVm(transferId)
        coEvery { w.raiseDispute(any(), any(), any()) } returns Resource.Success("d-1")
        coEvery { w.platformReview(any(), any(), any(), any()) } returns Resource.Success(Unit)
        vm.load(transferId); advanceUntilIdle()
        vm.raiseDispute("reason"); advanceUntilIdle()
        assertTrue(vm.state.value.success?.contains("Dispute opened") == true)
        vm.platformReview(true, "ok"); advanceUntilIdle()
        assertTrue(vm.state.value.success?.contains("Approved") == true || vm.state.value.success?.contains("Reviewed") == true)
    }

    @Test
    fun upload_photos_and_submit_seller_init() = runTest(dispatcher) {
        val (vm, events, workflow) = makeVmWithPlugs("t-1")
        coEvery { workflow.appendSellerEvidence(any(), any(), any(), any(), any()) } returns Resource.Success(Unit)
        vm.load("t-1"); advanceUntilIdle()
        // Simulate before/after image upload success events
        events.emit(MediaUploadManager.UploadEvent.Success("transfers/t-1/before_123.jpg", "url1"))
        events.emit(MediaUploadManager.UploadEvent.Success("transfers/t-1/after_456.jpg", "url2"))
        advanceUntilIdle()
        assertTrue(vm.state.value.success?.contains("Photos submitted") == true)
    }

    @Test
    fun identity_upload_sets_state_and_calls_buyer_verify() = runTest(dispatcher) {
        val (vm, events, workflow) = makeVmWithPlugs("t-1")
        coEvery { workflow.buyerVerify(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Resource.Success(Unit)
        vm.load("t-1"); advanceUntilIdle()
        events.emit(MediaUploadManager.UploadEvent.Success("transfers/t-1/identity_789.jpg", "url3"))
        advanceUntilIdle()
        assertTrue(vm.state.value.uploadedIdentityRef != null)
        assertTrue(vm.state.value.success?.contains("Identity uploaded") == true)
    }

    @Test
    fun signature_submission_success_flow() = runTest(dispatcher) {
        val (vm, _, workflow) = makeVmWithPlugs("t-1")
        coEvery { workflow.buyerVerify(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Resource.Success(Unit)
        vm.load("t-1"); advanceUntilIdle()
        vm.updateTemp(TransferVerificationViewModel.TempUpdate.Signature("sig-ref"))
        vm.submitSignature()
        advanceUntilIdle()
        assertTrue(vm.state.value.success?.contains("Signature submitted") == true)
    }

    @Test
    fun gps_confirmation_success_flow() = runTest(dispatcher) {
        val (vm, _, workflow) = makeVmWithPlugs("t-1")
        coEvery { workflow.buyerVerify(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Resource.Success(Unit)
        vm.load("t-1"); advanceUntilIdle()
        vm.updateTemp(TransferVerificationViewModel.TempUpdate.Gps(lat = "12.0", lng = "77.0", explanation = "nearby"))
        vm.submitGpsConfirm()
        advanceUntilIdle()
        assertTrue(vm.state.value.success?.contains("GPS submitted") == true)
    }

    @Test
    fun signature_without_value_sets_error() = runTest(dispatcher) {
        val (vm, _) = makeVm()
        vm.load("t-1"); advanceUntilIdle()
        vm.submitSignature()
        advanceUntilIdle()
        assertTrue(vm.state.value.error?.contains("signature", ignoreCase = true) == true)
    }

    @Test
    fun resolve_dispute_success_flow() = runTest(dispatcher) {
        val (vm, w) = makeVm("t-1")
        coEvery { w.resolveDispute(any(), any(), any()) } returns Resource.Success(Unit)
        vm.load("t-1"); advanceUntilIdle()
        vm.resolveDispute("d-1", "ok", true)
        advanceUntilIdle()
        assertTrue(vm.state.value.success?.contains("Dispute resolved") == true)
    }

    @Test
    fun seller_evidence_error_sets_error_and_does_not_clear_temp() = runTest(dispatcher) {
        val (vm, events, workflow) = makeVmWithPlugs("t-err")
        coEvery { workflow.appendSellerEvidence(any(), any(), any(), any(), any()) } returns Resource.Error("append failed")
        vm.load("t-err"); advanceUntilIdle()
        // Emit before/after success to trigger persistence attempt
        events.emit(MediaUploadManager.UploadEvent.Success("transfers/t-err/before_1.jpg", "url1"))
        events.emit(MediaUploadManager.UploadEvent.Success("transfers/t-err/after_2.jpg", "url2"))
        advanceUntilIdle()
        assertTrue(vm.state.value.error?.contains("append failed") == true)
        // Since we don't set temp exif in test, just ensure uploaded refs are not both cleared simultaneously due to error
        // Success path would null them; error path should keep at least one non-null
        val stillHasOne = (vm.state.value.uploadedBeforeUrl != null) || (vm.state.value.uploadedAfterUrl != null)
        assertTrue(stillHasOne)
    }

    @Test
    fun identity_upload_error_sets_error() = runTest(dispatcher) {
        val (vm, events, workflow) = makeVmWithPlugs("t-id-err")
        coEvery { workflow.buyerVerify(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Resource.Error("id failed")
        vm.load("t-id-err"); advanceUntilIdle()
        events.emit(MediaUploadManager.UploadEvent.Success("transfers/t-id-err/identity_9.jpg", "url3"))
        advanceUntilIdle()
        assertTrue(vm.state.value.error?.contains("id failed") == true)
    }

    @Test
    fun upload_progress_events_update_state_map() = runTest(dispatcher) {
        val (vm, events, _) = makeVmWithPlugs("t-prog")
        vm.load("t-prog"); advanceUntilIdle()
        events.emit(MediaUploadManager.UploadEvent.Progress("transfers/t-prog/before_11.jpg", 10))
        events.emit(MediaUploadManager.UploadEvent.Progress("transfers/t-prog/before_11.jpg", 45))
        advanceUntilIdle()
        val percent = vm.state.value.uploadProgress["transfers/t-prog/before_11.jpg"]
        assertEquals(45, percent)
    }

    @Test
    fun platform_review_error_sets_error() = runTest(dispatcher) {
        val (vm, w) = makeVm("t-1")
        coEvery { w.platformReview(any(), any(), any(), any()) } returns Resource.Error("platform failed")
        vm.load("t-1"); advanceUntilIdle()
        vm.platformReview(true, "ok"); advanceUntilIdle()
        assertTrue(vm.state.value.error?.contains("platform failed") == true)
    }

    @Test
    fun gps_confirmation_error_flow_sets_error() = runTest(dispatcher) {
        val (vm, _, workflow) = makeVmWithPlugs("t-1")
        coEvery { workflow.buyerVerify(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Resource.Error("gps fail")
        vm.load("t-1"); advanceUntilIdle()
        vm.updateTemp(TransferVerificationViewModel.TempUpdate.Gps(lat = "12.0", lng = "77.0", explanation = null))
        vm.submitGpsConfirm()
        advanceUntilIdle()
        assertTrue(vm.state.value.error?.contains("gps fail") == true)
    }
}
