package com.rio.rostry.workers.processors

import android.content.Context
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.domain.usecase.TaskGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processor for generating daily farmer tasks.
 * Creates vaccination, growth check, and feed logging tasks.
 */
@Singleton
class TaskGenerationProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val taskGenerator: TaskGenerator
) : LifecycleProcessor {
    
    override val processorName = "TaskGenerationProcessor"
    
    override suspend fun process(now: Long): Int {
        val startTime = System.currentTimeMillis()
        
        // Get midnight timestamp for today
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayMidnight = calendar.timeInMillis
        
        val farmerIds = productDao.getDistinctSellerIds()
        var totalTasksGenerated = 0
        
        for (farmerId in farmerIds) {
            try {
                val tasksCreated = taskGenerator.generateDailyTasks(farmerId, todayMidnight)
                totalTasksGenerated += tasksCreated
            } catch (e: Exception) {
                Timber.e(e, "Task generation failed for farmer=$farmerId")
            }
        }
        
        Timber.d("$processorName: Generated $totalTasksGenerated tasks in ${System.currentTimeMillis() - startTime}ms")
        return totalTasksGenerated
    }
}
