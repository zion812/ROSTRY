package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ExpertProfileEntity
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
public class ExpertProfileDao_Impl(
  __db: RoomDatabase,
) : ExpertProfileDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfExpertProfileEntity: EntityUpsertAdapter<ExpertProfileEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfExpertProfileEntity = EntityUpsertAdapter<ExpertProfileEntity>(object :
        EntityInsertAdapter<ExpertProfileEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `expert_profiles` (`userId`,`specialties`,`bio`,`rating`,`totalConsultations`,`availableForBooking`,`hourlyRate`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExpertProfileEntity) {
        statement.bindText(1, entity.userId)
        statement.bindText(2, entity.specialties)
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpBio)
        }
        statement.bindDouble(4, entity.rating)
        statement.bindLong(5, entity.totalConsultations.toLong())
        val _tmp: Int = if (entity.availableForBooking) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpHourlyRate: Double? = entity.hourlyRate
        if (_tmpHourlyRate == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpHourlyRate)
        }
        statement.bindLong(8, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<ExpertProfileEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `expert_profiles` SET `userId` = ?,`specialties` = ?,`bio` = ?,`rating` = ?,`totalConsultations` = ?,`availableForBooking` = ?,`hourlyRate` = ?,`updatedAt` = ? WHERE `userId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ExpertProfileEntity) {
        statement.bindText(1, entity.userId)
        statement.bindText(2, entity.specialties)
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpBio)
        }
        statement.bindDouble(4, entity.rating)
        statement.bindLong(5, entity.totalConsultations.toLong())
        val _tmp: Int = if (entity.availableForBooking) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpHourlyRate: Double? = entity.hourlyRate
        if (_tmpHourlyRate == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpHourlyRate)
        }
        statement.bindLong(8, entity.updatedAt)
        statement.bindText(9, entity.userId)
      }
    })
  }

  public override suspend fun upsert(profile: ExpertProfileEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfExpertProfileEntity.upsert(_connection, profile)
  }

  public override suspend fun getByUserId(userId: String): ExpertProfileEntity? {
    val _sql: String = "SELECT * FROM expert_profiles WHERE userId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfSpecialties: Int = getColumnIndexOrThrow(_stmt, "specialties")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTotalConsultations: Int = getColumnIndexOrThrow(_stmt,
            "totalConsultations")
        val _columnIndexOfAvailableForBooking: Int = getColumnIndexOrThrow(_stmt,
            "availableForBooking")
        val _columnIndexOfHourlyRate: Int = getColumnIndexOrThrow(_stmt, "hourlyRate")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: ExpertProfileEntity?
        if (_stmt.step()) {
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpSpecialties: String
          _tmpSpecialties = _stmt.getText(_columnIndexOfSpecialties)
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpRating: Double
          _tmpRating = _stmt.getDouble(_columnIndexOfRating)
          val _tmpTotalConsultations: Int
          _tmpTotalConsultations = _stmt.getLong(_columnIndexOfTotalConsultations).toInt()
          val _tmpAvailableForBooking: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAvailableForBooking).toInt()
          _tmpAvailableForBooking = _tmp != 0
          val _tmpHourlyRate: Double?
          if (_stmt.isNull(_columnIndexOfHourlyRate)) {
            _tmpHourlyRate = null
          } else {
            _tmpHourlyRate = _stmt.getDouble(_columnIndexOfHourlyRate)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              ExpertProfileEntity(_tmpUserId,_tmpSpecialties,_tmpBio,_tmpRating,_tmpTotalConsultations,_tmpAvailableForBooking,_tmpHourlyRate,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamAvailableExperts(): Flow<List<ExpertProfileEntity>> {
    val _sql: String =
        "SELECT * FROM expert_profiles WHERE availableForBooking = 1 ORDER BY rating DESC"
    return createFlow(__db, false, arrayOf("expert_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfSpecialties: Int = getColumnIndexOrThrow(_stmt, "specialties")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTotalConsultations: Int = getColumnIndexOrThrow(_stmt,
            "totalConsultations")
        val _columnIndexOfAvailableForBooking: Int = getColumnIndexOrThrow(_stmt,
            "availableForBooking")
        val _columnIndexOfHourlyRate: Int = getColumnIndexOrThrow(_stmt, "hourlyRate")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ExpertProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpertProfileEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpSpecialties: String
          _tmpSpecialties = _stmt.getText(_columnIndexOfSpecialties)
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpRating: Double
          _tmpRating = _stmt.getDouble(_columnIndexOfRating)
          val _tmpTotalConsultations: Int
          _tmpTotalConsultations = _stmt.getLong(_columnIndexOfTotalConsultations).toInt()
          val _tmpAvailableForBooking: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAvailableForBooking).toInt()
          _tmpAvailableForBooking = _tmp != 0
          val _tmpHourlyRate: Double?
          if (_stmt.isNull(_columnIndexOfHourlyRate)) {
            _tmpHourlyRate = null
          } else {
            _tmpHourlyRate = _stmt.getDouble(_columnIndexOfHourlyRate)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              ExpertProfileEntity(_tmpUserId,_tmpSpecialties,_tmpBio,_tmpRating,_tmpTotalConsultations,_tmpAvailableForBooking,_tmpHourlyRate,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamBySpecialty(specialty: String): Flow<List<ExpertProfileEntity>> {
    val _sql: String =
        "SELECT * FROM expert_profiles WHERE specialties LIKE '%' || ? || '%' ORDER BY rating DESC"
    return createFlow(__db, false, arrayOf("expert_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, specialty)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfSpecialties: Int = getColumnIndexOrThrow(_stmt, "specialties")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfRating: Int = getColumnIndexOrThrow(_stmt, "rating")
        val _columnIndexOfTotalConsultations: Int = getColumnIndexOrThrow(_stmt,
            "totalConsultations")
        val _columnIndexOfAvailableForBooking: Int = getColumnIndexOrThrow(_stmt,
            "availableForBooking")
        val _columnIndexOfHourlyRate: Int = getColumnIndexOrThrow(_stmt, "hourlyRate")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ExpertProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpertProfileEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpSpecialties: String
          _tmpSpecialties = _stmt.getText(_columnIndexOfSpecialties)
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpRating: Double
          _tmpRating = _stmt.getDouble(_columnIndexOfRating)
          val _tmpTotalConsultations: Int
          _tmpTotalConsultations = _stmt.getLong(_columnIndexOfTotalConsultations).toInt()
          val _tmpAvailableForBooking: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAvailableForBooking).toInt()
          _tmpAvailableForBooking = _tmp != 0
          val _tmpHourlyRate: Double?
          if (_stmt.isNull(_columnIndexOfHourlyRate)) {
            _tmpHourlyRate = null
          } else {
            _tmpHourlyRate = _stmt.getDouble(_columnIndexOfHourlyRate)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              ExpertProfileEntity(_tmpUserId,_tmpSpecialties,_tmpBio,_tmpRating,_tmpTotalConsultations,_tmpAvailableForBooking,_tmpHourlyRate,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStats(
    userId: String,
    rating: Double,
    total: Int,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE expert_profiles SET rating = ?, totalConsultations = ?, updatedAt = ? WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, rating)
        _argIndex = 2
        _stmt.bindLong(_argIndex, total.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
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
