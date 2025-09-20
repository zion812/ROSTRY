package com.rio.rostry.data.repository

import com.rio.rostry.data.local.ProductDao
import com.rio.rostry.data.model.Product as DataProduct
import com.rio.rostry.domain.model.Product as DomainProduct
import com.rio.rostry.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<DomainProduct>> {
        return productDao.getAllProducts().map { products ->
            products.map { it.toDomainModel() }
        }
    }

    override suspend fun getProductById(id: String): DomainProduct? {
        return productDao.getProductById(id)?.toDomainModel()
    }

    override fun getProductsByFarmerId(farmerId: String): Flow<List<DomainProduct>> {
        return productDao.getProductsByFarmerId(farmerId).map { products ->
            products.map { it.toDomainModel() }
        }
    }

    override suspend fun insertProduct(product: DomainProduct) {
        productDao.insertProduct(product.toDataModel())
    }

    override suspend fun updateProduct(product: DomainProduct) {
        productDao.updateProduct(product.toDataModel())
    }

    override suspend fun deleteProduct(product: DomainProduct) {
        productDao.deleteProduct(product.id)
    }

    private fun DataProduct.toDomainModel(): DomainProduct {
        return DomainProduct(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            unit = unit,
            farmerId = farmerId,
            imageUrl = imageUrl,
            category = category,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainProduct.toDataModel(): DataProduct {
        return DataProduct(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            unit = unit,
            farmerId = farmerId,
            imageUrl = imageUrl,
            category = category,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}