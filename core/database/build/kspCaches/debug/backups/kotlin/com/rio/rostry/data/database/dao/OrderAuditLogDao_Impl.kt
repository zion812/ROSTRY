package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderAuditLogEntity
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
public class OrderAuditLogDao_Impl(
  __db: RoomDatabase,
) : OrderAuditLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderAuditLogEntity: EntityInsertAdapter<OrderAuditLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderAuditLogEntity = object : EntityInsertAdapter<OrderAuditLogEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_audit_logs` (`logId`,`orderId`,`action`,`fromState`,`toState`,`performedBy`,`performedByRole`,`description`,`metadata`,`evidenceId`,`ipAddress`,`deviceInfo`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderAuditLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.action)
        val _tmpFromState: String? = entity.fromState
        if (_tmpFromState == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFromState)
        }
        val _tmpToState: String? = entity.toState
        if (_tmpToState == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpToState)
        }
        statement.bindText(6, entity.performedBy)
        statement.bindText(7, entity.performedByRole)
        statement.bindText(8, entity.description)
        val _tmpMetadata: String? = entity.metadata
        if (_tmpMetadata == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpMetadata)
        }
        val _tmpEvidenceId: String? = entity.evidenceId
        if (_tmpEvidenceId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpEvidenceId)
        }
        val _tmpIpAddress: String? = entity.ipAddress
        if (_tmpIpAddress == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpIpAddress)
        }
        val _tmpDeviceInfo: String? = entity.deviceInfo
        if (_tmpDeviceInfo == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpDeviceInfo)
        }
        statement.bindLong(13, entity.timestamp)
      }
    }
  }

  public override suspend fun insert(log: OrderAuditLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOrderAuditLogEntity.insert(_connection, log)
  }

  public override suspend fun insertAll(logs: List<OrderAuditLogEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfOrderAuditLogEntity.insert(_connection, logs)
  }

  public override fun getOrderAuditTrail(orderId: String): Flow<List<OrderAuditLogEntity>> {
    val _sql: String = "SELECT * FROM order_audit_logs WHERE orderId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("order_audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfFromState: Int = getColumnIndexOrThrow(_stmt, "fromState")
        val _columnIndexOfToState: Int = getColumnIndexOrThrow(_stmt, "toState")
        val _columnIndexOfPerformedBy: Int = getColumnIndexOrThrow(_stmt, "performedBy")
        val _columnIndexOfPerformedByRole: Int = getColumnIndexOrThrow(_stmt, "performedByRole")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfIpAddress: Int = getColumnIndexOrThrow(_stmt, "ipAddress")
        val _columnIndexOfDeviceInfo: Int = getColumnIndexOrThrow(_stmt, "deviceInfo")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<OrderAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpFromState: String?
          if (_stmt.isNull(_columnIndexOfFromState)) {
            _tmpFromState = null
          } else {
            _tmpFromState = _stmt.getText(_columnIndexOfFromState)
          }
          val _tmpToState: String?
          if (_stmt.isNull(_columnIndexOfToState)) {
            _tmpToState = null
          } else {
            _tmpToState = _stmt.getText(_columnIndexOfToState)
          }
          val _tmpPerformedBy: String
          _tmpPerformedBy = _stmt.getText(_columnIndexOfPerformedBy)
          val _tmpPerformedByRole: String
          _tmpPerformedByRole = _stmt.getText(_columnIndexOfPerformedByRole)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfEvidenceId)) {
            _tmpEvidenceId = null
          } else {
            _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          }
          val _tmpIpAddress: String?
          if (_stmt.isNull(_columnIndexOfIpAddress)) {
            _tmpIpAddress = null
          } else {
            _tmpIpAddress = _stmt.getText(_columnIndexOfIpAddress)
          }
          val _tmpDeviceInfo: String?
          if (_stmt.isNull(_columnIndexOfDeviceInfo)) {
            _tmpDeviceInfo = null
          } else {
            _tmpDeviceInfo = _stmt.getText(_columnIndexOfDeviceInfo)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              OrderAuditLogEntity(_tmpLogId,_tmpOrderId,_tmpAction,_tmpFromState,_tmpToState,_tmpPerformedBy,_tmpPerformedByRole,_tmpDescription,_tmpMetadata,_tmpEvidenceId,_tmpIpAddress,_tmpDeviceInfo,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAuditByAction(orderId: String, action: String):
      Flow<List<OrderAuditLogEntity>> {
    val _sql: String =
        "SELECT * FROM order_audit_logs WHERE orderId = ? AND action = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("order_audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        _argIndex = 2
        _stmt.bindText(_argIndex, action)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfFromState: Int = getColumnIndexOrThrow(_stmt, "fromState")
        val _columnIndexOfToState: Int = getColumnIndexOrThrow(_stmt, "toState")
        val _columnIndexOfPerformedBy: Int = getColumnIndexOrThrow(_stmt, "performedBy")
        val _columnIndexOfPerformedByRole: Int = getColumnIndexOrThrow(_stmt, "performedByRole")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfIpAddress: Int = getColumnIndexOrThrow(_stmt, "ipAddress")
        val _columnIndexOfDeviceInfo: Int = getColumnIndexOrThrow(_stmt, "deviceInfo")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<OrderAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpFromState: String?
          if (_stmt.isNull(_columnIndexOfFromState)) {
            _tmpFromState = null
          } else {
            _tmpFromState = _stmt.getText(_columnIndexOfFromState)
          }
          val _tmpToState: String?
          if (_stmt.isNull(_columnIndexOfToState)) {
            _tmpToState = null
          } else {
            _tmpToState = _stmt.getText(_columnIndexOfToState)
          }
          val _tmpPerformedBy: String
          _tmpPerformedBy = _stmt.getText(_columnIndexOfPerformedBy)
          val _tmpPerformedByRole: String
          _tmpPerformedByRole = _stmt.getText(_columnIndexOfPerformedByRole)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfEvidenceId)) {
            _tmpEvidenceId = null
          } else {
            _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          }
          val _tmpIpAddress: String?
          if (_stmt.isNull(_columnIndexOfIpAddress)) {
            _tmpIpAddress = null
          } else {
            _tmpIpAddress = _stmt.getText(_columnIndexOfIpAddress)
          }
          val _tmpDeviceInfo: String?
          if (_stmt.isNull(_columnIndexOfDeviceInfo)) {
            _tmpDeviceInfo = null
          } else {
            _tmpDeviceInfo = _stmt.getText(_columnIndexOfDeviceInfo)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              OrderAuditLogEntity(_tmpLogId,_tmpOrderId,_tmpAction,_tmpFromState,_tmpToState,_tmpPerformedBy,_tmpPerformedByRole,_tmpDescription,_tmpMetadata,_tmpEvidenceId,_tmpIpAddress,_tmpDeviceInfo,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserAuditLogs(userId: String, limit: Int):
      Flow<List<OrderAuditLogEntity>> {
    val _sql: String =
        "SELECT * FROM order_audit_logs WHERE performedBy = ? ORDER BY timestamp DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("order_audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfFromState: Int = getColumnIndexOrThrow(_stmt, "fromState")
        val _columnIndexOfToState: Int = getColumnIndexOrThrow(_stmt, "toState")
        val _columnIndexOfPerformedBy: Int = getColumnIndexOrThrow(_stmt, "performedBy")
        val _columnIndexOfPerformedByRole: Int = getColumnIndexOrThrow(_stmt, "performedByRole")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfIpAddress: Int = getColumnIndexOrThrow(_stmt, "ipAddress")
        val _columnIndexOfDeviceInfo: Int = getColumnIndexOrThrow(_stmt, "deviceInfo")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<OrderAuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderAuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpFromState: String?
          if (_stmt.isNull(_columnIndexOfFromState)) {
            _tmpFromState = null
          } else {
            _tmpFromState = _stmt.getText(_columnIndexOfFromState)
          }
          val _tmpToState: String?
          if (_stmt.isNull(_columnIndexOfToState)) {
            _tmpToState = null
          } else {
            _tmpToState = _stmt.getText(_columnIndexOfToState)
          }
          val _tmpPerformedBy: String
          _tmpPerformedBy = _stmt.getText(_columnIndexOfPerformedBy)
          val _tmpPerformedByRole: String
          _tmpPerformedByRole = _stmt.getText(_columnIndexOfPerformedByRole)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfEvidenceId)) {
            _tmpEvidenceId = null
          } else {
            _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          }
          val _tmpIpAddress: String?
          if (_stmt.isNull(_columnIndexOfIpAddress)) {
            _tmpIpAddress = null
          } else {
            _tmpIpAddress = _stmt.getText(_columnIndexOfIpAddress)
          }
          val _tmpDeviceInfo: String?
          if (_stmt.isNull(_columnIndexOfDeviceInfo)) {
            _tmpDeviceInfo = null
          } else {
            _tmpDeviceInfo = _stmt.getText(_columnIndexOfDeviceInfo)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              OrderAuditLogEntity(_tmpLogId,_tmpOrderId,_tmpAction,_tmpFromState,_tmpToState,_tmpPerformedBy,_tmpPerformedByRole,_tmpDescription,_tmpMetadata,_tmpEvidenceId,_tmpIpAddress,_tmpDeviceInfo,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldLogs(beforeTimestamp: Long) {
    val _sql: String = "DELETE FROM order_audit_logs WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, beforeTimestamp)
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
