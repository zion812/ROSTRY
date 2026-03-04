package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderPaymentEntity
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
public class OrderPaymentDao_Impl(
  __db: RoomDatabase,
) : OrderPaymentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderPaymentEntity: EntityInsertAdapter<OrderPaymentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderPaymentEntity = object : EntityInsertAdapter<OrderPaymentEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_payments` (`paymentId`,`orderId`,`quoteId`,`payerId`,`receiverId`,`paymentPhase`,`amount`,`currency`,`method`,`upiId`,`bankDetails`,`status`,`proofEvidenceId`,`transactionRef`,`verifiedAt`,`verifiedBy`,`rejectionReason`,`refundedAmount`,`refundedAt`,`refundReason`,`dueAt`,`expiredAt`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderPaymentEntity) {
        statement.bindText(1, entity.paymentId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.quoteId)
        statement.bindText(4, entity.payerId)
        statement.bindText(5, entity.receiverId)
        statement.bindText(6, entity.paymentPhase)
        statement.bindDouble(7, entity.amount)
        statement.bindText(8, entity.currency)
        statement.bindText(9, entity.method)
        val _tmpUpiId: String? = entity.upiId
        if (_tmpUpiId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpUpiId)
        }
        val _tmpBankDetails: String? = entity.bankDetails
        if (_tmpBankDetails == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpBankDetails)
        }
        statement.bindText(12, entity.status)
        val _tmpProofEvidenceId: String? = entity.proofEvidenceId
        if (_tmpProofEvidenceId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpProofEvidenceId)
        }
        val _tmpTransactionRef: String? = entity.transactionRef
        if (_tmpTransactionRef == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpTransactionRef)
        }
        val _tmpVerifiedAt: Long? = entity.verifiedAt
        if (_tmpVerifiedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpVerifiedAt)
        }
        val _tmpVerifiedBy: String? = entity.verifiedBy
        if (_tmpVerifiedBy == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpVerifiedBy)
        }
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpRejectionReason)
        }
        val _tmpRefundedAmount: Double? = entity.refundedAmount
        if (_tmpRefundedAmount == null) {
          statement.bindNull(18)
        } else {
          statement.bindDouble(18, _tmpRefundedAmount)
        }
        val _tmpRefundedAt: Long? = entity.refundedAt
        if (_tmpRefundedAt == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpRefundedAt)
        }
        val _tmpRefundReason: String? = entity.refundReason
        if (_tmpRefundReason == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpRefundReason)
        }
        statement.bindLong(21, entity.dueAt)
        val _tmpExpiredAt: Long? = entity.expiredAt
        if (_tmpExpiredAt == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpExpiredAt)
        }
        statement.bindLong(23, entity.createdAt)
        statement.bindLong(24, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(25, _tmp.toLong())
      }
    }
  }

  public override suspend fun upsert(payment: OrderPaymentEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOrderPaymentEntity.insert(_connection, payment)
  }

  public override suspend fun findById(paymentId: String): OrderPaymentEntity? {
    val _sql: String = "SELECT * FROM order_payments WHERE paymentId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, paymentId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: OrderPaymentEntity?
        if (_stmt.step()) {
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _result =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrderPayments(orderId: String): Flow<List<OrderPaymentEntity>> {
    val _sql: String = "SELECT * FROM order_payments WHERE orderId = ? ORDER BY createdAt"
    return createFlow(__db, false, arrayOf("order_payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderPaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderPaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPaymentByPhase(orderId: String, phase: String):
      OrderPaymentEntity? {
    val _sql: String = "SELECT * FROM order_payments WHERE orderId = ? AND paymentPhase = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        _argIndex = 2
        _stmt.bindText(_argIndex, phase)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: OrderPaymentEntity?
        if (_stmt.step()) {
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _result =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPendingPayments(payerId: String): Flow<List<OrderPaymentEntity>> {
    val _sql: String =
        "SELECT * FROM order_payments WHERE payerId = ? AND status = 'PENDING' ORDER BY dueAt"
    return createFlow(__db, false, arrayOf("order_payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, payerId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderPaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderPaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPendingPaymentsForBuyer(buyerId: String): Flow<List<OrderPaymentEntity>> {
    val _sql: String =
        "SELECT * FROM order_payments WHERE payerId = ? AND status = 'PENDING' ORDER BY dueAt"
    return createFlow(__db, false, arrayOf("order_payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderPaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderPaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPaymentsAwaitingVerification(receiverId: String):
      Flow<List<OrderPaymentEntity>> {
    val _sql: String =
        "SELECT * FROM order_payments WHERE receiverId = ? AND status = 'PROOF_SUBMITTED' ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("order_payments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, receiverId)
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderPaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderPaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalVerifiedAmount(orderId: String): Double? {
    val _sql: String =
        "SELECT SUM(amount) FROM order_payments WHERE orderId = ? AND status = 'VERIFIED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyRecords(): List<OrderPaymentEntity> {
    val _sql: String = "SELECT * FROM order_payments WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPaymentId: Int = getColumnIndexOrThrow(_stmt, "paymentId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfQuoteId: Int = getColumnIndexOrThrow(_stmt, "quoteId")
        val _columnIndexOfPayerId: Int = getColumnIndexOrThrow(_stmt, "payerId")
        val _columnIndexOfReceiverId: Int = getColumnIndexOrThrow(_stmt, "receiverId")
        val _columnIndexOfPaymentPhase: Int = getColumnIndexOrThrow(_stmt, "paymentPhase")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfMethod: Int = getColumnIndexOrThrow(_stmt, "method")
        val _columnIndexOfUpiId: Int = getColumnIndexOrThrow(_stmt, "upiId")
        val _columnIndexOfBankDetails: Int = getColumnIndexOrThrow(_stmt, "bankDetails")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProofEvidenceId: Int = getColumnIndexOrThrow(_stmt, "proofEvidenceId")
        val _columnIndexOfTransactionRef: Int = getColumnIndexOrThrow(_stmt, "transactionRef")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfRefundedAmount: Int = getColumnIndexOrThrow(_stmt, "refundedAmount")
        val _columnIndexOfRefundedAt: Int = getColumnIndexOrThrow(_stmt, "refundedAt")
        val _columnIndexOfRefundReason: Int = getColumnIndexOrThrow(_stmt, "refundReason")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfExpiredAt: Int = getColumnIndexOrThrow(_stmt, "expiredAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderPaymentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderPaymentEntity
          val _tmpPaymentId: String
          _tmpPaymentId = _stmt.getText(_columnIndexOfPaymentId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpQuoteId: String
          _tmpQuoteId = _stmt.getText(_columnIndexOfQuoteId)
          val _tmpPayerId: String
          _tmpPayerId = _stmt.getText(_columnIndexOfPayerId)
          val _tmpReceiverId: String
          _tmpReceiverId = _stmt.getText(_columnIndexOfReceiverId)
          val _tmpPaymentPhase: String
          _tmpPaymentPhase = _stmt.getText(_columnIndexOfPaymentPhase)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpMethod: String
          _tmpMethod = _stmt.getText(_columnIndexOfMethod)
          val _tmpUpiId: String?
          if (_stmt.isNull(_columnIndexOfUpiId)) {
            _tmpUpiId = null
          } else {
            _tmpUpiId = _stmt.getText(_columnIndexOfUpiId)
          }
          val _tmpBankDetails: String?
          if (_stmt.isNull(_columnIndexOfBankDetails)) {
            _tmpBankDetails = null
          } else {
            _tmpBankDetails = _stmt.getText(_columnIndexOfBankDetails)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProofEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfProofEvidenceId)) {
            _tmpProofEvidenceId = null
          } else {
            _tmpProofEvidenceId = _stmt.getText(_columnIndexOfProofEvidenceId)
          }
          val _tmpTransactionRef: String?
          if (_stmt.isNull(_columnIndexOfTransactionRef)) {
            _tmpTransactionRef = null
          } else {
            _tmpTransactionRef = _stmt.getText(_columnIndexOfTransactionRef)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpRefundedAmount: Double?
          if (_stmt.isNull(_columnIndexOfRefundedAmount)) {
            _tmpRefundedAmount = null
          } else {
            _tmpRefundedAmount = _stmt.getDouble(_columnIndexOfRefundedAmount)
          }
          val _tmpRefundedAt: Long?
          if (_stmt.isNull(_columnIndexOfRefundedAt)) {
            _tmpRefundedAt = null
          } else {
            _tmpRefundedAt = _stmt.getLong(_columnIndexOfRefundedAt)
          }
          val _tmpRefundReason: String?
          if (_stmt.isNull(_columnIndexOfRefundReason)) {
            _tmpRefundReason = null
          } else {
            _tmpRefundReason = _stmt.getText(_columnIndexOfRefundReason)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpExpiredAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiredAt)) {
            _tmpExpiredAt = null
          } else {
            _tmpExpiredAt = _stmt.getLong(_columnIndexOfExpiredAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          _item =
              OrderPaymentEntity(_tmpPaymentId,_tmpOrderId,_tmpQuoteId,_tmpPayerId,_tmpReceiverId,_tmpPaymentPhase,_tmpAmount,_tmpCurrency,_tmpMethod,_tmpUpiId,_tmpBankDetails,_tmpStatus,_tmpProofEvidenceId,_tmpTransactionRef,_tmpVerifiedAt,_tmpVerifiedBy,_tmpRejectionReason,_tmpRefundedAmount,_tmpRefundedAt,_tmpRefundReason,_tmpDueAt,_tmpExpiredAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun submitProof(
    paymentId: String,
    evidenceId: String,
    transactionRef: String?,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_payments SET status = 'PROOF_SUBMITTED', proofEvidenceId = ?, transactionRef = ?, updatedAt = ?, dirty = 1 WHERE paymentId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, evidenceId)
        _argIndex = 2
        if (transactionRef == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, transactionRef)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, paymentId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markVerified(
    paymentId: String,
    verifiedBy: String,
    verifiedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_payments SET status = 'VERIFIED', verifiedAt = ?, verifiedBy = ?, updatedAt = ?, dirty = 1 WHERE paymentId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, verifiedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, verifiedBy)
        _argIndex = 3
        _stmt.bindLong(_argIndex, verifiedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, paymentId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markRejected(
    paymentId: String,
    reason: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_payments SET status = 'REJECTED', rejectionReason = ?, updatedAt = ?, dirty = 1 WHERE paymentId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, reason)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, paymentId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun expireOverduePayments(expiredAt: Long) {
    val _sql: String =
        "UPDATE order_payments SET status = 'EXPIRED', expiredAt = ?, dirty = 1 WHERE dueAt < ? AND status = 'PENDING'"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, expiredAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, expiredAt)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markClean(paymentId: String) {
    val _sql: String = "UPDATE order_payments SET dirty = 0 WHERE paymentId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, paymentId)
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
