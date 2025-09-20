package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.BreedingRecordDao
import com.rio.rostry.data.model.BreedingRecord
import java.util.Date
import javax.inject.Inject

class UpdateBreedingRecordUseCase @Inject constructor(
    private val breedingRecordDao: BreedingRecordDao
) {
    suspend operator fun invoke(
        breedingRecord: BreedingRecord,
        actualHatchDate: Date? = null,
        hatchCount: Int = breedingRecord.hatchCount,
        notes: String? = breedingRecord.notes
    ) {
        val updatedRecord = breedingRecord.copy(
            actualHatchDate = actualHatchDate,
            hatchCount = hatchCount,
            successRate = if (breedingRecord.clutchSize > 0) {
                hatchCount.toDouble() / breedingRecord.clutchSize.toDouble()
            } else 0.0,
            notes = notes,
            updatedAt = java.util.Date()
        )
        
        breedingRecordDao.updateBreedingRecord(updatedRecord)
    }
}