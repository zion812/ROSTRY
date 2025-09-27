package com.rio.rostry.demo

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackManager @Inject constructor(
    private val demoMode: DemoModeManager
) {
    data class Feedback(
        val type: String, // BUG, SUGGESTION, PERFORMANCE
        val message: String,
        val extra: Map<String, String> = emptyMap(),
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _events = MutableSharedFlow<Feedback>(extraBufferCapacity = 64)
    val events: SharedFlow<Feedback> = _events

    fun report(type: String, message: String, extra: Map<String, String> = emptyMap()) {
        if (!demoMode.isEnabled()) return
        _events.tryEmit(Feedback(type, message, extra))
    }
}
