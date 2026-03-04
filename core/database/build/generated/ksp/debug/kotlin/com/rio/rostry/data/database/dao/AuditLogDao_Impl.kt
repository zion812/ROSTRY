package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AuditLogEntity
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
public class AuditLogDao_Impl(
  __db: RoomDatabase,
) : AuditLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAuditLogEntity: EntityInsertAdapter<AuditLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAuditLogEntity = object : EntityInsertAdapter<AuditLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `audit_logs` (`logId`,`type`,`refId`,`action`,`actorUserId`,`detailsJson`,`createdAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AuditLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.type)
        statement.bindText(3, entity.refId)
        statement.bindText(4, entity.action)
        val _tmpActorUserId: String? = entity.actorUserId
        if (_tmpActorUserId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpActorUserId)
        }
        val _tmpDetailsJson: String? = entity.detailsJson
        if (_tmpDetailsJson == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDetailsJson)
        }
        statement.bindLong(7, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(entity: AuditLogEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfAuditLogEntity.insert(_connection, entity)
  }

  public override suspend fun getAll(): List<AuditLogEntity> {
    val _sql: String = "SELECT * FROM audit_logs ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfRefId: Int = getColumnIndexOrThrow(_stmt, "refId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfActorUserId: Int = getColumnIndexOrThrow(_stmt, "actorUserId")
        val _columnIndexOfDetailsJson: Int = getColumnIndexOrThrow(_stmt, "detailsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<AuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpRefId: String
          _tmpRefId = _stmt.getText(_columnIndexOfRefId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpActorUserId: String?
          if (_stmt.isNull(_columnIndexOfActorUserId)) {
            _tmpActorUserId = null
          } else {
            _tmpActorUserId = _stmt.getText(_columnIndexOfActorUserId)
          }
          val _tmpDetailsJson: String?
          if (_stmt.isNull(_columnIndexOfDetailsJson)) {
            _tmpDetailsJson = null
          } else {
            _tmpDetailsJson = _stmt.getText(_columnIndexOfDetailsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              AuditLogEntity(_tmpLogId,_tmpType,_tmpRefId,_tmpAction,_tmpActorUserId,_tmpDetailsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByRef(refId: String): List<AuditLogEntity> {
    val _sql: String = "SELECT * FROM audit_logs WHERE refId = ? ORDER BY createdAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, refId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfRefId: Int = getColumnIndexOrThrow(_stmt, "refId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfActorUserId: Int = getColumnIndexOrThrow(_stmt, "actorUserId")
        val _columnIndexOfDetailsJson: Int = getColumnIndexOrThrow(_stmt, "detailsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<AuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpRefId: String
          _tmpRefId = _stmt.getText(_columnIndexOfRefId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpActorUserId: String?
          if (_stmt.isNull(_columnIndexOfActorUserId)) {
            _tmpActorUserId = null
          } else {
            _tmpActorUserId = _stmt.getText(_columnIndexOfActorUserId)
          }
          val _tmpDetailsJson: String?
          if (_stmt.isNull(_columnIndexOfDetailsJson)) {
            _tmpDetailsJson = null
          } else {
            _tmpDetailsJson = _stmt.getText(_columnIndexOfDetailsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              AuditLogEntity(_tmpLogId,_tmpType,_tmpRefId,_tmpAction,_tmpActorUserId,_tmpDetailsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamByRef(refId: String): Flow<List<AuditLogEntity>> {
    val _sql: String = "SELECT * FROM audit_logs WHERE refId = ? ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("audit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, refId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfRefId: Int = getColumnIndexOrThrow(_stmt, "refId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfActorUserId: Int = getColumnIndexOrThrow(_stmt, "actorUserId")
        val _columnIndexOfDetailsJson: Int = getColumnIndexOrThrow(_stmt, "detailsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<AuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpRefId: String
          _tmpRefId = _stmt.getText(_columnIndexOfRefId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpActorUserId: String?
          if (_stmt.isNull(_columnIndexOfActorUserId)) {
            _tmpActorUserId = null
          } else {
            _tmpActorUserId = _stmt.getText(_columnIndexOfActorUserId)
          }
          val _tmpDetailsJson: String?
          if (_stmt.isNull(_columnIndexOfDetailsJson)) {
            _tmpDetailsJson = null
          } else {
            _tmpDetailsJson = _stmt.getText(_columnIndexOfDetailsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              AuditLogEntity(_tmpLogId,_tmpType,_tmpRefId,_tmpAction,_tmpActorUserId,_tmpDetailsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByType(type: String, limit: Int): List<AuditLogEntity> {
    val _sql: String = "SELECT * FROM audit_logs WHERE type = ? ORDER BY createdAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfRefId: Int = getColumnIndexOrThrow(_stmt, "refId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfActorUserId: Int = getColumnIndexOrThrow(_stmt, "actorUserId")
        val _columnIndexOfDetailsJson: Int = getColumnIndexOrThrow(_stmt, "detailsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<AuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpRefId: String
          _tmpRefId = _stmt.getText(_columnIndexOfRefId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpActorUserId: String?
          if (_stmt.isNull(_columnIndexOfActorUserId)) {
            _tmpActorUserId = null
          } else {
            _tmpActorUserId = _stmt.getText(_columnIndexOfActorUserId)
          }
          val _tmpDetailsJson: String?
          if (_stmt.isNull(_columnIndexOfDetailsJson)) {
            _tmpDetailsJson = null
          } else {
            _tmpDetailsJson = _stmt.getText(_columnIndexOfDetailsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              AuditLogEntity(_tmpLogId,_tmpType,_tmpRefId,_tmpAction,_tmpActorUserId,_tmpDetailsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByActor(userId: String, limit: Int): List<AuditLogEntity> {
    val _sql: String =
        "SELECT * FROM audit_logs WHERE actorUserId = ? ORDER BY createdAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfRefId: Int = getColumnIndexOrThrow(_stmt, "refId")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfActorUserId: Int = getColumnIndexOrThrow(_stmt, "actorUserId")
        val _columnIndexOfDetailsJson: Int = getColumnIndexOrThrow(_stmt, "detailsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<AuditLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AuditLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpRefId: String
          _tmpRefId = _stmt.getText(_columnIndexOfRefId)
          val _tmpAction: String
          _tmpAction = _stmt.getText(_columnIndexOfAction)
          val _tmpActorUserId: String?
          if (_stmt.isNull(_columnIndexOfActorUserId)) {
            _tmpActorUserId = null
          } else {
            _tmpActorUserId = _stmt.getText(_columnIndexOfActorUserId)
          }
          val _tmpDetailsJson: String?
          if (_stmt.isNull(_columnIndexOfDetailsJson)) {
            _tmpDetailsJson = null
          } else {
            _tmpDetailsJson = _stmt.getText(_columnIndexOfDetailsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              AuditLogEntity(_tmpLogId,_tmpType,_tmpRefId,_tmpAction,_tmpActorUserId,_tmpDetailsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
