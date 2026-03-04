package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.UploadTaskEntity
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
public class UploadTaskDao_Impl(
  __db: RoomDatabase,
) : UploadTaskDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUploadTaskEntity: EntityInsertAdapter<UploadTaskEntity>

  private val __updateAdapterOfUploadTaskEntity: EntityDeleteOrUpdateAdapter<UploadTaskEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUploadTaskEntity = object : EntityInsertAdapter<UploadTaskEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `upload_tasks` (`taskId`,`localPath`,`remotePath`,`status`,`progress`,`retries`,`createdAt`,`updatedAt`,`error`,`contextJson`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UploadTaskEntity) {
        statement.bindText(1, entity.taskId)
        statement.bindText(2, entity.localPath)
        statement.bindText(3, entity.remotePath)
        statement.bindText(4, entity.status)
        statement.bindLong(5, entity.progress.toLong())
        statement.bindLong(6, entity.retries.toLong())
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmpError: String? = entity.error
        if (_tmpError == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpError)
        }
        val _tmpContextJson: String? = entity.contextJson
        if (_tmpContextJson == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpContextJson)
        }
      }
    }
    this.__updateAdapterOfUploadTaskEntity = object :
        EntityDeleteOrUpdateAdapter<UploadTaskEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `upload_tasks` SET `taskId` = ?,`localPath` = ?,`remotePath` = ?,`status` = ?,`progress` = ?,`retries` = ?,`createdAt` = ?,`updatedAt` = ?,`error` = ?,`contextJson` = ? WHERE `taskId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UploadTaskEntity) {
        statement.bindText(1, entity.taskId)
        statement.bindText(2, entity.localPath)
        statement.bindText(3, entity.remotePath)
        statement.bindText(4, entity.status)
        statement.bindLong(5, entity.progress.toLong())
        statement.bindLong(6, entity.retries.toLong())
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmpError: String? = entity.error
        if (_tmpError == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpError)
        }
        val _tmpContextJson: String? = entity.contextJson
        if (_tmpContextJson == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpContextJson)
        }
        statement.bindText(11, entity.taskId)
      }
    }
  }

  public override suspend fun upsert(task: UploadTaskEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfUploadTaskEntity.insert(_connection, task)
  }

  public override suspend fun update(task: UploadTaskEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfUploadTaskEntity.handle(_connection, task)
  }

  public override suspend fun getPending(limit: Int): List<UploadTaskEntity> {
    val _sql: String =
        "SELECT * FROM upload_tasks WHERE status IN ('QUEUED','UPLOADING') ORDER BY createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<UploadTaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UploadTaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByContext(transferId: String): Flow<List<UploadTaskEntity>> {
    val _sql: String =
        "SELECT * FROM upload_tasks WHERE contextJson LIKE '%' || ? || '%' ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("upload_tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transferId)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<UploadTaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UploadTaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByRemotePath(remotePath: String): UploadTaskEntity? {
    val _sql: String = "SELECT * FROM upload_tasks WHERE remotePath = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, remotePath)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: UploadTaskEntity?
        if (_stmt.step()) {
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _result =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFailedTasks(limit: Int): List<UploadTaskEntity> {
    val _sql: String =
        "SELECT * FROM upload_tasks WHERE status = 'FAILED' AND retries < 3 ORDER BY createdAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<UploadTaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UploadTaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByRemotePath(remotePath: String): Flow<UploadTaskEntity?> {
    val _sql: String = "SELECT * FROM upload_tasks WHERE remotePath = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("upload_tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, remotePath)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: UploadTaskEntity?
        if (_stmt.step()) {
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _result =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSuccessfulByUser(userId: String): List<UploadTaskEntity> {
    val _sql: String =
        "SELECT * FROM upload_tasks WHERE remotePath LIKE '%/' || ? || '/%' AND status = 'SUCCESS'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<UploadTaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UploadTaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getIncompleteByUser(userId: String): List<UploadTaskEntity> {
    val _sql: String =
        "SELECT * FROM upload_tasks WHERE remotePath LIKE '%/' || ? || '/%' AND status IN ('QUEUED','UPLOADING')"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfLocalPath: Int = getColumnIndexOrThrow(_stmt, "localPath")
        val _columnIndexOfRemotePath: Int = getColumnIndexOrThrow(_stmt, "remotePath")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfRetries: Int = getColumnIndexOrThrow(_stmt, "retries")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfError: Int = getColumnIndexOrThrow(_stmt, "error")
        val _columnIndexOfContextJson: Int = getColumnIndexOrThrow(_stmt, "contextJson")
        val _result: MutableList<UploadTaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UploadTaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpLocalPath: String
          _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath)
          val _tmpRemotePath: String
          _tmpRemotePath = _stmt.getText(_columnIndexOfRemotePath)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpRetries: Int
          _tmpRetries = _stmt.getLong(_columnIndexOfRetries).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpError: String?
          if (_stmt.isNull(_columnIndexOfError)) {
            _tmpError = null
          } else {
            _tmpError = _stmt.getText(_columnIndexOfError)
          }
          val _tmpContextJson: String?
          if (_stmt.isNull(_columnIndexOfContextJson)) {
            _tmpContextJson = null
          } else {
            _tmpContextJson = _stmt.getText(_columnIndexOfContextJson)
          }
          _item =
              UploadTaskEntity(_tmpTaskId,_tmpLocalPath,_tmpRemotePath,_tmpStatus,_tmpProgress,_tmpRetries,_tmpCreatedAt,_tmpUpdatedAt,_tmpError,_tmpContextJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePendingCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM upload_tasks WHERE status IN ('QUEUED','UPLOADING')"
    return createFlow(__db, false, arrayOf("upload_tasks")) { _connection ->
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

  public override suspend fun updateProgress(
    taskId: String,
    progress: Int,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE upload_tasks SET progress = ?, status = 'UPLOADING', updatedAt = ? WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, progress.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSuccess(
    taskId: String,
    contextJson: String?,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE upload_tasks SET status = 'SUCCESS', progress = 100, contextJson = ?, updatedAt = ?, error = NULL WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (contextJson == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, contextJson)
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markFailed(
    taskId: String,
    error: String?,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE upload_tasks SET status = 'FAILED', updatedAt = ?, error = ?, retries = retries + 1 WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        if (error == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, error)
        }
        _argIndex = 3
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementRetries(taskId: String, timestamp: Long) {
    val _sql: String =
        "UPDATE upload_tasks SET retries = retries + 1, updatedAt = ? WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 2
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldCompleted(cutoffTime: Long) {
    val _sql: String =
        "DELETE FROM upload_tasks WHERE status IN ('SUCCESS','FAILED') AND updatedAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoffTime)
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
