package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ErrorLogEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class ErrorLogDao_Impl(
  __db: RoomDatabase,
) : ErrorLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfErrorLogEntity: EntityInsertAdapter<ErrorLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfErrorLogEntity = object : EntityInsertAdapter<ErrorLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `error_logs` (`id`,`timestamp`,`user_id`,`operation_name`,`error_category`,`error_message`,`stack_trace`,`additional_data`,`reported`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ErrorLogEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.timestamp)
        val _tmpUserId: String? = entity.userId
        if (_tmpUserId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpUserId)
        }
        statement.bindText(4, entity.operationName)
        statement.bindText(5, entity.errorCategory)
        statement.bindText(6, entity.errorMessage)
        val _tmpStackTrace: String? = entity.stackTrace
        if (_tmpStackTrace == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpStackTrace)
        }
        val _tmpAdditionalData: String? = entity.additionalData
        if (_tmpAdditionalData == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpAdditionalData)
        }
        val _tmp: Int = if (entity.reported) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }
  }

  public override suspend fun insert(errorLog: ErrorLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfErrorLogEntity.insert(_connection, errorLog)
  }

  public override suspend fun getRecent(limit: Int): List<ErrorLogEntity> {
    val _sql: String = "SELECT * FROM error_logs ORDER BY timestamp DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "user_id")
        val _columnIndexOfOperationName: Int = getColumnIndexOrThrow(_stmt, "operation_name")
        val _columnIndexOfErrorCategory: Int = getColumnIndexOrThrow(_stmt, "error_category")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "error_message")
        val _columnIndexOfStackTrace: Int = getColumnIndexOrThrow(_stmt, "stack_trace")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additional_data")
        val _columnIndexOfReported: Int = getColumnIndexOrThrow(_stmt, "reported")
        val _result: MutableList<ErrorLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ErrorLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpUserId: String?
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          }
          val _tmpOperationName: String
          _tmpOperationName = _stmt.getText(_columnIndexOfOperationName)
          val _tmpErrorCategory: String
          _tmpErrorCategory = _stmt.getText(_columnIndexOfErrorCategory)
          val _tmpErrorMessage: String
          _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          val _tmpStackTrace: String?
          if (_stmt.isNull(_columnIndexOfStackTrace)) {
            _tmpStackTrace = null
          } else {
            _tmpStackTrace = _stmt.getText(_columnIndexOfStackTrace)
          }
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          val _tmpReported: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfReported).toInt()
          _tmpReported = _tmp != 0
          _item =
              ErrorLogEntity(_tmpId,_tmpTimestamp,_tmpUserId,_tmpOperationName,_tmpErrorCategory,_tmpErrorMessage,_tmpStackTrace,_tmpAdditionalData,_tmpReported)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByCategory(category: String, limit: Int): List<ErrorLogEntity> {
    val _sql: String =
        "SELECT * FROM error_logs WHERE error_category = ? ORDER BY timestamp DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, category)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "user_id")
        val _columnIndexOfOperationName: Int = getColumnIndexOrThrow(_stmt, "operation_name")
        val _columnIndexOfErrorCategory: Int = getColumnIndexOrThrow(_stmt, "error_category")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "error_message")
        val _columnIndexOfStackTrace: Int = getColumnIndexOrThrow(_stmt, "stack_trace")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additional_data")
        val _columnIndexOfReported: Int = getColumnIndexOrThrow(_stmt, "reported")
        val _result: MutableList<ErrorLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ErrorLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpUserId: String?
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          }
          val _tmpOperationName: String
          _tmpOperationName = _stmt.getText(_columnIndexOfOperationName)
          val _tmpErrorCategory: String
          _tmpErrorCategory = _stmt.getText(_columnIndexOfErrorCategory)
          val _tmpErrorMessage: String
          _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          val _tmpStackTrace: String?
          if (_stmt.isNull(_columnIndexOfStackTrace)) {
            _tmpStackTrace = null
          } else {
            _tmpStackTrace = _stmt.getText(_columnIndexOfStackTrace)
          }
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          val _tmpReported: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfReported).toInt()
          _tmpReported = _tmp != 0
          _item =
              ErrorLogEntity(_tmpId,_tmpTimestamp,_tmpUserId,_tmpOperationName,_tmpErrorCategory,_tmpErrorMessage,_tmpStackTrace,_tmpAdditionalData,_tmpReported)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSince(since: Long): List<ErrorLogEntity> {
    val _sql: String = "SELECT * FROM error_logs WHERE timestamp >= ? ORDER BY timestamp DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "user_id")
        val _columnIndexOfOperationName: Int = getColumnIndexOrThrow(_stmt, "operation_name")
        val _columnIndexOfErrorCategory: Int = getColumnIndexOrThrow(_stmt, "error_category")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "error_message")
        val _columnIndexOfStackTrace: Int = getColumnIndexOrThrow(_stmt, "stack_trace")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additional_data")
        val _columnIndexOfReported: Int = getColumnIndexOrThrow(_stmt, "reported")
        val _result: MutableList<ErrorLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ErrorLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpUserId: String?
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          }
          val _tmpOperationName: String
          _tmpOperationName = _stmt.getText(_columnIndexOfOperationName)
          val _tmpErrorCategory: String
          _tmpErrorCategory = _stmt.getText(_columnIndexOfErrorCategory)
          val _tmpErrorMessage: String
          _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          val _tmpStackTrace: String?
          if (_stmt.isNull(_columnIndexOfStackTrace)) {
            _tmpStackTrace = null
          } else {
            _tmpStackTrace = _stmt.getText(_columnIndexOfStackTrace)
          }
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          val _tmpReported: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfReported).toInt()
          _tmpReported = _tmp != 0
          _item =
              ErrorLogEntity(_tmpId,_tmpTimestamp,_tmpUserId,_tmpOperationName,_tmpErrorCategory,_tmpErrorMessage,_tmpStackTrace,_tmpAdditionalData,_tmpReported)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM error_logs"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOlderThan(before: Long) {
    val _sql: String = "DELETE FROM error_logs WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, before)
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
