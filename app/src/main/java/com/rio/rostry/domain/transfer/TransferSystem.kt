package com.rio.rostry.domain.transfer

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity

/**
 * Transfer System interface for managing product transfers between users.
 * 
 * Provides functionality for:
 * - Searching products owned by current user for transfer
 * - Searching for recipient users
 * - Detecting and resolving transfer conflicts
 * - Completing transfers atomically with audit trail
 */
interface TransferSystem {
    
    /**
     * Search for products owned by the current user that can be transferred.
     * 
     * @param request Search request with user ID, query, and filters
     * @return List of products matching the search criteria
     */
    suspend fun searchProducts(request: TransferSearchRequest): Result<List<TransferProductResult>>
    
    /**
     * Search for recipient users by name, email, or username.
     * Excludes the current user from results.
     * 
     * @param request Recipient search request with query and user to exclude
     * @return List of users matching the search criteria
     */
    suspend fun searchRecipients(request: RecipientSearchRequest): Result<List<RecipientResult>>
    
    /**
     * Initiate a transfer from one user to another.
     * 
     * @param productId Product to transfer
     * @param recipientId User receiving the product
     * @return Result containing transfer ID or error
     */
    suspend fun initiateTransfer(productId: String, recipientId: String): TransferOperationResult
    
    /**
     * Detect conflicts in a pending transfer.
     * 
     * @param transferId Transfer to check for conflicts
     * @return List of detected conflicts
     */
    suspend fun detectConflicts(transferId: String): Result<List<TransferConflict>>
    
    /**
     * Resolve a specific conflict by selecting a preferred value.
     * 
     * @param conflictId Identifier for the conflict
     * @param selectedValue The value chosen to resolve the conflict
     * @return Result indicating success or failure
     */
    suspend fun resolveConflict(conflictId: String, selectedValue: Any): Result<Unit>
    
    /**
     * Complete a transfer atomically.
     * Updates ownership, creates audit trail, sends notifications, and tracks analytics.
     * 
     * @param transferId Transfer to complete
     * @return Result indicating success or failure
     */
    suspend fun completeTransfer(transferId: String): Result<Unit>
}
