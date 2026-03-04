package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.PaymentEntity
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
public class PaymentDao_Impl(
  __db: RoomDatabase,
) : PaymentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPaymentEntity: EntityInsertAdapter<PaymentEntity>

  private val __updateAdapterOfPaymentEntity: EntityDeleteOrUpdateAdapter<PaymentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPaymentEntity = object : EntityInsertAdapter<PaymentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `payments` (`paymentId`,`orderId`,`userId`,`method`,`amount`,`currency`,`status`,`providerRef`,`upiUri`,`idempotencyKey`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PaymentEntity) {
        statement.bindText(1, entity.paymentId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.userId)
        statement.bindText(4, entity.method)
        statement.bindDouble(5, entity.amount)
        statement.bindText(6, entity.currency)
        statement.bindText(7, entity.status)
        val _tmpProviderRef: String? = entity.providerRef
        if (_tmpProviderRef == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpProviderRef)
        }
        val _tmpUpiUri: String? = entity.upiUri
        if (_tmpUpiUri == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpUpiUri)
        }
        statement.bindText(10, entity.idempotencyKey)
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
      }
    }
    this.__updateAdapterOfPaymentEntity = object : EntityDeleteOrUpdateAdapter<PaymentEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `payments` SET `paymentId` = ?,`orderId` = ?,`userId` = ?,`method` = ?,`amount` = ?,`currency` = ?,`status` = ?,`providerRef` = ?,`upiUri` = ?,`idempotencyKey` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `paymentId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PaymentEntity) {
        statement.bindText(1, entity.paymentId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.userId)
        statement.bindText(4, entity.method)
        statement.bindDouble(5, entity.amount)
        statement.bindText(6, entity.currency)
        statement.bindText(7, entity.status)
        val _tmpProviderRef: String? = entity.providerRef
        if (_tmpProviderRef == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpProviderRef)
        }
        val _tmpUpiUri: String? = entity.upiUri
        if (_tmpUpiUri == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpUpiUri)
        }
        statement.bindText(10, entity.idempotencyKey)
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
        statement.bindText(13, entity.paymentId)
      }
    }
  }

  public override suspend fun insert(payment: PaymentEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfPaymentEntity.insert(_connection, payment)
  }

  public override suspend fun update(payment: PaymentEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfPaymentEntity.handle(_connection, payment)
  }

  public override suspend fun findById(paymentId: String): PaymentEntity? {
    val _sql: String = "SELECT * FROM payments WHERE paymentId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, paymentId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProviderRef: Int = getColumnIndexOrThrow(_stmt, "providerRef")
        val _columnIndexOfUpiUri: Int = getColumnIndexOrThrow(_stmt, "upiUri")
        val _columnIndexOfIdempotencyKey: Int = getColumnIndexOrThrow(_stmt, "idempotencyKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: PaymentEntity?
        if (_stmt.step()) {
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProviderRef: String?
          if (_stmt.isNull(_columnIndexOfProviderRef)) {
            _tmpProviderRef = null
          } else {
            _tmpProviderRef = _stmt.getText(_columnIndexOfProviderRef)
          }
          val _tmpUpiUri: String?
          if (_stmt.isNull(_columnIndexOfUpiUri)) {
            _tmpUpiUri = null
          } else {
            _tmpUpiUri = _stmt.getText(_columnIndexOfUpiUri)
          }
          val _tmpIdempotencyKey: String
          _tmpIdempotencyKey = _stmt.getText(_columnIndexOfIdempotencyKey)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              PaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpUserId,_tmpMethod,_tmpAmount,_tmpCurrency,_tmpStatus,_tmpProviderRef,_tmpUpiUri,_tmpIdempotencyKey,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByIdempotencyKey(key: String): PaymentEntity? {
    val _sql: String = "SELECT * FROM payments WHERE idempotencyKey = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, key)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProviderRef: Int = getColumnIndexOrThrow(_stmt, "providerRef")
        val _columnIndexOfUpiUri: Int = getColumnIndexOrThrow(_stmt, "upiUri")
        val _columnIndexOfIdempotencyKey: Int = getColumnIndexOrThrow(_stmt, "idempotencyKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: PaymentEntity?
        if (_stmt.step()) {
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProviderRef: String?
          if (_stmt.isNull(_columnIndexOfProviderRef)) {
            _tmpProviderRef = null
          } else {
            _tmpProviderRef = _stmt.getText(_columnIndexOfProviderRef)
          }
          val _tmpUpiUri: String?
          if (_stmt.isNull(_columnIndexOfUpiUri)) {
            _tmpUpiUri = null
          } else {
            _tmpUpiUri = _stmt.getText(_columnIndexOfUpiUri)
          }
          val _tmpIdempotencyKey: String
          _tmpIdempotencyKey = _stmt.getText(_columnIndexOfIdempotencyKey)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              PaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpUserId,_tmpMethod,_tmpAmount,_tmpCurrency,_tmpStatus,_tmpProviderRef,_tmpUpiUri,_tmpIdempotencyKey,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByOrder(orderId: String): Flow<List<PaymentEntity>> {
    val _sql: String = "SELECT * FROM payments WHERE orderId = ? ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProviderRef: Int = getColumnIndexOrThrow(_stmt, "providerRef")
        val _columnIndexOfUpiUri: Int = getColumnIndexOrThrow(_stmt, "upiUri")
        val _columnIndexOfIdempotencyKey: Int = getColumnIndexOrThrow(_stmt, "idempotencyKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<PaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProviderRef: String?
          if (_stmt.isNull(_columnIndexOfProviderRef)) {
            _tmpProviderRef = null
          } else {
            _tmpProviderRef = _stmt.getText(_columnIndexOfProviderRef)
          }
          val _tmpUpiUri: String?
          if (_stmt.isNull(_columnIndexOfUpiUri)) {
            _tmpUpiUri = null
          } else {
            _tmpUpiUri = _stmt.getText(_columnIndexOfUpiUri)
          }
          val _tmpIdempotencyKey: String
          _tmpIdempotencyKey = _stmt.getText(_columnIndexOfIdempotencyKey)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              PaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpUserId,_tmpMethod,_tmpAmount,_tmpCurrency,_tmpStatus,_tmpProviderRef,_tmpUpiUri,_tmpIdempotencyKey,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByUser(userId: String): Flow<List<PaymentEntity>> {
    val _sql: String = "SELECT * FROM payments WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProviderRef: Int = getColumnIndexOrThrow(_stmt, "providerRef")
        val _columnIndexOfUpiUri: Int = getColumnIndexOrThrow(_stmt, "upiUri")
        val _columnIndexOfIdempotencyKey: Int = getColumnIndexOrThrow(_stmt, "idempotencyKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<PaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProviderRef: String?
          if (_stmt.isNull(_columnIndexOfProviderRef)) {
            _tmpProviderRef = null
          } else {
            _tmpProviderRef = _stmt.getText(_columnIndexOfProviderRef)
          }
          val _tmpUpiUri: String?
          if (_stmt.isNull(_columnIndexOfUpiUri)) {
            _tmpUpiUri = null
          } else {
            _tmpUpiUri = _stmt.getText(_columnIndexOfUpiUri)
          }
          val _tmpIdempotencyKey: String
          _tmpIdempotencyKey = _stmt.getText(_columnIndexOfIdempotencyKey)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              PaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpUserId,_tmpMethod,_tmpAmount,_tmpCurrency,_tmpStatus,_tmpProviderRef,_tmpUpiUri,_tmpIdempotencyKey,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findLatestByOrder(orderId: String): PaymentEntity? {
    val _sql: String = "SELECT * FROM payments WHERE orderId = ? ORDER BY createdAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProviderRef: Int = getColumnIndexOrThrow(_stmt, "providerRef")
        val _columnIndexOfUpiUri: Int = getColumnIndexOrThrow(_stmt, "upiUri")
        val _columnIndexOfIdempotencyKey: Int = getColumnIndexOrThrow(_stmt, "idempotencyKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: PaymentEntity?
        if (_stmt.step()) {
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProviderRef: String?
          if (_stmt.isNull(_columnIndexOfProviderRef)) {
            _tmpProviderRef = null
          } else {
            _tmpProviderRef = _stmt.getText(_columnIndexOfProviderRef)
          }
          val _tmpUpiUri: String?
          if (_stmt.isNull(_columnIndexOfUpiUri)) {
            _tmpUpiUri = null
          } else {
            _tmpUpiUri = _stmt.getText(_columnIndexOfUpiUri)
          }
          val _tmpIdempotencyKey: String
          _tmpIdempotencyKey = _stmt.getText(_columnIndexOfIdempotencyKey)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              PaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpUserId,_tmpMethod,_tmpAmount,_tmpCurrency,_tmpStatus,_tmpProviderRef,_tmpUpiUri,_tmpIdempotencyKey,_tmpCreatedAt,_tmpUpdatedAt)
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
