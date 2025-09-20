package com.rio.rostry.domain.usecase

import android.content.Context
import androidx.work.*
import com.rio.rostry.work.MilestoneAlertWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleMilestoneAlertsUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke(milestoneId: String, alertTimeMillis: Long) {
        val currentTime = System.currentTimeMillis()
        val delay = alertTimeMillis - currentTime
        
        if (delay > 0) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<MilestoneAlertWorker>()
                .setConstraints(constraints)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        MilestoneAlertWorker.MILESTONE_ID_KEY to milestoneId
                    )
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueue(workRequest)
        }
    }
}