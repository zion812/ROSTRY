package com.rio.rostry.insights

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartInsightsEngine @Inject constructor() {
    data class Insight(val id: String, val title: String, val detail: String)
    suspend fun insightsFor(userId: String): List<Insight> = emptyList()
}
