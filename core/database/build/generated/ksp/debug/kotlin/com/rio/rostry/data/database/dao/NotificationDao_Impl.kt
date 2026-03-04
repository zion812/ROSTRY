package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.NotificationEntity
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
public class NotificationDao_Impl(
  __db: RoomDatabase,
) : NotificationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfNotificationEntity: EntityInsertAdapter<NotificationEntity>

  private val __updateAdapterOfNotificationEntity: EntityDeleteOrUpdateAdapter<NotificationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfNotificationEntity = object : EntityInsertAdapter<NotificationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `notifications` (`notificationId`,`userId`,`title`,`message`,`type`,`deepLinkUrl`,`isRead`,`imageUrl`,`createdAt`,`isBatched`,`batchedAt`,`displayedAt`,`domain`,`userPreferenceEnabled`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: NotificationEntity) {
        statement.bindText(1, entity.notificationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.message)
        statement.bindText(5, entity.type)
        val _tmpDeepLinkUrl: String? = entity.deepLinkUrl
        if (_tmpDeepLinkUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDeepLinkUrl)
        }
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpImageUrl)
        }
        statement.bindLong(9, entity.createdAt)
        val _tmp_1: Int = if (entity.isBatched) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
        val _tmpBatchedAt: Long? = entity.batchedAt
        if (_tmpBatchedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpBatchedAt)
        }
        val _tmpDisplayedAt: Long? = entity.displayedAt
        if (_tmpDisplayedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpDisplayedAt)
        }
        val _tmpDomain: String? = entity.domain
        if (_tmpDomain == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpDomain)
        }
        val _tmp_2: Int = if (entity.userPreferenceEnabled) 1 else 0
        statement.bindLong(14, _tmp_2.toLong())
      }
    }
    this.__updateAdapterOfNotificationEntity = object :
        EntityDeleteOrUpdateAdapter<NotificationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `notifications` SET `notificationId` = ?,`userId` = ?,`title` = ?,`message` = ?,`type` = ?,`deepLinkUrl` = ?,`isRead` = ?,`imageUrl` = ?,`createdAt` = ?,`isBatched` = ?,`batchedAt` = ?,`displayedAt` = ?,`domain` = ?,`userPreferenceEnabled` = ? WHERE `notificationId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: NotificationEntity) {
        statement.bindText(1, entity.notificationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.message)
        statement.bindText(5, entity.type)
        val _tmpDeepLinkUrl: String? = entity.deepLinkUrl
        if (_tmpDeepLinkUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDeepLinkUrl)
        }
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpImageUrl)
        }
        statement.bindLong(9, entity.createdAt)
        val _tmp_1: Int = if (entity.isBatched) 1 else 0
        statement.bindLong(10, _tmp_1.toLong())
        val _tmpBatchedAt: Long? = entity.batchedAt
        if (_tmpBatchedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpBatchedAt)
        }
        val _tmpDisplayedAt: Long? = entity.displayedAt
        if (_tmpDisplayedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpDisplayedAt)
        }
        val _tmpDomain: String? = entity.domain
        if (_tmpDomain == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpDomain)
        }
        val _tmp_2: Int = if (entity.userPreferenceEnabled) 1 else 0
        statement.bindLong(14, _tmp_2.toLong())
        statement.bindText(15, entity.notificationId)
      }
    }
  }

  public override suspend fun insertNotification(notification: NotificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificationEntity.insert(_connection, notification)
  }

  public override suspend fun insertNotifications(notifications: List<NotificationEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificationEntity.insert(_connection, notifications)
  }

  public override suspend fun updateNotification(notification: NotificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfNotificationEntity.handle(_connection, notification)
  }

  public override fun getNotificationById(notificationId: String): Flow<NotificationEntity?> {
    val _sql: String = "SELECT * FROM notifications WHERE notificationId = ?"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, notificationId)
        val _columnIndexOfNotificationId: Int = getColumnIndexOrThrow(_stmt, "notificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDeepLinkUrl: Int = getColumnIndexOrThrow(_stmt, "deepLinkUrl")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsBatched: Int = getColumnIndexOrThrow(_stmt, "isBatched")
        val _columnIndexOfBatchedAt: Int = getColumnIndexOrThrow(_stmt, "batchedAt")
        val _columnIndexOfDisplayedAt: Int = getColumnIndexOrThrow(_stmt, "displayedAt")
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfUserPreferenceEnabled: Int = getColumnIndexOrThrow(_stmt,
            "userPreferenceEnabled")
        val _result: NotificationEntity?
        if (_stmt.step()) {
          val _tmpNotificationId: String
          _tmpNotificationId = _stmt.getText(_columnIndexOfNotificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDeepLinkUrl: String?
          if (_stmt.isNull(_columnIndexOfDeepLinkUrl)) {
            _tmpDeepLinkUrl = null
          } else {
            _tmpDeepLinkUrl = _stmt.getText(_columnIndexOfDeepLinkUrl)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsBatched: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatched).toInt()
          _tmpIsBatched = _tmp_1 != 0
          val _tmpBatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfBatchedAt)) {
            _tmpBatchedAt = null
          } else {
            _tmpBatchedAt = _stmt.getLong(_columnIndexOfBatchedAt)
          }
          val _tmpDisplayedAt: Long?
          if (_stmt.isNull(_columnIndexOfDisplayedAt)) {
            _tmpDisplayedAt = null
          } else {
            _tmpDisplayedAt = _stmt.getLong(_columnIndexOfDisplayedAt)
          }
          val _tmpDomain: String?
          if (_stmt.isNull(_columnIndexOfDomain)) {
            _tmpDomain = null
          } else {
            _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          }
          val _tmpUserPreferenceEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfUserPreferenceEnabled).toInt()
          _tmpUserPreferenceEnabled = _tmp_2 != 0
          _result =
              NotificationEntity(_tmpNotificationId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpType,_tmpDeepLinkUrl,_tmpIsRead,_tmpImageUrl,_tmpCreatedAt,_tmpIsBatched,_tmpBatchedAt,_tmpDisplayedAt,_tmpDomain,_tmpUserPreferenceEnabled)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity>> {
    val _sql: String = "SELECT * FROM notifications WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfNotificationId: Int = getColumnIndexOrThrow(_stmt, "notificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDeepLinkUrl: Int = getColumnIndexOrThrow(_stmt, "deepLinkUrl")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsBatched: Int = getColumnIndexOrThrow(_stmt, "isBatched")
        val _columnIndexOfBatchedAt: Int = getColumnIndexOrThrow(_stmt, "batchedAt")
        val _columnIndexOfDisplayedAt: Int = getColumnIndexOrThrow(_stmt, "displayedAt")
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfUserPreferenceEnabled: Int = getColumnIndexOrThrow(_stmt,
            "userPreferenceEnabled")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpNotificationId: String
          _tmpNotificationId = _stmt.getText(_columnIndexOfNotificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDeepLinkUrl: String?
          if (_stmt.isNull(_columnIndexOfDeepLinkUrl)) {
            _tmpDeepLinkUrl = null
          } else {
            _tmpDeepLinkUrl = _stmt.getText(_columnIndexOfDeepLinkUrl)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsBatched: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatched).toInt()
          _tmpIsBatched = _tmp_1 != 0
          val _tmpBatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfBatchedAt)) {
            _tmpBatchedAt = null
          } else {
            _tmpBatchedAt = _stmt.getLong(_columnIndexOfBatchedAt)
          }
          val _tmpDisplayedAt: Long?
          if (_stmt.isNull(_columnIndexOfDisplayedAt)) {
            _tmpDisplayedAt = null
          } else {
            _tmpDisplayedAt = _stmt.getLong(_columnIndexOfDisplayedAt)
          }
          val _tmpDomain: String?
          if (_stmt.isNull(_columnIndexOfDomain)) {
            _tmpDomain = null
          } else {
            _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          }
          val _tmpUserPreferenceEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfUserPreferenceEnabled).toInt()
          _tmpUserPreferenceEnabled = _tmp_2 != 0
          _item =
              NotificationEntity(_tmpNotificationId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpType,_tmpDeepLinkUrl,_tmpIsRead,_tmpImageUrl,_tmpCreatedAt,_tmpIsBatched,_tmpBatchedAt,_tmpDisplayedAt,_tmpDomain,_tmpUserPreferenceEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUnreadNotificationsByUserId(userId: String):
      Flow<List<NotificationEntity>> {
    val _sql: String =
        "SELECT * FROM notifications WHERE userId = ? AND isRead = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfNotificationId: Int = getColumnIndexOrThrow(_stmt, "notificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDeepLinkUrl: Int = getColumnIndexOrThrow(_stmt, "deepLinkUrl")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsBatched: Int = getColumnIndexOrThrow(_stmt, "isBatched")
        val _columnIndexOfBatchedAt: Int = getColumnIndexOrThrow(_stmt, "batchedAt")
        val _columnIndexOfDisplayedAt: Int = getColumnIndexOrThrow(_stmt, "displayedAt")
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfUserPreferenceEnabled: Int = getColumnIndexOrThrow(_stmt,
            "userPreferenceEnabled")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpNotificationId: String
          _tmpNotificationId = _stmt.getText(_columnIndexOfNotificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDeepLinkUrl: String?
          if (_stmt.isNull(_columnIndexOfDeepLinkUrl)) {
            _tmpDeepLinkUrl = null
          } else {
            _tmpDeepLinkUrl = _stmt.getText(_columnIndexOfDeepLinkUrl)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsBatched: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatched).toInt()
          _tmpIsBatched = _tmp_1 != 0
          val _tmpBatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfBatchedAt)) {
            _tmpBatchedAt = null
          } else {
            _tmpBatchedAt = _stmt.getLong(_columnIndexOfBatchedAt)
          }
          val _tmpDisplayedAt: Long?
          if (_stmt.isNull(_columnIndexOfDisplayedAt)) {
            _tmpDisplayedAt = null
          } else {
            _tmpDisplayedAt = _stmt.getLong(_columnIndexOfDisplayedAt)
          }
          val _tmpDomain: String?
          if (_stmt.isNull(_columnIndexOfDomain)) {
            _tmpDomain = null
          } else {
            _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          }
          val _tmpUserPreferenceEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfUserPreferenceEnabled).toInt()
          _tmpUserPreferenceEnabled = _tmp_2 != 0
          _item =
              NotificationEntity(_tmpNotificationId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpType,_tmpDeepLinkUrl,_tmpIsRead,_tmpImageUrl,_tmpCreatedAt,_tmpIsBatched,_tmpBatchedAt,_tmpDisplayedAt,_tmpDomain,_tmpUserPreferenceEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBatchedNotifications(userId: String): List<NotificationEntity> {
    val _sql: String =
        "SELECT * FROM notifications WHERE userId = ? AND isBatched = 1 ORDER BY createdAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfNotificationId: Int = getColumnIndexOrThrow(_stmt, "notificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDeepLinkUrl: Int = getColumnIndexOrThrow(_stmt, "deepLinkUrl")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsBatched: Int = getColumnIndexOrThrow(_stmt, "isBatched")
        val _columnIndexOfBatchedAt: Int = getColumnIndexOrThrow(_stmt, "batchedAt")
        val _columnIndexOfDisplayedAt: Int = getColumnIndexOrThrow(_stmt, "displayedAt")
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfUserPreferenceEnabled: Int = getColumnIndexOrThrow(_stmt,
            "userPreferenceEnabled")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpNotificationId: String
          _tmpNotificationId = _stmt.getText(_columnIndexOfNotificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDeepLinkUrl: String?
          if (_stmt.isNull(_columnIndexOfDeepLinkUrl)) {
            _tmpDeepLinkUrl = null
          } else {
            _tmpDeepLinkUrl = _stmt.getText(_columnIndexOfDeepLinkUrl)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsBatched: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatched).toInt()
          _tmpIsBatched = _tmp_1 != 0
          val _tmpBatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfBatchedAt)) {
            _tmpBatchedAt = null
          } else {
            _tmpBatchedAt = _stmt.getLong(_columnIndexOfBatchedAt)
          }
          val _tmpDisplayedAt: Long?
          if (_stmt.isNull(_columnIndexOfDisplayedAt)) {
            _tmpDisplayedAt = null
          } else {
            _tmpDisplayedAt = _stmt.getLong(_columnIndexOfDisplayedAt)
          }
          val _tmpDomain: String?
          if (_stmt.isNull(_columnIndexOfDomain)) {
            _tmpDomain = null
          } else {
            _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          }
          val _tmpUserPreferenceEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfUserPreferenceEnabled).toInt()
          _tmpUserPreferenceEnabled = _tmp_2 != 0
          _item =
              NotificationEntity(_tmpNotificationId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpType,_tmpDeepLinkUrl,_tmpIsRead,_tmpImageUrl,_tmpCreatedAt,_tmpIsBatched,_tmpBatchedAt,_tmpDisplayedAt,_tmpDomain,_tmpUserPreferenceEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeBatchedCount(userId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM notifications WHERE userId = ? AND isBatched = 1"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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

  public override fun getNotificationsByDomain(
    userId: String,
    domain: String,
    limit: Int,
  ): Flow<List<NotificationEntity>> {
    val _sql: String =
        "SELECT * FROM notifications WHERE userId = ? AND domain = ? ORDER BY createdAt DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("notifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, domain)
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfNotificationId: Int = getColumnIndexOrThrow(_stmt, "notificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDeepLinkUrl: Int = getColumnIndexOrThrow(_stmt, "deepLinkUrl")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfIsBatched: Int = getColumnIndexOrThrow(_stmt, "isBatched")
        val _columnIndexOfBatchedAt: Int = getColumnIndexOrThrow(_stmt, "batchedAt")
        val _columnIndexOfDisplayedAt: Int = getColumnIndexOrThrow(_stmt, "displayedAt")
        val _columnIndexOfDomain: Int = getColumnIndexOrThrow(_stmt, "domain")
        val _columnIndexOfUserPreferenceEnabled: Int = getColumnIndexOrThrow(_stmt,
            "userPreferenceEnabled")
        val _result: MutableList<NotificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificationEntity
          val _tmpNotificationId: String
          _tmpNotificationId = _stmt.getText(_columnIndexOfNotificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDeepLinkUrl: String?
          if (_stmt.isNull(_columnIndexOfDeepLinkUrl)) {
            _tmpDeepLinkUrl = null
          } else {
            _tmpDeepLinkUrl = _stmt.getText(_columnIndexOfDeepLinkUrl)
          }
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpIsBatched: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatched).toInt()
          _tmpIsBatched = _tmp_1 != 0
          val _tmpBatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfBatchedAt)) {
            _tmpBatchedAt = null
          } else {
            _tmpBatchedAt = _stmt.getLong(_columnIndexOfBatchedAt)
          }
          val _tmpDisplayedAt: Long?
          if (_stmt.isNull(_columnIndexOfDisplayedAt)) {
            _tmpDisplayedAt = null
          } else {
            _tmpDisplayedAt = _stmt.getLong(_columnIndexOfDisplayedAt)
          }
          val _tmpDomain: String?
          if (_stmt.isNull(_columnIndexOfDomain)) {
            _tmpDomain = null
          } else {
            _tmpDomain = _stmt.getText(_columnIndexOfDomain)
          }
          val _tmpUserPreferenceEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfUserPreferenceEnabled).toInt()
          _tmpUserPreferenceEnabled = _tmp_2 != 0
          _item =
              NotificationEntity(_tmpNotificationId,_tmpUserId,_tmpTitle,_tmpMessage,_tmpType,_tmpDeepLinkUrl,_tmpIsRead,_tmpImageUrl,_tmpCreatedAt,_tmpIsBatched,_tmpBatchedAt,_tmpDisplayedAt,_tmpDomain,_tmpUserPreferenceEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAsRead(notificationId: String) {
    val _sql: String = "UPDATE notifications SET isRead = 1 WHERE notificationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, notificationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markAllAsReadForUser(userId: String) {
    val _sql: String = "UPDATE notifications SET isRead = 1 WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteNotificationById(notificationId: String) {
    val _sql: String = "DELETE FROM notifications WHERE notificationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, notificationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteNotificationsForUser(userId: String) {
    val _sql: String = "DELETE FROM notifications WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteReadNotificationsForUser(userId: String) {
    val _sql: String = "DELETE FROM notifications WHERE userId = ? AND isRead = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllNotifications() {
    val _sql: String = "DELETE FROM notifications"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markBatchDisplayed(notificationIds: List<String>, displayedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE notifications SET isBatched = 0, displayedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE notificationId IN (")
    val _inputSize: Int = notificationIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, displayedAt)
        _argIndex = 2
        for (_item: String in notificationIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldReadNotifications(userId: String, threshold: Long) {
    val _sql: String =
        "DELETE FROM notifications WHERE userId = ? AND isRead = 1 AND displayedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
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
