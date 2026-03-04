package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ProfitabilityMetricsEntity
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
public class ProfitabilityMetricsDao_Impl(
  __db: RoomDatabase,
) : ProfitabilityMetricsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfProfitabilityMetricsEntity:
      EntityInsertAdapter<ProfitabilityMetricsEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfProfitabilityMetricsEntity = object :
        EntityInsertAdapter<ProfitabilityMetricsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `profitability_metrics` (`id`,`entity_id`,`entity_type`,`period_start`,`period_end`,`revenue`,`costs`,`profit`,`profit_margin`,`order_count`,`calculated_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProfitabilityMetricsEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.entityId)
        statement.bindText(3, entity.entityType)
        statement.bindLong(4, entity.periodStart)
        statement.bindLong(5, entity.periodEnd)
        statement.bindDouble(6, entity.revenue)
        statement.bindDouble(7, entity.costs)
        statement.bindDouble(8, entity.profit)
        statement.bindDouble(9, entity.profitMargin)
        statement.bindLong(10, entity.orderCount.toLong())
        statement.bindLong(11, entity.calculatedAt)
      }
    }
  }

  public override suspend fun insert(metrics: ProfitabilityMetricsEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfProfitabilityMetricsEntity.insert(_connection, metrics)
  }

  public override suspend fun insertAll(metrics: List<ProfitabilityMetricsEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfProfitabilityMetricsEntity.insert(_connection, metrics)
  }

  public override suspend fun getMetrics(
    entityId: String,
    entityType: String,
    periodStart: Long,
    periodEnd: Long,
  ): List<ProfitabilityMetricsEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM profitability_metrics 
        |        WHERE entity_id = ? 
        |        AND entity_type = ? 
        |        AND period_start >= ? 
        |        AND period_end <= ?
        |        ORDER BY period_start DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, entityId)
        _argIndex = 2
        _stmt.bindText(_argIndex, entityType)
        _argIndex = 3
        _stmt.bindLong(_argIndex, periodStart)
        _argIndex = 4
        _stmt.bindLong(_argIndex, periodEnd)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entity_id")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entity_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfRevenue: Int = getColumnIndexOrThrow(_stmt, "revenue")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfProfit: Int = getColumnIndexOrThrow(_stmt, "profit")
        val _columnIndexOfProfitMargin: Int = getColumnIndexOrThrow(_stmt, "profit_margin")
        val _columnIndexOfOrderCount: Int = getColumnIndexOrThrow(_stmt, "order_count")
        val _columnIndexOfCalculatedAt: Int = getColumnIndexOrThrow(_stmt, "calculated_at")
        val _result: MutableList<ProfitabilityMetricsEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProfitabilityMetricsEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpPeriodStart: Long
          _tmpPeriodStart = _stmt.getLong(_columnIndexOfPeriodStart)
          val _tmpPeriodEnd: Long
          _tmpPeriodEnd = _stmt.getLong(_columnIndexOfPeriodEnd)
          val _tmpRevenue: Double
          _tmpRevenue = _stmt.getDouble(_columnIndexOfRevenue)
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpProfit: Double
          _tmpProfit = _stmt.getDouble(_columnIndexOfProfit)
          val _tmpProfitMargin: Double
          _tmpProfitMargin = _stmt.getDouble(_columnIndexOfProfitMargin)
          val _tmpOrderCount: Int
          _tmpOrderCount = _stmt.getLong(_columnIndexOfOrderCount).toInt()
          val _tmpCalculatedAt: Long
          _tmpCalculatedAt = _stmt.getLong(_columnIndexOfCalculatedAt)
          _item =
              ProfitabilityMetricsEntity(_tmpId,_tmpEntityId,_tmpEntityType,_tmpPeriodStart,_tmpPeriodEnd,_tmpRevenue,_tmpCosts,_tmpProfit,_tmpProfitMargin,_tmpOrderCount,_tmpCalculatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestMetrics(entityId: String, entityType: String):
      ProfitabilityMetricsEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM profitability_metrics 
        |        WHERE entity_id = ? 
        |        AND entity_type = ? 
        |        ORDER BY period_start DESC
        |        LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, entityId)
        _argIndex = 2
        _stmt.bindText(_argIndex, entityType)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entity_id")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entity_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfRevenue: Int = getColumnIndexOrThrow(_stmt, "revenue")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfProfit: Int = getColumnIndexOrThrow(_stmt, "profit")
        val _columnIndexOfProfitMargin: Int = getColumnIndexOrThrow(_stmt, "profit_margin")
        val _columnIndexOfOrderCount: Int = getColumnIndexOrThrow(_stmt, "order_count")
        val _columnIndexOfCalculatedAt: Int = getColumnIndexOrThrow(_stmt, "calculated_at")
        val _result: ProfitabilityMetricsEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpPeriodStart: Long
          _tmpPeriodStart = _stmt.getLong(_columnIndexOfPeriodStart)
          val _tmpPeriodEnd: Long
          _tmpPeriodEnd = _stmt.getLong(_columnIndexOfPeriodEnd)
          val _tmpRevenue: Double
          _tmpRevenue = _stmt.getDouble(_columnIndexOfRevenue)
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpProfit: Double
          _tmpProfit = _stmt.getDouble(_columnIndexOfProfit)
          val _tmpProfitMargin: Double
          _tmpProfitMargin = _stmt.getDouble(_columnIndexOfProfitMargin)
          val _tmpOrderCount: Int
          _tmpOrderCount = _stmt.getLong(_columnIndexOfOrderCount).toInt()
          val _tmpCalculatedAt: Long
          _tmpCalculatedAt = _stmt.getLong(_columnIndexOfCalculatedAt)
          _result =
              ProfitabilityMetricsEntity(_tmpId,_tmpEntityId,_tmpEntityType,_tmpPeriodStart,_tmpPeriodEnd,_tmpRevenue,_tmpCosts,_tmpProfit,_tmpProfitMargin,_tmpOrderCount,_tmpCalculatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTopPerformers(
    entityType: String,
    periodStart: Long,
    periodEnd: Long,
    limit: Int,
  ): List<ProfitabilityMetricsEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM profitability_metrics 
        |        WHERE entity_type = ? 
        |        AND period_start >= ? 
        |        AND period_end <= ?
        |        ORDER BY profit DESC
        |        LIMIT ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, entityType)
        _argIndex = 2
        _stmt.bindLong(_argIndex, periodStart)
        _argIndex = 3
        _stmt.bindLong(_argIndex, periodEnd)
        _argIndex = 4
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entity_id")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entity_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfRevenue: Int = getColumnIndexOrThrow(_stmt, "revenue")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfProfit: Int = getColumnIndexOrThrow(_stmt, "profit")
        val _columnIndexOfProfitMargin: Int = getColumnIndexOrThrow(_stmt, "profit_margin")
        val _columnIndexOfOrderCount: Int = getColumnIndexOrThrow(_stmt, "order_count")
        val _columnIndexOfCalculatedAt: Int = getColumnIndexOrThrow(_stmt, "calculated_at")
        val _result: MutableList<ProfitabilityMetricsEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProfitabilityMetricsEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpPeriodStart: Long
          _tmpPeriodStart = _stmt.getLong(_columnIndexOfPeriodStart)
          val _tmpPeriodEnd: Long
          _tmpPeriodEnd = _stmt.getLong(_columnIndexOfPeriodEnd)
          val _tmpRevenue: Double
          _tmpRevenue = _stmt.getDouble(_columnIndexOfRevenue)
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpProfit: Double
          _tmpProfit = _stmt.getDouble(_columnIndexOfProfit)
          val _tmpProfitMargin: Double
          _tmpProfitMargin = _stmt.getDouble(_columnIndexOfProfitMargin)
          val _tmpOrderCount: Int
          _tmpOrderCount = _stmt.getLong(_columnIndexOfOrderCount).toInt()
          val _tmpCalculatedAt: Long
          _tmpCalculatedAt = _stmt.getLong(_columnIndexOfCalculatedAt)
          _item =
              ProfitabilityMetricsEntity(_tmpId,_tmpEntityId,_tmpEntityType,_tmpPeriodStart,_tmpPeriodEnd,_tmpRevenue,_tmpCosts,_tmpProfit,_tmpProfitMargin,_tmpOrderCount,_tmpCalculatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeMetrics(entityId: String, entityType: String):
      Flow<List<ProfitabilityMetricsEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM profitability_metrics 
        |        WHERE entity_id = ? 
        |        AND entity_type = ?
        |        ORDER BY period_start DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("profitability_metrics")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, entityId)
        _argIndex = 2
        _stmt.bindText(_argIndex, entityType)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfEntityId: Int = getColumnIndexOrThrow(_stmt, "entity_id")
        val _columnIndexOfEntityType: Int = getColumnIndexOrThrow(_stmt, "entity_type")
        val _columnIndexOfPeriodStart: Int = getColumnIndexOrThrow(_stmt, "period_start")
        val _columnIndexOfPeriodEnd: Int = getColumnIndexOrThrow(_stmt, "period_end")
        val _columnIndexOfRevenue: Int = getColumnIndexOrThrow(_stmt, "revenue")
        val _columnIndexOfCosts: Int = getColumnIndexOrThrow(_stmt, "costs")
        val _columnIndexOfProfit: Int = getColumnIndexOrThrow(_stmt, "profit")
        val _columnIndexOfProfitMargin: Int = getColumnIndexOrThrow(_stmt, "profit_margin")
        val _columnIndexOfOrderCount: Int = getColumnIndexOrThrow(_stmt, "order_count")
        val _columnIndexOfCalculatedAt: Int = getColumnIndexOrThrow(_stmt, "calculated_at")
        val _result: MutableList<ProfitabilityMetricsEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProfitabilityMetricsEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpEntityId: String
          _tmpEntityId = _stmt.getText(_columnIndexOfEntityId)
          val _tmpEntityType: String
          _tmpEntityType = _stmt.getText(_columnIndexOfEntityType)
          val _tmpPeriodStart: Long
          _tmpPeriodStart = _stmt.getLong(_columnIndexOfPeriodStart)
          val _tmpPeriodEnd: Long
          _tmpPeriodEnd = _stmt.getLong(_columnIndexOfPeriodEnd)
          val _tmpRevenue: Double
          _tmpRevenue = _stmt.getDouble(_columnIndexOfRevenue)
          val _tmpCosts: Double
          _tmpCosts = _stmt.getDouble(_columnIndexOfCosts)
          val _tmpProfit: Double
          _tmpProfit = _stmt.getDouble(_columnIndexOfProfit)
          val _tmpProfitMargin: Double
          _tmpProfitMargin = _stmt.getDouble(_columnIndexOfProfitMargin)
          val _tmpOrderCount: Int
          _tmpOrderCount = _stmt.getLong(_columnIndexOfOrderCount).toInt()
          val _tmpCalculatedAt: Long
          _tmpCalculatedAt = _stmt.getLong(_columnIndexOfCalculatedAt)
          _item =
              ProfitabilityMetricsEntity(_tmpId,_tmpEntityId,_tmpEntityType,_tmpPeriodStart,_tmpPeriodEnd,_tmpRevenue,_tmpCosts,_tmpProfit,_tmpProfitMargin,_tmpOrderCount,_tmpCalculatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldMetrics(beforeTimestamp: Long) {
    val _sql: String = "DELETE FROM profitability_metrics WHERE calculated_at < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, beforeTimestamp)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM profitability_metrics"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
