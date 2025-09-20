package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.LifecycleMilestoneDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyLifecycleMilestonesUseCase @Inject constructor(
    private val poultryDao: PoultryDao,
    private val lifecycleMilestoneDao: LifecycleMilestoneDao
) {
    suspend operator fun invoke(poultryId: String): Result<Boolean> {
        return try {
            val poultry = poultryDao.getPoultryById(poultryId)
            
            if (poultry == null) {
                return Result.failure(Exception("Poultry not found"))
            }
            
            val milestones = lifecycleMilestoneDao.getMilestonesByPoultryId(poultryId).first()
            
            // Check if milestones exist for this poultry
            val hasMilestones = milestones.isNotEmpty()
            
            // In a real implementation, we would check:
            // 1. All required milestones are present
            // 2. Milestones are in chronological order
            // 3. Milestone dates are consistent with poultry hatch date
            // 4. Completed milestones have valid completion dates
            
            Result.success(hasMilestones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}