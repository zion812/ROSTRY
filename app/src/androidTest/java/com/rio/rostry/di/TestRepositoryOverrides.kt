package com.rio.rostry.di

import com.rio.rostry.data.database.entity.ProductEntity
object TestRepositoryOverrides {

    private val now = System.currentTimeMillis()

    private val seedProducts: List<ProductEntity> = (1..10).map {
        ProductEntity(
            productId = "product-$it",
            sellerId = "seller-${it % 3}",
            name = if (it == 1) "Broiler Prime" else "Product $it",
            description = "Seeded product $it",
            category = if (it % 2 == 0) "MEAT" else "CHICKS",
            price = 100.0 + it,
            quantity = 5.0,
            unit = "piece",
            location = "BLR",
            latitude = null,
            longitude = null,
            imageUrls = emptyList(),
            breed = if (it % 2 == 0) "Broiler" else "Aseel",
            familyTreeId = if (it % 3 == 0) "tree-$it" else null,
            parentIdsJson = null,
            createdAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            isDeleted = false,
            dirty = false
        )
    }

}
