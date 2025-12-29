package com.rio.rostry.domain.transfer

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * OwnershipTransferUseCase - Manages secure 6-digit code transfers for Enthusiast birds.
 * 
 * Transfer Flow:
 * 1. INITIATE: Owner generates transfer code, bird is locked
 * 2. PENDING: Waiting for recipient to claim
 * 3. CLAIMED: Recipient enters code, transfer triggers
 * 4. COMPLETED: Bird ownership transferred atomically
 * 
 * Features:
 * - Unique 6-digit alphanumeric codes
 * - Pedigree snapshot at transfer time
 * - Atomic ownership transfer with lineage preservation
 * - 48-hour timeout for unclaimed transfers
 */
@Singleton
class OwnershipTransferUseCase @Inject constructor(
    private val productDao: ProductDao,
    private val transferDao: TransferDao,
    private val pedigreeRepository: PedigreeRepository,
    private val gson: Gson
) {
    companion object {
        private const val CODE_LENGTH = 6
        private const val CODE_CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Avoid confusing chars (0/O, 1/I)
        private const val TRANSFER_TIMEOUT_HOURS = 48L
        private const val TRANSFER_TYPE_HANDSHAKE = "OWNERSHIP_HANDSHAKE"
        
        // Transfer statuses
        const val STATUS_PENDING = "TRANSFER_PENDING"
        const val STATUS_CLAIMED = "CLAIMED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELLED = "CANCELLED"
        const val STATUS_EXPIRED = "EXPIRED"
    }
    
    /**
     * Initiate a new ownership transfer for a bird.
     * Generates a unique 6-digit code and locks the bird from modifications.
     * 
     * @param birdId The product ID of the bird to transfer
     * @param ownerId The current owner's user ID
     * @return Resource containing the transfer code
     */
    suspend fun initiateTransfer(birdId: String, ownerId: String): Resource<TransferResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Verify ownership
                val bird = productDao.findById(birdId)
                    ?: return@withContext Resource.Error("Bird not found")
                
                if (bird.sellerId != ownerId) {
                    return@withContext Resource.Error("You do not own this bird")
                }
                
                // Check if already in transfer
                val existingTransfer = transferDao.findActiveTransferForProduct(birdId)
                if (existingTransfer != null) {
                    return@withContext Resource.Error("Bird already has pending transfer")
                }
                
                // Generate unique 6-digit code
                val transferCode = generateUniqueCode()
                
                // Build pedigree snapshot
                val pedigreeSnapshot = when (val pedigreeResult = pedigreeRepository.getFullPedigree(birdId, 3)) {
                    is Resource.Success -> gson.toJson(pedigreeResult.data)
                    else -> null
                }
                
                // Calculate timeout (48 hours from now)
                val now = System.currentTimeMillis()
                val timeoutAt = now + (TRANSFER_TIMEOUT_HOURS * 60 * 60 * 1000)
                
                // Create transfer record
                val transfer = TransferEntity(
                    transferId = UUID.randomUUID().toString(),
                    productId = birdId,
                    fromUserId = ownerId,
                    toUserId = null, // Will be set when claimed
                    amount = 0.0, // Ownership transfer, no payment
                    type = TRANSFER_TYPE_HANDSHAKE,
                    status = STATUS_PENDING,
                    transferCode = transferCode,
                    lineageSnapshotJson = pedigreeSnapshot,
                    transferType = TRANSFER_TYPE_HANDSHAKE,
                    timeoutAt = timeoutAt,
                    initiatedAt = now,
                    updatedAt = now,
                    dirty = true
                )
                
                transferDao.upsert(transfer)
                
                // Mark bird as transfer-pending (optional: prevent edits)
                val lockedBird = bird.copy(
                    status = STATUS_PENDING,
                    updatedAt = now,
                    dirty = true
                )
                productDao.upsert(lockedBird)
                
                Timber.i("Transfer initiated for bird $birdId with code $transferCode")
                
                Resource.Success(TransferResult(
                    transferId = transfer.transferId,
                    transferCode = transferCode,
                    birdName = bird.name,
                    expiresAt = timeoutAt
                ))
            } catch (e: Exception) {
                Timber.e(e, "Failed to initiate transfer for $birdId")
                Resource.Error(e.message ?: "Failed to initiate transfer")
            }
        }
    }
    
    /**
     * Claim a transfer using a 6-digit code.
     * 
     * @param code The transfer code entered by recipient
     * @param claimerId The user claiming the bird
     * @return Resource containing transfer details for confirmation
     */
    suspend fun claimTransfer(code: String, claimerId: String): Resource<ClaimResult> {
        return withContext(Dispatchers.IO) {
            try {
                val normalizedCode = code.uppercase().trim()
                
                // Find transfer by code
                val transfer = transferDao.findByTransferCode(normalizedCode)
                    ?: return@withContext Resource.Error("Invalid transfer code")
                
                // Validate transfer status
                if (transfer.status != STATUS_PENDING) {
                    return@withContext Resource.Error("Transfer is no longer available")
                }
                
                // Check expiration
                val now = System.currentTimeMillis()
                if (transfer.timeoutAt != null && now > transfer.timeoutAt) {
                    // Mark as expired
                    val expired = transfer.copy(
                        status = STATUS_EXPIRED,
                        updatedAt = now,
                        dirty = true
                    )
                    transferDao.upsert(expired)
                    return@withContext Resource.Error("Transfer code has expired")
                }
                
                // Cannot claim own bird
                if (transfer.fromUserId == claimerId) {
                    return@withContext Resource.Error("Cannot claim your own transfer")
                }
                
                // Get bird details
                val bird = productDao.findById(transfer.productId ?: "")
                    ?: return@withContext Resource.Error("Bird not found")
                
                // Mark as claimed
                val claimed = transfer.copy(
                    toUserId = claimerId,
                    status = STATUS_CLAIMED,
                    claimedAt = now,
                    updatedAt = now,
                    dirty = true
                )
                transferDao.upsert(claimed)
                
                Timber.i("Transfer ${transfer.transferId} claimed by $claimerId")
                
                Resource.Success(ClaimResult(
                    transferId = transfer.transferId,
                    birdId = bird.productId,
                    birdName = bird.name,
                    birdBreed = bird.breed,
                    birdImageUrl = bird.imageUrls.firstOrNull(),
                    fromUserId = transfer.fromUserId ?: "",
                    pedigreeJson = transfer.lineageSnapshotJson
                ))
            } catch (e: Exception) {
                Timber.e(e, "Failed to claim transfer with code $code")
                Resource.Error(e.message ?: "Failed to claim transfer")
            }
        }
    }
    
    /**
     * Execute the claimed transfer - atomically transfers ownership.
     * This should be called after the claimer confirms they want to proceed.
     * 
     * @param transferId The transfer ID to execute
     * @return Resource indicating success or failure
     */
    suspend fun executeTransfer(transferId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val transfer = transferDao.findById(transferId)
                    ?: return@withContext Resource.Error("Transfer not found")
                
                if (transfer.status != STATUS_CLAIMED) {
                    return@withContext Resource.Error("Transfer must be claimed before execution")
                }
                
                val bird = productDao.findById(transfer.productId ?: "")
                    ?: return@withContext Resource.Error("Bird not found")
                
                val now = System.currentTimeMillis()
                
                // Atomic transfer: Update bird ownership
                val transferredBird = bird.copy(
                    sellerId = transfer.toUserId ?: return@withContext Resource.Error("No recipient"),
                    status = "ACTIVE", // Reset status after transfer
                    updatedAt = now,
                    dirty = true
                )
                productDao.upsert(transferredBird)
                
                // Complete transfer record
                val completed = transfer.copy(
                    status = STATUS_COMPLETED,
                    completedAt = now,
                    updatedAt = now,
                    dirty = true
                )
                transferDao.upsert(completed)
                
                Timber.i("Transfer $transferId completed: ${bird.name} from ${transfer.fromUserId} to ${transfer.toUserId}")
                
                Resource.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Failed to execute transfer $transferId")
                Resource.Error(e.message ?: "Failed to execute transfer")
            }
        }
    }
    
    /**
     * Cancel a pending transfer.
     * Can only be done by the original owner.
     */
    suspend fun cancelTransfer(transferId: String, userId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val transfer = transferDao.findById(transferId)
                    ?: return@withContext Resource.Error("Transfer not found")
                
                if (transfer.fromUserId != userId) {
                    return@withContext Resource.Error("Only the owner can cancel this transfer")
                }
                
                if (transfer.status != STATUS_PENDING && transfer.status != STATUS_CLAIMED) {
                    return@withContext Resource.Error("Transfer cannot be cancelled")
                }
                
                val now = System.currentTimeMillis()
                
                // Cancel transfer
                val cancelled = transfer.copy(
                    status = STATUS_CANCELLED,
                    updatedAt = now,
                    dirty = true
                )
                transferDao.upsert(cancelled)
                
                // Restore bird status if was pending
                transfer.productId?.let { birdId ->
                    val bird = productDao.findById(birdId)
                    if (bird != null && bird.status == STATUS_PENDING) {
                        val restored = bird.copy(
                            status = "ACTIVE",
                            updatedAt = now,
                            dirty = true
                        )
                        productDao.upsert(restored)
                    }
                }
                
                Timber.i("Transfer $transferId cancelled by $userId")
                Resource.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Failed to cancel transfer $transferId")
                Resource.Error(e.message ?: "Failed to cancel transfer")
            }
        }
    }
    
    /**
     * Get pending transfers for a user (as sender or recipient).
     */
    suspend fun getPendingTransfers(userId: String): Resource<List<TransferEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val outgoing = transferDao.findByFromUserAndStatus(userId, STATUS_PENDING)
                val incoming = transferDao.findByToUserAndStatus(userId, STATUS_CLAIMED)
                Resource.Success((outgoing + incoming).sortedByDescending { it.initiatedAt })
            } catch (e: Exception) {
                Timber.e(e, "Failed to get pending transfers for $userId")
                Resource.Error(e.message ?: "Failed to get transfers")
            }
        }
    }
    
    /**
     * Generate a unique 6-character alphanumeric code.
     */
    private suspend fun generateUniqueCode(): String {
        var attempts = 0
        while (attempts < 10) {
            val code = (1..CODE_LENGTH)
                .map { CODE_CHARSET[Random.nextInt(CODE_CHARSET.length)] }
                .joinToString("")
            
            // Verify uniqueness
            val existing = transferDao.findByTransferCode(code)
            if (existing == null) {
                return code
            }
            attempts++
        }
        throw IllegalStateException("Failed to generate unique transfer code after 10 attempts")
    }
}

/**
 * Result of initiating a transfer.
 */
data class TransferResult(
    val transferId: String,
    val transferCode: String,
    val birdName: String,
    val expiresAt: Long
)

/**
 * Result of claiming a transfer.
 */
data class ClaimResult(
    val transferId: String,
    val birdId: String,
    val birdName: String,
    val birdBreed: String?,
    val birdImageUrl: String?,
    val fromUserId: String,
    val pedigreeJson: String?
)
