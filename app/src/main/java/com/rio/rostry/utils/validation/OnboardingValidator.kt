package com.rio.rostry.utils.validation

object OnboardingValidator {
    data class CoreDetailsState(
        val name: String = "",
        val birthDate: Long? = null,
        val birthPlace: String = "",
        val gender: String = "Unknown",
        val weightGrams: String = "",
        val heightCm: String = "",
        val colors: String = "",
        val breed: String = "",
        val physicalId: String = "",
        val vaccinationRecords: String = "",
        val healthStatus: String = "OK",
        val breedingHistory: String = "",
        val awards: String = "",
        val location: String = ""
    )

    data class LineageState(
        val maleParentId: String? = null,
        val femaleParentId: String? = null
    )

    data class MediaState(
        val photoUris: List<String> = emptyList(),
        val videoUris: List<String> = emptyList(),
        val documentUris: List<String> = emptyList()
    )

    enum class AgeGroup { CHICK, JUVENILE, ADULT, BREEDER }

    fun validatePathSelection(isTraceable: Boolean?): Map<String, String> =
        if (isTraceable == null) mapOf("path" to "Please select traceable or non-traceable") else emptyMap()

    fun validateAgeGroup(ageGroup: AgeGroup?): Map<String, String> =
        if (ageGroup == null) mapOf("ageGroup" to "Please select an age group") else emptyMap()

    fun validateCoreDetails(
        details: CoreDetailsState,
        ageGroup: AgeGroup,
        isTraceable: Boolean
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (details.name.isBlank()) errors["name"] = "Name is required"
        if (details.birthDate == null) errors["birthDate"] = "Birth date is required"
        if (details.location.isBlank()) errors["location"] = "Location is required"
        val weight = details.weightGrams.toDoubleOrNull()
        if (weight == null || weight <= 0.0) errors["weightGrams"] = "Valid weight (g) required"
        if (isTraceable && details.gender.equals("Unknown", ignoreCase = true)) {
            errors["gender"] = "Gender is required for traceable birds"
        }
        when (ageGroup) {
            AgeGroup.CHICK -> {
                if (isTraceable && details.birthPlace.isBlank()) errors["birthPlace"] = "Birth place is required"
                if (isTraceable && details.vaccinationRecords.isBlank()) errors["vaccinationRecords"] = "Vaccination records required"
            }
            AgeGroup.ADULT -> {
                if (isTraceable && details.breed.isBlank()) errors["breed"] = "Breed is required"
                if (isTraceable && details.physicalId.isBlank()) errors["physicalId"] = "Physical ID required"
            }
            AgeGroup.BREEDER -> {
                if (isTraceable && details.breedingHistory.isBlank()) errors["breedingHistory"] = "Breeding history required"
            }
            AgeGroup.JUVENILE -> {}
        }
        return errors
    }

    fun validateLineage(lineage: LineageState, isTraceable: Boolean): Map<String, String> {
        return if (isTraceable && (lineage.maleParentId == null || lineage.femaleParentId == null)) {
            mapOf("lineage" to "Both parents are required for traceable birds")
        } else emptyMap()
    }

    fun validateMedia(media: MediaState, isTraceable: Boolean): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (media.photoUris.isEmpty()) errors["photos"] = "At least 1 photo is required"
        if (isTraceable && media.documentUris.isEmpty()) errors["documents"] = "Proof documents are required for traceable birds"
        return errors
    }
}
