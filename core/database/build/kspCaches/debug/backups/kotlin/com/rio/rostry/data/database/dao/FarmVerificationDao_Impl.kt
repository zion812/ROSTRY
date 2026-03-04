package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.FarmVerificationEntity
import com.rio.rostry.domain.model.VerificationStatus
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Float
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
public class FarmVerificationDao_Impl(
  __db: RoomDatabase,
) : FarmVerificationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFarmVerificationEntity: EntityInsertAdapter<FarmVerificationEntity>

  private val __updateAdapterOfFarmVerificationEntity:
      EntityDeleteOrUpdateAdapter<FarmVerificationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFarmVerificationEntity = object :
        EntityInsertAdapter<FarmVerificationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_verifications` (`verificationId`,`farmerId`,`farmLocationLat`,`farmLocationLng`,`farmAddressLine1`,`farmAddressLine2`,`farmCity`,`farmState`,`farmPostalCode`,`farmCountry`,`verificationDocumentUrls`,`gpsAccuracy`,`gpsTimestamp`,`status`,`submittedAt`,`reviewedAt`,`reviewedBy`,`rejectionReason`,`notes`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.farmerId)
        val _tmpFarmLocationLat: Double? = entity.farmLocationLat
        if (_tmpFarmLocationLat == null) {
          statement.bindNull(3)
        } else {
          statement.bindDouble(3, _tmpFarmLocationLat)
        }
        val _tmpFarmLocationLng: Double? = entity.farmLocationLng
        if (_tmpFarmLocationLng == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpFarmLocationLng)
        }
        val _tmpFarmAddressLine1: String? = entity.farmAddressLine1
        if (_tmpFarmAddressLine1 == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpFarmAddressLine1)
        }
        val _tmpFarmAddressLine2: String? = entity.farmAddressLine2
        if (_tmpFarmAddressLine2 == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpFarmAddressLine2)
        }
        val _tmpFarmCity: String? = entity.farmCity
        if (_tmpFarmCity == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpFarmCity)
        }
        val _tmpFarmState: String? = entity.farmState
        if (_tmpFarmState == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpFarmState)
        }
        val _tmpFarmPostalCode: String? = entity.farmPostalCode
        if (_tmpFarmPostalCode == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpFarmPostalCode)
        }
        val _tmpFarmCountry: String? = entity.farmCountry
        if (_tmpFarmCountry == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmCountry)
        }
        statement.bindText(11, entity.verificationDocumentUrls)
        val _tmpGpsAccuracy: Float? = entity.gpsAccuracy
        if (_tmpGpsAccuracy == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpGpsAccuracy.toDouble())
        }
        val _tmpGpsTimestamp: Long? = entity.gpsTimestamp
        if (_tmpGpsTimestamp == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpGpsTimestamp)
        }
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmp)
        }
        val _tmpSubmittedAt: Long? = entity.submittedAt
        if (_tmpSubmittedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSubmittedAt)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpReviewedAt)
        }
        val _tmpReviewedBy: String? = entity.reviewedBy
        if (_tmpReviewedBy == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpReviewedBy)
        }
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpRejectionReason)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpNotes)
        }
        statement.bindLong(20, entity.createdAt)
        statement.bindLong(21, entity.updatedAt)
      }
    }
    this.__updateAdapterOfFarmVerificationEntity = object :
        EntityDeleteOrUpdateAdapter<FarmVerificationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `farm_verifications` SET `verificationId` = ?,`farmerId` = ?,`farmLocationLat` = ?,`farmLocationLng` = ?,`farmAddressLine1` = ?,`farmAddressLine2` = ?,`farmCity` = ?,`farmState` = ?,`farmPostalCode` = ?,`farmCountry` = ?,`verificationDocumentUrls` = ?,`gpsAccuracy` = ?,`gpsTimestamp` = ?,`status` = ?,`submittedAt` = ?,`reviewedAt` = ?,`reviewedBy` = ?,`rejectionReason` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `verificationId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.farmerId)
        val _tmpFarmLocationLat: Double? = entity.farmLocationLat
        if (_tmpFarmLocationLat == null) {
          statement.bindNull(3)
        } else {
          statement.bindDouble(3, _tmpFarmLocationLat)
        }
        val _tmpFarmLocationLng: Double? = entity.farmLocationLng
        if (_tmpFarmLocationLng == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpFarmLocationLng)
        }
        val _tmpFarmAddressLine1: String? = entity.farmAddressLine1
        if (_tmpFarmAddressLine1 == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpFarmAddressLine1)
        }
        val _tmpFarmAddressLine2: String? = entity.farmAddressLine2
        if (_tmpFarmAddressLine2 == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpFarmAddressLine2)
        }
        val _tmpFarmCity: String? = entity.farmCity
        if (_tmpFarmCity == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpFarmCity)
        }
        val _tmpFarmState: String? = entity.farmState
        if (_tmpFarmState == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpFarmState)
        }
        val _tmpFarmPostalCode: String? = entity.farmPostalCode
        if (_tmpFarmPostalCode == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpFarmPostalCode)
        }
        val _tmpFarmCountry: String? = entity.farmCountry
        if (_tmpFarmCountry == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmCountry)
        }
        statement.bindText(11, entity.verificationDocumentUrls)
        val _tmpGpsAccuracy: Float? = entity.gpsAccuracy
        if (_tmpGpsAccuracy == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpGpsAccuracy.toDouble())
        }
        val _tmpGpsTimestamp: Long? = entity.gpsTimestamp
        if (_tmpGpsTimestamp == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpGpsTimestamp)
        }
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmp)
        }
        val _tmpSubmittedAt: Long? = entity.submittedAt
        if (_tmpSubmittedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSubmittedAt)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpReviewedAt)
        }
        val _tmpReviewedBy: String? = entity.reviewedBy
        if (_tmpReviewedBy == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpReviewedBy)
        }
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpRejectionReason)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpNotes)
        }
        statement.bindLong(20, entity.createdAt)
        statement.bindLong(21, entity.updatedAt)
        statement.bindText(22, entity.verificationId)
      }
    }
  }

  public override suspend fun insertVerification(verification: FarmVerificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFarmVerificationEntity.insert(_connection, verification)
  }

  public override suspend fun updateVerification(verification: FarmVerificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfFarmVerificationEntity.handle(_connection, verification)
  }

  public override fun getLatestVerificationForFarmer(farmerId: String):
      Flow<FarmVerificationEntity?> {
    val _sql: String =
        "SELECT * FROM farm_verifications WHERE farmerId = ? ORDER BY createdAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("farm_verifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfGpsAccuracy: Int = getColumnIndexOrThrow(_stmt, "gpsAccuracy")
        val _columnIndexOfGpsTimestamp: Int = getColumnIndexOrThrow(_stmt, "gpsTimestamp")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: FarmVerificationEntity?
        if (_stmt.step()) {
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpGpsAccuracy: Float?
          if (_stmt.isNull(_columnIndexOfGpsAccuracy)) {
            _tmpGpsAccuracy = null
          } else {
            _tmpGpsAccuracy = _stmt.getDouble(_columnIndexOfGpsAccuracy).toFloat()
          }
          val _tmpGpsTimestamp: Long?
          if (_stmt.isNull(_columnIndexOfGpsTimestamp)) {
            _tmpGpsTimestamp = null
          } else {
            _tmpGpsTimestamp = _stmt.getLong(_columnIndexOfGpsTimestamp)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              FarmVerificationEntity(_tmpVerificationId,_tmpFarmerId,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpVerificationDocumentUrls,_tmpGpsAccuracy,_tmpGpsTimestamp,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getVerificationById(verificationId: String): FarmVerificationEntity? {
    val _sql: String = "SELECT * FROM farm_verifications WHERE verificationId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, verificationId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfGpsAccuracy: Int = getColumnIndexOrThrow(_stmt, "gpsAccuracy")
        val _columnIndexOfGpsTimestamp: Int = getColumnIndexOrThrow(_stmt, "gpsTimestamp")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: FarmVerificationEntity?
        if (_stmt.step()) {
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpGpsAccuracy: Float?
          if (_stmt.isNull(_columnIndexOfGpsAccuracy)) {
            _tmpGpsAccuracy = null
          } else {
            _tmpGpsAccuracy = _stmt.getDouble(_columnIndexOfGpsAccuracy).toFloat()
          }
          val _tmpGpsTimestamp: Long?
          if (_stmt.isNull(_columnIndexOfGpsTimestamp)) {
            _tmpGpsTimestamp = null
          } else {
            _tmpGpsTimestamp = _stmt.getLong(_columnIndexOfGpsTimestamp)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              FarmVerificationEntity(_tmpVerificationId,_tmpFarmerId,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpVerificationDocumentUrls,_tmpGpsAccuracy,_tmpGpsTimestamp,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllVerificationsForFarmer(farmerId: String):
      List<FarmVerificationEntity> {
    val _sql: String = "SELECT * FROM farm_verifications WHERE farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfGpsAccuracy: Int = getColumnIndexOrThrow(_stmt, "gpsAccuracy")
        val _columnIndexOfGpsTimestamp: Int = getColumnIndexOrThrow(_stmt, "gpsTimestamp")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<FarmVerificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmVerificationEntity
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpGpsAccuracy: Float?
          if (_stmt.isNull(_columnIndexOfGpsAccuracy)) {
            _tmpGpsAccuracy = null
          } else {
            _tmpGpsAccuracy = _stmt.getDouble(_columnIndexOfGpsAccuracy).toFloat()
          }
          val _tmpGpsTimestamp: Long?
          if (_stmt.isNull(_columnIndexOfGpsTimestamp)) {
            _tmpGpsTimestamp = null
          } else {
            _tmpGpsTimestamp = _stmt.getLong(_columnIndexOfGpsTimestamp)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              FarmVerificationEntity(_tmpVerificationId,_tmpFarmerId,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpVerificationDocumentUrls,_tmpGpsAccuracy,_tmpGpsTimestamp,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt)
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
