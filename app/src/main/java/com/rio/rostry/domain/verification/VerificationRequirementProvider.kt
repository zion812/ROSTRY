package com.rio.rostry.domain.verification

import com.rio.rostry.domain.model.UpgradeType
import javax.inject.Inject

data class VerificationRequirements(
    val requiredImages: List<String>,
    val requiredDocuments: List<String>
)

interface VerificationRequirementProvider {
    suspend fun getRequirements(upgradeType: UpgradeType): VerificationRequirements
}

class DefaultVerificationRequirementProvider @Inject constructor() : VerificationRequirementProvider {
    override suspend fun getRequirements(upgradeType: UpgradeType): VerificationRequirements {
        // In the future, this can fetch from Firebase Remote Config or an API
        return VerificationRequirements(
            requiredImages = upgradeType.requiredImages,
            requiredDocuments = upgradeType.requiredDocuments
        )
    }
}
