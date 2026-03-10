package com.rio.rostry.domain.repository

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource

interface PublicBirdRepository {
    suspend fun lookupBird(birdCode: String): Resource<PublicBirdView>
}

sealed class PublicBirdView {
    data class Full(val data: ProductEntity) : PublicBirdView()
    data class Restricted(val birdCode: String, val message: String) : PublicBirdView()
}
