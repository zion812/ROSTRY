package com.rio.rostry.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FarmLocation(
    val lat: Double,
    val lng: Double,
    val name: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val addressComponents: List<LocationComponent> = emptyList()
) : Parcelable

@Parcelize
data class LocationComponent(
    val name: String,
    val types: List<String>
) : Parcelable
