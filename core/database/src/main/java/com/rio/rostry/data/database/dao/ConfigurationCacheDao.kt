package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.ConfigurationCacheEntity

@Dao
interface ConfigurationCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ConfigurationCacheEntity)

    @Query("SELECT * FROM configuration_cache WHERE `key` = :key")
    suspend fun get(key: String): ConfigurationCacheEntity?

    @Query("SELECT * FROM configuration_cache ORDER BY last_updated DESC")
    suspend fun getAll(): List<ConfigurationCacheEntity>

    @Query("DELETE FROM configuration_cache WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM configuration_cache")
    suspend fun deleteAll()
}
