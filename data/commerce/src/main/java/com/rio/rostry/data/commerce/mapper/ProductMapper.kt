package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Product
import com.rio.rostry.data.database.entity.ProductEntity

fun ProductEntity.toProduct(): Product =
    TODO("Temporary mapper stub during modularization")

fun Product.toEntity(): ProductEntity =
    TODO("Temporary mapper stub during modularization")

fun List<ProductEntity>.toProducts(): List<Product> = map { it.toProduct() }

fun List<Product>.toEntities(): List<ProductEntity> = map { it.toEntity() }
