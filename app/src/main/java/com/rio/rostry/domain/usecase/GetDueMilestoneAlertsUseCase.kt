package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.LifecycleMilestoneDao
import com.rio.rostry.data.model.LifecycleMilestone
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class GetDueMilestoneAlertsUseCase @Inject constructor(
    private val lifecycleMilestoneDao: LifecycleMilestoneDao
) {
    suspend operator fun invoke(): List<LifecycleMilestone> {
        val currentTime = System.currentTimeMillis()
        return lifecycleMilestoneDao.getDueMilestoneAlerts(currentTime).first()
    }
}