package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.DailyBirdLogEntity
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
public class DailyBirdLogDao_Impl(
  __db: RoomDatabase,
) : DailyBirdLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDailyBirdLogEntity: EntityInsertAdapter<DailyBirdLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDailyBirdLogEntity = object : EntityInsertAdapter<DailyBirdLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `daily_bird_logs` (`id`,`birdId`,`date`,`activityType`,`weight`,`feedIntakeGrams`,`notes`,`performanceRating`,`performanceScoreJson`,`mediaUrlsJson`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DailyBirdLogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.birdId)
        statement.bindLong(3, entity.date)
        statement.bindText(4, entity.activityType)
        val _tmpWeight: Double? = entity.weight
        if (_tmpWeight == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpWeight)
        }
        val _tmpFeedIntakeGrams: Double? = entity.feedIntakeGrams
        if (_tmpFeedIntakeGrams == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpFeedIntakeGrams)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNotes)
        }
        val _tmpPerformanceRating: Int? = entity.performanceRating
        if (_tmpPerformanceRating == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpPerformanceRating.toLong())
        }
        val _tmpPerformanceScoreJson: String? = entity.performanceScoreJson
        if (_tmpPerformanceScoreJson == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpPerformanceScoreJson)
        }
        val _tmpMediaUrlsJson: String? = entity.mediaUrlsJson
        if (_tmpMediaUrlsJson == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpMediaUrlsJson)
        }
        statement.bindLong(11, entity.createdAt)
      }
    }
  }

  public override suspend fun insertLog(log: DailyBirdLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDailyBirdLogEntity.insert(_connection, log)
  }

  public override fun getLogsForBird(birdId: String): Flow<List<DailyBirdLogEntity>> {
    val _sql: String = "SELECT * FROM daily_bird_logs WHERE birdId = ? ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("daily_bird_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfFeedIntakeGrams: Int = getColumnIndexOrThrow(_stmt, "feedIntakeGrams")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPerformanceRating: Int = getColumnIndexOrThrow(_stmt, "performanceRating")
        val _columnIndexOfPerformanceScoreJson: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreJson")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<DailyBirdLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyBirdLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpFeedIntakeGrams: Double?
          if (_stmt.isNull(_columnIndexOfFeedIntakeGrams)) {
            _tmpFeedIntakeGrams = null
          } else {
            _tmpFeedIntakeGrams = _stmt.getDouble(_columnIndexOfFeedIntakeGrams)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPerformanceRating: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceRating)) {
            _tmpPerformanceRating = null
          } else {
            _tmpPerformanceRating = _stmt.getLong(_columnIndexOfPerformanceRating).toInt()
          }
          val _tmpPerformanceScoreJson: String?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreJson)) {
            _tmpPerformanceScoreJson = null
          } else {
            _tmpPerformanceScoreJson = _stmt.getText(_columnIndexOfPerformanceScoreJson)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              DailyBirdLogEntity(_tmpId,_tmpBirdId,_tmpDate,_tmpActivityType,_tmpWeight,_tmpFeedIntakeGrams,_tmpNotes,_tmpPerformanceRating,_tmpPerformanceScoreJson,_tmpMediaUrlsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getLogsForBirdInRange(
    birdId: String,
    startDate: Long,
    endDate: Long,
  ): Flow<List<DailyBirdLogEntity>> {
    val _sql: String =
        "SELECT * FROM daily_bird_logs WHERE birdId = ? AND date BETWEEN ? AND ? ORDER BY date ASC"
    return createFlow(__db, false, arrayOf("daily_bird_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfActivityType: Int = getColumnIndexOrThrow(_stmt, "activityType")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfFeedIntakeGrams: Int = getColumnIndexOrThrow(_stmt, "feedIntakeGrams")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfPerformanceRating: Int = getColumnIndexOrThrow(_stmt, "performanceRating")
        val _columnIndexOfPerformanceScoreJson: Int = getColumnIndexOrThrow(_stmt,
            "performanceScoreJson")
        val _columnIndexOfMediaUrlsJson: Int = getColumnIndexOrThrow(_stmt, "mediaUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<DailyBirdLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyBirdLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpActivityType: String
          _tmpActivityType = _stmt.getText(_columnIndexOfActivityType)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpFeedIntakeGrams: Double?
          if (_stmt.isNull(_columnIndexOfFeedIntakeGrams)) {
            _tmpFeedIntakeGrams = null
          } else {
            _tmpFeedIntakeGrams = _stmt.getDouble(_columnIndexOfFeedIntakeGrams)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpPerformanceRating: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceRating)) {
            _tmpPerformanceRating = null
          } else {
            _tmpPerformanceRating = _stmt.getLong(_columnIndexOfPerformanceRating).toInt()
          }
          val _tmpPerformanceScoreJson: String?
          if (_stmt.isNull(_columnIndexOfPerformanceScoreJson)) {
            _tmpPerformanceScoreJson = null
          } else {
            _tmpPerformanceScoreJson = _stmt.getText(_columnIndexOfPerformanceScoreJson)
          }
          val _tmpMediaUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaUrlsJson)) {
            _tmpMediaUrlsJson = null
          } else {
            _tmpMediaUrlsJson = _stmt.getText(_columnIndexOfMediaUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              DailyBirdLogEntity(_tmpId,_tmpBirdId,_tmpDate,_tmpActivityType,_tmpWeight,_tmpFeedIntakeGrams,_tmpNotes,_tmpPerformanceRating,_tmpPerformanceScoreJson,_tmpMediaUrlsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteLog(id: Long) {
    val _sql: String = "DELETE FROM daily_bird_logs WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
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
