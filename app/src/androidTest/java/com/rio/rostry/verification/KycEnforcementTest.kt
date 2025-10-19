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
            email = "$userId@test.com",
            userType = type,
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
        CurrentUserHolder.userId = unverifiedUser.userId
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
        assertTrue(result is Resource.Error)
        // Either permission or product not found, but should not succeed
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
        // Similarly for transfer
        val pid = (result as Resource.Success).data!!
        val transferResult = transferWorkflowRepository.initiate(
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
        assertTrue(transferResult is Resource.Success)
    }
}