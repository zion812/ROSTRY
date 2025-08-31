package com.rio.rostry.data.repo

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SyncRepository {

    override val syncWorkInfo: Flow<WorkInfo?> = 
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow("sync-work")
            .map { it.firstOrNull() }

}
