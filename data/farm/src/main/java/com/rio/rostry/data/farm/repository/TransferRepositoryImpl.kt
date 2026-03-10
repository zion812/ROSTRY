package com.rio.rostry.data.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Transfer
import com.rio.rostry.core.model.TransferAnalytics
import com.rio.rostry.domain.farm.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe transfer repository stub used during modular migration.
 */
@Singleton
class TransferRepositoryImpl @Inject constructor() : TransferRepository {

    override fun getById(transferId: String): Flow<Transfer?> = flowOf(null)

    override fun getFromUser(userId: String): Flow<List<Transfer>> = flowOf(emptyList())

    override fun getToUser(userId: String): Flow<List<Transfer>> = flowOf(emptyList())

    override suspend fun upsert(transfer: Transfer): Result<Unit> = Result.Success(Unit)

    override suspend fun softDelete(transferId: String): Result<Unit> = Result.Success(Unit)

    override fun observePendingCountForFarmer(userId: String): Flow<Int> = flowOf(0)

    override fun observeAwaitingVerificationCountForFarmer(userId: String): Flow<Int> = flowOf(0)

    override fun observeRecentActivity(userId: String): Flow<List<Transfer>> = flowOf(emptyList())

    override suspend fun getTransferStatusSummary(userId: String): Result<Map<String, Int>> =
        Result.Success(emptyMap())

    override suspend fun initiateEnthusiastTransfer(
        productId: String,
        fromUserId: String,
        toUserId: String,
        lineageSnapshotJson: String,
        healthSnapshotJson: String,
        transferCode: String
    ): Result<Transfer> = Result.Error(UnsupportedOperationException("Enthusiast transfer not available in stub"))

    override suspend fun getTransferAnalytics(period: String): Result<TransferAnalytics> =
        Result.Error(UnsupportedOperationException("Transfer analytics not available in stub"))

    override suspend fun generateTransferReportCsv(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<File> = Result.Error(UnsupportedOperationException("CSV report not available in stub"))

    override suspend fun generateTransferReportPdf(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Result<File> = Result.Error(UnsupportedOperationException("PDF report not available in stub"))
}
