package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.DailyBirdLogDao
import com.rio.rostry.data.database.entity.DailyBirdLogEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class BirdHealthRepository @Inject constructor(
    private val dao: DailyBirdLogDao
) {
    fun getLogsForBird(birdId: String): Flow<List<DailyBirdLogEntity>> {
        return dao.getLogsForBird(birdId)
    }

    suspend fun addLog(log: DailyBirdLogEntity) {
        dao.insertLog(log)
    }
    
    suspend fun deleteLog(id: Long) {
        dao.deleteLog(id)
    }
}
