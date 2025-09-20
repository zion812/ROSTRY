package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.BreedingRecordDao
import com.rio.rostry.data.model.BreedingRecord
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBreedingRecordsByParentUseCase @Inject constructor(
    private val breedingRecordDao: BreedingRecordDao
) {
    suspend operator fun invoke(parentId: String): List<BreedingRecord> {
        return breedingRecordDao.getBreedingRecordsByParentId(parentId).first()
    }
}