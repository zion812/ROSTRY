package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.FarmProfileEntity
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
public class FarmProfileDao_Impl(
  __db: RoomDatabase,
) : FarmProfileDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFarmProfileEntity: EntityInsertAdapter<FarmProfileEntity>

  private val __updateAdapterOfFarmProfileEntity: EntityDeleteOrUpdateAdapter<FarmProfileEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFarmProfileEntity = object : EntityInsertAdapter<FarmProfileEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_profiles` (`farmerId`,`farmName`,`farmBio`,`logoUrl`,`coverPhotoUrl`,`locationName`,`barangay`,`municipality`,`province`,`latitude`,`longitude`,`isVerified`,`verifiedAt`,`memberSince`,`farmEstablished`,`trustScore`,`totalBirdsSold`,`totalOrdersCompleted`,`avgResponseTimeMinutes`,`vaccinationRate`,`returningBuyerRate`,`badgesJson`,`whatsappNumber`,`isWhatsappEnabled`,`isCallEnabled`,`isPublic`,`showLocation`,`showSalesHistory`,`showTimeline`,`shareVaccinationLogs`,`shareSanitationLogs`,`shareFeedLogs`,`shareWeightData`,`shareSalesActivity`,`shareMortalityData`,`shareExpenseData`,`createdAt`,`updatedAt`,`dirty`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmProfileEntity) {
        statement.bindText(1, entity.farmerId)
        statement.bindText(2, entity.farmName)
        val _tmpFarmBio: String? = entity.farmBio
        if (_tmpFarmBio == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpFarmBio)
        }
        val _tmpLogoUrl: String? = entity.logoUrl
        if (_tmpLogoUrl == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLogoUrl)
        }
        val _tmpCoverPhotoUrl: String? = entity.coverPhotoUrl
        if (_tmpCoverPhotoUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCoverPhotoUrl)
        }
        val _tmpLocationName: String? = entity.locationName
        if (_tmpLocationName == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpLocationName)
        }
        val _tmpBarangay: String? = entity.barangay
        if (_tmpBarangay == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpBarangay)
        }
        val _tmpMunicipality: String? = entity.municipality
        if (_tmpMunicipality == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpMunicipality)
        }
        val _tmpProvince: String? = entity.province
        if (_tmpProvince == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpProvince)
        }
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpLongitude)
        }
        val _tmp: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpVerifiedAt: Long? = entity.verifiedAt
        if (_tmpVerifiedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpVerifiedAt)
        }
        statement.bindLong(14, entity.memberSince)
        val _tmpFarmEstablished: Long? = entity.farmEstablished
        if (_tmpFarmEstablished == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpFarmEstablished)
        }
        statement.bindLong(16, entity.trustScore.toLong())
        statement.bindLong(17, entity.totalBirdsSold.toLong())
        statement.bindLong(18, entity.totalOrdersCompleted.toLong())
        val _tmpAvgResponseTimeMinutes: Int? = entity.avgResponseTimeMinutes
        if (_tmpAvgResponseTimeMinutes == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpAvgResponseTimeMinutes.toLong())
        }
        val _tmpVaccinationRate: Int? = entity.vaccinationRate
        if (_tmpVaccinationRate == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpVaccinationRate.toLong())
        }
        val _tmpReturningBuyerRate: Int? = entity.returningBuyerRate
        if (_tmpReturningBuyerRate == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpReturningBuyerRate.toLong())
        }
        statement.bindText(22, entity.badgesJson)
        val _tmpWhatsappNumber: String? = entity.whatsappNumber
        if (_tmpWhatsappNumber == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpWhatsappNumber)
        }
        val _tmp_1: Int = if (entity.isWhatsappEnabled) 1 else 0
        statement.bindLong(24, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.isCallEnabled) 1 else 0
        statement.bindLong(25, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.isPublic) 1 else 0
        statement.bindLong(26, _tmp_3.toLong())
        val _tmp_4: Int = if (entity.showLocation) 1 else 0
        statement.bindLong(27, _tmp_4.toLong())
        val _tmp_5: Int = if (entity.showSalesHistory) 1 else 0
        statement.bindLong(28, _tmp_5.toLong())
        val _tmp_6: Int = if (entity.showTimeline) 1 else 0
        statement.bindLong(29, _tmp_6.toLong())
        val _tmp_7: Int = if (entity.shareVaccinationLogs) 1 else 0
        statement.bindLong(30, _tmp_7.toLong())
        val _tmp_8: Int = if (entity.shareSanitationLogs) 1 else 0
        statement.bindLong(31, _tmp_8.toLong())
        val _tmp_9: Int = if (entity.shareFeedLogs) 1 else 0
        statement.bindLong(32, _tmp_9.toLong())
        val _tmp_10: Int = if (entity.shareWeightData) 1 else 0
        statement.bindLong(33, _tmp_10.toLong())
        val _tmp_11: Int = if (entity.shareSalesActivity) 1 else 0
        statement.bindLong(34, _tmp_11.toLong())
        val _tmp_12: Int = if (entity.shareMortalityData) 1 else 0
        statement.bindLong(35, _tmp_12.toLong())
        val _tmp_13: Int = if (entity.shareExpenseData) 1 else 0
        statement.bindLong(36, _tmp_13.toLong())
        statement.bindLong(37, entity.createdAt)
        statement.bindLong(38, entity.updatedAt)
        val _tmp_14: Int = if (entity.dirty) 1 else 0
        statement.bindLong(39, _tmp_14.toLong())
      }
    }
    this.__updateAdapterOfFarmProfileEntity = object :
        EntityDeleteOrUpdateAdapter<FarmProfileEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `farm_profiles` SET `farmerId` = ?,`farmName` = ?,`farmBio` = ?,`logoUrl` = ?,`coverPhotoUrl` = ?,`locationName` = ?,`barangay` = ?,`municipality` = ?,`province` = ?,`latitude` = ?,`longitude` = ?,`isVerified` = ?,`verifiedAt` = ?,`memberSince` = ?,`farmEstablished` = ?,`trustScore` = ?,`totalBirdsSold` = ?,`totalOrdersCompleted` = ?,`avgResponseTimeMinutes` = ?,`vaccinationRate` = ?,`returningBuyerRate` = ?,`badgesJson` = ?,`whatsappNumber` = ?,`isWhatsappEnabled` = ?,`isCallEnabled` = ?,`isPublic` = ?,`showLocation` = ?,`showSalesHistory` = ?,`showTimeline` = ?,`shareVaccinationLogs` = ?,`shareSanitationLogs` = ?,`shareFeedLogs` = ?,`shareWeightData` = ?,`shareSalesActivity` = ?,`shareMortalityData` = ?,`shareExpenseData` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ? WHERE `farmerId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmProfileEntity) {
        statement.bindText(1, entity.farmerId)
        statement.bindText(2, entity.farmName)
        val _tmpFarmBio: String? = entity.farmBio
        if (_tmpFarmBio == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpFarmBio)
        }
        val _tmpLogoUrl: String? = entity.logoUrl
        if (_tmpLogoUrl == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLogoUrl)
        }
        val _tmpCoverPhotoUrl: String? = entity.coverPhotoUrl
        if (_tmpCoverPhotoUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCoverPhotoUrl)
        }
        val _tmpLocationName: String? = entity.locationName
        if (_tmpLocationName == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpLocationName)
        }
        val _tmpBarangay: String? = entity.barangay
        if (_tmpBarangay == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpBarangay)
        }
        val _tmpMunicipality: String? = entity.municipality
        if (_tmpMunicipality == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpMunicipality)
        }
        val _tmpProvince: String? = entity.province
        if (_tmpProvince == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpProvince)
        }
        val _tmpLatitude: Double? = entity.latitude
        if (_tmpLatitude == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpLatitude)
        }
        val _tmpLongitude: Double? = entity.longitude
        if (_tmpLongitude == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpLongitude)
        }
        val _tmp: Int = if (entity.isVerified) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpVerifiedAt: Long? = entity.verifiedAt
        if (_tmpVerifiedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpVerifiedAt)
        }
        statement.bindLong(14, entity.memberSince)
        val _tmpFarmEstablished: Long? = entity.farmEstablished
        if (_tmpFarmEstablished == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpFarmEstablished)
        }
        statement.bindLong(16, entity.trustScore.toLong())
        statement.bindLong(17, entity.totalBirdsSold.toLong())
        statement.bindLong(18, entity.totalOrdersCompleted.toLong())
        val _tmpAvgResponseTimeMinutes: Int? = entity.avgResponseTimeMinutes
        if (_tmpAvgResponseTimeMinutes == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpAvgResponseTimeMinutes.toLong())
        }
        val _tmpVaccinationRate: Int? = entity.vaccinationRate
        if (_tmpVaccinationRate == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpVaccinationRate.toLong())
        }
        val _tmpReturningBuyerRate: Int? = entity.returningBuyerRate
        if (_tmpReturningBuyerRate == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpReturningBuyerRate.toLong())
        }
        statement.bindText(22, entity.badgesJson)
        val _tmpWhatsappNumber: String? = entity.whatsappNumber
        if (_tmpWhatsappNumber == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpWhatsappNumber)
        }
        val _tmp_1: Int = if (entity.isWhatsappEnabled) 1 else 0
        statement.bindLong(24, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.isCallEnabled) 1 else 0
        statement.bindLong(25, _tmp_2.toLong())
        val _tmp_3: Int = if (entity.isPublic) 1 else 0
        statement.bindLong(26, _tmp_3.toLong())
        val _tmp_4: Int = if (entity.showLocation) 1 else 0
        statement.bindLong(27, _tmp_4.toLong())
        val _tmp_5: Int = if (entity.showSalesHistory) 1 else 0
        statement.bindLong(28, _tmp_5.toLong())
        val _tmp_6: Int = if (entity.showTimeline) 1 else 0
        statement.bindLong(29, _tmp_6.toLong())
        val _tmp_7: Int = if (entity.shareVaccinationLogs) 1 else 0
        statement.bindLong(30, _tmp_7.toLong())
        val _tmp_8: Int = if (entity.shareSanitationLogs) 1 else 0
        statement.bindLong(31, _tmp_8.toLong())
        val _tmp_9: Int = if (entity.shareFeedLogs) 1 else 0
        statement.bindLong(32, _tmp_9.toLong())
        val _tmp_10: Int = if (entity.shareWeightData) 1 else 0
        statement.bindLong(33, _tmp_10.toLong())
        val _tmp_11: Int = if (entity.shareSalesActivity) 1 else 0
        statement.bindLong(34, _tmp_11.toLong())
        val _tmp_12: Int = if (entity.shareMortalityData) 1 else 0
        statement.bindLong(35, _tmp_12.toLong())
        val _tmp_13: Int = if (entity.shareExpenseData) 1 else 0
        statement.bindLong(36, _tmp_13.toLong())
        statement.bindLong(37, entity.createdAt)
        statement.bindLong(38, entity.updatedAt)
        val _tmp_14: Int = if (entity.dirty) 1 else 0
        statement.bindLong(39, _tmp_14.toLong())
        statement.bindText(40, entity.farmerId)
      }
    }
  }

  public override suspend fun upsert(profile: FarmProfileEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFarmProfileEntity.insert(_connection, profile)
  }

  public override suspend fun update(profile: FarmProfileEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfFarmProfileEntity.handle(_connection, profile)
  }

  public override fun observeProfile(farmerId: String): Flow<FarmProfileEntity?> {
    val _sql: String = "SELECT * FROM farm_profiles WHERE farmerId = ?"
    return createFlow(__db, false, arrayOf("farm_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmName: Int = getColumnIndexOrThrow(_stmt, "farmName")
        val _columnIndexOfFarmBio: Int = getColumnIndexOrThrow(_stmt, "farmBio")
        val _columnIndexOfLogoUrl: Int = getColumnIndexOrThrow(_stmt, "logoUrl")
        val _columnIndexOfCoverPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "coverPhotoUrl")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfBarangay: Int = getColumnIndexOrThrow(_stmt, "barangay")
        val _columnIndexOfMunicipality: Int = getColumnIndexOrThrow(_stmt, "municipality")
        val _columnIndexOfProvince: Int = getColumnIndexOrThrow(_stmt, "province")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfMemberSince: Int = getColumnIndexOrThrow(_stmt, "memberSince")
        val _columnIndexOfFarmEstablished: Int = getColumnIndexOrThrow(_stmt, "farmEstablished")
        val _columnIndexOfTrustScore: Int = getColumnIndexOrThrow(_stmt, "trustScore")
        val _columnIndexOfTotalBirdsSold: Int = getColumnIndexOrThrow(_stmt, "totalBirdsSold")
        val _columnIndexOfTotalOrdersCompleted: Int = getColumnIndexOrThrow(_stmt,
            "totalOrdersCompleted")
        val _columnIndexOfAvgResponseTimeMinutes: Int = getColumnIndexOrThrow(_stmt,
            "avgResponseTimeMinutes")
        val _columnIndexOfVaccinationRate: Int = getColumnIndexOrThrow(_stmt, "vaccinationRate")
        val _columnIndexOfReturningBuyerRate: Int = getColumnIndexOrThrow(_stmt,
            "returningBuyerRate")
        val _columnIndexOfBadgesJson: Int = getColumnIndexOrThrow(_stmt, "badgesJson")
        val _columnIndexOfWhatsappNumber: Int = getColumnIndexOrThrow(_stmt, "whatsappNumber")
        val _columnIndexOfIsWhatsappEnabled: Int = getColumnIndexOrThrow(_stmt, "isWhatsappEnabled")
        val _columnIndexOfIsCallEnabled: Int = getColumnIndexOrThrow(_stmt, "isCallEnabled")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfShowLocation: Int = getColumnIndexOrThrow(_stmt, "showLocation")
        val _columnIndexOfShowSalesHistory: Int = getColumnIndexOrThrow(_stmt, "showSalesHistory")
        val _columnIndexOfShowTimeline: Int = getColumnIndexOrThrow(_stmt, "showTimeline")
        val _columnIndexOfShareVaccinationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareVaccinationLogs")
        val _columnIndexOfShareSanitationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareSanitationLogs")
        val _columnIndexOfShareFeedLogs: Int = getColumnIndexOrThrow(_stmt, "shareFeedLogs")
        val _columnIndexOfShareWeightData: Int = getColumnIndexOrThrow(_stmt, "shareWeightData")
        val _columnIndexOfShareSalesActivity: Int = getColumnIndexOrThrow(_stmt,
            "shareSalesActivity")
        val _columnIndexOfShareMortalityData: Int = getColumnIndexOrThrow(_stmt,
            "shareMortalityData")
        val _columnIndexOfShareExpenseData: Int = getColumnIndexOrThrow(_stmt, "shareExpenseData")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: FarmProfileEntity?
        if (_stmt.step()) {
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmName: String
          _tmpFarmName = _stmt.getText(_columnIndexOfFarmName)
          val _tmpFarmBio: String?
          if (_stmt.isNull(_columnIndexOfFarmBio)) {
            _tmpFarmBio = null
          } else {
            _tmpFarmBio = _stmt.getText(_columnIndexOfFarmBio)
          }
          val _tmpLogoUrl: String?
          if (_stmt.isNull(_columnIndexOfLogoUrl)) {
            _tmpLogoUrl = null
          } else {
            _tmpLogoUrl = _stmt.getText(_columnIndexOfLogoUrl)
          }
          val _tmpCoverPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfCoverPhotoUrl)) {
            _tmpCoverPhotoUrl = null
          } else {
            _tmpCoverPhotoUrl = _stmt.getText(_columnIndexOfCoverPhotoUrl)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpBarangay: String?
          if (_stmt.isNull(_columnIndexOfBarangay)) {
            _tmpBarangay = null
          } else {
            _tmpBarangay = _stmt.getText(_columnIndexOfBarangay)
          }
          val _tmpMunicipality: String?
          if (_stmt.isNull(_columnIndexOfMunicipality)) {
            _tmpMunicipality = null
          } else {
            _tmpMunicipality = _stmt.getText(_columnIndexOfMunicipality)
          }
          val _tmpProvince: String?
          if (_stmt.isNull(_columnIndexOfProvince)) {
            _tmpProvince = null
          } else {
            _tmpProvince = _stmt.getText(_columnIndexOfProvince)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpMemberSince: Long
          _tmpMemberSince = _stmt.getLong(_columnIndexOfMemberSince)
          val _tmpFarmEstablished: Long?
          if (_stmt.isNull(_columnIndexOfFarmEstablished)) {
            _tmpFarmEstablished = null
          } else {
            _tmpFarmEstablished = _stmt.getLong(_columnIndexOfFarmEstablished)
          }
          val _tmpTrustScore: Int
          _tmpTrustScore = _stmt.getLong(_columnIndexOfTrustScore).toInt()
          val _tmpTotalBirdsSold: Int
          _tmpTotalBirdsSold = _stmt.getLong(_columnIndexOfTotalBirdsSold).toInt()
          val _tmpTotalOrdersCompleted: Int
          _tmpTotalOrdersCompleted = _stmt.getLong(_columnIndexOfTotalOrdersCompleted).toInt()
          val _tmpAvgResponseTimeMinutes: Int?
          if (_stmt.isNull(_columnIndexOfAvgResponseTimeMinutes)) {
            _tmpAvgResponseTimeMinutes = null
          } else {
            _tmpAvgResponseTimeMinutes = _stmt.getLong(_columnIndexOfAvgResponseTimeMinutes).toInt()
          }
          val _tmpVaccinationRate: Int?
          if (_stmt.isNull(_columnIndexOfVaccinationRate)) {
            _tmpVaccinationRate = null
          } else {
            _tmpVaccinationRate = _stmt.getLong(_columnIndexOfVaccinationRate).toInt()
          }
          val _tmpReturningBuyerRate: Int?
          if (_stmt.isNull(_columnIndexOfReturningBuyerRate)) {
            _tmpReturningBuyerRate = null
          } else {
            _tmpReturningBuyerRate = _stmt.getLong(_columnIndexOfReturningBuyerRate).toInt()
          }
          val _tmpBadgesJson: String
          _tmpBadgesJson = _stmt.getText(_columnIndexOfBadgesJson)
          val _tmpWhatsappNumber: String?
          if (_stmt.isNull(_columnIndexOfWhatsappNumber)) {
            _tmpWhatsappNumber = null
          } else {
            _tmpWhatsappNumber = _stmt.getText(_columnIndexOfWhatsappNumber)
          }
          val _tmpIsWhatsappEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWhatsappEnabled).toInt()
          _tmpIsWhatsappEnabled = _tmp_1 != 0
          val _tmpIsCallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsCallEnabled).toInt()
          _tmpIsCallEnabled = _tmp_2 != 0
          val _tmpIsPublic: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp_3 != 0
          val _tmpShowLocation: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfShowLocation).toInt()
          _tmpShowLocation = _tmp_4 != 0
          val _tmpShowSalesHistory: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfShowSalesHistory).toInt()
          _tmpShowSalesHistory = _tmp_5 != 0
          val _tmpShowTimeline: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfShowTimeline).toInt()
          _tmpShowTimeline = _tmp_6 != 0
          val _tmpShareVaccinationLogs: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfShareVaccinationLogs).toInt()
          _tmpShareVaccinationLogs = _tmp_7 != 0
          val _tmpShareSanitationLogs: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfShareSanitationLogs).toInt()
          _tmpShareSanitationLogs = _tmp_8 != 0
          val _tmpShareFeedLogs: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfShareFeedLogs).toInt()
          _tmpShareFeedLogs = _tmp_9 != 0
          val _tmpShareWeightData: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfShareWeightData).toInt()
          _tmpShareWeightData = _tmp_10 != 0
          val _tmpShareSalesActivity: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfShareSalesActivity).toInt()
          _tmpShareSalesActivity = _tmp_11 != 0
          val _tmpShareMortalityData: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfShareMortalityData).toInt()
          _tmpShareMortalityData = _tmp_12 != 0
          val _tmpShareExpenseData: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfShareExpenseData).toInt()
          _tmpShareExpenseData = _tmp_13 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_14 != 0
          _result =
              FarmProfileEntity(_tmpFarmerId,_tmpFarmName,_tmpFarmBio,_tmpLogoUrl,_tmpCoverPhotoUrl,_tmpLocationName,_tmpBarangay,_tmpMunicipality,_tmpProvince,_tmpLatitude,_tmpLongitude,_tmpIsVerified,_tmpVerifiedAt,_tmpMemberSince,_tmpFarmEstablished,_tmpTrustScore,_tmpTotalBirdsSold,_tmpTotalOrdersCompleted,_tmpAvgResponseTimeMinutes,_tmpVaccinationRate,_tmpReturningBuyerRate,_tmpBadgesJson,_tmpWhatsappNumber,_tmpIsWhatsappEnabled,_tmpIsCallEnabled,_tmpIsPublic,_tmpShowLocation,_tmpShowSalesHistory,_tmpShowTimeline,_tmpShareVaccinationLogs,_tmpShareSanitationLogs,_tmpShareFeedLogs,_tmpShareWeightData,_tmpShareSalesActivity,_tmpShareMortalityData,_tmpShareExpenseData,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(farmerId: String): FarmProfileEntity? {
    val _sql: String = "SELECT * FROM farm_profiles WHERE farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmName: Int = getColumnIndexOrThrow(_stmt, "farmName")
        val _columnIndexOfFarmBio: Int = getColumnIndexOrThrow(_stmt, "farmBio")
        val _columnIndexOfLogoUrl: Int = getColumnIndexOrThrow(_stmt, "logoUrl")
        val _columnIndexOfCoverPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "coverPhotoUrl")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfBarangay: Int = getColumnIndexOrThrow(_stmt, "barangay")
        val _columnIndexOfMunicipality: Int = getColumnIndexOrThrow(_stmt, "municipality")
        val _columnIndexOfProvince: Int = getColumnIndexOrThrow(_stmt, "province")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfMemberSince: Int = getColumnIndexOrThrow(_stmt, "memberSince")
        val _columnIndexOfFarmEstablished: Int = getColumnIndexOrThrow(_stmt, "farmEstablished")
        val _columnIndexOfTrustScore: Int = getColumnIndexOrThrow(_stmt, "trustScore")
        val _columnIndexOfTotalBirdsSold: Int = getColumnIndexOrThrow(_stmt, "totalBirdsSold")
        val _columnIndexOfTotalOrdersCompleted: Int = getColumnIndexOrThrow(_stmt,
            "totalOrdersCompleted")
        val _columnIndexOfAvgResponseTimeMinutes: Int = getColumnIndexOrThrow(_stmt,
            "avgResponseTimeMinutes")
        val _columnIndexOfVaccinationRate: Int = getColumnIndexOrThrow(_stmt, "vaccinationRate")
        val _columnIndexOfReturningBuyerRate: Int = getColumnIndexOrThrow(_stmt,
            "returningBuyerRate")
        val _columnIndexOfBadgesJson: Int = getColumnIndexOrThrow(_stmt, "badgesJson")
        val _columnIndexOfWhatsappNumber: Int = getColumnIndexOrThrow(_stmt, "whatsappNumber")
        val _columnIndexOfIsWhatsappEnabled: Int = getColumnIndexOrThrow(_stmt, "isWhatsappEnabled")
        val _columnIndexOfIsCallEnabled: Int = getColumnIndexOrThrow(_stmt, "isCallEnabled")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfShowLocation: Int = getColumnIndexOrThrow(_stmt, "showLocation")
        val _columnIndexOfShowSalesHistory: Int = getColumnIndexOrThrow(_stmt, "showSalesHistory")
        val _columnIndexOfShowTimeline: Int = getColumnIndexOrThrow(_stmt, "showTimeline")
        val _columnIndexOfShareVaccinationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareVaccinationLogs")
        val _columnIndexOfShareSanitationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareSanitationLogs")
        val _columnIndexOfShareFeedLogs: Int = getColumnIndexOrThrow(_stmt, "shareFeedLogs")
        val _columnIndexOfShareWeightData: Int = getColumnIndexOrThrow(_stmt, "shareWeightData")
        val _columnIndexOfShareSalesActivity: Int = getColumnIndexOrThrow(_stmt,
            "shareSalesActivity")
        val _columnIndexOfShareMortalityData: Int = getColumnIndexOrThrow(_stmt,
            "shareMortalityData")
        val _columnIndexOfShareExpenseData: Int = getColumnIndexOrThrow(_stmt, "shareExpenseData")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: FarmProfileEntity?
        if (_stmt.step()) {
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmName: String
          _tmpFarmName = _stmt.getText(_columnIndexOfFarmName)
          val _tmpFarmBio: String?
          if (_stmt.isNull(_columnIndexOfFarmBio)) {
            _tmpFarmBio = null
          } else {
            _tmpFarmBio = _stmt.getText(_columnIndexOfFarmBio)
          }
          val _tmpLogoUrl: String?
          if (_stmt.isNull(_columnIndexOfLogoUrl)) {
            _tmpLogoUrl = null
          } else {
            _tmpLogoUrl = _stmt.getText(_columnIndexOfLogoUrl)
          }
          val _tmpCoverPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfCoverPhotoUrl)) {
            _tmpCoverPhotoUrl = null
          } else {
            _tmpCoverPhotoUrl = _stmt.getText(_columnIndexOfCoverPhotoUrl)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpBarangay: String?
          if (_stmt.isNull(_columnIndexOfBarangay)) {
            _tmpBarangay = null
          } else {
            _tmpBarangay = _stmt.getText(_columnIndexOfBarangay)
          }
          val _tmpMunicipality: String?
          if (_stmt.isNull(_columnIndexOfMunicipality)) {
            _tmpMunicipality = null
          } else {
            _tmpMunicipality = _stmt.getText(_columnIndexOfMunicipality)
          }
          val _tmpProvince: String?
          if (_stmt.isNull(_columnIndexOfProvince)) {
            _tmpProvince = null
          } else {
            _tmpProvince = _stmt.getText(_columnIndexOfProvince)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpMemberSince: Long
          _tmpMemberSince = _stmt.getLong(_columnIndexOfMemberSince)
          val _tmpFarmEstablished: Long?
          if (_stmt.isNull(_columnIndexOfFarmEstablished)) {
            _tmpFarmEstablished = null
          } else {
            _tmpFarmEstablished = _stmt.getLong(_columnIndexOfFarmEstablished)
          }
          val _tmpTrustScore: Int
          _tmpTrustScore = _stmt.getLong(_columnIndexOfTrustScore).toInt()
          val _tmpTotalBirdsSold: Int
          _tmpTotalBirdsSold = _stmt.getLong(_columnIndexOfTotalBirdsSold).toInt()
          val _tmpTotalOrdersCompleted: Int
          _tmpTotalOrdersCompleted = _stmt.getLong(_columnIndexOfTotalOrdersCompleted).toInt()
          val _tmpAvgResponseTimeMinutes: Int?
          if (_stmt.isNull(_columnIndexOfAvgResponseTimeMinutes)) {
            _tmpAvgResponseTimeMinutes = null
          } else {
            _tmpAvgResponseTimeMinutes = _stmt.getLong(_columnIndexOfAvgResponseTimeMinutes).toInt()
          }
          val _tmpVaccinationRate: Int?
          if (_stmt.isNull(_columnIndexOfVaccinationRate)) {
            _tmpVaccinationRate = null
          } else {
            _tmpVaccinationRate = _stmt.getLong(_columnIndexOfVaccinationRate).toInt()
          }
          val _tmpReturningBuyerRate: Int?
          if (_stmt.isNull(_columnIndexOfReturningBuyerRate)) {
            _tmpReturningBuyerRate = null
          } else {
            _tmpReturningBuyerRate = _stmt.getLong(_columnIndexOfReturningBuyerRate).toInt()
          }
          val _tmpBadgesJson: String
          _tmpBadgesJson = _stmt.getText(_columnIndexOfBadgesJson)
          val _tmpWhatsappNumber: String?
          if (_stmt.isNull(_columnIndexOfWhatsappNumber)) {
            _tmpWhatsappNumber = null
          } else {
            _tmpWhatsappNumber = _stmt.getText(_columnIndexOfWhatsappNumber)
          }
          val _tmpIsWhatsappEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWhatsappEnabled).toInt()
          _tmpIsWhatsappEnabled = _tmp_1 != 0
          val _tmpIsCallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsCallEnabled).toInt()
          _tmpIsCallEnabled = _tmp_2 != 0
          val _tmpIsPublic: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp_3 != 0
          val _tmpShowLocation: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfShowLocation).toInt()
          _tmpShowLocation = _tmp_4 != 0
          val _tmpShowSalesHistory: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfShowSalesHistory).toInt()
          _tmpShowSalesHistory = _tmp_5 != 0
          val _tmpShowTimeline: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfShowTimeline).toInt()
          _tmpShowTimeline = _tmp_6 != 0
          val _tmpShareVaccinationLogs: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfShareVaccinationLogs).toInt()
          _tmpShareVaccinationLogs = _tmp_7 != 0
          val _tmpShareSanitationLogs: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfShareSanitationLogs).toInt()
          _tmpShareSanitationLogs = _tmp_8 != 0
          val _tmpShareFeedLogs: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfShareFeedLogs).toInt()
          _tmpShareFeedLogs = _tmp_9 != 0
          val _tmpShareWeightData: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfShareWeightData).toInt()
          _tmpShareWeightData = _tmp_10 != 0
          val _tmpShareSalesActivity: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfShareSalesActivity).toInt()
          _tmpShareSalesActivity = _tmp_11 != 0
          val _tmpShareMortalityData: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfShareMortalityData).toInt()
          _tmpShareMortalityData = _tmp_12 != 0
          val _tmpShareExpenseData: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfShareExpenseData).toInt()
          _tmpShareExpenseData = _tmp_13 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_14 != 0
          _result =
              FarmProfileEntity(_tmpFarmerId,_tmpFarmName,_tmpFarmBio,_tmpLogoUrl,_tmpCoverPhotoUrl,_tmpLocationName,_tmpBarangay,_tmpMunicipality,_tmpProvince,_tmpLatitude,_tmpLongitude,_tmpIsVerified,_tmpVerifiedAt,_tmpMemberSince,_tmpFarmEstablished,_tmpTrustScore,_tmpTotalBirdsSold,_tmpTotalOrdersCompleted,_tmpAvgResponseTimeMinutes,_tmpVaccinationRate,_tmpReturningBuyerRate,_tmpBadgesJson,_tmpWhatsappNumber,_tmpIsWhatsappEnabled,_tmpIsCallEnabled,_tmpIsPublic,_tmpShowLocation,_tmpShowSalesHistory,_tmpShowTimeline,_tmpShareVaccinationLogs,_tmpShareSanitationLogs,_tmpShareFeedLogs,_tmpShareWeightData,_tmpShareSalesActivity,_tmpShareMortalityData,_tmpShareExpenseData,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTopFarms(limit: Int): Flow<List<FarmProfileEntity>> {
    val _sql: String =
        "SELECT * FROM farm_profiles WHERE isPublic = 1 AND isVerified = 1 ORDER BY trustScore DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("farm_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmName: Int = getColumnIndexOrThrow(_stmt, "farmName")
        val _columnIndexOfFarmBio: Int = getColumnIndexOrThrow(_stmt, "farmBio")
        val _columnIndexOfLogoUrl: Int = getColumnIndexOrThrow(_stmt, "logoUrl")
        val _columnIndexOfCoverPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "coverPhotoUrl")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfBarangay: Int = getColumnIndexOrThrow(_stmt, "barangay")
        val _columnIndexOfMunicipality: Int = getColumnIndexOrThrow(_stmt, "municipality")
        val _columnIndexOfProvince: Int = getColumnIndexOrThrow(_stmt, "province")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfMemberSince: Int = getColumnIndexOrThrow(_stmt, "memberSince")
        val _columnIndexOfFarmEstablished: Int = getColumnIndexOrThrow(_stmt, "farmEstablished")
        val _columnIndexOfTrustScore: Int = getColumnIndexOrThrow(_stmt, "trustScore")
        val _columnIndexOfTotalBirdsSold: Int = getColumnIndexOrThrow(_stmt, "totalBirdsSold")
        val _columnIndexOfTotalOrdersCompleted: Int = getColumnIndexOrThrow(_stmt,
            "totalOrdersCompleted")
        val _columnIndexOfAvgResponseTimeMinutes: Int = getColumnIndexOrThrow(_stmt,
            "avgResponseTimeMinutes")
        val _columnIndexOfVaccinationRate: Int = getColumnIndexOrThrow(_stmt, "vaccinationRate")
        val _columnIndexOfReturningBuyerRate: Int = getColumnIndexOrThrow(_stmt,
            "returningBuyerRate")
        val _columnIndexOfBadgesJson: Int = getColumnIndexOrThrow(_stmt, "badgesJson")
        val _columnIndexOfWhatsappNumber: Int = getColumnIndexOrThrow(_stmt, "whatsappNumber")
        val _columnIndexOfIsWhatsappEnabled: Int = getColumnIndexOrThrow(_stmt, "isWhatsappEnabled")
        val _columnIndexOfIsCallEnabled: Int = getColumnIndexOrThrow(_stmt, "isCallEnabled")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfShowLocation: Int = getColumnIndexOrThrow(_stmt, "showLocation")
        val _columnIndexOfShowSalesHistory: Int = getColumnIndexOrThrow(_stmt, "showSalesHistory")
        val _columnIndexOfShowTimeline: Int = getColumnIndexOrThrow(_stmt, "showTimeline")
        val _columnIndexOfShareVaccinationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareVaccinationLogs")
        val _columnIndexOfShareSanitationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareSanitationLogs")
        val _columnIndexOfShareFeedLogs: Int = getColumnIndexOrThrow(_stmt, "shareFeedLogs")
        val _columnIndexOfShareWeightData: Int = getColumnIndexOrThrow(_stmt, "shareWeightData")
        val _columnIndexOfShareSalesActivity: Int = getColumnIndexOrThrow(_stmt,
            "shareSalesActivity")
        val _columnIndexOfShareMortalityData: Int = getColumnIndexOrThrow(_stmt,
            "shareMortalityData")
        val _columnIndexOfShareExpenseData: Int = getColumnIndexOrThrow(_stmt, "shareExpenseData")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmProfileEntity
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmName: String
          _tmpFarmName = _stmt.getText(_columnIndexOfFarmName)
          val _tmpFarmBio: String?
          if (_stmt.isNull(_columnIndexOfFarmBio)) {
            _tmpFarmBio = null
          } else {
            _tmpFarmBio = _stmt.getText(_columnIndexOfFarmBio)
          }
          val _tmpLogoUrl: String?
          if (_stmt.isNull(_columnIndexOfLogoUrl)) {
            _tmpLogoUrl = null
          } else {
            _tmpLogoUrl = _stmt.getText(_columnIndexOfLogoUrl)
          }
          val _tmpCoverPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfCoverPhotoUrl)) {
            _tmpCoverPhotoUrl = null
          } else {
            _tmpCoverPhotoUrl = _stmt.getText(_columnIndexOfCoverPhotoUrl)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpBarangay: String?
          if (_stmt.isNull(_columnIndexOfBarangay)) {
            _tmpBarangay = null
          } else {
            _tmpBarangay = _stmt.getText(_columnIndexOfBarangay)
          }
          val _tmpMunicipality: String?
          if (_stmt.isNull(_columnIndexOfMunicipality)) {
            _tmpMunicipality = null
          } else {
            _tmpMunicipality = _stmt.getText(_columnIndexOfMunicipality)
          }
          val _tmpProvince: String?
          if (_stmt.isNull(_columnIndexOfProvince)) {
            _tmpProvince = null
          } else {
            _tmpProvince = _stmt.getText(_columnIndexOfProvince)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpMemberSince: Long
          _tmpMemberSince = _stmt.getLong(_columnIndexOfMemberSince)
          val _tmpFarmEstablished: Long?
          if (_stmt.isNull(_columnIndexOfFarmEstablished)) {
            _tmpFarmEstablished = null
          } else {
            _tmpFarmEstablished = _stmt.getLong(_columnIndexOfFarmEstablished)
          }
          val _tmpTrustScore: Int
          _tmpTrustScore = _stmt.getLong(_columnIndexOfTrustScore).toInt()
          val _tmpTotalBirdsSold: Int
          _tmpTotalBirdsSold = _stmt.getLong(_columnIndexOfTotalBirdsSold).toInt()
          val _tmpTotalOrdersCompleted: Int
          _tmpTotalOrdersCompleted = _stmt.getLong(_columnIndexOfTotalOrdersCompleted).toInt()
          val _tmpAvgResponseTimeMinutes: Int?
          if (_stmt.isNull(_columnIndexOfAvgResponseTimeMinutes)) {
            _tmpAvgResponseTimeMinutes = null
          } else {
            _tmpAvgResponseTimeMinutes = _stmt.getLong(_columnIndexOfAvgResponseTimeMinutes).toInt()
          }
          val _tmpVaccinationRate: Int?
          if (_stmt.isNull(_columnIndexOfVaccinationRate)) {
            _tmpVaccinationRate = null
          } else {
            _tmpVaccinationRate = _stmt.getLong(_columnIndexOfVaccinationRate).toInt()
          }
          val _tmpReturningBuyerRate: Int?
          if (_stmt.isNull(_columnIndexOfReturningBuyerRate)) {
            _tmpReturningBuyerRate = null
          } else {
            _tmpReturningBuyerRate = _stmt.getLong(_columnIndexOfReturningBuyerRate).toInt()
          }
          val _tmpBadgesJson: String
          _tmpBadgesJson = _stmt.getText(_columnIndexOfBadgesJson)
          val _tmpWhatsappNumber: String?
          if (_stmt.isNull(_columnIndexOfWhatsappNumber)) {
            _tmpWhatsappNumber = null
          } else {
            _tmpWhatsappNumber = _stmt.getText(_columnIndexOfWhatsappNumber)
          }
          val _tmpIsWhatsappEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWhatsappEnabled).toInt()
          _tmpIsWhatsappEnabled = _tmp_1 != 0
          val _tmpIsCallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsCallEnabled).toInt()
          _tmpIsCallEnabled = _tmp_2 != 0
          val _tmpIsPublic: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp_3 != 0
          val _tmpShowLocation: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfShowLocation).toInt()
          _tmpShowLocation = _tmp_4 != 0
          val _tmpShowSalesHistory: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfShowSalesHistory).toInt()
          _tmpShowSalesHistory = _tmp_5 != 0
          val _tmpShowTimeline: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfShowTimeline).toInt()
          _tmpShowTimeline = _tmp_6 != 0
          val _tmpShareVaccinationLogs: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfShareVaccinationLogs).toInt()
          _tmpShareVaccinationLogs = _tmp_7 != 0
          val _tmpShareSanitationLogs: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfShareSanitationLogs).toInt()
          _tmpShareSanitationLogs = _tmp_8 != 0
          val _tmpShareFeedLogs: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfShareFeedLogs).toInt()
          _tmpShareFeedLogs = _tmp_9 != 0
          val _tmpShareWeightData: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfShareWeightData).toInt()
          _tmpShareWeightData = _tmp_10 != 0
          val _tmpShareSalesActivity: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfShareSalesActivity).toInt()
          _tmpShareSalesActivity = _tmp_11 != 0
          val _tmpShareMortalityData: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfShareMortalityData).toInt()
          _tmpShareMortalityData = _tmp_12 != 0
          val _tmpShareExpenseData: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfShareExpenseData).toInt()
          _tmpShareExpenseData = _tmp_13 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_14 != 0
          _item =
              FarmProfileEntity(_tmpFarmerId,_tmpFarmName,_tmpFarmBio,_tmpLogoUrl,_tmpCoverPhotoUrl,_tmpLocationName,_tmpBarangay,_tmpMunicipality,_tmpProvince,_tmpLatitude,_tmpLongitude,_tmpIsVerified,_tmpVerifiedAt,_tmpMemberSince,_tmpFarmEstablished,_tmpTrustScore,_tmpTotalBirdsSold,_tmpTotalOrdersCompleted,_tmpAvgResponseTimeMinutes,_tmpVaccinationRate,_tmpReturningBuyerRate,_tmpBadgesJson,_tmpWhatsappNumber,_tmpIsWhatsappEnabled,_tmpIsCallEnabled,_tmpIsPublic,_tmpShowLocation,_tmpShowSalesHistory,_tmpShowTimeline,_tmpShareVaccinationLogs,_tmpShareSanitationLogs,_tmpShareFeedLogs,_tmpShareWeightData,_tmpShareSalesActivity,_tmpShareMortalityData,_tmpShareExpenseData,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFarmsByProvince(province: String, limit: Int):
      Flow<List<FarmProfileEntity>> {
    val _sql: String =
        "SELECT * FROM farm_profiles WHERE isPublic = 1 AND province = ? ORDER BY trustScore DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("farm_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, province)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmName: Int = getColumnIndexOrThrow(_stmt, "farmName")
        val _columnIndexOfFarmBio: Int = getColumnIndexOrThrow(_stmt, "farmBio")
        val _columnIndexOfLogoUrl: Int = getColumnIndexOrThrow(_stmt, "logoUrl")
        val _columnIndexOfCoverPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "coverPhotoUrl")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfBarangay: Int = getColumnIndexOrThrow(_stmt, "barangay")
        val _columnIndexOfMunicipality: Int = getColumnIndexOrThrow(_stmt, "municipality")
        val _columnIndexOfProvince: Int = getColumnIndexOrThrow(_stmt, "province")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfMemberSince: Int = getColumnIndexOrThrow(_stmt, "memberSince")
        val _columnIndexOfFarmEstablished: Int = getColumnIndexOrThrow(_stmt, "farmEstablished")
        val _columnIndexOfTrustScore: Int = getColumnIndexOrThrow(_stmt, "trustScore")
        val _columnIndexOfTotalBirdsSold: Int = getColumnIndexOrThrow(_stmt, "totalBirdsSold")
        val _columnIndexOfTotalOrdersCompleted: Int = getColumnIndexOrThrow(_stmt,
            "totalOrdersCompleted")
        val _columnIndexOfAvgResponseTimeMinutes: Int = getColumnIndexOrThrow(_stmt,
            "avgResponseTimeMinutes")
        val _columnIndexOfVaccinationRate: Int = getColumnIndexOrThrow(_stmt, "vaccinationRate")
        val _columnIndexOfReturningBuyerRate: Int = getColumnIndexOrThrow(_stmt,
            "returningBuyerRate")
        val _columnIndexOfBadgesJson: Int = getColumnIndexOrThrow(_stmt, "badgesJson")
        val _columnIndexOfWhatsappNumber: Int = getColumnIndexOrThrow(_stmt, "whatsappNumber")
        val _columnIndexOfIsWhatsappEnabled: Int = getColumnIndexOrThrow(_stmt, "isWhatsappEnabled")
        val _columnIndexOfIsCallEnabled: Int = getColumnIndexOrThrow(_stmt, "isCallEnabled")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfShowLocation: Int = getColumnIndexOrThrow(_stmt, "showLocation")
        val _columnIndexOfShowSalesHistory: Int = getColumnIndexOrThrow(_stmt, "showSalesHistory")
        val _columnIndexOfShowTimeline: Int = getColumnIndexOrThrow(_stmt, "showTimeline")
        val _columnIndexOfShareVaccinationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareVaccinationLogs")
        val _columnIndexOfShareSanitationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareSanitationLogs")
        val _columnIndexOfShareFeedLogs: Int = getColumnIndexOrThrow(_stmt, "shareFeedLogs")
        val _columnIndexOfShareWeightData: Int = getColumnIndexOrThrow(_stmt, "shareWeightData")
        val _columnIndexOfShareSalesActivity: Int = getColumnIndexOrThrow(_stmt,
            "shareSalesActivity")
        val _columnIndexOfShareMortalityData: Int = getColumnIndexOrThrow(_stmt,
            "shareMortalityData")
        val _columnIndexOfShareExpenseData: Int = getColumnIndexOrThrow(_stmt, "shareExpenseData")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmProfileEntity
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmName: String
          _tmpFarmName = _stmt.getText(_columnIndexOfFarmName)
          val _tmpFarmBio: String?
          if (_stmt.isNull(_columnIndexOfFarmBio)) {
            _tmpFarmBio = null
          } else {
            _tmpFarmBio = _stmt.getText(_columnIndexOfFarmBio)
          }
          val _tmpLogoUrl: String?
          if (_stmt.isNull(_columnIndexOfLogoUrl)) {
            _tmpLogoUrl = null
          } else {
            _tmpLogoUrl = _stmt.getText(_columnIndexOfLogoUrl)
          }
          val _tmpCoverPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfCoverPhotoUrl)) {
            _tmpCoverPhotoUrl = null
          } else {
            _tmpCoverPhotoUrl = _stmt.getText(_columnIndexOfCoverPhotoUrl)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpBarangay: String?
          if (_stmt.isNull(_columnIndexOfBarangay)) {
            _tmpBarangay = null
          } else {
            _tmpBarangay = _stmt.getText(_columnIndexOfBarangay)
          }
          val _tmpMunicipality: String?
          if (_stmt.isNull(_columnIndexOfMunicipality)) {
            _tmpMunicipality = null
          } else {
            _tmpMunicipality = _stmt.getText(_columnIndexOfMunicipality)
          }
          val _tmpProvince: String?
          if (_stmt.isNull(_columnIndexOfProvince)) {
            _tmpProvince = null
          } else {
            _tmpProvince = _stmt.getText(_columnIndexOfProvince)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpMemberSince: Long
          _tmpMemberSince = _stmt.getLong(_columnIndexOfMemberSince)
          val _tmpFarmEstablished: Long?
          if (_stmt.isNull(_columnIndexOfFarmEstablished)) {
            _tmpFarmEstablished = null
          } else {
            _tmpFarmEstablished = _stmt.getLong(_columnIndexOfFarmEstablished)
          }
          val _tmpTrustScore: Int
          _tmpTrustScore = _stmt.getLong(_columnIndexOfTrustScore).toInt()
          val _tmpTotalBirdsSold: Int
          _tmpTotalBirdsSold = _stmt.getLong(_columnIndexOfTotalBirdsSold).toInt()
          val _tmpTotalOrdersCompleted: Int
          _tmpTotalOrdersCompleted = _stmt.getLong(_columnIndexOfTotalOrdersCompleted).toInt()
          val _tmpAvgResponseTimeMinutes: Int?
          if (_stmt.isNull(_columnIndexOfAvgResponseTimeMinutes)) {
            _tmpAvgResponseTimeMinutes = null
          } else {
            _tmpAvgResponseTimeMinutes = _stmt.getLong(_columnIndexOfAvgResponseTimeMinutes).toInt()
          }
          val _tmpVaccinationRate: Int?
          if (_stmt.isNull(_columnIndexOfVaccinationRate)) {
            _tmpVaccinationRate = null
          } else {
            _tmpVaccinationRate = _stmt.getLong(_columnIndexOfVaccinationRate).toInt()
          }
          val _tmpReturningBuyerRate: Int?
          if (_stmt.isNull(_columnIndexOfReturningBuyerRate)) {
            _tmpReturningBuyerRate = null
          } else {
            _tmpReturningBuyerRate = _stmt.getLong(_columnIndexOfReturningBuyerRate).toInt()
          }
          val _tmpBadgesJson: String
          _tmpBadgesJson = _stmt.getText(_columnIndexOfBadgesJson)
          val _tmpWhatsappNumber: String?
          if (_stmt.isNull(_columnIndexOfWhatsappNumber)) {
            _tmpWhatsappNumber = null
          } else {
            _tmpWhatsappNumber = _stmt.getText(_columnIndexOfWhatsappNumber)
          }
          val _tmpIsWhatsappEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWhatsappEnabled).toInt()
          _tmpIsWhatsappEnabled = _tmp_1 != 0
          val _tmpIsCallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsCallEnabled).toInt()
          _tmpIsCallEnabled = _tmp_2 != 0
          val _tmpIsPublic: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp_3 != 0
          val _tmpShowLocation: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfShowLocation).toInt()
          _tmpShowLocation = _tmp_4 != 0
          val _tmpShowSalesHistory: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfShowSalesHistory).toInt()
          _tmpShowSalesHistory = _tmp_5 != 0
          val _tmpShowTimeline: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfShowTimeline).toInt()
          _tmpShowTimeline = _tmp_6 != 0
          val _tmpShareVaccinationLogs: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfShareVaccinationLogs).toInt()
          _tmpShareVaccinationLogs = _tmp_7 != 0
          val _tmpShareSanitationLogs: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfShareSanitationLogs).toInt()
          _tmpShareSanitationLogs = _tmp_8 != 0
          val _tmpShareFeedLogs: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfShareFeedLogs).toInt()
          _tmpShareFeedLogs = _tmp_9 != 0
          val _tmpShareWeightData: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfShareWeightData).toInt()
          _tmpShareWeightData = _tmp_10 != 0
          val _tmpShareSalesActivity: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfShareSalesActivity).toInt()
          _tmpShareSalesActivity = _tmp_11 != 0
          val _tmpShareMortalityData: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfShareMortalityData).toInt()
          _tmpShareMortalityData = _tmp_12 != 0
          val _tmpShareExpenseData: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfShareExpenseData).toInt()
          _tmpShareExpenseData = _tmp_13 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_14 != 0
          _item =
              FarmProfileEntity(_tmpFarmerId,_tmpFarmName,_tmpFarmBio,_tmpLogoUrl,_tmpCoverPhotoUrl,_tmpLocationName,_tmpBarangay,_tmpMunicipality,_tmpProvince,_tmpLatitude,_tmpLongitude,_tmpIsVerified,_tmpVerifiedAt,_tmpMemberSince,_tmpFarmEstablished,_tmpTrustScore,_tmpTotalBirdsSold,_tmpTotalOrdersCompleted,_tmpAvgResponseTimeMinutes,_tmpVaccinationRate,_tmpReturningBuyerRate,_tmpBadgesJson,_tmpWhatsappNumber,_tmpIsWhatsappEnabled,_tmpIsCallEnabled,_tmpIsPublic,_tmpShowLocation,_tmpShowSalesHistory,_tmpShowTimeline,_tmpShareVaccinationLogs,_tmpShareSanitationLogs,_tmpShareFeedLogs,_tmpShareWeightData,_tmpShareSalesActivity,_tmpShareMortalityData,_tmpShareExpenseData,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun searchFarms(query: String, limit: Int): List<FarmProfileEntity> {
    val _sql: String =
        "SELECT * FROM farm_profiles WHERE isPublic = 1 AND farmName LIKE '%' || ? || '%' LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmName: Int = getColumnIndexOrThrow(_stmt, "farmName")
        val _columnIndexOfFarmBio: Int = getColumnIndexOrThrow(_stmt, "farmBio")
        val _columnIndexOfLogoUrl: Int = getColumnIndexOrThrow(_stmt, "logoUrl")
        val _columnIndexOfCoverPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "coverPhotoUrl")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfBarangay: Int = getColumnIndexOrThrow(_stmt, "barangay")
        val _columnIndexOfMunicipality: Int = getColumnIndexOrThrow(_stmt, "municipality")
        val _columnIndexOfProvince: Int = getColumnIndexOrThrow(_stmt, "province")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfMemberSince: Int = getColumnIndexOrThrow(_stmt, "memberSince")
        val _columnIndexOfFarmEstablished: Int = getColumnIndexOrThrow(_stmt, "farmEstablished")
        val _columnIndexOfTrustScore: Int = getColumnIndexOrThrow(_stmt, "trustScore")
        val _columnIndexOfTotalBirdsSold: Int = getColumnIndexOrThrow(_stmt, "totalBirdsSold")
        val _columnIndexOfTotalOrdersCompleted: Int = getColumnIndexOrThrow(_stmt,
            "totalOrdersCompleted")
        val _columnIndexOfAvgResponseTimeMinutes: Int = getColumnIndexOrThrow(_stmt,
            "avgResponseTimeMinutes")
        val _columnIndexOfVaccinationRate: Int = getColumnIndexOrThrow(_stmt, "vaccinationRate")
        val _columnIndexOfReturningBuyerRate: Int = getColumnIndexOrThrow(_stmt,
            "returningBuyerRate")
        val _columnIndexOfBadgesJson: Int = getColumnIndexOrThrow(_stmt, "badgesJson")
        val _columnIndexOfWhatsappNumber: Int = getColumnIndexOrThrow(_stmt, "whatsappNumber")
        val _columnIndexOfIsWhatsappEnabled: Int = getColumnIndexOrThrow(_stmt, "isWhatsappEnabled")
        val _columnIndexOfIsCallEnabled: Int = getColumnIndexOrThrow(_stmt, "isCallEnabled")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfShowLocation: Int = getColumnIndexOrThrow(_stmt, "showLocation")
        val _columnIndexOfShowSalesHistory: Int = getColumnIndexOrThrow(_stmt, "showSalesHistory")
        val _columnIndexOfShowTimeline: Int = getColumnIndexOrThrow(_stmt, "showTimeline")
        val _columnIndexOfShareVaccinationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareVaccinationLogs")
        val _columnIndexOfShareSanitationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareSanitationLogs")
        val _columnIndexOfShareFeedLogs: Int = getColumnIndexOrThrow(_stmt, "shareFeedLogs")
        val _columnIndexOfShareWeightData: Int = getColumnIndexOrThrow(_stmt, "shareWeightData")
        val _columnIndexOfShareSalesActivity: Int = getColumnIndexOrThrow(_stmt,
            "shareSalesActivity")
        val _columnIndexOfShareMortalityData: Int = getColumnIndexOrThrow(_stmt,
            "shareMortalityData")
        val _columnIndexOfShareExpenseData: Int = getColumnIndexOrThrow(_stmt, "shareExpenseData")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmProfileEntity
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmName: String
          _tmpFarmName = _stmt.getText(_columnIndexOfFarmName)
          val _tmpFarmBio: String?
          if (_stmt.isNull(_columnIndexOfFarmBio)) {
            _tmpFarmBio = null
          } else {
            _tmpFarmBio = _stmt.getText(_columnIndexOfFarmBio)
          }
          val _tmpLogoUrl: String?
          if (_stmt.isNull(_columnIndexOfLogoUrl)) {
            _tmpLogoUrl = null
          } else {
            _tmpLogoUrl = _stmt.getText(_columnIndexOfLogoUrl)
          }
          val _tmpCoverPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfCoverPhotoUrl)) {
            _tmpCoverPhotoUrl = null
          } else {
            _tmpCoverPhotoUrl = _stmt.getText(_columnIndexOfCoverPhotoUrl)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpBarangay: String?
          if (_stmt.isNull(_columnIndexOfBarangay)) {
            _tmpBarangay = null
          } else {
            _tmpBarangay = _stmt.getText(_columnIndexOfBarangay)
          }
          val _tmpMunicipality: String?
          if (_stmt.isNull(_columnIndexOfMunicipality)) {
            _tmpMunicipality = null
          } else {
            _tmpMunicipality = _stmt.getText(_columnIndexOfMunicipality)
          }
          val _tmpProvince: String?
          if (_stmt.isNull(_columnIndexOfProvince)) {
            _tmpProvince = null
          } else {
            _tmpProvince = _stmt.getText(_columnIndexOfProvince)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpMemberSince: Long
          _tmpMemberSince = _stmt.getLong(_columnIndexOfMemberSince)
          val _tmpFarmEstablished: Long?
          if (_stmt.isNull(_columnIndexOfFarmEstablished)) {
            _tmpFarmEstablished = null
          } else {
            _tmpFarmEstablished = _stmt.getLong(_columnIndexOfFarmEstablished)
          }
          val _tmpTrustScore: Int
          _tmpTrustScore = _stmt.getLong(_columnIndexOfTrustScore).toInt()
          val _tmpTotalBirdsSold: Int
          _tmpTotalBirdsSold = _stmt.getLong(_columnIndexOfTotalBirdsSold).toInt()
          val _tmpTotalOrdersCompleted: Int
          _tmpTotalOrdersCompleted = _stmt.getLong(_columnIndexOfTotalOrdersCompleted).toInt()
          val _tmpAvgResponseTimeMinutes: Int?
          if (_stmt.isNull(_columnIndexOfAvgResponseTimeMinutes)) {
            _tmpAvgResponseTimeMinutes = null
          } else {
            _tmpAvgResponseTimeMinutes = _stmt.getLong(_columnIndexOfAvgResponseTimeMinutes).toInt()
          }
          val _tmpVaccinationRate: Int?
          if (_stmt.isNull(_columnIndexOfVaccinationRate)) {
            _tmpVaccinationRate = null
          } else {
            _tmpVaccinationRate = _stmt.getLong(_columnIndexOfVaccinationRate).toInt()
          }
          val _tmpReturningBuyerRate: Int?
          if (_stmt.isNull(_columnIndexOfReturningBuyerRate)) {
            _tmpReturningBuyerRate = null
          } else {
            _tmpReturningBuyerRate = _stmt.getLong(_columnIndexOfReturningBuyerRate).toInt()
          }
          val _tmpBadgesJson: String
          _tmpBadgesJson = _stmt.getText(_columnIndexOfBadgesJson)
          val _tmpWhatsappNumber: String?
          if (_stmt.isNull(_columnIndexOfWhatsappNumber)) {
            _tmpWhatsappNumber = null
          } else {
            _tmpWhatsappNumber = _stmt.getText(_columnIndexOfWhatsappNumber)
          }
          val _tmpIsWhatsappEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWhatsappEnabled).toInt()
          _tmpIsWhatsappEnabled = _tmp_1 != 0
          val _tmpIsCallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsCallEnabled).toInt()
          _tmpIsCallEnabled = _tmp_2 != 0
          val _tmpIsPublic: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp_3 != 0
          val _tmpShowLocation: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfShowLocation).toInt()
          _tmpShowLocation = _tmp_4 != 0
          val _tmpShowSalesHistory: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfShowSalesHistory).toInt()
          _tmpShowSalesHistory = _tmp_5 != 0
          val _tmpShowTimeline: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfShowTimeline).toInt()
          _tmpShowTimeline = _tmp_6 != 0
          val _tmpShareVaccinationLogs: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfShareVaccinationLogs).toInt()
          _tmpShareVaccinationLogs = _tmp_7 != 0
          val _tmpShareSanitationLogs: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfShareSanitationLogs).toInt()
          _tmpShareSanitationLogs = _tmp_8 != 0
          val _tmpShareFeedLogs: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfShareFeedLogs).toInt()
          _tmpShareFeedLogs = _tmp_9 != 0
          val _tmpShareWeightData: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfShareWeightData).toInt()
          _tmpShareWeightData = _tmp_10 != 0
          val _tmpShareSalesActivity: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfShareSalesActivity).toInt()
          _tmpShareSalesActivity = _tmp_11 != 0
          val _tmpShareMortalityData: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfShareMortalityData).toInt()
          _tmpShareMortalityData = _tmp_12 != 0
          val _tmpShareExpenseData: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfShareExpenseData).toInt()
          _tmpShareExpenseData = _tmp_13 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_14 != 0
          _item =
              FarmProfileEntity(_tmpFarmerId,_tmpFarmName,_tmpFarmBio,_tmpLogoUrl,_tmpCoverPhotoUrl,_tmpLocationName,_tmpBarangay,_tmpMunicipality,_tmpProvince,_tmpLatitude,_tmpLongitude,_tmpIsVerified,_tmpVerifiedAt,_tmpMemberSince,_tmpFarmEstablished,_tmpTrustScore,_tmpTotalBirdsSold,_tmpTotalOrdersCompleted,_tmpAvgResponseTimeMinutes,_tmpVaccinationRate,_tmpReturningBuyerRate,_tmpBadgesJson,_tmpWhatsappNumber,_tmpIsWhatsappEnabled,_tmpIsCallEnabled,_tmpIsPublic,_tmpShowLocation,_tmpShowSalesHistory,_tmpShowTimeline,_tmpShareVaccinationLogs,_tmpShareSanitationLogs,_tmpShareFeedLogs,_tmpShareWeightData,_tmpShareSalesActivity,_tmpShareMortalityData,_tmpShareExpenseData,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyProfiles(limit: Int): List<FarmProfileEntity> {
    val _sql: String = "SELECT * FROM farm_profiles WHERE dirty = 1 LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfFarmName: Int = getColumnIndexOrThrow(_stmt, "farmName")
        val _columnIndexOfFarmBio: Int = getColumnIndexOrThrow(_stmt, "farmBio")
        val _columnIndexOfLogoUrl: Int = getColumnIndexOrThrow(_stmt, "logoUrl")
        val _columnIndexOfCoverPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "coverPhotoUrl")
        val _columnIndexOfLocationName: Int = getColumnIndexOrThrow(_stmt, "locationName")
        val _columnIndexOfBarangay: Int = getColumnIndexOrThrow(_stmt, "barangay")
        val _columnIndexOfMunicipality: Int = getColumnIndexOrThrow(_stmt, "municipality")
        val _columnIndexOfProvince: Int = getColumnIndexOrThrow(_stmt, "province")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfIsVerified: Int = getColumnIndexOrThrow(_stmt, "isVerified")
        val _columnIndexOfVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "verifiedAt")
        val _columnIndexOfMemberSince: Int = getColumnIndexOrThrow(_stmt, "memberSince")
        val _columnIndexOfFarmEstablished: Int = getColumnIndexOrThrow(_stmt, "farmEstablished")
        val _columnIndexOfTrustScore: Int = getColumnIndexOrThrow(_stmt, "trustScore")
        val _columnIndexOfTotalBirdsSold: Int = getColumnIndexOrThrow(_stmt, "totalBirdsSold")
        val _columnIndexOfTotalOrdersCompleted: Int = getColumnIndexOrThrow(_stmt,
            "totalOrdersCompleted")
        val _columnIndexOfAvgResponseTimeMinutes: Int = getColumnIndexOrThrow(_stmt,
            "avgResponseTimeMinutes")
        val _columnIndexOfVaccinationRate: Int = getColumnIndexOrThrow(_stmt, "vaccinationRate")
        val _columnIndexOfReturningBuyerRate: Int = getColumnIndexOrThrow(_stmt,
            "returningBuyerRate")
        val _columnIndexOfBadgesJson: Int = getColumnIndexOrThrow(_stmt, "badgesJson")
        val _columnIndexOfWhatsappNumber: Int = getColumnIndexOrThrow(_stmt, "whatsappNumber")
        val _columnIndexOfIsWhatsappEnabled: Int = getColumnIndexOrThrow(_stmt, "isWhatsappEnabled")
        val _columnIndexOfIsCallEnabled: Int = getColumnIndexOrThrow(_stmt, "isCallEnabled")
        val _columnIndexOfIsPublic: Int = getColumnIndexOrThrow(_stmt, "isPublic")
        val _columnIndexOfShowLocation: Int = getColumnIndexOrThrow(_stmt, "showLocation")
        val _columnIndexOfShowSalesHistory: Int = getColumnIndexOrThrow(_stmt, "showSalesHistory")
        val _columnIndexOfShowTimeline: Int = getColumnIndexOrThrow(_stmt, "showTimeline")
        val _columnIndexOfShareVaccinationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareVaccinationLogs")
        val _columnIndexOfShareSanitationLogs: Int = getColumnIndexOrThrow(_stmt,
            "shareSanitationLogs")
        val _columnIndexOfShareFeedLogs: Int = getColumnIndexOrThrow(_stmt, "shareFeedLogs")
        val _columnIndexOfShareWeightData: Int = getColumnIndexOrThrow(_stmt, "shareWeightData")
        val _columnIndexOfShareSalesActivity: Int = getColumnIndexOrThrow(_stmt,
            "shareSalesActivity")
        val _columnIndexOfShareMortalityData: Int = getColumnIndexOrThrow(_stmt,
            "shareMortalityData")
        val _columnIndexOfShareExpenseData: Int = getColumnIndexOrThrow(_stmt, "shareExpenseData")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _result: MutableList<FarmProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmProfileEntity
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpFarmName: String
          _tmpFarmName = _stmt.getText(_columnIndexOfFarmName)
          val _tmpFarmBio: String?
          if (_stmt.isNull(_columnIndexOfFarmBio)) {
            _tmpFarmBio = null
          } else {
            _tmpFarmBio = _stmt.getText(_columnIndexOfFarmBio)
          }
          val _tmpLogoUrl: String?
          if (_stmt.isNull(_columnIndexOfLogoUrl)) {
            _tmpLogoUrl = null
          } else {
            _tmpLogoUrl = _stmt.getText(_columnIndexOfLogoUrl)
          }
          val _tmpCoverPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfCoverPhotoUrl)) {
            _tmpCoverPhotoUrl = null
          } else {
            _tmpCoverPhotoUrl = _stmt.getText(_columnIndexOfCoverPhotoUrl)
          }
          val _tmpLocationName: String?
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName)
          }
          val _tmpBarangay: String?
          if (_stmt.isNull(_columnIndexOfBarangay)) {
            _tmpBarangay = null
          } else {
            _tmpBarangay = _stmt.getText(_columnIndexOfBarangay)
          }
          val _tmpMunicipality: String?
          if (_stmt.isNull(_columnIndexOfMunicipality)) {
            _tmpMunicipality = null
          } else {
            _tmpMunicipality = _stmt.getText(_columnIndexOfMunicipality)
          }
          val _tmpProvince: String?
          if (_stmt.isNull(_columnIndexOfProvince)) {
            _tmpProvince = null
          } else {
            _tmpProvince = _stmt.getText(_columnIndexOfProvince)
          }
          val _tmpLatitude: Double?
          if (_stmt.isNull(_columnIndexOfLatitude)) {
            _tmpLatitude = null
          } else {
            _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          }
          val _tmpLongitude: Double?
          if (_stmt.isNull(_columnIndexOfLongitude)) {
            _tmpLongitude = null
          } else {
            _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          }
          val _tmpIsVerified: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsVerified).toInt()
          _tmpIsVerified = _tmp != 0
          val _tmpVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfVerifiedAt)) {
            _tmpVerifiedAt = null
          } else {
            _tmpVerifiedAt = _stmt.getLong(_columnIndexOfVerifiedAt)
          }
          val _tmpMemberSince: Long
          _tmpMemberSince = _stmt.getLong(_columnIndexOfMemberSince)
          val _tmpFarmEstablished: Long?
          if (_stmt.isNull(_columnIndexOfFarmEstablished)) {
            _tmpFarmEstablished = null
          } else {
            _tmpFarmEstablished = _stmt.getLong(_columnIndexOfFarmEstablished)
          }
          val _tmpTrustScore: Int
          _tmpTrustScore = _stmt.getLong(_columnIndexOfTrustScore).toInt()
          val _tmpTotalBirdsSold: Int
          _tmpTotalBirdsSold = _stmt.getLong(_columnIndexOfTotalBirdsSold).toInt()
          val _tmpTotalOrdersCompleted: Int
          _tmpTotalOrdersCompleted = _stmt.getLong(_columnIndexOfTotalOrdersCompleted).toInt()
          val _tmpAvgResponseTimeMinutes: Int?
          if (_stmt.isNull(_columnIndexOfAvgResponseTimeMinutes)) {
            _tmpAvgResponseTimeMinutes = null
          } else {
            _tmpAvgResponseTimeMinutes = _stmt.getLong(_columnIndexOfAvgResponseTimeMinutes).toInt()
          }
          val _tmpVaccinationRate: Int?
          if (_stmt.isNull(_columnIndexOfVaccinationRate)) {
            _tmpVaccinationRate = null
          } else {
            _tmpVaccinationRate = _stmt.getLong(_columnIndexOfVaccinationRate).toInt()
          }
          val _tmpReturningBuyerRate: Int?
          if (_stmt.isNull(_columnIndexOfReturningBuyerRate)) {
            _tmpReturningBuyerRate = null
          } else {
            _tmpReturningBuyerRate = _stmt.getLong(_columnIndexOfReturningBuyerRate).toInt()
          }
          val _tmpBadgesJson: String
          _tmpBadgesJson = _stmt.getText(_columnIndexOfBadgesJson)
          val _tmpWhatsappNumber: String?
          if (_stmt.isNull(_columnIndexOfWhatsappNumber)) {
            _tmpWhatsappNumber = null
          } else {
            _tmpWhatsappNumber = _stmt.getText(_columnIndexOfWhatsappNumber)
          }
          val _tmpIsWhatsappEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsWhatsappEnabled).toInt()
          _tmpIsWhatsappEnabled = _tmp_1 != 0
          val _tmpIsCallEnabled: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsCallEnabled).toInt()
          _tmpIsCallEnabled = _tmp_2 != 0
          val _tmpIsPublic: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsPublic).toInt()
          _tmpIsPublic = _tmp_3 != 0
          val _tmpShowLocation: Boolean
          val _tmp_4: Int
          _tmp_4 = _stmt.getLong(_columnIndexOfShowLocation).toInt()
          _tmpShowLocation = _tmp_4 != 0
          val _tmpShowSalesHistory: Boolean
          val _tmp_5: Int
          _tmp_5 = _stmt.getLong(_columnIndexOfShowSalesHistory).toInt()
          _tmpShowSalesHistory = _tmp_5 != 0
          val _tmpShowTimeline: Boolean
          val _tmp_6: Int
          _tmp_6 = _stmt.getLong(_columnIndexOfShowTimeline).toInt()
          _tmpShowTimeline = _tmp_6 != 0
          val _tmpShareVaccinationLogs: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfShareVaccinationLogs).toInt()
          _tmpShareVaccinationLogs = _tmp_7 != 0
          val _tmpShareSanitationLogs: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfShareSanitationLogs).toInt()
          _tmpShareSanitationLogs = _tmp_8 != 0
          val _tmpShareFeedLogs: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfShareFeedLogs).toInt()
          _tmpShareFeedLogs = _tmp_9 != 0
          val _tmpShareWeightData: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfShareWeightData).toInt()
          _tmpShareWeightData = _tmp_10 != 0
          val _tmpShareSalesActivity: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfShareSalesActivity).toInt()
          _tmpShareSalesActivity = _tmp_11 != 0
          val _tmpShareMortalityData: Boolean
          val _tmp_12: Int
          _tmp_12 = _stmt.getLong(_columnIndexOfShareMortalityData).toInt()
          _tmpShareMortalityData = _tmp_12 != 0
          val _tmpShareExpenseData: Boolean
          val _tmp_13: Int
          _tmp_13 = _stmt.getLong(_columnIndexOfShareExpenseData).toInt()
          _tmpShareExpenseData = _tmp_13 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_14: Int
          _tmp_14 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_14 != 0
          _item =
              FarmProfileEntity(_tmpFarmerId,_tmpFarmName,_tmpFarmBio,_tmpLogoUrl,_tmpCoverPhotoUrl,_tmpLocationName,_tmpBarangay,_tmpMunicipality,_tmpProvince,_tmpLatitude,_tmpLongitude,_tmpIsVerified,_tmpVerifiedAt,_tmpMemberSince,_tmpFarmEstablished,_tmpTrustScore,_tmpTotalBirdsSold,_tmpTotalOrdersCompleted,_tmpAvgResponseTimeMinutes,_tmpVaccinationRate,_tmpReturningBuyerRate,_tmpBadgesJson,_tmpWhatsappNumber,_tmpIsWhatsappEnabled,_tmpIsCallEnabled,_tmpIsPublic,_tmpShowLocation,_tmpShowSalesHistory,_tmpShowTimeline,_tmpShareVaccinationLogs,_tmpShareSanitationLogs,_tmpShareFeedLogs,_tmpShareWeightData,_tmpShareSalesActivity,_tmpShareMortalityData,_tmpShareExpenseData,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateTrustScore(
    farmerId: String,
    score: Int,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET trustScore = ?, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, score.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun incrementSales(
    farmerId: String,
    count: Int,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET totalBirdsSold = totalBirdsSold + ?, totalOrdersCompleted = totalOrdersCompleted + 1, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, count.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateVaccinationRate(
    farmerId: String,
    rate: Int,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET vaccinationRate = ?, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, rate.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBadges(
    farmerId: String,
    badges: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET badgesJson = ?, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, badges)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markVerified(
    farmerId: String,
    verifiedAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET isVerified = 1, verifiedAt = ?, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, verifiedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setProfileVisibility(
    farmerId: String,
    isPublic: Boolean,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET isPublic = ?, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (isPublic) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setTimelineVisibility(
    farmerId: String,
    show: Boolean,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE farm_profiles SET showTimeline = ?, updatedAt = ?, dirty = 1 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (show) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(farmerId: String) {
    val _sql: String = "UPDATE farm_profiles SET dirty = 0 WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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
