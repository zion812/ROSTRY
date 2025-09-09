package com.rio.rostry.data.repository

import com.rio.rostry.data.local.db.OutboxDao
import com.rio.rostry.data.local.db.OutboxEntity
import com.rio.rostry.domain.repository.OutboxRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutboxRepositoryImpl @Inject constructor(
    private val dao: OutboxDao
) : OutboxRepository {
    override suspend fun enqueue(type: String, payloadJson: String) {
        dao.insert(OutboxEntity(type = type, payloadJson = payloadJson))
    }

    override fun observeQueue(): Flow<List<Any>> = dao.streamAll().map { it as List<Any> }
}
