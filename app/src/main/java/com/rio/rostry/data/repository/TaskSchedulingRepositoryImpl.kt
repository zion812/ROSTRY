package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.TaskRecurrenceDao
import com.rio.rostry.data.database.entity.TaskRecurrenceEntity
import com.rio.rostry.domain.repository.TaskSchedulingRepository
import javax.inject.Inject

class TaskSchedulingRepositoryImpl @Inject constructor(
    private val dao: TaskRecurrenceDao
) : TaskSchedulingRepository {

    override suspend fun createRecurrence(recurrence: TaskRecurrenceEntity) {
        dao.insert(recurrence)
    }

    override suspend fun getRecurrenceForTask(taskId: String): TaskRecurrenceEntity? {
        return dao.getRecurrenceForTask(taskId)
    }

    override suspend fun getDueRecurrences(currentTime: Long): List<TaskRecurrenceEntity> {
        return dao.getDueRecurrences(currentTime)
    }
}
