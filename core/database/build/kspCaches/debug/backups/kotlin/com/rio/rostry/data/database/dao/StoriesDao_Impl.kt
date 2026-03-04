package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.StoryEntity
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
public class StoriesDao_Impl(
  __db: RoomDatabase,
) : StoriesDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStoryEntity: EntityInsertAdapter<StoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfStoryEntity = object : EntityInsertAdapter<StoryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `stories` (`storyId`,`authorId`,`mediaUrl`,`createdAt`,`expiresAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StoryEntity) {
        statement.bindText(1, entity.storyId)
        statement.bindText(2, entity.authorId)
        statement.bindText(3, entity.mediaUrl)
        statement.bindLong(4, entity.createdAt)
        statement.bindLong(5, entity.expiresAt)
      }
    }
  }

  public override suspend fun upsert(story: StoryEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfStoryEntity.insert(_connection, story)
  }

  public override fun streamActive(now: Long): Flow<List<StoryEntity>> {
    val _sql: String = "SELECT * FROM stories WHERE expiresAt > ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("stories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfStoryId: Int = getColumnIndexOrThrow(_stmt, "storyId")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _result: MutableList<StoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: StoryEntity
          val _tmpStoryId: String
          _tmpStoryId = _stmt.getText(_columnIndexOfStoryId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpMediaUrl: String
          _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExpiresAt: Long
          _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          _item = StoryEntity(_tmpStoryId,_tmpAuthorId,_tmpMediaUrl,_tmpCreatedAt,_tmpExpiresAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteExpired(now: Long) {
    val _sql: String = "DELETE FROM stories WHERE expiresAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
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
