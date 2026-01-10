package com.rio.rostry.domain.rbac

import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests for RbacGuard testing permission enforcement
 * for different user roles and verification statuses.
 */
class RbacGuardTest {

    @MockK
    private lateinit var currentUserProvider: CurrentUserProvider

    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var rbacGuard: RbacGuard

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        rbacGuard = RbacGuard(currentUserProvider, userRepository)
    }

    // ===================
    // Farmer Role Tests
    // ===================

    @Test
    fun `verified Farmer can list products`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.VERIFIED)

        // When
        val canList = rbacGuard.canListProduct()

        // Then
        assertThat(canList).isTrue()
    }

    @Test
    fun `unverified Farmer cannot list products`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.PENDING)

        // When
        val canList = rbacGuard.canListProduct()

        // Then
        assertThat(canList).isFalse()
    }

    @Test
    fun `Farmer can add private products without verification`() = runTest {
        // Given - unverified farmer
        setupUserWithRole(UserType.FARMER, VerificationStatus.PENDING)

        // When
        val canAddPrivate = rbacGuard.canAddPrivateProduct()

        // Then - should be allowed for local farm management
        assertThat(canAddPrivate).isTrue()
    }

    @Test
    fun `Farmer can manage orders`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.VERIFIED)

        // When
        val canManage = rbacGuard.canManageOrders()

        // Then
        assertThat(canManage).isTrue()
    }

    // ======================
    // Enthusiast Role Tests
    // ======================

    @Test
    fun `Enthusiast can edit lineage`() = runTest {
        // Given
        setupUserWithRole(UserType.ENTHUSIAST, VerificationStatus.VERIFIED)

        // When
        val canEdit = rbacGuard.canEditLineage()

        // Then
        assertThat(canEdit).isTrue()
    }

    @Test
    fun `Enthusiast can initiate transfers`() = runTest {
        // Given
        setupUserWithRole(UserType.ENTHUSIAST, VerificationStatus.VERIFIED)

        // When
        val canTransfer = rbacGuard.canInitiateTransfer()

        // Then
        assertThat(canTransfer).isTrue()
    }

    // ====================
    // General Role Tests
    // ====================

    @Test
    fun `General user cannot list products`() = runTest {
        // Given
        setupUserWithRole(UserType.GENERAL, VerificationStatus.NOT_REQUIRED)

        // When
        val canList = rbacGuard.canListProduct()

        // Then
        assertThat(canList).isFalse()
    }

    @Test
    fun `General user cannot edit lineage`() = runTest {
        // Given
        setupUserWithRole(UserType.GENERAL, VerificationStatus.NOT_REQUIRED)

        // When
        val canEdit = rbacGuard.canEditLineage()

        // Then
        assertThat(canEdit).isFalse()
    }

    // ======================
    // Verification Tests
    // ======================

    @Test
    fun `isVerified returns true for verified user`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.VERIFIED)

        // When
        val isVerified = rbacGuard.isVerified()

        // Then
        assertThat(isVerified).isTrue()
    }

    @Test
    fun `isVerified returns false for pending user`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.PENDING)

        // When
        val isVerified = rbacGuard.isVerified()

        // Then
        assertThat(isVerified).isFalse()
    }

    @Test
    fun `requireVerified returns error for unverified user`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.PENDING)

        // When
        val result = rbacGuard.requireVerified("list a product")

        // Then
        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).message).contains("KYC verification")
    }

    @Test
    fun `requireVerified returns success for verified user`() = runTest {
        // Given
        setupUserWithRole(UserType.FARMER, VerificationStatus.VERIFIED)

        // When
        val result = rbacGuard.requireVerified("list a product")

        // Then
        assertThat(result).isInstanceOf(Resource.Success::class.java)
    }

    // ========================
    // No Authentication Tests
    // ========================

    @Test
    fun `canAsync returns false when user not authenticated`() = runTest {
        // Given
        every { currentUserProvider.userIdOrNull() } returns null

        // When
        val can = rbacGuard.canAsync(Permission.LIST_PRODUCT)

        // Then
        assertThat(can).isFalse()
    }

    // Helper function
    private fun setupUserWithRole(role: UserType, verificationStatus: VerificationStatus) {
        val testUser = UserEntity(
            odid = TEST_USER_ID,
            userId = TEST_USER_ID,
            email = "test@example.com",
            name = "Test User",
            role = role,
            verificationStatus = verificationStatus,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        every { currentUserProvider.userIdOrNull() } returns TEST_USER_ID
        coEvery { userRepository.getUserById(TEST_USER_ID) } returns flowOf(Resource.Success(testUser))
    }

    companion object {
        private const val TEST_USER_ID = "test_user_123"
    }
}
