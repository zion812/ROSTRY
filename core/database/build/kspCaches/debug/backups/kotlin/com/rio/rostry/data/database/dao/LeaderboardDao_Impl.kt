package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.LeaderboardEntity
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
public class LeaderboardDao_Impl(
  __db: RoomDatabase,
) : LeaderboardDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLeaderboardEntity: EntityInsertAdapter<LeaderboardEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLeaderboardEntity = object : EntityInsertAdapter<LeaderboardEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `leaderboard` (`id`,`periodKey`,`userId`,`score`,`rank`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LeaderboardEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.periodKey)
        statement.bindText(3, entity.userId)
        statement.bindLong(4, entity.score)
        statement.bindLong(5, entity.rank.toLong())
      }
    }
  }

  public override suspend fun upsertAll(list: List<LeaderboardEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfLeaderboardEntity.insert(_connection, list)
  }

  public override fun leaderboard(periodKey: String): Flow<List<LeaderboardEntity>> {
    val _sql: String = "SELECT * FROM leaderboard WHERE periodKey = ? ORDER BY rank ASC"
    return createFlow(__db, false, arrayOf("leaderboard")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, periodKey)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPeriodKey: Int = getColumnIndexOrThrow(_stmt, "periodKey")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfRank: Int = getColumnIndexOrThrow(_stmt, "rank")
        val _result: MutableList<LeaderboardEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LeaderboardEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpPeriodKey: String
          _tmpPeriodKey = _stmt.getText(_columnIndexOfPeriodKey)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpScore: Long
          _tmpScore = _stmt.getLong(_columnIndexOfScore)
          val _tmpRank: Int
          _tmpRank = _stmt.getLong(_columnIndexOfRank).toInt()
          _item = LeaderboardEntity(_tmpId,_tmpPeriodKey,_tmpUserId,_tmpScore,_tmpRank)
          _result.add(_item)
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
