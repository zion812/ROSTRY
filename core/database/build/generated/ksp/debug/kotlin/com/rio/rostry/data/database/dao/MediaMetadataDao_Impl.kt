package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MediaMetadataEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MediaMetadataDao_Impl(
  __db: RoomDatabase,
) : MediaMetadataDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMediaMetadataEntity: EntityInsertAdapter<MediaMetadataEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMediaMetadataEntity = object : EntityInsertAdapter<MediaMetadataEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `media_metadata` (`mediaId`,`originalUrl`,`thumbnailUrl`,`width`,`height`,`sizeBytes`,`format`,`duration`,`compressionQuality`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MediaMetadataEntity) {
        statement.bindText(1, entity.mediaId)
        statement.bindText(2, entity.originalUrl)
        statement.bindText(3, entity.thumbnailUrl)
        statement.bindLong(4, entity.width.toLong())
        statement.bindLong(5, entity.height.toLong())
        statement.bindLong(6, entity.sizeBytes)
        statement.bindText(7, entity.format)
        val _tmpDuration: Int? = entity.duration
        if (_tmpDuration == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpDuration.toLong())
        }
        val _tmpCompressionQuality: Int? = entity.compressionQuality
        if (_tmpCompressionQuality == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCompressionQuality.toLong())
        }
        statement.bindLong(10, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(metadata: MediaMetadataEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMediaMetadataEntity.insert(_connection, metadata)
  }

  public override suspend fun getById(mediaId: String): MediaMetadataEntity? {
    val _sql: String = "SELECT * FROM media_metadata WHERE mediaId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, mediaId)
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfOriginalUrl: Int = getColumnIndexOrThrow(_stmt, "originalUrl")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfSizeBytes: Int = getColumnIndexOrThrow(_stmt, "sizeBytes")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfCompressionQuality: Int = getColumnIndexOrThrow(_stmt,
            "compressionQuality")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MediaMetadataEntity?
        if (_stmt.step()) {
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpOriginalUrl: String
          _tmpOriginalUrl = _stmt.getText(_columnIndexOfOriginalUrl)
          val _tmpThumbnailUrl: String
          _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          val _tmpWidth: Int
          _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          val _tmpHeight: Int
          _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          val _tmpSizeBytes: Long
          _tmpSizeBytes = _stmt.getLong(_columnIndexOfSizeBytes)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpDuration: Int?
          if (_stmt.isNull(_columnIndexOfDuration)) {
            _tmpDuration = null
          } else {
            _tmpDuration = _stmt.getLong(_columnIndexOfDuration).toInt()
          }
          val _tmpCompressionQuality: Int?
          if (_stmt.isNull(_columnIndexOfCompressionQuality)) {
            _tmpCompressionQuality = null
          } else {
            _tmpCompressionQuality = _stmt.getLong(_columnIndexOfCompressionQuality).toInt()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              MediaMetadataEntity(_tmpMediaId,_tmpOriginalUrl,_tmpThumbnailUrl,_tmpWidth,_tmpHeight,_tmpSizeBytes,_tmpFormat,_tmpDuration,_tmpCompressionQuality,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(mediaId: String): Flow<MediaMetadataEntity?> {
    val _sql: String = "SELECT * FROM media_metadata WHERE mediaId = ?"
    return createFlow(__db, false, arrayOf("media_metadata")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, mediaId)
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfOriginalUrl: Int = getColumnIndexOrThrow(_stmt, "originalUrl")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfSizeBytes: Int = getColumnIndexOrThrow(_stmt, "sizeBytes")
        val _columnIndexOfFormat: Int = getColumnIndexOrThrow(_stmt, "format")
        val _columnIndexOfDuration: Int = getColumnIndexOrThrow(_stmt, "duration")
        val _columnIndexOfCompressionQuality: Int = getColumnIndexOrThrow(_stmt,
            "compressionQuality")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MediaMetadataEntity?
        if (_stmt.step()) {
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpOriginalUrl: String
          _tmpOriginalUrl = _stmt.getText(_columnIndexOfOriginalUrl)
          val _tmpThumbnailUrl: String
          _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          val _tmpWidth: Int
          _tmpWidth = _stmt.getLong(_columnIndexOfWidth).toInt()
          val _tmpHeight: Int
          _tmpHeight = _stmt.getLong(_columnIndexOfHeight).toInt()
          val _tmpSizeBytes: Long
          _tmpSizeBytes = _stmt.getLong(_columnIndexOfSizeBytes)
          val _tmpFormat: String
          _tmpFormat = _stmt.getText(_columnIndexOfFormat)
          val _tmpDuration: Int?
          if (_stmt.isNull(_columnIndexOfDuration)) {
            _tmpDuration = null
          } else {
            _tmpDuration = _stmt.getLong(_columnIndexOfDuration).toInt()
          }
          val _tmpCompressionQuality: Int?
          if (_stmt.isNull(_columnIndexOfCompressionQuality)) {
            _tmpCompressionQuality = null
          } else {
            _tmpCompressionQuality = _stmt.getLong(_columnIndexOfCompressionQuality).toInt()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              MediaMetadataEntity(_tmpMediaId,_tmpOriginalUrl,_tmpThumbnailUrl,_tmpWidth,_tmpHeight,_tmpSizeBytes,_tmpFormat,_tmpDuration,_tmpCompressionQuality,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM media_metadata"
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

  public override suspend fun delete(mediaId: String) {
    val _sql: String = "DELETE FROM media_metadata WHERE mediaId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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
