package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CommunityRecommendationEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
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
public class CommunityRecommendationDao_Impl(
  __db: RoomDatabase,
) : CommunityRecommendationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCommunityRecommendationEntity:
      EntityInsertAdapter<CommunityRecommendationEntity>

  private val __upsertAdapterOfCommunityRecommendationEntity:
      EntityUpsertAdapter<CommunityRecommendationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCommunityRecommendationEntity = object :
        EntityInsertAdapter<CommunityRecommendationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `community_recommendations` (`recommendationId`,`userId`,`type`,`targetId`,`score`,`reason`,`createdAt`,`expiresAt`,`dismissed`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement,
          entity: CommunityRecommendationEntity) {
        statement.bindText(1, entity.recommendationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.targetId)
        statement.bindDouble(5, entity.score)
        val _tmpReason: String? = entity.reason
        if (_tmpReason == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpReason)
        }
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.expiresAt)
        val _tmp: Int = if (entity.dismissed) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }
    this.__upsertAdapterOfCommunityRecommendationEntity =
        EntityUpsertAdapter<CommunityRecommendationEntity>(object :
        EntityInsertAdapter<CommunityRecommendationEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `community_recommendations` (`recommendationId`,`userId`,`type`,`targetId`,`score`,`reason`,`createdAt`,`expiresAt`,`dismissed`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement,
          entity: CommunityRecommendationEntity) {
        statement.bindText(1, entity.recommendationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.targetId)
        statement.bindDouble(5, entity.score)
        val _tmpReason: String? = entity.reason
        if (_tmpReason == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpReason)
        }
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.expiresAt)
        val _tmp: Int = if (entity.dismissed) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }, object : EntityDeleteOrUpdateAdapter<CommunityRecommendationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `community_recommendations` SET `recommendationId` = ?,`userId` = ?,`type` = ?,`targetId` = ?,`score` = ?,`reason` = ?,`createdAt` = ?,`expiresAt` = ?,`dismissed` = ? WHERE `recommendationId` = ?"

      protected override fun bind(statement: SQLiteStatement,
          entity: CommunityRecommendationEntity) {
        statement.bindText(1, entity.recommendationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.targetId)
        statement.bindDouble(5, entity.score)
        val _tmpReason: String? = entity.reason
        if (_tmpReason == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpReason)
        }
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.expiresAt)
        val _tmp: Int = if (entity.dismissed) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindText(10, entity.recommendationId)
      }
    })
  }

  public override suspend fun upsertAll(recommendations: List<CommunityRecommendationEntity>): Unit
      = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCommunityRecommendationEntity.insert(_connection, recommendations)
  }

  public override suspend fun upsert(recommendation: CommunityRecommendationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfCommunityRecommendationEntity.upsert(_connection, recommendation)
  }

  public override fun getRecommendations(
    userId: String,
    type: String,
    now: Long,
    limit: Int,
  ): Flow<List<CommunityRecommendationEntity>> {
    val _sql: String =
        "SELECT * FROM community_recommendations WHERE userId = ? AND type = ? AND dismissed = 0 AND expiresAt > ? ORDER BY score DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("community_recommendations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfRecommendationId: Int = getColumnIndexOrThrow(_stmt, "recommendationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _columnIndexOfDismissed: Int = getColumnIndexOrThrow(_stmt, "dismissed")
        val _result: MutableList<CommunityRecommendationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CommunityRecommendationEntity
          val _tmpRecommendationId: String
          _tmpRecommendationId = _stmt.getText(_columnIndexOfRecommendationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTargetId: String
          _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          val _tmpScore: Double
          _tmpScore = _stmt.getDouble(_columnIndexOfScore)
          val _tmpReason: String?
          if (_stmt.isNull(_columnIndexOfReason)) {
            _tmpReason = null
          } else {
            _tmpReason = _stmt.getText(_columnIndexOfReason)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpExpiresAt: Long
          _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          val _tmpDismissed: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDismissed).toInt()
          _tmpDismissed = _tmp != 0
          _item =
              CommunityRecommendationEntity(_tmpRecommendationId,_tmpUserId,_tmpType,_tmpTargetId,_tmpScore,_tmpReason,_tmpCreatedAt,_tmpExpiresAt,_tmpDismissed)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun dismiss(id: String) {
    val _sql: String =
        "UPDATE community_recommendations SET dismissed = 1 WHERE recommendationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteExpired(now: Long) {
    val _sql: String = "DELETE FROM community_recommendations WHERE expiresAt < ?"
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
