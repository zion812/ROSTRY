package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ConfigurationCacheEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ConfigurationCacheDao_Impl(
  __db: RoomDatabase,
) : ConfigurationCacheDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfConfigurationCacheEntity:
      EntityInsertAdapter<ConfigurationCacheEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfConfigurationCacheEntity = object :
        EntityInsertAdapter<ConfigurationCacheEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `configuration_cache` (`key`,`value`,`value_type`,`last_updated`,`source`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ConfigurationCacheEntity) {
        statement.bindText(1, entity.key)
        statement.bindText(2, entity.value)
        statement.bindText(3, entity.valueType)
        statement.bindLong(4, entity.lastUpdated)
        statement.bindText(5, entity.source)
      }
    }
  }

  public override suspend fun upsert(entity: ConfigurationCacheEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfConfigurationCacheEntity.insert(_connection, entity)
  }

  public override suspend fun `get`(key: String): ConfigurationCacheEntity? {
    val _sql: String = "SELECT * FROM configuration_cache WHERE `key` = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        val _columnIndexOfKey: Int = getColumnIndexOrThrow(_stmt, "key")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfValueType: Int = getColumnIndexOrThrow(_stmt, "value_type")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _result: ConfigurationCacheEntity?
        if (_stmt.step()) {
          val _tmpKey: String
          _tmpKey = _stmt.getText(_columnIndexOfKey)
          val _tmpValue: String
          _tmpValue = _stmt.getText(_columnIndexOfValue)
          val _tmpValueType: String
          _tmpValueType = _stmt.getText(_columnIndexOfValueType)
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          _result =
              ConfigurationCacheEntity(_tmpKey,_tmpValue,_tmpValueType,_tmpLastUpdated,_tmpSource)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<ConfigurationCacheEntity> {
    val _sql: String = "SELECT * FROM configuration_cache ORDER BY last_updated DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfKey: Int = getColumnIndexOrThrow(_stmt, "key")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfValueType: Int = getColumnIndexOrThrow(_stmt, "value_type")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _result: MutableList<ConfigurationCacheEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ConfigurationCacheEntity
          val _tmpKey: String
          _tmpKey = _stmt.getText(_columnIndexOfKey)
          val _tmpValue: String
          _tmpValue = _stmt.getText(_columnIndexOfValue)
          val _tmpValueType: String
          _tmpValueType = _stmt.getText(_columnIndexOfValueType)
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          _item =
              ConfigurationCacheEntity(_tmpKey,_tmpValue,_tmpValueType,_tmpLastUpdated,_tmpSource)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(key: String) {
    val _sql: String = "DELETE FROM configuration_cache WHERE `key` = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM configuration_cache"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
