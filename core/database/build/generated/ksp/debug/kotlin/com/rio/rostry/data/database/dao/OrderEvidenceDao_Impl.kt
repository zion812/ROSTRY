package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.OrderEvidenceEntity
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
public class OrderEvidenceDao_Impl(
  __db: RoomDatabase,
) : OrderEvidenceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOrderEvidenceEntity: EntityInsertAdapter<OrderEvidenceEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOrderEvidenceEntity = object : EntityInsertAdapter<OrderEvidenceEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `order_evidence` (`evidenceId`,`orderId`,`evidenceType`,`uploadedBy`,`uploadedByRole`,`imageUri`,`videoUri`,`textContent`,`geoLatitude`,`geoLongitude`,`geoAddress`,`isVerified`,`verifiedBy`,`verifiedAt`,`verificationNote`,`deviceTimestamp`,`createdAt`,`updatedAt`,`isDeleted`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OrderEvidenceEntity) {
        statement.bindText(1, entity.evidenceId)
        statement.bindText(2, entity.orderId)
        statement.bindText(3, entity.evidenceType)
        statement.bindText(4, entity.uploadedBy)
        statement.bindText(5, entity.uploadedByRole)
        val _tmpImageUri: String? = entity.imageUri
        if (_tmpImageUri == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpImageUri)
        }
        val _tmpVideoUri: String? = entity.videoUri
        if (_tmpVideoUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpVideoUri)
        }
        val _tmpTextContent: String? = entity.textContent
        if (_tmpTextContent == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpTextContent)
        }
        val _tmpGeoLatitude: Double? = entity.geoLatitude
        if (_tmpGeoLatitude == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpGeoLatitude)
        }
        val _tmpGeoLongitude: Double? = entity.geoLongitude
        if (_tmpGeoLongitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpGeoLongitude)
        }
        val _tmpGeoAddress: String? = entity.geoAddress
        if (_tmpGeoAddress == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpGeoAddress)
        }
        val _tmp: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpVerifiedBy: String? = entity.verifiedBy
        if (_tmpVerifiedBy == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpVerifiedBy)
        }
        val _tmpVerifiedAt: Long? = entity.verifiedAt
        if (_tmpVerifiedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpVerifiedAt)
        }
        val _tmpVerificationNote: String? = entity.verificationNote
        if (_tmpVerificationNote == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpVerificationNote)
        }
        statement.bindLong(16, entity.deviceTimestamp)
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(19, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(20, _tmp_2.toLong())
      }
    }
  }

  public override suspend fun upsert(evidence: OrderEvidenceEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOrderEvidenceEntity.insert(_connection, evidence)
  }

  public override suspend fun upsertAll(evidences: List<OrderEvidenceEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfOrderEvidenceEntity.insert(_connection, evidences)
  }

  public override suspend fun findById(evidenceId: String): OrderEvidenceEntity? {
    val _sql: String = "SELECT * FROM order_evidence WHERE evidenceId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, evidenceId)
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfEvidenceType: Int = getColumnIndexOrThrow(_stmt, "evidenceType")
        val _columnIndexOfUploadedBy: Int = getColumnIndexOrThrow(_stmt, "uploadedBy")
        val _columnIndexOfUploadedByRole: Int = getColumnIndexOrThrow(_stmt, "uploadedByRole")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _columnIndexOfVideoUri: Int = getColumnIndexOrThrow(_stmt, "videoUri")
        val _columnIndexOfTextContent: Int = getColumnIndexOrThrow(_stmt, "textContent")
        val _columnIndexOfGeoLatitude: Int = getColumnIndexOrThrow(_stmt, "geoLatitude")
        val _columnIndexOfGeoLongitude: Int = getColumnIndexOrThrow(_stmt, "geoLongitude")
        val _columnIndexOfGeoAddress: Int = getColumnIndexOrThrow(_stmt, "geoAddress")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerificationNote: Int = getColumnIndexOrThrow(_stmt, "verificationNote")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: OrderEvidenceEntity?
        if (_stmt.step()) {
          val _tmpEvidenceId: String
          _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpEvidenceType: String
          _tmpEvidenceType = _stmt.getText(_columnIndexOfEvidenceType)
          val _tmpUploadedBy: String
          _tmpUploadedBy = _stmt.getText(_columnIndexOfUploadedBy)
          val _tmpUploadedByRole: String
          _tmpUploadedByRole = _stmt.getText(_columnIndexOfUploadedByRole)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          val _tmpVideoUri: String?
          if (_stmt.isNull(_columnIndexOfVideoUri)) {
            _tmpVideoUri = null
          } else {
            _tmpVideoUri = _stmt.getText(_columnIndexOfVideoUri)
          }
          val _tmpTextContent: String?
          if (_stmt.isNull(_columnIndexOfTextContent)) {
            _tmpTextContent = null
          } else {
            _tmpTextContent = _stmt.getText(_columnIndexOfTextContent)
          }
          val _tmpGeoLatitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLatitude)) {
            _tmpGeoLatitude = null
          } else {
            _tmpGeoLatitude = _stmt.getDouble(_columnIndexOfGeoLatitude)
          }
          val _tmpGeoLongitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLongitude)) {
            _tmpGeoLongitude = null
          } else {
            _tmpGeoLongitude = _stmt.getDouble(_columnIndexOfGeoLongitude)
          }
          val _tmpGeoAddress: String?
          if (_stmt.isNull(_columnIndexOfGeoAddress)) {
            _tmpGeoAddress = null
          } else {
            _tmpGeoAddress = _stmt.getText(_columnIndexOfGeoAddress)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerificationNote: String?
          if (_stmt.isNull(_columnIndexOfVerificationNote)) {
            _tmpVerificationNote = null
          } else {
            _tmpVerificationNote = _stmt.getText(_columnIndexOfVerificationNote)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _result =
              OrderEvidenceEntity(_tmpEvidenceId,_tmpOrderId,_tmpEvidenceType,_tmpUploadedBy,_tmpUploadedByRole,_tmpImageUri,_tmpVideoUri,_tmpTextContent,_tmpGeoLatitude,_tmpGeoLongitude,_tmpGeoAddress,_tmpIsVerified,_tmpVerifiedBy,_tmpVerifiedAt,_tmpVerificationNote,_tmpDeviceTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getOrderEvidence(orderId: String): Flow<List<OrderEvidenceEntity>> {
    val _sql: String =
        "SELECT * FROM order_evidence WHERE orderId = ? AND isDeleted = 0 ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("order_evidence")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfEvidenceType: Int = getColumnIndexOrThrow(_stmt, "evidenceType")
        val _columnIndexOfUploadedBy: Int = getColumnIndexOrThrow(_stmt, "uploadedBy")
        val _columnIndexOfUploadedByRole: Int = getColumnIndexOrThrow(_stmt, "uploadedByRole")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _columnIndexOfVideoUri: Int = getColumnIndexOrThrow(_stmt, "videoUri")
        val _columnIndexOfTextContent: Int = getColumnIndexOrThrow(_stmt, "textContent")
        val _columnIndexOfGeoLatitude: Int = getColumnIndexOrThrow(_stmt, "geoLatitude")
        val _columnIndexOfGeoLongitude: Int = getColumnIndexOrThrow(_stmt, "geoLongitude")
        val _columnIndexOfGeoAddress: Int = getColumnIndexOrThrow(_stmt, "geoAddress")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerificationNote: Int = getColumnIndexOrThrow(_stmt, "verificationNote")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderEvidenceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEvidenceEntity
          val _tmpEvidenceId: String
          _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpEvidenceType: String
          _tmpEvidenceType = _stmt.getText(_columnIndexOfEvidenceType)
          val _tmpUploadedBy: String
          _tmpUploadedBy = _stmt.getText(_columnIndexOfUploadedBy)
          val _tmpUploadedByRole: String
          _tmpUploadedByRole = _stmt.getText(_columnIndexOfUploadedByRole)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          val _tmpVideoUri: String?
          if (_stmt.isNull(_columnIndexOfVideoUri)) {
            _tmpVideoUri = null
          } else {
            _tmpVideoUri = _stmt.getText(_columnIndexOfVideoUri)
          }
          val _tmpTextContent: String?
          if (_stmt.isNull(_columnIndexOfTextContent)) {
            _tmpTextContent = null
          } else {
            _tmpTextContent = _stmt.getText(_columnIndexOfTextContent)
          }
          val _tmpGeoLatitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLatitude)) {
            _tmpGeoLatitude = null
          } else {
            _tmpGeoLatitude = _stmt.getDouble(_columnIndexOfGeoLatitude)
          }
          val _tmpGeoLongitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLongitude)) {
            _tmpGeoLongitude = null
          } else {
            _tmpGeoLongitude = _stmt.getDouble(_columnIndexOfGeoLongitude)
          }
          val _tmpGeoAddress: String?
          if (_stmt.isNull(_columnIndexOfGeoAddress)) {
            _tmpGeoAddress = null
          } else {
            _tmpGeoAddress = _stmt.getText(_columnIndexOfGeoAddress)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerificationNote: String?
          if (_stmt.isNull(_columnIndexOfVerificationNote)) {
            _tmpVerificationNote = null
          } else {
            _tmpVerificationNote = _stmt.getText(_columnIndexOfVerificationNote)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              OrderEvidenceEntity(_tmpEvidenceId,_tmpOrderId,_tmpEvidenceType,_tmpUploadedBy,_tmpUploadedByRole,_tmpImageUri,_tmpVideoUri,_tmpTextContent,_tmpGeoLatitude,_tmpGeoLongitude,_tmpGeoAddress,_tmpIsVerified,_tmpVerifiedBy,_tmpVerifiedAt,_tmpVerificationNote,_tmpDeviceTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEvidenceByType(orderId: String, type: String):
      List<OrderEvidenceEntity> {
    val _sql: String =
        "SELECT * FROM order_evidence WHERE orderId = ? AND evidenceType = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfEvidenceType: Int = getColumnIndexOrThrow(_stmt, "evidenceType")
        val _columnIndexOfUploadedBy: Int = getColumnIndexOrThrow(_stmt, "uploadedBy")
        val _columnIndexOfUploadedByRole: Int = getColumnIndexOrThrow(_stmt, "uploadedByRole")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _columnIndexOfVideoUri: Int = getColumnIndexOrThrow(_stmt, "videoUri")
        val _columnIndexOfTextContent: Int = getColumnIndexOrThrow(_stmt, "textContent")
        val _columnIndexOfGeoLatitude: Int = getColumnIndexOrThrow(_stmt, "geoLatitude")
        val _columnIndexOfGeoLongitude: Int = getColumnIndexOrThrow(_stmt, "geoLongitude")
        val _columnIndexOfGeoAddress: Int = getColumnIndexOrThrow(_stmt, "geoAddress")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerificationNote: Int = getColumnIndexOrThrow(_stmt, "verificationNote")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderEvidenceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEvidenceEntity
          val _tmpEvidenceId: String
          _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpEvidenceType: String
          _tmpEvidenceType = _stmt.getText(_columnIndexOfEvidenceType)
          val _tmpUploadedBy: String
          _tmpUploadedBy = _stmt.getText(_columnIndexOfUploadedBy)
          val _tmpUploadedByRole: String
          _tmpUploadedByRole = _stmt.getText(_columnIndexOfUploadedByRole)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          val _tmpVideoUri: String?
          if (_stmt.isNull(_columnIndexOfVideoUri)) {
            _tmpVideoUri = null
          } else {
            _tmpVideoUri = _stmt.getText(_columnIndexOfVideoUri)
          }
          val _tmpTextContent: String?
          if (_stmt.isNull(_columnIndexOfTextContent)) {
            _tmpTextContent = null
          } else {
            _tmpTextContent = _stmt.getText(_columnIndexOfTextContent)
          }
          val _tmpGeoLatitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLatitude)) {
            _tmpGeoLatitude = null
          } else {
            _tmpGeoLatitude = _stmt.getDouble(_columnIndexOfGeoLatitude)
          }
          val _tmpGeoLongitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLongitude)) {
            _tmpGeoLongitude = null
          } else {
            _tmpGeoLongitude = _stmt.getDouble(_columnIndexOfGeoLongitude)
          }
          val _tmpGeoAddress: String?
          if (_stmt.isNull(_columnIndexOfGeoAddress)) {
            _tmpGeoAddress = null
          } else {
            _tmpGeoAddress = _stmt.getText(_columnIndexOfGeoAddress)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerificationNote: String?
          if (_stmt.isNull(_columnIndexOfVerificationNote)) {
            _tmpVerificationNote = null
          } else {
            _tmpVerificationNote = _stmt.getText(_columnIndexOfVerificationNote)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              OrderEvidenceEntity(_tmpEvidenceId,_tmpOrderId,_tmpEvidenceType,_tmpUploadedBy,_tmpUploadedByRole,_tmpImageUri,_tmpVideoUri,_tmpTextContent,_tmpGeoLatitude,_tmpGeoLongitude,_tmpGeoAddress,_tmpIsVerified,_tmpVerifiedBy,_tmpVerifiedAt,_tmpVerificationNote,_tmpDeviceTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEvidenceByUser(orderId: String, userId: String):
      Flow<List<OrderEvidenceEntity>> {
    val _sql: String =
        "SELECT * FROM order_evidence WHERE orderId = ? AND uploadedBy = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("order_evidence")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfEvidenceType: Int = getColumnIndexOrThrow(_stmt, "evidenceType")
        val _columnIndexOfUploadedBy: Int = getColumnIndexOrThrow(_stmt, "uploadedBy")
        val _columnIndexOfUploadedByRole: Int = getColumnIndexOrThrow(_stmt, "uploadedByRole")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _columnIndexOfVideoUri: Int = getColumnIndexOrThrow(_stmt, "videoUri")
        val _columnIndexOfTextContent: Int = getColumnIndexOrThrow(_stmt, "textContent")
        val _columnIndexOfGeoLatitude: Int = getColumnIndexOrThrow(_stmt, "geoLatitude")
        val _columnIndexOfGeoLongitude: Int = getColumnIndexOrThrow(_stmt, "geoLongitude")
        val _columnIndexOfGeoAddress: Int = getColumnIndexOrThrow(_stmt, "geoAddress")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerificationNote: Int = getColumnIndexOrThrow(_stmt, "verificationNote")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderEvidenceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEvidenceEntity
          val _tmpEvidenceId: String
          _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpEvidenceType: String
          _tmpEvidenceType = _stmt.getText(_columnIndexOfEvidenceType)
          val _tmpUploadedBy: String
          _tmpUploadedBy = _stmt.getText(_columnIndexOfUploadedBy)
          val _tmpUploadedByRole: String
          _tmpUploadedByRole = _stmt.getText(_columnIndexOfUploadedByRole)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          val _tmpVideoUri: String?
          if (_stmt.isNull(_columnIndexOfVideoUri)) {
            _tmpVideoUri = null
          } else {
            _tmpVideoUri = _stmt.getText(_columnIndexOfVideoUri)
          }
          val _tmpTextContent: String?
          if (_stmt.isNull(_columnIndexOfTextContent)) {
            _tmpTextContent = null
          } else {
            _tmpTextContent = _stmt.getText(_columnIndexOfTextContent)
          }
          val _tmpGeoLatitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLatitude)) {
            _tmpGeoLatitude = null
          } else {
            _tmpGeoLatitude = _stmt.getDouble(_columnIndexOfGeoLatitude)
          }
          val _tmpGeoLongitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLongitude)) {
            _tmpGeoLongitude = null
          } else {
            _tmpGeoLongitude = _stmt.getDouble(_columnIndexOfGeoLongitude)
          }
          val _tmpGeoAddress: String?
          if (_stmt.isNull(_columnIndexOfGeoAddress)) {
            _tmpGeoAddress = null
          } else {
            _tmpGeoAddress = _stmt.getText(_columnIndexOfGeoAddress)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerificationNote: String?
          if (_stmt.isNull(_columnIndexOfVerificationNote)) {
            _tmpVerificationNote = null
          } else {
            _tmpVerificationNote = _stmt.getText(_columnIndexOfVerificationNote)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              OrderEvidenceEntity(_tmpEvidenceId,_tmpOrderId,_tmpEvidenceType,_tmpUploadedBy,_tmpUploadedByRole,_tmpImageUri,_tmpVideoUri,_tmpTextContent,_tmpGeoLatitude,_tmpGeoLongitude,_tmpGeoAddress,_tmpIsVerified,_tmpVerifiedBy,_tmpVerifiedAt,_tmpVerificationNote,_tmpDeviceTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUnverifiedEvidence(orderId: String): Flow<List<OrderEvidenceEntity>> {
    val _sql: String =
        "SELECT * FROM order_evidence WHERE orderId = ? AND isVerified = 0 AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("order_evidence")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfEvidenceType: Int = getColumnIndexOrThrow(_stmt, "evidenceType")
        val _columnIndexOfUploadedBy: Int = getColumnIndexOrThrow(_stmt, "uploadedBy")
        val _columnIndexOfUploadedByRole: Int = getColumnIndexOrThrow(_stmt, "uploadedByRole")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _columnIndexOfVideoUri: Int = getColumnIndexOrThrow(_stmt, "videoUri")
        val _columnIndexOfTextContent: Int = getColumnIndexOrThrow(_stmt, "textContent")
        val _columnIndexOfGeoLatitude: Int = getColumnIndexOrThrow(_stmt, "geoLatitude")
        val _columnIndexOfGeoLongitude: Int = getColumnIndexOrThrow(_stmt, "geoLongitude")
        val _columnIndexOfGeoAddress: Int = getColumnIndexOrThrow(_stmt, "geoAddress")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerificationNote: Int = getColumnIndexOrThrow(_stmt, "verificationNote")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderEvidenceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEvidenceEntity
          val _tmpEvidenceId: String
          _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpEvidenceType: String
          _tmpEvidenceType = _stmt.getText(_columnIndexOfEvidenceType)
          val _tmpUploadedBy: String
          _tmpUploadedBy = _stmt.getText(_columnIndexOfUploadedBy)
          val _tmpUploadedByRole: String
          _tmpUploadedByRole = _stmt.getText(_columnIndexOfUploadedByRole)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          val _tmpVideoUri: String?
          if (_stmt.isNull(_columnIndexOfVideoUri)) {
            _tmpVideoUri = null
          } else {
            _tmpVideoUri = _stmt.getText(_columnIndexOfVideoUri)
          }
          val _tmpTextContent: String?
          if (_stmt.isNull(_columnIndexOfTextContent)) {
            _tmpTextContent = null
          } else {
            _tmpTextContent = _stmt.getText(_columnIndexOfTextContent)
          }
          val _tmpGeoLatitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLatitude)) {
            _tmpGeoLatitude = null
          } else {
            _tmpGeoLatitude = _stmt.getDouble(_columnIndexOfGeoLatitude)
          }
          val _tmpGeoLongitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLongitude)) {
            _tmpGeoLongitude = null
          } else {
            _tmpGeoLongitude = _stmt.getDouble(_columnIndexOfGeoLongitude)
          }
          val _tmpGeoAddress: String?
          if (_stmt.isNull(_columnIndexOfGeoAddress)) {
            _tmpGeoAddress = null
          } else {
            _tmpGeoAddress = _stmt.getText(_columnIndexOfGeoAddress)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerificationNote: String?
          if (_stmt.isNull(_columnIndexOfVerificationNote)) {
            _tmpVerificationNote = null
          } else {
            _tmpVerificationNote = _stmt.getText(_columnIndexOfVerificationNote)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              OrderEvidenceEntity(_tmpEvidenceId,_tmpOrderId,_tmpEvidenceType,_tmpUploadedBy,_tmpUploadedByRole,_tmpImageUri,_tmpVideoUri,_tmpTextContent,_tmpGeoLatitude,_tmpGeoLongitude,_tmpGeoAddress,_tmpIsVerified,_tmpVerifiedBy,_tmpVerifiedAt,_tmpVerificationNote,_tmpDeviceTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyRecords(): List<OrderEvidenceEntity> {
    val _sql: String = "SELECT * FROM order_evidence WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfEvidenceId: Int = getColumnIndexOrThrow(_stmt, "evidenceId")
        val _columnIndexOfOrderId: Int = getColumnIndexOrThrow(_stmt, "orderId")
        val _columnIndexOfEvidenceType: Int = getColumnIndexOrThrow(_stmt, "evidenceType")
        val _columnIndexOfUploadedBy: Int = getColumnIndexOrThrow(_stmt, "uploadedBy")
        val _columnIndexOfUploadedByRole: Int = getColumnIndexOrThrow(_stmt, "uploadedByRole")
        val _columnIndexOfImageUri: Int = getColumnIndexOrThrow(_stmt, "imageUri")
        val _columnIndexOfVideoUri: Int = getColumnIndexOrThrow(_stmt, "videoUri")
        val _columnIndexOfTextContent: Int = getColumnIndexOrThrow(_stmt, "textContent")
        val _columnIndexOfGeoLatitude: Int = getColumnIndexOrThrow(_stmt, "geoLatitude")
        val _columnIndexOfGeoLongitude: Int = getColumnIndexOrThrow(_stmt, "geoLongitude")
        val _columnIndexOfGeoAddress: Int = getColumnIndexOrThrow(_stmt, "geoAddress")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedBy: Int = getColumnIndexOrThrow(_stmt, "verifiedBy")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfVerificationNote: Int = getColumnIndexOrThrow(_stmt, "verificationNote")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<OrderEvidenceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OrderEvidenceEntity
          val _tmpEvidenceId: String
          _tmpEvidenceId = _stmt.getText(_columnIndexOfEvidenceId)
          val _tmpOrderId: String
          _tmpOrderId = _stmt.getText(_columnIndexOfOrderId)
          val _tmpEvidenceType: String
          _tmpEvidenceType = _stmt.getText(_columnIndexOfEvidenceType)
          val _tmpUploadedBy: String
          _tmpUploadedBy = _stmt.getText(_columnIndexOfUploadedBy)
          val _tmpUploadedByRole: String
          _tmpUploadedByRole = _stmt.getText(_columnIndexOfUploadedByRole)
          val _tmpImageUri: String?
          if (_stmt.isNull(_columnIndexOfImageUri)) {
            _tmpImageUri = null
          } else {
            _tmpImageUri = _stmt.getText(_columnIndexOfImageUri)
          }
          val _tmpVideoUri: String?
          if (_stmt.isNull(_columnIndexOfVideoUri)) {
            _tmpVideoUri = null
          } else {
            _tmpVideoUri = _stmt.getText(_columnIndexOfVideoUri)
          }
          val _tmpTextContent: String?
          if (_stmt.isNull(_columnIndexOfTextContent)) {
            _tmpTextContent = null
          } else {
            _tmpTextContent = _stmt.getText(_columnIndexOfTextContent)
          }
          val _tmpGeoLatitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLatitude)) {
            _tmpGeoLatitude = null
          } else {
            _tmpGeoLatitude = _stmt.getDouble(_columnIndexOfGeoLatitude)
          }
          val _tmpGeoLongitude: Double?
          if (_stmt.isNull(_columnIndexOfGeoLongitude)) {
            _tmpGeoLongitude = null
          } else {
            _tmpGeoLongitude = _stmt.getDouble(_columnIndexOfGeoLongitude)
          }
          val _tmpGeoAddress: String?
          if (_stmt.isNull(_columnIndexOfGeoAddress)) {
            _tmpGeoAddress = null
          } else {
            _tmpGeoAddress = _stmt.getText(_columnIndexOfGeoAddress)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedBy: String?
          if (_stmt.isNull(_columnIndexOfVerifiedBy)) {
            _tmpVerifiedBy = null
          } else {
            _tmpVerifiedBy = _stmt.getText(_columnIndexOfVerifiedBy)
          }
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpVerificationNote: String?
          if (_stmt.isNull(_columnIndexOfVerificationNote)) {
            _tmpVerificationNote = null
          } else {
            _tmpVerificationNote = _stmt.getText(_columnIndexOfVerificationNote)
          }
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_1 != 0
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          _item =
              OrderEvidenceEntity(_tmpEvidenceId,_tmpOrderId,_tmpEvidenceType,_tmpUploadedBy,_tmpUploadedByRole,_tmpImageUri,_tmpVideoUri,_tmpTextContent,_tmpGeoLatitude,_tmpGeoLongitude,_tmpGeoAddress,_tmpIsVerified,_tmpVerifiedBy,_tmpVerifiedAt,_tmpVerificationNote,_tmpDeviceTimestamp,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markVerified(
    evidenceId: String,
    verifiedBy: String,
    verifiedAt: Long,
    note: String?,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE order_evidence SET isVerified = 1, verifiedBy = ?, verifiedAt = ?, verificationNote = ?, updatedAt = ?, dirty = 1 WHERE evidenceId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, verifiedBy)
        _argIndex = 2
        _stmt.bindLong(_argIndex, verifiedAt)
        _argIndex = 3
        if (note == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, note)
        }
        _argIndex = 4
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 5
        _stmt.bindText(_argIndex, evidenceId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun softDelete(evidenceId: String, deletedAt: Long) {
    val _sql: String =
        "UPDATE order_evidence SET isDeleted = 1, updatedAt = ?, dirty = 1 WHERE evidenceId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, deletedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, evidenceId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markClean(evidenceId: String) {
    val _sql: String = "UPDATE order_evidence SET dirty = 0 WHERE evidenceId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, evidenceId)
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
