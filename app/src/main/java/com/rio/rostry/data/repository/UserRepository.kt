package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.VerificationSubmission

interface UserRepository {

    fun getCurrentUser(): Flow<Resource<UserEntity?>>

    suspend fun getCurrentUserSuspend(): UserEntity?

    suspend fun refreshCurrentUser(userId: String): Resource<Unit>

    suspend fun updateUserProfile(userEntity: UserEntity): Resource<Unit>

    fun getUserById(userId: String): Flow<Resource<UserEntity?>>

    fun searchUsers(query: String): Flow<List<UserEntity>>

    suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit>

    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit>

    suspend fun updateVerificationSubmissionStatus(userId: String, status: VerificationStatus, reviewerId: String, rejectionReason: String? = null): Resource<Unit>

    // KYC & Verification workflows
    suspend fun uploadVerificationEvidence(userId: String, evidenceUrls: List<String>): Resource<Unit>

    suspend fun requestBreederVerification(userId: String, breedingProofUrls: List<String>): Resource<Unit>

    suspend fun updateFarmLocationVerification(userId: String, latitude: Double, longitude: Double): Resource<Unit>

    suspend fun submitKycVerification(
        userId: String, 
        submissionId: String, 
        documentUrls: List<String>, 
        imageUrls: List<String>, 
        docTypes: Map<String, String> = emptyMap(),
        imageTypes: Map<String, String> = emptyMap(),
        upgradeType: UpgradeType = UpgradeType.GENERAL_TO_FARMER,
        currentRole: UserType = UserType.GENERAL,
        targetRole: UserType? = null,
        farmLocation: Map<String, Double>? = null
    ): Resource<Unit>

    fun getKycSubmissionStatus(userId: String): Flow<Resource<String?>>

    fun streamPendingVerifications(): Flow<Resource<List<VerificationSubmission>>>

    suspend fun refreshPhoneNumber(userId: String): Resource<Unit>

    fun getVerificationDetails(userId: String): Flow<Resource<Map<String, Any>?>>

    suspend fun getVerificationDetailsOnce(userId: String): Resource<Map<String, Any>?>

    // New methods for enhanced verification system
    fun getVerificationsByUpgradeType(upgradeType: UpgradeType): Flow<Resource<List<VerificationSubmission>>>

    fun getVerificationsByRoleAndStatus(role: UserType?, status: VerificationStatus?): Flow<Resource<List<VerificationSubmission>>>

    suspend fun getVerificationSubmission(userId: String): Resource<VerificationSubmission?>
}
