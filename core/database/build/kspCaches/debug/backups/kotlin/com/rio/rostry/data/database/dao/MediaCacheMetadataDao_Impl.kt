package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MediaCacheMetadataEntity
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
public class MediaCacheMetadataDao_Impl(
  __db: RoomDatabase,
) : MediaCacheMetadataDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMediaCacheMetadataEntity:
      EntityInsertAdapter<MediaCacheMetadataEntity>

  private val __deleteAdapterOfMediaCacheMetadataEntity:
      EntityDeleteOrUpdateAdapter<MediaCacheMetadataEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMediaCacheMetadataEntity = object :
        EntityInsertAdapter<MediaCacheMetadataEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `media_cache_metadata` (`mediaId`,`localPath`,`fileSize`,`downloadedAt`,`lastAccessedAt`,`accessCount`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MediaCacheMetadataEntity) {
        statement.bindText(1, entity.mediaId)
        statement.bindText(2, entity.localPath)
        statement.bindLong(3, entity.fileSize)
        statement.bindLong(4, entity.downloadedAt)
        statement.bindLong(5, entity.lastAccessedAt)
        statement.bindLong(6, entity.accessCount.toLong())
      }
    }
    this.__deleteAdapterOfMediaCacheMetadataEntity = object :
        EntityDeleteOrUpdateAdapter<MediaCacheMetadataEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `media_cache_metadata` WHERE `mediaId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MediaCacheMetadataEntity) {
        statement.bindText(1, entity.mediaId)
      }
    }
  }

  public override suspend fun insertCacheMetadata(metadata: MediaCacheMetadataEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMediaCacheMetadataEntity.insert(_connection, metadata)
  }

  public override suspend fun deleteCacheMetadata(metadata: MediaCacheMetadataEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfMediaCacheMetadataEntity.handle(_connection, metadata)
  }

  public override suspend fun getCacheMetadata(mediaId: String): MediaCacheMetadataEntity? {
    val _sql: String = "SELECT * FROM media_cache_metadata WHERE mediaId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, mediaId)
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfFileSize: Int = getColumnIndexOrThrow(_stmt, "fileSize")
        val _columnIndexOfDownloadedAt: Int = getColumnIndexOrThrow(_stmt, "downloadedAt")
        val _columnIndexOfLastAccessedAt: Int = getColumnIndexOrThrow(_stmt, "lastAccessedAt")
        val _columnIndexOfAccessCount: Int = getColumnIndexOrThrow(_stmt, "accessCount")
        val _result: MediaCacheMetadataEntity?
        if (_stmt.step()) {
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpFileSize: Long
          _tmpFileSize = _stmt.getLong(_columnIndexOfFileSize)
          val _tmpDownloadedAt: Long
          _tmpDownloadedAt = _stmt.getLong(_columnIndexOfDownloadedAt)
          val _tmpLastAccessedAt: Long
          _tmpLastAccessedAt = _stmt.getLong(_columnIndexOfLastAccessedAt)
          val _tmpAccessCount: Int
          _tmpAccessCount = _stmt.getLong(_columnIndexOfAccessCount).toInt()
          _result =
              MediaCacheMetadataEntity(_tmpMediaId,_tmpLocalPath,_tmpFileSize,_tmpDownloadedAt,_tmpLastAccessedAt,_tmpAccessCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCacheCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM media_cache_metadata"
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

  public override suspend fun getTotalCacheSize(): Long? {
    val _sql: String = "SELECT SUM(fileSize) FROM media_cache_metadata"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Long?
        if (_stmt.step()) {
          val _tmp: Long?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLeastRecentlyUsed(count: Int): List<MediaCacheMetadataEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM media_cache_metadata 
        |        ORDER BY lastAccessedAt ASC 
        |        LIMIT ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, count.toLong())
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfFileSize: Int = getColumnIndexOrThrow(_stmt, "fileSize")
        val _columnIndexOfDownloadedAt: Int = getColumnIndexOrThrow(_stmt, "downloadedAt")
        val _columnIndexOfLastAccessedAt: Int = getColumnIndexOrThrow(_stmt, "lastAccessedAt")
        val _columnIndexOfAccessCount: Int = getColumnIndexOrThrow(_stmt, "accessCount")
        val _result: MutableList<MediaCacheMetadataEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MediaCacheMetadataEntity
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpFileSize: Long
          _tmpFileSize = _stmt.getLong(_columnIndexOfFileSize)
          val _tmpDownloadedAt: Long
          _tmpDownloadedAt = _stmt.getLong(_columnIndexOfDownloadedAt)
          val _tmpLastAccessedAt: Long
          _tmpLastAccessedAt = _stmt.getLong(_columnIndexOfLastAccessedAt)
          val _tmpAccessCount: Int
          _tmpAccessCount = _stmt.getLong(_columnIndexOfAccessCount).toInt()
          _item =
              MediaCacheMetadataEntity(_tmpMediaId,_tmpLocalPath,_tmpFileSize,_tmpDownloadedAt,_tmpLastAccessedAt,_tmpAccessCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateAccess(mediaId: String, timestamp: Long) {
    val _sql: String =
        "UPDATE media_cache_metadata SET lastAccessedAt = ?, accessCount = accessCount + 1 WHERE mediaId = ?"
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
