package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CommentEntity
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
public class CommentsDao_Impl(
  __db: RoomDatabase,
) : CommentsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCommentEntity: EntityInsertAdapter<CommentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCommentEntity = object : EntityInsertAdapter<CommentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `comments` (`commentId`,`postId`,`authorId`,`text`,`createdAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CommentEntity) {
        statement.bindText(1, entity.commentId)
        statement.bindText(2, entity.postId)
        statement.bindText(3, entity.authorId)
        statement.bindText(4, entity.text)
        statement.bindLong(5, entity.createdAt)
      }
    }
  }

  public override suspend fun upsert(comment: CommentEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCommentEntity.insert(_connection, comment)
  }

  public override fun streamByPost(postId: String): Flow<List<CommentEntity>> {
    val _sql: String = "SELECT * FROM comments WHERE postId = ? ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("comments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
        val _columnIndexOfCommentId: Int = getColumnIndexOrThrow(_stmt, "commentId")
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<CommentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CommentEntity
          val _tmpCommentId: String
          _tmpCommentId = _stmt.getText(_columnIndexOfCommentId)
          val _tmpPostId: String
          _tmpPostId = _stmt.getText(_columnIndexOfPostId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = CommentEntity(_tmpCommentId,_tmpPostId,_tmpAuthorId,_tmpText,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countByUser(userId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM comments WHERE authorId = ?"
    return performSuspending(__db, true, false) { _connection ->
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
