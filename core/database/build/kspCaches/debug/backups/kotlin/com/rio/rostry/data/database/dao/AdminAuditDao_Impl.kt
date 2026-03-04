package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AdminAuditLogEntity
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
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AdminAuditDao_Impl(
  __db: RoomDatabase,
) : AdminAuditDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAdminAuditLogEntity: EntityInsertAdapter<AdminAuditLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAdminAuditLogEntity = object : EntityInsertAdapter<AdminAuditLogEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `admin_audit_logs` (`logId`,`adminId`,`adminName`,`actionType`,`targetId`,`targetType`,`details`,`timestamp`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AdminAuditLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.adminId)
        val _tmpAdminName: String? = entity.adminName
        if (_tmpAdminName == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpAdminName)
        }
        statement.bindText(4, entity.actionType)
        val _tmpTargetId: String? = entity.targetId
        if (_tmpTargetId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpTargetId)
        }
        val _tmpTargetType: String? = entity.targetType
        if (_tmpTargetType == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpTargetType)
        }
        val _tmpDetails: String? = entity.details
        if (_tmpDetails == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpDetails)
        }
        statement.bindLong(8, entity.timestamp)
      }
    }
  }

  public override suspend fun insertLog(log: AdminAuditLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfAdminAuditLogEntity.insert(_connection, log)
  }

  public override fun getAllLogs(): Flow<List<AdminAuditLogEntity>> {
    val _sql: String = "SELECT * FROM admin_audit_logs ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("admin_audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfAdminId: Int = getColumnIndexOrThrow(_stmt, "adminId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfActionType: Int = getColumnIndexOrThrow(_stmt, "actionType")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfTargetType: Int = getColumnIndexOrThrow(_stmt, "targetType")
        val _columnIndexOfDetails: Int = getColumnIndexOrThrow(_stmt, "details")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<AdminAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AdminAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpAdminId: String
          _tmpAdminId = _stmt.getText(_columnIndexOfAdminId)
          val _tmpAdminName: String?
          if (_stmt.isNull(_columnIndexOfAdminName)) {
            _tmpAdminName = null
          } else {
            _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          }
          val _tmpActionType: String
          _tmpActionType = _stmt.getText(_columnIndexOfActionType)
          val _tmpTargetId: String?
          if (_stmt.isNull(_columnIndexOfTargetId)) {
            _tmpTargetId = null
          } else {
            _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          }
          val _tmpTargetType: String?
          if (_stmt.isNull(_columnIndexOfTargetType)) {
            _tmpTargetType = null
          } else {
            _tmpTargetType = _stmt.getText(_columnIndexOfTargetType)
          }
          val _tmpDetails: String?
          if (_stmt.isNull(_columnIndexOfDetails)) {
            _tmpDetails = null
          } else {
            _tmpDetails = _stmt.getText(_columnIndexOfDetails)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              AdminAuditLogEntity(_tmpLogId,_tmpAdminId,_tmpAdminName,_tmpActionType,_tmpTargetId,_tmpTargetType,_tmpDetails,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getLogsByAdmin(adminId: String): Flow<List<AdminAuditLogEntity>> {
    val _sql: String = "SELECT * FROM admin_audit_logs WHERE adminId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("admin_audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, adminId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfAdminId: Int = getColumnIndexOrThrow(_stmt, "adminId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfActionType: Int = getColumnIndexOrThrow(_stmt, "actionType")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfTargetType: Int = getColumnIndexOrThrow(_stmt, "targetType")
        val _columnIndexOfDetails: Int = getColumnIndexOrThrow(_stmt, "details")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<AdminAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AdminAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpAdminId: String
          _tmpAdminId = _stmt.getText(_columnIndexOfAdminId)
          val _tmpAdminName: String?
          if (_stmt.isNull(_columnIndexOfAdminName)) {
            _tmpAdminName = null
          } else {
            _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          }
          val _tmpActionType: String
          _tmpActionType = _stmt.getText(_columnIndexOfActionType)
          val _tmpTargetId: String?
          if (_stmt.isNull(_columnIndexOfTargetId)) {
            _tmpTargetId = null
          } else {
            _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          }
          val _tmpTargetType: String?
          if (_stmt.isNull(_columnIndexOfTargetType)) {
            _tmpTargetType = null
          } else {
            _tmpTargetType = _stmt.getText(_columnIndexOfTargetType)
          }
          val _tmpDetails: String?
          if (_stmt.isNull(_columnIndexOfDetails)) {
            _tmpDetails = null
          } else {
            _tmpDetails = _stmt.getText(_columnIndexOfDetails)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              AdminAuditLogEntity(_tmpLogId,_tmpAdminId,_tmpAdminName,_tmpActionType,_tmpTargetId,_tmpTargetType,_tmpDetails,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getLogsForTarget(targetId: String): Flow<List<AdminAuditLogEntity>> {
    val _sql: String = "SELECT * FROM admin_audit_logs WHERE targetId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("admin_audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, targetId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfAdminId: Int = getColumnIndexOrThrow(_stmt, "adminId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfActionType: Int = getColumnIndexOrThrow(_stmt, "actionType")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfTargetType: Int = getColumnIndexOrThrow(_stmt, "targetType")
        val _columnIndexOfDetails: Int = getColumnIndexOrThrow(_stmt, "details")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<AdminAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AdminAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpAdminId: String
          _tmpAdminId = _stmt.getText(_columnIndexOfAdminId)
          val _tmpAdminName: String?
          if (_stmt.isNull(_columnIndexOfAdminName)) {
            _tmpAdminName = null
          } else {
            _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          }
          val _tmpActionType: String
          _tmpActionType = _stmt.getText(_columnIndexOfActionType)
          val _tmpTargetId: String?
          if (_stmt.isNull(_columnIndexOfTargetId)) {
            _tmpTargetId = null
          } else {
            _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          }
          val _tmpTargetType: String?
          if (_stmt.isNull(_columnIndexOfTargetType)) {
            _tmpTargetType = null
          } else {
            _tmpTargetType = _stmt.getText(_columnIndexOfTargetType)
          }
          val _tmpDetails: String?
          if (_stmt.isNull(_columnIndexOfDetails)) {
            _tmpDetails = null
          } else {
            _tmpDetails = _stmt.getText(_columnIndexOfDetails)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              AdminAuditLogEntity(_tmpLogId,_tmpAdminId,_tmpAdminName,_tmpActionType,_tmpTargetId,_tmpTargetType,_tmpDetails,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentLogsSnapshot(limit: Int): List<AdminAuditLogEntity> {
    val _sql: String = "SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfAdminId: Int = getColumnIndexOrThrow(_stmt, "adminId")
        val _columnIndexOfAdminName: Int = getColumnIndexOrThrow(_stmt, "adminName")
        val _columnIndexOfActionType: Int = getColumnIndexOrThrow(_stmt, "actionType")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfTargetType: Int = getColumnIndexOrThrow(_stmt, "targetType")
        val _columnIndexOfDetails: Int = getColumnIndexOrThrow(_stmt, "details")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<AdminAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AdminAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpAdminId: String
          _tmpAdminId = _stmt.getText(_columnIndexOfAdminId)
          val _tmpAdminName: String?
          if (_stmt.isNull(_columnIndexOfAdminName)) {
            _tmpAdminName = null
          } else {
            _tmpAdminName = _stmt.getText(_columnIndexOfAdminName)
          }
          val _tmpActionType: String
          _tmpActionType = _stmt.getText(_columnIndexOfActionType)
          val _tmpTargetId: String?
          if (_stmt.isNull(_columnIndexOfTargetId)) {
            _tmpTargetId = null
          } else {
            _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          }
          val _tmpTargetType: String?
          if (_stmt.isNull(_columnIndexOfTargetType)) {
            _tmpTargetType = null
          } else {
            _tmpTargetType = _stmt.getText(_columnIndexOfTargetType)
          }
          val _tmpDetails: String?
          if (_stmt.isNull(_columnIndexOfDetails)) {
            _tmpDetails = null
          } else {
            _tmpDetails = _stmt.getText(_columnIndexOfDetails)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              AdminAuditLogEntity(_tmpLogId,_tmpAdminId,_tmpAdminName,_tmpActionType,_tmpTargetId,_tmpTargetType,_tmpDetails,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeOldLogs(threshold: Long) {
    val _sql: String = "DELETE FROM admin_audit_logs WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, threshold)
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
