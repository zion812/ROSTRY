package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MediaTagEntity
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
public class MediaTagDao_Impl(
  __db: RoomDatabase,
) : MediaTagDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMediaTagEntity: EntityInsertAdapter<MediaTagEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMediaTagEntity = object : EntityInsertAdapter<MediaTagEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `media_tags` (`mediaId`,`tagId`,`tagType`,`value`,`createdAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MediaTagEntity) {
        statement.bindText(1, entity.mediaId)
        statement.bindText(2, entity.tagId)
        statement.bindText(3, entity.tagType)
        statement.bindText(4, entity.value)
        statement.bindLong(5, entity.createdAt)
      }
    }
  }

  public override suspend fun insertTag(tag: MediaTagEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfMediaTagEntity.insert(_connection, tag)
  }

  public override suspend fun insertTags(tags: List<MediaTagEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMediaTagEntity.insert(_connection, tags)
  }

  public override suspend fun getTagsForMedia(mediaId: String): List<MediaTagEntity> {
    val _sql: String = "SELECT * FROM media_tags WHERE mediaId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, mediaId)
        val _columnIndexOfMediaId: Int = getColumnIndexOrThrow(_stmt, "mediaId")
        val _columnIndexOfTagId: Int = getColumnIndexOrThrow(_stmt, "tagId")
        val _columnIndexOfTagType: Int = getColumnIndexOrThrow(_stmt, "tagType")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<MediaTagEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MediaTagEntity
          val _tmpMediaId: String
          _tmpMediaId = _stmt.getText(_columnIndexOfMediaId)
          val _tmpTagId: String
          _tmpTagId = _stmt.getText(_columnIndexOfTagId)
          val _tmpTagType: String
          _tmpTagType = _stmt.getText(_columnIndexOfTagType)
          val _tmpValue: String
          _tmpValue = _stmt.getText(_columnIndexOfValue)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = MediaTagEntity(_tmpMediaId,_tmpTagId,_tmpTagType,_tmpValue,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDistinctTagValues(tagType: String): List<String> {
    val _sql: String = """
        |
        |        SELECT DISTINCT value FROM media_tags 
        |        WHERE tagType = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, tagType)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteTagsForMedia(mediaId: String) {
    val _sql: String = "DELETE FROM media_tags WHERE mediaId = ?"
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
