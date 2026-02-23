package com.rio.rostry.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DegradationManagerImpl @Inject constructor() : DegradationManager {

    private val _degradedServices = MutableStateFlow<Set<DegradedService>>(emptySet())
    override val degradedServices: StateFlow<Set<DegradedService>> = _degradedServices.asStateFlow()

    override fun reportDegraded(service: DegradedService) {
        _degradedServices.update { it + service }
    }

    override fun reportRecovered(service: DegradedService) {
        _degradedServices.update { it - service }
    }

    override fun isDegraded(service: DegradedService): Boolean {
        return _degradedServices.value.contains(service)
    }
}
