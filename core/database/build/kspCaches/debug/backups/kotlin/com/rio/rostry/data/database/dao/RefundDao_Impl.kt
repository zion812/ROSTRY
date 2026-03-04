package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.RefundEntity
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
public class RefundDao_Impl(
  __db: RoomDatabase,
) : RefundDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRefundEntity: EntityInsertAdapter<RefundEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRefundEntity = object : EntityInsertAdapter<RefundEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `refunds` (`refundId`,`paymentId`,`orderId`,`amount`,`reason`,`createdAt`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RefundEntity) {
        statement.bindText(1, entity.refundId)
        statement.bindText(2, entity.paymentId)
        statement.bindText(3, entity.orderId)
        statement.bindDouble(4, entity.amount)
        val _tmpReason: String? = entity.reason
        if (_tmpReason == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpReason)
        }
        statement.bindLong(6, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(refund: RefundEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfRefundEntity.insert(_connection, refund)
  }

  public override suspend fun listByPayment(paymentId: String): List<RefundEntity> {
    val _sql: String = "SELECT * FROM refunds WHERE paymentId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, paymentId)
        val _columnIndexOfRefundId: Int = getColumnIndexOrThrow(_stmt, "refundId")
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<RefundEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RefundEntity
          val _tmpRefundId: String
          _tmpRefundId = _stmt.getText(_columnIndexOfRefundId)
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpReason: String?
          if (_stmt.isNull(_columnIndexOfReason)) {
            _tmpReason = null
          } else {
            _tmpReason = _stmt.getText(_columnIndexOfReason)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              RefundEntity(_tmpRefundId,_tmpPaymentId,_tmpOrderId,_tmpAmount,_tmpReason,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun totalRefundedForPayment(paymentId: String): Double {
    val _sql: String = "SELECT COALESCE(SUM(amount), 0) FROM refunds WHERE paymentId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, paymentId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
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
