package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.TransferVerificationEntity
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
public class TransferVerificationDao_Impl(
  __db: RoomDatabase,
) : TransferVerificationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransferVerificationEntity:
      EntityInsertAdapter<TransferVerificationEntity>

  private val __upsertAdapterOfTransferVerificationEntity:
      EntityUpsertAdapter<TransferVerificationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTransferVerificationEntity = object :
        EntityInsertAdapter<TransferVerificationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `transfer_verifications` (`verificationId`,`transferId`,`step`,`status`,`photoBeforeUrl`,`photoAfterUrl`,`photoBeforeMetaJson`,`photoAfterMetaJson`,`gpsLat`,`gpsLng`,`identityDocType`,`identityDocRef`,`notes`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransferVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.transferId)
        statement.bindText(3, entity.step)
        statement.bindText(4, entity.status)
        val _tmpPhotoBeforeUrl: String? = entity.photoBeforeUrl
        if (_tmpPhotoBeforeUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPhotoBeforeUrl)
        }
        val _tmpPhotoAfterUrl: String? = entity.photoAfterUrl
        if (_tmpPhotoAfterUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPhotoAfterUrl)
        }
        val _tmpPhotoBeforeMetaJson: String? = entity.photoBeforeMetaJson
        if (_tmpPhotoBeforeMetaJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpPhotoBeforeMetaJson)
        }
        val _tmpPhotoAfterMetaJson: String? = entity.photoAfterMetaJson
        if (_tmpPhotoAfterMetaJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpPhotoAfterMetaJson)
        }
        val _tmpGpsLat: Double? = entity.gpsLat
        if (_tmpGpsLat == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpGpsLat)
        }
        val _tmpGpsLng: Double? = entity.gpsLng
        if (_tmpGpsLng == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpGpsLng)
        }
        val _tmpIdentityDocType: String? = entity.identityDocType
        if (_tmpIdentityDocType == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpIdentityDocType)
        }
        val _tmpIdentityDocRef: String? = entity.identityDocRef
        if (_tmpIdentityDocRef == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpIdentityDocRef)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpNotes)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
      }
    }
    this.__upsertAdapterOfTransferVerificationEntity =
        EntityUpsertAdapter<TransferVerificationEntity>(object :
        EntityInsertAdapter<TransferVerificationEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `transfer_verifications` (`verificationId`,`transferId`,`step`,`status`,`photoBeforeUrl`,`photoAfterUrl`,`photoBeforeMetaJson`,`photoAfterMetaJson`,`gpsLat`,`gpsLng`,`identityDocType`,`identityDocRef`,`notes`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TransferVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.transferId)
        statement.bindText(3, entity.step)
        statement.bindText(4, entity.status)
        val _tmpPhotoBeforeUrl: String? = entity.photoBeforeUrl
        if (_tmpPhotoBeforeUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPhotoBeforeUrl)
        }
        val _tmpPhotoAfterUrl: String? = entity.photoAfterUrl
        if (_tmpPhotoAfterUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPhotoAfterUrl)
        }
        val _tmpPhotoBeforeMetaJson: String? = entity.photoBeforeMetaJson
        if (_tmpPhotoBeforeMetaJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpPhotoBeforeMetaJson)
        }
        val _tmpPhotoAfterMetaJson: String? = entity.photoAfterMetaJson
        if (_tmpPhotoAfterMetaJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpPhotoAfterMetaJson)
        }
        val _tmpGpsLat: Double? = entity.gpsLat
        if (_tmpGpsLat == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpGpsLat)
        }
        val _tmpGpsLng: Double? = entity.gpsLng
        if (_tmpGpsLng == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpGpsLng)
        }
        val _tmpIdentityDocType: String? = entity.identityDocType
        if (_tmpIdentityDocType == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpIdentityDocType)
        }
        val _tmpIdentityDocRef: String? = entity.identityDocRef
        if (_tmpIdentityDocRef == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpIdentityDocRef)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpNotes)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<TransferVerificationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `transfer_verifications` SET `verificationId` = ?,`transferId` = ?,`step` = ?,`status` = ?,`photoBeforeUrl` = ?,`photoAfterUrl` = ?,`photoBeforeMetaJson` = ?,`photoAfterMetaJson` = ?,`gpsLat` = ?,`gpsLng` = ?,`identityDocType` = ?,`identityDocRef` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `verificationId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: TransferVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.transferId)
        statement.bindText(3, entity.step)
        statement.bindText(4, entity.status)
        val _tmpPhotoBeforeUrl: String? = entity.photoBeforeUrl
        if (_tmpPhotoBeforeUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPhotoBeforeUrl)
        }
        val _tmpPhotoAfterUrl: String? = entity.photoAfterUrl
        if (_tmpPhotoAfterUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPhotoAfterUrl)
        }
        val _tmpPhotoBeforeMetaJson: String? = entity.photoBeforeMetaJson
        if (_tmpPhotoBeforeMetaJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpPhotoBeforeMetaJson)
        }
        val _tmpPhotoAfterMetaJson: String? = entity.photoAfterMetaJson
        if (_tmpPhotoAfterMetaJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpPhotoAfterMetaJson)
        }
        val _tmpGpsLat: Double? = entity.gpsLat
        if (_tmpGpsLat == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpGpsLat)
        }
        val _tmpGpsLng: Double? = entity.gpsLng
        if (_tmpGpsLng == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpGpsLng)
        }
        val _tmpIdentityDocType: String? = entity.identityDocType
        if (_tmpIdentityDocType == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpIdentityDocType)
        }
        val _tmpIdentityDocRef: String? = entity.identityDocRef
        if (_tmpIdentityDocRef == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpIdentityDocRef)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpNotes)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
        statement.bindText(16, entity.verificationId)
      }
    })
  }

  public override suspend fun insert(entity: TransferVerificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransferVerificationEntity.insert(_connection, entity)
  }

  public override suspend fun upsert(entity: TransferVerificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfTransferVerificationEntity.upsert(_connection, entity)
  }

  public override suspend fun getByTransferId(transferId: String):
      List<TransferVerificationEntity> {
    val _sql: String = "SELECT * FROM transfer_verifications WHERE transferId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfStep: Int = getColumnIndexOrThrow(_stmt, "step")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPhotoBeforeUrl: Int = getColumnIndexOrThrow(_stmt, "photoBeforeUrl")
        val _columnIndexOfPhotoAfterUrl: Int = getColumnIndexOrThrow(_stmt, "photoAfterUrl")
        val _columnIndexOfPhotoBeforeMetaJson: Int = getColumnIndexOrThrow(_stmt,
            "photoBeforeMetaJson")
        val _columnIndexOfPhotoAfterMetaJson: Int = getColumnIndexOrThrow(_stmt,
            "photoAfterMetaJson")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfIdentityDocType: Int = getColumnIndexOrThrow(_stmt, "identityDocType")
        val _columnIndexOfIdentityDocRef: Int = getColumnIndexOrThrow(_stmt, "identityDocRef")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<TransferVerificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferVerificationEntity
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpStep: String
          _tmpStep = _stmt.getText(_columnIndexOfStep)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPhotoBeforeUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoBeforeUrl)) {
            _tmpPhotoBeforeUrl = null
          } else {
            _tmpPhotoBeforeUrl = _stmt.getText(_columnIndexOfPhotoBeforeUrl)
          }
          val _tmpPhotoAfterUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoAfterUrl)) {
            _tmpPhotoAfterUrl = null
          } else {
            _tmpPhotoAfterUrl = _stmt.getText(_columnIndexOfPhotoAfterUrl)
          }
          val _tmpPhotoBeforeMetaJson: String?
          if (_stmt.isNull(_columnIndexOfPhotoBeforeMetaJson)) {
            _tmpPhotoBeforeMetaJson = null
          } else {
            _tmpPhotoBeforeMetaJson = _stmt.getText(_columnIndexOfPhotoBeforeMetaJson)
          }
          val _tmpPhotoAfterMetaJson: String?
          if (_stmt.isNull(_columnIndexOfPhotoAfterMetaJson)) {
            _tmpPhotoAfterMetaJson = null
          } else {
            _tmpPhotoAfterMetaJson = _stmt.getText(_columnIndexOfPhotoAfterMetaJson)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpIdentityDocType: String?
          if (_stmt.isNull(_columnIndexOfIdentityDocType)) {
            _tmpIdentityDocType = null
          } else {
            _tmpIdentityDocType = _stmt.getText(_columnIndexOfIdentityDocType)
          }
          val _tmpIdentityDocRef: String?
          if (_stmt.isNull(_columnIndexOfIdentityDocRef)) {
            _tmpIdentityDocRef = null
          } else {
            _tmpIdentityDocRef = _stmt.getText(_columnIndexOfIdentityDocRef)
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
              TransferVerificationEntity(_tmpVerificationId,_tmpTransferId,_tmpStep,_tmpStatus,_tmpPhotoBeforeUrl,_tmpPhotoAfterUrl,_tmpPhotoBeforeMetaJson,_tmpPhotoAfterMetaJson,_tmpGpsLat,_tmpGpsLng,_tmpIdentityDocType,_tmpIdentityDocRef,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByTransferId(transferId: String):
      Flow<List<TransferVerificationEntity>> {
    val _sql: String = "SELECT * FROM transfer_verifications WHERE transferId = ?"
    return createFlow(__db, false, arrayOf("transfer_verifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfStep: Int = getColumnIndexOrThrow(_stmt, "step")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfPhotoBeforeUrl: Int = getColumnIndexOrThrow(_stmt, "photoBeforeUrl")
        val _columnIndexOfPhotoAfterUrl: Int = getColumnIndexOrThrow(_stmt, "photoAfterUrl")
        val _columnIndexOfPhotoBeforeMetaJson: Int = getColumnIndexOrThrow(_stmt,
            "photoBeforeMetaJson")
        val _columnIndexOfPhotoAfterMetaJson: Int = getColumnIndexOrThrow(_stmt,
            "photoAfterMetaJson")
        val _columnIndexOfGpsLat: Int = getColumnIndexOrThrow(_stmt, "gpsLat")
        val _columnIndexOfGpsLng: Int = getColumnIndexOrThrow(_stmt, "gpsLng")
        val _columnIndexOfIdentityDocType: Int = getColumnIndexOrThrow(_stmt, "identityDocType")
        val _columnIndexOfIdentityDocRef: Int = getColumnIndexOrThrow(_stmt, "identityDocRef")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<TransferVerificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransferVerificationEntity
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpStep: String
          _tmpStep = _stmt.getText(_columnIndexOfStep)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpPhotoBeforeUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoBeforeUrl)) {
            _tmpPhotoBeforeUrl = null
          } else {
            _tmpPhotoBeforeUrl = _stmt.getText(_columnIndexOfPhotoBeforeUrl)
          }
          val _tmpPhotoAfterUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoAfterUrl)) {
            _tmpPhotoAfterUrl = null
          } else {
            _tmpPhotoAfterUrl = _stmt.getText(_columnIndexOfPhotoAfterUrl)
          }
          val _tmpPhotoBeforeMetaJson: String?
          if (_stmt.isNull(_columnIndexOfPhotoBeforeMetaJson)) {
            _tmpPhotoBeforeMetaJson = null
          } else {
            _tmpPhotoBeforeMetaJson = _stmt.getText(_columnIndexOfPhotoBeforeMetaJson)
          }
          val _tmpPhotoAfterMetaJson: String?
          if (_stmt.isNull(_columnIndexOfPhotoAfterMetaJson)) {
            _tmpPhotoAfterMetaJson = null
          } else {
            _tmpPhotoAfterMetaJson = _stmt.getText(_columnIndexOfPhotoAfterMetaJson)
          }
          val _tmpGpsLat: Double?
          if (_stmt.isNull(_columnIndexOfGpsLat)) {
            _tmpGpsLat = null
          } else {
            _tmpGpsLat = _stmt.getDouble(_columnIndexOfGpsLat)
          }
          val _tmpGpsLng: Double?
          if (_stmt.isNull(_columnIndexOfGpsLng)) {
            _tmpGpsLng = null
          } else {
            _tmpGpsLng = _stmt.getDouble(_columnIndexOfGpsLng)
          }
          val _tmpIdentityDocType: String?
          if (_stmt.isNull(_columnIndexOfIdentityDocType)) {
            _tmpIdentityDocType = null
          } else {
            _tmpIdentityDocType = _stmt.getText(_columnIndexOfIdentityDocType)
          }
          val _tmpIdentityDocRef: String?
          if (_stmt.isNull(_columnIndexOfIdentityDocRef)) {
            _tmpIdentityDocRef = null
          } else {
            _tmpIdentityDocRef = _stmt.getText(_columnIndexOfIdentityDocRef)
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
              TransferVerificationEntity(_tmpVerificationId,_tmpTransferId,_tmpStep,_tmpStatus,_tmpPhotoBeforeUrl,_tmpPhotoAfterUrl,_tmpPhotoBeforeMetaJson,_tmpPhotoAfterMetaJson,_tmpGpsLat,_tmpGpsLng,_tmpIdentityDocType,_tmpIdentityDocRef,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt)
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
