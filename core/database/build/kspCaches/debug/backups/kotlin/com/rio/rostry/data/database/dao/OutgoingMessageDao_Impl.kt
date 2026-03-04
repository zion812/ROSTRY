package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OutgoingMessageEntity
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
public class OutgoingMessageDao_Impl(
  __db: RoomDatabase,
) : OutgoingMessageDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOutgoingMessageEntity: EntityInsertAdapter<OutgoingMessageEntity>

  private val __updateAdapterOfOutgoingMessageEntity:
      EntityDeleteOrUpdateAdapter<OutgoingMessageEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOutgoingMessageEntity = object :
        EntityInsertAdapter<OutgoingMessageEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `outgoing_messages` (`id`,`kind`,`threadOrGroupId`,`fromUserId`,`toUserId`,`bodyText`,`fileUri`,`fileName`,`status`,`priority`,`retryCount`,`maxRetries`,`lastError`,`sentAt`,`deliveredAt`,`readAt`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OutgoingMessageEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.kind)
        statement.bindText(3, entity.threadOrGroupId)
        statement.bindText(4, entity.fromUserId)
        val _tmpToUserId: String? = entity.toUserId
        if (_tmpToUserId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpToUserId)
        }
        val _tmpBodyText: String? = entity.bodyText
        if (_tmpBodyText == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBodyText)
        }
        val _tmpFileUri: String? = entity.fileUri
        if (_tmpFileUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpFileUri)
        }
        val _tmpFileName: String? = entity.fileName
        if (_tmpFileName == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpFileName)
        }
        statement.bindText(9, entity.status)
        statement.bindLong(10, entity.priority.toLong())
        statement.bindLong(11, entity.retryCount.toLong())
        statement.bindLong(12, entity.maxRetries.toLong())
        val _tmpLastError: String? = entity.lastError
        if (_tmpLastError == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpLastError)
        }
        val _tmpSentAt: Long? = entity.sentAt
        if (_tmpSentAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpSentAt)
        }
        val _tmpDeliveredAt: Long? = entity.deliveredAt
        if (_tmpDeliveredAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpDeliveredAt)
        }
        val _tmpReadAt: Long? = entity.readAt
        if (_tmpReadAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpReadAt)
        }
        statement.bindLong(17, entity.createdAt)
      }
    }
    this.__updateAdapterOfOutgoingMessageEntity = object :
        EntityDeleteOrUpdateAdapter<OutgoingMessageEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `outgoing_messages` SET `id` = ?,`kind` = ?,`threadOrGroupId` = ?,`fromUserId` = ?,`toUserId` = ?,`bodyText` = ?,`fileUri` = ?,`fileName` = ?,`status` = ?,`priority` = ?,`retryCount` = ?,`maxRetries` = ?,`lastError` = ?,`sentAt` = ?,`deliveredAt` = ?,`readAt` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: OutgoingMessageEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.kind)
        statement.bindText(3, entity.threadOrGroupId)
        statement.bindText(4, entity.fromUserId)
        val _tmpToUserId: String? = entity.toUserId
        if (_tmpToUserId == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpToUserId)
        }
        val _tmpBodyText: String? = entity.bodyText
        if (_tmpBodyText == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBodyText)
        }
        val _tmpFileUri: String? = entity.fileUri
        if (_tmpFileUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpFileUri)
        }
        val _tmpFileName: String? = entity.fileName
        if (_tmpFileName == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpFileName)
        }
        statement.bindText(9, entity.status)
        statement.bindLong(10, entity.priority.toLong())
        statement.bindLong(11, entity.retryCount.toLong())
        statement.bindLong(12, entity.maxRetries.toLong())
        val _tmpLastError: String? = entity.lastError
        if (_tmpLastError == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpLastError)
        }
        val _tmpSentAt: Long? = entity.sentAt
        if (_tmpSentAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpSentAt)
        }
        val _tmpDeliveredAt: Long? = entity.deliveredAt
        if (_tmpDeliveredAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpDeliveredAt)
        }
        val _tmpReadAt: Long? = entity.readAt
        if (_tmpReadAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpReadAt)
        }
        statement.bindLong(17, entity.createdAt)
        statement.bindText(18, entity.id)
      }
    }
  }

  public override suspend fun upsert(msg: OutgoingMessageEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOutgoingMessageEntity.insert(_connection, msg)
  }

  public override suspend fun update(msg: OutgoingMessageEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfOutgoingMessageEntity.handle(_connection, msg)
  }

  public override suspend fun getByStatus(status: String, limit: Int): List<OutgoingMessageEntity> {
    val _sql: String =
        "SELECT * FROM outgoing_messages WHERE status = ? ORDER BY createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfKind: Int = getColumnIndexOrThrow(_stmt, "kind")
        val _columnIndexOfThreadOrGroupId: Int = getColumnIndexOrThrow(_stmt, "threadOrGroupId")
        val _columnIndexOfFromUserId: Int = getColumnIndexOrThrow(_stmt, "fromUserId")
        val _columnIndexOfToUserId: Int = getColumnIndexOrThrow(_stmt, "toUserId")
        val _columnIndexOfBodyText: Int = getColumnIndexOrThrow(_stmt, "bodyText")
        val _columnIndexOfFileUri: Int = getColumnIndexOrThrow(_stmt, "fileUri")
        val _columnIndexOfFileName: Int = getColumnIndexOrThrow(_stmt, "fileName")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfLastError: Int = getColumnIndexOrThrow(_stmt, "lastError")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _columnIndexOfDeliveredAt: Int = getColumnIndexOrThrow(_stmt, "deliveredAt")
        val _columnIndexOfReadAt: Int = getColumnIndexOrThrow(_stmt, "readAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<OutgoingMessageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OutgoingMessageEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpKind: String
          _tmpKind = _stmt.getText(_columnIndexOfKind)
          val _tmpThreadOrGroupId: String
          _tmpThreadOrGroupId = _stmt.getText(_columnIndexOfThreadOrGroupId)
          val _tmpFromUserId: String
          _tmpFromUserId = _stmt.getText(_columnIndexOfFromUserId)
          val _tmpToUserId: String?
          if (_stmt.isNull(_columnIndexOfToUserId)) {
            _tmpToUserId = null
          } else {
            _tmpToUserId = _stmt.getText(_columnIndexOfToUserId)
          }
          val _tmpBodyText: String?
          if (_stmt.isNull(_columnIndexOfBodyText)) {
            _tmpBodyText = null
          } else {
            _tmpBodyText = _stmt.getText(_columnIndexOfBodyText)
          }
          val _tmpFileUri: String?
          if (_stmt.isNull(_columnIndexOfFileUri)) {
            _tmpFileUri = null
          } else {
            _tmpFileUri = _stmt.getText(_columnIndexOfFileUri)
          }
          val _tmpFileName: String?
          if (_stmt.isNull(_columnIndexOfFileName)) {
            _tmpFileName = null
          } else {
            _tmpFileName = _stmt.getText(_columnIndexOfFileName)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPriority: Int
          _tmpPriority = _stmt.getLong(_columnIndexOfPriority).toInt()
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpLastError: String?
          if (_stmt.isNull(_columnIndexOfLastError)) {
            _tmpLastError = null
          } else {
            _tmpLastError = _stmt.getText(_columnIndexOfLastError)
          }
          val _tmpSentAt: Long?
          if (_stmt.isNull(_columnIndexOfSentAt)) {
            _tmpSentAt = null
          } else {
            _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt)
          }
          val _tmpDeliveredAt: Long?
          if (_stmt.isNull(_columnIndexOfDeliveredAt)) {
            _tmpDeliveredAt = null
          } else {
            _tmpDeliveredAt = _stmt.getLong(_columnIndexOfDeliveredAt)
          }
          val _tmpReadAt: Long?
          if (_stmt.isNull(_columnIndexOfReadAt)) {
            _tmpReadAt = null
          } else {
            _tmpReadAt = _stmt.getLong(_columnIndexOfReadAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              OutgoingMessageEntity(_tmpId,_tmpKind,_tmpThreadOrGroupId,_tmpFromUserId,_tmpToUserId,_tmpBodyText,_tmpFileUri,_tmpFileName,_tmpStatus,_tmpPriority,_tmpRetryCount,_tmpMaxRetries,_tmpLastError,_tmpSentAt,_tmpDeliveredAt,_tmpReadAt,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(id: String, status: String) {
    val _sql: String = "UPDATE outgoing_messages SET status = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
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
