package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.BreedingRecordDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyLineageUseCase @Inject constructor(
    private val poultryDao: PoultryDao,
    private val breedingRecordDao: BreedingRecordDao
) {
    suspend operator fun invoke(poultryId: String): Result<Boolean> {
        return try {
            val poultry = poultryDao.getPoultryById(poultryId)
            
            if (poultry == null) {
                return Result.failure(Exception("Poultry not found"))
            }
            
            // Verify parent IDs exist if specified
            var isValid = true
            
            if (poultry.parentId1 != null) {
                val parent1 = poultryDao.getPoultryById(poultry.parentId1)
                if (parent1 == null) {
                    isValid = false
                }
            }
            
            if (poultry.parentId2 != null) {
                val parent2 = poultryDao.getPoultryById(poultry.parentId2)
                if (parent2 == null) {
                    isValid = false
                }
            }
            
            // Verify breeding records exist for this poultry if it has parents
            if (poultry.parentId1 != null && poultry.parentId2 != null) {
                val breedingRecords = breedingRecordDao.getBreedingRecordsByParentPair(
                    poultry.parentId1!!, 
                    poultry.parentId2!!
                ).first()
                
                if (breedingRecords.isEmpty()) {
                    isValid = false
                }
            }
            
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}