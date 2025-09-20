package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.TransferRecordDao
import com.rio.rostry.data.model.TransferRecord
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyTransferQRCodeUseCase @Inject constructor(
    private val transferRecordDao: TransferRecordDao
) {
    suspend operator fun invoke(qrCode: String): TransferRecord? {
        return transferRecordDao.getTransferRecordByQRCode(qrCode)
    }
}