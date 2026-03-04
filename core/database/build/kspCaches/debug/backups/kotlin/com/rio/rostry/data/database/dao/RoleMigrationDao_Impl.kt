package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.RoleMigrationEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RoleMigrationDao_Impl(
  __db: RoomDatabase,
) : RoleMigrationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRoleMigrationEntity: EntityInsertAdapter<RoleMigrationEntity>

  private val __deleteAdapterOfRoleMigrationEntity: EntityDeleteOrUpdateAdapter<RoleMigrationEntity>

  private val __updateAdapterOfRoleMigrationEntity: EntityDeleteOrUpdateAdapter<RoleMigrationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRoleMigrationEntity = object : EntityInsertAdapter<RoleMigrationEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `role_migrations` (`migrationId`,`userId`,`fromRole`,`toRole`,`status`,`totalItems`,`migratedItems`,`currentPhase`,`currentEntity`,`startedAt`,`completedAt`,`pausedAt`,`lastProgressAt`,`errorMessage`,`retryCount`,`maxRetries`,`snapshotPath`,`metadataJson`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RoleMigrationEntity) {
        statement.bindText(1, entity.migrationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.fromRole)
        statement.bindText(4, entity.toRole)
        statement.bindText(5, entity.status)
        statement.bindLong(6, entity.totalItems.toLong())
        statement.bindLong(7, entity.migratedItems.toLong())
        val _tmpCurrentPhase: String? = entity.currentPhase
        if (_tmpCurrentPhase == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpCurrentPhase)
        }
        val _tmpCurrentEntity: String? = entity.currentEntity
        if (_tmpCurrentEntity == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpCurrentEntity)
        }
        val _tmpStartedAt: Long? = entity.startedAt
        if (_tmpStartedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpStartedAt)
        }
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpCompletedAt)
        }
        val _tmpPausedAt: Long? = entity.pausedAt
        if (_tmpPausedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpPausedAt)
        }
        val _tmpLastProgressAt: Long? = entity.lastProgressAt
        if (_tmpLastProgressAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpLastProgressAt)
        }
        val _tmpErrorMessage: String? = entity.errorMessage
        if (_tmpErrorMessage == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpErrorMessage)
        }
        statement.bindLong(15, entity.retryCount.toLong())
        statement.bindLong(16, entity.maxRetries.toLong())
        val _tmpSnapshotPath: String? = entity.snapshotPath
        if (_tmpSnapshotPath == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpSnapshotPath)
        }
        val _tmpMetadataJson: String? = entity.metadataJson
        if (_tmpMetadataJson == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpMetadataJson)
        }
        statement.bindLong(19, entity.createdAt)
        statement.bindLong(20, entity.updatedAt)
      }
    }
    this.__deleteAdapterOfRoleMigrationEntity = object :
        EntityDeleteOrUpdateAdapter<RoleMigrationEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `role_migrations` WHERE `migrationId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: RoleMigrationEntity) {
        statement.bindText(1, entity.migrationId)
      }
    }
    this.__updateAdapterOfRoleMigrationEntity = object :
        EntityDeleteOrUpdateAdapter<RoleMigrationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `role_migrations` SET `migrationId` = ?,`userId` = ?,`fromRole` = ?,`toRole` = ?,`status` = ?,`totalItems` = ?,`migratedItems` = ?,`currentPhase` = ?,`currentEntity` = ?,`startedAt` = ?,`completedAt` = ?,`pausedAt` = ?,`lastProgressAt` = ?,`errorMessage` = ?,`retryCount` = ?,`maxRetries` = ?,`snapshotPath` = ?,`metadataJson` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `migrationId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: RoleMigrationEntity) {
        statement.bindText(1, entity.migrationId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.fromRole)
        statement.bindText(4, entity.toRole)
        statement.bindText(5, entity.status)
        statement.bindLong(6, entity.totalItems.toLong())
        statement.bindLong(7, entity.migratedItems.toLong())
        val _tmpCurrentPhase: String? = entity.currentPhase
        if (_tmpCurrentPhase == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpCurrentPhase)
        }
        val _tmpCurrentEntity: String? = entity.currentEntity
        if (_tmpCurrentEntity == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpCurrentEntity)
        }
        val _tmpStartedAt: Long? = entity.startedAt
        if (_tmpStartedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpStartedAt)
        }
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpCompletedAt)
        }
        val _tmpPausedAt: Long? = entity.pausedAt
        if (_tmpPausedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpPausedAt)
        }
        val _tmpLastProgressAt: Long? = entity.lastProgressAt
        if (_tmpLastProgressAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpLastProgressAt)
        }
        val _tmpErrorMessage: String? = entity.errorMessage
        if (_tmpErrorMessage == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpErrorMessage)
        }
        statement.bindLong(15, entity.retryCount.toLong())
        statement.bindLong(16, entity.maxRetries.toLong())
        val _tmpSnapshotPath: String? = entity.snapshotPath
        if (_tmpSnapshotPath == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpSnapshotPath)
        }
        val _tmpMetadataJson: String? = entity.metadataJson
        if (_tmpMetadataJson == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpMetadataJson)
        }
        statement.bindLong(19, entity.createdAt)
        statement.bindLong(20, entity.updatedAt)
        statement.bindText(21, entity.migrationId)
      }
    }
  }

  public override suspend fun insert(migration: RoleMigrationEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfRoleMigrationEntity.insert(_connection, migration)
  }

  public override suspend fun delete(migration: RoleMigrationEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfRoleMigrationEntity.handle(_connection, migration)
  }

  public override suspend fun update(migration: RoleMigrationEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfRoleMigrationEntity.handle(_connection, migration)
  }

  public override suspend fun getById(migrationId: String): RoleMigrationEntity? {
    val _sql: String = "SELECT * FROM role_migrations WHERE migrationId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, migrationId)
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleMigrationEntity?
        if (_stmt.step()) {
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(migrationId: String): Flow<RoleMigrationEntity?> {
    val _sql: String = "SELECT * FROM role_migrations WHERE migrationId = ?"
    return createFlow(__db, false, arrayOf("role_migrations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, migrationId)
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleMigrationEntity?
        if (_stmt.step()) {
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestForUser(userId: String): RoleMigrationEntity? {
    val _sql: String =
        "SELECT * FROM role_migrations WHERE userId = ? ORDER BY createdAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleMigrationEntity?
        if (_stmt.step()) {
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeLatestForUser(userId: String): Flow<RoleMigrationEntity?> {
    val _sql: String =
        "SELECT * FROM role_migrations WHERE userId = ? ORDER BY createdAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("role_migrations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: RoleMigrationEntity?
        if (_stmt.step()) {
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByUserAndStatuses(userId: String, statuses: List<String>):
      List<RoleMigrationEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM role_migrations WHERE userId = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND status IN (")
    val _inputSize: Int = statuses.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        for (_item: String in statuses) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<RoleMigrationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: RoleMigrationEntity
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item_1 =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActiveMigrations(): List<RoleMigrationEntity> {
    val _sql: String =
        "SELECT * FROM role_migrations WHERE status IN ('PENDING', 'IN_PROGRESS', 'PAUSED')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<RoleMigrationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RoleMigrationEntity
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActiveMigrations(): Flow<List<RoleMigrationEntity>> {
    val _sql: String =
        "SELECT * FROM role_migrations WHERE status IN ('PENDING', 'IN_PROGRESS', 'PAUSED')"
    return createFlow(__db, false, arrayOf("role_migrations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfMigrationId: Int = getColumnIndexOrThrow(_stmt, "migrationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFromRole: Int = getColumnIndexOrThrow(_stmt, "fromRole")
        val _columnIndexOfToRole: Int = getColumnIndexOrThrow(_stmt, "toRole")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfMigratedItems: Int = getColumnIndexOrThrow(_stmt, "migratedItems")
        val _columnIndexOfCurrentPhase: Int = getColumnIndexOrThrow(_stmt, "currentPhase")
        val _columnIndexOfCurrentEntity: Int = getColumnIndexOrThrow(_stmt, "currentEntity")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfPausedAt: Int = getColumnIndexOrThrow(_stmt, "pausedAt")
        val _columnIndexOfLastProgressAt: Int = getColumnIndexOrThrow(_stmt, "lastProgressAt")
        val _columnIndexOfErrorMessage: Int = getColumnIndexOrThrow(_stmt, "errorMessage")
        val _columnIndexOfRetryCount: Int = getColumnIndexOrThrow(_stmt, "retryCount")
        val _columnIndexOfMaxRetries: Int = getColumnIndexOrThrow(_stmt, "maxRetries")
        val _columnIndexOfSnapshotPath: Int = getColumnIndexOrThrow(_stmt, "snapshotPath")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<RoleMigrationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RoleMigrationEntity
          val _tmpMigrationId: String
          _tmpMigrationId = _stmt.getText(_columnIndexOfMigrationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFromRole: String
          _tmpFromRole = _stmt.getText(_columnIndexOfFromRole)
          val _tmpToRole: String
          _tmpToRole = _stmt.getText(_columnIndexOfToRole)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpMigratedItems: Int
          _tmpMigratedItems = _stmt.getLong(_columnIndexOfMigratedItems).toInt()
          val _tmpCurrentPhase: String?
          if (_stmt.isNull(_columnIndexOfCurrentPhase)) {
            _tmpCurrentPhase = null
          } else {
            _tmpCurrentPhase = _stmt.getText(_columnIndexOfCurrentPhase)
          }
          val _tmpCurrentEntity: String?
          if (_stmt.isNull(_columnIndexOfCurrentEntity)) {
            _tmpCurrentEntity = null
          } else {
            _tmpCurrentEntity = _stmt.getText(_columnIndexOfCurrentEntity)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpPausedAt: Long?
          if (_stmt.isNull(_columnIndexOfPausedAt)) {
            _tmpPausedAt = null
          } else {
            _tmpPausedAt = _stmt.getLong(_columnIndexOfPausedAt)
          }
          val _tmpLastProgressAt: Long?
          if (_stmt.isNull(_columnIndexOfLastProgressAt)) {
            _tmpLastProgressAt = null
          } else {
            _tmpLastProgressAt = _stmt.getLong(_columnIndexOfLastProgressAt)
          }
          val _tmpErrorMessage: String?
          if (_stmt.isNull(_columnIndexOfErrorMessage)) {
            _tmpErrorMessage = null
          } else {
            _tmpErrorMessage = _stmt.getText(_columnIndexOfErrorMessage)
          }
          val _tmpRetryCount: Int
          _tmpRetryCount = _stmt.getLong(_columnIndexOfRetryCount).toInt()
          val _tmpMaxRetries: Int
          _tmpMaxRetries = _stmt.getLong(_columnIndexOfMaxRetries).toInt()
          val _tmpSnapshotPath: String?
          if (_stmt.isNull(_columnIndexOfSnapshotPath)) {
            _tmpSnapshotPath = null
          } else {
            _tmpSnapshotPath = _stmt.getText(_columnIndexOfSnapshotPath)
          }
          val _tmpMetadataJson: String?
          if (_stmt.isNull(_columnIndexOfMetadataJson)) {
            _tmpMetadataJson = null
          } else {
            _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              RoleMigrationEntity(_tmpMigrationId,_tmpUserId,_tmpFromRole,_tmpToRole,_tmpStatus,_tmpTotalItems,_tmpMigratedItems,_tmpCurrentPhase,_tmpCurrentEntity,_tmpStartedAt,_tmpCompletedAt,_tmpPausedAt,_tmpLastProgressAt,_tmpErrorMessage,_tmpRetryCount,_tmpMaxRetries,_tmpSnapshotPath,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hasActiveMigration(userId: String): Boolean {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM role_migrations WHERE userId = ? AND status IN ('PENDING', 'IN_PROGRESS', 'PAUSED'))"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeHasActiveMigration(userId: String): Flow<Boolean> {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM role_migrations WHERE userId = ? AND status IN ('PENDING', 'IN_PROGRESS', 'PAUSED'))"
    return createFlow(__db, false, arrayOf("role_migrations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCompletedMigrationCount(userId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM role_migrations WHERE userId = ? AND status = 'COMPLETED'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
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

  public override suspend fun updateProgress(
    migrationId: String,
    status: String,
    migratedItems: Int,
    phase: String?,
    entity: String?,
    timestamp: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE role_migrations 
        |        SET status = ?, 
        |            migratedItems = ?, 
        |            currentPhase = ?,
        |            currentEntity = ?,
        |            lastProgressAt = ?,
        |            updatedAt = ?
        |        WHERE migrationId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, migratedItems.toLong())
        _argIndex = 3
        if (phase == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, phase)
        }
        _argIndex = 4
        if (entity == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, entity)
        }
        _argIndex = 5
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 6
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 7
        _stmt.bindText(_argIndex, migrationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMetadata(
    migrationId: String,
    metadataJson: String,
    timestamp: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE role_migrations 
        |        SET metadataJson = ?,
        |            updatedAt = ?
        |        WHERE migrationId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, metadataJson)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, migrationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    migrationId: String,
    status: String,
    errorMessage: String?,
    timestamp: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE role_migrations 
        |        SET status = ?, 
        |            errorMessage = ?,
        |            updatedAt = ?
        |        WHERE migrationId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        if (errorMessage == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, errorMessage)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 4
        _stmt.bindText(_argIndex, migrationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCompletedBefore(beforeTimestamp: Long): Int {
    val _sql: String = "DELETE FROM role_migrations WHERE status = 'COMPLETED' AND completedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, beforeTimestamp)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
