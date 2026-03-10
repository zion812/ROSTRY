package com.rio.rostry.data.commerce.usecase

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.MarketListing
import com.rio.rostry.core.model.MarketListingStatus
import com.rio.rostry.domain.commerce.repository.MarketplaceRepository
import com.rio.rostry.domain.commerce.usecase.CreateListingRequest
import com.rio.rostry.domain.commerce.usecase.CreateListingUseCase
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CreateListingUseCase.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class CreateListingUseCaseImpl @Inject constructor(
    private val marketplaceRepository: MarketplaceRepository
) : CreateListingUseCase {

    override suspend fun invoke(request: CreateListingRequest): Result<MarketListing> {
        val listing = MarketListing(
            id = UUID.randomUUID().toString(),
            inventoryItemId = request.inventoryItemId,
            sellerId = "", // TODO: Get from auth
            price = request.price,
            currency = "USD",
            minimumOrderQuantity = request.minimumOrderQuantity,
            description = request.description,
            images = request.images,
            status = MarketListingStatus.ACTIVE,
            shippingOptions = emptyList(),
            category = null,
            tags = emptyList(),
            viewsCount = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return marketplaceRepository.createListing(listing)
    }
}

