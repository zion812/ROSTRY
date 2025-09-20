package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.model.Product
import com.rio.rostry.domain.repository.ProductRepository
import com.rio.rostry.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : BaseViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val products: StateFlow<Resource<List<Product>>> = _products

    private val _product = MutableStateFlow<Resource<Product?>>(Resource.Loading)
    val product: StateFlow<Resource<Product?>> = _product

    init {
        fetchAllProducts()
    }

    fun fetchAllProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().collectLatest { productList ->
                _products.value = Resource.Success(productList)
            }
        }
    }

    fun fetchProductById(id: String) {
        executeWithLoading(_product) {
            productRepository.getProductById(id)
        }
    }

    fun fetchProductsByFarmerId(farmerId: String) {
        viewModelScope.launch {
            productRepository.getProductsByFarmerId(farmerId).collectLatest { productList ->
                _products.value = Resource.Success(productList)
            }
        }
    }

    fun createProduct(product: Product) {
        executeWithLoading(_product) {
            productRepository.insertProduct(product)
            product
        }
    }

    fun updateProduct(product: Product) {
        executeWithLoading(_product) {
            productRepository.updateProduct(product)
            product
        }
    }

    fun deleteProduct(product: Product) {
        executeWithLoading(_product) {
            productRepository.deleteProduct(product)
            null
        }
    }
}