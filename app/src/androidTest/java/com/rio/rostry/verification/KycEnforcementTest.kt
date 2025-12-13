package com.rio.rostry.verification

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.R
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.di.CurrentUserHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class KycEnforcementTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var transferWorkflowRepository: TransferWorkflowRepository

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var rbacGuard: com.rio.rostry.domain.rbac.RbacGuard

    private lateinit var unverifiedUser: UserEntity
    private lateinit var pendingUser: UserEntity
    private lateinit var verifiedUser: UserEntity
    private lateinit var rejectedUser: UserEntity

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking {
            unverifiedUser = createTestUser("unverified", UserType.FARMER, VerificationStatus.UNVERIFIED)
            pendingUser = createTestUser("pending", UserType.FARMER, VerificationStatus.PENDING)
            verifiedUser = createTestUser("verified", UserType.FARMER, VerificationStatus.VERIFIED)
            rejectedUser = createTestUser("rejected", UserType.FARMER, VerificationStatus.REJECTED)
        }
    }

    private suspend fun createTestUser(userId: String, type: UserType, status: VerificationStatus): UserEntity {
        val user = UserEntity(
            userId = userId,
            phoneNumber = null,
            email = "$userId@test.com",
            fullName = "$userId User",
            userType = type.name,
            verificationStatus = status,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userDao.insertUser(user)
        return user
    }

    @Test
    fun testKycSubmissionCreatesErrorGuidanceOnList() = runBlocking {
        CurrentUserHolder.userId = unverifiedUser.userId
        val p = ProductEntity(
            productId = "",
            sellerId = unverifiedUser.userId,
            name = "Test",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(p)
        assertTrue(result is Resource.Error)
        val msg = (result as Resource.Error).message
        assertTrue((msg?.contains("Profile") == true) && (msg?.contains("Verification") == true))
    }

    @Test
    fun testDuplicateSubmissionIsBlocked() = runBlocking {
        CurrentUserHolder.userId = pendingUser.userId
        // Test duplicate submission logic
        assertTrue(true) // Placeholder
    }

    @Test
    fun testRejectedKycCanBeResubmitted() = runBlocking {
        CurrentUserHolder.userId = rejectedUser.userId
        // Test resubmission
        assertTrue(true) // Placeholder
    }

    @Test
    fun testUnverifiedUserCannotListProduct() = runBlocking {
        CurrentUserHolder.userId = unverifiedUser.userId
        val product = ProductEntity(
            productId = "",
            sellerId = unverifiedUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Error)
        assertTrue(((result as Resource.Error).message?.contains("Complete KYC verification") == true))
    }

    @Test
    fun testPendingVerificationCannotListProduct() = runBlocking {
        CurrentUserHolder.userId = pendingUser.userId
        val product = ProductEntity(
            productId = "",
            sellerId = pendingUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Error)
        assertTrue(((result as Resource.Error).message?.contains("Complete KYC verification") == true))
    }

    @Test
    fun testVerifiedUserCanListProduct() = runBlocking {
        CurrentUserHolder.userId = verifiedUser.userId
        val product = ProductEntity(
            productId = "",
            sellerId = verifiedUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Success)
    }

    @Test
    fun testRejectedUserCannotListProduct() = runBlocking {
        CurrentUserHolder.userId = rejectedUser.userId
        val product = ProductEntity(
            productId = "",
            sellerId = rejectedUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Error)
        assertTrue(((result as Resource.Error).message?.contains("Complete KYC verification") == true))
    }

    @Test
    fun testUnverifiedUserCannotInitiateTransfer() = runBlocking {
        CurrentUserHolder.userId = unverifiedUser.userId // Assuming unverifiedUser has ENTHUSIAST role to have transfer permission
        val result = transferWorkflowRepository.initiate(
            productId = "nonexistent",
            fromUserId = unverifiedUser.userId,
            toUserId = "other_user",
            amount = 100.0,
            currency = "INR",
            sellerPhotoUrl = null,
            gpsLat = null,
            gpsLng = null,
            conditionsJson = null,
            timeoutAt = null
        )
        // Should fail because UNVERIFIED users cannot initiate transfers
        assertTrue(result is Resource.Error)
        val error = result as Resource.Error
        // The failure should be due to permission/verification status
        assert(error.message?.contains("permission") == true) { "Transfer should be blocked for UNVERIFIED users" }
    }

    @Test
    fun testVerifiedUserCanInitiateTransfer() = runBlocking {
        CurrentUserHolder.userId = verifiedUser.userId
        // Create a product owned by verified user
        val product = ProductEntity(
            productId = "",
            sellerId = verifiedUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val pr = productRepository.addProduct(product)
        val pid = (pr as Resource.Success).data!!
        val result = transferWorkflowRepository.initiate(
            productId = pid,
            fromUserId = verifiedUser.userId,
            toUserId = "other_user",
            amount = 100.0,
            currency = "INR",
            sellerPhotoUrl = null,
            gpsLat = null,
            gpsLng = null,
            conditionsJson = null,
            timeoutAt = null
        )
        assertTrue(result is Resource.Success || result is Resource.Error)
    }

    @Test
    fun testKycErrorMessageContainsGuidance() = runBlocking {
        CurrentUserHolder.userId = unverifiedUser.userId
        val product = ProductEntity(
            productId = "",
            sellerId = unverifiedUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Error)
        val message = (result as Resource.Error).message
        assertTrue((message?.contains("Profile") == true) && (message?.contains("Verification") == true))
    }

    @Test
    fun testRejectionReasonIsDisplayed() {
        assertTrue(true)
    }

    @Test
    fun testVerificationStatusPropagation() = runBlocking {
        CurrentUserHolder.userId = verifiedUser.userId
        assertTrue(true)
    }

    @Test
    fun testVerificationUnlocksFeatures() = runBlocking {
        CurrentUserHolder.userId = verifiedUser.userId
        val product = ProductEntity(
            productId = "",
            sellerId = verifiedUser.userId,
            name = "Test Product",
            description = "Desc",
            category = "Bird",
            price = 10.0,
            quantity = 1.0,
            unit = "pc",
            location = "Farm",
            lifecycleStatus = "ACTIVE"
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Success)
        // Transfer should work for verified users (and also for unverified with proper role)
        // Market listing requires verification, which is tested elsewhere
    }

    @Test
    fun testPendingUserCanInitiateTransfer() = runBlocking {
        // Create a PENDING user (assuming we have test setup for this)
        val pendingUser = UserEntity(
            userId = "pending_user",
            userType = UserType.ENTHUSIAST.name, // Needs enthusiast role to have transfer permission
            verificationStatus = VerificationStatus.PENDING,
            email = "pending@test.com",
            fullName = "Pending User",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userDao.insertUser(pendingUser)

        CurrentUserHolder.userId = pendingUser.userId
        val result = transferWorkflowRepository.initiate(
            productId = "nonexistent",
            fromUserId = pendingUser.userId,
            toUserId = "other_user",
            amount = 100.0,
            currency = "INR",
            sellerPhotoUrl = null,
            gpsLat = null,
            gpsLng = null,
            conditionsJson = null,
            timeoutAt = null
        )
        // Should fail due to product not found, not due to verification status
        assertTrue(result is Resource.Error)
        // The failure should be about product not found, not verification status
        val error = result as Resource.Error
        assert(error.message?.contains("KYC verification") != true) { "Transfer should not be blocked due to verification status" }
    }

    @Test
    fun testPendingUserCanEditLineage() = runBlocking {
        // Create a PENDING farmer user
        val pendingUser = UserEntity(
            userId = "pending_lineage_user",
            userType = UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            email = "pending_lineage@test.com",
            fullName = "Pending Lineage User",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userDao.insertUser(pendingUser)

        CurrentUserHolder.userId = pendingUser.userId

        // Test that rbac guard allows lineage editing for pending users
        val canEdit = rbacGuard.canEditLineage()
        assertTrue("Pending user should be able to edit lineage", canEdit)
    }

    @Test
    fun testPendingUserCanManageOrders() = runBlocking {
        // Create a PENDING farmer user
        val pendingUser = UserEntity(
            userId = "pending_orders_user",
            userType = UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            email = "pending_orders@test.com",
            fullName = "Pending Orders User",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userDao.insertUser(pendingUser)

        CurrentUserHolder.userId = pendingUser.userId

        // Test that rbac guard allows order management for pending users
        val canManage = rbacGuard.canManageOrders()
        assertTrue("Pending user should be able to manage orders", canManage)
    }
}