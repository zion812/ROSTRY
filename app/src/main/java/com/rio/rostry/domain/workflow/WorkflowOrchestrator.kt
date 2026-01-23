package com.rio.rostry.domain.workflow

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates common farmer workflows by coordinating multiple fetchers and repositories.
 * Provides workflow state persistence, progress tracking, and resumability.
 */
@Singleton
class WorkflowOrchestrator @Inject constructor() {

    private val workflowScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Active workflows
    private val activeWorkflows = ConcurrentHashMap<String, WorkflowExecution<*>>()
    
    // Observable workflow states
    private val _workflowStates = MutableStateFlow<Map<String, WorkflowState>>(emptyMap())
    val workflowStates: StateFlow<Map<String, WorkflowState>> = _workflowStates.asStateFlow()

    /**
     * Start a new workflow execution.
     */
    fun <T> startWorkflow(
        definition: WorkflowDefinition<T>,
        params: Map<String, Any> = emptyMap()
    ): WorkflowExecution<T> {
        val executionId = UUID.randomUUID().toString()
        val execution = WorkflowExecution(
            id = executionId,
            definition = definition,
            params = params,
            state = WorkflowState.PENDING
        )
        
        activeWorkflows[executionId] = execution
        updateState(executionId, WorkflowState.PENDING)
        
        // Execute asynchronously
        workflowScope.launch {
            executeWorkflow(execution)
        }
        
        return execution
    }

    /**
     * Resume a paused or failed workflow.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> resumeWorkflow(executionId: String): WorkflowExecution<T>? {
        val execution = activeWorkflows[executionId] as? WorkflowExecution<T> ?: return null
        
        if (execution.state == WorkflowState.PAUSED || execution.state == WorkflowState.FAILED) {
            workflowScope.launch {
                executeWorkflow(execution, resumeFromStep = execution.currentStep)
            }
        }
        
        return execution
    }

    /**
     * Pause an active workflow.
     */
    fun pauseWorkflow(executionId: String) {
        val execution = activeWorkflows[executionId] ?: return
        if (execution.state == WorkflowState.RUNNING) {
            execution.state = WorkflowState.PAUSED
            updateState(executionId, WorkflowState.PAUSED)
        }
    }

    /**
     * Cancel a workflow.
     */
    fun cancelWorkflow(executionId: String) {
        val execution = activeWorkflows[executionId] ?: return
        execution.state = WorkflowState.CANCELLED
        updateState(executionId, WorkflowState.CANCELLED)
        activeWorkflows.remove(executionId)
    }

    /**
     * Get workflow progress as a Flow.
     */
    fun observeWorkflow(executionId: String): Flow<WorkflowProgress> = flow {
        val execution = activeWorkflows[executionId] ?: return@flow
        
        while (execution.state == WorkflowState.PENDING || execution.state == WorkflowState.RUNNING) {
            emit(WorkflowProgress(
                executionId = executionId,
                currentStep = execution.currentStep,
                totalSteps = execution.definition.steps.size,
                state = execution.state,
                estimatedTimeRemainingMs = estimateTimeRemaining(execution)
            ))
            kotlinx.coroutines.delay(500)
        }
        
        // Final emission
        emit(WorkflowProgress(
            executionId = executionId,
            currentStep = execution.currentStep,
            totalSteps = execution.definition.steps.size,
            state = execution.state,
            estimatedTimeRemainingMs = 0
        ))
    }

    private suspend fun <T> executeWorkflow(
        execution: WorkflowExecution<T>,
        resumeFromStep: Int = 0
    ) {
        execution.state = WorkflowState.RUNNING
        updateState(execution.id, WorkflowState.RUNNING)
        
        try {
            for (i in resumeFromStep until execution.definition.steps.size) {
                // Check for pause/cancellation
                if (execution.state == WorkflowState.PAUSED || execution.state == WorkflowState.CANCELLED) {
                    return
                }
                
                execution.currentStep = i
                val step = execution.definition.steps[i]
                
                // Execute step
                val stepResult = step.execute(execution.params)
                if (stepResult is Resource.Error) {
                    execution.state = WorkflowState.FAILED
                    execution.error = stepResult.message
                    updateState(execution.id, WorkflowState.FAILED)
                    return
                }
            }
            
            execution.state = WorkflowState.COMPLETED
            updateState(execution.id, WorkflowState.COMPLETED)
            
        } catch (e: Exception) {
            execution.state = WorkflowState.FAILED
            execution.error = e.message
            updateState(execution.id, WorkflowState.FAILED)
        }
    }

    private fun updateState(executionId: String, state: WorkflowState) {
        _workflowStates.value = _workflowStates.value.toMutableMap().apply {
            put(executionId, state)
        }
    }

    private fun <T> estimateTimeRemaining(execution: WorkflowExecution<T>): Long {
        val remaining = execution.definition.steps.size - execution.currentStep
        val avgStepTimeMs = 2000L // Placeholder - could be calculated from history
        return remaining * avgStepTimeMs
    }
}

/**
 * Definition of a workflow with ordered steps.
 */
data class WorkflowDefinition<T>(
    val id: String,
    val name: String,
    val description: String,
    val steps: List<WorkflowStep>
) {
    companion object {
        // Predefined farmer workflows
        val DAILY_CHECK_IN = WorkflowDefinition<Unit>(
            id = "daily_check_in",
            name = "Daily Check-In",
            description = "Complete daily farm monitoring tasks",
            steps = listOf(
                WorkflowStep("fetch_daily_log", "Load Daily Log"),
                WorkflowStep("fetch_tasks", "Load Pending Tasks"),
                WorkflowStep("fetch_mortality", "Check Mortality Records"),
                WorkflowStep("fetch_feed_status", "Check Feed Inventory")
            )
        )
        
        val VACCINATION_DAY = WorkflowDefinition<Unit>(
            id = "vaccination_day",
            name = "Vaccination Day",
            description = "Complete vaccination workflow",
            steps = listOf(
                WorkflowStep("fetch_vaccination_schedule", "Load Schedule"),
                WorkflowStep("fetch_flock_data", "Load Flock Data"),
                WorkflowStep("prepare_records", "Prepare Records"),
                WorkflowStep("confirm_completion", "Confirm Completion")
            )
        )
    }
}

/**
 * A single step in a workflow.
 */
data class WorkflowStep(
    val id: String,
    val name: String,
    val isOptional: Boolean = false
) {
    suspend fun execute(params: Map<String, Any>): Resource<Unit> {
        // Placeholder - actual implementation would call fetchers/repositories
        kotlinx.coroutines.delay(500) // Simulate work
        return Resource.Success(Unit)
    }
}

/**
 * Runtime state of a workflow execution.
 */
data class WorkflowExecution<T>(
    val id: String,
    val definition: WorkflowDefinition<T>,
    val params: Map<String, Any>,
    var state: WorkflowState,
    var currentStep: Int = 0,
    var error: String? = null,
    val startTime: Long = System.currentTimeMillis()
)

enum class WorkflowState {
    PENDING,
    RUNNING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class WorkflowProgress(
    val executionId: String,
    val currentStep: Int,
    val totalSteps: Int,
    val state: WorkflowState,
    val estimatedTimeRemainingMs: Long
) {
    val progressPercent: Float
        get() = if (totalSteps > 0) currentStep.toFloat() / totalSteps else 0f
}
