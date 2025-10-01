package com.rio.rostry.utils.performance

/**
 * Utilities to assist with database performance tuning and maintenance.
 * Pure scaffolding. Hook into Room DAOs and database in app modules.
 */
object DatabaseOptimizer {
    /** Suggest optimal page size for list queries based on device memory. */
    fun suggestedPageSize(): Int = when (val mb = runtimeMaxMb()) {
        in 0..256 -> 20
        in 257..512 -> 40
        in 513..1024 -> 60
        else -> 80
    }

    /** Lightweight compaction hint (Room supports VACUUM pragmas via callbacks). */
    fun shouldCompactDb(dbSizeMb: Int, freeSpaceMb: Int): Boolean = dbSizeMb > 256 && freeSpaceMb > 64

    /** Basic heuristic to decide if indices may be needed due to slow queries. */
    fun needsIndex(avgQueryMs: Long, thresholdMs: Long = 50): Boolean = avgQueryMs > thresholdMs

    private fun runtimeMaxMb(): Int = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt()
}
