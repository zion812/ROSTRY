package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.VerificationDraftEntity
import com.rio.rostry.domain.model.UpgradeType
import javax.`annotation`.processing.Generated
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
public class VerificationDraftDao_Impl(
  __db: RoomDatabase,
) : VerificationDraftDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfVerificationDraftEntity: EntityUpsertAdapter<VerificationDraftEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfVerificationDraftEntity =
        EntityUpsertAdapter<VerificationDraftEntity>(object :
        EntityInsertAdapter<VerificationDraftEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `verification_drafts` (`draftId`,`userId`,`upgradeType`,`farmLocationJson`,`uploadedImagesJson`,`uploadedDocsJson`,`uploadedImageTypesJson`,`uploadedDocTypesJson`,`uploadProgressJson`,`lastSavedAt`,`createdAt`,`updatedAt`,`mergedAt`,`mergedInto`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: VerificationDraftEntity) {
        statement.bindText(1, entity.draftId)
        statement.bindText(2, entity.userId)
        val _tmpUpgradeType: UpgradeType? = entity.upgradeType
        val _tmp: String? = AppDatabase.Converters.fromUpgradeType(_tmpUpgradeType)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        val _tmpFarmLocationJson: String? = entity.farmLocationJson
        if (_tmpFarmLocationJson == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFarmLocationJson)
        }
        val _tmpUploadedImagesJson: String? = entity.uploadedImagesJson
        if (_tmpUploadedImagesJson == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpUploadedImagesJson)
        }
        val _tmpUploadedDocsJson: String? = entity.uploadedDocsJson
        if (_tmpUploadedDocsJson == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpUploadedDocsJson)
        }
        val _tmpUploadedImageTypesJson: String? = entity.uploadedImageTypesJson
        if (_tmpUploadedImageTypesJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpUploadedImageTypesJson)
        }
        val _tmpUploadedDocTypesJson: String? = entity.uploadedDocTypesJson
        if (_tmpUploadedDocTypesJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpUploadedDocTypesJson)
        }
        val _tmpUploadProgressJson: String? = entity.uploadProgressJson
        if (_tmpUploadProgressJson == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpUploadProgressJson)
        }
        statement.bindLong(10, entity.lastSavedAt)
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
        val _tmpMergedAt: Long? = entity.mergedAt
        if (_tmpMergedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpMergedAt)
        }
        val _tmpMergedInto: String? = entity.mergedInto
        if (_tmpMergedInto == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpMergedInto)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<VerificationDraftEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `verification_drafts` SET `draftId` = ?,`userId` = ?,`upgradeType` = ?,`farmLocationJson` = ?,`uploadedImagesJson` = ?,`uploadedDocsJson` = ?,`uploadedImageTypesJson` = ?,`uploadedDocTypesJson` = ?,`uploadProgressJson` = ?,`lastSavedAt` = ?,`createdAt` = ?,`updatedAt` = ?,`mergedAt` = ?,`mergedInto` = ? WHERE `draftId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: VerificationDraftEntity) {
        statement.bindText(1, entity.draftId)
        statement.bindText(2, entity.userId)
        val _tmpUpgradeType: UpgradeType? = entity.upgradeType
        val _tmp: String? = AppDatabase.Converters.fromUpgradeType(_tmpUpgradeType)
        if (_tmp == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmp)
        }
        val _tmpFarmLocationJson: String? = entity.farmLocationJson
        if (_tmpFarmLocationJson == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFarmLocationJson)
        }
        val _tmpUploadedImagesJson: String? = entity.uploadedImagesJson
        if (_tmpUploadedImagesJson == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpUploadedImagesJson)
        }
        val _tmpUploadedDocsJson: String? = entity.uploadedDocsJson
        if (_tmpUploadedDocsJson == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpUploadedDocsJson)
        }
        val _tmpUploadedImageTypesJson: String? = entity.uploadedImageTypesJson
        if (_tmpUploadedImageTypesJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpUploadedImageTypesJson)
        }
        val _tmpUploadedDocTypesJson: String? = entity.uploadedDocTypesJson
        if (_tmpUploadedDocTypesJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpUploadedDocTypesJson)
        }
        val _tmpUploadProgressJson: String? = entity.uploadProgressJson
        if (_tmpUploadProgressJson == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpUploadProgressJson)
        }
        statement.bindLong(10, entity.lastSavedAt)
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
        val _tmpMergedAt: Long? = entity.mergedAt
        if (_tmpMergedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpMergedAt)
        }
        val _tmpMergedInto: String? = entity.mergedInto
        if (_tmpMergedInto == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpMergedInto)
        }
        statement.bindText(15, entity.draftId)
      }
    })
  }

  public override suspend fun upsertDraft(draft: VerificationDraftEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfVerificationDraftEntity.upsert(_connection, draft)
  }

  public override fun observeDraft(userId: String): Flow<VerificationDraftEntity?> {
    val _sql: String = "SELECT * FROM verification_drafts WHERE userId = ?"
    return createFlow(__db, false, arrayOf("verification_drafts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfDraftId: Int = getColumnIndexOrThrow(_stmt, "draftId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfUpgradeType: Int = getColumnIndexOrThrow(_stmt, "upgradeType")
        val _columnIndexOfFarmLocationJson: Int = getColumnIndexOrThrow(_stmt, "farmLocationJson")
        val _columnIndexOfUploadedImagesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedImagesJson")
        val _columnIndexOfUploadedDocsJson: Int = getColumnIndexOrThrow(_stmt, "uploadedDocsJson")
        val _columnIndexOfUploadedImageTypesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedImageTypesJson")
        val _columnIndexOfUploadedDocTypesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedDocTypesJson")
        val _columnIndexOfUploadProgressJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadProgressJson")
        val _columnIndexOfLastSavedAt: Int = getColumnIndexOrThrow(_stmt, "lastSavedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergedInto: Int = getColumnIndexOrThrow(_stmt, "mergedInto")
        val _result: VerificationDraftEntity?
        if (_stmt.step()) {
          val _tmpDraftId: String
          _tmpDraftId = _stmt.getText(_columnIndexOfDraftId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpUpgradeType: UpgradeType?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfUpgradeType)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfUpgradeType)
          }
          _tmpUpgradeType = AppDatabase.Converters.toUpgradeType(_tmp)
          val _tmpFarmLocationJson: String?
          if (_stmt.isNull(_columnIndexOfFarmLocationJson)) {
            _tmpFarmLocationJson = null
          } else {
            _tmpFarmLocationJson = _stmt.getText(_columnIndexOfFarmLocationJson)
          }
          val _tmpUploadedImagesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedImagesJson)) {
            _tmpUploadedImagesJson = null
          } else {
            _tmpUploadedImagesJson = _stmt.getText(_columnIndexOfUploadedImagesJson)
          }
          val _tmpUploadedDocsJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedDocsJson)) {
            _tmpUploadedDocsJson = null
          } else {
            _tmpUploadedDocsJson = _stmt.getText(_columnIndexOfUploadedDocsJson)
          }
          val _tmpUploadedImageTypesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedImageTypesJson)) {
            _tmpUploadedImageTypesJson = null
          } else {
            _tmpUploadedImageTypesJson = _stmt.getText(_columnIndexOfUploadedImageTypesJson)
          }
          val _tmpUploadedDocTypesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedDocTypesJson)) {
            _tmpUploadedDocTypesJson = null
          } else {
            _tmpUploadedDocTypesJson = _stmt.getText(_columnIndexOfUploadedDocTypesJson)
          }
          val _tmpUploadProgressJson: String?
          if (_stmt.isNull(_columnIndexOfUploadProgressJson)) {
            _tmpUploadProgressJson = null
          } else {
            _tmpUploadProgressJson = _stmt.getText(_columnIndexOfUploadProgressJson)
          }
          val _tmpLastSavedAt: Long
          _tmpLastSavedAt = _stmt.getLong(_columnIndexOfLastSavedAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergedInto: String?
          if (_stmt.isNull(_columnIndexOfMergedInto)) {
            _tmpMergedInto = null
          } else {
            _tmpMergedInto = _stmt.getText(_columnIndexOfMergedInto)
          }
          _result =
              VerificationDraftEntity(_tmpDraftId,_tmpUserId,_tmpUpgradeType,_tmpFarmLocationJson,_tmpUploadedImagesJson,_tmpUploadedDocsJson,_tmpUploadedImageTypesJson,_tmpUploadedDocTypesJson,_tmpUploadProgressJson,_tmpLastSavedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpMergedAt,_tmpMergedInto)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDraft(userId: String): VerificationDraftEntity? {
    val _sql: String = "SELECT * FROM verification_drafts WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfDraftId: Int = getColumnIndexOrThrow(_stmt, "draftId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfUpgradeType: Int = getColumnIndexOrThrow(_stmt, "upgradeType")
        val _columnIndexOfFarmLocationJson: Int = getColumnIndexOrThrow(_stmt, "farmLocationJson")
        val _columnIndexOfUploadedImagesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedImagesJson")
        val _columnIndexOfUploadedDocsJson: Int = getColumnIndexOrThrow(_stmt, "uploadedDocsJson")
        val _columnIndexOfUploadedImageTypesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedImageTypesJson")
        val _columnIndexOfUploadedDocTypesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedDocTypesJson")
        val _columnIndexOfUploadProgressJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadProgressJson")
        val _columnIndexOfLastSavedAt: Int = getColumnIndexOrThrow(_stmt, "lastSavedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergedInto: Int = getColumnIndexOrThrow(_stmt, "mergedInto")
        val _result: VerificationDraftEntity?
        if (_stmt.step()) {
          val _tmpDraftId: String
          _tmpDraftId = _stmt.getText(_columnIndexOfDraftId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpUpgradeType: UpgradeType?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfUpgradeType)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfUpgradeType)
          }
          _tmpUpgradeType = AppDatabase.Converters.toUpgradeType(_tmp)
          val _tmpFarmLocationJson: String?
          if (_stmt.isNull(_columnIndexOfFarmLocationJson)) {
            _tmpFarmLocationJson = null
          } else {
            _tmpFarmLocationJson = _stmt.getText(_columnIndexOfFarmLocationJson)
          }
          val _tmpUploadedImagesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedImagesJson)) {
            _tmpUploadedImagesJson = null
          } else {
            _tmpUploadedImagesJson = _stmt.getText(_columnIndexOfUploadedImagesJson)
          }
          val _tmpUploadedDocsJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedDocsJson)) {
            _tmpUploadedDocsJson = null
          } else {
            _tmpUploadedDocsJson = _stmt.getText(_columnIndexOfUploadedDocsJson)
          }
          val _tmpUploadedImageTypesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedImageTypesJson)) {
            _tmpUploadedImageTypesJson = null
          } else {
            _tmpUploadedImageTypesJson = _stmt.getText(_columnIndexOfUploadedImageTypesJson)
          }
          val _tmpUploadedDocTypesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedDocTypesJson)) {
            _tmpUploadedDocTypesJson = null
          } else {
            _tmpUploadedDocTypesJson = _stmt.getText(_columnIndexOfUploadedDocTypesJson)
          }
          val _tmpUploadProgressJson: String?
          if (_stmt.isNull(_columnIndexOfUploadProgressJson)) {
            _tmpUploadProgressJson = null
          } else {
            _tmpUploadProgressJson = _stmt.getText(_columnIndexOfUploadProgressJson)
          }
          val _tmpLastSavedAt: Long
          _tmpLastSavedAt = _stmt.getLong(_columnIndexOfLastSavedAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergedInto: String?
          if (_stmt.isNull(_columnIndexOfMergedInto)) {
            _tmpMergedInto = null
          } else {
            _tmpMergedInto = _stmt.getText(_columnIndexOfMergedInto)
          }
          _result =
              VerificationDraftEntity(_tmpDraftId,_tmpUserId,_tmpUpgradeType,_tmpFarmLocationJson,_tmpUploadedImagesJson,_tmpUploadedDocsJson,_tmpUploadedImageTypesJson,_tmpUploadedDocTypesJson,_tmpUploadProgressJson,_tmpLastSavedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpMergedAt,_tmpMergedInto)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOldDrafts(cutoffTime: Long): List<VerificationDraftEntity> {
    val _sql: String = "SELECT * FROM verification_drafts WHERE lastSavedAt < ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoffTime)
        val _columnIndexOfDraftId: Int = getColumnIndexOrThrow(_stmt, "draftId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfUpgradeType: Int = getColumnIndexOrThrow(_stmt, "upgradeType")
        val _columnIndexOfFarmLocationJson: Int = getColumnIndexOrThrow(_stmt, "farmLocationJson")
        val _columnIndexOfUploadedImagesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedImagesJson")
        val _columnIndexOfUploadedDocsJson: Int = getColumnIndexOrThrow(_stmt, "uploadedDocsJson")
        val _columnIndexOfUploadedImageTypesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedImageTypesJson")
        val _columnIndexOfUploadedDocTypesJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadedDocTypesJson")
        val _columnIndexOfUploadProgressJson: Int = getColumnIndexOrThrow(_stmt,
            "uploadProgressJson")
        val _columnIndexOfLastSavedAt: Int = getColumnIndexOrThrow(_stmt, "lastSavedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergedInto: Int = getColumnIndexOrThrow(_stmt, "mergedInto")
        val _result: MutableList<VerificationDraftEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VerificationDraftEntity
          val _tmpDraftId: String
          _tmpDraftId = _stmt.getText(_columnIndexOfDraftId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpUpgradeType: UpgradeType?
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfUpgradeType)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfUpgradeType)
          }
          _tmpUpgradeType = AppDatabase.Converters.toUpgradeType(_tmp)
          val _tmpFarmLocationJson: String?
          if (_stmt.isNull(_columnIndexOfFarmLocationJson)) {
            _tmpFarmLocationJson = null
          } else {
            _tmpFarmLocationJson = _stmt.getText(_columnIndexOfFarmLocationJson)
          }
          val _tmpUploadedImagesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedImagesJson)) {
            _tmpUploadedImagesJson = null
          } else {
            _tmpUploadedImagesJson = _stmt.getText(_columnIndexOfUploadedImagesJson)
          }
          val _tmpUploadedDocsJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedDocsJson)) {
            _tmpUploadedDocsJson = null
          } else {
            _tmpUploadedDocsJson = _stmt.getText(_columnIndexOfUploadedDocsJson)
          }
          val _tmpUploadedImageTypesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedImageTypesJson)) {
            _tmpUploadedImageTypesJson = null
          } else {
            _tmpUploadedImageTypesJson = _stmt.getText(_columnIndexOfUploadedImageTypesJson)
          }
          val _tmpUploadedDocTypesJson: String?
          if (_stmt.isNull(_columnIndexOfUploadedDocTypesJson)) {
            _tmpUploadedDocTypesJson = null
          } else {
            _tmpUploadedDocTypesJson = _stmt.getText(_columnIndexOfUploadedDocTypesJson)
          }
          val _tmpUploadProgressJson: String?
          if (_stmt.isNull(_columnIndexOfUploadProgressJson)) {
            _tmpUploadProgressJson = null
          } else {
            _tmpUploadProgressJson = _stmt.getText(_columnIndexOfUploadProgressJson)
          }
          val _tmpLastSavedAt: Long
          _tmpLastSavedAt = _stmt.getLong(_columnIndexOfLastSavedAt)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergedInto: String?
          if (_stmt.isNull(_columnIndexOfMergedInto)) {
            _tmpMergedInto = null
          } else {
            _tmpMergedInto = _stmt.getText(_columnIndexOfMergedInto)
          }
          _item =
              VerificationDraftEntity(_tmpDraftId,_tmpUserId,_tmpUpgradeType,_tmpFarmLocationJson,_tmpUploadedImagesJson,_tmpUploadedDocsJson,_tmpUploadedImageTypesJson,_tmpUploadedDocTypesJson,_tmpUploadProgressJson,_tmpLastSavedAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpMergedAt,_tmpMergedInto)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteDraft(userId: String) {
    val _sql: String = "DELETE FROM verification_drafts WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
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
