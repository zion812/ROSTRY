package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Listing
import com.rio.rostry.data.database.entity.MarketListingEntity

fun MarketListingEntity.toListing(): Listing =
    TODO("Temporary mapper stub during modularization")

fun Listing.toEntity(): MarketListingEntity =
    TODO("Temporary mapper stub during modularization")

fun List<MarketListingEntity>.toListings(): List<Listing> = map { it.toListing() }

fun List<Listing>.toListingEntities(): List<MarketListingEntity> = map { it.toEntity() }
