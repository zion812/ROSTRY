package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Transfer
import com.rio.rostry.core.model.TransferAnalytics
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Repository contract for managing asset transfers between users.
 * 
 * Handles transfer lifecycle, analytics, and reporting for farm asset ownership changes.
 */
interface TransferRepository {
    /**
     * Gets a transfer by ID.
     */
    fun getById(transferId: String): Flow<Transfer?>
    
    /**
     * Gets all transfers from a specific user.
     */
    fun getFromUser(userId: String): Flow<List<Transfer>>
    
    /**
     * Gets all transfers to a specific user.
     */
    fun getToUser(userId: String): Flow<List<Transfer>>
    
    /**
     * Creates or updates a transfer.
     */
    suspend fun upsert(transfer: Transfer): Result<Unit>
    
    /**
     * Soft deletes a transfer.
     */
    suspend fun softDelete(transferId: String): Result<Unit>
    
    /**
     * Observes the count of pending transfers for a farmer.
     */
    fun observePendingCountForFarmer(userId: String): Flow<Int>
    
    /**
     * Observes the count of transfers awaiting verification for a farmer.
     */
    fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int>
    
    /**
     * Observes recent transfer activity for a user (last 7 days).
     */
    fun observeRecentActivity(userId: String): Flow<List<Transfer>>
    
    /**
     * Gets a summary of transfer statuses for a user.
     */
    suspend fun getTransferStatusSummary(userId: String): Result<Map<String, Int>>
    
    /**
     * Initiates an enthusiast transfer with verification code.
     */
    suspend fun initiateEnthusiastTransfer(
        productId: String,
        fromUserId: String,
        toUserId: String,
        lineageSnapshotJson: String,
        healthSnapshotJson: String,
        transferCode: String
    ): Result<Transfer>
    
    /**
     * Gets transfer analytics for a specific period.
     */
    suspend fun getTransferAnalytics(period: String): Result<TransferAnalytics>
    
    /**
     * Generates a CSV report of transfers for a user within a date range.
     */
    suspend fun generateTransferReportCsv(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<File>
    
    /**
     * Generates a PDF report of transfers for a user within a date range.
     */
    suspend fun generateTransferReportPdf(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<File>
}

