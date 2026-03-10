package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.domain.monitoring.repository.TaskSchedulingRepository
import com.rio.rostry.core.model.TaskRecurrence
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.monitoring.mapper.toTaskRecurrence
import com.rio.rostry.data.monitoring.mapper.toEntity
import com.rio.rostry.data.database.dao.TaskRecurrenceDao
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TaskSchedulingRepository.
 * 
 * Handles recurring tasks, scheduled operations, and automated task generation
 * for farm management activities.
 */
@Singleton
class TaskSchedulingRepositoryImpl @Inject constructor(
    private val dao: TaskRecurrenceDao
) : TaskSchedulingRepository {

    override suspend fun createRecurrence(recurrence: TaskRecurrence): Result<Unit> {
        return try {
            dao.insert(recurrence.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create task recurrence")
            Result.Error(e)
        }
    }

    override suspend fun getRecurrenceForTask(taskId: String): Result<TaskRecurrence?> {
        return try {
            val entity = dao.getRecurrenceForTask(taskId)
            Result.Success(entity?.toTaskRecurrence())
        } catch (e: Exception) {
            Timber.e(e, "Failed to get recurrence for task: $taskId")
            Result.Error(e)
        }
    }

    override suspend fun getDueRecurrences(currentTime: Long): Result<List<TaskRecurrence>> {
        return try {
            val entities = dao.getDueRecurrences(currentTime)
            Result.Success(entities.map { it.toTaskRecurrence() })
        } catch (e: Exception) {
            Timber.e(e, "Failed to get due recurrences")
            Result.Error(e)
        }
    }
}
