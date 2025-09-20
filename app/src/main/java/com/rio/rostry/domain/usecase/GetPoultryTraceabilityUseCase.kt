package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.local.TransferRecordDao
import com.rio.rostry.data.model.Poultry
import com.rio.rostry.data.model.TransferRecord
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class TraceabilityInfo(
    val poultry: Poultry,
    val transferHistory: List<TransferRecord>,
    val currentOwner: String
)

class GetPoultryTraceabilityUseCase @Inject constructor(
    private val poultryDao: PoultryDao,
    private val transferRecordDao: TransferRecordDao
) {
    suspend operator fun invoke(poultryId: String): Result<TraceabilityInfo> {
        return try {
            val poultry = poultryDao.getPoultryById(poultryId)
            
            if (poultry == null) {
                return Result.failure(Exception("Poultry not found"))
            }
            
            val transferHistory = transferRecordDao.getTransferRecordsByPoultryId(poultryId).first()
            val currentOwner = transferHistory.lastOrNull()?.toOwnerId ?: "Unknown"
            
            val traceabilityInfo = TraceabilityInfo(
                poultry = poultry,
                transferHistory = transferHistory,
                currentOwner = currentOwner
            )
            
            Result.success(traceabilityInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}