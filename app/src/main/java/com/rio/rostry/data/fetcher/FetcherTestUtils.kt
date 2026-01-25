package com.rio.rostry.data.fetcher

import com.rio.rostry.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Testing utilities for fetcher unit and integration tests.
 * Provides mock implementations, fixtures, and controllable delays/errors.
 */

/**
 * Mock implementation for testing fetcher flows.
 * Does NOT extend FetcherCoordinator - uses composition instead.
 */
class MockFetcherResponder {
    private val mockResponses = mutableMapOf<String, Any?>()
    private val mockErrors = mutableMapOf<String, Throwable>()
    private val mockDelays = mutableMapOf<String, Long>()

    /**
     * Set a mock response for a fetcher.
     */
    fun <T> setResponse(fetcherId: String, response: T) {
        mockResponses[fetcherId] = response
    }

    /**
     * Set an error to be thrown for a fetcher.
     */
    fun setError(fetcherId: String, error: Throwable) {
        mockErrors[fetcherId] = error
    }

    /**
     * Set a delay before responding (simulates network latency).
     */
    fun setDelay(fetcherId: String, delayMs: Long) {
        mockDelays[fetcherId] = delayMs
    }

    /**
     * Clear all mocks.
     */
    fun clearMocks() {
        mockResponses.clear()
        mockErrors.clear()
        mockDelays.clear()
    }

    /**
     * Create a mock flow for a given request.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> createMockFlow(request: ClientRequest): Flow<Resource<T>> = flow {
        emit(Resource.Loading<T>() as Resource<T>)

        // Simulate delay if configured
        mockDelays[request.fetcherId]?.let { delay(it) }

        // Check for configured error
        mockErrors[request.fetcherId]?.let { error ->
            emit(Resource.Error<T>(error.message ?: "Mock error") as Resource<T>)
            return@flow
        }

        // Return mock response
        val response = mockResponses[request.fetcherId] as? T
        if (response != null) {
            emit(Resource.Success(response))
        } else {
            emit(Resource.Error<T>("No mock response configured for ${request.fetcherId}") as Resource<T>)
        }
    }
}

/**
 * Test fixtures for common farmer scenarios.
 */
object FetcherTestFixtures {

    /**
     * Create a mock daily log entry.
     */
    fun createDailyLog(
        id: String = "log_1",
        date: Long = System.currentTimeMillis(),
        notes: String = "Test daily log"
    ) = mapOf(
        "id" to id,
        "date" to date,
        "notes" to notes
    )

    /**
     * Create mock tasks list.
     */
    fun createTasks(count: Int = 5) = (1..count).map { i ->
        mapOf(
            "id" to "task_$i",
            "title" to "Task $i",
            "completed" to (i % 2 == 0),
            "priority" to if (i <= 2) "HIGH" else "NORMAL"
        )
    }

    /**
     * Create mock vaccination records.
     */
    fun createVaccinationRecords(count: Int = 3) = (1..count).map { i ->
        mapOf(
            "id" to "vax_$i",
            "name" to "Vaccine $i",
            "scheduledDate" to System.currentTimeMillis() + (i * 86400000L),
            "administered" to false
        )
    }

    /**
     * Create mock mortality record.
     */
    fun createMortalityRecord(
        count: Int = 5,
        cause: String = "Unknown"
    ) = mapOf(
        "id" to "mort_${System.currentTimeMillis()}",
        "count" to count,
        "cause" to cause,
        "date" to System.currentTimeMillis()
    )
}

/**
 * Fake repository base for testing.
 * Allows controlling delays and errors.
 */
abstract class FakeRepository {
    var simulatedDelayMs: Long = 0
    var simulatedError: Throwable? = null

    protected suspend fun <T> withSimulation(block: suspend () -> T): Resource<T> {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }
        simulatedError?.let {
            return Resource.Error(it.message ?: "Simulated error")
        }
        return try {
            Resource.Success(block())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}

/**
 * Extension to create a test FetcherRegistry with predefined fetchers.
 */
fun createTestRegistry(): FetcherRegistry {
    return FetcherRegistry().apply {
        register(fetcher<Map<String, Any>>(FetcherRegistry.ID_DAILY_LOG) {
            name("Daily Log")
            cacheFor(FetcherDurations.MINUTES_2)
            priority(FetcherPriority.HIGH)
        })
        register(fetcher<List<Map<String, Any>>>(FetcherRegistry.ID_TASKS) {
            name("Tasks")
            cacheFor(FetcherDurations.MINUTES_5)
            priority(FetcherPriority.HIGH)
            dependsOn(FetcherRegistry.ID_USER_PROFILE)
        })
        register(fetcher<List<Map<String, Any>>>(FetcherRegistry.ID_VACCINATION) {
            name("Vaccination")
            cacheFor(FetcherDurations.MINUTES_15)
            priority(FetcherPriority.NORMAL)
        })
    }
}

/**
 * Assert helper for Resource results.
 */
object ResourceAssertions {
    fun <T> assertSuccess(resource: Resource<T>): T {
        assert(resource is Resource.Success) { "Expected Success but got ${resource::class.simpleName}" }
        return (resource as Resource.Success).data!!
    }

    fun <T> assertError(resource: Resource<T>): String {
        assert(resource is Resource.Error) { "Expected Error but got ${resource::class.simpleName}" }
        return (resource as Resource.Error).message ?: ""
    }

    fun <T> assertLoading(resource: Resource<T>) {
        assert(resource is Resource.Loading) { "Expected Loading but got ${resource::class.simpleName}" }
    }
}
