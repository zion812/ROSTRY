package com.rio.rostry.domain.repository

import kotlinx.coroutines.flow.Flow

interface OutboxRepository {
    suspend fun enqueue(type: String, payloadJson: String)
    fun observeQueue(): Flow<List<Any>> // Simplified for scaffolding
}
