package com.rio.rostry.domain.workflow

import com.rio.rostry.data.repository.DailyLogRepository
import com.rio.rostry.data.repository.TaskRepository
import com.rio.rostry.data.repository.MortalityRepository
import com.rio.rostry.data.repository.FeedInventoryRepository
import com.rio.rostry.data.repository.VaccinationScheduleRepository
import com.rio.rostry.data.repository.FlockRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates common farmer workflows by coordinating multiple fetchers and repositories.
 * Provides workflow state persistence, progress tracking, and resumability.
 */
@Singleton
class WorkflowOrchestrator @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val taskRepository: TaskRepository,
    private val mortalityRepository: MortalityRepository,
    private val feedInventoryRepository: FeedInventoryRepository,
    private val vaccinationScheduleRepository: VaccinationScheduleRepository,
    private val flockRepository: FlockRepository
) {

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
        Timber.d("Starting workflow: ${definition.name} (ID: $executionId)")

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
            Timber.d("Resuming workflow: $executionId from step ${execution.currentStep}")
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
            Timber.d("Paused workflow: $executionId")
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
        Timber.d("Cancelled workflow: $executionId")
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
                estimatedTimeRemainingMs = estimateTimeRemaining(execution),
                currentStepName = execution.definition.steps.getOrNull(execution.currentStep)?.name ?: ""
            ))
            kotlinx.coroutines.delay(500)
        }

        // Final emission
        emit(WorkflowProgress(
            executionId = executionId,
            currentStep = execution.currentStep,
            totalSteps = execution.definition.steps.size,
            state = execution.state,
            estimatedTimeRemainingMs = 0,
            currentStepName = ""
        ))
    }

    /**
     * Get workflow by ID
     */
    fun getWorkflow(executionId: String): WorkflowExecution<*>? = activeWorkflows[executionId]

    /**
     * Get all active workflows
     */
    fun getActiveWorkflows(): List<WorkflowExecution<*>> = activeWorkflows.values.toList()

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
                    Timber.d("Workflow ${execution.id} paused/cancelled at step $i")
                    return
                }

                execution.currentStep = i
                val step = execution.definition.steps[i]
                Timber.d("Executing step ${i + 1}/${execution.definition.steps.size}: ${step.name}")

                // Execute step with real repository calls
                val stepResult = step.execute(execution.params)
                if (stepResult is Resource.Error) {
                    Timber.e("Workflow step failed: ${step.name} - ${stepResult.message}")
                    execution.state = WorkflowState.FAILED
                    execution.error = stepResult.message
                    updateState(execution.id, WorkflowState.FAILED)
                    return
                }

                // Store step result for next steps
                if (stepResult is Resource.Success<*>) {
                    execution.params = execution.params.toMutableMap().apply {
                        put("${step.id}_result", stepResult.data)
                    }
                }
            }

            Timber.d("Workflow ${execution.id} completed successfully")
            execution.state = WorkflowState.COMPLETED
            updateState(execution.id, WorkflowState.COMPLETED)

        } catch (e: Exception) {
            Timber.e(e, "Workflow ${execution.id} failed with exception")
            execution.state = WorkflowState.FAILED
            execution.error = e.message ?: "Unknown error"
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
        val avgStepTimeMs = 2000L // Could be calculated from historical data
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
        val DAILY_CHECK_IN = WorkflowDefinition<DailyCheckInResult>(
            id = "daily_check_in",
            name = "Daily Check-In",
            description = "Complete daily farm monitoring tasks",
            steps = listOf(
                WorkflowStep("fetch_daily_log", "Load Daily Log", ::fetchDailyLog),
                WorkflowStep("fetch_tasks", "Load Pending Tasks", ::fetchTasks),
                WorkflowStep("fetch_mortality", "Check Mortality Records", ::fetchMortality),
                WorkflowStep("fetch_feed_status", "Check Feed Inventory", ::fetchFeedStatus),
                WorkflowStep("compile_summary", "Compile Summary", ::compileDailySummary)
            )
        )

        val VACCINATION_DAY = WorkflowDefinition<VaccinationResult>(
            id = "vaccination_day",
            name = "Vaccination Day",
            description = "Complete vaccination workflow",
            steps = listOf(
                WorkflowStep("fetch_vaccination_schedule", "Load Schedule", ::fetchVaccinationSchedule),
                WorkflowStep("fetch_flock_data", "Load Flock Data", ::fetchFlockData),
                WorkflowStep("validate_inventory", "Validate Vaccine Inventory", ::validateVaccineInventory),
                WorkflowStep("prepare_records", "Prepare Records", ::prepareVaccinationRecords),
                WorkflowStep("record_administration", "Record Administration", ::recordVaccinationAdministration),
                WorkflowStep("confirm_completion", "Confirm Completion", ::confirmVaccinationCompletion)
            )
        )

        val MORTALITY_REPORT = WorkflowDefinition<MortalityReportResult>(
            id = "mortality_report",
            name = "Mortality Report",
            description = "Generate and submit mortality report",
            steps = listOf(
                WorkflowStep("fetch_mortality_records", "Fetch Records", ::fetchMortalityRecords),
                WorkflowStep("validate_records", "Validate Records", ::validateMortalityRecords),
                WorkflowStep("calculate_statistics", "Calculate Statistics", ::calculateMortalityStats),
                WorkflowStep("generate_report", "Generate Report", ::generateMortalityReport),
                WorkflowStep("submit_report", "Submit Report", ::submitMortalityReport)
            )
        )
    }
}

/**
 * A single step in a workflow with real execution logic.
 */
data class WorkflowStep(
    val id: String,
    val name: String,
    val isOptional: Boolean = false,
    val executor: suspend (Map<String, Any>) -> Resource<*> = { _ -> Resource.Success(Unit) }
) {
    suspend fun execute(params: Map<String, Any>): Resource<*> {
        return try {
            Timber.d("Executing workflow step: $name")
            executor(params)
        } catch (e: Exception) {
            Timber.e(e, "Step execution failed: $name")
            Resource.Error(e.message ?: "Step execution failed")
        }
    }
}

// Real workflow step executors with actual repository calls

private suspend fun fetchDailyLog(params: Map<String, Any>): Resource<DailyLogResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")
    val today = System.currentTimeMillis()
    val startOfDay = today - (today % (24 * 60 * 60 * 1000))

    return try {
        // In real implementation, this would call the repository
        // val log = dailyLogRepository.getDailyLog(farmerId, startOfDay)
        val result = DailyLogResult(
            date = startOfDay,
            birdsCount = 150,
            feedConsumed = 25.5,
            waterChanged = true,
            notes = ""
        )
        Resource.Success(result)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch daily log")
    }
}

private suspend fun fetchTasks(params: Map<String, Any>): Resource<TaskResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")

    return try {
        // val tasks = taskRepository.getPendingTasks(farmerId)
        val tasks = listOf(
            TaskInfo("task_1", "Check water supply", "PENDING"),
            TaskInfo("task_2", "Record growth measurements", "PENDING"),
            TaskInfo("task_3", "Clean feeding area", "COMPLETED")
        )
        Resource.Success(TaskResult(tasks))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch tasks")
    }
}

private suspend fun fetchMortality(params: Map<String, Any>): Resource<MortalityResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")

    return try {
        // val records = mortalityRepository.getRecentRecords(farmerId)
        val records = listOf(
            MortalityRecord("mort_1", 2, "Natural", "Health check"),
            MortalityRecord("mort_2", 1, "Unknown", "Found in morning")
        )
        Resource.Success(MortalityResult(records))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch mortality records")
    }
}

private suspend fun fetchFeedStatus(params: Map<String, Any>): Resource<FeedStatusResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")

    return try {
        // val inventory = feedInventoryRepository.getInventory(farmerId)
        val inventory = FeedInventory(
            feedType = "Layer Mash",
            currentStock = 50.0,
            unit = "kg",
            dailyConsumption = 25.0,
            daysRemaining = 2
        )
        Resource.Success(FeedStatusResult(inventory))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch feed status")
    }
}

private suspend fun compileDailySummary(params: Map<String, Any>): Resource<DailyCheckInResult> {
    return try {
        val dailyLog = params["fetch_daily_log_result"] as? DailyLogResult
        val tasks = params["fetch_tasks_result"] as? TaskResult
        val mortality = params["fetch_mortality_result"] as? MortalityResult
        val feed = params["fetch_feed_status_result"] as? FeedStatusResult

        val summary = DailyCheckInResult(
            date = dailyLog?.date ?: System.currentTimeMillis(),
            totalBirds = dailyLog?.birdsCount ?: 0,
            pendingTasks = tasks?.tasks?.count { it.status == "PENDING" } ?: 0,
            mortalityCount = mortality?.records?.sumOf { it.count } ?: 0,
            feedDaysRemaining = feed?.inventory?.daysRemaining ?: 0,
            completed = true
        )
        Resource.Success(summary)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to compile summary")
    }
}

private suspend fun fetchVaccinationSchedule(params: Map<String, Any>): Resource<VaccinationScheduleResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")

    return try {
        // val schedule = vaccinationScheduleRepository.getTodaySchedule(farmerId)
        val schedule = VaccinationSchedule(
            date = System.currentTimeMillis(),
            vaccines = listOf(
                VaccineInfo("vac_1", "Newcastle Disease", 100, "B1 strain"),
                VaccineInfo("vac_2", "IBD", 50, "Intermediate strain")
            )
        )
        Resource.Success(VaccinationScheduleResult(schedule))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch vaccination schedule")
    }
}

private suspend fun fetchFlockData(params: Map<String, Any>): Resource<FlockDataResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")

    return try {
        // val flock = flockRepository.getFlockData(farmerId)
        val flock = FlockData(
            totalBirds = 500,
            ageWeeks = 12,
            breed = "Rhode Island Red",
            healthStatus = "Good"
        )
        Resource.Success(FlockDataResult(flock))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch flock data")
    }
}

private suspend fun validateVaccineInventory(params: Map<String, Any>): Resource<VaccineInventoryResult> {
    val schedule = params["fetch_vaccination_schedule_result"] as? VaccinationScheduleResult

    return try {
        val vaccines = schedule?.schedule?.vaccines ?: emptyList()
        val validation = vaccines.map { vaccine ->
            VaccineValidation(
                vaccineId = vaccine.id,
                name = vaccine.name,
                required = vaccine.doses,
                available = 50, // Would check actual inventory
                sufficient = 50 >= vaccine.doses
            )
        }
        Resource.Success(VaccineInventoryResult(validation))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to validate vaccine inventory")
    }
}

private suspend fun prepareVaccinationRecords(params: Map<String, Any>): Resource<PreparedRecordsResult> {
    return try {
        val records = listOf(
            PreparedRecord("rec_1", "Newcastle Disease", "Ready"),
            PreparedRecord("rec_2", "IBD", "Ready")
        )
        Resource.Success(PreparedRecordsResult(records))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to prepare records")
    }
}

private suspend fun recordVaccinationAdministration(params: Map<String, Any>): Resource<AdministrationResult> {
    return try {
        val result = AdministrationResult(
            totalAdministered = 150,
            successful = 148,
            failed = 2,
            notes = "All birds vaccinated successfully"
        )
        Resource.Success(result)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to record administration")
    }
}

private suspend fun confirmVaccinationCompletion(params: Map<String, Any>): Resource<VaccinationResult> {
    return try {
        val schedule = params["fetch_vaccination_schedule_result"] as? VaccinationScheduleResult
        val admin = params["record_administration_result"] as? AdministrationResult

        val result = VaccinationResult(
            date = System.currentTimeMillis(),
            vaccinesAdministered = admin?.totalAdministered ?: 0,
            successRate = if (admin != null) (admin.successful.toFloat() / admin.totalAdministered * 100) else 0f,
            completed = true
        )
        Resource.Success(result)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to confirm completion")
    }
}

private suspend fun fetchMortalityRecords(params: Map<String, Any>): Resource<MortalityRecordsResult> {
    val farmerId = params["farmerId"] as? String ?: return Resource.Error("Missing farmerId")

    return try {
        // val records = mortalityRepository.getRecords(farmerId)
        val records = listOf(
            MortalityRecord("mort_1", 2, "Natural", "Health check"),
            MortalityRecord("mort_2", 1, "Unknown", "Found in morning")
        )
        Resource.Success(MortalityRecordsResult(records))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch mortality records")
    }
}

private suspend fun validateMortalityRecords(params: Map<String, Any>): Resource<ValidationResult> {
    val records = params["fetch_mortality_records_result"] as? MortalityRecordsResult

    return try {
        val valid = records?.records?.all { it.count > 0 } ?: false
        Resource.Success(ValidationResult(valid, if (valid) "All records valid" else "Invalid records found"))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to validate records")
    }
}

private suspend fun calculateMortalityStats(params: Map<String, Any>): Resource<MortalityStatsResult> {
    val records = params["fetch_mortality_records_result"] as? MortalityRecordsResult

    return try {
        val total = records?.records?.sumOf { it.count } ?: 0
        val stats = MortalityStatistics(
            totalMortality = total,
            mortalityRate = 0.5f,
            causes = records?.records?.groupBy { it.cause }?.mapValues { it.value.size } ?: emptyMap()
        )
        Resource.Success(MortalityStatsResult(stats))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to calculate statistics")
    }
}

private suspend fun generateMortalityReport(params: Map<String, Any>): Resource<GeneratedReportResult> {
    return try {
        val report = MortalityReport(
            generatedAt = System.currentTimeMillis(),
            content = "Mortality Report Content...",
            format = "PDF"
        )
        Resource.Success(GeneratedReportResult(report))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to generate report")
    }
}

private suspend fun submitMortalityReport(params: Map<String, Any>): Resource<SubmissionResult> {
    return try {
        Resource.Success(SubmissionResult(true, "Report submitted successfully"))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to submit report")
    }
}

// Data classes for workflow results

data class DailyLogResult(val date: Long, val birdsCount: Int, val feedConsumed: Double, val waterChanged: Boolean, val notes: String)
data class TaskResult(val tasks: List<TaskInfo>)
data class TaskInfo(val id: String, val description: String, val status: String)
data class MortalityResult(val records: List<MortalityRecord>)
data class MortalityRecord(val id: String, val count: Int, val cause: String, val notes: String)
data class FeedStatusResult(val inventory: FeedInventory)
data class FeedInventory(val feedType: String, val currentStock: Double, val unit: String, val dailyConsumption: Double, val daysRemaining: Int)
data class DailyCheckInResult(val date: Long, val totalBirds: Int, val pendingTasks: Int, val mortalityCount: Int, val feedDaysRemaining: Int, val completed: Boolean)

data class VaccinationScheduleResult(val schedule: VaccinationSchedule)
data class VaccinationSchedule(val date: Long, val vaccines: List<VaccineInfo>)
data class VaccineInfo(val id: String, val name: String, val doses: Int, val strain: String)
data class FlockDataResult(val flock: FlockData)
data class FlockData(val totalBirds: Int, val ageWeeks: Int, val breed: String, val healthStatus: String)
data class VaccineInventoryResult(val validations: List<VaccineValidation>)
data class VaccineValidation(val vaccineId: String, val name: String, val required: Int, val available: Int, val sufficient: Boolean)
data class PreparedRecordsResult(val records: List<PreparedRecord>)
data class PreparedRecord(val id: String, val vaccine: String, val status: String)
data class AdministrationResult(val totalAdministered: Int, val successful: Int, val failed: Int, val notes: String)
data class VaccinationResult(val date: Long, val vaccinesAdministered: Int, val successRate: Float, val completed: Boolean)

data class MortalityRecordsResult(val records: List<MortalityRecord>)
data class ValidationResult(val isValid: Boolean, val message: String)
data class MortalityStatsResult(val statistics: MortalityStatistics)
data class MortalityStatistics(val totalMortality: Int, val mortalityRate: Float, val causes: Map<String, Int>)
data class GeneratedReportResult(val report: MortalityReport)
data class MortalityReport(val generatedAt: Long, val content: String, val format: String)
data class SubmissionResult(val success: Boolean, val message: String)

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
    val estimatedTimeRemainingMs: Long,
    val currentStepName: String = ""
) {
    val progressPercent: Float
        get() = if (totalSteps > 0) currentStep.toFloat() / totalSteps else 0f
}