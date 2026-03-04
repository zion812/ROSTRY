package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.InvoiceEntity
import com.rio.rostry.`data`.database.entity.InvoiceLineEntity
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
public class InvoiceDao_Impl(
  __db: RoomDatabase,
) : InvoiceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfInvoiceEntity: EntityInsertAdapter<InvoiceEntity>

  private val __insertAdapterOfInvoiceLineEntity: EntityInsertAdapter<InvoiceLineEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfInvoiceEntity = object : EntityInsertAdapter<InvoiceEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `invoices` (`invoiceId`,`orderId`,`subtotal`,`gstPercent`,`gstAmount`,`total`,`createdAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InvoiceEntity) {
        statement.bindText(1, entity.invoiceId)
        statement.bindText(2, entity.orderId)
        statement.bindDouble(3, entity.subtotal)
        statement.bindDouble(4, entity.gstPercent)
        statement.bindDouble(5, entity.gstAmount)
        statement.bindDouble(6, entity.total)
        statement.bindLong(7, entity.createdAt)
      }
    }
    this.__insertAdapterOfInvoiceLineEntity = object : EntityInsertAdapter<InvoiceLineEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `invoice_lines` (`lineId`,`invoiceId`,`description`,`qty`,`unitPrice`,`lineTotal`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InvoiceLineEntity) {
        statement.bindText(1, entity.lineId)
        statement.bindText(2, entity.invoiceId)
        statement.bindText(3, entity.description)
        statement.bindDouble(4, entity.qty)
        statement.bindDouble(5, entity.unitPrice)
        statement.bindDouble(6, entity.lineTotal)
      }
    }
  }

  public override suspend fun insertInvoice(entity: InvoiceEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfInvoiceEntity.insert(_connection, entity)
  }

  public override suspend fun insertLines(lines: List<InvoiceLineEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfInvoiceLineEntity.insert(_connection, lines)
  }

  public override suspend fun insertWithLines(entity: InvoiceEntity,
      lines: List<InvoiceLineEntity>): Unit = performInTransactionSuspending(__db) {
    super@InvoiceDao_Impl.insertWithLines(entity, lines)
  }

  public override suspend fun findByOrder(orderId: String): InvoiceEntity? {
    val _sql: String = "SELECT * FROM invoices WHERE orderId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfSubtotal: Int = getColumnIndexOrThrow(_stmt, "subtotal")
        val _columnIndexOfGstPercent: Int = getColumnIndexOrThrow(_stmt, "gstPercent")
        val _columnIndexOfGstAmount: Int = getColumnIndexOrThrow(_stmt, "gstAmount")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: InvoiceEntity?
        if (_stmt.step()) {
          val _tmpInvoiceId: String
          _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpSubtotal: Double
          _tmpSubtotal = _stmt.getDouble(_columnIndexOfSubtotal)
          val _tmpGstPercent: Double
          _tmpGstPercent = _stmt.getDouble(_columnIndexOfGstPercent)
          val _tmpGstAmount: Double
          _tmpGstAmount = _stmt.getDouble(_columnIndexOfGstAmount)
          val _tmpTotal: Double
          _tmpTotal = _stmt.getDouble(_columnIndexOfTotal)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              InvoiceEntity(_tmpInvoiceId,_tmpOrderId,_tmpSubtotal,_tmpGstPercent,_tmpGstAmount,_tmpTotal,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun linesForInvoice(invoiceId: String): List<InvoiceLineEntity> {
    val _sql: String = "SELECT * FROM invoice_lines WHERE invoiceId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, invoiceId)
        val _columnIndexOfLineId: Int = getColumnIndexOrThrow(_stmt, "lineId")
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfQty: Int = getColumnIndexOrThrow(_stmt, "qty")
        val _columnIndexOfUnitPrice: Int = getColumnIndexOrThrow(_stmt, "unitPrice")
        val _columnIndexOfLineTotal: Int = getColumnIndexOrThrow(_stmt, "lineTotal")
        val _result: MutableList<InvoiceLineEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InvoiceLineEntity
          val _tmpLineId: String
          _tmpLineId = _stmt.getText(_columnIndexOfLineId)
          val _tmpInvoiceId: String
          _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpQty: Double
          _tmpQty = _stmt.getDouble(_columnIndexOfQty)
          val _tmpUnitPrice: Double
          _tmpUnitPrice = _stmt.getDouble(_columnIndexOfUnitPrice)
          val _tmpLineTotal: Double
          _tmpLineTotal = _stmt.getDouble(_columnIndexOfLineTotal)
          _item =
              InvoiceLineEntity(_tmpLineId,_tmpInvoiceId,_tmpDescription,_tmpQty,_tmpUnitPrice,_tmpLineTotal)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllInvoices(): List<InvoiceEntity> {
    val _sql: String = "SELECT * FROM invoices ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInvoiceId: Int = getColumnIndexOrThrow(_stmt, "invoiceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfSubtotal: Int = getColumnIndexOrThrow(_stmt, "subtotal")
        val _columnIndexOfGstPercent: Int = getColumnIndexOrThrow(_stmt, "gstPercent")
        val _columnIndexOfGstAmount: Int = getColumnIndexOrThrow(_stmt, "gstAmount")
        val _columnIndexOfTotal: Int = getColumnIndexOrThrow(_stmt, "total")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<InvoiceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InvoiceEntity
          val _tmpInvoiceId: String
          _tmpInvoiceId = _stmt.getText(_columnIndexOfInvoiceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpSubtotal: Double
          _tmpSubtotal = _stmt.getDouble(_columnIndexOfSubtotal)
          val _tmpGstPercent: Double
          _tmpGstPercent = _stmt.getDouble(_columnIndexOfGstPercent)
          val _tmpGstAmount: Double
          _tmpGstAmount = _stmt.getDouble(_columnIndexOfGstAmount)
          val _tmpTotal: Double
          _tmpTotal = _stmt.getDouble(_columnIndexOfTotal)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              InvoiceEntity(_tmpInvoiceId,_tmpOrderId,_tmpSubtotal,_tmpGstPercent,_tmpGstAmount,_tmpTotal,_tmpCreatedAt)
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
