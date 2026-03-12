package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/** Domain interface for the Aseel lifecycle engine (age stages, feather moult, etc.). */
interface AseelLifecycleEngine {
    suspend fun getCurrentStage(twinId: String): Result<Map<String, Any>>
    suspend fun getLifecycleTimeline(twinId: String): Result<List<Map<String, Any>>>
    fun calculateAgeInDays(hatchDateMillis: Long): Int
}
