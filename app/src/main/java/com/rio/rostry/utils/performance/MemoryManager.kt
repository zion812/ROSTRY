package com.rio.rostry.utils.performance

/**
 * Memory management utilities for image/cache tuning and leak prevention.
 * Pure scaffolding: hook into image loaders and caches from app modules.
 */
object MemoryManager {
    /** Adjust in-memory cache sizes based on runtime heuristics. */
    fun configureCaches(
        maxImagesMb: Int = defaultImageCacheMb(),
        maxOtherMb: Int = 32
    ) {
        // no-op scaffolding
    }

    /** Hint GC for large deallocations after heavy work. */
    fun hintGc() {
        System.gc()
    }

    /** A simple heuristic based on runtime memory class. */
    fun defaultImageCacheMb(runtimeMb: Int = runtimeMaxMb()): Int = (runtimeMb * 0.1).toInt().coerceAtLeast(32)

    private fun runtimeMaxMb(): Int = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt()
}
