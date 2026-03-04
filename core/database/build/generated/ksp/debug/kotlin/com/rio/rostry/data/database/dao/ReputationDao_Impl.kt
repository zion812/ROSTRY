package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ReputationEntity
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
public class ReputationDao_Impl(
  __db: RoomDatabase,
) : ReputationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfReputationEntity: EntityInsertAdapter<ReputationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfReputationEntity = object : EntityInsertAdapter<ReputationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `reputation` (`repId`,`userId`,`score`,`updatedAt`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReputationEntity) {
        statement.bindText(1, entity.repId)
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.score.toLong())
        statement.bindLong(4, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(rep: ReputationEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfReputationEntity.insert(_connection, rep)
  }

  public override fun top(limit: Int): Flow<List<ReputationEntity>> {
    val _sql: String = "SELECT * FROM reputation ORDER BY score DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("reputation")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfRepId: Int = getColumnIndexOrThrow(_stmt, "repId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ReputationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReputationEntity
          val _tmpRepId: String
          _tmpRepId = _stmt.getText(_columnIndexOfRepId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = ReputationEntity(_tmpRepId,_tmpUserId,_tmpScore,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByUserId(userId: String): ReputationEntity? {
    val _sql: String = "SELECT * FROM reputation WHERE userId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRepId: Int = getColumnIndexOrThrow(_stmt, "repId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: ReputationEntity?
        if (_stmt.step()) {
          val _tmpRepId: String
          _tmpRepId = _stmt.getText(_columnIndexOfRepId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = ReputationEntity(_tmpRepId,_tmpUserId,_tmpScore,_tmpUpdatedAt)
        } else {
          _result = null
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
