package com.rio.rostry.ui.transfer

import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.data.sync.SyncManager
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TransferCreateViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private fun makeViewModel(
        isOnline: Boolean = true,
        validatorQuarantine: Boolean = false,
        validatorValid: Boolean = true,
    ): TransferCreateViewModel {
        val transferDao = mockk<TransferDao>(relaxed = true)
        val productRepo = mockk<ProductRepository>(relaxed = true)
        val userRepo = mockk<UserRepository>(relaxed = true)
        val currentUser = mockk<CurrentUserProvider>()
        every { currentUser.userIdOrNull() } returns "u-1"
        val workflow = mockk<TransferWorkflowRepository>()
        coEvery { workflow.validateTransferEligibility(any(), any(), any()) } returns Resource.Success(Unit)
        val validator = mockk<ProductValidator>()
        coEvery { validator.checkQuarantineStatus(any()) } returns validatorQuarantine
        coEvery { validator.validateWithTraceability(any(), any(), any()) } returns ProductValidator.ValidationResult(validatorValid, if (validatorValid) emptyList() else listOf("outdated"))
        val quarantineDao = mockk<QuarantineRecordDao>(relaxed = true)
        val connectivity = mockk<ConnectivityManager>()
        every { connectivity.isOnline() } returns isOnline
        val conflictFlow = MutableSharedFlow<com.rio.rostry.data.sync.SyncManager.ConflictEvent>()
        val syncManager = mockk<SyncManager>()
        every { syncManager.conflictEvents } returns conflictFlow

        return TransferCreateViewModel(
            transferDao = transferDao,
            productRepository = productRepo,
            userRepository = userRepo,
            currentUserProvider = currentUser,
            transferWorkflowRepository = workflow,
            productValidator = validator,
            quarantineDao = quarantineDao,
            connectivityManager = connectivity,
            syncManager = syncManager,
        )
    }

    @Test
    fun initial_state_is_empty() = runTest(dispatcher) {
        val vm = makeViewModel()
        assertTrue(vm.state.value.availableProducts.isEmpty())
        assertNull(vm.state.value.selectedProduct)
    }

    @Test
    fun loadUserProducts_populates_seeded_products() = runTest(dispatcher) {
        val vm = makeViewModel()
        vm.loadUserProducts()
        advanceUntilIdle()
        assertTrue(vm.state.value.availableProducts.isNotEmpty())
    }

    @Test
    fun select_product_updates_selection_and_status() = runTest(dispatcher) {
        val vm = makeViewModel()
        vm.loadUserProducts()
        advanceUntilIdle()
        val firstId = vm.state.value.availableProducts.first().productId
        vm.selectProduct(firstId)
        advanceUntilIdle()
        assertEquals(firstId, vm.state.value.productId)
        assertNotNull(vm.state.value.selectedProduct)
    }

    @Test
    fun validation_errors_for_missing_fields_and_self_transfer() = runTest(dispatcher) {
        val vm = makeViewModel()
        vm.loadUserProducts(); advanceUntilIdle()
        val p = vm.state.value.availableProducts.first()
        vm.selectProduct(p.productId); advanceUntilIdle()
        // no recipient yet
        val errors = vm.validateForm()
        assertTrue(errors.containsKey("recipient"))
        // self-transfer
        vm.selectRecipient("u-1")
        val errors2 = vm.validateForm()
        assertTrue(errors2["recipient"].orEmpty().contains("Cannot transfer to yourself"))
    }

    @Test
    fun proceed_to_confirmation_after_valid() = runTest(dispatcher) {
        val vm = makeViewModel()
        vm.loadUserProducts(); advanceUntilIdle()
        val p = vm.state.value.availableProducts.first()
        vm.selectProduct(p.productId); advanceUntilIdle()
        vm.searchRecipients("john"); advanceUntilIdle()
        vm.selectRecipient("john_doe")
        vm.update("amount", "100")
        vm.proceedToConfirmation()
        assertTrue(vm.state.value.confirmationStep)
    }

    @Test
    fun confirm_and_submit_online_sets_success_and_pending_state() = runTest(dispatcher) {
        val vm = makeViewModel(isOnline = true)
        vm.loadUserProducts(); advanceUntilIdle()
        val p = vm.state.value.availableProducts.first()
        vm.selectProduct(p.productId); advanceUntilIdle()
        vm.searchRecipients("john"); advanceUntilIdle()
        vm.selectRecipient("john_doe")
        vm.update("amount", "120")
        vm.confirmAndSubmit()
        advanceUntilIdle()
        assertEquals(com.rio.rostry.ui.components.SyncState.PENDING, vm.state.value.transferSyncState)
        // Online flow sets a success-style message in error field per current impl
        assertTrue(vm.state.value.error?.contains("success", ignoreCase = true) == true)
    }

    @Test
    fun confirm_and_submit_offline_shows_queued_message() = runTest(dispatcher) {
        val vm = makeViewModel(isOnline = false)
        vm.loadUserProducts(); advanceUntilIdle()
        val p = vm.state.value.availableProducts.first()
        vm.selectProduct(p.productId); advanceUntilIdle()
        vm.searchRecipients("john"); advanceUntilIdle()
        vm.selectRecipient("john_doe")
        vm.update("amount", "120")
        vm.confirmAndSubmit()
        advanceUntilIdle()
        assertEquals(com.rio.rostry.ui.components.SyncState.PENDING, vm.state.value.transferSyncState)
        assertTrue(vm.state.value.error?.contains("queued", ignoreCase = true) == true)
    }
}
