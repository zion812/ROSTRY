package com.rio.rostry.data.repo

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    val syncWorkInfo: Flow<WorkInfo?>
}
