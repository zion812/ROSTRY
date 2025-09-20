package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.LifecycleMilestoneDao
import com.rio.rostry.data.model.LifecycleMilestone
import javax.inject.Inject

class UpdateMilestoneUseCase @Inject constructor(
    private val lifecycleMilestoneDao: LifecycleMilestoneDao
) {
    suspend operator fun invoke(milestone: LifecycleMilestone) {
        val updatedMilestone = milestone.copy(updatedAt = java.util.Date())
        lifecycleMilestoneDao.updateMilestone(updatedMilestone)
    }
}