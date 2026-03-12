package com.rio.rostry.ui.scan
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.rio.rostry.domain.farm.repository.WatchedLineagesRepository

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val productDao: com.rio.rostry.data.database.dao.ProductDao,
    private val watchedLineagesRepository: WatchedLineagesRepository
) : ViewModel() {
    suspend fun productExists(productId: String): Boolean = productDao.findById(productId) != null

    suspend fun watchLineage(assetId: String, lineageHash: String) {
        watchedLineagesRepository.watchLineage(assetId, lineageHash, null, null)
    }
}
