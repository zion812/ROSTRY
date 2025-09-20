package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Poultry
import kotlinx.coroutines.flow.Flow

@Dao
interface PoultryDao {
    @Query("SELECT * FROM poultry ORDER BY hatchDate DESC")
    fun getAllPoultry(): Flow<List<Poultry>>

    @Query("SELECT * FROM poultry WHERE id = :id")
    suspend fun getPoultryById(id: String): Poultry?

    @Query("SELECT * FROM poultry WHERE parentId1 = :parentId OR parentId2 = :parentId")
    fun getChildrenByParentId(parentId: String): Flow<List<Poultry>>

    @Query("SELECT * FROM poultry WHERE id = :id OR parentId1 = :id OR parentId2 = :id")
    fun getPoultryWithParentsAndChildren(id: String): Flow<List<Poultry>>

    @Query("SELECT * FROM poultry WHERE status = :status")
    fun getPoultryByStatus(status: String): Flow<List<Poultry>>

    @Query("SELECT * FROM poultry WHERE breederStatus = 1")
    fun getBreeders(): Flow<List<Poultry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoultry(poultry: Poultry)

    @Update
    suspend fun updatePoultry(poultry: Poultry)

    @Delete
    suspend fun deletePoultry(poultry: Poultry)

    @Query("SELECT * FROM poultry WHERE id IN (:ids)")
    suspend fun getPoultryByIds(ids: List<String>): List<Poultry>
}