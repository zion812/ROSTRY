package com.rio.rostry.ui.scan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val productDao: com.rio.rostry.data.database.dao.ProductDao
) : ViewModel() {
    suspend fun productExists(productId: String): Boolean = productDao.findById(productId) != null
}
