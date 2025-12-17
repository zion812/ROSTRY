package com.rio.rostry.ui.verification.state

import android.os.Parcelable
import com.rio.rostry.domain.model.FarmLocation
import com.rio.rostry.domain.model.UpgradeType
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerificationFormState(
    val uploadedDocuments: List<String> = emptyList(),
    val uploadedImages: List<String> = emptyList(),
    val uploadedDocTypes: Map<String, String> = emptyMap(),
    val uploadedImageTypes: Map<String, String> = emptyMap(),
    val uploadProgress: Map<String, Int> = emptyMap(),
    val farmLocation: FarmLocation? = null,
    val showLocationPicker: Boolean = true,
    val upgradeType: UpgradeType? = null,
    val exifWarnings: List<String> = emptyList(),
    // Transient flags that define current flow state
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val error: String? = null,
    val uploadError: String? = null
) : Parcelable
