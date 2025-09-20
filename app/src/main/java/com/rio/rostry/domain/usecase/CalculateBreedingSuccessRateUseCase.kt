package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.BreedingRecordDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class CalculateBreedingSuccessRateUseCase @Inject constructor(
    private val breedingRecordDao: BreedingRecordDao
) {
    suspend operator fun invoke(parentId: String): Double {
        val breedingRecords = breedingRecordDao.getBreedingRecordsByParentId(parentId).first()
        
        if (breedingRecords.isEmpty()) return 0.0
        
        val totalSuccessRate = breedingRecords.sumOf { it.successRate }
        return totalSuccessRate / breedingRecords.size
    }
}