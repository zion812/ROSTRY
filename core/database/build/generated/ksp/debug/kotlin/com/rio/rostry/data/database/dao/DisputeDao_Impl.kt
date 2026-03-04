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
import com.rio.rostry.`data`.database.entity.DisputeEntity
import com.rio.rostry.`data`.database.entity.DisputeStatus
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
public class DisputeDao_Impl(
  __db: RoomDatabase,
) : DisputeDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDisputeEntity: EntityInsertAdapter<DisputeEntity>

  private val __upsertAdapterOfDisputeEntity: EntityUpsertAdapter<DisputeEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDisputeEntity = object : EntityInsertAdapter<DisputeEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `disputes` (`disputeId`,`transferId`,`reporterId`,`reportedUserId`,`reason`,`description`,`evidenceUrls`,`status`,`resolution`,`resolvedByAdminId`,`createdAt`,`resolvedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DisputeEntity) {
        statement.bindText(1, entity.disputeId)
        statement.bindText(2, entity.transferId)
        statement.bindText(3, entity.reporterId)
        statement.bindText(4, entity.reportedUserId)
        statement.bindText(5, entity.reason)
        statement.bindText(6, entity.description)
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.evidenceUrls)
        if (_tmp == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmp)
        }
        val _tmp_1: String? = AppDatabase.Converters.fromDisputeStatus(entity.status)
        if (_tmp_1 == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmp_1)
        }
        val _tmpResolution: String? = entity.resolution
        if (_tmpResolution == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpResolution)
        }
        val _tmpResolvedByAdminId: String? = entity.resolvedByAdminId
        if (_tmpResolvedByAdminId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpResolvedByAdminId)
        }
        statement.bindLong(11, entity.createdAt)
        val _tmpResolvedAt: Long? = entity.resolvedAt
        if (_tmpResolvedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpResolvedAt)
        }
      }
    }
    this.__upsertAdapterOfDisputeEntity = EntityUpsertAdapter<DisputeEntity>(object :
        EntityInsertAdapter<DisputeEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `disputes` (`disputeId`,`transferId`,`reporterId`,`reportedUserId`,`reason`,`description`,`evidenceUrls`,`status`,`resolution`,`resolvedByAdminId`,`createdAt`,`resolvedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DisputeEntity) {
        statement.bindText(1, entity.disputeId)
        statement.bindText(2, entity.transferId)
        statement.bindText(3, entity.reporterId)
        statement.bindText(4, entity.reportedUserId)
        statement.bindText(5, entity.reason)
        statement.bindText(6, entity.description)
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.evidenceUrls)
        if (_tmp == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmp)
        }
        val _tmp_1: String? = AppDatabase.Converters.fromDisputeStatus(entity.status)
        if (_tmp_1 == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmp_1)
        }
        val _tmpResolution: String? = entity.resolution
        if (_tmpResolution == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpResolution)
        }
        val _tmpResolvedByAdminId: String? = entity.resolvedByAdminId
        if (_tmpResolvedByAdminId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpResolvedByAdminId)
        }
        statement.bindLong(11, entity.createdAt)
        val _tmpResolvedAt: Long? = entity.resolvedAt
        if (_tmpResolvedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpResolvedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<DisputeEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `disputes` SET `disputeId` = ?,`transferId` = ?,`reporterId` = ?,`reportedUserId` = ?,`reason` = ?,`description` = ?,`evidenceUrls` = ?,`status` = ?,`resolution` = ?,`resolvedByAdminId` = ?,`createdAt` = ?,`resolvedAt` = ? WHERE `disputeId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DisputeEntity) {
        statement.bindText(1, entity.disputeId)
        statement.bindText(2, entity.transferId)
        statement.bindText(3, entity.reporterId)
        statement.bindText(4, entity.reportedUserId)
        statement.bindText(5, entity.reason)
        statement.bindText(6, entity.description)
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.evidenceUrls)
        if (_tmp == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmp)
        }
        val _tmp_1: String? = AppDatabase.Converters.fromDisputeStatus(entity.status)
        if (_tmp_1 == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmp_1)
        }
        val _tmpResolution: String? = entity.resolution
        if (_tmpResolution == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpResolution)
        }
        val _tmpResolvedByAdminId: String? = entity.resolvedByAdminId
        if (_tmpResolvedByAdminId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpResolvedByAdminId)
        }
        statement.bindLong(11, entity.createdAt)
        val _tmpResolvedAt: Long? = entity.resolvedAt
        if (_tmpResolvedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpResolvedAt)
        }
        statement.bindText(13, entity.disputeId)
      }
    })
  }

  public override suspend fun insert(entity: DisputeEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfDisputeEntity.insert(_connection, entity)
  }

  public override suspend fun upsert(entity: DisputeEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfDisputeEntity.upsert(_connection, entity)
  }

  public override suspend fun getByTransferId(transferId: String): List<DisputeEntity> {
    val _sql: String = "SELECT * FROM disputes WHERE transferId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfReporterId: Int = getColumnIndexOrThrow(_stmt, "reporterId")
        val _columnIndexOfReportedUserId: Int = getColumnIndexOrThrow(_stmt, "reportedUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfEvidenceUrls: Int = getColumnIndexOrThrow(_stmt, "evidenceUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolution: Int = getColumnIndexOrThrow(_stmt, "resolution")
        val _columnIndexOfResolvedByAdminId: Int = getColumnIndexOrThrow(_stmt, "resolvedByAdminId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _result: MutableList<DisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpReporterId: String
          _tmpReporterId = _stmt.getText(_columnIndexOfReporterId)
          val _tmpReportedUserId: String
          _tmpReportedUserId = _stmt.getText(_columnIndexOfReportedUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpEvidenceUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfEvidenceUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfEvidenceUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpEvidenceUrls = _tmp_1
          }
          val _tmpStatus: DisputeStatus
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_3: DisputeStatus? = AppDatabase.Converters.toDisputeStatus(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'com.rio.rostry.`data`.database.entity.DisputeStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_3
          }
          val _tmpResolution: String?
          if (_stmt.isNull(_columnIndexOfResolution)) {
            _tmpResolution = null
          } else {
            _tmpResolution = _stmt.getText(_columnIndexOfResolution)
          }
          val _tmpResolvedByAdminId: String?
          if (_stmt.isNull(_columnIndexOfResolvedByAdminId)) {
            _tmpResolvedByAdminId = null
          } else {
            _tmpResolvedByAdminId = _stmt.getText(_columnIndexOfResolvedByAdminId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          _item =
              DisputeEntity(_tmpDisputeId,_tmpTransferId,_tmpReporterId,_tmpReportedUserId,_tmpReason,_tmpDescription,_tmpEvidenceUrls,_tmpStatus,_tmpResolution,_tmpResolvedByAdminId,_tmpCreatedAt,_tmpResolvedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(disputeId: String): DisputeEntity? {
    val _sql: String = "SELECT * FROM disputes WHERE disputeId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, disputeId)
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfReporterId: Int = getColumnIndexOrThrow(_stmt, "reporterId")
        val _columnIndexOfReportedUserId: Int = getColumnIndexOrThrow(_stmt, "reportedUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfEvidenceUrls: Int = getColumnIndexOrThrow(_stmt, "evidenceUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolution: Int = getColumnIndexOrThrow(_stmt, "resolution")
        val _columnIndexOfResolvedByAdminId: Int = getColumnIndexOrThrow(_stmt, "resolvedByAdminId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _result: DisputeEntity?
        if (_stmt.step()) {
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpReporterId: String
          _tmpReporterId = _stmt.getText(_columnIndexOfReporterId)
          val _tmpReportedUserId: String
          _tmpReportedUserId = _stmt.getText(_columnIndexOfReportedUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpEvidenceUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfEvidenceUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfEvidenceUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpEvidenceUrls = _tmp_1
          }
          val _tmpStatus: DisputeStatus
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_3: DisputeStatus? = AppDatabase.Converters.toDisputeStatus(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'com.rio.rostry.`data`.database.entity.DisputeStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_3
          }
          val _tmpResolution: String?
          if (_stmt.isNull(_columnIndexOfResolution)) {
            _tmpResolution = null
          } else {
            _tmpResolution = _stmt.getText(_columnIndexOfResolution)
          }
          val _tmpResolvedByAdminId: String?
          if (_stmt.isNull(_columnIndexOfResolvedByAdminId)) {
            _tmpResolvedByAdminId = null
          } else {
            _tmpResolvedByAdminId = _stmt.getText(_columnIndexOfResolvedByAdminId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          _result =
              DisputeEntity(_tmpDisputeId,_tmpTransferId,_tmpReporterId,_tmpReportedUserId,_tmpReason,_tmpDescription,_tmpEvidenceUrls,_tmpStatus,_tmpResolution,_tmpResolvedByAdminId,_tmpCreatedAt,_tmpResolvedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByTransferId(transferId: String): Flow<List<DisputeEntity>> {
    val _sql: String = "SELECT * FROM disputes WHERE transferId = ?"
    return createFlow(__db, false, arrayOf("disputes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfDisputeId: Int = getColumnIndexOrThrow(_stmt, "disputeId")
        val _columnIndexOfTransferId: Int = getColumnIndexOrThrow(_stmt, "transferId")
        val _columnIndexOfReporterId: Int = getColumnIndexOrThrow(_stmt, "reporterId")
        val _columnIndexOfReportedUserId: Int = getColumnIndexOrThrow(_stmt, "reportedUserId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfEvidenceUrls: Int = getColumnIndexOrThrow(_stmt, "evidenceUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfResolution: Int = getColumnIndexOrThrow(_stmt, "resolution")
        val _columnIndexOfResolvedByAdminId: Int = getColumnIndexOrThrow(_stmt, "resolvedByAdminId")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfResolvedAt: Int = getColumnIndexOrThrow(_stmt, "resolvedAt")
        val _result: MutableList<DisputeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DisputeEntity
          val _tmpDisputeId: String
          _tmpDisputeId = _stmt.getText(_columnIndexOfDisputeId)
          val _tmpTransferId: String
          _tmpTransferId = _stmt.getText(_columnIndexOfTransferId)
          val _tmpReporterId: String
          _tmpReporterId = _stmt.getText(_columnIndexOfReporterId)
          val _tmpReportedUserId: String
          _tmpReportedUserId = _stmt.getText(_columnIndexOfReportedUserId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpEvidenceUrls: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfEvidenceUrls)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfEvidenceUrls)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpEvidenceUrls = _tmp_1
          }
          val _tmpStatus: DisputeStatus
          val _tmp_2: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_3: DisputeStatus? = AppDatabase.Converters.toDisputeStatus(_tmp_2)
          if (_tmp_3 == null) {
            error("Expected NON-NULL 'com.rio.rostry.`data`.database.entity.DisputeStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_3
          }
          val _tmpResolution: String?
          if (_stmt.isNull(_columnIndexOfResolution)) {
            _tmpResolution = null
          } else {
            _tmpResolution = _stmt.getText(_columnIndexOfResolution)
          }
          val _tmpResolvedByAdminId: String?
          if (_stmt.isNull(_columnIndexOfResolvedByAdminId)) {
            _tmpResolvedByAdminId = null
          } else {
            _tmpResolvedByAdminId = _stmt.getText(_columnIndexOfResolvedByAdminId)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpResolvedAt: Long?
          if (_stmt.isNull(_columnIndexOfResolvedAt)) {
            _tmpResolvedAt = null
          } else {
            _tmpResolvedAt = _stmt.getLong(_columnIndexOfResolvedAt)
          }
          _item =
              DisputeEntity(_tmpDisputeId,_tmpTransferId,_tmpReporterId,_tmpReportedUserId,_tmpReason,_tmpDescription,_tmpEvidenceUrls,_tmpStatus,_tmpResolution,_tmpResolvedByAdminId,_tmpCreatedAt,_tmpResolvedAt)
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
