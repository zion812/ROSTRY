package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AnalyticsDailyEntity
import javax.`annotation`.processing.Generated
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
public class AnalyticsDao_Impl(
  __db: RoomDatabase,
) : AnalyticsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAnalyticsDailyEntity: EntityInsertAdapter<AnalyticsDailyEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAnalyticsDailyEntity = object :
        EntityInsertAdapter<AnalyticsDailyEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `analytics_daily` (`id`,`userId`,`role`,`dateKey`,`salesRevenue`,`ordersCount`,`productViews`,`likesCount`,`commentsCount`,`transfersCount`,`breedingSuccessRate`,`engagementScore`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AnalyticsDailyEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.role)
        statement.bindText(4, entity.dateKey)
        statement.bindDouble(5, entity.salesRevenue)
        statement.bindLong(6, entity.ordersCount.toLong())
        statement.bindLong(7, entity.productViews.toLong())
        statement.bindLong(8, entity.likesCount.toLong())
        statement.bindLong(9, entity.commentsCount.toLong())
        statement.bindLong(10, entity.transfersCount.toLong())
        statement.bindDouble(11, entity.breedingSuccessRate)
        statement.bindDouble(12, entity.engagementScore)
        statement.bindLong(13, entity.createdAt)
        val _tmpUpdatedAt: Long? = entity.updatedAt
        if (_tmpUpdatedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpUpdatedAt)
        }
      }
    }
  }

  public override suspend fun upsert(entity: AnalyticsDailyEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfAnalyticsDailyEntity.insert(_connection, entity)
  }

  public override suspend fun upsertDaily(entity: AnalyticsDailyEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAnalyticsDailyEntity.insert(_connection, entity)
  }

  public override fun streamRange(
    userId: String,
    from: String,
    to: String,
  ): Flow<List<AnalyticsDailyEntity>> {
    val _sql: String =
        "SELECT * FROM analytics_daily WHERE userId = ? AND dateKey BETWEEN ? AND ? ORDER BY dateKey ASC"
    return createFlow(__db, false, arrayOf("analytics_daily")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, from)
        _argIndex = 3
        _stmt.bindText(_argIndex, to)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfDateKey: Int = getColumnIndexOrThrow(_stmt, "dateKey")
        val _columnIndexOfSalesRevenue: Int = getColumnIndexOrThrow(_stmt, "salesRevenue")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfProductViews: Int = getColumnIndexOrThrow(_stmt, "productViews")
        val _columnIndexOfLikesCount: Int = getColumnIndexOrThrow(_stmt, "likesCount")
        val _columnIndexOfCommentsCount: Int = getColumnIndexOrThrow(_stmt, "commentsCount")
        val _columnIndexOfTransfersCount: Int = getColumnIndexOrThrow(_stmt, "transfersCount")
        val _columnIndexOfBreedingSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breedingSuccessRate")
        val _columnIndexOfEngagementScore: Int = getColumnIndexOrThrow(_stmt, "engagementScore")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<AnalyticsDailyEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AnalyticsDailyEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpDateKey: String
          _tmpDateKey = _stmt.getText(_columnIndexOfDateKey)
          val _tmpSalesRevenue: Double
          _tmpSalesRevenue = _stmt.getDouble(_columnIndexOfSalesRevenue)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpProductViews: Int
          _tmpProductViews = _stmt.getLong(_columnIndexOfProductViews).toInt()
          val _tmpLikesCount: Int
          _tmpLikesCount = _stmt.getLong(_columnIndexOfLikesCount).toInt()
          val _tmpCommentsCount: Int
          _tmpCommentsCount = _stmt.getLong(_columnIndexOfCommentsCount).toInt()
          val _tmpTransfersCount: Int
          _tmpTransfersCount = _stmt.getLong(_columnIndexOfTransfersCount).toInt()
          val _tmpBreedingSuccessRate: Double
          _tmpBreedingSuccessRate = _stmt.getDouble(_columnIndexOfBreedingSuccessRate)
          val _tmpEngagementScore: Double
          _tmpEngagementScore = _stmt.getDouble(_columnIndexOfEngagementScore)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _item =
              AnalyticsDailyEntity(_tmpId,_tmpUserId,_tmpRole,_tmpDateKey,_tmpSalesRevenue,_tmpOrdersCount,_tmpProductViews,_tmpLikesCount,_tmpCommentsCount,_tmpTransfersCount,_tmpBreedingSuccessRate,_tmpEngagementScore,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamAllFarmersRange(from: String, to: String):
      Flow<List<AnalyticsDailyEntity>> {
    val _sql: String =
        "SELECT * FROM analytics_daily WHERE dateKey BETWEEN ? AND ? ORDER BY dateKey ASC"
    return createFlow(__db, false, arrayOf("analytics_daily")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, from)
        _argIndex = 2
        _stmt.bindText(_argIndex, to)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfDateKey: Int = getColumnIndexOrThrow(_stmt, "dateKey")
        val _columnIndexOfSalesRevenue: Int = getColumnIndexOrThrow(_stmt, "salesRevenue")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfProductViews: Int = getColumnIndexOrThrow(_stmt, "productViews")
        val _columnIndexOfLikesCount: Int = getColumnIndexOrThrow(_stmt, "likesCount")
        val _columnIndexOfCommentsCount: Int = getColumnIndexOrThrow(_stmt, "commentsCount")
        val _columnIndexOfTransfersCount: Int = getColumnIndexOrThrow(_stmt, "transfersCount")
        val _columnIndexOfBreedingSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breedingSuccessRate")
        val _columnIndexOfEngagementScore: Int = getColumnIndexOrThrow(_stmt, "engagementScore")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<AnalyticsDailyEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AnalyticsDailyEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpDateKey: String
          _tmpDateKey = _stmt.getText(_columnIndexOfDateKey)
          val _tmpSalesRevenue: Double
          _tmpSalesRevenue = _stmt.getDouble(_columnIndexOfSalesRevenue)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpProductViews: Int
          _tmpProductViews = _stmt.getLong(_columnIndexOfProductViews).toInt()
          val _tmpLikesCount: Int
          _tmpLikesCount = _stmt.getLong(_columnIndexOfLikesCount).toInt()
          val _tmpCommentsCount: Int
          _tmpCommentsCount = _stmt.getLong(_columnIndexOfCommentsCount).toInt()
          val _tmpTransfersCount: Int
          _tmpTransfersCount = _stmt.getLong(_columnIndexOfTransfersCount).toInt()
          val _tmpBreedingSuccessRate: Double
          _tmpBreedingSuccessRate = _stmt.getDouble(_columnIndexOfBreedingSuccessRate)
          val _tmpEngagementScore: Double
          _tmpEngagementScore = _stmt.getDouble(_columnIndexOfEngagementScore)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _item =
              AnalyticsDailyEntity(_tmpId,_tmpUserId,_tmpRole,_tmpDateKey,_tmpSalesRevenue,_tmpOrdersCount,_tmpProductViews,_tmpLikesCount,_tmpCommentsCount,_tmpTransfersCount,_tmpBreedingSuccessRate,_tmpEngagementScore,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun recent(userId: String, limit: Int): Flow<List<AnalyticsDailyEntity>> {
    val _sql: String =
        "SELECT * FROM analytics_daily WHERE userId = ? ORDER BY dateKey DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("analytics_daily")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfDateKey: Int = getColumnIndexOrThrow(_stmt, "dateKey")
        val _columnIndexOfSalesRevenue: Int = getColumnIndexOrThrow(_stmt, "salesRevenue")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfProductViews: Int = getColumnIndexOrThrow(_stmt, "productViews")
        val _columnIndexOfLikesCount: Int = getColumnIndexOrThrow(_stmt, "likesCount")
        val _columnIndexOfCommentsCount: Int = getColumnIndexOrThrow(_stmt, "commentsCount")
        val _columnIndexOfTransfersCount: Int = getColumnIndexOrThrow(_stmt, "transfersCount")
        val _columnIndexOfBreedingSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breedingSuccessRate")
        val _columnIndexOfEngagementScore: Int = getColumnIndexOrThrow(_stmt, "engagementScore")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<AnalyticsDailyEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AnalyticsDailyEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpDateKey: String
          _tmpDateKey = _stmt.getText(_columnIndexOfDateKey)
          val _tmpSalesRevenue: Double
          _tmpSalesRevenue = _stmt.getDouble(_columnIndexOfSalesRevenue)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpProductViews: Int
          _tmpProductViews = _stmt.getLong(_columnIndexOfProductViews).toInt()
          val _tmpLikesCount: Int
          _tmpLikesCount = _stmt.getLong(_columnIndexOfLikesCount).toInt()
          val _tmpCommentsCount: Int
          _tmpCommentsCount = _stmt.getLong(_columnIndexOfCommentsCount).toInt()
          val _tmpTransfersCount: Int
          _tmpTransfersCount = _stmt.getLong(_columnIndexOfTransfersCount).toInt()
          val _tmpBreedingSuccessRate: Double
          _tmpBreedingSuccessRate = _stmt.getDouble(_columnIndexOfBreedingSuccessRate)
          val _tmpEngagementScore: Double
          _tmpEngagementScore = _stmt.getDouble(_columnIndexOfEngagementScore)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _item =
              AnalyticsDailyEntity(_tmpId,_tmpUserId,_tmpRole,_tmpDateKey,_tmpSalesRevenue,_tmpOrdersCount,_tmpProductViews,_tmpLikesCount,_tmpCommentsCount,_tmpTransfersCount,_tmpBreedingSuccessRate,_tmpEngagementScore,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun listRange(
    userId: String,
    from: String,
    to: String,
  ): List<AnalyticsDailyEntity> {
    val _sql: String =
        "SELECT * FROM analytics_daily WHERE userId = ? AND dateKey BETWEEN ? AND ? ORDER BY dateKey ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, from)
        _argIndex = 3
        _stmt.bindText(_argIndex, to)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfDateKey: Int = getColumnIndexOrThrow(_stmt, "dateKey")
        val _columnIndexOfSalesRevenue: Int = getColumnIndexOrThrow(_stmt, "salesRevenue")
        val _columnIndexOfOrdersCount: Int = getColumnIndexOrThrow(_stmt, "ordersCount")
        val _columnIndexOfProductViews: Int = getColumnIndexOrThrow(_stmt, "productViews")
        val _columnIndexOfLikesCount: Int = getColumnIndexOrThrow(_stmt, "likesCount")
        val _columnIndexOfCommentsCount: Int = getColumnIndexOrThrow(_stmt, "commentsCount")
        val _columnIndexOfTransfersCount: Int = getColumnIndexOrThrow(_stmt, "transfersCount")
        val _columnIndexOfBreedingSuccessRate: Int = getColumnIndexOrThrow(_stmt,
            "breedingSuccessRate")
        val _columnIndexOfEngagementScore: Int = getColumnIndexOrThrow(_stmt, "engagementScore")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<AnalyticsDailyEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AnalyticsDailyEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpDateKey: String
          _tmpDateKey = _stmt.getText(_columnIndexOfDateKey)
          val _tmpSalesRevenue: Double
          _tmpSalesRevenue = _stmt.getDouble(_columnIndexOfSalesRevenue)
          val _tmpOrdersCount: Int
          _tmpOrdersCount = _stmt.getLong(_columnIndexOfOrdersCount).toInt()
          val _tmpProductViews: Int
          _tmpProductViews = _stmt.getLong(_columnIndexOfProductViews).toInt()
          val _tmpLikesCount: Int
          _tmpLikesCount = _stmt.getLong(_columnIndexOfLikesCount).toInt()
          val _tmpCommentsCount: Int
          _tmpCommentsCount = _stmt.getLong(_columnIndexOfCommentsCount).toInt()
          val _tmpTransfersCount: Int
          _tmpTransfersCount = _stmt.getLong(_columnIndexOfTransfersCount).toInt()
          val _tmpBreedingSuccessRate: Double
          _tmpBreedingSuccessRate = _stmt.getDouble(_columnIndexOfBreedingSuccessRate)
          val _tmpEngagementScore: Double
          _tmpEngagementScore = _stmt.getDouble(_columnIndexOfEngagementScore)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _item =
              AnalyticsDailyEntity(_tmpId,_tmpUserId,_tmpRole,_tmpDateKey,_tmpSalesRevenue,_tmpOrdersCount,_tmpProductViews,_tmpLikesCount,_tmpCommentsCount,_tmpTransfersCount,_tmpBreedingSuccessRate,_tmpEngagementScore,_tmpCreatedAt,_tmpUpdatedAt)
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
