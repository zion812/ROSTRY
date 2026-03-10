package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Task
import com.rio.rostry.domain.monitoring.repository.TaskRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TaskRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TaskRepository {

    private val tasksCollection = firestore.collection("tasks")

    override fun getTasksByFarmer(farmerId: String): Flow<List<Task>> = callbackFlow {
        val listener = tasksCollection
            .whereEqualTo("farmerId", farmerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val tasks = snapshot?.documents?.mapNotNull {
                    it.toObject(Task::class.java)
                } ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return try {
            val document = tasksCollection.document(taskId).get().await()
            if (document.exists()) {
                val task = document.toObject(Task::class.java)
                if (task != null) {
                    Result.Success(task)
                } else {
                    Result.Error(Exception("Failed to parse task data"))
                }
            } else {
                Result.Error(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            tasksCollection.document(task.id).set(task).await()
            Result.Success(task)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            tasksCollection.document(task.id).set(task).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            tasksCollection.document(taskId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun completeTask(taskId: String): Result<Unit> {
        return try {
            tasksCollection.document(taskId)
                .update("status", "COMPLETED")
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getPendingTasks(farmerId: String): Flow<List<Task>> = callbackFlow {
        val listener = tasksCollection
            .whereEqualTo("farmerId", farmerId)
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val tasks = snapshot?.documents?.mapNotNull {
                    it.toObject(Task::class.java)
                } ?: emptyList()
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }
}
