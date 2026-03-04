package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ThreadMetadataEntity
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
public class ThreadMetadataDao_Impl(
  __db: RoomDatabase,
) : ThreadMetadataDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfThreadMetadataEntity: EntityUpsertAdapter<ThreadMetadataEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfThreadMetadataEntity = EntityUpsertAdapter<ThreadMetadataEntity>(object :
        EntityInsertAdapter<ThreadMetadataEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `thread_metadata` (`threadId`,`title`,`contextType`,`relatedEntityId`,`topic`,`participantIds`,`lastMessageAt`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ThreadMetadataEntity) {
        statement.bindText(1, entity.threadId)
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTitle)
        }
        val _tmpContextType: String? = entity.contextType
        if (_tmpContextType == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpContextType)
        }
        val _tmpRelatedEntityId: String? = entity.relatedEntityId
        if (_tmpRelatedEntityId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpRelatedEntityId)
        }
        val _tmpTopic: String? = entity.topic
        if (_tmpTopic == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpTopic)
        }
        statement.bindText(6, entity.participantIds)
        statement.bindLong(7, entity.lastMessageAt)
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<ThreadMetadataEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `thread_metadata` SET `threadId` = ?,`title` = ?,`contextType` = ?,`relatedEntityId` = ?,`topic` = ?,`participantIds` = ?,`lastMessageAt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `threadId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ThreadMetadataEntity) {
        statement.bindText(1, entity.threadId)
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTitle)
        }
        val _tmpContextType: String? = entity.contextType
        if (_tmpContextType == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpContextType)
        }
        val _tmpRelatedEntityId: String? = entity.relatedEntityId
        if (_tmpRelatedEntityId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpRelatedEntityId)
        }
        val _tmpTopic: String? = entity.topic
        if (_tmpTopic == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpTopic)
        }
        statement.bindText(6, entity.participantIds)
        statement.bindLong(7, entity.lastMessageAt)
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
        statement.bindText(10, entity.threadId)
      }
    })
  }

  public override suspend fun upsert(metadata: ThreadMetadataEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfThreadMetadataEntity.upsert(_connection, metadata)
  }

  public override suspend fun getByThreadId(threadId: String): ThreadMetadataEntity? {
    val _sql: String = "SELECT * FROM thread_metadata WHERE threadId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, threadId)
        val _columnIndexOfThreadId: Int = getColumnIndexOrThrow(_stmt, "threadId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContextType: Int = getColumnIndexOrThrow(_stmt, "contextType")
        val _columnIndexOfRelatedEntityId: Int = getColumnIndexOrThrow(_stmt, "relatedEntityId")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfParticipantIds: Int = getColumnIndexOrThrow(_stmt, "participantIds")
        val _columnIndexOfLastMessageAt: Int = getColumnIndexOrThrow(_stmt, "lastMessageAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: ThreadMetadataEntity?
        if (_stmt.step()) {
          val _tmpThreadId: String
          _tmpThreadId = _stmt.getText(_columnIndexOfThreadId)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContextType: String?
          if (_stmt.isNull(_columnIndexOfContextType)) {
            _tmpContextType = null
          } else {
            _tmpContextType = _stmt.getText(_columnIndexOfContextType)
          }
          val _tmpRelatedEntityId: String?
          if (_stmt.isNull(_columnIndexOfRelatedEntityId)) {
            _tmpRelatedEntityId = null
          } else {
            _tmpRelatedEntityId = _stmt.getText(_columnIndexOfRelatedEntityId)
          }
          val _tmpTopic: String?
          if (_stmt.isNull(_columnIndexOfTopic)) {
            _tmpTopic = null
          } else {
            _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          }
          val _tmpParticipantIds: String
          _tmpParticipantIds = _stmt.getText(_columnIndexOfParticipantIds)
          val _tmpLastMessageAt: Long
          _tmpLastMessageAt = _stmt.getLong(_columnIndexOfLastMessageAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              ThreadMetadataEntity(_tmpThreadId,_tmpTitle,_tmpContextType,_tmpRelatedEntityId,_tmpTopic,_tmpParticipantIds,_tmpLastMessageAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamUserThreads(userId: String): Flow<List<ThreadMetadataEntity>> {
    val _sql: String =
        "SELECT * FROM thread_metadata WHERE participantIds LIKE '%' || ? || '%' ORDER BY lastMessageAt DESC"
    return createFlow(__db, false, arrayOf("thread_metadata")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfThreadId: Int = getColumnIndexOrThrow(_stmt, "threadId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContextType: Int = getColumnIndexOrThrow(_stmt, "contextType")
        val _columnIndexOfRelatedEntityId: Int = getColumnIndexOrThrow(_stmt, "relatedEntityId")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfParticipantIds: Int = getColumnIndexOrThrow(_stmt, "participantIds")
        val _columnIndexOfLastMessageAt: Int = getColumnIndexOrThrow(_stmt, "lastMessageAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ThreadMetadataEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ThreadMetadataEntity
          val _tmpThreadId: String
          _tmpThreadId = _stmt.getText(_columnIndexOfThreadId)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContextType: String?
          if (_stmt.isNull(_columnIndexOfContextType)) {
            _tmpContextType = null
          } else {
            _tmpContextType = _stmt.getText(_columnIndexOfContextType)
          }
          val _tmpRelatedEntityId: String?
          if (_stmt.isNull(_columnIndexOfRelatedEntityId)) {
            _tmpRelatedEntityId = null
          } else {
            _tmpRelatedEntityId = _stmt.getText(_columnIndexOfRelatedEntityId)
          }
          val _tmpTopic: String?
          if (_stmt.isNull(_columnIndexOfTopic)) {
            _tmpTopic = null
          } else {
            _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          }
          val _tmpParticipantIds: String
          _tmpParticipantIds = _stmt.getText(_columnIndexOfParticipantIds)
          val _tmpLastMessageAt: Long
          _tmpLastMessageAt = _stmt.getLong(_columnIndexOfLastMessageAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              ThreadMetadataEntity(_tmpThreadId,_tmpTitle,_tmpContextType,_tmpRelatedEntityId,_tmpTopic,_tmpParticipantIds,_tmpLastMessageAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamByContextType(type: String): Flow<List<ThreadMetadataEntity>> {
    val _sql: String =
        "SELECT * FROM thread_metadata WHERE contextType = ? ORDER BY lastMessageAt DESC"
    return createFlow(__db, false, arrayOf("thread_metadata")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfThreadId: Int = getColumnIndexOrThrow(_stmt, "threadId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfContextType: Int = getColumnIndexOrThrow(_stmt, "contextType")
        val _columnIndexOfRelatedEntityId: Int = getColumnIndexOrThrow(_stmt, "relatedEntityId")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfParticipantIds: Int = getColumnIndexOrThrow(_stmt, "participantIds")
        val _columnIndexOfLastMessageAt: Int = getColumnIndexOrThrow(_stmt, "lastMessageAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ThreadMetadataEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ThreadMetadataEntity
          val _tmpThreadId: String
          _tmpThreadId = _stmt.getText(_columnIndexOfThreadId)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpContextType: String?
          if (_stmt.isNull(_columnIndexOfContextType)) {
            _tmpContextType = null
          } else {
            _tmpContextType = _stmt.getText(_columnIndexOfContextType)
          }
          val _tmpRelatedEntityId: String?
          if (_stmt.isNull(_columnIndexOfRelatedEntityId)) {
            _tmpRelatedEntityId = null
          } else {
            _tmpRelatedEntityId = _stmt.getText(_columnIndexOfRelatedEntityId)
          }
          val _tmpTopic: String?
          if (_stmt.isNull(_columnIndexOfTopic)) {
            _tmpTopic = null
          } else {
            _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          }
          val _tmpParticipantIds: String
          _tmpParticipantIds = _stmt.getText(_columnIndexOfParticipantIds)
          val _tmpLastMessageAt: Long
          _tmpLastMessageAt = _stmt.getLong(_columnIndexOfLastMessageAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              ThreadMetadataEntity(_tmpThreadId,_tmpTitle,_tmpContextType,_tmpRelatedEntityId,_tmpTopic,_tmpParticipantIds,_tmpLastMessageAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateLastMessageTime(threadId: String, timestamp: Long) {
    val _sql: String =
        "UPDATE thread_metadata SET lastMessageAt = ?, updatedAt = ? WHERE threadId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, threadId)
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
