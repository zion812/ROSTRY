package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.BreedEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
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
public class BreedDao_Impl(
  __db: RoomDatabase,
) : BreedDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBreedEntity: EntityInsertAdapter<BreedEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBreedEntity = object : EntityInsertAdapter<BreedEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `breeds` (`breedId`,`name`,`description`,`culinaryProfile`,`farmingDifficulty`,`imageUrl`,`tags`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BreedEntity) {
        statement.bindText(1, entity.breedId)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.culinaryProfile)
        statement.bindText(5, entity.farmingDifficulty)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpImageUrl)
        }
        val _tmp: String? = AppDatabase.Converters.fromStringList(entity.tags)
        if (_tmp == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmp)
        }
      }
    }
  }

  public override suspend fun insertBreeds(breeds: List<BreedEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBreedEntity.insert(_connection, breeds)
  }

  public override fun getAllBreeds(): Flow<List<BreedEntity>> {
    val _sql: String = "SELECT * FROM breeds"
    return createFlow(__db, false, arrayOf("breeds")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBreedId: Int = getColumnIndexOrThrow(_stmt, "breedId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCulinaryProfile: Int = getColumnIndexOrThrow(_stmt, "culinaryProfile")
        val _columnIndexOfFarmingDifficulty: Int = getColumnIndexOrThrow(_stmt, "farmingDifficulty")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<BreedEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedEntity
          val _tmpBreedId: String
          _tmpBreedId = _stmt.getText(_columnIndexOfBreedId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCulinaryProfile: String
          _tmpCulinaryProfile = _stmt.getText(_columnIndexOfCulinaryProfile)
          val _tmpFarmingDifficulty: String
          _tmpFarmingDifficulty = _stmt.getText(_columnIndexOfFarmingDifficulty)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          _item =
              BreedEntity(_tmpBreedId,_tmpName,_tmpDescription,_tmpCulinaryProfile,_tmpFarmingDifficulty,_tmpImageUrl,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBreedsByCulinaryProfile(profile: String): Flow<List<BreedEntity>> {
    val _sql: String = "SELECT * FROM breeds WHERE culinaryProfile = ?"
    return createFlow(__db, false, arrayOf("breeds")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, profile)
        val _columnIndexOfBreedId: Int = getColumnIndexOrThrow(_stmt, "breedId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCulinaryProfile: Int = getColumnIndexOrThrow(_stmt, "culinaryProfile")
        val _columnIndexOfFarmingDifficulty: Int = getColumnIndexOrThrow(_stmt, "farmingDifficulty")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<BreedEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedEntity
          val _tmpBreedId: String
          _tmpBreedId = _stmt.getText(_columnIndexOfBreedId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCulinaryProfile: String
          _tmpCulinaryProfile = _stmt.getText(_columnIndexOfCulinaryProfile)
          val _tmpFarmingDifficulty: String
          _tmpFarmingDifficulty = _stmt.getText(_columnIndexOfFarmingDifficulty)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          _item =
              BreedEntity(_tmpBreedId,_tmpName,_tmpDescription,_tmpCulinaryProfile,_tmpFarmingDifficulty,_tmpImageUrl,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBreedsByDifficulty(difficulty: String): Flow<List<BreedEntity>> {
    val _sql: String = "SELECT * FROM breeds WHERE farmingDifficulty = ?"
    return createFlow(__db, false, arrayOf("breeds")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, difficulty)
        val _columnIndexOfBreedId: Int = getColumnIndexOrThrow(_stmt, "breedId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCulinaryProfile: Int = getColumnIndexOrThrow(_stmt, "culinaryProfile")
        val _columnIndexOfFarmingDifficulty: Int = getColumnIndexOrThrow(_stmt, "farmingDifficulty")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<BreedEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedEntity
          val _tmpBreedId: String
          _tmpBreedId = _stmt.getText(_columnIndexOfBreedId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCulinaryProfile: String
          _tmpCulinaryProfile = _stmt.getText(_columnIndexOfCulinaryProfile)
          val _tmpFarmingDifficulty: String
          _tmpFarmingDifficulty = _stmt.getText(_columnIndexOfFarmingDifficulty)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          _item =
              BreedEntity(_tmpBreedId,_tmpName,_tmpDescription,_tmpCulinaryProfile,_tmpFarmingDifficulty,_tmpImageUrl,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBreedById(breedId: String): BreedEntity? {
    val _sql: String = "SELECT * FROM breeds WHERE breedId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, breedId)
        val _columnIndexOfBreedId: Int = getColumnIndexOrThrow(_stmt, "breedId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfCulinaryProfile: Int = getColumnIndexOrThrow(_stmt, "culinaryProfile")
        val _columnIndexOfFarmingDifficulty: Int = getColumnIndexOrThrow(_stmt, "farmingDifficulty")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: BreedEntity?
        if (_stmt.step()) {
          val _tmpBreedId: String
          _tmpBreedId = _stmt.getText(_columnIndexOfBreedId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpCulinaryProfile: String
          _tmpCulinaryProfile = _stmt.getText(_columnIndexOfCulinaryProfile)
          val _tmpFarmingDifficulty: String
          _tmpFarmingDifficulty = _stmt.getText(_columnIndexOfFarmingDifficulty)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpTags: List<String>
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfTags)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfTags)
          }
          val _tmp_1: List<String>? = AppDatabase.Converters.toStringList(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'kotlin.collections.List<kotlin.String>', but it was NULL.")
          } else {
            _tmpTags = _tmp_1
          }
          _result =
              BreedEntity(_tmpBreedId,_tmpName,_tmpDescription,_tmpCulinaryProfile,_tmpFarmingDifficulty,_tmpImageUrl,_tmpTags)
        } else {
          _result = null
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
