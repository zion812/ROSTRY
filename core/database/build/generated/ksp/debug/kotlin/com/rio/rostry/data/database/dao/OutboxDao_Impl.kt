package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OutboxEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class OutboxDao_Impl(
  __db: RoomDatabase,
) : OutboxDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOutboxEntity: EntityInsertAdapter<OutboxEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOutboxEntity = object : EntityInsertAdapter<OutboxEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `outbox` (`outboxId`,`userId`,`entityType`,`entityId`,`operation`,`payloadJson`,`createdAt`,`retryCount`,`lastAttemptAt`,`status`,`priority`,`maxRetries`,`contextJson`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OutboxEntity) {
        statement.bindText(1, entity.outboxId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.entityType)
        statement.bindText(4, entity.entityId)
        statement.bindText(5, entity.operation)
        statement.bindText(6, entity.payloadJson)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.retryCount.toLong())
        val _tmpLastAttemptAt: Long? = entity.lastAttemptAt
        if (_tmpLastAttemptAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpLastAttemptAt)
        }
        statement.bindText(10, entity.status)
        statement.bindText(11, entity.priority)
        statement.bindLong(12, entity.maxRetries.toLong())
        val _tmpContextJson: String? = entity.contextJson
        if (_tmpContextJson == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpContextJson)
        }
      }
    }
  }

  public override suspend fun insert(entry: OutboxEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfOutboxEntity.insert(_connection, entry)
  }

  public override suspend fun getPendingPrioritized(limit: Int): List<OutboxEntity> {
    val _sql: String =
        "SELECT * FROM outbox WHERE status = 'PENDING' AND retryCount < maxRetries ORDER BY CASE priority WHEN 'CRITICAL' THEN 4 WHEN 'HIGH' THEN 3 WHEN 'NORMAL' THEN 2 WHEN 'LOW' THEN 1 ELSE 0 END DESC, createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingByUser(userId: String): Flow<List<OutboxEntity>> {
    val _sql: String = "SELECT * FROM outbox WHERE userId = ? AND status != 'COMPLETED'"
    return createFlow(__db, false, arrayOf("outbox")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPendingByType(type: String, limit: Int): List<OutboxEntity> {
    val _sql: String =
        "SELECT * FROM outbox WHERE entityType = ? AND status = 'PENDING' ORDER BY createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingCountByType(userId: String, type: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM outbox WHERE userId = ? AND entityType = ? AND status = 'PENDING'"
    return createFlow(__db, false, arrayOf("outbox")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
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

  public override suspend fun getFailedByUser(userId: String, limit: Int): List<OutboxEntity> {
    val _sql: String =
        "SELECT * FROM outbox WHERE status = 'FAILED' AND userId = ? ORDER BY createdAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByTypeAndStatus(
    type: String,
    status: String,
    limit: Int,
  ): List<OutboxEntity> {
    val _sql: String =
        "SELECT * FROM outbox WHERE entityType = ? AND status = ? ORDER BY createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPending(limit: Int): List<OutboxEntity> {
    val _sql: String =
        "SELECT * FROM outbox WHERE status = 'PENDING' ORDER BY createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPendingCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM outbox WHERE status = 'PENDING' OR status = 'RETRYING'"
    return createFlow(__db, false, arrayOf("outbox")) { _connection ->
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

  public override fun getFailedCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM outbox WHERE status = 'FAILED'"
    return createFlow(__db, false, arrayOf("outbox")) { _connection ->
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

  public override fun getFailedOperations(): Flow<List<OutboxEntity>> {
    val _sql: String =
        "SELECT * FROM outbox WHERE status = 'FAILED' ORDER BY createdAt DESC LIMIT 10"
    return createFlow(__db, false, arrayOf("outbox")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfOutboxId: Int = getColumnIndexOrThrow(_stmt, "outboxId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entityType")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entityId")
        val _columnIndexOfOperation: Int = getColumnIndexOrThrow(_stmt, "operation")
        val _columnIndexOfPayloadJson: Int = getColumnIndexOrThrow(_stmt, "payloadJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfLastAttemptAt: Int = getColumnIndexOrThrow(_stmt, "lastAttemptAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<OutboxEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutboxEntity
          val _tmpOutboxId: String
          _tmpOutboxId = _stmt.getText(_columnIndexOfOutboxId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpOperation: String
          _tmpOperation = _stmt.getText(_columnIndexOfOperation)
          val _tmpPayloadJson: String
          _tmpPayloadJson = _stmt.getText(_columnIndexOfPayloadJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpLastAttemptAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAttemptAt)) {
            _tmpLastAttemptAt = null
          } else {
            _tmpLastAttemptAt = _stmt.getLong(_columnIndexOfLastAttemptAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              OutboxEntity(_tmpOutboxId,_tmpUserId,_tmpEntityType,_tmpEntityId,_tmpOperation,_tmpPayloadJson,_tmpCreatedAt,_tmpRetryCount,_tmpLastAttemptAt,_tmpStatus,_tmpPriority,_tmpMaxRetries,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    id: String,
    status: String,
    timestamp: Long,
  ) {
    val _sql: String = "UPDATE outbox SET status = ?, lastAttemptAt = ? WHERE outboxId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementRetry(id: String, timestamp: Long) {
    val _sql: String =
        "UPDATE outbox SET retryCount = retryCount + 1, lastAttemptAt = ? WHERE outboxId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeCompleted(threshold: Long) {
    val _sql: String = "DELETE FROM outbox WHERE status = 'COMPLETED' AND createdAt < ?"
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

  public override suspend fun updateStatusBatch(
    ids: List<String>,
    status: String,
    timestamp: Long,
  ) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE outbox SET status = ")
    _stringBuilder.append("?")
    _stringBuilder.append(", lastAttemptAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE outboxId IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun resetRetryAndStatus(
    id: String,
    status: String,
    timestamp: Long,
  ) {
    val _sql: String =
        "UPDATE outbox SET retryCount = 0, status = ?, lastAttemptAt = ? WHERE outboxId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, id)
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
