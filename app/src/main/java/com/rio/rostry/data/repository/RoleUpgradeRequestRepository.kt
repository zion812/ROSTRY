package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.RoleUpgradeRequestDao
import com.rio.rostry.data.database.entity.RoleUpgradeRequestEntity
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface RoleUpgradeRequestRepository {
    suspend fun submitRequest(userId: String, currentRole: UserType, requestedRole: UserType): Resource<String>
    suspend fun getPendingRequest(userId: String): Resource<RoleUpgradeRequestEntity?>
    fun observePendingRequests(): Flow<List<RoleUpgradeRequestEntity>>
    suspend fun approveRequest(requestId: String, adminId: String, notes: String?): Resource<Unit>
    suspend fun rejectRequest(requestId: String, adminId: String, notes: String?): Resource<Unit>
}

@Singleton
class RoleUpgradeRequestRepositoryImpl @Inject constructor(
    private val dao: RoleUpgradeRequestDao,
    private val userRepository: UserRepository
) : RoleUpgradeRequestRepository {

    override suspend fun submitRequest(userId: String, currentRole: UserType, requestedRole: UserType): Resource<String> {
        return try {
            val existing = dao.getPendingRequestByUser(userId)
            if (existing != null) {
                return Resource.Error("You already have a pending request for ${existing.requestedRole}")
            }

            val requestId = UUID.randomUUID().toString()
            val request = RoleUpgradeRequestEntity(
                requestId = requestId,
                userId = userId,
                currentRole = currentRole.name,
                requestedRole = requestedRole.name,
                status = RoleUpgradeRequestEntity.STATUS_PENDING
            )
            
            dao.insert(request)
            
            // Update user verification status to PENDING_UPGRADE if applicable
            // Implementation note: This might be handled by the Manager, but Repository can ensure consistency
            // userRepo.updateVerificationStatus(userId, VerificationStatus.PENDING_UPGRADE)
            
            Resource.Success(requestId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit request")
        }
    }

    override suspend fun getPendingRequest(userId: String): Resource<RoleUpgradeRequestEntity?> {
        return try {
            val request = dao.getPendingRequestByUser(userId)
            Resource.Success(request)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch request")
        }
    }

    override fun observePendingRequests(): Flow<List<RoleUpgradeRequestEntity>> {
        return dao.observePendingRequests()
    }

    override suspend fun approveRequest(requestId: String, adminId: String, notes: String?): Resource<Unit> {
        return try {
            val request = dao.getRequestById(requestId) ?: return Resource.Error("Request not found")
            if (request.status != RoleUpgradeRequestEntity.STATUS_PENDING) {
                return Resource.Error("Request is not pending")
            }
            
            val updated = request.copy(
                status = RoleUpgradeRequestEntity.STATUS_APPROVED,
                reviewedBy = adminId,
                adminNotes = notes,
                reviewedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            dao.update(updated)
            
            // Update the actual user role
            userRepository.updateUserType(request.userId, UserType.valueOf(request.requestedRole))
            // Reset verification status so the user must verify for the new role (Location or KYC)
            userRepository.updateVerificationStatus(request.userId, VerificationStatus.UNVERIFIED)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to approve request")
        }
    }

    override suspend fun rejectRequest(requestId: String, adminId: String, notes: String?): Resource<Unit> {
        return try {
            val request = dao.getRequestById(requestId) ?: return Resource.Error("Request not found")
            
            val updated = request.copy(
                status = RoleUpgradeRequestEntity.STATUS_REJECTED,
                reviewedBy = adminId,
                adminNotes = notes,
                reviewedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            dao.update(updated)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to reject request")
        }
    }
}
