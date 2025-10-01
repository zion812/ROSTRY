package com.rio.rostry.monitoring

/**
 * Lightweight performance monitoring facade. Can be bridged to Firebase Performance or custom trackers.
 */
object PerformanceMonitor {
    fun startTrace(name: String): PerfTrace = PerfTrace(name)

    fun recordMetric(name: String, value: Long) {
        // no-op scaffolding; wire to Firebase Performance or custom metrics sink
    }
}

class PerfTrace internal constructor(private val name: String) : AutoCloseable {
    private val startNs: Long = System.nanoTime()

    fun putMetric(key: String, value: Long) {
        // no-op scaffolding
    }

    override fun close() {
        val elapsedMs = (System.nanoTime() - startNs) / 1_000_000
        PerformanceMonitor.recordMetric("trace:$name", elapsedMs)
    }
}
