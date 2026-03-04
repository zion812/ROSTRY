package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.DigitalTwinEntity
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
public class DigitalTwinDao_Impl(
  __db: RoomDatabase,
) : DigitalTwinDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDigitalTwinEntity: EntityInsertAdapter<DigitalTwinEntity>

  private val __updateAdapterOfDigitalTwinEntity: EntityDeleteOrUpdateAdapter<DigitalTwinEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDigitalTwinEntity = object : EntityInsertAdapter<DigitalTwinEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `digital_twins` (`twinId`,`birdId`,`registryId`,`ownerId`,`birdName`,`baseBreed`,`strainType`,`localStrainName`,`geneticPurityScore`,`bodyType`,`boneDensityScore`,`heightCm`,`weightKg`,`beakType`,`combType`,`skinColor`,`legColor`,`spurType`,`morphologyScore`,`primaryBodyColor`,`neckHackleColor`,`wingHighlightColor`,`tailColor`,`tailIridescent`,`plumagePattern`,`localColorCode`,`colorCategoryCode`,`lifecycleStage`,`ageDays`,`maturityScore`,`breedingStatus`,`gender`,`birthDate`,`sireId`,`damId`,`generationDepth`,`inbreedingCoefficient`,`geneticsJson`,`geneticsScore`,`vaccinationCount`,`injuryCount`,`staminaScore`,`healthScore`,`currentHealthStatus`,`aggressionIndex`,`enduranceScore`,`intelligenceScore`,`totalFights`,`fightWins`,`performanceScore`,`valuationScore`,`verifiedStatus`,`certificationLevel`,`estimatedValueInr`,`totalShows`,`showWins`,`bestPlacement`,`totalBreedingAttempts`,`successfulBreedings`,`totalOffspring`,`appearanceJson`,`metadataJson`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`,`isDeleted`,`deletedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DigitalTwinEntity) {
        statement.bindText(1, entity.twinId)
        statement.bindText(2, entity.birdId)
        val _tmpRegistryId: String? = entity.registryId
        if (_tmpRegistryId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpRegistryId)
        }
        statement.bindText(4, entity.ownerId)
        val _tmpBirdName: String? = entity.birdName
        if (_tmpBirdName == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpBirdName)
        }
        statement.bindText(6, entity.baseBreed)
        val _tmpStrainType: String? = entity.strainType
        if (_tmpStrainType == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpStrainType)
        }
        val _tmpLocalStrainName: String? = entity.localStrainName
        if (_tmpLocalStrainName == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLocalStrainName)
        }
        val _tmpGeneticPurityScore: Int? = entity.geneticPurityScore
        if (_tmpGeneticPurityScore == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpGeneticPurityScore.toLong())
        }
        val _tmpBodyType: String? = entity.bodyType
        if (_tmpBodyType == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpBodyType)
        }
        val _tmpBoneDensityScore: Int? = entity.boneDensityScore
        if (_tmpBoneDensityScore == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpBoneDensityScore.toLong())
        }
        val _tmpHeightCm: Double? = entity.heightCm
        if (_tmpHeightCm == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpHeightCm)
        }
        val _tmpWeightKg: Double? = entity.weightKg
        if (_tmpWeightKg == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpWeightKg)
        }
        val _tmpBeakType: String? = entity.beakType
        if (_tmpBeakType == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpBeakType)
        }
        val _tmpCombType: String? = entity.combType
        if (_tmpCombType == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpCombType)
        }
        val _tmpSkinColor: String? = entity.skinColor
        if (_tmpSkinColor == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpSkinColor)
        }
        val _tmpLegColor: String? = entity.legColor
        if (_tmpLegColor == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpLegColor)
        }
        val _tmpSpurType: String? = entity.spurType
        if (_tmpSpurType == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpSpurType)
        }
        val _tmpMorphologyScore: Int? = entity.morphologyScore
        if (_tmpMorphologyScore == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpMorphologyScore.toLong())
        }
        val _tmpPrimaryBodyColor: Long? = entity.primaryBodyColor
        if (_tmpPrimaryBodyColor == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpPrimaryBodyColor)
        }
        val _tmpNeckHackleColor: Long? = entity.neckHackleColor
        if (_tmpNeckHackleColor == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpNeckHackleColor)
        }
        val _tmpWingHighlightColor: Long? = entity.wingHighlightColor
        if (_tmpWingHighlightColor == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpWingHighlightColor)
        }
        val _tmpTailColor: Long? = entity.tailColor
        if (_tmpTailColor == null) {
          statement.bindNull(23)
        } else {
          statement.bindLong(23, _tmpTailColor)
        }
        val _tmp: Int = if (entity.tailIridescent) 1 else 0
        statement.bindLong(24, _tmp.toLong())
        val _tmpPlumagePattern: String? = entity.plumagePattern
        if (_tmpPlumagePattern == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpPlumagePattern)
        }
        val _tmpLocalColorCode: String? = entity.localColorCode
        if (_tmpLocalColorCode == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpLocalColorCode)
        }
        val _tmpColorCategoryCode: String? = entity.colorCategoryCode
        if (_tmpColorCategoryCode == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpColorCategoryCode)
        }
        statement.bindText(28, entity.lifecycleStage)
        val _tmpAgeDays: Int? = entity.ageDays
        if (_tmpAgeDays == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmpAgeDays.toLong())
        }
        val _tmpMaturityScore: Int? = entity.maturityScore
        if (_tmpMaturityScore == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpMaturityScore.toLong())
        }
        statement.bindText(31, entity.breedingStatus)
        val _tmpGender: String? = entity.gender
        if (_tmpGender == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpGender)
        }
        val _tmpBirthDate: Long? = entity.birthDate
        if (_tmpBirthDate == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmpBirthDate)
        }
        val _tmpSireId: String? = entity.sireId
        if (_tmpSireId == null) {
          statement.bindNull(34)
        } else {
          statement.bindText(34, _tmpSireId)
        }
        val _tmpDamId: String? = entity.damId
        if (_tmpDamId == null) {
          statement.bindNull(35)
        } else {
          statement.bindText(35, _tmpDamId)
        }
        statement.bindLong(36, entity.generationDepth.toLong())
        val _tmpInbreedingCoefficient: Double? = entity.inbreedingCoefficient
        if (_tmpInbreedingCoefficient == null) {
          statement.bindNull(37)
        } else {
          statement.bindDouble(37, _tmpInbreedingCoefficient)
        }
        val _tmpGeneticsJson: String? = entity.geneticsJson
        if (_tmpGeneticsJson == null) {
          statement.bindNull(38)
        } else {
          statement.bindText(38, _tmpGeneticsJson)
        }
        val _tmpGeneticsScore: Int? = entity.geneticsScore
        if (_tmpGeneticsScore == null) {
          statement.bindNull(39)
        } else {
          statement.bindLong(39, _tmpGeneticsScore.toLong())
        }
        statement.bindLong(40, entity.vaccinationCount.toLong())
        statement.bindLong(41, entity.injuryCount.toLong())
        val _tmpStaminaScore: Int? = entity.staminaScore
        if (_tmpStaminaScore == null) {
          statement.bindNull(42)
        } else {
          statement.bindLong(42, _tmpStaminaScore.toLong())
        }
        val _tmpHealthScore: Int? = entity.healthScore
        if (_tmpHealthScore == null) {
          statement.bindNull(43)
        } else {
          statement.bindLong(43, _tmpHealthScore.toLong())
        }
        statement.bindText(44, entity.currentHealthStatus)
        val _tmpAggressionIndex: Int? = entity.aggressionIndex
        if (_tmpAggressionIndex == null) {
          statement.bindNull(45)
        } else {
          statement.bindLong(45, _tmpAggressionIndex.toLong())
        }
        val _tmpEnduranceScore: Int? = entity.enduranceScore
        if (_tmpEnduranceScore == null) {
          statement.bindNull(46)
        } else {
          statement.bindLong(46, _tmpEnduranceScore.toLong())
        }
        val _tmpIntelligenceScore: Int? = entity.intelligenceScore
        if (_tmpIntelligenceScore == null) {
          statement.bindNull(47)
        } else {
          statement.bindLong(47, _tmpIntelligenceScore.toLong())
        }
        statement.bindLong(48, entity.totalFights.toLong())
        statement.bindLong(49, entity.fightWins.toLong())
        val _tmpPerformanceScore: Int? = entity.performanceScore
        if (_tmpPerformanceScore == null) {
          statement.bindNull(50)
        } else {
          statement.bindLong(50, _tmpPerformanceScore.toLong())
        }
        val _tmpValuationScore: Int? = entity.valuationScore
        if (_tmpValuationScore == null) {
          statement.bindNull(51)
        } else {
          statement.bindLong(51, _tmpValuationScore.toLong())
        }
        val _tmp_1: Int = if (entity.verifiedStatus) 1 else 0
        statement.bindLong(52, _tmp_1.toLong())
        statement.bindText(53, entity.certificationLevel)
        val _tmpEstimatedValueInr: Double? = entity.estimatedValueInr
        if (_tmpEstimatedValueInr == null) {
          statement.bindNull(54)
        } else {
          statement.bindDouble(54, _tmpEstimatedValueInr)
        }
        statement.bindLong(55, entity.totalShows.toLong())
        statement.bindLong(56, entity.showWins.toLong())
        val _tmpBestPlacement: Int? = entity.bestPlacement
        if (_tmpBestPlacement == null) {
          statement.bindNull(57)
        } else {
          statement.bindLong(57, _tmpBestPlacement.toLong())
        }
        statement.bindLong(58, entity.totalBreedingAttempts.toLong())
        statement.bindLong(59, entity.successfulBreedings.toLong())
        statement.bindLong(60, entity.totalOffspring.toLong())
        val _tmpAppearanceJson: String? = entity.appearanceJson
        if (_tmpAppearanceJson == null) {
          statement.bindNull(61)
        } else {
          statement.bindText(61, _tmpAppearanceJson)
        }
        statement.bindText(62, entity.metadataJson)
        statement.bindLong(63, entity.createdAt)
        statement.bindLong(64, entity.updatedAt)
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(65, _tmp_2.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(66)
        } else {
          statement.bindLong(66, _tmpSyncedAt)
        }
        val _tmp_3: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(67, _tmp_3.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(68)
        } else {
          statement.bindLong(68, _tmpDeletedAt)
        }
      }
    }
    this.__updateAdapterOfDigitalTwinEntity = object :
        EntityDeleteOrUpdateAdapter<DigitalTwinEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `digital_twins` SET `twinId` = ?,`birdId` = ?,`registryId` = ?,`ownerId` = ?,`birdName` = ?,`baseBreed` = ?,`strainType` = ?,`localStrainName` = ?,`geneticPurityScore` = ?,`bodyType` = ?,`boneDensityScore` = ?,`heightCm` = ?,`weightKg` = ?,`beakType` = ?,`combType` = ?,`skinColor` = ?,`legColor` = ?,`spurType` = ?,`morphologyScore` = ?,`primaryBodyColor` = ?,`neckHackleColor` = ?,`wingHighlightColor` = ?,`tailColor` = ?,`tailIridescent` = ?,`plumagePattern` = ?,`localColorCode` = ?,`colorCategoryCode` = ?,`lifecycleStage` = ?,`ageDays` = ?,`maturityScore` = ?,`breedingStatus` = ?,`gender` = ?,`birthDate` = ?,`sireId` = ?,`damId` = ?,`generationDepth` = ?,`inbreedingCoefficient` = ?,`geneticsJson` = ?,`geneticsScore` = ?,`vaccinationCount` = ?,`injuryCount` = ?,`staminaScore` = ?,`healthScore` = ?,`currentHealthStatus` = ?,`aggressionIndex` = ?,`enduranceScore` = ?,`intelligenceScore` = ?,`totalFights` = ?,`fightWins` = ?,`performanceScore` = ?,`valuationScore` = ?,`verifiedStatus` = ?,`certificationLevel` = ?,`estimatedValueInr` = ?,`totalShows` = ?,`showWins` = ?,`bestPlacement` = ?,`totalBreedingAttempts` = ?,`successfulBreedings` = ?,`totalOffspring` = ?,`appearanceJson` = ?,`metadataJson` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ?,`isDeleted` = ?,`deletedAt` = ? WHERE `twinId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DigitalTwinEntity) {
        statement.bindText(1, entity.twinId)
        statement.bindText(2, entity.birdId)
        val _tmpRegistryId: String? = entity.registryId
        if (_tmpRegistryId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpRegistryId)
        }
        statement.bindText(4, entity.ownerId)
        val _tmpBirdName: String? = entity.birdName
        if (_tmpBirdName == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpBirdName)
        }
        statement.bindText(6, entity.baseBreed)
        val _tmpStrainType: String? = entity.strainType
        if (_tmpStrainType == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpStrainType)
        }
        val _tmpLocalStrainName: String? = entity.localStrainName
        if (_tmpLocalStrainName == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLocalStrainName)
        }
        val _tmpGeneticPurityScore: Int? = entity.geneticPurityScore
        if (_tmpGeneticPurityScore == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpGeneticPurityScore.toLong())
        }
        val _tmpBodyType: String? = entity.bodyType
        if (_tmpBodyType == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpBodyType)
        }
        val _tmpBoneDensityScore: Int? = entity.boneDensityScore
        if (_tmpBoneDensityScore == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpBoneDensityScore.toLong())
        }
        val _tmpHeightCm: Double? = entity.heightCm
        if (_tmpHeightCm == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpHeightCm)
        }
        val _tmpWeightKg: Double? = entity.weightKg
        if (_tmpWeightKg == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpWeightKg)
        }
        val _tmpBeakType: String? = entity.beakType
        if (_tmpBeakType == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpBeakType)
        }
        val _tmpCombType: String? = entity.combType
        if (_tmpCombType == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpCombType)
        }
        val _tmpSkinColor: String? = entity.skinColor
        if (_tmpSkinColor == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpSkinColor)
        }
        val _tmpLegColor: String? = entity.legColor
        if (_tmpLegColor == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpLegColor)
        }
        val _tmpSpurType: String? = entity.spurType
        if (_tmpSpurType == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpSpurType)
        }
        val _tmpMorphologyScore: Int? = entity.morphologyScore
        if (_tmpMorphologyScore == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpMorphologyScore.toLong())
        }
        val _tmpPrimaryBodyColor: Long? = entity.primaryBodyColor
        if (_tmpPrimaryBodyColor == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpPrimaryBodyColor)
        }
        val _tmpNeckHackleColor: Long? = entity.neckHackleColor
        if (_tmpNeckHackleColor == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpNeckHackleColor)
        }
        val _tmpWingHighlightColor: Long? = entity.wingHighlightColor
        if (_tmpWingHighlightColor == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpWingHighlightColor)
        }
        val _tmpTailColor: Long? = entity.tailColor
        if (_tmpTailColor == null) {
          statement.bindNull(23)
        } else {
          statement.bindLong(23, _tmpTailColor)
        }
        val _tmp: Int = if (entity.tailIridescent) 1 else 0
        statement.bindLong(24, _tmp.toLong())
        val _tmpPlumagePattern: String? = entity.plumagePattern
        if (_tmpPlumagePattern == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpPlumagePattern)
        }
        val _tmpLocalColorCode: String? = entity.localColorCode
        if (_tmpLocalColorCode == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpLocalColorCode)
        }
        val _tmpColorCategoryCode: String? = entity.colorCategoryCode
        if (_tmpColorCategoryCode == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpColorCategoryCode)
        }
        statement.bindText(28, entity.lifecycleStage)
        val _tmpAgeDays: Int? = entity.ageDays
        if (_tmpAgeDays == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmpAgeDays.toLong())
        }
        val _tmpMaturityScore: Int? = entity.maturityScore
        if (_tmpMaturityScore == null) {
          statement.bindNull(30)
        } else {
          statement.bindLong(30, _tmpMaturityScore.toLong())
        }
        statement.bindText(31, entity.breedingStatus)
        val _tmpGender: String? = entity.gender
        if (_tmpGender == null) {
          statement.bindNull(32)
        } else {
          statement.bindText(32, _tmpGender)
        }
        val _tmpBirthDate: Long? = entity.birthDate
        if (_tmpBirthDate == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmpBirthDate)
        }
        val _tmpSireId: String? = entity.sireId
        if (_tmpSireId == null) {
          statement.bindNull(34)
        } else {
          statement.bindText(34, _tmpSireId)
        }
        val _tmpDamId: String? = entity.damId
        if (_tmpDamId == null) {
          statement.bindNull(35)
        } else {
          statement.bindText(35, _tmpDamId)
        }
        statement.bindLong(36, entity.generationDepth.toLong())
        val _tmpInbreedingCoefficient: Double? = entity.inbreedingCoefficient
        if (_tmpInbreedingCoefficient == null) {
          statement.bindNull(37)
        } else {
          statement.bindDouble(37, _tmpInbreedingCoefficient)
        }
        val _tmpGeneticsJson: String? = entity.geneticsJson
        if (_tmpGeneticsJson == null) {
          statement.bindNull(38)
        } else {
          statement.bindText(38, _tmpGeneticsJson)
        }
        val _tmpGeneticsScore: Int? = entity.geneticsScore
        if (_tmpGeneticsScore == null) {
          statement.bindNull(39)
        } else {
          statement.bindLong(39, _tmpGeneticsScore.toLong())
        }
        statement.bindLong(40, entity.vaccinationCount.toLong())
        statement.bindLong(41, entity.injuryCount.toLong())
        val _tmpStaminaScore: Int? = entity.staminaScore
        if (_tmpStaminaScore == null) {
          statement.bindNull(42)
        } else {
          statement.bindLong(42, _tmpStaminaScore.toLong())
        }
        val _tmpHealthScore: Int? = entity.healthScore
        if (_tmpHealthScore == null) {
          statement.bindNull(43)
        } else {
          statement.bindLong(43, _tmpHealthScore.toLong())
        }
        statement.bindText(44, entity.currentHealthStatus)
        val _tmpAggressionIndex: Int? = entity.aggressionIndex
        if (_tmpAggressionIndex == null) {
          statement.bindNull(45)
        } else {
          statement.bindLong(45, _tmpAggressionIndex.toLong())
        }
        val _tmpEnduranceScore: Int? = entity.enduranceScore
        if (_tmpEnduranceScore == null) {
          statement.bindNull(46)
        } else {
          statement.bindLong(46, _tmpEnduranceScore.toLong())
        }
        val _tmpIntelligenceScore: Int? = entity.intelligenceScore
        if (_tmpIntelligenceScore == null) {
          statement.bindNull(47)
        } else {
          statement.bindLong(47, _tmpIntelligenceScore.toLong())
        }
        statement.bindLong(48, entity.totalFights.toLong())
        statement.bindLong(49, entity.fightWins.toLong())
        val _tmpPerformanceScore: Int? = entity.performanceScore
        if (_tmpPerformanceScore == null) {
          statement.bindNull(50)
        } else {
          statement.bindLong(50, _tmpPerformanceScore.toLong())
        }
        val _tmpValuationScore: Int? = entity.valuationScore
        if (_tmpValuationScore == null) {
          statement.bindNull(51)
        } else {
          statement.bindLong(51, _tmpValuationScore.toLong())
        }
        val _tmp_1: Int = if (entity.verifiedStatus) 1 else 0
        statement.bindLong(52, _tmp_1.toLong())
        statement.bindText(53, entity.certificationLevel)
        val _tmpEstimatedValueInr: Double? = entity.estimatedValueInr
        if (_tmpEstimatedValueInr == null) {
          statement.bindNull(54)
        } else {
          statement.bindDouble(54, _tmpEstimatedValueInr)
        }
        statement.bindLong(55, entity.totalShows.toLong())
        statement.bindLong(56, entity.showWins.toLong())
        val _tmpBestPlacement: Int? = entity.bestPlacement
        if (_tmpBestPlacement == null) {
          statement.bindNull(57)
        } else {
          statement.bindLong(57, _tmpBestPlacement.toLong())
        }
        statement.bindLong(58, entity.totalBreedingAttempts.toLong())
        statement.bindLong(59, entity.successfulBreedings.toLong())
        statement.bindLong(60, entity.totalOffspring.toLong())
        val _tmpAppearanceJson: String? = entity.appearanceJson
        if (_tmpAppearanceJson == null) {
          statement.bindNull(61)
        } else {
          statement.bindText(61, _tmpAppearanceJson)
        }
        statement.bindText(62, entity.metadataJson)
        statement.bindLong(63, entity.createdAt)
        statement.bindLong(64, entity.updatedAt)
        val _tmp_2: Int = if (entity.dirty) 1 else 0
        statement.bindLong(65, _tmp_2.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(66)
        } else {
          statement.bindLong(66, _tmpSyncedAt)
        }
        val _tmp_3: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(67, _tmp_3.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(68)
        } else {
          statement.bindLong(68, _tmpDeletedAt)
        }
        statement.bindText(69, entity.twinId)
      }
    }
  }

  public override suspend fun insertOrReplace(twin: DigitalTwinEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDigitalTwinEntity.insert(_connection, twin)
  }

  public override suspend fun insertAll(twins: List<DigitalTwinEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDigitalTwinEntity.insert(_connection, twins)
  }

  public override suspend fun update(twin: DigitalTwinEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfDigitalTwinEntity.handle(_connection, twin)
  }

  public override suspend fun getById(twinId: String): DigitalTwinEntity? {
    val _sql: String = "SELECT * FROM digital_twins WHERE twinId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, twinId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: DigitalTwinEntity?
        if (_stmt.step()) {
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _result =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByBirdId(birdId: String): DigitalTwinEntity? {
    val _sql: String = "SELECT * FROM digital_twins WHERE birdId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: DigitalTwinEntity?
        if (_stmt.step()) {
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _result =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByBirdId(birdId: String): Flow<DigitalTwinEntity?> {
    val _sql: String = "SELECT * FROM digital_twins WHERE birdId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, birdId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: DigitalTwinEntity?
        if (_stmt.step()) {
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _result =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByRegistryId(registryId: String): DigitalTwinEntity? {
    val _sql: String = "SELECT * FROM digital_twins WHERE registryId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, registryId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: DigitalTwinEntity?
        if (_stmt.step()) {
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _result =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllActive(): List<DigitalTwinEntity> {
    val _sql: String = "SELECT * FROM digital_twins WHERE isDeleted = 0 ORDER BY updatedAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getByOwner(ownerId: String): Flow<List<DigitalTwinEntity>> {
    val _sql: String =
        "SELECT * FROM digital_twins WHERE ownerId = ? AND isDeleted = 0 ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByOwnerOnce(ownerId: String): List<DigitalTwinEntity> {
    val _sql: String =
        "SELECT * FROM digital_twins WHERE ownerId = ? AND isDeleted = 0 ORDER BY updatedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getByStrain(ownerId: String, strainType: String):
      Flow<List<DigitalTwinEntity>> {
    val _sql: String =
        "SELECT * FROM digital_twins WHERE ownerId = ? AND strainType = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, strainType)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getByLifecycleStage(ownerId: String, stage: String):
      Flow<List<DigitalTwinEntity>> {
    val _sql: String =
        "SELECT * FROM digital_twins WHERE ownerId = ? AND lifecycleStage = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, stage)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getBreedingEligible(ownerId: String): Flow<List<DigitalTwinEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM digital_twins 
        |        WHERE ownerId = ? AND isDeleted = 0
        |        AND lifecycleStage IN ('ADULT_FIGHTER', 'BREEDER_PRIME')
        |        ORDER BY valuationScore DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getShowEligible(ownerId: String): Flow<List<DigitalTwinEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM digital_twins 
        |        WHERE ownerId = ? AND isDeleted = 0
        |        AND lifecycleStage IN ('PRE_ADULT', 'ADULT_FIGHTER', 'BREEDER_PRIME')
        |        AND currentHealthStatus = 'HEALTHY'
        |        ORDER BY valuationScore DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTopValuedBirds(ownerId: String, limit: Int):
      Flow<List<DigitalTwinEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM digital_twins 
        |        WHERE ownerId = ? AND isDeleted = 0
        |        AND valuationScore IS NOT NULL
        |        ORDER BY valuationScore DESC 
        |        LIMIT ?
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCertifiedBirds(ownerId: String): Flow<List<DigitalTwinEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM digital_twins 
        |        WHERE ownerId = ? AND isDeleted = 0
        |        AND certificationLevel != 'NONE'
        |        ORDER BY 
        |            CASE certificationLevel 
        |                WHEN 'CHAMPION' THEN 0 
        |                WHEN 'VERIFIED' THEN 1 
        |                WHEN 'REGISTERED' THEN 2 
        |            END
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("digital_twins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countByOwner(ownerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM digital_twins WHERE ownerId = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
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

  public override suspend fun countByStage(ownerId: String, stage: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM digital_twins WHERE ownerId = ? AND lifecycleStage = ? AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, stage)
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

  public override suspend fun averageValuationScore(ownerId: String): Double? {
    val _sql: String =
        "SELECT AVG(valuationScore) FROM digital_twins WHERE ownerId = ? AND valuationScore IS NOT NULL AND isDeleted = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirtyRecords(): List<DigitalTwinEntity> {
    val _sql: String = "SELECT * FROM digital_twins WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfTwinId: Int = getColumnIndexOrThrow(_stmt, "twinId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfRegistryId: Int = getColumnIndexOrThrow(_stmt, "registryId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBaseBreed: Int = getColumnIndexOrThrow(_stmt, "baseBreed")
        val _columnIndexOfStrainType: Int = getColumnIndexOrThrow(_stmt, "strainType")
        val _columnIndexOfLocalStrainName: Int = getColumnIndexOrThrow(_stmt, "localStrainName")
        val _columnIndexOfGeneticPurityScore: Int = getColumnIndexOrThrow(_stmt,
            "geneticPurityScore")
        val _columnIndexOfBodyType: Int = getColumnIndexOrThrow(_stmt, "bodyType")
        val _columnIndexOfBoneDensityScore: Int = getColumnIndexOrThrow(_stmt, "boneDensityScore")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfWeightKg: Int = getColumnIndexOrThrow(_stmt, "weightKg")
        val _columnIndexOfBeakType: Int = getColumnIndexOrThrow(_stmt, "beakType")
        val _columnIndexOfCombType: Int = getColumnIndexOrThrow(_stmt, "combType")
        val _columnIndexOfSkinColor: Int = getColumnIndexOrThrow(_stmt, "skinColor")
        val _columnIndexOfLegColor: Int = getColumnIndexOrThrow(_stmt, "legColor")
        val _columnIndexOfSpurType: Int = getColumnIndexOrThrow(_stmt, "spurType")
        val _columnIndexOfMorphologyScore: Int = getColumnIndexOrThrow(_stmt, "morphologyScore")
        val _columnIndexOfPrimaryBodyColor: Int = getColumnIndexOrThrow(_stmt, "primaryBodyColor")
        val _columnIndexOfNeckHackleColor: Int = getColumnIndexOrThrow(_stmt, "neckHackleColor")
        val _columnIndexOfWingHighlightColor: Int = getColumnIndexOrThrow(_stmt,
            "wingHighlightColor")
        val _columnIndexOfTailColor: Int = getColumnIndexOrThrow(_stmt, "tailColor")
        val _columnIndexOfTailIridescent: Int = getColumnIndexOrThrow(_stmt, "tailIridescent")
        val _columnIndexOfPlumagePattern: Int = getColumnIndexOrThrow(_stmt, "plumagePattern")
        val _columnIndexOfLocalColorCode: Int = getColumnIndexOrThrow(_stmt, "localColorCode")
        val _columnIndexOfColorCategoryCode: Int = getColumnIndexOrThrow(_stmt, "colorCategoryCode")
        val _columnIndexOfLifecycleStage: Int = getColumnIndexOrThrow(_stmt, "lifecycleStage")
        val _columnIndexOfAgeDays: Int = getColumnIndexOrThrow(_stmt, "ageDays")
        val _columnIndexOfMaturityScore: Int = getColumnIndexOrThrow(_stmt, "maturityScore")
        val _columnIndexOfBreedingStatus: Int = getColumnIndexOrThrow(_stmt, "breedingStatus")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfBirthDate: Int = getColumnIndexOrThrow(_stmt, "birthDate")
        val _columnIndexOfSireId: Int = getColumnIndexOrThrow(_stmt, "sireId")
        val _columnIndexOfDamId: Int = getColumnIndexOrThrow(_stmt, "damId")
        val _columnIndexOfGenerationDepth: Int = getColumnIndexOrThrow(_stmt, "generationDepth")
        val _columnIndexOfInbreedingCoefficient: Int = getColumnIndexOrThrow(_stmt,
            "inbreedingCoefficient")
        val _columnIndexOfGeneticsJson: Int = getColumnIndexOrThrow(_stmt, "geneticsJson")
        val _columnIndexOfGeneticsScore: Int = getColumnIndexOrThrow(_stmt, "geneticsScore")
        val _columnIndexOfVaccinationCount: Int = getColumnIndexOrThrow(_stmt, "vaccinationCount")
        val _columnIndexOfInjuryCount: Int = getColumnIndexOrThrow(_stmt, "injuryCount")
        val _columnIndexOfStaminaScore: Int = getColumnIndexOrThrow(_stmt, "staminaScore")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfCurrentHealthStatus: Int = getColumnIndexOrThrow(_stmt,
            "currentHealthStatus")
        val _columnIndexOfAggressionIndex: Int = getColumnIndexOrThrow(_stmt, "aggressionIndex")
        val _columnIndexOfEnduranceScore: Int = getColumnIndexOrThrow(_stmt, "enduranceScore")
        val _columnIndexOfIntelligenceScore: Int = getColumnIndexOrThrow(_stmt, "intelligenceScore")
        val _columnIndexOfTotalFights: Int = getColumnIndexOrThrow(_stmt, "totalFights")
        val _columnIndexOfFightWins: Int = getColumnIndexOrThrow(_stmt, "fightWins")
        val _columnIndexOfPerformanceScore: Int = getColumnIndexOrThrow(_stmt, "performanceScore")
        val _columnIndexOfValuationScore: Int = getColumnIndexOrThrow(_stmt, "valuationScore")
        val _columnIndexOfVerifiedStatus: Int = getColumnIndexOrThrow(_stmt, "verifiedStatus")
        val _columnIndexOfCertificationLevel: Int = getColumnIndexOrThrow(_stmt,
            "certificationLevel")
        val _columnIndexOfEstimatedValueInr: Int = getColumnIndexOrThrow(_stmt, "estimatedValueInr")
        val _columnIndexOfTotalShows: Int = getColumnIndexOrThrow(_stmt, "totalShows")
        val _columnIndexOfShowWins: Int = getColumnIndexOrThrow(_stmt, "showWins")
        val _columnIndexOfBestPlacement: Int = getColumnIndexOrThrow(_stmt, "bestPlacement")
        val _columnIndexOfTotalBreedingAttempts: Int = getColumnIndexOrThrow(_stmt,
            "totalBreedingAttempts")
        val _columnIndexOfSuccessfulBreedings: Int = getColumnIndexOrThrow(_stmt,
            "successfulBreedings")
        val _columnIndexOfTotalOffspring: Int = getColumnIndexOrThrow(_stmt, "totalOffspring")
        val _columnIndexOfAppearanceJson: Int = getColumnIndexOrThrow(_stmt, "appearanceJson")
        val _columnIndexOfMetadataJson: Int = getColumnIndexOrThrow(_stmt, "metadataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<DigitalTwinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DigitalTwinEntity
          val _tmpTwinId: String
          _tmpTwinId = _stmt.getText(_columnIndexOfTwinId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpRegistryId: String?
          if (_stmt.isNull(_columnIndexOfRegistryId)) {
            _tmpRegistryId = null
          } else {
            _tmpRegistryId = _stmt.getText(_columnIndexOfRegistryId)
          }
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String?
          if (_stmt.isNull(_columnIndexOfBirdName)) {
            _tmpBirdName = null
          } else {
            _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          }
          val _tmpBaseBreed: String
          _tmpBaseBreed = _stmt.getText(_columnIndexOfBaseBreed)
          val _tmpStrainType: String?
          if (_stmt.isNull(_columnIndexOfStrainType)) {
            _tmpStrainType = null
          } else {
            _tmpStrainType = _stmt.getText(_columnIndexOfStrainType)
          }
          val _tmpLocalStrainName: String?
          if (_stmt.isNull(_columnIndexOfLocalStrainName)) {
            _tmpLocalStrainName = null
          } else {
            _tmpLocalStrainName = _stmt.getText(_columnIndexOfLocalStrainName)
          }
          val _tmpGeneticPurityScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticPurityScore)) {
            _tmpGeneticPurityScore = null
          } else {
            _tmpGeneticPurityScore = _stmt.getLong(_columnIndexOfGeneticPurityScore).toInt()
          }
          val _tmpBodyType: String?
          if (_stmt.isNull(_columnIndexOfBodyType)) {
            _tmpBodyType = null
          } else {
            _tmpBodyType = _stmt.getText(_columnIndexOfBodyType)
          }
          val _tmpBoneDensityScore: Int?
          if (_stmt.isNull(_columnIndexOfBoneDensityScore)) {
            _tmpBoneDensityScore = null
          } else {
            _tmpBoneDensityScore = _stmt.getLong(_columnIndexOfBoneDensityScore).toInt()
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpWeightKg: Double?
          if (_stmt.isNull(_columnIndexOfWeightKg)) {
            _tmpWeightKg = null
          } else {
            _tmpWeightKg = _stmt.getDouble(_columnIndexOfWeightKg)
          }
          val _tmpBeakType: String?
          if (_stmt.isNull(_columnIndexOfBeakType)) {
            _tmpBeakType = null
          } else {
            _tmpBeakType = _stmt.getText(_columnIndexOfBeakType)
          }
          val _tmpCombType: String?
          if (_stmt.isNull(_columnIndexOfCombType)) {
            _tmpCombType = null
          } else {
            _tmpCombType = _stmt.getText(_columnIndexOfCombType)
          }
          val _tmpSkinColor: String?
          if (_stmt.isNull(_columnIndexOfSkinColor)) {
            _tmpSkinColor = null
          } else {
            _tmpSkinColor = _stmt.getText(_columnIndexOfSkinColor)
          }
          val _tmpLegColor: String?
          if (_stmt.isNull(_columnIndexOfLegColor)) {
            _tmpLegColor = null
          } else {
            _tmpLegColor = _stmt.getText(_columnIndexOfLegColor)
          }
          val _tmpSpurType: String?
          if (_stmt.isNull(_columnIndexOfSpurType)) {
            _tmpSpurType = null
          } else {
            _tmpSpurType = _stmt.getText(_columnIndexOfSpurType)
          }
          val _tmpMorphologyScore: Int?
          if (_stmt.isNull(_columnIndexOfMorphologyScore)) {
            _tmpMorphologyScore = null
          } else {
            _tmpMorphologyScore = _stmt.getLong(_columnIndexOfMorphologyScore).toInt()
          }
          val _tmpPrimaryBodyColor: Long?
          if (_stmt.isNull(_columnIndexOfPrimaryBodyColor)) {
            _tmpPrimaryBodyColor = null
          } else {
            _tmpPrimaryBodyColor = _stmt.getLong(_columnIndexOfPrimaryBodyColor)
          }
          val _tmpNeckHackleColor: Long?
          if (_stmt.isNull(_columnIndexOfNeckHackleColor)) {
            _tmpNeckHackleColor = null
          } else {
            _tmpNeckHackleColor = _stmt.getLong(_columnIndexOfNeckHackleColor)
          }
          val _tmpWingHighlightColor: Long?
          if (_stmt.isNull(_columnIndexOfWingHighlightColor)) {
            _tmpWingHighlightColor = null
          } else {
            _tmpWingHighlightColor = _stmt.getLong(_columnIndexOfWingHighlightColor)
          }
          val _tmpTailColor: Long?
          if (_stmt.isNull(_columnIndexOfTailColor)) {
            _tmpTailColor = null
          } else {
            _tmpTailColor = _stmt.getLong(_columnIndexOfTailColor)
          }
          val _tmpTailIridescent: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTailIridescent).toInt()
          _tmpTailIridescent = _tmp != 0
          val _tmpPlumagePattern: String?
          if (_stmt.isNull(_columnIndexOfPlumagePattern)) {
            _tmpPlumagePattern = null
          } else {
            _tmpPlumagePattern = _stmt.getText(_columnIndexOfPlumagePattern)
          }
          val _tmpLocalColorCode: String?
          if (_stmt.isNull(_columnIndexOfLocalColorCode)) {
            _tmpLocalColorCode = null
          } else {
            _tmpLocalColorCode = _stmt.getText(_columnIndexOfLocalColorCode)
          }
          val _tmpColorCategoryCode: String?
          if (_stmt.isNull(_columnIndexOfColorCategoryCode)) {
            _tmpColorCategoryCode = null
          } else {
            _tmpColorCategoryCode = _stmt.getText(_columnIndexOfColorCategoryCode)
          }
          val _tmpLifecycleStage: String
          _tmpLifecycleStage = _stmt.getText(_columnIndexOfLifecycleStage)
          val _tmpAgeDays: Int?
          if (_stmt.isNull(_columnIndexOfAgeDays)) {
            _tmpAgeDays = null
          } else {
            _tmpAgeDays = _stmt.getLong(_columnIndexOfAgeDays).toInt()
          }
          val _tmpMaturityScore: Int?
          if (_stmt.isNull(_columnIndexOfMaturityScore)) {
            _tmpMaturityScore = null
          } else {
            _tmpMaturityScore = _stmt.getLong(_columnIndexOfMaturityScore).toInt()
          }
          val _tmpBreedingStatus: String
          _tmpBreedingStatus = _stmt.getText(_columnIndexOfBreedingStatus)
          val _tmpGender: String?
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender)
          }
          val _tmpBirthDate: Long?
          if (_stmt.isNull(_columnIndexOfBirthDate)) {
            _tmpBirthDate = null
          } else {
            _tmpBirthDate = _stmt.getLong(_columnIndexOfBirthDate)
          }
          val _tmpSireId: String?
          if (_stmt.isNull(_columnIndexOfSireId)) {
            _tmpSireId = null
          } else {
            _tmpSireId = _stmt.getText(_columnIndexOfSireId)
          }
          val _tmpDamId: String?
          if (_stmt.isNull(_columnIndexOfDamId)) {
            _tmpDamId = null
          } else {
            _tmpDamId = _stmt.getText(_columnIndexOfDamId)
          }
          val _tmpGenerationDepth: Int
          _tmpGenerationDepth = _stmt.getLong(_columnIndexOfGenerationDepth).toInt()
          val _tmpInbreedingCoefficient: Double?
          if (_stmt.isNull(_columnIndexOfInbreedingCoefficient)) {
            _tmpInbreedingCoefficient = null
          } else {
            _tmpInbreedingCoefficient = _stmt.getDouble(_columnIndexOfInbreedingCoefficient)
          }
          val _tmpGeneticsJson: String?
          if (_stmt.isNull(_columnIndexOfGeneticsJson)) {
            _tmpGeneticsJson = null
          } else {
            _tmpGeneticsJson = _stmt.getText(_columnIndexOfGeneticsJson)
          }
          val _tmpGeneticsScore: Int?
          if (_stmt.isNull(_columnIndexOfGeneticsScore)) {
            _tmpGeneticsScore = null
          } else {
            _tmpGeneticsScore = _stmt.getLong(_columnIndexOfGeneticsScore).toInt()
          }
          val _tmpVaccinationCount: Int
          _tmpVaccinationCount = _stmt.getLong(_columnIndexOfVaccinationCount).toInt()
          val _tmpInjuryCount: Int
          _tmpInjuryCount = _stmt.getLong(_columnIndexOfInjuryCount).toInt()
          val _tmpStaminaScore: Int?
          if (_stmt.isNull(_columnIndexOfStaminaScore)) {
            _tmpStaminaScore = null
          } else {
            _tmpStaminaScore = _stmt.getLong(_columnIndexOfStaminaScore).toInt()
          }
          val _tmpHealthScore: Int?
          if (_stmt.isNull(_columnIndexOfHealthScore)) {
            _tmpHealthScore = null
          } else {
            _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          }
          val _tmpCurrentHealthStatus: String
          _tmpCurrentHealthStatus = _stmt.getText(_columnIndexOfCurrentHealthStatus)
          val _tmpAggressionIndex: Int?
          if (_stmt.isNull(_columnIndexOfAggressionIndex)) {
            _tmpAggressionIndex = null
          } else {
            _tmpAggressionIndex = _stmt.getLong(_columnIndexOfAggressionIndex).toInt()
          }
          val _tmpEnduranceScore: Int?
          if (_stmt.isNull(_columnIndexOfEnduranceScore)) {
            _tmpEnduranceScore = null
          } else {
            _tmpEnduranceScore = _stmt.getLong(_columnIndexOfEnduranceScore).toInt()
          }
          val _tmpIntelligenceScore: Int?
          if (_stmt.isNull(_columnIndexOfIntelligenceScore)) {
            _tmpIntelligenceScore = null
          } else {
            _tmpIntelligenceScore = _stmt.getLong(_columnIndexOfIntelligenceScore).toInt()
          }
          val _tmpTotalFights: Int
          _tmpTotalFights = _stmt.getLong(_columnIndexOfTotalFights).toInt()
          val _tmpFightWins: Int
          _tmpFightWins = _stmt.getLong(_columnIndexOfFightWins).toInt()
          val _tmpPerformanceScore: Int?
          if (_stmt.isNull(_columnIndexOfPerformanceScore)) {
            _tmpPerformanceScore = null
          } else {
            _tmpPerformanceScore = _stmt.getLong(_columnIndexOfPerformanceScore).toInt()
          }
          val _tmpValuationScore: Int?
          if (_stmt.isNull(_columnIndexOfValuationScore)) {
            _tmpValuationScore = null
          } else {
            _tmpValuationScore = _stmt.getLong(_columnIndexOfValuationScore).toInt()
          }
          val _tmpVerifiedStatus: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVerifiedStatus).toInt()
          _tmpVerifiedStatus = _tmp_1 != 0
          val _tmpCertificationLevel: String
          _tmpCertificationLevel = _stmt.getText(_columnIndexOfCertificationLevel)
          val _tmpEstimatedValueInr: Double?
          if (_stmt.isNull(_columnIndexOfEstimatedValueInr)) {
            _tmpEstimatedValueInr = null
          } else {
            _tmpEstimatedValueInr = _stmt.getDouble(_columnIndexOfEstimatedValueInr)
          }
          val _tmpTotalShows: Int
          _tmpTotalShows = _stmt.getLong(_columnIndexOfTotalShows).toInt()
          val _tmpShowWins: Int
          _tmpShowWins = _stmt.getLong(_columnIndexOfShowWins).toInt()
          val _tmpBestPlacement: Int?
          if (_stmt.isNull(_columnIndexOfBestPlacement)) {
            _tmpBestPlacement = null
          } else {
            _tmpBestPlacement = _stmt.getLong(_columnIndexOfBestPlacement).toInt()
          }
          val _tmpTotalBreedingAttempts: Int
          _tmpTotalBreedingAttempts = _stmt.getLong(_columnIndexOfTotalBreedingAttempts).toInt()
          val _tmpSuccessfulBreedings: Int
          _tmpSuccessfulBreedings = _stmt.getLong(_columnIndexOfSuccessfulBreedings).toInt()
          val _tmpTotalOffspring: Int
          _tmpTotalOffspring = _stmt.getLong(_columnIndexOfTotalOffspring).toInt()
          val _tmpAppearanceJson: String?
          if (_stmt.isNull(_columnIndexOfAppearanceJson)) {
            _tmpAppearanceJson = null
          } else {
            _tmpAppearanceJson = _stmt.getText(_columnIndexOfAppearanceJson)
          }
          val _tmpMetadataJson: String
          _tmpMetadataJson = _stmt.getText(_columnIndexOfMetadataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_2 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpIsDeleted: Boolean
          val _tmp_3: Int
          _tmp_3 = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp_3 != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              DigitalTwinEntity(_tmpTwinId,_tmpBirdId,_tmpRegistryId,_tmpOwnerId,_tmpBirdName,_tmpBaseBreed,_tmpStrainType,_tmpLocalStrainName,_tmpGeneticPurityScore,_tmpBodyType,_tmpBoneDensityScore,_tmpHeightCm,_tmpWeightKg,_tmpBeakType,_tmpCombType,_tmpSkinColor,_tmpLegColor,_tmpSpurType,_tmpMorphologyScore,_tmpPrimaryBodyColor,_tmpNeckHackleColor,_tmpWingHighlightColor,_tmpTailColor,_tmpTailIridescent,_tmpPlumagePattern,_tmpLocalColorCode,_tmpColorCategoryCode,_tmpLifecycleStage,_tmpAgeDays,_tmpMaturityScore,_tmpBreedingStatus,_tmpGender,_tmpBirthDate,_tmpSireId,_tmpDamId,_tmpGenerationDepth,_tmpInbreedingCoefficient,_tmpGeneticsJson,_tmpGeneticsScore,_tmpVaccinationCount,_tmpInjuryCount,_tmpStaminaScore,_tmpHealthScore,_tmpCurrentHealthStatus,_tmpAggressionIndex,_tmpEnduranceScore,_tmpIntelligenceScore,_tmpTotalFights,_tmpFightWins,_tmpPerformanceScore,_tmpValuationScore,_tmpVerifiedStatus,_tmpCertificationLevel,_tmpEstimatedValueInr,_tmpTotalShows,_tmpShowWins,_tmpBestPlacement,_tmpTotalBreedingAttempts,_tmpSuccessfulBreedings,_tmpTotalOffspring,_tmpAppearanceJson,_tmpMetadataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMorphologyScore(
    twinId: String,
    score: Int,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE digital_twins SET morphologyScore = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, score.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateGeneticsScore(
    twinId: String,
    score: Int,
    now: Long,
  ) {
    val _sql: String = "UPDATE digital_twins SET geneticsScore = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, score.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updatePerformanceScore(
    twinId: String,
    score: Int,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE digital_twins SET performanceScore = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, score.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateHealthScore(
    twinId: String,
    score: Int,
    now: Long,
  ) {
    val _sql: String = "UPDATE digital_twins SET healthScore = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, score.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateValuationScore(
    twinId: String,
    score: Int,
    now: Long,
  ) {
    val _sql: String = "UPDATE digital_twins SET valuationScore = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, score.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateFightStats(
    twinId: String,
    totalFights: Int,
    wins: Int,
    now: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE digital_twins SET 
        |            totalFights = ?, 
        |            fightWins = ?, 
        |            updatedAt = ? 
        |        WHERE twinId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, totalFights.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, wins.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateLifecycleStage(
    twinId: String,
    stage: String,
    ageDays: Int,
    now: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE digital_twins SET 
        |            lifecycleStage = ?, 
        |            ageDays = ?, 
        |            updatedAt = ? 
        |        WHERE twinId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, stage)
        _argIndex = 2
        _stmt.bindLong(_argIndex, ageDays.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateWeight(
    twinId: String,
    weightKg: Double,
    now: Long,
  ) {
    val _sql: String = "UPDATE digital_twins SET weightKg = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, weightKg)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateBreedingStatus(
    twinId: String,
    status: String,
    now: Long,
  ) {
    val _sql: String = "UPDATE digital_twins SET breedingStatus = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateCertification(
    twinId: String,
    level: String,
    verified: Boolean,
    now: Long,
  ) {
    val _sql: String =
        "UPDATE digital_twins SET certificationLevel = ?, verifiedStatus = ?, updatedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, level)
        _argIndex = 2
        val _tmp: Int = if (verified) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSynced(twinId: String, syncedAt: Long) {
    val _sql: String = "UPDATE digital_twins SET dirty = 0, syncedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun softDelete(twinId: String, now: Long) {
    val _sql: String = "UPDATE digital_twins SET isDeleted = 1, deletedAt = ? WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindText(_argIndex, twinId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hardDelete(twinId: String) {
    val _sql: String = "DELETE FROM digital_twins WHERE twinId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, twinId)
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
