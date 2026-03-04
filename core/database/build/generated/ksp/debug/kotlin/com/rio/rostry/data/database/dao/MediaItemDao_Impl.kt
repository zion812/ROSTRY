package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MediaItemEntity
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
public class MediaItemDao_Impl(
  __db: RoomDatabase,
) : MediaItemDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMediaItemEntity: EntityInsertAdapter<MediaItemEntity>

  private val __deleteAdapterOfMediaItemEntity: EntityDeleteOrUpdateAdapter<MediaItemEntity>

  private val __updateAdapterOfMediaItemEntity: EntityDeleteOrUpdateAdapter<MediaItemEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMediaItemEntity = object : EntityInsertAdapter<MediaItemEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `media_items` (`mediaId`,`assetId`,`url`,`localPath`,`mediaType`,`dateAdded`,`fileSize`,`width`,`height`,`duration`,`thumbnailUrl`,`uploadStatus`,`createdAt`,`updatedAt`,`isCached`,`lastAccessedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MediaItemEntity) {
        statement.bindText(1, entity.mediaId)
        val _tmpAssetId: String? = entity.assetId
        if (_tmpAssetId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpAssetId)
        }
        statement.bindText(3, entity.url)
        val _tmpLocalPath: String? = entity.localPath
        if (_tmpLocalPath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLocalPath)
        }
        statement.bindText(5, entity.mediaType)
        statement.bindLong(6, entity.dateAdded)
        statement.bindLong(7, entity.fileSize)
        val _tmpWidth: Int? = entity.width
        if (_tmpWidth == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpWidth.toLong())
        }
        val _tmpHeight: Int? = entity.height
        if (_tmpHeight == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpHeight.toLong())
        }
        val _tmpDuration: Int? = entity.duration
        if (_tmpDuration == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpDuration.toLong())
        }
        val _tmpThumbnailUrl: String? = entity.thumbnailUrl
        if (_tmpThumbnailUrl == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpThumbnailUrl)
        }
        statement.bindText(12, entity.uploadStatus)
        statement.bindLong(13, entity.createdAt)
        statement.bindLong(14, entity.updatedAt)
        val _tmp: Int = if (entity.isCached) 1 else 0
        statement.bindLong(15, _tmp.toLong())
        val _tmpLastAccessedAt: Long? = entity.lastAccessedAt
        if (_tmpLastAccessedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpLastAccessedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
      }
    }
    this.__deleteAdapterOfMediaItemEntity = object : EntityDeleteOrUpdateAdapter<MediaItemEntity>()
        {
      protected override fun createQuery(): String = "DELETE FROM `media_items` WHERE `mediaId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MediaItemEntity) {
        statement.bindText(1, entity.mediaId)
      }
    }
    this.__updateAdapterOfMediaItemEntity = object : EntityDeleteOrUpdateAdapter<MediaItemEntity>()
        {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `media_items` SET `mediaId` = ?,`assetId` = ?,`url` = ?,`localPath` = ?,`mediaType` = ?,`dateAdded` = ?,`fileSize` = ?,`width` = ?,`height` = ?,`duration` = ?,`thumbnailUrl` = ?,`uploadStatus` = ?,`createdAt` = ?,`updatedAt` = ?,`isCached` = ?,`lastAccessedAt` = ?,`dirty` = ? WHERE `mediaId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MediaItemEntity) {
        statement.bindText(1, entity.mediaId)
        val _tmpAssetId: String? = entity.assetId
        if (_tmpAssetId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpAssetId)
        }
        statement.bindText(3, entity.url)
        val _tmpLocalPath: String? = entity.localPath
        if (_tmpLocalPath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLocalPath)
        }
        statement.bindText(5, entity.mediaType)
        statement.bindLong(6, entity.dateAdded)
        statement.bindLong(7, entity.fileSize)
        val _tmpWidth: Int? = entity.width
        if (_tmpWidth == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpWidth.toLong())
        }
        val _tmpHeight: Int? = entity.height
        if (_tmpHeight == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpHeight.toLong())
        }
        val _tmpDuration: Int? = entity.duration
        if (_tmpDuration == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpDuration.toLong())
        }
        val _tmpThumbnailUrl: String? = entity.thumbnailUrl
        if (_tmpThumbnailUrl == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpThumbnailUrl)
        }
        statement.bindText(12, entity.uploadStatus)
        statement.bindLong(13, entity.createdAt)
        statement.bindLong(14, entity.updatedAt)
        val _tmp: Int = if (entity.isCached) 1 else 0
        statement.bindLong(15, _tmp.toLong())
        val _tmpLastAccessedAt: Long? = entity.lastAccessedAt
        if (_tmpLastAccessedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpLastAccessedAt)
        }
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
        statement.bindText(18, entity.mediaId)
      }
    }
  }

  public override suspend fun insertMedia(media: MediaItemEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMediaItemEntity.insert(_connection, media)
  }

  public override suspend fun deleteMedia(media: MediaItemEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfMediaItemEntity.handle(_connection, media)
  }

  public override suspend fun updateMedia(media: MediaItemEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfMediaItemEntity.handle(_connection, media)
  }

  public override suspend fun queryMedia(
    assetId: String?,
    limit: Int,
    offset: Int,
  ): List<MediaItemEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM media_items 
        |        WHERE (? IS NULL OR assetId = ?)
        |        AND uploadStatus = 'COMPLETED'
        |        ORDER BY dateAdded DESC 
        |        LIMIT ? OFFSET ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (assetId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, assetId)
        }
        _argIndex = 2
        if (assetId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, assetId)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 4
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "mediaType")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfFileSize: Int = getColumnIndexOrThrow(_stmt, "fileSize")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfUploadStatus: Int = getColumnIndexOrThrow(_stmt, "uploadStatus")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsCached: Int = getColumnIndexOrThrow(_stmt, "isCached")
        val _columnIndexOfLastAccessedAt: Int = getColumnIndexOrThrow(_stmt, "lastAccessedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MediaItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MediaItemEntity
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpMediaType: String
          _tmpMediaType = _stmt.getText(_columnIndexOfMediaType)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpFileSize: Long
          _tmpFileSize = _stmt.getLong(_columnIndexOfFileSize)
          val _tmpWidth: Int?
          if (_stmt.isNull(_columnIndexOfWidth)) {
            _tmpWidth = null
          } else {
            _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          }
          val _tmpHeight: Int?
          if (_stmt.isNull(_columnIndexOfHeight)) {
            _tmpHeight = null
          } else {
            _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          }
          val _tmpDuration: Int?
          if (_stmt.isNull(_columnIndexOfDuration)) {
            _tmpDuration = null
          } else {
            _tmpDuration = _stmt.getLong(_columnIndexOfDuration).toInt()
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpUploadStatus: String
          _tmpUploadStatus = _stmt.getText(_columnIndexOfUploadStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsCached: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCached).toInt()
          _tmpIsCached = _tmp != 0
          val _tmpLastAccessedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmpLastAccessedAt = null
          } else {
            _tmpLastAccessedAt = _stmt.getLong(_columnIndexOfLastAccessedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item =
              MediaItemEntity(_tmpMediaId,_tmpAssetId,_tmpUrl,_tmpLocalPath,_tmpMediaType,_tmpDateAdded,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpDuration,_tmpThumbnailUrl,_tmpUploadStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsCached,_tmpLastAccessedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun queryMediaByTags(
    tagType: String,
    values: List<String>,
    limit: Int,
    offset: Int,
  ): List<MediaItemEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT mi.* FROM media_items mi")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        INNER JOIN media_tags mt ON mi.mediaId = mt.mediaId")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE mt.tagType = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND mt.value IN (")
    val _inputSize: Int = values.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        AND uploadStatus = 'COMPLETED'")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        GROUP BY mi.mediaId")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        ORDER BY mi.dateAdded DESC")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        LIMIT ")
    _stringBuilder.append("?")
    _stringBuilder.append(" OFFSET ")
    _stringBuilder.append("?")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tagType)
        _argIndex = 2
        for (_item: String in values) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 2 + _inputSize
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 3 + _inputSize
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "mediaType")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfFileSize: Int = getColumnIndexOrThrow(_stmt, "fileSize")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfUploadStatus: Int = getColumnIndexOrThrow(_stmt, "uploadStatus")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsCached: Int = getColumnIndexOrThrow(_stmt, "isCached")
        val _columnIndexOfLastAccessedAt: Int = getColumnIndexOrThrow(_stmt, "lastAccessedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MediaItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: MediaItemEntity
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpMediaType: String
          _tmpMediaType = _stmt.getText(_columnIndexOfMediaType)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpFileSize: Long
          _tmpFileSize = _stmt.getLong(_columnIndexOfFileSize)
          val _tmpWidth: Int?
          if (_stmt.isNull(_columnIndexOfWidth)) {
            _tmpWidth = null
          } else {
            _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          }
          val _tmpHeight: Int?
          if (_stmt.isNull(_columnIndexOfHeight)) {
            _tmpHeight = null
          } else {
            _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          }
          val _tmpDuration: Int?
          if (_stmt.isNull(_columnIndexOfDuration)) {
            _tmpDuration = null
          } else {
            _tmpDuration = _stmt.getLong(_columnIndexOfDuration).toInt()
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpUploadStatus: String
          _tmpUploadStatus = _stmt.getText(_columnIndexOfUploadStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsCached: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCached).toInt()
          _tmpIsCached = _tmp != 0
          val _tmpLastAccessedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmpLastAccessedAt = null
          } else {
            _tmpLastAccessedAt = _stmt.getLong(_columnIndexOfLastAccessedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item_1 =
              MediaItemEntity(_tmpMediaId,_tmpAssetId,_tmpUrl,_tmpLocalPath,_tmpMediaType,_tmpDateAdded,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpDuration,_tmpThumbnailUrl,_tmpUploadStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsCached,_tmpLastAccessedAt,_tmpDirty)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMediaCount(assetId: String?): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM media_items 
        |        WHERE (? IS NULL OR assetId = ?)
        |        AND uploadStatus = 'COMPLETED'
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (assetId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, assetId)
        }
        _argIndex = 2
        if (assetId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, assetId)
        }
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

  public override suspend fun getMediaById(mediaId: String): MediaItemEntity? {
    val _sql: String = "SELECT * FROM media_items WHERE mediaId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, mediaId)
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "mediaType")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfFileSize: Int = getColumnIndexOrThrow(_stmt, "fileSize")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfUploadStatus: Int = getColumnIndexOrThrow(_stmt, "uploadStatus")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsCached: Int = getColumnIndexOrThrow(_stmt, "isCached")
        val _columnIndexOfLastAccessedAt: Int = getColumnIndexOrThrow(_stmt, "lastAccessedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MediaItemEntity?
        if (_stmt.step()) {
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpMediaType: String
          _tmpMediaType = _stmt.getText(_columnIndexOfMediaType)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpFileSize: Long
          _tmpFileSize = _stmt.getLong(_columnIndexOfFileSize)
          val _tmpWidth: Int?
          if (_stmt.isNull(_columnIndexOfWidth)) {
            _tmpWidth = null
          } else {
            _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          }
          val _tmpHeight: Int?
          if (_stmt.isNull(_columnIndexOfHeight)) {
            _tmpHeight = null
          } else {
            _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          }
          val _tmpDuration: Int?
          if (_stmt.isNull(_columnIndexOfDuration)) {
            _tmpDuration = null
          } else {
            _tmpDuration = _stmt.getLong(_columnIndexOfDuration).toInt()
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpUploadStatus: String
          _tmpUploadStatus = _stmt.getText(_columnIndexOfUploadStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsCached: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCached).toInt()
          _tmpIsCached = _tmp != 0
          val _tmpLastAccessedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmpLastAccessedAt = null
          } else {
            _tmpLastAccessedAt = _stmt.getLong(_columnIndexOfLastAccessedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _result =
              MediaItemEntity(_tmpMediaId,_tmpAssetId,_tmpUrl,_tmpLocalPath,_tmpMediaType,_tmpDateAdded,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpDuration,_tmpThumbnailUrl,_tmpUploadStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsCached,_tmpLastAccessedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeMedia(limit: Int, offset: Int): Flow<List<MediaItemEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM media_items 
        |        WHERE uploadStatus = 'COMPLETED'
        |        ORDER BY dateAdded DESC 
        |        LIMIT ? OFFSET ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("media_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, offset.toLong())
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "mediaType")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfFileSize: Int = getColumnIndexOrThrow(_stmt, "fileSize")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfUploadStatus: Int = getColumnIndexOrThrow(_stmt, "uploadStatus")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsCached: Int = getColumnIndexOrThrow(_stmt, "isCached")
        val _columnIndexOfLastAccessedAt: Int = getColumnIndexOrThrow(_stmt, "lastAccessedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<MediaItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MediaItemEntity
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpLocalPath: String?
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          }
          val _tmpMediaType: String
          _tmpMediaType = _stmt.getText(_columnIndexOfMediaType)
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpFileSize: Long
          _tmpFileSize = _stmt.getLong(_columnIndexOfFileSize)
          val _tmpWidth: Int?
          if (_stmt.isNull(_columnIndexOfWidth)) {
            _tmpWidth = null
          } else {
            _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          }
          val _tmpHeight: Int?
          if (_stmt.isNull(_columnIndexOfHeight)) {
            _tmpHeight = null
          } else {
            _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          }
          val _tmpDuration: Int?
          if (_stmt.isNull(_columnIndexOfDuration)) {
            _tmpDuration = null
          } else {
            _tmpDuration = _stmt.getLong(_columnIndexOfDuration).toInt()
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpUploadStatus: String
          _tmpUploadStatus = _stmt.getText(_columnIndexOfUploadStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsCached: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCached).toInt()
          _tmpIsCached = _tmp != 0
          val _tmpLastAccessedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastAccessedAt)) {
            _tmpLastAccessedAt = null
          } else {
            _tmpLastAccessedAt = _stmt.getLong(_columnIndexOfLastAccessedAt)
          }
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item =
              MediaItemEntity(_tmpMediaId,_tmpAssetId,_tmpUrl,_tmpLocalPath,_tmpMediaType,_tmpDateAdded,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpDuration,_tmpThumbnailUrl,_tmpUploadStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsCached,_tmpLastAccessedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMediaStatus(
    mediaId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE media_items SET uploadStatus = ?, updatedAt = ?, dirty = 1 WHERE mediaId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, mediaId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMediaUrlAndStatus(
    mediaId: String,
    url: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE media_items SET url = ?, uploadStatus = ?, updatedAt = ?, dirty = 1 WHERE mediaId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, url)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, mediaId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun batchDeleteMedia(mediaIds: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM media_items WHERE mediaId IN (")
    val _inputSize: Int = mediaIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in mediaIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateCacheStatus(mediaId: String, isCached: Boolean) {
    val _sql: String = "UPDATE media_items SET isCached = ? WHERE mediaId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isCached) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, mediaId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateLastAccessed(mediaId: String, timestamp: Long) {
    val _sql: String = "UPDATE media_items SET lastAccessedAt = ? WHERE mediaId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 2
        _stmt.bindText(_argIndex, mediaId)
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
