package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.ShowRecord
import com.rio.rostry.core.model.ShowRecordStats
import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.monitoring.mapper.ShowRecordMapper
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShowRecordRepository using Room database.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.3 - Data modules implement domain repository interfaces
 */
@Singleton
class ShowRecordRepositoryImpl @Inject constructor(
    private val showRecordDao: ShowRecordDao
) : ShowRecordRepository {
    
    override fun getRecordsForProduct(productId: String): Flow<List<ShowRecord>> {
        return showRecordDao.observeByProduct(productId).map { entities ->
            entities.map { ShowRecordMapper.toDomain(it) }
        }
    }

    override suspend fun getRecordsForProductSync(productId: String): Result<List<ShowRecord>> {
        return try {
            val records = showRecordDao.getByProduct(productId)
            Result.Success(records.map { ShowRecordMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getRecord(recordId: String): Result<ShowRecord> {
        return try {
            val record = showRecordDao.findById(recordId)
            if (record != null) {
                Result.Success(ShowRecordMapper.toDomain(record))
            } else {
                Result.Error(Exception("Record not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addRecord(record: ShowRecord): Result<Unit> {
        return try {
            showRecordDao.upsert(ShowRecordMapper.toEntity(record))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateRecord(record: ShowRecord): Result<Unit> {
        return try {
            showRecordDao.upsert(ShowRecordMapper.toEntity(record))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteRecord(recordId: String): Result<Unit> {
        return try {
            showRecordDao.softDelete(recordId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getStats(productId: String): Result<List<ShowRecordStats>> {
        return try {
            val stats = showRecordDao.getStatsByProduct(productId)
            Result.Success(stats.map { ShowRecordMapper.statsToDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

