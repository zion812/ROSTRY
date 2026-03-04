package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CircuitBreakerMetricsEntity
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CircuitBreakerMetricsDao_Impl(
  __db: RoomDatabase,
) : CircuitBreakerMetricsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCircuitBreakerMetricsEntity:
      EntityInsertAdapter<CircuitBreakerMetricsEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCircuitBreakerMetricsEntity = object :
        EntityInsertAdapter<CircuitBreakerMetricsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `circuit_breaker_metrics` (`service_name`,`state`,`failure_rate`,`total_calls`,`failed_calls`,`last_state_change`,`last_updated`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CircuitBreakerMetricsEntity) {
        statement.bindText(1, entity.serviceName)
        statement.bindText(2, entity.state)
        statement.bindDouble(3, entity.failureRate)
        statement.bindLong(4, entity.totalCalls.toLong())
        statement.bindLong(5, entity.failedCalls.toLong())
        statement.bindLong(6, entity.lastStateChange)
        statement.bindLong(7, entity.lastUpdated)
      }
    }
  }

  public override suspend fun upsert(entity: CircuitBreakerMetricsEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCircuitBreakerMetricsEntity.insert(_connection, entity)
  }

  public override suspend fun `get`(serviceName: String): CircuitBreakerMetricsEntity? {
    val _sql: String = "SELECT * FROM circuit_breaker_metrics WHERE service_name = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, serviceName)
        val _columnIndexOfServiceName: Int = getColumnIndexOrThrow(_stmt, "service_name")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfFailureRate: Int = getColumnIndexOrThrow(_stmt, "failure_rate")
        val _columnIndexOfTotalCalls: Int = getColumnIndexOrThrow(_stmt, "total_calls")
        val _columnIndexOfFailedCalls: Int = getColumnIndexOrThrow(_stmt, "failed_calls")
        val _columnIndexOfLastStateChange: Int = getColumnIndexOrThrow(_stmt, "last_state_change")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _result: CircuitBreakerMetricsEntity?
        if (_stmt.step()) {
          val _tmpServiceName: String
          _tmpServiceName = _stmt.getText(_columnIndexOfServiceName)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          val _tmpFailureRate: Double
          _tmpFailureRate = _stmt.getDouble(_columnIndexOfFailureRate)
          val _tmpTotalCalls: Int
          _tmpTotalCalls = _stmt.getLong(_columnIndexOfTotalCalls).toInt()
          val _tmpFailedCalls: Int
          _tmpFailedCalls = _stmt.getLong(_columnIndexOfFailedCalls).toInt()
          val _tmpLastStateChange: Long
          _tmpLastStateChange = _stmt.getLong(_columnIndexOfLastStateChange)
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _result =
              CircuitBreakerMetricsEntity(_tmpServiceName,_tmpState,_tmpFailureRate,_tmpTotalCalls,_tmpFailedCalls,_tmpLastStateChange,_tmpLastUpdated)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<CircuitBreakerMetricsEntity> {
    val _sql: String = "SELECT * FROM circuit_breaker_metrics"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfServiceName: Int = getColumnIndexOrThrow(_stmt, "service_name")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfFailureRate: Int = getColumnIndexOrThrow(_stmt, "failure_rate")
        val _columnIndexOfTotalCalls: Int = getColumnIndexOrThrow(_stmt, "total_calls")
        val _columnIndexOfFailedCalls: Int = getColumnIndexOrThrow(_stmt, "failed_calls")
        val _columnIndexOfLastStateChange: Int = getColumnIndexOrThrow(_stmt, "last_state_change")
        val _columnIndexOfLastUpdated: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _result: MutableList<CircuitBreakerMetricsEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CircuitBreakerMetricsEntity
          val _tmpServiceName: String
          _tmpServiceName = _stmt.getText(_columnIndexOfServiceName)
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          val _tmpFailureRate: Double
          _tmpFailureRate = _stmt.getDouble(_columnIndexOfFailureRate)
          val _tmpTotalCalls: Int
          _tmpTotalCalls = _stmt.getLong(_columnIndexOfTotalCalls).toInt()
          val _tmpFailedCalls: Int
          _tmpFailedCalls = _stmt.getLong(_columnIndexOfFailedCalls).toInt()
          val _tmpLastStateChange: Long
          _tmpLastStateChange = _stmt.getLong(_columnIndexOfLastStateChange)
          val _tmpLastUpdated: Long
          _tmpLastUpdated = _stmt.getLong(_columnIndexOfLastUpdated)
          _item =
              CircuitBreakerMetricsEntity(_tmpServiceName,_tmpState,_tmpFailureRate,_tmpTotalCalls,_tmpFailedCalls,_tmpLastStateChange,_tmpLastUpdated)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(serviceName: String) {
    val _sql: String = "DELETE FROM circuit_breaker_metrics WHERE service_name = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, serviceName)
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
