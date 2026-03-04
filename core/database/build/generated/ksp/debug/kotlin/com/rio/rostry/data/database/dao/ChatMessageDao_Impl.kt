package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ChatMessageEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ChatMessageDao_Impl(
  __db: RoomDatabase,
) : ChatMessageDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfChatMessageEntity: EntityUpsertAdapter<ChatMessageEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfChatMessageEntity = EntityUpsertAdapter<ChatMessageEntity>(object :
        EntityInsertAdapter<ChatMessageEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `chat_messages` (`messageId`,`senderId`,`receiverId`,`body`,`mediaUrl`,`sentAt`,`deliveredAt`,`readAt`,`createdAt`,`updatedAt`,`lastModifiedAt`,`isDeleted`,`deletedAt`,`dirty`,`syncedAt`,`deviceTimestamp`,`type`,`metadata`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChatMessageEntity) {
        statement.bindText(1, entity.messageId)
        statement.bindText(2, entity.senderId)
        statement.bindText(3, entity.receiverId)
        statement.bindText(4, entity.body)
        val _tmpMediaUrl: String? = entity.mediaUrl
        if (_tmpMediaUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpMediaUrl)
        }
        statement.bindLong(6, entity.sentAt)
        val _tmpDeliveredAt: Long? = entity.deliveredAt
        if (_tmpDeliveredAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpDeliveredAt)
        }
        val _tmpReadAt: Long? = entity.readAt
        if (_tmpReadAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReadAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindLong(11, entity.lastModifiedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
        statement.bindLong(16, entity.deviceTimestamp)
        statement.bindText(17, entity.type)
        val _tmpMetadata: String? = entity.metadata
        if (_tmpMetadata == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpMetadata)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<ChatMessageEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `chat_messages` SET `messageId` = ?,`senderId` = ?,`receiverId` = ?,`body` = ?,`mediaUrl` = ?,`sentAt` = ?,`deliveredAt` = ?,`readAt` = ?,`createdAt` = ?,`updatedAt` = ?,`lastModifiedAt` = ?,`isDeleted` = ?,`deletedAt` = ?,`dirty` = ?,`syncedAt` = ?,`deviceTimestamp` = ?,`type` = ?,`metadata` = ? WHERE `messageId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ChatMessageEntity) {
        statement.bindText(1, entity.messageId)
        statement.bindText(2, entity.senderId)
        statement.bindText(3, entity.receiverId)
        statement.bindText(4, entity.body)
        val _tmpMediaUrl: String? = entity.mediaUrl
        if (_tmpMediaUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpMediaUrl)
        }
        statement.bindLong(6, entity.sentAt)
        val _tmpDeliveredAt: Long? = entity.deliveredAt
        if (_tmpDeliveredAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpDeliveredAt)
        }
        val _tmpReadAt: Long? = entity.readAt
        if (_tmpReadAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReadAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindLong(11, entity.lastModifiedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpDeletedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
        statement.bindLong(16, entity.deviceTimestamp)
        statement.bindText(17, entity.type)
        val _tmpMetadata: String? = entity.metadata
        if (_tmpMetadata == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpMetadata)
        }
        statement.bindText(19, entity.messageId)
      }
    })
  }

  public override suspend fun upsertAll(items: List<ChatMessageEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfChatMessageEntity.upsert(_connection, items)
  }

  public override suspend fun upsert(item: ChatMessageEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfChatMessageEntity.upsert(_connection, item)
  }

  public override fun conversation(userA: String, userB: String): Flow<List<ChatMessageEntity>> {
    val _sql: String =
        "SELECT * FROM chat_messages WHERE ((senderId = ? AND receiverId = ?) OR (senderId = ? AND receiverId = ?)) AND isDeleted = 0 ORDER BY sentAt ASC"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userA)
        _argIndex = 2
        _stmt.bindText(_argIndex, userB)
        _argIndex = 3
        _stmt.bindText(_argIndex, userB)
        _argIndex = 4
        _stmt.bindText(_argIndex, userA)
        val _columnIndexOfMessageId: Int = getColumnIndexOrThrow(_stmt, "messageId")
        val _columnIndexOfSenderId: Int = getColumnIndexOrThrow(_stmt, "senderId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _columnIndexOfDeliveredAt: Int = getColumnIndexOrThrow(_stmt, "deliveredAt")
        val _columnIndexOfReadAt: Int = getColumnIndexOrThrow(_stmt, "readAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _result: MutableList<ChatMessageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessageEntity
          val _tmpMessageId: String
          _tmpMessageId = _stmt.getText(_columnIndexOfMessageId)
          val _tmpSenderId: String
          _tmpSenderId = _stmt.getText(_columnIndexOfSenderId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpSentAt: Long
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt)
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
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          _item =
              ChatMessageEntity(_tmpMessageId,_tmpSenderId,_tmpReceiverId,_tmpBody,_tmpMediaUrl,_tmpSentAt,_tmpDeliveredAt,_tmpReadAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpType,_tmpMetadata)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int): List<ChatMessageEntity> {
    val _sql: String =
        "SELECT * FROM chat_messages WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfMessageId: Int = getColumnIndexOrThrow(_stmt, "messageId")
        val _columnIndexOfSenderId: Int = getColumnIndexOrThrow(_stmt, "senderId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _columnIndexOfDeliveredAt: Int = getColumnIndexOrThrow(_stmt, "deliveredAt")
        val _columnIndexOfReadAt: Int = getColumnIndexOrThrow(_stmt, "readAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _result: MutableList<ChatMessageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessageEntity
          val _tmpMessageId: String
          _tmpMessageId = _stmt.getText(_columnIndexOfMessageId)
          val _tmpSenderId: String
          _tmpSenderId = _stmt.getText(_columnIndexOfSenderId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpSentAt: Long
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt)
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
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          _item =
              ChatMessageEntity(_tmpMessageId,_tmpSenderId,_tmpReceiverId,_tmpBody,_tmpMediaUrl,_tmpSentAt,_tmpDeliveredAt,_tmpReadAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpType,_tmpMetadata)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(messageId: String): ChatMessageEntity? {
    val _sql: String = "SELECT * FROM chat_messages WHERE messageId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, messageId)
        val _columnIndexOfMessageId: Int = getColumnIndexOrThrow(_stmt, "messageId")
        val _columnIndexOfSenderId: Int = getColumnIndexOrThrow(_stmt, "senderId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _columnIndexOfDeliveredAt: Int = getColumnIndexOrThrow(_stmt, "deliveredAt")
        val _columnIndexOfReadAt: Int = getColumnIndexOrThrow(_stmt, "readAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _result: ChatMessageEntity?
        if (_stmt.step()) {
          val _tmpMessageId: String
          _tmpMessageId = _stmt.getText(_columnIndexOfMessageId)
          val _tmpSenderId: String
          _tmpSenderId = _stmt.getText(_columnIndexOfSenderId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpSentAt: Long
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt)
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
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          _result =
              ChatMessageEntity(_tmpMessageId,_tmpSenderId,_tmpReceiverId,_tmpBody,_tmpMediaUrl,_tmpSentAt,_tmpDeliveredAt,_tmpReadAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpType,_tmpMetadata)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(limit: Int): List<ChatMessageEntity> {
    val _sql: String = "SELECT * FROM chat_messages WHERE dirty = 1 ORDER BY sentAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfMessageId: Int = getColumnIndexOrThrow(_stmt, "messageId")
        val _columnIndexOfSenderId: Int = getColumnIndexOrThrow(_stmt, "senderId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfSentAt: Int = getColumnIndexOrThrow(_stmt, "sentAt")
        val _columnIndexOfDeliveredAt: Int = getColumnIndexOrThrow(_stmt, "deliveredAt")
        val _columnIndexOfReadAt: Int = getColumnIndexOrThrow(_stmt, "readAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastModifiedAt: Int = getColumnIndexOrThrow(_stmt, "lastModifiedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _result: MutableList<ChatMessageEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessageEntity
          val _tmpMessageId: String
          _tmpMessageId = _stmt.getText(_columnIndexOfMessageId)
          val _tmpSenderId: String
          _tmpSenderId = _stmt.getText(_columnIndexOfSenderId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpSentAt: Long
          _tmpSentAt = _stmt.getLong(_columnIndexOfSentAt)
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
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpLastModifiedAt: Long
          _tmpLastModifiedAt = _stmt.getLong(_columnIndexOfLastModifiedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          _item =
              ChatMessageEntity(_tmpMessageId,_tmpSenderId,_tmpReceiverId,_tmpBody,_tmpMediaUrl,_tmpSentAt,_tmpDeliveredAt,_tmpReadAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpLastModifiedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpType,_tmpMetadata)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingCount(userId: String): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM chat_messages WHERE (senderId = ? OR receiverId = ?) AND dirty = 1"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
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

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM chat_messages WHERE isDeleted = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(messageIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE chat_messages SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE messageId IN (")
    val _inputSize: Int = messageIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in messageIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
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
