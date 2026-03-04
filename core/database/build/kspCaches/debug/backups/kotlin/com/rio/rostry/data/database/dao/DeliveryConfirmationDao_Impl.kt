package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.DeliveryConfirmationEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class DeliveryConfirmationDao_Impl(
  __db: RoomDatabase,
) : DeliveryConfirmationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDeliveryConfirmationEntity:
      EntityInsertAdapter<DeliveryConfirmationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDeliveryConfirmationEntity = object :
        EntityInsertAdapter<DeliveryConfirmationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `delivery_confirmations` (`confirmationId`,`orderId`,`buyerId`,`sellerId`,`deliveryOtp`,`otpGeneratedAt`,`otpExpiresAt`,`otpAttempts`,`maxOtpAttempts`,`status`,`confirmationMethod`,`deliveryPhotoEvidenceId`,`buyerConfirmationEvidenceId`,`gpsEvidenceId`,`confirmedAt`,`confirmedBy`,`deliveryNotes`,`balanceCollected`,`balanceCollectedAt`,`balanceEvidenceId`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DeliveryConfirmationEntity) {
        statement.bindText(1, entity.confirmationId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.buyerId)
        statement.bindText(4, entity.sellerId)
        statement.bindText(5, entity.deliveryOtp)
        statement.bindLong(6, entity.otpGeneratedAt)
        statement.bindLong(7, entity.otpExpiresAt)
        statement.bindLong(8, entity.otpAttempts.toLong())
        statement.bindLong(9, entity.maxOtpAttempts.toLong())
        statement.bindText(10, entity.status)
        val _tmpConfirmationMethod: String? = entity.confirmationMethod
        if (_tmpConfirmationMethod == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpConfirmationMethod)
        }
        val _tmpDeliveryPhotoEvidenceId: String? = entity.deliveryPhotoEvidenceId
        if (_tmpDeliveryPhotoEvidenceId == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpDeliveryPhotoEvidenceId)
        }
        val _tmpBuyerConfirmationEvidenceId: String? = entity.buyerConfirmationEvidenceId
        if (_tmpBuyerConfirmationEvidenceId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpBuyerConfirmationEvidenceId)
        }
        val _tmpGpsEvidenceId: String? = entity.gpsEvidenceId
        if (_tmpGpsEvidenceId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpGpsEvidenceId)
        }
        val _tmpConfirmedAt: Long? = entity.confirmedAt
        if (_tmpConfirmedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpConfirmedAt)
        }
        val _tmpConfirmedBy: String? = entity.confirmedBy
        if (_tmpConfirmedBy == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpConfirmedBy)
        }
        val _tmpDeliveryNotes: String? = entity.deliveryNotes
        if (_tmpDeliveryNotes == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpDeliveryNotes)
        }
        val _tmp: Int = if (entity.balanceCollected) 1 else 0
        statement.bindLong(18, _tmp.toLong())
        val _tmpBalanceCollectedAt: Long? = entity.balanceCollectedAt
        if (_tmpBalanceCollectedAt == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpBalanceCollectedAt)
        }
        val _tmpBalanceEvidenceId: String? = entity.balanceEvidenceId
        if (_tmpBalanceEvidenceId == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpBalanceEvidenceId)
        }
        statement.bindLong(21, entity.createdAt)
        statement.bindLong(22, entity.updatedAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(23, _tmp_1.toLong())
      }
    }
  }

  public override suspend fun upsert(confirmation: DeliveryConfirmationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDeliveryConfirmationEntity.insert(_connection, confirmation)
  }

  public override suspend fun findById(confirmationId: String): DeliveryConfirmationEntity? {
    val _sql: String = "SELECT * FROM delivery_confirmations WHERE confirmationId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, confirmationId)
        val _columnIndexOfConfirmationId: Int = getColumnIndexOrThrow(_stmt, "confirmationId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfDeliveryOtp: Int = getColumnIndexOrThrow(_stmt, "deliveryOtp")
        val _columnIndexOfOtpGeneratedAt: Int = getColumnIndexOrThrow(_stmt, "otpGeneratedAt")
        val _columnIndexOfOtpExpiresAt: Int = getColumnIndexOrThrow(_stmt, "otpExpiresAt")
        val _columnIndexOfOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "otpAttempts")
        val _columnIndexOfMaxOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "maxOtpAttempts")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfConfirmationMethod: Int = getColumnIndexOrThrow(_stmt,
            "confirmationMethod")
        val _columnIndexOfDeliveryPhotoEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "deliveryPhotoEvidenceId")
        val _columnIndexOfBuyerConfirmationEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "buyerConfirmationEvidenceId")
        val _columnIndexOfGpsEvidenceId: Int = getColumnIndexOrThrow(_stmt, "gpsEvidenceId")
        val _columnIndexOfConfirmedAt: Int = getColumnIndexOrThrow(_stmt, "confirmedAt")
        val _columnIndexOfConfirmedBy: Int = getColumnIndexOrThrow(_stmt, "confirmedBy")
        val _columnIndexOfDeliveryNotes: Int = getColumnIndexOrThrow(_stmt, "deliveryNotes")
        val _columnIndexOfBalanceCollected: Int = getColumnIndexOrThrow(_stmt, "balanceCollected")
        val _columnIndexOfBalanceCollectedAt: Int = getColumnIndexOrThrow(_stmt,
            "balanceCollectedAt")
        val _columnIndexOfBalanceEvidenceId: Int = getColumnIndexOrThrow(_stmt, "balanceEvidenceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: DeliveryConfirmationEntity?
        if (_stmt.step()) {
          val _tmpConfirmationId: String
          _tmpConfirmationId = _stmt.getText(_columnIndexOfConfirmationId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpDeliveryOtp: String
          _tmpDeliveryOtp = _stmt.getText(_columnIndexOfDeliveryOtp)
          val _tmpOtpGeneratedAt: Long
          _tmpOtpGeneratedAt = _stmt.getLong(_columnIndexOfOtpGeneratedAt)
          val _tmpOtpExpiresAt: Long
          _tmpOtpExpiresAt = _stmt.getLong(_columnIndexOfOtpExpiresAt)
          val _tmpOtpAttempts: Int
          _tmpOtpAttempts = _stmt.getLong(_columnIndexOfOtpAttempts).toInt()
          val _tmpMaxOtpAttempts: Int
          _tmpMaxOtpAttempts = _stmt.getLong(_columnIndexOfMaxOtpAttempts).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpConfirmationMethod: String?
          if (_stmt.isNull(_columnIndexOfConfirmationMethod)) {
            _tmpConfirmationMethod = null
          } else {
            _tmpConfirmationMethod = _stmt.getText(_columnIndexOfConfirmationMethod)
          }
          val _tmpDeliveryPhotoEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfDeliveryPhotoEvidenceId)) {
            _tmpDeliveryPhotoEvidenceId = null
          } else {
            _tmpDeliveryPhotoEvidenceId = _stmt.getText(_columnIndexOfDeliveryPhotoEvidenceId)
          }
          val _tmpBuyerConfirmationEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBuyerConfirmationEvidenceId)) {
            _tmpBuyerConfirmationEvidenceId = null
          } else {
            _tmpBuyerConfirmationEvidenceId =
                _stmt.getText(_columnIndexOfBuyerConfirmationEvidenceId)
          }
          val _tmpGpsEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfGpsEvidenceId)) {
            _tmpGpsEvidenceId = null
          } else {
            _tmpGpsEvidenceId = _stmt.getText(_columnIndexOfGpsEvidenceId)
          }
          val _tmpConfirmedAt: Long?
          if (_stmt.isNull(_columnIndexOfConfirmedAt)) {
            _tmpConfirmedAt = null
          } else {
            _tmpConfirmedAt = _stmt.getLong(_columnIndexOfConfirmedAt)
          }
          val _tmpConfirmedBy: String?
          if (_stmt.isNull(_columnIndexOfConfirmedBy)) {
            _tmpConfirmedBy = null
          } else {
            _tmpConfirmedBy = _stmt.getText(_columnIndexOfConfirmedBy)
          }
          val _tmpDeliveryNotes: String?
          if (_stmt.isNull(_columnIndexOfDeliveryNotes)) {
            _tmpDeliveryNotes = null
          } else {
            _tmpDeliveryNotes = _stmt.getText(_columnIndexOfDeliveryNotes)
          }
          val _tmpBalanceCollected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalanceCollected).toInt()
          _tmpBalanceCollected = _tmp != 0
          val _tmpBalanceCollectedAt: Long?
          if (_stmt.isNull(_columnIndexOfBalanceCollectedAt)) {
            _tmpBalanceCollectedAt = null
          } else {
            _tmpBalanceCollectedAt = _stmt.getLong(_columnIndexOfBalanceCollectedAt)
          }
          val _tmpBalanceEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBalanceEvidenceId)) {
            _tmpBalanceEvidenceId = null
          } else {
            _tmpBalanceEvidenceId = _stmt.getText(_columnIndexOfBalanceEvidenceId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _result =
              DeliveryConfirmationEntity(_tmpConfirmationId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpDeliveryOtp,_tmpOtpGeneratedAt,_tmpOtpExpiresAt,_tmpOtpAttempts,_tmpMaxOtpAttempts,_tmpStatus,_tmpConfirmationMethod,_tmpDeliveryPhotoEvidenceId,_tmpBuyerConfirmationEvidenceId,_tmpGpsEvidenceId,_tmpConfirmedAt,_tmpConfirmedBy,_tmpDeliveryNotes,_tmpBalanceCollected,_tmpBalanceCollectedAt,_tmpBalanceEvidenceId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByOrderId(orderId: String): DeliveryConfirmationEntity? {
    val _sql: String = "SELECT * FROM delivery_confirmations WHERE orderId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfConfirmationId: Int = getColumnIndexOrThrow(_stmt, "confirmationId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfDeliveryOtp: Int = getColumnIndexOrThrow(_stmt, "deliveryOtp")
        val _columnIndexOfOtpGeneratedAt: Int = getColumnIndexOrThrow(_stmt, "otpGeneratedAt")
        val _columnIndexOfOtpExpiresAt: Int = getColumnIndexOrThrow(_stmt, "otpExpiresAt")
        val _columnIndexOfOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "otpAttempts")
        val _columnIndexOfMaxOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "maxOtpAttempts")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfConfirmationMethod: Int = getColumnIndexOrThrow(_stmt,
            "confirmationMethod")
        val _columnIndexOfDeliveryPhotoEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "deliveryPhotoEvidenceId")
        val _columnIndexOfBuyerConfirmationEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "buyerConfirmationEvidenceId")
        val _columnIndexOfGpsEvidenceId: Int = getColumnIndexOrThrow(_stmt, "gpsEvidenceId")
        val _columnIndexOfConfirmedAt: Int = getColumnIndexOrThrow(_stmt, "confirmedAt")
        val _columnIndexOfConfirmedBy: Int = getColumnIndexOrThrow(_stmt, "confirmedBy")
        val _columnIndexOfDeliveryNotes: Int = getColumnIndexOrThrow(_stmt, "deliveryNotes")
        val _columnIndexOfBalanceCollected: Int = getColumnIndexOrThrow(_stmt, "balanceCollected")
        val _columnIndexOfBalanceCollectedAt: Int = getColumnIndexOrThrow(_stmt,
            "balanceCollectedAt")
        val _columnIndexOfBalanceEvidenceId: Int = getColumnIndexOrThrow(_stmt, "balanceEvidenceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: DeliveryConfirmationEntity?
        if (_stmt.step()) {
          val _tmpConfirmationId: String
          _tmpConfirmationId = _stmt.getText(_columnIndexOfConfirmationId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpDeliveryOtp: String
          _tmpDeliveryOtp = _stmt.getText(_columnIndexOfDeliveryOtp)
          val _tmpOtpGeneratedAt: Long
          _tmpOtpGeneratedAt = _stmt.getLong(_columnIndexOfOtpGeneratedAt)
          val _tmpOtpExpiresAt: Long
          _tmpOtpExpiresAt = _stmt.getLong(_columnIndexOfOtpExpiresAt)
          val _tmpOtpAttempts: Int
          _tmpOtpAttempts = _stmt.getLong(_columnIndexOfOtpAttempts).toInt()
          val _tmpMaxOtpAttempts: Int
          _tmpMaxOtpAttempts = _stmt.getLong(_columnIndexOfMaxOtpAttempts).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpConfirmationMethod: String?
          if (_stmt.isNull(_columnIndexOfConfirmationMethod)) {
            _tmpConfirmationMethod = null
          } else {
            _tmpConfirmationMethod = _stmt.getText(_columnIndexOfConfirmationMethod)
          }
          val _tmpDeliveryPhotoEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfDeliveryPhotoEvidenceId)) {
            _tmpDeliveryPhotoEvidenceId = null
          } else {
            _tmpDeliveryPhotoEvidenceId = _stmt.getText(_columnIndexOfDeliveryPhotoEvidenceId)
          }
          val _tmpBuyerConfirmationEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBuyerConfirmationEvidenceId)) {
            _tmpBuyerConfirmationEvidenceId = null
          } else {
            _tmpBuyerConfirmationEvidenceId =
                _stmt.getText(_columnIndexOfBuyerConfirmationEvidenceId)
          }
          val _tmpGpsEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfGpsEvidenceId)) {
            _tmpGpsEvidenceId = null
          } else {
            _tmpGpsEvidenceId = _stmt.getText(_columnIndexOfGpsEvidenceId)
          }
          val _tmpConfirmedAt: Long?
          if (_stmt.isNull(_columnIndexOfConfirmedAt)) {
            _tmpConfirmedAt = null
          } else {
            _tmpConfirmedAt = _stmt.getLong(_columnIndexOfConfirmedAt)
          }
          val _tmpConfirmedBy: String?
          if (_stmt.isNull(_columnIndexOfConfirmedBy)) {
            _tmpConfirmedBy = null
          } else {
            _tmpConfirmedBy = _stmt.getText(_columnIndexOfConfirmedBy)
          }
          val _tmpDeliveryNotes: String?
          if (_stmt.isNull(_columnIndexOfDeliveryNotes)) {
            _tmpDeliveryNotes = null
          } else {
            _tmpDeliveryNotes = _stmt.getText(_columnIndexOfDeliveryNotes)
          }
          val _tmpBalanceCollected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalanceCollected).toInt()
          _tmpBalanceCollected = _tmp != 0
          val _tmpBalanceCollectedAt: Long?
          if (_stmt.isNull(_columnIndexOfBalanceCollectedAt)) {
            _tmpBalanceCollectedAt = null
          } else {
            _tmpBalanceCollectedAt = _stmt.getLong(_columnIndexOfBalanceCollectedAt)
          }
          val _tmpBalanceEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBalanceEvidenceId)) {
            _tmpBalanceEvidenceId = null
          } else {
            _tmpBalanceEvidenceId = _stmt.getText(_columnIndexOfBalanceEvidenceId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _result =
              DeliveryConfirmationEntity(_tmpConfirmationId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpDeliveryOtp,_tmpOtpGeneratedAt,_tmpOtpExpiresAt,_tmpOtpAttempts,_tmpMaxOtpAttempts,_tmpStatus,_tmpConfirmationMethod,_tmpDeliveryPhotoEvidenceId,_tmpBuyerConfirmationEvidenceId,_tmpGpsEvidenceId,_tmpConfirmedAt,_tmpConfirmedBy,_tmpDeliveryNotes,_tmpBalanceCollected,_tmpBalanceCollectedAt,_tmpBalanceEvidenceId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByOrderId(orderId: String): Flow<DeliveryConfirmationEntity?> {
    val _sql: String = "SELECT * FROM delivery_confirmations WHERE orderId = ?"
    return createFlow(__db, false, arrayOf("delivery_confirmations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfConfirmationId: Int = getColumnIndexOrThrow(_stmt, "confirmationId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfDeliveryOtp: Int = getColumnIndexOrThrow(_stmt, "deliveryOtp")
        val _columnIndexOfOtpGeneratedAt: Int = getColumnIndexOrThrow(_stmt, "otpGeneratedAt")
        val _columnIndexOfOtpExpiresAt: Int = getColumnIndexOrThrow(_stmt, "otpExpiresAt")
        val _columnIndexOfOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "otpAttempts")
        val _columnIndexOfMaxOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "maxOtpAttempts")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfConfirmationMethod: Int = getColumnIndexOrThrow(_stmt,
            "confirmationMethod")
        val _columnIndexOfDeliveryPhotoEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "deliveryPhotoEvidenceId")
        val _columnIndexOfBuyerConfirmationEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "buyerConfirmationEvidenceId")
        val _columnIndexOfGpsEvidenceId: Int = getColumnIndexOrThrow(_stmt, "gpsEvidenceId")
        val _columnIndexOfConfirmedAt: Int = getColumnIndexOrThrow(_stmt, "confirmedAt")
        val _columnIndexOfConfirmedBy: Int = getColumnIndexOrThrow(_stmt, "confirmedBy")
        val _columnIndexOfDeliveryNotes: Int = getColumnIndexOrThrow(_stmt, "deliveryNotes")
        val _columnIndexOfBalanceCollected: Int = getColumnIndexOrThrow(_stmt, "balanceCollected")
        val _columnIndexOfBalanceCollectedAt: Int = getColumnIndexOrThrow(_stmt,
            "balanceCollectedAt")
        val _columnIndexOfBalanceEvidenceId: Int = getColumnIndexOrThrow(_stmt, "balanceEvidenceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: DeliveryConfirmationEntity?
        if (_stmt.step()) {
          val _tmpConfirmationId: String
          _tmpConfirmationId = _stmt.getText(_columnIndexOfConfirmationId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpDeliveryOtp: String
          _tmpDeliveryOtp = _stmt.getText(_columnIndexOfDeliveryOtp)
          val _tmpOtpGeneratedAt: Long
          _tmpOtpGeneratedAt = _stmt.getLong(_columnIndexOfOtpGeneratedAt)
          val _tmpOtpExpiresAt: Long
          _tmpOtpExpiresAt = _stmt.getLong(_columnIndexOfOtpExpiresAt)
          val _tmpOtpAttempts: Int
          _tmpOtpAttempts = _stmt.getLong(_columnIndexOfOtpAttempts).toInt()
          val _tmpMaxOtpAttempts: Int
          _tmpMaxOtpAttempts = _stmt.getLong(_columnIndexOfMaxOtpAttempts).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpConfirmationMethod: String?
          if (_stmt.isNull(_columnIndexOfConfirmationMethod)) {
            _tmpConfirmationMethod = null
          } else {
            _tmpConfirmationMethod = _stmt.getText(_columnIndexOfConfirmationMethod)
          }
          val _tmpDeliveryPhotoEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfDeliveryPhotoEvidenceId)) {
            _tmpDeliveryPhotoEvidenceId = null
          } else {
            _tmpDeliveryPhotoEvidenceId = _stmt.getText(_columnIndexOfDeliveryPhotoEvidenceId)
          }
          val _tmpBuyerConfirmationEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBuyerConfirmationEvidenceId)) {
            _tmpBuyerConfirmationEvidenceId = null
          } else {
            _tmpBuyerConfirmationEvidenceId =
                _stmt.getText(_columnIndexOfBuyerConfirmationEvidenceId)
          }
          val _tmpGpsEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfGpsEvidenceId)) {
            _tmpGpsEvidenceId = null
          } else {
            _tmpGpsEvidenceId = _stmt.getText(_columnIndexOfGpsEvidenceId)
          }
          val _tmpConfirmedAt: Long?
          if (_stmt.isNull(_columnIndexOfConfirmedAt)) {
            _tmpConfirmedAt = null
          } else {
            _tmpConfirmedAt = _stmt.getLong(_columnIndexOfConfirmedAt)
          }
          val _tmpConfirmedBy: String?
          if (_stmt.isNull(_columnIndexOfConfirmedBy)) {
            _tmpConfirmedBy = null
          } else {
            _tmpConfirmedBy = _stmt.getText(_columnIndexOfConfirmedBy)
          }
          val _tmpDeliveryNotes: String?
          if (_stmt.isNull(_columnIndexOfDeliveryNotes)) {
            _tmpDeliveryNotes = null
          } else {
            _tmpDeliveryNotes = _stmt.getText(_columnIndexOfDeliveryNotes)
          }
          val _tmpBalanceCollected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalanceCollected).toInt()
          _tmpBalanceCollected = _tmp != 0
          val _tmpBalanceCollectedAt: Long?
          if (_stmt.isNull(_columnIndexOfBalanceCollectedAt)) {
            _tmpBalanceCollectedAt = null
          } else {
            _tmpBalanceCollectedAt = _stmt.getLong(_columnIndexOfBalanceCollectedAt)
          }
          val _tmpBalanceEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBalanceEvidenceId)) {
            _tmpBalanceEvidenceId = null
          } else {
            _tmpBalanceEvidenceId = _stmt.getText(_columnIndexOfBalanceEvidenceId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _result =
              DeliveryConfirmationEntity(_tmpConfirmationId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpDeliveryOtp,_tmpOtpGeneratedAt,_tmpOtpExpiresAt,_tmpOtpAttempts,_tmpMaxOtpAttempts,_tmpStatus,_tmpConfirmationMethod,_tmpDeliveryPhotoEvidenceId,_tmpBuyerConfirmationEvidenceId,_tmpGpsEvidenceId,_tmpConfirmedAt,_tmpConfirmedBy,_tmpDeliveryNotes,_tmpBalanceCollected,_tmpBalanceCollectedAt,_tmpBalanceEvidenceId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPendingConfirmations(buyerId: String):
      Flow<List<DeliveryConfirmationEntity>> {
    val _sql: String =
        "SELECT * FROM delivery_confirmations WHERE buyerId = ? AND status = 'PENDING' ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("delivery_confirmations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, buyerId)
        val _columnIndexOfConfirmationId: Int = getColumnIndexOrThrow(_stmt, "confirmationId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfDeliveryOtp: Int = getColumnIndexOrThrow(_stmt, "deliveryOtp")
        val _columnIndexOfOtpGeneratedAt: Int = getColumnIndexOrThrow(_stmt, "otpGeneratedAt")
        val _columnIndexOfOtpExpiresAt: Int = getColumnIndexOrThrow(_stmt, "otpExpiresAt")
        val _columnIndexOfOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "otpAttempts")
        val _columnIndexOfMaxOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "maxOtpAttempts")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfConfirmationMethod: Int = getColumnIndexOrThrow(_stmt,
            "confirmationMethod")
        val _columnIndexOfDeliveryPhotoEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "deliveryPhotoEvidenceId")
        val _columnIndexOfBuyerConfirmationEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "buyerConfirmationEvidenceId")
        val _columnIndexOfGpsEvidenceId: Int = getColumnIndexOrThrow(_stmt, "gpsEvidenceId")
        val _columnIndexOfConfirmedAt: Int = getColumnIndexOrThrow(_stmt, "confirmedAt")
        val _columnIndexOfConfirmedBy: Int = getColumnIndexOrThrow(_stmt, "confirmedBy")
        val _columnIndexOfDeliveryNotes: Int = getColumnIndexOrThrow(_stmt, "deliveryNotes")
        val _columnIndexOfBalanceCollected: Int = getColumnIndexOrThrow(_stmt, "balanceCollected")
        val _columnIndexOfBalanceCollectedAt: Int = getColumnIndexOrThrow(_stmt,
            "balanceCollectedAt")
        val _columnIndexOfBalanceEvidenceId: Int = getColumnIndexOrThrow(_stmt, "balanceEvidenceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<DeliveryConfirmationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeliveryConfirmationEntity
          val _tmpConfirmationId: String
          _tmpConfirmationId = _stmt.getText(_columnIndexOfConfirmationId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpDeliveryOtp: String
          _tmpDeliveryOtp = _stmt.getText(_columnIndexOfDeliveryOtp)
          val _tmpOtpGeneratedAt: Long
          _tmpOtpGeneratedAt = _stmt.getLong(_columnIndexOfOtpGeneratedAt)
          val _tmpOtpExpiresAt: Long
          _tmpOtpExpiresAt = _stmt.getLong(_columnIndexOfOtpExpiresAt)
          val _tmpOtpAttempts: Int
          _tmpOtpAttempts = _stmt.getLong(_columnIndexOfOtpAttempts).toInt()
          val _tmpMaxOtpAttempts: Int
          _tmpMaxOtpAttempts = _stmt.getLong(_columnIndexOfMaxOtpAttempts).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpConfirmationMethod: String?
          if (_stmt.isNull(_columnIndexOfConfirmationMethod)) {
            _tmpConfirmationMethod = null
          } else {
            _tmpConfirmationMethod = _stmt.getText(_columnIndexOfConfirmationMethod)
          }
          val _tmpDeliveryPhotoEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfDeliveryPhotoEvidenceId)) {
            _tmpDeliveryPhotoEvidenceId = null
          } else {
            _tmpDeliveryPhotoEvidenceId = _stmt.getText(_columnIndexOfDeliveryPhotoEvidenceId)
          }
          val _tmpBuyerConfirmationEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBuyerConfirmationEvidenceId)) {
            _tmpBuyerConfirmationEvidenceId = null
          } else {
            _tmpBuyerConfirmationEvidenceId =
                _stmt.getText(_columnIndexOfBuyerConfirmationEvidenceId)
          }
          val _tmpGpsEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfGpsEvidenceId)) {
            _tmpGpsEvidenceId = null
          } else {
            _tmpGpsEvidenceId = _stmt.getText(_columnIndexOfGpsEvidenceId)
          }
          val _tmpConfirmedAt: Long?
          if (_stmt.isNull(_columnIndexOfConfirmedAt)) {
            _tmpConfirmedAt = null
          } else {
            _tmpConfirmedAt = _stmt.getLong(_columnIndexOfConfirmedAt)
          }
          val _tmpConfirmedBy: String?
          if (_stmt.isNull(_columnIndexOfConfirmedBy)) {
            _tmpConfirmedBy = null
          } else {
            _tmpConfirmedBy = _stmt.getText(_columnIndexOfConfirmedBy)
          }
          val _tmpDeliveryNotes: String?
          if (_stmt.isNull(_columnIndexOfDeliveryNotes)) {
            _tmpDeliveryNotes = null
          } else {
            _tmpDeliveryNotes = _stmt.getText(_columnIndexOfDeliveryNotes)
          }
          val _tmpBalanceCollected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalanceCollected).toInt()
          _tmpBalanceCollected = _tmp != 0
          val _tmpBalanceCollectedAt: Long?
          if (_stmt.isNull(_columnIndexOfBalanceCollectedAt)) {
            _tmpBalanceCollectedAt = null
          } else {
            _tmpBalanceCollectedAt = _stmt.getLong(_columnIndexOfBalanceCollectedAt)
          }
          val _tmpBalanceEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBalanceEvidenceId)) {
            _tmpBalanceEvidenceId = null
          } else {
            _tmpBalanceEvidenceId = _stmt.getText(_columnIndexOfBalanceEvidenceId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item =
              DeliveryConfirmationEntity(_tmpConfirmationId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpDeliveryOtp,_tmpOtpGeneratedAt,_tmpOtpExpiresAt,_tmpOtpAttempts,_tmpMaxOtpAttempts,_tmpStatus,_tmpConfirmationMethod,_tmpDeliveryPhotoEvidenceId,_tmpBuyerConfirmationEvidenceId,_tmpGpsEvidenceId,_tmpConfirmedAt,_tmpConfirmedBy,_tmpDeliveryNotes,_tmpBalanceCollected,_tmpBalanceCollectedAt,_tmpBalanceEvidenceId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyRecords(): List<DeliveryConfirmationEntity> {
    val _sql: String = "SELECT * FROM delivery_confirmations WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfConfirmationId: Int = getColumnIndexOrThrow(_stmt, "confirmationId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfBuyerId: Int = getColumnIndexOrThrow(_stmt, "buyerId")
        val _columnIndexOfSellerId: Int = getColumnIndexOrThrow(_stmt, "sellerId")
        val _columnIndexOfDeliveryOtp: Int = getColumnIndexOrThrow(_stmt, "deliveryOtp")
        val _columnIndexOfOtpGeneratedAt: Int = getColumnIndexOrThrow(_stmt, "otpGeneratedAt")
        val _columnIndexOfOtpExpiresAt: Int = getColumnIndexOrThrow(_stmt, "otpExpiresAt")
        val _columnIndexOfOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "otpAttempts")
        val _columnIndexOfMaxOtpAttempts: Int = getColumnIndexOrThrow(_stmt, "maxOtpAttempts")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfConfirmationMethod: Int = getColumnIndexOrThrow(_stmt,
            "confirmationMethod")
        val _columnIndexOfDeliveryPhotoEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "deliveryPhotoEvidenceId")
        val _columnIndexOfBuyerConfirmationEvidenceId: Int = getColumnIndexOrThrow(_stmt,
            "buyerConfirmationEvidenceId")
        val _columnIndexOfGpsEvidenceId: Int = getColumnIndexOrThrow(_stmt, "gpsEvidenceId")
        val _columnIndexOfConfirmedAt: Int = getColumnIndexOrThrow(_stmt, "confirmedAt")
        val _columnIndexOfConfirmedBy: Int = getColumnIndexOrThrow(_stmt, "confirmedBy")
        val _columnIndexOfDeliveryNotes: Int = getColumnIndexOrThrow(_stmt, "deliveryNotes")
        val _columnIndexOfBalanceCollected: Int = getColumnIndexOrThrow(_stmt, "balanceCollected")
        val _columnIndexOfBalanceCollectedAt: Int = getColumnIndexOrThrow(_stmt,
            "balanceCollectedAt")
        val _columnIndexOfBalanceEvidenceId: Int = getColumnIndexOrThrow(_stmt, "balanceEvidenceId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<DeliveryConfirmationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DeliveryConfirmationEntity
          val _tmpConfirmationId: String
          _tmpConfirmationId = _stmt.getText(_columnIndexOfConfirmationId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpBuyerId: String
          _tmpBuyerId = _stmt.getText(_columnIndexOfBuyerId)
          val _tmpSellerId: String
          _tmpSellerId = _stmt.getText(_columnIndexOfSellerId)
          val _tmpDeliveryOtp: String
          _tmpDeliveryOtp = _stmt.getText(_columnIndexOfDeliveryOtp)
          val _tmpOtpGeneratedAt: Long
          _tmpOtpGeneratedAt = _stmt.getLong(_columnIndexOfOtpGeneratedAt)
          val _tmpOtpExpiresAt: Long
          _tmpOtpExpiresAt = _stmt.getLong(_columnIndexOfOtpExpiresAt)
          val _tmpOtpAttempts: Int
          _tmpOtpAttempts = _stmt.getLong(_columnIndexOfOtpAttempts).toInt()
          val _tmpMaxOtpAttempts: Int
          _tmpMaxOtpAttempts = _stmt.getLong(_columnIndexOfMaxOtpAttempts).toInt()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpConfirmationMethod: String?
          if (_stmt.isNull(_columnIndexOfConfirmationMethod)) {
            _tmpConfirmationMethod = null
          } else {
            _tmpConfirmationMethod = _stmt.getText(_columnIndexOfConfirmationMethod)
          }
          val _tmpDeliveryPhotoEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfDeliveryPhotoEvidenceId)) {
            _tmpDeliveryPhotoEvidenceId = null
          } else {
            _tmpDeliveryPhotoEvidenceId = _stmt.getText(_columnIndexOfDeliveryPhotoEvidenceId)
          }
          val _tmpBuyerConfirmationEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBuyerConfirmationEvidenceId)) {
            _tmpBuyerConfirmationEvidenceId = null
          } else {
            _tmpBuyerConfirmationEvidenceId =
                _stmt.getText(_columnIndexOfBuyerConfirmationEvidenceId)
          }
          val _tmpGpsEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfGpsEvidenceId)) {
            _tmpGpsEvidenceId = null
          } else {
            _tmpGpsEvidenceId = _stmt.getText(_columnIndexOfGpsEvidenceId)
          }
          val _tmpConfirmedAt: Long?
          if (_stmt.isNull(_columnIndexOfConfirmedAt)) {
            _tmpConfirmedAt = null
          } else {
            _tmpConfirmedAt = _stmt.getLong(_columnIndexOfConfirmedAt)
          }
          val _tmpConfirmedBy: String?
          if (_stmt.isNull(_columnIndexOfConfirmedBy)) {
            _tmpConfirmedBy = null
          } else {
            _tmpConfirmedBy = _stmt.getText(_columnIndexOfConfirmedBy)
          }
          val _tmpDeliveryNotes: String?
          if (_stmt.isNull(_columnIndexOfDeliveryNotes)) {
            _tmpDeliveryNotes = null
          } else {
            _tmpDeliveryNotes = _stmt.getText(_columnIndexOfDeliveryNotes)
          }
          val _tmpBalanceCollected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfBalanceCollected).toInt()
          _tmpBalanceCollected = _tmp != 0
          val _tmpBalanceCollectedAt: Long?
          if (_stmt.isNull(_columnIndexOfBalanceCollectedAt)) {
            _tmpBalanceCollectedAt = null
          } else {
            _tmpBalanceCollectedAt = _stmt.getLong(_columnIndexOfBalanceCollectedAt)
          }
          val _tmpBalanceEvidenceId: String?
          if (_stmt.isNull(_columnIndexOfBalanceEvidenceId)) {
            _tmpBalanceEvidenceId = null
          } else {
            _tmpBalanceEvidenceId = _stmt.getText(_columnIndexOfBalanceEvidenceId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          _item =
              DeliveryConfirmationEntity(_tmpConfirmationId,_tmpOrderId,_tmpBuyerId,_tmpSellerId,_tmpDeliveryOtp,_tmpOtpGeneratedAt,_tmpOtpExpiresAt,_tmpOtpAttempts,_tmpMaxOtpAttempts,_tmpStatus,_tmpConfirmationMethod,_tmpDeliveryPhotoEvidenceId,_tmpBuyerConfirmationEvidenceId,_tmpGpsEvidenceId,_tmpConfirmedAt,_tmpConfirmedBy,_tmpDeliveryNotes,_tmpBalanceCollected,_tmpBalanceCollectedAt,_tmpBalanceEvidenceId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementOtpAttempts(confirmationId: String, updatedAt: Long) {
    val _sql: String =
        "UPDATE delivery_confirmations SET otpAttempts = otpAttempts + 1, updatedAt = ?, dirty = 1 WHERE confirmationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, confirmationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun confirmWithOtp(
    confirmationId: String,
    confirmedBy: String,
    confirmedAt: Long,
  ) {
    val _sql: String =
        "UPDATE delivery_confirmations SET status = 'OTP_VERIFIED', confirmationMethod = 'OTP', confirmedAt = ?, confirmedBy = ?, updatedAt = ?, dirty = 1 WHERE confirmationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, confirmedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, confirmedBy)
        _argIndex = 3
        _stmt.bindLong(_argIndex, confirmedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, confirmationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun confirmWithPhoto(
    confirmationId: String,
    photoEvidenceId: String?,
    buyerPhotoEvidenceId: String?,
    confirmedBy: String,
    confirmedAt: Long,
  ) {
    val _sql: String =
        "UPDATE delivery_confirmations SET status = 'PHOTO_CONFIRMED', confirmationMethod = 'PHOTO', deliveryPhotoEvidenceId = ?, buyerConfirmationEvidenceId = ?, confirmedAt = ?, confirmedBy = ?, updatedAt = ?, dirty = 1 WHERE confirmationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (photoEvidenceId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, photoEvidenceId)
        }
        _argIndex = 2
        if (buyerPhotoEvidenceId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, buyerPhotoEvidenceId)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, confirmedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, confirmedBy)
        _argIndex = 5
        _stmt.bindLong(_argIndex, confirmedAt)
        _argIndex = 6
        _stmt.bindText(_argIndex, confirmationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markBalanceCollected(
    confirmationId: String,
    evidenceId: String?,
    collectedAt: Long,
  ) {
    val _sql: String =
        "UPDATE delivery_confirmations SET balanceCollected = 1, balanceCollectedAt = ?, balanceEvidenceId = ?, updatedAt = ?, dirty = 1 WHERE confirmationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, collectedAt)
        _argIndex = 2
        if (evidenceId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, evidenceId)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, collectedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, confirmationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markClean(confirmationId: String) {
    val _sql: String = "UPDATE delivery_confirmations SET dirty = 0 WHERE confirmationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, confirmationId)
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
