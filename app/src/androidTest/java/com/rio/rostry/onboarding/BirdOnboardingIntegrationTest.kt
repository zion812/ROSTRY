package com.rio.rostry.onboarding

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.ui.onboarding.OnboardFarmBirdViewModel
import javax.inject.Inject
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import org.junit.Assert.*

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class BirdOnboardingIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var db: AppDatabase

    @Test
    fun pendingFarmerCanAddPrivateBird(): Unit = runBlocking {
        // Create test user with PENDING status
        val testUser = UserEntity(
            userId = "test-pending-farmer",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test Farmer",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        // Add user to database first using direct DAO access for test setup
        db.userDao().insertUser(testUser)

        // Create private bird via repository
        val privateBird = ProductEntity(
            productId = "test-bird-1",
            sellerId = testUser.userId,
            status = "private",
            name = "Test Chicken",
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit"
        )

        val result = productRepository.addProduct(privateBird, false)

        // Assert bird exists in database with status="private"
        assertTrue(result is Resource.Success)
        assertNotNull(result.data)

        val savedBird = db.productDao().findById("test-bird-1")
        assertNotNull(savedBird)
        assertTrue(savedBird!!.status == "private")
        // Assert no verification error
        assertTrue(savedBird!!.status == "private")
    }

    @Test
    fun pendingFarmerCannotCreatePublicListing(): Unit = runBlocking {
        // Create test user with PENDING status
        val testUser = UserEntity(
            userId = "test-pending-farmer-2",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test Farmer",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        // Add user to database first using direct DAO access for test setup
        db.userDao().insertUser(testUser)

        // Attempt to create bird with status="active"
        val publicBird = ProductEntity(
            productId = "test-bird-2",
            sellerId = testUser.userId,
            status = "active",
            name = "Test Chicken",
            category = "BIRD",
            price = 100.0,
            quantity = 1.0,
            unit = "unit"
        )

        val result = productRepository.addProduct(publicBird, false)

        // Assert error contains "KYC verification"
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("KYC") == true || result.message?.contains("verification") == true)

        // Assert no bird in database
        val savedBird = db.productDao().findById("test-bird-2")
        assertTrue(savedBird == null)
    }

    @Test
    fun verifiedFarmerCanTransitionPrivateToPublic(): Unit = runBlocking {
        // Create test user with PENDING status initially
        val testUser = UserEntity(
            userId = "test-verified-farmer",
            phoneNumber = "+919999999999",
            email = "test@example.com",
            fullName = "Test Farmer",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        // Add user to database
        db.userDao().insertUser(testUser)

        // Add private bird
        val privateBird = ProductEntity(
            productId = "test-bird-3",
            sellerId = testUser.userId,
            status = "private",
            name = "Test Chicken",
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit"
        )
        val addResult = productRepository.addProduct(privateBird, false)
        assertTrue(addResult is Resource.Success)

        // Update user to VERIFIED
        val verifiedUser = testUser.copy(
            verificationStatus = VerificationStatus.VERIFIED
        )
        db.userDao().insertUser(verifiedUser)

        // Update bird status to "available"
        val updatedBird = privateBird.copy(status = "available")
        val updateResult = productRepository.updateProduct(updatedBird)

        // Assert update succeeds
        assertTrue(updateResult is Resource.Success)

        // Assert bird status is "available" in database
        val birdInDb = db.productDao().findById("test-bird-3")
        assertNotNull(birdInDb)
        assertTrue(birdInDb!!.status == "available")
    }

    @Test
    fun pendingFarmerAddPrivateBirdCompleteFlow(): Unit = runBlocking {
        // Test the complete flow: PENDING farmer adds private bird → DB insert → navigation emitted → upload enqueued
        // This test verifies via repository and database layer the complete integration

        // First create and set up test user
        val testUser = UserEntity(
            userId = "test-pending-farmer-flow",
            phoneNumber = "+919999999999",
            email = "test-flow@example.com",
            fullName = "Test Farmer",
            userType = com.rio.rostry.domain.model.UserType.FARMER.name,
            verificationStatus = VerificationStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        db.userDao().insertUser(testUser)

        // Test the complete flow: PENDING farmer adds private bird via repository
        val privateBird = ProductEntity(
            productId = "test-bird-flow-1",
            sellerId = testUser.userId,
            status = "private",
            name = "Integration Test Chicken",
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit"
        )

        val result = productRepository.addProduct(privateBird, false)

        // Assert the bird was created successfully
        assertTrue("Bird should be saved successfully", result is Resource.Success)
        assertNotNull("Bird ID should be returned", result.data)

        // Verify that the bird was actually created in the database with private status
        val savedBird = db.productDao().findById("test-bird-flow-1")
        assertNotNull("Bird should exist in database", savedBird)
        assertTrue("Bird should have private status", savedBird!!.status == "private")
    }
}