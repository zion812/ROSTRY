package com.rio.rostry.workers.processors

import javax.inject.Inject

/**
 * Base interface for modular lifecycle processors.
 * Each processor handles a single responsibility extracted from LifecycleWorker.
 */
interface LifecycleProcessor {
    /**
     * Process the lifecycle check for this specific responsibility.
     * @param now Current timestamp in milliseconds
     * @return Number of items processed (for logging/metrics)
     */
    suspend fun process(now: Long): Int
    
    /**
     * Human-readable name for logging.
     */
    val processorName: String
}
