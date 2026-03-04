package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.RoleUpgradeRequestEntity
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
public class RoleUpgradeRequestDao_Impl(
  __db: RoomDatabase,
) : RoleUpgradeRequestDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRoleUpgradeRequestEntity:
      EntityInsertAdapter<RoleUpgradeRequestEntity>

  private val __updateAdapterOfRoleUpgradeRequestEntity:
      EntityDeleteOrUpdateAdapter<RoleUpgradeRequestEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRoleUpgradeRequestEntity = object :
        EntityInsertAdapter<RoleUpgradeRequestEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `role_upgrade_requests` (`requestId`,`userId`,`currentRole`,`requestedRole`,`status`,`adminNotes`,`reviewedBy`,`reviewedAt`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RoleUpgradeRequestEntity) {
        statement.bindText(1, entity.requestId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.currentRole)
        statement.bindText(4, entity.requestedRole)
        statement.bindText(5, entity.status)
        val _tmpAdminNotes: String? = entity.adminNotes
        if (_tmpAdminNotes == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAdminNotes)
        }
        val _tmpReviewedBy: String? = entity.reviewedBy
        if (_tmpReviewedBy == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpReviewedBy)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReviewedAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
      }
    }
    this.__updateAdapterOfRoleUpgradeRequestEntity = object :
        EntityDeleteOrUpdateAdapter<RoleUpgradeRequestEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `role_upgrade_requests` SET `requestId` = ?,`userId` = ?,`currentRole` = ?,`requestedRole` = ?,`status` = ?,`adminNotes` = ?,`reviewedBy` = ?,`reviewedAt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `requestId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: RoleUpgradeRequestEntity) {
        statement.bindText(1, entity.requestId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.currentRole)
        statement.bindText(4, entity.requestedRole)
        statement.bindText(5, entity.status)
        val _tmpAdminNotes: String? = entity.adminNotes
        if (_tmpAdminNotes == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAdminNotes)
        }
        val _tmpReviewedBy: String? = entity.reviewedBy
        if (_tmpReviewedBy == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpReviewedBy)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpReviewedAt)
        }
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        statement.bindText(11, entity.requestId)
      }
    }
  }

  public override suspend fun insert(request: RoleUpgradeRequestEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRoleUpgradeRequestEntity.insert(_connection, request)
  }

  public override suspend fun update(request: RoleUpgradeRequestEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfRoleUpgradeRequestEntity.handle(_connection, request)
  }

  public override fun observeRequestsByUser(userId: String): Flow<List<RoleUpgradeRequestEntity>> {
    val _sql: String =
        "SELECT * FROM role_upgrade_requests WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("role_upgrade_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCurrentRole: Int = getColumnIndexOrThrow(_stmt, "currentRole")
        val _columnIndexOfRequestedRole: Int = getColumnIndexOrThrow(_stmt, "requestedRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<RoleUpgradeRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RoleUpgradeRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCurrentRole: String
          _tmpCurrentRole = _stmt.getText(_columnIndexOfCurrentRole)
          val _tmpRequestedRole: String
          _tmpRequestedRole = _stmt.getText(_columnIndexOfRequestedRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              RoleUpgradeRequestEntity(_tmpRequestId,_tmpUserId,_tmpCurrentRole,_tmpRequestedRole,_tmpStatus,_tmpAdminNotes,_tmpReviewedBy,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestRequestByUser(userId: String): RoleUpgradeRequestEntity? {
    val _sql: String =
        "SELECT * FROM role_upgrade_requests WHERE userId = ? ORDER BY createdAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCurrentRole: Int = getColumnIndexOrThrow(_stmt, "currentRole")
        val _columnIndexOfRequestedRole: Int = getColumnIndexOrThrow(_stmt, "requestedRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleUpgradeRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCurrentRole: String
          _tmpCurrentRole = _stmt.getText(_columnIndexOfCurrentRole)
          val _tmpRequestedRole: String
          _tmpRequestedRole = _stmt.getText(_columnIndexOfRequestedRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleUpgradeRequestEntity(_tmpRequestId,_tmpUserId,_tmpCurrentRole,_tmpRequestedRole,_tmpStatus,_tmpAdminNotes,_tmpReviewedBy,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPendingRequestByUser(userId: String): RoleUpgradeRequestEntity? {
    val _sql: String =
        "SELECT * FROM role_upgrade_requests WHERE userId = ? AND status = 'PENDING' LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCurrentRole: Int = getColumnIndexOrThrow(_stmt, "currentRole")
        val _columnIndexOfRequestedRole: Int = getColumnIndexOrThrow(_stmt, "requestedRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleUpgradeRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCurrentRole: String
          _tmpCurrentRole = _stmt.getText(_columnIndexOfCurrentRole)
          val _tmpRequestedRole: String
          _tmpRequestedRole = _stmt.getText(_columnIndexOfRequestedRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleUpgradeRequestEntity(_tmpRequestId,_tmpUserId,_tmpCurrentRole,_tmpRequestedRole,_tmpStatus,_tmpAdminNotes,_tmpReviewedBy,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingRequests(): Flow<List<RoleUpgradeRequestEntity>> {
    val _sql: String =
        "SELECT * FROM role_upgrade_requests WHERE status = 'PENDING' ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("role_upgrade_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCurrentRole: Int = getColumnIndexOrThrow(_stmt, "currentRole")
        val _columnIndexOfRequestedRole: Int = getColumnIndexOrThrow(_stmt, "requestedRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<RoleUpgradeRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RoleUpgradeRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCurrentRole: String
          _tmpCurrentRole = _stmt.getText(_columnIndexOfCurrentRole)
          val _tmpRequestedRole: String
          _tmpRequestedRole = _stmt.getText(_columnIndexOfRequestedRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              RoleUpgradeRequestEntity(_tmpRequestId,_tmpUserId,_tmpCurrentRole,_tmpRequestedRole,_tmpStatus,_tmpAdminNotes,_tmpReviewedBy,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRequestById(requestId: String): RoleUpgradeRequestEntity? {
    val _sql: String = "SELECT * FROM role_upgrade_requests WHERE requestId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, requestId)
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCurrentRole: Int = getColumnIndexOrThrow(_stmt, "currentRole")
        val _columnIndexOfRequestedRole: Int = getColumnIndexOrThrow(_stmt, "requestedRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleUpgradeRequestEntity?
        if (_stmt.step()) {
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCurrentRole: String
          _tmpCurrentRole = _stmt.getText(_columnIndexOfCurrentRole)
          val _tmpRequestedRole: String
          _tmpRequestedRole = _stmt.getText(_columnIndexOfRequestedRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleUpgradeRequestEntity(_tmpRequestId,_tmpUserId,_tmpCurrentRole,_tmpRequestedRole,_tmpStatus,_tmpAdminNotes,_tmpReviewedBy,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM role_upgrade_requests WHERE status = 'PENDING'"
    return createFlow(__db, false, arrayOf("role_upgrade_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeProcessedRequests(): Flow<List<RoleUpgradeRequestEntity>> {
    val _sql: String =
        "SELECT * FROM role_upgrade_requests WHERE status IN ('APPROVED', 'REJECTED') ORDER BY reviewedAt DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("role_upgrade_requests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRequestId: Int = getColumnIndexOrThrow(_stmt, "requestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCurrentRole: Int = getColumnIndexOrThrow(_stmt, "currentRole")
        val _columnIndexOfRequestedRole: Int = getColumnIndexOrThrow(_stmt, "requestedRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<RoleUpgradeRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RoleUpgradeRequestEntity
          val _tmpRequestId: String
          _tmpRequestId = _stmt.getText(_columnIndexOfRequestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCurrentRole: String
          _tmpCurrentRole = _stmt.getText(_columnIndexOfCurrentRole)
          val _tmpRequestedRole: String
          _tmpRequestedRole = _stmt.getText(_columnIndexOfRequestedRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              RoleUpgradeRequestEntity(_tmpRequestId,_tmpUserId,_tmpCurrentRole,_tmpRequestedRole,_tmpStatus,_tmpAdminNotes,_tmpReviewedBy,_tmpReviewedAt,_tmpCreatedAt,_tmpUpdatedAt)
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
