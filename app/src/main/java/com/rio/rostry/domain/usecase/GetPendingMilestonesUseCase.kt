package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.LifecycleMilestoneDao
import com.rio.rostry.data.model.LifecycleMilestone
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPendingMilestonesUseCase @Inject constructor(
    private val lifecycleMilestoneDao: LifecycleMilestoneDao
) {
    suspend operator fun invoke(poultryId: String): List<LifecycleMilestone> {
        return lifecycleMilestoneDao.getPendingMilestonesByPoultryId(poultryId).first()
    }
}