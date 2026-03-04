package com.rio.rostry.`data`.database.dao

import androidx.paging.PagingSource
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery
import androidx.room.coroutines.createFlow
import androidx.room.paging.LimitOffsetPagingSource
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.PostEntity
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
public class PostsDao_Impl(
  __db: RoomDatabase,
) : PostsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPostEntity: EntityInsertAdapter<PostEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPostEntity = object : EntityInsertAdapter<PostEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `posts` (`postId`,`authorId`,`type`,`text`,`mediaUrl`,`thumbnailUrl`,`productId`,`hashtags`,`mentions`,`parentPostId`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PostEntity) {
        statement.bindText(1, entity.postId)
        statement.bindText(2, entity.authorId)
        statement.bindText(3, entity.type)
        val _tmpText: String? = entity.text
        if (_tmpText == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpText)
        }
        val _tmpMediaUrl: String? = entity.mediaUrl
        if (_tmpMediaUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpMediaUrl)
        }
        val _tmpThumbnailUrl: String? = entity.thumbnailUrl
        if (_tmpThumbnailUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpThumbnailUrl)
        }
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpProductId)
        }
        val _tmpHashtags: List<String>? = entity.hashtags
        val _tmp: String? = AppDatabase.Converters.fromStringList(_tmpHashtags)
        if (_tmp == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmp)
        }
        val _tmpMentions: List<String>? = entity.mentions
        val _tmp_1: String? = AppDatabase.Converters.fromStringList(_tmpMentions)
        if (_tmp_1 == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmp_1)
        }
        val _tmpParentPostId: String? = entity.parentPostId
        if (_tmpParentPostId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpParentPostId)
        }
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(post: PostEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __insertAdapterOfPostEntity.insert(_connection, post)
  }

  public override fun paging(): PagingSource<Int, PostEntity> {
    val _sql: String = "SELECT * FROM posts ORDER BY createdAt DESC"
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql)
    return object : LimitOffsetPagingSource<PostEntity>(_rawQuery, __db, "posts") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int):
          List<PostEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
          val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
          val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
          val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
          val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
          val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
          val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
          val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
          val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
          val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
          val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _result: MutableList<PostEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: PostEntity
            val _tmpPostId: String
            _tmpPostId = _stmt.getText(_columnIndexOfPostId)
            val _tmpAuthorId: String
            _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
            val _tmpType: String
            _tmpType = _stmt.getText(_columnIndexOfType)
            val _tmpText: String?
            if (_stmt.isNull(_columnIndexOfText)) {
              _tmpText = null
            } else {
              _tmpText = _stmt.getText(_columnIndexOfText)
            }
            val _tmpMediaUrl: String?
            if (_stmt.isNull(_columnIndexOfMediaUrl)) {
              _tmpMediaUrl = null
            } else {
              _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
            }
            val _tmpThumbnailUrl: String?
            if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null
            } else {
              _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
            }
            val _tmpProductId: String?
            if (_stmt.isNull(_columnIndexOfProductId)) {
              _tmpProductId = null
            } else {
              _tmpProductId = _stmt.getText(_columnIndexOfProductId)
            }
            val _tmpHashtags: List<String>?
            val _tmp: String?
            if (_stmt.isNull(_columnIndexOfHashtags)) {
              _tmp = null
            } else {
              _tmp = _stmt.getText(_columnIndexOfHashtags)
            }
            _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
            val _tmpMentions: List<String>?
            val _tmp_1: String?
            if (_stmt.isNull(_columnIndexOfMentions)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _stmt.getText(_columnIndexOfMentions)
            }
            _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
            val _tmpParentPostId: String?
            if (_stmt.isNull(_columnIndexOfParentPostId)) {
              _tmpParentPostId = null
            } else {
              _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            _item =
                PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public override fun pagingByAuthor(authorId: String): PagingSource<Int, PostEntity> {
    val _sql: String = "SELECT * FROM posts WHERE authorId = ? ORDER BY createdAt DESC"
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql) { _stmt ->
      var _argIndex: Int = 1
      _stmt.bindText(_argIndex, authorId)
    }
    return object : LimitOffsetPagingSource<PostEntity>(_rawQuery, __db, "posts") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int):
          List<PostEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
          val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
          val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
          val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
          val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
          val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
          val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
          val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
          val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
          val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
          val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _result: MutableList<PostEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: PostEntity
            val _tmpPostId: String
            _tmpPostId = _stmt.getText(_columnIndexOfPostId)
            val _tmpAuthorId: String
            _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
            val _tmpType: String
            _tmpType = _stmt.getText(_columnIndexOfType)
            val _tmpText: String?
            if (_stmt.isNull(_columnIndexOfText)) {
              _tmpText = null
            } else {
              _tmpText = _stmt.getText(_columnIndexOfText)
            }
            val _tmpMediaUrl: String?
            if (_stmt.isNull(_columnIndexOfMediaUrl)) {
              _tmpMediaUrl = null
            } else {
              _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
            }
            val _tmpThumbnailUrl: String?
            if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null
            } else {
              _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
            }
            val _tmpProductId: String?
            if (_stmt.isNull(_columnIndexOfProductId)) {
              _tmpProductId = null
            } else {
              _tmpProductId = _stmt.getText(_columnIndexOfProductId)
            }
            val _tmpHashtags: List<String>?
            val _tmp: String?
            if (_stmt.isNull(_columnIndexOfHashtags)) {
              _tmp = null
            } else {
              _tmp = _stmt.getText(_columnIndexOfHashtags)
            }
            _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
            val _tmpMentions: List<String>?
            val _tmp_1: String?
            if (_stmt.isNull(_columnIndexOfMentions)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _stmt.getText(_columnIndexOfMentions)
            }
            _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
            val _tmpParentPostId: String?
            if (_stmt.isNull(_columnIndexOfParentPostId)) {
              _tmpParentPostId = null
            } else {
              _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            _item =
                PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public override suspend fun getById(postId: String): PostEntity? {
    val _sql: String = "SELECT * FROM posts WHERE postId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
        val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
        val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: PostEntity?
        if (_stmt.step()) {
          val _tmpPostId: String
          _tmpPostId = _stmt.getText(_columnIndexOfPostId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpText: String?
          if (_stmt.isNull(_columnIndexOfText)) {
            _tmpText = null
          } else {
            _tmpText = _stmt.getText(_columnIndexOfText)
          }
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpHashtags: List<String>?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfHashtags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfHashtags)
          }
          _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
          val _tmpMentions: List<String>?
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfMentions)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfMentions)
          }
          _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
          val _tmpParentPostId: String?
          if (_stmt.isNull(_columnIndexOfParentPostId)) {
            _tmpParentPostId = null
          } else {
            _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun pagingRanked(): PagingSource<Int, PostEntity> {
    val _sql: String =
        "SELECT * FROM posts ORDER BY (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.postId) DESC, createdAt DESC"
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql)
    return object : LimitOffsetPagingSource<PostEntity>(_rawQuery, __db, "posts", "likes") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int):
          List<PostEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
          val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
          val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
          val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
          val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
          val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
          val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
          val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
          val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
          val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
          val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _result: MutableList<PostEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: PostEntity
            val _tmpPostId: String
            _tmpPostId = _stmt.getText(_columnIndexOfPostId)
            val _tmpAuthorId: String
            _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
            val _tmpType: String
            _tmpType = _stmt.getText(_columnIndexOfType)
            val _tmpText: String?
            if (_stmt.isNull(_columnIndexOfText)) {
              _tmpText = null
            } else {
              _tmpText = _stmt.getText(_columnIndexOfText)
            }
            val _tmpMediaUrl: String?
            if (_stmt.isNull(_columnIndexOfMediaUrl)) {
              _tmpMediaUrl = null
            } else {
              _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
            }
            val _tmpThumbnailUrl: String?
            if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null
            } else {
              _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
            }
            val _tmpProductId: String?
            if (_stmt.isNull(_columnIndexOfProductId)) {
              _tmpProductId = null
            } else {
              _tmpProductId = _stmt.getText(_columnIndexOfProductId)
            }
            val _tmpHashtags: List<String>?
            val _tmp: String?
            if (_stmt.isNull(_columnIndexOfHashtags)) {
              _tmp = null
            } else {
              _tmp = _stmt.getText(_columnIndexOfHashtags)
            }
            _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
            val _tmpMentions: List<String>?
            val _tmp_1: String?
            if (_stmt.isNull(_columnIndexOfMentions)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _stmt.getText(_columnIndexOfMentions)
            }
            _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
            val _tmpParentPostId: String?
            if (_stmt.isNull(_columnIndexOfParentPostId)) {
              _tmpParentPostId = null
            } else {
              _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            _item =
                PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public override suspend fun getTrending(limit: Int): List<PostEntity> {
    val _sql: String =
        "SELECT * FROM posts ORDER BY (SELECT COUNT(*) FROM likes WHERE likes.postId = posts.postId) DESC, createdAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
        val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
        val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<PostEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PostEntity
          val _tmpPostId: String
          _tmpPostId = _stmt.getText(_columnIndexOfPostId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpText: String?
          if (_stmt.isNull(_columnIndexOfText)) {
            _tmpText = null
          } else {
            _tmpText = _stmt.getText(_columnIndexOfText)
          }
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpHashtags: List<String>?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfHashtags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfHashtags)
          }
          _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
          val _tmpMentions: List<String>?
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfMentions)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfMentions)
          }
          _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
          val _tmpParentPostId: String?
          if (_stmt.isNull(_columnIndexOfParentPostId)) {
            _tmpParentPostId = null
          } else {
            _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReplies(parentId: String): Flow<List<PostEntity>> {
    val _sql: String = "SELECT * FROM posts WHERE parentPostId = ? ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("posts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, parentId)
        val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
        val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
        val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
        val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<PostEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PostEntity
          val _tmpPostId: String
          _tmpPostId = _stmt.getText(_columnIndexOfPostId)
          val _tmpAuthorId: String
          _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpText: String?
          if (_stmt.isNull(_columnIndexOfText)) {
            _tmpText = null
          } else {
            _tmpText = _stmt.getText(_columnIndexOfText)
          }
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpThumbnailUrl: String?
          if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
            _tmpThumbnailUrl = null
          } else {
            _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
          }
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpHashtags: List<String>?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfHashtags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfHashtags)
          }
          _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
          val _tmpMentions: List<String>?
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfMentions)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfMentions)
          }
          _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
          val _tmpParentPostId: String?
          if (_stmt.isNull(_columnIndexOfParentPostId)) {
            _tmpParentPostId = null
          } else {
            _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun countByAuthor(authorId: String): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM posts WHERE authorId = ?"
    return createFlow(__db, false, arrayOf("posts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, authorId)
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

  public override fun pagingByFollowedUsers(userId: String): PagingSource<Int, PostEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM posts 
        |        WHERE authorId IN (SELECT followedId FROM follows WHERE followerId = ?) 
        |        ORDER BY createdAt DESC
        |    
        """.trimMargin()
    val _rawQuery: RoomRawQuery = RoomRawQuery(_sql) { _stmt ->
      var _argIndex: Int = 1
      _stmt.bindText(_argIndex, userId)
    }
    return object : LimitOffsetPagingSource<PostEntity>(_rawQuery, __db, "posts", "follows") {
      protected override suspend fun convertRows(limitOffsetQuery: RoomRawQuery, itemCount: Int):
          List<PostEntity> = performSuspending(__db, true, false) { _connection ->
        val _stmt: SQLiteStatement = _connection.prepare(limitOffsetQuery.sql)
        limitOffsetQuery.getBindingFunction().invoke(_stmt)
        try {
          val _columnIndexOfPostId: Int = getColumnIndexOrThrow(_stmt, "postId")
          val _columnIndexOfAuthorId: Int = getColumnIndexOrThrow(_stmt, "authorId")
          val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
          val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
          val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
          val _columnIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_stmt, "thumbnailUrl")
          val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
          val _columnIndexOfHashtags: Int = getColumnIndexOrThrow(_stmt, "hashtags")
          val _columnIndexOfMentions: Int = getColumnIndexOrThrow(_stmt, "mentions")
          val _columnIndexOfParentPostId: Int = getColumnIndexOrThrow(_stmt, "parentPostId")
          val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
          val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
          val _result: MutableList<PostEntity> = mutableListOf()
          while (_stmt.step()) {
            val _item: PostEntity
            val _tmpPostId: String
            _tmpPostId = _stmt.getText(_columnIndexOfPostId)
            val _tmpAuthorId: String
            _tmpAuthorId = _stmt.getText(_columnIndexOfAuthorId)
            val _tmpType: String
            _tmpType = _stmt.getText(_columnIndexOfType)
            val _tmpText: String?
            if (_stmt.isNull(_columnIndexOfText)) {
              _tmpText = null
            } else {
              _tmpText = _stmt.getText(_columnIndexOfText)
            }
            val _tmpMediaUrl: String?
            if (_stmt.isNull(_columnIndexOfMediaUrl)) {
              _tmpMediaUrl = null
            } else {
              _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
            }
            val _tmpThumbnailUrl: String?
            if (_stmt.isNull(_columnIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null
            } else {
              _tmpThumbnailUrl = _stmt.getText(_columnIndexOfThumbnailUrl)
            }
            val _tmpProductId: String?
            if (_stmt.isNull(_columnIndexOfProductId)) {
              _tmpProductId = null
            } else {
              _tmpProductId = _stmt.getText(_columnIndexOfProductId)
            }
            val _tmpHashtags: List<String>?
            val _tmp: String?
            if (_stmt.isNull(_columnIndexOfHashtags)) {
              _tmp = null
            } else {
              _tmp = _stmt.getText(_columnIndexOfHashtags)
            }
            _tmpHashtags = AppDatabase.Converters.toStringList(_tmp)
            val _tmpMentions: List<String>?
            val _tmp_1: String?
            if (_stmt.isNull(_columnIndexOfMentions)) {
              _tmp_1 = null
            } else {
              _tmp_1 = _stmt.getText(_columnIndexOfMentions)
            }
            _tmpMentions = AppDatabase.Converters.toStringList(_tmp_1)
            val _tmpParentPostId: String?
            if (_stmt.isNull(_columnIndexOfParentPostId)) {
              _tmpParentPostId = null
            } else {
              _tmpParentPostId = _stmt.getText(_columnIndexOfParentPostId)
            }
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
            _item =
                PostEntity(_tmpPostId,_tmpAuthorId,_tmpType,_tmpText,_tmpMediaUrl,_tmpThumbnailUrl,_tmpProductId,_tmpHashtags,_tmpMentions,_tmpParentPostId,_tmpCreatedAt,_tmpUpdatedAt)
            _result.add(_item)
          }
          _result
        } finally {
          _stmt.close()
        }
      }
    }
  }

  public override suspend fun delete(postId: String) {
    val _sql: String = "DELETE FROM posts WHERE postId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, postId)
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
