package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ShowRecordDao
import com.rio.rostry.data.database.dao.ShowRecordStats
import com.rio.rostry.data.database.entity.ShowRecordEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing Show/Competition records.
 */
interface ShowRecordRepository {
    fun getRecordsForProduct(productId: String): Flow<List<ShowRecordEntity>>
    suspend fun getRecordsForProductSync(productId: String): Resource<List<ShowRecordEntity>>
    suspend fun getRecord(recordId: String): Resource<ShowRecordEntity>
    suspend fun addRecord(record: ShowRecordEntity): Resource<Unit>
    suspend fun updateRecord(record: ShowRecordEntity): Resource<Unit>
    suspend fun deleteRecord(recordId: String): Resource<Unit>
    suspend fun getStats(productId: String): Resource<List<ShowRecordStats>>
}

@Singleton
class ShowRecordRepositoryImpl @Inject constructor(
    private val showRecordDao: ShowRecordDao
) : ShowRecordRepository {
    
    override fun getRecordsForProduct(productId: String): Flow<List<ShowRecordEntity>> {
        return showRecordDao.observeByProduct(productId)
    }

    override suspend fun getRecordsForProductSync(productId: String): Resource<List<ShowRecordEntity>> {
        return try {
            val records = showRecordDao.getByProduct(productId)
            Resource.Success(records)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch records")
        }
    }

    override suspend fun getRecord(recordId: String): Resource<ShowRecordEntity> {
        return try {
            val record = showRecordDao.findById(recordId)
            if (record != null) {
                Resource.Success(record)
            } else {
                Resource.Error("Record not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch record")
        }
    }

    override suspend fun addRecord(record: ShowRecordEntity): Resource<Unit> {
        return try {
            showRecordDao.upsert(record)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add record")
        }
    }

    override suspend fun updateRecord(record: ShowRecordEntity): Resource<Unit> {
        return try {
            showRecordDao.upsert(record)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update record")
        }
    }

    override suspend fun deleteRecord(recordId: String): Resource<Unit> {
        return try {
            showRecordDao.softDelete(recordId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete record")
        }
    }
    
    override suspend fun getStats(productId: String): Resource<List<ShowRecordStats>> {
        return try {
            val stats = showRecordDao.getStatsByProduct(productId)
            Resource.Success(stats)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch stats")
        }
    }
}
