package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.VaccinationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationRecordDao {
    @Query("SELECT * FROM vaccination_records WHERE poultryId = :poultryId ORDER BY vaccinationDate ASC")
    fun getVaccinationRecordsByPoultryId(poultryId: String): Flow<List<VaccinationRecord>>

    @Query("SELECT * FROM vaccination_records WHERE poultryId = :poultryId AND nextDueDate <= :currentDate")
    fun getOverdueVaccinations(poultryId: String, currentDate: Long): Flow<List<VaccinationRecord>>

    @Query("SELECT * FROM vaccination_records WHERE id = :id")
    suspend fun getVaccinationRecordById(id: String): VaccinationRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccinationRecord(vaccinationRecord: VaccinationRecord)

    @Update
    suspend fun updateVaccinationRecord(vaccinationRecord: VaccinationRecord)

    @Delete
    suspend fun deleteVaccinationRecord(vaccinationRecord: VaccinationRecord)
}