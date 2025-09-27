package com.rio.rostry.demo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuidedTourManager @Inject constructor(
    private val demoMode: DemoModeManager
) {
    data class Step(val id: String, val title: String, val description: String)

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    private val _steps = MutableStateFlow<List<Step>>(emptyList())
    val steps: StateFlow<List<Step>> = _steps.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun startDefaultTour() {
        if (!demoMode.isEnabled()) return
        _steps.value = listOf(
            Step("welcome", "Welcome to Demo Mode", "We'll guide you through the main workflows."),
            Step("market", "Marketplace", "Browse realistic sample listings and add to cart."),
            Step("payment", "Payments", "Run through a simulated payment without real charges."),
            Step("offline", "Offline", "Toggle offline to validate resiliency and sync."),
            Step("social", "Community", "Engage with posts and try messaging."),
            Step("monitoring", "Farm Monitoring", "Review analytics and health metrics."),
        )
        _currentIndex.value = 0
        _active.value = true
    }

    fun next() {
        if (!_active.value) return
        val next = _currentIndex.value + 1
        if (next < _steps.value.size) {
            _currentIndex.value = next
        } else {
            stop()
        }
    }

    fun prev() {
        if (!_active.value) return
        val prev = _currentIndex.value - 1
        if (prev >= 0) {
            _currentIndex.value = prev
        }
    }

    fun stop() {
        _active.value = false
        _steps.value = emptyList()
        _currentIndex.value = 0
    }
}
