package com.rio.rostry.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for locally caching configuration values.
 */
@Entity(tableName = "configuration_cache")
data class ConfigurationCacheEntity(
    @PrimaryKey
    val key: String,

    val value: String,

    @ColumnInfo(name = "value_type")
    val valueType: String,

    @ColumnInfo(name = "last_updated", index = true)
    val lastUpdated: Long,

    val source: String
)
