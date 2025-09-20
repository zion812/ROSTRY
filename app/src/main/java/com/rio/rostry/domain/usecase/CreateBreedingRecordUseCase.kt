package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.BreedingRecordDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.model.BreedingRecord
import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class CreateBreedingRecordUseCase @Inject constructor(
    private val breedingRecordDao: BreedingRecordDao,
    private val poultryDao: PoultryDao
) {
    suspend operator fun invoke(
        parentId1: String,
        parentId2: String,
        breedingDate: Date,
        expectedHatchDate: Date,
        clutchSize: Int,
        notes: String? = null
    ): Result<String> {
        return try {
            // Validate parent IDs exist
            val parent1 = poultryDao.getPoultryById(parentId1)
            val parent2 = poultryDao.getPoultryById(parentId2)
            
            if (parent1 == null || parent2 == null) {
                return Result.failure(Exception("One or both parents not found"))
            }
            
            // Validate both parents are breeders
            if (!parent1.breederStatus || !parent2.breederStatus) {
                return Result.failure(Exception("Both parents must have breeder status"))
            }
            
            val breedingRecord = BreedingRecord(
                id = UUID.randomUUID().toString(),
                parentId1 = parentId1,
                parentId2 = parentId2,
                breedingDate = breedingDate,
                expectedHatchDate = expectedHatchDate,
                clutchSize = clutchSize,
                notes = notes,
                createdAt = Date(),
                updatedAt = Date()
            )
            
            breedingRecordDao.insertBreedingRecord(breedingRecord)
            Result.success(breedingRecord.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}