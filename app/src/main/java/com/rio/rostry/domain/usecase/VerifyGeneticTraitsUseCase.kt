package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.local.PoultryTraitDao
import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyGeneticTraitsUseCase @Inject constructor(
    private val poultryDao: PoultryDao,
    private val poultryTraitDao: PoultryTraitDao
) {
    suspend operator fun invoke(poultryId: String): Result<Boolean> {
        return try {
            val poultry = poultryDao.getPoultryById(poultryId)
            
            if (poultry == null) {
                return Result.failure(Exception("Poultry not found"))
            }
            
            // If poultry has no parents, traits are valid by definition
            if (poultry.parentId1 == null && poultry.parentId2 == null) {
                return Result.success(true)
            }
            
            val poultryTraits = poultryTraitDao.getTraitsByPoultryId(poultryId).first()
            
            // In a real implementation, we would check if the traits are consistent
            // with the parents' traits based on inheritance rules
            // For now, we'll just check that traits exist if parents exist
            
            val hasParents = poultry.parentId1 != null || poultry.parentId2 != null
            val hasTraits = poultryTraits.isNotEmpty()
            
            // If poultry has parents, it should have traits
            // If poultry has no parents, it may or may not have traits
            val isValid = if (hasParents) {
                hasTraits
            } else {
                true
            }
            
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}